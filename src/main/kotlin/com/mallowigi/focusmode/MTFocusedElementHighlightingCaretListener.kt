/**
 * ****************************************************************************
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2022 Elior "Mallowigi" Boukhobza
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR
 * ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * ****************************************************************************
 */

package com.mallowigi.focusmode

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.event.CaretEvent
import com.intellij.openapi.editor.event.CaretListener
import com.intellij.openapi.editor.markup.HighlighterTargetArea
import com.intellij.openapi.editor.markup.MarkupModel
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiEditorUtil
import com.intellij.ui.ColorUtil
import com.mallowigi.focusmode.config.ConfigNotifier
import kotlin.math.max

class MTFocusedElementHighlightingCaretListener(
  private val project: Project,
  private val editor: Editor,
  private val elementFinders: Collection<MTFocusedElementFinder>,
) : CaretListener, Disposable {
  private val focusModeManager: MTFocusModeManager = MTFocusModeManager.getInstance()

  // A list of computed highlighted ranges
  private val highlighters: MutableList<RangeHighlighter> = mutableListOf()

  init {
    val connect = ApplicationManager.getApplication().messageBus.connect()

    // subscribe with lambda
    connect.subscribe(MTTopics.CONFIG, object : ConfigNotifier {
      override fun configChanged(mtConfig: MTConfig) = highlightIfNeeded()
    })
  }

  /**
   * Highlight focused range when caret change if needed
   *
   * @param event
   */
  override fun caretPositionChanged(event: CaretEvent) {
    if (this.editor != event.editor) {
      error("Check failed.")
    }
    this.highlightIfNeeded()
  }

  /**
   * Highlight the element at the caret position, or nothing if at the top
   * level.
   */
  private fun highlightIfNeeded() {
    if (!focusModeManager.isEnabled() || DumbService.isDumb(project)) {
      if (this.highlighters.size > 0) {
        this.highlightFocusedRange(null)
      }
      return
    }

    val caretModel = editor.caretModel
    if (caretModel.caretCount != 1) return

    // Find the offset of the caret in the current file, and focus the range at the caret
    val offset = caretModel.offset
    val psiFile = PsiEditorUtil.getPsiFile(editor)

    // Find the focusedElement at the caret and highlight it
    val focusedElement: PsiElement? = this.findFocusedElement(psiFile, offset)
    this.highlightFocusedRange(focusedElement?.textRange)
  }

  /** Run over the element finders and return the first that returns not null. */
  private fun findFocusedElement(psiFile: PsiFile, offset: Int): PsiElement? = elementFinders
    .asSequence()
    .map { it.findElement(psiFile, offset) }
    .firstOrNull { it != null }

  /**
   * Highlight focused range and unhighlight the surrounding ranges
   *
   * @param range the range to highlight
   */
  private fun highlightFocusedRange(range: TextRange?) {
    val document = this.editor.document
    val markupModel = this.editor.markupModel

    // Remove all highlighters first
    this.highlighters.forEach { markupModel.removeHighlighter(it) }

    // clear registered highlighters
    this.highlighters.clear()

    if (range != null) {
      // Add the active range highlighting
      this.highlighters.add(
        this.addRangeHighlighter(
          markupModel,
          range,
          MTFocusedAttributes.activeTextAttributes
        )
      )

      // Add the range before as inactive
      this.highlighters.add(
        this.addRangeHighlighter(
          markupModel,
          TextRange(0, range.startOffset),
          MTFocusedAttributes.inactiveTextAttributes
        )
      )

      // Add the range after as inactive
      this.highlighters.add(
        this.addRangeHighlighter(
          markupModel,
          TextRange(range.endOffset, max(document.textLength, range.endOffset)),
          MTFocusedAttributes.inactiveTextAttributes
        )
      )
    }

  }

  /**
   * Add a highlighter to the markup model at the given range
   *
   * @param markupModel the markup model to highlight
   * @param range the range to highlight
   * @param textAttributes the attributes to assign
   * @return [RangeHighlighter] the highlighted range
   */
  private fun addRangeHighlighter(
    markupModel: MarkupModel,
    range: TextRange,
    textAttributes: TextAttributes,
  ): RangeHighlighter {
    try {
      return markupModel.addRangeHighlighter(
        /* startOffset = */ range.startOffset,
        /* endOffset = */ range.endOffset,
        /* layer = */ PRIORITY,
        /* textAttributes = */ textAttributes,
        /* targetArea = */ HighlighterTargetArea.EXACT_RANGE
      )
    } catch (e: Exception) {
      // This can happen if the range is invalid, e.g. if the caret is at the end of the file
      return markupModel.addRangeHighlighter(
        /* startOffset = */ 0,
        /* endOffset = */ 0,
        /* layer = */ PRIORITY,
        /* textAttributes = */ textAttributes,
        /* targetArea = */ HighlighterTargetArea.EXACT_RANGE
      )
    }
  }

  override fun dispose() {
    // do nothing
  }

  object MTFocusedAttributes {
    /** Active text attributes are unthemed. */
    val activeTextAttributes: TextAttributes = TextAttributes()

    /** Inactive text attributes: the theme focus color. */
    val inactiveTextAttributes: TextAttributes
      get() {
        val inactiveTextAttributes = TextAttributes()
        inactiveTextAttributes.foregroundColor = ColorUtil.fromHex(MTConfig.getInstance().themeFocusColor)
        return inactiveTextAttributes
      }
  }

  companion object {
    const val PRIORITY: Int = 5999;
  }
}

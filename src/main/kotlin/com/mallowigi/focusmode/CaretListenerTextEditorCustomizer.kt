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

import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.fileEditor.impl.text.TextEditorCustomizer
import com.intellij.openapi.progress.blockingContext
import com.intellij.openapi.util.Disposer

class CaretListenerTextEditorCustomizer : TextEditorCustomizer {
  override suspend fun execute(textEditor: TextEditor) {
    blockingContext {
      val editor = textEditor.editor
      val project = editor.project ?: return@blockingContext

      // Get element finder extensions
      val elementFinders = this.collectElementFinders()

      // Add a caret listener for the current editor and project
      val listener = FocusedElementHighlightingCaretListener(project, editor, elementFinders)
      editor.caretModel.addCaretListener(listener)
      // Dont forget to dispose
      Disposer.register(textEditor, listener)
    }
  }

  /**
   * Collect element finder extensions
   *
   * @return the list of extensions implementing the [FocusedElementFinder]
   *     interface
   */
  private fun collectElementFinders(): Collection<FocusedElementFinder> =
    FocusedElementFinder.EP_NAME.extensionList
}

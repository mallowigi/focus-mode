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

package com.mallowigi.focusmode.extensions

import com.intellij.lang.Language
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiUtilCore
import com.mallowigi.focusmode.FocusedElementFinder

abstract class AbstractFocusedElementFinder(private val language: Language) : FocusedElementFinder {

  /**
   * Find element: Find the closest parent of the current element
   *
   * @param file the psi file to analyze
   * @param offset the caret offset
   * @return
   */
  override fun findElement(file: PsiFile, offset: Int): PsiElement? {
    if (file.language != language) return null

    return getElement(file, offset)
  }

  protected fun getElement(file: PsiFile, offset: Int): PsiElement? {
    val elementAtOffset = PsiUtilCore.getElementAtOffset(file, offset)

    val parents = this.parents(elementAtOffset, true)
    return parents.firstOrNull { this.isFocusParent(it) }
  }

  abstract fun isFocusParent(element: PsiElement): Boolean

  /**
   * Move up the tree until we find the first parent that is not a child of
   * the previous parent
   *
   * @param element
   * @param withSelf
   * @return
   */
  private fun parents(element: PsiElement, withSelf: Boolean): List<PsiElement> {
    val seed = if (withSelf) element else parentWithoutWalkingDirectories(element)
    return generateSequence(seed) { it.parent }.toList()
  }

  /**
   * Parent without walking directories. Taken from PsiTreeUtilKt
   *
   * @param element
   * @return
   */
  private fun parentWithoutWalkingDirectories(element: PsiElement): PsiElement? =
    if (element is PsiFile) null else element.parent
}

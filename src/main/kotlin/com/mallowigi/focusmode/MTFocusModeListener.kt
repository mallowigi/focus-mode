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

import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.ex.EditorSettingsExternalizable
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile

/** Disable Focus Mode on large files. */
class MTFocusModeListener : FileEditorManagerListener.Before {
  /** Run before file is open. */
  override fun beforeFileOpened(source: FileEditorManager, file: VirtualFile): Unit = enableDisableFocusMode(file)

  private fun enableDisableFocusMode(file: VirtualFile) {
    val extension = file.extension ?: return
    val text = VfsUtil.loadText(file)

    if (EXTENSIONS.contains(extension) && text.lines().size > MAX_LOC) {
      EditorSettingsExternalizable.getInstance().isFocusMode = false
    } else {
      EditorSettingsExternalizable.getInstance().isFocusMode = MTConfig.getInstance().isFocusModeEnabled
    }
    EditorFactory.getInstance().refreshAllEditors()
  }

  companion object {
    /** Max Lines of code. */
    const val MAX_LOC: Int = 400

    /** Extensions. */
    val EXTENSIONS: Set<String> = setOf("json")
  }
}

/**
 * ****************************************************************************
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2023 Elior "Mallowigi" Boukhobza
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

package com.mallowigi.focusmode.config

import com.intellij.openapi.components.BaseState
import com.intellij.openapi.components.Service
import com.intellij.ui.ColorUtil
import com.intellij.util.ui.UIUtil
import java.awt.Color

@Service(Service.Level.APP)
class FocusModeState : BaseState() {
  var isFocusModeEnabled: Boolean by property(false)

  var overrideFocusColor: Boolean by property(true)

  var focusColorHex: String? by string(DEFAULT_FOCUS_COLOR)

  var focusAlpha: Int by property(DEFAULT_OPACITY)

  val focusColor: Color
    get() {
      return when {
        this.overrideFocusColor -> UIUtil.getLabelDisabledForeground()
        else -> ColorUtil.fromHex(this.focusColorHex ?: DEFAULT_FOCUS_COLOR)
      }
    }

  companion object {
    const val DEFAULT_FOCUS_COLOR: String = "#424242"
    const val DEFAULT_OPACITY: Int = 75

    val instance: FocusModeState
      get() = FocusModeConfig.instance.settingsState

  }
}

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

import com.intellij.openapi.options.BoundSearchableConfigurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.ColorPanel
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.bindValue
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.builder.selected
import com.intellij.ui.layout.not
import com.mallowigi.focusmode.messages.FocusModeBundle.message
import com.mallowigi.focusmode.utils.bind

class FocusModeConfigurable : BoundSearchableConfigurable(message("MTForm.focusModeCheckbox.text"), HELP_ID) {
  private var main: DialogPanel? = null

  private val mainSettings: FocusModeState = FocusModeState.instance

  override fun getId(): String = ID

  private fun init() {
    initComponents()
  }

  private fun initComponents() {
    val colorChooser = ColorPanel()
    lateinit var enabledCheckbox: Cell<JBCheckBox>

    main = panel {

      row {
        enabledCheckbox = checkBox(message("MTForm.focusModeCheckbox.text"))
          .bindSelected(mainSettings::isFocusModeEnabled)
          .comment(message("MTForm.focusModeCheckbox.toolTipText"))
      }

      indent {
        row {
          val overrideCheckbox = checkBox(message("MTForm.overrideFocusModeSwitch.text"))
            .bindSelected(mainSettings::overrideFocusColor)

          cell(colorChooser)
            .enabledIf(overrideCheckbox.selected.not())
            .bind(mainSettings::focusColorHex, FocusModeState.DEFAULT_FOCUS_COLOR)
        }.enabledIf(enabledCheckbox.selected)

        row("Opacity") {
          slider(0, 100, 10, 50)
            .bindValue(mainSettings::focusAlpha)
        }.enabledIf(enabledCheckbox.selected)
      }

    }
  }

  override fun createPanel(): DialogPanel {
    init()
    return main!!
  }

  override fun apply() {
    super.apply()
    FocusModeConfig.instance.applySettings()
  }

  companion object {
    const val ID: String = "FocusModeConfigurable"

    const val HELP_ID: String = "FocusModeConfigurable"
  }

}


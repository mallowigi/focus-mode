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
 */
package com.mallowigi.focusmode.config

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.SettingsCategory
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.util.xmlb.XmlSerializerUtil
import com.mallowigi.focusmode.Topics

@State(name = "FocusModeConfig", storages = [Storage("focus_mode.xml")], category = SettingsCategory.UI)
class FocusModeConfig : PersistentStateComponent<FocusModeConfig>, Cloneable {
  private var firstRun: Boolean = true
  private var isReset: Boolean = false

  private var metadata: FocusModeMetadataState = FocusModeMetadataState()
  var settingsState: FocusModeState = FocusModeState()

  public override fun clone(): FocusModeConfig = XmlSerializerUtil.createCopy(this)

  override fun getState(): FocusModeConfig = this

  override fun loadState(state: FocusModeConfig) {
    val changed = state != this
    XmlSerializerUtil.copyBean(state, this)
    if (changed && !firstRun) {
      ApplicationManager.getApplication().invokeAndWait { fireChanged() }
    }
    firstRun = false
  }

  /** Fire event before saving the form values. */
  private fun fireBeforeChanged() {
    ApplicationManager.getApplication().messageBus
      .syncPublisher(Topics.CONFIG)
      .beforeConfigChanged(this)
  }

  /** Fire an event to the application bus that the settings have changed. */
  private fun fireChanged() {
    ApplicationManager.getApplication().messageBus
      .syncPublisher(Topics.CONFIG)
      .configChanged(this)
  }

  fun applySettings() {
    fireBeforeChanged()
    isReset = false
    metadata.pristineConfig = false

    fireChanged()
  }

  companion object {
    val instance: FocusModeConfig by lazy { service() }
  }
}

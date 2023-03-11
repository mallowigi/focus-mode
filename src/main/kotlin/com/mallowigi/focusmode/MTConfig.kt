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
package com.mallowigi.focusmode

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.SettingsCategory
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.util.xmlb.XmlSerializerUtil
import com.mallowigi.focusmode.config.MTMainConfigState
import com.mallowigi.focusmode.config.MTMetadataState

@State(name = "FocusModeConfig", storages = [Storage("focus_mode.xml")], category = SettingsCategory.UI)
class MTConfig : PersistentStateComponent<MTConfig>, Cloneable {
  private var firstRun: Boolean = true
  private var isReset: Boolean = false

  private var metadata: MTMetadataState = MTMetadataState()
  var settingsState: MTMainConfigState = MTMainConfigState()

  public override fun clone(): MTConfig = XmlSerializerUtil.createCopy(this)

  override fun getState(): MTConfig = this

  override fun loadState(state: MTConfig) {
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
      .syncPublisher(MTTopics.CONFIG)
      .beforeConfigChanged(this)
  }

  /** Fire an event to the application bus that the settings have changed. */
  private fun fireChanged() {
    ApplicationManager.getApplication().messageBus
      .syncPublisher(MTTopics.CONFIG)
      .configChanged(this)
  }

  fun applySettings() {
    fireBeforeChanged()
    isReset = false
    metadata.pristineConfig = false

    fireChanged()
  }

  /** Reset the settings to the default values. */
  fun resetSettings() {
    isReset = true
    metadata.pristineConfig = true
  }

  companion object {
    val instance: MTConfig by lazy { service() }
  }
}

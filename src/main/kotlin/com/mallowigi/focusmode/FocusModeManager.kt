package com.mallowigi.focusmode

import com.intellij.ide.ui.LafManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.mallowigi.focusmode.config.FocusModeState

@Service(Service.Level.APP)
class FocusModeManager {
  fun isEnabled(): Boolean = FocusModeState.instance.isFocusModeEnabled

  /**
   * Set enabled state and fire event
   *
   * @param enabled new state
   * @param editor pass the editor if needed
   */
  fun setEnabled(enabled: Boolean) {
    FocusModeState.instance.isFocusModeEnabled = enabled
  }

  fun toggleFocusMode() {
    val focusModeEnabled = isEnabled()
    setEnabled(!focusModeEnabled)
    reloadUI()
  }

  /** Trigger a reloadUI event. */
  private fun reloadUI() {
    LafManager.getInstance().updateUI()
  }

  companion object {
    val instance: FocusModeManager by lazy { service() }
  }
}

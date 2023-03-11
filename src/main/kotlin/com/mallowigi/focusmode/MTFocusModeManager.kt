package com.mallowigi.focusmode

import com.intellij.ide.ui.LafManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.mallowigi.focusmode.config.MTMainConfigState

@Service(Service.Level.APP)
class MTFocusModeManager {
  fun isEnabled(): Boolean = MTMainConfigState.instance.isFocusModeEnabled

  /**
   * Set enabled state and fire event
   *
   * @param enabled new state
   * @param editor pass the editor if needed
   */
  fun setEnabled(enabled: Boolean) {
    MTMainConfigState.instance.isFocusModeEnabled = enabled
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
    val instance: MTFocusModeManager by lazy { service() }
  }
}

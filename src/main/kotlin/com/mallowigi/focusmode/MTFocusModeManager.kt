package com.mallowigi.focusmode

import com.intellij.ide.ui.LafManager
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
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
  private fun setEnabled(enabled: Boolean) {
    MTMainConfigState.instance.isFocusModeEnabled = enabled
  }

  fun toggleFocusMode() {
    val focusModeEnabled = isEnabled()
    setEnabled(!focusModeEnabled)
    applyFocusMode()
    reloadUI()
  }

  private fun applyFocusMode() {
    val enabled = isEnabled()
    setEnabled(enabled)
  }

  /** Trigger a reloadUI event. */
  private fun reloadUI() {
    LafManager.getInstance().updateUI()
  }

  companion object {
    /** Service instance. */
    fun getInstance(): MTFocusModeManager =
      ApplicationManager.getApplication().getService(MTFocusModeManager::class.java)

  }
}

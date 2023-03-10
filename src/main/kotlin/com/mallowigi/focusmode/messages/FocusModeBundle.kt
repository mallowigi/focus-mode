package com.mallowigi.focusmode.messages

import com.intellij.BundleBase
import com.intellij.DynamicBundle
import com.intellij.ide.plugins.PluginManager
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.util.NlsContexts
import org.jetbrains.annotations.Contract
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey
import java.util.Locale
import java.util.ResourceBundle
import java.util.function.Supplier

@NonNls
private const val BUNDLE: String = "messages.FocusModeBundle"


/**
 * Get strings of the Material Theme Resource Bundle
 *
 * @constructor Create empty Material theme bundle
 */
@Suppress("UnstableApiUsage")
object FocusModeBundle : DynamicBundle(BUNDLE) {
  private var locale: Locale? = null

  @JvmStatic
  fun getBundle(): ResourceBundle = getLocalizedResource()

  override fun messageOrDefault(
    @PropertyKey(resourceBundle = BUNDLE) key: String,
    defaultValue: String?,
    vararg params: Any,
  ): String = messageOrDefault(getBundle(), key, defaultValue, *params)

  @Contract(pure = true)
  override fun getMessage(key: String, vararg params: Any): String =
    BundleBase.messageOrDefault(getBundle(), key, null, *params)

  @NlsContexts.DialogTitle
  @NlsContexts.DialogMessage
  @JvmStatic
  fun message(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any): String =
    getMessage(key, *params)

  private fun getLocalizedResource(): ResourceBundle {
    if (locale != null) {
      return ResourceBundle.getBundle(BUNDLE, locale!!)
    }
    locale = when {
      PluginManager.isPluginInstalled(PluginId.getId("com.intellij.ja")) -> Locale.JAPAN
      PluginManager.isPluginInstalled(PluginId.getId("com.intellij.zh")) -> Locale.CHINA
      PluginManager.isPluginInstalled(PluginId.getId("com.intellij.ko")) -> Locale.KOREA
      else                                                               -> Locale.US
    }
    Locale.setDefault(locale)
    return ResourceBundle.getBundle(BUNDLE)
  }
}

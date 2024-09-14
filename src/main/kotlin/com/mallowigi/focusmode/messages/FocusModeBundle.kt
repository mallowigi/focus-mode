package com.mallowigi.focusmode.messages

import com.intellij.BundleBase
import com.intellij.DynamicBundle
import com.intellij.ide.plugins.PluginManager
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.util.NlsContexts
import org.jetbrains.annotations.Contract
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey
import java.util.*
import java.util.function.Supplier

@NonNls
private const val BUNDLE: String = "messages.FocusModeBundle"


object FocusModeBundle : DynamicBundle(BUNDLE) {

  @JvmStatic
  fun message(key: @PropertyKey(resourceBundle = BUNDLE) String, vararg params: Any?): String = getMessage(key, *params)

  @JvmStatic
  fun messagePointer(key: @PropertyKey(resourceBundle = BUNDLE) String, vararg params: Any?): Supplier<String> =
    getLazyMessage(key, *params)


}

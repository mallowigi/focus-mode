package com.mallowigi.focusmode.utils

import com.intellij.ide.plugins.IdeaPluginDescriptor
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.extensions.PluginId


const val PLUGIN_ID: String = "com.mallowigi.focusmode"

fun getPlugin(): IdeaPluginDescriptor? {
  return PluginManagerCore.getPlugin(PluginId.getId(PLUGIN_ID))
}

fun getVersion(): String {
  val plugin: IdeaPluginDescriptor = getPlugin() ?: return "1.0.0"
  return plugin.version
}

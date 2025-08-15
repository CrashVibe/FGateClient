package org.crashvibe.fGateClient.utils

import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

fun registerEvents(plugin: JavaPlugin, vararg listeners: Listener) {
  listeners.forEach { listener ->
    plugin.server.pluginManager.registerEvents(listener, plugin)
  }
}

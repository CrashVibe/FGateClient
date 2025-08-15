package org.crashvibe.fGateClient.service

import org.bukkit.Bukkit
import org.crashvibe.fGateClient.FGateClient
import org.crashvibe.fGateClient.service.websocket.WebSocketManager
import java.net.URI
import java.util.logging.Logger

object ServiceManager {
  lateinit var webSocketManager: WebSocketManager

  fun init(plugin: FGateClient) {
    ConfigManager.init(plugin)
    val config = ConfigManager.configData
    if (config.websocket.url.isEmpty() || config.websocket.token.isEmpty()) {
      plugin.logger.severe("WebSocket URL or token is not configured properly.")
      Bukkit.getPluginManager().disablePlugin(plugin)
    }
    webSocketManager = WebSocketManager(
      URI(config.websocket.url), config.websocket.token, plugin
    )
  }
}

fun Logger.debug(msg: String) {
  if (ConfigManager.configData.debug) {
    this.info("[DEBUG] $msg")
  }
}

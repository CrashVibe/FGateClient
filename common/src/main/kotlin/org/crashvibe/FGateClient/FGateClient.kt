package org.crashvibe.FGateClient

import com.crashvibe.fgateclient.handler.RequestHandler
import org.crashvibe.FGateClient.config.ConfigManager
import org.crashvibe.FGateClient.utils.PluginCoroutineScope
import org.crashvibe.FGateClient.websocket.RequestDispatcher
import org.crashvibe.FGateClient.websocket.WebSocketManager
import java.net.URI
import java.nio.file.Path
import java.util.logging.Logger

object FGateClient {
  lateinit var pluginScope: PluginCoroutineScope
    private set
  lateinit var logger: Logger
    private set
  lateinit var webSocketManager: WebSocketManager
    private set
  lateinit var requestDispatcher: RequestDispatcher
    private set

  fun init(configFile: Path, logger: Logger, vararg handler: RequestHandler) {
    this.logger = logger
    ConfigManager.init(configFile)
    this.requestDispatcher = RequestDispatcher(*handler)
    pluginScope = PluginCoroutineScope()
    val config = ConfigManager.configData
    if (config.websocket.url.isEmpty() || config.websocket.token.isEmpty()) {
      logger.severe("WebSocket URL or token is not configured properly.")
      throw RuntimeException("WebSocket URL or token is not configured properly.")
    }
    webSocketManager = WebSocketManager(
      URI(config.websocket.url), config.websocket.token, requestDispatcher
    )
  }

  fun disable() {
    if (this::webSocketManager.isInitialized && webSocketManager.isOpen) {
      webSocketManager.close()
    }
    if (this::pluginScope.isInitialized) {
      pluginScope.cancelAll()
    }
  }
}



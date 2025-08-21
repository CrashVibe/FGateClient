package com.crashvibe.fgateclient.handler

import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonElement
import org.crashvibe.fGateClient.FGateClient
import org.crashvibe.fGateClient.service.ServiceManager.webSocketManager
import org.crashvibe.fGateClient.service.debug
import org.crashvibe.fGateClient.service.websocket.JsonRpcRequest
import org.crashvibe.fGateClient.service.websocket.JsonRpcResponse
import java.util.logging.Level

/**
 * 请求分发器
 */
object RequestDispatcher {
  private val handlers: MutableMap<String, RequestHandler> = HashMap<String, RequestHandler>()

  val logger = FGateClient.instance.logger
  private val pluginScope = FGateClient.instance.pluginScope

  /**
   * 注册请求处理器
   */
  fun registerHandler(handler: RequestHandler) {
    handlers[handler.method] = handler
    logger.debug("注册方法: " + handler.method)
  }

  /**
   * 分发请求
   */
  fun dispatch(request: JsonRpcRequest<JsonElement>) {
    val method = request.method
    val handler: RequestHandler? = handlers[method]
    if (handler == null) {
      logger.warning("未知方法被请求: $method")
      return
    }
    logger.debug("处理请求: $method")
    pluginScope.launch {
      try {
        handler.handle(request)
      } catch (e: Exception) {
        logger.log(Level.SEVERE, "ERROR: $method", e)
        webSocketManager.sendResponse<JsonElement, JsonElement>(
          request.id, null, JsonRpcResponse.JsonRpcError(
            code = -32603, // Internal error
            message = e.message ?: "Internal error", null
          )
        )
      }
    }
  }
}

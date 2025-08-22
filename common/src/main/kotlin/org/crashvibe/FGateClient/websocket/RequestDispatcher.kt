package org.crashvibe.FGateClient.websocket

import com.crashvibe.fgateclient.handler.RequestHandler
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonElement
import org.crashvibe.FGateClient.FGateClient.logger
import org.crashvibe.FGateClient.FGateClient.pluginScope
import org.crashvibe.FGateClient.FGateClient.webSocketManager
import org.crashvibe.FGateClient.utils.debug
import java.util.logging.Level

/**
 * 请求分发器
 */
object RequestDispatcher {
  private val handlers: MutableMap<String, RequestHandler> = HashMap<String, RequestHandler>()

  /**
   * 注册请求处理器
   */
  fun registerHandler(vararg handler: RequestHandler) {
    handler.forEach { handler ->
      handlers[handler.method.method] = handler
      logger.debug("注册方法: " + handler.method)
    }
    MethodType.entries.forEach { method ->
      if (!handlers.containsKey(method.method)) {
        logger.warning("未注册方法: $method")
      }
    }
    handlers.keys.forEach { method ->
      if (MethodType.entries.none { it.method == method }) {
        logger.warning("注册了未知方法: $method")
      }
    }
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

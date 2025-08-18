package com.crashvibe.fgateclient.handler

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.serializer
import org.crashvibe.fGateClient.FGateClient
import org.crashvibe.fGateClient.service.ServiceManager.webSocketManager
import org.crashvibe.fGateClient.service.websocket.JsonRpcRequest
import org.crashvibe.fGateClient.service.websocket.JsonRpcResponse

/**
 * WebSocket 请求处理器基类
 */
abstract class RequestHandler {
  val logger = FGateClient.instance.logger

  /**
   * 获取处理器支持的方法名
   */
  abstract val method: String

  /**
   * 处理请求
   */
  abstract fun handle(request: JsonRpcRequest<JsonElement>)

  /**
   * 解析请求参数
   */
  protected inline fun <reified P> parseParams(request: JsonRpcRequest<JsonElement>): P? {
    val params = request.params?.let {
      Json.decodeFromJsonElement(serializer<P>(), it)
    }

    if (params == null) {
      return null
    }

    return params
  }

  /**
   * 发送成功响应
   */
  protected inline fun <reified R> sendResponse(requestId: String?, result: R) {
    webSocketManager.sendResponse<R, JsonElement>(requestId, result, null)
  }

  /**
   * 发送错误响应
   */
  protected inline fun <reified E> sendError(requestId: String?, error: JsonRpcResponse.JsonRpcError<E>) {
    webSocketManager.sendResponse<JsonElement, E>(requestId, null, error)
  }
}

package org.crashvibe.FGateClient.websocket

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.serializer
import org.crashvibe.BuildConfig
import org.crashvibe.FGateClient.FGateClient.logger
import org.crashvibe.FGateClient.FGateClient.pluginScope
import org.crashvibe.FGateClient.config.ConfigManager
import org.crashvibe.FGateClient.utils.debug
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.ConnectException
import java.net.URI
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.logging.Level


class WebSocketManager(
  uri: URI, token: String
) : WebSocketClient(
  uri, mapOf(
    "Authorization" to "Bearer $token",
    "X-API-Version" to BuildConfig.apiVersion,
  )
) {
  private val pendingRequests = ConcurrentHashMap<String, PendingRequest<*>>()

  init {
    instance = this
    logger.info("WebSocketManager 初始化完成，连接到: $uri")
    connect()
  }

  override fun onOpen(handshakedata: ServerHandshake) {
  }

  override fun onMessage(message: String) {
    pluginScope.launch {
      handleMessage(message)
    }
  }

  override fun onClose(code: Int, reason: String, remote: Boolean) {
    logger.debug("WebSocket 连接已关闭 [代码: $code, 原因: $reason]")
    if (remote && !ConfigManager.configData.debug) {
      logger.warning("WebSocket 连接已关闭 [代码: $code, 原因: $reason]")
    }
    if (ConfigManager.configData.websocket.autoReconnect) {
      scheduleReconnect(ConfigManager.configData.websocket.reconnectInterval)
    }
  }

  override fun onError(ex: Exception) {
    if (ex is ConnectException) {
      logger.warning("连接失败: ${ex.message}")
    } else {
      logger.log(Level.SEVERE, "WebSocket 连接发生错误", ex)
    }
  }

  fun scheduleReconnect(delaySeconds: Long) {
    if (delaySeconds <= 0) return

    pluginScope.launch {
      delay(delaySeconds * 1000)
      logger.info("正在重新连接 WebSocket...")
      val isconnect = reconnectBlocking()
      if (isconnect) {
        logger.info("重新连接成功")
      } else {
        logger.warning("重新连接失败，将在 $delaySeconds 秒后重试")
      }
    }
  }

  companion object {
    lateinit var instance: WebSocketManager
      private set
  }

  private fun handleMessage(message: String) {
    try {
      val jsonObject = Json.parseToJsonElement(message).jsonObject
      logger.debug("接收到消息: $jsonObject")
      when {
        jsonObject["type"]?.jsonPrimitive?.content == "welcome" -> {
          logger.debug("已建立连接: $message (API v${jsonObject["api_version"]?.jsonPrimitive?.content})")
          logger.info("连接 主节点 成功，主节点版本: ${jsonObject["api_version"]?.jsonPrimitive?.content}")
          return
        }

        jsonObject["jsonrpc"]?.jsonPrimitive?.content != "2.0" -> {
          logger.warning("收到无效 JSON-RPC 消息: $message")
          return
        }

        jsonObject.containsKey("method") -> {
          val request = Json.decodeFromString<JsonRpcRequest<JsonElement>>(message)
          RequestDispatcher.dispatch(request)
        }

        else -> {
          val response = Json.decodeFromString<JsonRpcResponse<JsonElement, JsonElement>>(message)
          response.id?.let { id ->
            @Suppress("UNCHECKED_CAST") (pendingRequests.remove(id) as? PendingRequest<JsonRpcResponse<JsonElement, JsonElement>>)?.let { pending ->
              if (response.error != null) {
                pending.reject(Exception(response.error.message))
              } else {
                pending.resolve(response)
              }
            }
          }
        }
      }
    } catch (e: Exception) {
      logger.warning("解析消息失败: ${e.message}")
    }
  }

  suspend fun <P, R> sendRequestTyped(
    method: String,
    params: P?,
    paramsSerializer: KSerializer<P>,
    resultSerializer: KSerializer<R>,
    timeoutMillis: Long = 5000
  ): JsonRpcResponse<R, JsonElement> {
    val id = UUID.randomUUID().toString()
    val paramsJson = params?.let { Json.encodeToJsonElement(paramsSerializer, it) }
    val request = JsonRpcRequest(
      jsonrpc = "2.0", method = method, params = paramsJson, id = id
    )

    val deferred = CompletableDeferred<JsonRpcResponse<JsonElement, JsonElement>>()
    pendingRequests[id] = PendingRequest(deferred, requestId = id)
    logger.debug("发送请求: ${Json.encodeToString(request)}")
    send(Json.encodeToString(request))

    @Suppress("UNCHECKED_CAST") val response =
      (pendingRequests[id] as PendingRequest<JsonRpcResponse<JsonElement, JsonElement>>).await(timeoutMillis)

    return if (response.result != null) {
      val result = Json.decodeFromJsonElement(resultSerializer, response.result)
      JsonRpcResponse(response.jsonrpc, result, null, response.id)
    } else {
      JsonRpcResponse(response.jsonrpc, null, response.error, response.id)
    }
  }

  suspend inline fun <reified P, reified R> sendRequest(
    method: String, params: P? = null, timeoutMillis: Long = 5000
  ): JsonRpcResponse<R, JsonElement> {
    return sendRequestTyped(
      method, params, serializer<P>(), serializer<R>(), timeoutMillis
    )
  }

  fun <R, E> sendResponseTyped(
    id: String?,
    result: R? = null,
    error: JsonRpcResponse.JsonRpcError<E>? = null,
    resultSerializer: KSerializer<R>,
    errorSerializer: KSerializer<E>
  ) {
    val resultJson = result?.let { Json.encodeToJsonElement(resultSerializer, it) }
    val errorJson = error?.let {
      JsonRpcResponse.JsonRpcError(
        code = it.code,
        message = it.message,
        data = it.data?.let { data -> Json.encodeToJsonElement(errorSerializer, data) }
      )
    }

    val response = JsonRpcResponse(
      jsonrpc = "2.0", result = resultJson, error = errorJson, id = id
    )

    val json = Json.encodeToString(response)
    logger.debug("发送响应: $json")
    send(json)
  }

  inline fun <reified R, reified E> sendResponse(
    id: String?,
    result: R? = null,
    error: JsonRpcResponse.JsonRpcError<E>? = null
  ) {
    sendResponseTyped(id, result, error, serializer<R>(), serializer<E>())
  }

  fun sendNoticeTyped(
    method: String, params: JsonElement? = null
  ) {
    val request = JsonRpcRequest(
      jsonrpc = "2.0", method = method, params = params, id = null
    )
    val json = Json.encodeToString(request)
    logger.debug("发送通知: $json")
    send(json)
  }

  inline fun <reified P> sendNotice(
    method: String, params: P? = null
  ) {
    sendNoticeTyped(
      method, params?.let { Json.encodeToJsonElement(serializer<P>(), it) }
    )
  }
}

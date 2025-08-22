package com.crashvibe.fgateclient.handler.impl

import com.crashvibe.fgateclient.handler.RequestHandler
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import org.crashvibe.FGateClient.websocket.JsonRpcRequest
import org.crashvibe.FGateClient.websocket.MethodType


/**
 * 获取客户端信息请求处理器
 */
class GetClientInfo(val clientInfo: ClientInfo) : RequestHandler() {
  override val method: MethodType = MethodType.GET_CLIENT_INFO

  @Serializable
  data class ClientInfo(
    val minecraft_version: String,
    val minecraft_software: String,
    val supports_papi: Boolean,
    val supports_command: Boolean,
    val player_count: Int
  )

  override fun handle(request: JsonRpcRequest<JsonElement>) {
    sendResponse(
      request.id,
      clientInfo
    )
  }
}

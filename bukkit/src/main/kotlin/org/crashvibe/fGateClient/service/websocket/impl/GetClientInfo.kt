package com.crashvibe.fgateclient.handler.impl

import com.crashvibe.fgateclient.handler.RequestHandler
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import org.bukkit.Bukkit
import org.crashvibe.fGateClient.service.websocket.JsonRpcRequest
import org.crashvibe.fGateClient.utils.Command


/**
 * 获取客户端信息请求处理器
 */
class GetClientInfo() : RequestHandler() {
  override val method: String = "get.client.info"

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
      ClientInfo(
        minecraft_version = Bukkit.getVersion(),
        minecraft_software = Bukkit.getName(),
        supports_papi = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null,
        supports_command = Command.isAvailable,
        player_count = Bukkit.getOnlinePlayers().size
      )
    )
  }
}

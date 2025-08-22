package org.crashvibe.FGateClient.listeners

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import org.crashvibe.FGateClient.FGateClient.logger
import org.crashvibe.FGateClient.config.ConfigManager
import org.crashvibe.FGateClient.websocket.JsonRpcResponse
import org.crashvibe.FGateClient.websocket.WebSocketManager

object OnJoinService {
  enum class Action { kick, allow }

  @Serializable
  data class JoinInfo(val player: String, val uuid: String, val ip: String, val timestamp: Long)

  @Serializable
  data class JoinResult(val action: Action, val reason: String? = null)

  fun handleJoin(player: String, uuid: String, ip: String): JoinResult {
    val config = ConfigManager.configData.eventResolve.join

    if (!WebSocketManager.instance.isOpen) {
      return if (config.allowJoin) {
        JoinResult(Action.allow)
      } else {
        JoinResult(Action.kick, config.kickMessage)
      }
    }

    val request = JoinInfo(player, uuid, ip, System.currentTimeMillis())

    return try {
      val response: JsonRpcResponse<JoinResult, JsonElement> = runBlocking {
        WebSocketManager.instance.sendRequest("player.join", request)
      }

      if (response.error != null) {
        logger.severe("加入事件错误: ${response.error.message}")
        return if (config.errorJoin) JoinResult(Action.allow)
        else JoinResult(Action.kick, config.errorMessage)
      }

      response.result ?: JoinResult(Action.kick, config.errorMessage)
    } catch (e: Exception) {
      logger.severe("加入事件异常: ${e.message}")
      if (config.errorJoin) JoinResult(Action.allow)
      else JoinResult(Action.kick, config.errorMessage)
    }
  }
}

package org.crashvibe.FGateClient.listeners

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import org.crashvibe.FGateClient.FGateClient.logger
import org.crashvibe.FGateClient.config.ConfigManager
import org.crashvibe.FGateClient.websocket.JsonRpcResponse
import org.crashvibe.FGateClient.websocket.WebSocketManager

object OnLoginService {
  enum class Action { kick, allow }

  @Serializable
  data class LoginInfo(val player: String, val uuid: String, val ip: String, val timestamp: Long)

  @Serializable
  data class LoginResult(val action: Action, val reason: String? = null)

  fun handleLogin(player: String, uuid: String, ip: String): LoginResult {
    val config = ConfigManager.configData.eventResolve.join

    if (!WebSocketManager.instance.isOpen) {
      return if (config.allowJoin) {
        LoginResult(Action.allow)
      } else {
        LoginResult(Action.kick, config.kickMessage)
      }
    }

    val request = LoginInfo(player, uuid, ip, System.currentTimeMillis())

    return try {
      val response: JsonRpcResponse<LoginResult, JsonElement> = runBlocking {
        WebSocketManager.instance.sendRequest("player.login", request)
      }

      if (response.error != null) {
        logger.severe("加入事件错误: ${response.error.message}")
        return if (config.errorJoin) LoginResult(Action.allow)
        else LoginResult(Action.kick, config.errorMessage)
      }

      response.result ?: LoginResult(Action.kick, config.errorMessage)
    } catch (e: Exception) {
      logger.severe("加入事件异常: ${e.message}")
      if (config.errorJoin) LoginResult(Action.allow)
      else LoginResult(Action.kick, config.errorMessage)
    }
  }
}

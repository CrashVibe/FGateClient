package org.crashvibe.fGateClient.listeners

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.crashvibe.fGateClient.FGateClient
import org.crashvibe.fGateClient.listeners.OnJoin.Action.allow
import org.crashvibe.fGateClient.listeners.OnJoin.Action.kick
import org.crashvibe.fGateClient.service.ConfigManager
import org.crashvibe.fGateClient.service.websocket.JsonRpcResponse
import org.crashvibe.fGateClient.service.websocket.WebSocketManager

class OnJoin : Listener {
  val logger = FGateClient.instance.logger

  @Serializable
  data class JoinInfo(
    val player: String,
    val uuid: String,
    val ip: String,
    val timestamp: Long,
  )

  enum class Action {
    kick, allow
  }

  @Serializable
  data class JoinResult(
    val action: Action,
    val reason: String? = null,
  )

  @EventHandler(priority = EventPriority.HIGHEST)
  fun onAsyncPlayerPreLogin(event: AsyncPlayerPreLoginEvent) {
    val config = ConfigManager.configData.eventResolve.join

    if (!WebSocketManager.instance.isOpen) {
      if (!config.allowJoin) {
        event.disallow(
          AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
          legacyAmpersand().deserialize(config.kickMessage)
        )
      }
      return
    }

    val paramsJson = JoinInfo(
      player = event.name,
      uuid = event.uniqueId.toString(),
      ip = event.address.hostAddress,
      timestamp = System.currentTimeMillis()
    )

    try {
      val response: JsonRpcResponse<JoinResult, JsonElement> = runBlocking {
        WebSocketManager.instance.sendRequest<JoinInfo, JoinResult>("player.join", paramsJson)
      }

      if (response.error != null) {
        logger.severe("处理玩家加入事件时发生错误: ${response.error.message}")
        if (!config.errorJoin) {
          event.disallow(
            AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
            legacyAmpersand().deserialize(config.errorMessage)
          )
        }
        return
      }

      when (response.result?.action) {
        kick -> {
          val reason = response.result.reason ?: config.kickMessage
          event.disallow(
            AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
            legacyAmpersand().deserialize(reason)
          )
          logger.info("玩家 ${event.name} 被远程服务器拒绝: $reason")
        }

        allow -> {
          logger.info("玩家 ${event.name} 被远程服务器允许加入")
        }

        null -> {
          logger.severe("处理玩家加入事件时未返回有效的操作类型: ${response.result}")
          if (!config.errorJoin) {
            event.disallow(
              AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
              legacyAmpersand().deserialize(config.errorMessage)
            )
          }
        }
      }
    } catch (e: Exception) {
      logger.severe("处理玩家加入事件时发生异常: ${e.message}")
      if (!config.errorJoin) {
        event.disallow(
          AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
          legacyAmpersand().deserialize(config.errorMessage)
        )
      }
    }
  }
}


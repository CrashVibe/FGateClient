package org.crashvibe.fGateClient.listeners

import io.papermc.paper.event.player.AsyncChatEvent
import kotlinx.serialization.Serializable
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.crashvibe.fGateClient.FGateClient
import org.crashvibe.fGateClient.service.websocket.WebSocketManager

class OnChat : Listener {
  val logger = FGateClient.instance.logger

  @Serializable
  data class ChatMessage(
    val playerName: String,
    val playerUUID: String,
    val message: String,
    val timestamp: Long
  )

  @EventHandler
  fun onPlayerChat(event: AsyncChatEvent) {
    if (!WebSocketManager.instance.isOpen) {
      return
    }

    val paramsJson = ChatMessage(
      playerName = event.player.name,
      playerUUID = event.player.uniqueId.toString(),
      message = plainText().serialize(event.message()),
      timestamp = System.currentTimeMillis()
    )
    try {
      WebSocketManager.instance.sendNotice("chat.message", paramsJson)
    } catch (e: Exception) {
      logger.severe("处理玩家消息事件时发生异常: ${e.message}")
    }
  }
}

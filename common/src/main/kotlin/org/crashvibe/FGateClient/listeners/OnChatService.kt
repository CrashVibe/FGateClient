package org.crashvibe.FGateClient.listeners

import kotlinx.serialization.Serializable
import org.crashvibe.FGateClient.FGateClient.logger
import org.crashvibe.FGateClient.websocket.WebSocketManager

object OnChatService {
  @Serializable
  data class ChatMessage(
    val playerName: String,
    val playerUUID: String,
    val message: String,
    val timestamp: Long
  )

  fun handleChat(playerName: String, playerUUID: String, message: String) {
    if (!WebSocketManager.instance.isOpen) return

    val chatMessage = ChatMessage(
      playerName = playerName,
      playerUUID = playerUUID,
      message = message,
      timestamp = System.currentTimeMillis()
    )

    try {
      WebSocketManager.instance.sendNotice("chat.message", chatMessage)
    } catch (e: Exception) {
      logger.severe("发送聊天消息失败: ${e.message}")
    }
  }
}

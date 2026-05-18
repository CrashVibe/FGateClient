package org.crashvibe.FGateClient.listeners

import kotlinx.serialization.Serializable
import org.crashvibe.FGateClient.websocket.WebSocketManager

object OnLeaveService {
  @Serializable
  data class LeaveInfo(val playerName: String, val playerUUID: String, val timestamp: Long)

  fun handleLeave(player: String, playerUUID: String, timestamp: Long = System.currentTimeMillis()) {
    if (!WebSocketManager.instance.isOpen) {
      return
    }

    val request = LeaveInfo(player, playerUUID, timestamp)

    WebSocketManager.instance.sendNotice("player.leave", request)
  }
}

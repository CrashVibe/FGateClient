package org.crashvibe.FGateClient.listeners

import kotlinx.serialization.Serializable
import org.crashvibe.FGateClient.websocket.WebSocketManager

object OnLeaveService {
  @Serializable
  data class LeaveInfo(val playerName: String)

  fun handleLeave(player: String) {
    if (!WebSocketManager.instance.isOpen) {
      return
    }

    val request = LeaveInfo(player)

    WebSocketManager.instance.sendNotice("player.leave", request)
  }
}

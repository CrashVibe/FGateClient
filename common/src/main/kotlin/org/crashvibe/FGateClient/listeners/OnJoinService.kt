package org.crashvibe.FGateClient.listeners

import kotlinx.serialization.Serializable
import org.crashvibe.FGateClient.websocket.WebSocketManager

object OnJoinService {
  @Serializable
  data class JoinInfo(val playerName: String,
                          val playerUUID: String,
                          val timestamp: Long)

  fun handleJoin(player: String, playerUUID: String, timestamp: Long = System.currentTimeMillis()) {
    if (!WebSocketManager.instance.isOpen) {
      return
    }

    val request = JoinInfo(player, playerUUID, timestamp)

    WebSocketManager.instance.sendNotice("player.join", request)
  }
}

package org.crashvibe.fgatebukkit.handler

import org.bukkit.Bukkit
import org.crashvibe.FGateClient.websocket.impl.GetPlayers

class GetPlayers : GetPlayers() {
  @Suppress("DEPRECATION")
  override fun getPlayers(): List<PlayerInfo> {
    return Bukkit.getOnlinePlayers().map { player ->
      PlayerInfo(
        name = player.name,
        uuid = player.uniqueId.toString(),
        displayName = player.displayName,
        gameMode = player.gameMode.name,
        world = player.world.name,
        ping = player.ping
      )
    }
  }
}

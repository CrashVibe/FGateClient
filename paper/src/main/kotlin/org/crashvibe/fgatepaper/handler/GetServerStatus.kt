package org.crashvibe.fgatepaper.handler

import org.bukkit.Bukkit
import org.crashvibe.FGateClient.websocket.impl.GetServerStatus

class GetServerStatus : GetServerStatus() {
  override fun getServerStatus(): ServerStatus {
    val worlds = Bukkit.getWorlds().map { world ->
      WorldInfo(name = world.name, playerCount = world.players.size)
    }
    return ServerStatus(
      online = Bukkit.getOnlinePlayers().size,
      max = Bukkit.getMaxPlayers(),
      worlds = worlds,
      tps = Bukkit.getTPS().firstOrNull(),
      mspt = Bukkit.getAverageTickTime()
    )
  }
}

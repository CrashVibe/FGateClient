package org.crashvibe.fgatebukkit.handler

import org.bukkit.Bukkit
import org.crashvibe.FGateClient.websocket.impl.GetServerStatus

class GetServerStatus : GetServerStatus() {
  override fun getServerStatus(): ServerStatus {
    val worlds = Bukkit.getWorlds().map { world ->
      WorldInfo(name = world.name, playerCount = world.players.size)
    }
    // 纯 Spigot/Bukkit API 不提供 TPS/MSPT，省略这两个字段（保持 null）
    return ServerStatus(
      online = Bukkit.getOnlinePlayers().size,
      max = Bukkit.getMaxPlayers(),
      worlds = worlds
    )
  }
}

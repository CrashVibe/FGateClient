package org.crashvibe.fgatepaper.handler

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.crashvibe.FGateClient.websocket.impl.GetPlayers

class GetPlayers : GetPlayers() {
  override fun getPlayers(): List<PlayerInfo> {
    val plain = PlainTextComponentSerializer.plainText()
    return Bukkit.getOnlinePlayers().map { player ->
      PlayerInfo(
        name = player.name,
        uuid = player.uniqueId.toString(),
        displayName = plain.serialize(player.displayName()),
        gameMode = player.gameMode.name,
        world = player.world.name,
        ping = player.ping
      )
    }
  }
}

package org.crashvibe.FGateBukkit.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerLoginEvent
import org.crashvibe.FGateClient.listeners.OnJoinService

class OnJoin : Listener {
  @EventHandler
  fun onQuit(event: PlayerLoginEvent) {
    OnJoinService.handleJoin(event.player.name)
  }
}

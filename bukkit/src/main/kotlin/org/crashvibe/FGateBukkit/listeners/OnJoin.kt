package org.crashvibe.FGateBukkit.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.crashvibe.FGateClient.listeners.OnJoinService

class OnJoin : Listener {
  @EventHandler
  fun onJoin(event: PlayerJoinEvent) {
    OnJoinService.handleJoin(event.player.name)
  }
}

package org.crashvibe.FGateBukkit.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.crashvibe.FGateClient.listeners.OnLeaveService

class OnLeave : Listener {
  @EventHandler
  fun onQuit(event: PlayerQuitEvent) {
    OnLeaveService.handleLeave(event.player.name)
  }
}

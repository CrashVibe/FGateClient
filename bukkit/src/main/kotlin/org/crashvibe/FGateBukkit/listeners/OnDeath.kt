package org.crashvibe.FGateBukkit.listeners

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.crashvibe.FGateClient.listeners.OnDeathService

class OnDeath : Listener {
  @EventHandler
  fun onDeath(event: PlayerDeathEvent) {
    OnDeathService.handleDeath(
      event.entity.name,
      event.deathMessage()?.let { LegacyComponentSerializer.legacySection().serialize(it) }
    )
  }
}

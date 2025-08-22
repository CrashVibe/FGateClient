package org.crashvibe.FGateBukkit.listeners

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.crashvibe.FGateBukkit.FGateBukkit
import org.crashvibe.FGateClient.config.ConfigManager
import org.crashvibe.FGateClient.listeners.OnJoinService

class OnJoin : Listener {
  private val logger = FGateBukkit.instance.logger

  @EventHandler(priority = EventPriority.HIGHEST)
  fun onAsyncPlayerPreLogin(event: AsyncPlayerPreLoginEvent) {
    val config = ConfigManager.configData.eventResolve.join
    val result = OnJoinService.handleJoin(event.name, event.uniqueId.toString(), event.address.hostAddress)

    when (result.action) {
      OnJoinService.Action.kick -> {
        val reason = result.reason ?: config.kickMessage
        event.disallow(
          AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
          LegacyComponentSerializer.legacyAmpersand().deserialize(reason)
        )
        logger.info("玩家 ${event.name} 被远程拒绝: $reason")
      }

      OnJoinService.Action.allow -> {
        logger.info("玩家 ${event.name} 被远程允许加入")
      }
    }
  }
}

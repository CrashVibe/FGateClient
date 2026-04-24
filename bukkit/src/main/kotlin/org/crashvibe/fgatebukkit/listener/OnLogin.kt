package org.crashvibe.fgatebukkit.listener

import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.crashvibe.FGateClient.config.ConfigManager
import org.crashvibe.FGateClient.listeners.OnLoginService
import org.crashvibe.fgatebukkit.FGateBukkit

class OnLogin : Listener {
  private val logger = FGateBukkit.instance.logger

  @EventHandler(priority = EventPriority.HIGHEST)
  fun onAsyncPlayerPreLogin(event: AsyncPlayerPreLoginEvent) {
    val config = ConfigManager.configData.eventResolve.join
    val result = OnLoginService.handleLogin(event.name, event.uniqueId.toString(), event.address.hostAddress)

    when (result.action) {
      OnLoginService.Action.kick -> {
        val reason = result.reason ?: config.kickMessage
        event.disallow(
          AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST,
          ChatColor.translateAlternateColorCodes('&', reason)
        )
        logger.info("玩家 ${event.name} 被远程拒绝: $reason")
      }

      OnLoginService.Action.allow -> {
        logger.info("玩家 ${event.name} 被远程允许加入")
      }
    }
  }
}

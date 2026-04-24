package org.crashvibe.fgatebukkit.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.crashvibe.FGateClient.listeners.OnChatService
import org.crashvibe.fgatebukkit.FGateBukkit

class OnChat : Listener {
  private val logger = FGateBukkit.instance.logger

  @EventHandler
  fun onPlayerChat(event: AsyncPlayerChatEvent) {
    try {
      OnChatService.handleChat(
        playerName = event.player.name,
        playerUUID = event.player.uniqueId.toString(),
        message = event.message
      )
    } catch (e: Exception) {
      logger.severe("处理玩家聊天事件时发生异常: ${e.message}")
    }
  }
}

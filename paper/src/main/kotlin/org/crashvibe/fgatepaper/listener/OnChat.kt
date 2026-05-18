package org.crashvibe.fgatepaper.listener

import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.crashvibe.FGateClient.listeners.OnChatService
import org.crashvibe.fgatepaper.FGatePaper

class OnChat : Listener {
  private val logger = FGatePaper.instance.logger

  @EventHandler
  fun onPlayerChat(event: AsyncChatEvent) {
    val message = PlainTextComponentSerializer.plainText().serialize(event.message())

    try {
      OnChatService.handleChat(
        playerName = event.player.name,
        playerUUID = event.player.uniqueId.toString(),
        message = message,
      )
    } catch (e: Exception) {
      logger.severe("处理玩家聊天事件时发生异常: ${e.message}")
    }
  }
}

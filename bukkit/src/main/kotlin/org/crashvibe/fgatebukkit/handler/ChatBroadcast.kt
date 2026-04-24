package org.crashvibe.fgatebukkit.handler

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.crashvibe.FGateClient.websocket.impl.ChatBroadcast

class ChatBroadcast : ChatBroadcast() {
  override fun broadcast(message: String) {
    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', message))
  }
}

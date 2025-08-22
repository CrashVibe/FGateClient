package org.crashvibe.FGateBukkit.handler

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand
import org.bukkit.Bukkit
import org.crashvibe.FGateClient.websocket.impl.ChatBroadcast

class ChatBroadcast : ChatBroadcast() {
  override fun broadcast(message: String) {
    Bukkit.broadcast(legacyAmpersand().deserialize(message))
  }
}

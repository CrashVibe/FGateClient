package org.crashvibe.fGateClient.service.websocket.impl

import com.crashvibe.fgateclient.handler.RequestHandler
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand
import org.bukkit.Bukkit
import org.crashvibe.fGateClient.service.websocket.JsonRpcRequest

class ChatBroadcast : RequestHandler() {
  override val method: String = "chat.broadcast"

  @Serializable
  data class Message(
    val message: String,
  )

  override fun handle(request: JsonRpcRequest<JsonElement>) {
    val params = parseParams<Message>(request) ?: return
    Bukkit.broadcast(legacyAmpersand().deserialize(params.message))
  }
}

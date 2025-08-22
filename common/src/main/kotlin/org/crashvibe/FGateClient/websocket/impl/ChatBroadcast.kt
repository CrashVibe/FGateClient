package org.crashvibe.FGateClient.websocket.impl

import com.crashvibe.fgateclient.handler.RequestHandler
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import org.crashvibe.FGateClient.websocket.JsonRpcRequest

abstract class ChatBroadcast : RequestHandler() {
  override val method: String = "chat.broadcast"

  @Serializable
  data class Message(
    val message: String,
  )

  override fun handle(request: JsonRpcRequest<JsonElement>) {
    val params = parseParams<Message>(request) ?: return
    broadcast(params.message)
  }

  abstract fun broadcast(message: String)
}

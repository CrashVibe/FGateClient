package org.crashvibe.FGateClient.websocket.impl

import com.crashvibe.fgateclient.handler.RequestHandler
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import org.crashvibe.FGateClient.websocket.JsonRpcRequest
import org.crashvibe.FGateClient.websocket.MethodType

abstract class ChatBroadcast : RequestHandler() {
  override val method: MethodType = MethodType.CHAT_BROADCAST

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

package org.crashvibe.FGateClient.websocket.impl

import com.crashvibe.fgateclient.handler.RequestHandler
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import org.crashvibe.FGateClient.websocket.JsonRpcRequest
import org.crashvibe.FGateClient.websocket.MethodType

abstract class Command : RequestHandler() {
  override val method: MethodType = MethodType.EXECUTE_COMMAND

  @Serializable
  data class Command(
    val command: String,
  )

  @Serializable
  data class CommandResult(
    val success: Boolean,
    val message: String,
  )

  override fun handle(request: JsonRpcRequest<JsonElement>) {
    val params = parseParams<Command>(request) ?: return
    sendResponse(request.id, executeCommand(params.command))
  }

  abstract fun executeCommand(command: String): CommandResult
}

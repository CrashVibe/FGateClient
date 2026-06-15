package org.crashvibe.FGateClient.websocket.impl

import com.crashvibe.fgateclient.handler.RequestHandler
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import org.crashvibe.FGateClient.websocket.JsonRpcRequest
import org.crashvibe.FGateClient.websocket.MethodType

abstract class GetServerStatus : RequestHandler() {
  override val method: MethodType = MethodType.GET_SERVER_STATUS

  @Serializable
  data class WorldInfo(
    val name: String,
    val playerCount: Int
  )

  @Serializable
  data class ServerStatus(
    val online: Int,
    val max: Int,
    val worlds: List<WorldInfo>,
    val tps: Double? = null,
    val mspt: Double? = null
  )

  override fun handle(request: JsonRpcRequest<JsonElement>) {
    sendResponse(request.id, getServerStatus())
  }

  abstract fun getServerStatus(): ServerStatus
}

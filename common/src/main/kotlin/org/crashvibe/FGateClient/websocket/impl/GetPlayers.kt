package org.crashvibe.FGateClient.websocket.impl

import com.crashvibe.fgateclient.handler.RequestHandler
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import org.crashvibe.FGateClient.websocket.JsonRpcRequest
import org.crashvibe.FGateClient.websocket.MethodType

abstract class GetPlayers : RequestHandler() {
  override val method: MethodType = MethodType.GET_PLAYERS

  @Serializable
  data class PlayerInfo(
    val name: String,
    val uuid: String,
    val displayName: String? = null,
    val gameMode: String? = null,
    val world: String? = null,
    val ping: Int? = null
  )

  @Serializable
  data class GetPlayersResponse(
    val players: List<PlayerInfo>
  )

  override fun handle(request: JsonRpcRequest<JsonElement>) {
    sendResponse(request.id, GetPlayersResponse(getPlayers()))
  }

  abstract fun getPlayers(): List<PlayerInfo>
}

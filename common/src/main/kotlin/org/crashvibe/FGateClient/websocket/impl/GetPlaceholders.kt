package org.crashvibe.FGateClient.websocket.impl

import com.crashvibe.fgateclient.handler.RequestHandler
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import org.crashvibe.FGateClient.websocket.JsonRpcRequest
import org.crashvibe.FGateClient.websocket.MethodType

abstract class GetPlaceholders : RequestHandler() {
  override val method: MethodType = MethodType.GET_PLACEHOLDERS

  @Serializable
  data class PlayerRef(
    val name: String,
    val uuid: String
  )

  @Serializable
  data class PlaceholderRequest(
    val player: PlayerRef,
    val placeholders: List<String>
  )

  @Serializable
  data class GetPlaceholdersParams(
    val requests: List<PlaceholderRequest>
  )

  /** player 字段回传请求中的 uuid，供宿主侧按 uuid 映射回玩家 */
  @Serializable
  data class PlaceholderResult(
    val player: String,
    val values: Map<String, String>
  )

  @Serializable
  data class GetPlaceholdersResponse(
    val results: List<PlaceholderResult>
  )

  override fun handle(request: JsonRpcRequest<JsonElement>) {
    val params = parseParams<GetPlaceholdersParams>(request)
    if (params == null) {
      sendResponse(request.id, GetPlaceholdersResponse(emptyList()))
      return
    }
    sendResponse(
      request.id,
      GetPlaceholdersResponse(resolvePlaceholders(params.requests))
    )
  }

  abstract fun resolvePlaceholders(
    requests: List<PlaceholderRequest>
  ): List<PlaceholderResult>
}

package org.crashvibe.FGateClient.websocket.impl

import com.crashvibe.fgateclient.handler.RequestHandler
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import org.crashvibe.FGateClient.FGateClient.logger
import org.crashvibe.FGateClient.websocket.JsonRpcRequest
import org.crashvibe.FGateClient.websocket.JsonRpcResponse
import org.crashvibe.FGateClient.websocket.MethodType

abstract class KickPlayer : RequestHandler() {
  override val method: MethodType = MethodType.KICK_PLAYER

  @Serializable
  data class KickPlayerParams(
    val playerUUID: String? = null,
    val playerName: String? = null,
    val reason: String
  )

  @Serializable
  data class KickPlayerResponse(
    val success: Boolean,
    val message: String
  )

  override fun handle(request: JsonRpcRequest<JsonElement>) {
    try {
      val kickParams = parseParams<KickPlayerParams>(request) ?: return
      logger.info("踢出玩家: ${kickParams.playerName ?: kickParams.playerUUID}, 原因: ${kickParams.reason}")

      executeKick(request, kickParams)
    } catch (e: Exception) {
      sendInvalidParamsError(request.id, "参数解析失败: ${e.message}")
    }
  }

  abstract fun executeKick(request: JsonRpcRequest<JsonElement>, params: KickPlayerParams)

  private fun sendInvalidParamsError(requestId: String?, data: String) {
    sendError<String>(
      requestId,
      JsonRpcResponse.JsonRpcError(
        code = -32602,
        message = "Invalid params",
        data = data
      )
    )
  }
}

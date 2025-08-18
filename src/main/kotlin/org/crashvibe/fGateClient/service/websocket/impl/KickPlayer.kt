package org.crashvibe.fGateClient.service.websocket.impl

import com.crashvibe.fgateclient.handler.RequestHandler
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.crashvibe.fGateClient.FGateClient
import org.crashvibe.fGateClient.service.websocket.JsonRpcRequest
import org.crashvibe.fGateClient.service.websocket.JsonRpcResponse
import java.util.*

class KickPlayer : RequestHandler() {
  override val method: String = "kick.player"

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

      Bukkit.getScheduler().runTask(FGateClient.instance, Runnable {
        executeKick(request, kickParams)
      })
    } catch (e: Exception) {
      sendInvalidParamsError(request.id, "参数解析失败: ${e.message}")
    }
  }

  private fun executeKick(request: JsonRpcRequest<JsonElement>, params: KickPlayerParams) {
    val player = findPlayer(request, params) ?: return

    player.kick(legacyAmpersand().deserialize(params.reason))
    sendResponse(
      request.id,
      KickPlayerResponse(
        success = true,
        message = "玩家 ${player.name} 已被踢出, 原因: ${params.reason}"
      )
    )
  }

  private fun findPlayer(request: JsonRpcRequest<JsonElement>, params: KickPlayerParams): Player? {
    return when {
      params.playerUUID != null -> findPlayerByUUID(request, params.playerUUID)
      params.playerName != null -> findPlayerByName(request, params)
      else -> {
        sendInvalidParamsError(request.id, "缺少 playerUUID 或 playerName 参数")
        null
      }
    }
  }

  private fun findPlayerByUUID(request: JsonRpcRequest<JsonElement>, playerUUID: String): Player? {
    return try {
      val uuid = UUID.fromString(playerUUID)
      Bukkit.getPlayer(uuid)
    } catch (_: IllegalArgumentException) {
      sendInvalidParamsError(request.id, "无效的 UUID 格式: $playerUUID")
      null
    }
  }

  private fun findPlayerByName(request: JsonRpcRequest<JsonElement>, params: KickPlayerParams): Player? {
    val player = Bukkit.getPlayerExact(params.playerName!!)
    if (player == null) {
      sendResponse(
        request.id,
        KickPlayerResponse(
          success = false,
          message = "无法找到玩家: ${params.playerName}"
        )
      )
    }
    return player
  }

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

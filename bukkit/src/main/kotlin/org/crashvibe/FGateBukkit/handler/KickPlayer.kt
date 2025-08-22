package org.crashvibe.FGateBukkit.handler

import kotlinx.serialization.json.JsonElement
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.crashvibe.FGateClient.websocket.JsonRpcRequest
import org.crashvibe.FGateClient.websocket.JsonRpcResponse
import org.crashvibe.FGateClient.websocket.impl.KickPlayer
import java.util.*

class KickPlayer : KickPlayer() {
  override fun executeKick(request: JsonRpcRequest<JsonElement>, params: KickPlayerParams) {
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
      params.playerUUID != null -> findPlayerByUUID(request, params.playerUUID!!)
      params.playerName != null -> findPlayerByName(request, params.playerName!!)
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

  private fun findPlayerByName(request: JsonRpcRequest<JsonElement>, playerName: String): Player? {
    val player = Bukkit.getPlayerExact(playerName)
    if (player == null) {
      sendResponse(
        request.id,
        KickPlayerResponse(
          success = false,
          message = "无法找到玩家: ${playerName}"
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

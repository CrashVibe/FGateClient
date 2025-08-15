package org.crashvibe.fGateClient.service.websocket.impl

import com.crashvibe.fgateclient.handler.RequestHandler
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.serializer
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand
import org.bukkit.Bukkit
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
      val kickParams = request.params?.let {
        Json.decodeFromJsonElement(serializer<KickPlayerParams>(), it)
      }

      if (kickParams != null) {
        logger.info("踢出玩家: ${kickParams.playerName ?: kickParams.playerUUID}, 原因: ${kickParams.reason}")

        Bukkit.getScheduler().runTask(FGateClient.instance, Runnable {
          val player = if (kickParams.playerUUID != null) {
            try {
              Bukkit.getPlayer(UUID.fromString(kickParams.playerUUID))
            } catch (_: IllegalArgumentException) {
              sendError<String>(
                request.id, JsonRpcResponse.JsonRpcError(
                  code = -32602,
                  message = "Invalid params",
                  data = "无效的 UUID 格式: ${kickParams.playerUUID}"
                )
              )
              return@Runnable
            }
          } else if (kickParams.playerName != null) {
            Bukkit.getPlayerExact(kickParams.playerName)
          } else {
            sendError<String>(
              request.id, JsonRpcResponse.JsonRpcError(
                code = -32602,
                message = "Invalid params",
                data = "缺少 playerUUID 或 playerName 参数"
              )
            )
            return@Runnable
          }

          if (player != null) {
            player.kick(legacyAmpersand().deserialize(kickParams.reason))
            sendResponse(
              request.id,
              KickPlayerResponse(success = true, message = "玩家 ${player.name} 已被踢出, 原因: ${kickParams.reason}")
            )
          } else {
            sendResponse(
              request.id,
              KickPlayerResponse(
                success = false,
                message = "无法找到玩家: ${kickParams.playerName ?: kickParams.playerUUID}"
              )
            )
          }
        })
      } else {
        sendError<String>(
          request.id, JsonRpcResponse.JsonRpcError(
            code = -32602,
            message = "Invalid params",
            data = "Missing parameters"
          )
        )
      }
    } catch (e: Exception) {
      sendError<String>(
        request.id, JsonRpcResponse.JsonRpcError(
          code = -32602,
          message = "Invalid params",
          data = "参数解析失败: ${e.message}"
        )
      )
    }
  }
}

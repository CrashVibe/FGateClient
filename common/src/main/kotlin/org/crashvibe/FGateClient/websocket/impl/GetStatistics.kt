package org.crashvibe.FGateClient.websocket.impl

import com.crashvibe.fgateclient.handler.RequestHandler
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import org.crashvibe.FGateClient.websocket.JsonRpcRequest
import org.crashvibe.FGateClient.websocket.MethodType

/**
 * 获取玩家 Vanilla 统计（org.bukkit.Statistic）。
 * 仅支持无需额外参数的统计（Statistic.Type.UNTYPED），如 DEATHS / PLAYER_KILLS /
 * MOB_KILLS / PLAY_ONE_MINUTE / WALK_ONE_CM 等；未知或需参数的统计名会被跳过。
 * 与 get.placeholders 一致：按 uuid 取离线玩家数据，player 字段回传请求 uuid 供宿主映射。
 */
abstract class GetStatistics : RequestHandler() {
  override val method: MethodType = MethodType.GET_STATISTICS

  @Serializable
  data class PlayerRef(
    val name: String,
    val uuid: String
  )

  @Serializable
  data class StatisticsRequest(
    val player: PlayerRef,
    val statistics: List<String>
  )

  @Serializable
  data class StatisticsParams(
    val requests: List<StatisticsRequest>
  )

  /** player 字段回传请求中的 uuid，供宿主侧按 uuid 映射回玩家 */
  @Serializable
  data class StatisticsResult(
    val player: String,
    val values: Map<String, Long>
  )

  @Serializable
  data class GetStatisticsResponse(
    val results: List<StatisticsResult>
  )

  override fun handle(request: JsonRpcRequest<JsonElement>) {
    val params = parseParams<StatisticsParams>(request)
    if (params == null) {
      sendResponse(request.id, GetStatisticsResponse(emptyList()))
      return
    }
    sendResponse(
      request.id,
      GetStatisticsResponse(resolveStatistics(params.requests))
    )
  }

  abstract fun resolveStatistics(
    requests: List<StatisticsRequest>
  ): List<StatisticsResult>
}

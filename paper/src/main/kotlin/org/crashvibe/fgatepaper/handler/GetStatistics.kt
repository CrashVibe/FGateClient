package org.crashvibe.fgatepaper.handler

import org.bukkit.Bukkit
import org.bukkit.Statistic
import org.crashvibe.FGateClient.websocket.impl.GetStatistics
import java.util.UUID

class GetStatistics : GetStatistics() {
  override fun resolveStatistics(
    requests: List<StatisticsRequest>
  ): List<StatisticsResult> = requests.map { req ->
    val player = Bukkit.getOfflinePlayer(UUID.fromString(req.player.uuid))
    val values = LinkedHashMap<String, Long>()
    for (name in req.statistics) {
      val stat = runCatching { Statistic.valueOf(name) }.getOrNull() ?: continue
      // 仅支持无需额外参数的统计（UNTYPED），如 DEATHS / PLAYER_KILLS / PLAY_ONE_MINUTE
      if (stat.type != Statistic.Type.UNTYPED) continue
      values[name] = runCatching { player.getStatistic(stat).toLong() }.getOrDefault(0L)
    }
    StatisticsResult(player = req.player.uuid, values = values)
  }
}

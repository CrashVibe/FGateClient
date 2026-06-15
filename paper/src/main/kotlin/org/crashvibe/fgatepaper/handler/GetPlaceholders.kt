package org.crashvibe.fgatepaper.handler

import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.Bukkit
import org.crashvibe.FGateClient.websocket.impl.GetPlaceholders
import org.crashvibe.fgatepaper.FGatePaper
import java.util.UUID
import java.util.concurrent.CountDownLatch

class GetPlaceholders : GetPlaceholders() {
  override fun resolvePlaceholders(
    requests: List<PlaceholderRequest>
  ): List<PlaceholderResult> {
    val results = mutableListOf<PlaceholderResult>()
    val latch = CountDownLatch(1)
    Bukkit.getScheduler().runTask(FGatePaper.instance, Runnable {
      try {
        for (req in requests) {
          val offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(req.player.uuid))
          val values = LinkedHashMap<String, String>()
          for (placeholder in req.placeholders) {
            values[placeholder] = PlaceholderAPI.setPlaceholders(offlinePlayer, "%$placeholder%")
          }
          results.add(PlaceholderResult(player = req.player.uuid, values = values))
        }
      } finally {
        latch.countDown()
      }
    })
    latch.await()
    return results
  }
}

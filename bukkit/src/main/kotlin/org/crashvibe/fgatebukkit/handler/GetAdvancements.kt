package org.crashvibe.fgatebukkit.handler

import org.bukkit.Bukkit
import org.crashvibe.FGateClient.websocket.impl.GetAdvancements
import java.util.UUID

class GetAdvancements : GetAdvancements() {
  override fun resolveAdvancements(
    requests: List<AdvancementsRequest>
  ): List<AdvancementsResult> = requests.map { req ->
    val player = Bukkit.getPlayer(UUID.fromString(req.player.uuid))
    if (player == null) {
      AdvancementsResult(req.player.uuid, false, 0, 0, emptyList())
    } else {
      val entries = ArrayList<AdvancementEntry>()
      val iterator = Bukkit.advancementIterator()
      while (iterator.hasNext()) {
        val adv = iterator.next()
        val key = adv.key.toString()
        // 跳过配方类成就
        if (key.startsWith("minecraft:recipes/") || key.contains("/recipes/")) continue
        // 只收录有展示信息（成就界面可见、带图标）的成就
        val display = adv.display ?: continue
        val material = display.icon.type
        entries.add(
          AdvancementEntry(
            key = key,
            done = player.getAdvancementProgress(adv).isDone,
            name = display.title,
            icon = material.name.lowercase(),
            block = material.isBlock
          )
        )
      }
      AdvancementsResult(
        player = req.player.uuid,
        online = true,
        total = entries.size,
        completed = entries.count { it.done },
        advancements = entries
      )
    }
  }
}

package org.crashvibe.fgatebukkit.handler

import org.bukkit.Bukkit
import org.bukkit.inventory.ItemStack
import org.crashvibe.FGateClient.websocket.impl.GetEquipment
import java.util.UUID

class GetEquipment : GetEquipment() {
  override fun resolveEquipment(
    requests: List<EquipmentRequest>
  ): List<EquipmentResult> = requests.map { req ->
    val player = Bukkit.getPlayer(UUID.fromString(req.player.uuid))
    if (player == null) {
      EquipmentResult(req.player.uuid, false, emptyList())
    } else {
      val inv = player.inventory
      val slots = listOf(
        "helmet" to inv.helmet,
        "chestplate" to inv.chestplate,
        "leggings" to inv.leggings,
        "boots" to inv.boots,
        "mainHand" to inv.itemInMainHand,
        "offHand" to inv.itemInOffHand
      )
      EquipmentResult(
        player = req.player.uuid,
        online = true,
        items = slots.mapNotNull { (slot, item) -> toItemInfo(slot, item) }
      )
    }
  }

  private fun toItemInfo(slot: String, item: ItemStack?): ItemInfo? {
    if (item == null || item.type.isAir) {
      return null
    }
    return ItemInfo(
      slot = slot,
      type = item.type.name,
      amount = item.amount,
      enchantments = item.enchantments.map { (ench, level) ->
        Enchant(name = ench.key.key, level = level)
      }
    )
  }
}

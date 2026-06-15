package org.crashvibe.FGateClient.websocket.impl

import com.crashvibe.fgateclient.handler.RequestHandler
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import org.crashvibe.FGateClient.websocket.JsonRpcRequest
import org.crashvibe.FGateClient.websocket.MethodType

/**
 * 获取玩家当前装备（盔甲 + 主副手）。
 * 受 Bukkit API 限制：仅能读取**在线**玩家（PlayerInventory 需 online Player），
 * 玩家离线时 online=false、列表为空。空槽位会被省略。
 */
abstract class GetEquipment : RequestHandler() {
  override val method: MethodType = MethodType.GET_EQUIPMENT

  @Serializable
  data class PlayerRef(
    val name: String,
    val uuid: String
  )

  @Serializable
  data class EquipmentRequest(
    val player: PlayerRef
  )

  @Serializable
  data class EquipmentParams(
    val requests: List<EquipmentRequest>
  )

  @Serializable
  data class Enchant(
    val name: String,
    val level: Int
  )

  @Serializable
  data class ItemInfo(
    /** helmet / chestplate / leggings / boots / mainHand / offHand */
    val slot: String,
    /** Material 枚举名，如 DIAMOND_CHESTPLATE */
    val type: String,
    val amount: Int,
    val enchantments: List<Enchant> = emptyList()
  )

  @Serializable
  data class EquipmentResult(
    val player: String,
    val online: Boolean,
    val items: List<ItemInfo>
  )

  @Serializable
  data class GetEquipmentResponse(
    val results: List<EquipmentResult>
  )

  override fun handle(request: JsonRpcRequest<JsonElement>) {
    val params = parseParams<EquipmentParams>(request)
    if (params == null) {
      sendResponse(request.id, GetEquipmentResponse(emptyList()))
      return
    }
    sendResponse(
      request.id,
      GetEquipmentResponse(resolveEquipment(params.requests))
    )
  }

  abstract fun resolveEquipment(
    requests: List<EquipmentRequest>
  ): List<EquipmentResult>
}

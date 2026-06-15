package org.crashvibe.FGateClient.websocket.impl

import com.crashvibe.fgateclient.handler.RequestHandler
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import org.crashvibe.FGateClient.websocket.JsonRpcRequest
import org.crashvibe.FGateClient.websocket.MethodType

/**
 * 获取玩家成就进度。
 * 受 Bukkit API 限制：仅能读取**在线**玩家（getAdvancementProgress 需 online Player），
 * 玩家离线时 online=false、列表为空。配方类成就（minecraft:recipes/...）会被过滤。
 */
abstract class GetAdvancements : RequestHandler() {
  override val method: MethodType = MethodType.GET_ADVANCEMENTS

  @Serializable
  data class PlayerRef(
    val name: String,
    val uuid: String
  )

  @Serializable
  data class AdvancementsRequest(
    val player: PlayerRef
  )

  @Serializable
  data class AdvancementsParams(
    val requests: List<AdvancementsRequest>
  )

  @Serializable
  data class AdvancementEntry(
    val key: String,
    val done: Boolean,
    /** 成就标题（服务器默认语言解析后的纯文本），无展示信息时为 null */
    val name: String? = null,
    /** 展示图标的 Material 名（小写），供宿主映射到物品贴图；无展示信息时为 null */
    val icon: String? = null,
    /** 图标是否为方块（决定贴图路径 textures/block 还是 textures/item） */
    val block: Boolean = false
  )

  @Serializable
  data class AdvancementsResult(
    val player: String,
    val online: Boolean,
    val total: Int,
    val completed: Int,
    val advancements: List<AdvancementEntry>
  )

  @Serializable
  data class GetAdvancementsResponse(
    val results: List<AdvancementsResult>
  )

  override fun handle(request: JsonRpcRequest<JsonElement>) {
    val params = parseParams<AdvancementsParams>(request)
    if (params == null) {
      sendResponse(request.id, GetAdvancementsResponse(emptyList()))
      return
    }
    sendResponse(
      request.id,
      GetAdvancementsResponse(resolveAdvancements(params.requests))
    )
  }

  abstract fun resolveAdvancements(
    requests: List<AdvancementsRequest>
  ): List<AdvancementsResult>
}

package org.crashvibe.FGateClient.config

import de.exlll.configlib.Comment
import de.exlll.configlib.Configuration
import de.exlll.configlib.YamlConfigurations
import java.nio.file.Path

/**
 * 插件配置数据结构
 */
@Configuration
data class PluginConfig(
  @Comment("是否开启调试模式", "其实就是详细的日志输出，告诉你我真的干活没偷懒啦！") var debug: Boolean = false,
  @Comment("连接配置") var websocket: WebSocketConfig = WebSocketConfig(),
  @Comment("事件解析配置") var eventResolve: EventResolve = EventResolve(),
) {
  @Configuration
  data class WebSocketConfig(
    @Comment("连接地址") var url: String = "ws://127.0.0.1:3000/api",
    @Comment("认证令牌") var token: String = "your-auth-token-here",
    @Comment("是否重连") var autoReconnect: Boolean = true,
    @Comment("重连间隔（秒）") var reconnectInterval: Long = 10,
  )

  @Configuration
  data class EventResolve(
    var join: JoinEvent = JoinEvent(),
  ) {
    @Configuration
    data class JoinEvent(
      @Comment("是否允许未连接状态下玩家加入服务器") var allowJoin: Boolean = true,
      @Comment("踢出服务器时的提示") var kickMessage: String = "服务器尚未准备好，请稍后再试",
      @Comment(
        "是否允许处理发生错误但仍然允许玩家加入服务器",
      ) var errorJoin: Boolean = true,
      @Comment(
        "处理玩家加入服务器时发生错误的提示",
      ) var errorMessage: String = "服务器发生错误，请稍后再试",
    )
  }
}


/**
 * 配置管理器
 */
object ConfigManager {
  lateinit var configData: PluginConfig
    private set

  fun init(configFile: Path) {
    configData = YamlConfigurations.update(
      configFile,
      PluginConfig::class.java,
    )
  }
}

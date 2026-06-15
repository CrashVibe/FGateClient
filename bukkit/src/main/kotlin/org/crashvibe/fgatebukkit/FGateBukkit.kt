package org.crashvibe.fgatebukkit

import com.crashvibe.fgateclient.handler.RequestHandler
import com.crashvibe.fgateclient.handler.impl.GetClientInfo
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.crashvibe.FGateClient.FGateClient
import org.crashvibe.FGateClient.listeners.requiredListeners
import org.crashvibe.fgatebukkit.handler.ChatBroadcast
import org.crashvibe.fgatebukkit.handler.Command
import org.crashvibe.fgatebukkit.handler.GetAdvancements
import org.crashvibe.fgatebukkit.handler.GetEquipment
import org.crashvibe.fgatebukkit.handler.GetPlaceholders
import org.crashvibe.fgatebukkit.handler.GetPlayers
import org.crashvibe.fgatebukkit.handler.GetServerStatus
import org.crashvibe.fgatebukkit.handler.GetStatistics
import org.crashvibe.fgatebukkit.handler.KickPlayer
import org.crashvibe.fgatebukkit.listener.BukkitPlatformListenerContract
import org.crashvibe.fgatebukkit.utils.registerEvents
import java.io.File


class FGateBukkit : JavaPlugin() {
  override fun onLoad() {
    instance = this
    Metrics(this, 26997)
  }

  override fun onEnable() {
    val hasPapi = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null
    val handlers = mutableListOf<RequestHandler>(
      GetClientInfo(
        GetClientInfo.ClientInfo(
          minecraft_version = Bukkit.getVersion(),
          minecraft_software = Bukkit.getName(),
          supports_papi = hasPapi,
          supports_command = true,
          player_count = Bukkit.getOnlinePlayers().size,
          capabilities = GetClientInfo.Capabilities(
            players = true,
            server_status = true,
            statistics = true,
            advancements = true,
            equipment = true
          )
        )
      ),
      KickPlayer(), ChatBroadcast(), Command(),
      GetPlayers(), GetServerStatus(), GetStatistics(),
      GetAdvancements(), GetEquipment()
    )
    if (hasPapi) {
      handlers.add(GetPlaceholders())
    }
    FGateClient.init(
      File(dataFolder, "config.yml").toPath(), logger,
      *handlers.toTypedArray()
    )

    initListeners()
  }

  override fun onDisable() {
    FGateClient.disable()
  }

  private fun initListeners() {
    val listeners = requiredListeners(BukkitPlatformListenerContract)
    registerEvents(this, *listeners.toTypedArray())
  }

  companion object {
    lateinit var instance: FGateBukkit
      private set
  }
}

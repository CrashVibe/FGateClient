package org.crashvibe.fgatebukkit

import com.crashvibe.fgateclient.handler.impl.GetClientInfo
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.crashvibe.FGateClient.FGateClient
import org.crashvibe.FGateClient.listeners.requiredListeners
import org.crashvibe.fgatebukkit.handler.ChatBroadcast
import org.crashvibe.fgatebukkit.handler.Command
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
    FGateClient.init(File(dataFolder, "config.yml").toPath(), logger,
      GetClientInfo(
        GetClientInfo.ClientInfo(
          minecraft_version = Bukkit.getVersion(),
          minecraft_software = Bukkit.getName(),
          supports_papi = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null,
          supports_command = true,
          player_count = Bukkit.getOnlinePlayers().size
        )
      ),
      KickPlayer(), ChatBroadcast(), Command()
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

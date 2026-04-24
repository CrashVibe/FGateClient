package org.crashvibe.fgatepaper

import com.crashvibe.fgateclient.handler.impl.GetClientInfo
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.crashvibe.FGateClient.FGateClient
import org.crashvibe.FGateClient.listeners.requiredListeners
import org.crashvibe.fgatepaper.handler.ChatBroadcast
import org.crashvibe.fgatepaper.handler.Command
import org.crashvibe.fgatepaper.handler.KickPlayer
import org.crashvibe.fgatepaper.listener.PaperPlatformListenerContract
import org.crashvibe.fgatepaper.utils.registerEvents
import java.io.File


class FGatePaper : JavaPlugin() {
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
    val listeners = requiredListeners(PaperPlatformListenerContract)
    registerEvents(this, *listeners.toTypedArray())
  }

  companion object {
    lateinit var instance: FGatePaper
      private set
  }
}

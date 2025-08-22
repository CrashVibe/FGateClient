package org.crashvibe.FGateBukkit

import com.crashvibe.fgateclient.handler.impl.GetClientInfo
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.crashvibe.FGateBukkit.handler.ChatBroadcast
import org.crashvibe.FGateBukkit.handler.KickPlayer
import org.crashvibe.FGateBukkit.listeners.OnChat
import org.crashvibe.FGateBukkit.listeners.OnJoin
import org.crashvibe.FGateBukkit.utils.Command
import org.crashvibe.FGateBukkit.utils.registerEvents
import org.crashvibe.FGateClient.FGateClient
import org.crashvibe.FGateClient.websocket.RequestDispatcher
import java.io.File


class FGateBukkit : JavaPlugin() {
  override fun onLoad() {
    instance = this
    Metrics(this, 26997)
  }

  override fun onEnable() {
    FGateClient.init(File(dataFolder, "config.yml").toPath(), logger)
    RequestDispatcher.registerHandler(
      GetClientInfo(
        GetClientInfo.ClientInfo(
          minecraft_version = Bukkit.getVersion(),
          minecraft_software = Bukkit.getName(),
          supports_papi = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null,
          supports_command = Command.isAvailable,
          player_count = Bukkit.getOnlinePlayers().size
        )
      ),
      KickPlayer(), ChatBroadcast()
    )
    initListeners()
  }

  override fun onDisable() {
    FGateClient.disable()
  }

  private fun initListeners() {
    registerEvents(this, OnJoin(), OnChat())
  }

  companion object {
    lateinit var instance: FGateBukkit
      private set
  }
}

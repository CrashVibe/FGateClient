package org.crashvibe.fGateClient

import PluginCoroutineScope
import org.bstats.bukkit.Metrics
import org.bukkit.plugin.java.JavaPlugin
import org.crashvibe.fGateClient.listeners.OnChat
import org.crashvibe.fGateClient.listeners.OnJoin
import org.crashvibe.fGateClient.service.ServiceManager
import org.crashvibe.fGateClient.utils.registerEvents


class FGateClient : JavaPlugin() {
  lateinit var pluginScope: PluginCoroutineScope
    private set

  override fun onLoad() {
    instance = this
    Metrics(this, 26997)
  }

  override fun onEnable() {
    pluginScope = PluginCoroutineScope()
    ServiceManager.init(this)
    initListeners()
  }

  override fun onDisable() {
    pluginScope.cancelAll()
  }

  private fun initListeners() {
    registerEvents(this, OnJoin(), OnChat())
  }

  companion object {
    lateinit var instance: FGateClient
      private set
  }
}

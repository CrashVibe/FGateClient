package org.crashvibe.fgatebukkit.listener

import org.bukkit.event.Listener
import org.crashvibe.FGateClient.listeners.PlatformListenerContract

object BukkitPlatformListenerContract : PlatformListenerContract<Listener> {
  override fun onLoginListener(): Listener = OnLogin()

  override fun onChatListener(): Listener = OnChat()

  override fun onLeaveListener(): Listener = OnLeave()

  override fun onJoinListener(): Listener = OnJoin()

  override fun onDeathListener(): Listener = OnDeath()
}

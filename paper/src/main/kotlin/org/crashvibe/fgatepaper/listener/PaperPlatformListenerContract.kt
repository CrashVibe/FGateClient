package org.crashvibe.fgatepaper.listener

import org.bukkit.event.Listener
import org.crashvibe.FGateClient.listeners.PlatformListenerContract

object PaperPlatformListenerContract : PlatformListenerContract<Listener> {
  override fun onLoginListener(): Listener = OnLogin()

  override fun onChatListener(): Listener = OnChat()

  override fun onLeaveListener(): Listener = OnLeave()

  override fun onJoinListener(): Listener = OnJoin()

  override fun onDeathListener(): Listener = OnDeath()
}

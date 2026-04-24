package org.crashvibe.FGateClient.listeners

/**
 * 强制平台实现 common 里所有监听器。
 * 新增事件时只需要在这里追加方法，平台实现会在编译期被强制补齐。
 */
interface PlatformListenerContract<TListener> {
  fun onLoginListener(): TListener
  fun onChatListener(): TListener
  fun onLeaveListener(): TListener
  fun onJoinListener(): TListener
  fun onDeathListener(): TListener
}

fun <TListener> requiredListeners(contract: PlatformListenerContract<TListener>): List<TListener> {
  return listOf(
    contract.onLoginListener(),
    contract.onChatListener(),
    contract.onLeaveListener(),
    contract.onJoinListener(),
    contract.onDeathListener()
  )
}

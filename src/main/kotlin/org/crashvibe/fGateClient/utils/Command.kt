package org.crashvibe.fGateClient.utils

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

@Suppress("JavaDefaultMethodsNotOverriddenByDelegation")
class CapturingCommandSender(private val original: CommandSender) : CommandSender by original {
  val messages = mutableListOf<String>()

  override fun sendMessage(message: String) {
    messages.add(message) // 收集输出
  }

  override fun sendMessage(messages: Array<out String>) {
    this.messages.addAll(messages)
  }
}

object Command {
  val isAvailable: Boolean = true

  fun executeCommand(command: String): String {
    val console = Bukkit.getServer().consoleSender
    val capturingSender = CapturingCommandSender(console)

    Bukkit.dispatchCommand(capturingSender, command)
    return capturingSender.messages.joinToString("\n")
  }
}

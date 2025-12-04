package org.crashvibe.FGateBukkit.handler

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.crashvibe.FGateBukkit.FGateBukkit
import org.crashvibe.FGateClient.websocket.impl.Command
import java.util.function.Consumer


class Command : Command() {
  override fun executeCommand(command: String): CommandResult {
    val messages: MutableList<String> = mutableListOf()
    val sender = Bukkit.createCommandSender(Consumer { feedback: Component ->
      messages.add(PlainTextComponentSerializer.plainText().serialize(feedback))
    })
    val latch = java.util.concurrent.CountDownLatch(1)
    var success = false
    Bukkit.getScheduler().runTask(FGateBukkit.instance, Runnable {
      success = Bukkit.dispatchCommand(sender, command)
      latch.countDown()
    })
    latch.await()
    return CommandResult(
      success,
      messages.joinToString("\n")
    )
  }
}

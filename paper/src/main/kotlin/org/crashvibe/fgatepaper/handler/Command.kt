package org.crashvibe.fgatepaper.handler

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.crashvibe.FGateClient.websocket.impl.Command
import org.crashvibe.fgatepaper.FGatePaper
import java.util.concurrent.CountDownLatch
import java.util.function.Consumer


class Command : Command() {
  override fun executeCommand(command: String, need_color: Boolean): CommandResult {
    val messages: MutableList<String> = mutableListOf()
    val sender = Bukkit.createCommandSender(Consumer { feedback: Component ->
      val serialized = if (need_color) {
        LegacyComponentSerializer.legacyAmpersand().serialize(feedback)
      } else {
        PlainTextComponentSerializer.plainText().serialize(feedback)
      }
      messages.add(serialized)
    })
    val latch = CountDownLatch(1)
    var success = false
    Bukkit.getScheduler().runTask(FGatePaper.instance, Runnable {
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

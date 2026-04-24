package org.crashvibe.fgatebukkit.handler

import org.bukkit.Bukkit
import org.crashvibe.FGateClient.websocket.impl.Command
import org.crashvibe.fgatebukkit.FGateBukkit
import java.util.concurrent.CountDownLatch

class Command : Command() {
  override fun executeCommand(command: String, need_color: Boolean): CommandResult {
    val latch = CountDownLatch(1)
    var success = false
    Bukkit.getScheduler().runTask(FGateBukkit.instance, Runnable {
      success = Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command)
      latch.countDown()
    })
    latch.await()
    return CommandResult(
      success,
      if (success) "命令已执行" else "命令执行失败"
    )
  }
}

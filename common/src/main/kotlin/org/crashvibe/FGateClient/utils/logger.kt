package org.crashvibe.FGateClient.utils

import org.crashvibe.FGateClient.config.ConfigManager
import java.util.logging.Logger

fun Logger.debug(msg: String) {
  if (ConfigManager.configData.debug) {
    this.info("[DEBUG] $msg")
  }
}

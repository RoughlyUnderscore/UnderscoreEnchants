// Copyright 2024 RoughlyUnderscore
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.roughlyunderscore.ue.utils

import com.roughlyunderscore.annotations.Since
import com.roughlyunderscore.ulib.log.TogglableLogger
import org.bukkit.plugin.java.JavaPlugin

/**
 * Shuts down the plugin.
 */
@Since("2.2")
fun JavaPlugin.shutdown(message: String) {
  logger.severe(message)
  logger.severe("If you believe this is an error, please report this message and the stacktrace (if present) to the support Discord of UnderscoreEnchants.")
  this.server.pluginManager.disablePlugin(this)
}

/**
 * Tracks the execution time of a [function], logs it with [logger] and returns the result.
 * - [doingWhat] is <action> in the following:
 * - [started] is "Started <action>."
 * - [finished] is "Finished <action>. Took <ms>ms."
 */
@Since("2.2")
fun <R> trackTime(doingWhat: String, logger: TogglableLogger, started: String, finished: String, function: () -> R): R {
  val startTime = System.currentTimeMillis()
  logger.commit { info(started.replace("<action>", doingWhat)) }

  val result = function.invoke()

  logger.commit { info(finished.replace("<action>", doingWhat).replace("<ms>", (System.currentTimeMillis() - startTime).toString())) }
  logger.commit { info("====================================================") }
  return result
}
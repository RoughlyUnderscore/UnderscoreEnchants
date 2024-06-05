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
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

object TaskUtils {
  /**
   * Runs [action] with a delay of [ticks] on behalf of [plugin].
   */
  @Since("2.2")
  fun delayed(ticks: Int, plugin: JavaPlugin, action: () -> Unit) {
    if (ticks == 0) action.invoke()
    else Bukkit.getScheduler().runTaskLater(plugin, action, ticks.toLong())
  }

  /**
   * Runs [action] with a delay of [ticks] on behalf of [plugin].
   */
  @Since("2.2")
  fun delayed(ticks: Long, plugin: JavaPlugin, action: () -> Unit) {
    if (ticks == 0L) action.invoke()
    else Bukkit.getScheduler().runTaskLater(plugin, action, ticks.toLong())
  }

  /**
   * Immediately runs an asynchronous [action] on behalf of [plugin].
   */
  @Since("2.2")
  fun async(plugin: JavaPlugin, action: () -> Unit) {
    Bukkit.getScheduler().runTaskAsynchronously(plugin, action)
  }
}
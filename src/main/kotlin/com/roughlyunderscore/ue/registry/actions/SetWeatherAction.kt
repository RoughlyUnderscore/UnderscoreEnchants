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

package com.roughlyunderscore.ue.registry.actions

import com.roughlyunderscore.annotations.Since
import com.roughlyunderscore.annotations.Stable
import com.roughlyunderscore.enums.TargetType
import com.roughlyunderscore.registry.RegistrableAction
import com.roughlyunderscore.data.EventModifications
import com.roughlyunderscore.registry.RegistrableTrigger
import org.bukkit.Bukkit
import org.bukkit.event.Event

/**
 * Sets weather at a world
 *
 * Example:
 * - `set-weather sun world`
 *
 * Syntax:
 * - `set-weather WEATHER WORLD`
 * - The weather argument excepts words that start with either `s` (for sun), `r` (for rain) or `t` (for thunder).
 */
@Since("2.2")
@Stable
class SetWeatherAction : RegistrableAction {
  override val aliases = listOf("set-weather", "change-weather", "weather")

  override fun execute(event: Event, trigger: RegistrableTrigger, arguments: List<String>, target: TargetType): EventModifications? {
    if (arguments.size < 2) return null

    val weather = arguments[0]
    val world = Bukkit.getWorld(arguments[1]) ?: return null

    when (weather.lowercase()[0]) {
      's' -> {
        world.setStorm(false)
        world.isThundering = false
      }
      'r' -> {
        world.setStorm(true)
        world.isThundering = false
      }
      't' -> {
        world.setStorm(true)
        world.isThundering = true
      }
    }

    return null
  }
}

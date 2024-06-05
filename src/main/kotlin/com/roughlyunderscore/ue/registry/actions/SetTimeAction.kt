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
 * Sets time at a world
 *
 * Example:
 * - `set-time 12000 world`
 *
 * Syntax:
 * - `set-time TIME WORLD`
 */
@Since("2.2")
@Stable
class SetTimeAction : RegistrableAction {
  override val aliases = listOf("set-time", "change-time", "time")

  override fun execute(event: Event, trigger: RegistrableTrigger, arguments: List<String>, target: TargetType): EventModifications? {
    if (arguments.size < 2) return null

    val time = arguments[0].toLongOrNull() ?: return null
    val world = Bukkit.getWorld(arguments[1]) ?: return null

    world.time = time

    return null
  }
}

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
import com.roughlyunderscore.ue.utils.mapToDrt
import org.bukkit.entity.Player
import org.bukkit.event.Event

/**
 * Sets player's personal time
 *
 * Example:
 * - `player-time 15000 true`
 *
 * Syntax:
 * - `player-time TIME RELATIVE`
 * - If `RELATIVE` is `true`, the time will be kept relative to the server time. If `false`, the time will be fixed.
 */
@Since("2.2")
@Stable
class PlayerSetTimeAction : RegistrableAction {
  override val aliases = listOf("player-time", "player-set-time")

  override fun execute(event: Event, trigger: RegistrableTrigger, arguments: List<String>, target: TargetType): EventModifications? {
    if (arguments.isEmpty()) return null

    val time = arguments[0].toLongOrNull() ?: return null
    val relative = arguments.getOrNull(1)?.lowercase()?.startsWith("t") ?: true

    val method = trigger.getTriggerDataHolder().dataRetrievalMethods[target.mapToDrt()] ?: return null
    val player = method.invoke(event) as? Player ?: return null

    player.setPlayerTime(time, relative)

    return null
  }
}
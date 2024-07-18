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
import com.roughlyunderscore.data.EventModifications
import com.roughlyunderscore.enums.TargetType
import com.roughlyunderscore.registry.RegistrableAction
import com.roughlyunderscore.registry.RegistrableTrigger
import com.roughlyunderscore.ue.utils.mapToDrt
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.Event

/**
 * Naturally breaks a block
 *
 * Example:
 * - `break 0 65 0 world`
 *
 * Syntax:
 * - `break X Y Z WORLD_NAME`
 */
@Since("2.2")
@Stable
class BreakBlockAction : RegistrableAction {
  override val aliases = listOf("break", "break-block")

  override fun execute(event: Event, trigger: RegistrableTrigger, arguments: List<String>, target: TargetType): EventModifications? {
    if (arguments.size < 3) return null

    val method = trigger.getTriggerDataHolder().dataRetrievalMethods[target.mapToDrt()] ?: return null
    val player = method.invoke(event) as? Player ?: return null

    val x = arguments[0].toIntOrNull() ?: return null
    val y = arguments[1].toIntOrNull() ?: return null
    val z = arguments[2].toIntOrNull() ?: return null
    val world = if (arguments.size > 3) Bukkit.getWorld(arguments[3]) ?: player.world else player.world

    world.getBlockAt(x, y, z).breakNaturally(player.inventory.itemInMainHand)

    return null
  }
}
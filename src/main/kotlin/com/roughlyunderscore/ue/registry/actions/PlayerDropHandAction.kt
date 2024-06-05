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
import com.roughlyunderscore.ue.utils.dropItemOffHand
import com.roughlyunderscore.ue.utils.mapToDrt
import org.bukkit.entity.Player
import org.bukkit.event.Event

/**
 * Drops the player's main/offhand item at their location.
 *
 * Example:
 * - `drop-hand main full`
 *
 * Syntax:
 * - `drop-hand (MAIN/OFF) (FULL/SINGLE)`
 * - If `MAIN` is specified, the main hand item will be dropped. If `OFF` is specified, the offhand item will be dropped.
 * - If `FULL` is specified, the entire stack will be dropped. If `SINGLE` is specified, only one item will be dropped.
 */
@Since("2.2")
@Stable
class PlayerDropHandAction : RegistrableAction {
  override val aliases = listOf("drop-hand", "drop-hand-item")

  override fun execute(event: Event, trigger: RegistrableTrigger, arguments: List<String>, target: TargetType): EventModifications? {
    if (arguments.size < 2) return null

    val mainHand = arguments[0].lowercase().startsWith("m")
    val dropFull = arguments[1].lowercase().startsWith("f")

    val method = trigger.getTriggerDataHolder().dataRetrievalMethods[target.mapToDrt()] ?: return null
    val player = method.invoke(event) as? Player ?: return null

    if (mainHand) player.dropItem(dropFull)
    else player.dropItemOffHand(dropFull)

    return null
  }
}
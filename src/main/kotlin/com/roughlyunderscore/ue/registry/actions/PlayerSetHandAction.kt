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
import com.roughlyunderscore.ue.utils.parseIntoItem
import org.bukkit.entity.Player
import org.bukkit.event.Event

/**
 * Sets the player's main/off hand item
 *
 * Example:
 * - `set-hand main DIAMOND:15`
 *
 * Syntax:
 * - `set-main-hand (MAIN/OFF) ITEM`
 * - If `MAIN` is specified, the main hand item will be set. If `OFF` is specified, the off hand item will be set.
 * - Item takes a standard item syntax found in the parseIntoItem method.
 * - Types can be found [here](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html)
 */
@Since("2.2")
@Stable
class PlayerSetHandAction : RegistrableAction {
  override val aliases = listOf("set-hand", "hand")

  override fun execute(event: Event, trigger: RegistrableTrigger, arguments: List<String>, target: TargetType): EventModifications? {
    if (arguments.size < 2) return null

    val mainHand = arguments[0].lowercase().startsWith("m")
    val item = arguments[1].parseIntoItem() ?: return null

    val method = trigger.getTriggerDataHolder().dataRetrievalMethods[target.mapToDrt()] ?: return null
    val player = method.invoke(event) as? Player ?: return null

    if (mainHand) player.inventory.setItemInMainHand(item)
    else player.inventory.setItemInOffHand(item)

    return null
  }
}
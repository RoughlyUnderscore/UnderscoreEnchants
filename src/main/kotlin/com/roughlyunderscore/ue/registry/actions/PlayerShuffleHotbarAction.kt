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
import com.roughlyunderscore.ulib.data.shuffleValues
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.inventory.ItemStack

/**
 * Shuffles the player's hotbar
 *
 * Syntax:
 * - `shuffle-hotbar`
 */
@Since("2.2")
@Stable
class PlayerShuffleHotbarAction : RegistrableAction {
  override val aliases = listOf("shuffle-hotbar", "shuffle-bar", "hotbar-shuffle")

  override fun execute(event: Event, trigger: RegistrableTrigger, arguments: List<String>, target: TargetType): EventModifications? {
    val method = trigger.getTriggerDataHolder().dataRetrievalMethods[target.mapToDrt()] ?: return null
    val player = method.invoke(event) as? Player ?: return null

    var hotbar = mutableMapOf<Int, ItemStack>()
    for (i in 0..8) player.inventory.getItem(i)?.let { hotbar.put(i, it) }
    hotbar = hotbar.shuffleValues()
    for (i in 0..hotbar.size) hotbar[i]?.let { player.inventory.setItem(i, it) }

    return null
  }
}
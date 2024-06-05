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
 * Gives an item to the player; if no space is available, the item will be dropped at the player's location.
 *
 * Example:
 * - `give DIAMOND`
 *
 * Syntax:
 * - `give ITEM`
 * - Item takes a standard item syntax found in the parseIntoItem method.
 * - Types can be found [here](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html)
 */
@Since("2.2")
@Stable
class PlayerGiveItemAction : RegistrableAction {
  override val aliases = listOf("give", "give-item", "item-give")

  override fun execute(event: Event, trigger: RegistrableTrigger, arguments: List<String>, target: TargetType): EventModifications? {
    if (arguments.isEmpty()) return null

    val item = arguments[0].parseIntoItem() ?: return null

    val method = trigger.getTriggerDataHolder().dataRetrievalMethods[target.mapToDrt()] ?: return null
    val player = method.invoke(event) as? Player ?: return null

    if (player.inventory.firstEmpty() == -1) player.world.dropItemNaturally(player.location, item)
    else player.inventory.addItem(item)

    return null
  }
}
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
import com.roughlyunderscore.ue.utils.parseIntoItem
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.event.Event

/**
 * Drops an item at a location
 *
 * Example:
 * - `drop-item DIAMOND 30 50 120 -801 world`
 *
 * Syntax:
 * - `drop-item ITEM X Y Z WORLD`
 * - Item takes a standard item syntax found in the parseIntoItem method.
 * - Types can be found [here](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html)
 * @see String.parseIntoItem
 */
@Since("2.2")
@Stable
class DropItemInWorldAction : RegistrableAction {
  override val aliases = listOf("drop-item", "drop-item-world", "drop-item-in-world")

  override fun execute(event: Event, trigger: RegistrableTrigger, arguments: List<String>, target: TargetType): EventModifications? {
    if (arguments.size < 5) return null

    val item = arguments[0].parseIntoItem() ?: return null

    val x = arguments[1].toDoubleOrNull() ?: return null
    val y = arguments[2].toDoubleOrNull() ?: return null
    val z = arguments[3].toDoubleOrNull() ?: return null
    val world = Bukkit.getWorld(arguments[4]) ?: return null

    val location = Location(world, x, y, z)
    world.dropItemNaturally(location, item)

    return null
  }
}

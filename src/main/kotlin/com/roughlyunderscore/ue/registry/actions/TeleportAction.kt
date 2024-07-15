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
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.event.Event

/**
 * Teleports an entity to a certain location
 *
 * Example:
 * - `tp 581 110 -303`
 *
 * Syntax:
 * - `tp X Y Z WORLD`
 * - If you leave the world field empty, or such world is not present, it will default to the current entity's world.
 * - You can also leave out the coordinates, but leave the world name, to teleport the entity to that world's spawnpoint.
 */
@Since("2.2")
@Stable
class TeleportAction : RegistrableAction {
  override val aliases = listOf("tp", "teleport")

  override fun execute(event: Event, trigger: RegistrableTrigger, arguments: List<String>, target: TargetType): EventModifications? {
    val method = trigger.getTriggerDataHolder().dataRetrievalMethods[target.mapToDrt()] ?: return null
    val entity = method.invoke(event) as? Entity ?: return null

    if (arguments.size == 1 && Bukkit.getWorld(arguments[0]) != null) {
      val worldName = arguments[0]
      val world = (Bukkit.getWorld(worldName) ?: entity.world)
      entity.teleport(world.spawnLocation)
      return null
    }

    if (arguments.size < 3) return null

    val x = arguments[0].toDoubleOrNull() ?: return null
    val y = arguments[1].toDoubleOrNull() ?: return null
    val z = arguments[2].toDoubleOrNull() ?: return null

    val worldName = arguments.getOrNull(3)
    val world =
      if (worldName == null) entity.world
      else (Bukkit.getWorld(worldName) ?: entity.world)

    entity.teleport(Location(world, x, y, z))

    return null
  }
}
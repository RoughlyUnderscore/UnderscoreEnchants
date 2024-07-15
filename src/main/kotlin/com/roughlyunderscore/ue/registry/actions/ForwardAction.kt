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
import org.bukkit.entity.LivingEntity
import org.bukkit.event.Event

/**
 * Teleports the entity forward a certain amount of blocks, or until there's no clear path
 *
 * Example:
 * - `forward 4.5`
 *
 * Syntax:
 * - `forward BLOCKS`
 */
@Since("2.2")
@Stable
class ForwardAction : RegistrableAction {
  override val aliases = listOf("forward", "go-forward", "teleport-forward", "dash")

  override fun execute(event: Event, trigger: RegistrableTrigger, arguments: List<String>, target: TargetType): EventModifications? {
    if (arguments.isEmpty()) return null

    var blocks = arguments[0].toDoubleOrNull() ?: return null

    val method = trigger.getTriggerDataHolder().dataRetrievalMethods[target.mapToDrt()] ?: return null
    val entity = method.invoke(event) as? LivingEntity ?: return null

    val rayTrace = entity.rayTraceBlocks(blocks)
    if (rayTrace != null && rayTrace.hitBlock != null) {
      // Something obstructs the path
      blocks = (rayTrace.hitBlock?.location?.distance(entity.location) ?: return null)
    }

    val clonedLocation = entity.location.clone()
    // Not setting the Y to 0 would mean that having your camera be even slightly non-straight
    // will result in being sent to either heaven or the underworld
    val direction = clonedLocation.direction.setY(0)
    // Normalization converts the vector to a vector with length 1,
    // which basically means that the direction remains, and the length
    // does not. It is exactly what we need to send the entity forward
    clonedLocation.add(direction.normalize().multiply(blocks))
    entity.teleport(clonedLocation)

    return null
  }
}
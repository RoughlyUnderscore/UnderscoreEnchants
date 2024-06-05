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
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.event.Event

/**
 * Changes the entity's yaw & pitch
 *
 * Example:
 * - `dir 58.4 -90`
 *
 * Syntax:
 * - `dir YAW PITCH`
 */
@Since("2.2")
@Stable
class DirectionAction : RegistrableAction {
  override val aliases = listOf("direction", "change-direction", "dir", "change-dir")

  override fun execute(event: Event, trigger: RegistrableTrigger, arguments: List<String>, target: TargetType): EventModifications? {
    if (arguments.size < 2) return null

    val yaw = arguments[0].toFloatOrNull() ?: return null
    val pitch = arguments[1].toFloatOrNull() ?: return null

    val method = trigger.getTriggerDataHolder().dataRetrievalMethods[target.mapToDrt()] ?: return null
    val entity = method.invoke(event) as? Entity ?: return null

    val playerLocation = entity.location.clone()
    entity.teleport(Location(playerLocation.world, playerLocation.x, playerLocation.y, playerLocation.z, yaw, pitch))

    return null
  }
}
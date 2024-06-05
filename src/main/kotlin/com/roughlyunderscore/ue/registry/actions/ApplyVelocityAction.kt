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
import org.bukkit.entity.Entity
import org.bukkit.event.Event
import org.bukkit.util.Vector

/**
 * Immediately applies velocity to an entity
 *
 * Example:
 * - `apply-velocity 0 0.75 0`
 *
 * Syntax:
 * - `apply-velocity X Y Z`
 */
@Since("2.2")
@Stable
class ApplyVelocityAction : RegistrableAction {
  override val aliases = listOf("velocity", "apply-velocity")

  override fun execute(event: Event, trigger: RegistrableTrigger, arguments: List<String>, target: TargetType): EventModifications? {
    if (arguments.size < 3) return null

    val x = arguments[0].toDoubleOrNull() ?: return null
    val y = arguments[1].toDoubleOrNull() ?: return null
    val z = arguments[2].toDoubleOrNull() ?: return null

    val method = trigger.getTriggerDataHolder().dataRetrievalMethods[target.mapToDrt()] ?: return null
    val entity = method.invoke(event) as? Entity ?: return null

    entity.velocity = Vector(x, y, z)

    return null
  }
}
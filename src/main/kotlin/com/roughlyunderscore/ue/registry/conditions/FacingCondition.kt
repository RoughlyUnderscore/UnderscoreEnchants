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

package com.roughlyunderscore.ue.registry.conditions

import com.roughlyunderscore.annotations.Since
import com.roughlyunderscore.annotations.Stable
import com.roughlyunderscore.enums.TargetType
import com.roughlyunderscore.registry.RegistrableCondition
import com.roughlyunderscore.registry.RegistrableTrigger
import com.roughlyunderscore.ue.utils.mapToDrt
import com.roughlyunderscore.ulib.text.normalize
import org.bukkit.entity.LivingEntity
import org.bukkit.event.Event

@Since("2.2")
@Stable
class FacingCondition : RegistrableCondition {
  override val aliases = listOf(
    "facing",
    "dir"
  )

  override fun evaluateCondition(trigger: RegistrableTrigger, event: Event, target: TargetType, arguments: List<String>): Boolean {
    if (arguments.size < 2) return false

    val method = trigger.getTriggerDataHolder().dataRetrievalMethods[target.mapToDrt()] ?: return false
    val entity = method.invoke(event) as? LivingEntity ?: return false

    val type = arguments[0].normalize().first()
    val expected = arguments[1]

    if (type == 'v') {
      val pitch = entity.location.pitch
      return when (expected.normalize().first()) {
        'd' -> pitch in 45.0.rangeUntil(90.0)
        'u' -> pitch in (-90.0).rangeUntil(-45.0)
        'f' -> pitch in (-45.0).rangeUntil(45.0)
        else -> false
      }
    }

    if (type == 'h') {
      val yaw = entity.location.yaw
      return when (expected.normalize().first()) {
        's' -> yaw in (-45.0).rangeUntil(45.0)
        'w' -> yaw in 45.0.rangeUntil(135.0)
        'n' -> yaw in 135.0..180.0 || yaw in (-180.0).rangeUntil(-135.0)
        'e' -> yaw in (-135.0).rangeUntil(-45.0)
        else -> false
      }
    }

    return false
  }
}
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
import org.bukkit.entity.Entity
import org.bukkit.event.Event
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

@Since("2.2")
@Stable
class HandIsCondition : RegistrableCondition {
  override val aliases = listOf(
    "hand",
    "handis",
    "hand-is"
  )

  override fun evaluateCondition(trigger: RegistrableTrigger, event: Event, target: TargetType, arguments: List<String>): Boolean {
    if (arguments.isEmpty()) return false
    val hand = arguments[0].normalize()

    if (event is PlayerInteractEvent) return when (hand.first()) {
      'h', 'm', 'r' -> event.hand == EquipmentSlot.HAND
      'o', 'l' -> event.hand == EquipmentSlot.OFF_HAND
      else -> false
    }

    if (event is PlayerInteractEntityEvent) return when (hand.first()) {
      'h', 'm', 'r' -> event.hand == EquipmentSlot.HAND
      'o', 'l' -> event.hand == EquipmentSlot.OFF_HAND
      else -> false
    }

    return false
  }
}
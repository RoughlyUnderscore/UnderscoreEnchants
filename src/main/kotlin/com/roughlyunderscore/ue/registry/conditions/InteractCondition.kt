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
import com.roughlyunderscore.ulib.text.normalize
import com.roughlyunderscore.ue.utils.normalize
import org.bukkit.event.Event
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

@Since("2.2")
@Stable
class InteractCondition : RegistrableCondition {
  override val aliases = listOf(
    "interact",
    "interacted",
    "interacting",
    "interaction",
    "world-interact",
    "world-interacted",
    "world-interacting",
    "world-interaction",
  )

  override fun evaluateCondition(trigger: RegistrableTrigger, event: Event, target: TargetType, arguments: List<String>): Boolean {
    if (event !is PlayerInteractEvent) return false
    if (arguments.isEmpty()) return false

    val action = event.action

    return when (arguments[0].normalize()) {
      "lmbair", "leftair" -> action == Action.LEFT_CLICK_AIR
      "lmbblock", "leftblock" -> action == Action.LEFT_CLICK_BLOCK
      "rmbair", "rightair" -> action == Action.RIGHT_CLICK_AIR
      "rmbblock", "rightblock" -> action == Action.RIGHT_CLICK_BLOCK
      "physical", "phys", "push" -> action == Action.PHYSICAL
      else -> false
    }
  }
}
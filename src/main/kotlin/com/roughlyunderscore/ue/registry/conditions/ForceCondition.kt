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
import com.roughlyunderscore.events.PlayerShootBowEvent
import com.roughlyunderscore.ue.registry.conditions.misc.ComparisonOperator
import org.bukkit.event.Event

@Since("2.2")
@Stable
class ForceCondition : RegistrableCondition {
  override val aliases = listOf(
    "force",
  )

  override fun evaluateCondition(trigger: RegistrableTrigger, event: Event, target: TargetType, arguments: List<String>): Boolean {
    if (arguments.size < 2) return false
    if (event !is PlayerShootBowEvent) return false

    val comparisonOperator = ComparisonOperator.getOperator(arguments[0])
    val value = arguments[1].toFloatOrNull() ?: return false

    val force = event.force

    return when (comparisonOperator) {
      ComparisonOperator.EQUAL -> force == value
      ComparisonOperator.MORE_THAN -> force > value
      ComparisonOperator.MORE_THAN_OR_EQUAL -> force >= value
      ComparisonOperator.LESS_THAN -> force < value
      ComparisonOperator.LESS_THAN_OR_EQUAL -> force <= value
      ComparisonOperator.NOT_AN_OPERATOR -> false
    }
  }
}
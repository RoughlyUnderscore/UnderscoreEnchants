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
import com.roughlyunderscore.ue.registry.conditions.misc.ComparisonOperator
import com.roughlyunderscore.ue.utils.mapToDrt
import com.roughlyunderscore.ulib.data.safeValue
import org.bukkit.Statistic
import org.bukkit.entity.Player
import org.bukkit.event.Event

@Since("2.2")
@Stable
class StatisticCondition : RegistrableCondition {
  override val aliases = listOf(
    "stat",
    "statistic",
    "statistic-value",
    "stat-value",
    "statisticvalue",
    "statvalue",
    "stat-val",
    "statistic-val",
    "statisticval",
    "statval"
  )

  override fun evaluateCondition(trigger: RegistrableTrigger, event: Event, target: TargetType, arguments: List<String>): Boolean {
    if (arguments.size < 3) return false

    val method = trigger.getTriggerDataHolder().dataRetrievalMethods[target.mapToDrt()] ?: return false
    val entity = method.invoke(event) as? Player ?: return false

    val statistic = safeValue<Statistic>(arguments[0].uppercase()) ?: return false
    val comparisonOperator = ComparisonOperator.getOperator(arguments[1])
    val value = arguments[2].toIntOrNull() ?: return false

    val actualValue = entity.getStatistic(statistic)

    return when (comparisonOperator) {
      ComparisonOperator.EQUAL -> actualValue == value
      ComparisonOperator.MORE_THAN -> actualValue > value
      ComparisonOperator.MORE_THAN_OR_EQUAL -> actualValue >= value
      ComparisonOperator.LESS_THAN -> actualValue < value
      ComparisonOperator.LESS_THAN_OR_EQUAL -> actualValue <= value
      ComparisonOperator.NOT_AN_OPERATOR -> false
    }
  }
}
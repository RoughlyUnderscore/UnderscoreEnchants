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
import com.roughlyunderscore.ue.UnderscoreEnchantsPlugin
import com.roughlyunderscore.enums.TargetType
import com.roughlyunderscore.registry.RegistrableCondition
import com.roughlyunderscore.registry.RegistrableTrigger
import com.roughlyunderscore.ue.registry.conditions.misc.ComparisonOperator
import com.roughlyunderscore.ue.utils.mapToDrt
import org.bukkit.entity.Player
import org.bukkit.event.Event

@Since("2.2")
@Stable
class MoneyCondition(private val plugin: UnderscoreEnchantsPlugin) : RegistrableCondition {
  override val aliases = listOf(
    "money",
    "balance",
    "moneybalance",
    "money-balance",
    "bal"
  )

  override fun evaluateCondition(trigger: RegistrableTrigger, event: Event, target: TargetType, arguments: List<String>): Boolean {
    if (arguments.size < 2) return false

    val method = trigger.getTriggerDataHolder().dataRetrievalMethods[target.mapToDrt()] ?: return false
    val player = method.invoke(event) as? Player ?: return false

    val comparisonOperator = ComparisonOperator.getOperator(arguments[0])
    val value = arguments[1].toBigDecimalOrNull() ?: return false

    val money = plugin.economyHandler.getMoney(player)

    return when (comparisonOperator) {
      ComparisonOperator.EQUAL -> money == value
      ComparisonOperator.MORE_THAN -> money > value
      ComparisonOperator.MORE_THAN_OR_EQUAL -> money >= value
      ComparisonOperator.LESS_THAN -> money < value
      ComparisonOperator.LESS_THAN_OR_EQUAL -> money <= value
      ComparisonOperator.NOT_AN_OPERATOR -> false
    }
  }
}
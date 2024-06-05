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
import com.roughlyunderscore.ulib.data.safeValue
import org.bukkit.Statistic
import org.bukkit.entity.Player
import org.bukkit.event.Event

/**
 * Sets a statistic of the target player.
 *
 * Example:
 * - `stat deaths 6`
 *
 * Syntax:
 * - `stat STATISTIC VALUE`
 */
@Since("2.2")
@Stable
class SetStatisticAction : RegistrableAction {
  override val aliases = listOf("statistic", "set-statistic", "stat", "set-stat", "statistic-value", "stat-value", "set-statistic-value", "set-stat-value")

  override fun execute(event: Event, trigger: RegistrableTrigger, arguments: List<String>, target: TargetType): EventModifications? {
    if (arguments.size < 2) return null

    val statisticName = arguments[0]
    val statisticValue = arguments[1].toIntOrNull() ?: return null

    val method = trigger.getTriggerDataHolder().dataRetrievalMethods[target.mapToDrt()] ?: return null
    val player = method.invoke(event) as? Player ?: return null

    val statistic = safeValue<Statistic>(statisticName.uppercase()) ?: return null
    player.setStatistic(statistic, statisticValue)

    return null
  }
}
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
import com.roughlyunderscore.data.EventModifications
import com.roughlyunderscore.enums.TargetType
import com.roughlyunderscore.registry.RegistrableAction
import com.roughlyunderscore.registry.RegistrableTrigger
import com.roughlyunderscore.ue.UnderscoreEnchantsPlugin
import com.roughlyunderscore.ue.utils.mapToDrt
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import org.bukkit.event.Event

/**
 * Damages an entity on behalf of a player
 *
 * Example:
 * - `dmg 306 Notch`
 *
 * Syntax:
 * - `dmg AMOUNT SOURCE`
 */
@Since("2.2")
@Stable
class DamageAction(private val plugin: UnderscoreEnchantsPlugin) : RegistrableAction {
  override val aliases = listOf("dmg", "damage", "attack")

  override fun execute(event: Event, trigger: RegistrableTrigger, arguments: List<String>, target: TargetType): EventModifications? {
    if (arguments.size < 2) return null

    val health = arguments[0].toDoubleOrNull() ?: return null
    val player = Bukkit.getPlayerExact(arguments[1]) ?: return null

    val method = trigger.getTriggerDataHolder().dataRetrievalMethods[target.mapToDrt()] ?: return null
    val entity = method.invoke(event) as? LivingEntity ?: return null

    return null.apply {
      Bukkit.getScheduler().runTaskLater(plugin, Runnable {
        entity.noDamageTicks = 0
        entity.damage(health, player)
      }, 1L)
    }
  }
}
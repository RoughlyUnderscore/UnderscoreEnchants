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
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.meta.Damageable
import kotlin.math.roundToInt

/**
 * Damages the player's item by an amount.
 *
 * Example:
 * - `damage-item chestplate 2`
 *
 * Syntax:
 * - `damage-item (HELMET/CHESTPLATE/LEGGINGS/BOOTS/MAIN/OFF) AMOUNT`
 * - If a positive amount is specified, the item will be damaged by that amount. If a negative amount is specified, the item will be repaired by that amount.
 * - The amount is clamped between [-maxDurability, maxDurability].
 * - If the item's durability reaches 0, it will be destroyed. If the item's durability reaches maxDurability, it will be repaired.
 */
@Since("2.2")
@Stable
class PlayerDamageItemAction : RegistrableAction {
  override val aliases = listOf("damage-item")

  override fun execute(event: Event, trigger: RegistrableTrigger, arguments: List<String>, target: TargetType): EventModifications? {
    if (arguments.size < 2) return null

    val itemSlot = when (arguments[0].lowercase().first()) {
      'h' -> EquipmentSlot.HEAD
      'c' -> EquipmentSlot.CHEST
      'l' -> EquipmentSlot.LEGS
      'b' -> EquipmentSlot.FEET
      'm' -> EquipmentSlot.HAND
      'o' -> EquipmentSlot.OFF_HAND
      else -> return null
    }

    val amount = arguments[1].toDoubleOrNull()?.roundToInt() ?: return null

    val method = trigger.getTriggerDataHolder().dataRetrievalMethods[target.mapToDrt()] ?: return null
    val player = method.invoke(event) as? Player ?: return null

    val item = player.inventory.getItem(itemSlot) ?: return null
    val durabilityMeta = item.itemMeta as? Damageable ?: return null

    durabilityMeta.damage += amount

    if (item.type.maxDurability.toInt() - durabilityMeta.damage < 1) {
      player.inventory.setItem(itemSlot, null)
      return null
    }

    item.itemMeta = durabilityMeta
    player.inventory.setItem(itemSlot, item)

    return null
  }
}
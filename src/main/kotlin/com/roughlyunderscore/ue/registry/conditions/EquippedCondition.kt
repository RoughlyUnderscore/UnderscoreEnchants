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

import com.cryptomorin.xseries.XMaterial
import com.jeff_media.armorequipevent.ArmorEquipEvent
import com.roughlyunderscore.annotations.Since
import com.roughlyunderscore.annotations.Stable
import com.roughlyunderscore.ue.data.Constants
import com.roughlyunderscore.enums.TargetType
import com.roughlyunderscore.registry.RegistrableCondition
import com.roughlyunderscore.registry.RegistrableTrigger
import com.roughlyunderscore.ulib.text.normalize
import org.bukkit.event.Event
import kotlin.jvm.optionals.getOrNull

@Since("2.2")
@Stable
class EquippedCondition : RegistrableCondition {
  override val aliases = listOf(
    "equipped",
    "equip",
    "puton",
    "put-on",
  )

  override fun evaluateCondition(trigger: RegistrableTrigger, event: Event, target: TargetType, arguments: List<String>): Boolean {
    if (event !is ArmorEquipEvent) return false

    val equippedItem = event.newArmorPiece?.type ?: return false
    if (arguments.isEmpty()) return true // If the condition has 0 arguments, it just checks whether there's anything equipped

    val argument = arguments[0]
    if (argument.startsWith("#")) { // If the argument starts with a #, it checks the general type of the item
      return when (argument.substring(1).normalize().first()) {
        'h' -> Constants.HELMETS.contains(equippedItem)
        'c' -> Constants.CHESTPLATES.contains(equippedItem)
        'l' -> Constants.LEGGINGS.contains(equippedItem)
        'b' -> Constants.BOOTS.contains(equippedItem)
        else -> false
      }
    }

    val checkAgainst = XMaterial.matchXMaterial(argument.uppercase()).getOrNull()?.parseMaterial() ?: return false
    return equippedItem == checkAgainst
  }
}
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

package com.roughlyunderscore.ue.ui.vanilla

import com.roughlyunderscore.enums.ItemStackEnchantResponseType
import com.roughlyunderscore.ue.UnderscoreEnchantsPlugin
import com.roughlyunderscore.ue.data.Constants
import com.roughlyunderscore.ue.utils.enchant
import com.roughlyunderscore.ue.utils.getAllEnchantmentsByKey
import com.roughlyunderscore.ue.utils.randomSuccessfulEnchant
import com.roughlyunderscore.ue.utils.randomSuccessfulTableEnchant
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.enchantment.EnchantItemEvent

class EnchantingIntercept(plugin: UnderscoreEnchantsPlugin) : Listener {
  val registry = plugin.registry
  val limit = plugin.configuration.enchantments.limit
  val chance = plugin.configuration.generation.populateEnchantingChance

  // Changing offers in PrepareItemEnchantEvent still does not seem to be possible,
  // so the best I can do is randomly add enchantments when using the enchantment table,
  // just like vanilla Minecraft does.
  @EventHandler
  fun onEnchant(event: EnchantItemEvent) {
    if (event.isCancelled) return

    var item = event.item
    // This means that no additional enchantments can be added
    if (limit == item.getAllEnchantmentsByKey(registry).size) {
      event.isCancelled = true
      return
    }

    event.enchantsToAdd.forEach { (ench, level) ->
      val response = item.enchant(ench.key, level, registry, emptyList())
      when (response.type) {
        ItemStackEnchantResponseType.SUCCESS -> item = response.resultItem
        else -> return@forEach
      }
    }

    // With the actions above, we enchanted the item with all the enchantments that were supposed to be added
    // We will now do another limit check to see if we can even roll the die to add a custom enchantment
    // We ensured that the amount of enchantments on the item is not above the limit (at most is
    // equal to the limit) by returning whenever a LIMIT_EXCEEDED response occurs
    if (limit == item.getAllEnchantmentsByKey(registry).size) {
      event.inventory.setItem(0, item)
      return
    }

    // Now it is guaranteed that the amount of enchantments on the item is less than the limit, and we are
    // free to attempt to add another one
    event.inventory.setItem(0,
      (if (Constants.RANDOM.nextDouble() <= chance) item.randomSuccessfulTableEnchant(registry, emptyList())
      else item)
    )
  }
}
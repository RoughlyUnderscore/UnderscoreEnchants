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

package com.roughlyunderscore.ue.listeners

import com.roughlyunderscore.ue.UnderscoreEnchantsPlugin
import com.roughlyunderscore.ue.data.Constants
import com.roughlyunderscore.ue.utils.*
import org.bukkit.entity.Item
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.VillagerAcquireTradeEvent
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.event.world.LootGenerateEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MerchantRecipe

class LootPopulationListener(private val plugin: UnderscoreEnchantsPlugin): Listener {
  private val registry = plugin.registry

  private val chestChance = plugin.configuration.generation.populateChestsChance
  private val villagerChance = plugin.configuration.generation.populateVillagersChance
  private val fishChance = plugin.configuration.generation.populateFishingChance

  @EventHandler
  fun onLootGenerate(event: LootGenerateEvent) {
    val oldLoot = event.loot
    val newLoot = mutableListOf<ItemStack>()

    for (oldLootItem in oldLoot) {
      if (oldLootItem == null) continue

      // Conversion from the vanilla format to the UE format is mandatory
      if (oldLootItem.enchantments.isNotEmpty()) {
        newLoot.add(oldLootItem.normalize(registry))
        continue
      }

      if ((chestChance == 0.0 || Constants.RANDOM.nextDouble() > chestChance) || !oldLootItem.isEnchantableWithCustom(plugin)) {
        newLoot.add(oldLootItem)
        continue
      }

      newLoot.add(oldLootItem.randomSuccessfulLootEnchant(registry, emptyList()))
    }

    event.setLoot(newLoot)
  }

  @EventHandler
  fun onVillagerAcquireTrade(event: VillagerAcquireTradeEvent) {
    val oldRecipe = event.recipe
    var item = oldRecipe.result

    if (!item.isEnchantableWithCustom(plugin)) return
    if (item.enchantments.isNotEmpty()) item = item.normalize(registry)
    else {
      if (villagerChance != 0.0 && Constants.RANDOM.nextDouble() <= villagerChance) item = item.disenchant(registry).randomSuccessfulTradeEnchant(registry, emptyList())
    }

    val newRecipe = MerchantRecipe(
      item, oldRecipe.uses, oldRecipe.maxUses, oldRecipe.hasExperienceReward(), oldRecipe.villagerExperience,
      oldRecipe.priceMultiplier, oldRecipe.demand, oldRecipe.specialPrice
    )

    newRecipe.ingredients = oldRecipe.ingredients
    event.recipe = newRecipe
  }

  @EventHandler
  fun onFish(event: PlayerFishEvent) {
    if (event.state != PlayerFishEvent.State.CAUGHT_FISH) return

    val caught = event.caught ?: return
    val itemEntity = caught as Item
    var item = itemEntity.itemStack

    if (!item.isEnchantableWithCustom(plugin)) return
    if (item.enchantments.isNotEmpty()) item = item.normalize(registry)
    else {
      if (fishChance != 0.0 && Constants.RANDOM.nextDouble() <= fishChance) item = item.randomSuccessfulFishingEnchant(registry, emptyList())
    }

    itemEntity.itemStack = item
  }

  @EventHandler
  fun onEntityDeath(event: EntityDeathEvent) {
    // Replace drops with normalized drops
    val drops = event.drops.map { it.normalize(registry) }
    event.drops.clear()
    event.drops.addAll(drops)
  }
}
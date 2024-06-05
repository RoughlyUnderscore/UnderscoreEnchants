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

package com.roughlyunderscore.ue.hook.api

import com.roughlyunderscore.ue.UnderscoreEnchantsPlugin
import com.roughlyunderscore.api.UEEnchanter
import com.roughlyunderscore.enums.EnchantingRestriction
import com.roughlyunderscore.result.ItemStackEnchantResponse
import com.roughlyunderscore.ue.registry.RegistryImpl
import com.roughlyunderscore.ue.utils.disenchant
import com.roughlyunderscore.ue.utils.enchant
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

class UEEnchanterImpl(plugin: UnderscoreEnchantsPlugin) : UEEnchanter {
  val registry = plugin.registry

  override fun getUnderscoreEnchantment(name: String): NamespacedKey? = registry.findEnchantment(name)?.key

  override fun disenchant(item: ItemStack, key: NamespacedKey): ItemStack = item.disenchant(key, registry)

  override fun disenchant(player: Player, slot: EquipmentSlot, key: NamespacedKey) {
    val item = player.inventory.getItem(slot) ?: return
    player.inventory.setItem(slot, disenchant(item, key))
  }

  override fun fullyDisenchant(item: ItemStack): ItemStack = item.disenchant(registry)

  override fun fullyDisenchant(player: Player, slot: EquipmentSlot) {
    val item = player.inventory.getItem(slot) ?: return
    player.inventory.setItem(slot, fullyDisenchant(item))
  }

  override fun enchant(
    item: ItemStack,
    key: NamespacedKey,
    level: Int,
    liftedRestrictions: List<EnchantingRestriction>
  ): ItemStackEnchantResponse = item.enchant(key, level, registry, liftedRestrictions)
}
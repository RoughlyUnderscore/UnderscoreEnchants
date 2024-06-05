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

import com.roughlyunderscore.ue.UnderscoreEnchantsPlugin
import com.roughlyunderscore.ue.utils.getAllEnchantmentsByKey
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.PrepareGrindstoneEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ArmorMeta
import org.bukkit.inventory.meta.Damageable
import kotlin.math.floor

class GrindstoneIntercept(plugin: UnderscoreEnchantsPlugin) : Listener {
  private val registry = plugin.registry

  @EventHandler
  fun onGrindstone(event: PrepareGrindstoneEvent) {
    val firstItem = event.inventory.getItem(0)
    val secondItem = event.inventory.getItem(1)

    if (firstItem == null && secondItem == null) return
    if (firstItem != null && secondItem != null && firstItem.type != secondItem.type) return

    // The grindstone should produce a result, if either:
    // 1) There is one item with enchantments
    // 2) There are two items with durability

    if (firstItem != null && secondItem != null) {
      val type = firstItem.type
      val firstMeta = firstItem.itemMeta as? Damageable ?: return
      val secondMeta = secondItem.itemMeta as? Damageable ?: return
      // There are two items with durability. Their new total durability is calculated by this formula, according
      // to the Minecraft Wiki:
      // min(Item A uses + Item B uses + floor(Max uses / 20), Max uses)
      val firstUses = type.maxDurability - firstMeta.damage
      val secondUses = type.maxDurability - secondMeta.damage
      val new = minOf(type.maxDurability.toInt(), firstUses + secondUses + floor(type.maxDurability / 20.0).toInt())

      val result = ItemStack(type)
      val resultMeta = Bukkit.getItemFactory().getItemMeta(type)!!

      if (resultMeta is Damageable) resultMeta.damage = type.maxDurability - new
      if (firstMeta.hasDisplayName()) resultMeta.setDisplayName(firstMeta.displayName)
      if (firstMeta is ArmorMeta && resultMeta is ArmorMeta) resultMeta.trim = firstMeta.trim

      result.itemMeta = resultMeta
      event.result = result
      return
    }

    // If there is one item in the inventory, we'll use it as the source of truth
    val item = (firstItem ?: secondItem) ?: return
    val type = item.type

    if (item.getAllEnchantmentsByKey(registry).isEmpty()) return

    val result = ItemStack(type)
    val resultMeta = Bukkit.getItemFactory().getItemMeta(type)!!
    val itemMeta = item.itemMeta ?: return

    if (resultMeta is Damageable && itemMeta is Damageable) resultMeta.damage = itemMeta.damage
    if (itemMeta.hasDisplayName()) resultMeta.setDisplayName(itemMeta.displayName)
    if (itemMeta is ArmorMeta && resultMeta is ArmorMeta) resultMeta.trim = itemMeta.trim

    result.itemMeta = resultMeta
    event.result = result
  }
}
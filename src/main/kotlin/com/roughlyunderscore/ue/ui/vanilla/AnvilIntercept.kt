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

import com.cryptomorin.xseries.XMaterial
import com.roughlyunderscore.enums.ItemStackEnchantResponseType
import com.roughlyunderscore.ue.UnderscoreEnchantsPlugin
import com.roughlyunderscore.ue.data.Constants
import com.roughlyunderscore.ue.utils.TaskUtils.delayed
import com.roughlyunderscore.ue.utils.contains
import com.roughlyunderscore.ue.utils.enchant
import com.roughlyunderscore.ue.utils.getAllEnchantmentsByKey
import com.roughlyunderscore.ulib.text.toRoman
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Tag
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ArmorMeta
import org.bukkit.inventory.meta.Damageable
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.roundToInt

class AnvilIntercept(private val plugin: UnderscoreEnchantsPlugin) : Listener {
  val limit = plugin.configuration.enchantments.limit
  val registry = plugin.registry

  // As of 2.2, not all mechanics from https://minecraft.wiki/w/Anvil_mechanics are respected.
  // Custom enchantments do not have multipliers from items/multipliers from books, and the work
  // penalty is not calculated for them. The XP requirement for the combination is calculated
  // by vanilla Minecraft solely by the vanilla enchantments. This is planned to be changed
  // with the addition of soft fields "anvil.item-multiplier" and "anvil.book-multiplier"
  // in the foreseeable future (soft fields refer to fields that are optional and have a
  // backing default value). For now, the repair cost is calculated using a formula with
  // one variable.
  private fun calculateCost(enchantments: Int) = (1 + enchantments * 3 + enchantments.toDouble().pow(1.6)).roundToInt()

  @EventHandler
  fun onAnvil(event: PrepareAnvilEvent) {
    // When intercepting anvils, we need to look at a lot of edge cases
    val inv = event.inventory

    val firstItem = inv.getItem(0)
    val secondItem = inv.getItem(1)

    if (firstItem == null || secondItem == null || firstItem.type == Material.AIR || secondItem.type == Material.AIR) {
      return
    }

    // We need to check what the player is doing with the anvil. We are only intercepting if there is some
    // combination of items happening that will add extra enchantments. We do not want to intercept if
    // the player is renaming their item or repairing it with the respective material.
    val firstType = firstItem.type
    val secondType = secondItem.type

    if (isRepair(firstType, secondType)) return

    // Additionally, if the second item has no enchantments, we also have nothing to worry about, as it is
    // a simple repair.
    val addedEnchants = secondItem.getAllEnchantmentsByKey(registry)
    if (addedEnchants.isEmpty()) return

    val enchantments = buildMap {
      putAll(firstItem.getAllEnchantmentsByKey(registry))

      addedEnchants.forEach { (key, level) ->
        if (containsKey(key)) this[key] = max(level, this[key]!!)
        else if ((limit > -1 && size < limit) || limit <= -1) this[key] = level
      }
    }

    val cost = calculateCost(enchantments.size)

    // Thankfully, for repairing items upon combining them, Minecraft does something really simple.
    // According to Minecraft wiki, "The second item's durability is added to the first". Thank you!!!
    val firstMeta = firstItem.itemMeta
    val secondMeta = secondItem.itemMeta

    val damage = if (firstType == secondType) {
      val firstDamage = (firstMeta as? Damageable)?.damage ?: 0
      val secondDamage = (secondMeta as? Damageable)?.damage ?: 0
      max(0, firstDamage - secondDamage)
    } else (firstMeta as? Damageable)?.damage ?: 0 // This is if the second item is a book

    var result = ItemStack(firstItem)
    val resultMeta = Bukkit.getItemFactory().getItemMeta(firstType)
    if (resultMeta is Damageable) resultMeta.damage = damage

    if (resultMeta is ArmorMeta) {
      if (firstMeta is ArmorMeta && firstMeta.hasTrim()) resultMeta.trim = firstMeta.trim
      if (firstMeta is ArmorMeta && !firstMeta.hasTrim() && secondMeta is ArmorMeta && secondMeta.hasTrim()) resultMeta.trim = secondMeta.trim
    }

    result.itemMeta = resultMeta

    enchantments.forEach { (key, level) ->
      val response = result.enchant(key, level, registry, emptyList())
      when (response.type) {
        ItemStackEnchantResponseType.SUCCESS -> result = response.resultItem
        else -> return@forEach
      }
    }

    delayed(1, plugin) {
      inv.repairCost = cost
      inv.setItem(2, result)
    }
  }

  // https://minecraft.wiki/w/Item_repair#Unit_repair
  private fun isRepair(itemType: Material, addedType: Material) =
    // Reason for XMaterial is another precaution (a server running on 1.20.6 might throw an error
    // if I call Material.MACE, didn't try though)
    (XMaterial.matchXMaterial(itemType) == XMaterial.MACE && XMaterial.matchXMaterial(addedType) == XMaterial.BREEZE_ROD) ||
      (addedType in Tag.PLANKS && itemType in Constants.MATERIAL_PLANKS_REPAIRABLE) ||
      (addedType in Constants.COBBLE_TYPES && itemType in Constants.MATERIAL_COBBLE_REPAIRABLE) ||
      (addedType == Material.LEATHER && itemType in Constants.MATERIAL_LEATHER_REPAIRABLE) ||
      (addedType == Material.IRON_INGOT && itemType in Constants.MATERIAL_IRON_REPAIRABLE) ||
      (addedType == Material.GOLD_INGOT && itemType in Constants.MATERIAL_GOLD_REPAIRABLE) ||
      (addedType == Material.DIAMOND && itemType in Constants.MATERIAL_DIAMOND_REPAIRABLE) ||
      (addedType == Material.NETHERITE_INGOT && itemType in Constants.MATERIAL_NETHERITE_REPAIRABLE) ||
      (addedType == Material.TURTLE_SCUTE && itemType == Material.TURTLE_HELMET) ||
      (addedType == Material.PHANTOM_MEMBRANE && itemType == Material.ELYTRA)

}
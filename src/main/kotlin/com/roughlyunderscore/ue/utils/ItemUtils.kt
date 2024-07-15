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

package com.roughlyunderscore.ue.utils

import com.roughlyunderscore.annotations.Since
import com.roughlyunderscore.annotations.Stable
import com.roughlyunderscore.ue.data.Constants
import com.roughlyunderscore.enums.EnchantingRestriction
import com.roughlyunderscore.enums.EnchantmentObtainmentMeans
import com.roughlyunderscore.enums.ItemStackEnchantResponseType
import com.roughlyunderscore.registry.RegistrableEnchantment
import com.roughlyunderscore.result.ItemStackEnchantResponse
import com.roughlyunderscore.ue.UnderscoreEnchantsPlugin
import com.roughlyunderscore.ue.registry.RegistryImpl
import com.roughlyunderscore.ulib.text.formatColor
import com.roughlyunderscore.ulib.text.toRoman
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.bukkit.persistence.PersistentDataType
import org.jetbrains.annotations.ApiStatus.Internal
import java.lang.IllegalStateException
import java.util.*

/**
 * Returns a user-friendly name for the material. For example, "iron_ingot" becomes "Iron Ingot".
 */
@Since("2.2")
fun Material.userFriendlyName(): String {
  return this.name.split("_").joinToString(" ") { str -> str.lowercase()
    .replaceFirstChar { char -> if (char.isLowerCase()) char.titlecase(Locale.getDefault()) else char.toString() } }
}

/**
 * Enchants an item with the given enchantment provided by [key] and [level], checking all restrictions not found
 * in [liftedRestrictions]. If any conflicts occur, the enchantment will not be applied.
 * @see addVanillaEnchantment
 * @see addCustomEnchantment
 * @see ItemStack.normalize
 */
@Since("2.2")
fun ItemStack.enchant(
  key: NamespacedKey, level: Int, registry: RegistryImpl,
  liftedRestrictions: List<EnchantingRestriction> = emptyList()
): ItemStackEnchantResponse {
  val vanilla = Registry.ENCHANTMENT.get(key)
  val custom = registry.findEnchantmentByKey(key)

  var finalItem = this.clone()

  if (vanilla != null) {
    val response = finalItem.addVanillaEnchantment(vanilla, level, registry, liftedRestrictions)
    if (response.type != ItemStackEnchantResponseType.SUCCESS) return response
    finalItem = response.resultItem
  }

  if (custom != null) {
    val response = finalItem.addCustomEnchantment(custom, level, registry, liftedRestrictions)
    if (response.type != ItemStackEnchantResponseType.SUCCESS) return response
    finalItem = response.resultItem
  }

  return ItemStackEnchantResponse(
    ItemStackEnchantResponseType.SUCCESS, key,
    level, finalItem.normalize(registry)
  )
}

/**
 * Enchants an item with a random enchantment, checking all restrictions not found in [liftedRestrictions].
 * If any conflicts occur, the enchantment will not be applied.
 * @see ItemStack.normalize
 */
@Since("2.2")
fun ItemStack.randomEnchant(
  registry: RegistryImpl, liftedRestrictions: List<EnchantingRestriction>,
  vanilla: (ItemStack) -> List<Enchantment>? = { item -> Registry.ENCHANTMENT.stream().filter { it.canEnchantItem(item) }.toList() },
  custom: (RegistryImpl, Material) -> List<RegistrableEnchantment> = { reg, type -> reg.enchantments.keys.filter { it.applicables.any { appl -> appl.canBeAppliedTo(type)}}}
): ItemStackEnchantResponse {
  val vanillaEnchantments = vanilla(this) ?: emptyList()
  val customEnchantments = custom(registry, this.type)
  val totalAmount = vanillaEnchantments.size + customEnchantments.size

  return if ((1..totalAmount).random() < vanillaEnchantments.size) {
    val vanillaEnch = vanillaEnchantments.random()
    val level = Constants.RANDOM.nextInt(vanillaEnch.maxLevel) + 1
    this.enchant(vanillaEnch.key, level, registry, liftedRestrictions)
  } else {
    val customEnch = customEnchantments.random()
    val level = Constants.RANDOM.nextInt(customEnch.levels.size) + 1
    this.enchant(customEnch.key, level, registry, liftedRestrictions)
  }
}



/**
 * Enchants an item with a random enchantment, checking all restrictions not found in [liftedRestrictions].
 * If any conflicts occur, the enchantment will not be applied. This method will keep trying until a successful
 * enchantment is applied.
 * @see ItemStack.normalize
 */
@Since("2.2")
fun ItemStack.randomSuccessfulEnchant(registry: RegistryImpl, liftedRestrictions: List<EnchantingRestriction>): ItemStack {
  var response = randomEnchant(registry, liftedRestrictions)
  while (response.type != ItemStackEnchantResponseType.SUCCESS) {
    response = randomEnchant(registry, liftedRestrictions)
  }

  return response.resultItem
}

/**
 * This method is equivalent to [randomSuccessfulEnchant], but only allows enchantments that come from the enchantment table.
 */
@Since("2.2")
fun ItemStack.randomSuccessfulTableEnchant(registry: RegistryImpl, lifted: List<EnchantingRestriction>) = randomSuccessfulEnchantWithBounds(
  registry, lifted,
  vanilla = { item -> Constants.ENCHANTMENT_TABLE_ENCHANTS.filter { it.canEnchantItem(item) } },
  means = EnchantmentObtainmentMeans.ENCHANTING_TABLE
)

/**
 * This method is equivalent to [randomSuccessfulEnchant], but only allows enchantments that can be traded.
 */
@Since("2.2")
fun ItemStack.randomSuccessfulTradeEnchant(registry: RegistryImpl, lifted: List<EnchantingRestriction>) = randomSuccessfulEnchantWithBounds(
  registry, lifted,
  vanilla = { item -> Constants.TRADABLE_ENCHANTS.filter { it.canEnchantItem(item) } },
  means = EnchantmentObtainmentMeans.TRADES
)

/**
 * This method is equivalent to [randomSuccessfulEnchant], but only allows enchantments that can be fished.
 */
@Since("2.2")
fun ItemStack.randomSuccessfulFishingEnchant(registry: RegistryImpl, lifted: List<EnchantingRestriction>) = randomSuccessfulEnchantWithBounds(
  registry, lifted,
  means = EnchantmentObtainmentMeans.FISHING
)

/**
 * This method is equivalent to [randomSuccessfulEnchant], but only allows enchantments that can be found in chests.
 */
@Since("2.2")
fun ItemStack.randomSuccessfulLootEnchant(registry: RegistryImpl, lifted: List<EnchantingRestriction>) = randomSuccessfulEnchantWithBounds(
  registry, lifted,
  means = EnchantmentObtainmentMeans.LOOT
)

/**
 * Generates a random successful enchantment within certain obtainment bounds.
 */
private fun ItemStack.randomSuccessfulEnchantWithBounds(
  registry: RegistryImpl,
  lifted: List<EnchantingRestriction>,
  vanilla: (ItemStack) -> List<Enchantment>? = { item -> Registry.ENCHANTMENT.filter { it.canEnchantItem(item) }.toList() },
  custom: (RegistryImpl, Material) -> List<RegistrableEnchantment> = { reg, type -> reg.enchantments.keys.filter { it.applicables.any { appl -> appl.canBeAppliedTo(type) } } },
  means: EnchantmentObtainmentMeans
): ItemStack {
  val responseFetch = { randomEnchant(registry, lifted, vanilla, custom) }
  var response = responseFetch()

  while (true) {
    if (response.type == ItemStackEnchantResponseType.HAS_UNIQUE) return this // Do not get stuck in an infinite loop :)

    if (response.type == ItemStackEnchantResponseType.SUCCESS) {
      val key = response.key
      if (Registry.ENCHANTMENT.get(key) != null) break

      val restrictions = registry.findEnchantmentByKey(key)!!.obtainmentRestrictions
      if (restrictions.filter { it.means == means }.none { response.level in it.levels}) break
    }

    response = responseFetch()
  }

  return response.resultItem
}


/**
 * Checks whether an item is enchantable. This means it is either a tool, weapon, armor, book, or enchanted book.
 */
@Since("2.2")
fun ItemStack.isEnchantable() =
  this.type.let {
    Constants.ARMOR.contains(it) || Constants.TOOLS.contains(it) || Constants.WEAPONS.contains(it) ||
      Constants.RANGED_WEAPONS.contains(it) || it == Material.BOOK || it == Material.ENCHANTED_BOOK
  }

fun ItemStack.isEnchantableWithCustom(plugin: UnderscoreEnchantsPlugin) =
  this.type in (plugin.registry).enchantable

/**
 * Disenchants an item with the given enchantment provided by [key]. If any conflicts occur,
 * the enchantment will not be removed. [registry] required to find custom enchantments.
 * @see ItemStack.normalize
 */
@Since("2.2")
fun ItemStack.disenchant(key: NamespacedKey, registry: RegistryImpl): ItemStack {
  val vanilla = Registry.ENCHANTMENT.get(key)
  val custom = registry.findEnchantmentByKey(key)

  if (vanilla != null) this.removeEnchantment(vanilla)
  if (custom != null) {
    val meta = this.itemMeta ?: return this
    meta.persistentDataContainer.remove(key)
    this.itemMeta = meta
  }

  return this.normalize(registry)
}

/**
 * Fully disenchant an item. This means removing all enchantments, including custom ones.
 * [registry] required to find custom enchantments.
 * @see ItemStack.normalize
 */
@Since("2.2")
fun ItemStack.disenchant(registry: RegistryImpl): ItemStack {
  val keys = this.getCustomEnchantmentsByKey(registry).keys // Vanilla enchantments are wiped with 1 line

  var item = this
  item.enchantments.keys.forEach { item.removeEnchantment(it) } // The line in question

  for (key in keys) item = this.disenchant(key, registry)
  return item.normalize(registry) // Is this call necessary?
}

/**
 * Adds a vanilla enchantment to an item, checking any restrictions that are not found in [lifted].
 * If any conflicts occur, the enchantment will not be applied.
 */
@Since("2.2")
fun ItemStack.addVanillaEnchantment(
  enchantment: Enchantment, level: Int, registry: RegistryImpl, lifted: List<EnchantingRestriction>
): ItemStackEnchantResponse {
  val limit = registry.underscoreEnchantsPlugin.configuration.enchantments.limit

  if (!lifted.contains(EnchantingRestriction.CONFLICT_RESTRICTION)) {
    if (this.conflictsWith(enchantment.key, registry) || this.getVanillaEnchantmentsByKey().containsKey(enchantment.key))
      return ItemStackEnchantResponse(ItemStackEnchantResponseType.CONFLICTS, enchantment.key, level, this)
  }

  if (!lifted.contains(EnchantingRestriction.LIMIT_RESTRICTION) && limit > 0 && this.getAllEnchantmentsByKey(registry).size >= limit)
    return ItemStackEnchantResponse(ItemStackEnchantResponseType.LIMIT_EXCEEDED, enchantment.key, level, this)

  if (!lifted.contains(EnchantingRestriction.MAXIMUM_LEVEL_RESTRICTION) && enchantment.maxLevel < level)
    return ItemStackEnchantResponse(ItemStackEnchantResponseType.LEVEL_TOO_HIGH, enchantment.key, level, this)

  if (!lifted.contains(EnchantingRestriction.UNIQUE_ENCHANTMENT_RESTRICTION)) {
    if (this.getCustomEnchantmentsByKey(registry).mapNotNull { registry.findEnchantmentByKey(it.key) }.any { it.unique })
      return ItemStackEnchantResponse(ItemStackEnchantResponseType.HAS_UNIQUE, enchantment.key, level, this)
  }

  if (!lifted.contains(EnchantingRestriction.UNAPPLICABLE_RESTRICTION) && !enchantment.canEnchantItem(this))
    return ItemStackEnchantResponse(ItemStackEnchantResponseType.UNAPPLICABLE, enchantment.key, level, this)

  this.addUnsafeEnchantment(enchantment, level)
  return ItemStackEnchantResponse(ItemStackEnchantResponseType.SUCCESS, enchantment.key, level, this)
}

/**
 * Adds a custom enchantment to an item, checking any restrictions that are not found in [liftedRestrictions]
 * If any conflicts occur, the enchantment will not be applied.
 */
@Since("2.2")
fun ItemStack.addCustomEnchantment(
  ench: RegistrableEnchantment, level: Int, registry: RegistryImpl, liftedRestrictions: List<EnchantingRestriction>
): ItemStackEnchantResponse {
  val limit = registry.underscoreEnchantsPlugin.configuration.enchantments.limit

  if (!liftedRestrictions.contains(EnchantingRestriction.CONFLICT_RESTRICTION)) {
    if (this.conflictsWith(ench.key, registry) || this.getCustomEnchantmentsByKey(registry).containsKey(ench.key) || this.type in ench.forbiddenMaterials)
      return ItemStackEnchantResponse(ItemStackEnchantResponseType.CONFLICTS, ench.key, level, this)
  }

  if (!liftedRestrictions.contains(EnchantingRestriction.LIMIT_RESTRICTION) && limit > 0 && this.getAllEnchantmentsByKey(registry).size >= limit)
    return ItemStackEnchantResponse(ItemStackEnchantResponseType.LIMIT_EXCEEDED, ench.key, level, this)

  if (!liftedRestrictions.contains(EnchantingRestriction.MAXIMUM_LEVEL_RESTRICTION) && ench.levels.size < level)
    return ItemStackEnchantResponse(ItemStackEnchantResponseType.LEVEL_TOO_HIGH, ench.key, level, this)

  if (!liftedRestrictions.contains(EnchantingRestriction.UNIQUE_ENCHANTMENT_RESTRICTION)) {
    if (this.getCustomEnchantmentsByKey(registry).mapNotNull { registry.findEnchantmentByKey(it.key) }.any { it.unique })
      return ItemStackEnchantResponse(ItemStackEnchantResponseType.HAS_UNIQUE, ench.key, level, this)

    if (ench.unique && this.getAllEnchantmentsByKey(registry).isNotEmpty())
      return ItemStackEnchantResponse(ItemStackEnchantResponseType.CANT_APPLY_UNIQUE, ench.key, level, this)
  }

  if (!liftedRestrictions.contains(EnchantingRestriction.UNAPPLICABLE_RESTRICTION) && ench.applicables.none { it.canBeAppliedTo(this.type) })
    return ItemStackEnchantResponse(ItemStackEnchantResponseType.UNAPPLICABLE, ench.key, level, this)

  val meta = this.itemMeta ?: throw IllegalStateException("Item meta is null")

  val pdc = meta.persistentDataContainer
  pdc.set(ench.key, PersistentDataType.INTEGER, level)

  this.itemMeta = meta

  return ItemStackEnchantResponse(
    ItemStackEnchantResponseType.SUCCESS, ench.key,
    level, this
  )
}

/**
 * Normalizes the item stack. This means:
 * - Lore cleanup
 * - Enchantment glow toggle
 * - Hide vanilla enchantments
 * - Removes the enchantments that exceed the limit if necessary
 */
@Since("2.2")
fun ItemStack.normalize(registry: RegistryImpl, limit: Int = -1): ItemStack {
  val meta = this.itemMeta ?: throw IllegalStateException("Item meta is null")
  meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)

  val customEnchantments = this.getCustomEnchantmentsByKey(registry)
  val vanillaEnchantments = this.getVanillaEnchantmentsByKey().toMutableMap()

  // Normalize enchanted books
  if (this.itemMeta != null && this.itemMeta is EnchantmentStorageMeta) {
    val storageMeta = this.itemMeta

    storageMeta?.enchants?.forEach { (ench, level) ->
      vanillaEnchantments[ench.key] = level
      storageMeta.removeEnchant(ench)
    }

    if (storageMeta != null) this.itemMeta = storageMeta
  }

  meta.setEnchantmentGlintOverride(vanillaEnchantments.isNotEmpty() || customEnchantments.isNotEmpty())

  val lore = mutableListOf<String>()
  for ((key, level) in vanillaEnchantments.entries) {
    val name = Constants.FRIENDLY_ENCHANT_NAMES[key] ?: continue
    val romanLevel = level.toRoman()

    val enchantment = Bukkit.getRegistry(Enchantment::class.java)?.get(key) ?: continue

    val loreString = (if (enchantment.isCursed) "&c" else "&7") + name + (if (enchantment.maxLevel == 1) "" else " $romanLevel")
    lore.add(loreString.formatColor())
  }

  for ((key, level) in customEnchantments) {
    val enchantment = registry.findEnchantmentByKey(key) ?: continue
    val name = enchantment.aliases.first()
    val romanLevel = level.toRoman()

    val loreString = "&7$name" + (if (enchantment.levels.size == 1) "" else " $romanLevel")
    lore.add(loreString.formatColor())
  }

  meta.lore = lore
  this.itemMeta = meta

  if (limit < 0) return this

  // Deal with the limit
  val enchants = this.getAllEnchantmentsByKey(registry).toMutableMap()
  if (enchants.size <= limit) return this

  while (enchants.size > limit) enchants.remove(enchants.keys.last())

  this.disenchant(registry)
  for ((key, level) in enchants) this.enchant(key, level, registry, emptyList())

  return this
}

/**
 * Returns a map of all the custom enchantments on the item, with the key being the enchantment's key, and the value being the level.
 */
@Since("2.2")
fun ItemStack.getVanillaEnchantmentsByKey(): Map<NamespacedKey, Int> {
  return this.enchantments.keys
    .map { it.key }
    .associateWith { key -> this.enchantments.getValue(Registry.ENCHANTMENT.get(key)!!) }
}

/**
 * Returns a map of all the custom enchantments on the item, with the key being the enchantment's key, and the value being the level.
 * [registry] required to find custom enchantments.
 */
@Since("2.2")
fun ItemStack.getCustomEnchantmentsByKey(registry: RegistryImpl): Map<NamespacedKey, Int> {
  val pdc = this.itemMeta?.persistentDataContainer ?: return emptyMap()
  return pdc.keys
    .filter { key -> registry.findEnchantmentByKey(key) != null }
    .associateWith { key -> pdc.getOrDefault(key, PersistentDataType.INTEGER, 0) }
}

/**
 * Checks whether an item conflicts with an enchantment defined by [key].
 * [registry] required to find custom enchantments.
 */
@Since("2.2")
fun ItemStack.conflictsWith(key: NamespacedKey, registry: RegistryImpl): Boolean {
  val enchantments = this.getAllEnchantmentsByKey(registry).keys

  for (enchantment in enchantments) {
    if (enchantment.conflictsWith(key, registry)) return true
  }

  return false
}

/**
 * Gets all the enchantments on the item, with the key being the enchantment's key, and the value being the level.
 * [registry] required to find custom enchantments.
 */
@Since("2.2")
fun ItemStack.getAllEnchantmentsByKey(registry: RegistryImpl): Map<NamespacedKey, Int> {
  return this.getVanillaEnchantmentsByKey() + this.getCustomEnchantmentsByKey(registry)
}

/**
 * This method takes a string and parses it into an item. Examples:
 * - `stone`: Returns itemstack of 1 stone
 * - `stone!cobblestone`: Returns either itemstack of 3 stone or 15 cobblestone (chance is equal)
 * - `stone|40%!cobblestone|60%`: Returns either itemstack of 3 stone (40% chance) or 15 cobblestone (60% chance)
 * - `stone|15%!cobblestone|20%!granite|10!oak_log|15%`: Returns either itemstack of 3 stone (15% chance), 30 cobblestone (20% chance), 10 granite (chance calculated later), or 1 oak log (15% chance)
 * - `stone|70%`: Returns either itemstack of 3 stone (70% chance) or nothing (30% chance)
 * - `stone|50%!cobblestone|30%`: Returns either itemstack of 1 stone (50% chance), 1 cobblestone (30% chance) or nothing (20% chance)
 * Notes:
 * - The total chance of all items must not exceed 100%. Otherwise, the method returns null.
 * - If any item requires smart chance calculation, then the total chance of implicitly specified
 *  items must be less than 100%. Otherwise, the method returns null.
 */
@Since("2.2")
@Stable
fun String.parseIntoItem(): ItemStack? {
  val items = this.split("!").mapNotNull { itemString ->

    val split = itemString.split("|")
    if (split.isEmpty()) return@mapNotNull null

    // First argument is always the type of the item
    val itemComponent = Bukkit.getItemFactory().createItemStack(split[0])

    // Second argument can be either choice or amount. If the string has a percent sign, it is the chance,
    // otherwise it is the amount. Alternatively, there's no second argument, in which case the amount is 1, and
    // the chance is undetermined.
    var chance = -1.0

    split.getOrNull(1)?.let { argument ->
      // -1 is the placeholder value indicating that the chance must be calculated later
      chance = argument.replace("%", "").toDoubleOrNull() ?: -1.0
    }

    ItemData(itemComponent, chance)
  }.toMutableList()

  // The total chance must not exceed 100%. However, some items may have smart chance calculation, which hinders the
  // ability to calculate the total chance immediately.
  if (items.none { item -> item.chance == -1.0 }) {
    // In this event, the chance can be calculated immediately
    if (items.sumOf { item -> item.chance } > 100.0) return null
  } else {
    // Otherwise, the requirement changes to the following:
    // The total chance must not be equal or greater than 100.0. This is because if the chance does exceed
    // or is equal to 100.0, the chance calculation will just be 0.
    // The total chance calculation must be increased by the amount of items that require smart chance calculation,
    // because every smart chance item has a chance of -1.0.
    if (items.sumOf { item -> if (item.chance == -1.0) 0.0 else item.chance } >= 100.0) return null
  }

  // After the prerequisites are met, the chance can be calculated
  // For each item with smart calculation, the chance must be distributed evenly
  val smartChanceItems = items.filter { item -> item.chance == -1.0 }
  val smartChance = (100.0 - items.sumOf { item -> if (item.chance == -1.0) 0.0 else item.chance }) / smartChanceItems.size
  items.replaceAll { item -> if (item.chance == -1.0) ItemData(item.item, smartChance) else item }

  // Finally, the item can be chosen
  val random = Random().nextDouble() * 100.0
  var chance = 0.0
  for (item in items) {
    chance += item.chance
    if (random <= chance) return item.item
  }

  return null
}

/**
 * Helper class for [String.parseIntoItem]
 */
@Internal internal class ItemData(val item: ItemStack, val chance: Double)
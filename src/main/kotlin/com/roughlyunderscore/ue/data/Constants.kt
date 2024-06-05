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

package com.roughlyunderscore.ue.data

import com.cryptomorin.xseries.XEnchantment
import com.cryptomorin.xseries.XMaterial
import com.cryptomorin.xseries.XPotion
import com.roughlyunderscore.annotations.Since
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Registry
import org.bukkit.enchantments.Enchantment
import java.util.concurrent.ThreadLocalRandom

/**
 * This class holds all the constants that may be important
 * at any point in the plugin. This includes (not an exhaustive list):
 * - The bStats Metrics ID
 * - The Spigot ID
 * - The user-friendly vanilla enchantment names
 * - Specific vanilla enchantment lists (e.g., weapon enchantments, helmet enchantments, etc.)
 * - Items that can be repaired by planks, cobblestone, etc.
 * - Buffs & debuffs
 * - Materials that represent weapons, tools, etc.
 */
@Since("2.2")
@Suppress("unused")
object Constants {
  /**
   * Represents the bStats Metrics ID of this plugin.
   */
  @Since("2.2")
  const val BSTATS_ID = 12413

  /**
   * Represents the Spigot ID of this plugin.
   */
  @Since("2.2")
  const val SPIGOT_ID = 97002

  /**
   * Represents the global random instance.
   */
  @Since("2.2")
  val RANDOM: ThreadLocalRandom = ThreadLocalRandom.current()

  /**
   * Holds the enchantment names that are user-friendly.
   */
  @Since("2.2")
  val FRIENDLY_ENCHANT_NAMES = mapOf(
    XEnchantment.AQUA_AFFINITY to "Aqua Affinity",
    XEnchantment.BANE_OF_ARTHROPODS to "Bane of Arthropods",
    XEnchantment.BINDING_CURSE to "Curse of Binding",
    XEnchantment.BLAST_PROTECTION to "Blast Protection",
    XEnchantment.BREACH to "Breach",
    XEnchantment.CHANNELING to "Channeling",
    XEnchantment.DENSITY to "Density",
    XEnchantment.DEPTH_STRIDER to "Depth Strider",
    XEnchantment.EFFICIENCY to "Efficiency",
    XEnchantment.FEATHER_FALLING to "Feather Falling",
    XEnchantment.FIRE_ASPECT to "Fire Aspect",
    XEnchantment.FIRE_PROTECTION to "Fire Protection",
    XEnchantment.FLAME to "Flame",
    XEnchantment.FORTUNE to "Fortune",
    XEnchantment.FROST_WALKER to "Frost Walker",
    XEnchantment.IMPALING to "Impaling",
    XEnchantment.INFINITY to "Infinity",
    XEnchantment.KNOCKBACK to "Knockback",
    XEnchantment.LOOTING to "Looting",
    XEnchantment.LOYALTY to "Loyalty",
    XEnchantment.LUCK_OF_THE_SEA to "Luck of the Sea",
    XEnchantment.LURE to "Lure",
    XEnchantment.MENDING to "Mending",
    XEnchantment.MULTISHOT to "Multishot",
    XEnchantment.PIERCING to "Piercing",
    XEnchantment.POWER to "Power",
    XEnchantment.PROJECTILE_PROTECTION to "Projectile Protection",
    XEnchantment.PROTECTION to "Protection",
    XEnchantment.PUNCH to "Punch",
    XEnchantment.QUICK_CHARGE to "Quick Charge",
    XEnchantment.RESPIRATION to "Respiration",
    XEnchantment.RIPTIDE to "Riptide",
    XEnchantment.SHARPNESS to "Sharpness",
    XEnchantment.SILK_TOUCH to "Silk Touch",
    XEnchantment.SMITE to "Smite",
    XEnchantment.SOUL_SPEED to "Soul Speed",
    XEnchantment.SWEEPING_EDGE to "Sweeping Edge",
    XEnchantment.SWIFT_SNEAK to "Swift Sneak",
    XEnchantment.THORNS to "Thorns",
    XEnchantment.UNBREAKING to "Unbreaking",
    XEnchantment.VANISHING_CURSE to "Curse of Vanishing",
    XEnchantment.WIND_BURST to "Wind Burst"
  ).mapKeys { it.key.enchant?.key }.filterKeys { it != null }

  /**
   * Holds all the vanilla enchantments.
   */
  @Since("2.2")
  val VANILLA_ENCHANTS: List<Enchantment> = Registry.ENCHANTMENT.stream().toList()

  /**
   * Holds all the vanilla weapon enchantments.
   */
  @Since("2.2")
  val VANILLA_WEAPON_ENCHANTS = listOf(
    XEnchantment.SHARPNESS,
    XEnchantment.BANE_OF_ARTHROPODS,
    XEnchantment.FIRE_ASPECT,
    XEnchantment.KNOCKBACK,
    XEnchantment.LOOTING,
    XEnchantment.SMITE,
    XEnchantment.UNBREAKING,
    XEnchantment.MENDING,
    XEnchantment.SWEEPING_EDGE,
    XEnchantment.VANISHING_CURSE,
  ).mapNotNull { it.enchant }

  /**
   * Holds all the vanilla bow enchantments.
   */
  @Since("2.2")
  val VANILLA_BOW_ENCHANTS = listOf(
    XEnchantment.FLAME,
    XEnchantment.PUNCH,
    XEnchantment.INFINITY,
    XEnchantment.UNBREAKING,
    XEnchantment.MENDING,
    XEnchantment.VANISHING_CURSE,
  ).mapNotNull { it.enchant }

  /**
   * Holds all the vanilla tool enchantments.
   */
  @Since("2.2")
  val VANILLA_TOOL_ENCHANTS = listOf(
    XEnchantment.UNBREAKING,
    XEnchantment.EFFICIENCY,
    XEnchantment.FORTUNE,
    XEnchantment.SILK_TOUCH,
    XEnchantment.LURE,
    XEnchantment.LUCK_OF_THE_SEA,
    XEnchantment.MENDING,
    XEnchantment.VANISHING_CURSE,
  ).mapNotNull { it.enchant }

  /**
   * Holds all the vanilla helmet enchantments.
   */
  @Since("2.2")
  val VANILLA_HELMET_ENCHANTS = listOf(
    XEnchantment.AQUA_AFFINITY,
    XEnchantment.BLAST_PROTECTION,
    XEnchantment.FIRE_PROTECTION,
    XEnchantment.PROJECTILE_PROTECTION,
    XEnchantment.PROTECTION,
    XEnchantment.RESPIRATION,
    XEnchantment.THORNS,
    XEnchantment.UNBREAKING,
    XEnchantment.VANISHING_CURSE,
    XEnchantment.BINDING_CURSE,
    XEnchantment.MENDING,
  ).mapNotNull { it.enchant }

  /**
   * Holds all the vanilla chestplate enchantments.
   */
  @Since("2.2")
  val VANILLA_CHESTPLATE_ENCHANTS = listOf(
    XEnchantment.BLAST_PROTECTION,
    XEnchantment.FIRE_PROTECTION,
    XEnchantment.PROJECTILE_PROTECTION,
    XEnchantment.PROTECTION,
    XEnchantment.THORNS,
    XEnchantment.UNBREAKING,
    XEnchantment.VANISHING_CURSE,
    XEnchantment.BINDING_CURSE,
    XEnchantment.MENDING,
  ).mapNotNull { it.enchant }

  /**
   * Holds all the vanilla leggings enchantments.
   */
  @Since("2.2")
  val VANILLA_LEGGINGS_ENCHANTS = listOf(
    XEnchantment.BLAST_PROTECTION,
    XEnchantment.FIRE_PROTECTION,
    XEnchantment.PROJECTILE_PROTECTION,
    XEnchantment.PROTECTION,
    XEnchantment.THORNS,
    XEnchantment.UNBREAKING,
    XEnchantment.VANISHING_CURSE,
    XEnchantment.BINDING_CURSE,
    XEnchantment.MENDING,
  ).mapNotNull { it.enchant }

  /**
   * Holds all the vanilla boots enchantments.
   */
  @Since("2.2")
  val VANILLA_BOOTS_ENCHANTS = listOf(
    XEnchantment.DEPTH_STRIDER,
    XEnchantment.BLAST_PROTECTION,
    XEnchantment.FIRE_PROTECTION,
    XEnchantment.PROJECTILE_PROTECTION,
    XEnchantment.PROTECTION,
    XEnchantment.THORNS,
    XEnchantment.UNBREAKING,
    XEnchantment.FEATHER_FALLING,
    XEnchantment.DEPTH_STRIDER,
    XEnchantment.VANISHING_CURSE,
    XEnchantment.BINDING_CURSE,
    XEnchantment.FROST_WALKER,
    XEnchantment.MENDING,
    XEnchantment.SOUL_SPEED,
  ).mapNotNull { it.enchant }

  /**
   * The list of all vanilla enchantments that can be obtained from the
   * enchanting table (https://minecraft.wiki/w/Enchanting_mechanics#Obtainable_enchants).
   */
  @Since("2.2")
  val ENCHANTMENT_TABLE_ENCHANTS = listOf(
    XEnchantment.PROTECTION,
    XEnchantment.FEATHER_FALLING,
    XEnchantment.FIRE_PROTECTION,
    XEnchantment.PROJECTILE_PROTECTION,
    XEnchantment.AQUA_AFFINITY,
    XEnchantment.BLAST_PROTECTION,
    XEnchantment.RESPIRATION,
    XEnchantment.DEPTH_STRIDER,
    XEnchantment.THORNS,
    XEnchantment.SHARPNESS,
    XEnchantment.BANE_OF_ARTHROPODS,
    XEnchantment.KNOCKBACK,
    XEnchantment.SMITE,
    XEnchantment.FIRE_ASPECT,
    XEnchantment.LOOTING,
    XEnchantment.SWEEPING_EDGE,
    XEnchantment.EFFICIENCY,
    XEnchantment.FORTUNE,
    XEnchantment.SILK_TOUCH,
    XEnchantment.POWER,
    XEnchantment.FLAME,
    XEnchantment.PUNCH,
    XEnchantment.INFINITY,
    XEnchantment.LUCK_OF_THE_SEA,
    XEnchantment.LURE,
    XEnchantment.LOYALTY,
    XEnchantment.IMPALING,
    XEnchantment.RIPTIDE,
    XEnchantment.CHANNELING,
    XEnchantment.PIERCING,
    XEnchantment.QUICK_CHARGE,
    XEnchantment.MULTISHOT,
    XEnchantment.UNBREAKING
  ).mapNotNull { it.enchant }

  /**
   * A list of all vanilla enchantments that can be acquired by trading with villagers
   * (https://minecraft.wiki/w/Enchanting#Summary_of_enchantments).
   */
  val TRADABLE_ENCHANTS = listOf(
    XEnchantment.AQUA_AFFINITY,
    XEnchantment.BANE_OF_ARTHROPODS,
    XEnchantment.BLAST_PROTECTION,
    XEnchantment.BREACH,
    XEnchantment.CHANNELING,
    XEnchantment.BINDING_CURSE,
    XEnchantment.VANISHING_CURSE,
    XEnchantment.DENSITY,
    XEnchantment.DEPTH_STRIDER,
    XEnchantment.EFFICIENCY,
    XEnchantment.FEATHER_FALLING,
    XEnchantment.FIRE_ASPECT,
    XEnchantment.FIRE_PROTECTION,
    XEnchantment.FLAME,
    XEnchantment.FORTUNE,
    XEnchantment.FROST_WALKER,
    XEnchantment.IMPALING,
    XEnchantment.INFINITY,
    XEnchantment.KNOCKBACK,
    XEnchantment.LOOTING,
    XEnchantment.LOYALTY,
    XEnchantment.LUCK_OF_THE_SEA,
    XEnchantment.LURE,
    XEnchantment.MENDING,
    XEnchantment.MULTISHOT,
    XEnchantment.PIERCING,
    XEnchantment.POWER,
    XEnchantment.PROJECTILE_PROTECTION,
    XEnchantment.PROTECTION,
    XEnchantment.PUNCH,
    XEnchantment.QUICK_CHARGE,
    XEnchantment.RESPIRATION,
    XEnchantment.RIPTIDE,
    XEnchantment.SHARPNESS,
    XEnchantment.SILK_TOUCH,
    XEnchantment.SMITE,
    XEnchantment.SWEEPING_EDGE,
    XEnchantment.THORNS,
    XEnchantment.UNBREAKING,
  ).mapNotNull { it.enchant }

  /**
   * Holds all materials that can be repaired by planks in an anvil.
   */
  @Since("2.2")
  val MATERIAL_PLANKS_REPAIRABLE = listOf(
    XMaterial.WOODEN_SWORD,
    XMaterial.WOODEN_SHOVEL,
    XMaterial.WOODEN_PICKAXE,
    XMaterial.WOODEN_AXE,
    XMaterial.WOODEN_HOE,
    XMaterial.SHIELD,
  ).mapNotNull { it.parseMaterial() }

  /**
   * Holds all materials that can be repaired by leather in an anvil.
   */
  @Since("2.2")
  val MATERIAL_LEATHER_REPAIRABLE = listOf(
    XMaterial.LEATHER_HELMET,
    XMaterial.LEATHER_CHESTPLATE,
    XMaterial.LEATHER_LEGGINGS,
    XMaterial.LEATHER_BOOTS,
  ).mapNotNull { it.parseMaterial() }

  /**
   * Holds all materials that can be repaired by cobblestone in an anvil.
   */
  @Since("2.2")
  val MATERIAL_COBBLE_REPAIRABLE = listOf(
    XMaterial.STONE_SWORD,
    XMaterial.STONE_SHOVEL,
    XMaterial.STONE_PICKAXE,
    XMaterial.STONE_AXE,
    XMaterial.STONE_HOE
  ).mapNotNull { it.parseMaterial() }

  /**
   * Represents all the materials that can be used to repair cobblestone-repairable items.
   */
  @Since("2.2")
  val COBBLE_TYPES = listOf(
    Material.COBBLESTONE,
    Material.BLACKSTONE,
    Material.COBBLED_DEEPSLATE
  )

  /**
   * Holds all materials that can be repaired by iron in an anvil.
   */
  @Since("2.2")
  val MATERIAL_IRON_REPAIRABLE = listOf(
    XMaterial.IRON_SWORD,
    XMaterial.IRON_SHOVEL,
    XMaterial.IRON_PICKAXE,
    XMaterial.IRON_AXE,
    XMaterial.IRON_HOE,
    XMaterial.IRON_HELMET,
    XMaterial.IRON_CHESTPLATE,
    XMaterial.IRON_LEGGINGS,
    XMaterial.IRON_BOOTS,
    XMaterial.CHAINMAIL_HELMET,
    XMaterial.CHAINMAIL_CHESTPLATE,
    XMaterial.CHAINMAIL_LEGGINGS,
    XMaterial.CHAINMAIL_BOOTS,
  ).mapNotNull { it.parseMaterial() }

  /**
   * Holds all materials that can be repaired by gold in an anvil.
   */
  @Since("2.2")
  val MATERIAL_GOLD_REPAIRABLE = listOf(
    XMaterial.GOLDEN_HELMET,
    XMaterial.GOLDEN_CHESTPLATE,
    XMaterial.GOLDEN_LEGGINGS,
    XMaterial.GOLDEN_BOOTS,
    XMaterial.GOLDEN_SWORD,
    XMaterial.GOLDEN_SHOVEL,
    XMaterial.GOLDEN_PICKAXE,
    XMaterial.GOLDEN_AXE,
    XMaterial.GOLDEN_HOE,
  ).mapNotNull { it.parseMaterial() }

  /**
   * Holds all materials that can be repaired by diamond in an anvil.
   */
  @Since("2.2")
  val MATERIAL_DIAMOND_REPAIRABLE = listOf(
    XMaterial.DIAMOND_HELMET,
    XMaterial.DIAMOND_CHESTPLATE,
    XMaterial.DIAMOND_LEGGINGS,
    XMaterial.DIAMOND_BOOTS,
    XMaterial.DIAMOND_SWORD,
    XMaterial.DIAMOND_SHOVEL,
    XMaterial.DIAMOND_PICKAXE,
    XMaterial.DIAMOND_AXE,
    XMaterial.DIAMOND_HOE,
  ).mapNotNull { it.parseMaterial() }

  /**
   * Holds all materials that can be repaired by netherite in an anvil.
   */
  @Since("2.2")
  val MATERIAL_NETHERITE_REPAIRABLE = listOf(
    XMaterial.NETHERITE_HELMET,
    XMaterial.NETHERITE_CHESTPLATE,
    XMaterial.NETHERITE_LEGGINGS,
    XMaterial.NETHERITE_BOOTS,
    XMaterial.NETHERITE_SWORD,
    XMaterial.NETHERITE_SHOVEL,
    XMaterial.NETHERITE_PICKAXE,
    XMaterial.NETHERITE_AXE,
    XMaterial.NETHERITE_HOE,
  ).mapNotNull { it.parseMaterial() }

  /**
   * Holds all materials that can represent weapons (swords).
   */
  @Since("2.2")
  val WEAPONS = listOf(
    XMaterial.WOODEN_SWORD,
    XMaterial.STONE_SWORD,
    XMaterial.GOLDEN_SWORD,
    XMaterial.IRON_SWORD,
    XMaterial.DIAMOND_SWORD,
    XMaterial.NETHERITE_SWORD,
    XMaterial.TRIDENT
  ).mapNotNull { it.parseMaterial() }

  /**
   * Holds all materials that can represent ranged weapons (bows, crossbows).
   */
  @Since("2.2")
  val RANGED_WEAPONS = listOf(
    XMaterial.BOW,
    XMaterial.CROSSBOW,
  ).mapNotNull { it.parseMaterial() }

  /**
   * Holds all materials that can represent swords.
   */
  val SHOVELS = listOf(
    XMaterial.WOODEN_SHOVEL,
    XMaterial.STONE_SHOVEL,
    XMaterial.IRON_SHOVEL,
    XMaterial.GOLDEN_SHOVEL,
    XMaterial.DIAMOND_SHOVEL,
    XMaterial.NETHERITE_SHOVEL,
  ).mapNotNull { it.parseMaterial() }

  /**
   * Holds all materials that can represent pickaxes.
   */
  val PICKAXES = listOf(
    XMaterial.WOODEN_PICKAXE,
    XMaterial.STONE_PICKAXE,
    XMaterial.IRON_PICKAXE,
    XMaterial.GOLDEN_PICKAXE,
    XMaterial.DIAMOND_PICKAXE,
    XMaterial.NETHERITE_PICKAXE,
  ).mapNotNull { it.parseMaterial() }

  /**
   * Holds all materials that can represent axes.
   */
  val AXES = listOf(
    XMaterial.WOODEN_AXE,
    XMaterial.STONE_AXE,
    XMaterial.IRON_AXE,
    XMaterial.GOLDEN_AXE,
    XMaterial.DIAMOND_AXE,
    XMaterial.NETHERITE_AXE,
  ).mapNotNull { it.parseMaterial() }

  /**
   * Holds all materials that can represent hoes.
   */
  val HOES = listOf(
    XMaterial.WOODEN_HOE,
    XMaterial.STONE_HOE,
    XMaterial.IRON_HOE,
    XMaterial.GOLDEN_HOE,
    XMaterial.DIAMOND_HOE,
    XMaterial.NETHERITE_HOE,
  ).mapNotNull { it.parseMaterial() }

  /**
   * Holds all materials that can represent tools (shovels, pickaxes, axes, hoes, flint & steel and fishing rods).
   */
  @Since("2.2")
  val TOOLS = listOf(
    XMaterial.FISHING_ROD,
    XMaterial.CARROT_ON_A_STICK,
    XMaterial.WARPED_FUNGUS_ON_A_STICK,
    XMaterial.FLINT_AND_STEEL,
  ).mapNotNull { it.parseMaterial() } + SHOVELS + PICKAXES + AXES + HOES

  /**
   * Holds all materials that can represent head wearables.
   */
  @Since("2.2")
  val HEADS = listOf(
    XMaterial.LEATHER_HELMET,
    XMaterial.CHAINMAIL_HELMET,
    XMaterial.IRON_HELMET,
    XMaterial.GOLDEN_HELMET,
    XMaterial.DIAMOND_HELMET,
    XMaterial.NETHERITE_HELMET,
    XMaterial.SKELETON_SKULL,
    XMaterial.CREEPER_HEAD,
    XMaterial.ZOMBIE_HEAD,
    XMaterial.PLAYER_HEAD,
    XMaterial.WITHER_SKELETON_SKULL,
    XMaterial.DRAGON_HEAD,
    XMaterial.TURTLE_HELMET
  ).mapNotNull { it.parseMaterial() }

  /**
   * Holds all materials that can represent helmets.
   */
  @Since("2.2")
  val HELMETS = listOf(
    XMaterial.LEATHER_HELMET,
    XMaterial.CHAINMAIL_HELMET,
    XMaterial.IRON_HELMET,
    XMaterial.GOLDEN_HELMET,
    XMaterial.DIAMOND_HELMET,
    XMaterial.NETHERITE_HELMET,
    XMaterial.TURTLE_HELMET
  ).mapNotNull { it.parseMaterial() }

  /**
   * Holds all materials that can represent chestplates.
   */
  @Since("2.2")
  val CHESTPLATES = listOf(
    XMaterial.LEATHER_CHESTPLATE,
    XMaterial.CHAINMAIL_CHESTPLATE,
    XMaterial.IRON_CHESTPLATE,
    XMaterial.GOLDEN_CHESTPLATE,
    XMaterial.DIAMOND_CHESTPLATE,
    XMaterial.NETHERITE_CHESTPLATE,
  ).mapNotNull { it.parseMaterial() }

  /**
   * Holds all materials that can represent leggings.
   */
  @Since("2.2")
  val LEGGINGS = listOf(
    XMaterial.LEATHER_LEGGINGS,
    XMaterial.CHAINMAIL_LEGGINGS,
    XMaterial.IRON_LEGGINGS,
    XMaterial.GOLDEN_LEGGINGS,
    XMaterial.DIAMOND_LEGGINGS,
    XMaterial.NETHERITE_LEGGINGS,
  ).mapNotNull { it.parseMaterial() }

  /**
   * Holds all materials that can represent boots.
   */
  @Since("2.2")
  val BOOTS = listOf(
    XMaterial.LEATHER_BOOTS,
    XMaterial.CHAINMAIL_BOOTS,
    XMaterial.IRON_BOOTS,
    XMaterial.GOLDEN_BOOTS,
    XMaterial.DIAMOND_BOOTS,
    XMaterial.NETHERITE_BOOTS,
  ).mapNotNull { it.parseMaterial() }

  /**
   * Holds all materials that can represent armor (helmet, chestplate, leggings and boots).
   */
  @Since("2.2")
  val ARMOR = HELMETS + CHESTPLATES + LEGGINGS + BOOTS

  /**
   * Holds all positive effects ("buffs").
   */
  @Since("2.2")
  val BUFFS = listOf(
    XPotion.RESISTANCE,
    XPotion.FIRE_RESISTANCE,
    XPotion.STRENGTH,
    XPotion.ABSORPTION,
    XPotion.HASTE,
    XPotion.INSTANT_HEALTH,
    XPotion.HEALTH_BOOST,
    XPotion.INVISIBILITY,
    XPotion.CONDUIT_POWER,
    XPotion.HERO_OF_THE_VILLAGE,
    XPotion.DOLPHINS_GRACE,
    XPotion.GLOWING,
    XPotion.LUCK,
    XPotion.WATER_BREATHING,
    XPotion.SATURATION,
    XPotion.SPEED,
    XPotion.JUMP_BOOST,
    XPotion.NIGHT_VISION,
    XPotion.REGENERATION,
  ).mapNotNull { it.potionEffectType }

  /**
   * Holds all negative effects ("debuffs").
   */
  @Since("2.2")
  val DEBUFFS = listOf(
    XPotion.BAD_OMEN,
    XPotion.BLINDNESS,
    XPotion.DARKNESS,
    XPotion.HUNGER,
    XPotion.INFESTED,
    XPotion.INSTANT_DAMAGE,
    XPotion.MINING_FATIGUE,
    XPotion.NAUSEA,
    XPotion.OOZING,
    XPotion.POISON,
    XPotion.RAID_OMEN,
    XPotion.SLOWNESS,
    XPotion.TRIAL_OMEN,
    XPotion.UNLUCK,
    XPotion.WEAKNESS,
    XPotion.WEAVING,
    XPotion.WIND_CHARGED,
    XPotion.WITHER,
  ).mapNotNull { it.potionEffectType }

  /**
   * Holds the regex used for replacing placeholders.
   */
  @Since("2.2")
  val PLACEHOLDER_REGEX = """<(.*?)>""".toRegex()

  /**
   * Holds the regex used for replacing {amount_X}.
   */
  @Since("2.2")
  val AMOUNT_REGEX = """\{amount_(\d+)\}""".toRegex()

  /**
   * Holds all vegetarian foods.
   */
  @Since("2.2")
  val VEGETARIAN_FOODS = listOf(
    XMaterial.GOLDEN_CARROT,
    XMaterial.CARROT,
    XMaterial.BAKED_POTATO,
    XMaterial.MUSHROOM_STEW,
    XMaterial.SUSPICIOUS_STEW,
    XMaterial.KELP,
    XMaterial.DRIED_KELP,
    XMaterial.CHORUS_FRUIT,
    XMaterial.SWEET_BERRIES,
    XMaterial.GLOW_BERRIES,
    XMaterial.BEETROOT_SOUP,
    XMaterial.POTATO,
    XMaterial.APPLE,
    XMaterial.GOLDEN_APPLE,
    XMaterial.ENCHANTED_GOLDEN_APPLE,
    XMaterial.MELON_SLICE,
    XMaterial.PUMPKIN_PIE,
    XMaterial.PUMPKIN_STEM,
  ).mapNotNull { it.parseMaterial() }

  /**
   * Holds all pescetarian foods.
   */
  @Since("2.2")
  val PESCETARIAN_FOODS = listOf(
    XMaterial.COOKED_COD,
    XMaterial.COOKED_SALMON,
    XMaterial.TROPICAL_FISH,
    XMaterial.COD,
    XMaterial.SALMON,
    XMaterial.PUFFERFISH,
  ).mapNotNull { it.parseMaterial() }
}
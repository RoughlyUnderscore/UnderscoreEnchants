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

@file:Suppress("UnstableApiUsage") // See documentation for UndiscoveredTrigger

package com.roughlyunderscore.ue.json

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.roughlyunderscore.ue.UnderscoreEnchantment
import com.roughlyunderscore.ue.UnderscoreEnchantsPlugin
import com.roughlyunderscore.annotations.Since
import com.roughlyunderscore.data.*
import com.roughlyunderscore.enums.EnchantmentObtainmentMeans
import com.roughlyunderscore.json.DeserializationNames
import com.roughlyunderscore.registry.RegistrableAction
import com.roughlyunderscore.registry.RegistrableApplicable
import com.roughlyunderscore.registry.internal.*
import com.roughlyunderscore.ue.registry.RegistryImpl
import com.roughlyunderscore.ulib.data.Time
import com.roughlyunderscore.ulib.data.TimeMeasurementUnit
import com.roughlyunderscore.ulib.json.*
import com.roughlyunderscore.ulib.text.normalize
import com.roughlyunderscore.ue.utils.*
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.jetbrains.annotations.ApiStatus.Internal
import java.lang.reflect.Type

class EnchantmentDeserializer(private val plugin: UnderscoreEnchantsPlugin, private val registry: RegistryImpl) : JsonDeserializer<UnderscoreEnchantment?> {
  override fun deserialize(src: JsonElement?, type: Type, ctx: JsonDeserializationContext?): UnderscoreEnchantment? {
    if (src == null) return null
    val json = src.asJsonObject ?: return null

    val name = json.anyString(DeserializationNames.Enchantment.NAME) ?: return null
    val description = json.anyArrayOfStringsStrict(DeserializationNames.Enchantment.DESCRIPTION) { emptyList() }
    val author = json.anyStringStrict(DeserializationNames.Enchantment.AUTHOR) { "Unknown" }

    val trigger = json.onAnyString(DeserializationNames.Enchantment.TRIGGER_NAME) { registry.findTrigger(this) ?: UndiscoveredTrigger(this) }!!

    val chance = json.anyDoubleStrict(DeserializationNames.Enchantment.CHANCE) { 100.0 }
    val cooldown = Time(json.anyLongStrict(DeserializationNames.Enchantment.COOLDOWN) { 0 }, TimeMeasurementUnit.TICKS)
    val indicator = json.onAnyStringStrict(DeserializationNames.Enchantment.ACTIVATION, { "bossbar" }) {
      registry.findIndicator(this) ?: UndiscoveredActivationIndicator(this)
    }

    val applicables = json.onAnyArrayOfStrings(DeserializationNames.Enchantment.APPLICABLES) { mapNotNull {
      if (it.startsWith("#")) object : RegistrableApplicable {
        override val aliases = listOf("\$custom-applicable{${it.substring(1)}}")
        override val custom = true
        override fun canBeAppliedTo(type: Material): Boolean = type == Material.matchMaterial(it.substring(1).uppercase())
      }

      else registry.findApplicable(it) ?: UndiscoveredApplicable(it)
    } }!!

    val forbidden = json.onAnyArrayOfStrings(DeserializationNames.Enchantment.FORBIDDEN) { mapNotNull { Material.matchMaterial(it) } } ?: emptyList()
    val seekers = json.onAnyArrayOfStrings(DeserializationNames.Enchantment.SEEKERS) { map { registry.findSeeker(it) ?: UndiscoveredEnchantmentSeeker(it) } } ?: emptyList()
    val conflicts = json.onAnyArrayOfStrings(DeserializationNames.Enchantment.CONFLICTS) { mapNotNull { it } } ?: emptyList()

    val unique = json.anyBoolean(DeserializationNames.Enchantment.UNIQUE) ?: false
    val stackable = json.anyBoolean(DeserializationNames.Enchantment.STACKABLE) ?: false

    val conditions = json.onAnyArray(DeserializationNames.Enchantment.CONDITIONS) { mapNotNull { loadCondition(it) } } ?: emptyList()
    val levels = json.onAnyArray(DeserializationNames.Enchantment.LEVELS) { mapNotNull { loadLevel(it) } } ?: emptyList()
    val obtainmentRestrictions = json.onAnyArray(DeserializationNames.Enchantment.OBTAINMENT) { mapNotNull { loadObtainmentRestriction(it) } } ?: emptyList()
    val requiredEnchantments = json.onAnyArray(DeserializationNames.Enchantment.REQUIRED_ENCHANTMENTS) { mapNotNull { loadRequiredEnchantment(it) } } ?: emptyList()
    val requiredPlugins = json.onAnyArray(DeserializationNames.Enchantment.REQUIRED_PLUGINS) { mapNotNull { loadRequiredPlugin(it) } } ?: emptyList()

    val worldBlacklist = json.anyArrayOfStrings(DeserializationNames.Enchantment.WORLD_BLACKLIST) ?: emptyList()
    val worldWhitelist = json.anyArrayOfStrings(DeserializationNames.Enchantment.WORLD_WHITELIST) ?: emptyList()

    if (levels.isEmpty()) return null

    val key = try {
      NamespacedKey(plugin, name.normalize())
    } catch (e: IllegalArgumentException) {
      return null
    }

    return UnderscoreEnchantment(
      name = name, author = author, description = description, key = key,
      activationChance = chance, cooldown = cooldown, trigger = trigger, activationIndicator = indicator,
      applicables = applicables, forbiddenMaterials = forbidden, conditions = conditions, conflicts = conflicts,
      levels = levels, obtainmentRestrictions = obtainmentRestrictions, enchantmentSeekers = seekers,
      unique = unique, stackable = stackable,
      requiredEnchantments = requiredEnchantments, requiredPlugins = requiredPlugins,
      worldBlacklist = worldBlacklist, worldWhitelist = worldWhitelist,
    )
  }

  /**
   * Loads a condition from a JSON [element].
   * @return the parsed EnchantmentCondition, or null if something goes wrong.
   */
  @Since("2.2")
  @Internal
  private fun loadCondition(element: JsonElement): EnchantmentCondition? {
    val json = element.asJsonObject ?: return null

    val fullConditionString = json.anyString(DeserializationNames.Condition.CONDITION) ?: return null
    val negate = json.anyBoolean(DeserializationNames.Condition.NEGATE) ?: false
    val targetPlayer = json.onAnyStringStrict(DeserializationNames.Condition.TARGET, { "first" }) { toTarget() }

    val conditionSplit = fullConditionString.split(" ").toMutableList()
    val condition = registry.findCondition(conditionSplit[0]) ?: UndiscoveredCondition(conditionSplit[0])
    conditionSplit.removeFirst()

    return EnchantmentCondition(condition, negate, targetPlayer, conditionSplit)
  }

  /**
   * Loads an EnchantmentLevel from a JSON [element].
   * @return the enchantment level, or null if something goes wrong.
   */
  @Since("2.2")
  @Internal
  private fun loadLevel(element: JsonElement): EnchantmentLevel? {
    val json = element.asJsonObject ?: return null

    val levelIndex = json.anyInt(DeserializationNames.Level.INDEX) ?: return null
    val levelChance = json.anyDoubleStrict(DeserializationNames.Level.CHANCE) { 100.0 }
    val levelCooldown = json.anyLongStrict(DeserializationNames.Level.COOLDOWN) { 0 }

    val actions = json.onAnyArray(DeserializationNames.Level.ACTIONS) { mapNotNull { loadActions(it) } }?.flatten() ?: return null
    val conditions = json.onAnyArray(DeserializationNames.Level.CONDITIONS) { mapNotNull { loadCondition(it) } } ?: emptyList()

    return EnchantmentLevel(levelIndex, conditions, levelChance, Time(levelCooldown, TimeMeasurementUnit.TICKS), actions)
  }

  /**
   * Loads action(s) from a JSON [element].
   * @return the list of actions, or null if something goes wrong.
   */
  @Since("2.2")
  @Internal
  private fun loadActions(element: JsonElement): List<EnchantmentAction>? {
    val json = element.asJsonObject ?: return null

    val singleAction = json.anyString(DeserializationNames.Action.SINGLE_ACTION)
    val multipleActions = json.anyArray(DeserializationNames.Action.MULTIPLE_ACTIONS)

    // In an action object, there can only be either the "action" field, or the "actions" field.
    if (singleAction == null && multipleActions == null) return null
    if (singleAction != null && multipleActions != null) return null

    val actionDelayTicks = json.anyIntStrict(DeserializationNames.Action.DELAY) { 0 }
    val actionChance = json.anyDoubleStrict(DeserializationNames.Action.CHANCE) { 100.0 }
    val targetPlayer = json.onAnyStringStrict(DeserializationNames.Action.TARGET, { "first" }) { toTarget() }

    val conditions = json.onAnyArray(DeserializationNames.Action.CONDITIONS) { mapNotNull { loadCondition(it) } } ?: emptyList()

    if (singleAction != null) {
      val (action, actionSplit) = json.anyElement(DeserializationNames.Action.SINGLE_ACTION)?.let { discoverAction(it) } ?: return null

      return listOf(EnchantmentAction(action, actionChance, Time(actionDelayTicks, TimeMeasurementUnit.TICKS), targetPlayer, actionSplit, conditions))
    } else if (multipleActions != null) {
      val actionList = mutableListOf<EnchantmentAction>()

      multipleActions.forEach {
        val (action, actionSplit) = discoverAction(it) ?: return@forEach
        actionList.add(EnchantmentAction(action, actionChance, Time(actionDelayTicks, TimeMeasurementUnit.TICKS), targetPlayer, actionSplit, conditions))
      }

      return actionList
    }

    return null
  }

  /**
   * A tiny utility for parsing a JSON [element] representing a string into an action type
   * and its arguments.
   * @return the resulting action type and its arguments, or null if something goes wrong.
   */
  @Since("2.2")
  @Internal
  private fun discoverAction(element: JsonElement?): Pair<RegistrableAction, List<String>>? {
    val fullAction = element?.asString ?: return null
    val actionSplit = fullAction.split(" ").toMutableList()
    val actionName = actionSplit[0]
    actionSplit.removeFirst()

    val action = registry.findAction(actionName) ?: UndiscoveredAction(actionName)
    return action to actionSplit
  }

  /**
   * Loads an obtainment restriction from a JSON [element].
   * @return the obtainment restriction, or null if something goes wrong.
   */
  @Since("2.2")
  @Internal
  private fun loadObtainmentRestriction(element: JsonElement): EnchantmentObtainmentRestriction? {
    val json = element.asJsonObject ?: return null

    val type = json.onAnyString(DeserializationNames.Obtainment.TYPE) { EnchantmentObtainmentMeans.entries.find { entry -> entry.name == this.uppercase() } } ?: return null
    val levels = json.anyString(DeserializationNames.Obtainment.LEVELS) ?: return null
    return EnchantmentObtainmentRestriction.of(type, levels)
  }

  /**
   * Loads a required enchantment from a JSON [element].
   * @return the required enchantment, or null if something goes wrong.
   */
  @Since("2.2")
  @Internal
  private fun loadRequiredEnchantment(element: JsonElement): RequiredEnchantment? {
    val json = element.asJsonObject ?: return null

    val keyString = json.anyString(DeserializationNames.RequiredEnchantment.KEY) ?: return null
    val seekers = json.onAnyArrayOfStrings(DeserializationNames.RequiredEnchantment.SEEKERS) { mapNotNull { registry.findSeeker(it) } } ?: emptyList()

    val enchantmentLevels = json.anyString(DeserializationNames.RequiredEnchantment.LEVELS)?.let {
      val levels = mutableListOf<Int>()

      val parts = it.split(",")
      for (part in parts) {
        if (!part.contains("-")) {
          levels.add(part.toIntOrNull() ?: continue)
          continue
        }

        val deepPart = part.split("-")
        val start = deepPart.getOrNull(0)?.toIntOrNull() ?: continue
        val end = deepPart.getOrNull(1)?.toIntOrNull() ?: continue

        for (i in start..end) levels.add(i)
      }

      levels
    } ?: emptyList()

    val minecraftKey = NamespacedKey.minecraft(keyString)
    val underscoreKey = NamespacedKey(plugin, keyString)
    val key = if (Registry.ENCHANTMENT.get(minecraftKey) != null) minecraftKey else underscoreKey

    return RequiredEnchantment(key, seekers, enchantmentLevels)
  }

  @Since("2.2")
  @Internal
  private fun loadRequiredPlugin(element: JsonElement): RequiredPlugin? {
    val json = element.asJsonObject ?: return null

    val pluginName = json.anyString(DeserializationNames.RequiredPlugin.PLUGIN_NAME) ?: return null
    val displayName = json.anyStringStrict(DeserializationNames.RequiredPlugin.DISPLAY_NAME) { pluginName }
    val link = json.anyString(DeserializationNames.RequiredPlugin.LINK) ?: return null

    return RequiredPlugin(
      pluginName = pluginName,
      displayName = displayName,
      link = link
    )
  }
}
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

package com.roughlyunderscore.ue

import com.roughlyunderscore.annotations.Since
import com.roughlyunderscore.annotations.Stable
import com.roughlyunderscore.data.*
import com.roughlyunderscore.enums.DataRetrievalType
import com.roughlyunderscore.enums.EnchantmentObtainmentMeans
import com.roughlyunderscore.enums.NotifiedPlayer
import com.roughlyunderscore.enums.TargetType
import com.roughlyunderscore.events.EnchantmentActivateEvent
import com.roughlyunderscore.registry.*
import com.roughlyunderscore.ue.data.Constants
import com.roughlyunderscore.ue.debug.describers.EnchantmentDescriber
import com.roughlyunderscore.ue.registry.RegistryImpl
import com.roughlyunderscore.ue.registry.RegistryImpl.Companion.isUndiscovered
import com.roughlyunderscore.ue.utils.TaskUtils.delayed
import com.roughlyunderscore.ulib.data.Time
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

/**
 * This class represents an enchantment registered by UnderscoreEnchants. It contains all the data needed to
 * register the enchantment, including:
 * - the name of the enchantment
 * - the chance of the enchantment triggering
 * - the cooldown of the enchantment
 * - the trigger of the enchantment
 * - the applicables of the enchantment
 * - the forbidden materials of the enchantment
 * - the conditions of the enchantment
 * - the levels of the enchantment
 */
@Since("2.2")
data class UnderscoreEnchantment(
  /**
   * The display name of the enchantment. An enchantment's name must be unique.
   */
  @Since("2.2")
  val name: String,

  /**
   * The author of the enchantment.
   */
  @Since("2.2")
  override val author: String,

  /**
   * The description of the enchantment.
   */
  @Since("2.2")
  override val description: List<String>,

  /**
   * The key of the enchantment. This is used to identify the enchantment. An enchantment's key must be unique.
   */
  @Since("2.2")
  private val key: NamespacedKey,

  /**
   * The chance of the enchantment triggering. This is a double between 0 and 100. If not specified,
   * defaults to 100.
   */
  @Since("2.2")
  override val activationChance: Double = 100.0,

  /**
   * The cooldown of the enchantment. If not specified, defaults
   * to 0.
   */
  @Since("2.2")
  override val cooldown: Time = Time.ZERO,

  /**
   * The trigger of the enchantment.
   */
  @Since("2.2")
  override val trigger: RegistrableTrigger,

  /**
   * The activation indicator of the enchantment.
   */
  @Since("2.2")
  override val activationIndicator: RegistrableActivationIndicator,

  /**
   * The player that will be notified about the activation.
   */
  @Since("2.2")
  override val notifiedPlayer: NotifiedPlayer,

  /**
   * The list of applicables for this enchantment. An enchantment can be legally applied to
   * any of these.
   */
  @Since("2.2")
  override val applicables: List<RegistrableApplicable>,

  /**
   * The list of forbidden materials for this enchantment. An enchantment cannot be legally applied to
   * any of these.
   */
  @Since("2.2")
  override val forbiddenMaterials: List<Material>,

  /**
   * The map of conditions for this enchantment. An enchantment can only trigger if all of these
   * conditions conform to their expected values.
   */
  @Since("2.2")
  override val conditions: List<EnchantmentCondition>,

  /**
   * The list of enchantment names that conflict with this enchantment. An enchantment cannot be
   * legally applied to an item if any of these enchantments are already applied to it.
   */
  @Since("2.2")
  override val conflicts: List<String>,

  /**
   * The list of levels for this enchantment. Depending on what level of the enchantment is used,
   * the according level's conditions & actions will be used.
   */
  @Since("2.2")
  override val levels: List<EnchantmentLevel>,

  /**
   * The list of enchantment seekers for this enchantment. See the
   * documentation for [RegistrableEnchantmentSeeker.seekItems].
   */
  @Since("2.2")
  override val enchantmentSeekers: List<RegistrableEnchantmentSeeker>,

  /**
   * The list of obtainment methods for this enchantment. A level of enchantment can only be obtained
   * by a certain means of obtainment ([EnchantmentObtainmentMeans]) if the list of restrictions
   * does not contain a restriction with that means and that level.
   */
  @Since("2.2")
  override val obtainmentRestrictions: List<EnchantmentObtainmentRestriction>,

  /**
   * Whether this enchantment is unique. If an enchantment is unique, it means that no
   * other enchantments can be present on the item. Enchanting an already enchanted item
   * with a unique enchantment, or adding a new enchantment to an item with a unique
   * enchantment is not possible.
   */
  @Since("2.2")
  override val unique: Boolean,

  /**
   * Whether this enchantment is stackable. Stackable enchantments will execute their actions the
   * same amount of times that an enchantment appears on an item. This means that if the player has
   * two enchanted items with level 2 and one item with level 1, the actions of level 2 will be executed
   * twice (each with its own chance and conditions check), and the actions of level 1 will be executed once.
   */
  @Since("2.2")
  override val stackable: Boolean,

  /**
   * The list of all enchantments that are required to be present with the player. A required enchantment
   * can have its own list of seekers (out of which, at least one must find a match), and a list of accepted
   * levels.
   */
  @Since("2.2")
  override val requiredEnchantments: List<RequiredEnchantment>,


  /**
   * The list of worlds that are blacklisted for this enchantment. If this list is empty,
   * the enchantment is not blacklisted anywhere. If this list is not empty, the enchantment
   * is blacklisted in all worlds that are in this list. This means that the enchantment will
   * not trigger in these worlds.
   */
  @Since("2.2")
  override val worldBlacklist: List<String>,

  /**
   * The list of worlds that are whitelisted for this enchantment. If this list is empty,
   * the enchantment is not whitelisted anywhere. If this list is not empty, the enchantment
   * is whitelisted in all worlds that are in this list. This means that the enchantment will
   * only trigger in these worlds.
   */
  @Since("2.2")
  override val worldWhitelist: List<String>,

  /**
   * The list of all plugins that are required for this enchantment to load. If this list is not empty,
   * the enchantment will not load if any of the plugins in this list are not present. This is used
   * to make sure that any dependencies (such as plugins providing new actions) are present before
   * the enchantment is loaded.
   */
  @Since("2.2")
  override val requiredPlugins: List<RequiredPlugin>,
) : RegistrableEnchantment, Listener {
  /**
   * Registers the enchantment. This makes it an actual enchantment, which means that it will
   * appear in game and will actually have an effect.
   *
   * Since the update 2.2, the enchantments are no longer registered for vanilla. Now they are
   * applied to lore & PDC. This is not a big hassle, as everything enchantment-related on the
   * server with UnderscoreEnchants should be handled by UnderscoreEnchants and UnderscoreEnchants
   * only. For example:
   * - The vanilla enchantment table is not replaced, but its behavior is redefined;
   * - The vanilla anvil is not replaced, but its behavior is redefined;
   * - The vanilla grindstone is not replaced, but its behavior is redefined;
   * - The /enchant command should not be used, whereas /ue enchant should be used instead;
   * - etc.
   *
   * This makes the plugin fully and completely incompatible with other enchantment plugins. As
   * such, this plugin should only be used in production if the user is sure that they will not need
   * any other enchantment plugin.
   *
   * On any instance of this class, this method should only be called once. If called multiple times,
   * errors will occur.
   * @see EnchantmentLevel
   * @see RegistrableTrigger
   * @see RegistrableApplicable
   * @see RegistrableCondition
   */
  @Since("2.2")
  @Stable
  fun initializeEnchantment(plugin: UnderscoreEnchantsPlugin) {
    val registry = plugin.registry
    val holder = trigger.getTriggerDataHolder()

    // Registering the enchantment is really registering an event and adding the enchantment to some lists.
    plugin.server.pluginManager.registerEvent(holder.eventType, this, EventPriority.HIGHEST, { _, event ->
      // There is no clear way as of now to unregister a listener when an enchantment is unloaded.
      // As such, check here.
      if (registry.findEnchantmentByKey(key) == null) return@registerEvent

      // Check that all the required plugins are loaded.
      if (requiredPlugins.any { !Bukkit.getPluginManager().isPluginEnabled(it.pluginName) }) return@registerEvent

      // Check if the event is cancelled.
      if (event is Cancellable && event.isCancelled) return@registerEvent

      // Get the player.
      val player = holder.dataRetrievalMethods[DataRetrievalType.FIRST_PLAYER]!!.invoke(event) as Player
      val worldName = player.world.name

      // Check if the enchantment is blacklisted or whitelisted in the world.
      if (worldBlacklist.isNotEmpty() && worldName in worldBlacklist) return@registerEvent
      if (worldWhitelist.isNotEmpty() && worldName !in worldWhitelist) return@registerEvent

      val metadata = registry.findEnchantmentPack(name)?.metadata
      if (metadata != null) {
        val metadataWorldBlacklist = metadata.worldBlacklist
        val metadataWorldWhitelist = metadata.worldWhitelist

        if (metadataWorldBlacklist.isNotEmpty() && worldName in metadataWorldBlacklist) return@registerEvent
        if (metadataWorldWhitelist.isNotEmpty() && worldName !in metadataWorldWhitelist) return@registerEvent
      }

      // Check the chance.
      if (Constants.RANDOM.nextDouble() * 100 > activationChance) return@registerEvent

      // Check the conditions.
      for (condition in conditions) {
        val arguments = condition.arguments.map { argument ->
          registry.replacePlaceholders(argument, event, trigger, condition.target)
        }

        val result = condition.condition.evaluateCondition(trigger, event, condition.target, arguments)
        if ((result && !condition.negate) || (!result && condition.negate)) continue
        return@registerEvent
      }

      // Check the cooldown.
      if (plugin.storage.hasCooldown(player.uniqueId, key) != -1L) return@registerEvent

      // Check if the enchantment is enabled for the player.
      if (plugin.storage.isEnchantmentDisabled(player.uniqueId, key)) return@registerEvent

      // Find the level of the enchantment
      val items = enchantmentSeekers.flatMap { seeker -> seeker.seekItems(player) }.filterNotNull()
      val enchantmentLevels = items.mapNotNull { item -> item.itemMeta?.persistentDataContainer?.get(key, PersistentDataType.INTEGER) }
      val level = enchantmentLevels.mapNotNull { levels.getOrNull(it - 1) }.maxByOrNull { it.level } ?: return@registerEvent

      // Ensure that all required enchantments are present
      for (requiredEnchantment in requiredEnchantments) {
        val requiredKey = requiredEnchantment.key
        val soughtItems = requiredEnchantment.seekers.also { if (it.isEmpty()) enchantmentSeekers }
          .flatMap { seeker -> seeker.seekItems(player) }
          .filterNotNull()

        if (requiredKey.namespace == NamespacedKey.MINECRAFT) {
          val enchantment = Bukkit.getRegistry(Enchantment::class.java)?.get(requiredKey) ?: continue
          val soughtLevels = soughtItems.map { item -> item.getEnchantmentLevel(enchantment) }.filter { it != 0 }.toSet()

          if (requiredEnchantment.levels.intersect(soughtLevels).isEmpty()) return@registerEvent
          continue
        }

        val levels = soughtItems.mapNotNull { item -> item.itemMeta?.persistentDataContainer }
          .map { container -> container.get(requiredKey, PersistentDataType.INTEGER) }.toSet()

        if (requiredEnchantment.levels.intersect(levels).isEmpty()) return@registerEvent
      }



      if (!stackable) executeLevel(items, level, registry, event, player, plugin)
      else {
        // If the enchantment is stackable, execute o̶r̶d̶e̶r̶ ̶6̶6̶ the levels the respective amount of times
        enchantmentLevels.map { index -> levels[index - 1] }.forEach { lvl -> executeLevel(items, lvl, registry, event, player, plugin) }
      }

      // Add a cooldown
      // Cooldowns are in ticks, they must be converted to milliseconds
      // 1 tick = 0.05s = 50ms
      val cooldownExpiry = System.currentTimeMillis() + cooldown.millis
      if (cooldown.amount != 0L) plugin.storage.addCooldown(player.uniqueId, key, cooldownExpiry)
    }, plugin)

    // Registering the enchantment for lore & PDC.
    registry.register(this, plugin)
  }

  private fun executeLevel(
    enchantedItems: List<ItemStack>,
    level: EnchantmentLevel,
    registry: RegistryImpl,
    event: Event,
    player: Player,
    plugin: UnderscoreEnchantsPlugin,
    applyCooldown: Boolean = true
  ) {
    // Check the level's conditions
    for (condition in level.conditions) {
      val arguments = condition.arguments.parseArguments(registry, enchantedItems, event, trigger, condition.target)
      val result = condition.condition.evaluateCondition(trigger, event, condition.target, arguments)
      if ((result && !condition.negate) || (!result && condition.negate)) continue

      return
    }

    // Check the level's chance
    if (Constants.RANDOM.nextDouble() * 100 > level.chance) return

    val activationEvent = EnchantmentActivateEvent(player, key, level.level)
    Bukkit.getPluginManager().callEvent(activationEvent)
    if (activationEvent.isCancelled) return

    // I definitely need to figure out how to not spam this if a level gets executed multiple times (stackable)
    activationIndicator.indicateAboutActivation(
      plugin.globalLocale.defaultActivationIndicator.replace("<enchantment>", name), when (notifiedPlayer) {
        NotifiedPlayer.FIRST -> player
        NotifiedPlayer.SECOND -> trigger.getTriggerDataHolder().dataRetrievalMethods[DataRetrievalType.SECOND_PLAYER]!!.invoke(event) as Player
      }
    )

    // Start the enchantment actions
    action@ for (action in level.actions) {
      if (Constants.RANDOM.nextDouble() * 100 > action.chance) continue

      for (condition in action.conditions) {
        val arguments = condition.arguments.parseArguments(registry, enchantedItems, event, trigger, condition.target)
        val result = condition.condition.evaluateCondition(trigger, event, condition.target, arguments)
        if ((result && !condition.negate) || (!result && condition.negate)) continue

        continue@action
      }

      val arguments = action.arguments.parseArguments(registry, enchantedItems, event, trigger, action.target)

      delayed(action.delay.ticks, plugin) {
        val modifications = action.action.execute(event, trigger, arguments, action.target) ?: return@delayed
        for ((field, value) in (modifications.fieldModifications ?: emptyMap()).entries) {
          field.set(value, event)
        }

        for ((method, methodArguments) in (modifications.methodsToCall ?: emptyMap()).entries) {
          method.invoke(event, *methodArguments.toTypedArray())
        }
      }
    }

    if (applyCooldown) {
      val levelCooldownExpiry = System.currentTimeMillis() + level.cooldown.millis
      if (level.cooldown.amount != 0L) plugin.storage.addCooldown(player.uniqueId, key, levelCooldownExpiry)
    }
  }

  /**
   * Replaces all placeholders, the {amount} keyword and parses calculations
   */
  private fun List<String>.parseArguments(registry: RegistryImpl, enchantedItems: List<ItemStack>, event: Event, trigger: RegistrableTrigger, target: TargetType): List<String> {
    val levels = enchantedItems
      .mapNotNull { item -> item.itemMeta?.persistentDataContainer?.get(key, PersistentDataType.INTEGER) }
      .groupingBy { it }.eachCount()

    return this
      .map { argument ->
        argument.replace(Constants.AMOUNT_REGEX) { match ->
          val level = match.value.replace("{amount_", "").replace("}", "").toIntOrNull() ?: return@replace ""
          return@replace levels[level].toString()
        }
      }

      .map { argument -> registry.replacePlaceholders(argument, event, trigger, target) }
  }

  override fun getKey() = key
  override fun getDescriber() = EnchantmentDescriber
  override val aliases = listOf(name)

  /**
   * Checks if this enchantment has any undiscovered members (trigger, seekers, etc).
   */
  internal fun anyUndiscovered() = trigger.isUndiscovered() ||
    enchantmentSeekers.any { it.isUndiscovered() } ||
    levels.any { it.isUndiscovered() } ||
    activationIndicator.isUndiscovered() ||
    applicables.any { it.isUndiscovered() } ||
    conditions.any { it.condition.isUndiscovered() }

}
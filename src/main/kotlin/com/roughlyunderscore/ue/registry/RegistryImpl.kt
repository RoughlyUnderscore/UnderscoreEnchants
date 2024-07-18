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

@file:Suppress("UnstableAPIUsage")

package com.roughlyunderscore.ue.registry

import com.roughlyunderscore.ue.UnderscoreEnchantment
import com.roughlyunderscore.ue.UnderscoreEnchantsPlugin
import com.roughlyunderscore.annotations.Beta
import com.roughlyunderscore.annotations.Since
import com.roughlyunderscore.annotations.Stable
import com.roughlyunderscore.annotations.Undiscovered
import com.roughlyunderscore.data.EnchantmentAction
import com.roughlyunderscore.data.EnchantmentCondition
import com.roughlyunderscore.data.EnchantmentLevel
import com.roughlyunderscore.ue.data.Constants
import com.roughlyunderscore.data.EnchantmentPack
import com.roughlyunderscore.enums.TargetType
import com.roughlyunderscore.registry.*
import com.roughlyunderscore.registry.internal.*
import com.roughlyunderscore.ue.utils.loadPackFromTARFile
import com.roughlyunderscore.ulib.data.safeValueOr
import com.roughlyunderscore.ulib.text.replaceMany
import com.roughlyunderscore.ue.utils.mapToDrt
import com.roughlyunderscore.ulib.text.containsIgnoreCase
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import org.mariuszgromada.math.mxparser.Expression
import org.reflections.Reflections
import org.reflections.scanners.Scanners
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder
import org.reflections.util.FilterBuilder
import java.io.FileFilter

/**
 * This class is the one and only implementation of the [UEAPIRegistry] interface.
 * It is responsible for registering all the [Registrable]s in the plugin,
 * and for providing a way to access and handle them.
 *
 * Any other implementations of the [UEAPIRegistry] interface are not supported
 * by UnderscoreEnchants, and any errors related to those will not be investigated.
 *
 * The only way to access this implementation is through the UnderscoreEnchantsAPI method
 * [getRegistry][com.roughlyunderscore.UEAPI.registry].
 * @see UEAPIRegistry
 */
@Since("2.2")
class RegistryImpl(val underscoreEnchantsPlugin: UnderscoreEnchantsPlugin) : UEAPIRegistry {
  internal val actions = mutableMapOf<RegistrableAction, JavaPlugin>()
  internal val applicables = mutableMapOf<RegistrableApplicable, JavaPlugin>()
  internal val conditions = mutableMapOf<RegistrableCondition, JavaPlugin>()
  internal val enchantments = mutableMapOf<RegistrableEnchantment, JavaPlugin>()
  internal val indicators = mutableMapOf<RegistrableActivationIndicator, JavaPlugin>()
  internal val placeholders = mutableMapOf<RegistrablePlaceholder, JavaPlugin>()
  internal val seekers = mutableMapOf<RegistrableEnchantmentSeeker, JavaPlugin>()
  internal val triggers = mutableMapOf<RegistrableTrigger, JavaPlugin>()

  override val registeredActions get() = actions.keys.toList()
  override val registeredActivationIndicators get() = indicators.keys.toList()
  override val registeredApplicables get() = applicables.keys.toList()
  override val registeredConditions get() = conditions.keys.toList()
  override val registeredEnchantments get() = enchantments.keys.toList()
  override val registeredEnchantmentSeekers get() = seekers.keys.toList()
  override val registeredPlaceholders get() = placeholders.keys.toList()
  override val registeredTriggers get() = triggers.keys.toList()

  val packs = mutableMapOf<String, EnchantmentPack>()

  private val providers = mutableListOf<RegistrablesProvider>()
  val enchantable = mutableSetOf<Material>()

  /**
   * Populates the set of all materials that can be enchanted by at least one enchantment.
   */
  @Since("2.2")
  @Stable
  fun initEnchantable() {
    enchantable.clear()
    enchantable.addAll(Material.entries.filter { mat ->
      enchantments.keys.any { ench ->
        ench.applicables.any { app ->
          app.canBeAppliedTo(mat)
        }
      }
    })
  }

  fun register(registrable: Registrable, registree: JavaPlugin) {
    when (registrable) {
      is RegistrableActivationIndicator -> registerIndicator(registrable, registree)
      is RegistrableAction -> registerAction(registrable, registree)
      is RegistrableApplicable -> registerApplicable(registrable, registree)
      is RegistrableCondition -> registerCondition(registrable, registree)
      is RegistrableEnchantmentSeeker -> registerSeeker(registrable, registree)
      is RegistrablePlaceholder -> registerPlaceholder(registrable, registree)
      is RegistrableTrigger -> registerTrigger(registrable, registree)

      is RegistrableEnchantment -> {
        if (registree.description != underscoreEnchantsPlugin.description)
          throw java.lang.IllegalArgumentException("This plugin cannot register an enchantment (${registree.description.main})")

        registerEnchantment(registrable, registree)
      }
      else -> throw IllegalArgumentException("Registrable $registrable is not supported")
    }
  }

  override fun refresh() {
    actions.clear()
    applicables.clear()
    conditions.clear()
    indicators.clear()
    placeholders.clear()
    seekers.clear()
    triggers.clear()

    providers.forEach {
      if (!Bukkit.getPluginManager().isPluginEnabled(it.getAssociatedPlugin().name)) return@forEach
      it.getProvidedRegistrables().forEach { registrable -> register(registrable, it.getAssociatedPlugin()) }
    }
  }

  override fun provide(provider: RegistrablesProvider) {
    val plugin = provider.getAssociatedPlugin()
    val registrables = provider.getProvidedRegistrables()

    providers.add(provider)

    if (!Bukkit.getPluginManager().isPluginEnabled(plugin.name)) return
    registrables.forEach { register(it, plugin) }

    // Go through all enchantments that have anything undiscovered and try to load them again
    enchantments.keys.mapNotNull { it as? UnderscoreEnchantment}.filter { it.anyUndiscovered() }.forEach { enchantment ->
      enchantments.remove(enchantment)

      // Attempt to find the new trigger/seeker/action/etc, and on failure, give back an Undiscovered instance of it
      // This way the enchantment can go through multiple rounds of rediscovering stuff without losing any data
      // This code probably can be cleaner, but it is perfectly readable as is.

      var newEnchantment = enchantment.copy()

      if (newEnchantment.trigger.isUndiscovered()) {
        val name = newEnchantment.trigger.aliases.first()
        newEnchantment = newEnchantment.copy(trigger = findTrigger(name) ?: UndiscoveredTrigger(name))
      }

      if (newEnchantment.enchantmentSeekers.any { it.isUndiscovered() }) {
        newEnchantment = newEnchantment.copy(enchantmentSeekers = newEnchantment.enchantmentSeekers.map {
          val name = it.aliases.first()
          findSeeker(name) ?: UndiscoveredEnchantmentSeeker(name)
        })
      }

      if (newEnchantment.levels.any { it.isUndiscovered() }) {
        newEnchantment = newEnchantment.copy(levels = newEnchantment.levels.map { rediscoverLevel(it) })
      }

      if (newEnchantment.activationIndicator.isUndiscovered()) {
        val name = newEnchantment.activationIndicator.aliases.first()
        newEnchantment = newEnchantment.copy(activationIndicator = findIndicator(name) ?: UndiscoveredActivationIndicator(name))
      }

      if (newEnchantment.applicables.any { it.isUndiscovered() }) {
        newEnchantment = newEnchantment.copy(applicables = newEnchantment.applicables.map {
          val name = it.aliases.first()
          findApplicable(name) ?: UndiscoveredApplicable(name)
        })
      }

      if (newEnchantment.conditions.any { it.condition.isUndiscovered() }) {
        newEnchantment = newEnchantment.copy(conditions = newEnchantment.conditions.map { rediscoverCondition(it) })
      }

      // If there's nothing undiscovered left, registers the enchantment properly
      if (!newEnchantment.anyUndiscovered()) newEnchantment.initializeEnchantment(underscoreEnchantsPlugin)
      // Otherwise just adds it to the map
      else registerEnchantment(enchantment, underscoreEnchantsPlugin)
    }
  }

  /**
   * This method initializes all the enchantments.
   * It is done in the following way (assuming a .json enchantment file):
   * 1. The enchantment's file is loaded with Configurate.
   * 2. Each relevant field is read from the file into a variable.
   * 3. The enchantment is registered as a [UnderscoreEnchantment] object.
   */
  @Since("2.2")
  internal fun initEnchantments() {
    val folder = underscoreEnchantsPlugin.dataFolder.resolve("enchantments")

    val jsonFiles = folder.listFiles(FileFilter { it.extension == "json" })
    val tarFiles = folder.listFiles(FileFilter { it.extension == "tar" })

    if (tarFiles != null) {
      for (file in tarFiles) {
        val pack = file.loadPackFromTARFile(underscoreEnchantsPlugin)

        loadPack(pack ?: continue)
        pack.enchantments.forEach {
          val enchantment = it as UnderscoreEnchantment
          if (!enchantment.anyUndiscovered()) enchantment.initializeEnchantment(underscoreEnchantsPlugin) // registers the enchantment properly
          else registerEnchantment(enchantment, underscoreEnchantsPlugin) // only adds it to the map
        }
      }
    }

    if (jsonFiles != null) {
      for (file in jsonFiles) {
        val enchantment = try {
          file.reader().use { underscoreEnchantsPlugin.gson.fromJson(it, UnderscoreEnchantment::class.java) } ?: continue
        } catch (ex: Exception) {
          underscoreEnchantsPlugin.logger.severe("Failed to load enchantment from file ${file.name}!")
          continue
        }

        if (!enchantment.anyUndiscovered()) enchantment.initializeEnchantment(underscoreEnchantsPlugin)
        else registerEnchantment(enchantment, underscoreEnchantsPlugin)
      }
    }

    initEnchantable()
  }

  override fun findAction(name: String) = actions.keys.firstOrNull { it.aliases.contains(name) }
  override fun findApplicable(name: String) = applicables.keys.firstOrNull { it.aliases.contains(name) }
  override fun findCondition(name: String) = conditions.keys.firstOrNull { it.aliases.contains(name) }
  override fun findEnchantment(name: String) = enchantments.keys.firstOrNull { it.aliases.containsIgnoreCase(name) }
  override fun findIndicator(name: String) = indicators.keys.firstOrNull { it.aliases.contains(name) }
  override fun findPlaceholder(name: String) = placeholders.keys.firstOrNull { it.aliases.contains(name) }
  override fun findSeeker(name: String) = seekers.keys.firstOrNull { it.aliases.contains(name) }
  override fun findTrigger(name: String) = triggers.keys.firstOrNull { it.aliases.contains(name) }

  override fun findEnchantmentByKey(key: NamespacedKey) = enchantments.keys.firstOrNull { it.key == key }

  override fun findActions(plugin: JavaPlugin) = actions.keys.filter { actions[it] == plugin }
  override fun findApplicables(plugin: JavaPlugin) = applicables.keys.filter { applicables[it] == plugin }
  override fun findConditions(plugin: JavaPlugin) = conditions.keys.filter { conditions[it] == plugin }
  override fun findEnchantments(plugin: JavaPlugin) = enchantments.keys.filter { enchantments[it] == plugin }
  override fun findIndicators(plugin: JavaPlugin) = indicators.keys.filter { indicators[it] == plugin }
  override fun findPlaceholders(plugin: JavaPlugin) = placeholders.keys.filter { placeholders[it] == plugin }
  override fun findSeekers(plugin: JavaPlugin) = seekers.keys.filter { seekers[it] == plugin }
  override fun findTriggers(plugin: JavaPlugin) = triggers.keys.filter { triggers[it] == plugin }

  /**
   * This method loads and **only loads** a [pack]. Enchantments do not get registered.
   */
  @Since("2.2")
  fun loadPack(pack: EnchantmentPack) {
    if (packs.containsKey(pack.metadata.name)) throw IllegalArgumentException("Pack ${pack.metadata.name} is already loaded")
    packs[pack.metadata.name] = pack
  }

  fun unloadPack(name: String) {
    packs.remove(name)?.let { for (enchantment in it.enchantments) unregisterEnchantmentByKey(enchantment.key) }
  }

  override fun findEnchantmentPack(name: String): EnchantmentPack? = packs[name]

  override fun findEnchantmentByKeyString(keyString: String) = enchantments.keys.firstOrNull { it.key.key.equals(keyString, true) }

  private fun registerAction(action: RegistrableAction, plugin: JavaPlugin) {
    val duplicate = findDuplicate(action)

    if (duplicate != null) {
      throw IllegalArgumentException(underscoreEnchantsPlugin.globalLocale.alreadyRegistered
        .replace("<new>", action.toString())
        .replace("<old", duplicate.aliases.toString())
        .replace("<plugin>", plugin.name))
    }

    actions[action] = plugin
  }

  private fun registerApplicable(applicable: RegistrableApplicable, plugin: JavaPlugin) {
    val duplicate = findDuplicate(applicable)

    if (duplicate != null) {
      throw IllegalArgumentException(underscoreEnchantsPlugin.globalLocale.alreadyRegistered
        .replace("<new>", applicable.aliases.toString())
        .replace("<old", duplicate.aliases.toString())
        .replace("<plugin>", plugin.name))
    }

    applicables[applicable] = plugin
  }

  private fun registerCondition(condition: RegistrableCondition, plugin: JavaPlugin) {
    val duplicate = findDuplicate(condition)

    if (duplicate != null) {
      throw IllegalArgumentException(underscoreEnchantsPlugin.globalLocale.alreadyRegistered
        .replace("<new>", condition.aliases.toString())
        .replace("<old", duplicate.aliases.toString())
        .replace("<plugin>", plugin.name))
    }

    conditions[condition] = plugin
  }

  /**
   * This method does not register the enchantment as you would expect, instead it just
   * checks that there is no registered enchantment with this name and then
   * adds it to the map. To register an enchantment fully, [UnderscoreEnchantment.initializeEnchantment] is used.
   */
  private fun registerEnchantment(enchantment: RegistrableEnchantment, plugin: JavaPlugin) {
    val duplicate = findDuplicate(enchantment)

    if (duplicate != null) {
      throw IllegalArgumentException(underscoreEnchantsPlugin.globalLocale.alreadyRegistered
        .replace("<new>", enchantment.aliases.toString())
        .replace("<old", duplicate.aliases.toString())
        .replace("<plugin>", plugin.name))
    }

    enchantments[enchantment] = plugin
  }

  private fun registerIndicator(indicator: RegistrableActivationIndicator, plugin: JavaPlugin) {
    val duplicate = findDuplicate(indicator)

    if (duplicate != null) {
      throw IllegalArgumentException(underscoreEnchantsPlugin.globalLocale.alreadyRegistered
        .replace("<new>", indicator.aliases.toString())
        .replace("<old", duplicate.aliases.toString())
        .replace("<plugin>", plugin.name))
    }

    indicators[indicator] = plugin
  }

  private fun registerPlaceholder(placeholder: RegistrablePlaceholder, plugin: JavaPlugin) {
    val duplicate = findDuplicate(placeholder)

    if (duplicate != null) {
      throw IllegalArgumentException(underscoreEnchantsPlugin.globalLocale.alreadyRegistered
        .replace("<new>", placeholder.aliases.toString())
        .replace("<old", duplicate.aliases.toString())
        .replace("<plugin>", plugin.name))
    }

    placeholders[placeholder] = plugin
  }

  private fun registerSeeker(seeker: RegistrableEnchantmentSeeker, plugin: JavaPlugin) {
    val duplicate = findDuplicate(seeker)

    if (duplicate != null) {
      throw IllegalArgumentException(underscoreEnchantsPlugin.globalLocale.alreadyRegistered
        .replace("<new>", seeker.aliases.toString())
        .replace("<old", duplicate.aliases.toString())
        .replace("<plugin>", plugin.name))
    }

    seekers[seeker] = plugin
  }

  private fun registerTrigger(trigger: RegistrableTrigger, plugin: JavaPlugin) {
    val duplicate = findDuplicate(trigger)

    if (duplicate != null) {
      throw IllegalArgumentException(underscoreEnchantsPlugin.globalLocale.alreadyRegistered
        .replace("<new>", trigger.aliases.toString())
        .replace("<old", duplicate.aliases.toString())
        .replace("<plugin>", plugin.name))
    }

    triggers[trigger] = plugin
  }

  /**
   * Finds a duplicate of the given [registrable] in the registry.
   * @return The duplicate of the given [registrable], or null if there is none.
   * @throws IllegalArgumentException If the given [registrable] is not supported.
   */
  @Since("2.2")
  private fun findDuplicate(registrable: Registrable): Registrable? {
    return when (registrable) {
      is RegistrableAction -> actions.keys.firstOrNull { registrable.aliases.any { alias -> it.aliases.contains(alias) } }
      is RegistrableActivationIndicator -> indicators.keys.firstOrNull { registrable.aliases.any { alias -> it.aliases.contains(alias) } }
      is RegistrableApplicable -> applicables.keys.firstOrNull { registrable.aliases.any { alias -> it.aliases.contains(alias) } }
      is RegistrableCondition -> conditions.keys.firstOrNull { registrable.aliases.any { alias -> it.aliases.contains(alias) } }
      is RegistrableEnchantment -> enchantments.keys.firstOrNull { registrable.aliases.any { alias -> it.aliases.contains(alias) } }
      is RegistrableEnchantmentSeeker -> seekers.keys.firstOrNull { registrable.aliases.any { alias -> it.aliases.contains(alias) } }
      is RegistrablePlaceholder -> placeholders.keys.firstOrNull { registrable.aliases.any { alias -> it.aliases.contains(alias) } }
      is RegistrableTrigger -> triggers.keys.firstOrNull { registrable.aliases.any { alias -> it.aliases.contains(alias) } }
      else -> throw IllegalArgumentException("Registrable $registrable is not supported")
    }
  }

  /**
   * Replaces all placeholders in a string of [text] string and parses the mathematical expressions.
   * The [event] & [trigger] are needed for [RegistrablePlaceholder.replacedText].
   */
  @Since("2.2")
  @Beta
  fun replacePlaceholders(text: String, event: Event, trigger: RegistrableTrigger, actionTarget: TargetType): String {
    val player = trigger.getTriggerDataHolder().dataRetrievalMethods[actionTarget.mapToDrt()]?.invoke(event) as? Player

    var cookedText = Constants.PLACEHOLDER_REGEX.replace(text) { match ->
      // Placeholders have this syntax:
      // <target=target:placeholder=placeholder:arguments={argument_1_name=argument1|argument_2_name=argument2|...}>
      // If only placeholder is specified, it can also take this form:
      // <placeholder>
      // Example:
      // <target=first_player:placeholder=attribute:arguments={type=GENERIC_MOVEMENT_SPEED}>
      // <placeholder=attribute:arguments={type=GENERIC_MOVEMENT_SPEED}:target=first_player>
      // Target is optional, if not specified, it will be the action target.
      // Arguments are optional, if not specified, it will be an empty list.
      // The following parsing comments are for the first example.

      val arguments = mutableMapOf<String, String>()

      val matchResult = match.value.replaceMany("", "<", ">") // target=first_player:placeholder=attribute:arguments={type=GENERIC_MOVEMENT_SPEED}
      val matchSplit = matchResult.split(":") // ["target=first_player", "placeholder=attribute", "arguments={type=GENERIC_MOVEMENT_SPEED}"]

      val target = safeValueOr(matchSplit.firstOrNull { it.startsWith("target=") }?.replace("target=", "")?.uppercase(), actionTarget)!! // first_player
      val placeholderName = matchSplit.firstOrNull { it.startsWith("placeholder=") }?.replace("placeholder=", "") ?: matchResult // attribute
      val argumentsString = matchSplit
        .firstOrNull { it.startsWith("arguments={") || it.startsWith("args={") } // arguments={type=GENERIC_MOVEMENT_SPEED}
        ?.replaceMany("", "arguments={", "args={") // type=GENERIC_MOVEMENT_SPEED}
        ?.dropLast(1) ?: "" // type=GENERIC_MOVEMENT_SPEED

      val placeholder = findPlaceholder(placeholderName) ?: return@replace text // AttributePlaceholder

      if (argumentsString.isNotEmpty()) argumentsString.split("|").forEach { argumentString ->
        val argumentSplit = argumentString.split("=") // ["type", "GENERIC_MOVEMENT_SPEED"]
        val key = argumentSplit.getOrNull(0)
        val value = argumentSplit.getOrNull(1)
        if (key != null && value != null) arguments[key] = value // ["type" = "GENERIC_MOVEMENT_SPEED"]
      }

      return@replace placeholder.replacedText(event, trigger, target, arguments)/*?.let { replacePlaceholders(it, event, trigger, actionTarget, true) }*/ ?: match.value
    }

    if (player != null) cookedText = PlaceholderAPI.setPlaceholders(player, cookedText)

    return Expression(cookedText).calculate().let { result ->
      if (result.isNaN()) return@let cookedText
      else return@let result.let { double ->
        (if (double % 1 == 0.0) double.toInt() else double).toString()
      }
    }
  }

  /**
   * Unregisters an enchantment by its [name].
   */
  @Since("2.2")
  fun unregisterEnchantment(name: String) {
    val enchantment = findEnchantment(name) as? UnderscoreEnchantment ?: return
    HandlerList.unregisterAll(enchantment)
    enchantments.remove(enchantment)
  }

  /**
   * Unregisters an enchantment by its [key].
   */
  @Since("2.2")
  fun unregisterEnchantmentByKey(key: NamespacedKey) {
    val enchantment = findEnchantmentByKey(key) as? UnderscoreEnchantment ?: return
    HandlerList.unregisterAll(enchantment)
    enchantments.remove(enchantment)
  }

  /**
   * Iterates through a package at [path] and creates a provider on behalf of [plugin] that provides
   * all registrables that extend [T] in this package.
   */
  @Since("2.2")
  inline fun <reified T : Registrable> iterateAndProvide(path: String, plugin: UnderscoreEnchantsPlugin): RegistrablesProvider {
    val registrables = mutableListOf<Registrable>()

    Reflections(ConfigurationBuilder()
      .filterInputsBy(FilterBuilder().includePackage(path))
      .setUrls(ClasspathHelper.forPackage(path))
      .setScanners(Scanners.SubTypes)
    ).getSubTypesOf(T::class.java)
      .forEach {
        // The registrables bundled with UnderscoreEnchants only have 2 possible
        // kinds of constructors: one takes no argument, the other takes the plugin
        // instance as an argument. The prevalent amount of registrables use an
        // empty constructor. This code attempts to create an instance of the
        // class with an empty constructor, and if it fails, it is assumed
        // that the constructor must contain the plugin.
        try {
          registrables.add(it.getDeclaredConstructor().newInstance())
        } catch (ex: Exception) {
          registrables.add(it.getDeclaredConstructor(UnderscoreEnchantsPlugin::class.java).newInstance(plugin))
        }
      }

    return object: RegistrablesProvider {
      override fun getAssociatedPlugin() = plugin
      override fun getProvidedRegistrables() = registrables
    }
  }

  /**
   * Attempts to rediscover an [EnchantmentLevel] with this registry.
   */
  private fun rediscoverLevel(level: EnchantmentLevel): EnchantmentLevel {
    if (!level.isUndiscovered()) return level
    return level.copy(actions = level.actions.map { rediscoverAction(it) }, conditions = level.conditions.map { rediscoverCondition(it) })
  }

  /**
   * Attempts to rediscover an [EnchantmentAction] with this registry.
   */
  private fun rediscoverAction(action: EnchantmentAction): EnchantmentAction {
    if (!action.action.isUndiscovered()) return action

    val name = action.action.aliases.first()
    return action.copy(action = findAction(name) ?: UndiscoveredAction(name))
  }

  /**
   * Attempts to rediscover an [EnchantmentCondition] with this registry.
   */
  private fun rediscoverCondition(condition: EnchantmentCondition): EnchantmentCondition {
    if (!condition.condition.isUndiscovered()) return condition

    val name = condition.condition.aliases.first()
    return condition.copy(condition = findCondition(name) ?: UndiscoveredCondition(name))
  }

  companion object {
    @JvmName("registrableIsUndiscovered")
    internal fun Registrable.isUndiscovered() = this::class.java.getAnnotation(Undiscovered::class.java) != null

    @JvmName("levelIsUndiscovered")
    internal fun EnchantmentLevel.isUndiscovered() = actions.any { it.action.isUndiscovered() } || conditions.any { it.condition.isUndiscovered() }
  }
}
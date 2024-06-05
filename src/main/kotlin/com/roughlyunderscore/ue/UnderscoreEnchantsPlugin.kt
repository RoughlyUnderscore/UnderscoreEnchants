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

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.Strictness
import com.jeff_media.armorequipevent.ArmorEquipEvent
import com.jeff_media.updatechecker.UpdateCheckSource
import com.jeff_media.updatechecker.UpdateChecker
import com.roughlyunderscore.UEAPI
import com.roughlyunderscore.annotations.Since
import com.roughlyunderscore.data.EnchantmentPackMetadata
import com.roughlyunderscore.data.UELocale
import com.roughlyunderscore.json.LocaleDeserializer
import com.roughlyunderscore.registry.*
import com.roughlyunderscore.ue.commands.CommandHandler
import com.roughlyunderscore.ue.commands.UnderscoreEnchantsCommand
import com.roughlyunderscore.ue.config.UEConfiguration
import com.roughlyunderscore.ue.config.ConnectionData
import com.roughlyunderscore.ue.data.Constants
import com.roughlyunderscore.ue.data.PlayerData
import com.roughlyunderscore.ue.hook.api.UEEnchanterImpl
import com.roughlyunderscore.ue.hook.api.UELoaderImpl
import com.roughlyunderscore.ue.hook.api.UEPlayerManagerImpl
import com.roughlyunderscore.ue.hook.economy.EconomyHandler
import com.roughlyunderscore.ue.hook.economy.TreasuryEconomyHandler
import com.roughlyunderscore.ue.hook.economy.VacantEconomyHandler
import com.roughlyunderscore.ue.hook.economy.VaultEconomyHandler
import com.roughlyunderscore.ue.http.BackendAPI
import com.roughlyunderscore.ue.http.BackendAPIRepository
import com.roughlyunderscore.ue.json.*
import com.roughlyunderscore.ue.json.config.ConfigurationDeserializer
import com.roughlyunderscore.ue.listeners.CustomEventsListener
import com.roughlyunderscore.ue.listeners.LootPopulationListener
import com.roughlyunderscore.ue.listeners.PlayerDataLoadingListener
import com.roughlyunderscore.ue.registry.RegistryImpl
import com.roughlyunderscore.ue.storage.DataStorage
import com.roughlyunderscore.ue.storage.StorageMedium
import com.roughlyunderscore.ue.storage.database.MongoDBStorage
import com.roughlyunderscore.ue.storage.database.MySQLStorage
import com.roughlyunderscore.ue.storage.file.JsonStorage
import com.roughlyunderscore.ue.storage.file.YamlStorage
import com.roughlyunderscore.ue.tasks.DataSavingTask
import com.roughlyunderscore.ue.ui.vanilla.EnchantingIntercept
import com.roughlyunderscore.ue.ui.vanilla.AnvilIntercept
import com.roughlyunderscore.ue.ui.vanilla.GrindstoneIntercept
import com.roughlyunderscore.ue.utils.*
import com.roughlyunderscore.ulib.data.Time
import com.roughlyunderscore.ulib.io.collectNames
import com.roughlyunderscore.ulib.io.requireChildDirectory
import com.roughlyunderscore.ulib.io.requireFileAsResource
import com.roughlyunderscore.ulib.log.TogglableLogger
import com.roughlyunderscore.ulib.math.NumberUtils.generateIntList
import com.roughlyunderscore.ulib.text.formatColor
import com.roughlyunderscore.ulib.text.normalize
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import org.bstats.bukkit.Metrics
import org.bstats.charts.SimplePie
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.bukkit.event.HandlerList
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin
import org.mariuszgromada.math.mxparser.License
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.io.File

/**
 * This is the main class of the plugin. It serves the purpose of:
 * - initializing the plugin
 * - implementing the API
 */
@Since("2.2")
class UnderscoreEnchantsPlugin : JavaPlugin(), UEAPI {
  // Configurable items
  lateinit var configuration: UEConfiguration
  lateinit var storage: DataStorage
  private lateinit var connectionData: ConnectionData

  // Metrics
  private lateinit var metrics: Metrics
  private lateinit var updater: UpdateChecker

  // Registry
  private lateinit var registryImpl: RegistryImpl

  // Hooks
  lateinit var economyHandler: EconomyHandler

  // Locale
  val locales = mutableListOf<UELocale>()
  lateinit var globalLocale: UELocale

  // Tasks
  private lateinit var dataSavingTask: DataSavingTask

  // Utils
  lateinit var gson: Gson

  // UEAPI
  private lateinit var ueEnchanterImpl: UEEnchanterImpl
  private lateinit var ueLoaderImpl: UELoaderImpl
  private lateinit var ueManagerImpl: UEPlayerManagerImpl

  // Networking
  lateinit var repository: BackendAPIRepository

  // Miscellaneous
  private var minecraftVersion = 0

  private val requiredFolders = listOf("messages")
  private val requiredResources = listOf("config.json", "connection.json")

  var ueLogger = TogglableLogger(logger, true)

  override fun onEnable() {
    dataFolder.mkdirs()
    requiredFolders.forEach { dataFolder.requireChildDirectory(it) }
    requiredResources.forEach {
      val (name, path) = it.split("/").let { split ->
        return@let split.last() to split.dropLast(1).let { prepath -> prepath.ifEmpty { null } }
      }

      var fullPath = dataFolder
      path?.forEach { element -> fullPath = fullPath.resolve(element) }

      fullPath.resolve(name).requireFileAsResource(path?.joinToString(File.separator), this)
    }

    init()
  }

  override fun onDisable() {
    dataSavingTask.runThenCancel()
  }

  /**
   * Reloads the plugin.
   * @return The time it took to reload the plugin.
   */
  fun reloadPlugin(): Time {
    val start = Time.NOW

    locales.clear()

    registryImpl.enchantments.clear()
    registryImpl.packs.clear()

    HandlerList.unregisterAll(this)

    dataSavingTask.runThenClearCancel()

    loadConfiguration()
    loadMessages()

    ensureVersion(16)

    registryImpl.refresh()

    initConnectionData()
    initDataStorage(configuration.settings.storageMedium)
    initMetrics()
    initUpdateChecker()
    initTasks()
    initListeners()
    initCommands()
    initEconomy()
    initEnchantments()
    initReadme()
    initRetrofit()

    storage.requestFullLoad(this)

    return Time.NOW - start
  }


  // Initializers begin
  /**
   * This method runs essential code to initialize the plugin.
   * This includes (not an exhaustive list):
   * - a version check to make sure the server is running on the correct version of Minecraft
   * - bStats initialization
   * - localization loading
   * - registry preparation
   */
  @Since("2.2")
  private fun init() {
    logger.info("Initializing UnderscoreEnchants v${description.version}...")
    val startTime = System.currentTimeMillis()

    Class.forName("com.mysql.jdbc.Driver")
    License.iConfirmNonCommercialUse("roughly.underscore@gmail.com")

    trackTime("verifying the version", ueLogger, "Started <action>.", "Finished <action>. Took <ms>ms.") { ensureVersion() }
    trackTime("creating the registry", ueLogger, "Started <action>.", "Finished <action>. Took <ms>ms.") { initRegistry() }
    trackTime("creating Gson", ueLogger, "Started <action>.", "Finished <action>. Took <ms>ms.") { initGson() }
    trackTime("loading the configuration", ueLogger, "Started <action>.", "Finished <action>. Took <ms>ms.") { loadConfiguration() }
    trackTime("loading the messages", ueLogger, "Started <action>.", "Finished <action>. Took <ms>ms.") { loadMessages() }

    val started = globalLocale.started
    val finished = globalLocale.finished

    trackTime(globalLocale.populatingRegistry, ueLogger, started, finished) { provideRegistry() }
    trackTime(globalLocale.initializingConnection, ueLogger, started, finished) { initConnectionData() }
    trackTime(globalLocale.initializingData, ueLogger, started, finished) { initDataStorage(configuration.settings.storageMedium) }
    trackTime(globalLocale.preparingMetrics, ueLogger, started, finished) { initMetrics() }
    trackTime(globalLocale.checkingUpdates, ueLogger, started, finished) { initUpdateChecker() }
    trackTime(globalLocale.creatingTasks, ueLogger, started, finished) { initTasks() }
    trackTime(globalLocale.registeringListeners, ueLogger, started, finished) { initListeners() }
    trackTime(globalLocale.initializingCommands, ueLogger, started, finished) { initCommands() }
    trackTime(globalLocale.initializingEconomy, ueLogger, started, finished) { initEconomy() }
    trackTime(globalLocale.registeringEnchantments, ueLogger, started, finished) { initEnchantments() }
    trackTime(globalLocale.addingReadme, ueLogger, started, finished) { initReadme() }
    trackTime(globalLocale.implementingApi, ueLogger, started, finished) { initApiImpl() }
    trackTime(globalLocale.startingRetrofit, ueLogger, started, finished) { initRetrofit() }

    logger.info(globalLocale.initialized
      .replace("<plugin>", "UnderscoreEnchants v${description.version}")
      .replace("<ms>", (System.currentTimeMillis() - startTime).toString())
    )
    logger.info(globalLocale.reportBugsHere)
    logger.info(globalLocale.reviewRequest.replace("<link>", "https://spigotmc.org/resources/${Constants.SPIGOT_ID}"))
    logger.info(globalLocale.thanksForUsingUe)
    logger.info("Roughly_, 2024. ♥")
  }

  /**
   * This method initializes the registry for further use.
   * @see UEAPIRegistry
   * @see RegistryImpl
   */
  @Since("2.2")
  private fun initRegistry() {
    registryImpl = RegistryImpl(this)
  }

  /**
   * This method registers the built-in triggers, placeholders, etc.
   */
  @Since("2.2")
  private fun provideRegistry() {
    registryImpl.provide(registryImpl.iterateAndProvide<RegistrableTrigger>("com.roughlyunderscore.ue.registry.triggers", this))
    registryImpl.provide(registryImpl.iterateAndProvide<RegistrablePlaceholder>("com.roughlyunderscore.ue.registry.placeholders", this))
    registryImpl.provide(registryImpl.iterateAndProvide<RegistrableApplicable>("com.roughlyunderscore.ue.registry.applicables", this))
    registryImpl.provide(registryImpl.iterateAndProvide<RegistrableActivationIndicator>("com.roughlyunderscore.ue.registry.indicators", this))
    registryImpl.provide(registryImpl.iterateAndProvide<RegistrableAction>("com.roughlyunderscore.ue.registry.actions", this))
    registryImpl.provide(registryImpl.iterateAndProvide<RegistrableCondition>("com.roughlyunderscore.ue.registry.conditions", this))
    registryImpl.provide(registryImpl.iterateAndProvide<RegistrableEnchantmentSeeker>("com.roughlyunderscore.ue.registry.seekers", this))
  }

  /**
   * This method initializes the Gson instance.
   */
  @Since("2.2")
  private fun initGson() {
    gson = GsonBuilder()
      .setPrettyPrinting()
      .setStrictness(Strictness.LENIENT)
      .registerTypeAdapter(UnderscoreEnchantment::class.java, EnchantmentDeserializer(this, registry))
      .registerTypeAdapter(PlayerData::class.java, PlayerDataSerializer)
      .registerTypeAdapter(PlayerData::class.java, PlayerDataDeserializer(this))
      .registerTypeAdapter(UELocale::class.java, LocaleDeserializer)
      .registerTypeAdapter(ConnectionData::class.java, ConnectionDataDeserializer)
      .registerTypeAdapter(UEConfiguration::class.java, ConfigurationDeserializer(this))
      .registerTypeAdapter(EnchantmentPackMetadata::class.java, PackMetadataDeserializer)
      .create()
  }

  /**
   * This method loads the configuration file from config.json.
   * The configuration loading is supported by SpongePowered's Configurate library.
   */
  @Since("2.2")
  private fun loadConfiguration() {
    dataFolder.resolve("config.json").reader().use { gson.fromJson(it, UEConfiguration::class.java) }?.let {
      configuration = it
    }

    ueLogger = TogglableLogger(this.logger, configuration.misc.debug)
  }

  /**
   * This method loads the messages file from messages.json.
   * The messages loading is supported by SpongePowered's Configurate library.
   * @see loadConfiguration
   */
  @Since("2.2")
  private fun loadMessages() {
    if (!::configuration.isInitialized) {
      this.shutdown("Invalid configuration. Could not load the messages. Will not proceed to load the plugin.")
      return
    }

    //dataFolder.requireChildDirectory("messages")

    val messagesFolder = dataFolder.resolve("messages")
    if (configuration.settings.includeDefaultLocales) {
      messagesFolder.resolve("en_US.json").requireFileAsResource("messages", this) and
        messagesFolder.resolve("ru_RU.json").requireFileAsResource("messages", this) and
        messagesFolder.resolve("tr_TR.json").requireFileAsResource("messages", this) and
        messagesFolder.resolve("ta_IN.json").requireFileAsResource("messages", this)
      //    messagesFolder.resolve("it_IT.json").requireFileAsResource("messages", this) and
      //    messagesFolder.resolve("es_ES.json").requireFileAsResource("messages", this) and
      //    messagesFolder.resolve("fr_FR.json").requireFileAsResource("messages", this) and
      //    messagesFolder.resolve("de_DE.json").requireFileAsResource("messages", this)
    }

    // After having made sure that the default locales exist, we need to go through all locales in the "messages"
    // folder and load the locale.

    for (file in messagesFolder.listFiles { _, name ->
      name.endsWith(".json")
    } ?: run {
      this.shutdown("No message files found.")
      return
    }) {
      locales.add(file.reader().use { gson.fromJson(it, UELocale::class.java) })
    }

    ueLogger.commit { info("Loaded ${locales.size} locales, all locale names: " +
      if (locales.size > 10) "(truncated)" else locales.map { it.localeIdentifier }.toString()
    ) }

    globalLocale = locales.getLocaleStrict(configuration.settings.language) ?: run {
      throw IllegalStateException("The default locale was not found.")
    }
  }

  /**
   * Loads the connection data from `connection.json`.
   */
  @Since("2.2")
  private fun initConnectionData() {
    dataFolder.resolve("connection.json").requireFileAsResource(null, this)

    connectionData = dataFolder.resolve("connection.json").reader().use { gson.fromJson(it, ConnectionData::class.java) }
  }

  /**
   * Initializes the data storage according to the specified [storageType].
   * @see StorageMedium
   */
  @Since("2.2")
  private fun initDataStorage(storageType: StorageMedium) {
    storage = when (storageType) {
      StorageMedium.JSON -> JsonStorage(this)
      StorageMedium.YAML -> YamlStorage(this)
      StorageMedium.MONGODB -> MongoDBStorage(connectionData, this)
      StorageMedium.MYSQL -> MySQLStorage(connectionData, this)
    }.apply {
      GlobalScope.launch { init() }
    }
  }

  /**
   * This method checks the version of Minecraft the server is running on.
   * If the version is less than the specified [minimum], the plugin will be disabled.
   */
  @Since("2.2")
  private fun ensureVersion(minimum: Int = 1206, minimumString: String = "1.20.6") {
    minecraftVersion = Bukkit.getBukkitVersion() // 1.19.3-R0.1-SNAPSHOT; 1.19-R0.1-SNAPSHOT
      .split("-")[0] // ["1.19.3", "R0.1", "SNAPSHOT"] -> "1.19.3"; ["1.19", "R0.1", "SNAPSHOT"] -> "1.19"
      .split(".") // ["1", "19", "3"]; ["1", "19"]
      .let {
        if (it.size == 2) "${it.joinToString("")}0".toInt() // ["1", "19"] -> "1190" -> 1190
        else it.joinToString("").toInt() // ["1", "19", "3"] -> "1193" -> 1193
      }

    if (minecraftVersion < minimum) {
      this.shutdown("&cYou are running an unsupported Minecraft version. Please update to &b$minimumString &cor higher.".formatColor())
    }
  }

  /**
   * This method initializes the metrics.
   * @see loadConfiguration
   */
  @Since("2.2")
  private fun initMetrics() {
    if (!configuration.misc.toRunMetrics) return

    metrics = Metrics(this, Constants.BSTATS_ID)
    metrics.addCustomChart(SimplePie("language") { configuration.settings.language })
    metrics.addCustomChart(SimplePie("enchantments_count") { registryImpl.enchantments.size.toString() })
  }

  /**
   * This method initializes the update checker.
   * @see loadConfiguration
   */
  @Since("2.2")
  private fun initUpdateChecker() {
    if (configuration.misc.updateFrequency <= 0) return

    updater = UpdateChecker(this, UpdateCheckSource.SPIGOT, Constants.SPIGOT_ID.toString())
      .setDownloadLink("https://www.spigotmc.org/resources/${Constants.SPIGOT_ID}/")
      .setDonationLink("https://donationalerts.com/r/zbll")
      .onFail { _, ex ->
        logger.warning(locales.getLocale(configuration.settings.language).badUpdate)
        logger.warning(ex.message)
      }
      .checkEveryXHours(configuration.misc.updateFrequency.toDouble())
      .setNotifyOpsOnJoin(configuration.misc.notifyOpsOnJoinAboutUpdates)
      .checkNow()
  }

  /**
   * This method initializes the tasks.
   */
  @Since("2.2")
  private fun initTasks() {
    dataSavingTask = DataSavingTask(this)

    dataSavingTask.runTaskTimer(this, configuration.settings.storageSavingPeriodTicks, configuration.settings.storageSavingPeriodTicks)
  }

  /**
   * This method initializes the listeners.
   */
  @Since("2.2")
  private fun initListeners() {
    ArmorEquipEvent.registerListener(this)

    Bukkit.getPluginManager().let { pm ->
      pm.registerEvents(CustomEventsListener(), this)
      pm.registerEvents(PlayerDataLoadingListener(this), this)
      pm.registerEvents(LootPopulationListener(this), this)

      pm.registerEvents(EnchantingIntercept(this), this)
      pm.registerEvents(AnvilIntercept(this), this)
      pm.registerEvents(GrindstoneIntercept(this), this)
    }
  }

  /**
   * See [RegistryImpl.initEnchantments]
    */
  @Since("2.2")
  fun initEnchantments() {
    registryImpl.initEnchantments()
    ueLogger.commit { info("Discovered ${registryImpl.enchantments.size} enchantments.") }
  }

  /**
   * This method initializes the commands by UnderscoreEnchants and other relevant parts,
   * such as the autocompletions and contexts.
   */
  @Since("2.2")
  private fun initCommands() {
    CommandHandler(this) {
      completion("general-completion") { listOf("debug", "enchant", "toggle", "download", "help", "load", "unload", "reload") }
      completion("debug-completion") { listOf("enchantment", "locales", "log", "registry") }
      completion("true-false") { listOf("true", "false") }
      completion("types-completion") { listOf("enchantment", "pack", "locale") }
      completion("enchantments-completion") { (registry.enchantments.map { it.key.key } + Registry.ENCHANTMENT.map { it.key }).map { it.key } }
      completion("custom-enchantments-completion") { registry.enchantments.map { it.key.key.key } }
      completion("locales-completion") { locales.map { it.localeIdentifier } }
      completion("content-files-completion") { dataFolder.resolve("enchantments").collectNames() + dataFolder.resolve("messages").collectNames() }

      completion("contextual-loaded-types-completion") { ctx ->
        val previousArgument = ctx.getContextValue(String::class.java).normalize().first()
        when (previousArgument) {
          'e' -> registryImpl.enchantments.map { it.key.key.key }
          'p' -> registryImpl.packs.keys.toList()
          'l' -> locales.map { it.localeIdentifier }
          else -> emptyList()
        }
      }

      completion("levels-completion") { ctx ->
        val previousArgument = ctx.getContextValue(String::class.java).trim()

        val underscoreEnchantment = registryImpl.findEnchantmentByKeyString(previousArgument)
        val vanillaEnchantment = try {
          Registry.ENCHANTMENT.get(NamespacedKey.minecraft(previousArgument))
        } catch (ex: IllegalArgumentException) {
          null
        }

        underscoreEnchantment?.levels?.size?.let { maxLevel -> generateIntList(1, maxLevel).map { it.toString() } } ?:
          vanillaEnchantment?.maxLevel?.let { maxLevel -> generateIntList(1, maxLevel).map { it.toString() } } ?: emptyList()
      }

      completion("debug-arguments-completion") { ctx ->
        val previousArgument = ctx.getContextValue(String::class.java).trim().normalize()

        when {
          previousArgument.startsWith("reg") -> listOf("actions", "applicables", "conditions", "indicators", "placeholders", "seekers", "triggers")
          previousArgument.startsWith("ench") -> registryImpl.enchantments.map { it.key.key.key }
          else -> emptyList()
        }
      }

      completion("players-completion") { Bukkit.getOnlinePlayers().map { it.name } }

      command(UnderscoreEnchantsCommand(this@UnderscoreEnchantsPlugin))
    }
  }

  /**
   * This method initializes the economy handler. Prioritizes Treasury.
   */
  @Since("2.2")
  private fun initEconomy() {
    economyHandler =
      if (server.pluginManager.isPluginEnabled("Treasury")) TreasuryEconomyHandler()
      else if (server.pluginManager.isPluginEnabled("Vault")) VaultEconomyHandler(this)
      else VacantEconomyHandler()
  }

  /**
   * Initializes the README.md file if it is missing.
   */
  @Since("2.2")
  private fun initReadme() {
    if (!configuration.settings.generateReadme) return
    dataFolder.resolve("README.md").requireFileAsResource(null, this)
  }

  /**
   * This method implements the API.
   */
  @Since("2.2")
  private fun initApiImpl() {
    this.ueEnchanterImpl = UEEnchanterImpl(this)
    this.ueLoaderImpl = UELoaderImpl(this)
    this.ueManagerImpl = UEPlayerManagerImpl(this)

    Bukkit.getServicesManager().register(UEAPI::class.java, this, this, ServicePriority.Normal)
  }

  /**
   * This method initializes the backend API using Retrofit
   * and initializes a repository.
   */
  @Since("2.2")
  private fun initRetrofit() {
    repository = BackendAPIRepository(Retrofit.Builder()
      .baseUrl(configuration.settings.repositoryUrl)
      .addConverterFactory(Json.asConverterFactory(MediaType.get("application/json; charset=UTF8")))
      .build()
      .create(BackendAPI::class.java), this)
  }

  // Initializers end


  // API impl begin

  override fun getLocale(localeIdentifier: String): UELocale? = locales.getLocaleStrict(localeIdentifier)
  override val serverLocale get() = globalLocale
  override val registry get() = registryImpl
  override val enchanter get() = ueEnchanterImpl
  override val loader get() = ueLoaderImpl
  override val playerManager get() = ueManagerImpl

  // API impl end
}

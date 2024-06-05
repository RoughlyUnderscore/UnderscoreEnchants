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

package com.roughlyunderscore.ue.json.config

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.roughlyunderscore.json.DeserializationNames
import com.roughlyunderscore.ue.config.*
import com.roughlyunderscore.ue.storage.StorageMedium
import com.roughlyunderscore.ulib.data.safeValueOr
import com.roughlyunderscore.ulib.json.*

abstract class ConfigSchema {
  abstract fun deserialize(configNode: JsonObject): UEConfiguration?
  abstract fun serialize(config: UEConfiguration): JsonElement

  object Schema22 : ConfigSchema() {
    override fun deserialize(configNode: JsonObject): UEConfiguration? {
      val settingsNode = configNode.anyObject(DeserializationNames.Configuration.SETTINGS) ?: JsonObject()
      val miscNode = configNode.anyObject(DeserializationNames.Configuration.MISC) ?: JsonObject()
      val generationNode = configNode.anyObject(DeserializationNames.Configuration.GENERATION) ?: JsonObject()
      val uiNode = configNode.anyObject(DeserializationNames.Configuration.UI) ?: JsonObject()
      val enchantmentsNode = configNode.anyObject(DeserializationNames.Configuration.ENCHANTMENTS) ?: JsonObject()

      return UEConfiguration(
        settings = Settings(
          generateReadme = settingsNode.anyBooleanStrict(DeserializationNames.Settings.GENERATE_README) { true },
          language = settingsNode.anyStringStrict(DeserializationNames.Settings.LOCALE) { "en_US" },
          playersCanChangeLocales = settingsNode.anyBooleanStrict(DeserializationNames.Settings.PLAYERS_CAN_CHANGE_LOCALES) { true },
          includeDefaultLocales = settingsNode.anyBooleanStrict(DeserializationNames.Settings.INCLUDE_DEFAULT_LOCALES) { true },
          storageMedium = safeValueOr(
            settingsNode.anyString(DeserializationNames.Settings.STORAGE_MEDIUM)?.uppercase(),
            StorageMedium.JSON
          )!!,
          storageSavingPeriodTicks = settingsNode.anyLongStrict(DeserializationNames.Settings.STORAGE_SAVING_PERIOD_TICKS) { 900 },
          notifyPlayersOfDataLoading = settingsNode.anyBooleanStrict(DeserializationNames.Settings.NOTIFY_PLAYERS_OF_DATA_LOADING) { true },
          repositoryUrl = settingsNode.anyStringStrict(DeserializationNames.Settings.REPOSITORY_URL) { "https://ue.runderscore.com/api/v1/" },
        ),
        misc = Misc(
          toRunMetrics = miscNode.anyBooleanStrict(DeserializationNames.Misc.TO_RUN_METRICS) { true },
          updateFrequency = miscNode.anyIntStrict(DeserializationNames.Misc.UPDATE_FREQUENCY) { 24 },
          notifyOpsOnJoinAboutUpdates = miscNode.anyBooleanStrict(DeserializationNames.Misc.NOTIFY_OPS_ON_JOIN_ABOUT_UPDATES) { true },
          debug = miscNode.anyBooleanStrict(DeserializationNames.Misc.DEBUG) { true }
        ),
        generation = Generation(
          populateChestsChance = generationNode.anyDoubleStrict(DeserializationNames.Generation.POPULATE_CHESTS_CHANCE) { 1.0 / 7.0 },
          populateFishingChance = generationNode.anyDoubleStrict(DeserializationNames.Generation.POPULATE_FISHING_CHANCE) { 1.0 / 7.0 },
          populateVillagersChance = generationNode.anyDoubleStrict(DeserializationNames.Generation.POPULATE_VILLAGERS_CHANCE) { 1.0 / 7.0 },
          populateEnchantingChance = generationNode.anyDoubleStrict(DeserializationNames.Generation.POPULATE_ENCHANTING_CHANCE) { 1.0 / 7.0 }
        ),
        ui = Ui(
          truncatePackDataAfterXEnchantments = uiNode.anyIntStrict(DeserializationNames.UI.TRUNCATE_PACK_DATA_AFTER_X_ENCHANTS) { 1 }
        ),
        enchantments = Enchantments(
          limit = enchantmentsNode.anyIntStrict(DeserializationNames.Enchantments.LIMIT) { -1 },
        )
      )
    }

    override fun serialize(config: UEConfiguration): JsonElement {
      val result = JsonObject()
      val configNode = JsonObject()

      val settings = JsonObject().apply {
        val settings = config.settings

        addProperty(DeserializationNames.Settings.GENERATE_README.first(), settings.generateReadme)
        addProperty(DeserializationNames.Settings.LOCALE.first(), settings.language)
        addProperty(DeserializationNames.Settings.PLAYERS_CAN_CHANGE_LOCALES.first(), settings.playersCanChangeLocales)
        addProperty(DeserializationNames.Settings.INCLUDE_DEFAULT_LOCALES.first(), settings.includeDefaultLocales)
        addProperty(DeserializationNames.Settings.STORAGE_MEDIUM.first(), settings.storageMedium.name.lowercase())
        addProperty(DeserializationNames.Settings.STORAGE_SAVING_PERIOD_TICKS.first(), settings.storageSavingPeriodTicks)
        addProperty(DeserializationNames.Settings.NOTIFY_PLAYERS_OF_DATA_LOADING.first(), settings.notifyPlayersOfDataLoading)
        addProperty(DeserializationNames.Settings.REPOSITORY_URL.first(), settings.repositoryUrl)
      }

      val misc = JsonObject().apply {
        val misc = config.misc

        addProperty(DeserializationNames.Misc.TO_RUN_METRICS.first(), misc.toRunMetrics)
        addProperty(DeserializationNames.Misc.UPDATE_FREQUENCY.first(), misc.updateFrequency)
        addProperty(DeserializationNames.Misc.NOTIFY_OPS_ON_JOIN_ABOUT_UPDATES.first(), misc.notifyOpsOnJoinAboutUpdates)
        addProperty(DeserializationNames.Misc.DEBUG.first(), misc.debug)
      }

      val generation = JsonObject().apply {
        val gen = config.generation

        addProperty(DeserializationNames.Generation.POPULATE_CHESTS_CHANCE.first(), gen.populateChestsChance)
        addProperty(DeserializationNames.Generation.POPULATE_FISHING_CHANCE.first(), gen.populateFishingChance)
        addProperty(DeserializationNames.Generation.POPULATE_VILLAGERS_CHANCE.first(), gen.populateVillagersChance)
        addProperty(DeserializationNames.Generation.POPULATE_ENCHANTING_CHANCE.first(), gen.populateEnchantingChance)
      }

      val ui = JsonObject().apply {
        val ui = config.ui

        addProperty(DeserializationNames.UI.TRUNCATE_PACK_DATA_AFTER_X_ENCHANTS.first(), ui.truncatePackDataAfterXEnchantments)
      }

      val enchantments = JsonObject().apply {
        val enchants = config.enchantments

        addProperty(DeserializationNames.Enchantments.LIMIT.first(), enchants.limit)
      }

      configNode.add(DeserializationNames.Configuration.SETTINGS.first(), settings)
      configNode.add(DeserializationNames.Configuration.MISC.first(), misc)
      configNode.add(DeserializationNames.Configuration.GENERATION.first(), generation)
      configNode.add(DeserializationNames.Configuration.UI.first(), ui)
      configNode.add(DeserializationNames.Configuration.ENCHANTMENTS.first(), enchantments)

      result.addProperty(DeserializationNames.Configuration.VERSION.first(), 22)
      result.add("configuration", configNode)

      return result
    }
  }
}
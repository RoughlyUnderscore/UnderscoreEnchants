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

package com.roughlyunderscore.ue.config

import com.roughlyunderscore.annotations.Since
import com.roughlyunderscore.ue.storage.StorageMedium

/**
 * This class serves the purpose of storing all configuration options used by the plugin.
 * They are to be loaded from the configuration file.
 */
@Since("2.2")
data class UEConfiguration(
  val settings: Settings, val misc: Misc, val generation: Generation, val ui: Ui, val enchantments: Enchantments
)

/**
 * The settings section of the configuration file.
 */
@Since("2.2")
data class Settings(
  /**
   * Whether to generate a readme file in the plugin folder.
   */
  @Since("2.2")
  val generateReadme: Boolean,

  /**
   * The language to use for messages.
   */
  @Since("2.2")
  val language: String,

  /**
   * Whether to allow players to change their locale.
   */
  @Since("2.2")
  val playersCanChangeLocales: Boolean,

  /**
   * Whether to include default locales (en_US, de_DE, etc.) if they were
   * not found in the plugin folder.
   */
  @Since("2.2")
  val includeDefaultLocales: Boolean,

  /**
   * Which storage medium to use for data storage.
   */
  @Since("2.2")
  val storageMedium: StorageMedium,

  /**
   * How often to save data to the storage medium.
   */
  @Since("2.2")
  val storageSavingPeriodTicks: Long,

  /**
   * Whether to notify players about the data loading process.
   */
  @Since("2.2")
  val notifyPlayersOfDataLoading: Boolean,

  /**
   * The URL to the plugin's API (enchantment & pack storage).
   */
  @Since("2.2")
  val repositoryUrl: String,
)

/**
 * The misc section of the configuration file.
 */
@Since("2.2")
data class Misc(
  /**
   * Whether to enable bStats metrics.
   */
  @Since("2.2")
  val toRunMetrics: Boolean,

  /**
   * How often to check for updates (in hours). 0 to disable.
   */
  @Since("2.2")
  val updateFrequency: Int,

  /**
   * Whether to notify ops on join about updates.
   */
  @Since("2.2")
  val notifyOpsOnJoinAboutUpdates: Boolean,

  /**
   * Whether to log extra information into the console.
   */
  @Since("2.2")
  val debug: Boolean,
)

/**
 * The generation section of the configuration file.
 */
data class Generation(
  /**
   * The chance for a villager item trade to receive an enchantment.
   *
   * Even when set to 0, all trades are listened to anyway,
   * because they might receive enchantments via the vanilla means, which
   * need to be replaced with the UnderscoreEnchantments means.
   */
  @Since("2.2")
  val populateVillagersChance: Double,

  /**
   * The chance for a chest loot to receive an enchantment.
   *
   * Even when set to 0, all loots are listened to anyway,
   * because they might receive enchantments via the vanilla means, which
   * need to be replaced with the UnderscoreEnchantments means.
   */
  @Since("2.2")
  val populateChestsChance: Double,

  /**
   * The chance for a fishing loot to receive an enchantment.
   *
   * Even when set to 0, all fishing is listened to anyway,
   * because they might receive enchantments via the vanilla means, which
   * need to be replaced with the UnderscoreEnchantments means.
   */
  @Since("2.2")
  val populateFishingChance: Double,

  /**
   * The chance for adding a random custom enchantment when using the enchantment table.
   *
   * Even when set to 0, all fishing is listened to anyway,
   * because they might receive enchantments via the vanilla means, which
   * need to be replaced with the UnderscoreEnchantments means.
   */
  @Since("2.2")
  val populateEnchantingChance: Double,
)

/**
 * The UI section of the configuration file.
 */
data class Ui(
  /**
   * The amount of enchantment names to show, after which
   * the rest will be truncated into a "...and N more enchantments" string.
   */
  @Since("2.2")
  val truncatePackDataAfterXEnchantments: Int,
)

/**
 * The enchantments section of the configuration file.
 */
data class Enchantments(
  /**
   * The limit for the amount of enchantments on an item.
   * When set to -1 or lower, there is no limit.
   */
  @Since("2.2")
  val limit: Int,
)


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

package com.roughlyunderscore.ue.storage

import com.roughlyunderscore.ue.UnderscoreEnchantsPlugin
import com.roughlyunderscore.annotations.Since
import com.roughlyunderscore.ue.data.Cooldown
import com.roughlyunderscore.ue.data.PlayerData
import com.roughlyunderscore.ue.data.PlayerDataModification
import com.roughlyunderscore.ue.data.PlayerDataModificationType
import com.roughlyunderscore.data.UELocale
import com.roughlyunderscore.ue.utils.getLocale
import com.roughlyunderscore.ue.utils.getLocaleStrict
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import java.util.UUID

/**
 * A [DataStorage] that loads and saves data from a storage medium.
 */
@Since("2.2")
interface DataStorage {
  /**
   * The data that is currently loaded.
   */
  val data: MutableMap<UUID, PlayerData>

  /**
   * Prepares the storage medium for use, e.g., creating tables.
   */
  @Since("2.2")
  suspend fun init()

  /**
   * Saves [playerData] to the storage medium,
   * including all the pending modifications.
   */
  @Since("2.2")
  suspend fun savePlayerData(playerData: PlayerData)

  /**
   * Saves player data of [uuid] to the storage medium,
   * including all the pending modifications.
   * If any IO or database calls are being done in the implementation,
   * they should be executed asynchronously.
   */
  @Since("2.2")
  suspend fun saveDataOf(uuid: UUID)

  /**
   * Loads player data from the storage medium, given a [uuid]. If this data
   * is already loaded, it will be returned. If none exists, it will be created.
   * @return The loaded data.
   */
  @Since("2.2")
  suspend fun loadPlayerData(uuid: UUID): PlayerData

  /**
   * Gets already loaded player data.
   * @return The loaded data, or null if none is loaded for the player.
   */
  @Since("2.2")
  fun getPlayerData(uuid: UUID): PlayerData?

  /**
   * Immediately saves all data to the storage medium, uploads all
   * modifications and clears all cached data. Then loads player
   * data for all online players.
   */
  @Since("2.2")
  suspend fun refresh()

  /**
   * Runs [fullLoad] in a coroutine without requiring asynchronous context.
   */
  @Since("2.2")
  fun requestFullLoad(plugin: UnderscoreEnchantsPlugin) = GlobalScope.launch { fullLoad() }

  /**
   * Loads all player data for all online players.
   */
  @Since("2.2")
  suspend fun fullLoad() {
    for (player in Bukkit.getOnlinePlayers()) loadPlayerData(player.uniqueId)
  }

  /**
   * Saves all data to the storage medium.
   */
  @Since("2.2")
  suspend fun saveAll()

  /**
   * Pends a [modification] to the player data with [uuid]. If there are
   * any other modifications of this type currently pending, they will be
   * discarded.
   */
  @Since("2.2")
  fun pendModification(uuid: UUID, modification: PlayerDataModification)

  /**
   * Uploads all pending modifications to the storage medium for the player
   * with [uuid].
   */
  @Since("2.2")
  suspend fun uploadModifications(uuid: UUID)

  /**
   * Uploads all pending modifications to the storage medium.
   */
  @Since("2.2")
  suspend fun uploadModifications()

  /**
   * Checks if an [enchantmentKey] is disabled for a player. Only looks at the loaded data.
   * @return whether the enchantment is disabled; false if data is not loaded.
   */
  @Since("2.2")
  fun isEnchantmentDisabled(uuid: UUID, enchantmentKey: NamespacedKey): Boolean {
    val playerData = data[uuid] ?: return false
    return playerData.disabled?.contains(enchantmentKey) ?: false
  }

  /**
   * Toggles the [enchantmentKey] state for a player.
   * @return whether the enchantment is enabled now; true if data is not loaded.
   */
  @Since("2.2")
  fun toggleEnchantment(uuid: UUID, enchantmentKey: NamespacedKey): Boolean {
    val playerData = getPlayerData(uuid) ?: return true

    val disabledList = playerData.disabled?.toMutableList() ?: mutableListOf()
    val currentlyDisabled = disabledList.contains(enchantmentKey)

    if (currentlyDisabled) disabledList.remove(enchantmentKey)
    else disabledList.add(enchantmentKey)

    playerData.disabled = disabledList
    data.replace(uuid, playerData)

    pendModification(uuid, PlayerDataModification(PlayerDataModificationType.DISABLED, disabledList))

    return !disabledList.contains(enchantmentKey)
  }

  /**
   * Sets the player's locale to a new one.
   */
  @Since("2.2")
  fun setLocale(uuid: UUID, localeIdentifier: String?, plugin: UnderscoreEnchantsPlugin): UELocale? {
    val playerData = getPlayerData(uuid) ?: return getLocaleStrict(uuid, plugin)

    playerData.locale = localeIdentifier
    pendModification(uuid, PlayerDataModification(PlayerDataModificationType.LOCALE, localeIdentifier))
    data.replace(uuid, playerData)

    val locale = getLocaleStrict(uuid, plugin)
    return locale
  }

  /**
   * Gets the player's locale.
   * @return the player's locale; falls back to the server-wide locale if something goes wrong.
   */
  @Since("2.2")
  fun getLocale(uuid: UUID, plugin: UnderscoreEnchantsPlugin): UELocale =
    getPlayerData(uuid)?.locale?.let { plugin.locales.getLocaleStrict(it) } ?: plugin.locales.getLocale(plugin.configuration.settings.language)

  /**
   * Gets the player's locale.
   * @return the player's locale, or null if something goes wrong.
   */
  @Since("2.2")
  fun getLocaleStrict(uuid: UUID, plugin: UnderscoreEnchantsPlugin): UELocale? =
    getPlayerData(uuid)?.locale?.let { plugin.locales.getLocaleStrict(it) }

  /**
   * Checks if a player has a locale for an [enchantmentKey]. If the locale is expired, removes it and returns -1.
   * @return the remaining cooldown in ticks, or -1 if something goes wrong/the player doesn't have one
   */
  @Since("2.2")
  fun hasCooldown(uuid: UUID, enchantmentKey: NamespacedKey): Long {
    val playerData = getPlayerData(uuid) ?: return -1


    val cooldown = playerData.cooldowns?.firstOrNull { it.enchantmentKey == enchantmentKey } ?: return -1

    if (cooldown.isExpired()) {
      removeCooldown(uuid, enchantmentKey)
      return -1L
    }

    return (cooldown.endsAt - System.currentTimeMillis()) / 50
  }

  /**
   * Adds a cooldown for an [enchantmentKey] that [endsAt] a certain timestamp.
   */
  @Since("2.2")
  fun addCooldown(uuid: UUID, enchantmentKey: NamespacedKey, endsAt: Long) {
    val playerData = getPlayerData(uuid) ?: return
    val cooldown = Cooldown(uuid, enchantmentKey, endsAt)

    if (playerData.cooldowns == null) playerData.cooldowns = mutableListOf(cooldown)
    else playerData.cooldowns!!.add(Cooldown(uuid, enchantmentKey, endsAt))
    data.replace(uuid, playerData)

    pendModification(uuid, PlayerDataModification(PlayerDataModificationType.COOLDOWNS, playerData.cooldowns))
  }

  /**
   * Removes a cooldown for an [enchantmentKey].
   */
  @Since("2.2")
  fun removeCooldown(uuid: UUID, enchantmentKey: NamespacedKey) {
    val playerData = getPlayerData(uuid) ?: return

    playerData.cooldowns?.removeIf { it.enchantmentKey == enchantmentKey }
    data.replace(uuid, playerData)
    pendModification(uuid, PlayerDataModification(PlayerDataModificationType.COOLDOWNS, playerData.cooldowns))
  }
}
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

package com.roughlyunderscore.ue.storage.database

import com.roughlyunderscore.annotations.Since
import com.roughlyunderscore.annotations.Stable
import com.roughlyunderscore.ue.UnderscoreEnchantsPlugin
import com.roughlyunderscore.ue.config.ConnectionData
import com.roughlyunderscore.ue.data.Cooldown
import com.roughlyunderscore.ue.data.PlayerData
import com.roughlyunderscore.ue.data.PlayerDataModification
import com.roughlyunderscore.ue.data.PlayerDataModificationType
import com.roughlyunderscore.ue.storage.DataStorage
import com.roughlyunderscore.ue.utils.serializableString
import com.roughlyunderscore.ue.utils.toCooldowns
import com.roughlyunderscore.ue.utils.toDisabledEnchantments
import kotlinx.coroutines.coroutineScope
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import java.util.*

/**
 * This class represents a MySQL data storage medium.
 */
@Since("2.2")
@Stable
class MySQLStorage(
  url: String, port: Int = 3306, username: String, password: String, database: String, private val plugin: UnderscoreEnchantsPlugin
) : JDBCTypeDatabase(
  url, "mysql", port, username, password, database
), DataStorage {
  constructor(connectionData: ConnectionData, plugin: UnderscoreEnchantsPlugin): this(
    url = connectionData.jdbc.url,
    port = connectionData.jdbc.port,
    username = connectionData.jdbc.username,
    password = connectionData.jdbc.password,
    database = connectionData.jdbc.database,
    plugin = plugin
  )

  override val data: MutableMap<UUID, PlayerData> = mutableMapOf()

  override suspend fun init() {
    update(Queries.CREATE_TABLE)
  }

  override suspend fun savePlayerData(playerData: PlayerData) {
    uploadModifications(playerData.uuid)
    if (playerData.isLoadedFromMedium) return

    val uuid = playerData.uuid.toString()
    val locale = playerData.locale
    val disabledEnchants = playerData.disabled.serializableString()
    val cooldowns = playerData.cooldowns.serializableString()

    update(Queries.ADD_DATA, uuid, locale, disabledEnchants, cooldowns)

    playerData.isLoadedFromMedium = true
    data.replace(playerData.uuid, playerData)
  }

  override suspend fun saveDataOf(uuid: UUID) {
    data[uuid]?.let { savePlayerData(it) }
  }

  override suspend fun loadPlayerData(uuid: UUID): PlayerData = coroutineScope {
    val loadedPlayerData = data[uuid]
    if (loadedPlayerData != null) return@coroutineScope loadedPlayerData

    return@coroutineScope fetch(Queries.GET_PLAYER_DATA, listOf(uuid.toString())) {
      if (!next()) {
        data[uuid] = PlayerData(uuid, null, null, null, false)
        return@fetch data[uuid]!!
      }

      val locale = getString("locale")
      val disabledEnchants = getString("disabled").toDisabledEnchantments(plugin)
      val cooldowns = getString("cooldowns").toCooldowns(uuid, plugin)

      val loadedData = PlayerData(uuid, locale, disabledEnchants, cooldowns, true)
      data[uuid] = loadedData
      loadedData
    }
  }

  override fun getPlayerData(uuid: UUID): PlayerData? = data[uuid]

  override suspend fun saveAll() {
    for (data in data.values) savePlayerData(data)
  }

  override suspend fun refresh() {
    saveAll()
    uploadModifications()
    data.clear()

    for (player in Bukkit.getOnlinePlayers()) loadPlayerData(player.uniqueId)
  }

  override fun pendModification(uuid: UUID, modification: PlayerDataModification) {
    val playerData = data[uuid] ?: return
    playerData.modifications.removeIf { it.type == modification.type }

    playerData.modifications.add(modification)
    data.replace(uuid, playerData)
  }

  override suspend fun uploadModifications(uuid: UUID) {
    val playerData = data[uuid] ?: return
    val modifications = playerData.modifications

    for (modification in modifications) {
      when (modification.type) {
        PlayerDataModificationType.LOCALE -> update(Queries.UPDATE_LOCALE, modification.value, uuid.toString())
        PlayerDataModificationType.DISABLED -> update(Queries.UPDATE_DISABLED_ENCHANTS, (modification.value as List<NamespacedKey>?).serializableString(), uuid.toString())
        PlayerDataModificationType.COOLDOWNS -> update(Queries.UPDATE_COOLDOWNS, (modification.value as List<Cooldown>?).serializableString(), uuid.toString())
      }
    }

    modifications.clear()
  }

  override suspend fun uploadModifications() {
    for (uuid in data.keys) uploadModifications(uuid)
  }

  object Queries {
    val CREATE_TABLE = """
      CREATE TABLE IF NOT EXISTS player_data (
        uuid VARCHAR(36) NOT NULL,
        locale VARCHAR(12),
        disabled TEXT,
        cooldowns TEXT,
        PRIMARY KEY (uuid)
      ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
    """.trimIndent()

    /**
     * This query is used to add player data to the database.
     * Parameters:
     * 1. The UUID of the player.
     * 2. The player's locale of choice, null if server-default.
     * 3. The player's disabled enchants (keys separated by commas), null if none.
     * 4. The player's cooldowns (key:endsAt separated by commas), null if none.
     */
    val ADD_DATA = """
      INSERT INTO player_data (uuid, locale, disabled, cooldowns) VALUES (?, ?, ?, ?)
    """.trimIndent()

    /**
     * This query is used to update player locale in the database.
     * Parameters:
     * 1. The player's locale of choice, null if server-default.
     * 2. The UUID of the player.
     */
    val UPDATE_LOCALE = """
      UPDATE player_data SET locale = ? WHERE uuid = ?
    """.trimIndent()

    /**
     * This query is used to update player disabled enchants in the database.
     * Parameters:
     * 1. The player's disabled enchants (keys separated by commas), null if none.
     * 2. The UUID of the player.
     */
    val UPDATE_DISABLED_ENCHANTS = """
      UPDATE player_data SET disabled = ? WHERE uuid = ?
    """.trimIndent()

    /**
     * This query is used to update player cooldowns in the database.
     * Parameters:
     * 1. The player's cooldowns (key:endsAt separated by commas), null if none.
     * 2. The UUID of the player.
     */
    val UPDATE_COOLDOWNS = """
      UPDATE player_data SET cooldowns = ? WHERE uuid = ?
    """.trimIndent()

    /**
     * This query is used to get player data from the database.
     * Parameters:
     * 1. The UUID of the player.
     */
    val GET_PLAYER_DATA = """
      SELECT * FROM player_data WHERE uuid = ?
    """.trimIndent()
  }
}
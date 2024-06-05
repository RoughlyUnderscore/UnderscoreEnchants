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

package com.roughlyunderscore.ue.storage.file

import com.roughlyunderscore.ue.UnderscoreEnchantsPlugin
import com.roughlyunderscore.annotations.Since
import com.roughlyunderscore.annotations.Stable
import com.roughlyunderscore.ue.data.Cooldown
import com.roughlyunderscore.ue.data.PlayerData
import com.roughlyunderscore.ue.data.PlayerDataModification
import com.roughlyunderscore.ue.data.PlayerDataModificationType
import com.roughlyunderscore.ue.storage.DataStorage
import com.roughlyunderscore.ue.utils.serializableString
import com.roughlyunderscore.ulib.io.requireChildDirectory
import kotlinx.coroutines.coroutineScope
import org.bukkit.NamespacedKey
import org.bukkit.configuration.file.YamlConfiguration
import java.util.*

/**
 * This class represents a YAML storage medium.
 */
@Since("2.2")
@Stable
class YamlStorage(private val plugin: UnderscoreEnchantsPlugin) : DataStorage {
  override suspend fun init() = plugin.dataFolder.requireChildDirectory("data")

  override val data: MutableMap<UUID, PlayerData> = mutableMapOf()

  override suspend fun savePlayerData(playerData: PlayerData) = coroutineScope {
    try {
      uploadModifications(playerData.uuid)
      if (playerData.isLoadedFromMedium) return@coroutineScope

      val uuid = playerData.uuid
      val file = plugin.dataFolder.resolve("data/$uuid.yaml")
      if (!file.exists()) file.createNewFile()

      val configuration = YamlConfiguration.loadConfiguration(file)
      configuration.set("data", playerData.serialize())
      configuration.save(file)

      playerData.isLoadedFromMedium = true
      data.replace(playerData.uuid, playerData)
    } catch (ex: Exception) {
      ex.printStackTrace()
    }
  }

  override suspend fun saveDataOf(uuid: UUID) {
    data[uuid]?.let { savePlayerData(it) }
  }

  override suspend fun loadPlayerData(uuid: UUID): PlayerData = coroutineScope {
    val file = plugin.dataFolder.resolve("data/$uuid.yaml")
    if (!file.exists()) {
      data[uuid] = PlayerData(uuid, null, null, null, false)
      return@coroutineScope data[uuid]!!
    }

    val configuration = YamlConfiguration.loadConfiguration(file)
    val playerData = PlayerData.deserialize(plugin, configuration.getValues(false)) ?: run {
      data[uuid] = PlayerData(uuid, null, null, null, false)
      return@coroutineScope data[uuid]!!
    }

    playerData.isLoadedFromMedium = true
    data[uuid] = playerData
    return@coroutineScope playerData
  }

  override fun getPlayerData(uuid: UUID): PlayerData? = data[uuid]

  override suspend fun refresh() {
    saveAll()
    uploadModifications()
    data.clear()

    for (player in plugin.server.onlinePlayers) loadPlayerData(player.uniqueId)
  }

  override suspend fun saveAll() {
    for (data in data.values) savePlayerData(data)
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

    val file = plugin.dataFolder.resolve("data/$uuid.yaml")
    if (!file.exists()) return

    val configuration = YamlConfiguration.loadConfiguration(file)
    for (modification in modifications) {
      when (modification.type) {
        PlayerDataModificationType.LOCALE -> configuration.set("data.locale", modification.value)
        PlayerDataModificationType.DISABLED -> configuration.set("data.disabled", (modification.value as List<NamespacedKey>?).serializableString())
        PlayerDataModificationType.COOLDOWNS -> configuration.set("data.cooldowns", (modification.value as List<Cooldown>?).serializableString())
      }
    }

    configuration.save(file)
    modifications.clear()
  }

  override suspend fun uploadModifications() {
    for (uuid in data.keys) uploadModifications(uuid)
  }
}
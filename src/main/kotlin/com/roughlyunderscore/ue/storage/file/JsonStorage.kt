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

import com.roughlyunderscore.annotations.Since
import com.roughlyunderscore.annotations.Stable
import com.roughlyunderscore.ue.UnderscoreEnchantsPlugin
import com.roughlyunderscore.ue.data.Cooldown
import com.roughlyunderscore.ue.data.PlayerData
import com.roughlyunderscore.ue.data.PlayerDataModification
import com.roughlyunderscore.ue.data.PlayerDataModificationType
import com.roughlyunderscore.ue.storage.DataStorage
import com.roughlyunderscore.ulib.io.requireChildDirectory
import kotlinx.coroutines.coroutineScope
import org.bukkit.NamespacedKey
import java.util.*

/**
 * This class represents a JSON storage medium.
 */
@Since("2.2")
@Stable
class JsonStorage(private val plugin: UnderscoreEnchantsPlugin) : DataStorage {
  override suspend fun init() = plugin.dataFolder.requireChildDirectory("data")

  override val data: MutableMap<UUID, PlayerData> = mutableMapOf()

  override suspend fun savePlayerData(playerData: PlayerData) = coroutineScope {
    uploadModifications(playerData.uuid)
    if (playerData.isLoadedFromMedium) return@coroutineScope

    val uuid = playerData.uuid
    val file = plugin.dataFolder.resolve("data/$uuid.json")
    if (!file.exists()) file.createNewFile()

    file.writer(Charsets.UTF_8).use { writer ->
      plugin.gson.toJson(playerData, writer)
    }

    playerData.isLoadedFromMedium = true
    data.replace(playerData.uuid, playerData)
  }

  override suspend fun saveDataOf(uuid: UUID) {
    data[uuid]?.let { savePlayerData(it) }
  }

  override suspend fun loadPlayerData(uuid: UUID): PlayerData = coroutineScope {
    val file = plugin.dataFolder.resolve("data/$uuid.json")
    if (!file.exists()) {
      data[uuid] = PlayerData(uuid, null, null, null, false)
      return@coroutineScope data[uuid]!!
    }

    val playerData = file.reader(Charsets.UTF_8).use { reader ->
      plugin.gson.fromJson(reader, PlayerData::class.java)
    } ?: run {
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

  override suspend fun uploadModifications(uuid: UUID) = coroutineScope {
    val playerData = data[uuid] ?: return@coroutineScope
    val modifications = playerData.modifications

    for (modification in modifications) {
      when (modification.type) {
        PlayerDataModificationType.LOCALE -> playerData.locale = modification.value as String?
        PlayerDataModificationType.DISABLED -> playerData.disabled = (modification.value as List<NamespacedKey>?)?.toMutableList()
        PlayerDataModificationType.COOLDOWNS -> playerData.cooldowns = (modification.value as List<Cooldown>?)?.toMutableList()
      }
    }

    // I don't know how to reliably replace values in JSON. If you do, please PR
    // For now, I'll just rewrite the file

    val file = plugin.dataFolder.resolve("data/$uuid.json")
    if (!file.exists()) file.createNewFile()

    file.writer(Charsets.UTF_8).use { writer ->
      plugin.gson.toJson(playerData, writer)
    }

    data[uuid] = playerData
    modifications.clear()
  }

  override suspend fun uploadModifications() {
    for (uuid in data.keys) uploadModifications(uuid)
  }
}
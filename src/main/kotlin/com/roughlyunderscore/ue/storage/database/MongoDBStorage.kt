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

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.ServerApi
import com.mongodb.ServerApiVersion
import com.mongodb.client.MongoClients
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
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
import org.bson.Document
import org.bson.conversions.Bson
import org.bukkit.NamespacedKey
import java.net.URLEncoder
import java.util.*

/**
 * This class represents a MongoDB storage medium.
 */
@Since("2.2")
@Stable
class MongoDBStorage(url: String, username: String, password: String, databaseName: String, collectionName: String, private val plugin: UnderscoreEnchantsPlugin) :
  DataStorage {
  constructor(connectionData: ConnectionData, plugin: UnderscoreEnchantsPlugin) : this(
    url = connectionData.mongo.url,
    username = connectionData.mongo.username,
    password = connectionData.mongo.password,
    databaseName = connectionData.mongo.database,
    collectionName = connectionData.mongo.collection,
    plugin = plugin
  )


  private val encodedUsername = URLEncoder.encode(username, "UTF-8")
  private val encodedPassword = URLEncoder.encode(password, "UTF-8")
  private val connectionString =
    if (encodedUsername.isNotEmpty() && encodedPassword.isNotEmpty()) "mongodb://$encodedUsername:$encodedPassword@$url/$databaseName"
    else "mongodb://$url/$databaseName"

  override val data: MutableMap<UUID, PlayerData> = mutableMapOf()

  private val serverApi = ServerApi.builder()
    .version(ServerApiVersion.V1)
    .build()

  private val settings = MongoClientSettings.builder()
    .applyConnectionString(ConnectionString(connectionString))
    .serverApi(serverApi)
    .build()

  private val client = MongoClients.create(settings)
  private val database = client.getDatabase(databaseName)
  private val collection = database.getCollection(collectionName)

  private fun find(filter: Bson): Document? = collection.find(filter).first() ?: null
  private fun insert(document: Document) = collection.insertOne(document)
  private fun update(filter: Bson, update: Bson) = collection.updateOne(filter, update)

  override suspend fun init() = Unit

  override suspend fun savePlayerData(playerData: PlayerData) = coroutineScope {
    uploadModifications(playerData.uuid)
    if (playerData.isLoadedFromMedium) return@coroutineScope

    val uuid = playerData.uuid.toString()
    val locale = playerData.locale
    val disabledEnchants = playerData.disabled.serializableString()
    val cooldowns = playerData.cooldowns.serializableString()

    insert(Document().apply {
      set("uuid", uuid)
      if (locale != null) set("locale", locale)
      if (disabledEnchants != null) set("disabled", disabledEnchants)
      if (cooldowns != null) set("cooldowns", cooldowns)
    })

    playerData.isLoadedFromMedium = true
    data.replace(playerData.uuid, playerData)
  }

  override suspend fun saveDataOf(uuid: UUID) {
    data[uuid]?.let { savePlayerData(it) }
  }

  override suspend fun loadPlayerData(uuid: UUID): PlayerData = coroutineScope {
    val loadedPlayerData = data[uuid]
    if (loadedPlayerData != null) return@coroutineScope loadedPlayerData

    val document = find(Document("uuid", uuid.toString())) ?: run {
      data[uuid] = PlayerData(uuid, null, null, null, false)
      return@coroutineScope data[uuid]!!
    }

    val locale = document.getString("locale")
    val disabledEnchants = document.getString("disabled").toDisabledEnchantments(plugin)
    val cooldowns = document.getString("cooldowns").toCooldowns(uuid, plugin)

    val loadedData = PlayerData(uuid, locale, disabledEnchants, cooldowns, true)
    data[uuid] = loadedData
    loadedData
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

    for (modification in modifications) {
      when (modification.type) {
        PlayerDataModificationType.LOCALE -> update(
          Filters.eq("uuid", uuid.toString()),
          Updates.set("locale", modification.value)
        )

        PlayerDataModificationType.DISABLED -> update(
          Filters.eq("uuid", uuid.toString()),
          Updates.set("disabled", (modification.value as List<NamespacedKey>?).serializableString())
        )

        PlayerDataModificationType.COOLDOWNS -> update(
          Filters.eq("uuid", uuid.toString()),
          Updates.set("cooldowns", (modification.value as List<Cooldown>?).serializableString())
        )
      }
    }

    modifications.clear()
  }

  override suspend fun uploadModifications() {
    for (uuid in data.keys) uploadModifications(uuid)
  }
}
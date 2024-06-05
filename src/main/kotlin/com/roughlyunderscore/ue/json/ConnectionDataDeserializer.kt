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

package com.roughlyunderscore.ue.json

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.roughlyunderscore.ue.config.ConnectionData
import java.lang.reflect.Type

object ConnectionDataDeserializer : JsonDeserializer<ConnectionData?> {
  override fun deserialize(src: JsonElement?, type: Type?, ctx: JsonDeserializationContext?): ConnectionData? {
    val json = src?.asJsonObject ?: return null
    val connectionNode = json.get("connection")?.asJsonObject ?: return null

    val jdbcData = connectionNode.get("jdbc")?.asJsonObject ?: return null
    val mongoData = connectionNode.get("mongo")?.asJsonObject ?: return null

    return ConnectionData(
      jdbc = ConnectionData.JDBCConnectionData(
        url = jdbcData.get("url")?.asString ?: "localhost",
        port = jdbcData.get("port")?.asInt ?: 3306,
        database = jdbcData.get("database")?.asString ?: "my_server",
        username = jdbcData.get("username")?.asString ?: "root",
        password = jdbcData.get("password")?.asString ?: "",
      ),

      mongo = ConnectionData.MongoDBConnectionData(
        url = mongoData.get("url")?.asString ?: "localhost",
        database = mongoData.get("database")?.asString ?: "my_server",
        collection = mongoData.get("collection")?.asString ?: "underscore_enchants",
        username = mongoData.get("username")?.asString ?: "bestest_username",
        password = mongoData.get("password")?.asString ?: "",
      ),
    )
  }
}
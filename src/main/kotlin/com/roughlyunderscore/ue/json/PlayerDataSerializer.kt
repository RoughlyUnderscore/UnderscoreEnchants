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

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.roughlyunderscore.json.DeserializationNames
import com.roughlyunderscore.ue.data.PlayerData
import java.lang.reflect.Type

object PlayerDataSerializer : JsonSerializer<PlayerData> {
  override fun serialize(playerData: PlayerData?, type: Type, ctx: JsonSerializationContext?): JsonElement {
    val data = JsonObject()
    if (playerData == null) return data

    data.addProperty(DeserializationNames.PlayerData.UUID.first(), playerData.uuid.toString())
    playerData.locale?.let { locale -> data.addProperty(DeserializationNames.PlayerData.LOCALE.first(), locale) }

    playerData.disabled?.let { disabled ->
      if (disabled.isEmpty()) return@let
      else data.addProperty(DeserializationNames.PlayerData.TOGGLED_ENCHANTS.first(), disabled.joinToString(",") { it.key })
    }

    playerData.cooldowns?.let { cooldowns ->
      if (cooldowns.isEmpty()) return@let
      else data.addProperty(DeserializationNames.PlayerData.COOLDOWNS.first(), cooldowns.joinToString(",") { "${it.enchantmentKey.key}:${it.endsAt}" })
    }

    return data
  }
}
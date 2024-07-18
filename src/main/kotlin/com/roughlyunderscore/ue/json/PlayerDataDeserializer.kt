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
import com.roughlyunderscore.json.DeserializationNames
import com.roughlyunderscore.ue.UnderscoreEnchantsPlugin
import com.roughlyunderscore.ue.data.PlayerData
import com.roughlyunderscore.ue.utils.toCooldowns
import com.roughlyunderscore.ue.utils.toDisabledEnchantments
import com.roughlyunderscore.ulib.json.anyString
import com.roughlyunderscore.ulib.json.onAnyString
import com.roughlyunderscore.ulib.text.safeUuid
import java.lang.reflect.Type

class PlayerDataDeserializer(private val plugin: UnderscoreEnchantsPlugin) : JsonDeserializer<PlayerData> {
  override fun deserialize(json: JsonElement?, type: Type, ctx: JsonDeserializationContext?): PlayerData? {

    val data = json?.asJsonObject ?: return null

    val uuid = data.onAnyString(DeserializationNames.PlayerData.UUID) { safeUuid() } ?: return null
    val locale = data.anyString(DeserializationNames.PlayerData.LOCALE)

    val toggledEnchants = data.onAnyString(DeserializationNames.PlayerData.TOGGLED_ENCHANTS) { this.toDisabledEnchantments(plugin) }
    val cooldowns = data.onAnyString(DeserializationNames.PlayerData.COOLDOWNS) { this.toCooldowns(uuid, plugin) }

    return PlayerData(uuid, locale, toggledEnchants, cooldowns)
  }
}
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

package com.roughlyunderscore.ue.data

import com.roughlyunderscore.ue.UnderscoreEnchantsPlugin
import com.roughlyunderscore.annotations.Since
import com.roughlyunderscore.ue.utils.serializableString
import com.roughlyunderscore.ue.utils.toCooldowns
import com.roughlyunderscore.ue.utils.toDisabledEnchantments
import com.roughlyunderscore.ulib.text.safeUuid
import org.bukkit.NamespacedKey
import org.bukkit.configuration.MemorySection
import org.bukkit.configuration.serialization.ConfigurationSerializable
import java.util.UUID

/**
 * The data that is stored for each player.
 */
@Since("2.2")
data class PlayerData(val uuid: UUID, var locale: String?, var disabled: MutableList<NamespacedKey>?, var cooldowns: MutableList<Cooldown>?, var isLoadedFromMedium: Boolean = false) : ConfigurationSerializable {
  val modifications = mutableListOf<PlayerDataModification>()

  override fun serialize(): MutableMap<String, Any> {
    val result = mutableMapOf<String, Any>("uuid" to uuid.toString())
    locale?.let { result["locale"] = it }
    disabled?.let { if (it.isEmpty()) return@let else result["disabled"] = it.serializableString()!! }
    cooldowns?.let { if (it.isEmpty()) return@let else result["cooldowns"] = it.serializableString()!! }

    return result
  }

  companion object {
    /**
     * Deserializes [data] into a potentially nullable PlayerData object.
     * [plugin] needed for initializing NamespacedKeys.
     */
    @Since("2.2")
    fun deserialize(plugin: UnderscoreEnchantsPlugin, data: Map<String, Any>): PlayerData? {
      (data["data"] as? MemorySection)?.let { node ->
        val uuid = node["uuid"]?.toString().safeUuid() ?: return null
        val locale = node["locale"]?.toString()
        val disabled = node["disabled"]?.toString().toDisabledEnchantments(plugin)
        val cooldowns = node["cooldowns"]?.toString().toCooldowns(uuid, plugin)

        return PlayerData(uuid, locale, disabled, cooldowns, true)
      } ?: return null
    }
  }
}
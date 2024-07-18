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

package com.roughlyunderscore.ue.utils

import com.roughlyunderscore.annotations.Since
import com.roughlyunderscore.annotations.Stable
import com.roughlyunderscore.enums.TargetType
import com.roughlyunderscore.enums.DataRetrievalType
import com.roughlyunderscore.ue.UnderscoreEnchantsPlugin
import com.roughlyunderscore.ue.data.Cooldown
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Tag
import java.util.*

/**
 * Maps a [TargetType] to a [DataRetrievalType].
 */
@Since("2.2")
@Stable
fun TargetType.mapToDrt(): DataRetrievalType = when (this) {
  TargetType.FIRST_PLAYER -> DataRetrievalType.FIRST_PLAYER
  TargetType.SECOND_PLAYER -> DataRetrievalType.SECOND_PLAYER
  TargetType.THIRD_PLAYER -> DataRetrievalType.THIRD_PLAYER
  TargetType.ENTITY -> DataRetrievalType.ENTITY
  TargetType.BLOCK -> DataRetrievalType.BLOCK
  TargetType.FIRST_ITEM -> DataRetrievalType.FIRST_ITEM
  TargetType.SECOND_ITEM -> DataRetrievalType.SECOND_ITEM
}

/**
 * Converts a (potentially null) list of [NamespacedKey]s to a String that's ready
 * to be saved in a storage medium.
 */
@Since("2.2")
@Stable
@JvmName("serializableString\$enchs")
fun List<NamespacedKey>?.serializableString() = this?.let { it.ifEmpty { null } }?.joinToString(",") { it.key }

/**
 * Converts a (potentially null) list of [Cooldown]s to a String that's ready
 * to be saved in a storage medium.
 */
@Since("2.2")
@Stable
@JvmName("serializableString\$cds")
fun List<Cooldown>?.serializableString() = this?.let { it.ifEmpty { null } }?.joinToString(",") { "${it.enchantmentKey.key}:${it.endsAt}" }

/**
 * Converts a (potentially null) String received from a storage medium to a [MutableList]
 * of [NamespacedKey]s representing all disabled enchantments of a player.
 */
@Since("2.2")
@Stable
fun String?.toDisabledEnchantments(plugin: UnderscoreEnchantsPlugin): MutableList<NamespacedKey>? = if (this?.isBlank() == true) null else
  this?.split(",")?.map { NamespacedKey(plugin, it) }?.toMutableList()

/**
 * Converts a (potentially null) String received from a storage medium to a [MutableList]
 * of [Cooldown]s representing all disabled enchantments of a player. [uuid] is the
 * [UUID] of the player.
 */
@Since("2.2")
@Stable
fun String?.toCooldowns(uuid: UUID, plugin: UnderscoreEnchantsPlugin): MutableList<Cooldown>? = if (this?.isBlank() == true) null else
  this?.split(",")?.map {
    val split = it.split(":")
    Cooldown(uuid, NamespacedKey(plugin, split[0]), split[1].toLong())
  }?.toMutableList()

operator fun Tag<Material>.contains(material: Material) = this.isTagged(material)
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

import com.roughlyunderscore.ue.UnderscoreEnchantment
import com.roughlyunderscore.ue.UnderscoreEnchantsPlugin
import com.roughlyunderscore.annotations.Beta
import com.roughlyunderscore.annotations.Since
import com.roughlyunderscore.data.EnchantmentPack
import com.roughlyunderscore.data.EnchantmentPackMetadata
import com.roughlyunderscore.registry.RegistrableEnchantment
import com.roughlyunderscore.registry.UEAPIRegistry
import com.roughlyunderscore.ulib.io.tempUntarOperate
import java.io.File
import java.io.InputStream

/**
 * Loads an [EnchantmentPack] from a TAR file.
 * The TAR file can contain any type of files, the only ones that will be loaded are JSON files.
 * If the JSON file does not conform to the expected enchantment or metadata schema, it will also be skipped.
 * [plugin] needed for intermediate steps.
 * @return A discovered pack.
 * @see UnderscoreEnchantment
 * @see UEAPIRegistry
 * @throws IllegalArgumentException If the file is not a TAR file.
 */
@Since("2.2")
@Beta
fun File.loadPackFromTARFile(plugin: UnderscoreEnchantsPlugin): EnchantmentPack? = inputStream().use { it.loadPackFromInputStream(this.name, plugin) }

/**
 * Loads an [EnchantmentPack] from an [inputStream] resembling the contents of a TAR archive.
 * The TAR file can contain any type of files, the only ones that will be loaded are JSON files.
 * If the JSON file does not conform to the expected enchantment or metadata schema, it will also be skipped.
 * [plugin] needed for intermediate steps.
 * @return A discovered pack.
 * @see UnderscoreEnchantment
 * @see UEAPIRegistry
 * @throws IllegalArgumentException If the file is not a TAR file.
 */
@Since("2.2")
@Beta
fun InputStream.loadPackFromInputStream(fileName: String, plugin: UnderscoreEnchantsPlugin): EnchantmentPack? = this.tempUntarOperate(listOf("json")) { files ->
  val enchantments = mutableListOf<RegistrableEnchantment>()
  var metadata: EnchantmentPackMetadata? = null

  for (file in files) {
    if (file.isDirectory) continue
    if (file.name.equals("pack_metadata.json", true)) {
      metadata = try {
        file.reader().use { plugin.gson.fromJson(it, EnchantmentPackMetadata::class.java) }
      } catch (ex: Exception) {
        plugin.logger.severe("Failed to load pack metadata from pack file ${fileName}!")
        continue
      }

      continue
    } else {
      val enchantment = try {
        file.reader().use { plugin.gson.fromJson(it, UnderscoreEnchantment::class.java) }
      } catch (ex: Exception) {
        plugin.logger.severe("Failed to load enchantment from file ${file.name}!")
        continue
      }

      if (enchantment != null) enchantments.add(enchantment)
    }
  }

  if (metadata == null) return@tempUntarOperate null
  return@tempUntarOperate EnchantmentPack(metadata, enchantments)
}
  /*TarArchiveInputStream(this).use { tar ->
  val enchantments = mutableListOf<RegistrableEnchantment>()

  var metadata: EnchantmentPackMetadata? = null
  var entry = tar.nextEntry

  while (entry != null) {
    if (entry.isDirectory) {
      entry = tar.nextEntry
      continue
    }
    if (!entry.name.endsWith(".json")) {
      entry = tar.nextEntry
      continue
    }

    if (entry.name.equals("pack_metadata.json", true)) {
      metadata = entry.file.reader().use { plugin.gson.fromJson(it, EnchantmentPackMetadata::class.java) }
    }

    val enchantment = entry.file.reader().use { plugin.gson.fromJson(it, UnderscoreEnchantment::class.java) }
    if (enchantment != null) enchantments.add(enchantment)

    entry = tar.nextEntry
  }

  if (metadata == null) return@use null
  return EnchantmentPack(metadata, enchantments)
}*/
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

package com.roughlyunderscore.ue.hook.api

import com.roughlyunderscore.ue.UnderscoreEnchantment
import com.roughlyunderscore.ue.UnderscoreEnchantsPlugin
import com.roughlyunderscore.api.UELoader
import com.roughlyunderscore.data.EnchantmentPackMetadata
import com.roughlyunderscore.data.UELocale
import com.roughlyunderscore.enums.LoadResponse
import com.roughlyunderscore.enums.UnloadResponse
import com.roughlyunderscore.ulib.text.containsIgnoreCase
import com.roughlyunderscore.ue.utils.loadPackFromTARFile
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import java.io.File
import java.io.FileFilter

class UELoaderImpl(private val plugin: UnderscoreEnchantsPlugin) : UELoader {
  val registry = plugin.registry

  override fun loadEnchantment(enchantmentName: String): LoadResponse {
    // I do not think there's a better approach to this than going through all
    // files and checking if the name matches. This is because the name of the
    // enchantment is not necessarily the same as the name of the file.

    if (!plugin.dataFolder.resolve("enchantments").exists()) return LoadResponse.NOT_FOUND

    val files = plugin.dataFolder.resolve("enchantments").listFiles(FileFilter { it.extension == "json" }) ?: return LoadResponse.NOT_FOUND
    for (file in files) {
      val enchantment = file.reader().use { plugin.gson.fromJson(it, UnderscoreEnchantment::class.java) } ?: continue
      if (!enchantment.aliases.containsIgnoreCase(enchantmentName)) continue

      return loadEnchantment(file)
    }

    return LoadResponse.NOT_FOUND
  }

  override fun loadEnchantment(file: File): LoadResponse {
    if (!file.exists()) return LoadResponse.NOT_FOUND

    val enchantment = file.reader().use { plugin.gson.fromJson(it, UnderscoreEnchantment::class.java) } ?: return LoadResponse.NOT_FOUND

    if (registry.findEnchantment(enchantment.name) != null) {
      unloadEnchantment(enchantment.name)
      enchantment.initializeEnchantment(plugin)
      return LoadResponse.RELOADED
    }

    enchantment.initializeEnchantment(plugin)
    return LoadResponse.LOADED
  }

  override fun unloadEnchantment(file: File): UnloadResponse {
    if (!file.exists()) return UnloadResponse.NOT_FOUND

    val enchantment = file.reader().use { plugin.gson.fromJson(it, UnderscoreEnchantment::class.java) } ?: return UnloadResponse.NOT_FOUND
    return unloadEnchantment(enchantment.name)
  }

  override fun unloadEnchantment(enchantmentName: String): UnloadResponse {
    if (registry.findEnchantment(enchantmentName) == null) return UnloadResponse.NOT_FOUND
    registry.unregisterEnchantment(enchantmentName)
    return UnloadResponse.UNLOADED
  }

  override fun loadPack(packFile: File): LoadResponse {
    if (!packFile.exists()) return LoadResponse.NOT_FOUND

    val pack = packFile.loadPackFromTARFile(plugin) ?: return LoadResponse.NOT_FOUND
    registry.loadPack(pack)
    pack.enchantments.forEach { (it as UnderscoreEnchantment).initializeEnchantment(plugin) }
    return LoadResponse.LOADED
  }

  override fun unloadPack(packFile: File): UnloadResponse {
    if (!packFile.exists()) return UnloadResponse.NOT_FOUND

    packFile.inputStream().use { stream ->
      // We need to dig into the pack to find its metadata.
      val archive = TarArchiveInputStream(stream)

      var entry = archive.nextEntry
      while (entry != null) {
        if (entry.isDirectory) {
          entry = archive.nextEntry
          continue
        }

        if (!entry.name.equals("pack_metadata.json", true)) {
          entry = archive.nextEntry
          continue
        }

        val metadata = entry.file.reader().use {
          plugin.gson.fromJson(it, EnchantmentPackMetadata::class.java)
        } ?: return UnloadResponse.NOT_FOUND

        registry.unloadPack(metadata.name)

        return UnloadResponse.UNLOADED
      }

      return UnloadResponse.NOT_FOUND
    }
  }

  override fun unloadPack(packName: String): UnloadResponse {
    if (registry.findEnchantmentPack(packName) == null) return UnloadResponse.NOT_FOUND
    registry.unloadPack(packName)
    return UnloadResponse.UNLOADED
  }

  override fun loadLocale(localeFile: File): LoadResponse {
    if (!localeFile.exists()) return LoadResponse.NOT_FOUND

    val locale = localeFile.reader().use { plugin.gson.fromJson(it, UELocale::class.java) } ?: return LoadResponse.NOT_FOUND
    if (plugin.getLocale(locale.localeIdentifier) != null) {
      plugin.locales.removeIf { it.localeIdentifier == locale.localeIdentifier }
      plugin.locales.add(locale)
      return LoadResponse.RELOADED
    }

    plugin.locales.add(locale)
    return LoadResponse.LOADED
  }

  override fun loadLocale(localeName: String): LoadResponse {
    if (!plugin.dataFolder.resolve("messages").exists()) return LoadResponse.NOT_FOUND

    val files = plugin.dataFolder.resolve("messages").listFiles(FileFilter { it.extension == "json" }) ?: return LoadResponse.NOT_FOUND
    for (file in files) {
      val locale = file.reader().use { plugin.gson.fromJson(it, UELocale::class.java) } ?: continue
      if (locale.localeIdentifier != localeName) continue

      return loadLocale(file)
    }

    return LoadResponse.NOT_FOUND
  }

  override fun unloadLocale(localeFile: File): UnloadResponse {
    if (!localeFile.exists()) return UnloadResponse.NOT_FOUND

    val locale = localeFile.reader().use { plugin.gson.fromJson(it, UELocale::class.java) } ?: return UnloadResponse.NOT_FOUND
    return unloadLocale(locale.localeIdentifier)
  }

  override fun unloadLocale(localeName: String): UnloadResponse {
    if (plugin.getLocale(localeName) == null) return UnloadResponse.NOT_FOUND
    plugin.locales.removeIf { it.localeIdentifier == localeName }
    return UnloadResponse.UNLOADED
  }
}
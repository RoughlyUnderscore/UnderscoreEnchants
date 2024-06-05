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

import com.roughlyunderscore.data.EnchantmentPack
import com.roughlyunderscore.data.UELocale
import com.roughlyunderscore.enums.LoadResponse
import com.roughlyunderscore.enums.UnloadResponse
import com.roughlyunderscore.ue.UnderscoreEnchantment
import com.roughlyunderscore.ue.UnderscoreEnchantsPlugin
import com.roughlyunderscore.ue.http.BackendAPIRepository
import com.roughlyunderscore.ue.registry.RegistryImpl
import java.io.File

open class ContentType<T>(
  val browsePerm: String,
  val downloadPerm: String,
  val loadPerm: String,
  val unloadPerm: String,

  val downloadCallback: suspend BackendAPIRepository.(Long) -> Pair<T, File>?,

  val loadCallback: RegistryImpl.(T, UnderscoreEnchantsPlugin) -> Unit,
  val loadFromFilenameCallback: (String, UnderscoreEnchantsPlugin) -> LoadResponse,

  val unloadFromNameCallback: (String, UnderscoreEnchantsPlugin) -> UnloadResponse,

  val downloadSuccessfulString: UELocale.() -> String,
  val downloadSuccessfulNoPromptString: UELocale.() -> String,
  val downloadAndLoadSuccessfulString: UELocale.() -> String,
  val downloadPromptString: UELocale.() -> String,

  val loadSuccessfulString: UELocale.() -> String,
  val loadNotFoundString: UELocale.() -> String,

  val unloadSuccessfulString: UELocale.() -> String,
  val unloadNotFoundString: UELocale.() -> String,

  val name: (T) -> String,

  val loadCommandString: (String) -> String
) {
  object Enchantment : ContentType<UnderscoreEnchantment>(
    browsePerm = Permissions.BROWSE_ENCHANTMENTS,
    downloadPerm = Permissions.DOWNLOAD_ENCHANTMENT,
    loadPerm = Permissions.LOAD_ENCHANTMENT,
    unloadPerm = Permissions.UNLOAD_ENCHANTMENT,

    downloadCallback = { id -> downloadEnchantment(id) },

    loadCallback = { ench, plugin -> ench.initializeEnchantment(plugin) },
    loadFromFilenameCallback = { name, plugin -> plugin.loader.loadEnchantment(plugin.dataFolder.resolve("enchantments").resolve(name)) },

    unloadFromNameCallback = { name, plugin -> plugin.loader.unloadEnchantment(name) },

    downloadSuccessfulString = { downloadEnchantmentSuccessful },
    downloadSuccessfulNoPromptString = { downloadEnchantmentSuccessfulNoPrompt },
    downloadAndLoadSuccessfulString = { downloadAndLoadEnchantmentSuccessful },
    downloadPromptString = { downloadEnchantmentClickToLoad },

    loadSuccessfulString = { loadEnchantmentSuccessful },
    loadNotFoundString = { loadEnchantmentNotFound },

    unloadSuccessfulString = { unloadEnchantmentSuccessful },
    unloadNotFoundString = { unloadEnchantmentNotFound },

    name = { ench -> ench.name },

    loadCommandString = { name -> "/ue load enchantment $name"}
  )

  object Pack : ContentType<EnchantmentPack>(
    browsePerm = Permissions.BROWSE_PACKS,
    downloadPerm = Permissions.DOWNLOAD_PACK,
    loadPerm = Permissions.LOAD_PACK,
    unloadPerm = Permissions.UNLOAD_PACK,

    downloadCallback = { id -> downloadPack(id) },

    loadCallback = { pack, plugin ->
      loadPack(pack)
      pack.enchantments.forEach { (it as UnderscoreEnchantment).initializeEnchantment(plugin) }
    },
    loadFromFilenameCallback = { name, plugin -> plugin.loader.loadPack(plugin.dataFolder.resolve("enchantments").resolve(name)) },

    unloadFromNameCallback = { name, plugin -> plugin.loader.unloadPack(name) },

    downloadSuccessfulString = { downloadPackSuccessful },
    downloadSuccessfulNoPromptString = { downloadPackSuccessfulNoPrompt },
    downloadAndLoadSuccessfulString = { downloadAndLoadPackSuccessful },
    downloadPromptString = { downloadPackClickToLoad },

    loadSuccessfulString = { loadPackSuccessful },
    loadNotFoundString = { loadPackNotFound },

    unloadSuccessfulString = { unloadPackSuccessful },
    unloadNotFoundString = { unloadPackNotFound },

    name = { pack -> pack.metadata.name },

    loadCommandString = { name -> "/ue load pack $name"}
  )

  object Locale : ContentType<UELocale>(
    browsePerm = Permissions.BROWSE_LOCALES,
    downloadPerm = Permissions.DOWNLOAD_LOCALE,
    loadPerm = Permissions.LOAD_LOCALE,
    unloadPerm = Permissions.UNLOAD_LOCALE,

    downloadCallback = { id -> downloadLocale(id) },

    loadCallback = { locale, plugin -> if (plugin.getLocale(locale.localeIdentifier) == null) plugin.locales.add(locale) },
    loadFromFilenameCallback = { name, plugin -> plugin.loader.loadLocale(plugin.dataFolder.resolve("messages").resolve(name)) },

    unloadFromNameCallback = { name, plugin -> plugin.loader.unloadLocale(name) },

    downloadSuccessfulString = { downloadLocaleSuccessful },
    downloadSuccessfulNoPromptString = { downloadLocaleSuccessfulNoPrompt },
    downloadAndLoadSuccessfulString = { downloadAndLoadLocaleSuccessful },
    downloadPromptString = { downloadLocaleClickToLoad },

    loadSuccessfulString = { loadLocaleSuccessful },
    loadNotFoundString = { loadLocaleNotFound },

    unloadSuccessfulString = { unloadLocaleSuccessful },
    unloadNotFoundString = { unloadLocaleNotFound },

    name = { locale -> locale.localeIdentifier },

    loadCommandString = { name -> "/ue load locale $name" }
  )
}
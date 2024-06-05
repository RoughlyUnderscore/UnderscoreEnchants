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

package com.roughlyunderscore.ue.commands

import com.roughlyunderscore.data.UELocale
import com.roughlyunderscore.enums.LoadResponse
import com.roughlyunderscore.enums.UnloadResponse
import com.roughlyunderscore.ue.UnderscoreEnchantsPlugin
import com.roughlyunderscore.ue.data.ContentType
import com.roughlyunderscore.ue.debug.RegistryDebugger
import com.roughlyunderscore.ue.debug.describers.EnchantmentDescriber
import com.roughlyunderscore.ue.registry.RegistryImpl
import com.roughlyunderscore.ulib.text.clickable
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandUtils {
  object Debugger {
    fun UnderscoreEnchantsCommand.debugEnchantment(arguments: List<String>, sender: CommandSender, locale: UELocale, registry: RegistryImpl) {
      val params = arguments.drop(1)
      if (params.isEmpty()) debug(sender, null)
      else {
        val enchantment = registry.findEnchantmentByKeyString(params[0]) ?: run {
          sender.sendMessage(locale.invalidEnchantmentName.replace("<enchantment>", params[0]))
          return
        }

        sender.sendMessage()
        EnchantmentDescriber.describe(locale, enchantment).forEach { sender.sendMessage(it) }
        sender.sendMessage()
      }
    }
    fun debugRegistry(arguments: List<String>, locale: UELocale, sender: CommandSender, registryDebugger: RegistryDebugger) {
      val params = arguments.drop(1)
      if (params.isEmpty()) registryDebugger.debugRegistry(locale, sender)
      else registryDebugger.debugRegistryType(locale, sender, params[0])
    }
  }

  object Downloader {
    private fun <T> download(sender: CommandSender, locale: UELocale, immediateLoad: Boolean, id: Long, registry: RegistryImpl, plugin: UnderscoreEnchantsPlugin, type: ContentType<T>) {
      if (!sender.hasPermission(type.downloadPerm)) {
        sender.sendMessage(locale.noPermissions)
        return
      }

      GlobalScope.launch {
        val item = (type.downloadCallback(plugin.repository, id)?.first ?: run {
          sender.sendMessage(locale.downloadUnsuccessful)
          return@launch
        })

        if (immediateLoad) {
          type.loadCallback(registry, item, plugin)
          sender.sendMessage(type.downloadSuccessfulString(locale))
        } else {
          if (sender is Player) sender.spigot().sendMessage(type.downloadSuccessfulString(locale).clickable(type.downloadPromptString(locale), type.loadCommandString(type.name(item))))
          else sender.sendMessage(type.downloadSuccessfulNoPromptString(locale))
        }
      }
    }
    fun downloadPack(sender: CommandSender, locale: UELocale, immediateLoad: Boolean, id: Long, registry: RegistryImpl, plugin: UnderscoreEnchantsPlugin) =
      download(sender, locale, immediateLoad, id, registry, plugin, ContentType.Pack)

    fun downloadEnchantment(sender: CommandSender, locale: UELocale, immediateLoad: Boolean, id: Long, registry: RegistryImpl, plugin: UnderscoreEnchantsPlugin) =
      download(sender, locale, immediateLoad, id, registry, plugin, ContentType.Enchantment)

    fun downloadLocale(sender: CommandSender, locale: UELocale, immediateLoad: Boolean, id: Long, registry: RegistryImpl, plugin: UnderscoreEnchantsPlugin) =
      download(sender, locale, immediateLoad, id, registry, plugin, ContentType.Locale)
  }

  object Loader {
    private fun <T> load(sender: CommandSender, filename: String, locale: UELocale, plugin: UnderscoreEnchantsPlugin, contentType: ContentType<T>) {
      if (!sender.hasPermission(contentType.loadPerm)) {
        sender.sendMessage(locale.noPermissions)
        return
      }

      sender.sendMessage(when (contentType.loadFromFilenameCallback(filename, plugin)) {
        LoadResponse.LOADED, LoadResponse.RELOADED -> contentType.loadSuccessfulString(locale)
        LoadResponse.NOT_FOUND -> contentType.loadNotFoundString(locale)
      })
    }

    fun loadEnchantment(sender: CommandSender, filename: String, locale: UELocale, plugin: UnderscoreEnchantsPlugin) =
      load(sender, filename, locale, plugin, ContentType.Enchantment)

    fun loadPack(sender: CommandSender, filename: String, locale: UELocale, plugin: UnderscoreEnchantsPlugin) =
      load(sender, filename, locale, plugin, ContentType.Pack)

    fun loadLocale(sender: CommandSender, filename: String, locale: UELocale, plugin: UnderscoreEnchantsPlugin) =
      load(sender, filename, locale, plugin, ContentType.Locale)
  }

  object Unloader {
    private fun <T> unload(sender: CommandSender, name: String, locale: UELocale, plugin: UnderscoreEnchantsPlugin, contentType: ContentType<T>) {
      if (!sender.hasPermission(contentType.unloadPerm)) {
        sender.sendMessage(locale.noPermissions)
        return
      }

      sender.sendMessage(when (contentType.unloadFromNameCallback(name, plugin)) {
        UnloadResponse.UNLOADED -> contentType.unloadSuccessfulString(locale)
        UnloadResponse.NOT_FOUND -> contentType.unloadNotFoundString(locale)
      })
    }

    fun unloadEnchantment(sender: CommandSender, name: String, locale: UELocale, plugin: UnderscoreEnchantsPlugin) =
      unload(sender, name, locale, plugin, ContentType.Enchantment)

    fun unloadPack(sender: CommandSender, name: String, locale: UELocale, plugin: UnderscoreEnchantsPlugin) =
      unload(sender, name, locale, plugin, ContentType.Pack)

    fun unloadLocale(sender: CommandSender, name: String, locale: UELocale, plugin: UnderscoreEnchantsPlugin) =
      unload(sender, name, locale, plugin, ContentType.Locale)
  }
}
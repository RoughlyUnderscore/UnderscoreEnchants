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

package com.roughlyunderscore.ue.debug

import com.roughlyunderscore.ue.UnderscoreEnchantment
import com.roughlyunderscore.ue.UnderscoreEnchantsPlugin
import com.roughlyunderscore.ue.debug.describers.BriefRegistryDescriber
import com.roughlyunderscore.ue.debug.describers.WorldDescriber
import com.roughlyunderscore.data.UELocale
import com.roughlyunderscore.ue.registry.RegistryImpl
import com.roughlyunderscore.ulib.io.requireChildDirectory
import com.roughlyunderscore.ulib.text.joinAndWrap
import com.roughlyunderscore.ue.utils.readable
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import java.io.BufferedWriter
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class LogCreator(private val plugin: UnderscoreEnchantsPlugin) {
  private val registry = plugin.registry

  fun createLogFile(locale: UELocale, sender: CommandSender) {
    val currentTime = Instant.now()
    sender.sendMessage(locale.creatingLog)

    plugin.dataFolder.requireChildDirectory("logs")
    val file = plugin.dataFolder.resolve("logs").resolve("log-${currentTime.epochSecond}.txt")
    if (!file.exists()) file.createNewFile()

    try {
      file.bufferedWriter().use { writer ->
        writer.writeLine("UnderscoreEnchants v${plugin.description.version}")
        writer.writeLine("${locale.logTokenAuthors}: ${plugin.description.authors.joinAndWrap("", "", ", ")}")
        writer.writeLine("${locale.logTokenCreated}: ${LocalDateTime.ofInstant(currentTime, ZoneId.systemDefault()).readable()}")
        writer.writeLine("======================")
        writer.writeLine(locale.logTokenWorlds)

        for (world in Bukkit.getWorlds()) {
          val described = WorldDescriber.describe(locale, world)
          described.forEach { writer.writeLine(it) }
          writer.newLine()
        }

        writer.writeLine("=======================")
        writer.writeLine(locale.logTokenRegistry)

        val describedRegistry = BriefRegistryDescriber.describe(locale, registry)
        describedRegistry.forEach { writer.writeLine(it) }

        writer.writeLine("=======================")
        writer.writeLine(locale.logTokenEnchantments)

        writer.writeLine("${locale.logTokenEnchantmentsLoaded}: ${registry.enchantments.size}")
        for (ench in registry.enchantments.keys.filterIsInstance<UnderscoreEnchantment>()) {
          val described = ench.getDescriber().describe(locale, ench)
          described.forEach { writer.writeLine(ChatColor.stripColor(it)!!) }
          writer.newLine()
        }
      }

      locale.logCreated.replace("<location>", file.absolutePath).apply {
        sender.sendMessage(this)
        plugin.ueLogger.commit { info(ChatColor.stripColor(this@apply)) }
      }
    } catch (ex: Exception) {
      sender.sendMessage(locale.logNotCreated)
      ex.printStackTrace()
    }
  }

  internal fun BufferedWriter.writeLine(text: String) {
    write(text)
    newLine()
  }
}
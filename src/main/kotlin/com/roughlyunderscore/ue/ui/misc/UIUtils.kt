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

package com.roughlyunderscore.ue.ui.misc

import com.roughlyunderscore.annotations.Since
import com.roughlyunderscore.annotations.Stable
import com.roughlyunderscore.ue.UnderscoreEnchantsPlugin
import com.roughlyunderscore.data.server.BackendEnchantment
import com.roughlyunderscore.data.server.BackendEnchantmentPack
import com.roughlyunderscore.data.server.BackendLocale
import com.roughlyunderscore.enums.LoadResponse
import com.roughlyunderscore.ue.utils.asComponent
import com.roughlyunderscore.ue.utils.getLocale
import com.roughlyunderscore.ulib.text.formatColor
import dev.triumphteam.gui.builder.item.ItemBuilder
import dev.triumphteam.gui.components.util.GuiFiller
import dev.triumphteam.gui.guis.BaseGui
import dev.triumphteam.gui.guis.Gui
import dev.triumphteam.gui.guis.GuiItem
import dev.triumphteam.gui.guis.PaginatedGui
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object UIUtils {
  @Since("2.2")
  @Stable
  fun downloadPack(downloader: CommandSender, pack: BackendEnchantmentPack, plugin: UnderscoreEnchantsPlugin) {
    val locale = downloader.getLocale(plugin)

    GlobalScope.launch {
      val result = plugin.repository.downloadPack(pack.identifier)
      downloader.sendMessage((if (result != null) locale.downloadedPack else locale.failedToDownloadPack).replace("<pack>", pack.metadata.name))
    }
  }

  @Since("2.2")
  @Stable
  fun downloadEnchantment(downloader: CommandSender, ench: BackendEnchantment, plugin: UnderscoreEnchantsPlugin) {
    val locale = downloader.getLocale(plugin)

    GlobalScope.launch {
      val result = plugin.repository.downloadEnchantment(ench.identifier)
      downloader.sendMessage((if (result != null) locale.downloadedEnchantment else locale.failedToDownloadEnchantment).replace("<enchantment>", ench.name))
    }
  }

  @Since("2.2")
  @Stable
  fun downloadLocale(downloader: CommandSender, locale: BackendLocale, plugin: UnderscoreEnchantsPlugin) {
    val playerLocale = downloader.getLocale(plugin)

    GlobalScope.launch {
      val result = plugin.repository.downloadLocale(locale.meta.id)
      downloader.sendMessage((if (result != null) playerLocale.downloadedLocale else playerLocale.failedToDownloadLocale).replace("<locale>", locale.locale.localeIdentifier))
    }
  }

  @Since("2.2")
  @Stable
  fun downloadAndLoadPack(downloader: CommandSender, pack: BackendEnchantmentPack, plugin: UnderscoreEnchantsPlugin) {
    val locale = downloader.getLocale(plugin)
    val name = pack.metadata.name

    GlobalScope.launch {
      val result = plugin.repository.downloadPack(pack.identifier) ?: run {
        downloader.sendMessage(locale.failedToLoadPack.replace("<pack>", name))
        return@launch
      }

      val loadResponse = plugin.loader.loadPack(result.second)
      if (loadResponse != LoadResponse.LOADED) downloader.sendMessage(locale.failedToLoadPack.replace("<en>", name))
      else downloader.sendMessage(locale.loadedPack.replace("<pack>", name).replace("<amount>", pack.enchantments.size.toString()))
    }
  }

  @Since("2.2")
  @Stable
  fun downloadAndLoadEnchantment(downloader: CommandSender, ench: BackendEnchantment, plugin: UnderscoreEnchantsPlugin) {
    val locale = downloader.getLocale(plugin)
    val name = ench.name

    GlobalScope.launch {
      plugin.repository.downloadEnchantment(ench.identifier) ?: run {
        downloader.sendMessage(locale.failedToLoadEnchantment.replace("<enchantment>", name))
        return@launch
      }

      val loadResponse = plugin.loader.loadEnchantment(name)
      if (loadResponse != LoadResponse.LOADED) downloader.sendMessage(locale.failedToLoadEnchantment.replace("<enchantment>", name))
      else downloader.sendMessage(locale.loadedEnchantment.replace("<enchantment>", name))
    }
  }

  @Since("2.2")
  @Stable
  fun downloadAndLoadLocale(downloader: CommandSender, locale: BackendLocale, plugin: UnderscoreEnchantsPlugin) {
    val playerLocale = downloader.getLocale(plugin)
    val name = locale.locale.localeIdentifier

    GlobalScope.launch {
      val result = plugin.repository.downloadLocale(locale.meta.id) ?: run {
        downloader.sendMessage(playerLocale.failedToLoadLocale.replace("<locale>", name))
        return@launch
      }

      val loadResponse = plugin.loader.loadLocale(result.second)
      if (loadResponse != LoadResponse.LOADED) downloader.sendMessage(playerLocale.failedToLoadLocale.replace("<locale>", name))
      else downloader.sendMessage(playerLocale.loadedLocale.replace("<locale>", name))
    }
  }

  /**
   * A shorthand for [ItemBuilder.from].
   */
  @Since("2.2")
  @Stable
  fun builder(material: Material) = ItemBuilder.from(material)

  /**
   * A shorthand for creating a 6 row [PaginatedGui] with a [title], applying a [block]
   * and opening it to a [player].
   */
  @Since("2.2")
  @Stable
  fun paginated(title: String, player: Player, block: PaginatedGui.() -> Unit) =
    Gui.paginated().rows(6).title(title.asComponent()).disableAllInteractions().create().apply(block).open(player)

  /**
   * A shorthand for creating a 6 row [Gui] with a [title], applying a [block]
   * and opening it to a [player].
   */
  @Since("2.2")
  @Stable
  fun gui(title: String, player: Player, block: Gui.() -> Unit) =
    Gui.gui().rows(6).title(title.asComponent()).disableAllInteractions().create().apply(block).open(player)
}

/**
 * A shorthand for [PaginatedGui.setItem].
 */
@Since("2.2")
@Stable
operator fun BaseGui.set(row: Int, column: Int, value: GuiItem) = setItem(row, column, value)

/**
 * A shorthand for [GuiFiller.fillBetweenPoints].
 */
@Since("2.2")
@Stable
operator fun GuiFiller.set(fromRow: Int, fromCol: Int, toRow: Int, toCol: Int, value: GuiItem) = fillBetweenPoints(fromRow, fromCol, toRow, toCol, value)

/**
 * A shorthand for [ItemBuilder.setName]. Avoids deprecation warnings.
 */
@Since("2.2")
@Stable
fun ItemBuilder.name(name: String) = setName(name.formatColor())

/**
 * A shorthand for [ItemBuilder.setLore]. Avoids deprecation warnings.
 */
@Since("2.2")
@Stable
fun ItemBuilder.lore(vararg lore: String) = setLore(lore.map { it.formatColor() })
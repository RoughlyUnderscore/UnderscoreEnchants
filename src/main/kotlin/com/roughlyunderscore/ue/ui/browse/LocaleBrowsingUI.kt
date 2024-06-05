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

package com.roughlyunderscore.ue.ui.browse

import com.roughlyunderscore.data.UELocale
import com.roughlyunderscore.data.server.BackendLocale
import com.roughlyunderscore.ue.UnderscoreEnchantsPlugin
import com.roughlyunderscore.ue.registry.RegistryImpl
import com.roughlyunderscore.ue.ui.misc.*
import com.roughlyunderscore.ue.ui.misc.UIUtils.builder
import com.roughlyunderscore.ue.ui.misc.UIUtils.paginated
import com.roughlyunderscore.ue.utils.getLocale
import dev.triumphteam.gui.guis.GuiItem
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag

class LocaleBrowsingUI(private val plugin: UnderscoreEnchantsPlugin) : BrowsingUI<BackendLocale>() {
  val registry = plugin.registry
  override var sorting = SortingType.BY_DOWNLOADS
  private var showDownloaded = false

  override fun fetchDynamicTitle(player: Player) = player.getLocale(plugin).uiLocaleBrowsingTitle
  override suspend fun fetchContent() = plugin.repository.getLocales()

  override fun prepareGui(player: Player, content: List<BackendLocale>): Unit = paginated(fetchDynamicTitle(player), player) {
    val locale = player.getLocale(plugin)

    filler[6, 1, 6, 9] = GuiItem(Material.GRAY_STAINED_GLASS_PANE)
    this[6, 3] = builder(Material.ARROW).name(locale.uiPrevious).asGuiItem { previous() }
    this[6, 5] = builder(Material.BARRIER).name(locale.uiClose).asGuiItem { close(player) }
    this[6, 7] = builder(Material.ARROW).name(locale.uiNext).asGuiItem { next() }

    this[6, 1] = builder(Material.OAK_SIGN).lore(
      (if (sorting == SortingType.BY_DOWNLOADS) "&a" else "&r") + locale.uiBrowsingSortByDownloads,
      (if (sorting == SortingType.BY_NAME) "&a" else "&r") + locale.uiBrowsingSortByName,
    ).asGuiItem {
      player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.MASTER, 1f, 2f)
      cycleSortingType()
      prepareGui(player, content)
    }

    this[6, 9] = builder(Material.LADDER).name(
      locale.uiLocaleBrowsingShowDownloaded.replace("<state>", if (showDownloaded) locale.stateOn else locale.stateOff)
    ).asGuiItem {
      player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.MASTER, 1f, 2f)
      showDownloaded = !showDownloaded
      prepareGui(player, content)
    }

    val sortedLocales = when (sorting) {
      SortingType.BY_DOWNLOADS -> content.sortedByDescending { it.meta.downloadedTimes }
      else -> content.sortedBy { it.locale.localeIdentifier }
    }.filter {
      // If we are allowed to show downloaded, we are allowed to show any
      // But if we are not allowed to show downloaded, we are only allowed to
      // show those that are not present
      // Hence, if showDownloaded is true, the entire expression is true
      // Otherwise, the expression is only true if there is no locale with this
      // identifier in the plugin
      showDownloaded || plugin.getLocale(it.locale.localeIdentifier) == null
    }

    for (loc in sortedLocales) {
      addItem(createItem(player, locale, loc))
    }
  }

  override fun cycleSortingType() = when (sorting) {
    SortingType.BY_DOWNLOADS -> sorting = SortingType.BY_NAME
    else -> sorting = SortingType.BY_DOWNLOADS
  }

  private fun createItem(player: Player, playerLocale: UELocale, targetLocale: BackendLocale) = builder(Material.PAPER)
    .name("&e&l${targetLocale.locale.localeIdentifier}")
    .lore(
      playerLocale.uiBrowsingDownloaded.replace("<amount>", targetLocale.meta.downloadedTimes.toString()),
      "",
      playerLocale.uiBrowsingClick,
      playerLocale.uiBrowsingShiftClick,
    )
    .flags(*ItemFlag.entries.toTypedArray())
    .asGuiItem {
      // Download & load
      if (it.isShiftClick) UIUtils.downloadAndLoadLocale(player, targetLocale, plugin)

      // Download
      else UIUtils.downloadLocale(player, targetLocale, plugin)
    }
}
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
import com.roughlyunderscore.data.server.BackendEnchantment
import com.roughlyunderscore.ue.UnderscoreEnchantsPlugin
import com.roughlyunderscore.ue.registry.RegistryImpl
import com.roughlyunderscore.ue.ui.misc.*
import com.roughlyunderscore.ue.ui.misc.UIUtils.builder
import com.roughlyunderscore.ue.ui.misc.UIUtils.paginated
import com.roughlyunderscore.ue.ui.preview.EnchantmentPreviewUI
import com.roughlyunderscore.ue.utils.getLocale
import dev.triumphteam.gui.guis.GuiItem
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag

class EnchantmentBrowsingUI(private val plugin: UnderscoreEnchantsPlugin) : BrowsingUI<BackendEnchantment>() {
  val registry = plugin.registry

  override var sorting = SortingType.BY_DOWNLOADS
  var showDownloaded = false
  override fun fetchDynamicTitle(player: Player) = player.getLocale(plugin).uiEnchantmentBrowsingTitle
  override suspend fun fetchContent() = plugin.repository.getEnchantments()

  override fun prepareGui(player: Player, content: List<BackendEnchantment>): Unit = paginated(fetchDynamicTitle(player), player) {
    val locale = player.getLocale(plugin)

    filler[6, 1, 6, 9] = GuiItem(Material.GRAY_STAINED_GLASS_PANE)
    this[6, 3] = builder(Material.ARROW).name(locale.uiPrevious).asGuiItem { previous() }
    this[6, 5] = builder(Material.BARRIER).name(locale.uiClose).asGuiItem { close(player) }
    this[6, 7] = builder(Material.ARROW).name(locale.uiNext).asGuiItem { next() }

    this[6, 1] = builder(Material.OAK_SIGN).lore(
      (if (sorting == SortingType.BY_DOWNLOADS) "&a" else "&r") + locale.uiBrowsingSortByDownloads,
      (if (sorting == SortingType.BY_NAME) "&a" else "&r") + locale.uiBrowsingSortByName,
      (if (sorting == SortingType.BY_AUTHOR) "&a" else "&r") + locale.uiBrowsingSortByAuthor
    ).asGuiItem {
      player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.MASTER, 1f, 2f)
      cycleSortingType()
      prepareGui(player, content)
    }

    this[6, 9] = builder(Material.LADDER).name(
      locale.uiEnchantmentBrowsingShowDownloaded.replace("<state>", if (showDownloaded) locale.stateOn else locale.stateOff)
    ).asGuiItem {
      player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.MASTER, 1f, 2f)
      showDownloaded = !showDownloaded
      prepareGui(player, content)
    }

    val sortedEnchs = when (sorting) {
      SortingType.BY_DOWNLOADS -> content.sortedByDescending { it.meta.downloadedTimes }
      SortingType.BY_NAME -> content.sortedBy { it.name }
      else -> content.sortedBy { it.author }
    }.filter {
      showDownloaded || registry.findEnchantment(it.name) == null
    }

    for (ench in sortedEnchs) {
      addItem(createItem(player, locale, ench, content))
    }
  }

  override fun cycleSortingType() = when (sorting) {
    SortingType.BY_DOWNLOADS -> sorting = SortingType.BY_NAME
    SortingType.BY_NAME -> sorting = SortingType.BY_AUTHOR
    else -> sorting = SortingType.BY_DOWNLOADS
  }

  private fun createItem(player: Player, locale: UELocale, ench: BackendEnchantment, enchs: List<BackendEnchantment>) = builder(ench.material)
    .name("&e&l${ench.name}")
    .lore(
      locale.uiEnchantmentBrowsingEnchantmentAuthor.replace("<author>", ench.author),
      locale.uiBrowsingDownloaded.replace("<amount>", ench.meta.downloadedTimes.toString()),
      "",
      locale.uiBrowsingClick,
      locale.uiBrowsingShiftClick,
      locale.uiBrowsingRightClick
    )
    .flags(*ItemFlag.entries.toTypedArray())
    .asGuiItem {
      // Open preview
      if (it.isRightClick) EnchantmentPreviewUI(plugin, ench, enchs, sorting, showDownloaded).createAndOpen(player)

      // Download & load
      else if (it.isShiftClick) UIUtils.downloadAndLoadEnchantment(player, ench, plugin)

      // Download
      else UIUtils.downloadEnchantment(player, ench, plugin)
    }
}
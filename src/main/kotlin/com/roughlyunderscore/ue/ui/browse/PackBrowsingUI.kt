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
import com.roughlyunderscore.data.server.BackendEnchantmentPack
import com.roughlyunderscore.ue.UnderscoreEnchantsPlugin
import com.roughlyunderscore.ue.registry.RegistryImpl
import com.roughlyunderscore.ue.ui.misc.*
import com.roughlyunderscore.ue.ui.misc.UIUtils.builder
import com.roughlyunderscore.ue.ui.misc.UIUtils.paginated
import com.roughlyunderscore.ue.ui.preview.PackPreviewUI
import com.roughlyunderscore.ue.utils.getLocale
import com.roughlyunderscore.ulib.text.joinAndWrap
import dev.triumphteam.gui.guis.GuiItem
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag

class PackBrowsingUI(private val plugin: UnderscoreEnchantsPlugin) : BrowsingUI<BackendEnchantmentPack>() {
  val registry = plugin.registry

  override var sorting = SortingType.BY_DOWNLOADS
  var showDownloaded = false

  override fun fetchDynamicTitle(player: Player) = player.getLocale(plugin).uiPackBrowsingTitle
  override suspend fun fetchContent() = plugin.repository.getPacks()

  override fun prepareGui(player: Player, content: List<BackendEnchantmentPack>): Unit = paginated(fetchDynamicTitle(player), player) {
    val locale = player.getLocale(plugin)

    filler[6, 1, 6, 9] = GuiItem(Material.GRAY_STAINED_GLASS_PANE)
    this[6, 3] = builder(Material.ARROW).name(locale.uiPrevious).asGuiItem { previous() }
    this[6, 5] = builder(Material.BARRIER).name(locale.uiClose).asGuiItem { close(player) }
    this[6, 7] = builder(Material.ARROW).name(locale.uiNext).asGuiItem { next() }

    this[6, 1] = builder(Material.OAK_SIGN).lore(
        (if (sorting == SortingType.BY_DOWNLOADS) "&a" else "&r") + locale.uiBrowsingSortByDownloads,
        (if (sorting == SortingType.BY_NAME) "&a" else "&r") + locale.uiBrowsingSortByName,
        (if (sorting == SortingType.BY_AUTHOR) "&a" else "&r") + locale.uiBrowsingSortByAuthor,
        (if (sorting == SortingType.BY_AMOUNT) "&a" else "&r") + locale.uiBrowsingSortByAmount
    ).asGuiItem {
      player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.MASTER, 1f, 2f)
      cycleSortingType()
      prepareGui(player, content)
    }

    this[6, 9] = builder(Material.LADDER).name(
      locale.uiPackBrowsingShowDownloaded.replace("<state>", if (showDownloaded) locale.stateOn else locale.stateOff)
    ).asGuiItem {
      player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.MASTER, 1f, 2f)
      showDownloaded = !showDownloaded
      prepareGui(player, content)
    }

    val sortedPacks = when (sorting) {
      SortingType.BY_DOWNLOADS -> content.sortedByDescending { it.metadata.meta.downloadedTimes }
      SortingType.BY_NAME -> content.sortedBy { it.metadata.name }
      SortingType.BY_AUTHOR -> content.sortedBy { it.metadata.authors.joinAndWrap("", ",", "") }
      SortingType.BY_AMOUNT -> content.sortedByDescending { it.enchantments.size }
    }.filter {
      showDownloaded || !registry.packs.containsKey(it.metadata.name)
    }

    for (pack in sortedPacks) {
      addItem(createItem(player, locale, pack, content))
    }
  }

  override fun cycleSortingType() = when (sorting) {
    SortingType.BY_DOWNLOADS -> sorting = SortingType.BY_NAME
    SortingType.BY_NAME -> sorting = SortingType.BY_AUTHOR
    SortingType.BY_AUTHOR -> sorting = SortingType.BY_AMOUNT
    SortingType.BY_AMOUNT -> sorting = SortingType.BY_DOWNLOADS
  }

  private fun createItem(player: Player, locale: UELocale, pack: BackendEnchantmentPack, packs: List<BackendEnchantmentPack>) = builder(pack.metadata.material)
    .name("&e&l${pack.metadata.name} &8(&7v${pack.metadata.version}&8)")
    .lore(*buildList {
      add(locale.uiPackBrowsingPackAuthor.replace("<author>", pack.metadata.authors.joinAndWrap("", ", ", "")))
      add(locale.uiPackBrowsingPackAmount.replace("<amount>", pack.enchantments.size.toString()))
      add(locale.uiBrowsingDownloaded.replace("<amount>", pack.metadata.meta.downloadedTimes.toString()))
      add(locale.uiPackBrowsingPackWebsite.replace("<website>", pack.metadata.website))
      add("")
      add(locale.uiBrowsingClick)
      add(locale.uiBrowsingShiftClick)
      add(locale.uiBrowsingRightClick)

      if (pack.enchantments.map { it.name }.any { plugin.registry.findEnchantment(it) != null }) {
        add("")
        add(locale.uiPackBrowsingHasConflictsFirstLine)
        add(locale.uiPackBrowsingHasConflictsSecondLine)
      }
    }.toTypedArray())
    .flags(*ItemFlag.entries.toTypedArray())
    .asGuiItem {
      // Open preview
      if (it.isRightClick) PackPreviewUI(plugin, pack, packs, sorting, showDownloaded).createAndOpen(player)

      // Download & load
      else if (it.isShiftClick) UIUtils.downloadAndLoadPack(player, pack, plugin)

      // Download
      else UIUtils.downloadPack(player, pack, plugin)
    }
}
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

package com.roughlyunderscore.ue.ui.preview

import com.roughlyunderscore.data.server.BackendEnchantmentPack
import com.roughlyunderscore.ue.UnderscoreEnchantsPlugin
import com.roughlyunderscore.ue.ui.misc.UIUtils.builder
import com.roughlyunderscore.ue.ui.misc.UIUtils.gui
import com.roughlyunderscore.ue.ui.browse.PackBrowsingUI
import com.roughlyunderscore.ue.ui.misc.*
import com.roughlyunderscore.ue.utils.getLocale
import com.roughlyunderscore.ulib.text.formatColor
import dev.triumphteam.gui.guis.GuiItem
import org.bukkit.Material
import org.bukkit.entity.Player

class PackPreviewUI(
  private val plugin: UnderscoreEnchantsPlugin,
  private val pack: BackendEnchantmentPack,
  private val packs: List<BackendEnchantmentPack>,
  private val sorting: SortingType,
  private val showDownloaded: Boolean
) {
  fun createAndOpen(player: Player) = gui(pack.metadata.name, player) {
    val locale = player.getLocale(plugin)
    val metadata = pack.metadata

    filler[1, 1, 6, 9] = GuiItem(Material.GRAY_STAINED_GLASS_PANE)

    val (enchantments, leftover) =
      if (pack.enchantments.size <= plugin.configuration.ui.truncatePackDataAfterXEnchantments) (pack.enchantments to 0)
      else pack.enchantments.let {
        val toDrop = it.size - plugin.configuration.ui.truncatePackDataAfterXEnchantments
        (it.dropLast(toDrop) to toDrop)
      }

    this[1, 1] = builder(Material.BARRIER).name(locale.uiPrevious).asGuiItem {
      PackBrowsingUI(plugin).apply pack@ {
        this@pack.sorting = this@PackPreviewUI.sorting
        this@pack.showDownloaded = this@PackPreviewUI.showDownloaded
      }.prepareGui(player, packs)
    }

    this[3, 2] = builder(Material.OAK_SIGN).name(locale.uiPackBrowsingPackDescription).lore(*metadata.description.toTypedArray()).asGuiItem()

    if (metadata.worldBlacklist.isNotEmpty() || metadata.worldWhitelist.isNotEmpty())
      this[2, 5] = builder(Material.REDSTONE_BLOCK).lore(
        locale.uiPackBrowsingPackWhitelistsFirstLine,
        locale.uiPackBrowsingPackWhitelistsSecondLine
      ).asGuiItem()

    this[3, 8] = builder(Material.ENCHANTED_BOOK).name(locale.uiPackBrowsingPackEnchantments)
      .lore(*buildList {
        addAll(enchantments.map { "&7${it.name}" })
        if (leftover > 0) add(locale.uiPackBrowsingPackTruncated.replace("<amount>", leftover.toString()))
      }.toTypedArray())
      .asGuiItem()

    this[4, 4] = builder(Material.PAPER).name(locale.uiBrowsingDownload).asGuiItem { UIUtils.downloadPack(player, pack, plugin) }
    this[4, 6] = builder(Material.PAPER).name(locale.uiBrowsingLoad).asGuiItem { UIUtils.downloadAndLoadPack(player, pack, plugin) }
  }
}
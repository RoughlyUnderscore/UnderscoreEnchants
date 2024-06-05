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

import com.roughlyunderscore.data.server.BackendEnchantment
import com.roughlyunderscore.ue.UnderscoreEnchantsPlugin
import com.roughlyunderscore.ue.ui.misc.UIUtils.builder
import com.roughlyunderscore.ue.ui.misc.UIUtils.gui
import com.roughlyunderscore.ue.ui.browse.EnchantmentBrowsingUI
import com.roughlyunderscore.ue.ui.misc.*
import com.roughlyunderscore.ue.utils.getLocale
import com.roughlyunderscore.ulib.text.joinAndWrap
import com.roughlyunderscore.ulib.text.link
import dev.triumphteam.gui.guis.GuiItem
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player


class EnchantmentPreviewUI(
  private val plugin: UnderscoreEnchantsPlugin,
  private val ench: BackendEnchantment,
  private val enchs: List<BackendEnchantment>,
  private val sorting: SortingType,
  private val showDownloaded: Boolean
) {
  fun createAndOpen(player: Player) = gui(ench.name, player) {
    val locale = player.getLocale(plugin)

    filler[1, 1, 6, 9] = GuiItem(Material.GRAY_STAINED_GLASS_PANE)

    this[1, 1] = builder(Material.BARRIER).name(locale.uiPrevious).asGuiItem {
      EnchantmentBrowsingUI(plugin).apply ench@ {
        this@ench.sorting = this@EnchantmentPreviewUI.sorting
        this@ench.showDownloaded = this@EnchantmentPreviewUI.showDownloaded
      }.prepareGui(player, enchs)
    }

    if (ench.description.isNotEmpty() && ench.description.any { it.isNotBlank() })
      this[2, 2] = builder(Material.OAK_SIGN).name(locale.uiEnchantmentBrowsingDescription).lore(*ench.description.toTypedArray()).asGuiItem()

    if (ench.requiredPlugins.isNotEmpty()) {
      this[2, 5] = builder(Material.COAL_BLOCK)
        .name(locale.uiEnchantmentBrowsingRequiredPlugins)

        .lore(*buildList {
          ench.requiredPlugins.map {
            add(locale.uiEnchantmentBrowsingRequiredPlugin
              .replace("<plugin>", it.displayName)
              .replace("<installed>",
                if (Bukkit.getPluginManager().isPluginEnabled(it.pluginName)) locale.uiEnchantmentBrowsingRequiredPluginInstalled
                else locale.uiEnchantmentBrowsingRequiredPluginNotInstalled
              ))
          }

          if (ench.requiredPlugins.any { !Bukkit.getPluginManager().isPluginEnabled(it.pluginName) }) {
            add("")
            add(locale.uiEnchantmentBrowsingRequiredPluginClick)
            add("")
            add(locale.uiEnchantmentBrowsingRequiredPluginDisclaimerFirstLine)
            add(locale.uiEnchantmentBrowsingRequiredPluginDisclaimerSecondLine)
          }
        }.toTypedArray())

        .asGuiItem {
          for (required in ench.requiredPlugins) {
            player.spigot().sendMessage(locale.uiEnchantmentRequiredPluginChatSyntax.replace("<plugin>", required.displayName).replace("<link>", required.link)
              .link("", required.link)
            )
          }
        }
    }

    val cooldownString = when (ench.cooldown.seconds) {
      in 1..59 -> "${ench.cooldown.seconds} ${locale.uiEnchantmentPreviewSeconds}"
      in 60..3599 -> "${ench.cooldown.minutes}:${ench.cooldown.seconds} ${locale.uiEnchantmentPreviewMinutes}"
      in 3600..86400 -> "${ench.cooldown.hours}:${ench.cooldown.minutes}:${ench.cooldown.seconds} ${locale.uiEnchantmentPreviewHours}"
      else -> ">1 ${locale.uiEnchantmentPreviewDay}"
    }

    val description = buildList {
      add(locale.uiEnchantmentBrowsingChance.replace("<chance>", "${ench.activationChance}%"))
      add(locale.uiEnchantmentBrowsingTrigger.replace("<trigger>", ench.trigger))

      if (ench.conflicts.isNotEmpty())
        add(locale.uiEnchantmentBrowsingConflicts.replace("<conflicts>", ench.conflicts.joinAndWrap("", ",", "")))

      if (ench.cooldown.amount > 1)
        add(locale.uiEnchantmentBrowsingCooldown.replace("<cooldown>", cooldownString))

      if (ench.forbiddenMaterials.isNotEmpty())
        add(locale.uiEnchantmentBrowsingForbidden.replace("<forbidden>", ench.forbiddenMaterials.joinAndWrap("", ",", "")))

      add(locale.uiEnchantmentBrowsingUnique.replace("<unique>", if (ench.unique) locale.yes else locale.no))
      add(locale.uiEnchantmentBrowsingSeekers.replace("<seekers>", ench.seekers.joinAndWrap("", ",", "")))

      if (ench.requiredEnchantments.isNotEmpty())
        add(locale.uiEnchantmentBrowsingRequired.replace("<required>", ench.requiredEnchantments.map { it.name }.joinAndWrap("", ",", "")))
    }

    this[2, 8] = builder(Material.LEVER).name(ench.name).lore(*description.toTypedArray()).asGuiItem()
    this[4, 4] = builder(Material.PAPER).name(locale.uiBrowsingDownload).asGuiItem { UIUtils.downloadEnchantment(player, ench, plugin) }
    this[4, 6] = builder(Material.PAPER).name(locale.uiBrowsingLoad).asGuiItem { UIUtils.downloadAndLoadEnchantment(player, ench, plugin) }
  }
}
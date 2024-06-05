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

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CatchUnknown
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Optional
import co.aikar.commands.annotation.Subcommand
import co.aikar.commands.annotation.Syntax
import com.roughlyunderscore.annotations.Since
import com.roughlyunderscore.annotations.Stable
import com.roughlyunderscore.ue.UnderscoreEnchantsPlugin
import com.roughlyunderscore.ue.debug.LogCreator
import com.roughlyunderscore.ue.debug.RegistryDebugger
import com.roughlyunderscore.ue.data.Constants
import com.roughlyunderscore.ue.data.Permissions
import com.roughlyunderscore.enums.EnchantingRestriction
import com.roughlyunderscore.enums.ItemStackEnchantResponseType
import com.roughlyunderscore.ue.commands.CommandUtils.Debugger.debugEnchantment
import com.roughlyunderscore.ue.commands.CommandUtils.Debugger.debugRegistry
import com.roughlyunderscore.ue.commands.CommandUtils.Downloader.downloadEnchantment
import com.roughlyunderscore.ue.commands.CommandUtils.Downloader.downloadLocale
import com.roughlyunderscore.ue.commands.CommandUtils.Downloader.downloadPack
import com.roughlyunderscore.ue.commands.CommandUtils.Loader.loadEnchantment
import com.roughlyunderscore.ue.commands.CommandUtils.Loader.loadLocale
import com.roughlyunderscore.ue.commands.CommandUtils.Loader.loadPack
import com.roughlyunderscore.ue.commands.CommandUtils.Unloader.unloadEnchantment
import com.roughlyunderscore.ue.commands.CommandUtils.Unloader.unloadLocale
import com.roughlyunderscore.ue.commands.CommandUtils.Unloader.unloadPack
import com.roughlyunderscore.ue.registry.RegistryImpl
import com.roughlyunderscore.ue.ui.browse.EnchantmentBrowsingUI
import com.roughlyunderscore.ue.ui.browse.LocaleBrowsingUI
import com.roughlyunderscore.ue.ui.browse.PackBrowsingUI
import com.roughlyunderscore.ulib.text.clickable
import com.roughlyunderscore.ue.utils.*
import com.roughlyunderscore.ulib.logic.UVerification.verify
import com.roughlyunderscore.ulib.text.normalize
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

// This class will be redone when I write my own command framework
@Suppress("unused")
@CommandAlias("ue|underscoreenchants|uenchants|underscoree|uenchs|underscoreenchs")
class UnderscoreEnchantsCommand(private val plugin: UnderscoreEnchantsPlugin) : BaseCommand() {
  private val registry = plugin.registry

  private val registryDebugger = RegistryDebugger(plugin)
  private val logCreator = LogCreator(plugin)

  @Default
  @CatchUnknown
  @CommandCompletion("@general-completion")
  @Since("2.2")
  @Stable
  fun help(sender: CommandSender) {
    val locale = sender.getLocale(plugin)

    if (!sender.hasPermission(Permissions.HELP)) {
      sender.sendMessage(locale.noPermissions)
      return
    }

    sender.sendMessage(locale.commandHelpTitle, "")

    sender.spigot().sendMessage(locale.commandHelpDebug.clickable(locale.commandHover, "/ue debug"))
    sender.spigot().sendMessage(locale.commandHelpEnchant.clickable(locale.commandHover, "/ue enchant"))
    sender.spigot().sendMessage(locale.commandHelpToggle.clickable(locale.commandHover, "/ue toggle"))
    sender.spigot().sendMessage(locale.commandHelpDownload.clickable(locale.commandHover, "/ue download"))
    sender.spigot().sendMessage(locale.commandHelpHelp.clickable(locale.commandHover, "/ue help"))
    sender.spigot().sendMessage(locale.commandHelpLoad.clickable(locale.commandHover, "/ue load"))
    sender.spigot().sendMessage(locale.commandHelpUnload.clickable(locale.commandHover, "/ue unload"))
    sender.spigot().sendMessage(locale.commandHelpReload.clickable(locale.commandHover, "/ue reload"))
    sender.spigot().sendMessage(locale.commandHelpBrowsePacks.clickable(locale.commandHover, "/ue browsepacks"))
    sender.spigot().sendMessage(locale.commandHelpBrowseEnchs.clickable(locale.commandHover, "/ue browseenchs"))
    sender.spigot().sendMessage(locale.commandHelpBrowseLocales.clickable(locale.commandHover, "/ue browselocales"))
    sender.spigot().sendMessage(locale.commandHelpBrowse.clickable(locale.commandHover, "/ue browse"))

    sender.sendMessage("")
    sender.sendMessage(locale.commandHelpSyntaxDetails)
    sender.sendMessage(locale.commandHelpSyntaxExceptions)
    sender.sendMessage(locale.commandHoverHint)
  }

  @Subcommand("debug|deb|dbg")
  @CommandCompletion("@debug-completion @debug-arguments-completion")
  @Since("2.2")
  @Stable
  fun debug(sender: CommandSender, @Optional argumentString: String?) {
    val locale = sender.getLocale(plugin)

    if (!sender.hasPermission(Permissions.DEBUG)) {
      sender.sendMessage(locale.noPermissions)
      return
    }

    val arguments = argumentString?.split(" ") ?: emptyList()
    val action = arguments.getOrNull(0)

    if (action == null) {
      sender.sendMessage(locale.debugHelpTitle, "")

      sender.spigot().sendMessage(locale.debugHelpRegistry.clickable(locale.commandHover, "/ue debug registry"))
      sender.spigot().sendMessage(locale.debugHelpEnchantment.clickable(locale.commandHover, "/ue debug enchantment"))
      sender.spigot().sendMessage(locale.debugHelpLog.clickable(locale.commandHover,"/ue debug log"))

      return
    }

    when (action.normalize()) {
      "registry", "reg", "register" -> debugRegistry(arguments, locale, sender, registryDebugger)
      "log", "logfile", "makelog", "createlog" -> logCreator.createLogFile(locale, sender)
      "enchantment", "ench" -> this.debugEnchantment(arguments, sender, locale, registry)
    }
  }

  @Subcommand("enchant|ench|e")
  @CommandCompletion("@enchantments-completion @levels-completion @players-completion @true-false")
  @Syntax("<enchantment> <level> [player] [bypass-types]")
  @Since("2.2")
  @Stable
  fun enchant(sender: CommandSender, @Optional argumentString: String?) {
    val locale = sender.getLocale(plugin)

    val player = sender as? Player ?: run {
      sender.sendMessage(locale.notForConsole)
      return
    }

    val arguments = argumentString?.split(" ") ?: emptyList()

    val enchantment = arguments.getOrNull(0) ?: run {
      sender.sendMessage(locale.commandEnchantNoEnchantmentName)
      return
    }

    val levelString = arguments.getOrNull(1) ?: run {
      sender.sendMessage(locale.commandEnchantNoEnchantmentLevel)
      return
    }

    val level = levelString.toIntOrNull() ?: run {
      player.sendMessage(locale.invalidEnchantmentLevel.replace("<level>", levelString))
      return
    }

    val targetPlayer = arguments.getOrNull(2)?.let { Bukkit.getPlayerExact(it) } ?: player
    val bypass = arguments.getOrNull(3)?.toBoolean() ?: false
    val liftedRestrictions = if (bypass) listOf(EnchantingRestriction.UNAPPLICABLE_RESTRICTION, EnchantingRestriction.CONFLICT_RESTRICTION) else emptyList()

    val underscoreEnchantment = registry.findEnchantmentByKeyString(enchantment)
    val vanillaEnchantment = try {
      Registry.ENCHANTMENT.get(NamespacedKey.minecraft(enchantment.lowercase()))
    } catch (ex: IllegalArgumentException) {
      null
    }

    if (underscoreEnchantment == null && vanillaEnchantment == null) {
      player.sendMessage(locale.invalidEnchantmentName.replace("<enchantment>", enchantment))
      return
    }

    val targetPlayerIsSender = targetPlayer.uniqueId == player.uniqueId

    if (
      (targetPlayerIsSender && !player.hasPermission(Permissions.ENCHANT)) ||
      (!targetPlayerIsSender && !player.hasPermission(Permissions.ENCHANT_OTHER))
    ) {
      player.sendMessage(locale.noPermissions)
      return
    }

    val item = targetPlayer.inventory.itemInMainHand
    val finalItem = item.enchant(underscoreEnchantment?.key ?: vanillaEnchantment?.key ?: run {
      player.sendMessage(locale.invalidEnchantmentName.replace("<enchantment>", enchantment))
      return
    }, level, registry, liftedRestrictions)

    when (finalItem.type) {
      ItemStackEnchantResponseType.SUCCESS -> {
        targetPlayer.inventory.setItemInMainHand(finalItem.resultItem)
        player.sendMessage(
          (if (targetPlayerIsSender) locale.enchantedItem else locale.enchantedItemOther)
            .replace("<enchantment>", Constants.FRIENDLY_ENCHANT_NAMES[vanillaEnchantment?.key] ?: underscoreEnchantment?.aliases?.first() ?: "")
            .replace("<level>", levelString)
            .replace("<player>", targetPlayer.name))
      }

      ItemStackEnchantResponseType.CONFLICTS -> player.sendMessage(locale.enchantmentConflicts)
      ItemStackEnchantResponseType.HAS_UNIQUE -> player.sendMessage(locale.hasUniqueEnchantment)
      ItemStackEnchantResponseType.CANT_APPLY_UNIQUE -> player.sendMessage(locale.enchantmentUnique)

      ItemStackEnchantResponseType.LIMIT_EXCEEDED -> player.sendMessage(locale.overTheLimit
        .replace("<limit>", "${plugin.configuration.enchantments.limit}"))

      ItemStackEnchantResponseType.LEVEL_TOO_HIGH -> player.sendMessage(locale.enchantmentLevelOutOfBounds
        .replace("<level>", levelString)
        .replace("<min>", "${underscoreEnchantment?.levels?.first()?.level ?: vanillaEnchantment?.startLevel ?: -1}")
        .replace("<max>", "${underscoreEnchantment?.levels?.size ?: vanillaEnchantment?.maxLevel ?: -1}"))

      ItemStackEnchantResponseType.UNAPPLICABLE -> player.sendMessage(locale.commandEnchantCannotApply
        .replace("<enchantment>", Constants.FRIENDLY_ENCHANT_NAMES[vanillaEnchantment?.key] ?: underscoreEnchantment?.aliases?.first() ?: "")
      )
    }
  }

  @Subcommand("toggle|t")
  @CommandCompletion("@custom-enchantments-completion @players-completion @nothing")
  @Syntax("<enchantment> [player]")
  @Since("2.2")
  @Stable
  fun toggle(sender: CommandSender, @Optional argumentString: String?) {
    val senderLocale = sender.getLocale(plugin)

    val arguments = argumentString?.split(" ")

    val enchantment = arguments?.getOrNull(0) ?: run {
      sender.sendMessage(senderLocale.commandHelpToggle)
      return
    }

    val argument = arguments.getOrNull(1)
    var selfChange = argument == null

    val player =
      if (argument == null) sender as? Player ?: run {
        sender.sendMessage(senderLocale.notForConsole)
        return
      }
      else Bukkit.getPlayerExact(argument) ?: run {
        sender.sendMessage(senderLocale.playerNotFound.replace("<player>", argument))
        return
      }

    if (player.uniqueId == (sender as Player?)?.uniqueId) selfChange = true

    if (
      (argument == null && !player.hasPermission(Permissions.TOGGLE)) ||
      (argument != null && !sender.hasPermission(Permissions.TOGGLE_OTHER))
    ) {
      sender.sendMessage(senderLocale.noPermissions)
      return
    }

    val underscoreEnchantment = registry.findEnchantmentByKeyString(enchantment)
    val vanillaEnchantment = Registry.ENCHANTMENT.get(NamespacedKey.minecraft(enchantment))

    if (vanillaEnchantment != null) {
      sender.sendMessage(senderLocale.cantToggleVanilla)
      return
    }

    if (underscoreEnchantment == null) {
      sender.sendMessage(senderLocale.invalidEnchantmentName.replace("<enchantment>", enchantment))
      return
    }

    val key = underscoreEnchantment.key
    val name = underscoreEnchantment.aliases.first()

    if (selfChange) {
      val toggled = plugin.playerManager.toggle(player.uniqueId, key, true)
      sender.sendMessage(senderLocale.toggledEnchantment
        .replace("<enchantment>", name)
        .replace("<state>", if (toggled) senderLocale.stateOn else senderLocale.stateOff)
      )
    } else {
      val toggled = plugin.playerManager.toggle(player.uniqueId, key, false)
      sender.sendMessage(senderLocale.toggledEnchantmentOther
        .replace("<enchantment>", name)
        .replace("<state>", if (toggled) senderLocale.stateOn else senderLocale.stateOff)
        .replace("<player>", argument ?: "???")
      )
      return
    }
  }

  @Subcommand("locale|language|lang|l")
  @CommandCompletion("@locales-completion @players-completion")
  @Syntax("<locale> [player]")
  @Since("2.2")
  @Stable
  fun locale(sender: CommandSender, @Optional argumentString: String?) {
    val senderLocale = sender.getLocale(plugin)

    val arguments = argumentString?.split(" ")

    val localeName = arguments?.getOrNull(0) ?: run {
      sender.sendMessage(senderLocale.commandHelpLocale)
      return
    }

    val targetPlayerName = arguments.getOrNull(1)

    val selfChange = targetPlayerName == null

    if ((selfChange && !sender.hasPermission(Permissions.LOCALE)) || (!selfChange && !sender.hasPermission(Permissions.LOCALE_OTHER))) {
      sender.sendMessage(senderLocale.noPermissions)
      return
    }

    val player =
      if (targetPlayerName == null) sender as? Player ?: run {
        sender.sendMessage(senderLocale.notForConsole)
        return
      }
      else Bukkit.getPlayerExact(targetPlayerName) ?: run {
        sender.sendMessage(senderLocale.playerNotFound.replace("<player>", targetPlayerName))
        return
      }

    if (!plugin.configuration.settings.playersCanChangeLocales) {
      sender.sendMessage(senderLocale.cantChangeLocale)
      return
    }

    val newLocale = plugin.locales.getLocaleStrict(localeName) ?: run {
      sender.sendMessage(senderLocale.noSuchLocale.replace("<locale>", localeName))
      return
    }

    plugin.storage.setLocale(player.uniqueId, newLocale.localeIdentifier, plugin)

    if (selfChange) {
      sender.sendMessage(newLocale.changedLocale)
    } else {
      sender.sendMessage(senderLocale.changedLocaleOther.replace("<player>", targetPlayerName ?: "???"))
      player.sendMessage(newLocale.changedLocaleExternally)
    }
  }

  @Subcommand("reload|rl|r")
  @Since("2.2")
  @Stable
  fun reload(sender: CommandSender) {
    val locale = sender.getLocale(plugin)

    if (!sender.hasPermission(Permissions.RELOAD)) {
      sender.sendMessage(locale.noPermissions)
      return
    }

    sender.sendMessage(locale.reloaded.replace("<ms>", "${plugin.reloadPlugin().millis}"))
  }

  @Subcommand("browsepacks|bp|brp")
  @Since("2.2")
  @Stable
  fun browsePacks(sender: CommandSender) {
    val locale = sender.getLocale(plugin)

    if (sender !is Player) {
      sender.sendMessage(locale.notForConsole)
      return
    }

    if (!sender.hasPermission(Permissions.BROWSE_PACKS)) {
      sender.sendMessage(locale.noPermissions)
      return
    }

    PackBrowsingUI(plugin).createAndOpen(sender)
  }

  @Subcommand("browseenchs|be|browseenchantments|browseenchants|bre")
  @Since("2.2")
  @Stable
  fun browseEnchantments(sender: CommandSender) {
    val locale = sender.getLocale(plugin)

    if (sender !is Player) {
      sender.sendMessage(locale.notForConsole)
      return
    }

    if (!sender.hasPermission(Permissions.BROWSE_ENCHANTMENTS)) {
      sender.sendMessage(locale.noPermissions)
      return
    }

    EnchantmentBrowsingUI(plugin).createAndOpen(sender)
  }

  @Subcommand("browselocales|bl|brl")
  @Since("2.2")
  @Stable
  fun browseLocales(sender: CommandSender) {
    val locale = sender.getLocale(plugin)

    if (sender !is Player) {
      sender.sendMessage(locale.notForConsole)
      return
    }

    if (!sender.hasPermission(Permissions.BROWSE_LOCALES)) {
      sender.sendMessage(locale.noPermissions)
      return
    }

    LocaleBrowsingUI(plugin).createAndOpen(sender)
  }

  @Subcommand("browse|br")
  @Since("2.2")
  @Stable
  fun browse(sender: CommandSender, @Optional argumentString: String?) {
    val locale = sender.getLocale(plugin)

    if (sender !is Player) {
      sender.sendMessage(locale.notForConsole)
      return
    }

    val what = argumentString?.split(" ")?.getOrNull(0)?.lowercase()?.first() ?: run {
      sender.sendMessage(locale.commandHelpBrowse)
      return
    }

    when (what) {
      'e' -> verify<Player> {
        fail { sendMessage(locale.noPermissions) }
        success { EnchantmentBrowsingUI(plugin).createAndOpen(this) }
        ensure { hasPermission(Permissions.BROWSE_ENCHANTMENTS) }
      }.verify(sender)

      'p' -> verify<Player> {
        fail { sendMessage(locale.noPermissions) }
        success { PackBrowsingUI(plugin).createAndOpen(this) }
        ensure { hasPermission(Permissions.BROWSE_PACKS )}
      }.verify(sender)

      'l' -> verify<Player> {
        fail { sendMessage(locale.noPermissions) }
        success { LocaleBrowsingUI(plugin).createAndOpen(this) }
        ensure { hasPermission(Permissions.BROWSE_LOCALES) }
      }.verify(sender)

      else -> sender.sendMessage(locale.commandHelpBrowse)
    }
  }

  @Subcommand("download|do|down|dnl|dw")
  @CommandCompletion("@types-completion @nothing @true-false")
  @Since("2.2")
  @Stable
  fun download(sender: CommandSender, @Optional argumentString: String?) {
    val locale = sender.getLocale(plugin)

    val argumentSplit = argumentString?.split(" ") ?: run {
      sender.sendMessage(locale.downloadHelpTitle)
      sender.sendMessage(locale.downloadHelpPack)
      sender.sendMessage(locale.downloadHelpEnchantment)
      sender.sendMessage(locale.downloadHelpLocale)
      return
    }

    val type = argumentSplit.getOrNull(0)
    val id = argumentSplit.getOrNull(1)?.toLongOrNull()
    val immediateLoad = argumentSplit.getOrNull(2) == "true"

    if (id == null) {
      sender.sendMessage(locale.downloadNoId)
      return
    }

    when (type?.get(0)?.lowercaseChar()) {
      'p' -> downloadPack(sender, locale, immediateLoad, id, registry, plugin)
      'e' -> downloadEnchantment(sender, locale, immediateLoad, id, registry, plugin)
      'l' -> downloadLocale(sender, locale, immediateLoad, id, registry, plugin)

      else -> sender.sendMessage(locale.downloadInvalidType)
    }
  }

  @Subcommand("load|lo")
  @CommandCompletion("@types-completion @content-files-completion")
  @Since("2.2")
  @Stable
  fun load(sender: CommandSender, @Optional argumentString: String?) {
    val locale = sender.getLocale(plugin)

    val argumentSplit = argumentString?.split(" ") ?: run {
      sender.sendMessage(locale.loadHelpTitle)
      sender.sendMessage(locale.loadHelpPack)
      sender.sendMessage(locale.loadHelpEnchantment)
      sender.sendMessage(locale.loadHelpLocale)
      return
    }

    val type = argumentSplit.getOrNull(0)
    val filename = argumentSplit.getOrNull(1)

    if (filename == null) {
      sender.sendMessage(locale.loadNoFilename)
      return
    }

    when (type?.get(0)?.lowercaseChar()) {
      'p' -> loadPack(sender, filename, locale, plugin)
      'e' -> loadEnchantment(sender, filename, locale, plugin)
      'l' -> loadLocale(sender, filename, locale, plugin)

      else -> sender.sendMessage(locale.loadInvalidType)
    }
  }

  @Subcommand("unload|unlo|unl|u|un|ul")
  @CommandCompletion("@types-completion @contextual-loaded-types-completion")
  @Since("2.2")
  @Stable
  fun unload(sender: CommandSender, @Optional argumentString: String?) {
    val locale = sender.getLocale(plugin)

    val argumentSplit = argumentString?.split(" ") ?: run {
      sender.sendMessage(locale.unloadHelpTitle)
      sender.sendMessage(locale.unloadHelpPack)
      sender.sendMessage(locale.unloadHelpEnchantment)
      sender.sendMessage(locale.unloadHelpLocale)
      return
    }

    if (argumentSplit.size < 2) {
      sender.sendMessage(locale.unloadNoName)
      return
    }

    val type = argumentSplit.getOrNull(0)
    val name = argumentSplit.drop(1).joinToString(" ")

    when (type?.get(0)?.lowercaseChar()) {
      'p' -> unloadPack(sender, name, locale, plugin)
      'e' -> unloadEnchantment(sender, name, locale, plugin)
      'l' -> unloadLocale(sender, name, locale, plugin)

      else -> sender.sendMessage(locale.unloadInvalidType)
    }
  }
}
package com.roughlyunderscore.ue.utils

import com.roughlyunderscore.ue.UnderscoreEnchantsPlugin
import com.roughlyunderscore.annotations.Since
import com.roughlyunderscore.annotations.Stable
import com.roughlyunderscore.data.UELocale
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * Finds a locale by an [identifier].
 * @return The locale if found, or the first locale in the list if not.
 */
@Since("2.2")
@Stable
fun List<UELocale>.getLocale(identifier: String): UELocale = this.firstOrNull { it.localeIdentifier == identifier } ?: this.first()

/**
 * Finds a locale by an [identifier].
 * @return The locale if found, or null if none is found.
 */
@Since("2.2")
@Stable
fun List<UELocale>.getLocaleStrict(identifier: String): UELocale? = this.firstOrNull { it.localeIdentifier == identifier }

/**
 * Gets a locale from a [CommandSender]: uses the global locale if the sender is Console,
 * or the sender's personal locale if it is a player.
 */
@Since("2.2")
@Stable
fun CommandSender.getLocale(plugin: UnderscoreEnchantsPlugin) =
  if (this is Player) plugin.storage.getLocale(this.uniqueId, plugin)
  else plugin.globalLocale
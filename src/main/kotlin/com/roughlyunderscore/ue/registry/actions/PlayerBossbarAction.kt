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

package com.roughlyunderscore.ue.registry.actions

import com.roughlyunderscore.ue.UnderscoreEnchantsPlugin
import com.roughlyunderscore.annotations.Since
import com.roughlyunderscore.annotations.Stable
import com.roughlyunderscore.enums.TargetType
import com.roughlyunderscore.registry.RegistrableAction
import com.roughlyunderscore.data.EventModifications
import com.roughlyunderscore.registry.RegistrableTrigger
import com.roughlyunderscore.ulib.text.formatColor
import com.roughlyunderscore.ue.utils.mapToDrt
import com.roughlyunderscore.ulib.data.safeValueOr
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.entity.Player
import org.bukkit.event.Event
import java.util.*

/**
 * Sends a message to the player via bossbar.
 *
 * Example:
 * - `bossbar yellow solid 160 You are DOOMED!`
 *
 * Syntax:
 * - `bossbar BARCOLOR BARSTYLE TICKS MESSAGE`
 * - BarColor can be found [here](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/boss/BarColor.html).
 * - BarStyle can be found [here](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/boss/BarStyle.html).
 * - Ticks is the amount of ticks the bossbar will be displayed for.
 */
@Since("2.2")
@Stable
class PlayerBossbarAction(private val plugin: UnderscoreEnchantsPlugin) : RegistrableAction {
  override val aliases = listOf("bossbar", "send-bossbar", "player-bossbar")

  override fun execute(event: Event, trigger: RegistrableTrigger, arguments: List<String>, target: TargetType): EventModifications? {
    if (arguments.size < 4) return null

    val color = safeValueOr(arguments[0].uppercase(), BarColor.YELLOW)!!
    val style = safeValueOr(arguments[1].uppercase(), BarStyle.SOLID)!!
    val ticks = arguments[2].toLongOrNull() ?: return null

    val trimmedArguments = arguments.toMutableList()
    for (i in 0..2) trimmedArguments.removeAt(0) // removes the first 3 arguments

    val method = trigger.getTriggerDataHolder().dataRetrievalMethods[target.mapToDrt()] ?: return null
    val player = method.invoke(event) as? Player ?: return null

    val key = NamespacedKey(plugin, "activation_${player.name}_${UUID.randomUUID()}")
    val bossbar = Bukkit.createBossBar(key, trimmedArguments.joinToString(separator = " ").formatColor(), color, style)
    bossbar.progress = 1.0
    bossbar.addPlayer(player)

    Bukkit.getScheduler().runTaskLater(plugin, Runnable {
      bossbar.removeAll()
      Bukkit.removeBossBar(key)
    }, ticks)

    return null
  }
}
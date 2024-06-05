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

import com.roughlyunderscore.annotations.Since
import com.roughlyunderscore.annotations.Stable
import com.roughlyunderscore.enums.TargetType
import com.roughlyunderscore.registry.RegistrableAction
import com.roughlyunderscore.data.EventModifications
import com.roughlyunderscore.registry.RegistrableTrigger
import com.roughlyunderscore.ulib.text.normalize
import com.roughlyunderscore.ue.utils.mapToDrt
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.Event
import kotlin.math.roundToInt

/**
 * Sets player's gamemode
 *
 * Example:
 * - `gamemode 0`
 *
 * Syntax:
 * - `gamemode GAMEMODE`
 * - The gamemode can be either a number or a string, for example `0` or `survival` (case-insensitive)
 */
@Since("2.2")
@Stable
class PlayerSetGamemodeAction : RegistrableAction {
  override val aliases = listOf("set-gamemode", "gamemode", "set-gm", "gm", "change-gamemode", "change-gm")

  override fun execute(event: Event, trigger: RegistrableTrigger, arguments: List<String>, target: TargetType): EventModifications? {
    if (arguments.isEmpty()) return null

    val gamemode = arguments[0]

    val method = trigger.getTriggerDataHolder().dataRetrievalMethods[target.mapToDrt()] ?: return null
    val player = method.invoke(event) as? Player ?: return null

    player.gameMode = when (gamemode.normalize()) {
      "0", "survival", "surv" -> GameMode.SURVIVAL
      "1", "creative", "c" -> GameMode.CREATIVE
      "2", "adventure", "a", "adv" -> GameMode.ADVENTURE
      "3", "spectator", "spec", "sp" -> GameMode.SPECTATOR
      else -> {
        val gamemodeInt = gamemode.toDoubleOrNull()?.roundToInt() ?: return null
        GameMode.getByValue(gamemodeInt) ?: return null
      }
    }

    return null
  }
}
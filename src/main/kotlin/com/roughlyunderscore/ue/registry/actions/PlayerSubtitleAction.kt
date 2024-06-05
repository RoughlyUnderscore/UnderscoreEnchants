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
import com.roughlyunderscore.ulib.text.formatColor
import com.roughlyunderscore.ue.utils.mapToDrt
import org.bukkit.entity.Player
import org.bukkit.event.Event
import kotlin.math.roundToInt

/**
 * Sends a message to the player via subtitle.
 *
 * Example:
 * - `subtitle 40 100 40 You are DOOMED!`
 *
 * Syntax:
 * - `subtitle FADEIN STAY FADEOUT MESSAGE`
 */
@Since("2.2")
@Stable
class PlayerSubtitleAction : RegistrableAction {
  override val aliases = listOf("subtitle", "send-subtitle")

  override fun execute(event: Event, trigger: RegistrableTrigger, arguments: List<String>, target: TargetType): EventModifications? {
    if (arguments.size < 4) return null

    val fadeIn = arguments[0].toDoubleOrNull()?.roundToInt() ?: return null
    val stay = arguments[1].toDoubleOrNull()?.roundToInt() ?: return null
    val fadeOut = arguments[2].toDoubleOrNull()?.roundToInt() ?: return null

    val trimmedArguments = arguments.toMutableList()
    for (i in 0..2) trimmedArguments.removeAt(0) // removes the first 3 arguments

    val method = trigger.getTriggerDataHolder().dataRetrievalMethods[target.mapToDrt()] ?: return null
    val player = method.invoke(event) as? Player ?: return null

    player.sendTitle("", trimmedArguments.joinToString(" ").formatColor(), fadeIn, stay, fadeOut)

    return null
  }
}
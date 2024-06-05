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

import com.cryptomorin.xseries.XSound
import com.roughlyunderscore.annotations.Since
import com.roughlyunderscore.annotations.Stable
import com.roughlyunderscore.enums.TargetType
import com.roughlyunderscore.registry.RegistrableAction
import com.roughlyunderscore.data.EventModifications
import com.roughlyunderscore.registry.RegistrableTrigger
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.event.Event

/**
 * Plays a sound at a location
 *
 * Example:
 * - `place-sound ENTITY_ENDERMAN_SCREAM 50 120 -801 world`
 *
 * Syntax:
 * - `place-sound SOUND X Y Z WORLD`
 * - Sounds can be found [here](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Sound.html)
 */
@Since("2.2")
@Stable
class LocationSoundAction : RegistrableAction {
  override val aliases = listOf("world-sound", "location-sound", "place-sound")

  override fun execute(event: Event, trigger: RegistrableTrigger, arguments: List<String>, target: TargetType): EventModifications? {
    if (arguments.size < 5) return null

    val sound = XSound.matchXSound(arguments[0])
    if (!sound.isPresent) return null

    val x = arguments[1].toDoubleOrNull() ?: return null
    val y = arguments[2].toDoubleOrNull() ?: return null
    val z = arguments[3].toDoubleOrNull() ?: return null
    val world = Bukkit.getWorld(arguments[4]) ?: return null

    val location = Location(world, x, y, z)
    sound.get().play(location)

    return null
  }
}

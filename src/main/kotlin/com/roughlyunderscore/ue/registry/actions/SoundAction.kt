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
import com.roughlyunderscore.ue.utils.mapToDrt
import org.bukkit.entity.Entity
import org.bukkit.event.Event

/**
 * Plays a sound to an entity
 *
 * Example:
 * - `play-sound ENTITY_ENDERMAN_SCREAM`
 *
 * Syntax:
 * - `play-sound SOUND`
 * - Sounds can be found [here](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Sound.html)
 */
@Since("2.2")
@Stable
class SoundAction : RegistrableAction {
  override val aliases = listOf("sound", "play-sound")

  override fun execute(event: Event, trigger: RegistrableTrigger, arguments: List<String>, target: TargetType): EventModifications? {
    if (arguments.isEmpty()) return null

    val sound = XSound.matchXSound(arguments[0])
    if (!sound.isPresent) return null

    val method = trigger.getTriggerDataHolder().dataRetrievalMethods[target.mapToDrt()] ?: return null
    val entity = method.invoke(event) as? Entity ?: return null

    sound.get().play(entity)

    return null
  }
}
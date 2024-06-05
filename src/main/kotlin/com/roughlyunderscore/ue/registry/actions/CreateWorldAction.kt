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
import com.roughlyunderscore.ulib.data.safeValue
import org.bukkit.Bukkit
import org.bukkit.WorldCreator
import org.bukkit.WorldType
import org.bukkit.event.Event

/**
 * Creates and loads a new world, or loads an existing one
 *
 * Example:
 * - `create-world AMPLIFIED -1 new_world`
 *
 * Syntax:
 * - `create-world WORLD-TYPE GENERATOR-SETTINGS NAME`
 * - No spaces are accepted within a world name, so if any spaces are present, only the first word of the string will be used.
 * - If you do not wish to use any generator settings, put `-1`.
 * - World types can be found [here](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/WorldType.html)
 */
@Since("2.2")
@Stable
class CreateWorldAction : RegistrableAction {
  override val aliases = listOf("create-world", "load-world")

  override fun execute(event: Event, trigger: RegistrableTrigger, arguments: List<String>, target: TargetType): EventModifications? {
    if (arguments.size < 3) return null

    val type = safeValue<WorldType>(arguments[0]) ?: WorldType.getByName(arguments[0]) ?: return null
    val generatorSettings = arguments[1]
    val name = arguments[2]

    val creator = WorldCreator(name).type(type)
    if (generatorSettings != "-1") creator.generatorSettings(generatorSettings)

    creator.createWorld()

    return null
  }
}

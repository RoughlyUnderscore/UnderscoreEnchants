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
import org.bukkit.Bukkit
import org.bukkit.event.Event

/**
 * Broadcasts the message server-wide
 *
 * Example:
 * - `broadcast The server enchantment system is powered by UnderscoreEnchants!`
 *
 * Syntax:
 * - `broadcast MESSAGE`
 */
@Since("2.2")
@Stable
class BroadcastMessageAction : RegistrableAction {
  override val aliases = listOf("broadcast", "broadcast-message", "servermessage", "allmessage")

  override fun execute(event: Event, trigger: RegistrableTrigger, arguments: List<String>, target: TargetType): EventModifications? {
    Bukkit.broadcastMessage(arguments.joinToString(separator = " ").formatColor())

    return null
  }
}
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

import com.roughlyunderscore.annotations.Beta
import com.roughlyunderscore.annotations.Since
import com.roughlyunderscore.annotations.Stable
import com.roughlyunderscore.enums.TargetType
import com.roughlyunderscore.registry.RegistrableAction
import com.roughlyunderscore.data.EventModifications
import com.roughlyunderscore.registry.RegistrableTrigger
import com.roughlyunderscore.ue.utils.mapToDrt
import org.bukkit.NamespacedKey
import org.bukkit.event.Event
import org.bukkit.persistence.PersistentDataHolder
import org.bukkit.persistence.PersistentDataType

/**
 * Adds a key-value to the entity's PDC
 *
 * Example:
 * - `set-pdc MyServerRank VIP`
 *
 * Syntax:
 * - `set-pdc KEY VALUE`
 * - Every value is a string.
 */
@Since("2.2")
@Stable
class SetPDCAction : RegistrableAction {
  override val aliases = listOf("pdc", "set-pdc")

  override fun execute(event: Event, trigger: RegistrableTrigger, arguments: List<String>, target: TargetType): EventModifications? {
    if (arguments.size < 2) return null

    val key = try {
      NamespacedKey.fromString(arguments[0]) ?: return null
    } catch (ex: IllegalArgumentException) {
      return null
    }

    val value = arguments[1]

    val method = trigger.getTriggerDataHolder().dataRetrievalMethods[target.mapToDrt()] ?: return null
    val entity = method.invoke(event) as? PersistentDataHolder ?: return null

    entity.persistentDataContainer.set(key, PersistentDataType.STRING, value)

    return null
  }
}
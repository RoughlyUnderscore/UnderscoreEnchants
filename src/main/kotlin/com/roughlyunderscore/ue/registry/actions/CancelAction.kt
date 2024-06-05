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
import org.bukkit.event.Cancellable
import org.bukkit.event.Event

/**
 * Cancels the event
 *
 * Syntax:
 * - `cancel`
 */
@Since("2.2")
@Stable
class CancelAction : RegistrableAction {
  override val aliases = listOf("cancel", "undo")

  override fun execute(event: Event, trigger: RegistrableTrigger, arguments: List<String>, target: TargetType): EventModifications? {
    if (event !is Cancellable) return null

    return EventModifications.Builder()
      .addMethodToCall(event.javaClass.getDeclaredMethod("setCancelled", Boolean::class.java), listOf(true))
      .build()
  }
}
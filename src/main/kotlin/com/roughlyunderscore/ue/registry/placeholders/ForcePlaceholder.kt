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

package com.roughlyunderscore.ue.registry.placeholders

import com.roughlyunderscore.annotations.Since
import com.roughlyunderscore.annotations.Stable
import com.roughlyunderscore.enums.TargetType
import com.roughlyunderscore.events.PlayerShootBowEvent
import com.roughlyunderscore.registry.RegistrablePlaceholder
import com.roughlyunderscore.registry.RegistrableTrigger
import com.roughlyunderscore.ue.utils.*
import org.bukkit.event.Event
import org.bukkit.event.entity.EntityShootBowEvent

@Since("2.2")
@Stable
class ForcePlaceholder : RegistrablePlaceholder {
  override val aliases = listOf(
    "force",
    "bow-force",
    "bowforce",
    "shoot-force",
    "shootforce"
  )

  override fun replacedText(
    event: Event,
    trigger: RegistrableTrigger,
    target: TargetType,
    args: Map<String, String>
  ): String? {
    // they do not have a common parent that shares `force` so we can't do one statement
    // (unless we get field `force` with reflection but that's annoying)
    if (event is EntityShootBowEvent) return event.force.toString()
    if (event is PlayerShootBowEvent) return event.force.toString()
    return null
  }
}
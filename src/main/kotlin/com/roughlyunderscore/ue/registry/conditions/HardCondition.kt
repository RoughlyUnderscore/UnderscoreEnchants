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

package com.roughlyunderscore.ue.registry.conditions

import com.roughlyunderscore.annotations.Since
import com.roughlyunderscore.annotations.Stable
import com.roughlyunderscore.enums.TargetType
import com.roughlyunderscore.registry.RegistrableCondition
import com.roughlyunderscore.registry.RegistrableTrigger
import com.roughlyunderscore.ue.utils.mapToDrt
import org.bukkit.Difficulty
import org.bukkit.entity.Entity
import org.bukkit.event.Event

@Since("2.2")
@Stable
class HardCondition : RegistrableCondition {
  override val aliases = listOf(
    "harddifficulty",
    "isharddifficulty",
    "harddiff",
    "isharddiff",
    "hard-difficulty",
    "is-hard-difficulty",
    "hard-diff",
    "is-hard-diff",
  )

  override fun evaluateCondition(trigger: RegistrableTrigger, event: Event, target: TargetType, arguments: List<String>): Boolean {
    val method = trigger.getTriggerDataHolder().dataRetrievalMethods[target.mapToDrt()] ?: return false
    val entity = method.invoke(event) as? Entity ?: return false
    return entity.world.difficulty == Difficulty.HARD
  }
}
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

package com.roughlyunderscore.ue.registry.triggers


import com.roughlyunderscore.annotations.Since
import com.roughlyunderscore.annotations.Stable
import com.roughlyunderscore.data.TriggerDataHolder
import com.roughlyunderscore.enums.DataRetrievalType
import com.roughlyunderscore.events.PlayerBowHitEvent
import com.roughlyunderscore.registry.RegistrableTrigger

@Since("2.2")
@Stable
class PlayerBowHitTrigger : RegistrableTrigger {
  override val aliases = listOf("bowhit", "bow", "hit", "bow-hit")

  override fun getTriggerDataHolder(): TriggerDataHolder = TriggerDataHolder.fromStringMethods(
    PlayerBowHitEvent::class.java,
    DataRetrievalType.FIRST_PLAYER to "getDamager",
    DataRetrievalType.SECOND_PLAYER to "getVictim",
    DataRetrievalType.DAMAGE to "getDamage",
  )
}
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

import com.cryptomorin.xseries.XMaterial
import com.roughlyunderscore.annotations.Since
import com.roughlyunderscore.annotations.Stable
import com.roughlyunderscore.enums.TargetType
import com.roughlyunderscore.registry.RegistrableCondition
import com.roughlyunderscore.registry.RegistrableTrigger
import org.bukkit.block.BlockFace
import org.bukkit.event.Event
import org.bukkit.event.player.PlayerMoveEvent
import kotlin.jvm.optionals.getOrNull

@Since("2.2")
@Stable
class ToIsCondition : RegistrableCondition {
  override val aliases = listOf(
    "tois",
    "to-is",
  )

  override fun evaluateCondition(trigger: RegistrableTrigger, event: Event, target: TargetType, arguments: List<String>): Boolean {
    if (arguments.isEmpty()) return false
    if (event !is PlayerMoveEvent) return false

    val toType = event.to?.block?.getRelative(BlockFace.DOWN)?.type ?: return false
    val type = XMaterial.matchXMaterial(arguments[0].uppercase()).getOrNull()?.parseMaterial() ?: return false
    return toType == type
  }
}
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
import com.jeff_media.armorequipevent.ArmorEquipEvent
import com.roughlyunderscore.annotations.Since
import com.roughlyunderscore.annotations.Stable
import com.roughlyunderscore.ue.data.Constants
import com.roughlyunderscore.enums.TargetType
import com.roughlyunderscore.registry.RegistrableCondition
import com.roughlyunderscore.registry.RegistrableTrigger
import com.roughlyunderscore.enums.DataRetrievalType
import org.bukkit.block.Block
import org.bukkit.event.Event
import kotlin.jvm.optionals.getOrNull

@Since("2.2")
@Stable
class BlockIsCondition : RegistrableCondition {
  override val aliases = listOf(
    "blockis",
    "block-is",
  )

  override fun evaluateCondition(trigger: RegistrableTrigger, event: Event, target: TargetType, arguments: List<String>): Boolean {
    if (arguments.isEmpty()) return false

    val method = trigger.getTriggerDataHolder().dataRetrievalMethods[DataRetrievalType.BLOCK] ?: return false
    val block = method.invoke(event) as? Block ?: return false

    val type = XMaterial.matchXMaterial(arguments[0].uppercase()).getOrNull()?.parseMaterial() ?: return false
    return block.type == type
  }
}
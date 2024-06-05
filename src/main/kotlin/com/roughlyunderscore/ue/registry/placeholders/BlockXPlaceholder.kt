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
import com.roughlyunderscore.registry.RegistrablePlaceholder
import com.roughlyunderscore.registry.RegistrableTrigger
import com.roughlyunderscore.enums.DataRetrievalType
import com.roughlyunderscore.ue.utils.*
import org.bukkit.block.Block
import org.bukkit.event.Event

@Since("2.2")
@Stable
class BlockXPlaceholder : RegistrablePlaceholder {
  override val aliases = listOf(
    "block-x",
    "blockx",
    "x-block",
    "xblock"
  )
  override fun replacedText(
    event: Event,
    trigger: RegistrableTrigger,
    target: TargetType,
    args: Map<String, String>
  ): String? {
    val method = trigger.getTriggerDataHolder().dataRetrievalMethods[DataRetrievalType.BLOCK] ?: return null
    val block = method.invoke(event) as? Block ?: return null
    return block.x.toString()
  }
}
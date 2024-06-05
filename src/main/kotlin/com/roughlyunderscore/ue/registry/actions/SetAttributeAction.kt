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
import com.roughlyunderscore.ue.utils.mapToDrt
import com.roughlyunderscore.ulib.data.safeValue
import com.roughlyunderscore.ulib.text.normalize
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.event.Event

/**
 * Sets an attribute of the target entity.
 *
 * Example:
 * - `attr generic_movement_speed 6`
 *
 * Syntax:
 * - `attr ATTRIBUTE VALUE`
 */
@Since("2.2")
@Stable
class SetAttributeAction : RegistrableAction {
  override val aliases = listOf("attribute", "set-attribute", "attr", "set-attr", "attribute-value", "attr-value", "set-attribute-value", "set-attr-value")

  override fun execute(event: Event, trigger: RegistrableTrigger, arguments: List<String>, target: TargetType): EventModifications? {
    if (arguments.size < 2) return null

    val attributeName = arguments[0]
    val attributeValue = arguments[1]

    val method = trigger.getTriggerDataHolder().dataRetrievalMethods[target.mapToDrt()] ?: return null
    val entity = method.invoke(event) as? LivingEntity ?: return null

    val attribute = safeValue<Attribute>(attributeName.uppercase()) ?: return null
    if (attributeValue.normalize().startsWith("def"))
      entity.getAttribute(attribute)?.baseValue = entity.getAttribute(attribute)?.defaultValue ?: return null
    else
      entity.getAttribute(attribute)?.baseValue = attributeValue.toDoubleOrNull() ?: return null

    return null
  }
}
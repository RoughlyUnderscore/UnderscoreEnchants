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
import com.roughlyunderscore.enums.DataRetrievalType
import com.roughlyunderscore.ulib.text.normalize
import org.bukkit.event.Event
import org.bukkit.event.entity.EntityDamageEvent.DamageCause

@Since("2.2")
@Stable
class CauseCondition : RegistrableCondition {
  override val aliases = listOf(
    "cause",
    "causeis",
    "damagecause",
    "damagecauseis",
    "cause-is",
    "damage-cause",
    "damage-cause-is",
  )

  override fun evaluateCondition(trigger: RegistrableTrigger, event: Event, target: TargetType, arguments: List<String>): Boolean {
    if (arguments.isEmpty()) return false

    val method = trigger.getTriggerDataHolder().dataRetrievalMethods[DataRetrievalType.DAMAGE_CAUSE] ?: return false
    val cause = method.invoke(event) as? DamageCause ?: return false

    return when (arguments[0].normalize()) {
      "blockexplosion", "blockexplode", "blockexploded" -> cause == DamageCause.BLOCK_EXPLOSION
      "contact", "contacted", "contacting", "hazard" -> cause == DamageCause.CONTACT
      "cramming", "crammed", "cram" -> cause == DamageCause.CRAMMING
      "custom", "customsource" -> cause == DamageCause.CUSTOM
      "dragonbreath", "dragonbreathed" -> cause == DamageCause.DRAGON_BREATH
      "drowning", "drowned", "drown" -> cause == DamageCause.DROWNING
      "entityattack", "entityattacked" -> cause == DamageCause.ENTITY_ATTACK
      "entityexplosion", "entityexplode", "entityexploded" -> cause == DamageCause.ENTITY_EXPLOSION
      "entityswipe", "entityswiped", "entitysweep" -> cause == DamageCause.ENTITY_SWEEP_ATTACK
      "fall", "fell", "falling" -> cause == DamageCause.FALL
      "fallingblock", "fallingblocks" -> cause == DamageCause.FALLING_BLOCK
      "directfire", "directfireexposure", "fireexposure" -> cause == DamageCause.FIRE
      "burn", "burned", "burning", "firetick" -> cause == DamageCause.FIRE_TICK
      "flyintowall", "flyinwall" -> cause == DamageCause.FLY_INTO_WALL
      "freezing", "froze", "frozen", "freeze" -> cause == DamageCause.FREEZE
      "hotfloor", "magma" -> cause == DamageCause.HOT_FLOOR
      "lava", "lavaflow", "lavaflowed", "lavaflowing" -> cause == DamageCause.LAVA
      "lightning", "lightningstrike", "lightningstruck" -> cause == DamageCause.LIGHTNING
      "magic", "magical", "magically" -> cause == DamageCause.MAGIC
      "poison", "poisoned", "poisoning" -> cause == DamageCause.POISON
      "projectile", "projectileattack", "projectileattacked" -> cause == DamageCause.PROJECTILE
      "starvation", "starved", "starving", "famine" -> cause == DamageCause.STARVATION
      "suffocation", "suffocated" -> cause == DamageCause.SUFFOCATION
      "plugin", "pluginenforcement" -> cause == DamageCause.SUICIDE
      "thorns", "thorn", "counter", "counterattack" -> cause == DamageCause.THORNS
      "void" -> cause == DamageCause.VOID
      "wither", "withering", "withered" -> cause == DamageCause.WITHER
      "worldborder", "border" -> cause == DamageCause.WORLD_BORDER
      "killcommand", "kill" -> cause == DamageCause.KILL
      "melting", "melt" -> cause == DamageCause.MELTING
      "dryout", "dry" -> cause == DamageCause.DRYOUT
      "sonicboom", "sonic" -> cause == DamageCause.SONIC_BOOM
      else -> false
    }
  }
}
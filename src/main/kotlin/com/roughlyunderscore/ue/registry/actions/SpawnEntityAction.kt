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

import com.cryptomorin.xseries.XPotion
import com.roughlyunderscore.annotations.Since
import com.roughlyunderscore.annotations.Stable
import com.roughlyunderscore.enums.TargetType
import com.roughlyunderscore.registry.RegistrableAction
import com.roughlyunderscore.data.EventModifications
import com.roughlyunderscore.registry.RegistrableTrigger
import com.roughlyunderscore.ulib.math.clamp
import com.roughlyunderscore.ulib.data.safeValue
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.event.Event
import org.bukkit.potion.PotionEffect
import kotlin.math.roundToInt

/**
 * Spawns an entity at a location
 *
 * Example:
 * - `spawn-entity ENDERMAN 50 120 -801 world HEALTH=47.5 EFFECTS=INCREASE-DAMAGE;-1;2|DAMAGE-RESISTANCE;-1;5 NAME=My_Pal`
 *
 * Syntax:
 * - `spawn-entity TYPE X Y Z WORLD (optional)HEALTH (optional)EFFECTS (optional)NAME`
 * - Effects syntax is: `EFFECT-TYPE;TICKS;AMPLIFIER|etc`
 * - Optional arguments must be prefixed with their type, e.g. `HEALTH=47.5`, `NAME="MyPal"`
 * - No spaces can be used with a name. If a space is needed, use an underscore `"_"`, because underscores are replaced with spaces.
 * - Entity types can be found [here](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/EntityType.html)
 */
@Since("2.2")
@Stable
class SpawnEntityAction : RegistrableAction {
  override val aliases = listOf("spawn-entity", "create-entity", "build-entity")

  override fun execute(event: Event, trigger: RegistrableTrigger, arguments: List<String>, target: TargetType): EventModifications? {
    if (arguments.size < 5) return null

    val entityType = safeValue<EntityType>(arguments[0].uppercase()) ?: return null
    val x = arguments[1].toDoubleOrNull() ?: return null
    val y = arguments[2].toDoubleOrNull() ?: return null
    val z = arguments[3].toDoubleOrNull() ?: return null
    val world = Bukkit.getWorld(arguments[4]) ?: return null

    // Optional arguments
    val optionals = listOf(
      treatArguments(arguments.getOrNull(5)),
      treatArguments(arguments.getOrNull(6)),
      treatArguments(arguments.getOrNull(7))
    )

    world.spawn(Location(world, x, y, z), entityType.entityClass!!) { entity ->
      dealWithOptionalArguments(optionals, entity)
    }

    return null
  }


  private enum class SpawnEntityArgumentType {
    HEALTH, NAME, EFFECTS
  }

  private fun dealWithOptionalArguments(optionals: List<Pair<SpawnEntityArgumentType, Any>?>, entity: Entity) {
    for (optional in optionals) {
      if (optional == null) continue

      when (optional.first) {
        SpawnEntityArgumentType.HEALTH -> {
          val health = (optional.second as? Double)?.let { number -> number.clamp(0.0, 2048.0)} ?: continue

          if (entity !is LivingEntity) continue
          entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue = health
          entity.health = health
        }
        SpawnEntityArgumentType.NAME -> {
          val name = optional.second as? String ?: continue
          entity.customName = name
        }
        SpawnEntityArgumentType.EFFECTS -> {
          val effects = optional.second as? List<*> ?: continue
          for (effect in effects)
            (entity as? LivingEntity ?: continue).addPotionEffect(effect as? PotionEffect ?: continue)
        }
      }
    }
  }

  // This is most likely stupidly suboptimal
  private fun treatArguments(argument: String?): Pair<SpawnEntityArgumentType, Any>? {
    if (argument == null) return null

    // A split by '=' should happen, but only the first '=' matters
    // E.g., in NAME=2+2=4 the equals sign appears twice, but only the first time matters
    // Thus we collect it back after splitting

    val splitArgument = argument.split("=").toMutableList()
    if (splitArgument.size < 2) return null

    val type = safeValue<SpawnEntityArgumentType>(splitArgument.removeFirst()) ?: return null
    val value = splitArgument.joinToString("=")

    return when (type) {
      SpawnEntityArgumentType.HEALTH -> {
        val health = value.toDoubleOrNull() ?: return null
        type to health
      }
      SpawnEntityArgumentType.NAME -> {
        val name = value.replace("_", " ")
        type to name
      }
      SpawnEntityArgumentType.EFFECTS -> {
        val effectsSplit = value.split("|")
        val effects = mutableListOf<PotionEffect>()
        for (effect in effectsSplit) {
          val effectSplit = effect.split(";")
          if (effectSplit.size != 3) continue

          val duration = effectSplit[1].toDoubleOrNull()?.roundToInt() ?: continue
          val amplifier = effectSplit[2].toDoubleOrNull()?.roundToInt() ?: continue

          val effectType = XPotion.matchXPotion(effectSplit[0])
          if (effectType.isEmpty) continue

          effects.add(effectType.get().buildPotionEffect(if (duration == -1) Int.MAX_VALUE else duration, amplifier) ?: continue)
        }

        type to effects
      }
    }
  }
}

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

import com.cryptomorin.xseries.particles.XParticle
import com.roughlyunderscore.annotations.Since
import com.roughlyunderscore.annotations.Stable
import com.roughlyunderscore.enums.TargetType
import com.roughlyunderscore.registry.RegistrableAction
import com.roughlyunderscore.data.EventModifications
import com.roughlyunderscore.registry.RegistrableTrigger
import com.roughlyunderscore.ue.utils.mapToDrt
import org.bukkit.entity.LivingEntity
import org.bukkit.event.Event

/**
 * Spawns a particle at the entity's head location
 *
 * Example:
 * - `particle-head SUSPENDED_DEPTH`
 *
 * Syntax:
 * - `particle-head PARTICLE`
 * - Particles can be found [here](https://github.com/CryptoMorin/XSeries/blob/master/src/main/java/com/cryptomorin/xseries/particles/XParticle.java)
 */
@Since("2.2")
@Stable
class ParticleHeadAction : RegistrableAction {
  override val aliases = listOf("particle-eyes", "spawn-particle-eyes", "particle-head", "spawn-particle-head")

  override fun execute(event: Event, trigger: RegistrableTrigger, arguments: List<String>, target: TargetType): EventModifications? {
    if (arguments.isEmpty()) return null

    val particle = XParticle.of(arguments[0].uppercase())?.get() ?: return null

    val method = trigger.getTriggerDataHolder().dataRetrievalMethods[target.mapToDrt()] ?: return null
    val entity = method.invoke(event) as? LivingEntity ?: return null

    entity.world.spawnParticle(particle, entity.eyeLocation, 1)

    return null
  }
}
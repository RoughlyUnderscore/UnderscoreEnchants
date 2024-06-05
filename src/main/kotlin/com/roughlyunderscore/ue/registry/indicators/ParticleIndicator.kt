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

package com.roughlyunderscore.ue.registry.indicators

import com.roughlyunderscore.annotations.Since
import com.roughlyunderscore.annotations.Stable
import com.roughlyunderscore.registry.RegistrableActivationIndicator
import org.bukkit.Particle
import org.bukkit.entity.Player

@Since("2.2")
@Stable
class ParticleIndicator : RegistrableActivationIndicator {
  override val aliases = listOf("particle", "particles", "effect")

  override fun indicateAboutActivation(text: String, player: Player) {
    player.spawnParticle(Particle.HAPPY_VILLAGER, player.location, (20..30).random(), 0.7, 0.7, 0.7)
  }
}
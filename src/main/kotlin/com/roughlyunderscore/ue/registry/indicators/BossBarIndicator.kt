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

import com.roughlyunderscore.ue.UnderscoreEnchantsPlugin
import com.roughlyunderscore.annotations.Since
import com.roughlyunderscore.annotations.Stable
import com.roughlyunderscore.registry.RegistrableActivationIndicator
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.entity.Player
import java.util.*

@Since("2.2")
@Stable
class BossBarIndicator(private val plugin: UnderscoreEnchantsPlugin) : RegistrableActivationIndicator {
  override val aliases = listOf("bossbar", "bar", "boss")

  override fun indicateAboutActivation(text: String, player: Player) {
    val key = NamespacedKey(plugin, "activation_${player.name}_${UUID.randomUUID()}")
    val bossbar = Bukkit.createBossBar(key, PlaceholderAPI.setPlaceholders(player, text), BarColor.YELLOW, BarStyle.SOLID)
    bossbar.progress = 1.0
    bossbar.addPlayer(player)

    Bukkit.getScheduler().runTaskLater(plugin, Runnable {
      bossbar.removeAll()
      Bukkit.removeBossBar(key)
    }, 50L)
  }
}
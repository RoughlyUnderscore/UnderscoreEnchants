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

package com.roughlyunderscore.ue.listeners

import com.roughlyunderscore.annotations.Since
import com.roughlyunderscore.events.*
import org.bukkit.Bukkit
import org.bukkit.Statistic
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.player.PlayerStatisticIncrementEvent

/**
 * This class handles the custom UnderscoreEnchants events.
 */
@Since("2.2")
class CustomEventsListener : Listener {
  @EventHandler
  fun onPVP(event: EntityDamageByEntityEvent) {
    // This is needed so that calling the event won't end up in an infinite loop
    if (isCustomEvent(event)) return
    if (event.damager !is Player || event.entity !is Player) return

    val damager = event.damager as Player
    val victim = event.entity as Player

    val pvpEvent = PVPEvent(damager, victim, event.cause, event.damage)
    pvpEvent.isCancelled = event.isCancelled

    Bukkit.getPluginManager().callEvent(pvpEvent)
    event.isCancelled = pvpEvent.isCancelled
  }

  @EventHandler
  fun onPlayerGotHurt(event: EntityDamageEvent) {
    if (isCustomEvent(event)) return
    if (event.entity !is Player) return

    val victim = event.entity as Player

    val playerGotHurtEvent = PlayerGotHurtEvent(victim, event.cause, event.damage)
    playerGotHurtEvent.isCancelled = event.isCancelled

    Bukkit.getPluginManager().callEvent(playerGotHurtEvent)
    event.isCancelled = playerGotHurtEvent.isCancelled
  }

  @EventHandler
  fun onPlayerHurtEntity(event: EntityDamageByEntityEvent) {
    if (isCustomEvent(event)) return

    // This event is not called if the victim is a player. Instead, PVPEvent is called.
    if (event.entity is Player) return
    val damager = event.damager as? Player ?: (event.damager as? Arrow)?.shooter as? Player ?: return

    val playerHurtEntityEvent = PlayerHurtEntityEvent(damager, event.entity, event.cause, event.damage)
    playerHurtEntityEvent.isCancelled = event.isCancelled

    Bukkit.getPluginManager().callEvent(playerHurtEntityEvent)
    event.isCancelled = playerHurtEntityEvent.isCancelled
  }

  @EventHandler
  fun onPlayerShootBow(event: EntityShootBowEvent) {
    if (isCustomEvent(event)) return
    if (event.entity !is Player || event.projectile !is Arrow) return

    val shooter = event.entity as Player
    val arrow = event.projectile as Arrow

    val playerShootBowEvent = PlayerShootBowEvent(shooter, event.bow, event.consumable, arrow, event.hand, event.force, event.shouldConsumeItem())
    playerShootBowEvent.isCancelled = event.isCancelled

    Bukkit.getPluginManager().callEvent(playerShootBowEvent)
    event.isCancelled = playerShootBowEvent.isCancelled
  }

  @EventHandler
  fun onPlayerBowHit(event: EntityDamageByEntityEvent) {
    if (isCustomEvent(event)) return
    if (event.damager !is Arrow || ((event.damager as? Arrow)?: return).shooter !is Player || event.entity !is Player) return

    val damager = (event.damager as Arrow).shooter as Player
    val victim = event.entity as Player

    val playerBowHitEvent = PlayerBowHitEvent(damager, victim, event.damage)
    playerBowHitEvent.isCancelled = event.isCancelled

    Bukkit.getPluginManager().callEvent(playerBowHitEvent)
    event.isCancelled = playerBowHitEvent.isCancelled
  }

  @EventHandler
  fun onJump(event: PlayerStatisticIncrementEvent) {
    if (isCustomEvent(event)) return
    if (event.statistic != Statistic.JUMP) return

    val playerJumpEvent = PlayerJumpEvent(event.player)
    Bukkit.getPluginManager().callEvent(playerJumpEvent)
  }

  /**
   * Checks if [event] is an instance of a custom UnderscoreEnchants event.
   * This is needed so that calling the event won't end up in an infinite loop.
   * @return Whether the event is an instance of a custom UnderscoreEnchants event
   */
  @Since("2.2")
  private fun isCustomEvent(event: Event): Boolean =
    event is PVPEvent
      || event is PlayerGotHurtEvent
      || event is PlayerHurtEntityEvent
      || event is PlayerShootBowEvent
      || event is PlayerBowHitEvent
      || event is PlayerJumpEvent
}
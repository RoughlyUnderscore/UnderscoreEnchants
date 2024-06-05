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
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

/**
 * Gives the player a skull of the specified player; if no space is available, the item will be dropped at the player's location.
 *
 * Example:
 * - `give-skull @p`
 *
 * Syntax:
 * - `give-skull PLAYER`
 * - The player can be a selector, if the selector only returns one player.
 */
@Since("2.2")
@Stable
class PlayerGiveSkullAction : RegistrableAction {
  override val aliases = listOf("give-skull", "skull")

  override fun execute(event: Event, trigger: RegistrableTrigger, arguments: List<String>, target: TargetType): EventModifications? {
    if (arguments.isEmpty()) return null

    val targetPlayer = arguments[0]

    val method = trigger.getTriggerDataHolder().dataRetrievalMethods[target.mapToDrt()] ?: return null
    val player = method.invoke(event) as? Player ?: return null

    val playerProfile = Bukkit.selectEntities(player, targetPlayer).let { if (it.isEmpty()) null else it.first() as? Player }?.playerProfile
      ?: Bukkit.createPlayerProfile(targetPlayer)

    val item = ItemStack(Material.PLAYER_HEAD)
    val meta = item.itemMeta as SkullMeta
    meta.ownerProfile = playerProfile
    item.itemMeta = meta

    if (player.inventory.firstEmpty() == -1) player.world.dropItemNaturally(player.location, item)
    else player.inventory.addItem(item)

    return null
  }
}
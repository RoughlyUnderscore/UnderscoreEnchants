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

import com.roughlyunderscore.ue.UnderscoreEnchantsPlugin
import com.roughlyunderscore.annotations.Since
import com.roughlyunderscore.ue.utils.getLocaleStrict
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

/**
 * Listens for a player join event to load their data.
 */
@Since("2.2")
class PlayerDataLoadingListener(private val plugin: UnderscoreEnchantsPlugin) : Listener {
  @EventHandler
  fun onJoin(event: PlayerJoinEvent) {
    val player = event.player
    if (plugin.configuration.settings.notifyPlayersOfDataLoading) player.sendMessage(plugin.globalLocale.loading)

    GlobalScope.launch {
      val data = plugin.storage.loadPlayerData(event.player.uniqueId)
      val locale = data.locale?.let { plugin.locales.getLocaleStrict(it) } ?: plugin.globalLocale
      player.sendMessage(locale.loaded)
    }
  }

  @EventHandler
  fun onLeave(event: PlayerQuitEvent) {
    val player = event.player

    GlobalScope.launch {
      plugin.storage.saveDataOf(player.uniqueId)
    }
  }
}
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

package com.roughlyunderscore.ue.hook.api

import com.roughlyunderscore.ue.UnderscoreEnchantsPlugin
import com.roughlyunderscore.api.UEPlayerManager
import com.roughlyunderscore.data.UELocale
import com.roughlyunderscore.ue.registry.RegistryImpl
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import java.util.*

class UEPlayerManagerImpl(private val plugin: UnderscoreEnchantsPlugin) : UEPlayerManager {
  val registry = plugin.registry
  override fun getLocale(uuid: UUID): UELocale? = plugin.storage.getLocaleStrict(uuid, plugin)

  override fun setLocale(uuid: UUID, localeIdentifier: String, silent: Boolean) {
    val locale = plugin.storage.setLocale(uuid, localeIdentifier, plugin) ?: return
    if (!silent) Bukkit.getPlayer(uuid)?.sendMessage(locale.changedLocaleExternally)
  }

  override fun isDisabled(uuid: UUID, key: NamespacedKey): Boolean = plugin.storage.isEnchantmentDisabled(uuid, key)

  override fun toggle(uuid: UUID, key: NamespacedKey, silent: Boolean): Boolean {
    val locale = plugin.storage.getLocale(uuid, plugin)

    val enchantment = registry.findEnchantmentByKey(key) ?: return false
    val name = enchantment.aliases.first()

    val currentlyToggled = plugin.storage.toggleEnchantment(uuid, key)

    if (!silent)
      Bukkit.getPlayer(uuid)?.sendMessage(locale.toggledEnchantmentExternally
        .replace("<enchantment>", name)
        .replace("<state>", if (currentlyToggled) locale.stateOn else locale.stateOff))

    return currentlyToggled
  }
}
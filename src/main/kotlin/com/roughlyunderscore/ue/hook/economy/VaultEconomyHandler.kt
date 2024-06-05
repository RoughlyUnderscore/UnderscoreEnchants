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

package com.roughlyunderscore.ue.hook.economy

import com.roughlyunderscore.ue.UnderscoreEnchantsPlugin
import com.roughlyunderscore.annotations.Since
import com.roughlyunderscore.annotations.Stable
import net.milkbowl.vault.economy.Economy
import org.bukkit.entity.Player
import java.lang.IllegalStateException
import java.math.BigDecimal

@Since("2.2")
@Stable
class VaultEconomyHandler(plugin: UnderscoreEnchantsPlugin) : EconomyHandler {
  private val economy: Economy

  init {
    val rsp = plugin.server.servicesManager.getRegistration(Economy::class.java) ?: run {
      throw IllegalStateException("Could not find an economy plugin that supports Vault Economy.")
    }

    economy = rsp.provider
  }

  override fun setMoney(player: Player, amount: BigDecimal) {
    economy.withdrawPlayer(player, getMoney(player).toDouble())
    economy.depositPlayer(player, amount.toDouble())
  }

  override fun getMoney(player: Player): BigDecimal {
    return economy.getBalance(player).toBigDecimal()
  }
}
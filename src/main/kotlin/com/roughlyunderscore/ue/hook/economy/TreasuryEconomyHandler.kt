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

import com.roughlyunderscore.annotations.Since
import com.roughlyunderscore.annotations.Stable
import me.lokka30.treasury.api.common.Cause
import me.lokka30.treasury.api.common.service.ServiceRegistry
import me.lokka30.treasury.api.economy.EconomyProvider
import me.lokka30.treasury.api.economy.transaction.EconomyTransactionImportance
import org.bukkit.entity.Player
import java.lang.IllegalStateException
import java.math.BigDecimal

@Since("2.2")
@Stable
class TreasuryEconomyHandler : EconomyHandler {
  private val serviceProvider: EconomyProvider

  init {
    val providerFetcher = ServiceRegistry.INSTANCE.serviceFor(EconomyProvider::class.java)
    if (providerFetcher.isEmpty) {
      throw IllegalStateException("Expected an Economy Provider to be registered through Treasury, but found none!")
    }

    serviceProvider = providerFetcher.get().get()
  }

  override fun setMoney(player: Player, amount: BigDecimal) {
    serviceProvider
      .accountAccessor()
      .player()
      .withUniqueId(player.uniqueId)
      .get()
      .thenAccept { account ->
        account.resetBalance(Cause.SERVER, serviceProvider.primaryCurrency, EconomyTransactionImportance.NORMAL)
        account.depositBalance(amount, Cause.SERVER, serviceProvider.primaryCurrency)
      }
  }

  override fun getMoney(player: Player): BigDecimal {
    return serviceProvider
      .accountAccessor()
      .player()
      .withUniqueId(player.uniqueId)
      .get()
      .thenCompose { account -> account.retrieveBalance(serviceProvider.primaryCurrency) }
      .get() // i probably shouldn't do this get call but idk how to do it otherwise?
  }
}
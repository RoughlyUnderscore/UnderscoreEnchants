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

package com.roughlyunderscore.ue.ui.browse

import com.roughlyunderscore.ue.UnderscoreEnchantsPlugin
import com.roughlyunderscore.ue.ui.misc.SortingType
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.bukkit.entity.Player

/**
 * This class represents a UI for browsing content (enchantments, packs,
 * locales or anything else).
 */
abstract class BrowsingUI<T> {
  abstract var sorting: SortingType

  /**
   * Fetches the [player]'s title for this GUI depending on their locale.
   */
  abstract fun fetchDynamicTitle(player: Player): String

  /**
   * Fetches the content from the repository.
   */
  abstract suspend fun fetchContent(): List<T>

  /**
   * Prepares the GUI and opens it for the [player]. Use this only
   * if you already have a list of [content].
   */
  abstract fun prepareGui(player: Player, content: List<T>)

  /**
   * Cycles the current [sorting] type.
   */
  abstract fun cycleSortingType()

  /**
   * Opens the GUI for the [player]. This is the method you should use
   * if you do not have a list of content but want to pull it from the
   * repository.
   */
  open fun createAndOpen(player: Player) = runBlocking {
    prepareGui(player, GlobalScope.async { fetchContent() }.await())
  }
}
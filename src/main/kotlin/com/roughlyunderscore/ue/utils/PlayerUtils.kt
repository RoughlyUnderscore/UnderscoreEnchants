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

package com.roughlyunderscore.ue.utils

import com.roughlyunderscore.annotations.Since
import com.roughlyunderscore.annotations.Stable
import org.bukkit.entity.Player

/**
 * Drops the item in the player's offhand at their location. If [dropAll] is true, the entire stack will be dropped.
 * Otherwise, only one item will be dropped.
 */
@Since("2.2")
@Stable
fun Player.dropItemOffHand(dropAll: Boolean) {
  var item = inventory.itemInOffHand.clone()
  if (item.type.isAir) return

  val amount = if (dropAll) item.amount else 1
  val remains = item.amount - amount

  if (remains > 0) {
    item.amount = remains
    inventory.setItemInOffHand(item)
  }
  else inventory.setItemInOffHand(null)

  item = item.clone() // maybe unnecessary but it is a precaution
  item.amount = amount
  world.dropItemNaturally(location, item)
}
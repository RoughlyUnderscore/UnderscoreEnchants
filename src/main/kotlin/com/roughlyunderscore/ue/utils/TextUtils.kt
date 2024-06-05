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
import com.roughlyunderscore.enums.TargetType
import com.roughlyunderscore.ulib.text.normalize
import net.kyori.adventure.text.Component

/**
 * Takes a double and returns a color based on the chance of the double.
 * The double should represent a chance between 0 and 100.
 */
@Since("2.2")
@Stable
fun Double.asChanceColor(): String = when {
  this == 0.0 -> "&4"
  this in 0.0 .. 20.0 -> "&c"
  this in 20.0 .. 50.0 -> "&6"
  this in 50.0 .. 75.0 -> "&e"
  this in 75.0 .. 100.0 -> "&2"
  else -> "&a"
}

/**
 * Converts a string into a TargetType.
 */
@Since("2.2")
@Stable
fun String.toTarget(): TargetType {
  val normal = this.normalize()
  return when {
    normal.startsWith("secondp") -> TargetType.SECOND_PLAYER
    normal.startsWith("t") -> TargetType.THIRD_PLAYER
    normal.startsWith("e") -> TargetType.ENTITY
    normal.startsWith("b") -> TargetType.BLOCK
    normal.startsWith("firsti") -> TargetType.FIRST_ITEM
    normal.startsWith("secondi") -> TargetType.SECOND_ITEM
    else -> TargetType.FIRST_PLAYER
  }
}

/**
 * Transforms a string into a [Component].
 */
@Since("2.2")
@Stable
fun String.asComponent() = Component.text(this)

/**
 * Splits an int/long into threes with a separator (" " by default).
 * Example: `33498675 -> 33 498 675`
 */
@Since("2.2")
@Stable
fun Long.splitIntoThree(separator: String = " ") = this.toString().reversed().chunked(3).joinToString(separator).reversed()
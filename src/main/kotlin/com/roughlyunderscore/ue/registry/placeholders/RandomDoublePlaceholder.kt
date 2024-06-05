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

package com.roughlyunderscore.ue.registry.placeholders

import com.roughlyunderscore.annotations.Since
import com.roughlyunderscore.annotations.Stable
import com.roughlyunderscore.ue.data.Constants
import com.roughlyunderscore.enums.TargetType
import com.roughlyunderscore.registry.RegistrablePlaceholder
import com.roughlyunderscore.registry.RegistrableTrigger
import com.roughlyunderscore.ue.utils.*
import org.bukkit.event.Event

@Since("2.2")
@Stable
class RandomDoublePlaceholder : RegistrablePlaceholder {
  override val aliases = listOf(
    "random-double",
    "randomdouble",
    "rnd-double",
    "rnddouble",
    "random-dbl",
    "randomdbl",
    "rnd-dbl",
    "rnddbl",
    "double-random",
    "doublerandom",
    "dbl-random",
    "dblrandom",
    "dbl-rnd",
    "dblrnd",
    "double-rnd",
    "doublernd"
  )

  override fun replacedText(
    event: Event,
    trigger: RegistrableTrigger,
    target: TargetType,
    args: Map<String, String>
  ): String {
    val lowerBound = args["lowerBound"]?.toDoubleOrNull()
    val upperBound = args["upperBound"]?.toDoubleOrNull()

    if (lowerBound == null || upperBound == null) return Constants.RANDOM.nextDouble().toString()
    return Constants.RANDOM.nextDouble(lowerBound, upperBound).toString()
  }
}
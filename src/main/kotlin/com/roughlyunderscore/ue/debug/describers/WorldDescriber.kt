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

package com.roughlyunderscore.ue.debug.describers

import com.roughlyunderscore.data.UELocale
import com.roughlyunderscore.debug.Describer
import org.bukkit.World
import org.bukkit.entity.SpawnCategory

object WorldDescriber : Describer<World> {
  override fun describeShortly(locale: UELocale, obj: World?): String? = null

  override fun describe(locale: UELocale, obj: World?): List<String> {
    if (obj == null) return emptyList()

    return buildList {
      add("${locale.logTokenWorldName}: ${obj.name}")
      add("${locale.logTokenWorldUuid}: ${obj.uid}")
      add("${locale.logTokenWorldEnv}: ${obj.environment.name}")
      add("${locale.logTokenWorldSeed}: ${obj.seed}")
      add("${locale.logTokenWorldDifficulty}: ${obj.difficulty.name}")
      add("${locale.logTokenWorldSpawn}: ${obj.spawnLocation}")
      add("${locale.logTokenWorldTime}: ${obj.time}")
      add("${locale.logTokenWorldRain}: ${obj.hasStorm()}")
      add("${locale.logTokenWorldThunder}: ${obj.isThundering}")

      for (category in SpawnCategory.entries) {
        // World#getSpawnLimit does not support SpawnCategory.MISC
        // (would be cool to put that in the docs!!)
        if (category == SpawnCategory.MISC) continue
        add("${category.name} ${locale.logTokenWorldSpawnLimit}: ${obj.getSpawnLimit(category)}")
      }
    }
  }
}
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

package com.roughlyunderscore.ue.registry.applicables

import com.roughlyunderscore.annotations.Since
import com.roughlyunderscore.annotations.Stable
import com.roughlyunderscore.registry.RegistrableApplicable
import org.bukkit.Material

@Since("2.2")
@Stable
class ShieldsApplicable : RegistrableApplicable {
  override val aliases = listOf("shield", "shields")

  override fun canBeAppliedTo(type: Material): Boolean = type == Material.SHIELD
}
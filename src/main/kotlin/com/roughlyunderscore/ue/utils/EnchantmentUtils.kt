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
import com.roughlyunderscore.registry.RegistrableEnchantment
import com.roughlyunderscore.ue.registry.RegistryImpl
import com.roughlyunderscore.ulib.text.containsIgnoreCase
import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.bukkit.enchantments.Enchantment

/**
 * Checks if this custom enchantment conflicts with another enchantment defined by [key].
 * [registry] required to find custom enchantments.
 */
@Since("2.2")
fun RegistrableEnchantment.conflictsWith(key: NamespacedKey, registry: RegistryImpl): Boolean {
  val vanilla = Registry.ENCHANTMENT.get(key)
  if (vanilla != null) return this.conflicts.containsIgnoreCase(vanilla.key.key)

  val custom = registry.findEnchantmentByKey(key)
  if (custom != null) return custom.conflicts.containsIgnoreCase(this.key.key) || this.conflicts.containsIgnoreCase(custom.key.key)

  return false
}

/**
 * Checks if this vanilla enchantment conflicts with another enchantment defined by [key].
 * [registry] required to find custom enchantments.
 */
@Since("2.2")
fun Enchantment.conflictsWith(key: NamespacedKey, registry: RegistryImpl): Boolean {
  val vanilla = Registry.ENCHANTMENT.get(key)
  val custom = registry.findEnchantmentByKey(key)

  if (vanilla != null) return this.conflictsWith(vanilla) || vanilla.conflictsWith(this)
  if (custom != null) return custom.conflictsWith(this.key, registry)
  return false
}

/**
 * Checks if an enchantment conflicts with another enchantment defined by [key].
 * [registry] required to find custom enchantments.
 */
@Since("2.2")
fun NamespacedKey.conflictsWith(key: NamespacedKey, registry: RegistryImpl): Boolean {
  val vanilla = Registry.ENCHANTMENT.get(key)
  val custom = registry.findEnchantmentByKey(key)

  return vanilla?.conflictsWith(this, registry) ?: (custom?.conflictsWith(this, registry) ?: false)
}
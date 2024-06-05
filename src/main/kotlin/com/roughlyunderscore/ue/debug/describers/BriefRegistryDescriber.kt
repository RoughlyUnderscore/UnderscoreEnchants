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
import com.roughlyunderscore.registry.Registrable
import com.roughlyunderscore.ue.registry.RegistryImpl
import com.roughlyunderscore.ulib.text.joinAndWrap
import org.bukkit.plugin.java.JavaPlugin

object BriefRegistryDescriber : Describer<RegistryImpl> {
  override fun describeShortly(locale: UELocale, obj: RegistryImpl?): String? = null

  override fun describe(locale: UELocale, obj: RegistryImpl?): List<String> {
    if (obj == null) return emptyList()

    return describeRegistryEntry(locale, locale.logTokenRegistryActionSingular, locale.logTokenRegistryActionPlural, obj.actions) +
      describeRegistryEntry(locale, locale.logTokenRegistryApplicableSingular, locale.logTokenRegistryApplicablePlural, obj.applicables) +
      describeRegistryEntry(locale, locale.logTokenRegistryConditionSingular, locale.logTokenRegistryConditionPlural, obj.conditions) +
      describeRegistryEntry(locale, locale.logTokenRegistryIndicatorSingular, locale.logTokenRegistryIndicatorPlural, obj.indicators) +
      describeRegistryEntry(locale, locale.logTokenRegistryPlaceholderSingular, locale.logTokenRegistryPlaceholderPlural, obj.placeholders) +
      describeRegistryEntry(locale, locale.logTokenRegistrySeekerSingular, locale.logTokenRegistrySeekerPlural, obj.seekers) +
      describeRegistryEntry(locale, locale.logTokenRegistryTriggerSingular, locale.logTokenRegistryTriggerPlural, obj.triggers)
  }

  private fun describeRegistryEntry(locale: UELocale, singularName: String, pluralName: String, map: Map<out Registrable, JavaPlugin>): List<String> {
    val result = mutableListOf<String>()
    result.add("$pluralName ${locale.logTokenRegistryLoaded}: ${map.size}")
    for (entry in map) {
      val registrable = entry.key
      val provider = entry.value

      result.add("$singularName: ${registrable.aliases.joinAndWrap("\"", "\"", ", ")}")
      result.add("${locale.logTokenRegistryProvider}: ${provider.name} v${provider.description.version}")
      result.add("")
    }

    return result
  }
}
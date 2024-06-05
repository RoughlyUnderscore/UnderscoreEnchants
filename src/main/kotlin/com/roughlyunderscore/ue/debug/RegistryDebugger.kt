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

package com.roughlyunderscore.ue.debug

import com.roughlyunderscore.ue.UnderscoreEnchantsPlugin
import com.roughlyunderscore.data.UELocale
import com.roughlyunderscore.registry.Registrable
import com.roughlyunderscore.ue.registry.RegistryImpl
import com.roughlyunderscore.ulib.text.normalize
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin

class RegistryDebugger(private val plugin: UnderscoreEnchantsPlugin) {
  private val registry = plugin.registry

  fun debugRegistry(locale: UELocale, sender: CommandSender) {
    val actions = registry.actions.let { it.size to it.filter { filtered -> filtered.value.description == plugin.description }.size }
    val applicables = registry.applicables.let { it.size to it.filter { filtered -> filtered.value.description == plugin.description }.size }
    val conditions = registry.conditions.let { it.size to it.filter { filtered -> filtered.value.description == plugin.description }.size }
    val indicators = registry.indicators.let { it.size to it.filter { filtered -> filtered.value.description == plugin.description }.size }
    val placeholders = registry.placeholders.let { it.size to it.filter { filtered -> filtered.value.description == plugin.description }.size }
    val seekers = registry.seekers.let { it.size to it.filter { filtered -> filtered.value.description == plugin.description }.size }
    val triggers = registry.triggers.let { it.size to it.filter { filtered -> filtered.value.description == plugin.description }.size }

    sender.sendMessage("")
    sender.sendMessage(
      """
        ${locale.registryDebugTitle}
        ${locale.registryDebugActionsLoaded.replace("<amount>", actions.first.toString())}, ${locale.registryDebugBuiltInAmount.replace("<amount>", actions.second.toString())}
        ${locale.registryDebugApplicablesLoaded.replace("<amount>", applicables.first.toString())}, ${locale.registryDebugBuiltInAmount.replace("<amount>", applicables.second.toString())}
        ${locale.registryDebugConditionsLoaded.replace("<amount>", conditions.first.toString())}, ${locale.registryDebugBuiltInAmount.replace("<amount>", conditions.second.toString())}
        ${locale.registryDebugIndicatorsLoaded.replace("<amount>", indicators.first.toString())}, ${locale.registryDebugBuiltInAmount.replace("<amount>", indicators.second.toString())}
        ${locale.registryDebugPlaceholdersLoaded.replace("<amount>", placeholders.first.toString())}, ${locale.registryDebugBuiltInAmount.replace("<amount>", placeholders.second.toString())}
        ${locale.registryDebugSeekersLoaded.replace("<amount>", seekers.first.toString())}, ${locale.registryDebugBuiltInAmount.replace("<amount>", seekers.second.toString())}
        ${locale.registryDebugTriggersLoaded.replace("<amount>", triggers.first.toString())}, ${locale.registryDebugBuiltInAmount.replace("<amount>", triggers.second.toString())}
      """.trimIndent()
    )
  }

  fun debugRegistryType(locale: UELocale, sender: CommandSender, type: String) {
    // This approach is kinda dirty & hacky. Yet, it works and is fairly readable. I'm fine with that.
    val data = when (type.normalize()) {
      "actions", "action" -> listOf(registry.actions, locale.registryDebugActionsTitle, locale.registryDebugActionsInfo, locale.registryDebugActionsBuiltIn, locale.registryDebugActionsCustom)
      "applicables", "applicable" -> listOf(registry.applicables, locale.registryDebugApplicablesTitle, locale.registryDebugApplicablesInfo, locale.registryDebugApplicablesBuiltIn, locale.registryDebugApplicablesCustom)
      "conditions", "condition" -> listOf(registry.conditions, locale.registryDebugConditionsTitle, locale.registryDebugConditionsInfo, locale.registryDebugConditionsBuiltIn, locale.registryDebugConditionsCustom)
      "indicators", "indicator" -> listOf(registry.indicators, locale.registryDebugIndicatorsTitle, locale.registryDebugIndicatorsInfo, locale.registryDebugIndicatorsBuiltIn, locale.registryDebugIndicatorsCustom)
      "placeholders", "placeholder" -> listOf(registry.placeholders, locale.registryDebugPlaceholdersTitle, locale.registryDebugPlaceholdersInfo, locale.registryDebugPlaceholdersBuiltIn, locale.registryDebugPlaceholdersCustom)
      "seekers", "seeker" -> listOf(registry.seekers, locale.registryDebugSeekersTitle, locale.registryDebugSeekersInfo, locale.registryDebugSeekersBuiltIn, locale.registryDebugSeekersCustom)
      "triggers", "trigger" -> listOf(registry.triggers, locale.registryDebugTriggersTitle, locale.registryDebugTriggersInfo, locale.registryDebugTriggersBuiltIn, locale.registryDebugTriggersCustom)
      else -> {
        sender.sendMessage(locale.registryDebugInvalidType)
        return
      }
    }

    val registryType = (data[0] as Map<*, *>)
      .mapNotNull { it.key as Registrable to it.value as JavaPlugin }
      .sortedBy { it.second.description == plugin.description }
      .toMap()
    val title = data[1] as String
    val info = data[2] as String
    val builtIn = data[3] as String
    val custom = data[4] as String

    val loaded = registryType.size

    sender.sendMessage(title, info.replace("<amount>", "$loaded"))

    for (entry in registryType.entries) {
      val firstAlias = entry.key.aliases.firstOrNull() ?: continue
      val registree = entry.value

      if (registree.description == plugin.description) sender.sendMessage(builtIn.replace("<name>", firstAlias))
      else sender.sendMessage(custom.replace("<name>", firstAlias).replace("<plugin>", "${registree.name} v${registree.description.version}"))
    }
  }
}
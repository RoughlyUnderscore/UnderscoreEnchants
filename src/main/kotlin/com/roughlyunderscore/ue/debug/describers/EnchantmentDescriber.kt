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

import com.roughlyunderscore.ue.UnderscoreEnchantment
import com.roughlyunderscore.data.UELocale
import com.roughlyunderscore.debug.Describer
import com.roughlyunderscore.registry.RegistrableEnchantment
import com.roughlyunderscore.ulib.text.formatColor
import com.roughlyunderscore.ulib.text.joinAndWrap

object EnchantmentDescriber : Describer<RegistrableEnchantment> {
  override fun describeShortly(locale: UELocale, obj: RegistrableEnchantment?): String? = null

  override fun describe(locale: UELocale, obj: RegistrableEnchantment?): List<String> {
    if (obj !is UnderscoreEnchantment) return emptyList()

    return buildList {
      add("${locale.tokenEnchantment}&f: &l${obj.name} &7| &f&l${obj.key}")
      add("${locale.tokenEnchantmentIndicator}&f: &l${obj.activationIndicator.aliases.first() }")
      add("${locale.tokenEnchantmentApplicables}&f: &l${obj.applicables.map { it.aliases.first() }.joinAndWrap("[", "]", ", ")}")
      add("${locale.tokenEnchantmentAuthor}&f: &l${obj.author}")
      add("${locale.tokenEnchantmentChance}&f: &l${obj.activationChance}")
      add("${locale.tokenEnchantmentConditions}&f: &l${obj.conditions.map { it.getDescriber().describeShortly(locale, it) }.joinAndWrap("[", "]", ", ")}")

      if (obj.conflicts.isNotEmpty())
        add("${locale.tokenEnchantmentConflicts}&f: &l${obj.conflicts.joinAndWrap("\"", "\"", ", ")}")

      add("${locale.tokenEnchantmentCooldown}&f: &l${obj.cooldown.ticks}")
      add("${locale.tokenEnchantmentDescription}&f: &l${obj.description.joinAndWrap("\"", "\"", ", ")}")

      if (obj.forbiddenMaterials.isNotEmpty())
        add("${locale.tokenEnchantmentForbidden}&f: &l${obj.forbiddenMaterials.map { it.name }.joinAndWrap("\"", "\"", ", ")}")

      add("${locale.tokenEnchantmentUnique}&f: &l${obj.unique}")
      add("${locale.tokenEnchantmentLevels}&f: &l${obj.levels.map { it.getDescriber().describeShortly(locale, it) }.joinAndWrap("[", "]", ", ")}")

      if (obj.obtainmentRestrictions.isNotEmpty())
        add("${locale.tokenEnchantmentRestrictions}&f: &l${obj.obtainmentRestrictions.map { it.getDescriber().describeShortly(locale, it) }.joinAndWrap("[", "]", ", ")}")

      if (obj.requiredEnchantments.isNotEmpty())
        add("${locale.tokenEnchantmentRequiredEnchantments}&f: &l${obj.requiredEnchantments.map { it.getDescriber().describeShortly(locale, it) }.joinAndWrap("[", "]", ", ")}")

      if (obj.requiredPlugins.isNotEmpty())
        add("${locale.tokenEnchantmentRequiredPlugins}&f: &l${obj.requiredPlugins.map { it.getDescriber().describeShortly(locale, it) }.joinAndWrap("[", "]", ", ")}")

      add("${locale.tokenEnchantmentSeekers}&f: &l${obj.enchantmentSeekers.map { it.aliases.first() }.joinAndWrap("[", "]", ", ")}")
      add("${locale.tokenEnchantmentTrigger}&f: &l${obj.trigger.aliases.first() }")

      if (obj.worldWhitelist.isNotEmpty())
        add("${locale.tokenEnchantmentWhitelist}&f: &l${obj.worldWhitelist.joinAndWrap("\"", "\"", ", ")}")

      if (obj.worldBlacklist.isNotEmpty())
        add("${locale.tokenEnchantmentBlacklist}&f: &l${obj.worldBlacklist.joinAndWrap("\"", "\"", ", ")}")

    }.map { "&7- &6$it".formatColor() }
  }
}
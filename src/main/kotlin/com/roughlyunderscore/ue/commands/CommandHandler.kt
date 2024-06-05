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

package com.roughlyunderscore.ue.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.BukkitCommandCompletionContext
import co.aikar.commands.BukkitCommandExecutionContext
import co.aikar.commands.BukkitCommandIssuer
import co.aikar.commands.ConditionContext
import co.aikar.commands.PaperCommandManager
import com.roughlyunderscore.ue.UnderscoreEnchantsPlugin

// This class will be removed when I write my own command framework
@Suppress("unused")
class CommandHandler(plugin: UnderscoreEnchantsPlugin, block: CommandHandler.() -> Unit) {
  private val commandManager = PaperCommandManager(plugin)
  private val commands = mutableMapOf<String, BaseCommand>()

  init {
    block.invoke(this)
  }

  fun completion(name: String, logic: (BukkitCommandCompletionContext) -> List<String>) {
    commandManager.commandCompletions.registerAsyncCompletion(name) { ctx ->
      logic.invoke(ctx)
    }
  }

  fun condition(name: String, logic: (ConditionContext<BukkitCommandIssuer>) -> Boolean) {
    commandManager.commandConditions.addCondition(name) { ctx ->
      logic.invoke(ctx)
    }
  }

  fun <T> context(clazz: Class<T>, logic: (BukkitCommandExecutionContext) -> T) {
    commandManager.commandContexts.registerContext(clazz) { ctx ->
      logic.invoke(ctx)
    }
  }

  fun command(command: BaseCommand) {
    commandManager.registerCommand(command)
    commands[command.name] = (command)
  }

  fun unregisterCommand(name: String) {
    val command = commands[name] ?: return
    commandManager.unregisterCommand(command)
  }

  fun unregisterAll() = commandManager.unregisterCommands()
}
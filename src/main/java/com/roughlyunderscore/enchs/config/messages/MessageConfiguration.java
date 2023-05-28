package com.roughlyunderscore.enchs.config.messages;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;

@Configuration @SuppressWarnings("unused")
public class MessageConfiguration {
  public record Language(
    @Comment({
      "This message will be displayed when a player does not have",
      "sufficient permissions to execute a certain action."
    })
    String noPerms,

    @Comment({
      "This message will be displayed when a player-only action is attempted",
      "to be executed by console."
    })
    String noConsole,

    @Comment({
      "This message will be displayed while a log is being created."
    })
    String creatingLog,

    @Comment({
      "This message will be displayed when a log is created successfully."
    })
    String logCreated,

    @Comment({
      "This message will be displayed when a log is not created successfully."
    })
    String couldntMakeLog,

    @Comment({
      "This message will be displayed when a player attempts to enchant an item",
      "with an unknown enchantment."
    })
    String wrongEnchantmentName,

    @Comment({
      "This message will be displayed when a player attempts to enchant an item",
      "with an enchantment level that's out of bounds."
    })
    String wrongEnchantmentLevel,

    @Comment({
      "This message will be displayed when a player successfully enchants an item."
    })
    String enchanted,

    @Comment({
      "This message will be displayed when an action is successfully activated."
    })
    String activated,

    @Comment({
      "This message will be displayed when a command receives an invalid parameter (argument)."
    })
    String wrongParameter,

    @Comment({
      "This message will be displayed when an enchantment is successfully downloaded.",
      "Seeing this message does not mean that the message is loaded (see \"loaded\")"
    })
    String downloaded,

    @Comment({
      "This message will be displayed when an enchantment is successfully loaded."
    })
    String loaded,

    @Comment({
      "This message will be displayed when an enchantment is successfully unloaded."
    })
    String unloaded,

    @Comment({
      "This message will be displayed when a player toggles an enchantment."
    })
    String toggled,

    @Comment({
      "This message will be displayed when a player attempts to enchant an item",
      "that will overflow the enchantment limit."
    })
    String overTheLimit,

    @Comment({
      "This message will be displayed when the plugin is reloaded with \"/ue reload\"."
    })
    String reloaded
  ) {}
}

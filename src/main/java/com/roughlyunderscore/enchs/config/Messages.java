package com.roughlyunderscore.enchs.config;

import com.roughlyunderscore.enchs.UnderscoreEnchants;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import static com.roughlyunderscore.enchs.util.general.Utils.*;

@ToString
@EqualsAndHashCode
/*
This is a way to quickly fetch the messages from the configuration.
 */
public class Messages {

  private UnderscoreEnchants enchants;

  public String NO_PERMS,
    NO_CONSOLE,
    CREATING_LOG,
    LOG_CREATED,
    NO_LOG,
    UNLOADED,
    WRONG_NAME,
    WRONG_LEVEL,
    ENCHANTED,
    ACTIVATED,
    DOWNLOADED,
    LOADED,
    WRONG_PARAMETER,
    TOGGLED,
    OVER_THE_LIMIT,
    RELOADED;

  public Messages(final String string, final UnderscoreEnchants enchants) {
    if (string.equals("Default")) {
      this.enchants = enchants;
      String prefix = enchants.getMainConfig().PREFIX;

      NO_PERMS = prefix + getMessage("no-perms");
      NO_CONSOLE = prefix + getMessage("no-console");
      CREATING_LOG = prefix + getMessage("creating-log");
      LOG_CREATED = prefix + getMessage("log-created");
      NO_LOG = prefix + getMessage("couldnt-make-log");
      UNLOADED = prefix + getMessage("unloaded");
      WRONG_NAME = prefix + getMessage("wrong-enchantment-name");
      WRONG_LEVEL = prefix + getMessage("wrong-enchantment-level");
      ENCHANTED = prefix + getMessage("enchanted");
      ACTIVATED = prefix + getMessage("activated");
      DOWNLOADED = prefix + getMessage("downloaded");
      LOADED = prefix + getMessage("loaded");
      TOGGLED = prefix + getMessage("toggled");
      WRONG_PARAMETER = prefix + getMessage("wrong-parameter");
      OVER_THE_LIMIT = prefix + getMessage("over-the-limit");
      RELOADED = prefix + getMessage("reloaded");
    }
  }

  public Messages(final UnderscoreEnchants enchants) {
    this("Default", enchants);
  }

  String getMessage(final String arg) {
    return format(enchants.getConfig().getString(String.format("messages.%s.%s", enchants.getMainConfig().LANGUAGE, arg), "Message " + arg + " not found"));
  }
}

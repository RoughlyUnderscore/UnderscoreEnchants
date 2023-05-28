package com.roughlyunderscore.enchs.config.messages;

import com.roughlyunderscore.enchs.UnderscoreEnchants;
import de.exlll.configlib.YamlConfigurations;
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
      final String prefix = enchants.getConfigValues().PREFIX;

      switch (enchants.getConfigValues().LANGUAGE.toLowerCase()) {
        case "en" -> {
          enchants.getEnglishMsgConfig();
        }
      }
    }
  }

  public Messages(final UnderscoreEnchants enchants) {
    this("Default", enchants);
  }
}

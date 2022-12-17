package com.roughlyunderscore.enchs.parsers.action.events.consume;

import lombok.ToString;

@ToString
public enum ItemConsumeActionPlaceholders {
  ITEM_NAME("<item_name>", "<name_of_item>", "<item_type>", "<[Ljava.lang.Object>"); // YetAnotherPlaceholderEasterEgg

  private final String[] aliases;

  ItemConsumeActionPlaceholders(final String... aliases0) {
    String[] aliases = new String[aliases0.length];
    for (int i = 0; i < aliases0.length; i++) aliases[i] = aliases0[i].toUpperCase();

    this.aliases = aliases;
  }

  ItemConsumeActionPlaceholders() {
    this.aliases = new String[] {this.name()};
  }


  public boolean matches(final String arg) {
    for (String name : aliases) if (name.equals(arg.toUpperCase())) return true;
    return false;
  }

  public static ItemConsumeActionPlaceholders lookup(final String arg) {
    for (ItemConsumeActionPlaceholders condition : values()) if (condition.matches(arg)) return condition;
    return null;
  }
}

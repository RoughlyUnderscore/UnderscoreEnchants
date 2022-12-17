package com.roughlyunderscore.enchs.parsers.action.events.item;

import lombok.ToString;

@ToString
public enum ItemBreakActionPlaceholders {
  ITEM_NAME("<item_name>", "<name_of_item>", "<item_type>");

  private final String[] aliases;

  ItemBreakActionPlaceholders(final String... aliases0) {
    String[] aliases = new String[aliases0.length];
    for (int i = 0; i < aliases0.length; i++) aliases[i] = aliases0[i].toUpperCase();

    this.aliases = aliases;
  }

  ItemBreakActionPlaceholders() {
    this.aliases = new String[] {this.name()};
  }


  public boolean matches(final String arg) {
    for (String name : aliases) if (name.equals(arg.toUpperCase())) return true;
    return false;
  }

  public static ItemBreakActionPlaceholders lookup(final String arg) {
    for (ItemBreakActionPlaceholders condition : values()) if (condition.matches(arg)) return condition;
    return null;
  }
}

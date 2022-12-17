package com.roughlyunderscore.enchs.parsers.action.events.sneak;

import lombok.ToString;

@ToString
public enum SneakActionPlaceholders {
  ;

  private final String[] aliases;

  SneakActionPlaceholders(final String... aliases0) {
    String[] aliases = new String[aliases0.length];
    for (int i = 0; i < aliases0.length; i++) aliases[i] = aliases0[i].toUpperCase();

    this.aliases = aliases;
  }

  SneakActionPlaceholders() {
    this.aliases = new String[] {this.name()};
  }


  public boolean matches(final String arg) {
    for (String name : aliases) if (name.equals(arg.toUpperCase())) return true;
    return false;
  }

  public static SneakActionPlaceholders lookup(final String arg) {
    for (SneakActionPlaceholders condition : values()) if (condition.matches(arg)) return condition;
    return null;
  }
}

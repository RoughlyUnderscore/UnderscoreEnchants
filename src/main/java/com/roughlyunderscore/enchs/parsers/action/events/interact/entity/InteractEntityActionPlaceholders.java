package com.roughlyunderscore.enchs.parsers.action.events.interact.entity;

import lombok.ToString;

@ToString
public enum InteractEntityActionPlaceholders {
  ;

  private final String[] aliases;

  InteractEntityActionPlaceholders(final String... aliases0) {
    String[] aliases = new String[aliases0.length];
    for (int i = 0; i < aliases0.length; i++) aliases[i] = aliases0[i].toUpperCase();

    this.aliases = aliases;
  }

  InteractEntityActionPlaceholders() {
    this.aliases = new String[] {this.name()};
  }


  public boolean matches(final String arg) {
    for (String name : aliases) if (name.equals(arg.toUpperCase())) return true;
    return false;
  }

  public static InteractEntityActionPlaceholders lookup(final String arg) {
    for (InteractEntityActionPlaceholders condition : values()) if (condition.matches(arg)) return condition;
    return null;
  }
}

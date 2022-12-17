package com.roughlyunderscore.enchs.parsers.action.events.projectile;

import lombok.ToString;

@ToString
public enum BowShootActionPlaceholders {
  FORCE("<force>", "<bow_force>", "<bowforce>", "<shoot_force>", "<shootforce>");

  private final String[] aliases;

  BowShootActionPlaceholders(final String... aliases0) {
    String[] aliases = new String[aliases0.length];
    for (int i = 0; i < aliases0.length; i++) aliases[i] = aliases0[i].toUpperCase();

    this.aliases = aliases;
  }

  BowShootActionPlaceholders() {
    this.aliases = new String[] {this.name()};
  }


  public boolean matches(final String arg) {
    for (String name : aliases) if (name.equals(arg.toUpperCase())) return true;
    return false;
  }

  public static BowShootActionPlaceholders lookup(final String arg) {
    for (BowShootActionPlaceholders condition : values()) if (condition.matches(arg)) return condition;
    return null;
  }
}

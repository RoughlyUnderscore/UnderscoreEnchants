package com.roughlyunderscore.enchs.parsers.action.events.projectile;

import lombok.ToString;

@ToString
public enum BowShootActions {
  ;


  private final String[] aliases;

  BowShootActions(final String... aliases0) {
    String[] aliases = new String[aliases0.length];
    for (int i = 0; i < aliases0.length; i++) aliases[i] = aliases0[i].toUpperCase();

    this.aliases = aliases;
  }

  @SuppressWarnings("unused")
  BowShootActions() {
    this.aliases = new String[] {this.name()};
  }


  final public boolean matches(final String arg) {
    for (String name : aliases) if (name.equals(arg.toUpperCase())) return true;
    return false;
  }

  public static BowShootActions lookup(final String arg) {
    for (BowShootActions action : values()) if (action.matches(arg)) return action;
    return null;
  }
}

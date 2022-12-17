package com.roughlyunderscore.enchs.parsers.action.events.bow;

import lombok.ToString;

@ToString
public enum BowHitActionPlaceholders {
  DAMAGE("<damage>", "<dmg>", "<damag>", "<hurt>", "<hurtdmg>", "<hurtdamage>");

  private final String[] aliases;

  BowHitActionPlaceholders(final String... aliases0) {
    String[] aliases = new String[aliases0.length];
    for (int i = 0; i < aliases0.length; i++) aliases[i] = aliases0[i].toUpperCase();

    this.aliases = aliases;
  }

  BowHitActionPlaceholders() {
    this.aliases = new String[] {this.name()};
  }


  public boolean matches(final String arg) {
    for (String name : aliases) if (name.equals(arg.toUpperCase())) return true;
    return false;
  }

  public static BowHitActionPlaceholders lookup(final String arg) {
    for (BowHitActionPlaceholders condition : values()) if (condition.matches(arg)) return condition;
    return null;
  }
}

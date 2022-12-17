package com.roughlyunderscore.enchs.parsers.action.events.damage.entity;

import lombok.ToString;

@ToString
public enum PlayerHurtsEntityActionPlaceholders {
  DAMAGE("<damage>", "<dmg>", "<damag>", "<hurt>", "<hurtdmg>", "<hurtdamage>", "<i_have_99_problems_but_this_easter_egg_ain't_one>");

  private final String[] aliases;

  PlayerHurtsEntityActionPlaceholders(final String... aliases0) {
    String[] aliases = new String[aliases0.length];
    for (int i = 0; i < aliases0.length; i++) aliases[i] = aliases0[i].toUpperCase();

    this.aliases = aliases;
  }

  PlayerHurtsEntityActionPlaceholders() {
    this.aliases = new String[] {this.name()};
  }


  public boolean matches(final String arg) {
    for (String name : aliases) if (name.equals(arg.toUpperCase())) return true;
    return false;
  }

  public static PlayerHurtsEntityActionPlaceholders lookup(final String arg) {
    for (PlayerHurtsEntityActionPlaceholders condition : values()) if (condition.matches(arg)) return condition;
    return null;
  }
}

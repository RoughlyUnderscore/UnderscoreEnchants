package com.roughlyunderscore.enchs.parsers.condition.events.pvp;

import lombok.ToString;

@ToString
// Aliases
public enum PVPCondition {
  DAMAGE("damage", "dam", "dmg", "dmg-is", "dam-is", "damage-is", "is-damage", "is-dam", "is-dmg"),
  DAMAGE_LETHAL("damage-lethal", "dam-lethal", "dmg-lethal", "dmg-is-lethal", "dam-is-lethal", "damage-is-lethal", "is-damage-lethal", "is-dam-lethal", "is-dmg-lethal");

  private final String[] name;

  PVPCondition(String... name) {
    String[] names = new String[name.length];
    for (int i = 0; i < name.length; i++) names[i] = name[i].toUpperCase();

    this.name = names;
  }

  PVPCondition() {
    this.name = new String[] {this.name()};
  }


  public boolean matches(final String arg) {
    for (String name : name) if (name.equals(arg.toUpperCase())) return true;
    return false;
  }

  public static PVPCondition lookup(final String arg) {
    for (PVPCondition condition : values()) if (condition.matches(arg)) return condition;
    return null;
  }
}

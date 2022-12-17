package com.roughlyunderscore.enchs.parsers.condition.events.bow;

import lombok.ToString;

@ToString
// Aliases
// P.S. As of 2.1, is identical to PVPCondition, but is still separate for extensibility purposes
public enum BowHitCondition {
  DAMAGE("damage", "dam", "dmg", "dmg-is", "dam-is", "damage-is", "is-damage", "is-dam", "is-dmg"),
  DAMAGE_LETHAL("damage-lethal", "dam-lethal", "dmg-lethal", "dmg-is-lethal", "dam-is-lethal", "damage-is-lethal", "is-damage-lethal", "is-dam-lethal", "is-dmg-lethal");

  private final String[] name;

  BowHitCondition(String... name) {
    String[] names = new String[name.length];
    for (int i = 0; i < name.length; i++) names[i] = name[i].toUpperCase();

    this.name = names;
  }

  BowHitCondition() {
    this.name = new String[] {this.name()};
  }


  public boolean matches(final String arg) {
    for (String name : name) if (name.equals(arg.toUpperCase())) return true;
    return false;
  }

  public static BowHitCondition lookup(final String arg) {
    for (BowHitCondition condition : values()) if (condition.matches(arg)) return condition;
    return null;
  }
}

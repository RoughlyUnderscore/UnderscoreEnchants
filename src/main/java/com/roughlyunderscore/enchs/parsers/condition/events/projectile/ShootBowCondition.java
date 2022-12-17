package com.roughlyunderscore.enchs.parsers.condition.events.projectile;

import lombok.ToString;

@ToString
// Aliases
public enum ShootBowCondition {
  FORCE("force", "is-force", "force-is");

  private final String[] name;

  ShootBowCondition(String... name) {
    String[] names = new String[name.length];
    for (int i = 0; i < name.length; i++) names[i] = name[i].toUpperCase();

    this.name = names;
  }

  ShootBowCondition() {
    this.name = new String[] {this.name()};
  }


  public boolean matches(final String arg) {
    for (String name : name) if (name.equals(arg.toUpperCase())) return true;
    return false;
  }

  public static ShootBowCondition lookup(final String arg) {
    for (ShootBowCondition condition : values()) if (condition.matches(arg)) return condition;
    return null;
  }
}

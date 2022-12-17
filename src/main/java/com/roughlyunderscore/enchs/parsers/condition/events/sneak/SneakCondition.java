package com.roughlyunderscore.enchs.parsers.condition.events.sneak;

import lombok.ToString;

@ToString
// Aliases
public enum SneakCondition {
  SNEAKED("sneak", "sneaked", "sneaking", "is-sneaking", "is-sneaked", "is-sneak"),
  UNSNEAKED("unsneak", "unsneaked", "unsneaking", "is-unsneaking", "is-unsneaked", "is-unsneak");

  private final String[] name;

  SneakCondition(String... name) {
    String[] names = new String[name.length];
    for (int i = 0; i < name.length; i++) names[i] = name[i].toUpperCase();

    this.name = names;
  }

  SneakCondition() {
    this.name = new String[] {this.name()};
  }


  public boolean matches(final String arg) {
    for (String name : name) if (name.equals(arg.toUpperCase())) return true;
    return false;
  }

  public static SneakCondition lookup(final String arg) {
    for (SneakCondition condition : values()) if (condition.matches(arg)) return condition;
    return null;
  }
}

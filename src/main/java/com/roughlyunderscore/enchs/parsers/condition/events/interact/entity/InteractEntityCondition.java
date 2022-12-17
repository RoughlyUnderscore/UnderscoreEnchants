package com.roughlyunderscore.enchs.parsers.condition.events.interact.entity;

import lombok.ToString;

@ToString
// Aliases
public enum InteractEntityCondition {
  ; // maybe it is currently empty... so what?!

  private final String[] name;

  InteractEntityCondition(String... name) {
    String[] names = new String[name.length];
    for (int i = 0; i < name.length; i++) names[i] = name[i].toUpperCase();

    this.name = names;
  }

  InteractEntityCondition() {
    this.name = new String[] {this.name()};
  }


  public boolean matches(final String arg) {
    for (String name : name) if (name.equals(arg.toUpperCase())) return true;
    return false;
  }

  public static InteractEntityCondition lookup(final String arg) {
    for (InteractEntityCondition condition : values()) if (condition.matches(arg)) return condition;
    return null;
  }
}

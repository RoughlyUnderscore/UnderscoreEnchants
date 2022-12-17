package com.roughlyunderscore.enchs.parsers.condition.generic.entity;

import lombok.ToString;

@ToString
// Aliases
public enum EntityCondition {
  ENTITY_SWIMMING("ENTITY-SWIMMING", "ENTITY-SWIM", "ENTITY-SWIMS", "ENTITY-IS-SWIMMING", "ENTITY-IS-SWIM"),
  ENTITY_ON_FIRE("ENTITY-ON-FIRE", "ENTITY-ON-FIRES", "ENTITY-IS-ON-FIRE", "ENTITY-IS-ON-FIRES", "ENTITY-IS-BURNING", "ENTITY-IS-BURN", "ENTITY-IGNITED", "ENTITY-IS-IGNITED"),
  ENTITY_ON_HIGHEST_BLOCK("ENTITY-ON-HIGHEST-BLOCK", "ENTITY-IS-ON-HIGHEST-BLOCK", "ENTITY-IS-ON-TOP", "ENTITY-IS-ON-TOP-BLOCK"),

  HEALTH("HEALTH", "IS-HEALTH", "HEALTH-IS");

  private final String[] name;

  EntityCondition(String... name) {
    String[] names = new String[name.length];
    for (int i = 0; i < name.length; i++) names[i] = name[i].toUpperCase();

    this.name = names;
  }

  EntityCondition() {
    this.name = new String[] {this.name()};
  }


  public boolean matches(final String arg) {
    for (String name : name) if (name.equals(arg.toUpperCase())) return true;
    return false;
  }

  public static EntityCondition lookup(final String arg) {
    for (EntityCondition condition : values()) if (condition.matches(arg)) return condition;
    return null;
  }
}

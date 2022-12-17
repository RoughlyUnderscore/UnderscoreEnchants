package com.roughlyunderscore.enchs.parsers.condition.generic.player;

import lombok.ToString;

@ToString
// Aliases
public enum PlayerCondition {
  PDC_MATCH("PDC", "PDC-MATCH", "PDC-MATCHES", "PDC-MATCHING", "PDC-MATCHED"),
  SNEAKING("SNEAKING", "SNEAK", "SNEAKS", "SHIFT", "SHIFTS", "IS-SNEAKING", "IS-SNEAK", "IS-SHIFT", "IS-SHIFTING"),
  SPRINTING("SPRINTING", "SPRINT", "SPRINTS", "IS-SPRINTING", "IS-SPRINT", "RUN", "RUNS", "IS-RUNNING", "IS-RUN"),
  SWIMMING("SWIMMING", "SWIM", "SWIMS", "IS-SWIMMING", "IS-SWIM"),
  FLYING("FLYING", "FLY", "FLIES", "IS-FLYING", "IS-FLY"),
  ON_FIRE("ON-FIRE", "ON-FIRES", "IS-ON-FIRE", "IS-ON-FIRES", "IS-BURNING", "IS-BURN", "IGNITED", "IS-IGNITED"),
  ON_HIGHEST_BLOCK("ON-HIGHEST-BLOCK", "IS-ON-HIGHEST-BLOCK", "IS-ON-TOP", "IS-ON-TOP-BLOCK"),
  RAIN("RAIN", "RAINS", "IS-RAINING", "DOWNFALL", "IS-DOWNFALL"),
  CLEAR("CLEAR", "IS-CLEAR", "IS-NOT-RAINING", "IS-NOT-DOWNFALL"),
  THUNDER("THUNDER", "THUNDERS", "IS-THUNDERING", "IS-THUNDER", "IS-THUNDER-STORM", "IS-THUNDER-STORMING"),
  DAY("DAY", "IS-DAY", "IS-DAYTIME", "IS-DAY-TIME"),
  NIGHT("NIGHT", "IS-NIGHT", "IS-NIGHTTIME", "IS-NIGHT-TIME"),
  OVERWORLD("OVERWORLD", "IS-OVERWORLD", "IS-OVER-WORLD", "IS-OVERWORLD-LEVEL", "IS-OVER-WORLD-LEVEL"),
  NETHER("NETHER", "IS-NETHER", "IS-NETHER-LEVEL", "IS-NETHER-WORLD", "IS-NETHER-WORLD-LEVEL", "HELL", "IS-HELL", "IS-HELL-LEVEL", "IS-HELL-WORLD", "IS-HELL-WORLD-LEVEL"),
  END("END", "IS-END", "IS-END-LEVEL", "IS-END-WORLD", "IS-END-WORLD-LEVEL", "THE-END", "IS-THE-END", "IS-THE-END-LEVEL", "IS-THE-END-WORLD", "IS-THE-END-WORLD-LEVEL"),
  OP("OP", "IS-OP", "IS-OPERATOR", "OPERATOR"),
  BLOCKING("BLOCKING", "IS-BLOCKING", "BLOCKING-IS", "BLOCK", "IS-BLOCK", "BLOCK-IS"),

  HEALTH("HEALTH", "IS-HEALTH", "HEALTH-IS"),
  FOOD("FOOD", "IS-FOOD", "FOOD-IS"),
  AIR("AIR", "IS-AIR", "AIR-IS"),
  GODMODE("GODMODE", "IS-GODMODE", "GODMODE-IS", "GOD", "IS-GOD", "GOD-IS");

  private final String[] name;

  PlayerCondition(String... name) {
    String[] names = new String[name.length];
    for (int i = 0; i < name.length; i++) names[i] = name[i].toUpperCase();

    this.name = names;
  }

  PlayerCondition() {
    this.name = new String[] {this.name()};
  }


  public boolean matches(final String arg) {
    for (String name : name) if (name.equals(arg.toUpperCase())) return true;
    return false;
  }

  public static PlayerCondition lookup(final String arg) {
    for (PlayerCondition condition : values()) if (condition.matches(arg)) return condition;
    return null;
  }
}

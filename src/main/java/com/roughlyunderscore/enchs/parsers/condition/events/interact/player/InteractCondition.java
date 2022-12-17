package com.roughlyunderscore.enchs.parsers.condition.events.interact.player;

import lombok.ToString;

@ToString
// Aliases
public enum InteractCondition {
  BLOCK_IS("block-is", "block-type", "block-type-is", "type-block-is"),
  CLICKED_LMB_AIR("clicked-lmb-air", "lmb-air", "clicked-air-lmb", "air-lmb"),
  CLICKED_RMB_AIR("clicked-rmb-air", "rmb-air", "clicked-air-rmb", "air-rmb"),
  CLICKED_LMB_BLOCK("clicked-lmb-block", "lmb-block", "clicked-block-lmb", "block-lmb"),
  CLICKED_RMB_BLOCK("clicked-rmb-block", "rmb-block", "clicked-block-rmb", "block-rmb"),
  PHYSICAL("physical", "is-physical", "is-physic", "is-phys", "physic", "phys", "is-physical", "physical-is", "physical-action");

  private final String[] name;

  InteractCondition(String... name) {
    String[] names = new String[name.length];
    for (int i = 0; i < name.length; i++) names[i] = name[i].toUpperCase();

    this.name = names;
  }

  InteractCondition() {
    this.name = new String[] {this.name()};
  }


  public boolean matches(final String arg) {
    for (String name : name) if (name.equals(arg.toUpperCase())) return true;
    return false;
  }

  public static InteractCondition lookup(final String arg) {
    for (InteractCondition condition : values()) if (condition.matches(arg)) return condition;
    return null;
  }
}

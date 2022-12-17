package com.roughlyunderscore.enchs.parsers.condition.events.block;

import lombok.ToString;

@ToString
// Aliases
public enum BlockBreakCondition {
  BLOCK_IS("block-is", "is-block", "block", "block-type", "block-type-is", "is-block-type");

  private final String[] name;

  BlockBreakCondition(String... name) {
    String[] names = new String[name.length];
    for (int i = 0; i < name.length; i++) names[i] = name[i].toUpperCase();

    this.name = names;
  }

  BlockBreakCondition() {
    this.name = new String[] {this.name()};
  }


  public boolean matches(final String arg) {
    for (String name : name) if (name.equals(arg.toUpperCase())) return true;
    return false;
  }

  public static BlockBreakCondition lookup(final String arg) {
    for (BlockBreakCondition condition : values()) if (condition.matches(arg)) return condition;
    return null;
  }
}

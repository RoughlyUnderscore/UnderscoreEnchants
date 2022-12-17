package com.roughlyunderscore.enchs.parsers.condition.events.move;

import lombok.ToString;

@ToString
// Aliases
public enum MoveCondition {
  FROM_IS("from-is", "from-type-is", "from-type", "from", "from-block-is", "from-block-type-is", "from-block-type", "from-block"),
  TO_IS("to-is", "to-type-is", "to-type", "to", "to-block-is", "to-block-type-is", "to-block-type", "to-block"),
  JUMP("jump", "by-jump", "jumped", "by-jumping"),
  SAME_BLOCK("same-block", "same"),
  NOT_SAME_BLOCK("not-same-block", "not-same", "different-block", "different"),
  HEAD_ROTATE("head-rotate", "head-rotated", "by-head-rotate");

  private final String[] name;

  MoveCondition(String... name) {
    String[] names = new String[name.length];
    for (int i = 0; i < name.length; i++) names[i] = name[i].toUpperCase();

    this.name = names;
  }

  MoveCondition() {
    this.name = new String[] {this.name()};
  }


  public boolean matches(final String arg) {
    for (String name : name) if (name.equals(arg.toUpperCase())) return true;
    return false;
  }

  public static MoveCondition lookup(final String arg) {
    for (MoveCondition condition : values()) if (condition.matches(arg)) return condition;
    return null;
  }
}

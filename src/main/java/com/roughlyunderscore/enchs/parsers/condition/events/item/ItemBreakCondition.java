package com.roughlyunderscore.enchs.parsers.condition.events.item;

import lombok.ToString;

@ToString
// Aliases
public enum ItemBreakCondition {
  ITEM_IS("item-is", "is-item", "item", "item-type", "item-type-is", "is-item-type");

  private final String[] name;

  ItemBreakCondition(String... name) {
    String[] names = new String[name.length];
    for (int i = 0; i < name.length; i++) names[i] = name[i].toUpperCase();

    this.name = names;
  }

  ItemBreakCondition() {
    this.name = new String[] {this.name()};
  }


  public boolean matches(final String arg) {
    for (String name : name) if (name.equals(arg.toUpperCase())) return true;
    return false;
  }

  public static ItemBreakCondition lookup(final String arg) {
    for (ItemBreakCondition condition : values()) if (condition.matches(arg)) return condition;
    return null;
  }
}

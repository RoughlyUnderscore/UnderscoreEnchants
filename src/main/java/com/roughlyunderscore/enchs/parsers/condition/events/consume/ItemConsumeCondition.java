package com.roughlyunderscore.enchs.parsers.condition.events.consume;

import lombok.ToString;

@ToString
// Aliases
public enum ItemConsumeCondition {
  FOOD_IS("food-is", "food-type", "food-type-is"),
  IS_VEGETARIAN("is-vegetarian", "vegetarian", "vegeterian", "is-vegeterian"),
  IS_PESCETARIAN("is-pesceterian", "pesceterian", "pescetarian", "is-pescetarian"),
  IS_POTION("is-potion", "potion"),
  IS_HONEY("is-honey", "honey"),
  IS_MILK("is-milk", "milk"),
  IS_FOOD("is-food", "food");

  private final String[] name;

  ItemConsumeCondition(String... name) {
    String[] names = new String[name.length];
    for (int i = 0; i < name.length; i++) names[i] = name[i].toUpperCase();

    this.name = names;
  }

  ItemConsumeCondition() {
    this.name = new String[] {this.name()};
  }


  public boolean matches(final String arg) {
    for (String name : name) if (name.equals(arg.toUpperCase())) return true;
    return false;
  }

  public static ItemConsumeCondition lookup(final String arg) {
    for (ItemConsumeCondition condition : values()) if (condition.matches(arg)) return condition;
    return null;
  }
}

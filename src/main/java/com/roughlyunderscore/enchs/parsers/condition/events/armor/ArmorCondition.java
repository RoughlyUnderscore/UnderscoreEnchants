package com.roughlyunderscore.enchs.parsers.condition.events.armor;

import lombok.ToString;

@ToString
// Aliases
public enum ArmorCondition {
  EQUIPPED_HELMET("equipped-helmet", "equipped-helm", "equipped-h", "equipped-hat", "equipped-helmet-is", "equipped-helm-is", "equipped-h-is", "equipped-hat-is", "is-equipped-helmet", "is-equipped-helm", "is-equipped-h", "is-equipped-hat"),
  EQUIPPED_CHESTPLATE("equipped-chestplate", "equipped-chest", "equipped-c", "equipped-chestplate-is", "equipped-chest-is", "equipped-c-is", "is-equipped-chestplate", "is-equipped-chest", "is-equipped-c"),
  EQUIPPED_LEGGINGS("equipped-leggings", "equipped-legs", "equipped-l", "equipped-leggings-is", "equipped-legs-is", "equipped-l-is", "is-equipped-leggings", "is-equipped-legs", "is-equipped-l"),
  EQUIPPED_BOOTS("equipped-boots", "equipped-b", "equipped-boots-is", "equipped-b-is", "is-equipped-boots", "is-equipped-b"),
  EQUIPPED_ANY("equipped-any", "equipped-a", "equipped-any-is", "equipped-a-is", "is-equipped-any", "is-equipped-a", "equipped"),
  EQUIPPED_IS("equipped-is", "is-equipped", "is-equipped-is"),

  UNEQUIPPED_HELMET("unequipped-helmet", "unequipped-helm", "unequipped-h", "unequipped-hat", "unequipped-helmet-is", "unequipped-helm-is", "unequipped-h-is", "unequipped-hat-is", "is-unequipped-helmet", "is-unequipped-helm", "is-unequipped-h", "is-unequipped-hat"),
  UNEQUIPPED_CHESTPLATE("unequipped-chestplate", "unequipped-chest", "unequipped-c", "unequipped-chestplate-is", "unequipped-chest-is", "unequipped-c-is", "is-unequipped-chestplate", "is-unequipped-chest", "is-unequipped-c"),
  UNEQUIPPED_LEGGINGS("unequipped-leggings", "unequipped-legs", "unequipped-l", "unequipped-leggings-is", "unequipped-legs-is", "unequipped-l-is", "is-unequipped-leggings", "is-unequipped-legs", "is-unequipped-l"),
  UNEQUIPPED_BOOTS("unequipped-boots", "unequipped-b", "unequipped-boots-is", "unequipped-b-is", "is-unequipped-boots", "is-unequipped-b"),
  UNEQUIPPED_ANY("unequipped-any", "unequipped-a", "unequipped-any-is", "unequipped-a-is", "is-unequipped-any", "is-unequipped-a", "unequipped"),
  UNEQUIPPED_IS("unequipped-is", "is-unequipped", "is-unequipped-is");

  private final String[] name;

  ArmorCondition(String... name) {
    String[] names = new String[name.length];
    for (int i = 0; i < name.length; i++) names[i] = name[i].toUpperCase();

    this.name = names;
  }

  ArmorCondition() {
    this.name = new String[] {this.name()};
  }


  public boolean matches(final String arg) {
    for (String name : name) if (name.equals(arg.toUpperCase())) return true;
    return false;
  }

  public static ArmorCondition lookup(final String arg) {
    for (ArmorCondition condition : values()) if (condition.matches(arg)) return condition;
    return null;
  }
}

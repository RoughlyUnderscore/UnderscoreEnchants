package com.roughlyunderscore.enchs.parsers.action.events.damage.player;

import lombok.ToString;

@ToString
public enum PlayerGotHurtActions {
  ;


  private final String[] aliases;

  PlayerGotHurtActions(final String... aliases0) {
    String[] aliases = new String[aliases0.length];
    for (int i = 0; i < aliases0.length; i++) aliases[i] = aliases0[i].toUpperCase();

    this.aliases = aliases;
  }

  @SuppressWarnings("unused")
  PlayerGotHurtActions() {
    this.aliases = new String[] {this.name()};
  }


  final public boolean matches(final String arg) {
    for (String name : aliases) if (name.equals(arg.toUpperCase())) return true;
    return false;
  }

  public static PlayerGotHurtActions lookup(final String arg) {
    for (PlayerGotHurtActions action : values()) if (action.matches(arg)) return action;
    return null;
  }
}

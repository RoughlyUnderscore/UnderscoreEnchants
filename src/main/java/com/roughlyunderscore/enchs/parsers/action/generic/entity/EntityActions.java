package com.roughlyunderscore.enchs.parsers.action.generic.entity;

import lombok.ToString;

@ToString
public enum EntityActions {
  ENTITY_VELOCITY("entity-velocity", "entity-set-velocity", "entity-velocity-set", "entity-set-velocity", "entity-produce-velocity"),
  ENTITY_EFFECT("entity-effect", "entity-add-effect", "entity-effect-add"),
  ENTITY_REMOVE_EFFECT("entity-remove-effect", "entity-effect-remove"),
  ENTITY_REMOVE_BUFFS("entity-remove-buffs", "entity-buffs-remove"),
  ENTITY_REMOVE_DEBUFFS("entity-remove-debuffs", "entity-debuffs-remove"),
  ENTITY_STRIKE_FAKE_LIGHTNING("entity-strike-fake-lightning", "entity-fake-lightning-strike", "entity-strike-lightning-fake", "entity-fake-strike-lightning", "entity-lightning-strike-fake", "entity-fake-lightning-strike"),
  ENTITY_STRIKE_LIGHTNING("entity-strike-lightning", "entity-lightning-strike", "entity-strike-real-lightning", "entity-real-lightning-strike", "entity-real-strike-lightning", "entity-lightning-strike-real"),
  ENTITY_PARTICLE("entity-particle", "entity-particle-send", "entity-send-particle"),
  ENTITY_PARTICLE_BOOTS("entity-particle-boots", "entity-boots-particle", "entity-boots-particle-send", "entity-send-boots-particle"),
  ENTITY_TELEPORT("entity-teleport", "teleport-entity", "entity-tp", "tp-entity", "entity-move", "move-entity", "entity-move-to", "move-entity-to", "entity-teleport-to", "teleport-entity-to", "entity-tp-to", "tp-entity-to", "move", "tp"),
  ENTITY_DIRECTION("entity-direction", "entity-set-direction", "entity-direction-set", "entity-set-direction", "entity-produce-direction"),
  ENTITY_WORLD("entity-world", "entity-set-world", "entity-world-set", "entity-set-world", "entity-produce-world"),
  ENTITY_FORWARD("entity-forward", "entity-set-forward", "entity-forward-set", "entity-set-forward", "entity-produce-forward"),
  ENTITY_SET_HEALTH("entity-set-health", "entity-health-set", "entity-health-set", "entity-set-health", "entity-produce-health", "entity-health", "entity-hp", "entity-set-hp", "entity-hp-set", "entity-set-hp", "entity-produce-hp"),
  ENTITY_SET_MAX_HEALTH("entity-set-max-health", "entity-max-health-set", "entity-max-health-set", "entity-set-max-health", "entity-produce-max-health", "entity-max-health", "entity-set-max-hp", "entity-max-hp-set", "entity-max-hp-set", "entity-set-max-hp", "entity-produce-max-hp"),
  ENTITY_SET_FIRE("entity-set-fire", "entity-fire-set", "entity-fire-set", "entity-set-fire", "entity-produce-fire", "entity-fire", "entity-set-on-fire", "entity-on-fire-set", "entity-on-fire-set", "entity-set-on-fire", "entity-produce-on-fire"),
  ENTITY_SEND_ARROW("entity-send-arrow", "entity-arrow-send", "entity-arrow-send", "entity-send-arrow", "entity-produce-arrow", "entity-arrow", "entity-shoot-arrow", "entity-arrow-shoot", "entity-arrow-shoot", "entity-shoot-arrow", "entity-produce-arrow"),
  ENTITY_SEND_FIREBALL("entity-send-fireball", "entity-fireball-send", "entity-fireball-send", "entity-send-fireball", "entity-produce-fireball", "entity-fireball", "entity-shoot-fireball", "entity-fireball-shoot", "entity-fireball-shoot", "entity-shoot-fireball", "entity-produce-fireball"),;


  private final String[] aliases;

  EntityActions(final String... aliases0) {
    String[] aliases = new String[aliases0.length];
    for (int i = 0; i < aliases0.length; i++) aliases[i] = aliases0[i].toUpperCase();

    this.aliases = aliases;
  }

  @SuppressWarnings("unused")
  EntityActions() {
    this.aliases = new String[] {this.name()};
  }


  final public boolean matches(final String arg) {
    for (String name : aliases) if (name.equals(arg.toUpperCase())) return true;
    return false;
  }

  public static EntityActions lookup(final String arg) {
    for (EntityActions action : values()) if (action.matches(arg)) return action;
    return null;
  }
}

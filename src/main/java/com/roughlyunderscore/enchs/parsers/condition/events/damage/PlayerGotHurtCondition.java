package com.roughlyunderscore.enchs.parsers.condition.events.damage;

import lombok.ToString;

@ToString
// Aliases
public enum PlayerGotHurtCondition {
  DAMAGE("damage", "dam", "dmg", "dmg-is", "dam-is", "damage-is", "is-damage", "is-dam", "is-dmg"),
  DAMAGE_LETHAL("damage-lethal", "dam-lethal", "dmg-lethal", "dmg-is-lethal", "dam-is-lethal", "damage-is-lethal", "is-damage-lethal", "is-dam-lethal", "is-dmg-lethal"),

  CAUSE_BLOCK_EXPLOSION("cause-block-explosion", "cause-block-explosions", "cause-block-explosion-is", "cause-block-explosions-is", "cause-is-block-explosion", "cause-is-block-explosions", "is-cause-block-explosion", "is-cause-block-explosions", "caused-by-block-explosion"),
  CAUSE_HAZARDOUS_BLOCK("cause-hazardous-block", "cause-hazardous-blocks", "cause-hazardous-block-is", "cause-hazardous-blocks-is", "cause-is-hazardous-block", "cause-is-hazardous-blocks", "is-cause-hazardous-block", "is-cause-hazardous-blocks", "caused-by-hazardous-block"),
  CAUSE_ENTITY_CRAMMING("cause-entity-cramming", "cause-entity-crammings", "cause-entity-cramming-is", "cause-entity-crammings-is", "cause-is-entity-cramming", "cause-is-entity-crammings", "is-cause-entity-cramming", "is-cause-entity-crammings", "caused-by-entity-cramming"),
  CAUSE_UNKNOWN_SOURCE("cause-unknown-source", "cause-unknown-sources", "cause-unknown-source-is", "cause-unknown-sources-is", "cause-is-unknown-source", "cause-is-unknown-sources", "is-cause-unknown-source", "is-cause-unknown-sources", "caused-by-unknown-source"),
  CAUSE_DRAGON_BREATH("cause-dragon-breath", "cause-dragon-breaths", "cause-dragon-breath-is", "cause-dragon-breaths-is", "cause-is-dragon-breath", "cause-is-dragon-breaths", "is-cause-dragon-breath", "is-cause-dragon-breaths", "caused-by-dragon-breath"),
  CAUSE_ENTITY_ATTACK("cause-entity-attack", "cause-entity-attacks", "cause-entity-attack-is", "cause-entity-attacks-is", "cause-is-entity-attack", "cause-is-entity-attacks", "is-cause-entity-attack", "is-cause-entity-attacks", "caused-by-entity-attack"),
  CAUSE_ENTITY_EXPLOSION("cause-entity-explosion", "cause-entity-explosions", "cause-entity-explosion-is", "cause-entity-explosions-is", "cause-is-entity-explosion", "cause-is-entity-explosions", "is-cause-entity-explosion", "is-cause-entity-explosions", "caused-by-entity-explosion"),
  CAUSE_ENTITY_SWEEP_ATTACK("cause-entity-sweep-attack", "cause-entity-sweep-attacks", "cause-entity-sweep-attack-is", "cause-entity-sweep-attacks-is", "cause-is-entity-sweep-attack", "cause-is-entity-sweep-attacks", "is-cause-entity-sweep-attack", "is-cause-entity-sweep-attacks", "caused-by-entity-sweep-attack"),
  CAUSE_FALLING_BLOCK("cause-falling-block", "cause-falling-blocks", "cause-falling-block-is", "cause-falling-blocks-is", "cause-is-falling-block", "cause-is-falling-blocks", "is-cause-falling-block", "is-cause-falling-blocks", "caused-by-falling-block"),
  CAUSE_FALLING("cause-falling", "cause-fallings", "cause-falling-is", "cause-fallings-is", "cause-is-falling", "cause-is-fallings", "is-cause-falling", "is-cause-fallings", "caused-by-falling"),
  CAUSE_DIRECT_FIRE_EXPOSURE("cause-direct-fire-exposure", "cause-direct-fire-exposures", "cause-direct-fire-exposure-is", "cause-direct-fire-exposures-is", "cause-is-direct-fire-exposure", "cause-is-direct-fire-exposures", "is-cause-direct-fire-exposure", "is-cause-direct-fire-exposures", "caused-by-direct-fire-exposure"),
  CAUSE_FIRE_TICK("cause-fire-tick", "cause-fire-ticks", "cause-fire-tick-is", "cause-fire-ticks-is", "cause-is-fire-tick", "cause-is-fire-ticks", "is-cause-fire-tick", "is-cause-fire-ticks", "caused-by-fire-tick"),
  CAUSE_FLYING_INTO_WALL("cause-flying-into-wall", "cause-flying-into-walls", "cause-flying-into-wall-is", "cause-flying-into-walls-is", "cause-is-flying-into-wall", "cause-is-flying-into-walls", "is-cause-flying-into-wall", "is-cause-flying-into-walls", "caused-by-flying-into-wall"),
  CAUSE_FREEZING("cause-freezing", "cause-freezings", "cause-freezing-is", "cause-freezings-is", "cause-is-freezing", "cause-is-freezings", "is-cause-freezing", "is-cause-freezings", "caused-by-freezing"),
  CAUSE_MAGMA_DAMAGE("cause-magma-damage", "cause-magma-damages", "cause-magma-damage-is", "cause-magma-damages-is", "cause-is-magma-damage", "cause-is-magma-damages", "is-cause-magma-damage", "is-cause-magma-damages", "caused-by-magma-damage"),
  CAUSE_LAVA("cause-lava", "cause-lavas", "cause-lava-is", "cause-lavas-is", "cause-is-lava", "cause-is-lavas", "is-cause-lava", "is-cause-lavas", "caused-by-lava"),
  CAUSE_LIGHTNING("cause-lightning", "cause-lightnings", "cause-lightning-is", "cause-lightnings-is", "cause-is-lightning", "cause-is-lightnings", "is-cause-lightning", "is-cause-lightnings", "caused-by-lightning"),
  CAUSE_MAGIC("cause-magic", "cause-magics", "cause-magic-is", "cause-magics-is", "cause-is-magic", "cause-is-magics", "is-cause-magic", "is-cause-magics", "caused-by-magic"),
  CAUSE_POISON("cause-poison", "cause-poisons", "cause-poison-is", "cause-poisons-is", "cause-is-poison", "cause-is-poisons", "is-cause-poison", "is-cause-poisons", "caused-by-poison"),
  CAUSE_PROJECTILE("cause-projectile", "cause-projectiles", "cause-projectile-is", "cause-projectiles-is", "cause-is-projectile", "cause-is-projectiles", "is-cause-projectile", "is-cause-projectiles", "caused-by-projectile"),
  CAUSE_STARVATION("cause-starvation", "cause-starvations", "cause-starvation-is", "cause-starvations-is", "cause-is-starvation", "cause-is-starvations", "is-cause-starvation", "is-cause-starvations", "caused-by-starvation"),
  CAUSE_SUFFOCATION("cause-suffocation", "cause-suffocations", "cause-suffocation-is", "cause-suffocations-is", "cause-is-suffocation", "cause-is-suffocations", "is-cause-suffocation", "is-cause-suffocations", "caused-by-suffocation"),
  CAUSE_PLUGIN_ENFORCEMENT("cause-plugin-enforcement", "cause-plugin-enforcements", "cause-plugin-enforcement-is", "cause-plugin-enforcements-is", "cause-is-plugin-enforcement", "cause-is-plugin-enforcements", "is-cause-plugin-enforcement", "is-cause-plugin-enforcements", "caused-by-plugin-enforcement"),
  CAUSE_THORNS("cause-thorns", "cause-thornses", "cause-thorns-is", "cause-thornses-is", "cause-is-thorns", "cause-is-thornses", "is-cause-thorns", "is-cause-thornses", "caused-by-thorns"),
  CAUSE_VOID("cause-void", "cause-voids", "cause-void-is", "cause-voids-is", "cause-is-void", "cause-is-voids", "is-cause-void", "is-cause-voids", "caused-by-void"),
  CAUSE_WITHERING("cause-withering", "cause-witherings", "cause-withering-is", "cause-witherings-is", "cause-is-withering", "cause-is-witherings", "is-cause-withering", "is-cause-witherings", "caused-by-withering");

  private final String[] name;

  PlayerGotHurtCondition(String... name) {
    String[] names = new String[name.length];
    for (int i = 0; i < name.length; i++) names[i] = name[i].toUpperCase();

    this.name = names;
  }

  PlayerGotHurtCondition() {
    this.name = new String[] {this.name()};
  }


  public boolean matches(final String arg) {
    for (String name : name) if (name.equals(arg.toUpperCase())) return true;
    return false;
  }

  public static PlayerGotHurtCondition lookup(final String arg) {
    for (PlayerGotHurtCondition condition : values()) if (condition.matches(arg)) return condition;
    return null;
  }
}

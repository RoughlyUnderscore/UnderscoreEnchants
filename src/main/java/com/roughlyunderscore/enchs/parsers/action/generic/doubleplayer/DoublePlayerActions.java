package com.roughlyunderscore.enchs.parsers.action.generic.doubleplayer;

import lombok.ToString;

@ToString
public enum DoublePlayerActions {
  LOCATION_SOUND("location-sound", "location-play-sound", "location-sound-play"),
  DROP_ITEM_WORLD("drop-item-world", "world-drop-item", "drop-item", "item-drop"),
  BROADCAST_MESSAGE("broadcast-message", "message-broadcast", "produce-message", "broadcast"),
  LOG_MESSAGE("log-message", "message-log", "log"),
  CONSOLE_COMMAND("console-command", "command-console", "console-execute", "execute-console", "console-run", "run-console"),
  SET_BLOCK("set-block", "world-set-block", "set-block-in-world", "set-block-world"),
  TIME("time", "set-time", "time-set", "change-time", "time-change", "set-world-time", "world-time-set", "change-world-time", "world-time-change", "alter-time", "time-alter", "alter-world-time", "world-time-alter"),
  WEATHER("weather", "set-weather", "weather-set", "change-weather", "weather-change", "set-world-weather", "world-weather-set", "change-world-weather", "world-weather-change", "alter-weather", "weather-alter", "alter-world-weather", "world-weather-alter"),
  SPAWN_ENTITY("spawn-entity", "entity-spawn", "spawn-entity-world", "world-spawn-entity", "create-entity", "entity-create"),
  SPAWN_ENTITY_DETAILED("spawn-entity-detailed", "entity-spawn-detailed", "spawn-entity-world-detailed", "world-spawn-entity-detailed", "create-entity-detailed", "entity-create-detailed"),
  CREATE_WORLD("create-world", "world-create", "make-world", "world-make"),

  PLAYER_ONE_PDC_SET("player-one-pdc-set", "player-one-set-pdc", "player-one-set-pdc-value", "player-one-pdc-value-set", "player-one-set-pdc-value", "player-one-pdc-value-set", "player-one-set-pdc", "player-one-pdc-set"),
  PLAYER_ONE_VELOCITY("player-one-velocity", "player-one-set-velocity", "player-one-velocity-set", "player-one-set-velocity", "player-one-produce-velocity"),
  PLAYER_ONE_SOUND("player-one-sound", "player-one-play-sound", "player-one-sound-play"),
  PLAYER_ONE_EFFECT("player-one-effect", "player-one-add-effect", "player-one-effect-add"),
  PLAYER_ONE_REMOVE_EFFECT("player-one-remove-effect", "player-one-effect-remove"),
  PLAYER_ONE_REMOVE_BUFFS("player-one-remove-buffs", "player-one-buffs-remove"),
  PLAYER_ONE_REMOVE_DEBUFFS("player-one-remove-debuffs", "player-one-debuffs-remove"),
  PLAYER_ONE_DROP_ITEM("player-one-drop-item", "player-one-item-drop"),
  PLAYER_ONE_SET_HAND("player-one-set-hand", "player-one-hand-set") ,
  PLAYER_ONE_SHUFFLE_HOTBAR("player-one-shuffle-hotbar", "player-one-hotbar-shuffle"),
  PLAYER_ONE_SHUFFLE_INVENTORY("player-one-shuffle-inventory", "player-one-inventory-shuffle"),
  PLAYER_ONE_STRIKE_FAKE_LIGHTNING("player-one-strike-fake-lightning", "player-one-fake-lightning-strike", "player-one-strike-lightning-fake", "player-one-fake-strike-lightning", "player-one-lightning-strike-fake", "player-one-fake-lightning-strike"),
  PLAYER_ONE_STRIKE_LIGHTNING("player-one-strike-lightning", "player-one-lightning-strike", "player-one-strike-real-lightning", "player-one-real-lightning-strike", "player-one-real-strike-lightning", "player-one-lightning-strike-real"),
  PLAYER_ONE_PARTICLE("player-one-particle", "player-one-particle-send", "player-one-send-particle"),
  PLAYER_ONE_PARTICLE_BOOTS("player-one-particle-boots", "player-one-boots-particle", "player-one-boots-particle-send", "player-one-send-boots-particle"),
  PLAYER_ONE_TELEPORT("player-one-teleport", "teleport-player-one", "player-one-tp", "tp-player-one", "player-one-move", "move-player-one", "player-one-move-to", "move-player-one-to", "player-one-teleport-to", "teleport-player-one-to", "player-one-tp-to", "tp-player-one-to"),
  PLAYER_ONE_DIRECTION("player-one-direction", "player-one-set-direction", "player-one-direction-set", "player-one-set-direction", "player-one-produce-direction"),
  PLAYER_ONE_WORLD("player-one-world", "player-one-set-world", "player-one-world-set", "player-one-set-world", "player-one-produce-world"),
  PLAYER_ONE_FORWARD("player-one-forward", "player-one-set-forward", "player-one-forward-set", "player-one-set-forward", "player-one-produce-forward"),
  PLAYER_ONE_TELEPORT_BED("player-one-teleport-bed", "teleport-player-one-bed", "player-one-tp-bed", "tp-player-one-bed", "player-one-move-bed", "move-player-one-bed", "player-one-move-to-bed", "move-player-one-to-bed", "player-one-teleport-to-bed", "teleport-player-one-to-bed", "player-one-tp-to-bed", "tp-player-one-to-bed"),
  PLAYER_ONE_SET_HEALTH("player-one-set-health", "player-one-health-set", "player-one-health-set", "player-one-set-health", "player-one-produce-health", "player-one-health", "player-one-hp", "player-one-set-hp", "player-one-hp-set", "player-one-set-hp", "player-one-produce-hp"),
  PLAYER_ONE_SET_FOOD("player-one-set-food", "player-one-food-set", "player-one-food-set", "player-one-set-food", "player-one-produce-food", "player-one-food", "player-one-set-food-level", "player-one-food-level-set", "player-one-food-level-set", "player-one-set-food-level", "player-one-produce-food-level"),
  PLAYER_ONE_SET_MAX_HEALTH("player-one-set-max-health", "player-one-max-health-set", "player-one-max-health-set", "player-one-set-max-health", "player-one-produce-max-health", "player-one-max-health", "player-one-set-max-hp", "player-one-max-hp-set", "player-one-max-hp-set", "player-one-set-max-hp", "player-one-produce-max-hp"),
  PLAYER_ONE_SET_FIRE("player-one-set-fire", "player-one-fire-set", "player-one-fire-set", "player-one-set-fire", "player-one-produce-fire", "player-one-fire", "player-one-set-on-fire", "player-one-on-fire-set", "player-one-on-fire-set", "player-one-set-on-fire", "player-one-produce-on-fire"),
  PLAYER_ONE_SET_AIR("player-one-set-air", "player-one-air-set", "player-one-air-set", "player-one-set-air", "player-one-produce-air", "player-one-air", "player-one-set-air-level", "player-one-air-level-set", "player-one-air-level-set", "player-one-set-air-level", "player-one-produce-air-level", "player-one-set-oxygen", "player-one-oxygen-set", "player-one-oxygen-set", "player-one-set-oxygen", "player-one-produce-oxygen"),
  PLAYER_ONE_SET_EXP("player-one-set-exp", "player-one-exp-set", "player-one-exp-set", "player-one-set-exp", "player-one-produce-exp", "player-one-exp", "player-one-set-experience", "player-one-experience-set", "player-one-experience-set", "player-one-set-experience", "player-one-produce-experience", "player-one-set-xp", "player-one-xp-set", "player-one-xp-set", "player-one-set-xp", "player-one-produce-xp"),
  PLAYER_ONE_SET_LEVEL("player-one-set-level", "player-one-level-set", "player-one-level-set", "player-one-set-level", "player-one-produce-level", "player-one-level", "player-one-set-experience-level", "player-one-experience-level-set", "player-one-experience-level-set", "player-one-set-experience-level", "player-one-produce-experience-level", "player-one-set-xp-level", "player-one-xp-level-set", "player-one-xp-level-set", "player-one-set-xp-level", "player-one-produce-xp-level"),
  PLAYER_ONE_SEND_ARROW("player-one-send-arrow", "player-one-arrow-send", "player-one-arrow-send", "player-one-send-arrow", "player-one-produce-arrow", "player-one-arrow", "player-one-shoot-arrow", "player-one-arrow-shoot", "player-one-arrow-shoot", "player-one-shoot-arrow", "player-one-produce-arrow"),
  PLAYER_ONE_SEND_FIREBALL("player-one-send-fireball", "player-one-fireball-send", "player-one-fireball-send", "player-one-send-fireball", "player-one-produce-fireball", "player-one-fireball", "player-one-shoot-fireball", "player-one-fireball-shoot", "player-one-fireball-shoot", "player-one-shoot-fireball", "player-one-produce-fireball"),
  PLAYER_ONE_SET_MONEY("player-one-set-money", "player-one-money-set", "player-one-money-set", "player-one-set-money", "player-one-produce-money", "player-one-money", "player-one-set-money-level", "player-one-money-level-set", "player-one-money-level-set", "player-one-set-money-level", "player-one-produce-money-level", "player-one-balance", "player-one-set-balance", "player-one-balance-set", "player-one-set-balance", "player-one-produce-balance"),
  PLAYER_ONE_SET_GAMEMODE("player-one-set-gamemode", "player-one-gamemode-set", "player-one-gamemode-set", "player-one-set-gamemode", "player-one-produce-gamemode", "player-one-gamemode", "player-one-set-game-mode", "player-one-game-mode-set", "player-one-game-mode-set", "player-one-set-game-mode", "player-one-produce-game-mode"),
  PLAYER_ONE_GIVE_HEAD("player-one-give-head", "give-player-one-head", "give-player-one-other-player-one-head"),
  PLAYER_ONE_GIVE_ITEM("player-one-give-item", "give-player-one-item"),
  PLAYER_ONE_DROP_HAND("player-one-drop-hand", "drop-player-one-hand"),
  PLAYER_ONE_DAMAGE_ARMOR("player-one-damage-armor", "player-one-armor-damage", "damage-player-one-armor"),
  PLAYER_ONE_DAMAGE_HAND("player-one-damage-hand", "player-one-hand-damage", "damage-player-one-hand"),
  PLAYER_ONE_REPAIR_ARMOR("player-one-repair-armor", "player-one-armor-repair", "repair-player-one-armor"),
  PLAYER_ONE_REPAIR_HAND("player-one-repair-hand", "player-one-armor-hand", "repair-player-one-hand"),
  PLAYER_ONE_TIME("player-one-time", "player-one-set-time", "player-one-time-set", "player-one-change-time", "player-one-time-change", "player-one-set-world-time", "player-one-world-time-set", "player-one-change-world-time", "player-one-world-time-change", "player-one-alter-time", "player-one-time-alter", "player-one-alter-world-time", "player-one-world-time-alter"),
  PLAYER_ONE_WEATHER("player-one-weather", "player-one-set-weather", "player-one-weather-set", "player-one-change-weather", "player-one-weather-change", "player-one-set-world-weather", "player-one-world-weather-set", "player-one-change-world-weather", "player-one-world-weather-change", "player-one-alter-weather", "player-one-weather-alter", "player-one-alter-world-weather", "player-one-world-weather-alter"),
  RESET_PLAYER_ONE_TIME("reset-player-one-time", "player-one-reset-time", "player-one-time-reset", "reset-player-one-world-time", "player-one-reset-world-time", "player-one-world-time-reset"),
  RESET_PLAYER_ONE_WEATHER("reset-player-one-weather", "player-one-reset-weather", "player-one-weather-reset", "reset-player-one-world-weather", "player-one-reset-world-weather", "player-one-world-weather-reset"),
  PLAYER_ONE_GODMODE("player-one-godmode", "player-one-god-mode", "player-one-produce-godmode", "player-one-god-mode", "player-one-produce-god-mode"),
  PLAYER_ONE_BOSSBAR("player-one-bossbar", "player-one-bossbar-send", "player-one-send-bossbar"),
  PLAYER_ONE_TITLE("player-one-title", "player-one-title-send", "player-one-send-title"),
  PLAYER_ONE_SUBTITLE("player-one-subtitle", "player-one-subtitle-send", "player-one-send-subtitle"),
  PLAYER_ONE_SEND_MESSAGE("player-one-send-message", "player-one-message-send", "player-one-message-send", "player-one-send-message", "player-one-produce-message", "player-one-message"),
  PLAYER_ONE_SEND_ACTIONBAR("player-one-send-actionbar", "player-one-actionbar-send", "player-one-actionbar-send", "player-one-send-actionbar", "player-one-produce-actionbar", "player-one-actionbar"),
  PLAYER_ONE_SEND_CHAT("player-one-send-chat", "player-one-chat-send", "player-one-chat-send", "player-one-send-chat", "player-one-produce-chat", "player-one-chat"),

  PLAYER_TWO_PDC_SET("player-two-pdc-set", "player-two-set-pdc", "player-two-set-pdc-value", "player-two-pdc-value-set", "player-two-set-pdc-value", "player-two-pdc-value-set", "player-two-set-pdc", "player-two-pdc-set"),
  PLAYER_TWO_VELOCITY("player-two-velocity", "player-two-set-velocity", "player-two-velocity-set", "player-two-set-velocity", "player-two-produce-velocity"),
  PLAYER_TWO_SOUND("player-two-sound", "player-two-play-sound", "player-two-sound-play"),
  PLAYER_TWO_EFFECT("player-two-effect", "player-two-add-effect", "player-two-effect-add"),
  PLAYER_TWO_REMOVE_EFFECT("player-two-remove-effect", "player-two-effect-remove"),
  PLAYER_TWO_REMOVE_BUFFS("player-two-remove-buffs", "player-two-buffs-remove"),
  PLAYER_TWO_REMOVE_DEBUFFS("player-two-remove-debuffs", "player-two-debuffs-remove"),
  PLAYER_TWO_DROP_ITEM("player-two-drop-item", "player-two-item-drop"),
  PLAYER_TWO_SET_HAND("player-two-set-hand", "player-two-hand-set") ,
  PLAYER_TWO_SHUFFLE_HOTBAR("player-two-shuffle-hotbar", "player-two-hotbar-shuffle"),
  PLAYER_TWO_SHUFFLE_INVENTORY("player-two-shuffle-inventory", "player-two-inventory-shuffle"),
  PLAYER_TWO_STRIKE_FAKE_LIGHTNING("player-two-strike-fake-lightning", "player-two-fake-lightning-strike", "player-two-strike-lightning-fake", "player-two-fake-strike-lightning", "player-two-lightning-strike-fake", "player-two-fake-lightning-strike"),
  PLAYER_TWO_STRIKE_LIGHTNING("player-two-strike-lightning", "player-two-lightning-strike", "player-two-strike-real-lightning", "player-two-real-lightning-strike", "player-two-real-strike-lightning", "player-two-lightning-strike-real"),
  PLAYER_TWO_PARTICLE("player-two-particle", "player-two-particle-send", "player-two-send-particle"),
  PLAYER_TWO_PARTICLE_BOOTS("player-two-particle-boots", "player-two-boots-particle", "player-two-boots-particle-send", "player-two-send-boots-particle"),
  PLAYER_TWO_TELEPORT("player-two-teleport", "teleport-player-two", "player-two-tp", "tp-player-two", "player-two-move", "move-player-two", "player-two-move-to", "move-player-two-to", "player-two-teleport-to", "teleport-player-two-to", "player-two-tp-to", "tp-player-two-to"),
  PLAYER_TWO_DIRECTION("player-two-direction", "player-two-set-direction", "player-two-direction-set", "player-two-set-direction", "player-two-produce-direction"),
  PLAYER_TWO_WORLD("player-two-world", "player-two-set-world", "player-two-world-set", "player-two-set-world", "player-two-produce-world"),
  PLAYER_TWO_FORWARD("player-two-forward", "player-two-set-forward", "player-two-forward-set", "player-two-set-forward", "player-two-produce-forward"),
  PLAYER_TWO_TELEPORT_BED("player-two-teleport-bed", "teleport-player-two-bed", "player-two-tp-bed", "tp-player-two-bed", "player-two-move-bed", "move-player-two-bed", "player-two-move-to-bed", "move-player-two-to-bed", "player-two-teleport-to-bed", "teleport-player-two-to-bed", "player-two-tp-to-bed", "tp-player-two-to-bed"),
  PLAYER_TWO_SET_HEALTH("player-two-set-health", "player-two-health-set", "player-two-health-set", "player-two-set-health", "player-two-produce-health", "player-two-health", "player-two-hp", "player-two-set-hp", "player-two-hp-set", "player-two-set-hp", "player-two-produce-hp"),
  PLAYER_TWO_SET_FOOD("player-two-set-food", "player-two-food-set", "player-two-food-set", "player-two-set-food", "player-two-produce-food", "player-two-food", "player-two-set-food-level", "player-two-food-level-set", "player-two-food-level-set", "player-two-set-food-level", "player-two-produce-food-level"),
  PLAYER_TWO_SET_MAX_HEALTH("player-two-set-max-health", "player-two-max-health-set", "player-two-max-health-set", "player-two-set-max-health", "player-two-produce-max-health", "player-two-max-health", "player-two-set-max-hp", "player-two-max-hp-set", "player-two-max-hp-set", "player-two-set-max-hp", "player-two-produce-max-hp"),
  PLAYER_TWO_SET_FIRE("player-two-set-fire", "player-two-fire-set", "player-two-fire-set", "player-two-set-fire", "player-two-produce-fire", "player-two-fire", "player-two-set-on-fire", "player-two-on-fire-set", "player-two-on-fire-set", "player-two-set-on-fire", "player-two-produce-on-fire"),
  PLAYER_TWO_SET_AIR("player-two-set-air", "player-two-air-set", "player-two-air-set", "player-two-set-air", "player-two-produce-air", "player-two-air", "player-two-set-air-level", "player-two-air-level-set", "player-two-air-level-set", "player-two-set-air-level", "player-two-produce-air-level", "player-two-set-oxygen", "player-two-oxygen-set", "player-two-oxygen-set", "player-two-set-oxygen", "player-two-produce-oxygen"),
  PLAYER_TWO_SET_EXP("player-two-set-exp", "player-two-exp-set", "player-two-exp-set", "player-two-set-exp", "player-two-produce-exp", "player-two-exp", "player-two-set-experience", "player-two-experience-set", "player-two-experience-set", "player-two-set-experience", "player-two-produce-experience", "player-two-set-xp", "player-two-xp-set", "player-two-xp-set", "player-two-set-xp", "player-two-produce-xp"),
  PLAYER_TWO_SET_LEVEL("player-two-set-level", "player-two-level-set", "player-two-level-set", "player-two-set-level", "player-two-produce-level", "player-two-level", "player-two-set-experience-level", "player-two-experience-level-set", "player-two-experience-level-set", "player-two-set-experience-level", "player-two-produce-experience-level", "player-two-set-xp-level", "player-two-xp-level-set", "player-two-xp-level-set", "player-two-set-xp-level", "player-two-produce-xp-level"),
  PLAYER_TWO_SEND_ARROW("player-two-send-arrow", "player-two-arrow-send", "player-two-arrow-send", "player-two-send-arrow", "player-two-produce-arrow", "player-two-arrow", "player-two-shoot-arrow", "player-two-arrow-shoot", "player-two-arrow-shoot", "player-two-shoot-arrow", "player-two-produce-arrow"),
  PLAYER_TWO_SEND_FIREBALL("player-two-send-fireball", "player-two-fireball-send", "player-two-fireball-send", "player-two-send-fireball", "player-two-produce-fireball", "player-two-fireball", "player-two-shoot-fireball", "player-two-fireball-shoot", "player-two-fireball-shoot", "player-two-shoot-fireball", "player-two-produce-fireball"),
  PLAYER_TWO_SET_MTWOY("player-two-set-mtwoy", "player-two-mtwoy-set", "player-two-mtwoy-set", "player-two-set-mtwoy", "player-two-produce-mtwoy", "player-two-mtwoy", "player-two-set-mtwoy-level", "player-two-mtwoy-level-set", "player-two-mtwoy-level-set", "player-two-set-mtwoy-level", "player-two-produce-mtwoy-level", "player-two-balance", "player-two-set-balance", "player-two-balance-set", "player-two-set-balance", "player-two-produce-balance"),
  PLAYER_TWO_SET_GAMEMODE("player-two-set-gamemode", "player-two-gamemode-set", "player-two-gamemode-set", "player-two-set-gamemode", "player-two-produce-gamemode", "player-two-gamemode", "player-two-set-game-mode", "player-two-game-mode-set", "player-two-game-mode-set", "player-two-set-game-mode", "player-two-produce-game-mode"),
  PLAYER_TWO_GIVE_HEAD("player-two-give-head", "give-player-two-head", "give-player-two-other-player-two-head"),
  PLAYER_TWO_GIVE_ITEM("player-two-give-item", "give-player-two-item"),
  PLAYER_TWO_DROP_HAND("player-two-drop-hand", "drop-player-two-hand"),
  PLAYER_TWO_DAMAGE_ARMOR("player-two-damage-armor", "player-two-armor-damage", "damage-player-two-armor"),
  PLAYER_TWO_DAMAGE_HAND("player-two-damage-hand", "player-two-hand-damage", "damage-player-two-hand"),
  PLAYER_TWO_REPAIR_ARMOR("player-two-repair-armor", "player-two-armor-repair", "repair-player-two-armor"),
  PLAYER_TWO_REPAIR_HAND("player-two-repair-hand", "player-two-armor-hand", "repair-player-two-hand"),
  PLAYER_TWO_TIME("player-two-time", "player-two-set-time", "player-two-time-set", "player-two-change-time", "player-two-time-change", "player-two-set-world-time", "player-two-world-time-set", "player-two-change-world-time", "player-two-world-time-change", "player-two-alter-time", "player-two-time-alter", "player-two-alter-world-time", "player-two-world-time-alter"),
  PLAYER_TWO_WEATHER("player-two-weather", "player-two-set-weather", "player-two-weather-set", "player-two-change-weather", "player-two-weather-change", "player-two-set-world-weather", "player-two-world-weather-set", "player-two-change-world-weather", "player-two-world-weather-change", "player-two-alter-weather", "player-two-weather-alter", "player-two-alter-world-weather", "player-two-world-weather-alter"),
  RESET_PLAYER_TWO_TIME("reset-player-two-time", "player-two-reset-time", "player-two-time-reset", "reset-player-two-world-time", "player-two-reset-world-time", "player-two-world-time-reset"),
  RESET_PLAYER_TWO_WEATHER("reset-player-two-weather", "player-two-reset-weather", "player-two-weather-reset", "reset-player-two-world-weather", "player-two-reset-world-weather", "player-two-world-weather-reset"),
  PLAYER_TWO_GODMODE("player-two-godmode", "player-two-god-mode", "player-two-produce-godmode", "player-two-god-mode", "player-two-produce-god-mode"),
  PLAYER_TWO_BOSSBAR("player-two-bossbar", "player-two-bossbar-send", "player-two-send-bossbar"),
  PLAYER_TWO_TITLE("player-two-title", "player-two-title-send", "player-two-send-title"),
  PLAYER_TWO_SUBTITLE("player-two-subtitle", "player-two-subtitle-send", "player-two-send-subtitle"),
  PLAYER_TWO_SEND_MESSAGE("player-two-send-message", "player-two-message-send", "player-two-message-send", "player-two-send-message", "player-two-produce-message", "player-two-message"),
  PLAYER_TWO_SEND_ACTIONBAR("player-two-send-actionbar", "player-two-actionbar-send", "player-two-actionbar-send", "player-two-send-actionbar", "player-two-produce-actionbar", "player-two-actionbar"),
  PLAYER_TWO_SEND_CHAT("player-two-send-chat", "player-two-chat-send", "player-two-chat-send", "player-two-send-chat", "player-two-produce-chat", "player-two-chat");
  


  private final String[] aliases;

  DoublePlayerActions(final String... aliases0) {
    String[] aliases = new String[aliases0.length];
    for (int i = 0; i < aliases0.length; i++) aliases[i] = aliases0[i].toUpperCase();

    this.aliases = aliases;
  }

  @SuppressWarnings("unused")
  DoublePlayerActions() {
    this.aliases = new String[] {this.name()};
  }


  final public boolean matches(final String arg) {
    for (String name : aliases) if (name.equals(arg.toUpperCase())) return true;
    return false;
  }

  public static DoublePlayerActions lookup(final String arg) {
    for (DoublePlayerActions action : values()) if (action.matches(arg)) return action;
    return null;
  }
}

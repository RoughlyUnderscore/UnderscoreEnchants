package com.roughlyunderscore.enchs.parsers.action.generic.player;

import lombok.ToString;

@ToString
public enum PlayerActions {
  PLAYER_PDC_SET("player-pdc-set", "player-set-pdc", "player-set-pdc-value", "player-pdc-value-set", "player-set-pdc-value", "player-pdc-value-set", "player-set-pdc", "player-pdc-set"),
  PLAYER_VELOCITY("player-velocity", "player-set-velocity", "player-velocity-set", "player-set-velocity", "player-produce-velocity"),
  PLAYER_SOUND("player-sound", "player-play-sound", "player-sound-play"),
  LOCATION_SOUND("location-sound", "location-play-sound", "location-sound-play"),
  PLAYER_EFFECT("player-effect", "player-add-effect", "player-effect-add"),
  PLAYER_REMOVE_EFFECT("player-remove-effect", "player-effect-remove"),
  PLAYER_REMOVE_BUFFS("player-remove-buffs", "player-buffs-remove"),
  PLAYER_REMOVE_DEBUFFS("player-remove-debuffs", "player-debuffs-remove"),
  PLAYER_DROP_ITEM("player-drop-item", "player-item-drop"),
  DROP_ITEM_WORLD("drop-item-world", "world-drop-item", "drop-item", "item-drop"),
  PLAYER_SET_HAND("player-set-hand", "player-hand-set") ,
  PLAYER_SHUFFLE_HOTBAR("player-shuffle-hotbar", "player-hotbar-shuffle"),
  PLAYER_SHUFFLE_INVENTORY("player-shuffle-inventory", "player-inventory-shuffle"),
  PLAYER_STRIKE_FAKE_LIGHTNING("player-strike-fake-lightning", "player-fake-lightning-strike", "player-strike-lightning-fake", "player-fake-strike-lightning", "player-lightning-strike-fake", "player-fake-lightning-strike"),
  PLAYER_STRIKE_LIGHTNING("player-strike-lightning", "player-lightning-strike", "player-strike-real-lightning", "player-real-lightning-strike", "player-real-strike-lightning", "player-lightning-strike-real"),
  PLAYER_PARTICLE("player-particle", "player-particle-send", "player-send-particle"),
  PLAYER_PARTICLE_BOOTS("player-particle-boots", "player-boots-particle", "player-boots-particle-send", "player-send-boots-particle"),
  SPAWN_ENTITY("spawn-entity", "entity-spawn", "spawn-entity-world", "world-spawn-entity", "create-entity", "entity-create"),
  SPAWN_ENTITY_DETAILED("spawn-entity-detailed", "entity-spawn-detailed", "spawn-entity-world-detailed", "world-spawn-entity-detailed", "create-entity-detailed", "entity-create-detailed"),
  PLAYER_TELEPORT("player-teleport", "teleport-player", "player-tp", "tp-player", "player-move", "move-player", "player-move-to", "move-player-to", "player-teleport-to", "teleport-player-to", "player-tp-to", "tp-player-to", "move", "tp"),
  PLAYER_DIRECTION("player-direction", "player-set-direction", "player-direction-set", "player-set-direction", "player-produce-direction"),
  PLAYER_WORLD("player-world", "player-set-world", "player-world-set", "player-set-world", "player-produce-world"),
  PLAYER_FORWARD("player-forward", "player-set-forward", "player-forward-set", "player-set-forward", "player-produce-forward"),
  CREATE_WORLD("create-world", "world-create", "make-world", "world-make"),
  PLAYER_TELEPORT_BED("player-teleport-bed", "teleport-player-bed", "player-tp-bed", "tp-player-bed", "player-move-bed", "move-player-bed", "player-move-to-bed", "move-player-to-bed", "player-teleport-to-bed", "teleport-player-to-bed", "player-tp-to-bed", "tp-player-to-bed"),
  PLAYER_SET_HEALTH("player-set-health", "player-health-set", "player-health-set", "player-set-health", "player-produce-health", "player-health", "player-hp", "player-set-hp", "player-hp-set", "player-set-hp", "player-produce-hp"),
  PLAYER_SET_FOOD("player-set-food", "player-food-set", "player-food-set", "player-set-food", "player-produce-food", "player-food", "player-set-food-level", "player-food-level-set", "player-food-level-set", "player-set-food-level", "player-produce-food-level"),
  PLAYER_SET_MAX_HEALTH("player-set-max-health", "player-max-health-set", "player-max-health-set", "player-set-max-health", "player-produce-max-health", "player-max-health", "player-set-max-hp", "player-max-hp-set", "player-max-hp-set", "player-set-max-hp", "player-produce-max-hp"),
  PLAYER_SET_FIRE("player-set-fire", "player-fire-set", "player-fire-set", "player-set-fire", "player-produce-fire", "player-fire", "player-set-on-fire", "player-on-fire-set", "player-on-fire-set", "player-set-on-fire", "player-produce-on-fire"),
  PLAYER_SET_AIR("player-set-air", "player-air-set", "player-air-set", "player-set-air", "player-produce-air", "player-air", "player-set-air-level", "player-air-level-set", "player-air-level-set", "player-set-air-level", "player-produce-air-level", "player-set-oxygen", "player-oxygen-set", "player-oxygen-set", "player-set-oxygen", "player-produce-oxygen"),
  PLAYER_SET_EXP("player-set-exp", "player-exp-set", "player-exp-set", "player-set-exp", "player-produce-exp", "player-exp", "player-set-experience", "player-experience-set", "player-experience-set", "player-set-experience", "player-produce-experience", "player-set-xp", "player-xp-set", "player-xp-set", "player-set-xp", "player-produce-xp"),
  PLAYER_SET_LEVEL("player-set-level", "player-level-set", "player-level-set", "player-set-level", "player-produce-level", "player-level", "player-set-experience-level", "player-experience-level-set", "player-experience-level-set", "player-set-experience-level", "player-produce-experience-level", "player-set-xp-level", "player-xp-level-set", "player-xp-level-set", "player-set-xp-level", "player-produce-xp-level"),
  PLAYER_SEND_ARROW("player-send-arrow", "player-arrow-send", "player-arrow-send", "player-send-arrow", "player-produce-arrow", "player-arrow", "player-shoot-arrow", "player-arrow-shoot", "player-arrow-shoot", "player-shoot-arrow", "player-produce-arrow"),
  PLAYER_SEND_FIREBALL("player-send-fireball", "player-fireball-send", "player-fireball-send", "player-send-fireball", "player-produce-fireball", "player-fireball", "player-shoot-fireball", "player-fireball-shoot", "player-fireball-shoot", "player-shoot-fireball", "player-produce-fireball"),
  PLAYER_SET_MONEY("player-set-money", "player-money-set", "player-money-set", "player-set-money", "player-produce-money", "player-money", "player-set-money-level", "player-money-level-set", "player-money-level-set", "player-set-money-level", "player-produce-money-level", "player-balance", "player-set-balance", "player-balance-set", "player-set-balance", "player-produce-balance"),
  PLAYER_SET_GAMEMODE("player-set-gamemode", "player-gamemode-set", "player-gamemode-set", "player-set-gamemode", "player-produce-gamemode", "player-gamemode", "player-set-game-mode", "player-game-mode-set", "player-game-mode-set", "player-set-game-mode", "player-produce-game-mode"),
  PLAYER_GIVE_HEAD("player-give-head", "give-player-head", "give-player-other-player-head"),
  PLAYER_GIVE_ITEM("player-give-item", "give-player-item"),
  PLAYER_DROP_HAND("player-drop-hand", "drop-player-hand", "drop-hand"),
  PLAYER_DAMAGE_ARMOR("player-damage-armor", "player-armor-damage", "damage-player-armor", "damage-armor"),
  PLAYER_DAMAGE_HAND("player-damage-hand", "player-hand-damage", "damage-player-hand", "damage-hand"),
  PLAYER_REPAIR_ARMOR("player-repair-armor", "player-armor-repair", "repair-player-armor", "repair-armor"),
  PLAYER_REPAIR_HAND("player-repair-hand", "player-armor-hand", "repair-player-hand", "repair-hand"),
  SET_BLOCK("set-block", "world-set-block", "set-block-in-world", "set-block-world"),
  TIME("time", "set-time", "time-set", "change-time", "time-change", "set-world-time", "world-time-set", "change-world-time", "world-time-change", "alter-time", "time-alter", "alter-world-time", "world-time-alter"),
  WEATHER("weather", "set-weather", "weather-set", "change-weather", "weather-change", "set-world-weather", "world-weather-set", "change-world-weather", "world-weather-change", "alter-weather", "weather-alter", "alter-world-weather", "world-weather-alter"),
  PLAYER_TIME("player-time", "player-set-time", "player-time-set", "player-change-time", "player-time-change", "player-set-world-time", "player-world-time-set", "player-change-world-time", "player-world-time-change", "player-alter-time", "player-time-alter", "player-alter-world-time", "player-world-time-alter"),
  PLAYER_WEATHER("player-weather", "player-set-weather", "player-weather-set", "player-change-weather", "player-weather-change", "player-set-world-weather", "player-world-weather-set", "player-change-world-weather", "player-world-weather-change", "player-alter-weather", "player-weather-alter", "player-alter-world-weather", "player-world-weather-alter"),
  RESET_PLAYER_TIME("reset-player-time", "player-reset-time", "player-time-reset", "reset-player-world-time", "player-reset-world-time", "player-world-time-reset"),
  RESET_PLAYER_WEATHER("reset-player-weather", "player-reset-weather", "player-weather-reset", "reset-player-world-weather", "player-reset-world-weather", "player-world-weather-reset"),
  PLAYER_GODMODE("player-godmode", "player-god-mode", "player-produce-godmode", "player-god-mode", "player-produce-god-mode"),
  PLAYER_BOSSBAR("player-bossbar", "player-bossbar-send", "player-send-bossbar"),
  PLAYER_TITLE("player-title", "player-title-send", "player-send-title"),
  PLAYER_SUBTITLE("player-subtitle", "player-subtitle-send", "player-send-subtitle"),
  PLAYER_SEND_MESSAGE("player-send-message", "player-message-send", "player-message-send", "player-send-message", "player-produce-message", "player-message"),
  PLAYER_SEND_ACTIONBAR("player-send-actionbar", "player-actionbar-send", "player-actionbar-send", "player-send-actionbar", "player-produce-actionbar", "player-actionbar"),
  PLAYER_SEND_CHAT("player-send-chat", "player-chat-send", "player-chat-send", "player-send-chat", "player-produce-chat", "player-chat"),
  BROADCAST_MESSAGE("broadcast-message", "message-broadcast", "produce-message", "broadcast"),
  LOG_MESSAGE("log-message", "message-log", "log"),
  CONSOLE_COMMAND("console-command", "command-console", "console-execute", "execute-console", "console-run", "run-console");


  private final String[] aliases;

  PlayerActions(final String... aliases0) {
    String[] aliases = new String[aliases0.length];
    for (int i = 0; i < aliases0.length; i++) aliases[i] = aliases0[i].toUpperCase();

    this.aliases = aliases;
  }

  @SuppressWarnings("unused")
  PlayerActions() {
    this.aliases = new String[] {this.name()};
  }


  final public boolean matches(final String arg) {
    for (String name : aliases) if (name.equals(arg.toUpperCase())) return true;
    return false;
  }

  public static PlayerActions lookup(final String arg) {
    for (PlayerActions action : values()) if (action.matches(arg)) return action;
    return null;
  }
}

package com.roughlyunderscore.enchs.parsers.action.generic.player;

import lombok.ToString;

@ToString
public enum PlayerActionPlaceholders {
  RANDOM_INT("<random_int_[min]_[max]>", "<int_random_[min]_[max]>"),
  RANDOM_DOUBLE("<random_double_[min]_[max]>", "<double_random_[min]_[max]>"),
  MAX_Y_AT_LOCATION("<max_y_loc_[x]_[z]>", "<max_y_location_[x]_[z]>", "<max_y_at_[x]_[z]>"),
  PLAYER_PDC("<player_pdc_[key]>", "<pdc_get_[key]>", "<player_data_[key]>", "<persistent_data_[key]>"),
  BLOCK_AT_LOC("<block_at_location_[world]_[x]_[y]_[z]>", "<block_[world]_[x]_[y]_[z]>", "<block_at_[world]_[x]_[y]_[z]>, <block_location_[world]_[x]_[y]_[z]>"),

  PLAYER_NAME("<player_name>", "<name>", "<player>", "<player_nickname>", "<nickname>", "<player_nick>", "<nick>", "<player_username>", "<username>"),
  PLAYER_UUID("<player_uuid>", "<uuid>", "<player_id>", "<id>", "<player_unique_id>", "<unique_id>", "<player_unique_identifier>", "<unique_identifier>"),
  PLAYER_X("<player_x>", "<x>", "<player_location_x>", "<location_x>", "<player_loc_x>", "<loc_x>"),
  PLAYER_Y("<player_y>", "<y>", "<player_location_y>", "<location_y>", "<player_loc_y>", "<loc_y>"),
  PLAYER_Z("<player_z>", "<z>", "<player_location_z>", "<location_z>", "<player_loc_z>", "<loc_z>"),
  PLAYER_YAW("<player_yaw>", "<yaw>", "<player_location_yaw>", "<location_yaw>", "<player_loc_yaw>", "<loc_yaw>"),
  PLAYER_PITCH("<player_pitch>", "<pitch>", "<player_location_pitch>", "<location_pitch>", "<player_loc_pitch>", "<loc_pitch>"),
  PLAYER_MONEY("<player_money>", "<money>", "<player_balance>", "<balance>", "<player_cash>", "<cash>", "<player_economy>", "<economy>", "<player_eco>", "<eco>", "<player_economy_balance>", "<economy_balance>", "<player_eco_balance>", "<eco_balance>"),
  PLAYER_HEALTH("<player_health>", "<health>", "<player_hp>", "<hp>"),
  PLAYER_MAX_HEALTH("<player_max_health>", "<max_health>", "<player_max_hp>", "<max_hp>"),
  PLAYER_FOOD_LEVEL("<player_food_level>", "<food_level>", "<player_food>", "<food>"),
  PLAYER_XP_LEVEL("<player_xp_level>", "<xp_level>", "<player_level>", "<level>", "<player_exp_levels>", "<exp_levels>", "<player_levels>", "<levels>"),
  PLAYER_XP_PROGRESS("<player_xp>", "<xp>", "<player_exp>", "<exp>"),
  PLAYER_AIR_LEVEL("<player_air_level>", "<air_level>", "<player_air>", "<air>"),
  PLAYER_MAX_AIR_LEVEL("<player_max_air_level>", "<max_air_level>", "<player_max_air>", "<max_air>"),
  PLAYER_GODMODE("<player_godmode>", "<godmode>", "<player_god>", "<god>"),
  RANDOM_NEGATIVE_EFFECT("<random_negative_effect>", "<random_negative_potion_effect>", "<random_negative_potion>", "<random_negative_effect>", "<random_negative>", "<random_neg_effect>", "<random_neg_potion_effect>", "<random_neg_potion>", "<random_neg_effect>", "<random_neg>"),
  RANDOM_POSITIVE_EFFECT("<random_positive_effect>", "<random_positive_potion_effect>", "<random_positive_potion>", "<random_positive_effect>", "<random_positive>", "<random_pos_effect>", "<random_pos_potion_effect>", "<random_pos_potion>", "<random_pos_effect>", "<random_pos>"),

  PLAYER_WORLD("<player_world>", "<world>", "<player_location_world>", "<location_world>", "<player_loc_world>", "<loc_world>"),
  PLAYER_IP("<player_ip>", "<ip>", "<player_address>", "<address>"),
  PLAYER_PING("<player_ping>", "<ping>"),
  PLAYER_GAMEMODE("<player_gamemode>", "<gamemode>", "<player_game_mode>", "<game_mode>");

  private final String[] aliases;

  PlayerActionPlaceholders(final String... aliases0) {
    String[] aliases = new String[aliases0.length];
    for (int i = 0; i < aliases0.length; i++) aliases[i] = aliases0[i].toUpperCase();

    this.aliases = aliases;
  }

  PlayerActionPlaceholders() {
    this.aliases = new String[] {this.name()};
  }


  public boolean matches(final String arg) {
    for (String name : aliases) if (name.equals(arg.toUpperCase())) return true;
    return false;
  }

  public static PlayerActionPlaceholders lookup(final String arg) {
    for (PlayerActionPlaceholders condition : values()) if (condition.matches(arg)) return condition;
    return null;
  }
}

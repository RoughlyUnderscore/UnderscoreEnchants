package com.roughlyunderscore.enchs.parsers.action.generic.doubleplayer;

import lombok.ToString;

@ToString
public enum DoublePlayerActionPlaceholders {
  RANDOM_INT("<random_int_[min]_[max]>", "<int_random_[min]_[max]>"),
  RANDOM_DOUBLE("<random_double_[min]_[max]>", "<double_random_[min]_[max]>"),
  MAX_Y_AT_LOCATION("<max_y_loc_[x]_[z]>", "<max_y_location_[x]_[z]>", "<max_y_at_[x]_[z]>"),
  BLOCK_AT_LOC("<block_at_location_[world]_[x]_[y]_[z]>", "<block_[world]_[x]_[y]_[z]>", "<block_at_[world]_[x]_[y]_[z]>, <block_location_[world]_[x]_[y]_[z]>"),

  RANDOM_NEGATIVE_EFFECT("<random_negative_effect>", "<random_negative_potion_effect>", "<random_negative_potion>", "<random_negative_effect>", "<random_negative>", "<random_neg_effect>", "<random_neg_potion_effect>", "<random_neg_potion>", "<random_neg_effect>", "<random_neg>"),
  RANDOM_POSITIVE_EFFECT("<random_positive_effect>", "<random_positive_potion_effect>", "<random_positive_potion>", "<random_positive_effect>", "<random_positive>", "<random_pos_effect>", "<random_pos_potion_effect>", "<random_pos_potion>", "<random_pos_effect>", "<random_pos>"),

  PLAYER_ONE_NAME("<player_one_name>", "<player_one>", "<player_one_nickname>", "<player_one_nick>", "<player_one_username>"),
  PLAYER_ONE_UUID("<player_one_uuid>", "<player_one_id>", "<player_one_unique_id>", "<player_one_unique_identifier>"),
  PLAYER_ONE_X("<player_one_x>", "<player_one_location_x>", "<player_one_loc_x>"),
  PLAYER_ONE_Y("<player_one_y>", "<player_one_location_y>", "<player_one_loc_y>"),
  PLAYER_ONE_Z("<player_one_z>", "<player_one_location_z>", "<player_one_loc_z>"),
  PLAYER_ONE_YAW("<player_one_yaw>", "<player_one_location_yaw>", "<player_one_loc_yaw>"),
  PLAYER_ONE_PITCH("<player_one_pitch>", "<player_one_location_pitch>", "<player_one_loc_pitch>"),
  PLAYER_ONE_MONEY("<player_one_money>", "<player_one_balance>", "<player_one_cash>", "<player_one_economy>", "<player_one_eco>", "<player_one_economy_balance>", "<player_one_eco_balance>"),
  PLAYER_ONE_HEALTH("<player_one_health>", "<player_one_hp>"),
  PLAYER_ONE_MAX_HEALTH("<player_one_max_health>", "<player_one_max_hp>"),
  PLAYER_ONE_FOOD_LEVEL("<player_one_food_level>", "<player_one_food>"),
  PLAYER_ONE_XP_LEVEL("<player_one_xp_level>", "<player_one_level>", "<player_one_exp_levels>", "<player_one_levels>"),
  PLAYER_ONE_XP_PROGRESS("<player_one_xp>", "<player_one_exp>"),
  PLAYER_ONE_AIR_LEVEL("<player_one_air_level>", "<player_one_air>"),
  PLAYER_ONE_MAX_AIR_LEVEL("<player_one_max_air_level>", "<player_one_max_air>"),
  PLAYER_ONE_GODMODE("<player_one_godmode>", "<player_one_god>"),
  PLAYER_ONE_PDC("<one_pdc_[key]>", "<one_data_[key]>"),
  PLAYER_ONE_WORLD("<player_one_world>", "<player_one_location_world>", "<player_one_loc_world>"),
  PLAYER_ONE_IP("<player_one_ip>", "<player_one_address>"),
  PLAYER_ONE_PING("<player_one_ping>"),
  PLAYER_ONE_GAMEMODE("<player_one_gamemode>", "<player_one_game_mode>"),

  PLAYER_TWO_NAME("<player_two_name>", "<player_two>", "<player_two_nickname>", "<player_two_nick>", "<player_two_username>"),
  PLAYER_TWO_UUID("<player_two_uuid>", "<player_two_id>", "<player_two_unique_id>", "<player_two_unique_identifier>"),
  PLAYER_TWO_X("<player_two_x>", "<player_two_location_x>", "<player_two_loc_x>"),
  PLAYER_TWO_Y("<player_two_y>", "<player_two_location_y>", "<player_two_loc_y>"),
  PLAYER_TWO_Z("<player_two_z>", "<player_two_location_z>", "<player_two_loc_z>"),
  PLAYER_TWO_YAW("<player_two_yaw>", "<player_two_location_yaw>", "<player_two_loc_yaw>"),
  PLAYER_TWO_PITCH("<player_two_pitch>", "<player_two_location_pitch>", "<player_two_loc_pitch>"),
  PLAYER_TWO_MONEY("<player_two_money>", "<player_two_balance>", "<player_two_cash>", "<player_two_economy>", "<player_two_eco>", "<player_two_economy_balance>", "<player_two_eco_balance>"),
  PLAYER_TWO_HEALTH("<player_two_health>", "<player_two_hp>"),
  PLAYER_TWO_MAX_HEALTH("<player_two_max_health>", "<player_two_max_hp>"),
  PLAYER_TWO_FOOD_LEVEL("<player_two_food_level>", "<player_two_food>"),
  PLAYER_TWO_XP_LEVEL("<player_two_xp_level>", "<player_two_level>", "<player_two_exp_levels>", "<player_two_levels>"),
  PLAYER_TWO_XP_PROGRESS("<player_two_xp>", "<player_two_exp>"),
  PLAYER_TWO_AIR_LEVEL("<player_two_air_level>", "<player_two_air>"),
  PLAYER_TWO_MAX_AIR_LEVEL("<player_two_max_air_level>", "<player_two_max_air>"),
  PLAYER_TWO_GODMODE("<player_two_godmode>", "<player_two_god>"),
  PLAYER_TWO_PDC("<two_pdc_[key]>", "<two_data_[key]>"),
  PLAYER_TWO_WORLD("<player_two_world>", "<player_two_location_world>", "<player_two_loc_world>"),
  PLAYER_TWO_IP("<player_two_ip>", "<player_two_address>"),
  PLAYER_TWO_PING("<player_two_ping>"),
  PLAYER_TWO_GAMEMODE("<player_two_gamemode>", "<player_two_game_mode>");

  private final String[] aliases;

  DoublePlayerActionPlaceholders(final String... aliases0) {
    String[] aliases = new String[aliases0.length];
    for (int i = 0; i < aliases0.length; i++) aliases[i] = aliases0[i].toUpperCase();

    this.aliases = aliases;
  }

  DoublePlayerActionPlaceholders() {
    this.aliases = new String[] {this.name()};
  }


  public boolean matches(final String arg) {
    for (String name : aliases) if (name.equals(arg.toUpperCase())) return true;
    return false;
  }

  public static DoublePlayerActionPlaceholders lookup(final String arg) {
    for (DoublePlayerActionPlaceholders condition : values()) if (condition.matches(arg)) return condition;
    return null;
  }
}

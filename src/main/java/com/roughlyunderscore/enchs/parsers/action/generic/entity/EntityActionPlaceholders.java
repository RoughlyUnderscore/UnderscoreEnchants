package com.roughlyunderscore.enchs.parsers.action.generic.entity;

import lombok.ToString;

@ToString
public enum EntityActionPlaceholders {
  ENTITY_TYPE("<entity_type>", "<type>", "<entity>", "<ent_type>", "<type_entity>", "<type_ent>", "<ent>", "<et>", "<te>"),
  ENTITY_X("<entity_x>", "<entity_location_x>", "<entity_loc_x>"),
  ENTITY_Y("<entity_y>", "<entity_location_y>", "<entity_loc_y>"),
  ENTITY_Z("<entity_z>", "<entity_location_z>", "<entity_loc_z>"),
  ENTITY_YAW("<entity_yaw>", "<entity_location_yaw>", "<entity_loc_yaw>"),
  ENTITY_MONEY("<entity_money>", "<entity_balance>", "<entity_cash>", "<entity_economy>", "<entity_eco>", "<entity_economy_balance>", "<entity_eco_balance>"),
  ENTITY_HEALTH("<entity_health>", "<entity_hp>"),
  ENTITY_MAX_HEALTH("<entity_max_health>", "<entity_max_hp>");

  private final String[] aliases;

  EntityActionPlaceholders(final String... aliases0) {
    String[] aliases = new String[aliases0.length];
    for (int i = 0; i < aliases0.length; i++) aliases[i] = aliases0[i].toUpperCase();

    this.aliases = aliases;
  }

  EntityActionPlaceholders() {
    this.aliases = new String[] {this.name()};
  }


  public boolean matches(final String arg) {
    for (String name : aliases) if (name.equals(arg.toUpperCase())) return true;
    return false;
  }

  public static EntityActionPlaceholders lookup(final String arg) {
    for (EntityActionPlaceholders condition : values()) if (condition.matches(arg)) return condition;
    return null;
  }
}

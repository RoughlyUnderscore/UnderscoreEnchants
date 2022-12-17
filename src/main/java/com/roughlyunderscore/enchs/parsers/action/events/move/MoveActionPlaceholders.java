package com.roughlyunderscore.enchs.parsers.action.events.move;

import lombok.ToString;

@ToString
public enum MoveActionPlaceholders {
  FROM_X("<from_x>", "<fromx>", "<x_from>", "<xfrom>"),
  FROM_Y("<from_y>", "<fromy>", "<y_from>", "<yfrom>"),
  FROM_Z("<from_z>", "<fromz>", "<z_from>", "<zfrom>"),
  TO_X("<to_x>", "<tox>", "<x_to>", "<xto>"),
  TO_Y("<to_y>", "<toy>", "<y_to>", "<yto>"),
  TO_Z("<to_z>", "<toz>", "<z_to>", "<zto>"),
  FROM_YAW("<from_yaw>", "<fromyaw>", "<yaw_from>", "<yawfrom>"),
  FROM_PITCH("<from_pitch>", "<frompitch>", "<pitch_from>", "<pitchfrom>"),
  TO_YAW("<to_yaw>", "<toyaw>", "<yaw_to>", "<yawto>"),
  TO_PITCH("<to_pitch>", "<topitch>", "<pitch_to>", "<pitchto>"),
  FROM_TYPE("<from_type>", "<fromtype>", "<type_from>", "<typefrom>"),
  TO_TYPE("<to_type>", "<totype>", "<type_to>", "<typeto>");

  private final String[] aliases;

  MoveActionPlaceholders(final String... aliases0) {
    String[] aliases = new String[aliases0.length];
    for (int i = 0; i < aliases0.length; i++) aliases[i] = aliases0[i].toUpperCase();

    this.aliases = aliases;
  }

  MoveActionPlaceholders() {
    this.aliases = new String[] {this.name()};
  }


  public boolean matches(final String arg) {
    for (String name : aliases) if (name.equals(arg.toUpperCase())) return true;
    return false;
  }

  public static MoveActionPlaceholders lookup(final String arg) {
    for (MoveActionPlaceholders condition : values()) if (condition.matches(arg)) return condition;
    return null;
  }
}

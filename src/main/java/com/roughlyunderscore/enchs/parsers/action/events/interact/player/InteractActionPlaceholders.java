package com.roughlyunderscore.enchs.parsers.action.events.interact.player;

import lombok.ToString;

@ToString
public enum InteractActionPlaceholders {
  BLOCK_X("<block_x>", "<blockx>", "x_block", "xblock"),
  BLOCK_Y("<block_y>", "<blocky>", "y_block", "yblock"),
  BLOCK_Z("<block_z>", "<blockz>", "z_block", "zblock"),
  BLOCK_MATERIAL("<block_material>", "<blockmaterial>", "<material_block>", "<materialblock>", "<block_type>", "<blocktype>", "<type_block>", "<typeblock>");

  private final String[] aliases;

  InteractActionPlaceholders(final String... aliases0) {
    String[] aliases = new String[aliases0.length];
    for (int i = 0; i < aliases0.length; i++) aliases[i] = aliases0[i].toUpperCase();

    this.aliases = aliases;
  }

  InteractActionPlaceholders() {
    this.aliases = new String[] {this.name()};
  }


  public boolean matches(final String arg) {
    for (String name : aliases) if (name.equals(arg.toUpperCase())) return true;
    return false;
  }

  public static InteractActionPlaceholders lookup(final String arg) {
    for (InteractActionPlaceholders condition : values()) if (condition.matches(arg)) return condition;
    return null;
  }
}

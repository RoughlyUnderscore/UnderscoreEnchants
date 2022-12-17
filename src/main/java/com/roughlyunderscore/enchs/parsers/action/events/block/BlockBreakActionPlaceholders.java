package com.roughlyunderscore.enchs.parsers.action.events.block;

import lombok.ToString;

@ToString
public enum BlockBreakActionPlaceholders {
  BLOCK_NAME("<block_name>", "<name_of_block>", "<block_type>"),
  BLOCK_X("<block_x>"),
  BLOCK_Y("<block_y>"),
  BLOCK_Z("<block_z>");

  private final String[] aliases;

  BlockBreakActionPlaceholders(final String... aliases0) {
    String[] aliases = new String[aliases0.length];
    for (int i = 0; i < aliases0.length; i++) aliases[i] = aliases0[i].toUpperCase();

    this.aliases = aliases;
  }

  BlockBreakActionPlaceholders() {
    this.aliases = new String[] {this.name()};
  }


  public boolean matches(final String arg) {
    for (String name : aliases) if (name.equals(arg.toUpperCase())) return true;
    return false;
  }

  public static BlockBreakActionPlaceholders lookup(final String arg) {
    for (BlockBreakActionPlaceholders condition : values()) if (condition.matches(arg)) return condition;
    return null;
  }
}

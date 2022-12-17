package com.roughlyunderscore.enchs.parsers.condition.events.item;

import com.roughlyunderscore.enchs.UnderscoreEnchants;
import com.roughlyunderscore.enchs.parsers.condition.generic.player.PlayerConditionParser;
import lombok.ToString;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemBreakEvent;

import java.util.Arrays;

@ToString
public class ItemBreakConditionParser extends PlayerConditionParser {
  private final PlayerItemBreakEvent event;

  public ItemBreakConditionParser(final Player arg, final String condition, final UnderscoreEnchants plugin, final PlayerItemBreakEvent event) {
    super(arg, condition, plugin);
    this.event = event;
  }

  @Override
  public boolean evaluate() {
    if (super.evaluate()) return true;

    final String[] args0 = getCondition().split(" "), args = Arrays.copyOfRange(args0, 1, args0.length);
    final ItemBreakCondition condition = ItemBreakCondition.lookup(args0[0].replace("!", ""));
    if (condition == null) return false;

    final boolean negate = getCondition().startsWith("!");
    final Material type = event.getBrokenItem().getType();

    final boolean result = switch (condition) {
      case ITEM_IS -> type.name().equalsIgnoreCase(args[0]);
    };

    if (negate) return !result;
    return result;

  }
}

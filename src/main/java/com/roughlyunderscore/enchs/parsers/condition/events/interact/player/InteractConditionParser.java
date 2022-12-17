package com.roughlyunderscore.enchs.parsers.condition.events.interact.player;

import com.roughlyunderscore.enchs.UnderscoreEnchants;
import com.roughlyunderscore.enchs.parsers.condition.generic.player.PlayerConditionParser;
import lombok.ToString;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Arrays;

@ToString
public class InteractConditionParser extends PlayerConditionParser {
  private final PlayerInteractEvent event;

  public InteractConditionParser(final Player arg, final String condition, final UnderscoreEnchants plugin, final PlayerInteractEvent event) {
    super(arg, condition, plugin);
    this.event = event;
  }

  @Override
  public boolean evaluate() {
    if (super.evaluate()) return true;

    final String[] args0 = getCondition().split(" "), args = Arrays.copyOfRange(args0, 1, args0.length);
    final InteractCondition condition = InteractCondition.lookup(args0[0].replace("!", ""));

    if (condition == null) return false;

    final boolean negate = getCondition().startsWith("!");

    final boolean result = switch (condition) {
      case BLOCK_IS ->
        event.getClickedBlock() != null && event.getClickedBlock().getType().name().equalsIgnoreCase(args[0]);
      case CLICKED_LMB_AIR -> event.getAction() == Action.LEFT_CLICK_AIR;
      case CLICKED_RMB_AIR -> event.getAction() == Action.RIGHT_CLICK_AIR;
      case CLICKED_LMB_BLOCK -> event.getAction() == Action.LEFT_CLICK_BLOCK;
      case CLICKED_RMB_BLOCK -> event.getAction() == Action.RIGHT_CLICK_BLOCK;
      case PHYSICAL -> event.getAction() == Action.PHYSICAL;
    };

    if (negate) return !result;
    return result;
  }
}

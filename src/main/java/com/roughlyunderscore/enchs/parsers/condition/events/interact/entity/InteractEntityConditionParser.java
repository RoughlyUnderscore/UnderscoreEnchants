package com.roughlyunderscore.enchs.parsers.condition.events.interact.entity;

import com.roughlyunderscore.enchs.UnderscoreEnchants;
import com.roughlyunderscore.enchs.parsers.condition.generic.entity.EntityConditionParser;
import lombok.ToString;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import java.util.Arrays;

@ToString
public class InteractEntityConditionParser extends EntityConditionParser {
  private final PlayerInteractAtEntityEvent event;

  public InteractEntityConditionParser(final Player arg, final String condition, final UnderscoreEnchants plugin, final PlayerInteractAtEntityEvent event) {
    super(arg, event.getRightClicked(), condition, plugin);
    this.event = event;
  }

  @Override
  public boolean evaluate() {
    if (super.evaluate()) return true;

    final String[] args0 = getCondition().split(" "), args = Arrays.copyOfRange(args0, 1, args0.length);
    final InteractEntityCondition condition = InteractEntityCondition.lookup(args0[0].replace("!", ""));

    if (condition == null) return false;

    final boolean negate = getCondition().startsWith("!");

    final boolean result = switch (condition) {
      case default -> true;
    };

    if (negate) return !result;
    return result;
  }
}

package com.roughlyunderscore.enchs.parsers.condition.events.sneak;

import com.roughlyunderscore.enchs.UnderscoreEnchants;
import com.roughlyunderscore.enchs.parsers.condition.generic.player.PlayerConditionParser;
import lombok.ToString;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.Arrays;

@ToString
public class SneakConditionParser extends PlayerConditionParser {
  private final PlayerToggleSneakEvent event;

  public SneakConditionParser(final Player arg, final String condition, final UnderscoreEnchants plugin, final PlayerToggleSneakEvent event) {
    super(arg, condition, plugin);
    this.event = event;
  }

  @Override
  public boolean evaluate() {
    if (super.evaluate()) return true;

    final String[] args0 = getCondition().split(" "), args = Arrays.copyOfRange(args0, 1, args0.length);
    final SneakCondition condition = SneakCondition.lookup(args0[0].replace("!", ""));

    if (condition == null) return false;

    final boolean negate = getCondition().startsWith("!");
    final boolean sneaking = event.isSneaking();

    final boolean result = switch (condition) {
      case SNEAKED -> sneaking;
      case UNSNEAKED -> !sneaking;
    };

    if (negate) return !result;
    return result;
  }
}

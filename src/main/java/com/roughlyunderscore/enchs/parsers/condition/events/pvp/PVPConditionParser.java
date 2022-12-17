package com.roughlyunderscore.enchs.parsers.condition.events.pvp;

import com.roughlyunderscore.enchs.UnderscoreEnchants;
import com.roughlyunderscore.enchs.events.PlayerPVPEvent;
import com.roughlyunderscore.enchs.parsers.condition.ComparativeOperator;
import com.roughlyunderscore.enchs.parsers.condition.generic.doubleplayer.DoublePlayerConditionParser;
import lombok.ToString;
import org.bukkit.entity.Player;

import java.util.Arrays;

import static com.roughlyunderscore.enchs.util.general.PlayerUtils.*;
import static com.roughlyunderscore.enchs.util.general.Utils.*;

@ToString
public class PVPConditionParser extends DoublePlayerConditionParser {
  private final PlayerPVPEvent event;

  public PVPConditionParser(final Player first, final Player second, final String condition, final UnderscoreEnchants plugin, final PlayerPVPEvent event) {
    super(first, second, condition, plugin);
    this.event = event;
  }

  @Override
  public boolean evaluate() {
    if (super.evaluate()) return true;

    final String[] args0 = getCondition().split(" "), args = Arrays.copyOfRange(args0, 1, args0.length);
    final PVPCondition condition = PVPCondition.lookup(args0[0].replace("!", ""));
    if (condition == null) return false;

    final Player player = getSecond();
    final UnderscoreEnchants plugin = getPlugin();
    final ComparativeOperator operator = isComparativeOperator(args.length == 0 ? "" : args[0]);

    final boolean negate = getCondition().startsWith("!");
    final double damage = event.getDamage();

    final boolean result = switch (condition) {
      case DAMAGE -> {
        final double expectation = parseD(args[1]);
        yield switch (operator) {
          case EQUAL -> damage == expectation;
          case MORE_THAN -> damage > expectation;
          case LESS_THAN -> damage < expectation;
          case MORE_THAN_OR_EQUAL -> damage >= expectation;
          case LESS_THAN_OR_EQUAL -> damage <= expectation;
          case NOT_EQUAL -> damage != expectation;
          case NONE -> false;
        };
      }

      case DAMAGE_LETHAL -> damage >= getHealth(player, plugin);
    };

    if (negate) return !result;
    return result;

  }
}

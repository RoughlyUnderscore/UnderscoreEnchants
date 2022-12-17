package com.roughlyunderscore.enchs.parsers.condition.events.projectile;

import com.roughlyunderscore.enchs.UnderscoreEnchants;
import com.roughlyunderscore.enchs.events.PlayerShootBowEvent;
import com.roughlyunderscore.enchs.parsers.condition.ComparativeOperator;
import com.roughlyunderscore.enchs.parsers.condition.generic.player.PlayerConditionParser;
import lombok.ToString;
import org.bukkit.entity.Player;

import java.util.Arrays;

import static com.roughlyunderscore.enchs.util.general.Utils.*;

@ToString
public class ShootBowConditionParser extends PlayerConditionParser {
  private final PlayerShootBowEvent event;

  public ShootBowConditionParser(final Player arg, final String condition, final UnderscoreEnchants plugin, final PlayerShootBowEvent event) {
    super(arg, condition, plugin);
    this.event = event;
  }

  @Override
  public boolean evaluate() {
    if (super.evaluate()) return true;

    final String[] args0 = getCondition().split(" "), args = Arrays.copyOfRange(args0, 1, args0.length);
    final ShootBowCondition condition = ShootBowCondition.lookup(args0[0].replace("!", ""));

    if (condition == null) return false;

    final ComparativeOperator operator = isComparativeOperator(args.length == 0 ? "" : args[0]);

    final boolean negate = getCondition().startsWith("!");
    final double force = event.getForce();

    final boolean result = switch (condition) {
      case FORCE -> {
        final double expectation = parseD(args[1]);
        yield switch (operator) {
          case EQUAL -> force == expectation;
          case MORE_THAN -> force > expectation;
          case LESS_THAN -> force < expectation;
          case MORE_THAN_OR_EQUAL -> force >= expectation;
          case LESS_THAN_OR_EQUAL -> force <= expectation;
          case NOT_EQUAL -> force != expectation;
          case NONE -> false;
        };
      }
    };

    if (negate) return !result;
    return result;
  }
}

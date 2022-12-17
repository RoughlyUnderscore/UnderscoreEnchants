package com.roughlyunderscore.enchs.parsers.condition.generic.player;

import com.roughlyunderscore.enchs.UnderscoreEnchants;
import com.roughlyunderscore.enchs.parsers.condition.ComparativeOperator;
import com.roughlyunderscore.enchs.parsers.condition.generic.AbstractConditionParser;
import org.bukkit.entity.Player;

import java.util.Arrays;

import static com.roughlyunderscore.enchs.util.general.EntityUtils.*;
import static com.roughlyunderscore.enchs.util.general.PlayerUtils.*;
import static com.roughlyunderscore.enchs.util.general.Utils.*;

public class PlayerConditionParser extends AbstractConditionParser<Player> {
  public PlayerConditionParser(final Player arg, final String condition, final UnderscoreEnchants plugin) {
    super(arg, condition, plugin);
  }

  @Override
  public boolean evaluate() {
    final String[] args0 = getCondition().split(" "), args = Arrays.copyOfRange(args0, 1, args0.length);
    final PlayerCondition condition = PlayerCondition.lookup(args0[0].replace("!", ""));
    if (condition == null) return false;

    final Player player = getArg();
    final UnderscoreEnchants plugin = getPlugin();
    final ComparativeOperator operator = isComparativeOperator(args.length == 0 ? "" : args[0]);

    final boolean negate = getCondition().startsWith("!");

    final boolean result = switch (condition) {
      case DAY -> day(player, plugin);
      case NIGHT -> night(player, plugin);
      case CLEAR -> !rains(player, plugin);
      case PDC_MATCH -> getPDCValue(player, getKey(args[0], plugin), plugin).equals(args[1]);
      case SNEAKING -> sneaking(player, plugin);
      case SPRINTING -> sprinting(player, plugin);
      case SWIMMING -> swimming(player, plugin);
      case FLYING -> flying(player, plugin);
      case ON_FIRE -> onFire(player, plugin);
      case ON_HIGHEST_BLOCK -> onTop(player, plugin);
      case RAIN -> rains(player, plugin);
      case THUNDER -> thunders(player, plugin);
      case OVERWORLD -> overworld(player, plugin);
      case NETHER -> nether(player, plugin);
      case END -> end(player, plugin);
      case OP -> op(player, plugin);
      case BLOCKING -> blocking(player, plugin);

      case HEALTH -> {
        final double health = getHealth(player, plugin);
        final double expectation = parseD(args[1]);
        yield switch (operator) {
          case EQUAL -> health == expectation;
          case MORE_THAN -> health > expectation;
          case LESS_THAN -> health < expectation;
          case MORE_THAN_OR_EQUAL -> health >= expectation;
          case LESS_THAN_OR_EQUAL -> health <= expectation;
          case NOT_EQUAL -> health != expectation;
          case NONE -> false;
        };
      }

      case FOOD -> {
        final int food = getFood(player, plugin);
        final int expectation = parseI(args[1]);
        yield switch (operator) {
          case EQUAL -> food == expectation;
          case MORE_THAN -> food > expectation;
          case LESS_THAN -> food < expectation;
          case MORE_THAN_OR_EQUAL -> food >= expectation;
          case LESS_THAN_OR_EQUAL -> food <= expectation;
          case NOT_EQUAL -> food != expectation;
          case NONE -> false;
        };
      }

      case AIR -> {
        final int air = getAir(player, plugin);
        final int expectation = parseI(args[1]);
        yield switch (operator) {
          case EQUAL -> air == expectation;
          case MORE_THAN -> air > expectation;
          case LESS_THAN -> air < expectation;
          case MORE_THAN_OR_EQUAL -> air >= expectation;
          case LESS_THAN_OR_EQUAL -> air <= expectation;
          case NOT_EQUAL -> air != expectation;
          case NONE -> false;
        };
      }

      case GODMODE -> {
        final int remainingGodMode = invisibleFor(player, plugin);
        final int expectation = parseI(args[1]);
        yield switch (operator) {
          case EQUAL -> remainingGodMode == expectation;
          case MORE_THAN -> remainingGodMode > expectation;
          case LESS_THAN -> remainingGodMode < expectation;
          case MORE_THAN_OR_EQUAL -> remainingGodMode >= expectation;
          case LESS_THAN_OR_EQUAL -> remainingGodMode <= expectation;
          case NOT_EQUAL -> remainingGodMode != expectation;
          case NONE -> false;
        };
      }
    };

    if (negate) return !result;
    return result;
  }
}

package com.roughlyunderscore.enchs.parsers.condition.generic.doubleplayer;

import com.roughlyunderscore.enchs.UnderscoreEnchants;
import com.roughlyunderscore.enchs.parsers.condition.ComparativeOperator;
import com.roughlyunderscore.enchs.parsers.condition.generic.AbstractConditionParser;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.entity.Player;

import java.util.Arrays;

import static com.roughlyunderscore.enchs.util.general.EntityUtils.*;
import static com.roughlyunderscore.enchs.util.general.PlayerUtils.*;
import static com.roughlyunderscore.enchs.util.general.Utils.*;

@ToString
public class DoublePlayerConditionParser extends AbstractConditionParser<Player> {
  @Getter
  private final Player first, second;

  public DoublePlayerConditionParser(final Player first, final Player second, final String condition, final UnderscoreEnchants plugin) {
    super(first, condition, plugin);
    this.first = first;
    this.second = second;
  }

  @Override
  public boolean evaluate() {
    final String[] args0 = getCondition().split(" "), args = Arrays.copyOfRange(args0, 1, args0.length);
    final DoublePlayerCondition condition = DoublePlayerCondition.lookup(args0[0].replace("!", ""));
    if (condition == null) return false;

    final UnderscoreEnchants plugin = getPlugin();
    final ComparativeOperator operator = isComparativeOperator(args.length == 0 ? "" : args[0]);

    final boolean negate = getCondition().startsWith("!");

    final boolean result = switch (condition) {

      // Player one

      case PLAYER_ONE_DAY -> day(first, plugin);
      case PLAYER_ONE_NIGHT -> night(first, plugin);
      case PLAYER_ONE_CLEAR -> !rains(first, plugin);
      case PLAYER_ONE_PDC_MATCH -> getPDCValue(first, getKey(args[0], plugin), plugin).equals(args[1]);
      case PLAYER_ONE_SNEAKING -> sneaking(first, plugin);
      case PLAYER_ONE_SPRINTING -> sprinting(first, plugin);
      case PLAYER_ONE_SWIMMING -> swimming(first, plugin);
      case PLAYER_ONE_FLYING -> flying(first, plugin);
      case PLAYER_ONE_ON_FIRE -> onFire(first, plugin);
      case PLAYER_ONE_ON_HIGHEST_BLOCK -> onTop(first, plugin);
      case PLAYER_ONE_RAIN -> rains(first, plugin);
      case PLAYER_ONE_THUNDER -> thunders(first, plugin);
      case PLAYER_ONE_OVERWORLD -> overworld(first, plugin);
      case PLAYER_ONE_NETHER -> nether(first, plugin);
      case PLAYER_ONE_END -> end(first, plugin);
      case PLAYER_ONE_OP -> op(first, plugin);
      case PLAYER_ONE_BLOCKING -> blocking(first, plugin);

      case PLAYER_ONE_HEALTH -> {
        final double health = getHealth(first, plugin);
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

      case PLAYER_ONE_FOOD -> {
        final int food = getFood(first, plugin);
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

      case PLAYER_ONE_AIR -> {
        final int air = getAir(first, plugin);
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

      case PLAYER_ONE_GODMODE -> {
        final int remainingGodMode = invisibleFor(first, plugin);
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

      // Player two

      case PLAYER_TWO_DAY -> day(second, plugin);
      case PLAYER_TWO_NIGHT -> night(second, plugin);
      case PLAYER_TWO_CLEAR -> !rains(second, plugin);
      case PLAYER_TWO_PDC_MATCH -> getPDCValue(second, getKey(args[0], plugin), plugin).equals(args[1]);
      case PLAYER_TWO_SNEAKING -> sneaking(second, plugin);
      case PLAYER_TWO_SPRINTING -> sprinting(second, plugin);
      case PLAYER_TWO_SWIMMING -> swimming(second, plugin);
      case PLAYER_TWO_FLYING -> flying(second, plugin);
      case PLAYER_TWO_ON_FIRE -> onFire(second, plugin);
      case PLAYER_TWO_ON_HIGHEST_BLOCK -> onTop(second, plugin);
      case PLAYER_TWO_RAIN -> rains(second, plugin);
      case PLAYER_TWO_THUNDER -> thunders(second, plugin);
      case PLAYER_TWO_OVERWORLD -> overworld(second, plugin);
      case PLAYER_TWO_NETHER -> nether(second, plugin);
      case PLAYER_TWO_END -> end(second, plugin);
      case PLAYER_TWO_OP -> op(second, plugin);
      case PLAYER_TWO_BLOCKING -> blocking(second, plugin);

      case PLAYER_TWO_HEALTH -> {
        final double health = getHealth(second, plugin);
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

      case PLAYER_TWO_FOOD -> {
        final int food = getFood(second, plugin);
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

      case PLAYER_TWO_AIR -> {
        final int air = getAir(second, plugin);
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

      case PLAYER_TWO_GODMODE -> {
        final int remainingGodMode = invisibleFor(second, plugin);
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

package com.roughlyunderscore.enchs.parsers.condition.generic.entity;

import com.roughlyunderscore.enchs.UnderscoreEnchants;
import com.roughlyunderscore.enchs.parsers.condition.ComparativeOperator;
import com.roughlyunderscore.enchs.parsers.condition.generic.AbstractConditionParser;
import com.roughlyunderscore.enchs.parsers.condition.generic.player.PlayerConditionParser;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Arrays;

import static com.roughlyunderscore.enchs.util.general.EntityUtils.*;
import static com.roughlyunderscore.enchs.util.general.Utils.*;

@ToString
public class EntityConditionParser extends AbstractConditionParser<Player> {
  @Getter
  private final Entity entity;

  public EntityConditionParser(final Player arg, final Entity entity, final String condition, final UnderscoreEnchants plugin) {
    super(arg, condition, plugin);
    this.entity = entity;
  }

  @Override
  public boolean evaluate() {
    final UnderscoreEnchants plugin = getPlugin();
    final String[] args0 = getCondition().split(" "), args = Arrays.copyOfRange(args0, 1, args0.length);
    final EntityCondition condition = EntityCondition.lookup(args0[0].replace("!", ""));
    /* ? debug */
    plugin.getDebugger().log("EntityConditionParser, Condition: " + (condition == null ? "null" : condition));
    final Player player = getArg();

    final ComparativeOperator operator = isComparativeOperator(args.length == 0 ? "" : args[0]);

    final boolean negate = getCondition().startsWith("!");
    /* ? debug */
    plugin.getDebugger().log("EntityConditionParser, Negate: " + negate);
    final boolean playerParse = new PlayerConditionParser(player, getCondition(), plugin).evaluate();

    final boolean entityParse = switch (condition) {
      case null -> false;
      case ENTITY_ON_FIRE -> onFire(entity, plugin);
      case ENTITY_SWIMMING -> swimming(entity, plugin);
      case ENTITY_ON_HIGHEST_BLOCK -> onTop(entity, plugin);

      case HEALTH -> {
        final double health = getHealth(entity, plugin);
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
    };

    final boolean result = entityParse || playerParse; // At least one of them should be true.

    if (negate) return !result;
    return result;
  }
}

package com.roughlyunderscore.enchs.parsers.condition.events.damage;

import com.roughlyunderscore.enchs.UnderscoreEnchants;
import com.roughlyunderscore.enchs.events.PlayerHurtsEntityEvent;
import com.roughlyunderscore.enchs.parsers.condition.ComparativeOperator;
import com.roughlyunderscore.enchs.parsers.condition.generic.entity.EntityConditionParser;
import lombok.ToString;
import org.bukkit.entity.Player;

import java.util.Arrays;

import static com.roughlyunderscore.enchs.util.general.EntityUtils.*;
import static com.roughlyunderscore.enchs.util.general.Utils.*;

@ToString
public class PlayerHurtsEntityConditionParser extends EntityConditionParser {
  private final PlayerHurtsEntityEvent event;

  public PlayerHurtsEntityConditionParser(final Player arg, final String condition, final UnderscoreEnchants plugin, final PlayerHurtsEntityEvent event) {
    super(arg, event.getEntity(), condition, plugin);
    this.event = event;
  }

  @Override
  public boolean evaluate() {
    if (super.evaluate()) return true;

    final String[] args0 = getCondition().split(" "), args = Arrays.copyOfRange(args0, 1, args0.length);
    final PlayerHurtsEntityCondition condition = PlayerHurtsEntityCondition.lookup(args0[0].replace("!", ""));

    if (condition == null) return false;

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

      case DAMAGE_LETHAL -> damage >= getHealth(getEntity(), plugin);
    };

    if (negate) return !result;
    return result;
  }
}

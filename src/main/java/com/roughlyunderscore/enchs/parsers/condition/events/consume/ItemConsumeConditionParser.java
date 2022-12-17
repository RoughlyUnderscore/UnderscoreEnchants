package com.roughlyunderscore.enchs.parsers.condition.events.consume;

import com.roughlyunderscore.enchs.UnderscoreEnchants;
import com.roughlyunderscore.enchs.parsers.condition.generic.player.PlayerConditionParser;
import lombok.ToString;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import java.util.Arrays;

import static com.roughlyunderscore.enchs.util.general.Utils.*;

@ToString
public class ItemConsumeConditionParser extends PlayerConditionParser {
  private final PlayerItemConsumeEvent event;

  public ItemConsumeConditionParser(final Player arg, final String condition, final UnderscoreEnchants plugin, final PlayerItemConsumeEvent event) {
    super(arg, condition, plugin);
    this.event = event;
  }

  @Override
  public boolean evaluate() {
    if (super.evaluate()) return true;

    final String[] args0 = getCondition().split(" "), args = Arrays.copyOfRange(args0, 1, args0.length);
    final ItemConsumeCondition condition = ItemConsumeCondition.lookup(args0[0].replace("!", ""));
    if (condition == null) return false;

    final boolean negate = getCondition().startsWith("!");
    final Material type = event.getItem().getType();

    final boolean result = switch (condition) {
      case FOOD_IS -> type.name().equalsIgnoreCase(args[0]);
      case IS_FOOD -> type != Material.POTION && type != Material.MILK_BUCKET && type != Material.HONEY_BOTTLE;
      case IS_MILK -> type == Material.MILK_BUCKET;
      case IS_HONEY -> type == Material.HONEY_BOTTLE;
      case IS_POTION -> type == Material.POTION;
      case IS_VEGETARIAN -> isVegetarian(type);
      case IS_PESCETARIAN -> isPescetarian(type);
    };

    if (negate) return !result;
    return result;

  }
}

package com.roughlyunderscore.enchs.parsers.condition.events.move;

import com.cryptomorin.xseries.XMaterial;
import com.roughlyunderscore.enchs.UnderscoreEnchants;
import com.roughlyunderscore.enchs.parsers.condition.generic.player.PlayerConditionParser;
import lombok.ToString;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Arrays;

import static com.roughlyunderscore.enchs.util.general.Utils.*;

@ToString
public class MoveConditionParser extends PlayerConditionParser {
  private final PlayerMoveEvent event;

  public MoveConditionParser(final Player arg, final String condition, final UnderscoreEnchants plugin, final PlayerMoveEvent event) {
    super(arg, condition, plugin);
    this.event = event;
  }

  @Override
  public boolean evaluate() {
    if (super.evaluate()) return true;

    final String[] args0 = getCondition().split(" "), args = Arrays.copyOfRange(args0, 1, args0.length);
    final MoveCondition condition = MoveCondition.lookup(args0[0].replace("!", ""));

    if (condition == null) return false;

    final Location from = event.getFrom().clone().subtract(0, 1, 0);
    final Location to = (event.getTo() == null ? event.getFrom() : event.getTo()).clone().subtract(0, 1, 0); // null check

    final boolean negate = getCondition().startsWith("!");

    final boolean result = switch (condition) {
      case FROM_IS -> from.getBlock().getType() == XMaterial.valueOf(args0[0]).parseMaterial();
      case TO_IS -> to.getBlock().getType() == XMaterial.valueOf(args0[0]).parseMaterial();
      case JUMP -> isByJump(event);
      case SAME_BLOCK -> isBySameBlock(event);
      case NOT_SAME_BLOCK -> isByDifferentBlocks(event);
      case HEAD_ROTATE -> isByHeadRotate(event);
    };

    if (negate) return !result;
    return result;
  }
}

package com.roughlyunderscore.enchs.parsers.condition.events.damage;

import com.roughlyunderscore.enchs.UnderscoreEnchants;
import com.roughlyunderscore.enchs.events.PlayerGotHurtEvent;
import com.roughlyunderscore.enchs.parsers.condition.ComparativeOperator;
import com.roughlyunderscore.enchs.parsers.condition.generic.player.PlayerConditionParser;
import lombok.ToString;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Arrays;

import static com.roughlyunderscore.enchs.util.general.PlayerUtils.*;
import static com.roughlyunderscore.enchs.util.general.Utils.*;

@ToString
public class PlayerGotHurtConditionParser extends PlayerConditionParser {
  private final PlayerGotHurtEvent event;

  public PlayerGotHurtConditionParser(final Player arg, final String condition, final UnderscoreEnchants plugin, final PlayerGotHurtEvent event) {
    super(arg, condition, plugin);
    this.event = event;
  }

  @Override
  public boolean evaluate() {
    if (super.evaluate()) return true;

    final String[] args0 = getCondition().split(" "), args = Arrays.copyOfRange(args0, 1, args0.length);
    final PlayerGotHurtCondition condition = PlayerGotHurtCondition.lookup(args0[0].replace("!", ""));

    if (condition == null) return false;

    final Player player = getArg();
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

      case CAUSE_BLOCK_EXPLOSION -> event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION;
      case CAUSE_HAZARDOUS_BLOCK -> event.getCause() == EntityDamageEvent.DamageCause.CONTACT;
      case CAUSE_ENTITY_CRAMMING -> event.getCause() == EntityDamageEvent.DamageCause.CRAMMING;
      case CAUSE_UNKNOWN_SOURCE -> event.getCause() == EntityDamageEvent.DamageCause.CUSTOM;
      case CAUSE_DRAGON_BREATH -> event.getCause() == EntityDamageEvent.DamageCause.DRAGON_BREATH;
      case CAUSE_ENTITY_ATTACK -> event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK;
      case CAUSE_ENTITY_EXPLOSION -> event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION;
      case CAUSE_ENTITY_SWEEP_ATTACK -> event.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK;
      case CAUSE_FALLING_BLOCK -> event.getCause() == EntityDamageEvent.DamageCause.FALLING_BLOCK;
      case CAUSE_FALLING -> event.getCause() == EntityDamageEvent.DamageCause.FALL;
      case CAUSE_DIRECT_FIRE_EXPOSURE -> event.getCause() == EntityDamageEvent.DamageCause.FIRE;
      case CAUSE_FIRE_TICK -> event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK;
      case CAUSE_FLYING_INTO_WALL -> event.getCause() == EntityDamageEvent.DamageCause.FLY_INTO_WALL;
      case CAUSE_FREEZING -> event.getCause() == EntityDamageEvent.DamageCause.FREEZE;
      case CAUSE_MAGMA_DAMAGE -> event.getCause() == EntityDamageEvent.DamageCause.HOT_FLOOR;
      case CAUSE_LAVA -> event.getCause() == EntityDamageEvent.DamageCause.LAVA;
      case CAUSE_LIGHTNING -> event.getCause() == EntityDamageEvent.DamageCause.LIGHTNING;
      case CAUSE_MAGIC -> event.getCause() == EntityDamageEvent.DamageCause.MAGIC;
      case CAUSE_POISON -> event.getCause() == EntityDamageEvent.DamageCause.POISON;
      case CAUSE_PROJECTILE -> event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE;
      case CAUSE_STARVATION -> event.getCause() == EntityDamageEvent.DamageCause.STARVATION;
      case CAUSE_SUFFOCATION -> event.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION;
      case CAUSE_PLUGIN_ENFORCEMENT -> event.getCause() == EntityDamageEvent.DamageCause.SUICIDE;
      case CAUSE_THORNS -> event.getCause() == EntityDamageEvent.DamageCause.THORNS;
      case CAUSE_VOID -> event.getCause() == EntityDamageEvent.DamageCause.VOID;
      case CAUSE_WITHERING -> event.getCause() == EntityDamageEvent.DamageCause.WITHER;
    };

    if (negate) return !result;
    return result;
  }
}

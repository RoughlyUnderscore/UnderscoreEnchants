package com.roughlyunderscore.enchs.parsers.action.generic.entity;

import com.roughlyunderscore.enchs.UnderscoreEnchants;
import com.roughlyunderscore.enchs.parsers.action.generic.AbstractActionParser;
import com.roughlyunderscore.enchs.parsers.action.generic.player.PlayerActionParser;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.mariuszgromada.math.mxparser.Expression;

import java.util.Arrays;

import static com.roughlyunderscore.enchs.util.general.EntityUtils.*;
import static com.roughlyunderscore.enchs.util.general.PlayerUtils.*;
import static com.roughlyunderscore.enchs.util.general.Utils.*;

@ToString
@EqualsAndHashCode(callSuper = true)
public class EntityActionParser extends AbstractActionParser<Player> {

  @Getter
  private final Entity entity;

  public EntityActionParser(final Event event, final Player argument, final Entity ent, final String action, final UnderscoreEnchants plugin) {
    super(event, argument, action, plugin);
    this.entity = ent;
  }

  @Override
  public void execute() {
    final EntityActions action = EntityActions.lookup(getAction().split(" ")[0]);
    if (action == null) return;

    final Player player = getArgument();
    final UnderscoreEnchants plugin = getPlugin();

    // calculating the delay and chance
    final String[] splitAction = getAction().split(" ");
    final String[] lastArgument = splitAction[splitAction.length - 1].split(";");

    long delay = 0, chance = 100;

    if (arrayOfStringsContainsPartly(lastArgument, "delay:"))
      delay = (long)
        clamp(0, 100, parseL(Arrays.stream(lastArgument)
          .filter(st -> st.startsWith("delay:"))
          .findFirst()
          .orElseThrow(() -> new IllegalArgumentException("Could not find delay but it's apparently present in the argument postfix!"))
          .split(":")[1]));

    if (arrayOfStringsContainsPartly(lastArgument, "chance:"))
      chance = (long)
        clamp(0, 100, parseL(Arrays.stream(lastArgument)
          .filter(st -> st.startsWith("chance:"))
          .findFirst()
          .orElseThrow(() -> new IllegalArgumentException("Could not find delay but it's apparently present in the argument postfix!"))
          .split(":")[1]));

    if (Math.random() * 100 >= chance) return;


    Bukkit.getScheduler().runTaskLater(plugin, () -> {
      final String[] args0 = parseEntityPlaceholders(getAction(), player, entity, plugin), args = Arrays.copyOfRange(args0, 0, args0.length); // args0 IS REDUNDANT BUT I'M TOO LAZY TO DELETE IT!
      new PlayerActionParser(getEvent(), player, getAction(), plugin).execute(); // try to parse Player actions as well
      switch (action) {

        // Assumption: [0]ENTITY-VELOCITY [1]X [2]Y [3]Z
        case ENTITY_VELOCITY -> produceVelocity(entity, parseD(args[1]), parseD(args[2]), parseD(args[3]), plugin);

        // Assumption: [0]ENTITY-EFFECT [1]EFFECT [2]DURATION [3]AMPLIFIER
        case ENTITY_EFFECT -> addPotion(entity, args[1], parseI(args[2]), parseI(args[3]), plugin);

        // Assumption: [0]ENTITY-REMOVE-EFFECT [1]EFFECT
        case ENTITY_REMOVE_EFFECT -> removePotion(entity, args[1], plugin);

        // Assumption: [0]ENTITY-REMOVE-BUFFS
        case ENTITY_REMOVE_BUFFS -> removeBuffs(entity, plugin);

        // Assumption: [0]ENTITY-REMOVE-DEBUFFS
        case ENTITY_REMOVE_DEBUFFS -> removeDebuffs(entity, plugin);

        // Assumption: [0]ENTITY-STRIKE-FAKE-LIGHTNING
        case ENTITY_STRIKE_FAKE_LIGHTNING -> strikeFakeLightning(getLocation(entity, plugin), plugin);

        // Assumption: [0]ENTITY-STRIKE-LIGHTNING
        case ENTITY_STRIKE_LIGHTNING -> strikeLightning(getLocation(entity, plugin), plugin);

        // Assumption: [0]ENTITY-PARTICLE [1]PARTICLE
        case ENTITY_PARTICLE -> spawnParticle(entity, getLocation(entity, plugin), args[1], plugin);

        // Assumption: [0]ENTITY-PARTICLE-BOOTS [1]PARTICLE
        case ENTITY_PARTICLE_BOOTS -> spawnParticleBoots(entity, getLocation(entity, plugin), args[1], plugin);

        // Assumption: [0]ENTITY-TELEPORT [1]X [2]Y [3]Z
        case ENTITY_TELEPORT -> setLocation(entity, parseD(args[1]), parseD(args[2]), parseD(args[3]), plugin);

        // Assumption: [0]ENTITY-DIRECTION [1]YAW [2]PITCH
        case ENTITY_DIRECTION -> setDirection(entity, parseF(args[1]), parseF(args[2]), plugin);

        // Assumption: [0]ENTITY-WORLD [1]WORLD
        case ENTITY_WORLD -> setWorld(entity, args[1], plugin);

        // Assumption: [0]ENTITY-FORWARD [1]DISTANCE
        case ENTITY_FORWARD -> sendForward(entity, parseD(args[1]), plugin);

        // Assumption: [0]ENTITY-SET-HEALTH [1]HEALTH
        case ENTITY_SET_HEALTH -> setHealth(entity, parseD(args[1]), plugin);

        // Assumption: [0]ENTITY-SET-MAX-HEALTH [1]MAX-HEALTH
        case ENTITY_SET_MAX_HEALTH -> setMaximumHealth(entity, parseD(args[1]), plugin);

        // Assumption: [0]ENTITY-SET-FIRE [1]DURATION-TICKS
        case ENTITY_SET_FIRE -> setFire(entity, parseI(args[1]), plugin);

        // Assumption: [0]ENTITY-SEND-ARROW
        case ENTITY_SEND_ARROW -> sendArrow(entity, plugin);

        // Assumption: [0]ENTITY-SEND-FIREBALL
        case ENTITY_SEND_FIREBALL -> sendFireball(entity, plugin);

      }
    }, delay);

    // completeAction(runnable, args, plugin);

    // action.executeAction(getArgument(), getAction(), getPlugin());
  }

  @Override
  public String[] parsePlaceholders(final String args, final Player player, final UnderscoreEnchants plugin) {
    throw new UnsupportedOperationException("For entity actions, parseEntityPlaceholders should be called instead!");
  }
  public String[] parseEntityPlaceholders(String args, final Player player, final Entity entity, final UnderscoreEnchants plugin) {
    for (final String str : args.split("[(\\)\\+\\*\\-\\/ ]")) {
      switch (EntityActionPlaceholders.lookup(str)) {
        case null -> {
        }

        case ENTITY_TYPE -> args = args.replace(str, entity.getType().name());
        case ENTITY_X -> args = args.replace(str, getXString(entity, plugin));
        case ENTITY_Y -> args = args.replace(str, getYString(entity, plugin));
        case ENTITY_Z -> args = args.replace(str, getZString(entity, plugin));
        case ENTITY_YAW -> args = args.replace(str, getYawString(entity, plugin));
        case ENTITY_MONEY -> args = args.replace(str, getMoneyString(player, plugin));
        case ENTITY_HEALTH -> args = args.replace(str, getHealthString(entity, plugin));
        case ENTITY_MAX_HEALTH -> args = args.replace(str, getMaximumHealthString(entity, plugin));
      }
    }

    final String[] evaluatedArgsArray = args.split(" ");
    for (int i = 0; i < evaluatedArgsArray.length; i++) {
      final double exp = new Expression(evaluatedArgsArray[i]).calculate();
      if (!Double.isNaN(exp)) evaluatedArgsArray[i] = Double.toString(exp);
    }

    final String evaluatedArgs = arrayToString(evaluatedArgsArray, " ");

    return new PlayerActionParser(getEvent(), getArgument(), evaluatedArgs, getPlugin()).parsePlaceholders(evaluatedArgs, player, plugin);
  }
}

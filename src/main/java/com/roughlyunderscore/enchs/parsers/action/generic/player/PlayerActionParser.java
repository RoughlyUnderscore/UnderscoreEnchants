package com.roughlyunderscore.enchs.parsers.action.generic.player;

import com.roughlyunderscore.enchs.UnderscoreEnchants;
import com.roughlyunderscore.enchs.parsers.action.generic.AbstractActionParser;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.scheduler.BukkitRunnable;
import org.mariuszgromada.math.mxparser.Expression;

import java.util.Arrays;
import java.util.Random;

import static com.roughlyunderscore.enchs.parsers.action.ActionUtils.*;
import static com.roughlyunderscore.enchs.util.general.EntityUtils.*;
import static com.roughlyunderscore.enchs.util.general.PlayerUtils.*;
import static com.roughlyunderscore.enchs.util.general.Utils.*;

@ToString
@EqualsAndHashCode(callSuper = true)
public class PlayerActionParser extends AbstractActionParser<Player> {
  public PlayerActionParser(final Event event, final Player argument, final String action, final UnderscoreEnchants plugin) {
    super(event, argument, action, plugin);
  }

  @Override
  public void execute() {
    final PlayerActions action = PlayerActions.lookup(getAction().split(" ")[0]);
    if (action == null) return;

    final Player player = getArgument();
    final UnderscoreEnchants plugin = getPlugin();

    final String[] args0 = parsePlaceholders(getAction(), player, player, plugin), args = Arrays.copyOfRange(args0, 0, args0.length); // args[0] IS REDUNDANT BUT I'M TOO LAZY TO DELETE IT!
    plugin.getUnderscoreLogger().info("Arguments (PlayerActionParser): " + Arrays.toString(args));

    final BukkitRunnable runnable = new BukkitRunnable() {
      @Override
      public void run() {
        switch (action) {
          // Assumption: [0]PLAYER-PDC-SET [1]KEY [2]VALUE
          case PLAYER_PDC_SET -> setPDC(player, NamespacedKey.fromString(args[1], plugin), args[2], plugin);

          // Assumption: [0]PLAYER-VELOCITY [1]X [2]Y [3]Z
          case PLAYER_VELOCITY -> produceVelocity(player, args[1], args[2], args[3], plugin);

          // Assumption: [0]PLAYER-SOUND [1]SOUND
          case PLAYER_SOUND -> playSound(player, args[1], plugin);

          // Assumption: [0]PLAY-SOUND [1]SOUND [2]X [3]Y [4]Z
          case LOCATION_SOUND -> playWorldSound(player, args[2], args[3], args[4], args[1], plugin);

          // Assumption: [0]PLAYER-EFFECT [1]EFFECT [2]DURATION [3]AMPLIFIER
          case PLAYER_EFFECT -> addPotion(player, args[1], parseI(args[2]), parseI(args[3]), plugin);

          // Assumption: [0]PLAYER-REMOVE-EFFECT [1]EFFECT
          case PLAYER_REMOVE_EFFECT -> removePotion(player, args[1], plugin);

          // Assumption: [0]PLAYER-REMOVE-BUFFS
          case PLAYER_REMOVE_BUFFS -> removeBuffs(player, plugin);

          // Assumption: [0]PLAYER-REMOVE-DEBUFFS
          case PLAYER_REMOVE_DEBUFFS -> removeDebuffs(player, plugin);

          // Assumption: [0]PLAYER-DROP-ITEM [1]ITEM [2]X [3]Y [4]Z
          case PLAYER_DROP_ITEM -> dropItem(
            new Location(player.getWorld(), parseD(args[2]), parseD(args[3]), parseD(args[4])),
            parseItem(args[1]),
            plugin
          );

          // Assumption: [0]DROP-ITEM-WORLD [1]ITEM [2]X [3]Y [4]Z [5]WORLD
          case DROP_ITEM_WORLD -> dropItem(
            new Location(Bukkit.getWorld(args[5]), parseD(args[2]), parseD(args[3]), parseD(args[4])),
            parseItem(args[1]),
            plugin
          );

          // Assumption: [0]PLAYER-SET-HAND [1]ITEM
          case PLAYER_SET_HAND -> setMainHand(player, parseItem(args[1]), plugin);

          // Assumption: [0]PLAYER-SHUFFLE-HOTBAR
          case PLAYER_SHUFFLE_HOTBAR -> shuffleHotbar(player, plugin);

          // Assumption: [0]PLAYER-SHUFFLE-INVENTORY
          case PLAYER_SHUFFLE_INVENTORY -> shuffleInventory(player, plugin);

          // Assumption: [0]PLAYER-STRIKE-FAKE-LIGHTNING
          case PLAYER_STRIKE_FAKE_LIGHTNING -> strikeFakeLightning(getLocation(player, plugin), plugin);

          // Assumption: [0]PLAYER-STRIKE-LIGHTNING
          case PLAYER_STRIKE_LIGHTNING -> strikeLightning(getLocation(player, plugin), plugin);

          // Assumption: [0]PLAYER-PARTICLE [1]PARTICLE
          case PLAYER_PARTICLE -> spawnParticle(player, getLocation(player, plugin), args[1], plugin);

          // Assumption: [0]PLAYER-PARTICLE-BOOTS [1]PARTICLE
          case PLAYER_PARTICLE_BOOTS -> spawnParticleBoots(player, getLocation(player, plugin), args[1], plugin);

          // Assumption: [0]SPAWN-ENTITY [1]ENTITY [2]X [3]Y [4]Z
          case SPAWN_ENTITY -> spawnEntity(player, args[1], args[2], args[3], args[4], plugin);

          // Assumption: [0]SPAWN-ENTITY-DETAILED [1]ENTITY [2]X [3]Y [4]Z [5]NAME [6]HEALTH [7]POTION-EFFECT [8]DURATION [9]AMPLIFIER
          case SPAWN_ENTITY_DETAILED -> spawnEntity(player, args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], plugin);

          // Assumption: [0]PLAYER-TELEPORT [1]X [2]Y [3]Z
          case PLAYER_TELEPORT -> setLocation(player, parseD(args[1]), parseD(args[2]), parseD(args[3]), plugin);

          // Assumption: [0]PLAYER-DIRECTION [1]YAW [2]PITCH
          case PLAYER_DIRECTION -> setDirection(player, parseF(args[1]), parseF(args[2]), plugin);

          // Assumption: [0]PLAYER-WORLD [1]WORLD
          case PLAYER_WORLD -> setWorld(player, args[1], plugin);

          // Assumption: [0]PLAYER-FORWARD [1]DISTANCE
          case PLAYER_FORWARD -> sendForward(player, parseD(args[1]), plugin);

          // Assumption: [0]CREATE-WORLD [1]WORLD [2]TYPE [4]GENERATOR
          case CREATE_WORLD -> createWorld(args[1], args[2], args[3], plugin);

          // Assumption: [0]PLAYER-TELEPORT-BED
          case PLAYER_TELEPORT_BED -> teleportToBed(player, plugin);

          // Assumption: [0]PLAYER-SET-HEALTH [1]HEALTH
          case PLAYER_SET_HEALTH -> setHealth(player, parseD(args[1]), plugin);

          // Assumption: [0]PLAYER-SET-FOOD [1]FOOD
          case PLAYER_SET_FOOD -> setFood(player, parseI(args[1]), plugin);

          // Assumption: [0]PLAYER-SET-MAX-HEALTH [1]MAX-HEALTH
          case PLAYER_SET_MAX_HEALTH -> setMaximumHealth(player, parseD(args[1]), plugin);

          // Assumption: [0]PLAYER-SET-FIRE [1]DURATION-TICKS
          case PLAYER_SET_FIRE -> setFire(player, parseI(args[1]), plugin);

          // Assumption: [0]PLAYER-SET-AIR [1]AIR
          case PLAYER_SET_AIR -> setAir(player, parseI(args[1]), plugin);

          // Assumption: [0]PLAYER-SET-EXP [1]EXP
          case PLAYER_SET_EXP -> setXp(player, parseI(args[1]), plugin);

          // Assumption: [0]PLAYER-SET-LEVEL [1]LEVEL
          case PLAYER_SET_LEVEL -> setLevel(player, parseI(args[1]), plugin);

          // Assumption: [0]PLAYER-SEND-ARROW
          case PLAYER_SEND_ARROW -> sendArrow(player, plugin);

          // Assumption: [0]PLAYER-SEND-FIREBALL
          case PLAYER_SEND_FIREBALL -> sendFireball(player, plugin);

          // Assumption: [0]PLAYER-SET-MONEY [1]MONEY
          case PLAYER_SET_MONEY -> setMoney(player, parseI(args[1]), plugin);

          // Assumption: [0]PLAYER-SET-GAMEMODE [1]GAMEMODE
          case PLAYER_SET_GAMEMODE -> setGamemode(player, args[1], plugin);

          // Assumption: [0]PLAYER-GIVE-HEAD [1]PLAYER NAME
          case PLAYER_GIVE_HEAD -> giveHeadOf(player, args[1], plugin);

          // Assumption: [0]PLAYER-GIVE-ITEM [1]ITEM
          case PLAYER_GIVE_ITEM -> give(player, parseItem(args[1]), plugin);

          // Assumption: [0]PLAYER-DROP-HAND
          case PLAYER_DROP_HAND -> dropHand(player, plugin);

          // Assumption: [0]PLAYER-DAMAGE-ARMOR [1]DAMAGE [2]ARMOR PIECE
          case PLAYER_DAMAGE_ARMOR -> damageArmorPiece(player, args[2], args[1], plugin);

          // Assumption: [0]PLAYER-DAMAGE-HAND [1]DAMAGE
          case PLAYER_DAMAGE_HAND -> damageHand(player, args[1], plugin);

          // Assumption: [0]PLAYER-REPAIR-ARMOR [1]ARMOR PIECE
          case PLAYER_REPAIR_ARMOR -> fixArmorPiece(player, args[1], plugin);

          // Assumption: [0]PLAYER-REPAIR-HAND
          case PLAYER_REPAIR_HAND -> fixHand(player, plugin);

          // Assumption: [0]SET-BLOCK [1]MATERIAL [2]X [3]Y [4]Z
          case SET_BLOCK -> setBlock(getWorld(player, plugin), parseI(args[2]), parseI(args[3]), parseI(args[4]), parseItem(args[1]), plugin);

          // Assumption: [0]TIME [1]WHAT-TIME
          case TIME -> setTime(getWorld(player, plugin), args[1], plugin);

          // Assumption: [0]WEATHER [1]WHAT-WEATHER
          case WEATHER -> setWeather(getWorld(player, plugin), args[1], plugin);

          // Assumption: [0]PLAYER-TIME [1]TIME
          case PLAYER_TIME -> setPlayerTime(player, args[1], plugin);

          // Assumption: [0]PLAYER-WEATHER [1]WEATHER
          case PLAYER_WEATHER -> setPlayerWeather(player, args[1], plugin);

          // Assumption: [0]RESET-PLAYER-TIME
          case RESET_PLAYER_TIME -> resetPlayerTime(player, plugin);

          // Assumption: [0]RESET-PLAYER-WEATHER
          case RESET_PLAYER_WEATHER -> resetPlayerWeather(player, plugin);

          // Assumption: [0]PLAYER-GODMODE [1]DURATION
          case PLAYER_GODMODE -> makeInvisibleFor(player, parseI(args[1]), plugin);

          // Assumption: [0]PLAYER-BOSSBAR [1]STYLE [2]DURATION [3]TEXT
          case PLAYER_BOSSBAR -> sendBar(player, args[1], args[2], collectTail(args, 3, " ", (String[]) arrayOf("delay:", "chance:")), plugin);

          // Assumption: [0]PLAYER-TITLE [1]TITLE
          case PLAYER_TITLE -> sendTitle(player, collectTail(args, 1, " ", (String[]) arrayOf("delay:", "chance:")), plugin);

          // Assumption: [0]PLAYER-SUBTITLE [1]SUBTITLE
          case PLAYER_SUBTITLE -> sendSubtitle(player, collectTail(args, 1, " ", (String[]) arrayOf("delay:", "chance:")), plugin);

          // Assumption: [0]PLAYER-SEND-MESSAGE [1]MESSAGE
          case PLAYER_SEND_MESSAGE -> tellTo(player, collectTail(args, 1, " ", (String[]) arrayOf("delay:", "chance:")), plugin);

          // Assumption: [0]PLAYER-SEND-ACTIONBAR [1]ACTIONBAR
          case PLAYER_SEND_ACTIONBAR -> sendActionbar(player, collectTail(args, 1, " ", (String[]) arrayOf("delay:", "chance:")), plugin);

          // Assumption: [0]PLAYER-SEND-CHAT [1]CHAT
          case PLAYER_SEND_CHAT -> say(player, collectTail(args, 1, " ", (String[]) arrayOf("delay:", "chance:")), plugin);

          // Assumption: [0]BROADCAST-MESSAGE [1]MESSAGE
          case BROADCAST_MESSAGE -> announce(collectTail(args, 1, " ", (String[]) arrayOf("delay:", "chance:")), plugin);

          // Assumption: [0]LOG-MESSAGE [1]MESSAGE
          case LOG_MESSAGE -> plugin.debugger.log(collectTail(args, 1, " ", (String[]) arrayOf("delay:", "chance:")));

          // Assumption: [0]CONSOLE-COMMAND [1]COMMAND
          case CONSOLE_COMMAND -> Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), collectTail(args, 1, " ", (String[]) arrayOf("delay:", "chance:")));

        }
      }
    };

    completeAction(runnable, args, plugin);

    // action.executeAction(getArgument(), getAction(), getPlugin());
  }

  @Override
  public String[] parsePlaceholders(String args, final Player player, final UnderscoreEnchants plugin) {
    for (final String str : args.split(" ")) {
      switch (PlayerActionPlaceholders.lookup(str)) {
        case null -> {
        }

        case RANDOM_INT -> {
          // random_int_[min]_[max]
          String[] split = str.split(":");
          if (split.length == 4) {
            try {
              int min = Integer.parseInt(split[2]);
              int max = Integer.parseInt(split[3]);
              args = args.replace(str, String.valueOf(new Random().nextInt(max - min) + min));
            } catch (NumberFormatException e) {
              plugin.debugger.log("Invalid random int: " + str);
            }
          }
        }
        case RANDOM_DOUBLE -> {
          // random_double_[min]_[max]
          String[] split = str.split(":");
          if (split.length == 4) {
            try {
              double min = Double.parseDouble(split[2]);
              double max = Double.parseDouble(split[3]);
              args = args.replace(str, String.valueOf(new Random().nextDouble() * (max - min) + min));
            } catch (NumberFormatException e) {
              plugin.debugger.log("Invalid random double: " + str);
            }
          }
        }
        case MAX_Y_AT_LOCATION -> {
          // max_y_at_[x]_[z]
          String[] split = str.split("_");
          if (split.length == 5) {
            try {
              double x = Double.parseDouble(split[3]);
              double z = Double.parseDouble(split[4]);
              args = args.replace(str, String.valueOf(player.getWorld().getHighestBlockYAt((int) x, (int) z)));
            } catch (NumberFormatException e) {
              plugin.debugger.log("Invalid max Y at location: " + str);
            }
          }
        }
        case PLAYER_PDC -> {
          // player_pdc_[key]
          String[] split = str.split("_");
          if (split.length == 3) {
            String key = split[2];
            args = args.replace(str, getPDCValue(player, getKey(key, plugin), plugin).toString());
          }
        }
        case BLOCK_AT_LOC -> {
          // block_at_location_[world]_[x]_[y]_[z]
          String[] split = str.split("_");
          if (split.length == 7) {
            try {
              World world = Bukkit.getWorld(split[3]);
              double x = Double.parseDouble(split[4]);
              double y = Double.parseDouble(split[5]);
              double z = Double.parseDouble(split[6]);
              args = args.replace(str, world.getBlockAt((int) x, (int) y, (int) z).getType().name());
            } catch (NumberFormatException e) {
              plugin.debugger.log("Invalid block at location: " + str);
            }
          }
        }

        case PLAYER_NAME -> args = args.replace(str, player.getName());
        case PLAYER_UUID -> args = args.replace(str, player.getUniqueId().toString());
        case PLAYER_X -> args = args.replace(str, getXString(player, plugin));
        case PLAYER_Y -> args = args.replace(str, getYString(player, plugin));
        case PLAYER_Z -> args = args.replace(str, getZString(player, plugin));
        case PLAYER_YAW -> args = args.replace(str, getYawString(player, plugin));
        case PLAYER_PITCH -> args = args.replace(str, getPitchString(player, plugin));
        case PLAYER_MONEY -> args = args.replace(str, getMoneyString(player, plugin));
        case PLAYER_HEALTH -> args = args.replace(str, getHealthString(player, plugin));
        case PLAYER_MAX_HEALTH -> args = args.replace(str, getMaximumHealthString(player, plugin));
        case PLAYER_FOOD_LEVEL -> args = args.replace(str, getFoodString(player, plugin));
        case PLAYER_XP_LEVEL -> args = args.replace(str, getLevelString(player, plugin));
        case PLAYER_XP_PROGRESS -> args = args.replace(str, getXpString(player, plugin));
        case PLAYER_AIR_LEVEL -> args = args.replace(str, getAirString(player, plugin));
        case PLAYER_MAX_AIR_LEVEL -> args = args.replace(str, getMaximumAirString(player, plugin));
        case PLAYER_GODMODE -> args = args.replace(str, invisibleForString(player, plugin));
        case RANDOM_NEGATIVE_EFFECT -> args = args.replace(str, plugin.getPositiveEffects().get(new Random().nextInt(plugin.getPositiveEffects().size())).getName());
        case RANDOM_POSITIVE_EFFECT -> args = args.replace(str, plugin.getNegativeEffects().get(new Random().nextInt(plugin.getNegativeEffects().size())).getName());
        case PLAYER_WORLD -> args = args.replace(str, getWorldName(player, plugin));
        case PLAYER_IP -> args = args.replace(str, getIP(player, plugin));
        case PLAYER_PING -> args = args.replace(str, getPingString(player, plugin));
        case PLAYER_GAMEMODE -> args = args.replace(str, getGameMode(player, plugin).name());
      }
    }

    final String[] evaluatedArgs = args.split(" ");
    for (int i = 0; i < evaluatedArgs.length; i++) {
      final double exp = new Expression(evaluatedArgs[i]).calculate();
      if (!Double.isNaN(exp)) evaluatedArgs[i] = Double.toString(exp);
    }

    return evaluatedArgs;
  }
}

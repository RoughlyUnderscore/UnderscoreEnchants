package com.roughlyunderscore.enchs.parsers.action.generic.doubleplayer;

import com.roughlyunderscore.enchs.UnderscoreEnchants;
import com.roughlyunderscore.enchs.parsers.action.generic.AbstractActionParser;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.mariuszgromada.math.mxparser.Expression;

import java.util.Arrays;
import java.util.Random;

import static com.roughlyunderscore.enchs.parsers.action.ActionUtils.*;
import static com.roughlyunderscore.enchs.util.general.EntityUtils.*;
import static com.roughlyunderscore.enchs.util.general.PlayerUtils.*;
import static com.roughlyunderscore.enchs.util.general.Utils.*;

@ToString
@EqualsAndHashCode(callSuper = true)
public class DoublePlayerActionParser extends AbstractActionParser<Player> {
  @Getter
  private final Player first, second;

  public DoublePlayerActionParser(final Event event, final Player first, final Player second, final String action, final UnderscoreEnchants plugin) {
    super(event, first, action, plugin);
    this.first = first;
    this.second = second;
  }

  @Override
  public void execute() {
    final DoublePlayerActions action = DoublePlayerActions.lookup(getAction().split(" ")[0]);
    if (action == null) return;

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
      final String[] args0 = parsePlaceholders(getAction(), first, second, plugin), args = Arrays.copyOfRange(args0, 0, args0.length); // args0 IS REDUNDANT BUT I'M TOO LAZY TO DELETE IT!

      switch (action) {
        // Assumption: [0]BROADCAST-MESSAGE [1]MESSAGE
        case BROADCAST_MESSAGE -> announce(collectTail(args, 1, " ", (String[]) arrayOf("delay:", "chance:")), plugin);

        // Assumption: [0]LOG-MESSAGE [1]MESSAGE
        case LOG_MESSAGE -> plugin.debugger.log(collectTail(args, 1, " ", (String[]) arrayOf("delay:", "chance:")));

        // Assumption: [0]CONSOLE-COMMAND [1]COMMAND
        case CONSOLE_COMMAND -> Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), collectTail(args, 1, " ", (String[]) arrayOf("delay:", "chance:")));

        // Assumption: [0]PLAY-SOUND [1]SOUND [2]X [3]Y [4]Z
        case LOCATION_SOUND -> playWorldSound(first, args[2], args[3], args[4], args[1], plugin);

        // Assumption: [0]DROP-ITEM-WORLD [1]ITEM [2]X [3]Y [4]Z [5]WORLD
        case DROP_ITEM_WORLD -> dropItem(
          new Location(Bukkit.getWorld(args[5]), parseD(args[2]), parseD(args[3]), parseD(args[4])),
          parseItem(args[1]),
          plugin
        );
          
        // Assumption: [0]SPAWN-ENTITY [1]ENTITY [2]X [3]Y [4]Z
        case SPAWN_ENTITY -> spawnEntity(first, args[1], args[2], args[3], args[4], plugin);

        // Assumption: [0]SPAWN-ENTITY-DETAILED [1]ENTITY [2]X [3]Y [4]Z [5]NAME [6]HEALTH [7]POTION-EFFECT [8]DURATION [9]AMPLIFIER
        case SPAWN_ENTITY_DETAILED -> spawnEntity(first, args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], plugin);

        // Assumption: [0]CREATE-WORLD [1]WORLD [2]TYPE [4]GENERATOR
        case CREATE_WORLD -> createWorld(args[1], args[2], args[3], plugin);

        // Assumption: [0]SET-BLOCK [1]MATERIAL [2]X [3]Y [4]Z
        case SET_BLOCK -> setBlock(getWorld(first, plugin), parseI(args[2]), parseI(args[3]), parseI(args[4]), parseItem(args[1]), plugin);

        // Assumption: [0]TIME [1]WHAT-TIME
        case TIME -> setTime(getWorld(first, plugin), args[1], plugin);

        // Assumption: [0]WEATHER [1]WHAT-WEATHER
        case WEATHER -> setWeather(getWorld(first, plugin), args[1], plugin);








        ////////////////////////////////////////////////////////////////////////////////////////////
        //// =================================== PLAYER TWO =================================== ////
        ////////////////////////////////////////////////////////////////////////////////////////////








        // Assumption: [0]PLAYER-ONE-PDC-SET [1]KEY [2]VALUE
        case PLAYER_ONE_PDC_SET -> setPDC(first, NamespacedKey.fromString(args[1], plugin), args[2], plugin);

        // Assumption: [0]PLAYER-ONE-VELOCITY [1]X [2]Y [3]Z
        case PLAYER_ONE_VELOCITY -> produceVelocity(first, args[1], args[2], args[3], plugin);

        // Assumption: [0]PLAYER-ONE-SOUND [1]SOUND
        case PLAYER_ONE_SOUND -> playSound(first, args[1], plugin);

        // Assumption: [0]PLAYER-ONE-EFFECT [1]EFFECT [2]DURATION [3]AMPLIFIER
        case PLAYER_ONE_EFFECT -> addPotion(first, args[1], parseI(args[2]), parseI(args[3]), plugin);

        // Assumption: [0]PLAYER-ONE-REMOVE-EFFECT [1]EFFECT
        case PLAYER_ONE_REMOVE_EFFECT -> removePotion(first, args[1], plugin);

        // Assumption: [0]PLAYER-ONE-REMOVE-BUFFS
        case PLAYER_ONE_REMOVE_BUFFS -> removeBuffs(first, plugin);

        // Assumption: [0]PLAYER-ONE-REMOVE-DEBUFFS
        case PLAYER_ONE_REMOVE_DEBUFFS -> removeDebuffs(first, plugin);

        // Assumption: [0]PLAYER-ONE-DROP-ITEM [1]ITEM [2]X [3]Y [4]Z
        case PLAYER_ONE_DROP_ITEM -> dropItem(
          new Location(first.getWorld(), parseD(args[2]), parseD(args[3]), parseD(args[4])),
          parseItem(args[1]),
          plugin
        );

        // Assumption: [0]PLAYER-ONE-SET-HAND [1]ITEM
        case PLAYER_ONE_SET_HAND -> setMainHand(first, parseItem(args[1]), plugin);

        // Assumption: [0]PLAYER-ONE-SHUFFLE-HOTBAR
        case PLAYER_ONE_SHUFFLE_HOTBAR -> shuffleHotbar(first, plugin);

        // Assumption: [0]PLAYER-ONE-SHUFFLE-INVENTORY
        case PLAYER_ONE_SHUFFLE_INVENTORY -> shuffleInventory(first, plugin);

        // Assumption: [0]PLAYER-ONE-STRIKE-FAKE-LIGHTNING
        case PLAYER_ONE_STRIKE_FAKE_LIGHTNING -> strikeFakeLightning(getLocation(first, plugin), plugin);

        // Assumption: [0]PLAYER-ONE-STRIKE-LIGHTNING
        case PLAYER_ONE_STRIKE_LIGHTNING -> strikeLightning(getLocation(first, plugin), plugin);

        // Assumption: [0]PLAYER-ONE-PARTICLE [1]PARTICLE
        case PLAYER_ONE_PARTICLE -> spawnParticle(first, getLocation(first, plugin), args[1], plugin);

        // Assumption: [0]PLAYER-ONE-PARTICLE-BOOTS [1]PARTICLE
        case PLAYER_ONE_PARTICLE_BOOTS -> spawnParticleBoots(first, getLocation(first, plugin), args[1], plugin);

        // Assumption: [0]PLAYER-ONE-TELEPORT [1]X [2]Y [3]Z
        case PLAYER_ONE_TELEPORT -> setLocation(first, parseD(args[1]), parseD(args[2]), parseD(args[3]), plugin);

        // Assumption: [0]PLAYER-ONE-DIRECTION [1]YAW [2]PITCH
        case PLAYER_ONE_DIRECTION -> setDirection(first, parseF(args[1]), parseF(args[2]), plugin);

        // Assumption: [0]PLAYER-ONE-WORLD [1]WORLD
        case PLAYER_ONE_WORLD -> setWorld(first, args[1], plugin);

        // Assumption: [0]PLAYER-ONE-FORWARD [1]DISTANCE
        case PLAYER_ONE_FORWARD -> sendForward(first, parseD(args[1]), plugin);
          
        // Assumption: [0]PLAYER-ONE-TELEPORT-BED
        case PLAYER_ONE_TELEPORT_BED -> teleportToBed(first, plugin);

        // Assumption: [0]PLAYER-ONE-SET-HEALTH [1]HEALTH
        case PLAYER_ONE_SET_HEALTH -> setHealth(first, parseD(args[1]), plugin);

        // Assumption: [0]PLAYER-ONE-SET-FOOD [1]FOOD
        case PLAYER_ONE_SET_FOOD -> setFood(first, parseI(args[1]), plugin);

        // Assumption: [0]PLAYER-ONE-SET-MAX-HEALTH [1]MAX-HEALTH
        case PLAYER_ONE_SET_MAX_HEALTH -> setMaximumHealth(first, parseD(args[1]), plugin);

        // Assumption: [0]PLAYER-ONE-SET-FIRE [1]DURATION-TICKS
        case PLAYER_ONE_SET_FIRE -> setFire(first, parseI(args[1]), plugin);

        // Assumption: [0]PLAYER-ONE-SET-AIR [1]AIR
        case PLAYER_ONE_SET_AIR -> setAir(first, parseI(args[1]), plugin);

        // Assumption: [0]PLAYER-ONE-SET-EXP [1]EXP
        case PLAYER_ONE_SET_EXP -> setXp(first, parseI(args[1]), plugin);

        // Assumption: [0]PLAYER-ONE-SET-LEVEL [1]LEVEL
        case PLAYER_ONE_SET_LEVEL -> setLevel(first, parseI(args[1]), plugin);

        // Assumption: [0]PLAYER-ONE-SEND-ARROW
        case PLAYER_ONE_SEND_ARROW -> sendArrow(first, plugin);

        // Assumption: [0]PLAYER-ONE-SEND-FIREBALL
        case PLAYER_ONE_SEND_FIREBALL -> sendFireball(first, plugin);

        // Assumption: [0]PLAYER-ONE-SET-MONEY [1]MONEY
        case PLAYER_ONE_SET_MONEY -> setMoney(first, parseI(args[1]), plugin);

        // Assumption: [0]PLAYER-ONE-SET-GAMEMODE [1]GAMEMODE
        case PLAYER_ONE_SET_GAMEMODE -> setGamemode(first, args[1], plugin);

        // Assumption: [0]PLAYER-ONE-GIVE-HEAD [1]PLAYER NAME
        case PLAYER_ONE_GIVE_HEAD -> giveHeadOf(first, args[1], plugin);

        // Assumption: [0]PLAYER-ONE-GIVE-ITEM [1]ITEM
        case PLAYER_ONE_GIVE_ITEM -> give(first, parseItem(args[1]), plugin);

        // Assumption: [0]PLAYER-ONE-DROP-HAND
        case PLAYER_ONE_DROP_HAND -> dropHand(first, plugin);

        // Assumption: [0]PLAYER-ONE-DAMAGE-ARMOR [1]DAMAGE [2]ARMOR PIECE
        case PLAYER_ONE_DAMAGE_ARMOR -> damageArmorPiece(first, args[2], args[1], plugin);

        // Assumption: [0]PLAYER-ONE-DAMAGE-HAND [1]DAMAGE
        case PLAYER_ONE_DAMAGE_HAND -> damageHand(first, args[1], plugin);

        // Assumption: [0]PLAYER-ONE-REPAIR-ARMOR [1]ARMOR PIECE
        case PLAYER_ONE_REPAIR_ARMOR -> fixArmorPiece(first, args[1], plugin);

        // Assumption: [0]PLAYER-ONE-REPAIR-HAND
        case PLAYER_ONE_REPAIR_HAND -> fixHand(first, plugin);
          
        // Assumption: [0]PLAYER-ONE-TIME [1]TIME
        case PLAYER_ONE_TIME -> setPlayerTime(first, args[1], plugin);

        // Assumption: [0]PLAYER-ONE-WEATHER [1]WEATHER
        case PLAYER_ONE_WEATHER -> setPlayerWeather(first, args[1], plugin);

        // Assumption: [0]RESET-PLAYER-ONE-TIME
        case RESET_PLAYER_ONE_TIME -> resetPlayerTime(first, plugin);

        // Assumption: [0]RESET-PLAYER-ONE-WEATHER
        case RESET_PLAYER_ONE_WEATHER -> resetPlayerWeather(first, plugin);

        // Assumption: [0]PLAYER-ONE-GODMODE [1]DURATION
        case PLAYER_ONE_GODMODE -> makeInvisibleFor(first, parseI(args[1]), plugin);

        // Assumption: [0]PLAYER-ONE-BOSSBAR [1]STYLE [2]DURATION [3]TEXT
        case PLAYER_ONE_BOSSBAR -> sendBar(first, args[1], args[2], collectTail(args, 3, " ", (String[]) arrayOf("delay:", "chance:")), plugin);

        // Assumption: [0]PLAYER-ONE-TITLE [1]TITLE
        case PLAYER_ONE_TITLE -> sendTitle(first, collectTail(args, 1, " ", (String[]) arrayOf("delay:", "chance:")), plugin);

        // Assumption: [0]PLAYER-ONE-SUBTITLE [1]SUBTITLE
        case PLAYER_ONE_SUBTITLE -> sendSubtitle(first, collectTail(args, 1, " ", (String[]) arrayOf("delay:", "chance:")), plugin);

        // Assumption: [0]PLAYER-ONE-SEND-MESSAGE [1]MESSAGE
        case PLAYER_ONE_SEND_MESSAGE -> tellTo(first, collectTail(args, 1, " ", (String[]) arrayOf("delay:", "chance:")), plugin);

        // Assumption: [0]PLAYER-ONE-SEND-ACTIONBAR [1]ACTIONBAR
        case PLAYER_ONE_SEND_ACTIONBAR -> sendActionbar(first, collectTail(args, 1, " ", (String[]) arrayOf("delay:", "chance:")), plugin);

        // Assumption: [0]PLAYER-ONE-SEND-CHAT [1]CHAT
        case PLAYER_ONE_SEND_CHAT -> say(first, collectTail(args, 1, " ", (String[]) arrayOf("delay:", "chance:")), plugin);





        ////////////////////////////////////////////////////////////////////////////////////////////
        //// =================================== PLAYER TWO =================================== ////
        ////////////////////////////////////////////////////////////////////////////////////////////





        // Assumption: [0]PLAYER-TWO-PDC-SET [1]KEY [2]VALUE
        case PLAYER_TWO_PDC_SET -> setPDC(second, NamespacedKey.fromString(args[1], plugin), args[2], plugin);

        // Assumption: [0]PLAYER-TWO-VELOCITY [1]X [2]Y [3]Z
        case PLAYER_TWO_VELOCITY -> produceVelocity(second, args[1], args[2], args[3], plugin);

        // Assumption: [0]PLAYER-TWO-SOUND [1]SOUND
        case PLAYER_TWO_SOUND -> playSound(second, args[1], plugin);

        // Assumption: [0]PLAYER-TWO-EFFECT [1]EFFECT [2]DURATION [3]AMPLIFIER
        case PLAYER_TWO_EFFECT -> addPotion(second, args[1], parseI(args[2]), parseI(args[3]), plugin);

        // Assumption: [0]PLAYER-TWO-REMOVE-EFFECT [1]EFFECT
        case PLAYER_TWO_REMOVE_EFFECT -> removePotion(second, args[1], plugin);

        // Assumption: [0]PLAYER-TWO-REMOVE-BUFFS
        case PLAYER_TWO_REMOVE_BUFFS -> removeBuffs(second, plugin);

        // Assumption: [0]PLAYER-TWO-REMOVE-DEBUFFS
        case PLAYER_TWO_REMOVE_DEBUFFS -> removeDebuffs(second, plugin);

        // Assumption: [0]PLAYER-TWO-DROP-ITEM [1]ITEM [2]X [3]Y [4]Z
        case PLAYER_TWO_DROP_ITEM -> dropItem(
          new Location(second.getWorld(), parseD(args[2]), parseD(args[3]), parseD(args[4])),
          parseItem(args[1]),
          plugin
        );

        // Assumption: [0]PLAYER-TWO-SET-HAND [1]ITEM
        case PLAYER_TWO_SET_HAND -> setMainHand(second, parseItem(args[1]), plugin);

        // Assumption: [0]PLAYER-TWO-SHUFFLE-HOTBAR
        case PLAYER_TWO_SHUFFLE_HOTBAR -> shuffleHotbar(second, plugin);

        // Assumption: [0]PLAYER-TWO-SHUFFLE-INVENTORY
        case PLAYER_TWO_SHUFFLE_INVENTORY -> shuffleInventory(second, plugin);

        // Assumption: [0]PLAYER-TWO-STRIKE-FAKE-LIGHTNING
        case PLAYER_TWO_STRIKE_FAKE_LIGHTNING -> strikeFakeLightning(getLocation(second, plugin), plugin);

        // Assumption: [0]PLAYER-TWO-STRIKE-LIGHTNING
        case PLAYER_TWO_STRIKE_LIGHTNING -> strikeLightning(getLocation(second, plugin), plugin);

        // Assumption: [0]PLAYER-TWO-PARTICLE [1]PARTICLE
        case PLAYER_TWO_PARTICLE -> spawnParticle(second, getLocation(second, plugin), args[1], plugin);

        // Assumption: [0]PLAYER-TWO-PARTICLE-BOOTS [1]PARTICLE
        case PLAYER_TWO_PARTICLE_BOOTS -> spawnParticleBoots(second, getLocation(second, plugin), args[1], plugin);

        // Assumption: [0]PLAYER-TWO-TELEPORT [1]X [2]Y [3]Z
        case PLAYER_TWO_TELEPORT -> setLocation(second, parseD(args[1]), parseD(args[2]), parseD(args[3]), plugin);

        // Assumption: [0]PLAYER-TWO-DIRECTION [1]YAW [2]PITCH
        case PLAYER_TWO_DIRECTION -> setDirection(second, parseF(args[1]), parseF(args[2]), plugin);

        // Assumption: [0]PLAYER-TWO-WORLD [1]WORLD
        case PLAYER_TWO_WORLD -> setWorld(second, args[1], plugin);

        // Assumption: [0]PLAYER-TWO-FORWARD [1]DISTANCE
        case PLAYER_TWO_FORWARD -> sendForward(second, parseD(args[1]), plugin);

        // Assumption: [0]PLAYER-TWO-TELEPORT-BED
        case PLAYER_TWO_TELEPORT_BED -> teleportToBed(second, plugin);

        // Assumption: [0]PLAYER-TWO-SET-HEALTH [1]HEALTH
        case PLAYER_TWO_SET_HEALTH -> setHealth(second, parseD(args[1]), plugin);

        // Assumption: [0]PLAYER-TWO-SET-FOOD [1]FOOD
        case PLAYER_TWO_SET_FOOD -> setFood(second, parseI(args[1]), plugin);

        // Assumption: [0]PLAYER-TWO-SET-MAX-HEALTH [1]MAX-HEALTH
        case PLAYER_TWO_SET_MAX_HEALTH -> setMaximumHealth(second, parseD(args[1]), plugin);

        // Assumption: [0]PLAYER-TWO-SET-FIRE [1]DURATION-TICKS
        case PLAYER_TWO_SET_FIRE -> setFire(second, parseI(args[1]), plugin);

        // Assumption: [0]PLAYER-TWO-SET-AIR [1]AIR
        case PLAYER_TWO_SET_AIR -> setAir(second, parseI(args[1]), plugin);

        // Assumption: [0]PLAYER-TWO-SET-EXP [1]EXP
        case PLAYER_TWO_SET_EXP -> setXp(second, parseI(args[1]), plugin);

        // Assumption: [0]PLAYER-TWO-SET-LEVEL [1]LEVEL
        case PLAYER_TWO_SET_LEVEL -> setLevel(second, parseI(args[1]), plugin);

        // Assumption: [0]PLAYER-TWO-SEND-ARROW
        case PLAYER_TWO_SEND_ARROW -> sendArrow(second, plugin);

        // Assumption: [0]PLAYER-TWO-SEND-FIREBALL
        case PLAYER_TWO_SEND_FIREBALL -> sendFireball(second, plugin);

        // Assumption: [0]PLAYER-TWO-SET-MONEY [1]MONEY
        case PLAYER_TWO_SET_MONEY -> setMoney(second, parseI(args[1]), plugin);

        // Assumption: [0]PLAYER-TWO-SET-GAMEMODE [1]GAMEMODE
        case PLAYER_TWO_SET_GAMEMODE -> setGamemode(second, args[1], plugin);

        // Assumption: [0]PLAYER-TWO-GIVE-HEAD [1]PLAYER NAME
        case PLAYER_TWO_GIVE_HEAD -> giveHeadOf(second, args[1], plugin);

        // Assumption: [0]PLAYER-TWO-GIVE-ITEM [1]ITEM
        case PLAYER_TWO_GIVE_ITEM -> give(second, parseItem(args[1]), plugin);

        // Assumption: [0]PLAYER-TWO-DROP-HAND
        case PLAYER_TWO_DROP_HAND -> dropHand(second, plugin);

        // Assumption: [0]PLAYER-TWO-DAMAGE-ARMOR [1]DAMAGE [2]ARMOR PIECE
        case PLAYER_TWO_DAMAGE_ARMOR -> damageArmorPiece(second, args[2], args[1], plugin);

        // Assumption: [0]PLAYER-TWO-DAMAGE-HAND [1]DAMAGE
        case PLAYER_TWO_DAMAGE_HAND -> damageHand(second, args[1], plugin);

        // Assumption: [0]PLAYER-TWO-REPAIR-ARMOR [1]ARMOR PIECE
        case PLAYER_TWO_REPAIR_ARMOR -> fixArmorPiece(second, args[1], plugin);

        // Assumption: [0]PLAYER-TWO-REPAIR-HAND
        case PLAYER_TWO_REPAIR_HAND -> fixHand(second, plugin);

        // Assumption: [0]PLAYER-TWO-TIME [1]TIME
        case PLAYER_TWO_TIME -> setPlayerTime(second, args[1], plugin);

        // Assumption: [0]PLAYER-TWO-WEATHER [1]WEATHER
        case PLAYER_TWO_WEATHER -> setPlayerWeather(second, args[1], plugin);

        // Assumption: [0]RESET-PLAYER-TWO-TIME
        case RESET_PLAYER_TWO_TIME -> resetPlayerTime(second, plugin);

        // Assumption: [0]RESET-PLAYER-TWO-WEATHER
        case RESET_PLAYER_TWO_WEATHER -> resetPlayerWeather(second, plugin);

        // Assumption: [0]PLAYER-TWO-GODMODE [1]DURATION
        case PLAYER_TWO_GODMODE -> makeInvisibleFor(second, parseI(args[1]), plugin);

        // Assumption: [0]PLAYER-TWO-BOSSBAR [1]STYLE [2]DURATION [3]TEXT
        case PLAYER_TWO_BOSSBAR -> sendBar(second, args[1], args[2], collectTail(args, 3, " ", (String[]) arrayOf("delay:", "chance:")), plugin);

        // Assumption: [0]PLAYER-TWO-TITLE [1]TITLE
        case PLAYER_TWO_TITLE -> sendTitle(second, collectTail(args, 1, " ", (String[]) arrayOf("delay:", "chance:")), plugin);

        // Assumption: [0]PLAYER-TWO-SUBTITLE [1]SUBTITLE
        case PLAYER_TWO_SUBTITLE -> sendSubtitle(second, collectTail(args, 1, " ", (String[]) arrayOf("delay:", "chance:")), plugin);

        // Assumption: [0]PLAYER-TWO-SEND-MESSAGE [1]MESSAGE
        case PLAYER_TWO_SEND_MESSAGE -> tellTo(second, collectTail(args, 1, " ", (String[]) arrayOf("delay:", "chance:")), plugin);

        // Assumption: [0]PLAYER-TWO-SEND-ACTIONBAR [1]ACTIONBAR
        case PLAYER_TWO_SEND_ACTIONBAR -> sendActionbar(second, collectTail(args, 1, " ", (String[]) arrayOf("delay:", "chance:")), plugin);

        // Assumption: [0]PLAYER-TWO-SEND-CHAT [1]CHAT
        case PLAYER_TWO_SEND_CHAT -> say(second, collectTail(args, 1, " ", (String[]) arrayOf("delay:", "chance:")), plugin);

      }
    }, delay);

    // completeAction(runnable, args, plugin);

    // action.executeAction(getArgument(), getAction(), getPlugin());
  }
  
  public String[] parseDoublePlayerPlaceholders(String args, final Player first, final Player second, final UnderscoreEnchants plugin) {
    for (final String str : args.split("[(\\)\\+\\*\\-\\/ ]")) {
      switch (DoublePlayerActionPlaceholders.lookup(str)) {
        case null -> {}

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
              args = args.replace(str, String.valueOf(first.getWorld().getHighestBlockYAt((int) x, (int) z)));
            } catch (NumberFormatException e) {
              plugin.debugger.log("Invalid max Y at location: " + str);
            }
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
        
        case PLAYER_ONE_PDC -> {
          // player_pdc_[key]
          String[] split = str.split("_");
          if (split.length == 3) {
            String key = split[2];
            args = args.replace(str, getPDCValue(first, getKey(key, plugin), plugin).toString());
          }
        }
        
        case PLAYER_TWO_PDC -> {
          // player_pdc_[key]
          String[] split = str.split("_");
          if (split.length == 3) {
            String key = split[2];
            args = args.replace(str, getPDCValue(second, getKey(key, plugin), plugin).toString());
          }
        }

        case RANDOM_NEGATIVE_EFFECT -> args = args.replace(str, plugin.getPositiveEffects().get(new Random().nextInt(plugin.getPositiveEffects().size())).getName());
        case RANDOM_POSITIVE_EFFECT -> args = args.replace(str, plugin.getNegativeEffects().get(new Random().nextInt(plugin.getNegativeEffects().size())).getName());

        
        
        case PLAYER_ONE_NAME -> args = args.replace(str, first.getName());
        case PLAYER_ONE_UUID -> args = args.replace(str, first.getUniqueId().toString());
        case PLAYER_ONE_X -> args = args.replace(str, getXString(first, plugin));
        case PLAYER_ONE_Y -> args = args.replace(str, getYString(first, plugin));
        case PLAYER_ONE_Z -> args = args.replace(str, getZString(first, plugin));
        case PLAYER_ONE_YAW -> args = args.replace(str, getYawString(first, plugin));
        case PLAYER_ONE_PITCH -> args = args.replace(str, getPitchString(first, plugin));
        case PLAYER_ONE_MONEY -> args = args.replace(str, getMoneyString(first, plugin));
        case PLAYER_ONE_HEALTH -> args = args.replace(str, getHealthString(first, plugin));
        case PLAYER_ONE_MAX_HEALTH -> args = args.replace(str, getMaximumHealthString(first, plugin));
        case PLAYER_ONE_FOOD_LEVEL -> args = args.replace(str, getFoodString(first, plugin));
        case PLAYER_ONE_XP_LEVEL -> args = args.replace(str, getLevelString(first, plugin));
        case PLAYER_ONE_XP_PROGRESS -> args = args.replace(str, getXpString(first, plugin));
        case PLAYER_ONE_AIR_LEVEL -> args = args.replace(str, getAirString(first, plugin));
        case PLAYER_ONE_MAX_AIR_LEVEL -> args = args.replace(str, getMaximumAirString(first, plugin));
        case PLAYER_ONE_GODMODE -> args = args.replace(str, invisibleForString(first, plugin));
        case PLAYER_ONE_WORLD -> args = args.replace(str, getWorldName(first, plugin));
        case PLAYER_ONE_IP -> args = args.replace(str, getIP(first, plugin));
        case PLAYER_ONE_PING -> args = args.replace(str, getPingString(first, plugin));
        case PLAYER_ONE_GAMEMODE -> args = args.replace(str, getGameMode(first, plugin).name());
          
        case PLAYER_TWO_NAME -> args = args.replace(str, second.getName());
        case PLAYER_TWO_UUID -> args = args.replace(str, second.getUniqueId().toString());
        case PLAYER_TWO_X -> args = args.replace(str, getXString(second, plugin));
        case PLAYER_TWO_Y -> args = args.replace(str, getYString(second, plugin));
        case PLAYER_TWO_Z -> args = args.replace(str, getZString(second, plugin));
        case PLAYER_TWO_YAW -> args = args.replace(str, getYawString(second, plugin));
        case PLAYER_TWO_PITCH -> args = args.replace(str, getPitchString(second, plugin));
        case PLAYER_TWO_MONEY -> args = args.replace(str, getMoneyString(second, plugin));
        case PLAYER_TWO_HEALTH -> args = args.replace(str, getHealthString(second, plugin));
        case PLAYER_TWO_MAX_HEALTH -> args = args.replace(str, getMaximumHealthString(second, plugin));
        case PLAYER_TWO_FOOD_LEVEL -> args = args.replace(str, getFoodString(second, plugin));
        case PLAYER_TWO_XP_LEVEL -> args = args.replace(str, getLevelString(second, plugin));
        case PLAYER_TWO_XP_PROGRESS -> args = args.replace(str, getXpString(second, plugin));
        case PLAYER_TWO_AIR_LEVEL -> args = args.replace(str, getAirString(second, plugin));
        case PLAYER_TWO_MAX_AIR_LEVEL -> args = args.replace(str, getMaximumAirString(second, plugin));
        case PLAYER_TWO_GODMODE -> args = args.replace(str, invisibleForString(second, plugin));
        case PLAYER_TWO_WORLD -> args = args.replace(str, getWorldName(second, plugin));
        case PLAYER_TWO_IP -> args = args.replace(str, getIP(second, plugin));
        case PLAYER_TWO_PING -> args = args.replace(str, getPingString(second, plugin));
        case PLAYER_TWO_GAMEMODE -> args = args.replace(str, getGameMode(second, plugin).name());
      }
    }

    final String[] evaluatedArgsArray = args.split(" ");
    for (int i = 0; i < evaluatedArgsArray.length; i++) {
      final double exp = new Expression(evaluatedArgsArray[i]).calculate();
      if (!Double.isNaN(exp)) evaluatedArgsArray[i] = Double.toString(exp);
    }

    return evaluatedArgsArray;
  }

  @Override
  public String[] parsePlaceholders(final String args, final Player player, final UnderscoreEnchants plugin) {
    throw new UnsupportedOperationException("For double-player actions, parseDoublePlayerPlaceholders should be used instead!");
  }
}

package com.roughlyunderscore.enchs.parsers;

import com.codingforcookies.armorequip.ArmorEquipEvent;
import com.roughlyunderscore.enchs.UnderscoreEnchants;
import com.roughlyunderscore.enchs.enchants.EnchantmentLevel;
import com.roughlyunderscore.enchs.events.*;
import com.roughlyunderscore.enchs.parsers.condition.generic.GeneralConditionParser;
import com.roughlyunderscore.enchs.util.cooldownutils.Cooldown;
import com.roughlyunderscore.enchs.util.general.Utils;
import lombok.experimental.UtilityClass;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@UtilityClass
public class PreparatoryParsers {
  /**
   * Parses the {@link EnchantmentTarget} from string properly. Suppresses the deprecations, as using {@link EnchantmentTarget}.ALL is unavoidable.
   *
   * @param parsee the string to parse
   * @return the resulting {@link EnchantmentTarget}
   */
  @SuppressWarnings("deprecation")
  public EnchantmentTarget parseTarget(final String parsee) {
    if (parsee == null || parsee.isEmpty() || parsee.isBlank()) return EnchantmentTarget.ALL;

    return switch (parsee) {
      case "WEAPON", "WEAPONS" -> EnchantmentTarget.WEAPON;
      case "ARMOR" -> EnchantmentTarget.ARMOR;
      case "HELMET", "HEAD", "ARMOR_HELMET", "ARMOR_HEAD" -> EnchantmentTarget.ARMOR_HEAD;
      case "CHESTPLATE", "TORSO", "ARMOR_CHESTPLATE", "ARMOR_TORSO" -> EnchantmentTarget.ARMOR_TORSO;
      case "LEGGINGS", "LEGS", "ARMOR_LEGGINGS", "ARMOR_LEGS" -> EnchantmentTarget.ARMOR_LEGS;
      case "BOOTS", "FEET", "ARMOR_BOOTS", "ARMOR_FEET" -> EnchantmentTarget.ARMOR_FEET;
      case "BOW", "BOWS" -> EnchantmentTarget.BOW;
      case "TOOL", "TOOLS" -> EnchantmentTarget.TOOL;
      default -> EnchantmentTarget.ALL;
    };
  }

  /**
   * Parses the {@link Event} from string.
   *
   * @param event the string to parse
   * @return the resulting {@link Event} - more precisely, {@link Class} with a wildcard, extending {@link Event}
   */
  public Class<? extends Event> parseEvent(final String event) {
    if (event == null || event.isEmpty() || event.isBlank()) return NeverHappeningEvent.class;

    return switch (event.toUpperCase()) {
      case "PLAYERHITPLAYER", "PLAYERPVP", "PLAYERPVPEVENT", "PLAYERHITPLAYEREVENT", "PVP", "PVPEVENT" ->
        PlayerPVPEvent.class;
      case "ARMOREQUIP", "EQUIPARMOR", "ARMOREQUIPEVENT", "EQUIPARMOREVENT" -> ArmorEquipEvent.class;
      case "PLAYERBOWHIT", "PLAYERHITBOW", "PLAYERHITPLAYERBOW", "PLAYERBOWHITEVENT", "PLAYERHITBOWEVENT", "PLAYERHITPLAYERBOWEVENT" ->
        PlayerBowHitEvent.class;
      case "PLAYERBREAKBLOCK", "PLAYERBLOCKBREAKEVENT" -> BlockBreakEvent.class;
      case "PLAYERBREAKITEM", "PLAYERITEMBREAK", "PLAYERBREAKITEMEVENT", "PLAYERITEMBREAKEVENT" ->
        PlayerItemBreakEvent.class;
      case "PLAYEREAT", "EAT", "PLAYERCONSUME", "CONSUME", "PLAYEREATEVENT", "EATEVENT", "PLAYERCONSUMEEVENT", "CONSUMEEVENT" ->
        PlayerItemConsumeEvent.class;
      case "PLAYERRMBENTITY", "PLAYERRMBENTITYEVENT" -> PlayerInteractAtEntityEvent.class;
      case "PLAYERINTERACT", "INTERACT", "PLAYERINTERACTEVENT", "INTERACTEVENT" -> PlayerInteractEvent.class;
      case "PLAYERMOVE", "MOVE", "STEP", "PLAYERMOVEEVENT", "MOVEEVENT", "STEPEVENT" -> PlayerMoveEvent.class;
      case "PLAYERGOTHURT", "PLAYERDAMAGED", "PLAYERHURT", "PLAYERGOTHURTEVENT", "PLAYERDAMAGEDEVENT", "PLAYERHURTEVENT" ->
        PlayerGotHurtEvent.class;
      case "PLAYERHURTENTITY", "PLAYERDAMAGEENTITY", "PLAYERHURTENTITYEVENT", "PLAYERDAMAGEENTITYEVENT" ->
        PlayerHurtsEntityEvent.class;
      case "PLAYERSHOOTBOW", "PLAYERBOWSHOOT", "PLAYERSHOOTBOWEVENT", "PLAYERBOWSHOOTEVENT" ->
        PlayerShootBowEvent.class;
      case "PLAYERTOGGLESNEAK", "PLAYERSNEAK", "SNEAK", "TOGGLESNEAK", "PLAYERTOGGLESNEAKEVENT", "PLAYERSNEAKEVENT", "SNEAKEVENT", "TOGGLESNEAKEVENT" ->
        PlayerToggleSneakEvent.class;
      default -> NeverHappeningEvent.class;
    };
  }

  /**
   * Gets a {@link List} of {@link EnchantmentLevel} objects from a configuration section.
   *
   * @param levels the section to parse
   * @return the resulting {@link List} of {@link EnchantmentLevel}
   */
  public List<EnchantmentLevel> getLevelsOf(final ConfigurationSection levels) {
    if (levels == null) return Collections.emptyList();

    List<EnchantmentLevel> levels0 = new ArrayList<>();
    int level = 0;

    for (String path : levels.getKeys(false)) {
      level++;
      double chance = levels.getDouble(path + ".chance");
      if (chance == 0) chance = 100;

      List<String> actions = levels.getStringList(path + ".action");
      List<String> conditions = levels.getStringList(path + ".conditions");
      int cooldown = levels.getInt(path + ".cooldown");

      String flag = levels.getString(path + ".flag");
      if (flag == null) flag = "";

      levels0.add(new EnchantmentLevel(level, chance, cooldown, actions, conditions, flag));
    }

    return levels0;
  }

  /**
   * Gets the maximum level of a configuration section.
   *
   * @param levels the section to parse
   * @return the integer, indicating the maximum level
   */
  public int getMaxLevelOf(final ConfigurationSection levels) {
    if (levels == null) return -1;
    return levels.getKeys(false).size();
  }

  /**
   * Runs all the initial checks.
   *
   * @param plugin      the {@link org.bukkit.plugin.java.JavaPlugin} source, supposed to be {@link UnderscoreEnchants}
   * @param event       the {@link Event} object, necessary for many parsings
   * @param player      the {@link Player} object, who's checked for "enablability" (enabled/disabled enchantment) and for the item enchantment
   * @param level       the {@link EnchantmentLevel} object to parse the level for
   * @param key         the {@link NamespacedKey} object, necessary for the registration
   * @param conditions  the {@link List} of {@link String} objects, which are conditions to pass.
   * @param flag        the {@link Boolean} flag, which can change how they are parsed
   * @param playerField "victim", "damager" or none - for 2-player events to parse PAPI
   * @return the result of the checks - true if none fail, false otherwise
   */
  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  public boolean validateActivation(
    final UnderscoreEnchants plugin, final Event event, final Player player, final EnchantmentLevel level, final NamespacedKey key, final List<String> conditions, final String flag, final String... playerField
  ) {
    // 0) Checking if the event is cancelled.
    if (event instanceof Cancellable ca && ca.isCancelled()) return false;
    // plugin.getUnderscoreLogger().info("VALIDATOR | Event is not cancelled.");

    // 1) Checking if the player has the enchantment enabled.
    if (!Utils.isEnabled(player, key)) return false;
    // plugin.getUnderscoreLogger().info("VALIDATOR | Player has the enchantment enabled.");

    // 2) Checking if the player is subject to cooldown with this enchantment.
    final UUID uuid = player.getUniqueId();
    boolean result = true;
    if (!plugin.getCooldowns().isEmpty()) {
      for (final Cooldown cooldown : plugin.getCooldowns()) {
        // there's a cooldown with this uuid       this cooldown is with this enchantment
        if (cooldown.getUuid().equals(uuid) && cooldown.getEnchantment().getKey().equals(key)) result = false;
      }
    }
    if (!result) return false;
    // plugin.getUnderscoreLogger().info("VALIDATOR | Player is not subject to cooldown with this enchantment.");

    // 3) Checking if the randomly generated chance is applicable to the EnchantmentLevel.
    if (Math.random() * 100 > level.getChance()) return false;
    // plugin.getUnderscoreLogger().info("VALIDATOR | Random chance is applicable to the EnchantmentLevel.");

    // 4) Parsing and checking for the conditions to match.
    // plugin.getUnderscoreLogger().info("VALIDATOR | Attempting to pass the conditions...");
    return passConditions(event, conditions, flag, plugin, playerField) && passConditions(event, level.getConditions(), flag, plugin, playerField);
  }

  /**
   * @param player Either "victim", "damager" or none!
   */
  private boolean passConditions(final Event event, final List<String> conditions, final String flag, final UnderscoreEnchants plugin, final String... player) {
    boolean passed = true;
    if (conditions != null && !conditions.isEmpty()) {
      // plugin.getUnderscoreLogger().info("> CONDITIONS | Currently parsing " + conditions.size() + " conditions.");
      for (final String condition : conditions) {
        // plugin.getUnderscoreLogger().info(" > CONDITIONS | Parsing condition: " + condition + " with flag: " + flag);
        //! ---------------
        //! Flags
        if (flag.equalsIgnoreCase("need-one")) {
          passed = false;
          if (GeneralConditionParser.parseCondition(event, condition, plugin, player)) {
            // plugin.getUnderscoreLogger().info(" > CONDITIONS | Condition " + condition + " with flag " + flag + " passed.");
            passed = true;
            break;
          }
        } else {
          if (!GeneralConditionParser.parseCondition(event, condition, plugin, player)) {
            // plugin.getUnderscoreLogger().info("> CONDITIONS | Condition " + condition + " with flag " + flag + " failed.");
            passed = false;
          }
        }
      }
    }
    // plugin.getUnderscoreLogger().info(" > CONDITIONS | Result: " + passed);
    return passed;
  }


}

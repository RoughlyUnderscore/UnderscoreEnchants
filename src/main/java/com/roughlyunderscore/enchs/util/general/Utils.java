package com.roughlyunderscore.enchs.util.general;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import com.google.common.collect.ImmutableMap;
import com.roughlyunderscore.enchs.UnderscoreEnchants;
import com.roughlyunderscore.enchs.enchants.abstracts.AbstractEnchantment;
import com.roughlyunderscore.enchs.events.PlayerPVPEvent;
import com.roughlyunderscore.enchs.parsers.condition.ComparativeOperator;
import com.roughlyunderscore.enchs.util.RomanNumber;
import com.roughlyunderscore.enchs.util.DetailedEnchantment;
import com.roughlyunderscore.enchs.util.datastructures.Pair;
import com.roughlyunderscore.enchs.util.datastructures.Triple;
import com.roughlyunderscore.enchs.util.enums.Type;
import me.clip.placeholderapi.PlaceholderAPI;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.mariuszgromada.math.mxparser.Expression;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.roughlyunderscore.enchs.UnderscoreEnchants.WRONG_LEVEL;
import static com.roughlyunderscore.enchs.UnderscoreEnchants.WRONG_NAME;
import static com.roughlyunderscore.enchs.registration.Register.*;
import static com.roughlyunderscore.enchs.util.general.PlayerUtils.*;

@SuppressWarnings({"unused", "deprecation"})
public final class Utils {
  private static int current = 0;

  private Utils() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }

  /**
   * Converts an arabic number to roman.
   *
   * @param arabic the number to convert
   * @return the resulting roman number in the form of a {@link String}
   */
  public static String toRoman(final int arabic) {
    return RomanNumber.toRoman(arabic);
  }

  /**
   * Converts a roman number to arabic. Limit is 20 because I am way too lazy?
   *
   * @param roman the number to convert
   * @return the resulting arabic number
   */
  public static int toArabic(final String roman) {
    return RomanNumber.fromRoman(roman);
  }

  /**
   * Gets a user-friendly name of an {@link Enchantment}.
   *
   * @param ench the {@link Enchantment} object to get a user-friendly name of.
   * @return the name in a form of {@link String}
   */
  public static String getName(final Enchantment ench) {
    if (ench.equals(XEnchantment.ARROW_DAMAGE.getEnchant())) return "Power";
    else if (ench.equals(XEnchantment.DAMAGE_ARTHROPODS.getEnchant())) return "Bane of Arthropods";
    else if (ench.equals(XEnchantment.ARROW_KNOCKBACK.getEnchant())) return "Punch";
    else if (ench.equals(XEnchantment.DAMAGE_ALL.getEnchant())) return "Sharpness";
    else if (ench.equals(XEnchantment.THORNS.getEnchant())) return "Thorns";
    else if (ench.equals(XEnchantment.WATER_WORKER.getEnchant())) return "Aqua Affinity";
    else if (ench.equals(XEnchantment.KNOCKBACK.getEnchant())) return "Knockback";
    else if (ench.equals(XEnchantment.ARROW_INFINITE.getEnchant())) return "Infinity";
    else if (ench.equals(XEnchantment.PROTECTION_ENVIRONMENTAL.getEnchant())) return "Protection";
    else if (ench.equals(XEnchantment.ARROW_FIRE.getEnchant())) return "Flame";
    else if (ench.equals(XEnchantment.SILK_TOUCH.getEnchant())) return "Silk Touch";
    else if (ench.equals(XEnchantment.PROTECTION_PROJECTILE.getEnchant())) return "Projectile Protection";
    else if (ench.equals(XEnchantment.PROTECTION_FIRE.getEnchant())) return "Fire Protection";
    else if (ench.equals(XEnchantment.PROTECTION_EXPLOSIONS.getEnchant())) return "Blast Protection";
    else if (ench.equals(XEnchantment.PROTECTION_FALL.getEnchant())) return "Feather Falling";
    else if (ench.equals(XEnchantment.OXYGEN.getEnchant())) return "Respiration";
    else if (ench.equals(XEnchantment.LURE.getEnchant())) return "Lure";
    else if (ench.equals(XEnchantment.LUCK.getEnchant())) return "Luck of the Sea";
    else if (ench.equals(XEnchantment.FIRE_ASPECT.getEnchant())) return "Fire Aspect";
    else if (ench.equals(XEnchantment.DEPTH_STRIDER.getEnchant())) return "Depth Strider";
    else if (ench.equals(XEnchantment.DAMAGE_UNDEAD.getEnchant())) return "Smite";
    else if (ench.equals(XEnchantment.DIG_SPEED.getEnchant())) return "Efficiency";
    else if (ench.equals(XEnchantment.LOOT_BONUS_BLOCKS.getEnchant())) return "Fortune";
    else if (ench.equals(XEnchantment.LOOT_BONUS_MOBS.getEnchant())) return "Looting";
    else if (ench.equals(XEnchantment.DURABILITY.getEnchant())) return "Unbreaking";
    else if (ench.equals(XEnchantment.VANISHING_CURSE.getEnchant())) return "Curse of Vanishing";
    else if (ench.equals(XEnchantment.BINDING_CURSE.getEnchant())) return "Curse of Binding";
    else if (ench.equals(XEnchantment.SWEEPING_EDGE.getEnchant())) return "Sweeping Edge";
    else if (ench.equals(XEnchantment.MENDING.getEnchant())) return "Mending";
    else if (ench.equals(XEnchantment.SOUL_SPEED.getEnchant())) return "Soul Speed";
    else if (ench.equals(XEnchantment.FROST_WALKER.getEnchant())) return "Frost Walker";
    else if (ench.equals(XEnchantment.RIPTIDE.getEnchant())) return "Riptide";
    else if (ench.equals(XEnchantment.IMPALING.getEnchant())) return "Impaling";
    else if (ench.equals(XEnchantment.LOYALTY.getEnchant())) return "Loyalty";
    else if (ench.equals(XEnchantment.CHANNELING.getEnchant())) return "Channeling";
    else if (ench.equals(XEnchantment.SWIFT_SNEAK.getEnchant())) return "Swift Sneak";
    else return ench.getName();
  }

  /**
   * Checks if a certain {@link ItemStack} object is a glass pane.
   *
   * @param itemStack the {@link ItemStack} object to check
   * @return {@code true} if is pane, {@code false} otherwise
   */
  public static boolean isPane(final ItemStack itemStack) {
    if (itemStack == null) return false;
    return itemStack.getType().name().endsWith("PANE");
  }

  /**
   * A utility for action parsers to parse the %random% placeholders and numerical expressions.
   *
   * @param action  an action to parse in the form of {@link String}
   * @param world   the {@link World} to work with while parsing max Y
   * @param players a {@link List} object to parse the PDC placeholders
   * @param plugin  UnderscoreEnchants
   * @return an array of {@link String} objects, containing the parsed values separated with a space
   */
  public static String[] parseRegex(String action, final World world, final List<Player> players, final UnderscoreEnchants plugin) {
    final Matcher intMatcher = Pattern.compile("<random_int_([-]?[0-9]+)_([-]?[0-9]+)>").matcher(action);
    final StringBuilder intBuffer = new StringBuilder();

    while (intMatcher.find()) {
      final int min = parseI(intMatcher.group(1));
      final int max = parseI(intMatcher.group(2));
      intMatcher.appendReplacement(intBuffer, String.valueOf(ThreadLocalRandom.current().nextInt(min, max)));
    }

    intMatcher.appendTail(intBuffer);
    action = intBuffer.toString();

    final Matcher doubleMatcher = Pattern.compile("<random_double_([-]?[0-9]+[.][0-9]+)_([-]?[0-9]+[.][0-9]+)>").matcher(action);
    final StringBuilder doubleBuffer = new StringBuilder();

    while (doubleMatcher.find()) {
      final double min = parseD(doubleMatcher.group(1));
      final double max = parseD(doubleMatcher.group(2));
      doubleMatcher.appendReplacement(doubleBuffer, String.valueOf(ThreadLocalRandom.current().nextDouble(min, max)));
    }

    doubleMatcher.appendTail(doubleBuffer);
    action = doubleBuffer.toString();


    final Matcher maxYMatcher = Pattern.compile("<max_y_at_([-]?[0-9]+)_([-]?[0-9]+)>").matcher(action);
    final StringBuilder maxYBuilder = new StringBuilder();

    while (maxYMatcher.find()) {
      final double x = parseD(maxYMatcher.group(1));
      final double z = parseD(maxYMatcher.group(2));
      maxYMatcher.appendReplacement(maxYBuilder, String.valueOf(world.getHighestBlockYAt((int) x, (int) z)));
    }

    maxYMatcher.appendTail(maxYBuilder);
    action = maxYBuilder.toString();

    if (players.size() == 1) {
      final Matcher playerMatcher = Pattern.compile("<player_pdc_(.+)>").matcher(action);

      final StringBuilder playerBuilder = new StringBuilder();

      while (playerMatcher.find()) {
        final String arg = playerMatcher.group(1);
        final String val = String.valueOf(getPDCValue(players.get(0), getKey(arg, plugin), plugin));
        playerMatcher.appendReplacement(playerBuilder, val);
      }

      playerMatcher.appendTail(playerBuilder);
      action = playerBuilder.toString();
    } else {
      final Matcher victimMatcher = Pattern.compile("<victim_pdc_(.+)>").matcher(action);
      final StringBuilder victimBuilder = new StringBuilder();

      while (victimMatcher.find()) {
        final String arg = victimMatcher.group(1);
        final String val = String.valueOf(getPDCValue(players.get(0), getKey(arg, plugin), plugin));
        victimMatcher.appendReplacement(victimBuilder, val);
      }

      victimMatcher.appendTail(victimBuilder);
      action = victimBuilder.toString();

      final Matcher damagerMatcher = Pattern.compile("<damager_pdc_(.+)>").matcher(action);
      final StringBuilder damagerBuilder = new StringBuilder();

      while (damagerMatcher.find()) {
        final String arg = damagerMatcher.group(1);
        final String val = String.valueOf(getPDCValue(players.get(1), getKey(arg, plugin), plugin));
        damagerMatcher.appendReplacement(damagerBuilder, val);
      }

      damagerMatcher.appendTail(damagerBuilder);
      action = damagerBuilder.toString();
    }

    final String[] split0 = action.split(" ");
    final String[] split = new String[split0.length];


    for (int i = 0; i < split0.length; i++) {
      final String test = String.valueOf(new Expression(split0[i]).calculate());
      if (!test.equals(String.valueOf(Double.NaN))) split[i] = test;
      else split[i] = split0[i];
    }

    return split;
  }

  /**
   * A utility for parsers to quickly replace placeholders.
   *
   * @param input a {@link String} to replace the entries in
   * @param pairs an array of {@link Pair} objects, containing a replacee and a replacer.
   * @return a resulting {@link String}
   */
  @SafeVarargs
  public static String replacePlaceholders(String input, final Pair<String, String>... pairs) {
    for (final Pair<String, String> pair : pairs) {
      input = input.replace(pair.getKey(), pair.getValue());
    }

    return input;
  }

  /**
   * A utility, combining {@link #replacePlaceholders(String, Pair[])} and {@link #parseRegex(String, World, List, UnderscoreEnchants)}.
   *
   * @param input a {@link String} to parse
   * @param pairs {@link #replacePlaceholders(String, Pair[])}
   * @return the result of parsing in the form of a {@link String} array
   */
  @SafeVarargs
  public static String[] completeParse(final List<Player> players, final String input, final World world, final UnderscoreEnchants plugin, final Pair<String, String>... pairs) {
    return parseRegex(replacePlaceholders(input, pairs), world, players, plugin);
  }

  /**
   * Formats the colorcodes, see {@link ChatColor#translateAlternateColorCodes(char, String)}.
   *
   * @param arg the {@link String} to format
   * @return the formatted {@link String}
   */
  public static String format(final String arg) {
    return ChatColor.translateAlternateColorCodes('&', arg);
  }

  /**
   * Replicates {@link #format(String)} but for {@link List}s of {@link String}s.
   *
   * @param list0 the {@link List} to format
   * @return the formatted {@link List}
   */
  public static List<String> format(final List<String> list0) {
    final List<String> list = new ArrayList<>();
    for (final String s : list0) {
      list.add(format("&r" + s));
    }
    return list;
  }

  /**
   * A utility to get a {@link NamespacedKey} from {@link String}.
   *
   * @param str the {@link String} to use
   * @return the {@link NamespacedKey} result
   */
  public static NamespacedKey getKey(final String str, final UnderscoreEnchants plugin) {
    return new NamespacedKey(plugin, str);
  }

  /**
   * Quickly parses a double from a {@link String}, best used in other parsers for shortening the code.
   *
   * @param string the {@link String} to parse.
   * @return the resulting double, or {@link Double#NaN} if the {@link String} is invalid.
   * @see #parseI(String)
   * @see #parseF(String)
   * @see #parseL(String)
   * @see #parseB(String)
   */
  public static double parseD(final String string) {
    try {
      return Double.parseDouble(string);
    } catch (final Exception ex) {
      return -Double.NaN;
    }
  }

  /**
   * Quickly parses an integer from a {@link String}, best used in other parsers for shortening the code.
   *
   * @param string the {@link String} to parse.
   * @return the resulting integer, or {@link Integer#MIN_VALUE} if the {@link String} is invalid.
   * @see #parseD(String)
   * @see #parseF(String)
   * @see #parseL(String)
   * @see #parseB(String)
   */
  public static int parseI(final String string) {
    try {
      return Integer.parseInt(string);
    } catch (final Exception ex) {
      return Integer.MIN_VALUE;
    }
  }

  /**
   * Quickly parses a float from a {@link String}, best used in other parsers for shortening the code.
   *
   * @param string the {@link String} to parse.
   * @return the resulting float, or {@link Float#NaN} if the {@link String} is invalid.
   * @see #parseD(String)
   * @see #parseI(String)
   * @see #parseL(String)
   * @see #parseB(String)
   */
  public static float parseF(final String string) {
    try {
      return Float.parseFloat(string);
    } catch (final Exception ex) {
      return Float.NaN;
    }
  }

  /**
   * Quickly parses a long integer from a {@link String}, best used in other parsers for shortening the code.
   *
   * @param string the {@link String} to parse.
   * @return the resulting long integer, or {@link Long#MIN_VALUE} if the {@link String} is invalid.
   * @see #parseD(String)
   * @see #parseI(String)
   * @see #parseF(String)
   * @see #parseB(String)
   */
  public static long parseL(final String string) {
    try {
      return (long) parseD(string);
    } catch (final Exception ex) {
      return Long.MIN_VALUE;
    }
  }

  /**
   * Quickly parses a boolean from a {@link String}, best used in other parsers for shortening the code.
   *
   * @param string the {@link String} to parse.
   * @return the resulting boolean
   * @see #parseD(String)
   * @see #parseI(String)
   * @see #parseF(String)
   * @see #parseL(String)
   */
  public static boolean parseB(final String string) {
    return Boolean.parseBoolean(string);
  }

  /**
   * Sends an activation message to the {@link Player} if the message is enabled.
   *
   * @param name     the {@link Enchantment} name in a form of {@link String}
   * @param receiver the message receiver in a form of {@link Player}
   * @param plugin   an {@link UnderscoreEnchants} object, used for configuration access
   */
  public static void activationMessage(final String name, final Player receiver, final UnderscoreEnchants plugin) {
    if (plugin.getMainConfig().ACTIVATED_MESSAGE_ENABLED) {
      activationMessage(receiver, name, plugin);
    }
  }

  /**
   * Sends an activation message to the {@link Player} if the message is enabled.
   *
   * @param target the {@link EnchantmentTarget} used to seek the message target
   * @param ev     the {@link PlayerPVPEvent} object to fetch the {@link Player}s from
   * @param name   the {@link String} object, representing the name of the {@link Enchantment}
   * @param plugin an {@link UnderscoreEnchants} object, used for configuration access
   */
  public static void activationMessage(final EnchantmentTarget target, final PlayerPVPEvent ev, final String name, final UnderscoreEnchants plugin) {
    if (plugin.getMainConfig().ACTIVATED_MESSAGE_ENABLED) {
      switch (target) {
        case BOW, WEAPON, TOOL -> activationMessage(ev.getDamager(), name, plugin);
        default -> activationMessage(ev.getVictim(), name, plugin);
      }
    }
  }

  /**
   * Checks if an {@link ItemStack} is enchantable.
   *
   * @param item an {@link ItemStack} to check
   * @return {@code true} if the {@link ItemStack} is enchantable, {@code false} otherwise
   */
  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  public static boolean isEnchantable(final ItemStack item) {
    if (item == null || item.getType() == Material.AIR) return false;
    final Material material = item.getType();
    if (UnderscoreEnchants.weaponsList.contains(material)) return true;
    else if (UnderscoreEnchants.armorList.contains(material)) return true;
    else if (UnderscoreEnchants.toolsList.contains(material)) return true;
    else if (material.equals(XMaterial.BOW.parseMaterial())) return true;
    else return material.equals(XMaterial.BOOK.parseMaterial());
  }

  /**
   * Gets randomized possible {@link Enchantment}s for the current item.
   *
   * @param item                 the {@link ItemStack} to generate the {@link Enchantment}s for
   * @param possibleEnchantments the {@link Enchantment} {@link List} to fetch from
   * @param maxAmount            the amount of {@link Enchantment}s to put inside the list
   * @return the {@link ArrayList} of the generated {@link Enchantment} objects
   */
  public static ArrayList<Enchantment> getPossibleEnchantments(final ItemStack item, final List<Enchantment> possibleEnchantments, final int maxAmount) {
    final ArrayList<Enchantment> nonConflictingEnchants = new ArrayList<>();
    // AIR cannot be enchanted and doesn't have meta
    if (item.getType().equals(Material.AIR) || item.getItemMeta() == null) return nonConflictingEnchants;
    final ItemMeta meta = item.getItemMeta();
    // Add all possible enchants to our list
    possibleEnchantments
      .stream()
      .filter(candidate -> !meta.hasConflictingEnchant(candidate) && !meta.hasEnchant(candidate))
      .forEach(nonConflictingEnchants::add);
    // Shuffle our list
    Collections.shuffle(nonConflictingEnchants);
    // Create a new list
    final ArrayList<Enchantment> result = new ArrayList<>();
    for (final Enchantment enchantment : nonConflictingEnchants) {
      // Only add up to maxAmount enchantments to the list
      if (result.size() >= maxAmount) {
        break;
      }
      result.add(enchantment);
    }
    return result;
  }

  /**
   * Gets all possible {@link Enchantment}s for the current {@link ItemStack}.
   *
   * @param item the {@link ItemStack} to generate the {@link Enchantment}s for
   * @return the {@link ArrayList} of the generated {@link Enchantment} objects
   */
  public static List<Enchantment> getTypicalEnchantments(final ItemStack item) {
    final ArrayList<Enchantment> empty = new ArrayList<>();
    Type itemType;
    final Material material = item.getType();

    if (UnderscoreEnchants.weaponsList.contains(material)) itemType = Type.WEAPON;
    else if (UnderscoreEnchants.armorList.contains(material)) itemType = Type.ARMOR;
    else if (UnderscoreEnchants.toolsList.contains(material)) itemType = Type.TOOL;
    else if (material.equals(XMaterial.BOW.parseMaterial())) itemType = Type.BOW;
    else if (material.equals(XMaterial.BOOK.parseMaterial()))
      itemType = Type.values()[new Random().nextInt(Type.values().length)];
    else return empty;

    if (itemType == Type.ARMOR) {
      if (material.toString().toLowerCase().contains("helmet")) itemType = Type.ARMOR_HEAD;
      else if (material.toString().toLowerCase().contains("chestplate")) itemType = Type.ARMOR_TORSO;
      else if (material.toString().toLowerCase().contains("leggings")) itemType = Type.ARMOR_LEGGINGS;
      else if (material.toString().toLowerCase().contains("boots")) itemType = Type.ARMOR_BOOTS;
      else return empty;
    }

    return switch (itemType) {
      case BOW -> UnderscoreEnchants.bowEnchantments;
      case TOOL -> UnderscoreEnchants.toolEnchantments;
      case WEAPON -> UnderscoreEnchants.weaponEnchantments;
      case ARMOR_HEAD -> UnderscoreEnchants.helmetEnchantments;
      case ARMOR_TORSO -> UnderscoreEnchants.chestplateEnchantments;
      case ARMOR_LEGGINGS -> UnderscoreEnchants.leggingsEnchantments;
      case ARMOR_BOOTS -> UnderscoreEnchants.bootsEnchantments;
      default -> empty;
    };
  }

  /**
   * Checks if a {@link Player} has enabled an {@link Enchantment}.
   *
   * @param player the {@link Player} to check
   * @param key    the {@link Enchantment}'s {@link NamespacedKey} to look for in the PDC
   * @return {@code true} if the {@link Player} has it enabled, {@code false} otherwise
   */
  public static boolean isEnabled(final Player player, final NamespacedKey key) {
    final PersistentDataContainer pdc = player.getPersistentDataContainer();
    if (!pdc.has(key, PersistentDataType.STRING)) return true;
    return Objects.requireNonNull(pdc.get(key, PersistentDataType.STRING)).equalsIgnoreCase("on");
  }

  /**
   * Checks if an {@link Entity} is instanceof {@link Player} has enabled an {@link Enchantment}.
   *
   * @param entity the {@link Entity} to check
   * @param key    the {@link Enchantment}'s {@link NamespacedKey} to look for in the PDC
   * @return {@code true} if the {@link Entity} has it enabled, {@code false} otherwise
   */
  public static boolean isEnabled(final Entity entity, final NamespacedKey key) {
    if (!(entity instanceof Player player)) return false;
    return isEnabled(player, key);
  }

  /**
   * Sends an activation message to the {@link Player} if the message is enabled.
   *
   * @param name     the {@link Enchantment} name in a form of {@link String}
   * @param receiver the message receiver in a form of {@link Player}
   * @param plugin   an {@link UnderscoreEnchants} object, used for configuration access
   */
  public static void activationMessage(final Player receiver, final String name, final UnderscoreEnchants plugin) {
    if (plugin.getMainConfig().ACTIVATED_MESSAGE_ENABLED) {

      // build a unique namespace for the bossbar creation
      final String player = ChatColor.stripColor(receiver.getName());

      final String builder = "underscore_enchants_" + name +
        "_" +
        player +
        ThreadLocalRandom.current().nextLong(Long.MIN_VALUE, Long.MAX_VALUE)
        + System.currentTimeMillis();
      final String namespace = builder.replace("-", "_");

      // build a key
      final NamespacedKey key = new NamespacedKey(plugin, namespace.replace(" ", "_"));

      // create & send the bossbar
      final BossBar bar = Bukkit.createBossBar(
        key,
        PlaceholderAPI.setPlaceholders(receiver, format(plugin.getMessages().ACTIVATED.replace("<name>", name))),
        BarColor.PURPLE,
        BarStyle.SEGMENTED_20,
        BarFlag.DARKEN_SKY
      );
      bar.setProgress(1);
      bar.addPlayer(receiver);

      Bukkit.getScheduler().runTaskLater(plugin, () -> {
        bar.removeAll();
        Bukkit.removeBossBar(key);
      }, 60);
    }
  }

  /**
   * Sends an activation message to the {@link Player} if the message is enabled.
   *
   * @param ench     the {@link AbstractEnchantment} to send the activation message of
   * @param receiver the message receiver in a form of {@link Player}
   * @param plugin   an {@link UnderscoreEnchants} object, used for configuration access
   */
  public static void activationMessage(final Player receiver, final AbstractEnchantment ench, final UnderscoreEnchants plugin) {
    activationMessage(receiver, ench.getName(), plugin);
  }

  /**
   * Checks if the {@link EntityDamageByEntityEvent} has both the victim and the damager as {@link Player}s
   *
   * @param ev the {@link EntityDamageByEntityEvent} to check
   * @return {@code true} if both are {@link Player}s, {@code false} otherwise
   */
  public static boolean arePlayersInEvent(final EntityDamageByEntityEvent ev) {
    return ev.getDamager() instanceof Player && ev.getEntity() instanceof Player;
  }

  /**
   * Checks whether an {@link ItemStack} is enchanted or not.
   *
   * @param item the {@link ItemStack} to check
   * @param ench the {@link DetailedEnchantment} object to get the {@link Enchantment} from
   * @return {@code true} if the {@link ItemStack} is enchanted, {@code false} otherwise
   */
  public static boolean enchanted(final ItemStack item, final DetailedEnchantment ench) {
    return item.getEnchantments().containsKey(ench.getEnchantment());
  }

  /**
   * Checks whether an {@link ItemStack} is enchanted or not.
   *
   * @param player the {@link Player}, whose main hand is checked
   * @param ench   the {@link DetailedEnchantment} object to get the {@link Enchantment} from
   * @return {@code true} if the {@link ItemStack} is enchanted, {@code false} otherwise
   */
  public static boolean enchanted(final Player player, final DetailedEnchantment ench, final UnderscoreEnchants plugin) {
    return validItemInHand(player, plugin) != null && getMainHand(player, plugin).getEnchantments().containsKey(ench.getEnchantment());
  }

  /**
   * Checks whether an {@link ItemStack} is enchanted or not.
   *
   * @param pl     the {@link Player}, whose item is checked
   * @param ench   the {@link DetailedEnchantment} object to get the {@link Enchantment} from
   * @param target the {@link EnchantmentTarget} object to get the {@link Enchantment} from
   * @return {@code true} if the {@link ItemStack} is enchanted, {@code false} otherwise
   */
  public static boolean enchanted(final Player pl, final DetailedEnchantment ench, final EnchantmentTarget target, final UnderscoreEnchants plugin) {
    return switch (target) {
      case ARMOR -> Arrays.stream(pl.getInventory().getArmorContents())
        .filter(Objects::nonNull)
        .filter(it -> it.getType() != Material.AIR)
        .anyMatch(it -> it.getEnchantments().containsKey(ench.getEnchantment()));
      case ARMOR_FEET -> pl.getInventory().getBoots() != null &&
        pl.getInventory().getBoots().getType() != Material.AIR &&
        pl.getInventory().getBoots().getEnchantments().containsKey(ench.getEnchantment());
      case ARMOR_HEAD -> pl.getInventory().getHelmet() != null &&
        pl.getInventory().getHelmet().getType() != Material.AIR &&
        pl.getInventory().getHelmet().getEnchantments().containsKey(ench.getEnchantment());
      case ARMOR_LEGS -> pl.getInventory().getLeggings() != null &&
        pl.getInventory().getLeggings().getType() != Material.AIR &&
        pl.getInventory().getLeggings().getEnchantments().containsKey(ench.getEnchantment());
      case ARMOR_TORSO -> pl.getInventory().getChestplate() != null &&
        pl.getInventory().getChestplate().getType() != Material.AIR &&
        pl.getInventory().getChestplate().getEnchantments().containsKey(ench.getEnchantment());
      case BOW, WEAPON, TOOL -> enchanted(pl, ench, plugin);
      default -> false;
    };
  }

  /**
   * Checks if an {@link EntityDamageByEntityEvent} is a result of a {@link Player} shooting a {@link Player}
   *
   * @param ev the {@link EntityDamageByEntityEvent} to check
   * @return {@code true} if the {@link EntityDamageByEntityEvent} is a result of a {@link Player} shooting a {@link Player}, {@code false} otherwise
   */
  public static boolean areArrowAndPlayerInEvent(final EntityDamageByEntityEvent ev) {
    return ev.getDamager() instanceof Arrow arrow && arrow.getShooter() != null && arrow.getShooter() instanceof Player;
  }

  /**
   * Checks whether a {@link Player} has a valid {@link ItemStack} in main hand.
   *
   * @param player the {@link Player} to check
   * @return the {@link ItemStack} if is valid, null otherwise
   */
  public static ItemStack validItemInHand(final Player player, final UnderscoreEnchants plugin) {
    return getMainHand(player, plugin).getType() == Material.AIR ? null : getMainHand(player, plugin);
  }

  /**
   * A generic check to see if the {@link ItemStack} is eligible for having an {@link Enchantment} and if it has one.
   *
   * @param item      the {@link ItemStack} to check
   * @param ench      the {@link Enchantment} to look for
   * @param forbidden a {@link List} of {@link Material}s that are forbidden to have this enchantment
   * @return {@code true} if the {@link ItemStack} has the {@link Enchantment}, {@code false} otherwise
   */
  private static boolean hasEnchantment(final ItemStack item, final Enchantment ench, final List<Material> forbidden) {
    return item != null && !item.getEnchantments().isEmpty() && item.getEnchantments().get(ench) != null && !forbidden.contains(item.getType());
  }

  /**
   * Returns the level of {@link Player}'s {@link ItemStack}.
   *
   * @param player the {@link Player} whose item is checked
   * @param ench   the {@link DetailedEnchantment} entry to fetch the {@link Enchantment} from
   * @param target the {@link EnchantmentTarget} object to get the {@link Enchantment} from
   * @return the level as an integer
   */
  public static int getEnchantLevel(final Player player, final DetailedEnchantment ench, final EnchantmentTarget target, final List<String> forbidden0, final UnderscoreEnchants plugin) {
    final List<Material> forbidden = new ArrayList<>(Collections.emptyList());
    if (forbidden0 != null && !forbidden0.isEmpty()) {
      forbidden0.forEach(str -> forbidden.add(Material.valueOf(str)));
    }

    final ItemStack hand = getMainHand(player, plugin);
    final ItemStack[] armor = player.getInventory().getArmorContents();
    final Enchantment enchant = ench.getEnchantment();
    int level = 0;

    switch (target) {
      case ARMOR -> {
        for (final ItemStack item : armor) {
          if (hasEnchantment(item, enchant, forbidden)) {
            level = item.getEnchantments().get(ench.getEnchantment());
            break;
          }
        }
      }
      case ARMOR_HEAD -> {
        final ItemStack piece = player.getInventory().getHelmet();
        if (hasEnchantment(piece, enchant, forbidden))
          level = piece.getEnchantments().get(ench.getEnchantment());
      }
      case ARMOR_TORSO -> {
        final ItemStack piece = player.getInventory().getChestplate();
        if (hasEnchantment(piece, enchant, forbidden))
          level = piece.getEnchantments().get(ench.getEnchantment());
      }
      case ARMOR_LEGS -> {
        final ItemStack piece = player.getInventory().getLeggings();
        if (hasEnchantment(piece, enchant, forbidden))
          level = piece.getEnchantments().get(ench.getEnchantment());
      }
      case ARMOR_FEET -> {
        final ItemStack piece = player.getInventory().getBoots();
        if (hasEnchantment(piece, enchant, forbidden))
          level = piece.getEnchantments().get(ench.getEnchantment());
      }
      case WEAPON, BOW, CROSSBOW, TOOL, FISHING_ROD -> {
        if (hasEnchantment(hand, enchant, forbidden))
          level = hand.getEnchantments().get(ench.getEnchantment());
      }
    }

    return level;
  }

  /**
   * Returns the level of {@link Player}'s {@link ItemStack}.
   *
   * @param player     the {@link Player} whose item is checked
   * @param ench       the {@link DetailedEnchantment} entry to fetch the {@link Enchantment} from
   * @param target     the {@link EnchantmentTarget} object to get the {@link Enchantment} from
   * @param extra      an extra {@link ItemStack} to check the enchantability of
   * @param forbidden0 a list of {@link Material}s that are forbidden to have the enchantment
   * @return the level as an integer
   */
  public static int getEnchantLevel(final Player player, final DetailedEnchantment ench, final ItemStack extra, final EnchantmentTarget target, final List<String> forbidden0, final UnderscoreEnchants plugin) {
    final List<Material> forbidden = new ArrayList<>(Collections.emptyList());
    if (forbidden0 != null && !forbidden0.isEmpty()) {
      forbidden0.forEach(str -> forbidden.add(Material.valueOf(str)));
    }

    int level = getEnchantLevel(player, ench, target, forbidden0, plugin);

    if (level == 0) { // level is 0, which means that neither hand nor armor worked, checking extra items
      if (hasEnchantment(extra, ench.getEnchantment(), forbidden))
        level = extra.getEnchantments().get(ench.getEnchantment());
    }
    return level;
  }

  /**
   * Checks whether an {@link ItemStack} is {@link lombok.NonNull} and is not AIR.
   *
   * @param itemStack the {@link ItemStack} to check
   * @return {@code true} if the conditions match, {@code false} otherwise
   */
  public static boolean validItem(final ItemStack itemStack) {
    return itemStack != null && itemStack.getType() != Material.AIR;
  }

  /**
   * encahnt <br>
   * banana banana banana banana
   */
  public static Pair<ItemStack, Map<Enchantment, Integer>> enchant(final ItemStack item0, final Enchantment enchantment, final int level, final UnderscoreEnchants instance) {
    if (item0 == null || item0.getType() == Material.AIR)                                       // Deal will null and AIR
      return Pair.of(null, ImmutableMap.of(enchantment, level));                           // Return a pair of null item and same enchantments

    final ItemStack item = item0.clone();                                                       // Clone the itemstack for further work

    if (item.getEnchantments().size() >= instance.getMainConfig().MAXIMUM_ENCHANTMENTS) // Can't enchant due to the limit
      return Pair.of(item, ImmutableMap.of(enchantment, level));                              // Return a pair of same item and same enchantments


    final AtomicBoolean conflicts = new AtomicBoolean(false);                          // Prepare for conflict parser
    item.getEnchantments().forEach((ench, lvl) -> {                                             // Iterate through the existing enchantments
      if (enchantment.conflictsWith(ench) || enchantment.equals(ench))
        conflicts.set(true);   // Check if any of those conflicts with the proposed enchantment
    });

    if (conflicts.get())
      return Pair.of(item, ImmutableMap.of(enchantment, level));             // If so, return the pair of same item and same enchantments

    final ItemMeta meta = Objects.requireNonNull(item.getItemMeta());                           // Meta can't be null, but use requireNonNull to get rid of warns

    List<String> lore;                                                                          // Lore assignment
    if (meta.hasLore())
      lore = meta.getLore();                                                  // If it has lore, make it a thing
    else
      lore = new ArrayList<>();                                                              // Otherwise, make a new list

    final List<String> newLore = Collections.singletonList(generateLoreLine(enchantment, level)); // Generate the new lore
    lore = clearDuplicateLore(lore, newLore);
    meta.setLore(lore);
    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
    item.setItemMeta(meta);

    item.addUnsafeEnchantment(enchantment, level);

    return Pair.of(item, null);
  }

  /**
   * Replaces PAPI placeholders on any object if it's a player to save some code lines.
   * @param entity the potential player to use for placeholders
   * @param string the string to replace
   * @return the replaced string
   */
  public static String replacePAPI(final Object entity, final String string) {
    if (entity instanceof Player player) return PlaceholderAPI.setPlaceholders(player, string);
    return string;
  }

  /**
   * Enchant an {@link ItemStack} with multiple {@link Enchantment}s.
   *
   * @param item         an {@link ItemStack} to enchant
   * @param enchantments a {@link Map}, consisting of {@link Enchantment}s and their levels
   * @return a {@link Pair} of the (possibly) enchanted {@link ItemStack} and the leftover {@link Enchantment}s along with their levels. If the amount of
   * enchantments does not exceed the limit, the {@link Map} will be empty.
   */
  public static Pair<ItemStack, Map<Enchantment, Integer>> enchant(ItemStack item, final Map<Enchantment, Integer> enchantments, final UnderscoreEnchants instance) {
    if (item == null || item.getType() == Material.AIR)                                         // Deal will null and AIR
      return Pair.of(null, enchantments);                                                  // Return a pair of null item and same enchantments

    final Map<Enchantment, Integer> leftovers = new HashMap<>();                                // Prepare a leftovers storage

    for (final Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
      final Pair<ItemStack, Map<Enchantment, Integer>> pair = enchant(item, entry.getKey(), entry.getValue(), instance);
      if (pair.getValue() != null)
        leftovers.putAll(pair.getValue());                         // An enchantment iteration resulted in non-null (has leftovers);
        // need to put everything in the leftovers storage
      else item = pair.getKey();
    }

    return Pair.of(item, leftovers);
  }

  /**
   * Generates a lore line as if this enchantment was just added to an item.
   *
   * @param enchantment the enchantment for the lore
   * @param level       the level for the lore
   * @return the lore string
   */
  public static String generateLoreLine(final Enchantment enchantment, final int level) {
    return format(String.format("&7%s %s", getName(enchantment), toRoman(level)));
  }

  /**
   * Generates multiple lore lines for multiple enchantments.
   *
   * @param enchantments a map of enchantments and their corresponding enchantments
   * @return the generated lore line
   */
  public static List<String> generateLoreLine(final HashMap<Enchantment, Integer> enchantments) {
    final List<String> lore = new ArrayList<>();
    enchantments.forEach((ench, level) -> lore.add(generateLoreLine(ench, level)));
    return lore;
  }

  /**
   * Combines two lists of lores and removes the duplicates.
   *
   * @param existingLore the first list
   * @param newLore      the second list
   * @return the un-duplicated lore
   */
  public static List<String> clearDuplicateLore(final List<String> existingLore, final List<String> newLore) {
    if (existingLore == null || existingLore.isEmpty())
      return newLore;                             // Deal with null and empty
    if (newLore == null || newLore.isEmpty())
      return existingLore;                                  // Deal with null and empty

    for (final String existingItem0 : existingLore) {                                               // Iterating through the entire existing lore list
      for (final String newItem0 : newLore) {                                                     // Along with it, iterating through the new lore

        final String existingItem = ChatColor.stripColor(existingItem0);                        // Clearing the chatcolors
        final String newItem = ChatColor.stripColor(newItem0);                                  // Clearing the chatcolors

        if (existingItem.split(" ")[0].equalsIgnoreCase(newItem.split(" ")[0])) {   // Luck I, Luck II -> Luck.equalsIgnoreCase(Luck)
          final int existingLevel = toArabic(existingItem.split("")[1]);                // Getting the existing level
          final int newLevel = toArabic(newItem.split("")[1]);                          // Getting the new level

          if (existingLevel > newLevel)
            newLore.remove(newItem0);                             // If the existing level is higher, remove the new one
          else
            existingLore.remove(existingItem0);                                            // Otherwise, remove the existing one
        }
      }
    }

    final List<String> lore = ListUtils.union(existingLore, newLore);                               // Combine the lores
    Collections.sort(lore);                                                                         // Sort the list alphabetically
    return lore;
  }

  /**
   * Checks if the {@link Material} provided is a vegetarian food unit.
   *
   * @param food the {@link Material} to check
   * @return {@code true} if the provided {@link Material} is a vegetarian food unit, {@code false} otherwise
   */
  public static boolean isVegetarian(final Material food) {
    return food == Material.GOLDEN_CARROT ||
      food == Material.CARROT ||
      food == Material.BAKED_POTATO ||
      food == Material.MUSHROOM_STEW ||
      food == Material.SUSPICIOUS_STEW ||
      food == Material.KELP ||
      food == Material.DRIED_KELP ||
      food == Material.CHORUS_FRUIT ||
      food == Material.SWEET_BERRIES ||
      food == Material.GLOW_BERRIES ||
      food == Material.BEETROOT_SOUP ||
      food == Material.POTATO ||
      food == Material.APPLE ||
      food == Material.GOLDEN_APPLE ||
      food == Material.ENCHANTED_GOLDEN_APPLE ||
      food == Material.MELON_SLICE ||
      food == Material.PUMPKIN_PIE ||
      food == Material.PUMPKIN_STEM;
  }

  /**
   * Checks if the {@link Material} provided is a pescetarian food unit.
   *
   * @param food the {@link Material} to check
   * @return {@code true} if the provided {@link Material} is a pescetarian food unit, {@code false} otherwise
   */
  public static boolean isPescetarian(final Material food) {
    return food == Material.COOKED_COD ||
      food == Material.COOKED_SALMON ||
      food == Material.TROPICAL_FISH ||
      food == Material.COD ||
      food == Material.SALMON ||
      food == Material.PUFFERFISH;
  }

  /**
   * Checks if the {@link PlayerMoveEvent} cause was a jump.
   *
   * @param event the {@link PlayerMoveEvent} to check
   * @return {@code true} if the {@link PlayerMoveEvent} was caused by a jump, {@code false} otherwise
   * @apiNote There's a way to improve this code.
   * Listen to PlayerMoveEvent, then log the Y velocity every tick. Now jump a few times.
   * The first change in velocity will always be the same, write it down
   * Repeat it with different levels of jump potion - it should be exactly the same value every time with the same level of jump boost.
   * And now do not listen whether the NEW velocity is the value you wrote down, but the difference between the value before and the new one.
   * <br>An even better way would be to manage to listen to {@link PlayerStatisticIncrementEvent} and detect JUMP increment.
   */
  public static boolean isByJump(final PlayerMoveEvent event) {
    // Credit: https://www.spigotmc.org/threads/how-to-check-if-player-is-jumping.367036/#post-3359877
    final Player player = event.getPlayer();
    final Vector velocity = player.getVelocity();
    // Check if the player is moving "up"
    if (velocity.getY() > 0) {
      // Default jump velocity
      double jumpVelocity = 0.42D;
      final PotionEffect jumpPotion = player.getPotionEffect(PotionEffectType.JUMP);
      if (jumpPotion != null) {
        // If player has jump potion add it to jump velocity
        jumpVelocity += (double) ((float) jumpPotion.getAmplifier() + 1) * 0.1F;
      }
      // Check if player is not on ladder and if jump velocity calculated is equals to player Y velocity
      return player.getLocation().getBlock().getType() != Material.LADDER && Double.compare(velocity.getY(), jumpVelocity) == 0;
    }
    return false;
  }

  /**
   * Checks if the {@link PlayerMoveEvent} cause was a movement on the same block.
   *
   * @param event the {@link PlayerMoveEvent} to check
   * @return {@code true} if the {@link PlayerMoveEvent} was caused by a movement on the same block, {@code false} otherwise
   */
  public static boolean isBySameBlock(final PlayerMoveEvent event) {
    final Location from = event.getFrom().getBlock().getLocation();
    final Location to = event.getTo() == null ? from : event.getTo().getBlock().getLocation();

    return from.equals(to) && !isByJump(event);
  }

  /**
   * Checks if the {@link PlayerMoveEvent} cause was a movement on two blocks.
   *
   * @param event the {@link PlayerMoveEvent} to check
   * @return {@code true} if the {@link PlayerMoveEvent} was caused by a movement on two blocks, {@code false} otherwise
   */
  public static boolean isByDifferentBlocks(final PlayerMoveEvent event) {
    final Location from = event.getFrom().getBlock().getLocation();
    final Location to = event.getTo() == null ? from : event.getTo().getBlock().getLocation();

    return !from.equals(to) && !isByJump(event);
  }

  /**
   * Checks if the {@link PlayerMoveEvent} cause was a head rotation.
   *
   * @param event the {@link PlayerMoveEvent} to check
   * @return {@code true} if the {@link PlayerMoveEvent} was caused by a head rotation, {@code false} otherwise
   */
  public static boolean isByHeadRotate(final PlayerMoveEvent event) {
    final Location from = event.getFrom();
    final Location to = event.getTo() == null ? from : event.getTo();

    final Pair<Float, Float> fromRotation = Pair.of(from.getYaw(), from.getPitch());
    final Pair<Float, Float> toRotation = Pair.of(to.getYaw(), to.getPitch());

    return !fromRotation.equals(toRotation) && !isByJump(event);
  }


  /**
   * Parses an enchantment by name and makes sure that it fits the level restrictions.
   *
   * @param name         the enchantment name
   * @param level        the suggested level
   * @param unrestricted whether the level should bypass the restrictions or not (true if should)
   * @param plugin       UnderscoreEnchants
   * @return the parsed enchantment, or {@code UnderscoreEnchants.STATIC_EMPTY} (identical to EMPTY) if such enchantment doesn't exist or if the level boundaries are broken
   */
  public static DetailedEnchantment parseEnchantment(final String name, final int level, final boolean unrestricted, final UnderscoreEnchants plugin) {
    final DetailedEnchantment ench = parseEnchantment(name, plugin);
    if (ench.equals(WRONG_LEVEL) || ench.equals(WRONG_NAME)) return ench;

    if (!unrestricted && (level < ench.getEnchantment().getStartLevel() || level > ench.getEnchantment().getMaxLevel())) {
      return WRONG_LEVEL;
    } else return new DetailedEnchantment(ench.getKey());
  }

  // two debug methods
  private static String tellMeTheStaticEnchantmentData(final UnderscoreEnchants plugin) {
    final StringBuilder builder = new StringBuilder();
    for (final DetailedEnchantment en : plugin.getEnchantmentData().keySet()) {
      builder.append(en.getName()).append(" / ").append(en.getKey()).append("  |===|***|===|  ");
    }
    return builder.toString();
  }

  private static String tellMeTheDefaultEnchantmentData() {
    final StringBuilder builder = new StringBuilder();
    for (final Enchantment en : Enchantment.values()) {
      builder.append(en.getName()).append(" / ").append(en.getKey()).append("  |===|***|===|  ");
    }
    return builder.toString();
  }

  /**
   * Parses an enchantment by name.
   *
   * @param name the enchantment name
   * @return the enchantment, or {@code UnderscoreEnchants.STATIC_EMPTY} (identical to EMPTY) if such enchantment doesn't exist
   */
  public static DetailedEnchantment parseEnchantment(final String name, final UnderscoreEnchants plugin) {
    if (plugin.getEnchantmentData().keySet().stream().noneMatch(ench -> ench.getCommandName().equalsIgnoreCase(name.replace(" ", "_")))) {
      //if (Arrays.stream(Enchantment.values()).noneMatch(ench -> getName(ench).replace(" ", "_").equalsIgnoreCase(name))) {
      return WRONG_NAME; // returns the placeholder if there's no enchantment with such name
      //}
    }

    return plugin.getEnchantmentData().keySet().stream()
      .filter(e -> e.getCommandName().equalsIgnoreCase(name.replace(" ", "_")))
      .findFirst()
      .orElse(null); // Should never end up here.
  }

  /**
   * Runs an asynchronous task.
   *
   * @param plugin   a plugin instance
   * @param runnable the task to run
   */
  public static void async(final UnderscoreEnchants plugin, final Runnable runnable) {
    async(plugin, runnable, 0L);
  }

  /**
   * Runs an asynchronous task.
   *
   * @param plugin   a plugin instance
   * @param runnable the task to run
   * @param delay    delay before running the task
   */
  public static void async(final UnderscoreEnchants plugin, final Runnable runnable, final long delay) {
    Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, runnable, delay);
  }


  public static void downloadWithJavaNIO(final String fileURL, final String localFilename, final UnderscoreEnchants plugin) throws MalformedURLException {
    // github.com/eugenp/tutorials/blob/master/core-java-modules/core-java-networking-2/src/main/java/com/baeldung/download/FileDownload.java#L52

    int CONNECT_TIMEOUT = 10000;
    int READ_TIMEOUT = 10000;
    try {
      FileUtils.copyURLToFile(new URL(fileURL), new File(localFilename), CONNECT_TIMEOUT, READ_TIMEOUT);
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  /**
   * Downloads all files from the link with a per-second limit. A VERY case-specific method, as of 2.0.12 only used to simplify /ue download default. Uses a lot of
   * repeated code, because I did not want to make another boilerplate method that will be used in one method that is only used once.
   *
   * @param files    the files' addresses
   * @param dir      the directory to download to
   * @param limit    the limit
   * @param wait     the time to wait after reaching a limit (in ticks)
   * @param link     the base link
   * @param load     whether to load the file as an enchantment
   * @param notifiee somebody to notify about the enchantment progress
   * @param plugin   an UnderscoreEnchants instance
   */
  public static void downloadEnchantmentLimitedAndLoad(
    final List<String> files,
    final File dir,
    final int limit,
    final long wait,
    final String link,
    final boolean load,
    final CommandSender notifiee,
    final UnderscoreEnchants plugin
  ) {
    for (final var filename : files) {
      final String name = dir.getPath() + File.separator + filename;
      current++;

      if (current >= limit) {
        async(plugin, () -> {
          try {
            downloadWithJavaNIO(link + "/" + filename, name, plugin);

            final File enchantment = new File(name);

            String downloaded = plugin.getMessages().DOWNLOADED.replace("<ench>", enchantment.getName());
            if (notifiee instanceof Player player) downloaded = PlaceholderAPI.setPlaceholders(player, downloaded);
            notifiee.sendMessage(downloaded);

            if (load) {
              loadEnchantment(enchantment, plugin);

              String loaded = plugin.getMessages().LOADED.replace("<ench>", enchantment.getName());
              if (notifiee instanceof Player player) loaded = PlaceholderAPI.setPlaceholders(player, loaded);
              notifiee.sendMessage(loaded);
            }
          } catch (MalformedURLException e) {
            e.printStackTrace();
            plugin.getUnderscoreLogger().info("Something went wrong while downloading a file!");
            plugin.getDebugger().log("Something went wrong while downloading a file!");
          }
        }, wait);
      } else {
        async(plugin, () -> {
          try {
            downloadWithJavaNIO(link + "/" + filename, name, plugin);

            final File enchantment = new File(name);

            String downloaded = plugin.getMessages().NO_PERMS.replace("<ench>", enchantment.getName());
            if (notifiee instanceof Player player) downloaded = PlaceholderAPI.setPlaceholders(player, downloaded);
            notifiee.sendMessage(downloaded);

            if (load) {
              loadEnchantment(enchantment, plugin);

              String loaded = plugin.getMessages().NO_PERMS.replace("<ench>", enchantment.getName());
              if (notifiee instanceof Player player) loaded = PlaceholderAPI.setPlaceholders(player, loaded);
              notifiee.sendMessage(loaded);
            }
          } catch (final MalformedURLException e) {
            e.printStackTrace();
            plugin.getUnderscoreLogger().info("Something went wrong while downloading a file!");
            plugin.getDebugger().log("Something went wrong while downloading a file!");
          }
        });
      }


    }
  }

  /**
   * Checks if the given argument is equal to a given XMaterial type.
   *
   * @param argument the argument to check
   * @param check    the validating factor (XMaterial type)
   */
  public static boolean is(final Material argument, final XMaterial check) {
    return argument.equals(check.parseMaterial());
  }

  /**
   * Repairs an item by a given amount of durability.
   *
   * @param source   the item to repair
   * @param toRepair the amount of durability to repair
   * @return the repaired item
   */
  public static ItemStack repair(final ItemStack source, final int toRepair) {
    if (source.getType().getMaxDurability() == 0) return source;
    if (!(source.getItemMeta() instanceof Damageable meta)) return source;

    meta.setDamage(Math.max(0, meta.getDamage() - toRepair));
    source.setItemMeta(meta);
    return source;
  }

  /**
   * Repairs an item by a given durability percentage.
   *
   * @param source     the item to repair
   * @param percentage the percentage of maximum durability to add to the item (from 0.01 to 1.00)
   * @return the repaired item
   */
  public static ItemStack repair(final ItemStack source, double percentage) {
    percentage = clamp(0.01, 1, percentage) * 100;
    final short durability = (short) (source.getType().getMaxDurability() / 100 * percentage);
    return repair(source, durability);
  }

  /**
   * Transfers durability from one ItemStack to another.
   *
   * @param from the item to transfer durability from
   * @param to   the item to transfer durability to
   * @return the item with transferred durability
   */
  public static ItemStack transferDurability(final ItemStack from, final ItemStack to) {
    if (!(from.getItemMeta() instanceof Damageable fromMeta)) return to;
    if (!(to.getItemMeta() instanceof Damageable toMeta)) return to;

    toMeta.setDamage(fromMeta.getDamage());
    to.setItemMeta(toMeta);
    return to;
  }

  /**
   * Clamps a double value between two set points.
   *
   * @param min the lower bound
   * @param max the upper bound
   * @param val the value to clamp
   * @return the clamped value
   */
  public static double clamp(final double min, final double max, final double val) {
    return Math.max(min, Math.min(max, val));
  }

  /**
   * Generates an array of {@code EnchantmentOffer}s from given enchantments.
   *
   * @param enchs the enchantments to generate offers from
   * @return the generated offers
   */
  @SafeVarargs
  public static EnchantmentOffer[] generateOffers(final Triple<Enchantment, Integer, Integer>... enchs) {
    return generateOffersList(enchs).toArray(new EnchantmentOffer[0]);
  }

  /**
   * Generates a list of {@code EnchantmentOffer}s from given enchantments.
   *
   * @param enchs the enchantments to generate offers from
   * @return the generated offers
   */
  @SafeVarargs
  public static List<EnchantmentOffer> generateOffersList(final Triple<Enchantment, Integer, Integer>... enchs) {
    final List<EnchantmentOffer> offers = new ArrayList<>();

    for (final Triple<Enchantment, Integer, Integer> ench : enchs) {
      offers.add(new EnchantmentOffer(ench.getA(), ench.getB(), ench.getC()));
    }

    return offers;
  }

  /**
   * Properly generates an enchanted item (lore, etc)
   *
   * @param oldItem      the item to copy
   * @param enchantments the enchantments to add
   * @return the generated item
   */
  public static ItemStack generateEnchantedItemWithMergedEnchantments(final ItemStack oldItem,
                                                                      final Map<Enchantment, Integer> enchantments,
                                                                      final UnderscoreEnchants plugin) throws IllegalArgumentException {
    return generateEnchantedItemWithMergedEnchantments(oldItem, enchantments, Map.of(), plugin);
  }

  /**
   * Properly generates an enchanted item (lore, etc), where enchantments are combined from two maps
   *
   * @param oldItem       the item to copy
   * @param enchantments1 the enchantments to add
   * @param enchantments2 the enchantments to add
   * @return the generated item
   */
  public static ItemStack generateEnchantedItemWithMergedEnchantments(final ItemStack oldItem,
                                                                      final Map<Enchantment, Integer> enchantments1,
                                                                      final Map<Enchantment, Integer> enchantments2,
                                                                      final UnderscoreEnchants plugin) throws IllegalArgumentException {
    if (oldItem == null || oldItem.getType() == Material.AIR) return oldItem;

    final ItemStack item = new ItemStack(oldItem.getType());
    item.setAmount(oldItem.getAmount());

    final ConcurrentHashMap<Enchantment, Integer> enchantments = mergeEnchantments(enchantments1, enchantments2);

    item.addUnsafeEnchantments(enchantments);

    final ItemMeta meta = item.getItemMeta();
    assert meta != null; // just to get my ide to fuck off about mEtA bEiNg PoTeNtIaLlY nUlL
    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

    final List<String> lore = new ArrayList<>();
    if (!enchantments.isEmpty()) {
      for (final Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
        lore.add(format("&7" + getName(entry.getKey()) + " " + toRoman(entry.getValue())));
      }
    }

    if (oldItem.getItemMeta() != null && oldItem.getItemMeta().hasDisplayName()) {
      meta.setDisplayName(oldItem.getItemMeta().getDisplayName());
    }

    for (final Enchantment conflictable : enchantments.keySet()) {
      for (final Enchantment conflict : enchantments.keySet()) {
        if (conflict.equals(conflictable)) continue;
        if (conflict.conflictsWith(conflictable)) {
          enchantments.remove(conflict);
          enchantments.remove(conflictable);
        }
      }
    }

    if (!enchantments.isEmpty() && enchantments.size() >= plugin.getMainConfig().MAXIMUM_ENCHANTMENTS) {
      throw new IllegalArgumentException("You can't have more than " + plugin.getMainConfig().MAXIMUM_ENCHANTMENTS + " enchantments on an item!");
    }

    meta.setLore(lore);
    item.setItemMeta(meta);

    return item;
  }

  /**
   * Anvil-esque merges two enchantment maps.
   *
   * @param map1 the first map
   * @param map2 the second map
   * @return the merged map
   */
  public static ConcurrentHashMap<Enchantment, Integer> mergeEnchantments(final Map<Enchantment, Integer> map1, final Map<Enchantment, Integer> map2) {
    final ConcurrentHashMap<Enchantment, Integer> enchantments = new ConcurrentHashMap<>();
    for (final Map.Entry<Enchantment, Integer> combinedEntry : map1.entrySet()) {
      for (final Map.Entry<Enchantment, Integer> combineeEntry : map2.entrySet()) {
        // The goal of this loop is to find identical enchantments and get the max level.

        if (!combinedEntry.getKey().equals(combineeEntry.getKey()))
          continue; // Leave the enchantment intact, it's not identical

        map1.remove(combinedEntry.getKey()); // Every item should only have one of the same enchantment, this shouldn't go wrong
        map2.remove(combineeEntry.getKey());

        if (combinedEntry.getValue().equals(combineeEntry.getValue())) { // If the values are identical, increase it by 1 unless it's maximum
          enchantments.put(combinedEntry.getKey(), Math.min(combinedEntry.getValue() + 1, combinedEntry.getKey().getMaxLevel()));
          continue;
        }

        enchantments.put(combinedEntry.getKey(), Math.min(Math.max(combinedEntry.getValue(), combineeEntry.getValue()), combinedEntry.getKey().getMaxLevel()));
      }
    }

    enchantments.putAll(map1);
    enchantments.putAll(map2);

    return enchantments;
  }

  /**
   * Checks if an array of objects contains a certain object.
   *
   * @param array the array to check
   * @param key   the object to check for
   * @return whether the array contains the object
   */
  public static boolean arrayContains(final Object[] array, final Object key) {
    for (final Object o : array) {
      if (o.equals(key)) return true;
    }
    return false;
  }

  /**
   * Checks if an array of strings contains a case-insensitive string.
   *
   * @param array the array to check
   * @param key   the string to check for
   * @return whether the array contains the string
   */
  public static boolean arrayOfStringsContains(final String[] array, final String key) {
    for (final String s : array) {
      if (s.equalsIgnoreCase(key)) return true;
    }
    return false;
  }

  /**
   * Checks if an array of strings contains a case-insensitive string even partly.
   *
   * @param array the array to check
   * @param key   the string to check for
   * @return whether the array contains the string
   */
  public static boolean arrayOfStringsContainsPartly(final String[] array, final String key) {
    for (final String s : array) {
      if (s.toLowerCase().contains(key.toLowerCase())) return true;
    }
    return false;
  }


  /**
   * Checks if an argument is a comparative operator.
   *
   * @param arg the argument to check
   * @return whether the argument is a comparative operator
   */
  public static ComparativeOperator isComparativeOperator(final String arg) {
    return switch (arg) {
      case "==" -> ComparativeOperator.EQUAL;
      case "!=" -> ComparativeOperator.NOT_EQUAL;
      case ">" -> ComparativeOperator.MORE_THAN;
      case ">=" -> ComparativeOperator.MORE_THAN_OR_EQUAL;
      case "<" -> ComparativeOperator.LESS_THAN;
      case "<=" -> ComparativeOperator.LESS_THAN_OR_EQUAL;
      default -> ComparativeOperator.NONE;
    };
  }

  /**
   * Creates an array from a multitude of arguments.
   *
   * @param objects the arguments to create the array from
   * @return the array
   */
  public static Object[] arrayOf(Object... objects) {
    return objects;
  }

  /**
   * Creates a string from an array of strings with a certain separator.
   * @param array the array to create the string from
   * @param separator the separator to use
   * @return the string
   */
  public static String arrayToString(final String[] array, final String separator) {
    final StringBuilder builder = new StringBuilder();
    for (final String s : array) {
      builder.append(s).append(separator);
    }
    return builder.toString().trim();
  }
}

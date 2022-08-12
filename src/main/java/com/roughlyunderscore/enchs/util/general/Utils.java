package com.roughlyunderscore.enchs.util.general;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import com.google.common.collect.ImmutableMap;
import com.roughlyunderscore.enchs.parsers.PDCPlaceholder;
import com.roughlyunderscore.enchs.util.Pair;
import com.roughlyunderscore.enchs.UnderscoreEnchants;
import com.roughlyunderscore.enchs.enchants.abstracts.AbstractEnchantment;
import com.roughlyunderscore.enchs.events.PlayerPVPEvent;
import com.roughlyunderscore.enchs.util.data.DetailedEnchantment;
import com.roughlyunderscore.enchs.util.enums.Type;
import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.ListUtils;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.mariuszgromada.math.mxparser.Expression;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.net.URL;

import static com.roughlyunderscore.enchs.UnderscoreEnchants.STATIC_EMPTY;
import static com.roughlyunderscore.enchs.UnderscoreEnchants.staticEnchantmentData;
import static com.roughlyunderscore.enchs.registration.Register.*;
import static com.roughlyunderscore.enchs.util.general.PlayerUtils.*;

@UtilityClass @SuppressWarnings({"unused", "deprecation"})
public class Utils {

    Set<Material> panes = new HashSet<>();
    static {
        panes.add(XMaterial.BLACK_STAINED_GLASS_PANE.parseMaterial());
        panes.add(XMaterial.BLUE_STAINED_GLASS_PANE.parseMaterial());
        panes.add(XMaterial.BROWN_STAINED_GLASS_PANE.parseMaterial());
        panes.add(XMaterial.RED_STAINED_GLASS_PANE.parseMaterial());
        panes.add(XMaterial.GRAY_STAINED_GLASS_PANE.parseMaterial());
        panes.add(XMaterial.LIGHT_GRAY_STAINED_GLASS_PANE.parseMaterial());
        panes.add(XMaterial.ORANGE_STAINED_GLASS_PANE.parseMaterial());
        panes.add(XMaterial.WHITE_STAINED_GLASS_PANE.parseMaterial());
        panes.add(XMaterial.GREEN_STAINED_GLASS_PANE.parseMaterial());
        panes.add(XMaterial.LIME_STAINED_GLASS_PANE.parseMaterial());
        panes.add(XMaterial.LIGHT_BLUE_STAINED_GLASS_PANE.parseMaterial());
        panes.add(XMaterial.CYAN_STAINED_GLASS_PANE.parseMaterial());
        panes.add(XMaterial.MAGENTA_STAINED_GLASS_PANE.parseMaterial());
        panes.add(XMaterial.PURPLE_STAINED_GLASS_PANE.parseMaterial());
        panes.add(XMaterial.PINK_STAINED_GLASS_PANE.parseMaterial());
        panes.add(XMaterial.YELLOW_STAINED_GLASS_PANE.parseMaterial());
    }

    /**
     * Converts an arabic number to roman.
     * @param arabic the number to convert
     * @return the resulting roman number in the form of a {@link String}
     */
    public String toRoman(int arabic) {
        if (arabic >= 1 && arabic < 4) return "I".repeat(arabic);
        else if (arabic == 4) return "IV";
        else if (arabic >= 5 && arabic <= 8) return "V" + "I".repeat(arabic - 5);
        else if (arabic == 9) return "IX";
        else if (arabic == 10) return "X";
        else if (arabic >= 11 && arabic < 14) return "X" + "I".repeat(arabic - 10);
        else if (arabic == 14) return "XIV";
        else if (arabic >= 15 && arabic <= 18) return "XV" + "I".repeat(arabic - 15);
        else if (arabic == 19) return "XIX";
        else if (arabic == 20) return "XX";
        else return String.valueOf(arabic);
    }

    // P.S. for above and below methods
    // Shut up I am too lazy to actually figure out the way
    // to do it universally for 1-4000
    // Smarty pants? Make a PR.

    /**
     * Converts a roman number to arabic.
     * @param roman the number to convert
     * @return the resulting arabic number
     */
    public int toArabic(String roman) {
        return switch (roman) {
            case "I" -> 1;
            case "II" -> 2;
            case "III" -> 3;
            case "IV" -> 4;
            case "V" -> 5;
            case "VI" -> 6;
            case "VII" -> 7;
            case "VIII" -> 8;
            case "IX" -> 9;
            case "X" -> 10;
            case "XI" -> 11;
            case "XII" -> 12;
            case "XIII" -> 13;
            case "XIV" -> 14;
            case "XV" -> 15;
            case "XVI" -> 16;
            case "XVII" -> 17;
            case "XVIII" -> 18;
            case "XIX" -> 19;
            case "XX" -> 20;
            default -> 0;
        };
    }

    /**
     * Gets a user-friendly name of an {@link Enchantment}.
     * @param ench the {@link Enchantment} object to get a user-friendly name of.
     * @return the name in a form of {@link String}
     */
    public String getName(Enchantment ench) {
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
        else return ench.getName();
    }

    /**
     * Checks if a certain {@link ItemStack} object is a glass pane.
     * @param itemStack the {@link ItemStack} object to check
     * @throws NullPointerException if a certain entry from {@link XMaterial} wasn't found
     * @return {@code true} if is pane, {@code false} otherwise
     */
    public boolean isPane(ItemStack itemStack) {
        if (itemStack == null) return false;
        Material material = itemStack.getType();

        return panes.contains(material);
    }

    /**
     * A utility for action parsers to parse the %random% placeholders and numerical expressions.
     * @param action an action to parse in the form of {@link String}
     * @param world the {@link World} to work with while parsing max Y
     * @param players a {@link PDCPlaceholder} object to parse the PDC placeholders
     * @param plugin UnderscoreEnchants
     * @return an array of {@link String} objects, containing the parsed values separated with a space
     */
    public String[] parseRandomAndExpression(String action, World world, PDCPlaceholder players, UnderscoreEnchants plugin) {
        Matcher intMatcher = Pattern.compile("%random_int_([-]?[0-9]+)_([-]?[0-9]+)%").matcher(action);
        StringBuilder intBuffer = new StringBuilder();

        while (intMatcher.find()) {
            int min = Integer.parseInt(intMatcher.group(1));
            int max = Integer.parseInt(intMatcher.group(2));
            intMatcher.appendReplacement(intBuffer, String.valueOf(ThreadLocalRandom.current().nextInt(min, max)));
        }

        intMatcher.appendTail(intBuffer);
        action = intBuffer.toString();

        Matcher doubleMatcher = Pattern.compile("%random_double_([-]?[0-9]+[.][0-9]+)_([-]?[0-9]+[.][0-9]+)%").matcher(action);
        StringBuilder doubleBuffer = new StringBuilder();

        while (doubleMatcher.find()) {
            double min = Double.parseDouble(doubleMatcher.group(1));
            double max = Double.parseDouble(doubleMatcher.group(2));
            doubleMatcher.appendReplacement(doubleBuffer, String.valueOf(ThreadLocalRandom.current().nextDouble(min, max)));
        }

        doubleMatcher.appendTail(doubleBuffer);
        action = doubleBuffer.toString();


        Matcher maxYMatcher = Pattern.compile("%max_y_at_([-]?[0-9]+)_([-]?[0-9]+)%").matcher(action);
        StringBuilder maxYBuilder = new StringBuilder();

        while (maxYMatcher.find()) {
            double x = Double.parseDouble(maxYMatcher.group(1));
            double z = Double.parseDouble(maxYMatcher.group(2));
            maxYMatcher.appendReplacement(maxYBuilder, String.valueOf(world.getHighestBlockYAt((int)x, (int)z)));
        }

        maxYMatcher.appendTail(maxYBuilder);
        action = maxYBuilder.toString();

        String[] split0 = action.split(" ");
        String[] split = new String[split0.length];

        if (players.getSize() == 1) {
            Matcher playerMatcher = Pattern.compile("%player_pdc_(.+)%").matcher(action);

            StringBuffer playerBuilder = new StringBuffer();

            while (playerMatcher.find()) {
                String arg = playerMatcher.group(1);
                String val = String.valueOf(getPDCValue(players.getPlayers()[0], getKey(arg, plugin)));
                playerMatcher.appendReplacement(playerBuilder, val);
            }

            playerMatcher.appendTail(playerBuilder);
            action = playerBuilder.toString();
        }
        else {
            Matcher victimMatcher = Pattern.compile("%victim_pdc_(.+)%").matcher(action);
            Matcher damagerMatcher = Pattern.compile("%damager_pdc_(.+)%").matcher(action);

            StringBuilder victimBuilder = new StringBuilder();
            StringBuilder damagerBuilder = new StringBuilder();

            while (victimMatcher.find()) {
                String arg = victimMatcher.group(1);
                String val = String.valueOf(getPDCValue(players.getPlayers()[0], getKey(arg, plugin)));
                victimMatcher.appendReplacement(victimBuilder, val);
            }

            victimMatcher.appendTail(victimBuilder);
            action = victimBuilder.toString();

            while (damagerMatcher.find()) {
                String arg = damagerMatcher.group(1);
                String val = String.valueOf(getPDCValue(players.getPlayers()[1], getKey(arg, plugin)));
                damagerMatcher.appendReplacement(damagerBuilder, val);
            }

            damagerMatcher.appendTail(damagerBuilder);
            action = damagerBuilder.toString();
        }


        for (int i = 0; i < split0.length; i++) {
            String test = String.valueOf(new Expression(split0[i]).calculate());
            if (!test.equals(String.valueOf(Double.NaN))) split[i] = test;
            else split[i] = split0[i];
        }

        return split;
    }

    /**
     * A utility for parsers to quickly replace placeholders.
     * @param input a {@link String} to replace the entries in
     * @param pairs an array of {@link Pair} objects, containing a replacee and a replacer.
     * @return a resulting {@link String}
     */
    @SafeVarargs
    public String replacePlaceholders(String input, Pair<String, String>... pairs) {
        for (Pair<String, String> pair : pairs) {
            input = input.replace(pair.getKey(), pair.getValue());
        }

        return input;
    }

    /**
     * A utility, combining {@link #replacePlaceholders(String, Pair[])} and {@link #parseRandomAndExpression(String, World, PDCPlaceholder, UnderscoreEnchants)}.
     * @param input a {@link String} to parse
     * @param pairs {@link #replacePlaceholders(String, Pair[])}
     * @return the result of parsing in the form of a {@link String} array
     */
    @SafeVarargs
    public String[] completeParse(PDCPlaceholder players, String input, World world, UnderscoreEnchants plugin, Pair<String, String>... pairs) {
        return parseRandomAndExpression(replacePlaceholders(input, pairs), world, players, plugin);
    }

    /**
     * Formats the colorcodes, see {@link ChatColor#translateAlternateColorCodes(char, String)}.
     * @param arg the {@link String} to format
     * @return the formatted {@link String}
     */
    public String format(String arg) {
        return ChatColor.translateAlternateColorCodes('&', arg);
    }

    /**
     * Replicates {@link #format(String)} but for {@link List}s of {@link String}s.
     * @param list0 the {@link List} to format
     * @return the formatted {@link List}
     */
    public List<String> format(List<String> list0) {
        List<String> list = new ArrayList<>();
        for (String s : list0) {
            list.add(format("&r" + s));
        }
        return list;
    }

    /**
     * A utility to get a {@link NamespacedKey} from {@link String}.
     * @param str the {@link String} to use
     * @return the {@link NamespacedKey} result
     */
    public NamespacedKey getKey(String str, UnderscoreEnchants plugin) {
        return new NamespacedKey(plugin, str);
    }

    /**
     * Quickly parses a double from a {@link String}, best used in other parsers for shortening the code.
     * @param string the {@link String} to parse.
     * @return the resulting double, or {@link Double#NaN} if the {@link String} is invalid.
     * @see #parseI(String)
     * @see #parseF(String)
     * @see #parseL(String)
     * @see #parseB(String)
     */
    public double parseD(String string) {
        try {
            return Double.parseDouble(string);
        } catch (Exception ex) {
            return -Double.NaN;
        }
    }

    /**
     * Quickly parses an integer from a {@link String}, best used in other parsers for shortening the code.
     * @param string the {@link String} to parse.
     * @return the resulting integer, or {@link Integer#MIN_VALUE} if the {@link String} is invalid.
     * @see #parseD(String)
     * @see #parseF(String)
     * @see #parseL(String)
     * @see #parseB(String)
     */
    public int parseI(String string) {
        try {
            return (int) Math.floor(Double.parseDouble(string));
        } catch (Exception ex) {
            return Integer.MIN_VALUE;
        }
    }

    /**
     * Quickly parses a float from a {@link String}, best used in other parsers for shortening the code.
     * @param string the {@link String} to parse.
     * @return the resulting float, or {@link Float#NaN} if the {@link String} is invalid.
     * @see #parseD(String)
     * @see #parseI(String)
     * @see #parseL(String)
     * @see #parseB(String)
     */
    public float parseF(String string) {
        try {
            return Float.parseFloat(string);
        } catch (Exception ex) {
            return Float.NaN;
        }
    }

    /**
     * Quickly parses a long integer from a {@link String}, best used in other parsers for shortening the code.
     * @param string the {@link String} to parse.
     * @return the resulting long integer, or {@link Long#MIN_VALUE} if the {@link String} is invalid.
     * @see #parseD(String)
     * @see #parseI(String)
     * @see #parseF(String)
     * @see #parseB(String)
     */
    public long parseL(String string) {
        try {
            return (long) Double.parseDouble(string);
        } catch (Exception ex) {
            return Long.MIN_VALUE;
        }
    }

    /**
     * Quickly parses a boolean from a {@link String}, best used in other parsers for shortening the code.
     * @param string the {@link String} to parse.
     * @return the resulting boolean
     * @see #parseD(String)
     * @see #parseI(String)
     * @see #parseF(String)
     * @see #parseL(String)
     */
    public boolean parseB(String string) {
        return Boolean.parseBoolean(string);
    }

    /**
     * Sends an activation message to the {@link Player} if the message is enabled.
     * @param name the {@link Enchantment} name in a form of {@link String}
     * @param receiver the message receiver in a form of {@link Player}
     * @param plugin an {@link UnderscoreEnchants} object, used for configuration access
     */
    public void activationMessage(String name, Player receiver, UnderscoreEnchants plugin) {
        if (plugin.getConfig().getBoolean("activated")) {
            activationMessage(receiver, name, plugin);
        }
    }

    /**
     * Sends an activation message to the {@link Player} if the message is enabled.
     * @param target the {@link EnchantmentTarget} used to seek the message target
     * @param ev the {@link PlayerPVPEvent} object to fetch the {@link Player}s from
     * @param name the {@link String} object, representing the name of the {@link Enchantment}
     * @param plugin an {@link UnderscoreEnchants} object, used for configuration access
     */
    public void activationMessage(EnchantmentTarget target, PlayerPVPEvent ev, String name, UnderscoreEnchants plugin) {
        if (plugin.getConfig().getBoolean("activated")) {
            switch (target) {
                case BOW, WEAPON, TOOL -> activationMessage(ev.getDamager(), name, plugin);
                default -> activationMessage(ev.getVictim(), name, plugin);
            }
        }
    }

    /**
     * Checks if an {@link ItemStack} is enchantable.
     * @param item an {@link ItemStack} to check
     * @return {@code true} if the {@link ItemStack} is enchantable, {@code false} otherwise
     */
    public boolean isEnchantable(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;
        Material material = item.getType();
        if (UnderscoreEnchants.weaponsList.contains(material)) return true;
        else if (UnderscoreEnchants.armorList.contains(material)) return true;
        else if (UnderscoreEnchants.toolsList.contains(material)) return true;
        else if (material.equals(XMaterial.BOW.parseMaterial())) return true;
        else return material.equals(XMaterial.BOOK.parseMaterial());
    }

    /**
     * Gets randomized possible {@link Enchantment}s for the current item.
     * @param item the {@link ItemStack} to generate the {@link Enchantment}s for
     * @param possibleEnchantments the {@link Enchantment} {@link List} to fetch from
     * @param maxAmount the amount of {@link Enchantment}s to put inside the list
     * @return the {@link ArrayList} of the generated {@link Enchantment} objects
     */
    public ArrayList<Enchantment> getPossibleEnchantments(ItemStack item, List<Enchantment> possibleEnchantments, int maxAmount) {
        ArrayList<Enchantment> nonConflictingEnchants = new ArrayList<>();
        // AIR cannot be enchanted and doesn't have meta
        if(item.getType().equals(Material.AIR)) return nonConflictingEnchants;
        ItemMeta meta = item.getItemMeta();
        // Add all possible enchants to our list
        possibleEnchantments
            .stream()
            .filter(candidate -> !meta.hasConflictingEnchant(candidate) && !meta.hasEnchant(candidate))
            .forEach(nonConflictingEnchants::add);
        // Shuffle our list
        Collections.shuffle(nonConflictingEnchants);
        // Create a new list
        ArrayList<Enchantment> result = new ArrayList<>();
        for(Enchantment enchantment : nonConflictingEnchants) {
            // Only add up to maxAmount enchantments to the list
            if(result.size() >= maxAmount) {
                break;
            } result.add(enchantment);
        }
        return result;
    }

    /**
     * Gets all possible {@link Enchantment}s for the current {@link ItemStack}.
     * @param item the {@link ItemStack} to generate the {@link Enchantment}s for
     * @return the {@link ArrayList} of the generated {@link Enchantment} objects
     */
    public ArrayList<Enchantment> getTypicalEnchantments(ItemStack item) {
        ArrayList<Enchantment> empty = new ArrayList<>();
        Type itemType;
        Material material = item.getType();

        if (UnderscoreEnchants.weaponsList.contains(material)) itemType = Type.WEAPON;
        else if (UnderscoreEnchants.armorList.contains(material)) itemType = Type.ARMOR;
        else if (UnderscoreEnchants.toolsList.contains(material)) itemType = Type.TOOL;
        else if (material.equals(XMaterial.BOW.parseMaterial())) itemType = Type.BOW;
        else if (material.equals(XMaterial.BOOK.parseMaterial())) itemType = Type.values()[new Random().nextInt(Type.values().length)];
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
     * @param player the {@link Player} to check
     * @param key the {@link Enchantment}'s {@link NamespacedKey} to look for in the PDC
     * @return {@code true} if the {@link Player} has it enabled, {@code false} otherwise
     */
    public boolean isEnabled(Player player, NamespacedKey key) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        if (!pdc.has(key, PersistentDataType.STRING)) return true;
        return pdc.get(key, PersistentDataType.STRING).equalsIgnoreCase("on");
    }

    /**
     * Checks if an {@link Entity} is instanceof {@link Player} has enabled an {@link Enchantment}.
     * @param entity the {@link Entity} to check
     * @param key the {@link Enchantment}'s {@link NamespacedKey} to look for in the PDC
     * @return {@code true} if the {@link Entity} has it enabled, {@code false} otherwise
     */
    public boolean isEnabled(Entity entity, NamespacedKey key) {
        if (!(entity instanceof Player player)) return false;
        return isEnabled(player, key);
    }

    /**
     * Sends an activation message to the {@link Player} if the message is enabled.
     * @param name the {@link Enchantment} name in a form of {@link String}
     * @param receiver the message receiver in a form of {@link Player}
     * @param plugin an {@link UnderscoreEnchants} object, used for configuration access
     */
    public void activationMessage(Player receiver, String name, UnderscoreEnchants plugin) {
        if (plugin.getConfig().getBoolean("activated")) {

            // build a unique namespace for the bossbar creation
            String player = ChatColor.stripColor(receiver.getName());

            String builder = "underscore_enchants_" + name +
                "_" +
                player +
                ThreadLocalRandom.current().nextLong(-9223372036854775808L, 9223372036854775807L)
                + System.currentTimeMillis();
            String namespace = builder.replace("-", "_");

            // build a key
            NamespacedKey key = new NamespacedKey(plugin, namespace.replace(" ", "_"));

            // create & send the bossbar
            BossBar bar = Bukkit.createBossBar(key, format(plugin.getMessages().ACTIVATED.replace("%name%", name)), BarColor.PURPLE, BarStyle.SEGMENTED_20, BarFlag.DARKEN_SKY);
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
     * @param ench the {@link AbstractEnchantment} to send the activation message of
     * @param receiver the message receiver in a form of {@link Player}
     * @param plugin an {@link UnderscoreEnchants} object, used for configuration access
     */
    public void activationMessage(Player receiver, AbstractEnchantment ench, UnderscoreEnchants plugin) {
        activationMessage(receiver, ench.getName(), plugin);
    }

    /**
     * Checks if the {@link EntityDamageByEntityEvent} has both the victim and the damager as {@link Player}s
     * @param ev the {@link EntityDamageByEntityEvent} to check
     * @return {@code true} if both are {@link Player}s, {@code false} otherwise
     */
    public boolean arePlayersInEvent(EntityDamageByEntityEvent ev) {
        return ev.getDamager() instanceof Player && ev.getEntity() instanceof Player;
    }

    /**
     * Checks whether an {@link ItemStack} is enchanted or not.
     * @param item the {@link ItemStack} to check
     * @param ench the {@link DetailedEnchantment} object to get the {@link Enchantment} from
     * @return {@code true} if the {@link ItemStack} is enchanted, {@code false} otherwise
     */
    public boolean enchanted(ItemStack item, DetailedEnchantment ench) {
        return item.getEnchantments().containsKey(ench.getEnchantment());
    }

    /**
     * Checks whether an {@link ItemStack} is enchanted or not.
     * @param player the {@link Player}, whose main hand is checked
     * @param ench the {@link DetailedEnchantment} object to get the {@link Enchantment} from
     * @return {@code true} if the {@link ItemStack} is enchanted, {@code false} otherwise
     */
    public boolean enchanted(Player player, DetailedEnchantment ench) {
        return validItemInHand(player) != null && getMainHand(player).getEnchantments().containsKey(ench.getEnchantment());
    }

    /**
     * Checks whether an {@link ItemStack} is enchanted or not.
     * @param pl the {@link Player}, whose item is checked
     * @param ench the {@link DetailedEnchantment} object to get the {@link Enchantment} from
     * @param target the {@link EnchantmentTarget} object to get the {@link Enchantment} from
     * @return {@code true} if the {@link ItemStack} is enchanted, {@code false} otherwise
     */
    public boolean enchanted(Player pl, DetailedEnchantment ench, EnchantmentTarget target) {
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
            case BOW, WEAPON, TOOL -> enchanted(pl, ench);
            default -> false;
        };
    }

    /**
     * Checks if an {@link EntityDamageByEntityEvent} is a result of a {@link Player} shooting a {@link Player}
     * @param ev the {@link EntityDamageByEntityEvent} to check
     * @return {@code true} if the {@link EntityDamageByEntityEvent} is a result of a {@link Player} shooting a {@link Player}, {@code false} otherwise
     */
    public boolean areArrowAndPlayerInEvent(EntityDamageByEntityEvent ev) {
        return ev.getDamager() instanceof Arrow arrow && arrow.getShooter() != null && arrow.getShooter() instanceof Player;
    }

    /**
     * Checks whether a {@link Player} has a valid {@link ItemStack} in main hand.
     * @param player the {@link Player} to check
     * @return the {@link ItemStack} if is valid, null otherwise
     */
    public ItemStack validItemInHand(Player player) {
        return getMainHand(player).getType() == Material.AIR ? null : getMainHand(player);
    }

    /**
     * A generic check to see if the {@link ItemStack} is eligible for having an {@link Enchantment} and if it has one.
     * @param item the {@link ItemStack} to check
     * @param ench the {@link Enchantment} to look for
     * @param forbidden a {@link List} of {@link Material}s that are forbidden to have this enchantment
     * @return {@code true} if the {@link ItemStack} has the {@link Enchantment}, {@code false} otherwise
     */
    private boolean hasEnchantment(ItemStack item, Enchantment ench, List<Material> forbidden) {
        return item != null && !item.getEnchantments().isEmpty() && item.getEnchantments().get(ench) != null && !forbidden.contains(item.getType());
    }

    /**
     * Returns the level of {@link Player}'s {@link ItemStack}.
     * @param player the {@link Player} whose item is checked
     * @param ench the {@link DetailedEnchantment} entry to fetch the {@link Enchantment} from
     * @param target the {@link EnchantmentTarget} object to get the {@link Enchantment} from
     * @return the level as an integer
     */
    public int getEnchantLevel(Player player, DetailedEnchantment ench, EnchantmentTarget target, List<String> forbidden0) {
        List<Material> forbidden = new ArrayList<>(Collections.emptyList());
        if (forbidden0 != null && !forbidden0.isEmpty()) {
            forbidden0.forEach(str -> forbidden.add(Material.valueOf(str)));
        }

        ItemStack hand = getMainHand(player);
        ItemStack[] armor = player.getInventory().getArmorContents();
        Enchantment enchant = ench.getEnchantment();
        int level = 0;

        switch (target) {
            case ARMOR -> {
                for (ItemStack item : armor) {
                    if (hasEnchantment(item, enchant, forbidden)) {
                        level = item.getEnchantments().get(ench.getEnchantment());
                        break;
                    }
                }
            }
            case ARMOR_HEAD -> {
                ItemStack piece = player.getInventory().getHelmet();
                if (hasEnchantment(piece, enchant, forbidden))
                    level = piece.getEnchantments().get(ench.getEnchantment());
            }
            case ARMOR_TORSO -> {
                ItemStack piece = player.getInventory().getChestplate();
                if (hasEnchantment(piece, enchant, forbidden))
                    level = piece.getEnchantments().get(ench.getEnchantment());
            }
            case ARMOR_LEGS -> {
                ItemStack piece = player.getInventory().getLeggings();
                if (hasEnchantment(piece, enchant, forbidden))
                    level = piece.getEnchantments().get(ench.getEnchantment());
            }
            case ARMOR_FEET -> {
                ItemStack piece = player.getInventory().getBoots();
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
     * @param player the {@link Player} whose item is checked
     * @param ench the {@link DetailedEnchantment} entry to fetch the {@link Enchantment} from
     * @param target the {@link EnchantmentTarget} object to get the {@link Enchantment} from
     * @param extra an extra {@link ItemStack} to check the enchantability of
     * @param forbidden0 a list of {@link Material}s that are forbidden to have the enchantment
     * @return the level as an integer
     */
    public int getEnchantLevel(Player player, DetailedEnchantment ench, ItemStack extra, EnchantmentTarget target, List<String> forbidden0) {
        List<Material> forbidden = new ArrayList<>(Collections.emptyList());
        if (forbidden0 != null && !forbidden0.isEmpty()) {
            forbidden0.forEach(str -> forbidden.add(Material.valueOf(str)));
        }

        int level = getEnchantLevel(player, ench, target, forbidden0);

        if (level == 0) { // level is 0, which means that neither hand nor armor worked, checking extra items
            if (hasEnchantment(extra, ench.getEnchantment(), forbidden))
                level = extra.getEnchantments().get(ench.getEnchantment());
        }
        return level;
    }

    /**
     * Checks whether an {@link ItemStack} is {@link lombok.NonNull} and is not AIR.
     * @param itemStack the {@link ItemStack} to check
     * @return {@code true} if the conditions match, {@code false} otherwise
     */
    public boolean validItem(ItemStack itemStack) {
        return itemStack != null && itemStack.getType() != Material.AIR;
    }

    /**
     * encahnt <br>
     * banana banana banana banana
     */
    public Pair<ItemStack, Map<Enchantment, Integer>> enchant(ItemStack item0, Enchantment enchantment, int level) {
        if (item0 == null || item0.getType() == Material.AIR)                                       // Deal will null and AIR
            return Pair.of(null, ImmutableMap.of(enchantment, level));                           // Return a pair of null item and same enchantments

        ItemStack item = item0.clone();                                                             // Clone the itemstack for further work

        if (item.getEnchantments().size() >= UnderscoreEnchants.staticConfig.getInt("enchantmentLimit")) // Can't enchant due to the limit
            return Pair.of(item, ImmutableMap.of(enchantment, level));                              // Return a pair of same item and same enchantments


        AtomicBoolean conflicts = new AtomicBoolean(false);                                // Prepare for conflict parser
        item.getEnchantments().forEach((ench, lvl) -> {                                             // Iterate through the existing enchantments
            if (enchantment.conflictsWith(ench) || enchantment.equals(ench)) conflicts.set(true);   // Check if any of those conflicts with the proposed enchantment
        });

        if (conflicts.get()) return Pair.of(item, ImmutableMap.of(enchantment, level));             // If so, return the pair of same item and same enchantments

        ItemMeta meta = Objects.requireNonNull(item.getItemMeta());                                 // Meta can't be null, but use requireNonNull to get rid of warns

        List<String> lore;                                                                          // Lore assignment
        if (meta.hasLore()) lore = meta.getLore();                                                  // If it has lore, make it a thing
        else lore = new ArrayList<>();                                                              // Otherwise, make a new list

        List<String> newLore = Collections.singletonList(generateLoreLine(enchantment, level));     // Generate the new lore
        lore = clearDuplicateLore(lore, newLore);
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);

        item.addUnsafeEnchantment(enchantment, level);

        return Pair.of(item, null);
    }

    /**
     * Enchant an {@link ItemStack} with multiple {@link Enchantment}s.
     * @param item an {@link ItemStack} to enchant
     * @param enchantments a {@link Map}, consisting of {@link Enchantment}s and their levels
     * @return a {@link Pair} of the (possibly) enchanted {@link ItemStack} and the leftover {@link Enchantment}s along with their levels. If the amount of
     * enchantments does not exceed the limit, the {@link Map} will be empty.
     */
    public Pair<ItemStack, Map<Enchantment, Integer>> enchant(ItemStack item, Map<Enchantment, Integer> enchantments) {
        if (item == null || item.getType() == Material.AIR)                                         // Deal will null and AIR
            return Pair.of(null, enchantments);                                                  // Return a pair of null item and same enchantments

        Map<Enchantment, Integer> leftovers = new HashMap<>();                                      // Prepare a leftovers storage

        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            Pair<ItemStack, Map<Enchantment, Integer>> pair = enchant(item, entry.getKey(), entry.getValue());
            if (pair.getValue() != null) leftovers.putAll(pair.getValue());                         // An enchantment iteration resulted in non-null (has leftovers);
                                                                                                    // need to put everything in the leftovers storage
            else item = pair.getKey();
        }

        return Pair.of(item, leftovers);
    }

    /**
     * Generates a lore line as if this enchantment was just added to an item.
     * @param enchantment the enchantment for the lore
     * @param level the level for the lore
     * @return the lore string
     */
    public String generateLoreLine(Enchantment enchantment, int level) {
        return format(String.format("&7%s %s", getName(enchantment), toRoman(level)));
    }

    /**
     * Generates multiple lore lines for multiple enchantments.
     * @param enchantments a map of enchantments and their corresponding enchantments
     * @return the generated lore line
     */
    public List<String> generateLoreLine(HashMap<Enchantment, Integer> enchantments) {
        List<String> lore = new ArrayList<>();
        enchantments.forEach((ench, level) -> lore.add(generateLoreLine(ench, level)));
        return lore;
    }

    /**
     * Combines two lists of lores and removes the duplicates.
     * @param existingLore the first list
     * @param newLore the second list
     * @return the un-duplicated lore
     */
    public List<String> clearDuplicateLore(List<String> existingLore, List<String> newLore) {
        if (existingLore == null || existingLore.isEmpty()) return newLore;                             // Deal with null and empty
        if (newLore == null || newLore.isEmpty()) return existingLore;                                  // Deal with null and empty

        for (String existingItem0 : existingLore) {                                                     // Iterating through the entire existing lore list
            for (String newItem0 : newLore) {                                                           // Along with it, iterating through the new lore

                String existingItem = ChatColor.stripColor(existingItem0);                              // Clearing the chatcolors
                String newItem = ChatColor.stripColor(newItem0);                                        // Clearing the chatcolors

                if (existingItem.split(" ")[0].equalsIgnoreCase(newItem.split(" ")[0])) {   // Luck I, Luck II -> Luck.equalsIgnoreCase(Luck)
                    int existingLevel = toArabic(existingItem.split("")[1]);                      // Getting the existing level
                    int newLevel = toArabic(newItem.split("")[1]);                                // Getting the new level

                    if (existingLevel > newLevel) newLore.remove(newItem0);                             // If the existing level is higher, remove the new one
                    else existingLore.remove(existingItem0);                                            // Otherwise, remove the existing one
                }
            }
        }

        List<String> lore = ListUtils.union(existingLore, newLore);                                     // Combine the lores
        Collections.sort(lore);                                                                         // Sort the list alphabetically
        return lore;
    }

    /**
     * Checks if the {@link Material} provided is a vegetarian food unit.
     * @param food the {@link Material} to check
     * @return {@code true} if the provided {@link Material} is a vegetarian food unit, {@code false} otherwise
     */
    public boolean isVegetarian(Material food) {
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
     * @param food the {@link Material} to check
     * @return {@code true} if the provided {@link Material} is a pescetarian food unit, {@code false} otherwise
     */
    public boolean isPescetarian(Material food) {
        return food == Material.COOKED_COD ||
            food == Material.COOKED_SALMON ||
            food == Material.TROPICAL_FISH ||
            food == Material.COD ||
            food == Material.SALMON ||
            food == Material.PUFFERFISH;
    }

    /**
     * Checks if the {@link PlayerMoveEvent} cause was a jump.
     * @param event the {@link PlayerMoveEvent} to check
     * @return {@code true} if the {@link PlayerMoveEvent} was caused by a jump, {@code false} otherwise
     *
     * @apiNote There's a way to improve this code.
     * Listen to PlayerMoveEvent, then log the Y velocity every tick. Now jump a few times.
     * The first change in velocity will always be the same, write it down
     * Repeat it with different levels of jump potion - it should be exactly the same value every time with the same level of jump boost.
     * And now do not listen whether the NEW velocity is the value you wrote down, but the difference between the value before and the new one.
     * <br>An even better way would be to manage to listen to {@link PlayerStatisticIncrementEvent} and detect JUMP increment.
     */
    public boolean isByJump(PlayerMoveEvent event) {
        // Credit: https://www.spigotmc.org/threads/how-to-check-if-player-is-jumping.367036/#post-3359877
        Player player = event.getPlayer();
        Vector velocity = player.getVelocity();
        // Check if the player is moving "up"
        if (velocity.getY() > 0) {
            // Default jump velocity
            double jumpVelocity = 0.42D;
            PotionEffect jumpPotion = player.getPotionEffect(PotionEffectType.JUMP);
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
     * @param event the {@link PlayerMoveEvent} to check
     * @return {@code true} if the {@link PlayerMoveEvent} was caused by a movement on the same block, {@code false} otherwise
     */
    public boolean isBySameBlock(PlayerMoveEvent event) {
        Location from = event.getFrom().getBlock().getLocation();
        Location to = event.getTo() == null ? from : event.getTo().getBlock().getLocation();

        return from.equals(to) && !isByJump(event);
    }

    /**
     * Checks if the {@link PlayerMoveEvent} cause was a movement on two blocks.
     * @param event the {@link PlayerMoveEvent} to check
     * @return {@code true} if the {@link PlayerMoveEvent} was caused by a movement on two blocks, {@code false} otherwise
     */
    public boolean isByDifferentBlocks(PlayerMoveEvent event) {
        Location from = event.getFrom().getBlock().getLocation();
        Location to = event.getTo() == null ? from : event.getTo().getBlock().getLocation();

        return !from.equals(to) && !isByJump(event);
    }

    /**
     * Checks if the {@link PlayerMoveEvent} cause was a head rotation.
     * @param event the {@link PlayerMoveEvent} to check
     * @return {@code true} if the {@link PlayerMoveEvent} was caused by a head rotation, {@code false} otherwise
     */
    public boolean isByHeadRotate(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo() == null ? from : event.getTo();

        Pair<Float, Float> fromRotation = Pair.of(from.getYaw(), from.getPitch());
        Pair<Float, Float> toRotation = Pair.of(to.getYaw(), to.getPitch());

        return !fromRotation.equals(toRotation) && !isByJump(event);
    }


    /**
     * Parses an enchantment by name and makes sure that it fits the level restrictions.
     * @param name the enchantment name
     * @param level the suggested level
     * @param unrestricted whether the level should bypass the restrictions or not (true if should)
     * @return the parsed enchantment, or {@code UnderscoreEnchants.STATIC_EMPTY} (identical to EMPTY) if such enchantment doesn't exist or if the level boundaries are broken
     */
    public DetailedEnchantment parseEnchantment(String name, int level, boolean unrestricted) {
        DetailedEnchantment ench = parseEnchantment(name);

        if (!unrestricted && (level < ench.getEnchantment().getStartLevel() || level > ench.getEnchantment().getMaxLevel())) {
            return STATIC_EMPTY;
        }

        else return new DetailedEnchantment(ench.getKey());
    }

    // two debug methods, used before and maybe will be used later
    private String tellMeTheStaticEnchantmentData() {
        StringBuilder builder = new StringBuilder();
        for (DetailedEnchantment en : staticEnchantmentData) {
            builder.append(en.getName()).append(" / ").append(en.getKey()).append("\n");
        }
        return builder.toString();
    }

    private String tellMeTheDefaultEnchantmentData() {
        StringBuilder builder = new StringBuilder();
        for (Enchantment en : Enchantment.values()) {
            builder.append(en.getName()).append(" / ").append(en.getKey()).append("\n");
        }
        return builder.toString();
    }

    /**
     * Parses an enchantment by name.
     * @param name the enchantment name
     * @return the enchantment, or {@code UnderscoreEnchants.STATIC_EMPTY} (identical to EMPTY) if such enchantment doesn't exist
     */
    public DetailedEnchantment parseEnchantment(final String name) {

        if (UnderscoreEnchants.staticEnchantmentData.stream().noneMatch(ench -> ench.getCommandName().equalsIgnoreCase(name))) {
            if (Arrays.stream(Enchantment.values()).noneMatch(ench -> getName(ench).replace(" ", "_").equalsIgnoreCase(name))) {
                return STATIC_EMPTY; // returns the placeholder if there's no enchantment with such name
            }
        }

        Enchantment ench;

        Optional<DetailedEnchantment> optional = UnderscoreEnchants.staticEnchantmentData.stream().filter(enchn -> enchn.getCommandName().equalsIgnoreCase(name)).findFirst();
        if (optional.isPresent()) {
            ench = optional.get().getEnchantment(); // sets the enchantment to the received one (this is a custom enchantment)
        } else {
            Optional<Enchantment> opt = Arrays.stream(Enchantment.values()).filter(enchn -> getName(enchn).replace(" ", "_").equalsIgnoreCase(name)).findFirst();
            if (opt.isPresent()) {
                ench = opt.get(); // sets the enchantment to the received one (this is a default enchantment)
            }
            else return STATIC_EMPTY; // returns the placeholder if such enchantment somehow does not exist
        }

        return new DetailedEnchantment(ench.getKey()); // returns a DetailedEnchantment object, built from the received enchantment

    }

    /**
     * Runs an asynchronous task.
     * @param plugin a plugin instance
     * @param runnable the task to run
     */
    public void async(final UnderscoreEnchants plugin, final Runnable runnable) {
        async(plugin, runnable, 0L);
    }

    /**
     * Runs an asynchronous task.
     * @param plugin a plugin instance
     * @param runnable the task to run
     * @param delay delay before running the task
     */
    public void async(final UnderscoreEnchants plugin, final Runnable runnable, final long delay) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, runnable, delay);
    }


    public void downloadWithJavaNIO(final String fileURL, final String localFilename, final UnderscoreEnchants plugin) throws MalformedURLException {
        // github.com/eugenp/tutorials/blob/master/core-java-modules/core-java-networking-2/src/main/java/com/baeldung/download/FileDownload.java

        final URL url = new URL(fileURL);
        try (
            final ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
            final FileOutputStream fileOutputStream = new FileOutputStream(localFilename);
            final FileChannel fileChannel = fileOutputStream.getChannel()
        ) {
            fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
            fileOutputStream.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Downloads all files from the link with a per-second limit. A VERY case-specific method, as of 2.0.12 only used to simplify /ue download default
     * @param files the files' addresses
     * @param dir the directory to download to
     * @param limit the limit
     * @param wait the time to wait after reaching a limit (in ticks)
     * @param link the base link
     * @param load whether to load the file as an enchantment
     * @param notifiee somebody to notify about the enchantment progress
     * @param plugin an UnderscoreEnchants instance
     */
    public void downloadEnchantmentLimitedAndLoad(
        final List<String> files,
        final File dir,
        final int limit,
        final long wait,
        final String link,
        final boolean load,
        final CommandSender notifiee,
        final UnderscoreEnchants plugin
    )
    {
        for (final var filename : files) {
            final String name = dir.getPath() + File.separator + filename;
            current++;

            if (current >= limit) {
                async(plugin, () -> {
                    try {
                        downloadWithJavaNIO(link + "/" + filename, name, plugin);

                        final File enchantment = new File(name);
                        notifiee.sendMessage(plugin.getMessages().DOWNLOADED.replace("%ench%", enchantment.getName()));

                        if (load) {
                            loadEnchantment(enchantment, plugin);
                            notifiee.sendMessage(plugin.getMessages().LOADED.replace("%ench%", enchantment.getName()));
                        }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                        Bukkit.getLogger().info("Something went wrong while downloading a file!");
                        plugin.getDebugger().log("Something went wrong while downloading a file!");
                    }
                }, wait);
            }
            else {
                async(plugin, () -> {
                    try {
                        downloadWithJavaNIO(link + "/" + filename, name, plugin);

                        final File enchantment = new File(name);
                        notifiee.sendMessage(plugin.getMessages().DOWNLOADED.replace("%ench%", enchantment.getName()));

                        if (load) {
                            loadEnchantment(enchantment, plugin);
                            notifiee.sendMessage(plugin.getMessages().LOADED.replace("%ench%", enchantment.getName()));
                        }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                        Bukkit.getLogger().info("Something went wrong while downloading a file!");
                        plugin.getDebugger().log("Something went wrong while downloading a file!");
                    }
                });
            }


        }
    }

    private int current = 0;
}

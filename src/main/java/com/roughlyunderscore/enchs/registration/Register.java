package com.roughlyunderscore.enchs.registration;

import com.codingforcookies.armorequip.ArmorEquipEvent;
import com.roughlyunderscore.enchs.UnderscoreEnchants;
import com.roughlyunderscore.enchs.util.cooldownutils.Cooldown;
import com.roughlyunderscore.enchs.enchants.EnchantmentLevel;
import com.roughlyunderscore.enchs.enchants.abstracts.*;
import com.roughlyunderscore.enchs.events.*;
import com.roughlyunderscore.enchs.util.datastructures.Pair;
import com.roughlyunderscore.enchs.util.data.DetailedEnchantment;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.roughlyunderscore.enchs.parsers.action.ActionParser.*;
import static com.roughlyunderscore.enchs.parsers.PreparatoryParsers.*;
import static com.roughlyunderscore.enchs.util.general.Utils.*;

@UtilityClass
/*
Not really a parser, but a help class for registering the enchantments.
 */
public class Register {
    /**
     * Adds an enchantment to a corresponding list.
     * @param ench the enchantment
     * @param plugin UnderscoreEnchants
     */
    public void listEnchantment(Enchantment ench, UnderscoreEnchants plugin) {
        switch (ench.getItemTarget()) {
            case BOW, CROSSBOW -> UnderscoreEnchants.bowEnchantments.add(ench);
            case TOOL, FISHING_ROD -> UnderscoreEnchants.toolEnchantments.add(ench);
            case WEAPON -> UnderscoreEnchants.weaponEnchantments.add(ench);
            case ARMOR_FEET -> UnderscoreEnchants.bootsEnchantments.add(ench);
            case ARMOR_HEAD -> UnderscoreEnchants.helmetEnchantments.add(ench);
            case ARMOR_LEGS -> UnderscoreEnchants.leggingsEnchantments.add(ench);
            case ARMOR_TORSO -> UnderscoreEnchants.chestplateEnchantments.add(ench);
            case TRIDENT -> UnderscoreEnchants.tridentEnchantments.add(ench);
            case ALL -> {
                UnderscoreEnchants.bowEnchantments.add(ench);
                UnderscoreEnchants.toolEnchantments.add(ench);
                UnderscoreEnchants.bootsEnchantments.add(ench);
                UnderscoreEnchants.helmetEnchantments.add(ench);
                UnderscoreEnchants.leggingsEnchantments.add(ench);
                UnderscoreEnchants.chestplateEnchantments.add(ench);
                UnderscoreEnchants.weaponEnchantments.add(ench);
            }
            case ARMOR -> {
                UnderscoreEnchants.helmetEnchantments.add(ench);
                UnderscoreEnchants.chestplateEnchantments.add(ench);
                UnderscoreEnchants.leggingsEnchantments.add(ench);
                UnderscoreEnchants.bootsEnchantments.add(ench);
            }
            default -> {
                plugin.getUnderscoreLogger().severe(format("&cEnchantment " + ench.getName() + " could not process the enchantment target."));
                plugin.debugger.log("&cEnchantment " + ench.getName() + " could not process the enchantment target.");
            }
        }
    }

    /**
     * Does some behind-the-scenes job on the enchantment, such as registering the events, making it an actual enchantment, listing it.
     * @param ench the enchantment
     * @param keyHolder a DetailedEnchantment object for that very enchantment
     * @param plugin UnderscoreEnchants
     */
    public void wrapEnchantment(Enchantment ench, DetailedEnchantment keyHolder, UnderscoreEnchants plugin) {

        if (!(ench instanceof Listener l)) return;
        Bukkit.getServer().getPluginManager().registerEvents(l, plugin);

        try {
            Field field = Enchantment.class.getDeclaredField("acceptingNew");
            field.setAccessible(true);
            field.set(null, true);
            Enchantment.registerEnchantment(ench);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            plugin.getUnderscoreLogger().severe("Enchantment " + keyHolder.getName() + " didn't get registered. Restart the server; if the problem won't be fixed, report it to the support Discord.");
            plugin.debugger.log("Enchantment " + keyHolder.getName() + " didn't get registered. Restart the server; if the problem won't be fixed, report it to the support Discord.");
            e.printStackTrace();
            return;
        }

        listEnchantment(keyHolder.getEnchantment(), plugin);

        plugin.getAllEnchs().add(keyHolder.getEnchantment());
        plugin.enchantmentData.put(keyHolder, new AbstractEnchantment(ench));
    }

    /**
     * Loads an enchantment from a file.
     * @param file the file to load
     * @param plugin UnderscoreEnchants
     */
    @SneakyThrows
    public void loadEnchantment(File file, UnderscoreEnchants plugin) {
        plugin.debugger.log("Registering enchantment: " + file.getAbsolutePath());
        plugin.debugger.log("Enchantment file name: " + file.getName());
        plugin.debugger.log("Enchantment path: " + file.getPath());
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        Pair<DetailedEnchantment, AbstractEnchantment> enchant = parseEnchantment(configuration, plugin);
        if (enchant == null) return;
        AbstractEnchantment enchantment = enchant.getValue();
        wrapEnchantment(enchantment, enchant.getKey(), plugin);
        UnderscoreEnchants.staticEnchantmentData.put(enchant.getKey(), enchantment);
    }

    /**
     * Unloads an enchantment from a file.
     * @param file the file to load
     * @param plugin UnderscoreEnchants
     */
    @SneakyThrows @SuppressWarnings({"unchecked", "unused"})
    public void unloadEnchantment(File file, UnderscoreEnchants plugin) {
        plugin.debugger.log("Attempting to unload an enchantment: " + file.getAbsolutePath());
        plugin.debugger.log("Enchantment file name: " + file.getName());
        plugin.debugger.log("Enchantment path: " + file.getPath());
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        // Pair<DetailedEnchantment, AbstractEnchantment> enchant = plugin.parseEnchantment(configuration);
        DetailedEnchantment enchantment = findEnchantment(file, plugin);
        String name = enchantment.getName();
        NamespacedKey key = enchantment.getKey();

        Field keyField = Enchantment.class.getDeclaredField("byKey");
        keyField.setAccessible(true);

        HashMap<NamespacedKey, Enchantment> byKey = (HashMap<NamespacedKey, Enchantment>) keyField.get(null);
        byKey.remove(key);

        Field nameField = Enchantment.class.getDeclaredField("byName");
        nameField.setAccessible(true);

        HashMap<String, Enchantment> byName = (HashMap<String, Enchantment>) nameField.get(null);
        byName.remove(name);

        Listener listener = UnderscoreEnchants.staticEnchantmentData.get(enchantment);
        HandlerList.unregisterAll(listener);
        UnderscoreEnchants.staticEnchantmentData.remove(enchantment);

        plugin.getEnchantmentData().remove(enchantment);
    }

    /**
     * Finds an enchantment from a file.
     * @param file the file to look at
     * @param plugin UnderscoreEnchants
     */
    public DetailedEnchantment findEnchantment(File file, UnderscoreEnchants plugin) {
        // Load file as a yamlconfiguration
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        // Get the enchantment name
        String name = configuration.getString("name");

        final NamespacedKey key = new NamespacedKey(plugin, "underscore_enchants_" + name.replace(" ", "__"));

        return new DetailedEnchantment(key.getKey(), plugin);
    }

    /**
     * Checks if an enchantment is loaded.
     * @param file the enchantment to check
     * @param plugin UnderscoreEnchants
     */
    public boolean isEnchantmentLoaded(File file, UnderscoreEnchants plugin) {
        return isEnchantmentLoaded(findEnchantment(file, plugin), plugin);
    }

    /**
     * Checks if an enchantment is loaded.
     * @param enchantment the enchantment to check
     * @param plugin UnderscoreEnchants
     */
    public boolean isEnchantmentLoaded(DetailedEnchantment enchantment, UnderscoreEnchants plugin) {
        return plugin.getEnchantmentData().containsKey(enchantment);
    }

    /**
     * Works in parseEnchantment and is placed here for making the code more readable
     * @param event The event itself, used here to check if is cancelled
     * @param player The event's player, used for various checks
     * @param entry The DetailedEnchantment entry, used to check for the item enchantment status
     * @param target The EnchantmentTarget, used to select the item (ref. above)
     * @param forbidOn A list of Materials that are forbidden to have this enchantment
     * @param levels A list of all EnchantmentLevels accessible for this enchantment
     * @param key The NamespacedKey of this enchantment, used for checking the toggle status
     * @param conditions A list of conditions that must be met before activating the enchantments
     * @param flag A potential condition flag that changes the condition parse
     * @param name The enchantment name
     * @param cooldown An overall enchantment cooldown
     * @param plugin UnderscoreEnchants
     * <br>Also see {@link Register#parseEnchantment(YamlConfiguration, UnderscoreEnchants)}
     */
    public void commonAction(
        Event event,
        Player player,
        DetailedEnchantment entry,
        EnchantmentTarget target,
        List<String> forbidOn,
        List<EnchantmentLevel> levels,
        NamespacedKey key,
        List<String> conditions,
        String flag,
        String name,
        int cooldown,
        UnderscoreEnchants plugin
    ) {
        int lvl = getEnchantLevel(player, entry, target, forbidOn, plugin);
        if (lvl == 0) return;

        // plugin.getUnderscoreLogger().info("Found enchantment: " + name + " with level: " + lvl + "by player: " + player.getName());

        if (flag == null) flag = "";

        // plugin.getUnderscoreLogger().info("Current flag: " + flag);

        EnchantmentLevel level = levels.get(lvl - 1);

        // plugin.getUnderscoreLogger().info("Attempting to validate the activation...");
        // Pass all the checks
        if (!validateActivation(plugin, event, player, level, key, conditions, flag)) return;

        // plugin.getUnderscoreLogger().info("Activation validated!");

        // Parse every action
        for (String lev : level.getAction()) {
            try {
                parseAction(event, lev, plugin);
            } catch (Exception e) {
                plugin.debugger.log(Arrays.toString(e.getStackTrace()));
                plugin.getUnderscoreLogger().info("Please check your actions in " + name + "! They are wrongly configured.");
                return;
            }
        }

        // Send the activation message
        activationMessage(name, player, plugin);

        // If the cooldown provided by the configuration is not 0, create that cooldown
        if (cooldown != 0) plugin.getCooldowns().add(new Cooldown(cooldown, entry.getEnchantment(), player.getUniqueId()));
        // If the enchantment level has a cooldown (it's not 0), create that cooldown
        if (level.getCooldown() != 0) plugin.getCooldowns().add(new Cooldown(level.getCooldown(), entry.getEnchantment(), player.getUniqueId()));
    }

    /**
     * Works in parseEnchantment and is placed here for making the code more readable
     * @param event The event itself, used here to check if is cancelled
     * @param player The event's player,used for various checks
     * @param entry The DetailedEnchantment entry, used to check for the item enchantment status
     * @param extra An extra item to check for the enchantment status
     * @param target The EnchantmentTarget, used to select the item (ref. above)
     * @param forbidOn A list of Materials that are forbidden to have this enchantment
     * @param levels A list of all EnchantmentLevels accessible for this enchantment
     * @param key The NamespacedKey of this enchantment, used for checking the toggle status
     * @param conditions A list of conditions that must be met before activating the enchantments
     * @param flag A potential condition flag that changes the condition parse
     * @param name The enchantment name
     * @param cooldown An overall enchantment cooldown
     * @param plugin UnderscoreEnchants
     * <br>Also see {@link Register#parseEnchantment(YamlConfiguration, UnderscoreEnchants)}
     */
    public void extraItemAction(
        Event event,
        Player player,
        DetailedEnchantment entry,
        ItemStack extra,
        EnchantmentTarget target,
        List<String> forbidOn,
        List<EnchantmentLevel> levels,
        NamespacedKey key,
        List<String> conditions,
        String flag,
        String name,
        int cooldown,
        UnderscoreEnchants plugin
    ) {
        int lvl = getEnchantLevel(player, entry, extra, target, forbidOn, plugin);
        if (lvl == 0) return;

        if (flag == null) flag = "";

        EnchantmentLevel level = levels.get(lvl - 1);

        // Pass all the checks
        if (!validateActivation(plugin, event, player, level, key, conditions, flag)) return;

        // Parse every action
        for (String lev : level.getAction()) {
            try {
                parseAction(event, lev, plugin);
            } catch (Exception e) {
                plugin.debugger.log(Arrays.toString(e.getStackTrace()));
                plugin.getUnderscoreLogger().info("Please check your actions in " + name + "! They are wrongly configured.");
                return;
            }
        }

        // Send the activation message
        activationMessage(name, player, plugin);

        // If the cooldown provided by the configuration is not 0, create that cooldown
        if (cooldown != 0) plugin.getCooldowns().add(new Cooldown(cooldown, entry.getEnchantment(), player.getUniqueId()));
        // If the enchantment level has a cooldown (it's not 0), create that cooldown
        if (level.getCooldown() != 0) plugin.getCooldowns().add(new Cooldown(level.getCooldown(), entry.getEnchantment(), player.getUniqueId()));
    }

    /**
     * Works in parseEnchantment and is placed here for making the code more readable
     * @param event The event itself, used here to check if is cancelled
     * @param damager The event's damager, used for various checks
     * @param victim The event's victim, used for various checks
     * @param entry The DetailedEnchantment entry, used to check for the item enchantment status
     * @param target The EnchantmentTarget, used to select the item (ref. above)
     * @param forbidOn A list of Materials that are forbidden to have this enchantment
     * @param levels A list of all EnchantmentLevels accessible for this enchantment
     * @param key The NamespacedKey of this enchantment, used for checking the toggle status
     * @param conditions A list of conditions that must be met before activating the enchantments
     * @param flag A potential condition flag that changes the condition parse
     * @param isEmpty A boolean, indicating if the "player" value exists in the configuration header
     * @param isForDamager A boolean, indicating if the "player" value is equal to DAMAGER
     * @param isForVictim A boolean, indicating if the "player" value is equal to VICTIM
     * @param name The enchantment name
     * @param cooldown An overall enchantment cooldown
     * @param plugin UnderscoreEnchants
     * <br>Also see {@link Register#parseEnchantment(YamlConfiguration, UnderscoreEnchants)}
     */
    public void twoPlayerDamageAction(
        Event event,
        Player damager,
        Player victim,
        DetailedEnchantment entry,
        EnchantmentTarget target,
        List<String> forbidOn,
        List<EnchantmentLevel> levels,
        NamespacedKey key,
        List<String> conditions,
        String flag,
        boolean isEmpty,
        boolean isForDamager,
        boolean isForVictim,
        String name,
        int cooldown,
        UnderscoreEnchants plugin
    ) {
        // Create the EnchantmentLevel
        int damagerLvl = getEnchantLevel(damager, entry, target, forbidOn, plugin), victimLvl = getEnchantLevel(victim, entry, target, forbidOn, plugin);
        boolean damagerActivated = true, victimActivated = true;

        if (flag == null) flag = "";


        if (damagerLvl == 0) damagerActivated = false;
        if (victimLvl == 0) victimActivated = false;

        EnchantmentLevel damagerEnchLevel = EnchantmentLevel.empty(), victimEnchLevel = EnchantmentLevel.empty();

        if (damagerActivated) {
            damagerEnchLevel = levels.get(damagerLvl - 1);
            if (!validateActivation(plugin, event, damager, damagerEnchLevel, key, conditions, flag)) damagerActivated = false;
        }
        if (victimActivated) {
            victimEnchLevel = levels.get(victimLvl - 1);
            if (!validateActivation(plugin, event, victim, victimEnchLevel, key, conditions, flag)) victimActivated = false;
        }
        if (!isEmpty) {
            if (!isForDamager) damagerActivated = false;
            if (!isForVictim) victimActivated = false;
        }

        if (damagerActivated) {
            // Parse every action
            for (String lev : damagerEnchLevel.getAction()) {
                try {
                    parseAction(event, lev, plugin);
                } catch (Exception e) {
                    plugin.debugger.log(Arrays.toString(e.getStackTrace()));
                    plugin.getUnderscoreLogger().info("Please check your actions in " + name + "! They are wrongly configured.");
                    return;
                }
            }

            // Send the activation message
            activationMessage(name, damager, plugin);

            // If the cooldown provided by the configuration is not 0, create that cooldown
            if (cooldown != 0) plugin.getCooldowns().add(new Cooldown(cooldown, entry.getEnchantment(), damager.getUniqueId()));
            // If the enchantment level has a cooldown (it's not 0), create that cooldown
            if (damagerEnchLevel.getCooldown() != 0) plugin.getCooldowns().add(new Cooldown(damagerEnchLevel.getCooldown(), entry.getEnchantment(), damager.getUniqueId()));
        }

        if (victimActivated) {
            // Parse every action
            for (String lev : victimEnchLevel.getAction()) {
                try {
                    parseAction(event, lev, plugin);
                } catch (Exception e) {
                    plugin.debugger.log(Arrays.toString(e.getStackTrace()));
                    plugin.getUnderscoreLogger().info("Please check your actions in " + name + "! They are wrongly configured.");
                    return;
                }
            }

            // Send the activation message
            activationMessage(name, victim, plugin);

            // If the cooldown provided by the configuration is not 0, create that cooldown
            if (cooldown != 0) plugin.getCooldowns().add(new Cooldown(cooldown, entry.getEnchantment(), victim.getUniqueId()));
            // If the enchantment level has a cooldown (it's not 0), create that cooldown
            if (victimEnchLevel.getCooldown() != 0) plugin.getCooldowns().add(new Cooldown(victimEnchLevel.getCooldown(), entry.getEnchantment(), victim.getUniqueId()));
        }
    }

    public Pair<DetailedEnchantment, AbstractEnchantment> parseEnchantment(YamlConfiguration file, UnderscoreEnchants instance) {

        //<editor-fold desc="Preparatory">
        final @NonNull String enchantmentName = format(file.getString("name"));
        final @NonNull EnchantmentTarget target = parseTarget(file.getString("applicable"));
        final @NonNull Class<? extends Event> eventString = parseEvent(file.getString("trigger"));
        final List<String> conditions = file.getStringList("conditions");
        final List<String> forbidOn = file.getStringList("forbid-on");
        final @NonNull List<EnchantmentLevel> levelsList = getLevelsOf(file.getConfigurationSection("levels"));
        final String damagerOrVictim = file.getString("player");
        final @Nullable String conditionFlag = file.getString("condition-flag");
        boolean forDamager = false, forVictim = false, valueEmpty = true;
        if (damagerOrVictim != null) {
            valueEmpty = false;
            if (damagerOrVictim.equalsIgnoreCase("damager")) forDamager = true;
            else if (damagerOrVictim.equalsIgnoreCase("victim")) forVictim = true;
        }

        final int maximumLevel = getMaxLevelOf(file.getConfigurationSection("levels"));
        final int cooldownApplied = file.getInt("cooldown");

        final NamespacedKey key = new NamespacedKey(instance, "underscore_enchants_" + enchantmentName.replace(" ", "__"));
        final DetailedEnchantment entry = new DetailedEnchantment(key.getKey(), instance);
        AbstractEnchantment ench;
        //</editor-fold>

        //<editor-fold desc="PlayerPVPEvent">
        if (eventString.getName().equals(PlayerPVPEvent.class.getName())) {
            boolean finalForDamager = forDamager, finalForVictim = forVictim, finalValueEmpty = valueEmpty;

            ench = new PVPEnchantment(key, enchantmentName, maximumLevel, target) {
                @Override
                public void onPVP(PlayerPVPEvent event) {
                    twoPlayerDamageAction(event,
                        event.getDamager(),
                        event.getVictim(),
                        entry,
                        target,
                        forbidOn,
                        levelsList,
                        key,
                        conditions,
                        conditionFlag,
                        finalValueEmpty,
                        finalForDamager,
                        finalForVictim,
                        enchantmentName,
                        cooldownApplied,
                        instance
                    );
                }
            };

        }
        //</editor-fold>
        //<editor-fold desc="ArmorEquipEvent">
        else if (eventString.getName().equals(ArmorEquipEvent.class.getName())) {
            ench = new ArmorEquipEnchantment(key, enchantmentName, maximumLevel, target) {
                @Override
                public void onEquip(ArmorEquipEvent event) {
                    extraItemAction(event,
                        event.getPlayer(),
                        entry,
                        event.getNewArmorPiece(),
                        target,
                        forbidOn,
                        levelsList,
                        key,
                        conditions,
                        conditionFlag,
                        enchantmentName,
                        cooldownApplied,
                        instance);
                }
            };

        }
        //</editor-fold>
        //<editor-fold desc="BlockBreakEvent">
        else if (eventString.getName().equals(BlockBreakEvent.class.getName())) {
            ench = new BlockBreakEnchantment(key, enchantmentName, maximumLevel, target) {
                @Override
                public void onBreak(BlockBreakEvent event) {
                    commonAction(event,
                        event.getPlayer(),
                        entry,
                        target,
                        forbidOn,
                        levelsList,
                        key,
                        conditions,
                        conditionFlag,
                        enchantmentName,
                        cooldownApplied,
                        instance);
                }
            };

        }
        //</editor-fold>
        //<editor-fold desc="PlayerItemBreakEvent">
        else if (eventString.getName().equals(PlayerItemBreakEvent.class.getName())) {
            ench = new ItemBreakEnchantment(key, enchantmentName, maximumLevel, target) {
                @Override
                public void onBreak(PlayerItemBreakEvent event) {
                    // Create the EnchantmentLevel
                    commonAction(event,
                        event.getPlayer(),
                        entry,
                        target,
                        forbidOn,
                        levelsList,
                        key,
                        conditions,
                        conditionFlag,
                        enchantmentName,
                        cooldownApplied,
                        instance);
                }
            };

        }
        //</editor-fold>
        //<editor-fold desc="PlayerItemConsumeEvent">
        else if (eventString.getName().equals(PlayerItemConsumeEvent.class.getName())) {
            ench = new ItemEatEnchantment(key, enchantmentName, maximumLevel, target) {
                @Override
                public void onConsume(PlayerItemConsumeEvent event) {
                    commonAction(event,
                        event.getPlayer(),
                        entry,
                        target,
                        forbidOn,
                        levelsList,
                        key,
                        conditions,
                        conditionFlag,
                        enchantmentName,
                        cooldownApplied,
                        instance);
                }
            };

        }
        //</editor-fold>
        //<editor-fold desc="PlayerInteractAtEntityEvent">
        else if (eventString.getName().equals(PlayerInteractAtEntityEvent.class.getName())) {
            ench = new RMBEntityEnchantment(key, enchantmentName, maximumLevel, target) {
                @Override
                public void onRMB(PlayerInteractAtEntityEvent event) {
                    commonAction(event,
                        event.getPlayer(),
                        entry,
                        target,
                        forbidOn,
                        levelsList,
                        key,
                        conditions,
                        conditionFlag,
                        enchantmentName,
                        cooldownApplied,
                        instance);
                }
            };

        }
        //</editor-fold>
        //<editor-fold desc="PlayerInteractEvent">
        else if (eventString.getName().equals(PlayerInteractEvent.class.getName())) {
            ench = new RMBEnchantment(key, enchantmentName, maximumLevel, target) {
                @Override
                public void onRMB(PlayerInteractEvent event) {
                    commonAction(event,
                        event.getPlayer(),
                        entry,
                        target,
                        forbidOn,
                        levelsList,
                        key,
                        conditions,
                        conditionFlag,
                        enchantmentName,
                        cooldownApplied,
                        instance);
                }
            };

        }
        //</editor-fold>
        //<editor-fold desc="PlayerMoveEvent">
        else if (eventString.getName().equals(PlayerMoveEvent.class.getName())) {
            ench = new MoveEnchantment(key, enchantmentName, maximumLevel, target) {
                @Override
                public void onMove(PlayerMoveEvent event) {
                    commonAction(event,
                        event.getPlayer(),
                        entry,
                        target,
                        forbidOn,
                        levelsList,
                        key,
                        conditions,
                        conditionFlag,
                        enchantmentName,
                        cooldownApplied,
                        instance);
                }
            };

        }
        //</editor-fold>
        //<editor-fold desc="PlayerGotHurtEvent">
        else if (eventString.getName().equals(PlayerGotHurtEvent.class.getName())) {
            ench = new GotHurtEnchantment(key, enchantmentName, maximumLevel, target) {
                @Override
                public void onHurt(PlayerGotHurtEvent event) {
                    commonAction(event,
                        event.getVictim(),
                        entry,
                        target,
                        forbidOn,
                        levelsList,
                        key,
                        conditions,
                        conditionFlag,
                        enchantmentName,
                        cooldownApplied,
                        instance);
                }
            };

        }
        //</editor-fold>
        //<editor-fold desc="PlayerHurtsEntityEvent">
        else if (eventString.getName().equals(PlayerHurtsEntityEvent.class.getName())) {
            ench = new HurtsEntityEnchantment(key, enchantmentName, maximumLevel, target) {
                @Override
                public void onAttack(PlayerHurtsEntityEvent event) {
                    commonAction(event,
                        event.getDamager(),
                        entry,
                        target,
                        forbidOn,
                        levelsList,
                        key,
                        conditions,
                        conditionFlag,
                        enchantmentName,
                        cooldownApplied,
                        instance);
                }
            };

        }
        //</editor-fold>
        //<editor-fold desc="PlayerShootBowEvent">
        else if (eventString.getName().equals(PlayerShootBowEvent.class.getName())) {
            ench = new ShootBowEnchantment(key, enchantmentName, maximumLevel, target) {
                @Override
                public void onShoot(PlayerShootBowEvent event) {
                    commonAction(event,
                        event.getShooter(),
                        entry,
                        target,
                        forbidOn,
                        levelsList,
                        key,
                        conditions,
                        conditionFlag,
                        enchantmentName,
                        cooldownApplied,
                        instance);
                }
            };

        }
        //</editor-fold>
        //<editor-fold desc="PlayerToggleSneakEvent">
        else if (eventString.getName().equals(PlayerToggleSneakEvent.class.getName())) {
            ench = new ToggleSneakEnchantment(key, enchantmentName, maximumLevel, target) {
                @Override
                public void onToggle(PlayerToggleSneakEvent event) {
                    commonAction(event,
                        event.getPlayer(),
                        entry,
                        target,
                        forbidOn,
                        levelsList,
                        key,
                        conditions,
                        conditionFlag,
                        enchantmentName,
                        cooldownApplied,
                        instance);
                }
            };

        }
        //</editor-fold>
        //<editor-fold desc="PlayerBowHitEvent">
        else if (eventString.getName().equals(PlayerBowHitEvent.class.getName())) {
            boolean finalForDamager = forDamager, finalForVictim = forVictim, finalValueEmpty = valueEmpty;
            ench = new BowHitEnchantment(key, enchantmentName, maximumLevel, target) {
                @Override
                public void onHit(PlayerBowHitEvent event) {
                    twoPlayerDamageAction(  event,
                        event.getDamager(),
                        event.getVictim(),
                        entry,
                        target,
                        forbidOn,
                        levelsList,
                        key,
                        conditions,
                        conditionFlag,
                        finalValueEmpty,
                        finalForDamager,
                        finalForVictim,
                        enchantmentName,
                        cooldownApplied,
                        instance
                    );
                }
            };

        }
        //</editor-fold>
        //<editor-fold desc="Exception case">
        else { // Invalid trigger parsing
            instance.getUnderscoreLogger().severe("Enchantment " + enchantmentName + " did not get registered - invalid trigger!");
            instance.getDebugger().log("Enchantment " + enchantmentName + " did not get registered - invalid trigger!");
            return null;
        }
        //</editor-fold>

        return new Pair<>(entry, ench);
    }
}

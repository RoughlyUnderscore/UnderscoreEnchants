package com.roughlyunderscore.enchs.parsers;

import com.codingforcookies.armorequip.ArmorEquipEvent;
import com.roughlyunderscore.enchs.UnderscoreEnchants;
import com.roughlyunderscore.enchs.enchants.EnchantmentLevel;
import com.roughlyunderscore.enchs.enchants.UEnchant;
import com.roughlyunderscore.enchs.enchants.abstracts.*;
import com.roughlyunderscore.enchs.events.*;
import com.roughlyunderscore.enchs.parsers.condition.generic.GeneralConditionParser;
import com.roughlyunderscore.enchs.util.DetailedEnchantment;
import com.roughlyunderscore.enchs.util.cooldownutils.Cooldown;
import com.roughlyunderscore.enchs.util.datastructures.Pair;
import com.roughlyunderscore.enchs.util.enums.ActivationIndicator;
import com.roughlyunderscore.enchs.util.general.Utils;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;

import static com.roughlyunderscore.enchs.parsers.action.generic.GeneralActionParser.parseAction;
import static com.roughlyunderscore.enchs.util.general.Utils.*;

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


  /**
   * Adds an enchantment to a corresponding list.
   *
   * @param ench   the enchantment
   * @param plugin UnderscoreEnchants
   */
  @SuppressWarnings("deprecation")
  public void listEnchantment(final Enchantment ench, final UnderscoreEnchants plugin) {
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
      }
    }
  }

  /**
   * Does some behind-the-scenes job on the enchantment, such as registering the events, making it an actual enchantment, listing it.
   *
   * @param ench      the enchantment
   * @param keyHolder a DetailedEnchantment object for that very enchantment
   * @param plugin    UnderscoreEnchants
   */
  public void wrapEnchantment(final Enchantment ench, final DetailedEnchantment keyHolder, final UnderscoreEnchants plugin) {

    if (!(ench instanceof Listener l)) return;
    Bukkit.getServer().getPluginManager().registerEvents(l, plugin);

    try {
      final Field field = Enchantment.class.getDeclaredField("acceptingNew");
      field.setAccessible(true);
      field.set(null, true);
      Enchantment.registerEnchantment(ench);
    } catch (final IllegalAccessException | NoSuchFieldException e) {
      plugin.getUnderscoreLogger().severe("Enchantment " + keyHolder.getName() + " didn't get registered. Restart the server; if the problem won't be fixed, report it to the support Discord.");
      e.printStackTrace();
      return;
    }

    listEnchantment(keyHolder.getEnchantment(), plugin);

    plugin.getAllEnchs().add(keyHolder.getEnchantment());
    plugin.enchantmentData.put(keyHolder, new UEnchant(ench));
  }

  /**
   * Loads an enchantment from a file.
   *
   * @param file   the file to load
   * @param plugin UnderscoreEnchants
   */
  public void loadEnchantment(final File file, final UnderscoreEnchants plugin) {
    final YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
    final Pair<DetailedEnchantment, UEnchant> enchant = parseEnchantment(configuration, plugin);
    if (enchant == null) return;

    final UEnchant enchantment = enchant.getValue();
    wrapEnchantment(enchantment, enchant.getKey(), plugin);

    plugin.getCachedAutocompleteEnchantments().add(enchant.getKey().getCommandName().toLowerCase().replace(" ", "_"));
    UnderscoreEnchants.staticEnchantmentData.put(enchant.getKey(), enchantment);
  }


  @SuppressWarnings({"unchecked", "unused"})
  public void unloadEnchantment(final File file, final UnderscoreEnchants plugin) {
    final YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
    // Pair<DetailedEnchantment, AbstractEnchantment> enchant = plugin.parseEnchantment(configuration);
    final DetailedEnchantment enchantment = findEnchantment(file, plugin);
    final String name = enchantment.getName();
    final NamespacedKey key = enchantment.getKey();

    try {
      final Field keyField = Enchantment.class.getDeclaredField("byKey");
      keyField.setAccessible(true);

      final HashMap<NamespacedKey, Enchantment> byKey = (HashMap<NamespacedKey, Enchantment>) keyField.get(null);
      byKey.remove(key);

      final Field nameField = Enchantment.class.getDeclaredField("byName");
      nameField.setAccessible(true);

      final HashMap<String, Enchantment> byName = (HashMap<String, Enchantment>) nameField.get(null);
      byName.remove(name);
    } catch (final NoSuchFieldException | IllegalAccessException e) {
      plugin.getUnderscoreLogger().severe("Enchantment " + name + " didn't get unregistered. Restart the server; if the problem won't be fixed, report it to the support Discord.");
      e.printStackTrace();
    }

    final Listener listener = UnderscoreEnchants.staticEnchantmentData.get(enchantment);
    HandlerList.unregisterAll(listener);
    UnderscoreEnchants.staticEnchantmentData.remove(enchantment);

    plugin.getCachedAutocompleteEnchantments().remove(name.toLowerCase().replace(" ", "_"));
    plugin.getEnchantmentData().remove(enchantment);
  }

  /**
   * Finds an enchantment from a file.
   *
   * @param file   the file to look at
   * @param plugin UnderscoreEnchants
   */
  public DetailedEnchantment findEnchantment(final File file, final UnderscoreEnchants plugin) {
    // Load file as a yamlconfiguration
    final YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
    // Get the enchantment name
    final String name = configuration.getString("name", "");

    final NamespacedKey key = new NamespacedKey(plugin, "underscore_enchants_" + name.replace(" ", "__"));

    return new DetailedEnchantment(key.getKey(), plugin);
  }

  /**
   * Checks if an enchantment is loaded.
   *
   * @param file   the enchantment to check
   * @param plugin UnderscoreEnchants
   */
  public boolean isEnchantmentLoaded(final File file, final UnderscoreEnchants plugin) {
    return isEnchantmentLoaded(findEnchantment(file, plugin), plugin);
  }

  /**
   * Checks if an enchantment is loaded.
   *
   * @param enchantment the enchantment to check
   * @param plugin      UnderscoreEnchants
   */
  public boolean isEnchantmentLoaded(final DetailedEnchantment enchantment, final UnderscoreEnchants plugin) {
    return plugin.getEnchantmentData().containsKey(enchantment);
  }

  /**
   * This is the root of UnderscoreEnchants parsers.<br>
   * The enchantment parsing starts here, where the YAML file is broken down into individual fields and each field is accounted for.
   * Alongside creating a UEnchant, it also creates a DetailedEnchantment, which might be unnecessary and requires investigation.<br><br>
   *
   * Not only does this method parse the enchantment and return the parsing results, but it also adds it to a list of unique/conflictable enchantments
   * if needed. The rest of the parsing is done with #wrapEnchantment(UEnchant, DetailedEnchantment, UnderscoreEnchants).
   * @param file
   * @param instance
   * @return
   */
  public Pair<DetailedEnchantment, UEnchant> parseEnchantment(final YamlConfiguration file, final UnderscoreEnchants instance) {
    final String damagerOrVictim = file.getString("player");

    final List<String> conditions = file.getStringList("conditions");
    final @Nullable String conditionFlag = file.getString("condition-flag");

    final List<String> forbidOn = file.getStringList("forbid-on");

    final @NonNull List<EnchantmentLevel> levelsList = getLevelsOf(file.getConfigurationSection("levels"));

    // Since 2.2
    final ActivationIndicator activationIndicator = ActivationIndicator.valueOf(file.getString("activation-indicator", "bossbar").toUpperCase());
    final boolean unique = file.getBoolean("unique", false);
    @SuppressWarnings("unchecked") final List<String> conflicts = (List<String>) (file.getList("conflicts-with", Collections.EMPTY_LIST));

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
    UEnchant ench;

    if (eventClass.getName().equals(PlayerPVPEvent.class.getName())) {
      boolean finalForDamager = forDamager, finalForVictim = forVictim, finalValueEmpty = valueEmpty;

      ench = new PVPEnchantment(key, enchantmentName, maximumLevel, target) {
        @Override
        public void onPVP(final PlayerPVPEvent event) {
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
            activationIndicator,
            instance
          );
        }
      };


    }

    else if (eventClass.getName().equals(ArmorEquipEvent.class.getName())) {
      ench = new ArmorEquipEnchantment(key, enchantmentName, maximumLevel, target) {
        @Override
        public void onEquip(final ArmorEquipEvent event) {
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
            activationIndicator,
            instance
          );
        }
      };


    }

    else if (eventClass.getName().equals(BlockBreakEvent.class.getName())) {
      ench = new BlockBreakEnchantment(key, enchantmentName, maximumLevel, target) {
        @Override
        public void onBreak(final BlockBreakEvent event) {
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
            activationIndicator,
            instance
          );
        }
      };


    }

    else if (eventClass.getName().equals(PlayerItemBreakEvent.class.getName())) {
      ench = new ItemBreakEnchantment(key, enchantmentName, maximumLevel, target) {
        @Override
        public void onBreak(final PlayerItemBreakEvent event) {
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
            activationIndicator,
            instance
          );
        }
      };


    }

    else if (eventClass.getName().equals(PlayerItemConsumeEvent.class.getName())) {

      ench = new ItemEatEnchantment(key, enchantmentName, maximumLevel, target) {
        @Override
        public void onConsume(final PlayerItemConsumeEvent event) {
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
            activationIndicator,
            instance
          );
        }
      };


    }

    else if (eventClass.getName().equals(PlayerInteractAtEntityEvent.class.getName())) {
      ench = new RMBEntityEnchantment(key, enchantmentName, maximumLevel, target) {
        @Override
        public void onRMB(final PlayerInteractAtEntityEvent event) {
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
            activationIndicator,
            instance
          );
        }
      };

    }

    else if (eventClass.getName().equals(PlayerInteractEvent.class.getName())) {
      ench = new RMBEnchantment(key, enchantmentName, maximumLevel, target) {
        @Override
        public void onRMB(final PlayerInteractEvent event) {
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
            activationIndicator,
            instance
          );
        }
      };


    }

    else if (eventClass.getName().equals(PlayerMoveEvent.class.getName())) {
      ench = new MoveEnchantment(key, enchantmentName, maximumLevel, target) {
        @Override
        public void onMove(final PlayerMoveEvent event) {
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
            activationIndicator,
            instance
          );
        }
      };


    }

    else if (eventClass.getName().equals(PlayerGotHurtEvent.class.getName())) {
      ench = new GotHurtEnchantment(key, enchantmentName, maximumLevel, target) {
        @Override
        public void onHurt(final PlayerGotHurtEvent event) {
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
            activationIndicator,
            instance
          );
        }
      };


    }

    else if (eventClass.getName().equals(PlayerHurtsEntityEvent.class.getName())) {
      ench = new HurtsEntityEnchantment(key, enchantmentName, maximumLevel, target) {
        @Override
        public void onAttack(final PlayerHurtsEntityEvent event) {
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
            activationIndicator,
            instance
          );
        }
      };


    }

    else if (eventClass.getName().equals(PlayerShootBowEvent.class.getName())) {
      ench = new ShootBowEnchantment(key, enchantmentName, maximumLevel, target) {
        @Override
        public void onShoot(final PlayerShootBowEvent event) {
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
            activationIndicator,
            instance
          );
        }
      };


    }

    else if (eventClass.getName().equals(PlayerToggleSneakEvent.class.getName())) {
      ench = new ToggleSneakEnchantment(key, enchantmentName, maximumLevel, target) {
        @Override
        public void onToggle(final PlayerToggleSneakEvent event) {
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
            activationIndicator,
            instance
          );
        }
      };

    }

    else if (eventClass.getName().equals(PlayerBowHitEvent.class.getName())) {
      boolean finalForDamager = forDamager, finalForVictim = forVictim, finalValueEmpty = valueEmpty;
      ench = new BowHitEnchantment(key, enchantmentName, maximumLevel, target) {
        @Override
        public void onHit(final PlayerBowHitEvent event) {
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
            activationIndicator,
            instance
          );
        }
      };

    }

    else { // Invalid trigger parsing
      instance.getUnderscoreLogger().severe("Enchantment " + enchantmentName + " did not get registered - invalid trigger!");
      return null;
    }

    if (unique) UnderscoreEnchants.uniqueEnchantments.add(ench);
    if (!conflicts.isEmpty()) UnderscoreEnchants.conflicts.put(ench, conflicts);

    return new Pair<>(entry, ench);
  }









  /**
   * Works in parseEnchantment and is placed here for making the code more readable
   *
   * @param event               The event itself, used here to check if is cancelled
   * @param player              The event's player, used for various checks
   * @param entry               The DetailedEnchantment entry, used to check for the item enchantment status
   * @param target              The EnchantmentTarget, used to select the item (ref. above)
   * @param forbidOn            A list of Materials that are forbidden to have this enchantment
   * @param levels              A list of all EnchantmentLevels accessible for this enchantment
   * @param key                 The NamespacedKey of this enchantment, used for checking the toggle status
   * @param conditions          A list of conditions that must be met before activating the enchantments
   * @param flag                A potential condition flag that changes the condition parse
   * @param name                The enchantment name
   * @param cooldown            An overall enchantment cooldown
   * @param activationIndicator The activation indicator placement
   * @param plugin              UnderscoreEnchants
   *                   <br>Also see {@link #parseEnchantment(YamlConfiguration, UnderscoreEnchants)}
   */
  public void commonAction(
    final Event event,
    final Player player,
    final DetailedEnchantment entry,
    final EnchantmentTarget target,
    final List<String> forbidOn,
    final List<EnchantmentLevel> levels,
    final NamespacedKey key,
    final List<String> conditions,
    String flag,
    final String name,
    final int cooldown,
    final ActivationIndicator activationIndicator,
    final UnderscoreEnchants plugin
  ) {
    final int lvl = getEnchantLevel(player, entry, target, forbidOn, plugin);
    if (lvl == 0) return;

    // plugin.getUnderscoreLogger().info(">" + name + " | Found enchantment: " + name + " with level: " + lvl + "by player: " + player.getName());

    if (flag == null) flag = "";

    // plugin.getUnderscoreLogger().info(">" + name + " | Current flag: " + flag);

    final EnchantmentLevel level = levels.get(lvl - 1);

    // plugin.getUnderscoreLogger().info(">" + name + " | Attempting to validate the activation...");
    // Pass all the checks
    if (!validateActivation(plugin, event, player, level, key, conditions, flag)) return;

    // plugin.getUnderscoreLogger().info(">" + name + " | Activation validated!");

    // Parse every action
    for (final String lev : level.getAction()) {
      try {
        parseAction(event, lev, plugin);
      } catch (final Exception e) {
        plugin.getUnderscoreLogger().info("Please check your actions in " + name + "! They are wrongly configured.");
        return;
      }
    }

    // Send the activation message
    activationMessage(name, player, plugin, activationIndicator);

    // If the cooldown provided by the configuration is not 0, create that cooldown
    if (cooldown != 0) plugin.getCooldowns().add(new Cooldown(cooldown, entry.getEnchantment(), player.getUniqueId()));
    // If the enchantment level has a cooldown (it's not 0), create that cooldown
    if (level.getCooldown() != 0)
      plugin.getCooldowns().add(new Cooldown(level.getCooldown(), entry.getEnchantment(), player.getUniqueId()));
  }

  /**
   * Works in parseEnchantment and is placed here for making the code more readable
   *
   * @param event               The event itself, used here to check if is cancelled
   * @param player              The event's player,used for various checks
   * @param entry               The DetailedEnchantment entry, used to check for the item enchantment status
   * @param extra               An extra item to check for the enchantment status
   * @param target              The EnchantmentTarget, used to select the item (ref. above)
   * @param forbidOn            A list of Materials that are forbidden to have this enchantment
   * @param levels              A list of all EnchantmentLevels accessible for this enchantment
   * @param key                 The NamespacedKey of this enchantment, used for checking the toggle status
   * @param conditions          A list of conditions that must be met before activating the enchantments
   * @param flag                A potential condition flag that changes the condition parse
   * @param name                The enchantment name
   * @param cooldown            An overall enchantment cooldown
   * @param activationIndicator The activation indicator placement
   * @param plugin              UnderscoreEnchants
   *                   <br>Also see {@link #parseEnchantment(YamlConfiguration, UnderscoreEnchants)}
   */
  public void extraItemAction(
    final Event event,
    final Player player,
    final DetailedEnchantment entry,
    final ItemStack extra,
    final EnchantmentTarget target,
    final List<String> forbidOn,
    final List<EnchantmentLevel> levels,
    final NamespacedKey key,
    final List<String> conditions,
    String flag,
    final String name,
    final int cooldown,
    final ActivationIndicator activationIndicator,
    final UnderscoreEnchants plugin
  ) {
    int lvl = getEnchantLevel(player, entry, extra, target, forbidOn, plugin);
    if (lvl == 0) return;

    if (flag == null) flag = "";

    final EnchantmentLevel level = levels.get(lvl - 1);

    // Pass all the checks
    if (!validateActivation(plugin, event, player, level, key, conditions, flag)) return;

    // Parse every action
    for (final String lev : level.getAction()) {
      try {
        parseAction(event, lev, plugin);
      } catch (final Exception e) {
        plugin.getUnderscoreLogger().info("Please check your actions in " + name + "! They are wrongly configured.");
        return;
      }
    }

    // Send the activation message
    activationMessage(name, player, plugin, activationIndicator);

    // If the cooldown provided by the configuration is not 0, create that cooldown
    if (cooldown != 0) plugin.getCooldowns().add(new Cooldown(cooldown, entry.getEnchantment(), player.getUniqueId()));
    // If the enchantment level has a cooldown (it's not 0), create that cooldown
    if (level.getCooldown() != 0)
      plugin.getCooldowns().add(new Cooldown(level.getCooldown(), entry.getEnchantment(), player.getUniqueId()));
  }

  /**
   * Works in parseEnchantment and is placed here for making the code more readable
   *
   * @param event                 The event itself, used here to check if is cancelled
   * @param damager               The event's damager, used for various checks
   * @param victim                The event's victim, used for various checks
   * @param entry                 The DetailedEnchantment entry, used to check for the item enchantment status
   * @param target                The EnchantmentTarget, used to select the item (ref. above)
   * @param forbidOn              A list of Materials that are forbidden to have this enchantment
   * @param levels                A list of all EnchantmentLevels accessible for this enchantment
   * @param key                   The NamespacedKey of this enchantment, used for checking the toggle status
   * @param conditions            A list of conditions that must be met before activating the enchantments
   * @param flag                  A potential condition flag that changes the condition parse
   * @param isEmpty               A boolean, indicating if the "player" value exists in the configuration header
   * @param isForDamager          A boolean, indicating if the "player" value is equal to DAMAGER
   * @param isForVictim           A boolean, indicating if the "player" value is equal to VICTIM
   * @param name                  The enchantment name
   * @param cooldown              An overall enchantment cooldown
   * @param activationIndicator   The activation indicator placement
   * @param plugin                UnderscoreEnchants
   *                     <br>Also see {@link #parseEnchantment(YamlConfiguration, UnderscoreEnchants)}
   */
  public void twoPlayerDamageAction(
    final Event event,
    final Player damager,
    final Player victim,
    final DetailedEnchantment entry,
    final EnchantmentTarget target,
    final List<String> forbidOn,
    final List<EnchantmentLevel> levels,
    final NamespacedKey key,
    final List<String> conditions,
    String flag,
    final boolean isEmpty,
    final boolean isForDamager,
    final boolean isForVictim,
    final String name,
    final int cooldown,
    final ActivationIndicator activationIndicator,
    final UnderscoreEnchants plugin
  ) {
    // Create the EnchantmentLevel
    final int damagerLvl = getEnchantLevel(damager, entry, target, forbidOn, plugin), victimLvl = getEnchantLevel(victim, entry, target, forbidOn, plugin);
    boolean damagerActivated = true, victimActivated = true;

    if (flag == null) flag = "";


    if (damagerLvl == 0) damagerActivated = false;
    if (victimLvl == 0) victimActivated = false;

    EnchantmentLevel damagerEnchLevel = EnchantmentLevel.empty(), victimEnchLevel = EnchantmentLevel.empty();

    if (damagerActivated) {
      damagerEnchLevel = levels.get(damagerLvl - 1);
      if (!validateActivation(plugin, event, damager, damagerEnchLevel, key, conditions, flag, "damager"))
        damagerActivated = false;
    }
    if (victimActivated) {
      victimEnchLevel = levels.get(victimLvl - 1);
      if (!validateActivation(plugin, event, victim, victimEnchLevel, key, conditions, flag, "victim"))
        victimActivated = false;
    }
    if (!isEmpty) {
      if (!isForDamager) damagerActivated = false;
      if (!isForVictim) victimActivated = false;
    }

    if (damagerActivated) {
      // Parse every action
      for (final String lev : damagerEnchLevel.getAction()) {
        try {
          parseAction(event, lev, plugin);
        } catch (final Exception e) {
          plugin.getUnderscoreLogger().info("Please check your actions in " + name + "! They are wrongly configured.");
          return;
        }
      }

      // Send the activation message
      activationMessage(name, damager, plugin, activationIndicator);

      // If the cooldown provided by the configuration is not 0, create that cooldown
      if (cooldown != 0)
        plugin.getCooldowns().add(new Cooldown(cooldown, entry.getEnchantment(), damager.getUniqueId()));
      // If the enchantment level has a cooldown (it's not 0), create that cooldown
      if (damagerEnchLevel.getCooldown() != 0)
        plugin.getCooldowns().add(new Cooldown(damagerEnchLevel.getCooldown(), entry.getEnchantment(), damager.getUniqueId()));
    }

    if (victimActivated) {
      // Parse every action
      for (final String lev : victimEnchLevel.getAction()) {
        try {
          parseAction(event, lev, plugin);
        } catch (final Exception e) {
          plugin.getUnderscoreLogger().info("Please check your actions in " + name + "! They are wrongly configured.");
          return;
        }
      }

      // Send the activation message
      activationMessage(name, victim, plugin, activationIndicator);

      // If the cooldown provided by the configuration is not 0, create that cooldown
      if (cooldown != 0)
        plugin.getCooldowns().add(new Cooldown(cooldown, entry.getEnchantment(), victim.getUniqueId()));
      // If the enchantment level has a cooldown (it's not 0), create that cooldown
      if (victimEnchLevel.getCooldown() != 0)
        plugin.getCooldowns().add(new Cooldown(victimEnchLevel.getCooldown(), entry.getEnchantment(), victim.getUniqueId()));
    }
  }
}

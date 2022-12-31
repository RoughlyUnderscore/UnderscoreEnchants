package com.roughlyunderscore.enchs;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.InvalidCommandArgument;
import com.codingforcookies.armorequip.ArmorListener;
import com.codingforcookies.armorequip.DispenserArmorListener;
import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XPotion;
import com.roughlyunderscore.enchantsapi.EnchantmentLoadResponse;
import com.roughlyunderscore.enchantsapi.EnchantmentUnloadResponse;
import com.roughlyunderscore.enchantsapi.UEnchantsAPI;
import com.roughlyunderscore.enchs.commands.UnderscoreEnchantsCommand;
import com.roughlyunderscore.enchs.config.MainConfig;
import com.roughlyunderscore.enchs.enchants.abstracts.AbstractEnchantment;
import com.roughlyunderscore.enchs.events.GeneralListener;
import com.roughlyunderscore.enchs.gui.AnvilHandler;
import com.roughlyunderscore.enchs.gui.EnchantTableHandler;
import com.roughlyunderscore.enchs.listeners.InteractListener;
import com.roughlyunderscore.enchs.listeners.JoinListener;
import com.roughlyunderscore.enchs.listeners.LeaveListener;
import com.roughlyunderscore.enchs.listeners.LootPopulateListener;
import com.roughlyunderscore.enchs.registration.Register;
import com.roughlyunderscore.enchs.util.Constants;
import com.roughlyunderscore.enchs.util.Debug;
import com.roughlyunderscore.enchs.util.cooldownutils.ActionbarCooldown;
import com.roughlyunderscore.enchs.util.cooldownutils.Cooldown;
import com.roughlyunderscore.enchs.util.DetailedEnchantment;
import com.roughlyunderscore.enchs.config.Messages;
import com.roughlyunderscore.enchs.util.general.Utils;
import de.jeff_media.updatechecker.UpdateChecker;
import lombok.Getter;
import lombok.Setter;
import me.clip.placeholderapi.PlaceholderAPI;
import net.milkbowl.vault.economy.Economy;
import org.apache.commons.io.FileUtils;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.roughlyunderscore.enchs.registration.Register.*;
import static com.roughlyunderscore.enchs.registration.Register.unloadEnchantment;

public class UnderscoreEnchants extends JavaPlugin implements UEnchantsAPI {


  // The main class.
  // A lot of stuff in a confined place, don't trip over something!


  @Getter @Setter
  public Messages messages;

  public static DetailedEnchantment WRONG_LEVEL, WRONG_NAME;

  @Getter
  public final List<PotionEffectType> positiveEffects = new ArrayList<>();
  @Getter
  public final List<PotionEffectType> negativeEffects = new ArrayList<>();

  public static Economy econ;

  @Getter
  public Debug debugger = null;
  private static BufferedWriter writer = null;

  @Getter
  private final List<ActionbarCooldown> actionbars = new ArrayList<>();
  @Getter
  private final List<Cooldown> cooldowns = new ArrayList<>();

  // this is not top tier init code for sure
  public static List<Material> weaponsList = new ArrayList<>();
  public static List<Material> toolsList = new ArrayList<>();
  public static List<Material> armorList = new ArrayList<>();
  @Getter
  public List<Enchantment> allEnchs = new ArrayList<>();
  @Getter
  public Map<DetailedEnchantment, AbstractEnchantment> enchantmentData = new HashMap<>();
  public static final Map<DetailedEnchantment, AbstractEnchantment> staticEnchantmentData = new HashMap<>();

  @Getter
  private final Map<UUID, Integer> gods = new HashMap<>();

  @Getter
  private final Logger underscoreLogger = Logger.getLogger(UnderscoreEnchants.class.getName());

  public static List<Enchantment> weaponEnchantments = new ArrayList<>();
  public static List<Enchantment> bowEnchantments = new ArrayList<>();
  public static List<Enchantment> toolEnchantments = new ArrayList<>();
  public static List<Enchantment> helmetEnchantments = new ArrayList<>();
  public static List<Enchantment> chestplateEnchantments = new ArrayList<>();
  public static List<Enchantment> leggingsEnchantments = new ArrayList<>();
  public static List<Enchantment> bootsEnchantments = new ArrayList<>();
  public static List<Enchantment> tridentEnchantments = new ArrayList<>();

  @Getter
  public final List<Material> plankRepariable = new ArrayList<>();
  @Getter
  public final List<Material> leatherRepariable = new ArrayList<>();
  @Getter
  public final List<Material> cobbleRepariable = new ArrayList<>();
  @Getter
  public final List<Material> cobbleTypes = new ArrayList<>();
  @Getter
  public final List<Material> ironRepariable = new ArrayList<>();
  @Getter
  public final List<Material> goldRepariable = new ArrayList<>();
  @Getter
  public final List<Material> diamondRepariable = new ArrayList<>();
  @Getter
  public final List<Material> netheriteRepariable = new ArrayList<>();

  private final String serverVersion = Bukkit.getBukkitVersion();

  private void regTest() {
    if (!serverVersion.contains("1.13") &&
      !serverVersion.contains("1.14") &&
      !serverVersion.contains("1.15") &&
      !serverVersion.contains("1.16") &&
      !serverVersion.contains("1.17") &&
      !serverVersion.contains("1.18") &&
      !serverVersion.contains("1.19")) {
      throw new IllegalStateException("Couldn't start the plugin. Is your server running on <1.13? The plugin works on 1.13+ and functions properly on 1.17+.");
    }
  }
  @Getter
  @Setter
  UpdateChecker checker;
  @Getter
  @Setter
  Metrics metrics;

  @Getter @Setter
  MainConfig mainConfig;

  @Override
  public void onEnable() {
    WRONG_LEVEL = new DetailedEnchantment("WRONG_LEVEL");
    WRONG_NAME = new DetailedEnchantment("WRONG_NAME");

    final List<Enchantment> vanillaEnchants = Arrays.stream(Enchantment.values()).toList();
    vanillaEnchants.forEach(enchantment -> {
      DetailedEnchantment ench = new DetailedEnchantment(enchantment.getKey());
      enchantmentData.put(ench, new AbstractEnchantment(enchantment));
    });

    staticEnchantmentData.putAll(enchantmentData);

    saveDefaultConfig();
    final FileConfiguration config = this.getConfig();
    config.options().copyDefaults(true);
    saveDefaultConfig();
    reloadConfig();

    this.init();
    mainConfig = new MainConfig(this);
    messages = new Messages(this);

    try {
      regTest();
    } catch (final IllegalStateException why_do_you_run_a_plugin_that_says_1_17_on_its_page_on_a_server_that_is_under_1_13) {
      onDisable();
      return;
    }

    final String directPath = this.getDataFolder().getPath() + File.separator + "debug";
    final String debugFile = System.currentTimeMillis() + ".debug";

    final File directPathFile = new File(directPath);
    if (!directPathFile.exists()) {
      directPathFile.mkdirs();
    }
    final File filee = new File(directPath + File.separator + debugFile);
    if (!filee.exists()) {
      try { // TODO remove
        filee.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    final FileWriter fileW;
    try { // TODO remove
      fileW = new FileWriter(filee.getAbsoluteFile());
    } catch (final IOException e) {
      e.printStackTrace();
      return;
    }
    writer = new BufferedWriter(fileW);
    debugger = new Debug(mainConfig.DEBUG, writer, this);

    checker = UpdateChecker.init(this, Constants.SPIGOT_ID)
      .setDownloadLink(Constants.SPIGOT_ID)
      .setDonationLink("https://donationalerts.com/r/zbll")
      .onFail((senders, ex) -> {
        this.getUnderscoreLogger().severe("Could not check for updates, make sure your connection is stable!");
        this.getUnderscoreLogger().severe(ex.getMessage());
      })
      .onSuccess((senders, ex) -> {
        this.getUnderscoreLogger().finest("Thanks for using UnderscoreEnchants!");
        this.getUnderscoreLogger().finest("Successfully checked for updates.");
      })
      .checkEveryXHours(mainConfig.UPDATER_INTERVAL)
      .setNotifyOpsOnJoin(true)
      .checkNow();

    if (mainConfig.BSTATS_ENABLED) {
      metrics = new Metrics(this, Constants.BSTATS_ID);

      metrics.addCustomChart(new SimplePie(
        "language",
        () -> mainConfig.LANGUAGE
      ));

      metrics.addCustomChart(new SimplePie(
        "enchantments_count",
        () -> String.valueOf(allEnchs.size())
      ));
    }
    

    
    final File file0 = new File(this.getDataFolder().getPath() + File.separator + "enchantments");
    if (!file0.exists()) file0.mkdirs();

    final ArrayList<File> files = new ArrayList<>(FileUtils.listFiles(file0, new String[] {"yml", "yaml"}, true));
    for (File file : files)
      Register.loadEnchantment(file, this);

    

    
    final BukkitCommandManager commandManager = new BukkitCommandManager(this);
    commandManager.getCommandConditions().addCondition("cant_console", (sender) -> {
      if (!sender.getIssuer().isPlayer()) {
        sender.getIssuer().sendMessage(messages.NO_CONSOLE);
        throw new ConditionFailedException("This command cannot be executed by console.");
      }
    });

    commandManager.getCommandCompletions().registerCompletion("enchant-name-completion", context -> {
      final List<String> enchants = new ArrayList<>();
      for (DetailedEnchantment ench : enchantmentData.keySet()) {
        enchants.add(ench.getCommandName());
      }
      return enchants;
    });

    commandManager.getCommandCompletions().registerCompletion("yes-no-completion", context -> Arrays.asList("yes", "no"));

    commandManager.getCommandContexts().registerContext(DetailedEnchantment.class, context -> {
      final String enchName = context.popFirstArg();
      final DetailedEnchantment ench = Utils.parseEnchantment(enchName, this);
      if (ench == null) {
        String message = getMessages().WRONG_NAME;
        if (context.getSender() instanceof Player player) message = PlaceholderAPI.setPlaceholders(player, message);
        context.getSender().sendMessage(message);

        throw new InvalidCommandArgument("Enchantment " + enchName + " does not exist!", false);
      }
      return ench;
    });

    commandManager.registerCommand(new UnderscoreEnchantsCommand(this));

    commandManager.getCommandCompletions().registerCompletion("enchant-level-completion", context -> {
      final DetailedEnchantment enchantment = context.getContextValue(DetailedEnchantment.class);
      if (enchantment == null || enchantment.getEnchantment() == null) {
        // throw new InvalidCommandArgument("Enchantment does not exist!", false);
        return List.of();
      }
      return IntStream
        .range(enchantment.getEnchantment().getStartLevel(), enchantment.getEnchantment().getMaxLevel() + 1)
        .boxed()
        .map(String::valueOf)
        .collect(Collectors.toList());
    });
    
    
    final PluginManager manager = getServer().getPluginManager();
    manager.registerEvents(new InteractListener(this), this);

    manager.registerEvents(new AnvilHandler(this), this);
    manager.registerEvents(new EnchantTableHandler(this), this);

    manager.registerEvents(new LootPopulateListener(this), this);

    manager.registerEvents(new ArmorListener(), this);
    manager.registerEvents(new DispenserArmorListener(), this);

    manager.registerEvents(new GeneralListener(), this);

    manager.registerEvents(new JoinListener(), this);
    manager.registerEvents(new LeaveListener(this), this);

    

    
    if (!setupEconomy()) {
      this.getUnderscoreLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
      getServer().getPluginManager().disablePlugin(this);
      return;
    }
    

    new BukkitRunnable() {
      @Override
      public void run() {
        cooldowns.removeIf(Cooldown::decrease);
      }
    }.runTaskTimer(this, 0, 20);

    new BukkitRunnable() {
      @Override
      public void run() {
        for (Map.Entry<UUID, Integer> entry : gods.entrySet()) {
          if (entry.getValue() == 1) {
            gods.remove(entry.getKey());
            Bukkit.getPlayer(entry.getKey()).setInvulnerable(false);
          } else {
            gods.replace(entry.getKey(), entry.getValue() - 1);
          }
        }
      }
    }.runTaskTimer(this, 0, 1);

    new BukkitRunnable() {
      @Override
      public void run() {
        actionbars.removeIf(ActionbarCooldown::decrease);
      }
    }.runTaskTimer(this, 0, 20);

  }

  
  private boolean setupEconomy() {
    if (!mainConfig.REQUIRE_VAULT) return true;
    if (getServer().getPluginManager().getPlugin("Vault") == null) {
      return false;
    }
    final RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
    if (rsp == null) {
      return false;
    }
    econ = rsp.getProvider();
    return true;
  }

  private void init() {
    weaponEnchantments.addAll(Arrays.asList(
      XEnchantment.DAMAGE_ALL.getEnchant(),
      XEnchantment.DAMAGE_ARTHROPODS.getEnchant(),
      XEnchantment.FIRE_ASPECT.getEnchant(),
      XEnchantment.KNOCKBACK.getEnchant(),
      XEnchantment.LOOT_BONUS_MOBS.getEnchant(),
      XEnchantment.DAMAGE_UNDEAD.getEnchant(),
      XEnchantment.DURABILITY.getEnchant(),
      XEnchantment.MENDING.getEnchant(),
      XEnchantment.SWEEPING_EDGE.getEnchant(),
      XEnchantment.VANISHING_CURSE.getEnchant()
    ));
    bowEnchantments.addAll(Arrays.asList(
      XEnchantment.ARROW_FIRE.getEnchant(),
      XEnchantment.ARROW_DAMAGE.getEnchant(),
      XEnchantment.ARROW_KNOCKBACK.getEnchant(),
      XEnchantment.ARROW_INFINITE.getEnchant(),
      XEnchantment.DURABILITY.getEnchant(),
      XEnchantment.MENDING.getEnchant(),
      XEnchantment.VANISHING_CURSE.getEnchant()
    ));
    toolEnchantments.addAll(Arrays.asList(
      XEnchantment.DURABILITY.getEnchant(),
      XEnchantment.DIG_SPEED.getEnchant(),
      XEnchantment.LOOT_BONUS_BLOCKS.getEnchant(),
      XEnchantment.SILK_TOUCH.getEnchant(),
      XEnchantment.LURE.getEnchant(),
      XEnchantment.LUCK.getEnchant(),
      XEnchantment.MENDING.getEnchant(),
      XEnchantment.VANISHING_CURSE.getEnchant()
    ));
    helmetEnchantments.addAll(Arrays.asList(
      XEnchantment.WATER_WORKER.getEnchant(),
      XEnchantment.PROTECTION_EXPLOSIONS.getEnchant(),
      XEnchantment.PROTECTION_FIRE.getEnchant(),
      XEnchantment.PROTECTION_PROJECTILE.getEnchant(),
      XEnchantment.PROTECTION_ENVIRONMENTAL.getEnchant(),
      XEnchantment.OXYGEN.getEnchant(),
      XEnchantment.THORNS.getEnchant(),
      XEnchantment.DURABILITY.getEnchant(),
      XEnchantment.VANISHING_CURSE.getEnchant(),
      XEnchantment.BINDING_CURSE.getEnchant(),
      XEnchantment.MENDING.getEnchant()
    ));
    chestplateEnchantments.addAll(Arrays.asList(
      XEnchantment.PROTECTION_EXPLOSIONS.getEnchant(),
      XEnchantment.PROTECTION_FIRE.getEnchant(),
      XEnchantment.PROTECTION_PROJECTILE.getEnchant(),
      XEnchantment.PROTECTION_ENVIRONMENTAL.getEnchant(),
      XEnchantment.THORNS.getEnchant(),
      XEnchantment.DURABILITY.getEnchant(),
      XEnchantment.VANISHING_CURSE.getEnchant(),
      XEnchantment.BINDING_CURSE.getEnchant(),
      XEnchantment.MENDING.getEnchant()
    ));
    leggingsEnchantments.addAll(Arrays.asList(
      XEnchantment.PROTECTION_EXPLOSIONS.getEnchant(),
      XEnchantment.PROTECTION_FIRE.getEnchant(),
      XEnchantment.PROTECTION_PROJECTILE.getEnchant(),
      XEnchantment.PROTECTION_ENVIRONMENTAL.getEnchant(),
      XEnchantment.THORNS.getEnchant(),
      XEnchantment.DURABILITY.getEnchant(),
      XEnchantment.VANISHING_CURSE.getEnchant(),
      XEnchantment.BINDING_CURSE.getEnchant(),
      XEnchantment.MENDING.getEnchant()
    ));
    bootsEnchantments.addAll(Arrays.asList(
      XEnchantment.DEPTH_STRIDER.getEnchant(),
      XEnchantment.PROTECTION_FALL.getEnchant(),
      XEnchantment.PROTECTION_FIRE.getEnchant(),
      XEnchantment.PROTECTION_PROJECTILE.getEnchant(),
      XEnchantment.PROTECTION_ENVIRONMENTAL.getEnchant(),
      XEnchantment.THORNS.getEnchant(),
      XEnchantment.DURABILITY.getEnchant(),
      XEnchantment.PROTECTION_EXPLOSIONS.getEnchant(),
      XEnchantment.DEPTH_STRIDER.getEnchant(),
      XEnchantment.VANISHING_CURSE.getEnchant(),
      XEnchantment.BINDING_CURSE.getEnchant(),
      XEnchantment.FROST_WALKER.getEnchant(),
      XEnchantment.MENDING.getEnchant(),
      XEnchantment.SOUL_SPEED.getEnchant()
    ));

    plankRepariable.addAll(Arrays.asList(
      XMaterial.WOODEN_SWORD.parseMaterial(),
      XMaterial.WOODEN_SHOVEL.parseMaterial(),
      XMaterial.WOODEN_PICKAXE.parseMaterial(),
      XMaterial.WOODEN_AXE.parseMaterial(),
      XMaterial.WOODEN_HOE.parseMaterial(),
      XMaterial.SHIELD.parseMaterial()
    ));
    leatherRepariable.addAll(Arrays.asList(
      XMaterial.LEATHER_HELMET.parseMaterial(),
      XMaterial.LEATHER_CHESTPLATE.parseMaterial(),
      XMaterial.LEATHER_LEGGINGS.parseMaterial(),
      XMaterial.LEATHER_BOOTS.parseMaterial()
    ));
    cobbleRepariable.addAll(Arrays.asList(
      XMaterial.STONE_SWORD.parseMaterial(),
      XMaterial.STONE_SHOVEL.parseMaterial(),
      XMaterial.STONE_PICKAXE.parseMaterial(),
      XMaterial.STONE_AXE.parseMaterial(),
      XMaterial.STONE_HOE.parseMaterial()
    ));
    cobbleTypes.addAll(Arrays.asList(
      XMaterial.COBBLESTONE.parseMaterial(),
      XMaterial.COBBLED_DEEPSLATE.parseMaterial(),
      XMaterial.BLACKSTONE.parseMaterial()
    ));
    ironRepariable.addAll(Arrays.asList(
      XMaterial.IRON_SWORD.parseMaterial(),
      XMaterial.IRON_SHOVEL.parseMaterial(),
      XMaterial.IRON_PICKAXE.parseMaterial(),
      XMaterial.IRON_AXE.parseMaterial(),
      XMaterial.IRON_HOE.parseMaterial(),
      XMaterial.IRON_HELMET.parseMaterial(),
      XMaterial.IRON_CHESTPLATE.parseMaterial(),
      XMaterial.IRON_LEGGINGS.parseMaterial(),
      XMaterial.IRON_BOOTS.parseMaterial(),
      XMaterial.CHAINMAIL_HELMET.parseMaterial(),
      XMaterial.CHAINMAIL_CHESTPLATE.parseMaterial(),
      XMaterial.CHAINMAIL_LEGGINGS.parseMaterial(),
      XMaterial.CHAINMAIL_BOOTS.parseMaterial()
    ));
    goldRepariable.addAll(Arrays.asList(
      XMaterial.GOLDEN_HELMET.parseMaterial(),
      XMaterial.GOLDEN_CHESTPLATE.parseMaterial(),
      XMaterial.GOLDEN_LEGGINGS.parseMaterial(),
      XMaterial.GOLDEN_BOOTS.parseMaterial(),
      XMaterial.GOLDEN_SWORD.parseMaterial(),
      XMaterial.GOLDEN_SHOVEL.parseMaterial(),
      XMaterial.GOLDEN_PICKAXE.parseMaterial(),
      XMaterial.GOLDEN_AXE.parseMaterial(),
      XMaterial.GOLDEN_HOE.parseMaterial()
    ));
    diamondRepariable.addAll(Arrays.asList(
      XMaterial.DIAMOND_HELMET.parseMaterial(),
      XMaterial.DIAMOND_CHESTPLATE.parseMaterial(),
      XMaterial.DIAMOND_LEGGINGS.parseMaterial(),
      XMaterial.DIAMOND_BOOTS.parseMaterial(),
      XMaterial.DIAMOND_SWORD.parseMaterial(),
      XMaterial.DIAMOND_SHOVEL.parseMaterial(),
      XMaterial.DIAMOND_PICKAXE.parseMaterial(),
      XMaterial.DIAMOND_AXE.parseMaterial(),
      XMaterial.DIAMOND_HOE.parseMaterial()
    ));
    netheriteRepariable.addAll(Arrays.asList(
      XMaterial.NETHERITE_HELMET.parseMaterial(),
      XMaterial.NETHERITE_CHESTPLATE.parseMaterial(),
      XMaterial.NETHERITE_LEGGINGS.parseMaterial(),
      XMaterial.NETHERITE_BOOTS.parseMaterial(),
      XMaterial.NETHERITE_SWORD.parseMaterial(),
      XMaterial.NETHERITE_SHOVEL.parseMaterial(),
      XMaterial.NETHERITE_PICKAXE.parseMaterial(),
      XMaterial.NETHERITE_AXE.parseMaterial(),
      XMaterial.NETHERITE_HOE.parseMaterial()
    ));

    positiveEffects.add(XPotion.DAMAGE_RESISTANCE.getPotionEffectType());
    positiveEffects.add(XPotion.FIRE_RESISTANCE.getPotionEffectType());
    positiveEffects.add(XPotion.INCREASE_DAMAGE.getPotionEffectType());
    positiveEffects.add(XPotion.ABSORPTION.getPotionEffectType());
    positiveEffects.add(XPotion.FAST_DIGGING.getPotionEffectType());
    positiveEffects.add(XPotion.HEAL.getPotionEffectType());
    positiveEffects.add(XPotion.HEALTH_BOOST.getPotionEffectType());
    positiveEffects.add(XPotion.INVISIBILITY.getPotionEffectType());
    positiveEffects.add(XPotion.CONDUIT_POWER.getPotionEffectType());
    positiveEffects.add(XPotion.HERO_OF_THE_VILLAGE.getPotionEffectType());
    positiveEffects.add(XPotion.DOLPHINS_GRACE.getPotionEffectType());
    positiveEffects.add(XPotion.GLOWING.getPotionEffectType());
    positiveEffects.add(XPotion.LUCK.getPotionEffectType());
    positiveEffects.add(XPotion.WATER_BREATHING.getPotionEffectType());
    positiveEffects.add(XPotion.SATURATION.getPotionEffectType());
    positiveEffects.add(XPotion.SPEED.getPotionEffectType());
    positiveEffects.add(XPotion.JUMP.getPotionEffectType());
    positiveEffects.add(XPotion.NIGHT_VISION.getPotionEffectType());
    positiveEffects.add(XPotion.REGENERATION.getPotionEffectType());

    XPotion.DEBUFFS.forEach(effect -> negativeEffects.add(effect.getPotionEffectType()));

    weaponsList = Arrays.asList(
      XMaterial.WOODEN_SWORD.parseMaterial(),
      XMaterial.STONE_SWORD.parseMaterial(),
      XMaterial.GOLDEN_SWORD.parseMaterial(),
      XMaterial.IRON_SWORD.parseMaterial(),
      XMaterial.DIAMOND_SWORD.parseMaterial(),
      XMaterial.NETHERITE_SWORD.parseMaterial()
    );
    toolsList = Arrays.asList(
      XMaterial.IRON_SHOVEL.parseMaterial(),
      XMaterial.IRON_PICKAXE.parseMaterial(),
      XMaterial.IRON_AXE.parseMaterial(),
      XMaterial.WOODEN_SHOVEL.parseMaterial(),
      XMaterial.WOODEN_PICKAXE.parseMaterial(),
      XMaterial.WOODEN_AXE.parseMaterial(),
      XMaterial.STONE_SHOVEL.parseMaterial(),
      XMaterial.STONE_PICKAXE.parseMaterial(),
      XMaterial.STONE_AXE.parseMaterial(),
      XMaterial.DIAMOND_SHOVEL.parseMaterial(),
      XMaterial.DIAMOND_PICKAXE.parseMaterial(),
      XMaterial.DIAMOND_AXE.parseMaterial(),
      XMaterial.GOLDEN_SHOVEL.parseMaterial(),
      XMaterial.GOLDEN_PICKAXE.parseMaterial(),
      XMaterial.GOLDEN_AXE.parseMaterial(),
      XMaterial.WOODEN_HOE.parseMaterial(),
      XMaterial.STONE_HOE.parseMaterial(),
      XMaterial.IRON_HOE.parseMaterial(),
      XMaterial.DIAMOND_HOE.parseMaterial(),
      XMaterial.GOLDEN_HOE.parseMaterial(),
      XMaterial.NETHERITE_HOE.parseMaterial(),
      XMaterial.NETHERITE_SHOVEL.parseMaterial(),
      XMaterial.NETHERITE_AXE.parseMaterial(),
      XMaterial.NETHERITE_PICKAXE.parseMaterial(),
      XMaterial.FISHING_ROD.parseMaterial(),
      XMaterial.FLINT_AND_STEEL.parseMaterial()
    );
    armorList = Arrays.asList(
      XMaterial.DIAMOND_HELMET.parseMaterial(),
      XMaterial.DIAMOND_CHESTPLATE.parseMaterial(),
      XMaterial.DIAMOND_LEGGINGS.parseMaterial(),
      XMaterial.DIAMOND_BOOTS.parseMaterial(),
      XMaterial.GOLDEN_HELMET.parseMaterial(),
      XMaterial.GOLDEN_CHESTPLATE.parseMaterial(),
      XMaterial.GOLDEN_LEGGINGS.parseMaterial(),
      XMaterial.GOLDEN_BOOTS.parseMaterial(),
      XMaterial.IRON_HELMET.parseMaterial(),
      XMaterial.IRON_CHESTPLATE.parseMaterial(),
      XMaterial.IRON_LEGGINGS.parseMaterial(),
      XMaterial.IRON_BOOTS.parseMaterial(),
      XMaterial.CHAINMAIL_HELMET.parseMaterial(),
      XMaterial.CHAINMAIL_CHESTPLATE.parseMaterial(),
      XMaterial.CHAINMAIL_LEGGINGS.parseMaterial(),
      XMaterial.CHAINMAIL_BOOTS.parseMaterial(),
      XMaterial.LEATHER_BOOTS.parseMaterial(),
      XMaterial.LEATHER_CHESTPLATE.parseMaterial(),
      XMaterial.LEATHER_LEGGINGS.parseMaterial(),
      XMaterial.LEATHER_BOOTS.parseMaterial(),
      XMaterial.NETHERITE_HELMET.parseMaterial(),
      XMaterial.NETHERITE_CHESTPLATE.parseMaterial(),
      XMaterial.NETHERITE_LEGGINGS.parseMaterial(),
      XMaterial.NETHERITE_BOOTS.parseMaterial(),
      XMaterial.TURTLE_HELMET.parseMaterial()
    );


  }
  

  // @SneakyThrows
  @Override
  @SuppressWarnings({"unused"})
  public void onDisable() {
    try {
      unregisterEnchantments();
      writer.close();
    } catch (final NoSuchFieldException | IllegalAccessException | IOException e) {
      e.printStackTrace();
    }
    
    if (isEnabled()) Bukkit.getPluginManager().disablePlugin(this);
  }

  @SuppressWarnings("unchecked")
  private void unregisterEnchantments() throws NoSuchFieldException, IllegalAccessException {
    final Field keyField = Enchantment.class.getDeclaredField("byKey");
    keyField.setAccessible(true);

    final HashMap<NamespacedKey, Enchantment> byKey = (HashMap<NamespacedKey, Enchantment>) keyField.get(null);
    for (final Enchantment enchantment : allEnchs) byKey.remove(enchantment.getKey());

    final Field nameField = Enchantment.class.getDeclaredField("byName");
    nameField.setAccessible(true);

    final HashMap<String, Enchantment> byName = (HashMap<String, Enchantment>) nameField.get(null);
    for (final Enchantment enchantment : allEnchs) byName.remove(enchantment.getName());
  }
  
  public void reloadEnchantments() throws NoSuchFieldException, IllegalAccessException {
    staticEnchantmentData.clear();
    enchantmentData.clear();
    unregisterEnchantments();

    final List<Enchantment> vanillaEnchants = Arrays.stream(Enchantment.values()).toList();
    vanillaEnchants.forEach(enchantment -> {
      DetailedEnchantment ench = new DetailedEnchantment(enchantment.getKey());
      enchantmentData.put(ench, new AbstractEnchantment(enchantment));
    });

    staticEnchantmentData.putAll(enchantmentData);

    final File file0 = new File(this.getDataFolder().getPath() + File.separator + "enchantments");
    if (!file0.exists()) file0.mkdirs();

    final ArrayList<File> files = new ArrayList<>(FileUtils.listFiles(file0, new String[] {"yml", "yaml"}, true));
    for (File file : files)
      Register.loadEnchantment(file, this);

    bowEnchantments.clear();
    toolEnchantments.clear();
    bootsEnchantments.clear();
    helmetEnchantments.clear();
    leggingsEnchantments.clear();
    chestplateEnchantments.clear();
    weaponEnchantments.clear();
  }


  // IMPLEMENTATION START

  private ItemStack enchant0(ItemStack it, Enchantment en, int lvl) {
    return Utils.enchant(it, en, lvl, this).getKey();
  }

  /**
   * Disenchants an ItemStack - removes an enchantment of this name
   *
   * @param item the item to disenchant
   * @param name the name of the enchantment
   * @return the disenchanted item
   * @throws IllegalArgumentException if the enchantment wasn't found or if the item doesn't have the given enchantment
   */
  @Override
  public ItemStack disenchant(ItemStack item, String name) throws IllegalArgumentException {
    final DetailedEnchantment enchantment = Utils.parseEnchantment(name, -1, true, this);
    if (enchantment == null || enchantment.equals(WRONG_LEVEL) || enchantment.equals(WRONG_NAME)) {
      throw new IllegalArgumentException(String.format("The enchantment wasn't found! Name: %s", name));
    }

    item = disenchant(item, enchantment.getEnchantment());
    return item;
  }

  /**
   * Disenchants an ItemStack - removes an enchantment, found by this namespaced key
   *
   * @param item the item to disenchant
   * @param key  the namespaced key of the enchantment
   * @return the disenchanted item
   * @throws IllegalArgumentException if the enchantment wasn't found or if the item doesn't have the given enchantment
   */
  @Override
  public ItemStack disenchant(ItemStack item, NamespacedKey key) throws IllegalArgumentException {
    DetailedEnchantment ench = new DetailedEnchantment(key);
    if (ench.getEnchantment() == null) {
      throw new IllegalArgumentException(String.format("The enchantment wasn't found! Key: %s", key.getKey()));
    }

    item = disenchant(item, ench.getEnchantment());
    return item;
  }

  /**
   * Disenchants an ItemStack - removes a given enchantment
   *
   * @param item the item to disenchant
   * @param ench the enchantment
   * @return the disenchanted item
   * @throws IllegalArgumentException if the item doesn't have the given enchantment
   */
  @Override
  public ItemStack disenchant(ItemStack item, Enchantment ench) throws IllegalArgumentException {
    if (item == null || item.getType() == Material.AIR) return item;

    final ItemMeta meta = item.getItemMeta();

    List<String> lore = new ArrayList<>();
    if (meta.getLore() != null) lore = meta.getLore();

    Set<Enchantment> enchantments = meta.getEnchants().keySet();
    if (enchantments.isEmpty()) return item;
    if (!enchantments.contains(ench))
      throw new IllegalArgumentException("The item doesn't have the marked-for-removal enchantment!");

    meta.removeEnchant(ench);
    lore.removeIf(s -> s.toLowerCase().contains(Utils.getName(ench).toLowerCase()));

    meta.setLore(lore);
    item.setItemMeta(meta);

    return item;
  }

  /**
   * Fully disenchants an ItemStack
   *
   * @param item the item to disenchant
   * @return the disenchanted item
   */
  @Override
  public ItemStack fullyDisenchant(ItemStack item) {
    for (final Enchantment ench : item.getEnchantments().keySet()) {
      item = disenchant(item, ench);
    }

    return item;
  }

  @Override
  public ItemStack enchant(ItemStack itemStack, String name, int level) throws IllegalArgumentException {
    final DetailedEnchantment enchantment = Utils.parseEnchantment(name, level, false, this);
    if (enchantment == null || enchantment.equals(WRONG_LEVEL) || enchantment.equals(WRONG_NAME)) {
      throw new IllegalArgumentException(String.format("The enchantment wasn't found or the level is invalid! Name: %s, level: %d", name, level));
    }

    return enchant0(itemStack, enchantment.getEnchantment(), level);
  }

  @Override
  public ItemStack enchant(ItemStack itemStack, NamespacedKey namespacedKey, int level) throws IllegalArgumentException {
    final DetailedEnchantment ench = new DetailedEnchantment(namespacedKey);
    if (ench.getEnchantment() == null) {
      throw new IllegalArgumentException(String.format("The enchantment wasn't found! Key: %s", namespacedKey.getKey()));
    }

    if (level < ench.getEnchantment().getStartLevel() || level > ench.getEnchantment().getMaxLevel()) {
      throw new IllegalArgumentException(String.format("The enchantment's level is invalid! Key: %s", namespacedKey.getKey()));
    }

    return enchant0(itemStack, ench.getEnchantment(), level);
  }

  @Override
  public ItemStack enchant(ItemStack itemStack, Enchantment enchantment, int level) throws IllegalArgumentException {
    if (level < enchantment.getStartLevel() || level > enchantment.getMaxLevel()) {
      throw new IllegalArgumentException(String.format("The enchantment's level is invalid! Name: %s", enchantment.getName()));
    }

    return enchant0(itemStack, enchantment, level);
  }

  @Override
  public void enchant(Player player, EquipmentSlot equipmentSlot, String name, int level) throws IllegalArgumentException {
    ItemStack itemStack = player.getInventory().getItem(equipmentSlot);
    itemStack = enchant(itemStack, name, level);
    if (itemStack != null) {
      player.getInventory().setItem(equipmentSlot, itemStack);
    }
  }

  @Override
  public void enchant(Player player, EquipmentSlot equipmentSlot, NamespacedKey namespacedKey, int level) throws IllegalArgumentException {
    ItemStack itemStack = player.getInventory().getItem(equipmentSlot);
    itemStack = enchant(itemStack, namespacedKey, level);
    if (itemStack != null) {
      player.getInventory().setItem(equipmentSlot, itemStack);
    }
  }

  @Override
  public void enchant(Player player, EquipmentSlot equipmentSlot, Enchantment enchantment, int level) throws IllegalArgumentException {
    ItemStack itemStack = player.getInventory().getItem(equipmentSlot);
    itemStack = enchant(itemStack, enchantment, level);
    if (itemStack != null) {
      player.getInventory().setItem(equipmentSlot, itemStack);
    }
  }


  @Override
  public ItemStack enchantUnrestricted(ItemStack itemStack, String name, int level) throws IllegalArgumentException {
    final DetailedEnchantment enchantment = Utils.parseEnchantment(name, level, true, this);
    if (enchantment.equals(WRONG_LEVEL) || enchantment.equals(WRONG_NAME)) {
      throw new IllegalArgumentException(String.format("The enchantment wasn't found or the level is invalid! Name: %s, level: %d", name, level));
    }

    return enchant0(itemStack, enchantment.getEnchantment(), level);
  }

  @Override
  public ItemStack enchantUnrestricted(ItemStack itemStack, NamespacedKey namespacedKey, int level) throws IllegalArgumentException {
    final DetailedEnchantment ench = new DetailedEnchantment(namespacedKey);
    if (ench.getEnchantment() == null) {
      throw new IllegalArgumentException(String.format("The enchantment wasn't found! Key: %s", namespacedKey.getKey()));
    }

    return enchant0(itemStack, ench.getEnchantment(), level);
  }

  @Override
  public ItemStack enchantUnrestricted(ItemStack itemStack, Enchantment enchantment, int level) {
    return enchant0(itemStack, enchantment, level);
  }

  @Override
  public void enchantUnrestricted(Player player, EquipmentSlot equipmentSlot, String name, int level) throws IllegalArgumentException {
    ItemStack itemStack = player.getInventory().getItem(equipmentSlot);
    itemStack = enchantUnrestricted(itemStack, name, level);
    if (itemStack != null) {
      player.getInventory().setItem(equipmentSlot, itemStack);
    }
  }

  @Override
  public void enchantUnrestricted(Player player, EquipmentSlot equipmentSlot, NamespacedKey namespacedKey, int level) throws IllegalArgumentException {
    ItemStack itemStack = player.getInventory().getItem(equipmentSlot);
    itemStack = enchantUnrestricted(itemStack, namespacedKey, level);
    if (itemStack != null) {
      player.getInventory().setItem(equipmentSlot, itemStack);
    }
  }

  @Override
  public void enchantUnrestricted(Player player, EquipmentSlot equipmentSlot, Enchantment enchantment, int level) {
    ItemStack itemStack = player.getInventory().getItem(equipmentSlot);
    itemStack = enchantUnrestricted(itemStack, enchantment, level);
    if (itemStack != null) {
      player.getInventory().setItem(equipmentSlot, itemStack);
    }
  }

  @Override
  public EnchantmentLoadResponse loadEnchantment(String enchantmentName) {
    final File file = new File(getDataFolder().getPath() + File.separator + "enchantments" + File.separator +
      enchantmentName.replace("/", File.separator).replace("\\", File.separator)
    );
    if (!file.exists()) return EnchantmentLoadResponse.NOT_FOUND;

    if (isEnchantmentLoaded(file, this)) {
      Register.unloadEnchantment(file, this);
      Register.loadEnchantment(file, this);
      return EnchantmentLoadResponse.RELOADED;
    }

    Register.loadEnchantment(file, this);
    return EnchantmentLoadResponse.LOADED;
  }

  @Override
  public EnchantmentUnloadResponse unloadEnchantment(String enchantmentName) {
    final File file = new File(getDataFolder().getPath() + File.separator + "enchantments" + File.separator +
      enchantmentName.replace("/", File.separator).replace("\\", File.separator)
    );
    if (!file.exists()) return EnchantmentUnloadResponse.NOT_FOUND;

    if (isEnchantmentLoaded(file, this)) {
      Register.unloadEnchantment(file, this);
      return EnchantmentUnloadResponse.UNLOADED;
    }

    return EnchantmentUnloadResponse.NOT_LOADED;
  }

  // IMPLEMENTATION END

  // MockBukkit start
  public UnderscoreEnchants() {
    super();
  }

  protected UnderscoreEnchants(final JavaPluginLoader loader, final PluginDescriptionFile description, final File dataFolder, final File file) {
    super(loader, description, dataFolder, file);
  }
  // MockBukkit end








  @Override
  public @NotNull String toString() {
    return "UnderscoreEnchants{" +
      "messages=" + messages +
      ", positiveEffects=" + positiveEffects +
      ", negativeEffects=" + negativeEffects +
      ", debugger=" + debugger +
      ", actionbars=" + actionbars +
      ", cooldowns=" + cooldowns +
      ", allEnchs=" + allEnchs +
      ", enchantmentData=" + enchantmentData +
      ", gods=" + gods +
      ", plankRepariable=" + plankRepariable +
      ", leatherRepariable=" + leatherRepariable +
      ", cobbleRepariable=" + cobbleRepariable +
      ", cobbleTypes=" + cobbleTypes +
      ", ironRepariable=" + ironRepariable +
      ", goldRepariable=" + goldRepariable +
      ", diamondRepariable=" + diamondRepariable +
      ", netheriteRepariable=" + netheriteRepariable +
      ", serverVersion='" + serverVersion + '\'' +
      ", checker=" + checker +
      ", metrics=" + metrics +
      ", mainConfig=" + mainConfig +
      '}';
  }
}
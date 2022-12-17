package com.roughlyunderscore.enchs;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.InvalidCommandArgument;
import com.codingforcookies.armorequip.ArmorListener;
import com.codingforcookies.armorequip.DispenserArmorListener;
import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XPotion;
import com.roughlyunderscore.enchantsapi.UEnchantsAPI;
import com.roughlyunderscore.enchs.commands.UnderscoreEnchantsCommand;
import com.roughlyunderscore.enchs.enchants.abstracts.AbstractEnchantment;
import com.roughlyunderscore.enchs.events.GeneralListener;
import com.roughlyunderscore.enchs.gui.AnvilHandler;
import com.roughlyunderscore.enchs.gui.EnchantTableHandler;
import com.roughlyunderscore.enchs.listeners.InteractListener;
import com.roughlyunderscore.enchs.listeners.JoinListener;
import com.roughlyunderscore.enchs.listeners.LeaveListener;
import com.roughlyunderscore.enchs.listeners.LootPopulateListener;
import com.roughlyunderscore.enchs.util.Debug;
import com.roughlyunderscore.enchs.util.cooldownutils.ActionbarCooldown;
import com.roughlyunderscore.enchs.util.cooldownutils.Cooldown;
import com.roughlyunderscore.enchs.util.data.DetailedEnchantment;
import com.roughlyunderscore.enchs.util.data.Messages;
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

public class UnderscoreEnchants extends JavaPlugin implements UEnchantsAPI {

  @Override
  public String toString() {
    throw new UnsupportedOperationException("This method should not have been called on UnderscoreEnchants.");
  }


  // The main class.
  // A lot of stuff in a confined place, don't trip over something!

  private UnderscoreEnchants instance;
  @Getter
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

  public static FileConfiguration staticConfig;

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
  final int id = 97002;
  @Getter
  final int metricsId = 12413;
  @Getter
  @Setter
  UpdateChecker checker;
  @Getter
  @Setter
  Metrics metrics;

  @Override
  public void onEnable() {
    //<editor-fold desc="Last initializations.">
    WRONG_LEVEL = new DetailedEnchantment("WRONG_LEVEL");
    WRONG_NAME = new DetailedEnchantment("WRONG_NAME");

    List<Enchantment> vanillaEnchants = Arrays.stream(Enchantment.values()).toList();
    vanillaEnchants.forEach(enchantment -> {
      DetailedEnchantment ench = new DetailedEnchantment(enchantment.getKey());
      enchantmentData.put(ench, new AbstractEnchantment(enchantment));
    });

    staticEnchantmentData.putAll(enchantmentData);

    instance = this;
    messages = new Messages(this);
    staticConfig = getConfig();

    saveDefaultConfig();
    FileConfiguration config = this.getConfig();
    config.options().copyDefaults(true);
    saveDefaultConfig();
    reloadConfig();

    //</editor-fold>

    try {
      regTest();
    } catch (
      IllegalStateException why_the_hell_do_you_run_a_plugin_that_explicitly_says_1_17_on_its_page_on_a_server_that_is_under_1_13_question_mark) {
      onDisable();
      return;
      // this is not a malicious easter egg, I'm just straight up saving my sanity because people actually tried running it on 1.8.
    }

    //<editor-fold desc="Debug mode initialization"
    String directPath = this.getDataFolder().getPath() + File.separator + "debug";
    String debugFile = System.currentTimeMillis() + ".debug";

    File directPathFile = new File(directPath);
    if (!directPathFile.exists()) {
      directPathFile.mkdirs();
    }
    File filee = new File(directPath + File.separator + debugFile);
    if (!filee.exists()) {
      try { // TODO remove
        filee.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    FileWriter fileW;
    try { // TODO remove
      fileW = new FileWriter(filee.getAbsoluteFile());
    } catch (IOException e) {
      e.printStackTrace();
      return;
    }
    writer = new BufferedWriter(fileW);
    debugger = new Debug(getConfig().getBoolean("debugMode"), writer, this);
    //</editor-fold>
    //<editor-fold desc="UpdateChecker initialization.">
    checker = UpdateChecker.init(this, id)
      .setDownloadLink(id)
      .setDonationLink("https://donationalerts.com/r/zbll")
      .onFail((senders, ex) -> {
        this.getUnderscoreLogger().severe("Could not check for updates, make sure your connection is stable!");
        this.getUnderscoreLogger().severe(ex.getMessage());
      })
      .onSuccess((senders, ex) -> {
        this.getUnderscoreLogger().finest("Thanks for using UnderscoreEnchants!");
        this.getUnderscoreLogger().finest("Successfully checked for updates.");
      })
      .checkEveryXHours(getConfig().getInt("updater"))
      .setNotifyOpsOnJoin(true)
      .checkNow();
    //</editor-fold>
    //<editor-fold desc="bStats initialization if is enabled.">
    if (getConfig().getBoolean("bStats")) {
      metrics = new Metrics(this, metricsId);

      metrics.addCustomChart(new SimplePie(
        "language",
        () -> getConfig().getString("lang")
      ));

      metrics.addCustomChart(new SimplePie(
        "enchantments_count",
        () -> String.valueOf(allEnchs.size())
      ));
    }
    //</editor-fold>

    this.init();

    //<editor-fold desc="Enchantments">
    File file0 = new File(this.getDataFolder().getPath() + File.separator + "enchantments");
    if (!file0.exists()) file0.mkdirs();

    ArrayList<File> files = new ArrayList<>(FileUtils.listFiles(file0, new String[] {"yml", "yaml"}, true));
    for (File file : files)
      loadEnchantment(file, this);

    //</editor-fold>

    //<editor-fold desc="Commands initialization.">
    BukkitCommandManager commandManager = new BukkitCommandManager(this);
    commandManager.getCommandConditions().addCondition("cant_console", (sender) -> {
      if (!sender.getIssuer().isPlayer()) {
        sender.getIssuer().sendMessage(messages.NO_CONSOLE);
        throw new ConditionFailedException("This command cannot be executed by console.");
      }
    });

    commandManager.getCommandCompletions().registerCompletion("enchant-name-completion", context -> {
      List<String> enchants = new ArrayList<>();
      for (DetailedEnchantment ench : enchantmentData.keySet()) {
        enchants.add(ench.getCommandName());
      }
      return enchants;
    });

    commandManager.getCommandCompletions().registerCompletion("yes-no-completion", context -> Arrays.asList("yes", "no"));

    commandManager.getCommandContexts().registerContext(DetailedEnchantment.class, context -> {
      String enchName = context.popFirstArg();
      DetailedEnchantment ench = Utils.parseEnchantment(enchName, this);
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
      DetailedEnchantment enchantment = context.getContextValue(DetailedEnchantment.class);
      if (enchantment == null || enchantment.getEnchantment() == null) {
        throw new InvalidCommandArgument("Enchantment does not exist!", false);
      }
      return IntStream
        .range(enchantment.getEnchantment().getStartLevel(), enchantment.getEnchantment().getMaxLevel() + 1)
        .boxed()
        .map(String::valueOf)
        .collect(Collectors.toList());
    });
    //</editor-fold>
    //<editor-fold desc="Listeners initialization.">
    PluginManager manager = getServer().getPluginManager();
    manager.registerEvents(new InteractListener(this), this);

    manager.registerEvents(new AnvilHandler(this), this);
    manager.registerEvents(new EnchantTableHandler(this), this);

    manager.registerEvents(new LootPopulateListener(this), this);

    manager.registerEvents(new ArmorListener(), this);
    manager.registerEvents(new DispenserArmorListener(), this);

    manager.registerEvents(new GeneralListener(), this);

    manager.registerEvents(new JoinListener(), this);
    manager.registerEvents(new LeaveListener(this), this);

    //</editor-fold>

    //<editor-fold desc="Economy setup.">
    if (!setupEconomy()) {
      this.getUnderscoreLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
      getServer().getPluginManager().disablePlugin(this);
      return;
    }
    //</editor-fold>

    new BukkitRunnable() {
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

  //<editor-fold desc="Initialization and setup methods.">
  private boolean setupEconomy() {
    if (!getConfig().getBoolean("require-vault")) return true;
    if (getServer().getPluginManager().getPlugin("Vault") == null) {
      return false;
    }
    RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
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
  //</editor-fold>

  // @SneakyThrows
  @Override
  @SuppressWarnings({"unchecked", "unused"})
  public void onDisable() {

    // The following block of code, starting after this string and ending on the second enhanced-for loop, is not made by me.
    // I took it from a custom enchantment tutorial, because I couldn't figure out how to unregister enchantments upon disabling the plugin.
    // However, I made sure that I now understand what this code does and can rewrite it by myself if it ever will be necessary.

    try {
      Field keyField = Enchantment.class.getDeclaredField("byKey");
      keyField.setAccessible(true);

      HashMap<NamespacedKey, Enchantment> byKey = (HashMap<NamespacedKey, Enchantment>) keyField.get(null);
      for (Enchantment enchantment : allEnchs) byKey.remove(enchantment.getKey());

      Field nameField = Enchantment.class.getDeclaredField("byName");
      nameField.setAccessible(true);

      HashMap<String, Enchantment> byName = (HashMap<String, Enchantment>) nameField.get(null);
      for (Enchantment enchantment : allEnchs) byName.remove(enchantment.getName());


      writer.close();
    } catch (NoSuchFieldException | IllegalAccessException | IOException e) {
      e.printStackTrace();
    }
    if (isEnabled()) Bukkit.getPluginManager().disablePlugin(this);
  }


  // IMPLEMENTATION START

  private ItemStack enchant0(ItemStack it, Enchantment en, int lvl) {
    return Utils.enchant(it, en, lvl).getKey();
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
    DetailedEnchantment enchantment = Utils.parseEnchantment(name, -1, true, this);
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

    ItemMeta meta = item.getItemMeta();

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
    for (Enchantment ench : item.getEnchantments().keySet()) {
      item = disenchant(item, ench);
    }

    return item;
  }

  @Override
  public ItemStack enchant(ItemStack itemStack, String name, int level) throws IllegalArgumentException {
    DetailedEnchantment enchantment = Utils.parseEnchantment(name, level, false, this);
    if (enchantment == null || enchantment.equals(WRONG_LEVEL) || enchantment.equals(WRONG_NAME)) {
      throw new IllegalArgumentException(String.format("The enchantment wasn't found or the level is invalid! Name: %s, level: %d", name, level));
    }

    return enchant0(itemStack, enchantment.getEnchantment(), level);
  }

  @Override
  public ItemStack enchant(ItemStack itemStack, NamespacedKey namespacedKey, int level) throws IllegalArgumentException {
    DetailedEnchantment ench = new DetailedEnchantment(namespacedKey);
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
    DetailedEnchantment enchantment = Utils.parseEnchantment(name, level, true, this);
    if (enchantment.equals(WRONG_LEVEL) || enchantment.equals(WRONG_NAME)) {
      throw new IllegalArgumentException(String.format("The enchantment wasn't found or the level is invalid! Name: %s, level: %d", name, level));
    }

    return enchant0(itemStack, enchantment.getEnchantment(), level);
  }

  @Override
  public ItemStack enchantUnrestricted(ItemStack itemStack, NamespacedKey namespacedKey, int level) throws IllegalArgumentException {
    DetailedEnchantment ench = new DetailedEnchantment(namespacedKey);
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


  // IMPLEMENTATION END

  // MockBukkit start
  public UnderscoreEnchants() {
    super();
  }

  protected UnderscoreEnchants(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
    super(loader, description, dataFolder, file);
  }
  // MockBukkit end


  // excuse me https://github.com/skjorrface/animals.txt/blob/master/animals.txt

  public static String[] animals = new String[] {
    "Aardvark",
    "Abyssinian",
    "Adelie Penguin",
    "Affenpinscher",
    "Afghan Hound",
    "African Bush Elephant",
    "African Civet",
    "African Clawed Frog",
    "African Forest Elephant",
    "African Palm Civet",
    "African Penguin",
    "African Tree Toad",
    "African Wild Dog",
    "Ainu Dog",
    "Airedale Terrier",
    "Akbash",
    "Akita",
    "Alaskan Malamute",
    "Albatross",
    "Aldabra Giant Tortoise",
    "Alligator",
    "Alpine Dachsbracke",
    "American Bulldog",
    "American Cocker Spaniel",
    "American Coonhound",
    "American Eskimo Dog",
    "American Foxhound",
    "American Pit Bull Terrier",
    "American Staffordshire Terrier",
    "American Water Spaniel",
    "Anatolian Shepherd Dog",
    "Angelfish",
    "Ant",
    "Anteater",
    "Antelope",
    "Appenzeller Dog",
    "Arctic Fox",
    "Arctic Hare",
    "Arctic Wolf",
    "Armadillo",
    "Asian Elephant",
    "Asian Giant Hornet",
    "Asian Palm Civet",
    "Asiatic Black Bear",
    "Australian Cattle Dog",
    "Australian Kelpie Dog",
    "Australian Mist",
    "Australian Shepherd",
    "Australian Terrier",
    "Avocet",
    "Axolotl",
    "Aye Aye",
    "Baboon",
    "Bactrian Camel",
    "Badger",
    "Balinese",
    "Banded Palm Civet",
    "Bandicoot",
    "Barb",
    "Barn Owl",
    "Barnacle",
    "Barracuda",
    "Basenji Dog",
    "Basking Shark",
    "Basset Hound",
    "Bat",
    "Bavarian Mountain Hound",
    "Beagle",
    "Bear",
    "Bearded Collie",
    "Bearded Dragon",
    "Beaver",
    "Bedlington Terrier",
    "Beetle",
    "Bengal Tiger",
    "Bernese Mountain Dog",
    "Bichon Frise",
    "Binturong",
    "Bird",
    "Birds Of Paradise",
    "Birman",
    "Bison",
    "Black Bear",
    "Black Rhinoceros",
    "Black Russian Terrier",
    "Black Widow Spider",
    "Bloodhound",
    "Blue Lacy Dog",
    "Blue Whale",
    "Bluetick Coonhound",
    "Bobcat",
    "Bolognese Dog",
    "Bombay",
    "Bongo",
    "Bonobo",
    "Booby",
    "Border Collie",
    "Border Terrier",
    "Bornean Orang-utan",
    "Borneo Elephant",
    "Boston Terrier",
    "Bottle Nosed Dolphin",
    "Boxer Dog",
    "Boykin Spaniel",
    "Brazilian Terrier",
    "Brown Bear",
    "Budgerigar",
    "Buffalo",
    "Bull Mastiff",
    "Bull Shark",
    "Bull Terrier",
    "Bulldog",
    "Bullfrog",
    "Bumble Bee",
    "Burmese",
    "Burrowing Frog",
    "Butterfly",
    "Butterfly Fish",
    "Caiman",
    "Caiman Lizard",
    "Cairn Terrier",
    "Camel",
    "Canaan Dog",
    "Capybara",
    "Caracal",
    "Carolina Dog",
    "Cassowary",
    "Cat",
    "Caterpillar",
    "Catfish",
    "Cavalier King Charles Spaniel",
    "Centipede",
    "Cesky Fousek",
    "Chameleon",
    "Chamois",
    "Cheetah",
    "Chesapeake Bay Retriever",
    "Chicken",
    "Chihuahua",
    "Chimpanzee",
    "Chinchilla",
    "Chinese Crested Dog",
    "Chinook",
    "Chinstrap Penguin",
    "Chipmunk",
    "Chow Chow",
    "Cichlid",
    "Clouded Leopard",
    "Clown Fish",
    "Clumber Spaniel",
    "Coati",
    "Cockroach",
    "Collared Peccary",
    "Collie",
    "Common Buzzard",
    "Common Frog",
    "Common Loon",
    "Common Toad",
    "Coral",
    "Cottontop Tamarin",
    "Cougar",
    "Cow",
    "Coyote",
    "Crab",
    "Crab-Eating Macaque",
    "Crane",
    "Crested Penguin",
    "Crocodile",
    "Cross River Gorilla",
    "Curly Coated Retriever",
    "Cuscus",
    "Cuttlefish",
    "Dachshund",
    "Dalmatian",
    "Darwin's Frog",
    "Deer",
    "Desert Tortoise",
    "Deutsche Bracke",
    "Dhole",
    "Dingo",
    "Discus",
    "Doberman Pinscher",
    "Dodo",
    "Dog",
    "Dogo Argentino",
    "Dogue De Bordeaux",
    "Dolphin",
    "Donkey",
    "Dormouse",
    "Dragonfly",
    "Drever",
    "Duck",
    "Dugong",
    "Dunker",
    "Dusky Dolphin",
    "Dwarf Crocodile",
    "Eagle",
    "Earwig",
    "Eastern Gorilla",
    "Eastern Lowland Gorilla",
    "Echidna",
    "Edible Frog",
    "Egyptian Mau",
    "Electric Eel",
    "Elephant",
    "Elephant Seal",
    "Elephant Shrew",
    "Emperor Penguin",
    "Emperor Tamarin",
    "Emu",
    "English Cocker Spaniel",
    "English Shepherd",
    "English Springer Spaniel",
    "Entlebucher Mountain Dog",
    "Epagneul Pont Audemer",
    "Eskimo Dog",
    "Estrela Mountain Dog",
    "Falcon",
    "Fennec Fox",
    "Ferret",
    "Field Spaniel",
    "Fin Whale",
    "Finnish Spitz",
    "Fire-Bellied Toad",
    "Fish",
    "Fishing Cat",
    "Flamingo",
    "Flat Coat Retriever",
    "Flounder",
    "Fly",
    "Flying Squirrel",
    "Fossa",
    "Fox",
    "Fox Terrier",
    "French Bulldog",
    "Frigatebird",
    "Frilled Lizard",
    "Frog",
    "Fur Seal",
    "Galapagos Penguin",
    "Galapagos Tortoise",
    "Gar",
    "Gecko",
    "Gentoo Penguin",
    "Geoffroys Tamarin",
    "Gerbil",
    "German Pinscher",
    "German Shepherd",
    "Gharial",
    "Giant African Land Snail",
    "Giant Clam",
    "Giant Panda Bear",
    "Giant Schnauzer",
    "Gibbon",
    "Gila Monster",
    "Giraffe",
    "Glass Lizard",
    "Glow Worm",
    "Goat",
    "Golden Lion Tamarin",
    "Golden Oriole",
    "Golden Retriever",
    "Goose",
    "Gopher",
    "Gorilla",
    "Grasshopper",
    "Great Dane",
    "Great White Shark",
    "Greater Swiss Mountain Dog",
    "Green Bee-Eater",
    "Greenland Dog",
    "Grey Mouse Lemur",
    "Grey Reef Shark",
    "Grey Seal",
    "Greyhound",
    "Grizzly Bear",
    "Grouse",
    "Guinea Fowl",
    "Guinea Pig",
    "Guppy",
    "Hammerhead Shark",
    "Hamster",
    "Hare",
    "Harrier",
    "Havanese",
    "Hedgehog",
    "Hercules Beetle",
    "Hermit Crab",
    "Heron",
    "Highland Cattle",
    "Himalayan",
    "Hippopotamus",
    "Honey Bee",
    "Horn Shark",
    "Horned Frog",
    "Horse",
    "Horseshoe Crab",
    "Howler Monkey",
    "Human",
    "Humboldt Penguin",
    "Hummingbird",
    "Humpback Whale",
    "Hyena",
    "Ibis",
    "Ibizan Hound",
    "Iguana",
    "Impala",
    "Indian Elephant",
    "Indian Palm Squirrel",
    "Indian Rhinoceros",
    "Indian Star Tortoise",
    "Indochinese Tiger",
    "Indri",
    "Insect",
    "Irish Setter",
    "Irish WolfHound",
    "Jack Russel",
    "Jackal",
    "Jaguar",
    "Japanese Chin",
    "Japanese Macaque",
    "Javan Rhinoceros",
    "Javanese",
    "Jellyfish",
    "Kakapo",
    "Kangaroo",
    "Keel Billed Toucan",
    "Killer Whale",
    "King Crab",
    "King Penguin",
    "Kingfisher",
    "Kiwi",
    "Koala",
    "Komodo Dragon",
    "Kudu",
    "Labradoodle",
    "Labrador Retriever",
    "Ladybird",
    "Leaf-Tailed Gecko",
    "Lemming",
    "Lemur",
    "Leopard",
    "Leopard Cat",
    "Leopard Seal",
    "Leopard Tortoise",
    "Liger",
    "Lion",
    "Lionfish",
    "Little Penguin",
    "Lizard",
    "Llama",
    "Lobster",
    "Long-Eared Owl",
    "Lynx",
    "Macaroni Penguin",
    "Macaw",
    "Magellanic Penguin",
    "Magpie",
    "Maine Coon",
    "Malayan Civet",
    "Malayan Tiger",
    "Maltese",
    "Manatee",
    "Mandrill",
    "Manta Ray",
    "Marine Toad",
    "Markhor",
    "Marsh Frog",
    "Masked Palm Civet",
    "Mastiff",
    "Mayfly",
    "Meerkat",
    "Millipede",
    "Minke Whale",
    "Mole",
    "Molly",
    "Mongoose",
    "Mongrel",
    "Monitor Lizard",
    "Monkey",
    "Monte Iberia Eleuth",
    "Moorhen",
    "Moose",
    "Moray Eel",
    "Moth",
    "Mountain Gorilla",
    "Mountain Lion",
    "Mouse",
    "Mule",
    "Neanderthal",
    "Neapolitan Mastiff",
    "Newfoundland",
    "Newt",
    "Nightingale",
    "Norfolk Terrier",
    "Norwegian Forest",
    "Numbat",
    "Nurse Shark",
    "Ocelot",
    "Octopus",
    "Okapi",
    "Old English Sheepdog",
    "Olm",
    "Opossum",
    "Orang-utan",
    "Ostrich",
    "Otter",
    "Oyster",
    "Pademelon",
    "Panther",
    "Parrot",
    "Patas Monkey",
    "Peacock",
    "Pekingese",
    "Pelican",
    "Penguin",
    "Persian",
    "Pheasant",
    "Pied Tamarin",
    "Pig",
    "Pika",
    "Pike",
    "Pink Fairy Armadillo",
    "Piranha",
    "Platypus",
    "Pointer",
    "Poison Dart Frog",
    "Polar Bear",
    "Pond Skater",
    "Poodle",
    "Pool Frog",
    "Porcupine",
    "Possum",
    "Prawn",
    "Proboscis Monkey",
    "Puffer Fish",
    "Puffin",
    "Pug",
    "Puma",
    "Purple Emperor",
    "Puss Moth",
    "Pygmy Hippopotamus",
    "Pygmy Marmoset",
    "Quail",
    "Quetzal",
    "Quokka",
    "Quoll",
    "Rabbit",
    "Raccoon",
    "Raccoon Dog",
    "Radiated Tortoise",
    "Ragdoll",
    "Rat",
    "Rattlesnake",
    "Red Knee Tarantula",
    "Red Panda",
    "Red Wolf",
    "Red-handed Tamarin",
    "Reindeer",
    "Rhinoceros",
    "River Dolphin",
    "River Turtle",
    "Robin",
    "Rock Hyrax",
    "Rockhopper Penguin",
    "Roseate Spoonbill",
    "Rottweiler",
    "Royal Penguin",
    "Russian Blue",
    "Sabre-Toothed Tiger",
    "Saint Bernard",
    "Salamander",
    "Sand Lizard",
    "Saola",
    "Scorpion",
    "Scorpion Fish",
    "Sea Dragon",
    "Sea Lion",
    "Sea Otter",
    "Sea Slug",
    "Sea Squirt",
    "Sea Turtle",
    "Sea Urchin",
    "Seahorse",
    "Seal",
    "Serval",
    "Sheep",
    "Shih Tzu",
    "Shrimp",
    "Siamese",
    "Siamese Fighting Fish",
    "Siberian",
    "Siberian Husky",
    "Siberian Tiger",
    "Silver Dollar",
    "Skunk",
    "Sloth",
    "Slow Worm",
    "Snail",
    "Snake",
    "Snapping Turtle",
    "Snowshoe",
    "Snowy Owl",
    "Somali",
    "South China Tiger",
    "Spadefoot Toad",
    "Sparrow",
    "Spectacled Bear",
    "Sperm Whale",
    "Spider Monkey",
    "Spiny Dogfish",
    "Sponge",
    "Squid",
    "Squirrel",
    "Squirrel Monkey",
    "Sri Lankan Elephant",
    "Staffordshire Bull Terrier",
    "Stag Beetle",
    "Starfish",
    "Stellers Sea Cow",
    "Stick Insect",
    "Stingray",
    "Stoat",
    "Striped Rocket Frog",
    "Sumatran Elephant",
    "Sumatran Orang-utan",
    "Sumatran Rhinoceros",
    "Sumatran Tiger",
    "Sun Bear",
    "Swan",
    "Tang",
    "Tapanuli Orang-utan",
    "Tapir",
    "Tarsier",
    "Tasmanian Devil",
    "Tawny Owl",
    "Termite",
    "Tetra",
    "Thorny Devil",
    "Tibetan Mastiff",
    "Tiffany",
    "Tiger",
    "Tiger Salamander",
    "Tiger Shark",
    "Tortoise",
    "Toucan",
    "Tree Frog",
    "Tropicbird",
    "Tuatara",
    "Turkey",
    "Turkish Angora",
    "Uakari",
    "Uguisu",
    "Umbrellabird",
    "Vampire Bat",
    "Vervet Monkey",
    "Vulture",
    "Wallaby",
    "Walrus",
    "Warthog",
    "Wasp",
    "Water Buffalo",
    "Water Dragon",
    "Water Vole",
    "Weasel",
    "Welsh Corgi",
    "West Highland Terrier",
    "Western Gorilla",
    "Western Lowland Gorilla",
    "Whale Shark",
    "Whippet",
    "White Faced Capuchin",
    "White Rhinoceros",
    "White Tiger",
    "Wild Boar",
    "Wildebeest",
    "Wolf",
    "Wolverine",
    "Wombat",
    "Woodlouse",
    "Woodpecker",
    "Woolly Mammoth",
    "Woolly Monkey",
    "Wrasse",
    "X-Ray Tetra",
    "Yak",
    "Yellow-Eyed Penguin",
    "Yorkshire Terrier",
    "Zebra",
    "Zebra Shark",
    "Zebu",
    "Zonkey",
    "Zorse"
  };
}
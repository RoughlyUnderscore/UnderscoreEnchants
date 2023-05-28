package com.roughlyunderscore.enchs.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.roughlyunderscore.enchs.UnderscoreEnchants;
import com.roughlyunderscore.enchs.config.main.MainConfigValues;
import com.roughlyunderscore.enchs.config.messages.Messages;
import com.roughlyunderscore.enchs.easter_eggs.EasterEggs;
import com.roughlyunderscore.enchs.parsers.PreparatoryParsers;
import com.roughlyunderscore.enchs.util.DetailedEnchantment;
import com.roughlyunderscore.enchs.util.Permissions;
import com.roughlyunderscore.enchs.util.datastructures.Pair;
import com.roughlyunderscore.enchs.util.general.Utils;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.roughlyunderscore.enchs.util.general.PlayerUtils.*;
import static com.roughlyunderscore.enchs.util.general.Utils.*;

@EqualsAndHashCode(callSuper = true)
@ToString
@CommandAlias("ue|underscoreenchants|underscoree|uenchants|uenchs|underscoreenchs")
@AllArgsConstructor
@SuppressWarnings("unused")
public class UnderscoreEnchantsCommand extends BaseCommand {
  private final UnderscoreEnchants plugin;

  @Default
  @CatchUnknown
  @Subcommand("help")
  public void defaultCommand(CommandSender sender) {
    if (!sender.hasPermission(Permissions.HELP)) {
      sender.sendMessage(replacePAPI(sender, plugin.getMessages().NO_PERMS));
      return;
    }

    sender.sendMessage(format(plugin.getConfigValues().PREFIX + "&r" + plugin.getDescription().getVersion()));
    sender.sendMessage(format("&b&n&m----------------------------------"));
    sender.sendMessage(" ");
    sender.sendMessage(format("&9/ue log - &bCreates a plugin log for the support team to review"));
    sender.sendMessage(format("&9/ue enchant <ench> <level> - &bEnchants the item in your hand, provided that the arguments are valid"));
    sender.sendMessage(format("&9/ue toggle <ench> <on/off> - &bToggles an enchantment for you"));
    sender.sendMessage(format("&9/ue download <link> <name> <load or not (true/false)> - &bDownloads an enchantment from that link"));
    sender.sendMessage(format("&9/ue default <load or not (true/false)> - &bDownloads the default enchantment set"));
    sender.sendMessage(format("&9/ue load <name> - &bTries to load an enchantment with the given file name"));
    sender.sendMessage(format("&9/ue unload <name> - &bTries to unload an enchantment with the given file name"));
  }

  @Subcommand("log")
  // @CommandPermission(Permissions.LOG)
  public void onLog(CommandSender sender) {
    if (!sender.hasPermission(Permissions.LOG)) {
      sender.sendMessage(replacePAPI(sender, plugin.getMessages().NO_PERMS));
      return;
    }

    createLog(sender, plugin);
  }

  @Subcommand("duck")
  @Private
  public void onDuck(CommandSender sender) {
    sender.sendMessage(EasterEggs.animals[(int) (Math.random() * EasterEggs.animals.length)]);
  }

  @Subcommand("enchant|ench|addenchant|addenchantment|absolutelyobliteratethisitemwiththisnewoverpoweredenchantment")
  @Conditions("cant_console")
  @CommandCompletion("@enchant-name-completion @enchant-level-completion")
  @Syntax("<enchantment> <level>")
  public void onEnchant(Player player, DetailedEnchantment preEnchantment, Integer level) {
    if (!player.hasPermission(Permissions.ENCHANT)) {
      player.sendMessage(PlaceholderAPI.setPlaceholders(player, plugin.getMessages().NO_PERMS));
      return;
    }

    final ItemStack handItem = getMainHand(player, plugin);
    if (handItem.getType() == XMaterial.AIR.parseMaterial()) {
      playSound(player, XSound.ENTITY_VILLAGER_NO, plugin);
      return;
    }

    if (preEnchantment == null) {
      player.sendMessage(PlaceholderAPI.setPlaceholders(player, plugin.getMessages().WRONG_NAME));
      return;
    }

    // make sure that the level is in the bounds
    final DetailedEnchantment ench = parseEnchantment(preEnchantment.getName(), level, false, plugin);

    if (ench.equals(UnderscoreEnchants.WRONG_LEVEL)) {
      player.sendMessage(PlaceholderAPI.setPlaceholders(player, plugin.getMessages().WRONG_LEVEL));
      return;
    }

    if (ench.equals(UnderscoreEnchants.WRONG_NAME)) {
      player.sendMessage(PlaceholderAPI.setPlaceholders(player, plugin.getMessages().WRONG_NAME));
      return;
    }

    // Convert the hand if necessary
    if (is(handItem.getType(), XMaterial.BOOK)) {
      handItem.setType(XMaterial.ENCHANTED_BOOK.parseMaterial());
    }

    final Pair<ItemStack, Map<Enchantment, Integer>> pair = enchant(handItem, ench.getEnchantment(), level, plugin);
    if (pair.getValue() != null && !pair.getValue().isEmpty()) {
      playSound(player, XSound.ENTITY_VILLAGER_NO, plugin);
      return;
    }

    if (pair.getKey() == null) return;

    player.sendMessage(PlaceholderAPI.setPlaceholders(player, plugin.getMessages().ENCHANTED.replace("<name>", Utils.getName(ench.getEnchantment())).replace("<level>", level.toString())));
    player.getInventory().setItemInMainHand(pair.getKey());
  }

  @Subcommand("toggle|toggl|togle|toogle|toglle|toglee|ttogle|tlgeo|tlge|tolge|tpoglge|toglge|totge")
  @Conditions("cant_console")
  @CommandCompletion("@enchant-name-completion @yes-no-completion")
  public void onToggle(Player player, String name, String onOff) {
    if (!player.hasPermission(Permissions.TOGGLE)) {
      player.sendMessage(PlaceholderAPI.setPlaceholders(player, plugin.getMessages().NO_PERMS));
      return;
    }

    final PersistentDataContainer pdc = player.getPersistentDataContainer();

    // Attempt to parse the proposed enchantment
    final DetailedEnchantment parsed = parseEnchantment(name, plugin);
    if (parsed.equals(UnderscoreEnchants.WRONG_LEVEL)) {
      player.sendMessage(PlaceholderAPI.setPlaceholders(player, plugin.getMessages().WRONG_LEVEL));
      return;
    }

    if (parsed.equals(UnderscoreEnchants.WRONG_NAME)) {
      player.sendMessage(PlaceholderAPI.setPlaceholders(player, plugin.getMessages().WRONG_NAME));
      return;
    }

    if (onOff.equalsIgnoreCase("on") || onOff.equalsIgnoreCase("off")) {
      pdc.set(parsed.getKey(), PersistentDataType.STRING, onOff);
      player.sendMessage(PlaceholderAPI.setPlaceholders(player, plugin.getMessages().TOGGLED.replace("<name>", Utils.getName(parsed.getEnchantment()))));
    } else {
      player.sendMessage(PlaceholderAPI.setPlaceholders(player, plugin.getMessages().WRONG_PARAMETER.replace("<param>", onOff)));
    }
  }

  @Subcommand("download|dw|get|fetch|absolutelyobliteratemystoragewiththisbrandnewincredibleenchantmentorenchantmentpack")
  public void onDownload(Player player, String link, String name, Boolean load) {
    if (!player.hasPermission(Permissions.DOWNLOAD)) {
      player.sendMessage(PlaceholderAPI.setPlaceholders(player, plugin.getMessages().NO_PERMS));
      return;
    }

    final File file0 = new File(plugin.getDataFolder().getPath() + File.separator + "enchantments");
    if (!file0.exists()) file0.mkdirs();

    final File enchantment = new File(name);

    try {
      downloadWithJavaNIO(link, name);
    } catch (final MalformedURLException ex) {
      ex.printStackTrace();
    }
    player.sendMessage(PlaceholderAPI.setPlaceholders(player, plugin.getMessages().DOWNLOADED.replace("<ench>", enchantment.getName())));

    if (load) {
      PreparatoryParsers.loadEnchantment(enchantment, plugin);
      player.sendMessage(PlaceholderAPI.setPlaceholders(player, plugin.getMessages().LOADED.replace("<ench>", enchantment.getName())));
    }
  }

  @Subcommand("reload|rl|rel")
  public void reload(CommandSender sender) {
    final long start = System.currentTimeMillis();

    if (!sender.hasPermission(Permissions.RELOAD)) {
      sender.sendMessage(replacePAPI(sender, plugin.getMessages().NO_PERMS));
      return;
    }

    try {
      final MainConfigValues newConfig = new MainConfigValues(plugin);
      final Messages newMessages = new Messages(plugin);

      plugin.setConfigValues(newConfig);
      plugin.setMessages(newMessages);

      plugin.reloadEnchantments();
      final long end = System.currentTimeMillis();

      sender.sendMessage(replacePAPI(sender, newMessages.RELOADED).replace("<ms>", String.valueOf(end - start)));
    } catch (final Exception ex) {
      ex.printStackTrace();
    }


  }

  @Subcommand("default|def|standard|normal|getdefault|defaultget|getstandard|standardget|normalget|getnormal|getdef|defget")
  public void onDownloadDefault(CommandSender sender, Boolean load) {
    if (!sender.hasPermission(Permissions.DOWNLOAD)) {
      sender.sendMessage(replacePAPI(sender, plugin.getMessages().NO_PERMS));
      return;
    }

    final File dir = new File(plugin.getDataFolder().getPath() + File.separator + "enchantments" + File.separator + "default");
    if (!dir.exists()) dir.mkdirs();

    final String linkBase = "https://roughlyunderscore.7m.pl/default_enchantments/";

    Document doc; // gets the document
    try {
      doc = Jsoup.connect(linkBase).get();
    } catch (final IOException e) {
      e.printStackTrace();
      return;
    }
    final Elements links = doc.getElementsByTag("a"); // gets all "a" objects
    final List<String> files = new ArrayList<>();

    for (final var link : links) {
      final String res = link.attr("href"); // gets the href attribute
      if (res.endsWith(".yaml") || res.endsWith(".yml"))
        files.add(res); // makes sure that it's a YML file and adds it to the resources
    }

    final int LIMIT = 50;
    final int WAIT = 25;
    downloadEnchantmentLimitedAndLoad(files, dir, LIMIT, WAIT, linkBase, load, sender, plugin);
  }

  @Subcommand("load|loadench|loadenchantment|enchload|enchantmentload")
  public void onLoad(Player player, String address) {
    if (!player.hasPermission(Permissions.LOAD)) {
      player.sendMessage(PlaceholderAPI.setPlaceholders(player, plugin.getMessages().NO_PERMS));
      return;
    }

    // get file from plugin's folder with given address
    final File file = new File(plugin.getDataFolder().getPath() + File.separator + "enchantments" + File.separator +
      address.replace("/", File.separator).replace("\\", File.separator)
    );
    if (!file.exists()) {
      player.sendMessage(PlaceholderAPI.setPlaceholders(player, plugin.getMessages().WRONG_PARAMETER.replace("<param>", address)));
      return;
    }

    if (PreparatoryParsers.isEnchantmentLoaded(file, plugin)) {
      PreparatoryParsers.unloadEnchantment(file, plugin);
    }
    PreparatoryParsers.loadEnchantment(file, plugin);
    player.sendMessage(PlaceholderAPI.setPlaceholders(player, plugin.getMessages().LOADED.replace("<ench>", file.getName())));
  }

  @Subcommand("unload|unloadench|unloadenchantment|enchunload|enchantmentunload")
  public void onUnload(Player player, String address) {
    if (!player.hasPermission(Permissions.LOAD)) {
      player.sendMessage(PlaceholderAPI.setPlaceholders(player, plugin.getMessages().NO_PERMS));
      return;
    }

    // get file from plugin's folder with given address
    final File file = new File(plugin.getDataFolder().getPath() + File.separator + "enchantments" + File.separator +
      address.replace("/", File.separator).replace("\\", File.separator)
    );
    if (!file.exists()) {
      player.sendMessage(PlaceholderAPI.setPlaceholders(player, plugin.getMessages().WRONG_PARAMETER.replace("<param>", address)));
      return;
    }

    if (PreparatoryParsers.isEnchantmentLoaded(file, plugin)) {
      PreparatoryParsers.unloadEnchantment(file, plugin);
    }
    player.sendMessage(PlaceholderAPI.setPlaceholders(player, plugin.getMessages().UNLOADED.replace("<ench>", file.getName())));
  }

  /**
   * Creates a log on /ue log.
   *
   * @param sender someone to notify if something fails
   * @param plugin UnderscoreEnchants
   */
  protected void createLog(final CommandSender sender, final UnderscoreEnchants plugin) {
    try {
      final String path = plugin.getDataFolder() + "/logfile.log";
      final File file = new File(path);
      if (file.exists()) {
        file.delete();
      }
      file.createNewFile();

      final FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
      final BufferedWriter writer = new BufferedWriter(fileWriter);

      String message = plugin.getMessages().CREATING_LOG;
      if (sender instanceof Player player) message = PlaceholderAPI.setPlaceholders(player, message);
      sender.sendMessage(message);

      World w;
      if (sender instanceof Player player) w = player.getWorld();
      else w = Bukkit.getWorlds().get(0);

      if (w == null) throw new IllegalStateException("No worlds present!");

      final Server server = Bukkit.getServer();

      write0(sender, writer, Arrays.asList(
        "-World parameters-",
        " ",
        "World name: " + w.getName(),
        "World UUID: " + w.getUID(),
        "World spawn location: " + w.getSpawnLocation(),
        "doFireTick: " + w.getGameRuleValue(GameRule.DO_FIRE_TICK),
        "mobGriefing: " + w.getGameRuleValue(GameRule.MOB_GRIEFING),
        "doMobSpawning: " + w.getGameRuleValue(GameRule.DO_MOB_SPAWNING),
        "doMobLoot: " + w.getGameRuleValue(GameRule.DO_MOB_LOOT),
        "doTileDrops: " + w.getGameRuleValue(GameRule.DO_TILE_DROPS),
        "commandBlockOutput: " + w.getGameRuleValue(GameRule.COMMAND_BLOCK_OUTPUT),
        "naturalRegeneration: " + w.getGameRuleValue(GameRule.NATURAL_REGENERATION),
        "doDaylightCycle: " + w.getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE),
        "logAdminCommands: " + w.getGameRuleValue(GameRule.LOG_ADMIN_COMMANDS),
        "showDeathMessages: " + w.getGameRuleValue(GameRule.SHOW_DEATH_MESSAGES),
        "randomTickSpeed: " + w.getGameRuleValue(GameRule.RANDOM_TICK_SPEED),
        "sendCommandFeedback: " + w.getGameRuleValue(GameRule.SEND_COMMAND_FEEDBACK),
        "reducedDebugInfo: " + w.getGameRuleValue(GameRule.REDUCED_DEBUG_INFO),
        "doEntityDrops: " + w.getGameRuleValue(GameRule.DO_ENTITY_DROPS),
        "keepInventory: " + w.getGameRuleValue(GameRule.KEEP_INVENTORY),
        "doWeatherCycle: " + w.getGameRuleValue(GameRule.DO_WEATHER_CYCLE),
        "doInsomnia: " + w.getGameRuleValue(GameRule.DO_INSOMNIA),
        "announceAdvancements: " + w.getGameRuleValue(GameRule.ANNOUNCE_ADVANCEMENTS),
        "disableElytraMovementCheck: " + w.getGameRuleValue(GameRule.DISABLE_ELYTRA_MOVEMENT_CHECK),
        "maxEntityCramming: " + w.getGameRuleValue(GameRule.MAX_ENTITY_CRAMMING),
        "spawnRadius: " + w.getGameRuleValue(GameRule.SPAWN_RADIUS),
        "doLimitedCrafting: " + w.getGameRuleValue(GameRule.DO_LIMITED_CRAFTING),
        "maxCommandChainLength: " + w.getGameRuleValue(GameRule.MAX_COMMAND_CHAIN_LENGTH),
        "spectatorsGenerateChunks: " + w.getGameRuleValue(GameRule.SPECTATORS_GENERATE_CHUNKS),
        "Difficulty: " + w.getDifficulty(),
        "Time: " + w.getTime(),
        "PVP: " + w.getPVP(),
        " ",
        "-Server parameters-",
        "Name: " + server.getName(),
        "IP: " + server.getIp(),
        "Bukkit API Version: " + server.getBukkitVersion(),
        "Server version: " + server.getVersion(),
        "MOTD: " + server.getMotd(),
        "Allows End: " + server.getAllowEnd(),
        "Allows Flight: " + server.getAllowFlight(),
        "Allows Nether: " + server.getAllowNether(),
        "Default gamemode: " + server.getDefaultGameMode(),
        "Has whitelist: " + server.hasWhitelist(),

        "Plugins: ", " ", Arrays.toString(server.getPluginManager().getPlugins()),
        " ",
        "-Underscore Enchants parameters-",
        "Enchantments:"
      ), plugin);

      for (final DetailedEnchantment ench : plugin.getEnchantmentData().keySet()) {
        write0(sender, writer, Arrays.asList(
          " - Key: " + ench.getKey(),
          " - Name: " + ench.getName(),
          " - Target: " + ench.getEnchantment().getItemTarget().name(),
          " - Start level: " + ench.getEnchantment().getStartLevel(),
          " - Maximum level: " + ench.getEnchantment().getMaxLevel(),
          " - TabCompletable name: " + ench.getCommandName(),
          "------------------"

        ), plugin);
      }
      writer.write("Creator of UnderscoreEnchants plays Genshin Impact: true");
      writer.write("------------------");

      writer.close();

      String logCreatedMessage = plugin.getMessages().LOG_CREATED;
      if (sender instanceof Player player) logCreatedMessage = PlaceholderAPI.setPlaceholders(player, message);
      sender.sendMessage(logCreatedMessage);
    } catch (final Exception e) {
      String message = plugin.getMessages().NO_LOG;
      if (sender instanceof Player player) message = PlaceholderAPI.setPlaceholders(player, message);
      sender.sendMessage(message);
      e.printStackTrace();
    }
  }

  /**
   * Uses a BufferedWriter to write something.
   *
   * @param sender   someone to notify if something fails
   * @param writer   the BufferedWriter
   * @param contents the contents to write
   * @param plugin   UnderscoreEnchants
   */
  private void write0(final CommandSender sender, final BufferedWriter writer, final List<String> contents, final UnderscoreEnchants plugin) {
    try {
      for (final String string : contents) {
        writer.write(string);
        writer.write("\n");
      }
    } catch (IOException e) {
      String message = plugin.getMessages().NO_LOG;
      if (sender instanceof Player player) message = PlaceholderAPI.setPlaceholders(player, message);
      sender.sendMessage(message);
    }
  }
}

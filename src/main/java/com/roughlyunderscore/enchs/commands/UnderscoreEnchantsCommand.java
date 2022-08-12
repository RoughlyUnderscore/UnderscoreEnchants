package com.roughlyunderscore.enchs.commands;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.roughlyunderscore.enchs.util.Pair;
import com.roughlyunderscore.enchs.UnderscoreEnchants;
import com.roughlyunderscore.enchs.util.general.Utils;
import com.roughlyunderscore.enchs.util.data.DetailedEnchantment;
import com.roughlyunderscore.enchs.util.enums.Permissions;
import com.roughlyunderscore.enchs.util.holders.AnvilHolder;
import com.roughlyunderscore.enchs.util.holders.EnchantHolder;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
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
import java.util.*;

import static com.roughlyunderscore.enchs.util.general.PlayerUtils.*;
import static com.roughlyunderscore.enchs.registration.Register.*;
import static com.roughlyunderscore.enchs.util.general.Utils.*;

@AllArgsConstructor
/*
Here is the UnderscoreEnchants command (/ue) before your very eyes.
The code is a bit scuffed, but I tried to get the best out of it
after rewriting it from the terrible legacy state.
 */
public class UnderscoreEnchantsCommand implements CommandExecutor {
    public static final int LIMIT = 50;
    public static final int WAIT = 25;
    private final UnderscoreEnchants plugin;

    @Override
    @SneakyThrows
    public boolean onCommand(@NonNull final CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String[] args) {

        boolean isPlayer = sender instanceof Player;

        switch (args.length) {
            case 0 -> {
                // ue
                // Just a help message
                sender.sendMessage(format(plugin.getConfig().getString("prefix")));
                sender.sendMessage(format("&b&n&m----------------------------------"));
                sender.sendMessage(" ");
                sender.sendMessage(format("&9/ue log - &bCreates a plugin log for the support team to review"));
                sender.sendMessage(format("&9/ue anvil - &bOpens the new anvil interface"));
                sender.sendMessage(format("&9/ue enchanttable - &bOpens the new enchantment table interface"));
                sender.sendMessage(format("&9/ue enchant <ench> <level> - &bEnchants the item in your hand, provided that the arguments are valid"));
                sender.sendMessage(format("&9/ue toggle <ench> <on/off> - &bToggles an enchantment for you"));
                sender.sendMessage(format("&9/ue download <link> <name> <load or not (true/false)> - &bDownloads an enchantment from that link"));
                sender.sendMessage(format("&9/ue download default <load or not (true/false)> - &bDownloads the default enchantment set"));
            }

            case 1 -> {
                if (args[0].equalsIgnoreCase("log")) {
                    // ue log
                    // When used by console, create a log without asking
                    if (!isPlayer) {
                        createLog(sender, plugin);
                        return false;
                    }

                    // For player, do a permission check
                    Player player = (Player) sender;
                    if (!player.hasPermission(Permissions.LOG.getPermission())) {
                        sender.sendMessage(plugin.getMessages().NO_PERMS);
                        return false;
                    }

                    // A player with the satisfying permissions can create a log
                    createLog(sender, plugin);
                }

                if (args[0].equalsIgnoreCase("anvil")) {
                    // ue anvil
                    // Restrict the command execution for console
                    if (!isPlayer) {
                        sender.sendMessage(plugin.getMessages().NO_CONSOLE);
                        return false;
                    }

                    // Create the player variable for a perm-check
                    Player player = (Player) sender;
                    if (!player.hasPermission(Permissions.ANVIL_GUI.getPermission())) {
                        sender.sendMessage(plugin.getMessages().NO_PERMS);
                        return false;
                    }

                    // Now all the checks are passed, thus the GUI can be opened
                    player.openInventory(getAnvilGUI());
                }

                if (args[0].equalsIgnoreCase("enchanttable")) {
                    // ue enchanttable
                    // Restrict the command execution for console
                    if (!isPlayer) {
                        sender.sendMessage(plugin.getMessages().NO_CONSOLE);
                        return false;
                    }

                    // Create the player variable for a perm-check
                    Player player = (Player) sender;
                    if (!player.hasPermission(Permissions.ENCHANT_GUI.getPermission())) {
                        sender.sendMessage(plugin.getMessages().NO_PERMS);
                        return false;
                    }

                    // Now all the checks are passed, thus the GUI can be opened
                    player.openInventory(getTableGUI());
                }

                if (args[0].equalsIgnoreCase("duck")) {
                    // ue duck
                    sender.sendMessage("Duck");
                }
            }

            case 2 -> {
                // Empty
                sender.sendMessage(plugin.getMessages().BAD_ARGS);
                return false;
            }

            case 3 -> {
                if (args[0].equalsIgnoreCase("enchant")) {
                    // ue enchant <name> <level>
                    // Restrict the command execution for console
                    if (!isPlayer) {
                        sender.sendMessage(plugin.getMessages().NO_CONSOLE);
                        return false;
                    }

                    // Create the player variable for future use, perm-check
                    Player player = (Player) sender;
                    if (!player.hasPermission(Permissions.ENCHANT.getPermission())) {
                        sender.sendMessage(plugin.getMessages().NO_PERMS);
                        return false;
                    }

                    // Get player's hand and check for AIR
                    ItemStack handItem = getMainHand(player);
                    if (handItem.getType() == XMaterial.AIR.parseMaterial()) {
                        player.playSound(player.getLocation(), XSound.ENTITY_VILLAGER_NO.parseSound(), SoundCategory.MASTER, 1, 1);
                        return false;
                    }

                    // Fetch the suggested enchantment data
                    String name = args[1];
                    int level = parseI(args[2]);

                    // Check for the level validity
                    if (level == Integer.MIN_VALUE) {
                        sender.sendMessage(plugin.getMessages().WRONG_LEVEL);
                        return false;
                    }

                    // Check for the enchantment existence and it being inside the level bounds
                    DetailedEnchantment ench = parseEnchantment(name, level, false);
                    if (ench.equals(plugin.EMPTY) || ench.equals(UnderscoreEnchants.STATIC_EMPTY)) {
                        sender.sendMessage(plugin.getMessages().WRONG_NAME);
                        return false;
                    }

                    // Convert the hand if necessary
                    if (handItem.getType() == XMaterial.BOOK.parseMaterial()) {
                        handItem.setType(XMaterial.ENCHANTED_BOOK.parseMaterial());
                    }

                    // Enchant the item
                    Pair<ItemStack, Map<Enchantment, Integer>> pair = enchant(handItem, ench.getEnchantment(), level);
                    if (pair.getValue() != null && !pair.getValue().isEmpty()) {
                        playSound(player, XSound.ENTITY_VILLAGER_NO);
                        return false;
                    }

                    if (pair.getKey() == null) return false;


                    player.sendMessage(plugin.getMessages().ENCHANTED.replace("%name%", Utils.getName(ench.getEnchantment())).replace("%level%", String.valueOf(level)));
                    player.getInventory().setItemInMainHand(pair.getKey());
                }

                if (args[0].equalsIgnoreCase("toggle")) {
                    // ue toggle <enchantment> <on/off>
                    if (!isPlayer) {
                        sender.sendMessage(plugin.getMessages().NO_CONSOLE);
                        return false;
                    }

                    // Create the player variable for future use, perm-check
                    Player player = (Player) sender;
                    if (!player.hasPermission(Permissions.TOGGLE.getPermission())) {
                        player.sendMessage(plugin.getMessages().NO_PERMS);
                        return false;
                    }

                    // Fetch the player's PDC for toggling
                    PersistentDataContainer pdc = player.getPersistentDataContainer();

                    // Attempt to parse the proposed enchantment
                    DetailedEnchantment parsed = parseEnchantment(args[1]);
                    if (parsed.equals(plugin.EMPTY) || parsed.equals(UnderscoreEnchants.STATIC_EMPTY)) {
                        player.sendMessage(plugin.getMessages().WRONG_NAME);
                        return false;
                    }

                    // The second command argument must be "on" or "off"
                    if (!args[2].equalsIgnoreCase("off") && !args[2].equalsIgnoreCase("on")) {
                        player.sendMessage(plugin.getMessages().WRONG_PARAMETER);
                        return false;
                    }

                    // Initialize the variables for toggling
                    DetailedEnchantment detailedEnchantment = parseEnchantment(args[1]);
                    String state = args[2];

                    // Toggle the enchantment
                    pdc.set(detailedEnchantment.getKey(), PersistentDataType.STRING, state);

                    // Inform the player
                    player.playSound(player.getLocation(), XSound.BLOCK_NOTE_BLOCK_HARP.parseSound(), SoundCategory.MASTER, 1, 1);
                }

                if (args[0].equalsIgnoreCase("download")) {
                    if (!args[1].equalsIgnoreCase("default")) {
                        // "ue download" cannot be called with 3 arguments if the first one is not "default"
                        sender.sendMessage(plugin.getMessages().WRONG_PARAMETER.replace("%param%", args[1]));
                        return false;
                    }
                    // ue download default <load or not>

                    if (isPlayer) {
                        Player player = (Player) sender;
                        if (!player.hasPermission(Permissions.ENCHANT.getPermission())) {
                            sender.sendMessage(plugin.getMessages().NO_PERMS);
                            return false;
                        }
                    }

                    final boolean load = parseB(args[2]);

                    final File dir = new File(plugin.getDataFolder().getPath() + File.separator + "enchantments" + File.separator + "default");
                    if (!dir.exists()) dir.mkdirs();

                    final String linkBase = "https://roughlyunderscore.7m.pl/default_enchantments/";

                    Document doc = Jsoup.connect(linkBase).get(); // gets the document
                    Elements links = doc.getElementsByTag("a"); // gets all "a" objects
                    List<String> files = new ArrayList<>();

                    for (final var link : links) {
                        String res = link.attr("href"); // gets the href attribute
                        if (res.endsWith(".yaml") || res.endsWith(".yml"))
                            files.add(res); // makes sure that it's a YML file and adds it to the resources
                    }

                    downloadEnchantmentLimitedAndLoad(files, dir, LIMIT, WAIT, linkBase, load, sender, plugin);


                }

            }

            case 4 -> {
                if (args[0].equalsIgnoreCase("download")) {
                    // ue download <link> <name> <load or not>
                    if (isPlayer) {
                        final Player player = (Player) sender;
                        if (!player.hasPermission(Permissions.ENCHANT.getPermission())) {
                            sender.sendMessage(plugin.getMessages().NO_PERMS);
                            return false;
                        }
                    }


                    File file0 = new File(plugin.getDataFolder().getPath() + File.separator + "enchantments");
                    if (!file0.exists()) file0.mkdirs();

                    String link = args[1];
                    String name = file0.getPath() + File.separator + args[2];
                    File enchantment = new File(name);
                    boolean load = parseB(args[3]);

                    Utils.downloadWithJavaNIO(link, name, plugin);
                    sender.sendMessage(plugin.getMessages().DOWNLOADED.replace("%ench%", enchantment.getName()));

                    if (load) {
                        loadEnchantment(enchantment, plugin);
                        sender.sendMessage(plugin.getMessages().LOADED.replace("%ench%", enchantment.getName()));
                    }
                }
            }

            default -> {
                sender.sendMessage(plugin.getMessages().BAD_ARGS);
                return false;
            }
        }

        return false;
    }

    /**
     * Creates a log on /ue log.
     * @param sender someone to notify if something fails
     * @param plugin UnderscoreEnchants
     */
    protected void createLog(CommandSender sender, UnderscoreEnchants plugin) {
        try {
            String path = plugin.getDataFolder() + "/logfile.log";
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();

            FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
            BufferedWriter writer = new BufferedWriter(fileWriter);

            sender.sendMessage(plugin.getMessages().CREATING_LOG);

            World w;
            if (sender instanceof Player player) w = player.getWorld();
            else w = Bukkit.getWorlds().get(0);

            if (w == null) throw new IllegalStateException("No worlds present!");

            Server server = Bukkit.getServer();

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
                    "Difficulty: " + w.getDifficulty(),
                    "Time: " + w.getTime(),
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

            for (DetailedEnchantment ench : plugin.getEnchantmentData()) {
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

            sender.sendMessage(plugin.getMessages().LOG_CREATED);
        } catch (Exception e) {
            sender.sendMessage(plugin.getMessages().NO_LOG);
            e.printStackTrace();
        }
    }

    /**
     * Uses a BufferedWriter to write something.
     * @param sender someone to notify if something fails
     * @param writer the BufferedWriter
     * @param contents the contents to write
     * @param plugin UnderscoreEnchants
     */
    private void write0(CommandSender sender, BufferedWriter writer, List<String> contents, UnderscoreEnchants plugin) {
        try {
            for (String string : contents) {
                writer.write(string);
                writer.write("\n");
            }
        } catch (IOException e) {
            sender.sendMessage(plugin.getMessages().NO_LOG);
        }
    }

    /**
     * LEGACY (reason: couldn't care less about this method)<br>
     * A method that creates a standardized anvil GUI.
     * @return the GUI
     */
    protected Inventory getAnvilGUI() {
        int size = 27;
        Inventory inv = Bukkit.createInventory(new AnvilHolder(), size, "Anvil");
        for (int i = 0; i != 27; i++) {
            inv.setItem(i, new ItemStack(XMaterial.GRAY_STAINED_GLASS_PANE.parseMaterial()));
        }
        inv.setItem(10, new ItemStack(XMaterial.RED_STAINED_GLASS_PANE.parseMaterial()));
        inv.setItem(12, new ItemStack(XMaterial.RED_STAINED_GLASS_PANE.parseMaterial()));
        inv.setItem(16, new ItemStack(XMaterial.RED_STAINED_GLASS_PANE.parseMaterial()));

        return inv;
    }



    /**
     * LEGACY (reason: couldn't care less about this method)<br>
     * A method that creates a standardized enchantment table GUI.
     * @return the GUI
     */
    protected Inventory getTableGUI() {
        Inventory inv = Bukkit.createInventory(new EnchantHolder(), 54, format("&eEnchant an item!"));

        for (int i = 0; i != 54; i++)
            inv.setItem(i, new ItemStack(XMaterial.GRAY_STAINED_GLASS_PANE.parseMaterial()));
        for (int i = 14; i < 16; i++)
            inv.setItem(i, new ItemStack(XMaterial.CYAN_STAINED_GLASS_PANE.parseMaterial()));
        for (int i = 32; i != 34; i++)
            inv.setItem(i, new ItemStack(XMaterial.CYAN_STAINED_GLASS_PANE.parseMaterial()));

        inv.setItem(16, new ItemStack(XMaterial.CYAN_STAINED_GLASS_PANE.parseMaterial()));
        inv.setItem(23, new ItemStack(XMaterial.CYAN_STAINED_GLASS_PANE.parseMaterial()));
        inv.setItem(25, new ItemStack(XMaterial.CYAN_STAINED_GLASS_PANE.parseMaterial()));
        inv.setItem(32, new ItemStack(XMaterial.CYAN_STAINED_GLASS_PANE.parseMaterial()));
        inv.setItem(34, new ItemStack(XMaterial.CYAN_STAINED_GLASS_PANE.parseMaterial()));

        inv.setItem(10, new ItemStack(XMaterial.PINK_STAINED_GLASS_PANE.parseMaterial()));
        inv.setItem(24, new ItemStack(XMaterial.RED_STAINED_GLASS_PANE.parseMaterial()));

        return inv;
    }
}

package com.roughlyunderscore.enchs.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.roughlyunderscore.enchs.util.Pair;
import com.roughlyunderscore.enchs.UnderscoreEnchants;
import com.roughlyunderscore.enchs.util.general.Utils;
import com.roughlyunderscore.enchs.util.data.DetailedEnchantment;
import com.roughlyunderscore.enchs.util.data.Permissions;
import com.roughlyunderscore.enchs.util.holders.EnchantHolder;
import lombok.*;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
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
import java.net.MalformedURLException;
import java.util.*;

import static com.roughlyunderscore.enchs.util.general.PlayerUtils.*;
import static com.roughlyunderscore.enchs.registration.Register.*;
import static com.roughlyunderscore.enchs.util.general.Utils.*;

/*
Here is the UnderscoreEnchants command (/ue) before your very eyes.
The code is a bit scuffed, but I tried to get the best out of it
after rewriting it from the terrible legacy state.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@CommandAlias("ue|underscoreenchants|underscoree|uenchants|uenchs|underscoreenchs")
@SuppressWarnings("unused")

public class UnderscoreEnchantsCommand extends BaseCommand {
    private final UnderscoreEnchants plugin;

    @Default @CatchUnknown @Subcommand("help")
    // @CommandPermission(Permissions.HELP) Before a global localization update, I'll have to manage these myself
    public void defaultCommand(CommandSender sender) {
        if (!sender.hasPermission(Permissions.HELP)) {
            sender.sendMessage(plugin.getMessages().NO_PERMS);
            return;
        }

        sender.sendMessage(format(plugin.getConfig().getString("prefix") + " &r" + plugin.getDescription().getVersion()));
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

    @Subcommand("log")
    // @CommandPermission(Permissions.LOG)
    public void onLog(CommandSender sender) {
        if (!sender.hasPermission(Permissions.LOG)) {
            sender.sendMessage(plugin.getMessages().NO_PERMS);
            return;
        }

        createLog(sender, plugin);
    }

    @Subcommand("anvil")
    // @CommandPermission(Permissions.ANVIL_GUI)
    @Conditions("cant_console")
    public void openAnvil(Player player) {
        if (!player.hasPermission(Permissions.ANVIL_GUI)) {
            player.sendMessage(plugin.getMessages().NO_PERMS);
            return;
        }

        player.openInventory(Bukkit.createInventory(player, InventoryType.ANVIL));
    }

    @Subcommand("anvil")
    // @CommandPermission(Permissions.ENCHANT_GUI)
    @Conditions("cant_console")
    public void openEnchant(Player player) {
        if (!player.hasPermission(Permissions.ENCHANT_GUI)) {
            player.sendMessage(plugin.getMessages().NO_PERMS);
            return;
        }

        player.sendMessage("This is currently under development"); // TODO
    }

    @Subcommand("duck")
    @Private
    public void onDuck(CommandSender sender) {
        sender.sendMessage(UnderscoreEnchants.animals[(int) (Math.random() * UnderscoreEnchants.animals.length)]);
    }

    @Subcommand("enchant|ench|addenchant|addenchantment|absolutelyobliteratethisitemwiththisnewoverpoweredenchantment")
    @Conditions("cant_console")
    public void onEnchant(Player player, String name, Integer level) {
        if (!player.hasPermission(Permissions.ENCHANT)) {
            player.sendMessage(plugin.getMessages().NO_PERMS);
            return;
        }

        ItemStack handItem = getMainHand(player);
        if (handItem.getType() == XMaterial.AIR.parseMaterial()) {
            playSound(player, XSound.ENTITY_VILLAGER_NO);
            return;
        }

        DetailedEnchantment ench = parseEnchantment(name, level, false);
        if (ench.equals(plugin.EMPTY) || ench.equals(UnderscoreEnchants.STATIC_EMPTY)) {
            player.sendMessage(plugin.getMessages().WRONG_NAME);
            return;
        }

        // Convert the hand if necessary
        if (handItem.getType() == XMaterial.BOOK.parseMaterial()) {
            handItem.setType(XMaterial.ENCHANTED_BOOK.parseMaterial());
        }

        Pair<ItemStack, Map<Enchantment, Integer>> pair = enchant(handItem, ench.getEnchantment(), level);
        if (pair.getValue() != null && !pair.getValue().isEmpty()) {
            playSound(player, XSound.ENTITY_VILLAGER_NO);
            return;
        }

        if (pair.getKey() == null) return;

        player.sendMessage(plugin.getMessages().ENCHANTED.replace("%name%", Utils.getName(ench.getEnchantment())).replace("%level%", level.toString()));
        player.getInventory().setItemInMainHand(pair.getKey());
    }

    @Subcommand("toggle|toggl|togle|toogle|toglle|toglee|ttogle")
    @Conditions("cant_console")
    public void onToggle(Player player, String name, String onOff) {
        if (!player.hasPermission(Permissions.TOGGLE)) {
            player.sendMessage(plugin.getMessages().NO_PERMS);
            return;
        }

        PersistentDataContainer pdc = player.getPersistentDataContainer();

        // Attempt to parse the proposed enchantment
        DetailedEnchantment parsed = parseEnchantment(name);
        if (parsed.equals(plugin.EMPTY) || parsed.equals(UnderscoreEnchants.STATIC_EMPTY)) {
            player.sendMessage(plugin.getMessages().WRONG_NAME);
            return;
        }

        if (onOff.equalsIgnoreCase("on") || onOff.equalsIgnoreCase("off")) {
            pdc.set(parsed.getKey(), PersistentDataType.STRING, onOff);
            player.sendMessage(plugin.getMessages().TOGGLED.replace("%name%", Utils.getName(parsed.getEnchantment())));
        } else {
            player.sendMessage(plugin.getMessages().WRONG_PARAMETER.replace("%param%", onOff));
        }
    }

    @Subcommand("download|dw|get|fetch|absolutelyobliteratemystoragewiththisbrandnewincredibleenchantmentorenchantmentpack")
    public void onDownload(Player player, String link, String name, Boolean load) {
        if (!player.hasPermission(Permissions.DOWNLOAD)) {
            player.sendMessage(plugin.getMessages().NO_PERMS);
            return;
        }

        File file0 = new File(plugin.getDataFolder().getPath() + File.separator + "enchantments");
        if (!file0.exists()) file0.mkdirs();

        File enchantment = new File(name);

        try {
            downloadWithJavaNIO(link, name, plugin);
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
        player.sendMessage(plugin.getMessages().DOWNLOADED.replace("%ench%", enchantment.getName()));

        if (load) {
            loadEnchantment(enchantment, plugin);
            player.sendMessage(plugin.getMessages().LOADED.replace("%ench%", enchantment.getName()));
        }
    }

    @Subcommand("default|def|standard|normal|getdefault|defaultget|getstandard|standardget|normalget|getnormal|getdef|defget")
    public void onDownloadDefault(Player player, Boolean load) {
        if (!player.hasPermission(Permissions.DOWNLOAD)) {
            player.sendMessage(plugin.getMessages().NO_PERMS);
            return;
        }

        final File dir = new File(plugin.getDataFolder().getPath() + File.separator + "enchantments" + File.separator + "default");
        if (!dir.exists()) dir.mkdirs();

        final String linkBase = "https://roughlyunderscore.7m.pl/default_enchantments/";

        Document doc; // gets the document
        try {
            doc = Jsoup.connect(linkBase).get();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Elements links = doc.getElementsByTag("a"); // gets all "a" objects
        List<String> files = new ArrayList<>();

        for (final var link : links) {
            String res = link.attr("href"); // gets the href attribute
            if (res.endsWith(".yaml") || res.endsWith(".yml"))
                files.add(res); // makes sure that it's a YML file and adds it to the resources
        }

        int LIMIT = 50;
        int WAIT = 25;
        downloadEnchantmentLimitedAndLoad(files, dir, LIMIT, WAIT, linkBase, load, player, plugin);
    }

    @Subcommand("load|loadench|loadenchantment|enchload|enchantmentload")
    public void onLoad(Player player, String address) {
        if (!player.hasPermission(Permissions.LOAD)) {
            player.sendMessage(plugin.getMessages().NO_PERMS);
            return;
        }

        // get file from plugin's folder with given address
        File file = new File(plugin.getDataFolder().getPath() + File.separator + "enchantments" + File.separator +
            address.replace("/", File.separator).replace("\\", File.separator)
        );
        if (!file.exists()) {
            player.sendMessage(plugin.getMessages().WRONG_PARAMETER.replace("%param%", address));
            return;
        }

        if (isEnchantmentLoaded(file, plugin)) {
            unloadEnchantment(file, plugin);
        }
        loadEnchantment(file, plugin);
        player.sendMessage(plugin.getMessages().LOADED.replace("%ench%", file.getName()));
    }

    @Subcommand("unload|unloadench|unloadenchantment|enchunload|enchantmentunload")
    public void onUnload(Player player, String address) {
        if (!player.hasPermission(Permissions.LOAD)) {
            player.sendMessage(plugin.getMessages().NO_PERMS);
            return;
        }

        // get file from plugin's folder with given address
        File file = new File(plugin.getDataFolder().getPath() + File.separator + "enchantments" + File.separator +
            address.replace("/", File.separator).replace("\\", File.separator)
        );
        if (!file.exists()) {
            player.sendMessage(plugin.getMessages().WRONG_PARAMETER.replace("%param%", address));
            return;
        }

        if (isEnchantmentLoaded(file, plugin)) {
            unloadEnchantment(file, plugin);
        }
        player.sendMessage(plugin.getMessages().UNLOADED.replace("%ench%", file.getName()));
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

            for (DetailedEnchantment ench : plugin.getEnchantmentData().keySet()) {
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

package com.roughlyunderscore.enchs.gui;

import com.cryptomorin.xseries.XMaterial;
import com.roughlyunderscore.enchantsapi.events.EnchantmentsCombineEvent;
import com.roughlyunderscore.enchs.UnderscoreEnchants;
import com.roughlyunderscore.enchs.util.holders.AnvilHolder;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import lombok.*;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.roughlyunderscore.enchs.util.general.PlayerUtils.*;
import static com.roughlyunderscore.enchs.util.general.Utils.*;

/*
AnvilGUI is the second hardest and the most bug-prone class in this entire project.
It has been rewritten from scratch countless times.
This time, I also left some comments when writing it, but I am not entirely sure about how correct they are.
 */
public class AnvilGUI {

    /*
    @Getter private final Player opener;
    @Getter private final Gui GUI;

    public AnvilGUI(final UnderscoreEnchants plugin, final Player opener) {
        this.opener = opener;

        GUI = Gui.gui()
            .title(Component.text(format("&eCombine items!")))
            .rows(3)
            .disableAllInteractions()
            .create();

        final ItemStack fillerItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        final ItemStack insertItem = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        final ItemStack retrieveItem = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);

        final GuiItem filler = ItemBuilder.from(fillerItem)
            .name(Component.text(format("&r======")))
            .asGuiItem(ev -> {
                if (!isPane(ev.getCurrentItem())) {
                    ItemStack current = ev.getCurrentItem().clone(); // anti-dupe measure
                    ev.setCurrentItem(fillerItem);
                    ev.getWhoClicked().getInventory().addItem(current);
                }
            });

        final GuiItem insert = ItemBuilder.from(insertItem)
            .name(Component.text(format("&r======")))
            .asGuiItem(ev -> {
                if (!isPane(ev.getCurrentItem())) {
                    ItemStack current = ev.getCurrentItem().clone(); // anti-dupe measure
                    ev.setCurrentItem(insertItem);
                    ev.getWhoClicked().getInventory().addItem(current);
                }
            });

        final GuiItem retrieve = ItemBuilder.from(retrieveItem)
            .name(Component.text(format("&r======")))
            .asGuiItem(ev -> {
                Player pl = (Player) ev.getWhoClicked();
                if (!isPane(ev.getCurrentItem()) && pl.getInventory().firstEmpty() != -1) {
                    ItemStack current = ev.getCurrentItem().clone(); // anti-dupe measure
                    ev.setCurrentItem(retrieveItem);
                    pl.getInventory().addItem(current);
                }
            });

        GUI.getFiller().fill(filler);

        GUI.setItem(10, insert);
        GUI.setItem(12, insert);
        GUI.setItem(16, retrieve);

        GUI.setPlayerInventoryAction(ev -> {

        });
    }

    */

    /*

    private final UnderscoreEnchants plugin;
    private final int combined0 = 10;
    private final int combinee0 = 12;
    private final int result0 = 16;

    @EventHandler
    public void onClick(InventoryClickEvent ev) {
        // a click happened in player GUI with anvil GUI open
        else if (ev.getClickedInventory().getHolder() instanceof Player && top.getHolder() instanceof AnvilHolder) {
            // variables
            @SuppressWarnings("ALL") Inventory anvilGUI = bottom;
            @SuppressWarnings("ALL") Inventory playerGUI = top;

            ItemStack combined = top.getItem(combined0);
            ItemStack combinee = top.getItem(combinee0);
            ItemStack result = top.getItem(result0);
            ItemStack clicked = player.getInventory().getItem(clicked0);

            // check for enchantability
            if (clicked == null || clicked.getType() == Material.AIR) return;


            // check if result is empty
            if (!isPane(result)) return;

            // check for fullity and insert the item
            if (isPane(combined)) top.setItem(combined0, clicked);
            else if (isPane(combinee)) top.setItem(combinee0, clicked);
            else return;

            player.getInventory().setItem(clicked0, null);

            combined = top.getItem(combined0);
            combinee = top.getItem(combinee0);

            // both are full so we need to combine them
            if (!isPane(combined) && !isPane(combinee)) {
                if ((combined.getType() == combinee.getType()) || combinee.getType() == Material.ENCHANTED_BOOK) {

                    // creating new item
                    ItemStack newItem = new ItemStack(combined.getType());
                    ItemMeta newMeta = Bukkit.getItemFactory().getItemMeta(newItem.getType());

                    ConcurrentHashMap<Enchantment, Integer> combinedEnchants = new ConcurrentHashMap<>(combined.getEnchantments());
                    ConcurrentHashMap<Enchantment, Integer> combineeEnchants = new ConcurrentHashMap<>(combinee.getEnchantments());
                    Map<Enchantment, Integer> allEnchants = new ConcurrentHashMap<>();

                    for (Map.Entry<Enchantment, Integer> combinedEntry : combinedEnchants.entrySet()) {
                        for (Map.Entry<Enchantment, Integer> combineeEntry : combineeEnchants.entrySet()) {

                            // o^2 complexity? didn't ask lol
                            if (!combinedEntry.getKey().equals(combineeEntry.getKey())) continue;

                            // pass

                            combinedEnchants.remove(combinedEntry.getKey());
                            combineeEnchants.remove(combineeEntry.getKey());

                            if (combinedEntry.getValue().equals(combineeEntry.getValue())) {
                                allEnchants.put(combinedEntry.getKey(), Math.min(combinedEntry.getValue() + 1, combinedEntry.getKey().getMaxLevel()));
                                continue;
                            }

                            allEnchants.put(combinedEntry.getKey(), Math.min(Math.max(combinedEntry.getValue(), combineeEntry.getValue()), combinedEntry.getKey().getMaxLevel()));
                        }
                    }

                    allEnchants.putAll(combinedEnchants);
                    allEnchants.putAll(combineeEnchants);

                    // generic
                    newMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

                    // making sure nothing conflicts
                    // the method is weirdly coded so i inverse it
                    if (!hasConflicts(newItem)) return;

                    // lore
                    List<String> newLore = new ArrayList<>();
                    if (!allEnchants.isEmpty()) {
                        for (Map.Entry<Enchantment, Integer> entry : allEnchants.entrySet()) {
                            newLore.add(format("&7" + getName(entry.getKey()) + " " + toRoman(entry.getValue())));
                        }
                    }

                    // tweaking and adding
                    newMeta.setLore(newLore);
                    newItem.setItemMeta(newMeta);

                    // adding enchantments from combined & combinee
                    allEnchants.forEach(newItem::addUnsafeEnchantment);
                    ItemStack book = new ItemStack(Material.ENCHANTED_BOOK); // making a book to store the leftover enchantments in case they'd exist

                    boolean overlap = false;

                    if (!allEnchants.isEmpty() && allEnchants.size() >= plugin.getConfig().getInt("enchantmentLimit")) {
                        overlap = true;
                        ArrayList<Enchantment> keys = new ArrayList<>(allEnchants.keySet());
                        ArrayList<Integer> values = new ArrayList<>(allEnchants.values());
                        for (int i = 0; i < allEnchants.size(); i++) {
                            allEnchants.remove(keys.get(i));
                            keys.remove(0); // remove the OK enchantments
                            values.remove(0);
                        }
                        for (int i = 0; i < allEnchants.size(); i++) newItem.removeEnchantment(keys.get(i));

                        ItemMeta meta = book.getItemMeta();
                        List<String> bookLore = new ArrayList<>();

                        bookLore.add(format("&7Book created in result of combining two items"));
                        bookLore.add(format("&7and overflowing the per-item enchantment limit."));
                        bookLore.add(format("&7 "));

                        for (int i = 0; i < keys.size(); i++) {
                            bookLore.add(format("&7" + getName(keys.get(i)) + " " + toRoman(values.get(i))));
                        }

                        meta.setLore(bookLore);
                        book.setItemMeta(meta);
                    }

                    // making sure has enough levels
                    int enchants = newItem.getEnchantments().size() * 3 + 3;
                    if (player.getLevel() < enchants && player.getGameMode() != GameMode.CREATIVE) {
                        sendActionbar(player, "&cNot enough levels (&6" + player.getLevel() + "/" + enchants + "&c levels)");
                        return;
                    }

                    // final
                    EnchantmentsCombineEvent ece = new EnchantmentsCombineEvent(player, newItem);
                    Bukkit.getPluginManager().callEvent(ece);
                    if (!ece.isCancelled()) {
                        if (player.getGameMode() != GameMode.CREATIVE) player.setLevel(player.getLevel() - enchants);

                        if (overlap) dropItem(player, book);
                        top.setItem(combined0, XMaterial.RED_STAINED_GLASS_PANE.parseItem());
                        top.setItem(combinee0, XMaterial.RED_STAINED_GLASS_PANE.parseItem());
                        top.setItem(result0, ece.getResult());
                    }

                }

                // ingot & item

                else if (goodTypes(combined, combinee)) {
                    int enchants = combined.getEnchantments().size() + 2;
                    if (player.getLevel() < enchants) {
                        player.spigot().sendMessage(ACTION_BAR, new TextComponent("&cNot enough levels (&6" + player.getLevel() + "/" + enchants + "&c)"));
                        return;
                    }

                    Damageable damageable = (Damageable) (combined.getItemMeta() == null ? Bukkit.getItemFactory().getItemMeta(combined.getType()) : combined.getItemMeta());
                    damageable.setDamage(0);
                    combined.setItemMeta(damageable);

                    // final
                    player.setLevel(player.getLevel() - enchants);
                    top.setItem(result0, combined);
                    top.setItem(combined0, XMaterial.RED_STAINED_GLASS_PANE.parseItem());
                    top.setItem(combinee0, XMaterial.RED_STAINED_GLASS_PANE.parseItem());
                }
            }
        }
    }






    @EventHandler(priority = org.bukkit.event.EventPriority.LOWEST)
    public void onClose(InventoryCloseEvent ev) {
        if (ev.getInventory().getHolder() instanceof AnvilHolder) {
            if (ev.getInventory().getItem(combined0) != null) {
                if (!isPane(ev.getInventory().getItem(combined0))) {
                    ev.getPlayer().getInventory().addItem(ev.getInventory().getItem(combined0));
                }
            }

            if (ev.getInventory().getItem(combinee0) != null) {
                if (!isPane(ev.getInventory().getItem(combinee0))) {
                    ev.getPlayer().getInventory().addItem(ev.getInventory().getItem(combinee0));

                }
            }

            if (ev.getInventory().getItem(result0) != null) {
                if (!isPane(ev.getInventory().getItem(result0))) {
                    ev.getPlayer().getInventory().addItem(ev.getInventory().getItem(result0));

                }
            }
        }
    }

    //@EventHandler(priority = org.bukkit.event.EventPriority.LOWEST)
    //public void onQuit(PlayerQuitEvent ev) {
    //    if (ev.getPlayer().getOpenInventory().getTopInventory().getHolder() instanceof AnvilHolder) {
    //        Bukkit.getPluginManager().callEvent(new InventoryCloseEvent(ev.getPlayer().getOpenInventory()));
    //    }
    //}



    boolean hasConflicts(ItemStack stack) {
        for (Enchantment testable : stack.getEnchantments().keySet()) {
            for (Enchantment testSubject : stack.getEnchantments().keySet()) {

                if (testable.getKey().equals(testSubject.getKey())) continue;
                if (testable.conflictsWith(testSubject)) return true;

            }
        }
        return false;
    }


     */

}

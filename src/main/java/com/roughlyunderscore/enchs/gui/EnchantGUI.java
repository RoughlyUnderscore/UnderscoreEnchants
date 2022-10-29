package com.roughlyunderscore.enchs.gui;

/*
import com.cryptomorin.xseries.*;
import com.roughlyunderscore.enchantsapi.events.PreEnchantEvent;
import com.roughlyunderscore.enchs.UnderscoreEnchants;
import com.roughlyunderscore.enchs.util.Pair;
import com.roughlyunderscore.enchs.util.general.Utils;
import com.roughlyunderscore.enchs.util.holders.AnvilHolder;
import com.roughlyunderscore.enchs.util.holders.EnchantHolder;
import lombok.AllArgsConstructor;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.collections4.*;
import org.bukkit.*;
import org.bukkit.enchantments.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import java.util.*;

import static net.md_5.bungee.api.ChatMessageType.ACTION_BAR;

@AllArgsConstructor

public class EnchantGUI implements Listener {

    private final int itemSlot = 10;
    private final int resultSlot = 24;
    private final UnderscoreEnchants plugin;

    @EventHandler(priority = org.bukkit.event.EventPriority.LOWEST)
    public void onClickEvent(InventoryClickEvent ev) {
        if (!(ev.getWhoClicked() instanceof Player pl)) return;

        int result = resultSlot;
        int input = itemSlot;
        int book1 = result - 10;
        int book2 = result - 1;
        int book3 = result + 8;
        int slot = ev.getSlot();

        Inventory inv = ev.getClickedInventory();
        Inventory topInv = ev.getView().getTopInventory();

        ItemStack item = ev.getCurrentItem();

        if (inv == null || inv.getHolder() == null) return;
        if (!(ev.getWhoClicked() instanceof Player)) return;

        if (ev.getInventory().getHolder() == null) return;

        if (ev.getInventory().getHolder() instanceof EnchantHolder) { // clicked in the GUI

            if (slot == input) { // player clicked in the item slot
                if (!Utils.isPane(item) && Utils.validItem(item)) {
                    pl.getInventory().addItem(item);

                    inv.setItem(input, new ItemStack(XMaterial.PINK_STAINED_GLASS_PANE.parseMaterial()));
                    inv.setItem(book1, new ItemStack(XMaterial.CYAN_STAINED_GLASS_PANE.parseMaterial()));
                    inv.setItem(book2, new ItemStack(XMaterial.CYAN_STAINED_GLASS_PANE.parseMaterial()));
                    inv.setItem(book3, new ItemStack(XMaterial.CYAN_STAINED_GLASS_PANE.parseMaterial()));
                }
            }

            if (slot == result) { // player clicked in the result slot
                if (!Utils.isPane(item) && Utils.validItem(item)) {
                    pl.getInventory().addItem(item);

                    inv.setItem(input, new ItemStack(XMaterial.PINK_STAINED_GLASS_PANE.parseMaterial()));
                    inv.setItem(book1, new ItemStack(XMaterial.CYAN_STAINED_GLASS_PANE.parseMaterial()));
                    inv.setItem(book2, new ItemStack(XMaterial.CYAN_STAINED_GLASS_PANE.parseMaterial()));
                    inv.setItem(book3, new ItemStack(XMaterial.CYAN_STAINED_GLASS_PANE.parseMaterial()));
                    inv.setItem(result, new ItemStack(XMaterial.RED_STAINED_GLASS_PANE.parseMaterial()));
                }
            }



            if (slot == book1 || slot == book2 || slot == book3) {
                if (item == null) return;
                if (item.getType().equals(XMaterial.BOOK.parseMaterial()) || item.getType().equals(XMaterial.ENCHANTED_BOOK.parseMaterial())) {

                    int enchantments = inv.getItem(input).getEnchantments().size();
                    int requirement = 2 * (enchantments + 1);
                    for (int level : item.getEnchantments().values()) requirement += level;

                    if (pl.getLevel() < requirement && pl.getGameMode() != GameMode.CREATIVE) {
                        pl.spigot().sendMessage(ACTION_BAR, new TextComponent(Utils.format("&cNot enough levels! Needed: " + 2 * (enchantments + 1) + ". Present: " + pl.getLevel())));
                    } else {
                        if (pl.getLevel() >= requirement) pl.setLevel(pl.getLevel() - requirement);

                        ItemStack finalStack;
                        if (inv.getItem(input).getType().equals(Material.BOOK)) {
                            finalStack = new ItemStack(Material.ENCHANTED_BOOK);
                            ItemMeta preMeta = inv.getItem(input).getItemMeta();
                            finalStack.setItemMeta(preMeta);
                        } else finalStack = inv.getItem(input).clone();

                        ItemMeta finalMeta = finalStack.getItemMeta();

                        if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
                            if (finalMeta.hasLore()) {
                                finalMeta.setLore(ListUtils.union(finalMeta.getLore(), item.getItemMeta().getLore()));
                            } else {
                                finalMeta.setLore(item.getItemMeta().getLore());
                            }
                        }
                        finalMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

                        finalStack.setItemMeta(finalMeta);
                        item.getEnchantments().forEach(finalStack::addUnsafeEnchantment);

                        //<editor-fold desc="Event preparation, jeez">
                        Map<Enchantment, Integer> addedEnchantments0 = item.getEnchantments();
                        Pair<Enchantment, Integer> addedEnchantments = Pair.empty();
                        for (Map.Entry<Enchantment, Integer> entry : addedEnchantments0.entrySet()) {
                            addedEnchantments = Pair.of(entry.getKey(), entry.getValue());
                            // Technically "item" should only have one enchantment, so the pair should get it...
                        }
                        PreEnchantEvent pee = new PreEnchantEvent(pl, addedEnchantments.getKey(), addedEnchantments.getValue(), finalStack); // haha pee
                        Bukkit.getPluginManager().callEvent(pee);
                        //</editor-fold>
                        if (!pee.isCancelled()) {

                            inv.setItem(result, pee.getItem());

                            inv.setItem(input, new ItemStack(XMaterial.PINK_STAINED_GLASS_PANE.parseMaterial()));
                            inv.setItem(book1, new ItemStack(XMaterial.CYAN_STAINED_GLASS_PANE.parseMaterial()));
                            inv.setItem(book2, new ItemStack(XMaterial.CYAN_STAINED_GLASS_PANE.parseMaterial()));
                            inv.setItem(book3, new ItemStack(XMaterial.CYAN_STAINED_GLASS_PANE.parseMaterial()));

                            if (plugin.getConfig().getBoolean("fireworks-on-enchants")) {
                                Location l = pl.getLocation();
                                Firework fw = (Firework) l.getWorld().spawnEntity(l, EntityType.FIREWORK);
                                FireworkMeta fwmeta = fw.getFireworkMeta();
                                fwmeta.setPower(1);
                                fwmeta.addEffect(FireworkEffect.builder().withColor(Color.fromRGB(
                                    new Random().nextInt(255),
                                    new Random().nextInt(255),
                                    new Random().nextInt(255)
                                ))
                                  .trail(true)
                                  .flicker(true)
                                  .with(FireworkEffect.Type.STAR)
                                  .build());
                                fw.setFireworkMeta(fwmeta);
                            }
                        }
                    }
                }
            }
        }

        if (inv.getHolder() instanceof Player && topInv.getHolder() instanceof EnchantHolder) {

            if (item == null || item.getType() == Material.AIR) return;
            if (!item.getEnchantments().isEmpty()) return;
            // clicked in the player's inv while in GUI

            if (Utils.isPane(topInv.getItem(input)) && Utils.isPane(topInv.getItem(result))) {

                if (item.getType().toString().toUpperCase().contains("CROSSBOW") ||
                        item.getType().toString().toUpperCase().contains("ELYTRA") ||
                        item.getType().toString().toUpperCase().contains("TRIDENT")
                ) {
                    pl.closeInventory();
                    pl.spigot().sendMessage(ACTION_BAR, new TextComponent(Utils.format("&cThis GUI doesn't support this item! Opening the default GUI...")));
                    pl.playSound(pl.getLocation(), XSound.ENTITY_VILLAGER_NO.parseSound(), SoundCategory.MASTER, 1, 1);

                    Bukkit.getScheduler().runTaskLater(plugin, () -> pl.openEnchanting(null, true), 20);

                    pl.playSound(pl.getLocation(), XSound.BLOCK_NOTE_BLOCK_PLING.parseSound(), SoundCategory.MASTER, 1, 1);
                    return;
                }

                if (!item.getEnchantments().isEmpty() && item.getEnchantments().size() >= plugin.getConfig().getInt("enchantmentLimit")) return;

                List<Enchantment> enchs = Utils.getTypicalEnchantments(item);

                ItemStack firstSuggestion = new ItemStack(Material.ENCHANTED_BOOK),
                        secondSuggestion = new ItemStack(Material.ENCHANTED_BOOK),
                        thirdSuggestion = new ItemStack(Material.ENCHANTED_BOOK);

                topInv.setItem(input, item);
                pl.getInventory().setItem(ev.getSlot(), null); // remove(item);

                ArrayList<Enchantment> list = Utils.getPossibleEnchantments(item, enchs, 3);
                Enchantment e1 = list.get(0), e2 = list.get(1), e3 = list.get(2);

                firstSuggestion.addUnsafeEnchantment(e1, new Random().nextInt(e1.getMaxLevel()) + 1);
                secondSuggestion.addUnsafeEnchantment(e2, new Random().nextInt(e2.getMaxLevel()) + 1);
                thirdSuggestion.addUnsafeEnchantment(e3, new Random().nextInt(e3.getMaxLevel()) + 1);

                int firstEnchLevel = firstSuggestion.getEnchantmentLevel(e1);
                int secondEnchLevel = secondSuggestion.getEnchantmentLevel(e2);
                int thirdEnchLevel = thirdSuggestion.getEnchantmentLevel(e3);

                String firstEnchLore = Utils.format("&7" + Utils.getName(e1) + " " + Utils.toRoman(firstEnchLevel));
                String secondEnchLore = Utils.format("&7" + Utils.getName(e2) + " " + Utils.toRoman(secondEnchLevel));
                String thirdEnchLore = Utils.format("&7" + Utils.getName(e3) + " " + Utils.toRoman(thirdEnchLevel));

                ItemMeta firstMeta = firstSuggestion.getItemMeta();
                ItemMeta secondMeta = secondSuggestion.getItemMeta();
                ItemMeta thirdMeta = thirdSuggestion.getItemMeta();

                firstMeta.removeItemFlags();

                firstMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                secondMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                thirdMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

                firstMeta.setLore(Collections.singletonList(firstEnchLore));
                secondMeta.setLore(Collections.singletonList(secondEnchLore));
                thirdMeta.setLore(Collections.singletonList(thirdEnchLore));

                firstSuggestion.setItemMeta(firstMeta);
                secondSuggestion.setItemMeta(secondMeta);
                thirdSuggestion.setItemMeta(thirdMeta);

                topInv.setItem(book1, firstSuggestion);
                topInv.setItem(book2, secondSuggestion);
                topInv.setItem(book3, thirdSuggestion);

            }


        }

    }

    @EventHandler(priority = org.bukkit.event.EventPriority.LOWEST) @SuppressWarnings("all")
    public void click(InventoryClickEvent ev) {
        if (ev.getInventory().getHolder() instanceof EnchantHolder) {
            ev.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void cancelClicked(InventoryClickEvent ev) {
        if (ev.getInventory().getHolder() instanceof EnchantHolder) ev.setCancelled(true);
        if (ev.getInventory().getHolder() instanceof AnvilHolder) ev.setCancelled(true);
    }

    @EventHandler(priority = org.bukkit.event.EventPriority.LOWEST)
    public void onCloseEvent(InventoryCloseEvent ev) {
        if (ev.getInventory().getHolder() instanceof EnchantHolder) {

            if (Utils.validItem(ev.getInventory().getItem(resultSlot)) && !Utils.isPane(ev.getInventory().getItem(resultSlot))) {
                ev.getPlayer().getInventory().addItem(ev.getInventory().getItem(resultSlot));
            }

            if (Utils.validItem(ev.getInventory().getItem(itemSlot)) && !Utils.isPane(ev.getInventory().getItem(itemSlot))) {
                ev.getPlayer().getInventory().addItem(ev.getInventory().getItem(itemSlot));
            }
        }
    }

    @EventHandler(priority = org.bukkit.event.EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent ev) {
        Bukkit.getServer().getPluginManager().callEvent(new InventoryCloseEvent(ev.getPlayer().getOpenInventory()));
    }

}
*/
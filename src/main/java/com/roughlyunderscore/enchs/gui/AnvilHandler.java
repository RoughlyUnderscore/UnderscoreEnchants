package com.roughlyunderscore.enchs.gui;

import com.cryptomorin.xseries.XMaterial;
import com.roughlyunderscore.enchantsapi.events.EnchantmentsCombineEvent;
import com.roughlyunderscore.enchs.UnderscoreEnchants;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.roughlyunderscore.enchs.util.general.Utils.*;
import static com.roughlyunderscore.enchs.util.general.PlayerUtils.*;

@Data
public class AnvilHandler implements Listener {
    private final UnderscoreEnchants plugin;

    @EventHandler
    public void onAnvil(PrepareAnvilEvent ev) {
        if (ev.getInventory().getItem(0) == null || ev.getInventory().getItem(1) == null) {
            return;
        }
        if (ev.getResult() != null) {
            return;
        }
        Player player = (Player) ev.getView().getPlayer();

        ItemStack item1 = ev.getInventory().getItem(0);
        ItemStack item2 = ev.getInventory().getItem(1);

        Material type = item2.getType();
        if (
                !type.isItem() // ???
                && type != Material.NAME_TAG
        ) return;

        if (!isSecondItemValid(item1, item2)) return;

        ItemStack newItem = new ItemStack(item1.getType());
        ItemMeta newMeta = Bukkit.getItemFactory().getItemMeta(newItem.getType());

        Map<Enchantment, Integer> item1Enchants = new ConcurrentHashMap<>(item1.getEnchantments());
        Map<Enchantment, Integer> item2Enchants = new ConcurrentHashMap<>(item2.getEnchantments());
        Map<Enchantment, Integer> allEnchants = new ConcurrentHashMap<>();

        for (Map.Entry<Enchantment, Integer> combinedEntry : item1Enchants.entrySet()) {
            for (Map.Entry<Enchantment, Integer> combineeEntry : item2Enchants.entrySet()) {

                // o^2 complexity? didn't ask lol
                if (!combinedEntry.getKey().equals(combineeEntry.getKey())) continue;

                // pass

                item1Enchants.remove(combinedEntry.getKey());
                item2Enchants.remove(combineeEntry.getKey());

                if (combinedEntry.getValue().equals(combineeEntry.getValue())) {
                    allEnchants.put(combinedEntry.getKey(), Math.min(combinedEntry.getValue() + 1, combinedEntry.getKey().getMaxLevel()));
                    continue;
                }

                allEnchants.put(combinedEntry.getKey(), Math.min(Math.max(combinedEntry.getValue(), combineeEntry.getValue()), combinedEntry.getKey().getMaxLevel()));
            }
        }

        allEnchants.putAll(item1Enchants);
        allEnchants.putAll(item2Enchants);

        // generic
        newMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        // making sure no enchantments conflict
        // the method is weirdly coded so i inverse it


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

        if (!allEnchants.isEmpty() && allEnchants.size() >= plugin.getConfig().getInt("enchantmentLimit")) {
            sendActionbar(player, "&cYou can't have more than " + plugin.getConfig().getInt("enchantmentLimit") + " enchantments on an item!");
            return;
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

            ev.setResult(newItem);
           // ev.getInventory().setItem(0, null);
           // ev.getInventory().setItem(1, null);
        }
    }

    boolean isSecondItemValid(ItemStack first, ItemStack second) {
        Material item = first.getType(), ingot = second.getType();

        if (item == ingot) return true;

        if (Tag.PLANKS.isTagged(ingot) && plugin.getPlankRepariable().contains(item)) return true;
        if (is(ingot, XMaterial.LEATHER) && plugin.getLeatherRepariable().contains(item)) return true;
        if (plugin.getCobbleTypes().contains(ingot) && plugin.getCobbleRepariable().contains(item)) return true;
        if (is(ingot, XMaterial.IRON_INGOT) && plugin.getIronRepariable().contains(item)) return true;
        if (is(ingot, XMaterial.GOLD_INGOT) && plugin.getGoldRepariable().contains(item)) return true;
        if (is(ingot, XMaterial.DIAMOND) && plugin.getDiamondRepariable().contains(item)) return true;
        if (is(ingot, XMaterial.NETHERITE_INGOT) && plugin.getNetheriteRepariable().contains(item)) return true;
        if (is(ingot, XMaterial.SCUTE) && is(item, XMaterial.TURTLE_HELMET)) return true;
        return is(ingot, XMaterial.PHANTOM_MEMBRANE) && is(item, XMaterial.ELYTRA);
    }

}

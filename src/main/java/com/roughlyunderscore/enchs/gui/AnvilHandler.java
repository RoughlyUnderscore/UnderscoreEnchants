package com.roughlyunderscore.enchs.gui;

import com.cryptomorin.xseries.XMaterial;
import com.roughlyunderscore.enchantsapi.events.EnchantmentsCombineEvent;
import com.roughlyunderscore.enchs.UnderscoreEnchants;
import com.roughlyunderscore.enchs.util.Constants;
import com.roughlyunderscore.enchs.util.general.Utils;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.roughlyunderscore.enchs.util.general.PlayerUtils.*;
import static com.roughlyunderscore.enchs.util.general.Utils.*;

@Data
public class AnvilHandler implements Listener {
  private final UnderscoreEnchants plugin;

  @EventHandler
  public void onAnvil(final PrepareAnvilEvent ev) {
    // <editor-fold desc="Preparations">
    // if (!(ev.getInventory() instanceof AnvilInventory inv)) return;
    final AnvilInventory inv = ev.getInventory();

    final ItemStack item1 = inv.getItem(0);
    final ItemStack item2 = inv.getItem(1);

    // Can't combine the items yet - better yet, a player might be trying to scam the anvil by requesting a result and removing one of the items.
    if (item1 == null || item2 == null || item1.getType() == Material.AIR || item2.getType() == Material.AIR) {
      ev.setResult(null);
      return;
    }
    // The items have already been combined
    if (inv.getItem(2) != null) return;
    // Name tags should be handled with vanilla
    if (item1.getType() == Material.NAME_TAG || item2.getType() == Material.NAME_TAG) return;

    final Player player = (Player) ev.getView().getPlayer();

    final Material type = item2.getType();
    if (!type.isItem()) return; // ?? I guess I need this but why
    

    switch (itemMatchesSecondItem(item1, item2)) {
      case IDENTICAL_ITEMS, BOOK -> {
        if (item1.getType() == XMaterial.BOOK.parseMaterial()) item1.setType(XMaterial.ENCHANTED_BOOK.parseMaterial());

        ItemStack newItem = new ItemStack(item1.getType()); // The first item is always the result item (example: in repairs the second item isn't the same)
        // final ItemMeta newMeta = Bukkit.getItemFactory().getItemMeta(newItem.getType());

        final Map<Enchantment, Integer> item1Enchants = new ConcurrentHashMap<>(item1.getEnchantments());
        final Map<Enchantment, Integer> item2Enchants = new ConcurrentHashMap<>(item2.getEnchantments());

        try {
          newItem = Utils.generateEnchantedItemWithMergedEnchantments(newItem, item1Enchants, item2Enchants, plugin);
        } catch (IllegalArgumentException ex) {
          sendActionbar(player, ex.getMessage(), plugin);
          return;
        }

        // UnderscoreEnchants calculates the anvil cost differently.
        final int enchants = 1 + newItem.getEnchantments().size() * 3 + (int) Math.pow(newItem.getEnchantments().size(), 1.6);
        plugin.getServer().getScheduler().runTask(plugin, () -> inv.setRepairCost(enchants));

        // final
        final EnchantmentsCombineEvent ece = new EnchantmentsCombineEvent(player, newItem);
        Bukkit.getPluginManager().callEvent(ece);
        if (!ece.isCancelled()) {
          final ItemStack finalNewItem = newItem; // lambdas :rolled_eyes:
          plugin.getServer().getScheduler().runTaskLater(plugin, () -> inv.setItem(2, finalNewItem), 1);
          // TODO degrade anvil: 12% chance
          // maybe degrading the anvil is not necessary? it should be accounted for with vanilla...

          // We don't need to set the other items to null, because the player might just think again.
          // inv.setItem(0, null);
          // inv.setItem(1, null);
        }
      }
      case REPAIR -> {
        ItemStack newItem = Utils.repair(item1, 0.2 * item2.getAmount());
        plugin.getServer().getScheduler().runTask(plugin, () -> inv.setRepairCost(1));

        final EnchantmentsCombineEvent ece = new EnchantmentsCombineEvent(player, newItem);
        Bukkit.getPluginManager().callEvent(ece);
        if (!ece.isCancelled()) {
          final ItemStack finalNewItem = newItem; // lambdas :rolled_eyes:
          plugin.getServer().getScheduler().runTaskLater(plugin, () -> inv.setItem(2, finalNewItem), 1);

        }
        // TODO test REPAIR and BOOKS
      }
      default -> {
      }
    }

  }

  AnvilPlacementType itemMatchesSecondItem(ItemStack first, ItemStack second) {
    Material item = first.getType(), ingot = second.getType();

    if (
      item == ingot ||
        (is(ingot, XMaterial.ENCHANTED_BOOK) || (is(item, XMaterial.BOOK) && is(item, XMaterial.ENCHANTED_BOOK))) // book/enchanted book + enchanted book
    )
      return AnvilPlacementType.IDENTICAL_ITEMS;

    if (
      (Tag.PLANKS.isTagged(ingot) && Constants.REPAIRED_BY_PLANKS.contains(item)) || // wooden item + planks
        (is(ingot, XMaterial.LEATHER) && Constants.REPAIRED_BY_LEATHER.contains(item)) || // leather item + leather
        (is(ingot, XMaterial.IRON_INGOT) && Constants.REPAIRED_BY_IRON.contains(item)) || // iron item + iron ingot
        (is(ingot, XMaterial.GOLD_INGOT) && Constants.REPAIRED_BY_GOLD.contains(item)) || // gold item + gold ingot
        (is(ingot, XMaterial.DIAMOND) && Constants.REPAIRED_BY_DIAMOND.contains(item)) || // diamond item + diamond
        (is(ingot, XMaterial.NETHERITE_INGOT) && Constants.REPAIRED_BY_NETHERITE.contains(item)) || // netherite item + netherite ingot
        (is(ingot, XMaterial.SCUTE) && is(item, XMaterial.TURTLE_HELMET)) || // turtle helmet + scute
        (is(ingot, XMaterial.PHANTOM_MEMBRANE) && is(item, XMaterial.ELYTRA)) // elytra + phantom membrane
    )
      return AnvilPlacementType.REPAIR;

    if ((is(ingot, XMaterial.ENCHANTED_BOOK)))
      return AnvilPlacementType.BOOK; // it can't be book + book, because that's handled in the first if statement

    return AnvilPlacementType.INVALID;
  }

  // For internal purposes
  private enum AnvilPlacementType {
    /**
     * When two identical items are placed into the anvil. Currently has identical behavior with {@code BOOK}.
     */
    IDENTICAL_ITEMS,

    /**
     * When an item and a book are placed into the anvil. Currently has identical behavior with {@code IDENTICAL_ITEMS}.
     */
    BOOK,

    /**
     * When an item and a successive repair ingot are placed into the anvil
     */
    REPAIR,

    /**
     * When the item combination could not be detected or is invalid.
     */
    INVALID
  }

}

package com.roughlyunderscore.enchs.gui;

import com.roughlyunderscore.enchantsapi.events.PreEnchantEvent;
import com.roughlyunderscore.enchs.UnderscoreEnchants;
import com.roughlyunderscore.enchs.util.general.Utils;
import lombok.Data;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.roughlyunderscore.enchs.util.general.PlayerUtils.*;

@Data
public class EnchantTableHandler implements Listener {
  private final UnderscoreEnchants plugin;

  @EventHandler
  public void preEnchant(final PrepareItemEnchantEvent ev) {
    // It seems like replacing offers is not possible right now? Super unfortunate.
    // Unless I fuck with reflection, that is!

    // Nevermind, I gave up for now. This does indeed replace the offers, but doesn't go much further.
		/*

		ItemStack item = ev.getItem();

		// Just generate some random possible enchantments.
		List<Enchantment> enchs = Utils.getTypicalEnchantments(item);
		List<Enchantment> list = Utils.getPossibleEnchantments(item, enchs, 3);
		Enchantment ench1 = list.get(0), ench2 = list.get(1), ench3 = list.get(2);

		// Save old offers just in case!
		EnchantmentOffer[] oldOffers = ev.getOffers();
		Random random = new Random();

		plugin.getUnderscoreLogger().info("Found offers: " + oldOffers[0].getEnchantment().getName() + ", " + oldOffers[1].getEnchantment().getName() + ", " + oldOffers[2].getEnchantment().getName());

		EnchantmentOffer[] offers = Utils.generateOffers(
			Triple.of(ench1, random.nextInt(ench1.getMaxLevel()) + 1, oldOffers[0].getCost()),
			Triple.of(ench2, random.nextInt(ench2.getMaxLevel()) + 1, oldOffers[0].getCost()),
			Triple.of(ench3, random.nextInt(ench3.getMaxLevel()) + 1, oldOffers[0].getCost())
		);

		plugin.getUnderscoreLogger().info("Generated offers: " + offers[0].getEnchantment().getName() + ", " + offers[1].getEnchantment().getName() + ", " + offers[2].getEnchantment().getName());

		try {
			Field offersField = ev.getClass().getDeclaredField("offers");
			offersField.setAccessible(true);
			offersField.set(ev, offers);
		} catch (Exception e) {
			e.printStackTrace();
		}

		plugin.getUnderscoreLogger().info("Event offers: " + ev.getOffers()[0].getEnchantment().getName() + ", " + ev.getOffers()[1].getEnchantment().getName() + ", " + ev.getOffers()[2].getEnchantment().getName());

		 */
  }

  @EventHandler
  public void onEnchant(final EnchantItemEvent ev) {
    final Player enchanter = ev.getEnchanter();

    if (!plugin.getConfigValues().ADD_ENCHANTMENTS_TO_ENCHANTMENT_TABLE) return;
    final int chance = plugin.getConfigValues().ENCHANTMENT_TABLE_CHANCE;

    final ItemStack item = ev.getItem();

    if (item.getEnchantments().size() == plugin.getConfigValues().MAXIMUM_ENCHANTMENTS) { // This will probably never happen, because the enchantment table only allows unenchanted items
      enchanter.sendMessage(PlaceholderAPI.setPlaceholders(enchanter, plugin.getMessages().OVER_THE_LIMIT));
      ev.setCancelled(true);                                                            // but perhaps someone will use a 0 limit.
      return;
    }

    final List<Enchantment> enchs = Utils.getTypicalEnchantments(item);
    final List<Enchantment> list = Utils.getPossibleEnchantments(item, enchs, 1);

    final Enchantment enchantment = list.get(0);
    final int level = new Random().nextInt(enchantment.getMaxLevel()) + 1;

    final Map<Enchantment, Integer> currentEnchants = item.getEnchantments(),
      newEnchants = ev.getEnchantsToAdd();

    try {
      ItemStack resultItem = item.clone();
      if (new Random().nextInt(chance) + 1 == chance)
        resultItem = Utils.generateEnchantedItemWithMergedEnchantments(item, Utils.mergeEnchantments(currentEnchants, newEnchants), Map.of(enchantment, level), plugin);

      final PreEnchantEvent pee = new PreEnchantEvent(ev.getEnchanter(), enchantment, level, resultItem); // haha pee
      Bukkit.getPluginManager().callEvent(pee);

      if (!pee.isCancelled()) ev.getInventory().setItem(0, resultItem);
    } catch (final IllegalArgumentException ex) {
      sendActionbar(ev.getEnchanter(), ex.getMessage(), plugin);
    }

  }
}

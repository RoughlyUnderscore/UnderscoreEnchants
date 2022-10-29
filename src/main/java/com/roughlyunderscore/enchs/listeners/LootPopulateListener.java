package com.roughlyunderscore.enchs.listeners;

import com.roughlyunderscore.enchs.UnderscoreEnchants;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Minecart;
import org.bukkit.event.EventHandler;
import static org.bukkit.event.EventPriority.HIGHEST;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static com.roughlyunderscore.enchs.util.general.Utils.*;

@Data
/*
Populate the loot where possible
 */
public class LootPopulateListener implements Listener {

	private final UnderscoreEnchants plugin;

	@EventHandler(ignoreCancelled = true, priority = HIGHEST)
	public void onLootPopulate(LootGenerateEvent ev) {
		if (!plugin.getConfig().getBoolean("populateLoot")) return;

		Entity ent = ev.getEntity();
		InventoryHolder ih = ev.getInventoryHolder();
		int chance = plugin.getConfig().getInt("populateLootChance");

		if (!(ih instanceof Chest) && !(ent instanceof Minecart)) return;

		// Unsure if this would work, as I never pass the loot value back.
		// May need to create a new collection
		ev.getLoot().forEach(loot -> {
			if (!isEnchantable(loot)) return;
			if (new Random().nextInt(chance) + 1 != chance) return;

			Enchantment enchantment = getPossibleEnchantments(loot, getTypicalEnchantments(loot), 1).get(0);
			int level = ThreadLocalRandom.current().nextInt(enchantment.getStartLevel(), enchantment.getMaxLevel() + 1);

			loot.setItemMeta(enchant(loot, enchantment, level).getKey().getItemMeta());
		});
	}

	@EventHandler(ignoreCancelled = true, priority = HIGHEST)
	public void onVillagerAcquireTrades(VillagerAcquireTradeEvent ev) {
		if (!plugin.getConfig().getBoolean("populateVillagers")) return;

		MerchantRecipe recipe0 = ev.getRecipe();
		ItemStack item = recipe0.getResult();

		int chance = plugin.getConfig().getInt("populateVillagersChance");

		if (new Random().nextInt(chance) + 1 != chance) return;
		if (!isEnchantable(item)) return;

		int uses = recipe0.getUses(), maxUses = recipe0.getMaxUses();
		boolean reward = recipe0.hasExperienceReward();
		int exp = recipe0.getVillagerExperience();
		float coefficient = recipe0.getPriceMultiplier();

		List<Enchantment> enchantments = getPossibleEnchantments(item, getTypicalEnchantments(item), 1);
		Enchantment enchantment = enchantments.isEmpty() ? Enchantment.BINDING_CURSE : enchantments.get(0);
		int level = ThreadLocalRandom.current().nextInt(enchantment.getStartLevel(), enchantment.getMaxLevel() + 1);

		item = enchant(item, enchantment, level).getKey();

		MerchantRecipe recipe = new MerchantRecipe(item, uses, maxUses, reward, exp, coefficient);
		recipe.setIngredients(recipe0.getIngredients());
		ev.setRecipe(recipe);
	}

	@EventHandler(ignoreCancelled = true, priority = HIGHEST)
	public void tunaIsLove(PlayerFishEvent ev) {
		if (!plugin.getConfig().getBoolean("populateFish")) return;

		int chance = plugin.getConfig().getInt("populateFishChance");
		if (new Random().nextInt(chance) + 1 != chance) return;

		if (ev.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
		if (!(ev.getCaught() instanceof Item it)) return;

		ItemStack result = it.getItemStack();
		if (!isEnchantable(result)) return;

		Enchantment enchantment = getPossibleEnchantments(result, getTypicalEnchantments(result), 1).get(0);
		int level = ThreadLocalRandom.current().nextInt(enchantment.getStartLevel(), enchantment.getMaxLevel() + 1);
		result = enchant(result, enchantment, level).getKey();

		it.setItemStack(result);
	}
}

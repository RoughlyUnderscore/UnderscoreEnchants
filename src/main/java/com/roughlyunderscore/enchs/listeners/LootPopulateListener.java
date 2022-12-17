package com.roughlyunderscore.enchs.listeners;

import com.roughlyunderscore.enchs.UnderscoreEnchants;
import lombok.Data;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Minecart;
import org.bukkit.event.EventHandler;
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
import static org.bukkit.event.EventPriority.HIGHEST;

@Data
/*
Populate the loot where possible
 */
public class LootPopulateListener implements Listener {

  private final UnderscoreEnchants plugin;

  @EventHandler(ignoreCancelled = true, priority = HIGHEST)
  public void onLootPopulate(final LootGenerateEvent ev) {
    if (!plugin.getConfig().getBoolean("populateLoot")) return;

    final Entity ent = ev.getEntity();
    final InventoryHolder ih = ev.getInventoryHolder();
    final int chance = plugin.getConfig().getInt("populateLootChance");

    if (!(ih instanceof Chest) && !(ent instanceof Minecart)) return;

    // Unsure if this would work, as I never pass the loot value back.
    // May need to create a new collection
    ev.getLoot().forEach(loot -> {
      if (!isEnchantable(loot)) return;
      if (new Random().nextInt(chance) + 1 != chance) return;

      final Enchantment enchantment = getPossibleEnchantments(loot, getTypicalEnchantments(loot), 1).get(0);
      final int level = ThreadLocalRandom.current().nextInt(enchantment.getStartLevel(), enchantment.getMaxLevel() + 1);

      loot.setItemMeta(enchant(loot, enchantment, level).getKey().getItemMeta());
    });
  }

  @EventHandler(ignoreCancelled = true, priority = HIGHEST)
  public void onVillagerAcquireTrades(final VillagerAcquireTradeEvent ev) {
    if (!plugin.getConfig().getBoolean("populateVillagers")) return;

    final MerchantRecipe recipe0 = ev.getRecipe();
    ItemStack item = recipe0.getResult();

    final int chance = plugin.getConfig().getInt("populateVillagersChance");

    if (new Random().nextInt(chance) + 1 != chance) return;
    if (!isEnchantable(item)) return;

    final int uses = recipe0.getUses(), maxUses = recipe0.getMaxUses();
    final boolean reward = recipe0.hasExperienceReward();
    final int exp = recipe0.getVillagerExperience();
    final float coefficient = recipe0.getPriceMultiplier();

    final List<Enchantment> enchantments = getPossibleEnchantments(item, getTypicalEnchantments(item), 1);
    final Enchantment enchantment = enchantments.isEmpty() ? Enchantment.BINDING_CURSE : enchantments.get(0);
    final int level = ThreadLocalRandom.current().nextInt(enchantment.getStartLevel(), enchantment.getMaxLevel() + 1);

    item = enchant(item, enchantment, level).getKey();

    final MerchantRecipe recipe = new MerchantRecipe(item, uses, maxUses, reward, exp, coefficient);
    recipe.setIngredients(recipe0.getIngredients());
    ev.setRecipe(recipe);
  }

  @EventHandler(ignoreCancelled = true, priority = HIGHEST)
  public void tunaIsLove(final PlayerFishEvent ev) {
    if (!plugin.getConfig().getBoolean("populateFish")) return;

    final int chance = plugin.getConfig().getInt("populateFishChance");
    if (new Random().nextInt(chance) + 1 != chance) return;

    if (ev.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
    if (!(ev.getCaught() instanceof Item it)) return;

    ItemStack result = it.getItemStack();
    if (!isEnchantable(result)) return;

    final Enchantment enchantment = getPossibleEnchantments(result, getTypicalEnchantments(result), 1).get(0);
    final int level = ThreadLocalRandom.current().nextInt(enchantment.getStartLevel(), enchantment.getMaxLevel() + 1);
    result = enchant(result, enchantment, level).getKey();

    it.setItemStack(result);
  }
}

package com.roughlyunderscore.enchs.enchants.abstracts;

import com.roughlyunderscore.enchs.events.PlayerBowHitEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public abstract class BowHitEnchantment extends AbstractEnchantment implements Listener {
  public BowHitEnchantment(NamespacedKey key, String name, int maxLevel, EnchantmentTarget target) {
    super(key, name, maxLevel, 1, target, false, false, null, null);
  }

  public abstract void onHit(PlayerBowHitEvent ev);

  @EventHandler(priority = EventPriority.HIGHEST)
  public void event(PlayerBowHitEvent ev) {
    if (!ev.isCancelled()) onHit(ev);
  }
}

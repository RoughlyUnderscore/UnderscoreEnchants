package com.roughlyunderscore.enchs.enchants.abstracts;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public abstract class RMBEnchantment extends AbstractEnchantment implements Listener {
  public RMBEnchantment(NamespacedKey key, String name, int maxLevel, EnchantmentTarget target) {
    super(key, name, maxLevel, 1, target, false, false, null, null);
  }

  public abstract void onRMB(PlayerInteractEvent ev);

  @EventHandler(priority = EventPriority.HIGHEST)
  public void event(PlayerInteractEvent ev) {
    onRMB(ev);
  }
}

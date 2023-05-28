package com.roughlyunderscore.enchs.enchants.abstracts;

import com.roughlyunderscore.enchs.enchants.UEnchant;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public abstract class RMBEntityEnchantment extends UEnchant implements Listener {
  public RMBEntityEnchantment(NamespacedKey key, String name, int maxLevel, EnchantmentTarget target) {
    super(key, name, maxLevel, 1, target, false, false, null, null);
  }

  public abstract void onRMB(PlayerInteractAtEntityEvent ev);

  @EventHandler(priority = EventPriority.HIGHEST)
  public void event(PlayerInteractAtEntityEvent ev) {
    onRMB(ev);
  }
}

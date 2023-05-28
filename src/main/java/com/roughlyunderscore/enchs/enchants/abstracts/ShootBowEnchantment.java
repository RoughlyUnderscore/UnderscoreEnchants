package com.roughlyunderscore.enchs.enchants.abstracts;

import com.roughlyunderscore.enchs.enchants.UEnchant;
import com.roughlyunderscore.enchs.events.PlayerShootBowEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public abstract class ShootBowEnchantment extends UEnchant implements Listener {
  public ShootBowEnchantment(NamespacedKey key, String name, int maxLevel, EnchantmentTarget target) {
    super(key, name, maxLevel, 1, target, false, false, null, null);
  }

  public abstract void onShoot(PlayerShootBowEvent ev);

  @EventHandler(priority = EventPriority.HIGHEST)
  public void event(PlayerShootBowEvent ev) {
    if (!ev.isCancelled()) onShoot(ev);
  }
}

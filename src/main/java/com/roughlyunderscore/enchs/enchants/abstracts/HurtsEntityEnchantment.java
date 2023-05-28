package com.roughlyunderscore.enchs.enchants.abstracts;

import com.roughlyunderscore.enchs.enchants.UEnchant;
import com.roughlyunderscore.enchs.events.PlayerHurtsEntityEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public abstract class HurtsEntityEnchantment extends UEnchant implements Listener {
  public HurtsEntityEnchantment(NamespacedKey key, String name, int maxLevel, EnchantmentTarget target) {
    super(key, name, maxLevel, 1, target, false, false, null, null);
  }

  public abstract void onAttack(PlayerHurtsEntityEvent ev);

  @EventHandler(priority = EventPriority.HIGHEST)
  public void event(PlayerHurtsEntityEvent ev) {
    if (!ev.isCancelled()) onAttack(ev);
  }
}

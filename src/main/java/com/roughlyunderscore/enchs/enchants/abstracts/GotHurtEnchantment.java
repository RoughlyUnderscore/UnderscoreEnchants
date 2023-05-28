package com.roughlyunderscore.enchs.enchants.abstracts;

import com.roughlyunderscore.enchs.enchants.UEnchant;
import com.roughlyunderscore.enchs.events.PlayerGotHurtEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public abstract class GotHurtEnchantment extends UEnchant implements Listener {
  public GotHurtEnchantment(NamespacedKey key, String name, int maxLevel, EnchantmentTarget target) {
    super(key, name, maxLevel, 1, target, false, false, null, null);
  }

  public abstract void onHurt(PlayerGotHurtEvent ev);

  @EventHandler(priority = EventPriority.HIGHEST)
  public void event(PlayerGotHurtEvent ev) {
    if (!ev.isCancelled()) onHurt(ev);
  }
}

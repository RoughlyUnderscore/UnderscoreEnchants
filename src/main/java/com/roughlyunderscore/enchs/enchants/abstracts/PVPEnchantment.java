package com.roughlyunderscore.enchs.enchants.abstracts;

import com.roughlyunderscore.enchs.events.PlayerPVPEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public abstract class PVPEnchantment extends AbstractEnchantment implements Listener {
  public PVPEnchantment(NamespacedKey key, String name, int maxLevel, EnchantmentTarget target) {
    super(key, name, maxLevel, 1, target, false, false, null, null);
  }

  public abstract void onPVP(PlayerPVPEvent ev);

  @EventHandler(priority = EventPriority.HIGHEST)
  public void event(PlayerPVPEvent ev) {
    if (!ev.isCancelled()) onPVP(ev);
  }
}

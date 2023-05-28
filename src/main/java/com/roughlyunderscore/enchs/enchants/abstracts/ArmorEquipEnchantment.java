package com.roughlyunderscore.enchs.enchants.abstracts;

import com.codingforcookies.armorequip.ArmorEquipEvent;
import com.roughlyunderscore.enchs.enchants.UEnchant;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public abstract class ArmorEquipEnchantment extends UEnchant implements Listener {
  public ArmorEquipEnchantment(NamespacedKey key, String name, int maxLevel, EnchantmentTarget target) {
    super(key, name, maxLevel, 1, target, false, false, null, null);
  }

  public abstract void onEquip(ArmorEquipEvent ev);

  @EventHandler(priority = EventPriority.HIGHEST)
  public void event(ArmorEquipEvent ev) {
    if (!ev.isCancelled()) onEquip(ev);
  }
}

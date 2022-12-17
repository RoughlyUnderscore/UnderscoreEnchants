package com.codingforcookies.armorequip;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseArmorEvent;

/**
 * @author Arnah
 * @since Feb 08, 2019
 */
public class DispenserArmorListener implements Listener {

    @EventHandler(priority = org.bukkit.event.EventPriority.LOWEST)
    public void dispenseArmorEvent(BlockDispenseArmorEvent event){
        ArmorType type = ArmorType.matchType(event.getItem());
        if (type == null) return;
        if (!(event.getTargetEntity() instanceof Player p)) return;

        ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(p, EquipMethod.DISPENSER, type, null, event.getItem());
        Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
        if(armorEquipEvent.isCancelled()) event.setCancelled(true);

    }
}
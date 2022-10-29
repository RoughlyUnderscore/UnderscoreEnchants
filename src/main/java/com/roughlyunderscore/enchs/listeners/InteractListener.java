package com.roughlyunderscore.enchs.listeners;

import com.cryptomorin.xseries.XMaterial;
import com.roughlyunderscore.enchs.UnderscoreEnchants;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

@AllArgsConstructor
public class InteractListener implements Listener {

    private final UnderscoreEnchants plugin;
/*
    @EventHandler(priority = org.bukkit.event.EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent ev) {

        if (ev.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (ev.getClickedBlock() == null) return;
            if (ev.getClickedBlock().getType() != XMaterial.ENCHANTING_TABLE.parseMaterial()) return;
            if (plugin.getConfig().getBoolean("replace-table-gui")) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Bukkit.dispatchCommand(ev.getPlayer(), "ue enchtable");
                    }
                }.runTaskLater(plugin, 2L);
            }
        }
    }

    @EventHandler(priority = org.bukkit.event.EventPriority.LOWEST)
    public void otherOnInteract(PlayerInteractEvent ev) {

        if (ev.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (ev.getClickedBlock() == null) return;
            if (!isAnvil(ev.getClickedBlock())) return;
            if (plugin.getConfig().getBoolean("replace-anvil-gui")) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Bukkit.dispatchCommand(ev.getPlayer(), "ue anvil");
                    }
                }.runTaskLater(plugin, 2L);
            }
        }
    }

    boolean isAnvil(Block b) {
        Material m = b.getType();
        return m == XMaterial.ANVIL.parseMaterial() ||
                m == XMaterial.CHIPPED_ANVIL.parseMaterial() ||
                m == XMaterial.DAMAGED_ANVIL.parseMaterial();
    }*/
}

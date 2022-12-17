package com.roughlyunderscore.enchs.listeners;

import com.roughlyunderscore.enchs.UnderscoreEnchants;
import lombok.AllArgsConstructor;
import org.bukkit.event.Listener;

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

package com.roughlyunderscore.enchs.listeners;

import com.cryptomorin.xseries.XSound;
import org.bukkit.SoundCategory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

// A method for... me?
public class JoinListener implements Listener {

  @EventHandler(priority = org.bukkit.event.EventPriority.LOWEST)
  public void onJoin(PlayerJoinEvent ev) {
    if (ev.getPlayer().getName().equalsIgnoreCase("CantEvenPVP")) {
      ev.getPlayer().sendMessage("Hey! This server uses UnderscoreEnchants.");
      ev.getPlayer().sendMessage("Ask the owner about their experience; suggest support.");
      ev.getPlayer().playSound(ev.getPlayer().getLocation(), XSound.ENTITY_ENDERMAN_TELEPORT.parseSound(), SoundCategory.MASTER, 1, 1);
    }
  }
}

package com.roughlyunderscore.enchs.listeners;

import com.roughlyunderscore.enchs.UnderscoreEnchants;
import lombok.AllArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

@AllArgsConstructor
// Just remove the player from gods if is present there
public class LeaveListener implements Listener {

  private final UnderscoreEnchants plugin;

  @EventHandler
  public void onLeave(PlayerQuitEvent ev) {
    if (plugin.getGods().containsKey(ev.getPlayer().getUniqueId())) {
      plugin.getGods().remove(ev.getPlayer().getUniqueId());
      ev.getPlayer().setInvulnerable(false);
    }
  }
}

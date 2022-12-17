package com.roughlyunderscore.enchs.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;

import static com.roughlyunderscore.enchs.util.general.Utils.*;

public class GeneralListener implements Listener {

  // PlayerPVPEvent handler
  @EventHandler
  public void playerPVPEvent(EntityDamageByEntityEvent ev) {
    if (arePlayersInEvent(ev)) {
      PlayerPVPEvent event = new PlayerPVPEvent((Player) ev.getDamager(), (Player) ev.getEntity(), ev.getCause(), ev.getDamage());
      event.setCancelled(ev.isCancelled());
      Bukkit.getPluginManager().callEvent(event);
    }
  }

  // PlayerGotHurtEvent handler
  @EventHandler
  public void playerGotHurtEvent(EntityDamageEvent ev) {
    if (ev instanceof PlayerGotHurtEvent || ev instanceof PlayerPVPEvent || ev instanceof PlayerHurtsEntityEvent ||
      ev instanceof PlayerBowHitEvent) return;
    if (!(ev.getEntity() instanceof Player player)) return;

    PlayerGotHurtEvent event = new PlayerGotHurtEvent(player, ev.getCause(), ev.getDamage());
    event.setCancelled(ev.isCancelled());
    Bukkit.getPluginManager().callEvent(event);

  }

  // PlayerShootBowEvent handler
  @EventHandler
  public void playerShootBowEvent(EntityShootBowEvent ev) {
    if (ev instanceof PlayerShootBowEvent) return;
    if (ev.getEntity() instanceof Player player && ev.getProjectile() instanceof Arrow arrow) {
      PlayerShootBowEvent event = new PlayerShootBowEvent(player, ev.getBow(), ev.getConsumable(), arrow,
        ev.getHand(), ev.getForce(), ev.shouldConsumeItem());
      event.setCancelled(ev.isCancelled());
      Bukkit.getPluginManager().callEvent(event);
    }
  }

  // PlayerHurtsEntityEvent handler
  @EventHandler
  public void playerHurtsEntityEvent(EntityDamageByEntityEvent ev) {
    if (ev instanceof PlayerHurtsEntityEvent) return;
    if (ev.getDamager() instanceof Player player) {
      PlayerHurtsEntityEvent event = new PlayerHurtsEntityEvent(player, ev.getEntity(), ev.getCause(), ev.getDamage());
      event.setCancelled(ev.isCancelled());
      Bukkit.getPluginManager().callEvent(event);
    }
  }

  // PlayerBowHitEvent listener
  @EventHandler
  public void playerBowHitEvent(EntityDamageByEntityEvent ev) {
    if (ev.getDamager() instanceof Arrow arrow && arrow.getShooter() instanceof Player damager && ev.getEntity() instanceof Player victim) {
      PlayerBowHitEvent event = new PlayerBowHitEvent(damager, victim, EntityDamageEvent.DamageCause.PROJECTILE, ev.getDamage());
      event.setCancelled(ev.isCancelled());
      Bukkit.getPluginManager().callEvent(event);
    }
  }

}

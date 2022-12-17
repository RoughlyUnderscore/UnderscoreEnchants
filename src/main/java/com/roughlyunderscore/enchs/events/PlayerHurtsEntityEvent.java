package com.roughlyunderscore.enchs.events;

import lombok.NonNull;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerHurtsEntityEvent extends EntityDamageByEntityEvent {
  private final Player damager;

  public PlayerHurtsEntityEvent(@NonNull Player damager, @NonNull Entity damagee, @NonNull EntityDamageEvent.DamageCause cause, double damage) {
    super(damager, damagee, cause, damage);
    this.damager = damager;
  }

  @NonNull
  public Player getDamager() {
    return damager;
  }
}

package com.roughlyunderscore.enchs.events;

import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerPVPEvent extends EntityDamageEvent {
  private final Player damager;
  private final Player victim;

  public PlayerPVPEvent(@NonNull Player damager, @NonNull Player damagee, @NonNull EntityDamageEvent.DamageCause cause, double damage) {
    super(damagee, cause, damage);
    this.damager = damager;
    this.victim = damagee;
  }

  /**
   * @return the PVP agressor.
   */
  @NonNull
  public Player getDamager() {
    return damager;
  }

  /**
   * @return the PVP victim.
   */
  @NonNull
  public Player getVictim() {
    return victim;
  }
}

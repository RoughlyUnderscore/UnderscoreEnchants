package com.roughlyunderscore.enchs.events;

import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerGotHurtEvent extends EntityDamageEvent {

  private final Player victim;

  public PlayerGotHurtEvent(@NonNull Player damagee, @NonNull EntityDamageEvent.DamageCause cause, double damage) {
    super(damagee, cause, damage);

    this.victim = damagee;
  }

  @NonNull
  public Player getVictim() {
    return victim;
  }
}

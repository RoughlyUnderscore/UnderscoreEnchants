package com.roughlyunderscore.enchs.events;

import lombok.NonNull;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class PlayerShootBowEvent extends EntityShootBowEvent {

  private final Player shooter;

  public PlayerShootBowEvent(@NonNull Player shooter, ItemStack bow, ItemStack consumable, @NonNull Entity projectile, @NonNull EquipmentSlot hand, float force, boolean consumeItem) {
    super(shooter, bow, consumable, projectile, hand, force, consumeItem);
    this.shooter = shooter;
  }

  public Player getShooter() {
    return this.shooter;
  }
}

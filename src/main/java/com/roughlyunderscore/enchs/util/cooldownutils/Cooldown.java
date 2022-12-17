package com.roughlyunderscore.enchs.util.cooldownutils;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.enchantments.Enchantment;

import java.util.UUID;

/*
do i have to explain this?
 */
@ToString
@EqualsAndHashCode
public class Cooldown {
  @Getter
  private long seconds;
  @Getter
  private final Enchantment enchantment;
  @Getter
  private final UUID uuid;

  public Cooldown(long seconds, Enchantment enchantment, UUID uuid) {
    this.seconds = seconds;
    this.enchantment = enchantment;
    this.uuid = uuid;
  }

  /**
   * @return true if cooldown is over
   */
  public boolean decrease() {
    if (seconds == 1) return true;
    else seconds--;
    return false;
  }
}

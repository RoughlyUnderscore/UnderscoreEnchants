package com.roughlyunderscore.enchs.util.cooldownutils;

import com.roughlyunderscore.enchs.UnderscoreEnchants;
import com.roughlyunderscore.enchs.util.general.PlayerUtils;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

/*
do i have to explain this?
 */
@ToString
@EqualsAndHashCode
@Builder(buildMethodName = "create")
public class ActionbarCooldown {
  @Getter
  private long seconds;
  @Getter
  private final String message;
  @Getter
  private final UUID uuid;
  @Getter
  private final UnderscoreEnchants plugin;

  public ActionbarCooldown(long seconds, String message, UUID uuid, UnderscoreEnchants plugin) {
    this.seconds = seconds;
    this.message = message;
    this.uuid = uuid;
    this.plugin = plugin;
  }

  /**
   * @return true if cooldown is over
   */
  public boolean decrease() {
    if (seconds == 1) return true;
    else {
      seconds--;
      Player player = Bukkit.getPlayer(uuid);
      if (player != null) PlayerUtils.sendActionbar(player, message, plugin);
    }
    return false;
  }
}


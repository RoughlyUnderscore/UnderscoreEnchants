package com.roughlyunderscore.enchs.parsers.action.events.pvp;

import com.roughlyunderscore.enchs.UnderscoreEnchants;
import com.roughlyunderscore.enchs.events.PlayerPVPEvent;
import com.roughlyunderscore.enchs.parsers.action.generic.doubleplayer.DoublePlayerActionParser;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

import static com.roughlyunderscore.enchs.parsers.action.ActionUtils.*;

@ToString @EqualsAndHashCode(callSuper = true)
public class PVPActionParser extends DoublePlayerActionParser {
  private final PlayerPVPEvent event;

  public PVPActionParser(final PlayerPVPEvent event, final Player damager, final Player victim, final String action, final UnderscoreEnchants plugin) {
    super(event, damager, victim, action, plugin);
    this.event = event;
  }

  @Override
  public void execute() {
    super.execute();

    final PVPActions action = PVPActions.lookup(getAction().split(" ")[0]);
    if (action == null) return;

    final Player player = getArgument();
    final UnderscoreEnchants plugin = getPlugin();

    final String[] args0 = parsePlaceholders(getAction(), player, getSecond(), plugin), args = Arrays.copyOfRange(args0, 0, args0.length); // args[0] IS REDUNDANT BUT I'M TOO LAZY TO DELETE IT!

    final BukkitRunnable runnable = new BukkitRunnable() {
      @Override
      public void run() {
        switch (action) {} // No actions as of 2.1 :(
      }
    };

    completeAction(runnable, args, plugin);

    // action.executeAction(getArgument(), getAction(), getPlugin());
  }

  @Override
  public String[] parsePlaceholders(String args, final Player first, final Player second, final UnderscoreEnchants plugin) {
    for (final String str : args.split(" ")) {
      switch (PVPActionPlaceholders.lookup(str)) {
        case null -> {}

        case DAMAGE -> args = args.replace(str, Double.toString(event.getDamage()));
      }
    }
    return super.parsePlaceholders(args, first, second, plugin);
  }
}

package com.roughlyunderscore.enchs.parsers.action.events.move;

import com.roughlyunderscore.enchs.UnderscoreEnchants;
import com.roughlyunderscore.enchs.parsers.action.generic.player.PlayerActionParser;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

import static com.roughlyunderscore.enchs.parsers.action.ActionUtils.*;

@ToString @EqualsAndHashCode(callSuper = true)
public class MoveActionParser extends PlayerActionParser {
  private final PlayerMoveEvent event;

  public MoveActionParser(final PlayerMoveEvent event, final Player argument, final String action, final UnderscoreEnchants plugin) {
    super(event, argument, action, plugin);
    this.event = event;
  }

  @Override
  public void execute() {
    super.execute();

    final MoveActions action = MoveActions.lookup(getAction().split(" ")[0]);
    if (action == null) return;

    final Player player = getArgument();
    final UnderscoreEnchants plugin = getPlugin();

    final String[] args0 = parsePlaceholders(getAction(), player, player, plugin), args = Arrays.copyOfRange(args0, 0, args0.length); // args[0] IS REDUNDANT BUT I'M TOO LAZY TO DELETE IT!

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
  public String[] parsePlaceholders(String args, final Player player, final UnderscoreEnchants plugin) {
    final Location from = event.getFrom(), to = event.getTo() == null ? from : event.getTo();
    for (final String str : args.split(" ")) {
      switch (MoveActionPlaceholders.lookup(str)) {
        case null -> {}

        case FROM_X -> args = args.replace(str, Double.toString(from.getX()));
        case FROM_Y -> args = args.replace(str, Double.toString(from.getY()));
        case FROM_Z -> args = args.replace(str, Double.toString(from.getZ()));
        case TO_X -> args = args.replace(str, Double.toString(to.getX()));
        case TO_Y -> args = args.replace(str, Double.toString(to.getY()));
        case TO_Z -> args = args.replace(str, Double.toString(to.getZ()));
        case FROM_YAW -> args = args.replace(str, Double.toString(from.getYaw()));
        case FROM_PITCH -> args = args.replace(str, Double.toString(from.getPitch()));
        case TO_YAW -> args = args.replace(str, Double.toString(to.getYaw()));
        case TO_PITCH -> args = args.replace(str, Double.toString(to.getPitch()));
        case FROM_TYPE -> args = args.replace(str, from.getBlock().getType().name());
        case TO_TYPE -> args = args.replace(str, to.getBlock().getType().name());
      }
    }
    return super.parsePlaceholders(args, player, plugin);
  }
}

package com.roughlyunderscore.enchs.parsers.action.events.interact.player;

import com.roughlyunderscore.enchs.UnderscoreEnchants;
import com.roughlyunderscore.enchs.parsers.action.generic.player.PlayerActionParser;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.Set;

import static com.roughlyunderscore.enchs.parsers.action.ActionUtils.*;

@ToString @EqualsAndHashCode(callSuper = true)
public class InteractActionParser extends PlayerActionParser {
  private final PlayerInteractEvent event;

  public InteractActionParser(final PlayerInteractEvent event, final Player argument, final String action, final UnderscoreEnchants plugin) {
    super(event, argument, action, plugin);
    this.event = event;
  }

  @Override
  public void execute() {
    super.execute();

    final InteractActions action = InteractActions.lookup(getAction().split(" ")[0]);
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
    final Block block = event.getClickedBlock() == null ? player.getTargetBlock(Set.of(), 5) : event.getClickedBlock();
    for (final String str : args.split(" ")) {
      switch (InteractActionPlaceholders.lookup(str)) {
        case null -> {}

        case BLOCK_X -> args = args.replace(str, Double.toString(block.getX()));
        case BLOCK_Y -> args = args.replace(str, Double.toString(block.getY()));
        case BLOCK_Z -> args = args.replace(str, Double.toString(block.getZ()));
        case BLOCK_MATERIAL -> args = args.replace(str, block.getType().name());
      }
    }
    return super.parsePlaceholders(args, player, plugin);
  }
}

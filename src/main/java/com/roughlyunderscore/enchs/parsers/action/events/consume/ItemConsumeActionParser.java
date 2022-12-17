package com.roughlyunderscore.enchs.parsers.action.events.consume;

import com.roughlyunderscore.enchs.UnderscoreEnchants;
import com.roughlyunderscore.enchs.parsers.action.generic.player.PlayerActionParser;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

import static com.roughlyunderscore.enchs.parsers.action.ActionUtils.*;

@ToString
@EqualsAndHashCode(callSuper = true)
public class ItemConsumeActionParser extends PlayerActionParser {
  private final PlayerItemConsumeEvent event;

  public ItemConsumeActionParser(final PlayerItemConsumeEvent event, final Player argument, final String action, final UnderscoreEnchants plugin) {
    super(event, argument, action, plugin);
    this.event = event;
  }

  @Override
  public void execute() {
    super.execute();

    final ItemConsumeActions action = ItemConsumeActions.lookup(getAction().split(" ")[0]);
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
    for (final String str : args.split(" ")) {
      switch (ItemConsumeActionPlaceholders.lookup(str)) {
        case null -> {} // No placeholders as of 2.1 :(

        case ITEM_NAME -> args = args.replace(str, event.getItem().getType().name());
      }
    }
    return super.parsePlaceholders(args, player, plugin);
  }
}

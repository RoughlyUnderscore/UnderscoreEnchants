package com.roughlyunderscore.enchs.parsers.action.events.damage.entity;

import com.roughlyunderscore.enchs.UnderscoreEnchants;
import com.roughlyunderscore.enchs.events.PlayerHurtsEntityEvent;
import com.roughlyunderscore.enchs.parsers.action.generic.entity.EntityActionParser;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

import static com.roughlyunderscore.enchs.parsers.action.ActionUtils.*;

@ToString
@EqualsAndHashCode(callSuper = true)
public class PlayerHurtsEntityActionParser extends EntityActionParser {
  private final PlayerHurtsEntityEvent event;

  public PlayerHurtsEntityActionParser(final PlayerHurtsEntityEvent event, final Player argument, final Entity ent, final String action, final UnderscoreEnchants plugin) {
    super(event, argument, ent, action, plugin);
    this.event = event;
  }

  @Override
  public void execute() {
    final PlayerHurtsEntityActions action = PlayerHurtsEntityActions.lookup(getAction().split(" ")[0]);
    if (action == null) return;

    final Player player = getArgument();
    final UnderscoreEnchants plugin = getPlugin();

    final String[] args0 = parsePlaceholders(getAction(), player, player, plugin), args = Arrays.copyOfRange(args0, 0, args0.length); // args[0] IS REDUNDANT BUT I'M TOO LAZY TO DELETE IT!

    new EntityActionParser(getEvent(), player, getEntity(), getAction(), plugin).execute(); // try to parse Entity actions as well

    final BukkitRunnable runnable = new BukkitRunnable() {
      @Override
      public void run() {
        switch (action) {
          // No actions as of 2.1
        }
      }
    };

    completeAction(runnable, args, plugin);

    // action.executeAction(getArgument(), getAction(), getPlugin());
  }

  @Override
  public String[] parsePlaceholders(String args, final Player player, final UnderscoreEnchants plugin) {
    for (final String str : args.split(" ")) {
      switch (PlayerHurtsEntityActionPlaceholders.lookup(str)) {
        case null -> {
        }

        case DAMAGE -> args = args.replace(str, Double.toString(event.getDamage()));

        // No placeholders as of 2.1
      }
    }

    return super.parsePlaceholders(args, player, plugin);
  }
}

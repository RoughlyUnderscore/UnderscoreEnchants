package com.roughlyunderscore.enchs.parsers.action.events.pvp;

import com.roughlyunderscore.enchs.UnderscoreEnchants;
import com.roughlyunderscore.enchs.events.PlayerPVPEvent;
import com.roughlyunderscore.enchs.parsers.action.generic.doubleplayer.DoublePlayerActionParser;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;

import static com.roughlyunderscore.enchs.util.general.Utils.*;

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

    // calculating the delay and chance
    final String[] splitAction = getAction().split(" ");
    final String[] lastArgument = splitAction[splitAction.length - 1].split(";");

    long delay = 0, chance = 100;

    if (arrayOfStringsContainsPartly(lastArgument, "delay:"))
      delay = (long)
        clamp(0, 100, parseL(Arrays.stream(lastArgument)
          .filter(st -> st.startsWith("delay:"))
          .findFirst()
          .orElseThrow(() -> new IllegalArgumentException("Could not find delay but it's apparently present in the argument postfix!"))
          .split(":")[1]));

    if (arrayOfStringsContainsPartly(lastArgument, "chance:"))
      chance = (long)
        clamp(0, 100, parseL(Arrays.stream(lastArgument)
          .filter(st -> st.startsWith("chance:"))
          .findFirst()
          .orElseThrow(() -> new IllegalArgumentException("Could not find delay but it's apparently present in the argument postfix!"))
          .split(":")[1]));

    if (Math.random() * 100 >= chance) return;


    Bukkit.getScheduler().runTaskLater(plugin, () -> {
      final String[] args0 = parseDoublePlayerPlaceholders(getAction(), player, getSecond(), plugin), args = Arrays.copyOfRange(args0, 0, args0.length); // args0 IS REDUNDANT BUT I'M TOO LAZY TO DELETE IT!
      switch (action) {} // No actions as of 2.1 :(
    }, delay);

    // completeAction(runnable, args, plugin);

    // action.executeAction(getArgument(), getAction(), getPlugin());
  }

  @Override
  public String[] parseDoublePlayerPlaceholders(String args, final Player first, final Player second, final UnderscoreEnchants plugin) {
    args = arrayToString(super.parseDoublePlayerPlaceholders(args, first, second, plugin), " ");
    for (final String str : args.split("[(\\)\\+\\*\\-\\/ ]")) {
      switch (PVPActionPlaceholders.lookup(str)) {
        case null -> {}

        case DAMAGE -> args = args.replace(str, Double.toString(event.getDamage()));
      }
    }
    return args.split(" ");
  }
}

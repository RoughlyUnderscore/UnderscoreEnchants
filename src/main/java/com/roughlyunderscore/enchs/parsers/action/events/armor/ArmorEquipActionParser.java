package com.roughlyunderscore.enchs.parsers.action.events.armor;

import com.codingforcookies.armorequip.ArmorEquipEvent;
import com.roughlyunderscore.enchs.UnderscoreEnchants;
import com.roughlyunderscore.enchs.parsers.action.generic.player.PlayerActionParser;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;

import static com.roughlyunderscore.enchs.util.general.Utils.*;

@ToString
@EqualsAndHashCode(callSuper = true)
public class ArmorEquipActionParser extends PlayerActionParser {
  private final ArmorEquipEvent event;
  public ArmorEquipActionParser(final ArmorEquipEvent event, final Player argument, final String action, final UnderscoreEnchants plugin) {
    super(event, argument, action, plugin);
    this.event = event;
  }

  @Override
  public void execute() {
    super.execute();

    final ArmorEquipActions action = ArmorEquipActions.lookup(getAction().split(" ")[0]);
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
      final String[] args0 = parsePlaceholders(getAction(), player, plugin), args = Arrays.copyOfRange(args0, 0, args0.length); // args0 IS REDUNDANT BUT I'M TOO LAZY TO DELETE IT!

      switch (action) {} // No actions as of 2.1 :(
    }, delay);

    // completeAction(runnable, args, plugin);

    // action.executeAction(getArgument(), getAction(), getPlugin());
  }

  @Override
  public String[] parsePlaceholders(String args, final Player player, final UnderscoreEnchants plugin) {
    args = arrayToString(super.parsePlaceholders(args, player, plugin), " ");
    for (final String str : args.split("[(\\)\\+\\*\\-\\/ ]")) {
      switch (ArmorEquipActionPlaceholders.lookup(str)) {
        case null, default -> {} // No placeholders as of 2.1 :(
        // also wtf
      }
    }
    return args.split(" ");
  }
}

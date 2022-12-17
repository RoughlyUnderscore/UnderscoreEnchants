package com.roughlyunderscore.enchs.parsers.action.generic;

import com.roughlyunderscore.enchs.UnderscoreEnchants;
import lombok.Data;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import static com.roughlyunderscore.enchs.util.general.Utils.*;

@Data
public abstract class AbstractActionParser<T> {
  @Getter
  private final Event event;
  @Getter
  private final T argument;
  @Getter
  private final String action;
  @Getter
  private final UnderscoreEnchants plugin;

  /**
   * Executes the action with provided data. This method is <b>action-dependent</b> and must be overridden.
   * This method assumes that the arguments
   * are sufficient (e.g. has the address key & value for set-pdc), otherwise
   * throws a {@link java.lang.ArrayIndexOutOfBoundsException}.
   */
  public abstract void execute();

  /**
   * Parses the placeholders, found in the string. This method is <b>action-dependent</b> and must be overridden.
   * This method also parses the mathematical expressions.
   *
   * @param arg The placeholders to parse.
   * @param pl The player to parse the placeholders for.
   * @param plugin The plugin instance.
   * @return The parsed placeholders.
   */
  public abstract String[] parsePlaceholders(final String arg, final Player pl, final UnderscoreEnchants plugin);

  /**
   *
   * @param arg
   * @param first
   * @param second
   * @param plugin
   * @return
   */
  public String[] parsePlaceholders(final String arg, final Player first, final Player second, final UnderscoreEnchants plugin) {
    return parsePlaceholders(
      arrayToString(
        parsePlaceholders(arg, first, plugin),
        " "
      ),
      second,
      plugin
    );
  }
}

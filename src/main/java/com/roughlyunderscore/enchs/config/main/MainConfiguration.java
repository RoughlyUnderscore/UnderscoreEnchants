package com.roughlyunderscore.enchs.config.main;

import com.roughlyunderscore.enchs.UnderscoreEnchants;
import de.exlll.configlib.*;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.File;
import java.nio.file.Path;

/**
 * @since 2.2
 */
@Configuration @SuppressWarnings("unused")
public class MainConfiguration {

  private Misc misc = new Misc(
    true,
    true,
    24
  );

  private Enchantments enchantments = new Enchantments(
    true,
    99999
  );

  private Populate populate = new Populate(
    true,
    true,
    true,
    7,
    7,
    7
  );

  private EnchantmentTable enchantmentTable = new EnchantmentTable(
    true,
    3
  );

  private Debug debug = new Debug(
    false,
    true
  );


  @Comment("This is the prefix for all messages sent by the plugin.")
  private String prefix = "&8[&6&lUnderscoreEnchants&8] &r";

  @Comment("This is the language to use.")
  private String language = "en";







  // SECTIONS

  record Misc(
    @Comment({
      "This section is for miscellaneous UnderscoreEnchants settings.",
      "These properties normally only affect the server administrators and not the players,",
      "and the performance barely changes when changing these values.",
      "",
      "Whether to require an economy plugin to be installed for the plugin to work"
    })
    boolean requireVault,

    @Comment("Whether to use bStats Metrics")
    boolean bStats,

    @Comment("The frequency of the updater in hours")
    int updater
  ) {}

  record Populate(
    @Comment({
      "This section controls the world enchantment population.",
      "It's not too difficult to get the hang of it.",
      "Feel free to tweak it to your liking.",
      "",
      "Whether to populate villager trades with enchantments"
    })
    boolean villagerTrades,

    @Comment("Whether to populate fishing loot with enchantments")
    boolean fishLoot,

    @Comment("Whether to populate chest loot with enchantments")
    boolean chestLoot,

    @Comment({
      "The chance of an enchantment being added to a villager trade (imagine this value as X in 1/x chance to populate)",
      "This should be a positive non-zero integer!",
    })
    int villagerTradesChance,

    @Comment({
      "The chance of an enchantment being added to fishing loot (imagine this value as X in 1/x chance to populate)",
      "This should be a positive non-zero integer!",
    })
    int fishLootChance,

    @Comment({
      "The chance of an enchantment being added to chest loot (imagine this value as X in 1/x chance to populate)",
      "This should be a positive non-zero integer!",
    })
    int chestLootChance
  ) {}

  record Enchantments(
    @Comment({
      "This section is about general enchantment settings.",
      "Feel free to tweak them to your liking.",
      "",
      "Whether to indicate about an enchantment being activated"
    })
    boolean activatedMessage,

    @Comment("The maximum amount of enchantments that can be applied to an item")
    int limit
  ) {}

  record EnchantmentTable(
    @Comment({
      "This section is about the enchantment table.",
      "Feel free to tweak them to your liking.",
      "",
      "Whether to add extra enchantments when enchanting with the enchantment table.",
      "If disabled, the only way to get custom enchantments is with commands."
    })
    boolean addExtraEnchantment,

    @Comment({
      "The chance of an extra enchantment being added.",
      "This should be a positive non-zero integer!",
    })
    int extraEnchantmentChance
  ) {}

  record Debug(
    @Comment({
      "This section is for advanced users only.",
      "Please do not change these values unless you know",
      "what you're doing or you're asked to do so by the developer.",
      "",
      "Whether to enable debug mode"
    })
    boolean debugMode,

    @Comment("Whether to suppress the \"getMainHand()\" warning")
    boolean suppressGetMainHand
  ) {}
}

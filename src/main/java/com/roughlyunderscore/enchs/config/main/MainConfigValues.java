package com.roughlyunderscore.enchs.config.main;

import com.roughlyunderscore.enchs.UnderscoreEnchants;
import lombok.ToString;
import org.bukkit.configuration.file.FileConfiguration;

import static com.roughlyunderscore.enchs.util.general.Utils.*;

@ToString
public final class MainConfigValues {
  // misc.*, enchantments.*
  public final boolean REQUIRE_VAULT;
  public final boolean BSTATS_ENABLED;
  public final boolean ACTIVATED_MESSAGE_ENABLED;
  
  public final int UPDATER_INTERVAL;
  public final int MAXIMUM_ENCHANTMENTS;
  
  // populate.*
  public final boolean POPULATE_VILLAGER_TRADES;
  public final boolean POPULATE_FISHING_LOOT;
  public final boolean POPULATE_CHEST_LOOT;
  
  public final int POPULATE_VILLAGER_TRADES_CHANCE; // 1 in X
  public final int POPULATE_FISHING_LOOT_CHANCE; // 1 in X
  public final int POPULATE_CHEST_LOOT_CHANCE; // 1 in X
  
  // enchantmentTable.*
  public final boolean ADD_ENCHANTMENTS_TO_ENCHANTMENT_TABLE;
  public final int ENCHANTMENT_TABLE_CHANCE; // 1 in X
  
  // debug.*
  public final boolean DEBUG;
  public final boolean SUPPRESS_GET_MAIN_HAND;
  
  // unleveled config values
  public final String PREFIX;
  public final String LANGUAGE;
  
  public MainConfigValues(final UnderscoreEnchants plugin) {
    final FileConfiguration config = plugin.getConfig();
    REQUIRE_VAULT = config.getBoolean("misc.require-vault", config.getBoolean("misc.requireVault", true)); // backwards compatibility
    BSTATS_ENABLED = config.getBoolean("misc.bStats", true);
    ACTIVATED_MESSAGE_ENABLED = config.getBoolean("misc.activatedMessageEnabled", true);
    
    UPDATER_INTERVAL = config.getInt("misc.updaterInterval", 24);
    MAXIMUM_ENCHANTMENTS = config.getInt("misc.maximumEnchantments", 99999);
    
    POPULATE_VILLAGER_TRADES = config.getBoolean("populate.villagerTrades", true);
    POPULATE_FISHING_LOOT = config.getBoolean("populate.fishLoot", true);
    POPULATE_CHEST_LOOT = config.getBoolean("populate.chestLoot", true);
    
    POPULATE_VILLAGER_TRADES_CHANCE = config.getInt("populate.villagerTradesChance", 7);
    POPULATE_FISHING_LOOT_CHANCE = config.getInt("populate.fishLootChance", 7);
    POPULATE_CHEST_LOOT_CHANCE = config.getInt("populate.chestLootChance", 7);
    
    ADD_ENCHANTMENTS_TO_ENCHANTMENT_TABLE = config.getBoolean("enchantmentTable.addEnchantments", true);
    ENCHANTMENT_TABLE_CHANCE = config.getInt("enchantmentTable.chance", 3);
    
    DEBUG = config.getBoolean("debug.debugMode", false);
    SUPPRESS_GET_MAIN_HAND = config.getBoolean("debug.suppressGetMainHand", true);
    
    PREFIX = format(config.getString("prefix", "&c&l[&r&6UnderscoreEnchants&c&l] "));
    LANGUAGE = format(config.getString("messages.lang", "en"));
  }
}

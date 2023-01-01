package com.roughlyunderscore.enchs.enchants.new_sys;

import com.roughlyunderscore.enchs.UnderscoreEnchants;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@ToString @EqualsAndHashCode(callSuper = false)
public class UEnchant extends Enchantment implements Listener {
  private final NamespacedKey key;
  private final String name;
  private final int maxLevel;
  private final int startLevel;
  private final EnchantmentTarget target;
  private final boolean isCursed;
  private final boolean isTreasure;
  private final List<Enchantment> conflicts;
  private final List<ItemStack> canEnchant;
  private final Event event;
  private final BukkitRunnable action;
  private final UnderscoreEnchants plugin;

  public UEnchant(final Enchantment enchantment, final Event event, final BukkitRunnable action, final UnderscoreEnchants plugin) {
    this(
      enchantment.getKey(),
      enchantment.getName(),
      enchantment.getMaxLevel(),
      enchantment.getStartLevel(),
      enchantment.getItemTarget(),
      enchantment.isCursed(),
      enchantment.isTreasure(),
      null,
      null,
      event,
      action,
      plugin
    );
  }

  public UEnchant(final NamespacedKey key,
                  final String name,
                  final int maxLevel,
                  final int startLevel,
                  final EnchantmentTarget target,
                  final boolean isCursed,
                  final boolean isTreasure,
                  final List<Enchantment> conflicts,
                  final List<ItemStack> canEnchant,
                  final Event event,
                  final BukkitRunnable action,
                  final UnderscoreEnchants plugin
  ) {
    super(key);
    this.key = key;
    this.name = name;
    this.maxLevel = maxLevel;
    this.startLevel = startLevel;
    this.target = target;
    this.isCursed = isCursed;
    this.isTreasure = isTreasure;
    this.conflicts = conflicts;
    this.canEnchant = canEnchant;
    this.event = event;
    this.action = action;
    this.plugin = plugin;
  }

  @NonNull
  @Override
  public NamespacedKey getKey() {
    return key;
  }

  @NotNull
  @Override
  public String getName() {
    return name;
  }

  @Override
  public int getMaxLevel() {
    return maxLevel;
  }

  @Override
  public int getStartLevel() {
    return startLevel;
  }

  @NotNull
  @Override
  public EnchantmentTarget getItemTarget() {
    return target;
  }

  @Override
  public boolean isTreasure() {
    return isTreasure;
  }

  @Override
  public boolean isCursed() {
    return isCursed;
  }

  @Override
  public boolean conflictsWith(@NotNull final Enchantment other) {
    return conflicts != null && conflicts.contains(other);
  }

  @Override
  public boolean canEnchantItem(@NotNull final ItemStack item) {
    return canEnchant == null || canEnchant.contains(item);
  }

  @EventHandler
  public void call(final Event event) {
    if (event.getEventName().equals(this.event.getEventName())) action.runTask(plugin);
  }
}

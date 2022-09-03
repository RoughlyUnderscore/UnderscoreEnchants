package com.roughlyunderscore.enchs.enchants.abstracts;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import java.util.List;

@ToString
@EqualsAndHashCode(callSuper = false)
/*
A class that extends Enchantment for my own convenience.
 */
public class AbstractEnchantment extends Enchantment implements Listener {

    private final NamespacedKey key;
    private final String name;
    private final int maxLevel;
    private final int startLevel;
    private final EnchantmentTarget target;
    private final boolean isCursed;
    private final boolean isTreasure;
    private final List<Enchantment> conflicts;
    private final List<ItemStack> canEnchant;

    public AbstractEnchantment(Enchantment enchantment) {
        this(
                enchantment.getKey(),
                enchantment.getName(),
                enchantment.getMaxLevel(),
                enchantment.getStartLevel(),
                enchantment.getItemTarget(),
                enchantment.isCursed(),
                enchantment.isTreasure(),
                null,
                null
        );
    }

    public AbstractEnchantment(NamespacedKey key, String name, int maxLevel, int startLevel, EnchantmentTarget target, boolean isCursed, boolean isTreasure, List<Enchantment> conflicts, List<ItemStack> canEnchant) {
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
    }

    public @NonNull NamespacedKey getKey() {
        return key;
    }
    @Override
    public @NonNull String getName() {
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
    @Override
    public @NonNull EnchantmentTarget getItemTarget() {
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
    public boolean conflictsWith(@NonNull Enchantment other) {
        return conflicts != null && conflicts.contains(other);
    }
    @Override
    public boolean canEnchantItem(@NonNull ItemStack item) {
        return canEnchant == null || canEnchant.contains(item);
    }
}

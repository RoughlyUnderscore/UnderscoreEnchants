package com.roughlyunderscore.enchs.util.data;

import com.roughlyunderscore.enchs.UnderscoreEnchants;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;

import java.util.Locale;
import java.util.Objects;

/*
This is a way of storing an Enchantment and a lot of necessary data in one class.
 */
@EqualsAndHashCode
public class DetailedEnchantment {
    private final NamespacedKey key;

    public DetailedEnchantment(String key, UnderscoreEnchants plugin) {
        this.key = new NamespacedKey(plugin, key);
    }

    public DetailedEnchantment(NamespacedKey key) {
        this.key = key;
    }

    public DetailedEnchantment(String key) {
        switch (key.toLowerCase(Locale.ROOT)) {
            case "wrong_level", "wrong_name" -> this.key = NamespacedKey.minecraft(key.toLowerCase(Locale.ROOT) + "_underscore_enchants");
            default -> throw new IllegalArgumentException("DetailedEnchantment was attempted to be initialized with an unknown string value: " + key);
        }
    }

    public String getCommandName() {
        return getName().toLowerCase(Locale.ROOT).replace(" ", "_");
    }
    public Enchantment getEnchantment() {
        return Enchantment.getByKey(key);
    }
    public NamespacedKey getKey() {
        return this.key;
    }
    public String getName() {
        return this.getEnchantment().getName();
    }

    @Override
    public String toString() {
        return "DetailedEnchantment{" +
                "key=" + key +
                ",name=" + this.getEnchantment().getName() +
                '}';
    }

}

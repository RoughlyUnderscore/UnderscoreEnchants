package com.roughlyunderscore.enchs.enchants;

import lombok.Data;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

@Data
/*
This is a class for the levels that are created in the configuration.
 */
public class EnchantmentLevel {
  private final int level;
  private final double chance;
  private final int cooldown;
  private final @NonNull List<String> action;
  private final List<String> conditions;
  private final @Nullable String flag;

  public static EnchantmentLevel empty() {
    return new EnchantmentLevel(1, 100, 0, Collections.emptyList(), Collections.emptyList(), "");
  }
}

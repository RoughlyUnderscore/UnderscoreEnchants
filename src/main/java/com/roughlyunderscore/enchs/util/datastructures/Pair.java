package com.roughlyunderscore.enchs.util.datastructures;

import lombok.Data;

@Data
public class Pair<K, V> {
  private final K key;
  private final V value;

  public static <K, V> Pair<K, V> of(K k, V v) {
    return new Pair<>(k, v);
  }

  public static <K, V> Pair<K, V> empty() {
    return new Pair<>(null, null);
  }
}

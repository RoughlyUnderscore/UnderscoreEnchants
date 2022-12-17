package com.roughlyunderscore.enchs.parsers.condition.generic;

import com.roughlyunderscore.enchs.UnderscoreEnchants;
import lombok.Data;
import lombok.Getter;

@Data
public abstract class AbstractConditionParser<T> {
  @Getter
  private final T arg;
  @Getter
  private final String condition;
  @Getter
  private final UnderscoreEnchants plugin;

  public abstract boolean evaluate();
}

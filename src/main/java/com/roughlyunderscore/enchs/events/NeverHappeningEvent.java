package com.roughlyunderscore.enchs.events;

import lombok.NonNull;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

// for my own convenience
public class NeverHappeningEvent extends Event {
  @NonNull
  @Override
  public HandlerList getHandlers() {
    return new HandlerList();
  }
}

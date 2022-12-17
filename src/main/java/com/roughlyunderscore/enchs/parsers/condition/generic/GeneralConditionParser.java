package com.roughlyunderscore.enchs.parsers.condition.generic;

import com.codingforcookies.armorequip.ArmorEquipEvent;
import com.roughlyunderscore.enchs.UnderscoreEnchants;
import com.roughlyunderscore.enchs.events.*;
import com.roughlyunderscore.enchs.parsers.condition.events.armor.ArmorConditionParser;
import com.roughlyunderscore.enchs.parsers.condition.events.block.BlockBreakConditionParser;
import com.roughlyunderscore.enchs.parsers.condition.events.bow.BowHitConditionParser;
import com.roughlyunderscore.enchs.parsers.condition.events.damage.PlayerGotHurtConditionParser;
import com.roughlyunderscore.enchs.parsers.condition.events.damage.PlayerHurtsEntityConditionParser;
import com.roughlyunderscore.enchs.parsers.condition.events.interact.player.InteractConditionParser;
import com.roughlyunderscore.enchs.parsers.condition.events.interact.entity.InteractEntityConditionParser;
import com.roughlyunderscore.enchs.parsers.condition.events.item.ItemBreakConditionParser;
import com.roughlyunderscore.enchs.parsers.condition.events.consume.ItemConsumeConditionParser;
import com.roughlyunderscore.enchs.parsers.condition.events.move.MoveConditionParser;
import com.roughlyunderscore.enchs.parsers.condition.events.projectile.ShootBowConditionParser;
import com.roughlyunderscore.enchs.parsers.condition.events.pvp.PVPConditionParser;
import com.roughlyunderscore.enchs.parsers.condition.events.sneak.SneakConditionParser;
import lombok.experimental.UtilityClass;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.*;

@UtilityClass
public class GeneralConditionParser {

  /**
   * Required for a preparatory parser
   */
  public boolean parseCondition(Event event0, String condition, UnderscoreEnchants plugin, String... player) {
    if (condition == null || condition.isEmpty() || condition.isBlank()) return true;

    if (event0 instanceof PlayerPVPEvent event) {

      if (player.length == 1 && (player[0].equalsIgnoreCase("victim") || player[0].equalsIgnoreCase("damager"))) {
        condition = switch (player[0].toLowerCase()) {
          case "victim" -> PlaceholderAPI.setPlaceholders(event.getVictim(), condition);
          case "damager" -> PlaceholderAPI.setPlaceholders(event.getDamager(), condition);
          default -> condition;
        };
      }
      return parseCondition(event, condition, plugin);
    } else if (event0 instanceof ArmorEquipEvent event) {
      condition = PlaceholderAPI.setPlaceholders(event.getPlayer(), condition);
      return parseCondition(event, condition, plugin);
    } else if (event0 instanceof BlockBreakEvent event) {
      condition = PlaceholderAPI.setPlaceholders(event.getPlayer(), condition);
      return parseCondition(event, condition, plugin);
    } else if (event0 instanceof PlayerItemBreakEvent event) {
      condition = PlaceholderAPI.setPlaceholders(event.getPlayer(), condition);
      return parseCondition(event, condition, plugin);
    } else if (event0 instanceof PlayerItemConsumeEvent event) {
      condition = PlaceholderAPI.setPlaceholders(event.getPlayer(), condition);
      return parseCondition(event, condition, plugin);
    } else if (event0 instanceof PlayerInteractAtEntityEvent event) {
      condition = PlaceholderAPI.setPlaceholders(event.getPlayer(), condition);
      return parseCondition(event, condition, plugin);
    } else if (event0 instanceof PlayerInteractEvent event) {
      condition = PlaceholderAPI.setPlaceholders(event.getPlayer(), condition);
      return parseCondition(event, condition, plugin);
    } else if (event0 instanceof PlayerMoveEvent event) {
      condition = PlaceholderAPI.setPlaceholders(event.getPlayer(), condition);
      return parseCondition(event, condition, plugin);
    } else if (event0 instanceof PlayerGotHurtEvent event) {
      condition = PlaceholderAPI.setPlaceholders(event.getVictim(), condition);
      return parseCondition(event, condition, plugin);
    } else if (event0 instanceof PlayerHurtsEntityEvent event) {
      condition = PlaceholderAPI.setPlaceholders(event.getDamager(), condition);
      return parseCondition(event, condition, plugin);
    } else if (event0 instanceof PlayerShootBowEvent event) {
      condition = PlaceholderAPI.setPlaceholders(event.getShooter(), condition);
      return parseCondition(event, condition, plugin);
    } else if (event0 instanceof PlayerToggleSneakEvent event) {
      condition = PlaceholderAPI.setPlaceholders(event.getPlayer(), condition);
      return parseCondition(event, condition, plugin);
    } else if (event0 instanceof PlayerBowHitEvent event) {
      if (player.length == 1 && (player[0].equalsIgnoreCase("victim") || player[0].equalsIgnoreCase("damager"))) {
        condition = switch (player[0].toLowerCase()) {
          case "victim" -> PlaceholderAPI.setPlaceholders(event.getVictim(), condition);
          case "damager" -> PlaceholderAPI.setPlaceholders(event.getDamager(), condition);
          default -> condition;
        };
      }
      return parseCondition(event, condition, plugin);
    }
    return false;
  }

  public boolean parseCondition(PlayerPVPEvent ev, String condition0, UnderscoreEnchants plugin) {
    return new PVPConditionParser(ev.getDamager(), ev.getVictim(), condition0, plugin, ev).evaluate();
  }

  public boolean parseCondition(PlayerBowHitEvent ev, String condition0, UnderscoreEnchants plugin) {
    return new BowHitConditionParser(ev.getDamager(), ev.getVictim(), condition0, plugin, ev).evaluate();
  }

  public boolean parseCondition(ArmorEquipEvent ev, String condition0, UnderscoreEnchants plugin) {
    return new ArmorConditionParser(ev.getPlayer(), condition0, plugin, ev).evaluate();
  }

  public boolean parseCondition(BlockBreakEvent ev, String condition0, UnderscoreEnchants plugin) {
    return new BlockBreakConditionParser(ev.getPlayer(), condition0, plugin, ev).evaluate();
  }

  public boolean parseCondition(PlayerItemBreakEvent ev, String condition0, UnderscoreEnchants plugin) {
    return new ItemBreakConditionParser(ev.getPlayer(), condition0, plugin, ev).evaluate();
  }

  public boolean parseCondition(PlayerItemConsumeEvent ev, String condition0, UnderscoreEnchants plugin) {
    return new ItemConsumeConditionParser(ev.getPlayer(), condition0, plugin, ev).evaluate();
  }

  public boolean parseCondition(PlayerInteractAtEntityEvent ev, String condition0, UnderscoreEnchants plugin) {
    return new InteractEntityConditionParser(ev.getPlayer(), condition0, plugin, ev).evaluate();
  }

  public boolean parseCondition(PlayerInteractEvent ev, String condition0, UnderscoreEnchants plugin) {
    return new InteractConditionParser(ev.getPlayer(), condition0, plugin, ev).evaluate();
  }

  public boolean parseCondition(PlayerMoveEvent ev, String condition0, UnderscoreEnchants plugin) {
    return new MoveConditionParser(ev.getPlayer(), condition0, plugin, ev).evaluate();
  }

  public boolean parseCondition(PlayerGotHurtEvent ev, String condition0, UnderscoreEnchants plugin) {
    return new PlayerGotHurtConditionParser(ev.getVictim(), condition0, plugin, ev).evaluate();
  }

  public boolean parseCondition(PlayerHurtsEntityEvent ev, String condition0, UnderscoreEnchants plugin) {
    return new PlayerHurtsEntityConditionParser(ev.getDamager(), condition0, plugin, ev).evaluate();
  }

  public boolean parseCondition(PlayerShootBowEvent ev, String condition0, UnderscoreEnchants plugin) {
    return new ShootBowConditionParser(ev.getShooter(), condition0, plugin, ev).evaluate();
  }

  public boolean parseCondition(PlayerToggleSneakEvent ev, String condition0, UnderscoreEnchants plugin) {
    return new SneakConditionParser(ev.getPlayer(), condition0, plugin, ev).evaluate();
  }
}
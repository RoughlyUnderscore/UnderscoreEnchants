package com.roughlyunderscore.enchs.parsers.action.generic;

import com.codingforcookies.armorequip.ArmorEquipEvent;
import com.roughlyunderscore.enchs.UnderscoreEnchants;
import com.roughlyunderscore.enchs.events.*;
import com.roughlyunderscore.enchs.parsers.action.events.armor.ArmorEquipActionParser;
import com.roughlyunderscore.enchs.parsers.action.events.block.BlockBreakActionParser;
import com.roughlyunderscore.enchs.parsers.action.events.bow.BowHitActionParser;
import com.roughlyunderscore.enchs.parsers.action.events.consume.ItemConsumeActionParser;
import com.roughlyunderscore.enchs.parsers.action.events.damage.entity.PlayerHurtsEntityActionParser;
import com.roughlyunderscore.enchs.parsers.action.events.damage.player.PlayerGotHurtActionParser;
import com.roughlyunderscore.enchs.parsers.action.events.interact.entity.InteractEntityActionParser;
import com.roughlyunderscore.enchs.parsers.action.events.interact.player.InteractActionParser;
import com.roughlyunderscore.enchs.parsers.action.events.item.ItemBreakActionParser;
import com.roughlyunderscore.enchs.parsers.action.events.move.MoveActionParser;
import com.roughlyunderscore.enchs.parsers.action.events.projectile.BowShootActionParser;
import com.roughlyunderscore.enchs.parsers.action.events.pvp.PVPActionParser;
import com.roughlyunderscore.enchs.parsers.action.events.sneak.SneakActionParser;
import lombok.experimental.UtilityClass;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.*;

@UtilityClass
public class GeneralActionParser {

  // This is one of the core parts of parsing an enchantment!
  // Maybe too repetitive? (see GeneralConditionParser too)
  // PRs are welcome!

  public void parseAction(Event ev, String action, UnderscoreEnchants plugin) {
    if (action == null || action.isEmpty() || action.isBlank()) return;

    if (ev instanceof PlayerPVPEvent event) parseAction(event, action, plugin);
    else if (ev instanceof ArmorEquipEvent event) parseAction(event, action, plugin);
    else if (ev instanceof BlockBreakEvent event) parseAction(event, action, plugin);
    else if (ev instanceof PlayerBowHitEvent event) parseAction(event, action, plugin);
    else if (ev instanceof PlayerItemBreakEvent event) parseAction(event, action, plugin);
    else if (ev instanceof PlayerItemConsumeEvent event) parseAction(event, action, plugin);
    else if (ev instanceof PlayerInteractEvent event) parseAction(event, action, plugin);
    else if (ev instanceof PlayerInteractAtEntityEvent event) parseAction(event, action, plugin);
    else if (ev instanceof PlayerMoveEvent event) parseAction(event, action, plugin);
    else if (ev instanceof PlayerGotHurtEvent event) parseAction(event, action, plugin);
    else if (ev instanceof PlayerHurtsEntityEvent event) parseAction(event, action, plugin);
    else if (ev instanceof PlayerShootBowEvent event) parseAction(event, action, plugin);
    else if (ev instanceof PlayerToggleSneakEvent event) parseAction(event, action, plugin);
  }


  public void parseAction(PlayerPVPEvent ev, String action, UnderscoreEnchants plugin) {
    new PVPActionParser(ev, ev.getDamager(), ev.getVictim(), action, plugin).execute();
  }

  public void parseAction(ArmorEquipEvent ev, String action, UnderscoreEnchants plugin) {
    new ArmorEquipActionParser(ev, ev.getPlayer(), action, plugin).execute();
  }

  public void parseAction(BlockBreakEvent ev, String action, UnderscoreEnchants plugin) {
    new BlockBreakActionParser(ev, ev.getPlayer(), action, plugin).execute();
  }

  public void parseAction(PlayerBowHitEvent ev, String action, UnderscoreEnchants plugin) {
    new BowHitActionParser(ev, ev.getDamager(), ev.getVictim(), action, plugin).execute();
  }

  public void parseAction(PlayerItemBreakEvent ev, String action, UnderscoreEnchants plugin) {
    new ItemBreakActionParser(ev, ev.getPlayer(), action, plugin).execute();
  }

  public void parseAction(PlayerItemConsumeEvent ev, String action, UnderscoreEnchants plugin) {
    new ItemConsumeActionParser(ev, ev.getPlayer(), action, plugin).execute();
  }

  public void parseAction(PlayerInteractEvent ev, String action, UnderscoreEnchants plugin) {
    new InteractActionParser(ev, ev.getPlayer(), action, plugin).execute();
  }

  public void parseAction(PlayerInteractAtEntityEvent ev, String action, UnderscoreEnchants plugin) {
    new InteractEntityActionParser(ev, ev.getPlayer(), ev.getRightClicked(), action, plugin).execute();
  }

  public void parseAction(PlayerMoveEvent ev, String action, UnderscoreEnchants plugin) {
    new MoveActionParser(ev, ev.getPlayer(), action, plugin).execute();
  }

  public void parseAction(PlayerGotHurtEvent ev, String action, UnderscoreEnchants plugin) {
    new PlayerGotHurtActionParser(ev, ev.getVictim(), action, plugin).execute();
  }

  public void parseAction(PlayerHurtsEntityEvent ev, String action, UnderscoreEnchants plugin) {
    new PlayerHurtsEntityActionParser(ev, ev.getDamager(), ev.getEntity(), action, plugin).execute();
  }

  public void parseAction(PlayerShootBowEvent ev, String action, UnderscoreEnchants plugin) {
    new BowShootActionParser(ev, ev.getShooter(), action, plugin).execute();
  }

  public void parseAction(PlayerToggleSneakEvent ev, String action, UnderscoreEnchants plugin) {
    new SneakActionParser(ev, ev.getPlayer(), action, plugin).execute();
  }
}

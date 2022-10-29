package com.roughlyunderscore.enchs.parsers;

import com.codingforcookies.armorequip.ArmorEquipEvent;
import com.roughlyunderscore.enchs.UnderscoreEnchants;
import com.roughlyunderscore.enchs.events.*;
import com.roughlyunderscore.enchs.util.Pair;
import com.roughlyunderscore.enchs.util.general.Utils;
import com.roughlyunderscore.enchs.util.general.PlayerUtils;
import lombok.experimental.UtilityClass;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static com.roughlyunderscore.enchs.util.general.PlayerUtils.*;
import static com.roughlyunderscore.enchs.util.general.EntityUtils.*;
import static com.roughlyunderscore.enchs.util.general.Utils.*;
import static org.bukkit.Bukkit.getServer;

@UtilityClass
public class ActionParsers {

	// This is one of the core parts of parsing an enchantment!

	public void parseAction(Event ev, String action, UnderscoreEnchants plugin) {
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
		Player victim = ev.getVictim(), damager = ev.getDamager();
		Location victimLocation = getLocation(victim, plugin), damagerLocation = getLocation(damager, plugin);

		String[] split = completeParse(new PDCPlaceholder(victim, damager), action, victimLocation.getWorld(), plugin,
			Pair.of("%damage%",                     String.valueOf(ev.getDamage())),
			Pair.of("%victim_name%",                victim.getName()),
			Pair.of("%damager_name%",               damager.getName()),
			Pair.of("%victim_x%",                   getXString(victim, plugin)),
			Pair.of("%damager_x%",                  getXString(damager, plugin)),
			Pair.of("%victim_y%",                   getYString(victim, plugin)),
			Pair.of("%damager_y%",                  getYString(damager, plugin)),
			Pair.of("%victim_z%",                   getZString(victim, plugin)),
			Pair.of("%damager_z%",                  getZString(damager, plugin)),
			Pair.of("%victim_yaw%",                 getYawString(victim, plugin)),
			Pair.of("%victim_pitch%",               getPitchString(victim, plugin)),
			Pair.of("%damager_yaw%",                getYawString(damager, plugin)),
			Pair.of("%damager_pitch%",              getPitchString(damager, plugin)),
			Pair.of("%victim_money%",               getMoneyString(victim, plugin)),
			Pair.of("%damager_money%",              getMoneyString(damager, plugin)),
			Pair.of("%victim_xp%",                  getXpString(victim, plugin)),
			Pair.of("%damager_xp%",                 getXpString(damager, plugin)),
			Pair.of("%victim_xp_levels%",           getLevelString(victim, plugin)),
			Pair.of("%damager_xp_levels%",          getLevelString(damager, plugin)),
			Pair.of("%victim_health%",              getHealthString(victim, plugin)),
			Pair.of("%damager_health%",             getHealthString(damager, plugin)),
			Pair.of("%victim_max_health%",          getMaximumHealthString(victim, plugin)),
			Pair.of("%damager_max_health%",         getMaximumHealthString(damager, plugin)),
			Pair.of("%victim_food%",                getFoodString(victim, plugin)),
			Pair.of("%damager_food%",               getFoodString(damager, plugin)),
			Pair.of("%victim_air%",                 getAirString(victim, plugin)),
			Pair.of("%damager_air%",                getAirString(damager, plugin)),
			Pair.of("%victim_max_air%",             getMaximumAirString(victim, plugin)),
			Pair.of("%damager_max_air%",            getMaximumAirString(damager, plugin)),
			Pair.of("%victim_godmode%",             invisibleForString(victim, plugin)),
			Pair.of("%damager_godmode%",            invisibleForString(damager, plugin)),
			Pair.of("%random_positive_effect%",     plugin.getPositiveEffects().get(new Random().nextInt(plugin.getPositiveEffects().size())).getName()),
			Pair.of("%random_negative_effect%",     plugin.getNegativeEffects().get(new Random().nextInt(plugin.getNegativeEffects().size())).getName())
		);

		standardDoubleParse(plugin, victim, damager, victimLocation, damagerLocation, split);
	}

	public void parseAction(ArmorEquipEvent ev, String action, UnderscoreEnchants plugin) {
		Player player = ev.getPlayer();
		Location location = player.getLocation();
		String[] split = completeParse(new PDCPlaceholder(player), action, location.getWorld(), plugin,
			Pair.of("%player_name%",                player.getName()),
			Pair.of("%player_x%",                   getXString(player, plugin)),
			Pair.of("%player_y%",                   getYString(player, plugin)),
			Pair.of("%player_z%",                   getZString(player, plugin)),
			Pair.of("%player_yaw%",                 getYawString(player, plugin)),
			Pair.of("%player_pitch%",               getPitchString(player, plugin)),
			Pair.of("%player_money%",               getMoneyString(player, plugin)),
			Pair.of("%player_xp%",                  getXpString(player, plugin)),
			Pair.of("%player_xp_levels%",           getLevelString(player, plugin)),
			Pair.of("%player_health%",              getHealthString(player, plugin)),
			Pair.of("%player_max_health%",          getMaximumHealthString(player, plugin)),
			Pair.of("%player_food%",                getFoodString(player, plugin)),
			Pair.of("%player_air%",                 getAirString(player, plugin)),
			Pair.of("%player_max_air%",             getMaximumAirString(player, plugin)),
			Pair.of("%player_godmode%",             invisibleForString(player, plugin)),
			Pair.of("%random_positive_effect%",     plugin.getPositiveEffects().get(new Random().nextInt(plugin.getPositiveEffects().size())).getName()),
			Pair.of("%random_negative_effect%",     plugin.getNegativeEffects().get(new Random().nextInt(plugin.getNegativeEffects().size())).getName())
		);

		standardParse(plugin, player, location, split);
	}

	public void parseAction(BlockBreakEvent ev, String action, UnderscoreEnchants plugin) {
		Player player = ev.getPlayer();
		Location location = player.getLocation();
		Block block = ev.getBlock();

		String[] split = completeParse(new PDCPlaceholder(player), action, location.getWorld(), plugin,
			Pair.of("%player_name%",                player.getName()),
			Pair.of("%player_x%",                   getXString(player, plugin)),
			Pair.of("%player_y%",                   getYString(player, plugin)),
			Pair.of("%player_z%",                   getZString(player, plugin)),
			Pair.of("%player_yaw%",                 getYawString(player, plugin)),
			Pair.of("%player_pitch%",               getPitchString(player, plugin)),
			Pair.of("%player_money%",               getMoneyString(player, plugin)),
			Pair.of("%player_xp%",                  getXpString(player, plugin)),
			Pair.of("%player_xp_levels%",           getLevelString(player, plugin)),
			Pair.of("%player_health%",              getHealthString(player, plugin)),
			Pair.of("%player_max_health%",          getMaximumHealthString(player, plugin)),
			Pair.of("%player_food%",                getFoodString(player, plugin)),
			Pair.of("%player_air%",                 getAirString(player, plugin)),
			Pair.of("%player_max_air%",             getMaximumAirString(player, plugin)),
			Pair.of("%player_godmode%",             invisibleForString(player, plugin)),
			Pair.of("%random_positive_effect%",     plugin.getPositiveEffects().get(new Random().nextInt(plugin.getPositiveEffects().size())).getName()),
			Pair.of("%random_negative_effect%",     plugin.getNegativeEffects().get(new Random().nextInt(plugin.getNegativeEffects().size())).getName()),
			Pair.of("%block_name%",                 block.getType().name()),
			Pair.of("%block_x%",                    String.valueOf(block.getX())),
			Pair.of("%block_y%",                    String.valueOf(block.getY())),
			Pair.of("%block_z%",                    String.valueOf(block.getZ()))
		);

		standardParse(plugin, player, location, split);
	}

	public void parseAction(PlayerBowHitEvent ev, String action, UnderscoreEnchants plugin) {
		Player victim = ev.getVictim(), damager = ev.getDamager();
		Location victimLocation = getLocation(victim, plugin), damagerLocation = getLocation(damager, plugin);

		String[] split = completeParse(new PDCPlaceholder(victim, damager), action, victimLocation.getWorld(), plugin,
			Pair.of("%damage%",                     String.valueOf(ev.getDamage())),
			Pair.of("%victim_name%",                victim.getName()),
			Pair.of("%damager_name%",               damager.getName()),
			Pair.of("%victim_x%",                   getXString(victim, plugin)),
			Pair.of("%damager_x%",                  getXString(damager, plugin)),
			Pair.of("%victim_y%",                   getYString(victim, plugin)),
			Pair.of("%damager_y%",                  getYString(damager, plugin)),
			Pair.of("%victim_z%",                   getZString(victim, plugin)),
			Pair.of("%damager_z%",                  getZString(damager, plugin)),
			Pair.of("%victim_yaw%",                 getYawString(victim, plugin)),
			Pair.of("%victim_pitch%",               getPitchString(victim, plugin)),
			Pair.of("%damager_yaw%",                getYawString(damager, plugin)),
			Pair.of("%damager_pitch%",              getPitchString(damager, plugin)),
			Pair.of("%victim_money%",               getMoneyString(victim, plugin)),
			Pair.of("%damager_money%",              getMoneyString(damager, plugin)),
			Pair.of("%victim_xp%",                  getXpString(victim, plugin)),
			Pair.of("%damager_xp%",                 getXpString(damager, plugin)),
			Pair.of("%victim_xp_levels%",           getLevelString(victim, plugin)),
			Pair.of("%damager_xp_levels%",          getLevelString(damager, plugin)),
			Pair.of("%victim_health%",              getHealthString(victim, plugin)),
			Pair.of("%damager_health%",             getHealthString(damager, plugin)),
			Pair.of("%victim_max_health%",          getMaximumHealthString(victim, plugin)),
			Pair.of("%damager_max_health%",         getMaximumHealthString(damager, plugin)),
			Pair.of("%victim_food%",                getFoodString(victim, plugin)),
			Pair.of("%damager_food%",               getFoodString(damager, plugin)),
			Pair.of("%victim_air%",                 getAirString(victim, plugin)),
			Pair.of("%damager_air%",                getAirString(damager, plugin)),
			Pair.of("%victim_max_air%",             getMaximumAirString(victim, plugin)),
			Pair.of("%damager_max_air%",            getMaximumAirString(damager, plugin)),
			Pair.of("%victim_godmode%",             invisibleForString(victim, plugin)),
			Pair.of("%damager_godmode%",            invisibleForString(damager, plugin)),
			Pair.of("%random_positive_effect%",     plugin.getPositiveEffects().get(new Random().nextInt(plugin.getPositiveEffects().size())).getName()),
			Pair.of("%random_negative_effect%",     plugin.getNegativeEffects().get(new Random().nextInt(plugin.getNegativeEffects().size())).getName())
		);

		standardDoubleParse(plugin, victim, damager, victimLocation, damagerLocation, split);
	}

	public void parseAction(PlayerItemBreakEvent ev, String action, UnderscoreEnchants plugin) {
		Player player = ev.getPlayer();
		Location location = player.getLocation();
		ItemStack item = ev.getBrokenItem();

		String[] split = completeParse(new PDCPlaceholder(player), action, location.getWorld(), plugin,
			Pair.of("%player_name%",                player.getName()),
			Pair.of("%player_x%",                   getXString(player, plugin)),
			Pair.of("%player_y%",                   getYString(player, plugin)),
			Pair.of("%player_z%",                   getZString(player, plugin)),
			Pair.of("%player_yaw%",                 getYawString(player, plugin)),
			Pair.of("%player_pitch%",               getPitchString(player, plugin)),
			Pair.of("%player_money%",               getMoneyString(player, plugin)),
			Pair.of("%player_xp%",                  getXpString(player, plugin)),
			Pair.of("%player_xp_levels%",           getLevelString(player, plugin)),
			Pair.of("%player_health%",              getHealthString(player, plugin)),
			Pair.of("%player_max_health%",          getMaximumHealthString(player, plugin)),
			Pair.of("%player_food%",                getFoodString(player, plugin)),
			Pair.of("%player_air%",                 getAirString(player, plugin)),
			Pair.of("%player_max_air%",             getMaximumAirString(player, plugin)),
			Pair.of("%player_godmode%",             invisibleForString(player, plugin)),
			Pair.of("%random_positive_effect%",     plugin.getPositiveEffects().get(new Random().nextInt(plugin.getPositiveEffects().size())).getName()),
			Pair.of("%random_negative_effect%",     plugin.getNegativeEffects().get(new Random().nextInt(plugin.getNegativeEffects().size())).getName()),
			Pair.of("%item_name%",                  item.getType().name())
		);

		standardParse(plugin, player, location, split);
	}

	public void parseAction(PlayerItemConsumeEvent ev, String action, UnderscoreEnchants plugin) {
		Player player = ev.getPlayer();
		Location location = player.getLocation();
		ItemStack item = ev.getItem();

		String[] split = completeParse(new PDCPlaceholder(player), action, location.getWorld(), plugin,
			Pair.of("%player_name%",                player.getName()),
			Pair.of("%player_x%",                   getXString(player, plugin)),
			Pair.of("%player_y%",                   getYString(player, plugin)),
			Pair.of("%player_z%",                   getZString(player, plugin)),
			Pair.of("%player_yaw%",                 getYawString(player, plugin)),
			Pair.of("%player_pitch%",               getPitchString(player, plugin)),
			Pair.of("%player_money%",               getMoneyString(player, plugin)),
			Pair.of("%player_xp%",                  getXpString(player, plugin)),
			Pair.of("%player_xp_levels%",           getLevelString(player, plugin)),
			Pair.of("%player_health%",              getHealthString(player, plugin)),
			Pair.of("%player_max_health%",          getMaximumHealthString(player, plugin)),
			Pair.of("%player_food%",                getFoodString(player, plugin)),
			Pair.of("%player_air%",                 getAirString(player, plugin)),
			Pair.of("%player_max_air%",             getMaximumAirString(player, plugin)),
			Pair.of("%player_godmode%",             invisibleForString(player, plugin)),
			Pair.of("%random_positive_effect%",     plugin.getPositiveEffects().get(new Random().nextInt(plugin.getPositiveEffects().size())).getName()),
			Pair.of("%random_negative_effect%",     plugin.getNegativeEffects().get(new Random().nextInt(plugin.getNegativeEffects().size())).getName()),
			Pair.of("%food_name%",                  item.getType().name())
		);

		standardParse(plugin, player, location, split);
	}

	public void parseAction(PlayerInteractEvent ev, String action, UnderscoreEnchants plugin) {
		Player player = ev.getPlayer();
		Location location = player.getLocation();
		Block block = ev.getClickedBlock() == null ? location.getWorld().getBlockAt(0, 0, 0) : ev.getClickedBlock(); // null check

		String[] split = completeParse(new PDCPlaceholder(player), action, location.getWorld(), plugin,
			Pair.of("%player_name%",                player.getName()),
			Pair.of("%player_x%",                   getXString(player, plugin)),
			Pair.of("%player_y%",                   getYString(player, plugin)),
			Pair.of("%player_z%",                   getZString(player, plugin)),
			Pair.of("%player_yaw%",                 getYawString(player, plugin)),
			Pair.of("%player_pitch%",               getPitchString(player, plugin)),
			Pair.of("%player_money%",               getMoneyString(player, plugin)),
			Pair.of("%player_xp%",                  getXpString(player, plugin)),
			Pair.of("%player_xp_levels%",           getLevelString(player, plugin)),
			Pair.of("%player_health%",              getHealthString(player, plugin)),
			Pair.of("%player_max_health%",          getMaximumHealthString(player, plugin)),
			Pair.of("%player_food%",                getFoodString(player, plugin)),
			Pair.of("%player_air%",                 getAirString(player, plugin)),
			Pair.of("%player_max_air%",             getMaximumAirString(player, plugin)),
			Pair.of("%player_godmode%",             invisibleForString(player, plugin)),
			Pair.of("%random_positive_effect%",     plugin.getPositiveEffects().get(new Random().nextInt(plugin.getPositiveEffects().size())).getName()),
			Pair.of("%random_negative_effect%",     plugin.getNegativeEffects().get(new Random().nextInt(plugin.getNegativeEffects().size())).getName()),
			Pair.of("%block_x%",                    String.valueOf(block.getX())),
			Pair.of("%block_y%",                    String.valueOf(block.getY())),
			Pair.of("%block_z%",                    String.valueOf(block.getZ())),
			Pair.of("%block_material%",             block.getType().name())
		);

		standardParse(plugin, player, location, split);
	}

	public void parseAction(PlayerInteractAtEntityEvent ev, String action, UnderscoreEnchants plugin) {
		Player player = ev.getPlayer();
		Location location = player.getLocation();
		Entity entity = ev.getRightClicked();

		String[] split = completeParse(new PDCPlaceholder(player), action, location.getWorld(), plugin,
			Pair.of("%player_name%",                player.getName()),
			Pair.of("%player_x%",                   getXString(player, plugin)),
			Pair.of("%player_y%",                   getYString(player, plugin)),
			Pair.of("%player_z%",                   getZString(player, plugin)),
			Pair.of("%player_yaw%",                 getYawString(player, plugin)),
			Pair.of("%player_pitch%",               getPitchString(player, plugin)),
			Pair.of("%player_money%",               getMoneyString(player, plugin)),
			Pair.of("%player_xp%",                  getXpString(player, plugin)),
			Pair.of("%player_xp_levels%",           getLevelString(player, plugin)),
			Pair.of("%player_health%",              getHealthString(player, plugin)),
			Pair.of("%player_max_health%",          getMaximumHealthString(player, plugin)),
			Pair.of("%player_food%",                getFoodString(player, plugin)),
			Pair.of("%player_air%",                 getAirString(player, plugin)),
			Pair.of("%player_max_air%",             getMaximumAirString(player, plugin)),
			Pair.of("%player_godmode%",             invisibleForString(player, plugin)),
			Pair.of("%random_positive_effect%",     plugin.getPositiveEffects().get(new Random().nextInt(plugin.getPositiveEffects().size())).getName()),
			Pair.of("%random_negative_effect%",     plugin.getNegativeEffects().get(new Random().nextInt(plugin.getNegativeEffects().size())).getName()),
			Pair.of("%entity_type",                 entity.getType().name()),
			Pair.of("%entity_x%",                   getXString(entity, plugin)),
			Pair.of("%entity_y%",                   getYString(entity, plugin)),
			Pair.of("%entity_z%",                   getZString(entity, plugin)),
			Pair.of("%entity_yaw%",                 getYawString(entity, plugin)),
			Pair.of("%entity_pitch%",               getPitchString(entity, plugin)),
			Pair.of("%entity_health%",              getHealthString(entity, plugin)),
			Pair.of("%entity_max_health%",          getMaximumHealthString(entity, plugin))
		);

		standardEntityParse(plugin, player, location, entity, split);
	}

	public void parseAction(PlayerMoveEvent ev, String action, UnderscoreEnchants plugin) {
		Player player = ev.getPlayer();
		Location location = player.getLocation();
		Location from = ev.getFrom().clone().subtract(0, 1, 0);
		Location to = (ev.getTo() == null ? ev.getFrom() : ev.getTo()).clone().subtract(0, 1, 0); // null check

		String[] split = completeParse(new PDCPlaceholder(player), action, location.getWorld(), plugin,
			Pair.of("%player_name%",                player.getName()),
			Pair.of("%player_x%",                   getXString(player, plugin)),
			Pair.of("%player_y%",                   getYString(player, plugin)),
			Pair.of("%player_z%",                   getZString(player, plugin)),
			Pair.of("%player_yaw%",                 getYawString(player, plugin)),
			Pair.of("%player_pitch%",               getPitchString(player, plugin)),
			Pair.of("%player_money%",               getMoneyString(player, plugin)),
			Pair.of("%player_xp%",                  getXpString(player, plugin)),
			Pair.of("%player_xp_levels%",           getLevelString(player, plugin)),
			Pair.of("%player_health%",              getHealthString(player, plugin)),
			Pair.of("%player_max_health%",          getMaximumHealthString(player, plugin)),
			Pair.of("%player_food%",                getFoodString(player, plugin)),
			Pair.of("%player_air%",                 getAirString(player, plugin)),
			Pair.of("%player_max_air%",             getMaximumAirString(player, plugin)),
			Pair.of("%player_godmode%",             invisibleForString(player, plugin)),
			Pair.of("%random_positive_effect%",     plugin.getPositiveEffects().get(new Random().nextInt(plugin.getPositiveEffects().size())).getName()),
			Pair.of("%random_negative_effect%",     plugin.getNegativeEffects().get(new Random().nextInt(plugin.getNegativeEffects().size())).getName()),
			Pair.of("%block_from_x%",               String.valueOf(from.getBlockX())),
			Pair.of("%block_from_y%",               String.valueOf(from.getBlockY())),
			Pair.of("%block_from_z%",               String.valueOf(from.getBlockZ())),
			Pair.of("%block_from_material%",        from.getBlock().getType().name()),
			Pair.of("%block_to_x%",                 String.valueOf(to.getBlockX())),
			Pair.of("%block_to_y%",                 String.valueOf(to.getBlockY())),
			Pair.of("%block_to_z%",                 String.valueOf(to.getBlockZ())),
			Pair.of("%block_to_material%",          to.getBlock().getType().name())
		);

		standardParse(plugin, player, location, split);
	}

	public void parseAction(PlayerGotHurtEvent ev, String action, UnderscoreEnchants plugin) {
		Player player = ev.getVictim();
		Location location = player.getLocation();
		double damage = ev.getDamage();

		String[] split = completeParse(new PDCPlaceholder(player), action, location.getWorld(), plugin,
			Pair.of("%player_name%",                player.getName()),
			Pair.of("%player_x%",                   getXString(player, plugin)),
			Pair.of("%player_y%",                   getYString(player, plugin)),
			Pair.of("%player_z%",                   getZString(player, plugin)),
			Pair.of("%player_yaw%",                 getYawString(player, plugin)),
			Pair.of("%player_pitch%",               getPitchString(player, plugin)),
			Pair.of("%player_money%",               getMoneyString(player, plugin)),
			Pair.of("%player_xp%",                  getXpString(player, plugin)),
			Pair.of("%player_xp_levels%",           getLevelString(player, plugin)),
			Pair.of("%player_health%",              getHealthString(player, plugin)),
			Pair.of("%player_max_health%",          getMaximumHealthString(player, plugin)),
			Pair.of("%player_food%",                getFoodString(player, plugin)),
			Pair.of("%player_air%",                 getAirString(player, plugin)),
			Pair.of("%player_max_air%",             getMaximumAirString(player, plugin)),
			Pair.of("%player_godmode%",             invisibleForString(player, plugin)),
			Pair.of("%random_positive_effect%",     plugin.getPositiveEffects().get(new Random().nextInt(plugin.getPositiveEffects().size())).getName()),
			Pair.of("%random_negative_effect%",     plugin.getNegativeEffects().get(new Random().nextInt(plugin.getNegativeEffects().size())).getName()),
			Pair.of("%damage%",                     String.valueOf(damage))
		);

		standardParse(plugin, player, location, split);
	}

	public void parseAction(PlayerHurtsEntityEvent ev, String action, UnderscoreEnchants plugin) {
		Player player = ev.getDamager();
		Entity entity = ev.getEntity();
		Location location = player.getLocation();
		double damage = ev.getDamage();

		String[] split = completeParse(new PDCPlaceholder(player), action, location.getWorld(), plugin,
			Pair.of("%player_name%",                player.getName()),
			Pair.of("%player_x%",                   getXString(player, plugin)),
			Pair.of("%player_y%",                   getYString(player, plugin)),
			Pair.of("%player_z%",                   getZString(player, plugin)),
			Pair.of("%player_yaw%",                 getYawString(player, plugin)),
			Pair.of("%player_pitch%",               getPitchString(player, plugin)),
			Pair.of("%player_money%",               getMoneyString(player, plugin)),
			Pair.of("%player_xp%",                  getXpString(player, plugin)),
			Pair.of("%player_xp_levels%",           getLevelString(player, plugin)),
			Pair.of("%player_health%",              getHealthString(player, plugin)),
			Pair.of("%player_max_health%",          getMaximumHealthString(player, plugin)),
			Pair.of("%player_food%",                getFoodString(player, plugin)),
			Pair.of("%player_air%",                 getAirString(player, plugin)),
			Pair.of("%player_max_air%",             getMaximumAirString(player, plugin)),
			Pair.of("%player_godmode%",             invisibleForString(player, plugin)),
			Pair.of("%random_positive_effect%",     plugin.getPositiveEffects().get(new Random().nextInt(plugin.getPositiveEffects().size())).getName()),
			Pair.of("%random_negative_effect%",     plugin.getNegativeEffects().get(new Random().nextInt(plugin.getNegativeEffects().size())).getName()),
			Pair.of("%damage%",                     String.valueOf(damage)),
			Pair.of("%entity_type",                 entity.getType().name()),
			Pair.of("%entity_x%",                   getXString(entity, plugin)),
			Pair.of("%entity_y%",                   getYString(entity, plugin)),
			Pair.of("%entity_z%",                   getZString(entity, plugin)),
			Pair.of("%entity_yaw%",                 getYawString(entity, plugin)),
			Pair.of("%entity_pitch%",               getPitchString(entity, plugin)),
			Pair.of("%entity_health%",              getHealthString(entity, plugin)),
			Pair.of("%entity_max_health%",          getMaximumHealthString(entity, plugin))
		);

		standardEntityParse(plugin, player, location, entity, split);
	}

	public void parseAction(PlayerShootBowEvent ev, String action, UnderscoreEnchants plugin) {
		Player player = ev.getShooter();
		Location location = player.getLocation();
		float force = ev.getForce();

		String[] split = completeParse(new PDCPlaceholder(player), action, location.getWorld(), plugin,
			Pair.of("%player_name%",                player.getName()),
			Pair.of("%player_x%",                   getXString(player, plugin)),
			Pair.of("%player_y%",                   getYString(player, plugin)),
			Pair.of("%player_z%",                   getZString(player, plugin)),
			Pair.of("%player_yaw%",                 getYawString(player, plugin)),
			Pair.of("%player_pitch%",               getPitchString(player, plugin)),
			Pair.of("%player_money%",               getMoneyString(player, plugin)),
			Pair.of("%player_xp%",                  getXpString(player, plugin)),
			Pair.of("%player_xp_levels%",           getLevelString(player, plugin)),
			Pair.of("%player_health%",              getHealthString(player, plugin)),
			Pair.of("%player_max_health%",          getMaximumHealthString(player, plugin)),
			Pair.of("%player_food%",                getFoodString(player, plugin)),
			Pair.of("%player_air%",                 getAirString(player, plugin)),
			Pair.of("%player_max_air%",             getMaximumAirString(player, plugin)),
			Pair.of("%player_godmode%",             invisibleForString(player, plugin)),
			Pair.of("%random_positive_effect%",     plugin.getPositiveEffects().get(new Random().nextInt(plugin.getPositiveEffects().size())).getName()),
			Pair.of("%random_negative_effect%",     plugin.getNegativeEffects().get(new Random().nextInt(plugin.getNegativeEffects().size())).getName()),
			Pair.of("%force%",                      String.valueOf(force))
		);

		standardParse(plugin, player, location, split);
	}

	public void parseAction(PlayerToggleSneakEvent ev, String action, UnderscoreEnchants plugin) {
		Player player = ev.getPlayer();
		Location location = player.getLocation();

		String[] split = completeParse(new PDCPlaceholder(player), action, location.getWorld(), plugin,
			Pair.of("%player_name%",                player.getName()),
			Pair.of("%player_x%",                   getXString(player, plugin)),
			Pair.of("%player_y%",                   getYString(player, plugin)),
			Pair.of("%player_z%",                   getZString(player, plugin)),
			Pair.of("%player_yaw%",                 getYawString(player, plugin)),
			Pair.of("%player_pitch%",               getPitchString(player, plugin)),
			Pair.of("%player_money%",               getMoneyString(player, plugin)),
			Pair.of("%player_xp%",                  getXpString(player, plugin)),
			Pair.of("%player_xp_levels%",           getLevelString(player, plugin)),
			Pair.of("%player_health%",              getHealthString(player, plugin)),
			Pair.of("%player_max_health%",          getMaximumHealthString(player, plugin)),
			Pair.of("%player_food%",                getFoodString(player, plugin)),
			Pair.of("%player_air%",                 getAirString(player, plugin)),
			Pair.of("%player_max_air%",             getMaximumAirString(player, plugin)),
			Pair.of("%player_godmode%",             invisibleForString(player, plugin)),
			Pair.of("%random_positive_effect%",     plugin.getPositiveEffects().get(new Random().nextInt(plugin.getPositiveEffects().size())).getName()),
			Pair.of("%random_negative_effect%",     plugin.getNegativeEffects().get(new Random().nextInt(plugin.getNegativeEffects().size())).getName())
		);

		standardParse(plugin, player, location, split);
	}




	// Don't Repeat Yourself
	private void standardParse(UnderscoreEnchants plugin, Player player, Location location, String[] split) {
		long delay = 0;

		if (split[split.length - 1].toLowerCase().startsWith("delay:")) {
			String parsableDelay = split[split.length - 1].replace("delay:", "");
			delay = parseL(parsableDelay);
		}

		Bukkit.getScheduler().runTaskLater(plugin, () -> {

			switch (split[0].toLowerCase(Locale.ROOT)) {
				case "player-pdc-set" -> setPDC(player, NamespacedKey.fromString(split[1], plugin), split[2], plugin);

				case "player-velocity" -> produceVelocity(player, split);

				case "player-sound" -> playSound(player, split[1], plugin);
				case "location-sound" -> PlayerUtils.playWorldSound(player, split[2], split[3], split[4], split[1], plugin);

				case "player-effect" -> addPotion(player, split[1], Utils.parseI(split[2]), Utils.parseI(split[3]), plugin);

				case "player-remove-effect" -> removePotion(player, split[1], plugin);
				case "player-remove-buffs" -> removeBuffs(player, plugin);
				case "player-remove-debuffs" -> removeDebuffs(player, plugin);

				case "player-bossbar" -> {
					StringBuilder builder = new StringBuilder();
					boolean debug = split[split.length - 1].toLowerCase().startsWith("delay:");
					int endingIndex = debug ? split.length - 1 : split.length; // if ends with the delay argument, ignore that last bit
					for (int i = 3; i < endingIndex; i++) {
						builder.append(split[i]).append(" ");
					}
					sendBar(player, split[3], split[2], builder.toString(), plugin);
				}

				case "player-set-hand" -> setMainHand(player, split[1], plugin);

				case "player-shuffle-hotbar" -> shuffleHotbar(player, plugin);
				case "player-shuffle-inventory" -> shuffleInventory(player, plugin);

				case "player-strike-fake-lightning" -> strikeFakeLightning(location, plugin);
				case "player-strike-real-lightning" -> strikeLightning(location, plugin);

				case "player-particle" -> spawnParticle(player, location, split[1], plugin);
				case "player-particle-boots" -> spawnParticleBoots(player, location, split[1], plugin);

				case "spawn-entity" -> spawnEntity(player, split[1], split[2], split[3], split[4], plugin);
				case "spawn-entity-detailed" -> spawnEntity(player, split[1], split[2], split[3], split[4], split[5], split[6], split[7], split[8], split[9], plugin);

				case "player-teleport" -> setLocation(player, Utils.parseI(split[1]), Utils.parseI(split[2]), Utils.parseI(split[3]), plugin);
				case "player-direction" -> setDirection(player, split[1], split[2], plugin);
				case "player-forward" -> sendForward(player, split[1], plugin);

				case "player-world" -> setWorld(player, split[1], plugin);
				case "create-world" -> createWorld(split[1], split[2], split[3], plugin);

				case "player-teleport-bed" -> teleportToBed(player, plugin);

				case "player-set-health" -> setHealth(player, split[1], plugin);
				case "player-set-max-health" -> setMaximumHealth(player, split[1], plugin);

				case "player-set-fire" -> setFire(player, split[1], plugin);
				case "player-set-food" -> setFood(player, split[1], plugin);
				case "player-set-oxygen" -> setAir(player, split[1], plugin);

				case "player-set-exp" -> setXp(player, split[1], plugin);
				case "player-set-level" -> setLevel(player, split[1], plugin);

				case "player-send-arrow" -> sendArrow(player, plugin);
				case "player-send-fireball" -> sendFireball(player, plugin);

				case "player-send-message" -> {
					StringBuilder builder = new StringBuilder();
					boolean debug = split[split.length - 1].toLowerCase().startsWith("delay:");
					int endingIndex = debug ? split.length - 1 : split.length; // if ends with the delay argument, ignore that last bit
					for (int i = 1; i < endingIndex; i++) {
						builder.append(split[i]).append(" ");
					}
					tellTo(player, builder.toString(), plugin);
				}
				case "player-send-title" -> {
					StringBuilder builder = new StringBuilder();
					boolean debug = split[split.length - 1].toLowerCase().startsWith("delay:");
					int endingIndex = debug ? split.length - 1 : split.length; // if ends with the delay argument, ignore that last bit
					for (int i = 1; i < endingIndex; i++) {
						builder.append(split[i]).append(" ");
					}
					sendTitle(player, builder.toString(), plugin);
				}
				case "player-send-subtitle" -> {
					StringBuilder builder = new StringBuilder();
					boolean debug = split[split.length - 1].toLowerCase().startsWith("delay:");
					int endingIndex = debug ? split.length - 1 : split.length; // if ends with the delay argument, ignore that last bit
					for (int i = 1; i < endingIndex; i++) {
						builder.append(split[i]).append(" ");
					}
					sendSubtitle(player, builder.toString(), plugin);
				}
				case "player-actionbar" -> {
					StringBuilder builder = new StringBuilder();
					boolean debug = split[split.length - 1].toLowerCase().startsWith("delay:");
					int endingIndex = debug ? split.length - 1 : split.length; // if ends with the delay argument, ignore that last bit
					for (int i = 1; i < endingIndex; i++) {
						builder.append(split[i]).append(" ");
					}
					sendActionbar(player, builder.toString(), plugin);
				}
				case "player-chat" -> {
					StringBuilder builder = new StringBuilder();
					boolean debug = split[split.length - 1].toLowerCase().startsWith("delay:");
					int endingIndex = debug ? split.length - 1 : split.length; // if ends with the delay argument, ignore that last bit
					for (int i = 1; i < endingIndex; i++) {
						builder.append(split[i]).append(" ");
					}
					say(player, builder.toString(), plugin);
				}

				case "player-set-money" -> setMoney(player, split[1], plugin);

				case "player-give-head" -> giveHeadOf(player, split[1], plugin);
				case "player-give-item" -> give(player, split[1], parseI(split[2]), plugin);

				case "player-drop-hand" -> dropHand(player, plugin);

				case "player-damage-armor" -> damageArmorPiece(player, split[2], split[1], plugin);
				case "player-damage-hand" -> damageHand(player, split[1], plugin);

				case "player-repair-armor" -> fixArmorPiece(player, split[1], plugin);
				case "player-repair-hand" -> fixHand(player, plugin);

				case "set-block" -> setBlock(location.getWorld(), split[1], split[2], split[3], split[4], plugin);

				case "time" -> setTime(location.getWorld(), split[1], plugin);
				case "weather" -> setWeather(Objects.requireNonNull(location.getWorld()), split[1], plugin);

				case "player-time" -> setPlayerTime(player, split[1], plugin);
				case "player-weather" -> setPlayerWeather(player, split[1], plugin);

				case "reset-player-time" -> resetPlayerTime(player, plugin);
				case "reset-player-weather" -> resetPlayerWeather(player, plugin);

				case "player-godmode" -> makeInvisibleFor(player, Utils.parseI(split[1]), plugin);

				case "broadcast" -> {
					StringBuilder builder = new StringBuilder();
					boolean debug = split[split.length - 1].toLowerCase().startsWith("delay:");
					int endingIndex = debug ? split.length - 1 : split.length; // if ends with the delay argument, ignore that last bit
					for (int i = 1; i < endingIndex; i++) {
						builder.append(split[i]).append(" ");
					}
					announce(builder.toString(), plugin);
				}
				case "log" -> {
					StringBuilder builder = new StringBuilder();
					boolean debug = split[split.length - 1].toLowerCase().startsWith("delay:");
					int endingIndex = debug ? split.length - 1 : split.length; // if ends with the delay argument, ignore that last bit
					for (int i = 1; i < endingIndex; i++) {
						builder.append(split[i]).append(" ");
					}
					plugin.debugger.log("[Manual Logging] " + builder);
				}

				case "console-command" ->{
					StringBuilder builder = new StringBuilder();
					boolean debug = split[split.length - 1].toLowerCase().startsWith("delay:");
					int endingIndex = debug ? split.length - 1 : split.length; // if ends with the delay argument, ignore that last bit
					for (int i = 1; i < endingIndex; i++) {
						builder.append(split[i]).append(" ");
					}
					getServer().dispatchCommand(getServer().getConsoleSender(), builder.toString());
				}
			}
		}, delay);
	}

	private void standardDoubleParse(UnderscoreEnchants plugin, Player victim, Player damager, Location victimLocation, Location damagerLocation, String[] split) {
		long delay = 0;

		if (split[split.length - 1].toLowerCase().startsWith("delay:")) {
			String parsableDelay = split[split.length - 1].replace("delay:", "");
			delay = parseL(parsableDelay);
		}

		Bukkit.getScheduler().runTaskLater(plugin, () -> {

			switch (split[0].toLowerCase(Locale.ROOT)) {
				case "victim-velocity" -> produceVelocity(victim, split);
				case "damager-velocity" -> produceVelocity(damager, split);

				case "victim-pdc-set" -> setPDC(victim, NamespacedKey.fromString(split[1], plugin), split[2], plugin);
				case "damager-pdc-set" -> setPDC(damager, NamespacedKey.fromString(split[1], plugin), split[2], plugin);

				case "victim-sound" -> playSound(victim, split[1], plugin);
				case "damager-sound" -> playSound(damager, split[1], plugin);
				case "location-sound" -> playWorldSound(damager, split[2], split[3], split[4], split[1], plugin);

				case "victim-effect" -> addPotion(victim, split[1], Utils.parseI(split[2]), Utils.parseI(split[3]), plugin);
				case "damager-effect" -> addPotion(damager, split[1], Utils.parseI(split[2]), Utils.parseI(split[3]), plugin);

				case "victim-remove-effect" -> removePotion(victim, split[1], plugin);
				case "damager-remove-effect" -> removePotion(damager, split[1], plugin);
				case "victim-remove-buffs" -> removeBuffs(victim, plugin);
				case "damager-remove-buffs" -> removeBuffs(damager, plugin);
				case "victim-remove-debuffs" -> removeDebuffs(victim, plugin);
				case "damager-remove-debuffs" -> removeDebuffs(damager, plugin);

				case "victim-bossbar" -> {
					StringBuilder builder = new StringBuilder();
					boolean debug = split[split.length - 1].toLowerCase().startsWith("delay:");
					int endingIndex = debug ? split.length - 1 : split.length; // if ends with the delay argument, ignore that last bit
					for (int i = 3; i < endingIndex; i++) {
						builder.append(split[i]).append(" ");
					}
					sendBar(victim, split[3], split[2], builder.toString(), plugin);
				}

				case "damager-bossbar" -> {
					StringBuilder builder = new StringBuilder();
					boolean debug = split[split.length - 1].toLowerCase().startsWith("delay:");
					int endingIndex = debug ? split.length - 1 : split.length; // if ends with the delay argument, ignore that last bit
					for (int i = 3; i < endingIndex; i++) {
						builder.append(split[i]).append(" ");
					}
					sendBar(damager, split[3], split[2], builder.toString(), plugin);
				}

				case "victim-set-hand" -> setMainHand(victim, split[1], plugin);
				case "damager-set-hand" -> setMainHand(damager, split[1], plugin);

				case "victim-shuffle-hotbar" -> shuffleHotbar(victim, plugin);
				case "damager-shuffle-hotbar" -> shuffleHotbar(damager, plugin);

				case "victim-shuffle-inventory" -> shuffleInventory(victim, plugin);
				case "damager-shuffle-inventory" -> shuffleInventory(damager, plugin);

				case "victim-strike-fake-lightning" -> strikeFakeLightning(victimLocation, plugin);
				case "damager-strike-fake-lightning" -> strikeFakeLightning(damagerLocation, plugin);
				case "victim-strike-real-lightning" -> strikeLightning(victimLocation, plugin);
				case "damager-strike-real-lightning" -> strikeLightning(damagerLocation, plugin);

				case "victim-particle" -> spawnParticle(victim, victimLocation, split[1], plugin);
				case "damager-particle" -> spawnParticle(damager, damagerLocation, split[1], plugin);
				case "victim-particle-boots" -> spawnParticleBoots(victim, victimLocation, split[1], plugin);
				case "damager-particle-boots" -> spawnParticleBoots(damager, damagerLocation, split[1], plugin);

				case "spawn-entity" -> spawnEntity(victim, split[1], split[2], split[3], split[4], plugin);
				case "spawn-entity-detailed" -> spawnEntity(victim, split[1], split[2], split[3], split[4], split[5], split[6], split[7], split[8], split[9], plugin);

				case "victim-teleport" -> setLocation(victim, Utils.parseD(split[1]), Utils.parseD(split[2]), Utils.parseD(split[3]), plugin);
				case "damager-teleport" -> setLocation(damager, Utils.parseD(split[1]), Utils.parseD(split[2]), Utils.parseD(split[3]), plugin);

				case "victim-direction" -> setDirection(victim, split[1], split[2], plugin);
				case "damager-direction" -> setDirection(damager, split[1], split[2], plugin);

				case "victim-forward" -> sendForward(victim, split[1], plugin);
				case "damager-forward" -> sendForward(damager, split[1], plugin);

				case "victim-world" -> setWorld(victim, split[1], plugin);
				case "damager-world" -> setWorld(victim, split[2], plugin);

				case "create-world" -> createWorld(split[1], split[2], split[3], plugin);

				case "victim-teleport-bed" -> teleportToBed(victim, plugin);
				case "damager-teleport-bed" -> teleportToBed(damager, plugin);

				case "victim-set-health" -> setHealth(victim, Utils.parseD(split[1]), plugin);
				case "damager-set-health" -> setHealth(damager, Utils.parseD(split[1]), plugin);

				case "victim-set-max-health" -> setMaximumHealth(victim, Utils.parseD(split[1]), plugin);
				case "damager-set-max-health" -> setMaximumHealth(damager, Utils.parseD(split[1]), plugin);

				case "victim-set-fire" -> setFire(victim, split[1], plugin);
				case "damager-set-fire" -> setFire(damager, split[1], plugin);

				case "victim-set-food" -> setFood(victim, split[1], plugin);
				case "damager-set-food" -> setFood(damager, split[1], plugin);

				case "victim-set-oxygen" -> setAir(victim, split[1], plugin);
				case "damager-set-oxygen" -> setAir(damager, split[1], plugin);

				case "victim-set-exp" -> setXp(victim, split[1], plugin);
				case "damager-set-exp" -> setXp(damager, split[1], plugin);

				case "victim-set-level" -> setLevel(victim, split[1], plugin);
				case "damager-set-level" -> setLevel(damager, split[1], plugin);

				case "victim-send-arrow" -> sendArrow(victim, plugin);
				case "damager-send-arrow" -> sendArrow(damager, plugin);

				case "victim-send-fireball" -> sendFireball(victim, plugin);
				case "damager-send-fireball" -> sendFireball(damager, plugin);

				case "victim-send-message" -> {
					StringBuilder builder = new StringBuilder();
					boolean debug = split[split.length - 1].toLowerCase().startsWith("delay:");
					int endingIndex = debug ? split.length - 1 : split.length; // if ends with the delay argument, ignore that last bit
					for (int i = 1; i < endingIndex; i++) {
						builder.append(split[i]).append(" ");
					}
					tellTo(victim, builder.toString(), plugin);
				}
				case "victim-send-title" -> {
					StringBuilder builder = new StringBuilder();
					boolean debug = split[split.length - 1].toLowerCase().startsWith("delay:");
					int endingIndex = debug ? split.length - 1 : split.length; // if ends with the delay argument, ignore that last bit
					for (int i = 1; i < endingIndex; i++) {
						builder.append(split[i]).append(" ");
					}
					sendTitle(victim, builder.toString(), plugin);
				}
				case "victim-send-subtitle" -> {
					StringBuilder builder = new StringBuilder();
					boolean debug = split[split.length - 1].toLowerCase().startsWith("delay:");
					int endingIndex = debug ? split.length - 1 : split.length; // if ends with the delay argument, ignore that last bit
					for (int i = 1; i < endingIndex; i++) {
						builder.append(split[i]).append(" ");
					}
					sendSubtitle(victim, builder.toString(), plugin);
				}
				case "victim-actionbar" -> {
					StringBuilder builder = new StringBuilder();
					boolean debug = split[split.length - 1].toLowerCase().startsWith("delay:");
					int endingIndex = debug ? split.length - 1 : split.length; // if ends with the delay argument, ignore that last bit
					for (int i = 1; i < endingIndex; i++) {
						builder.append(split[i]).append(" ");
					}
					sendActionbar(victim, builder.toString(), plugin);
				}
				case "victim-chat" -> {
					StringBuilder builder = new StringBuilder();
					boolean debug = split[split.length - 1].toLowerCase().startsWith("delay:");
					int endingIndex = debug ? split.length - 1 : split.length; // if ends with the delay argument, ignore that last bit
					for (int i = 1; i < endingIndex; i++) {
						builder.append(split[i]).append(" ");
					}
					say(victim, builder.toString(), plugin);
				}

				case "damager-send-message" -> {
					StringBuilder builder = new StringBuilder();
					boolean debug = split[split.length - 1].toLowerCase().startsWith("delay:");
					int endingIndex = debug ? split.length - 1 : split.length; // if ends with the delay argument, ignore that last bit
					for (int i = 1; i < endingIndex; i++) {
						builder.append(split[i]).append(" ");
					}
					tellTo(damager, builder.toString(), plugin);
				}
				case "damager-send-title" -> {
					StringBuilder builder = new StringBuilder();
					boolean debug = split[split.length - 1].toLowerCase().startsWith("delay:");
					int endingIndex = debug ? split.length - 1 : split.length; // if ends with the delay argument, ignore that last bit
					for (int i = 1; i < endingIndex; i++) {
						builder.append(split[i]).append(" ");
					}
					sendTitle(damager, builder.toString(), plugin);
				}
				case "damager-send-subtitle" -> {
					StringBuilder builder = new StringBuilder();
					boolean debug = split[split.length - 1].toLowerCase().startsWith("delay:");
					int endingIndex = debug ? split.length - 1 : split.length; // if ends with the delay argument, ignore that last bit
					for (int i = 1; i < endingIndex; i++) {
						builder.append(split[i]).append(" ");
					}
					sendSubtitle(damager, builder.toString(), plugin);
				}
				case "damager-actionbar" -> {
					StringBuilder builder = new StringBuilder();
					boolean debug = split[split.length - 1].toLowerCase().startsWith("delay:");
					int endingIndex = debug ? split.length - 1 : split.length; // if ends with the delay argument, ignore that last bit
					for (int i = 1; i < endingIndex; i++) {
						builder.append(split[i]).append(" ");
					}
					sendActionbar(damager, builder.toString(), plugin);
				}
				case "damager-chat" -> {
					StringBuilder builder = new StringBuilder();
					boolean debug = split[split.length - 1].toLowerCase().startsWith("delay:");
					int endingIndex = debug ? split.length - 1 : split.length; // if ends with the delay argument, ignore that last bit
					for (int i = 1; i < endingIndex; i++) {
						builder.append(split[i]).append(" ");
					}
					say(damager, builder.toString(), plugin);
				}

				case "victim-set-money" -> setMoney(victim, split[1], plugin);
				case "damager-set-money" -> setMoney(damager, split[1], plugin);

				case "victim-give-head" -> giveHeadOf(victim, split[1], plugin);
				case "damager-give-head" -> giveHeadOf(damager, split[1], plugin);

				case "victim-give-item" -> give(victim, split[1], parseI(split[2]), plugin);
				case "damager-give-item" -> give(damager, split[1], parseI(split[2]), plugin);

				case "victim-steal-damagers-hand" -> takeHandFrom(damager, victim, plugin);
				case "damager-steal-victims-hand" -> takeHandFrom(victim, damager, plugin);

				case "victim-drop-hand" -> dropHand(victim, plugin);
				case "damager-drop-hand" -> dropHand(damager, plugin);

				case "victim-damage-armor" -> damageArmorPiece(victim, split[2], split[1], plugin);
				case "damager-damage-armor" -> damageArmorPiece(damager, split[2], split[1], plugin);

				case "victim-damage-hand" -> damageHand(victim, split[1], plugin);
				case "damager-damage-hand" -> damageHand(damager, split[2], plugin);

				case "victim-repair-armor" -> fixArmorPiece(victim, split[1], plugin);
				case "damager-repair-armor" -> fixArmorPiece(damager, split[1], plugin);

				case "victim-repair-hand" -> fixHand(victim, plugin);
				case "damager-repair-hand" -> fixHand(damager, plugin);

				case "set-block" -> setBlock(victimLocation.getWorld(), split[1], split[2], split[3], split[4], plugin);

				case "time" -> setTime(victimLocation.getWorld(), split[1], plugin);
				case "weather" -> setWeather(Objects.requireNonNull(victim.getLocation().getWorld()), split[1], plugin);

				case "victim-time" -> setPlayerTime(victim, split[1], plugin);
				case "victim-weather" -> setPlayerWeather(victim, split[1], plugin);

				case "damager-time" -> setPlayerTime(damager, split[1], plugin);
				case "damager-weather" -> setPlayerWeather(damager, split[1], plugin);

				case "reset-victim-time" -> resetPlayerTime(victim, plugin);
				case "reset-victim-weather" -> resetPlayerWeather(victim, plugin);

				case "reset-damager-time" -> resetPlayerTime(damager, plugin);
				case "reset-damager-weather" -> resetPlayerWeather(damager, plugin);

				case "victim-godmode" -> makeInvisibleFor(victim, Utils.parseI(split[1]), plugin);
				case "damager-godmode" -> makeInvisibleFor(damager, Utils.parseI(split[1]), plugin);

				case "broadcast" -> {
					StringBuilder builder = new StringBuilder();
					boolean debug = split[split.length - 1].toLowerCase().startsWith("delay:");
					int endingIndex = debug ? split.length - 1 : split.length; // if ends with the delay argument, ignore that last bit
					for (int i = 1; i < endingIndex; i++) {
						builder.append(split[i]).append(" ");
					}
					announce(builder.toString(), plugin);
				}
				case "log" -> {
					StringBuilder builder = new StringBuilder();
					boolean debug = split[split.length - 1].toLowerCase().startsWith("delay:");
					int endingIndex = debug ? split.length - 1 : split.length; // if ends with the delay argument, ignore that last bit
					for (int i = 1; i < endingIndex; i++) {
						builder.append(split[i]).append(" ");
					}
					plugin.debugger.log("[Manual Logging] " + builder);
				}
				case "console-command" -> {
					StringBuilder builder = new StringBuilder();
					boolean debug = split[split.length - 1].toLowerCase().startsWith("delay:");
					int endingIndex = debug ? split.length - 1 : split.length; // if ends with the delay argument, ignore that last bit
					for (int i = 1; i < endingIndex; i++) {
						builder.append(split[i]).append(" ");
					}
					plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), builder.toString());
				}
			}
		}, delay);
	}

	private void standardEntityParse(UnderscoreEnchants plugin, Player player, Location location, Entity entity, String[] split) {
		long delay = 0;

		if (split[split.length - 1].toLowerCase().startsWith("delay:")) {
			String parsableDelay = split[split.length - 1].replace("delay:", "");
			delay = parseL(parsableDelay);
		}

		Bukkit.getScheduler().runTaskLater(plugin, () -> {

			switch (split[0].toLowerCase(Locale.ROOT)) {
				case "player-velocity" -> produceVelocity(player, split);

				case "player-pdc-set" -> setPDC(player, NamespacedKey.fromString(split[1], plugin), split[2], plugin);

				case "player-sound" -> playSound(player, split[1], plugin);
				case "location-sound" -> PlayerUtils.playWorldSound(player, split[2], split[3], split[4], split[1], plugin);

				case "player-effect" -> addPotion(player, split[1], Utils.parseI(split[2]), Utils.parseI(split[3]), plugin);

				case "player-remove-effect" -> removePotion(player, split[1], plugin);
				case "player-remove-buffs" -> removeBuffs(player, plugin);
				case "player-remove-debuffs" -> removeDebuffs(player, plugin);

				case "player-bossbar" -> {
					StringBuilder builder = new StringBuilder();
					boolean debug = split[split.length - 1].toLowerCase().startsWith("delay:");
					int endingIndex = debug ? split.length - 1 : split.length; // if ends with the delay argument, ignore that last bit
					for (int i = 3; i < endingIndex; i++) {
						builder.append(split[i]).append(" ");
					}
					sendBar(player, split[3], split[2], builder.toString(), plugin);
				}

				case "player-set-hand" -> setMainHand(player, split[1], plugin);

				case "player-shuffle-hotbar" -> shuffleHotbar(player, plugin);
				case "player-shuffle-inventory" -> shuffleInventory(player, plugin);

				case "player-strike-fake-lightning" -> strikeFakeLightning(location, plugin);
				case "player-strike-real-lightning" -> strikeLightning(location, plugin);

				case "player-particle" -> spawnParticle(player, location, split[1], plugin);
				case "player-particle-boots" -> spawnParticleBoots(player, location, split[1], plugin);

				case "spawn-entity" -> spawnEntity(player, split[1], split[2], split[3], split[4], plugin);
				case "spawn-entity-detailed" -> spawnEntity(player, split[1], split[2], split[3], split[4], split[5], split[6], split[7], split[8], split[9], plugin);

				case "player-teleport" -> setLocation(player, Utils.parseI(split[1]), Utils.parseI(split[2]), Utils.parseI(split[3]), plugin);
				case "player-direction" -> setDirection(player, split[1], split[2], plugin);
				case "player-forward" -> sendForward(player, split[1], plugin);

				case "player-world" -> setWorld(player, split[1], plugin);
				case "create-world" -> createWorld(split[1], split[2], split[3], plugin);

				case "player-teleport-bed" -> teleportToBed(player, plugin);

				case "player-set-health" -> setHealth(player, split[1], plugin);
				case "player-set-max-health" -> setMaximumHealth(player, split[1], plugin);

				case "player-set-fire" -> setFire(player, split[1], plugin);
				case "player-set-food" -> setFood(player, split[1], plugin);
				case "player-set-oxygen" -> setAir(player, split[1], plugin);

				case "player-set-exp" -> setXp(player, split[1], plugin);
				case "player-set-level" -> setLevel(player, split[1], plugin);

				case "player-send-arrow" -> sendArrow(player, plugin);
				case "player-send-fireball" -> sendFireball(player, plugin);

				case "player-send-message" -> {
					StringBuilder builder = new StringBuilder();
					boolean debug = split[split.length - 1].toLowerCase().startsWith("delay:");
					int endingIndex = debug ? split.length - 1 : split.length; // if ends with the delay argument, ignore that last bit
					for (int i = 1; i < endingIndex; i++) {
						builder.append(split[i]).append(" ");
					}
					tellTo(player, builder.toString(), plugin);
				}
				case "player-send-title" -> {
					StringBuilder builder = new StringBuilder();
					boolean debug = split[split.length - 1].toLowerCase().startsWith("delay:");
					int endingIndex = debug ? split.length - 1 : split.length; // if ends with the delay argument, ignore that last bit
					for (int i = 1; i < endingIndex; i++) {
						builder.append(split[i]).append(" ");
					}
					sendTitle(player, builder.toString(), plugin);
				}
				case "player-send-subtitle" -> {
					StringBuilder builder = new StringBuilder();
					boolean debug = split[split.length - 1].toLowerCase().startsWith("delay:");
					int endingIndex = debug ? split.length - 1 : split.length; // if ends with the delay argument, ignore that last bit
					for (int i = 1; i < endingIndex; i++) {
						builder.append(split[i]).append(" ");
					}
					sendSubtitle(player, builder.toString(), plugin);
				}
				case "player-actionbar" -> {
					StringBuilder builder = new StringBuilder();
					boolean debug = split[split.length - 1].toLowerCase().startsWith("delay:");
					int endingIndex = debug ? split.length - 1 : split.length; // if ends with the delay argument, ignore that last bit
					for (int i = 1; i < endingIndex; i++) {
						builder.append(split[i]).append(" ");
					}
					sendActionbar(player, builder.toString(), plugin);
				}
				case "player-chat" -> {
					StringBuilder builder = new StringBuilder();
					boolean debug = split[split.length - 1].toLowerCase().startsWith("delay:");
					int endingIndex = debug ? split.length - 1 : split.length; // if ends with the delay argument, ignore that last bit
					for (int i = 1; i < endingIndex; i++) {
						builder.append(split[i]).append(" ");
					}
					say(player, builder.toString(), plugin);
				}

				case "player-set-money" -> setMoney(player, split[1], plugin);

				case "player-give-head" -> giveHeadOf(player, split[1], plugin);
				case "player-give-item" -> give(player, split[1], parseI(split[2]), plugin);

				case "player-drop-hand" -> dropHand(player, plugin);

				case "player-damage-armor" -> damageArmorPiece(player, split[2], split[1], plugin);
				case "player-damage-hand" -> damageHand(player, split[1], plugin);

				case "player-repair-armor" -> fixArmorPiece(player, split[1], plugin);
				case "player-repair-hand" -> fixHand(player, plugin);

				case "set-block" -> setBlock(location.getWorld(), split[1], split[2], split[3], split[4], plugin);

				case "time" -> setTime(location.getWorld(), split[1], plugin);
				case "weather" -> setWeather(Objects.requireNonNull(location.getWorld()), split[1], plugin);

				case "player-time" -> setPlayerTime(player, split[1], plugin);
				case "player-weather" -> setPlayerWeather(player, split[1], plugin);

				case "reset-player-time" -> resetPlayerTime(player, plugin);
				case "reset-player-weather" -> resetPlayerWeather(player, plugin);

				case "player-godmode" -> makeInvisibleFor(player, Utils.parseI(split[1]), plugin);

				case "broadcast" -> {
					StringBuilder builder = new StringBuilder();
					boolean debug = split[split.length - 1].toLowerCase().startsWith("delay:");
					int endingIndex = debug ? split.length - 1 : split.length; // if ends with the delay argument, ignore that last bit
					for (int i = 1; i < endingIndex; i++) {
						builder.append(split[i]).append(" ");
					}
					announce(builder.toString(), plugin);
				}
				case "log" -> {
					StringBuilder builder = new StringBuilder();
					boolean debug = split[split.length - 1].toLowerCase().startsWith("delay:");
					int endingIndex = debug ? split.length - 1 : split.length; // if ends with the delay argument, ignore that last bit
					for (int i = 1; i < endingIndex; i++) {
						builder.append(split[i]).append(" ");
					}
					plugin.debugger.log("[Manual Logging] " + builder);
				}

				case "console-command" -> {
					StringBuilder builder = new StringBuilder();
					boolean debug = split[split.length - 1].toLowerCase().startsWith("delay:");
					int endingIndex = debug ? split.length - 1 : split.length; // if ends with the delay argument, ignore that last bit
					for (int i = 1; i < endingIndex; i++) {
						builder.append(split[i]).append(" ");
					}
					plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), builder.toString());
				}

				case "entity-velocity" -> produceVelocity(entity, split);

				case "entity-effect" -> addPotion(entity, split[1], Utils.parseI(split[2]), Utils.parseI(split[3]), plugin);

				case "entity-remove-effect" -> removePotion(entity, split[1], plugin);
				case "entity-remove-buffs" -> removeBuffs(entity, plugin);
				case "entity-remove-debuffs" -> removeDebuffs(entity, plugin);

				case "entity-strike-fake-lightning" -> strikeFakeLightning(entity.getLocation(), plugin);
				case "entity-strike-real-lightning" -> strikeLightning(entity.getLocation(), plugin);

				case "entity-particle" -> spawnParticle(entity, entity.getLocation(), split[1], plugin);
				case "entity-particle-boots" -> spawnParticleBoots(entity, entity.getLocation(), split[1], plugin);

				case "entity-teleport" -> setLocation(entity, Utils.parseI(split[1]), Utils.parseI(split[2]), Utils.parseI(split[3]), plugin);
				case "entity-direction" -> setDirection(entity, split[1], split[2], plugin);
				case "entity-forward" -> sendForward(entity, split[1], plugin);

				case "entity-world" -> setWorld(entity, split[1], plugin);

				case "entity-set-health" -> setHealth(entity, split[1], plugin);
				case "entity-set-max-health" -> setMaximumHealth(entity, split[1], plugin);

				case "entity-set-fire" -> setFire(entity, split[1], plugin);

				case "entity-send-arrow" -> sendArrow(entity, plugin);
				case "entity-send-fireball" -> sendFireball(entity, plugin);
			}
		}, delay);
	}
}

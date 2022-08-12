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
		Location victimLocation = getLocation(victim), damagerLocation = getLocation(damager);

		String[] split = completeParse(new PDCPlaceholder(victim, damager), action, victimLocation.getWorld(), plugin,
			Pair.of("%damage%",                     String.valueOf(ev.getDamage())),
			Pair.of("%victim_name%",                victim.getName()),
			Pair.of("%damager_name%",               damager.getName()),
			Pair.of("%victim_x%",                   getXString(victim)),
			Pair.of("%damager_x%",                  getXString(damager)),
			Pair.of("%victim_y%",                   getYString(victim)),
			Pair.of("%damager_y%",                  getYString(damager)),
			Pair.of("%victim_z%",                   getZString(victim)),
			Pair.of("%damager_z%",                  getZString(damager)),
			Pair.of("%victim_yaw%",                 getYawString(victim)),
			Pair.of("%victim_pitch%",               getPitchString(victim)),
			Pair.of("%damager_yaw%",                getYawString(damager)),
			Pair.of("%damager_pitch%",              getPitchString(damager)),
			Pair.of("%victim_money%",               getMoneyString(victim)),
			Pair.of("%damager_money%",              getMoneyString(damager)),
			Pair.of("%victim_xp%",                  getXpString(victim)),
			Pair.of("%damager_xp%",                 getXpString(damager)),
			Pair.of("%victim_xp_levels%",           getLevelString(victim)),
			Pair.of("%damager_xp_levels%",          getLevelString(damager)),
			Pair.of("%victim_health%",              getHealthString(victim)),
			Pair.of("%damager_health%",             getHealthString(damager)),
			Pair.of("%victim_max_health%",          getMaximumHealthString(victim)),
			Pair.of("%damager_max_health%",         getMaximumHealthString(damager)),
			Pair.of("%victim_food%",                getFoodString(victim)),
			Pair.of("%damager_food%",               getFoodString(damager)),
			Pair.of("%victim_air%",                 getAirString(victim)),
			Pair.of("%damager_air%",                getAirString(damager)),
			Pair.of("%victim_max_air%",             getMaximumAirString(victim)),
			Pair.of("%damager_max_air%",            getMaximumAirString(damager)),
			Pair.of("%victim_godmode%",             invisibleForString(victim)),
			Pair.of("%damager_godmode%",            invisibleForString(damager)),
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
			Pair.of("%player_x%",                   getXString(player)),
			Pair.of("%player_y%",                   getYString(player)),
			Pair.of("%player_z%",                   getZString(player)),
			Pair.of("%player_yaw%",                 getYawString(player)),
			Pair.of("%player_pitch%",               getPitchString(player)),
			Pair.of("%player_money%",               getMoneyString(player)),
			Pair.of("%player_xp%",                  getXpString(player)),
			Pair.of("%player_xp_levels%",           getLevelString(player)),
			Pair.of("%player_health%",              getHealthString(player)),
			Pair.of("%player_max_health%",          getMaximumHealthString(player)),
			Pair.of("%player_food%",                getFoodString(player)),
			Pair.of("%player_air%",                 getAirString(player)),
			Pair.of("%player_max_air%",             getMaximumAirString(player)),
			Pair.of("%player_godmode%",             invisibleForString(player)),
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
			Pair.of("%player_x%",                   getXString(player)),
			Pair.of("%player_y%",                   getYString(player)),
			Pair.of("%player_z%",                   getZString(player)),
			Pair.of("%player_yaw%",                 getYawString(player)),
			Pair.of("%player_pitch%",               getPitchString(player)),
			Pair.of("%player_money%",               getMoneyString(player)),
			Pair.of("%player_xp%",                  getXpString(player)),
			Pair.of("%player_xp_levels%",           getLevelString(player)),
			Pair.of("%player_health%",              getHealthString(player)),
			Pair.of("%player_max_health%",          getMaximumHealthString(player)),
			Pair.of("%player_food%",                getFoodString(player)),
			Pair.of("%player_air%",                 getAirString(player)),
			Pair.of("%player_max_air%",             getMaximumAirString(player)),
			Pair.of("%player_godmode%",             invisibleForString(player)),
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
		Location victimLocation = getLocation(victim), damagerLocation = getLocation(damager);

		String[] split = completeParse(new PDCPlaceholder(victim, damager), action, victimLocation.getWorld(), plugin,
			Pair.of("%damage%",                     String.valueOf(ev.getDamage())),
			Pair.of("%victim_name%",                victim.getName()),
			Pair.of("%damager_name%",               damager.getName()),
			Pair.of("%victim_x%",                   getXString(victim)),
			Pair.of("%damager_x%",                  getXString(damager)),
			Pair.of("%victim_y%",                   getYString(victim)),
			Pair.of("%damager_y%",                  getYString(damager)),
			Pair.of("%victim_z%",                   getZString(victim)),
			Pair.of("%damager_z%",                  getZString(damager)),
			Pair.of("%victim_yaw%",                 getYawString(victim)),
			Pair.of("%victim_pitch%",               getPitchString(victim)),
			Pair.of("%damager_yaw%",                getYawString(damager)),
			Pair.of("%damager_pitch%",              getPitchString(damager)),
			Pair.of("%victim_money%",               getMoneyString(victim)),
			Pair.of("%damager_money%",              getMoneyString(damager)),
			Pair.of("%victim_xp%",                  getXpString(victim)),
			Pair.of("%damager_xp%",                 getXpString(damager)),
			Pair.of("%victim_xp_levels%",           getLevelString(victim)),
			Pair.of("%damager_xp_levels%",          getLevelString(damager)),
			Pair.of("%victim_health%",              getHealthString(victim)),
			Pair.of("%damager_health%",             getHealthString(damager)),
			Pair.of("%victim_max_health%",          getMaximumHealthString(victim)),
			Pair.of("%damager_max_health%",         getMaximumHealthString(damager)),
			Pair.of("%victim_food%",                getFoodString(victim)),
			Pair.of("%damager_food%",               getFoodString(damager)),
			Pair.of("%victim_air%",                 getAirString(victim)),
			Pair.of("%damager_air%",                getAirString(damager)),
			Pair.of("%victim_max_air%",             getMaximumAirString(victim)),
			Pair.of("%damager_max_air%",            getMaximumAirString(damager)),
			Pair.of("%victim_godmode%",             invisibleForString(victim)),
			Pair.of("%damager_godmode%",            invisibleForString(damager)),
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
			Pair.of("%player_x%",                   getXString(player)),
			Pair.of("%player_y%",                   getYString(player)),
			Pair.of("%player_z%",                   getZString(player)),
			Pair.of("%player_yaw%",                 getYawString(player)),
			Pair.of("%player_pitch%",               getPitchString(player)),
			Pair.of("%player_money%",               getMoneyString(player)),
			Pair.of("%player_xp%",                  getXpString(player)),
			Pair.of("%player_xp_levels%",           getLevelString(player)),
			Pair.of("%player_health%",              getHealthString(player)),
			Pair.of("%player_max_health%",          getMaximumHealthString(player)),
			Pair.of("%player_food%",                getFoodString(player)),
			Pair.of("%player_air%",                 getAirString(player)),
			Pair.of("%player_max_air%",             getMaximumAirString(player)),
			Pair.of("%player_godmode%",             invisibleForString(player)),
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
			Pair.of("%player_x%",                   getXString(player)),
			Pair.of("%player_y%",                   getYString(player)),
			Pair.of("%player_z%",                   getZString(player)),
			Pair.of("%player_yaw%",                 getYawString(player)),
			Pair.of("%player_pitch%",               getPitchString(player)),
			Pair.of("%player_money%",               getMoneyString(player)),
			Pair.of("%player_xp%",                  getXpString(player)),
			Pair.of("%player_xp_levels%",           getLevelString(player)),
			Pair.of("%player_health%",              getHealthString(player)),
			Pair.of("%player_max_health%",          getMaximumHealthString(player)),
			Pair.of("%player_food%",                getFoodString(player)),
			Pair.of("%player_air%",                 getAirString(player)),
			Pair.of("%player_max_air%",             getMaximumAirString(player)),
			Pair.of("%player_godmode%",             invisibleForString(player)),
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
			Pair.of("%player_x%",                   getXString(player)),
			Pair.of("%player_y%",                   getYString(player)),
			Pair.of("%player_z%",                   getZString(player)),
			Pair.of("%player_yaw%",                 getYawString(player)),
			Pair.of("%player_pitch%",               getPitchString(player)),
			Pair.of("%player_money%",               getMoneyString(player)),
			Pair.of("%player_xp%",                  getXpString(player)),
			Pair.of("%player_xp_levels%",           getLevelString(player)),
			Pair.of("%player_health%",              getHealthString(player)),
			Pair.of("%player_max_health%",          getMaximumHealthString(player)),
			Pair.of("%player_food%",                getFoodString(player)),
			Pair.of("%player_air%",                 getAirString(player)),
			Pair.of("%player_max_air%",             getMaximumAirString(player)),
			Pair.of("%player_godmode%",             invisibleForString(player)),
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
			Pair.of("%player_x%",                   getXString(player)),
			Pair.of("%player_y%",                   getYString(player)),
			Pair.of("%player_z%",                   getZString(player)),
			Pair.of("%player_yaw%",                 getYawString(player)),
			Pair.of("%player_pitch%",               getPitchString(player)),
			Pair.of("%player_money%",               getMoneyString(player)),
			Pair.of("%player_xp%",                  getXpString(player)),
			Pair.of("%player_xp_levels%",           getLevelString(player)),
			Pair.of("%player_health%",              getHealthString(player)),
			Pair.of("%player_max_health%",          getMaximumHealthString(player)),
			Pair.of("%player_food%",                getFoodString(player)),
			Pair.of("%player_air%",                 getAirString(player)),
			Pair.of("%player_max_air%",             getMaximumAirString(player)),
			Pair.of("%player_godmode%",             invisibleForString(player)),
			Pair.of("%random_positive_effect%",     plugin.getPositiveEffects().get(new Random().nextInt(plugin.getPositiveEffects().size())).getName()),
			Pair.of("%random_negative_effect%",     plugin.getNegativeEffects().get(new Random().nextInt(plugin.getNegativeEffects().size())).getName()),
			Pair.of("%entity_type",                 entity.getType().name()),
			Pair.of("%entity_x%",                   getXString(entity)),
			Pair.of("%entity_y%",                   getYString(entity)),
			Pair.of("%entity_z%",                   getZString(entity)),
			Pair.of("%entity_yaw%",                 getYawString(entity)),
			Pair.of("%entity_pitch%",               getPitchString(entity)),
			Pair.of("%entity_health%",              getHealthString(entity)),
			Pair.of("%entity_max_health%",          getMaximumHealthString(entity))
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
			Pair.of("%player_x%",                   getXString(player)),
			Pair.of("%player_y%",                   getYString(player)),
			Pair.of("%player_z%",                   getZString(player)),
			Pair.of("%player_yaw%",                 getYawString(player)),
			Pair.of("%player_pitch%",               getPitchString(player)),
			Pair.of("%player_money%",               getMoneyString(player)),
			Pair.of("%player_xp%",                  getXpString(player)),
			Pair.of("%player_xp_levels%",           getLevelString(player)),
			Pair.of("%player_health%",              getHealthString(player)),
			Pair.of("%player_max_health%",          getMaximumHealthString(player)),
			Pair.of("%player_food%",                getFoodString(player)),
			Pair.of("%player_air%",                 getAirString(player)),
			Pair.of("%player_max_air%",             getMaximumAirString(player)),
			Pair.of("%player_godmode%",             invisibleForString(player)),
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
			Pair.of("%player_x%",                   getXString(player)),
			Pair.of("%player_y%",                   getYString(player)),
			Pair.of("%player_z%",                   getZString(player)),
			Pair.of("%player_yaw%",                 getYawString(player)),
			Pair.of("%player_pitch%",               getPitchString(player)),
			Pair.of("%player_money%",               getMoneyString(player)),
			Pair.of("%player_xp%",                  getXpString(player)),
			Pair.of("%player_xp_levels%",           getLevelString(player)),
			Pair.of("%player_health%",              getHealthString(player)),
			Pair.of("%player_max_health%",          getMaximumHealthString(player)),
			Pair.of("%player_food%",                getFoodString(player)),
			Pair.of("%player_air%",                 getAirString(player)),
			Pair.of("%player_max_air%",             getMaximumAirString(player)),
			Pair.of("%player_godmode%",             invisibleForString(player)),
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
			Pair.of("%player_x%",                   getXString(player)),
			Pair.of("%player_y%",                   getYString(player)),
			Pair.of("%player_z%",                   getZString(player)),
			Pair.of("%player_yaw%",                 getYawString(player)),
			Pair.of("%player_pitch%",               getPitchString(player)),
			Pair.of("%player_money%",               getMoneyString(player)),
			Pair.of("%player_xp%",                  getXpString(player)),
			Pair.of("%player_xp_levels%",           getLevelString(player)),
			Pair.of("%player_health%",              getHealthString(player)),
			Pair.of("%player_max_health%",          getMaximumHealthString(player)),
			Pair.of("%player_food%",                getFoodString(player)),
			Pair.of("%player_air%",                 getAirString(player)),
			Pair.of("%player_max_air%",             getMaximumAirString(player)),
			Pair.of("%player_godmode%",             invisibleForString(player)),
			Pair.of("%random_positive_effect%",     plugin.getPositiveEffects().get(new Random().nextInt(plugin.getPositiveEffects().size())).getName()),
			Pair.of("%random_negative_effect%",     plugin.getNegativeEffects().get(new Random().nextInt(plugin.getNegativeEffects().size())).getName()),
			Pair.of("%damage%",                     String.valueOf(damage)),
			Pair.of("%entity_type",                 entity.getType().name()),
			Pair.of("%entity_x%",                   getXString(entity)),
			Pair.of("%entity_y%",                   getYString(entity)),
			Pair.of("%entity_z%",                   getZString(entity)),
			Pair.of("%entity_yaw%",                 getYawString(entity)),
			Pair.of("%entity_pitch%",               getPitchString(entity)),
			Pair.of("%entity_health%",              getHealthString(entity)),
			Pair.of("%entity_max_health%",          getMaximumHealthString(entity))
		);

		standardEntityParse(plugin, player, location, entity, split);
	}

	public void parseAction(PlayerShootBowEvent ev, String action, UnderscoreEnchants plugin) {
		Player player = ev.getShooter();
		Location location = player.getLocation();
		float force = ev.getForce();

		String[] split = completeParse(new PDCPlaceholder(player), action, location.getWorld(), plugin,
			Pair.of("%player_name%",                player.getName()),
			Pair.of("%player_x%",                   getXString(player)),
			Pair.of("%player_y%",                   getYString(player)),
			Pair.of("%player_z%",                   getZString(player)),
			Pair.of("%player_yaw%",                 getYawString(player)),
			Pair.of("%player_pitch%",               getPitchString(player)),
			Pair.of("%player_money%",               getMoneyString(player)),
			Pair.of("%player_xp%",                  getXpString(player)),
			Pair.of("%player_xp_levels%",           getLevelString(player)),
			Pair.of("%player_health%",              getHealthString(player)),
			Pair.of("%player_max_health%",          getMaximumHealthString(player)),
			Pair.of("%player_food%",                getFoodString(player)),
			Pair.of("%player_air%",                 getAirString(player)),
			Pair.of("%player_max_air%",             getMaximumAirString(player)),
			Pair.of("%player_godmode%",             invisibleForString(player)),
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
			Pair.of("%player_x%",                   getXString(player)),
			Pair.of("%player_y%",                   getYString(player)),
			Pair.of("%player_z%",                   getZString(player)),
			Pair.of("%player_yaw%",                 getYawString(player)),
			Pair.of("%player_pitch%",               getPitchString(player)),
			Pair.of("%player_money%",               getMoneyString(player)),
			Pair.of("%player_xp%",                  getXpString(player)),
			Pair.of("%player_xp_levels%",           getLevelString(player)),
			Pair.of("%player_health%",              getHealthString(player)),
			Pair.of("%player_max_health%",          getMaximumHealthString(player)),
			Pair.of("%player_food%",                getFoodString(player)),
			Pair.of("%player_air%",                 getAirString(player)),
			Pair.of("%player_max_air%",             getMaximumAirString(player)),
			Pair.of("%player_godmode%",             invisibleForString(player)),
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
				case "player-pdc-set" -> setPDC(player, NamespacedKey.fromString(split[1], plugin), split[2]);

				case "player-velocity" -> produceVelocity(player, split);

				case "player-sound" -> playSound(player, split[1]);
				case "location-sound" -> PlayerUtils.playWorldSound(player, split[2], split[3], split[4], split[1]);

				case "player-effect" -> addPotion(player, split[1], Utils.parseI(split[2]), Utils.parseI(split[3]));

				case "player-remove-effect" -> removePotion(player, split[1]);
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

				case "player-set-hand" -> setMainHand(player, split[1]);

				case "player-shuffle-hotbar" -> shuffleHotbar(player);
				case "player-shuffle-inventory" -> shuffleInventory(player);

				case "player-strike-fake-lightning" -> strikeFakeLightning(location);
				case "player-strike-real-lightning" -> strikeLightning(location);

				case "player-particle" -> spawnParticle(player, location, split[1]);
				case "player-particle-boots" -> spawnParticleBoots(player, location, split[1]);

				case "spawn-entity" -> spawnEntity(player, split[1], split[2], split[3], split[4]);
				case "spawn-entity-detailed" -> spawnEntity(player, split[1], split[2], split[3], split[4], split[5], split[6], split[7], split[8], split[9]);

				case "player-teleport" -> setLocation(player, Utils.parseI(split[1]), Utils.parseI(split[2]), Utils.parseI(split[3]));
				case "player-direction" -> setDirection(player, split[1], split[2]);
				case "player-forward" -> sendForward(player, split[1]);

				case "player-world" -> setWorld(player, split[1]);
				case "create-world" -> createWorld(split[1], split[2], split[3]);

				case "player-teleport-bed" -> teleportToBed(player);

				case "player-set-health" -> setHealth(player, split[1]);
				case "player-set-max-health" -> setMaximumHealth(player, split[1]);

				case "player-set-fire" -> setFire(player, split[1]);
				case "player-set-food" -> setFood(player, split[1]);
				case "player-set-oxygen" -> setAir(player, split[1]);

				case "player-set-exp" -> setXp(player, split[1]);
				case "player-set-level" -> setLevel(player, split[1]);

				case "player-send-arrow" -> sendArrow(player);
				case "player-send-fireball" -> sendFireball(player);

				case "player-send-message" -> {
					StringBuilder builder = new StringBuilder();
					boolean debug = split[split.length - 1].toLowerCase().startsWith("delay:");
					int endingIndex = debug ? split.length - 1 : split.length; // if ends with the delay argument, ignore that last bit
					for (int i = 1; i < endingIndex; i++) {
						builder.append(split[i]).append(" ");
					}
					tellTo(player, builder.toString());
				}
				case "player-send-title" -> {
					StringBuilder builder = new StringBuilder();
					boolean debug = split[split.length - 1].toLowerCase().startsWith("delay:");
					int endingIndex = debug ? split.length - 1 : split.length; // if ends with the delay argument, ignore that last bit
					for (int i = 1; i < endingIndex; i++) {
						builder.append(split[i]).append(" ");
					}
					sendTitle(player, builder.toString());
				}
				case "player-send-subtitle" -> {
					StringBuilder builder = new StringBuilder();
					boolean debug = split[split.length - 1].toLowerCase().startsWith("delay:");
					int endingIndex = debug ? split.length - 1 : split.length; // if ends with the delay argument, ignore that last bit
					for (int i = 1; i < endingIndex; i++) {
						builder.append(split[i]).append(" ");
					}
					sendSubtitle(player, builder.toString());
				}
				case "player-actionbar" -> {
					StringBuilder builder = new StringBuilder();
					boolean debug = split[split.length - 1].toLowerCase().startsWith("delay:");
					int endingIndex = debug ? split.length - 1 : split.length; // if ends with the delay argument, ignore that last bit
					for (int i = 1; i < endingIndex; i++) {
						builder.append(split[i]).append(" ");
					}
					sendActionbar(player, builder.toString());
				}
				case "player-chat" -> {
					StringBuilder builder = new StringBuilder();
					boolean debug = split[split.length - 1].toLowerCase().startsWith("delay:");
					int endingIndex = debug ? split.length - 1 : split.length; // if ends with the delay argument, ignore that last bit
					for (int i = 1; i < endingIndex; i++) {
						builder.append(split[i]).append(" ");
					}
					say(player, builder.toString());
				}

				case "player-set-money" -> setMoney(player, split[1]);

				case "player-give-head" -> giveHeadOf(player, split[1]);
				case "player-give-item" -> give(player, split[1], parseI(split[2]));

				case "player-drop-hand" -> dropHand(player);

				case "player-damage-armor" -> damageArmorPiece(player, split[2], split[1]);
				case "player-damage-hand" -> damageHand(player, split[1]);

				case "player-repair-armor" -> fixArmorPiece(player, split[1]);
				case "player-repair-hand" -> fixHand(player);

				case "set-block" -> setBlock(location.getWorld(), split[1], split[2], split[3], split[4]);

				case "time" -> setTime(location.getWorld(), split[1]);
				case "weather" -> setWeather(Objects.requireNonNull(location.getWorld()), split[1]);

				case "player-time" -> setPlayerTime(player, split[1]);
				case "player-weather" -> setPlayerWeather(player, split[1]);

				case "reset-player-time" -> resetPlayerTime(player);
				case "reset-player-weather" -> resetPlayerWeather(player);

				case "player-godmode" -> makeInvisibleFor(player, Utils.parseI(split[1]), plugin);

				case "broadcast" -> {
					StringBuilder builder = new StringBuilder();
					boolean debug = split[split.length - 1].toLowerCase().startsWith("delay:");
					int endingIndex = debug ? split.length - 1 : split.length; // if ends with the delay argument, ignore that last bit
					for (int i = 1; i < endingIndex; i++) {
						builder.append(split[i]).append(" ");
					}
					announce(builder.toString());
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

				case "victim-pdc-set" -> setPDC(victim, NamespacedKey.fromString(split[1], plugin), split[2]);
				case "damager-pdc-set" -> setPDC(damager, NamespacedKey.fromString(split[1], plugin), split[2]);

				case "victim-sound" -> playSound(victim, split[1]);
				case "damager-sound" -> playSound(damager, split[1]);
				case "location-sound" -> playWorldSound(damager, split[2], split[3], split[4], split[1]);

				case "victim-effect" -> addPotion(victim, split[1], Utils.parseI(split[2]), Utils.parseI(split[3]));
				case "damager-effect" -> addPotion(damager, split[1], Utils.parseI(split[2]), Utils.parseI(split[3]));

				case "victim-remove-effect" -> removePotion(victim, split[1]);
				case "damager-remove-effect" -> removePotion(damager, split[1]);
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

				case "victim-set-hand" -> setMainHand(victim, split[1]);
				case "damager-set-hand" -> setMainHand(damager, split[1]);

				case "victim-shuffle-hotbar" -> shuffleHotbar(victim);
				case "damager-shuffle-hotbar" -> shuffleHotbar(damager);

				case "victim-shuffle-inventory" -> shuffleInventory(victim);
				case "damager-shuffle-inventory" -> shuffleInventory(damager);

				case "victim-strike-fake-lightning" -> strikeFakeLightning(victimLocation);
				case "damager-strike-fake-lightning" -> strikeFakeLightning(damagerLocation);
				case "victim-strike-real-lightning" -> strikeLightning(victimLocation);
				case "damager-strike-real-lightning" -> strikeLightning(damagerLocation);

				case "victim-particle" -> spawnParticle(victim, victimLocation, split[1]);
				case "damager-particle" -> spawnParticle(damager, damagerLocation, split[1]);
				case "victim-particle-boots" -> spawnParticleBoots(victim, victimLocation, split[1]);
				case "damager-particle-boots" -> spawnParticleBoots(damager, damagerLocation, split[1]);

				case "spawn-entity" -> spawnEntity(victim, split[1], split[2], split[3], split[4]);
				case "spawn-entity-detailed" -> spawnEntity(victim, split[1], split[2], split[3], split[4], split[5], split[6], split[7], split[8], split[9]);

				case "victim-teleport" -> setLocation(victim, Utils.parseD(split[1]), Utils.parseD(split[2]), Utils.parseD(split[3]));
				case "damager-teleport" -> setLocation(damager, Utils.parseD(split[1]), Utils.parseD(split[2]), Utils.parseD(split[3]));

				case "victim-direction" -> setDirection(victim, split[1], split[2]);
				case "damager-direction" -> setDirection(damager, split[1], split[2]);

				case "victim-forward" -> sendForward(victim, split[1]);
				case "damager-forward" -> sendForward(damager, split[1]);

				case "victim-world" -> setWorld(victim, split[1]);
				case "damager-world" -> setWorld(victim, split[2]);

				case "create-world" -> createWorld(split[1], split[2], split[3]);

				case "victim-teleport-bed" -> teleportToBed(victim);
				case "damager-teleport-bed" -> teleportToBed(damager);

				case "victim-set-health" -> setHealth(victim, Utils.parseD(split[1]));
				case "damager-set-health" -> setHealth(damager, Utils.parseD(split[1]));

				case "victim-set-max-health" -> setMaximumHealth(victim, Utils.parseD(split[1]));
				case "damager-set-max-health" -> setMaximumHealth(damager, Utils.parseD(split[1]));

				case "victim-set-fire" -> setFire(victim, split[1]);
				case "damager-set-fire" -> setFire(damager, split[1]);

				case "victim-set-food" -> setFood(victim, split[1]);
				case "damager-set-food" -> setFood(damager, split[1]);

				case "victim-set-oxygen" -> setAir(victim, split[1]);
				case "damager-set-oxygen" -> setAir(damager, split[1]);

				case "victim-set-exp" -> setXp(victim, split[1]);
				case "damager-set-exp" -> setXp(damager, split[1]);

				case "victim-set-level" -> setLevel(victim, split[1]);
				case "damager-set-level" -> setLevel(damager, split[1]);

				case "victim-send-arrow" -> sendArrow(victim);
				case "damager-send-arrow" -> sendArrow(damager);

				case "victim-send-fireball" -> sendFireball(victim);
				case "damager-send-fireball" -> sendFireball(damager);

				case "victim-send-message" -> {
					StringBuilder builder = new StringBuilder();
					boolean debug = split[split.length - 1].toLowerCase().startsWith("delay:");
					int endingIndex = debug ? split.length - 1 : split.length; // if ends with the delay argument, ignore that last bit
					for (int i = 1; i < endingIndex; i++) {
						builder.append(split[i]).append(" ");
					}
					tellTo(victim, builder.toString());
				}
				case "victim-send-title" -> {
					StringBuilder builder = new StringBuilder();
					boolean debug = split[split.length - 1].toLowerCase().startsWith("delay:");
					int endingIndex = debug ? split.length - 1 : split.length; // if ends with the delay argument, ignore that last bit
					for (int i = 1; i < endingIndex; i++) {
						builder.append(split[i]).append(" ");
					}
					sendTitle(victim, builder.toString());
				}
				case "victim-send-subtitle" -> {
					StringBuilder builder = new StringBuilder();
					boolean debug = split[split.length - 1].toLowerCase().startsWith("delay:");
					int endingIndex = debug ? split.length - 1 : split.length; // if ends with the delay argument, ignore that last bit
					for (int i = 1; i < endingIndex; i++) {
						builder.append(split[i]).append(" ");
					}
					sendSubtitle(victim, builder.toString());
				}
				case "victim-actionbar" -> {
					StringBuilder builder = new StringBuilder();
					boolean debug = split[split.length - 1].toLowerCase().startsWith("delay:");
					int endingIndex = debug ? split.length - 1 : split.length; // if ends with the delay argument, ignore that last bit
					for (int i = 1; i < endingIndex; i++) {
						builder.append(split[i]).append(" ");
					}
					sendActionbar(victim, builder.toString());
				}
				case "victim-chat" -> {
					StringBuilder builder = new StringBuilder();
					boolean debug = split[split.length - 1].toLowerCase().startsWith("delay:");
					int endingIndex = debug ? split.length - 1 : split.length; // if ends with the delay argument, ignore that last bit
					for (int i = 1; i < endingIndex; i++) {
						builder.append(split[i]).append(" ");
					}
					say(victim, builder.toString());
				}

				case "damager-send-message" -> {
					StringBuilder builder = new StringBuilder();
					boolean debug = split[split.length - 1].toLowerCase().startsWith("delay:");
					int endingIndex = debug ? split.length - 1 : split.length; // if ends with the delay argument, ignore that last bit
					for (int i = 1; i < endingIndex; i++) {
						builder.append(split[i]).append(" ");
					}
					tellTo(damager, builder.toString());
				}
				case "damager-send-title" -> {
					StringBuilder builder = new StringBuilder();
					boolean debug = split[split.length - 1].toLowerCase().startsWith("delay:");
					int endingIndex = debug ? split.length - 1 : split.length; // if ends with the delay argument, ignore that last bit
					for (int i = 1; i < endingIndex; i++) {
						builder.append(split[i]).append(" ");
					}
					sendTitle(damager, builder.toString());
				}
				case "damager-send-subtitle" -> {
					StringBuilder builder = new StringBuilder();
					boolean debug = split[split.length - 1].toLowerCase().startsWith("delay:");
					int endingIndex = debug ? split.length - 1 : split.length; // if ends with the delay argument, ignore that last bit
					for (int i = 1; i < endingIndex; i++) {
						builder.append(split[i]).append(" ");
					}
					sendSubtitle(damager, builder.toString());
				}
				case "damager-actionbar" -> {
					StringBuilder builder = new StringBuilder();
					boolean debug = split[split.length - 1].toLowerCase().startsWith("delay:");
					int endingIndex = debug ? split.length - 1 : split.length; // if ends with the delay argument, ignore that last bit
					for (int i = 1; i < endingIndex; i++) {
						builder.append(split[i]).append(" ");
					}
					sendActionbar(damager, builder.toString());
				}
				case "damager-chat" -> {
					StringBuilder builder = new StringBuilder();
					boolean debug = split[split.length - 1].toLowerCase().startsWith("delay:");
					int endingIndex = debug ? split.length - 1 : split.length; // if ends with the delay argument, ignore that last bit
					for (int i = 1; i < endingIndex; i++) {
						builder.append(split[i]).append(" ");
					}
					say(damager, builder.toString());
				}

				case "victim-set-money" -> setMoney(victim, split[1]);
				case "damager-set-money" -> setMoney(damager, split[1]);

				case "victim-give-head" -> giveHeadOf(victim, split[1]);
				case "damager-give-head" -> giveHeadOf(damager, split[1]);

				case "victim-give-item" -> give(victim, split[1], parseI(split[2]));
				case "damager-give-item" -> give(damager, split[1], parseI(split[2]));

				case "victim-steal-damagers-hand" -> takeHandFrom(damager, victim);
				case "damager-steal-victims-hand" -> takeHandFrom(victim, damager);

				case "victim-drop-hand" -> dropHand(victim);
				case "damager-drop-hand" -> dropHand(damager);

				case "victim-damage-armor" -> damageArmorPiece(victim, split[2], split[1]);
				case "damager-damage-armor" -> damageArmorPiece(damager, split[2], split[1]);

				case "victim-damage-hand" -> damageHand(victim, split[1]);
				case "damager-damage-hand" -> damageHand(damager, split[2]);

				case "victim-repair-armor" -> fixArmorPiece(victim, split[1]);
				case "damager-repair-armor" -> fixArmorPiece(damager, split[1]);

				case "victim-repair-hand" -> fixHand(victim);
				case "damager-repair-hand" -> fixHand(damager);

				case "set-block" -> setBlock(victimLocation.getWorld(), split[1], split[2], split[3], split[4]);

				case "time" -> setTime(victimLocation.getWorld(), split[1]);
				case "weather" -> setWeather(Objects.requireNonNull(victim.getLocation().getWorld()), split[1]);

				case "victim-time" -> setPlayerTime(victim, split[1]);
				case "victim-weather" -> setPlayerWeather(victim, split[1]);

				case "damager-time" -> setPlayerTime(damager, split[1]);
				case "damager-weather" -> setPlayerWeather(damager, split[1]);

				case "reset-victim-time" -> resetPlayerTime(victim);
				case "reset-victim-weather" -> resetPlayerWeather(victim);

				case "reset-damager-time" -> resetPlayerTime(damager);
				case "reset-damager-weather" -> resetPlayerWeather(damager);

				case "victim-godmode" -> makeInvisibleFor(victim, Utils.parseI(split[1]), plugin);
				case "damager-godmode" -> makeInvisibleFor(damager, Utils.parseI(split[1]), plugin);

				case "broadcast" -> {
					StringBuilder builder = new StringBuilder();
					boolean debug = split[split.length - 1].toLowerCase().startsWith("delay:");
					int endingIndex = debug ? split.length - 1 : split.length; // if ends with the delay argument, ignore that last bit
					for (int i = 1; i < endingIndex; i++) {
						builder.append(split[i]).append(" ");
					}
					announce(builder.toString());
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

				case "player-pdc-set" -> setPDC(player, NamespacedKey.fromString(split[1], plugin), split[2]);

				case "player-sound" -> playSound(player, split[1]);
				case "location-sound" -> PlayerUtils.playWorldSound(player, split[2], split[3], split[4], split[1]);

				case "player-effect" -> addPotion(player, split[1], Utils.parseI(split[2]), Utils.parseI(split[3]));

				case "player-remove-effect" -> removePotion(player, split[1]);
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

				case "player-set-hand" -> setMainHand(player, split[1]);

				case "player-shuffle-hotbar" -> shuffleHotbar(player);
				case "player-shuffle-inventory" -> shuffleInventory(player);

				case "player-strike-fake-lightning" -> strikeFakeLightning(location);
				case "player-strike-real-lightning" -> strikeLightning(location);

				case "player-particle" -> spawnParticle(player, location, split[1]);
				case "player-particle-boots" -> spawnParticleBoots(player, location, split[1]);

				case "spawn-entity" -> spawnEntity(player, split[1], split[2], split[3], split[4]);
				case "spawn-entity-detailed" -> spawnEntity(player, split[1], split[2], split[3], split[4], split[5], split[6], split[7], split[8], split[9]);

				case "player-teleport" -> setLocation(player, Utils.parseI(split[1]), Utils.parseI(split[2]), Utils.parseI(split[3]));
				case "player-direction" -> setDirection(player, split[1], split[2]);
				case "player-forward" -> sendForward(player, split[1]);

				case "player-world" -> setWorld(player, split[1]);
				case "create-world" -> createWorld(split[1], split[2], split[3]);

				case "player-teleport-bed" -> teleportToBed(player);

				case "player-set-health" -> setHealth(player, split[1]);
				case "player-set-max-health" -> setMaximumHealth(player, split[1]);

				case "player-set-fire" -> setFire(player, split[1]);
				case "player-set-food" -> setFood(player, split[1]);
				case "player-set-oxygen" -> setAir(player, split[1]);

				case "player-set-exp" -> setXp(player, split[1]);
				case "player-set-level" -> setLevel(player, split[1]);

				case "player-send-arrow" -> sendArrow(player);
				case "player-send-fireball" -> sendFireball(player);

				case "player-send-message" -> {
					StringBuilder builder = new StringBuilder();
					boolean debug = split[split.length - 1].toLowerCase().startsWith("delay:");
					int endingIndex = debug ? split.length - 1 : split.length; // if ends with the delay argument, ignore that last bit
					for (int i = 1; i < endingIndex; i++) {
						builder.append(split[i]).append(" ");
					}
					tellTo(player, builder.toString());
				}
				case "player-send-title" -> {
					StringBuilder builder = new StringBuilder();
					boolean debug = split[split.length - 1].toLowerCase().startsWith("delay:");
					int endingIndex = debug ? split.length - 1 : split.length; // if ends with the delay argument, ignore that last bit
					for (int i = 1; i < endingIndex; i++) {
						builder.append(split[i]).append(" ");
					}
					sendTitle(player, builder.toString());
				}
				case "player-send-subtitle" -> {
					StringBuilder builder = new StringBuilder();
					boolean debug = split[split.length - 1].toLowerCase().startsWith("delay:");
					int endingIndex = debug ? split.length - 1 : split.length; // if ends with the delay argument, ignore that last bit
					for (int i = 1; i < endingIndex; i++) {
						builder.append(split[i]).append(" ");
					}
					sendSubtitle(player, builder.toString());
				}
				case "player-actionbar" -> {
					StringBuilder builder = new StringBuilder();
					boolean debug = split[split.length - 1].toLowerCase().startsWith("delay:");
					int endingIndex = debug ? split.length - 1 : split.length; // if ends with the delay argument, ignore that last bit
					for (int i = 1; i < endingIndex; i++) {
						builder.append(split[i]).append(" ");
					}
					sendActionbar(player, builder.toString());
				}
				case "player-chat" -> {
					StringBuilder builder = new StringBuilder();
					boolean debug = split[split.length - 1].toLowerCase().startsWith("delay:");
					int endingIndex = debug ? split.length - 1 : split.length; // if ends with the delay argument, ignore that last bit
					for (int i = 1; i < endingIndex; i++) {
						builder.append(split[i]).append(" ");
					}
					say(player, builder.toString());
				}

				case "player-set-money" -> setMoney(player, split[1]);

				case "player-give-head" -> giveHeadOf(player, split[1]);
				case "player-give-item" -> give(player, split[1], parseI(split[2]));

				case "player-drop-hand" -> dropHand(player);

				case "player-damage-armor" -> damageArmorPiece(player, split[2], split[1]);
				case "player-damage-hand" -> damageHand(player, split[1]);

				case "player-repair-armor" -> fixArmorPiece(player, split[1]);
				case "player-repair-hand" -> fixHand(player);

				case "set-block" -> setBlock(location.getWorld(), split[1], split[2], split[3], split[4]);

				case "time" -> setTime(location.getWorld(), split[1]);
				case "weather" -> setWeather(Objects.requireNonNull(location.getWorld()), split[1]);

				case "player-time" -> setPlayerTime(player, split[1]);
				case "player-weather" -> setPlayerWeather(player, split[1]);

				case "reset-player-time" -> resetPlayerTime(player);
				case "reset-player-weather" -> resetPlayerWeather(player);

				case "player-godmode" -> makeInvisibleFor(player, Utils.parseI(split[1]), plugin);

				case "broadcast" -> {
					StringBuilder builder = new StringBuilder();
					boolean debug = split[split.length - 1].toLowerCase().startsWith("delay:");
					int endingIndex = debug ? split.length - 1 : split.length; // if ends with the delay argument, ignore that last bit
					for (int i = 1; i < endingIndex; i++) {
						builder.append(split[i]).append(" ");
					}
					announce(builder.toString());
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

				case "entity-effect" -> addPotion(entity, split[1], Utils.parseI(split[2]), Utils.parseI(split[3]));

				case "entity-remove-effect" -> removePotion(entity, split[1]);
				case "entity-remove-buffs" -> removeBuffs(entity, plugin);
				case "entity-remove-debuffs" -> removeDebuffs(entity, plugin);

				case "entity-strike-fake-lightning" -> strikeFakeLightning(entity.getLocation());
				case "entity-strike-real-lightning" -> strikeLightning(entity.getLocation());

				case "entity-particle" -> spawnParticle(entity, entity.getLocation(), split[1]);
				case "entity-particle-boots" -> spawnParticleBoots(entity, entity.getLocation(), split[1]);

				case "entity-teleport" -> setLocation(entity, Utils.parseI(split[1]), Utils.parseI(split[2]), Utils.parseI(split[3]));
				case "entity-direction" -> setDirection(entity, split[1], split[2]);
				case "entity-forward" -> sendForward(entity, split[1]);

				case "entity-world" -> setWorld(entity, split[1]);

				case "entity-set-health" -> setHealth(entity, split[1]);
				case "entity-set-max-health" -> setMaximumHealth(entity, split[1]);

				case "entity-set-fire" -> setFire(entity, split[1]);

				case "entity-send-arrow" -> sendArrow(entity);
				case "entity-send-fireball" -> sendFireball(entity);
			}
		}, delay);
	}
}

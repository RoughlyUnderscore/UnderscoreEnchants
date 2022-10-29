package com.roughlyunderscore.enchs.parsers;

import com.codingforcookies.armorequip.ArmorEquipEvent;
import com.cryptomorin.xseries.XMaterial;
import com.roughlyunderscore.enchs.UnderscoreEnchants;
import com.roughlyunderscore.enchs.events.*;
import com.roughlyunderscore.enchs.util.Pair;
import com.roughlyunderscore.enchs.util.general.Utils;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.Locale;

import static com.roughlyunderscore.enchs.util.general.EntityUtils.*;
import static com.roughlyunderscore.enchs.util.general.PlayerUtils.*;
import static com.roughlyunderscore.enchs.util.general.Utils.*;

@UtilityClass
public class ConditionParsers {

	// This is one of the core parts of parsing an enchantment!

	// Required for a preparatory parser
	public boolean parseCondition(Event event0, String condition, UnderscoreEnchants plugin) {
		if (event0 instanceof PlayerPVPEvent event) return parseCondition(event, condition, plugin);
		else if (event0 instanceof ArmorEquipEvent event) return parseCondition(event, condition, plugin);
		else if (event0 instanceof BlockBreakEvent event) return parseCondition(event, condition, plugin);
		else if (event0 instanceof PlayerItemBreakEvent event) return parseCondition(event, condition, plugin);
		else if (event0 instanceof PlayerItemConsumeEvent event) return parseCondition(event, condition, plugin);
		else if (event0 instanceof PlayerInteractAtEntityEvent event) return parseCondition(event, condition, plugin);
		else if (event0 instanceof PlayerInteractEvent event) return parseCondition(event, condition, plugin);
		else if (event0 instanceof PlayerMoveEvent event) return parseCondition(event, condition, plugin);
		else if (event0 instanceof PlayerGotHurtEvent event) return parseCondition(event, condition, plugin);
		else if (event0 instanceof PlayerHurtsEntityEvent event) return parseCondition(event, condition, plugin);
		else if (event0 instanceof PlayerShootBowEvent event) return parseCondition(event, condition, plugin);
		else if (event0 instanceof PlayerToggleSneakEvent event) return parseCondition(event, condition, plugin);
		else if (event0 instanceof PlayerBowHitEvent event) return parseCondition(event, condition, plugin);
		return false;
	}

	public boolean parseCondition(PlayerPVPEvent ev, String condition0, UnderscoreEnchants plugin) {
		if (condition0 == null || condition0.isEmpty() || condition0.isBlank()) return true;
		String[] condition = condition0.split(" ");

		Player pl = ev.getDamager();
		Player vic = ev.getVictim();
		double damage = ev.getDamage();

		boolean negate = false;
		if (condition[0].startsWith("!")) {
			condition[0] = condition[0].substring(1);
			negate = true;
		}

		return defaultParse(condition, pl, vic, negate, plugin,

			Pair.of("damage-of", Math.floor(damage) == Utils.parseD(condition[1])),
			Pair.of("damage-lower", damage < Utils.parseD(condition[1])),
			Pair.of("damage-higher", damage > Utils.parseD(condition[1])),
			Pair.of("damage-lethal", damage >= getHealth(vic, plugin)),
			Pair.of("damage-non-lethal", damage < getHealth(vic, plugin))

		);
	}

	public boolean parseCondition(PlayerBowHitEvent ev, String condition0, UnderscoreEnchants plugin) {
		if (condition0 == null || condition0.isEmpty() || condition0.isBlank()) return true;
		String[] condition = condition0.split(" ");

		Player pl = ev.getDamager();
		Player vic = ev.getVictim();
		double damage = ev.getDamage();

		boolean negate = false;
		if (condition[0].startsWith("!")) {
			condition[0] = condition[0].substring(1);
			negate = true;
		}

		return defaultParse(condition, pl, vic, negate, plugin,

			Pair.of("damage-of", Math.floor(damage) == Utils.parseD(condition[1])),
			Pair.of("damage-lower", damage < Utils.parseD(condition[1])),
			Pair.of("damage-higher", damage > Utils.parseD(condition[1])),
			Pair.of("damage-lethal", damage >= getHealth(vic, plugin)),
			Pair.of("damage-non-lethal", damage < getHealth(vic, plugin))

		);

	}

	public boolean parseCondition(ArmorEquipEvent ev, String condition0, UnderscoreEnchants plugin) {
		if (condition0 == null || condition0.isEmpty() || condition0.isBlank()) return true;
		String[] condition = condition0.split(" ");

		Player pl = ev.getPlayer();
		Material oldType = ev.getOldArmorPiece() == null ? Material.AIR : ev.getOldArmorPiece().getType(),
				 newType = ev.getNewArmorPiece() == null ? Material.AIR : ev.getNewArmorPiece().getType();
		ItemStack oldItem = ev.getOldArmorPiece(),
				  newItem = ev.getNewArmorPiece();
		String oldName = oldItem == null ? "" : (oldItem.getItemMeta() == null ? "" : oldItem.getItemMeta().getDisplayName().toLowerCase(Locale.ROOT)),
			   newName = newItem == null ? "" : (newItem.getItemMeta() == null ? "" : newItem.getItemMeta().getDisplayName().toLowerCase(Locale.ROOT));

		boolean negate = false;
		if (condition[0].startsWith("!")) {
			condition[0] = condition[0].substring(1);
			negate = true;
		}

		return defaultParse(condition, pl, negate, plugin,
			Pair.of("equipped-helmet", newItem != null && newType != Material.AIR && (newName.contains("helmet"))),
			Pair.of("equipped-chestplate", newItem != null && newType != Material.AIR && newName.contains("chestplate")),
			Pair.of("equipped-leggings", newItem != null && newType != Material.AIR && newName.contains("leggings")),
			Pair.of("equipped-boots", newItem != null && newType != Material.AIR && newName.contains("boots")),
			Pair.of("equipped", newItem != null && newType != Material.AIR),

			Pair.of("unequipped-helmet", oldItem != null && oldType != Material.AIR && (oldName.contains("helmet"))),
			Pair.of("unequipped-chestplate", oldItem != null && oldType != Material.AIR && oldName.contains("chestplate")),
			Pair.of("unequipped-leggings", oldItem != null && oldType != Material.AIR && oldName.contains("leggings")),
			Pair.of("unequipped-boots", oldItem != null && oldType != Material.AIR && oldName.contains("boots")),
			Pair.of("unequipped", oldItem != null && oldType != Material.AIR)
		);
	}

	public boolean parseCondition(BlockBreakEvent ev, String condition0, UnderscoreEnchants plugin) {
		if (condition0 == null || condition0.isEmpty() || condition0.isBlank()) return true;
		String[] condition = condition0.split(" ");

		Player pl = ev.getPlayer();
		Block block = ev.getBlock();

		boolean negate = false;
		if (condition[0].startsWith("!")) {
			condition[0] = condition[0].substring(1);
			negate = true;
		}

		return defaultParse(condition, pl, negate, plugin,
			Pair.of("block-is", block.getType().name().equalsIgnoreCase(condition[1]))
		);
	}

	public boolean parseCondition(PlayerItemBreakEvent ev, String condition0, UnderscoreEnchants plugin) {
		if (condition0 == null || condition0.isEmpty() || condition0.isBlank()) return true;
		String[] condition = condition0.split(" ");

		Player pl = ev.getPlayer();
		ItemStack item = ev.getBrokenItem();

		boolean negate = false;
		if (condition[0].startsWith("!")) {
			condition[0] = condition[0].substring(1);
			negate = true;
		}

		return defaultParse(condition, pl, negate, plugin,
			Pair.of("item-is", item.getType().name().equalsIgnoreCase(condition[1]))
		);
	}

	public boolean parseCondition(PlayerItemConsumeEvent ev, String condition0, UnderscoreEnchants plugin) {
		if (condition0 == null || condition0.isEmpty() || condition0.isBlank()) return true;
		String[] condition = condition0.split(" ");

		Player pl = ev.getPlayer();
		ItemStack item = ev.getItem();

		boolean negate = false;
		if (condition[0].startsWith("!")) {
			condition[0] = condition[0].substring(1);
			negate = true;
		}

		return defaultParse(condition, pl, negate, plugin,
			Pair.of("food-is", item.getType().name().equalsIgnoreCase(condition[1])),
			Pair.of("is-vegetarian", isVegetarian(item.getType())),
			Pair.of("is-pescetarian", isPescetarian(item.getType())),
			Pair.of("is-potion", item.getType() == Material.POTION),
			Pair.of("is-honey", item.getType() == Material.HONEY_BOTTLE),
			Pair.of("is-milk", item.getType() == Material.MILK_BUCKET),
			Pair.of("is-food",
				!(parseCondition(ev, "is-potion", plugin) ||
					parseCondition(ev, "is-milk", plugin) ||
					parseCondition(ev, "is-honey", plugin))
			)
		);
	}

	public boolean parseCondition(PlayerInteractAtEntityEvent ev, String condition0, UnderscoreEnchants plugin) {
		if (condition0 == null || condition0.isEmpty() || condition0.isBlank()) return true;
		String[] condition = condition0.split(" ");

		Player pl = ev.getPlayer();
		Entity entity = ev.getRightClicked();

		boolean negate = false;
		if (condition[0].startsWith("!")) {
			condition[0] = condition[0].substring(1);
			negate = true;
		}

		return defaultParse(condition, pl, entity, negate, plugin, Pair.empty());
	}

	public boolean parseCondition(PlayerInteractEvent ev, String condition0, UnderscoreEnchants plugin) {
		if (condition0 == null || condition0.isEmpty() || condition0.isBlank()) return true;
		String[] condition = condition0.split(" ");

		Player pl = ev.getPlayer();
		Player player = ev.getPlayer();
		Location location = player.getLocation();
		Block block = ev.getClickedBlock() == null ? location.getWorld().getBlockAt(0, 0, 0) : ev.getClickedBlock(); // null check

		boolean negate = false;
		if (condition[0].startsWith("!")) {
			condition[0] = condition[0].substring(1);
			negate = true;
		}

		return defaultParse(condition, pl, negate, plugin,
			Pair.of("block-is", block.getType().name().equalsIgnoreCase(condition[1])),
			Pair.of("clicked-lmb-air", ev.getAction() == Action.LEFT_CLICK_AIR),
			Pair.of("clicked-lmb-block", ev.getAction() == Action.LEFT_CLICK_BLOCK),
			Pair.of("clicked-rmb-air", ev.getAction() == Action.RIGHT_CLICK_AIR),
			Pair.of("clicked-rmb-block", ev.getAction() == Action.RIGHT_CLICK_BLOCK),
			Pair.of("physical-action", ev.getAction() == Action.PHYSICAL)
		);
	}

	public boolean parseCondition(PlayerMoveEvent ev, String condition0, UnderscoreEnchants plugin) {
		if (condition0 == null || condition0.isEmpty() || condition0.isBlank()) return true;
		String[] condition = condition0.split(" ");

		Player pl = ev.getPlayer();
		Location from = ev.getFrom().clone().subtract(0, 1, 0);
		Location to = (ev.getTo() == null ? ev.getFrom() : ev.getTo()).clone().subtract(0, 1, 0); // null check

		boolean negate = false;
		if (condition[0].startsWith("!")) {
			condition[0] = condition[0].substring(1);
			negate = true;
		}

		return defaultParse(condition, pl, negate, plugin,
			Pair.of("from-is", from.getBlock().getType() == XMaterial.valueOf(condition[1]).parseMaterial()),
			Pair.of("to-is", to.getBlock().getType() == XMaterial.valueOf(condition[1]).parseMaterial()),
			Pair.of("jump", isByJump(ev)),
			Pair.of("same-block", isBySameBlock(ev)),
			Pair.of("not-same-block", isByDifferentBlocks(ev)),
			Pair.of("head-rotate", isByHeadRotate(ev))
		);

	}

	public boolean parseCondition(PlayerGotHurtEvent ev, String condition0, UnderscoreEnchants plugin) {
		if (condition0 == null || condition0.isEmpty() || condition0.isBlank()) return true;
		String[] condition = condition0.split(" ");

		Player pl = ev.getVictim();
		double damage = ev.getDamage();

		boolean negate = false;
		if (condition[0].startsWith("!")) {
			condition[0] = condition[0].substring(1);
			negate = true;
		}

		if (condition.length == 1) {
			String temp = condition[0];
			condition = new String[2];
			condition[0] = temp;
		}

		return defaultParse(condition, pl, negate, plugin,
			Pair.of("damage-of", Math.floor(damage) == Utils.parseD(condition[1])),
			Pair.of("damage-lower", damage < Utils.parseD(condition[1])),
			Pair.of("damage-higher", damage > Utils.parseD(condition[1])),
			Pair.of("damage-lethal", damage >= getHealth(pl, plugin)),
			Pair.of("damage-non-lethal", damage < getHealth(pl, plugin)),
			Pair.of("caused-by-block-explosion", ev.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION),
			Pair.of("caused-by-hazardous-block", ev.getCause() == EntityDamageEvent.DamageCause.CONTACT),
			Pair.of("caused-by-entity-cramming", ev.getCause() == EntityDamageEvent.DamageCause.CRAMMING),
			Pair.of("caused-by-unknown-source", ev.getCause() == EntityDamageEvent.DamageCause.CUSTOM),
			Pair.of("caused-by-dragon-breath", ev.getCause() == EntityDamageEvent.DamageCause.DRAGON_BREATH),
			Pair.of("caused-by-drowning", ev.getCause() == EntityDamageEvent.DamageCause.DROWNING),
			Pair.of("caused-by-entity-attack", ev.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK),
			Pair.of("caused-by-entity-explosion", ev.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION),
			Pair.of("caused-by-entity-sweep-attack", ev.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK),
			Pair.of("caused-by-falling", ev.getCause() == EntityDamageEvent.DamageCause.FALL),
			Pair.of("caused-by-falling-block", ev.getCause() == EntityDamageEvent.DamageCause.FALLING_BLOCK),
			Pair.of("caused-by-direct-fire-exposure", ev.getCause() == EntityDamageEvent.DamageCause.FIRE),
			Pair.of("caused-by-fire-tick", ev.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK),
			Pair.of("caused-by-flying-into-wall", ev.getCause() == EntityDamageEvent.DamageCause.FLY_INTO_WALL),
			Pair.of("caused-by-freezing", ev.getCause() == EntityDamageEvent.DamageCause.FREEZE),
			Pair.of("caused-by-magma-damage", ev.getCause() == EntityDamageEvent.DamageCause.HOT_FLOOR),
			Pair.of("caused-by-lava", ev.getCause() == EntityDamageEvent.DamageCause.LAVA),
			Pair.of("caused-by-lightning", ev.getCause() == EntityDamageEvent.DamageCause.LIGHTNING),
			Pair.of("caused-by-magic", ev.getCause() == EntityDamageEvent.DamageCause.MAGIC),
			Pair.of("caused-by-poison", ev.getCause() == EntityDamageEvent.DamageCause.POISON),
			Pair.of("caused-by-projectile", ev.getCause() == EntityDamageEvent.DamageCause.PROJECTILE),
			Pair.of("caused-by-starvation", ev.getCause() == EntityDamageEvent.DamageCause.STARVATION),
			Pair.of("caused-by-suffocation", ev.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION),
			Pair.of("caused-by-plugin-enforcement", ev.getCause() == EntityDamageEvent.DamageCause.SUICIDE),
			Pair.of("caused-by-thorns", ev.getCause() == EntityDamageEvent.DamageCause.THORNS),
			Pair.of("caused-by-void", ev.getCause() == EntityDamageEvent.DamageCause.VOID),
			Pair.of("caused-by-withering", ev.getCause() == EntityDamageEvent.DamageCause.WITHER)
		);


	}

	public boolean parseCondition(PlayerHurtsEntityEvent ev, String condition0, UnderscoreEnchants plugin) {
		if (condition0 == null || condition0.isEmpty() || condition0.isBlank()) return true;
		String[] condition = condition0.split(" ");

		Player pl = ev.getDamager();
		Entity entity = ev.getEntity();
		double damage = ev.getDamage();

		boolean negate = false;
		if (condition[0].startsWith("!")) {
			condition[0] = condition[0].substring(1);
			negate = true;
		}

		if (!(entity instanceof LivingEntity liv)) return false;

		return defaultParse(condition, pl, entity, negate, plugin,
			Pair.of("damage-of", Math.floor(damage) == Utils.parseD(condition[1])),
			Pair.of("damage-lower", damage < Utils.parseD(condition[1])),
			Pair.of("damage-higher", damage > Utils.parseD(condition[1])),
			Pair.of("damage-lethal", damage >= liv.getHealth()),
			Pair.of("damage-non-lethal", damage < liv.getHealth())
		);

	}

	public boolean parseCondition(PlayerShootBowEvent ev, String condition0, UnderscoreEnchants plugin) {
		if (condition0 == null || condition0.isEmpty() || condition0.isBlank()) return true;
		String[] condition = condition0.split(" ");

		Player pl = ev.getShooter();
		float force = ev.getForce();

		boolean negate = false;
		if (condition[0].startsWith("!")) {
			condition[0] = condition[0].substring(1);
			negate = true;
		}

		return defaultParse(condition, pl, negate, plugin,
			Pair.of("force-of", Float.parseFloat(new DecimalFormat("#.#").format(force)) == Utils.parseD(condition[1])),
			Pair.of("force-lower", force < Utils.parseD(condition[1])),
			Pair.of("force-higher", force > Utils.parseD(condition[1]))
		);

	}

	public boolean parseCondition(PlayerToggleSneakEvent ev, String condition0, UnderscoreEnchants plugin) {
		if (condition0 == null || condition0.isEmpty() || condition0.isBlank()) return true;
		String[] condition = condition0.split(" ");

		Player pl = ev.getPlayer();

		boolean negate = false;
		if (condition[0].startsWith("!")) {
			condition[0] = condition[0].substring(1);
			negate = true;
		}

		return defaultParse(condition, pl, negate, plugin,
			Pair.of("sneaked", ev.isSneaking()),
			Pair.of("unsneaked", !ev.isSneaking())
		);
	}



	@SafeVarargs
	private boolean defaultParse(String[] condition, Player pl, boolean negate, UnderscoreEnchants plugin, Pair<String, Boolean>... extra) {
		Boolean result = null;

		if ("pdc-match".equalsIgnoreCase(condition[0]))             result = getPDCValue(pl, getKey(condition[1], plugin), plugin).equals(condition[2]);
		else if ("sneaking".equalsIgnoreCase(condition[0]))         result = sneaking(pl, plugin);
		else if ("sprinting".equalsIgnoreCase(condition[0]))        result = sprinting(pl, plugin);
		else if ("swimming".equalsIgnoreCase(condition[0]))         result = swimming(pl, plugin);
		else if ("blocking".equalsIgnoreCase(condition[0]))         result = blocking(pl, plugin);
		else if ("flying".equalsIgnoreCase(condition[0]))           result = flying(pl, plugin);
		else if ("onfire".equalsIgnoreCase(condition[0]))           result = onFire(pl, plugin);
		else if ("onhighestblock".equalsIgnoreCase(condition[0]))   result = onTop(pl, plugin);
		else if ("rain".equalsIgnoreCase(condition[0]))             result = rains(pl, plugin);
		else if ("clear".equalsIgnoreCase(condition[0]))            result = sunshines(pl, plugin);
		else if ("thunder".equalsIgnoreCase(condition[0]))          result = thunders(pl, plugin);
		else if ("day".equalsIgnoreCase(condition[0]))              result = day(pl, plugin);
		else if ("night".equalsIgnoreCase(condition[0]))            result = night(pl, plugin);
		else if ("overworld".equalsIgnoreCase(condition[0]))        result = overworld(pl, plugin);
		else if ("nether".equalsIgnoreCase(condition[0]))           result = nether(pl, plugin);
		else if ("end".equalsIgnoreCase(condition[0]))              result = end(pl, plugin);
		else if ("op".equalsIgnoreCase(condition[0]))               result = op(pl, plugin);
		else if ("health-of".equalsIgnoreCase(condition[0]))        result = (int) getHealth(pl, plugin) == parseD(condition[1]);
		else if ("health-lower".equalsIgnoreCase(condition[0]))     result = getHealth(pl, plugin) < parseD(condition[1]);
		else if ("health-higher".equalsIgnoreCase(condition[0]))    result = getHealth(pl, plugin) > parseD(condition[1]);
		else if ("healthy".equalsIgnoreCase(condition[0]))          result = getHealth(pl, plugin) == getMaximumHealth(pl, plugin);
		else if ("food-of".equalsIgnoreCase(condition[0]))          result = getFood(pl, plugin) == parseI(condition[1]);
		else if ("food-lower".equalsIgnoreCase(condition[0]))       result = getFood(pl, plugin) < parseI(condition[1]);
		else if ("food-higher".equalsIgnoreCase(condition[0]))      result = getFood(pl, plugin) > parseI(condition[1]);
		else if ("satiated".equalsIgnoreCase(condition[0]))         result = getFood(pl, plugin) == 20;
		else if ("air-of".equalsIgnoreCase(condition[0]))           result = getAir(pl, plugin) == parseI(condition[1]);
		else if ("air-lower".equalsIgnoreCase(condition[0]))        result = getAir(pl, plugin) < parseI(condition[1]);
		else if ("air-higher".equalsIgnoreCase(condition[0]))       result = getAir(pl, plugin) > parseI(condition[1]);
		else if ("oxygenated".equalsIgnoreCase(condition[0]))       result = getAir(pl, plugin) == getMaximumAir(pl, plugin);
		else if ("godmode-of".equalsIgnoreCase(condition[0]))       result = invisibleFor(pl, plugin) == parseI(condition[1]);
		else if ("godmode-lower".equalsIgnoreCase(condition[0]))    result = invisibleFor(pl, plugin) < parseI(condition[1]);
		else if ("godmode-higher".equalsIgnoreCase(condition[0]))   result = invisibleFor(pl, plugin) > parseI(condition[1]);

		else {
			for (Pair<String, Boolean> pair : extra) {
				if (pair.getKey().equalsIgnoreCase(condition[0]))   result = pair.getValue();
			}
		}

		if (result == null) return false;

		if (negate) return !result;
		else return result;
	}

	@SafeVarargs
	private boolean defaultParse(String[] condition, Player pl, Entity entity, boolean negate, UnderscoreEnchants plugin, Pair<String, Boolean>... extra) {
		Boolean result = null;

		if (entity instanceof LivingEntity lEntity) {

			if ("pdc-match".equalsIgnoreCase(condition[0])) result = getPDCValue(pl, getKey(condition[1], plugin), plugin).equals(condition[2]);
			else if ("sneaking".equalsIgnoreCase(condition[0])) result = sneaking(pl, plugin);
			else if ("sprinting".equalsIgnoreCase(condition[0])) result = sprinting(pl, plugin);
			else if ("swimming".equalsIgnoreCase(condition[0])) result = swimming(pl, plugin);
			else if ("blocking".equalsIgnoreCase(condition[0])) result = blocking(pl, plugin);
			else if ("flying".equalsIgnoreCase(condition[0])) result = flying(pl, plugin);
			else if ("onfire".equalsIgnoreCase(condition[0])) result = onFire(pl, plugin);
			else if ("onhighestblock".equalsIgnoreCase(condition[0])) result = onTop(pl, plugin);
			else if ("rain".equalsIgnoreCase(condition[0])) result = rains(pl, plugin);
			else if ("clear".equalsIgnoreCase(condition[0])) result = sunshines(pl, plugin);
			else if ("thunder".equalsIgnoreCase(condition[0])) result = thunders(pl, plugin);
			else if ("day".equalsIgnoreCase(condition[0])) result = day(pl, plugin);
			else if ("night".equalsIgnoreCase(condition[0])) result = night(pl, plugin);
			else if ("overworld".equalsIgnoreCase(condition[0])) result = overworld(pl, plugin);
			else if ("nether".equalsIgnoreCase(condition[0])) result = nether(pl, plugin);
			else if ("end".equalsIgnoreCase(condition[0])) result = end(pl, plugin);
			else if ("op".equalsIgnoreCase(condition[0])) result = op(pl, plugin);
			else if ("health-of".equalsIgnoreCase(condition[0])) result = (int) getHealth(pl, plugin) == parseD(condition[1]);
			else if ("health-lower".equalsIgnoreCase(condition[0])) result = getHealth(pl, plugin) < parseD(condition[1]);
			else if ("health-higher".equalsIgnoreCase(condition[0])) result = getHealth(pl, plugin) > parseD(condition[1]);
			else if ("healthy".equalsIgnoreCase(condition[0])) result = getHealth(pl, plugin) == getMaximumHealth(pl, plugin);
			else if ("food-of".equalsIgnoreCase(condition[0])) result = getFood(pl, plugin) == parseI(condition[1]);
			else if ("food-lower".equalsIgnoreCase(condition[0])) result = getFood(pl, plugin) < parseI(condition[1]);
			else if ("food-higher".equalsIgnoreCase(condition[0])) result = getFood(pl, plugin) > parseI(condition[1]);
			else if ("satiated".equalsIgnoreCase(condition[0])) result = getFood(pl, plugin) == 20;
			else if ("air-of".equalsIgnoreCase(condition[0])) result = getAir(pl, plugin) == parseI(condition[1]);
			else if ("air-lower".equalsIgnoreCase(condition[0])) result = getAir(pl, plugin) < parseI(condition[1]);
			else if ("air-higher".equalsIgnoreCase(condition[0])) result = getAir(pl, plugin) > parseI(condition[1]);
			else if ("oxygenated".equalsIgnoreCase(condition[0])) result = getAir(pl, plugin) == getMaximumAir(pl, plugin);
			else if ("godmode-of".equalsIgnoreCase(condition[0])) result = invisibleFor(pl, plugin) == parseI(condition[1]);
			else if ("godmode-lower".equalsIgnoreCase(condition[0])) result = invisibleFor(pl, plugin) < parseI(condition[1]);
			else if ("godmode-higher".equalsIgnoreCase(condition[0])) result = invisibleFor(pl, plugin) > parseI(condition[1]);

			else if ("entity-swimming".equalsIgnoreCase(condition[0])) result = swimming(entity, plugin);
			else if ("entity-onfire".equalsIgnoreCase(condition[0])) result = onFire(entity, plugin);
			else if ("entity-onhighestblock".equalsIgnoreCase(condition[0])) result = onTop(entity, plugin);
			else if ("entity-health-of".equalsIgnoreCase(condition[0])) result = (int) lEntity.getHealth() == Utils.parseD(condition[1]);
			else if ("entity-health-lower".equalsIgnoreCase(condition[0])) result = lEntity.getHealth() < Utils.parseD(condition[1]);
			else if ("entity-health-higher".equalsIgnoreCase(condition[0])) result = lEntity.getHealth() > Utils.parseD(condition[1]);
			else if ("entity-healthy".equalsIgnoreCase(condition[0])) result = lEntity.getHealth() == getMaximumHealth(entity, plugin);

			else {
				for (Pair<String, Boolean> pair : extra) {
					if (pair.getKey().equalsIgnoreCase(condition[0])) result = pair.getValue();
				}
			}
		}

		if (result == null) return false;

		if (negate) return !result;
		else return result;
	}

	@SafeVarargs
	private boolean defaultParse(String[] condition, Player pl, Player vic, boolean negate, UnderscoreEnchants plugin, Pair<String, Boolean>... extra) {
		Boolean result = null;

		if ("pdc-match".equalsIgnoreCase(condition[0]))             result = getPDCValue(pl, getKey(condition[1], plugin), plugin).equals(condition[2]);
		if ("victim-pdc-match".equalsIgnoreCase(condition[0]))      result = getPDCValue(vic, getKey(condition[1], plugin), plugin).equals(condition[2]);

		if ("sneaking".equalsIgnoreCase(condition[0]))              result = sneaking(pl, plugin);
		if ("sprinting".equalsIgnoreCase(condition[0]))             result = sprinting(pl, plugin);
		if ("swimming".equalsIgnoreCase(condition[0]))              result = swimming(pl, plugin);
		if ("blocking".equalsIgnoreCase(condition[0]))              result = blocking(pl, plugin);
		if ("flying".equalsIgnoreCase(condition[0]))                result = flying(pl, plugin);

		if ("onfire".equalsIgnoreCase(condition[0]))                result = onFire(pl, plugin);
		if ("onhighestblock".equalsIgnoreCase(condition[0]))        result = onTop(pl, plugin);

		if ("rain".equalsIgnoreCase(condition[0]))                  result = rains(pl, plugin);
		if ("clear".equalsIgnoreCase(condition[0]))                 result = sunshines(pl, plugin);
		if ("thunder".equalsIgnoreCase(condition[0]))               result = thunders(pl, plugin);

		if ("day".equalsIgnoreCase(condition[0]))                   result = day(pl, plugin);
		if ("night".equalsIgnoreCase(condition[0]))                 result = night(pl, plugin);

		if ("overworld".equalsIgnoreCase(condition[0]))             result = overworld(pl, plugin);
		if ("nether".equalsIgnoreCase(condition[0]))                result = nether(pl, plugin);
		if ("end".equalsIgnoreCase(condition[0]))                   result = end(pl, plugin);

		if ("op".equalsIgnoreCase(condition[0]))                    result = op(pl, plugin);

		if ("health-of".equalsIgnoreCase(condition[0]))             result = (int) getHealth(pl, plugin) == Utils.parseD(condition[1]);
		if ("health-lower".equalsIgnoreCase(condition[0]))          result = getHealth(pl, plugin) < Utils.parseD(condition[1]);
		if ("health-higher".equalsIgnoreCase(condition[0]))         result = getHealth(pl, plugin) > Utils.parseD(condition[1]);
		if ("healthy".equalsIgnoreCase(condition[0]))               result = getHealth(pl, plugin) == getMaximumHealth(pl, plugin);

		if ("food-of".equalsIgnoreCase(condition[0]))               result = getFood(pl, plugin) == Utils.parseI(condition[1]);
		if ("food-lower".equalsIgnoreCase(condition[0]))            result = getFood(pl, plugin) < Utils.parseI(condition[1]);
		if ("food-higher".equalsIgnoreCase(condition[0]))           result = getFood(pl, plugin) > Utils.parseI(condition[1]);
		if ("satiated".equalsIgnoreCase(condition[0]))              result = getFood(pl, plugin) == 20;

		if ("air-of".equalsIgnoreCase(condition[0]))                result = getAir(pl, plugin) == Utils.parseI(condition[1]);
		if ("air-lower".equalsIgnoreCase(condition[0]))             result = getAir(pl, plugin) < Utils.parseI(condition[1]);
		if ("air-higher".equalsIgnoreCase(condition[0]))            result = getAir(pl, plugin) > Utils.parseI(condition[1]);
		if ("oxygenated".equalsIgnoreCase(condition[0]))            result = getAir(pl, plugin) == getMaximumAir(pl, plugin);

		if ("godmode-of".equalsIgnoreCase(condition[0]))            result = invisibleFor(pl, plugin) == Utils.parseI(condition[1]);
		if ("godmode-lower".equalsIgnoreCase(condition[0]))         result = invisibleFor(pl, plugin) < Utils.parseI(condition[1]);
		if ("godmode-higher".equalsIgnoreCase(condition[0]))        result = invisibleFor(pl, plugin) > Utils.parseI(condition[1]);

		if ("victim-sneaking".equalsIgnoreCase(condition[0]))       result = sneaking(vic, plugin);
		if ("victim-sprinting".equalsIgnoreCase(condition[0]))      result = sprinting(vic, plugin);
		if ("victim-swimming".equalsIgnoreCase(condition[0]))       result = swimming(vic, plugin);
		if ("victim-blocking".equalsIgnoreCase(condition[0]))       result = blocking(vic, plugin);
		if ("victim-flying".equalsIgnoreCase(condition[0]))         result = flying(vic, plugin);

		if ("victim-onfire".equalsIgnoreCase(condition[0]))         result = onFire(vic, plugin);
		if ("victim-onhighestblock".equalsIgnoreCase(condition[0])) result = onTop(vic, plugin);

		if ("victim-rain".equalsIgnoreCase(condition[0]))           result = rains(vic, plugin);
		if ("victim-clear".equalsIgnoreCase(condition[0]))          result = sunshines(vic, plugin);
		if ("victim-thunder".equalsIgnoreCase(condition[0]))        result = thunders(vic, plugin);

		if ("victim-day".equalsIgnoreCase(condition[0]))            result = day(vic, plugin);
		if ("victim-night".equalsIgnoreCase(condition[0]))          result = night(vic, plugin);

		if ("victim-overworld".equalsIgnoreCase(condition[0]))      result = overworld(vic, plugin);
		if ("victim-nether".equalsIgnoreCase(condition[0]))         result = nether(vic, plugin);
		if ("victim-end".equalsIgnoreCase(condition[0]))            result = end(vic, plugin);

		if ("victim-op".equalsIgnoreCase(condition[0]))             result = op(vic, plugin);

		if ("victim-health-of".equalsIgnoreCase(condition[0]))      result = (int) getHealth(vic, plugin) == Utils.parseD(condition[1]);
		if ("victim-health-lower".equalsIgnoreCase(condition[0]))   result = getHealth(vic, plugin) < Utils.parseD(condition[1]);
		if ("victim-health-higher".equalsIgnoreCase(condition[0]))  result = getHealth(vic, plugin) > Utils.parseD(condition[1]);
		if ("victim-healthy".equalsIgnoreCase(condition[0]))        result = getHealth(vic, plugin) == getMaximumHealth(vic, plugin);

		if ("victim-food-of".equalsIgnoreCase(condition[0]))        result = getFood(vic, plugin) == Utils.parseI(condition[1]);
		if ("victim-food-lower".equalsIgnoreCase(condition[0]))     result = getFood(vic, plugin) < Utils.parseI(condition[1]);
		if ("victim-food-higher".equalsIgnoreCase(condition[0]))    result = getFood(vic, plugin) > Utils.parseI(condition[1]);
		if ("victim-satiated".equalsIgnoreCase(condition[0]))       result = getFood(vic, plugin) == 20;

		if ("victim-air-of".equalsIgnoreCase(condition[0]))         result = getAir(vic, plugin) == Utils.parseI(condition[1]);
		if ("victim-air-lower".equalsIgnoreCase(condition[0]))      result = getAir(vic, plugin) < Utils.parseI(condition[1]);
		if ("victim-air-higher".equalsIgnoreCase(condition[0]))     result = getAir(vic, plugin) > Utils.parseI(condition[1]);
		if ("victim-oxygenated".equalsIgnoreCase(condition[0]))     result = getAir(vic, plugin) == getMaximumAir(vic, plugin);

		if ("victim-godmode-of".equalsIgnoreCase(condition[0]))     result = invisibleFor(vic, plugin) == Utils.parseI(condition[1]);
		if ("victim-godmode-lower".equalsIgnoreCase(condition[0]))  result = invisibleFor(vic, plugin) < Utils.parseI(condition[1]);
		if ("victim-godmode-higher".equalsIgnoreCase(condition[0])) result = invisibleFor(vic, plugin) > Utils.parseI(condition[1]);

		else {
			for (Pair<String, Boolean> pair : extra) {
				if (pair.getKey().equalsIgnoreCase(condition[0]))   result = pair.getValue();
			}
		}

		if (result == null) return false;

		if (negate) return !result;
		else return result;
	}
}

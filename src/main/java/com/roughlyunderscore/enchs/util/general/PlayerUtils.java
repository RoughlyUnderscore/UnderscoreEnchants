package com.roughlyunderscore.enchs.util.general;

import com.cryptomorin.xseries.SkullUtils;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XPotion;
import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.particles.XParticle;
import com.roughlyunderscore.enchs.UnderscoreEnchants;
import com.roughlyunderscore.enchs.util.ArmorPiece;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;

@UtilityClass @SuppressWarnings("unused")
/*
Just a lot of utils that I don't want to explain
 */
public class PlayerUtils<T, Z> {
	//<editor-fold desc="Getters">
	//<editor-fold desc="PDC">
	public PersistentDataContainer getPDC(Player player) {
		return player.getPersistentDataContainer();
	}

	public Object getPDCValue(Player player, NamespacedKey address) {
		UnderscoreEnchants.getStaticLogger().log(String.format("getPDCValue was called on player %s with address %s and yielded %s.",
			player.getName(),
			address.toString(),
			getPDC(player).get(address, PersistentDataType.STRING)));
		return getPDC(player).get(address, PersistentDataType.STRING);
	}
	//</editor-fold>
	//<editor-fold desc="Location">
	public Location getLocation(Player player) {
		UnderscoreEnchants.getStaticLogger().log("getLocation was called on player " + player.getName() + " and resulted in the following location: " + player.getLocation());
		return player.getLocation();
	}

	public double getX(Player player) {
		UnderscoreEnchants.getStaticLogger().log("getX was called on player " + player.getName() + " and resulted in the following coordinate: " + getLocation(player).getX());
		return getLocation(player).getX();
	}
	public double getY(Player player) {
		UnderscoreEnchants.getStaticLogger().log("getY was called on player " + player.getName() + " and resulted in the following coordinate: " + getLocation(player).getY());
		return getLocation(player).getY();
	}
	public double getZ(Player player) {
		UnderscoreEnchants.getStaticLogger().log("getZ was called on player " + player.getName() + " and resulted in the following coordinate: " + getLocation(player).getZ());
		return getLocation(player).getZ();
	}
	public float getYaw(Player player) {
		UnderscoreEnchants.getStaticLogger().log("getYaw was called on player " + player.getName() + " and resulted in the following angle: " + getLocation(player).getYaw());
		return getLocation(player).getYaw();
	}
	public float getPitch(Player player) {
		UnderscoreEnchants.getStaticLogger().log("getPitch was called on player " + player.getName() + " and resulted in the following angle: " + getLocation(player).getPitch());
		return getLocation(player).getPitch();
	}

	public String getXString(Player player) {
		return String.valueOf(getX(player));
	}
	public String getYString(Player player) {
		return String.valueOf(getY(player));
	}
	public String getZString(Player player) {
		return String.valueOf(getZ(player));
	}
	public String getYawString(Player player) {
		return String.valueOf(getYaw(player));
	}
	public String getPitchString(Player player) {
		return String.valueOf(getPitch(player));
	}

	public World getWorld(Player player) {
		UnderscoreEnchants.getStaticLogger().log("getWorld was called on player " + player.getName() + " and resulted in the following world: " + getLocation(player).getWorld());
		return getLocation(player).getWorld();
	}
	public String getWorldName(Player player) {
		return getWorld(player).getName();
	}
	//</editor-fold>

	//<editor-fold desc="Money">
	public double getMoney(Player player) {
		if (UnderscoreEnchants.econ == null) {
			UnderscoreEnchants.getStaticLogger().log("getMoney was called with economy features disabled");
			return 0;
		}
		UnderscoreEnchants.getStaticLogger().log("getMoney was called on player " + player.getName() + " and resulted in the following balance: " + UnderscoreEnchants.econ.getBalance(player));
		return UnderscoreEnchants.econ.getBalance(player);
	}
	public String getMoneyString(Player player) {
		return String.valueOf(getMoney(player));
	}
	//</editor-fold>

	//<editor-fold desc="Experience">
	public int getXp(Player player) {
		UnderscoreEnchants.getStaticLogger().log("getXp was called on player " + player.getName() + " and resulted in the following value: " + player.getTotalExperience());
		return player.getTotalExperience();
	}
	public String getXpString(Player player) {
		return String.valueOf(getXp(player));
	}

	public int getLevel(Player player) {
		UnderscoreEnchants.getStaticLogger().log("getLevel was called on player " + player.getName() + " and resulted in the following value: " + player.getLevel());
		return player.getLevel();
	}
	public String getLevelString(Player player) {
		return String.valueOf(getLevel(player));
	}
	//</editor-fold>

	//<editor-fold desc="Health">
	public double getMaximumHealth(Player player) {
		UnderscoreEnchants.getStaticLogger().log("getMaximumHealth was called on player " + player.getName() + " and resulted in the following value: " + Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getBaseValue());
		return Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getBaseValue();
	}
	public String getMaximumHealthString(Player player) {
		return String.valueOf(getMaximumHealth(player));
	}

	public double getHealth(Player player) {
		UnderscoreEnchants.getStaticLogger().log("getHealth was called on player " + player.getName() + " and resulted in the following value: " + player.getHealth());
		return player.getHealth();
	}
	public String getHealthString(Player player) {
		return String.valueOf(getHealth(player));
	}
	//</editor-fold>

	//<editor-fold desc="Food">
	public int getFood(Player player) {
		UnderscoreEnchants.getStaticLogger().log("getFood was called on player " + player.getName() + " and resulted in the following value: " + player.getFoodLevel());
		return player.getFoodLevel();
	}
	public String getFoodString(Player player) {
		return String.valueOf(getFood(player));
	}
	//</editor-fold>

	//<editor-fold desc="Air">
	public int getAir(Player player) {
		UnderscoreEnchants.getStaticLogger().log("getAir was called on player " + player.getName() + " and resulted in the following value: " + player.getRemainingAir());
		return player.getRemainingAir();
	}
	public String getAirString(Player player) {
		return String.valueOf(getAir(player));
	}

	public int getMaximumAir(Player player) {
		UnderscoreEnchants.getStaticLogger().log("getMaximumAir was called on player " + player.getName() + " and resulted in the following value: " + player.getMaximumAir());
		return player.getMaximumAir();
	}
	public String getMaximumAirString(Player player) {
		return String.valueOf(getMaximumAir(player));
	}
	//</editor-fold>

	//<editor-fold desc="Effects & potions">
	public boolean hasPotion(Player player, PotionEffectType effect) {
		UnderscoreEnchants.getStaticLogger().log("hasPotion was called on player " + player.getName() + " for " + effect.getName() + " and resulted in: " + player.hasPotionEffect(effect));
		return player.hasPotionEffect(effect);
	}
	public boolean hasPotion(Player player, XPotion effect) {
		return hasPotion(player, Objects.requireNonNull(effect.getPotionEffectType()));
	}
	public boolean hasPotion(Player player, String effect) {
		return hasPotion(player, Objects.requireNonNull(XPotion.valueOf(effect)));
	}
	//</editor-fold>

	//<editor-fold desc="Inventory">
	public ItemStack getMainHand(Player player) {
		UnderscoreEnchants.getStaticLogger().log("getMainHand was called on player " + player.getName() + " and resulted in: " + player.getInventory().getItemInMainHand().getType() + " item");
		return player.getInventory().getItemInMainHand();
	}

	public ItemStack getHelmet(Player player) {
		UnderscoreEnchants.getStaticLogger().log("getHelmet was called on player " + player.getName() + " and resulted in: " + player.getInventory().getHelmet());
		return player.getInventory().getHelmet();
	}
	public ItemStack getChestplate(Player player) {
		UnderscoreEnchants.getStaticLogger().log("getChestplate was called on player " + player.getName() + " and resulted in: " + player.getInventory().getChestplate());
		return player.getInventory().getChestplate();
	}
	public ItemStack getLeggings(Player player) {
		UnderscoreEnchants.getStaticLogger().log("getLeggings was called on player " + player.getName() + " and resulted in: " + player.getInventory().getLeggings());
		return player.getInventory().getLeggings();
	}
	public ItemStack getBoots(Player player) {
		UnderscoreEnchants.getStaticLogger().log("getBoots was called on player " + player.getName() + " and resulted in: " + player.getInventory().getBoots());
		return player.getInventory().getBoots();
	}
	//</editor-fold>

	//<editor-fold desc="Player state">
	public boolean hasPDC(Player player, NamespacedKey address) {
		UnderscoreEnchants.getStaticLogger().log(String.format("boolean$hasPDC was called on player %s with address %s and resulted in %b.",
			player.getName(),
			address.toString(),
			getPDC(player).has(address, PersistentDataType.STRING)));
		return getPDC(player).has(address, PersistentDataType.STRING);
	}
	public boolean sprinting(Player player) {
		UnderscoreEnchants.getStaticLogger().log("boolean$sprinting was called on player " + player.getName() + " and resulted in: " + player.isSprinting());
		return player.isSprinting();
	}
	public boolean sneaking(Player player) {
		UnderscoreEnchants.getStaticLogger().log("boolean$sneaking was called on player " + player.getName() + " and resulted in: " + player.isSneaking());
		return player.isSneaking();
	}
	public boolean swimming(Player player) {
		UnderscoreEnchants.getStaticLogger().log("boolean$swimming was called on player " + player.getName() + " and resulted in: " + player.isSwimming());
		return player.isSwimming();
	}
	public boolean blocking(Player player) {
		UnderscoreEnchants.getStaticLogger().log("boolean$blocking was called on player " + player.getName() + " and resulted in: " + player.isBlocking());
		return player.isBlocking();
	}
	public boolean flying(Player player) {
		UnderscoreEnchants.getStaticLogger().log("boolean$flying was called on player " + player.getName() + " and resulted in: " + player.isFlying());
		return player.isFlying();
	}

	public boolean onFire(Player player) {
		UnderscoreEnchants.getStaticLogger().log("onFire was called on player " + player.getName() + " and resulted in: " + (player.getFireTicks() > 0));
		return player.getFireTicks() > 0;
	}
	public boolean onTop(Player player) {
		// The Y of the player (floored, e.g. 56.14 -> 56) must be equal or above the top block in the same location.
		boolean onTop = (int) Math.floor(getY(player)) >= (int) Math.floor(getWorld(player).getHighestBlockYAt(getLocation(player)));

		UnderscoreEnchants.getStaticLogger().log("onTop was called on player " + player.getName() + " and resulted in: " + onTop);
		return onTop;
	}

	public boolean op(Player player) {
		UnderscoreEnchants.getStaticLogger().log("boolean$op was called on player " + player.getName() + " and resulted in: " + player.isOp());
		return player.isOp();
	}
	//</editor-fold>

	//<editor-fold desc="World">
	public boolean rains(World world) {
		UnderscoreEnchants.getStaticLogger().log("rains was called on world " + world.getName() + " and resulted in: " + (world.getClearWeatherDuration() == 0 || world.hasStorm()));
		return world.hasStorm();
	}
	public boolean sunshines(World world) {
		UnderscoreEnchants.getStaticLogger().log("sunshines was called on world " + world.getName() + " and resulted in: " + (world.getClearWeatherDuration() > 0 && !world.hasStorm()));
		return !world.hasStorm();
	}
	public boolean thunders(World world) {
		UnderscoreEnchants.getStaticLogger().log("thunders was called on world " + world.getName() + " and resulted in: " + world.isThundering());
		return world.isThundering();
	}

	public boolean rains(Player player) {
		return rains(player.getWorld());
	}
	public boolean sunshines(Player player) {
		return sunshines(player.getWorld());
	}
	public boolean thunders(Player player) {
		return thunders(player.getWorld());
	}

	public boolean day(World world, boolean external) {
		if (!external) UnderscoreEnchants.getStaticLogger().log("boolean$day was called on world " + world.getName() + " and resulted in: " + (world.getTime() < 12300 || world.getTime() > 23850));
		return world.getTime() < 12300 || world.getTime() > 23850;
	}
	public boolean night(World world) {
		UnderscoreEnchants.getStaticLogger().log("boolean$night was called on world " + world.getName() + " and resulted in: " + !day(world, true));
		return !day(world, true);
	}

	public boolean day(Player player) {
		return day(player.getWorld(), false);
	}
	public boolean night(Player player) {
		return night(player.getWorld());
	}

	public boolean overworld(World world) {
		UnderscoreEnchants.getStaticLogger().log("boolean$overworld was called on world " + world.getName() + " and resulted in: " + (world.getEnvironment() == World.Environment.NORMAL));
		return world.getEnvironment() == World.Environment.NORMAL;
	}
	public boolean nether(World world) {
		UnderscoreEnchants.getStaticLogger().log("boolean$nether was called on world " + world.getName() + " and resulted in: " + (world.getEnvironment() == World.Environment.NETHER));
		return world.getEnvironment() == World.Environment.NETHER;
	}
	public boolean end(World world) {
		UnderscoreEnchants.getStaticLogger().log("boolean$end was called on world " + world.getName() + " and resulted in: " + (world.getEnvironment() == World.Environment.THE_END));
		return world.getEnvironment() == World.Environment.THE_END;
	}

	public boolean overworld(Player player) {
		return overworld(player.getWorld());
	}
	public boolean nether(Player player) {
		return nether(player.getWorld());
	}
	public boolean end(Player player) {
		return end(player.getWorld());
	}
	//</editor-fold>
	//</editor-fold>
	//<editor-fold desc="Setters & actions">
	public void setPDC(Player player, NamespacedKey address, String value) {
		getPDC(player).set(address, PersistentDataType.STRING, value);
		UnderscoreEnchants.getStaticLogger().log(String.format("setPDC was called on player %s with address %s and value %s.",
			player.getName(),
			address,
			value));
	}
	public void deletePDC(Player player, NamespacedKey address) {
		getPDC(player).remove(address);
	}
	//<editor-fold desc="Location">
	public void setLocation(Player player, Location location) {
		player.teleport(location);
		UnderscoreEnchants.getStaticLogger().log("setLocation was called on player " + player.getName() + " with location of: " + location);
	}
	public void setLocation(Player player, double x, double y, double z) {
		setLocation(player, x, y, z, getYaw(player), getPitch(player));
	}
	public void setLocation(Player player, double x, double y, double z, float yaw, float pitch) {
		setLocation(player, new Location(getWorld(player), x, y, z, yaw, pitch));
	}

	public void teleportToBed(Player player) {
		player.teleport(player.getBedSpawnLocation() == null ? player.getWorld().getSpawnLocation() : player.getBedSpawnLocation());
		UnderscoreEnchants.getStaticLogger().log("teleportToBed was called on player " + player.getName() + ".");
	}

	public void setX(Player player, double x) {
		setLocation(player, x, getY(player), getZ(player));
	}
	public void increaseX(Player player, double adjustment) {
		setX(player, getX(player) + adjustment);
	}

	public void setY(Player player, double y) {
		setLocation(player, getX(player), y, getZ(player));
	}
	public void increaseY(Player player, double adjustment) {
		setY(player, getY(player) + adjustment);
	}

	public void setZ(Player player, double z) {
		setLocation(player, getX(player), getY(player), z);
	}
	public void increaseZ(Player player, double adjustment) {
		setZ(player, getZ(player) + adjustment);
	}

	public void sendForward(Player player, double adjustment) {
		UnderscoreEnchants.getStaticLogger().log("sendForward was called on player " + player.getName() + " with a value of " + adjustment + ".");
		Location loc = player.getLocation().clone();
		Vector dir = loc.getDirection();
		dir.normalize();
		dir.multiply(adjustment);
		loc.add(dir);
		player.teleport(loc);
	}
	public void sendForward(Player player, String adjustment) {
		sendForward(player, Utils.parseD(adjustment));
	}

	public void setYaw(Player player, float yaw) {
		setLocation(player, getX(player), getY(player), getZ(player), yaw, getPitch(player));
	}
	public void increaseYaw(Player player, float adjustment) {
		setYaw(player, getYaw(player) + adjustment);
	}

	public void setPitch(Player player, float pitch) {
		setLocation(player, getX(player), getY(player), getZ(player), getYaw(player), pitch);
	}
	public void increasePitch(Player player, float adjustment) {
		setPitch(player, getPitch(player) + adjustment);
	}

	public void setDirection(Player player, float yaw, float pitch) {
		setYaw(player, yaw);
		setPitch(player, pitch);
	}
	public void setDirection(Player player, String yaw, String pitch) {
		setYaw(player, Utils.parseF(yaw));
		setPitch(player, Utils.parseF(pitch));
	}

	public void setWorld(Player player, World world) {
		UnderscoreEnchants.getStaticLogger().log("sendForward was called on player " + player.getName() + " with a world of " + world + ".");
		player.teleport(world.getSpawnLocation());
	}
	public void setWorld(Player player, String name) {
		World world = (Bukkit.getWorld(name) == null ? Bukkit.createWorld(new WorldCreator(name)) : Bukkit.getWorld(name));
		setWorld(player, Objects.requireNonNull(world));
	}

	@SuppressWarnings("all") // with my usage it can't be null
	public void strikeLightning(Location location) {
		UnderscoreEnchants.getStaticLogger().log("strikeLightning was called on location " + location + ".");
		location.getWorld().strikeLightning(location);
	}

	@SuppressWarnings("all") // with my usage it can't be null
	public void strikeFakeLightning(Location location) {
		UnderscoreEnchants.getStaticLogger().log("strikeFakeLightning was called on location " + location + ".");
		location.getWorld().strikeLightningEffect(location);
	}

	public void spawnParticle(Player player, Location location, Particle particle) {
		UnderscoreEnchants.getStaticLogger().log("spawnParticle was called on location " + location + ", player " + player.getName() + " and particle " + particle.name());
		Location locatio = location.clone();
		locatio.setY(locatio.getY() + 1);
		player.spawnParticle(particle, locatio, 1);
	}
	public void spawnParticle(Player player, Location location, String particle0) {
		spawnParticle(player, location, XParticle.getParticle(particle0));
	}

	public void spawnParticleBoots(Player player, Location location, Particle particle) {
		UnderscoreEnchants.getStaticLogger().log("spawnParticleBoots was called on location " + location + ", player " + player.getName() + " and particle " + particle.name());
		player.spawnParticle(particle, location, 1);
	}
	public void spawnParticleBoots(Player player, Location location, String particle0) {
		spawnParticleBoots(player, location, XParticle.getParticle(particle0));
	}
	//</editor-fold>

	//<editor-fold desc="Money">
	public EconomyResponse setMoney(Player player, double money) {
		if (UnderscoreEnchants.econ == null) {
			UnderscoreEnchants.getStaticLogger().log("setMoney was called with economy features disabled");
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "");
		}

		UnderscoreEnchants.getStaticLogger().log("setMoney was called on player " + player.getName() + " with a value of " + money + ".");
		UnderscoreEnchants.econ.withdrawPlayer(player, UnderscoreEnchants.econ.getBalance(player));
		return UnderscoreEnchants.econ.depositPlayer(player, money);
	}
	@SuppressWarnings("all") // was arguing that I never used the return EconomyResponse and asked me to change it to void; didn't have other warnings
	public EconomyResponse setMoney(Player player, String money) {
		return setMoney(player, Utils.parseD(money));
	}

	public EconomyResponse increaseMoney(Player player, double adjustment) {
		UnderscoreEnchants.getStaticLogger().log("increaseMoney was called on player " + player.getName() + " with a value of " + adjustment + ".");
		return UnderscoreEnchants.econ.depositPlayer(player, adjustment);
	}
	public EconomyResponse increaseMoney(Player player, String adjustment) {
		return increaseMoney(player, Utils.parseD(adjustment));
	}
	//</editor-fold>

	//<editor-fold desc="Experience">
	public void setXp(Player player, int xp) {
		UnderscoreEnchants.getStaticLogger().log("setXp was called on player " + player.getName() + " with a value of " + xp + ".");
		player.setTotalExperience(0);
		player.setExp(0);
		player.setLevel(0);
		player.giveExp(xp);
	}
	public void setXp(Player player, String xp) {
		setXp(player, Utils.parseI(xp));
	}

	public void increaseXp(Player player, int adjustment) {
		setXp(player, getXp(player) + adjustment);
	}
	public void increaseXp(Player player, String adjustment) {
		increaseXp(player, Utils.parseI(adjustment));
	}

	public void setLevel(Player player, int level) {
		UnderscoreEnchants.getStaticLogger().log("setLevel was called on player " + player.getName() + " with a value of " + level + ".");
		player.setLevel(level);
	}
	public void setLevel(Player player, String level) {
		setLevel(player, Utils.parseI(level));
	}

	public void increaseLevel(Player player, int adjustment) {
		setLevel(player, getLevel(player) + adjustment);
	}
	public void increaseLevel(Player player, String adjustment) {
		increaseLevel(player, Utils.parseI(adjustment));
	}
	//</editor-fold>

	//<editor-fold desc="Health">
	public void setMaximumHealth(Player player, double health) {
		UnderscoreEnchants.getStaticLogger().log("setMaximumHealth was called on player " + player.getName() + " with a value of " + health + ".");
		Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(health);
	}
	public void setMaximumHealth(Player player, String health) {
		setMaximumHealth(player, Utils.parseD(health));
	}

	public void increaseMaximumHealth(Player player, double adjustment) {
		setMaximumHealth(player, getMaximumHealth(player) + adjustment);
	}
	public void increaseMaximumHealth(Player player, String adjustment) {
		increaseMaximumHealth(player, Utils.parseD(adjustment));
	}

	public void setHealth(Player player, double health) {
		if (player.isDead()) return;
		UnderscoreEnchants.getStaticLogger().log("setHealth was called on player " + player.getName() + " with a value of " + health + ".");
		player.setHealth(Math.max(0, Math.min(getMaximumHealth(player), health))); // needs to be under the maximum value but above 0
	}
	public void setHealth(Player player, String health) {
		setHealth(player, Utils.parseD(health));
	}

	public void increaseHealth(Player player, double adjustment) {
		setHealth(player, getHealth(player) + adjustment);
	}
	public void increaseHealth(Player player, String adjustment) {
		increaseHealth(player, Utils.parseD(adjustment));
	}
	//</editor-fold>

	//<editor-fold desc="Food">
	public void setFood(Player player, int food) {
		UnderscoreEnchants.getStaticLogger().log("setFood was called on player " + player.getName() + " with a value of " + food + ".");
		player.setFoodLevel(Math.max(0, Math.min(food, 20))); // needs to be 20 or less
	}
	public void setFood(Player player, String food) {
		setFood(player, Utils.parseI(food));
	}

	public void increaseFood(Player player, int adjustment) {
		setFood(player, getFood(player) + adjustment);
	}
	public void increaseFood(Player player, String adjustment) {
		increaseFood(player, Utils.parseI(adjustment));
	}
	//</editor-fold>

	//<editor-fold desc="Air">
	public void setAir(Player player, int air) {
		UnderscoreEnchants.getStaticLogger().log("setAir was called on player " + player.getName() + " with a value of " + air + ".");
		player.setRemainingAir(Math.max(0, Math.min(getMaximumAir(player), air))); // needs to be under the maximum value but above 0
	}
	public void setAir(Player player, String air) {
		setAir(player, Utils.parseI(air));
	}

	public void increaseAir(Player player, int adjustment) {
		setAir(player, getAir(player) + adjustment);
	}
	public void increaseAir(Player player, String adjustment) {
		increaseAir(player, Utils.parseI(adjustment));
	}

	public void setMaximumAir(Player player, int air) {
		UnderscoreEnchants.getStaticLogger().log("setMaximumAir was called on player " + player.getName() + " with a value of " + air + ".");
		player.setMaximumAir(air);
	}
	public void setMaximumAir(Player player, String air) {
		setMaximumAir(player, Utils.parseI(air));
	}

	public void increaseMaximumAir(Player player, int adjustment) {
		setMaximumAir(player, getMaximumAir(player) + adjustment);
	}
	public void increaseMaximumAir(Player player, String adjustment) {
		increaseMaximumAir(player, Utils.parseI(adjustment));
	}
	//</editor-fold>

	//<editor-fold desc="Velocity">
	public void produceVelocity(Player player, double x, double y, double z) {
		UnderscoreEnchants.getStaticLogger().log("produceVelocity was called on player " + player.getName() + " with coordinates of " + x + ", " + y + " and " + z + ".");
		player.setVelocity(new Vector(x, y, z));
	}
	public void produceVelocity(Player player, String x, String y, String z) {
		player.setVelocity(new Vector(Utils.parseD(x), Utils.parseD(y), Utils.parseD(z)));
	}
	public void produceVelocity(Player player, String[] parameters) {
		produceVelocity(player, parameters[1], parameters[2], parameters[3]);
	}
	//</editor-fold>

	//<editor-fold desc="Sound">
	public void playSound(Player player, Sound sound) {
		UnderscoreEnchants.getStaticLogger().log("playSound was called on player " + player.getName() + " with the following sound: " + sound + ".");
		player.playSound(getLocation(player), sound, SoundCategory.MASTER, 1f, 1f);
	}
	public void playSound(Player player, XSound sound) {
		playSound(player, sound.parseSound());
	}
	public void playSound(Player player, String sound) {
		playSound(player, Objects.requireNonNull(XSound.parse(sound)).sound.parseSound());
	}

	public void playWorldSound(Player player, String x, String y, String z, Sound sound) {
		UnderscoreEnchants.getStaticLogger().log("playWorldSound was called on world " + getWorld(player).getName() + " with the following sound: " + sound + ". There might be a getWorld log after this, belonging here.");
		player.getWorld().playSound(
			new Location(player.getWorld(), Utils.parseD(x), Utils.parseD(y), Utils.parseD(z)),
			sound,
			SoundCategory.MASTER,
			1f,
			1f
		);
	}
	public void playWorldSound(Player player, String x, String y, String z, XSound sound) {
		UnderscoreEnchants.getStaticLogger().log("playWorldSound was called on world " + getWorld(player).getName() + " with the following sound: " + sound + ". There might be a getWorld log after this, belonging here.");
		player.getWorld().playSound(
			new Location(player.getWorld(), Utils.parseD(x), Utils.parseD(y), Utils.parseD(z)),
			Objects.requireNonNull(sound.parseSound()),
			SoundCategory.MASTER,
			1f,
			1f
		);
	}
	public void playWorldSound(Player player, String x, String y, String z, String sound) {
		UnderscoreEnchants.getStaticLogger().log("playWorldSound was called on world " + getWorld(player).getName() + " with the following sound: " + sound + ". There might be a getWorld log after this, belonging here.");
		player.getWorld().playSound(
			new Location(player.getWorld(), Utils.parseD(x), Utils.parseD(y), Utils.parseD(z)),
			Objects.requireNonNull(Objects.requireNonNull(XSound.parse(sound)).sound.parseSound()),
			SoundCategory.MASTER,
			1f,
			1f
		);
	}

	public void playWorldSound(Player player, double x, double y, double z, Sound sound) {
		UnderscoreEnchants.getStaticLogger().log("playWorldSound was called on world " + getWorld(player).getName() + " with the following sound: " + sound + ". There might be a getWorld log after this, belonging here.");
		player.getWorld().playSound(
			new Location(player.getWorld(), x, y, z),
			sound,
			SoundCategory.MASTER,
			1f,
			1f
		);
	}
	public void playWorldSound(Player player, double x, double y, double z, XSound sound) {
		UnderscoreEnchants.getStaticLogger().log("playWorldSound was called on world " + getWorld(player).getName() + " with the following sound: " + sound + ". There might be a getWorld log after this, belonging here.");
		player.getWorld().playSound(
			new Location(player.getWorld(), x, y, z),
			Objects.requireNonNull(sound.parseSound()),
			SoundCategory.MASTER,
			1f,
			1f
		);
	}
	public void playWorldSound(Player player, double x, double y, double z, String sound) {
		UnderscoreEnchants.getStaticLogger().log("playWorldSound was called on world " + getWorld(player).getName() + " with the following sound: " + sound + ". There might be a getWorld log after this, belonging here.");
		player.getWorld().playSound(
			new Location(player.getWorld(), x, y, z),
			Objects.requireNonNull(Objects.requireNonNull(XSound.parse(sound)).sound.parseSound()),
			SoundCategory.MASTER,
			1f,
			1f
		);
	}
	//</editor-fold>

	//<editor-fold desc="Effects & potions">
	public void addPotion(Player player, PotionEffect potion) {
		UnderscoreEnchants.getStaticLogger().log("addPotion was called on player " + player.getName() + " with the following potion: " + potion + ".");
		player.addPotionEffect(potion);
	}

	public void addPermanentPotion(Player player, PotionEffectType effect) {
		UnderscoreEnchants.getStaticLogger().log("addPermanentPotion was called on player " + player.getName() + " with the following potion effect: " + effect + ".");
		player.addPotionEffect(new PotionEffect(effect, 999999, 0));
	}
	public void addPermanentPotion(Player player, XPotion effect) {
		addPermanentPotion(player, effect.getPotionEffectType());
	}
	public void addPermanentPotion(Player player, String effect) {
		addPermanentPotion(player, XPotion.valueOf(effect).getPotionEffectType());
	}

	public void addPermanentPotion(Player player, PotionEffectType effect, int amplifier) {
		UnderscoreEnchants.getStaticLogger().log("addPermanentPotion was called on player " + player.getName() + " with effect " + effect + "and amplifier " + amplifier + ".");
		player.addPotionEffect(new PotionEffect(effect, 999999, amplifier));
	}
	public void addPermanentPotion(Player player, XPotion effect, int amplifier) {
		addPermanentPotion(player, effect.getPotionEffectType(), amplifier);
	}
	public void addPermanentPotion(Player player, String effect, int amplifier) {
		addPermanentPotion(player, XPotion.valueOf(effect).getPotionEffectType(), amplifier);
	}

	public void addPotion(Player player, PotionEffectType effect, int ticks) {
		UnderscoreEnchants.getStaticLogger().log("addPermanentPotion was called on player " + player.getName() + " with effect " + effect + "and duration " + ticks + ".");
		player.addPotionEffect(new PotionEffect(effect, ticks, 0));
	}
	public void addPotion(Player player, XPotion effect, int ticks) {
		addPotion(player, effect.getPotionEffectType(), ticks, 0);
	}
	public void addPotion(Player player, String effect, int ticks) {
		addPotion(player, XPotion.valueOf(effect).getPotionEffectType(), ticks, 0);
	}

	public void addPotion(Player player, PotionEffectType effect, int ticks, int amplifier) {
		UnderscoreEnchants.getStaticLogger().log("addPermanentPotion was called on player " + player.getName() + " with effect " + effect + ", duration " + ticks + "and amplifier " + amplifier + ".");
		player.addPotionEffect(new PotionEffect(effect, ticks, amplifier));
	}
	public void addPotion(Player player, XPotion effect, int ticks, int amplifier) {
		addPotion(player, effect.getPotionEffectType(), ticks, amplifier);
	}
	public void addPotion(Player player, String effect, int ticks, int amplifier) {
		addPotion(player, XPotion.valueOf(effect).getPotionEffectType(), ticks, amplifier);
	}

	public void removePotion(Player player, PotionEffectType effect) {
		UnderscoreEnchants.getStaticLogger().log("removePotion was called on player " + player.getName() + " with the following potion effect: " + effect + ".");
		player.removePotionEffect(effect);
	}
	public void removePotion(Player player, XPotion effect) {
		removePotion(player, effect.getPotionEffectType());
	}
	public void removePotion(Player player, String effect) {
		removePotion(player, XPotion.valueOf(effect).getPotionEffectType());
	}

	public void removeBuffs(Player player, UnderscoreEnchants plugin) {
		UnderscoreEnchants.getStaticLogger().log("removeBuffs was called on player " + player.getName() + ".");
		plugin.getPositiveEffects().forEach(player::removePotionEffect);
	}
	public void removeDebuffs(Player player, UnderscoreEnchants plugin) {
		UnderscoreEnchants.getStaticLogger().log("removeDebuffs was called on player " + player.getName() + ".");
		plugin.getNegativeEffects().forEach(player::removePotionEffect);
	}
	//</editor-fold>

	//<editor-fold desc="Bossbars">
	public void sendBar(Player player, String text, int ticks, BarStyle style, UnderscoreEnchants plugin) {
		UnderscoreEnchants.getStaticLogger().log("sendBar was called on player " + player.getName() + "with text " + text + ", duration " + ticks + " and style " + style + ".");
		BossBar bar = Bukkit.createBossBar(Utils.format(text), BarColor.RED, style);
		bar.addPlayer(player);
		Bukkit.getScheduler().runTaskLater(plugin, bar::removeAll, ticks);
	}
	public void sendBar(Player player, String style, String ticks, String text, UnderscoreEnchants plugin) {
		sendBar(player, text, Utils.parseI(ticks), BarStyle.valueOf(style), plugin);
	}
	//</editor-fold>

	//<editor-fold desc="Inventory">
	public void give(Player player, Material material, int amount) {
		UnderscoreEnchants.getStaticLogger().log("give was called on player " + player.getName() + " with the following material: " + material + ".");
		player.getInventory().addItem(new ItemStack(material, amount)).forEach((num, it) -> dropItem(player, it));
	}
	public void give(Player player, XMaterial material, int amount) {
		give(player, material.parseMaterial(), amount);
	}
	public void give(Player player, String material, int amount) {
		give(player, XMaterial.valueOf(material), amount);
	}
	public void give(Player player, ItemStack item, int amount) {
		UnderscoreEnchants.getStaticLogger().log("give was called on player " + player.getName() + " with the following item: " + item + ".");
		player.getInventory().addItem(new ItemStack(item.getType(), amount)).forEach((num, it) -> dropItem(player, it));
	}

	public void setMainHand(Player player, Material material) {
		UnderscoreEnchants.getStaticLogger().log("setMainHand was called on player " + player.getName() + " with the following material: " + material + ".");
		player.getInventory().setItemInMainHand(new ItemStack(material));
	}
	public void setMainHand(Player player, XMaterial material) {
		setMainHand(player, material.parseMaterial());
	}
	public void setMainHand(Player player, String material) {
		setMainHand(player, XMaterial.valueOf(material));
	}
	public void setMainHand(Player player, ItemStack item) {
		UnderscoreEnchants.getStaticLogger().log("setMainHand was called on player " + player.getName() + " with the following item: " + item + ".");
		player.getInventory().setItemInMainHand(item);
	}

	public void nullateMainHand(Player player) {
		UnderscoreEnchants.getStaticLogger().log("nullateMainHand was called on player " + player.getName() + ".");
		player.getInventory().setItemInMainHand(null);
	}
	public void takeHandFrom(Player target, Player receiver) {
		UnderscoreEnchants.getStaticLogger().log("takeHandFrom was called on target " + target.getName() + " and for receiver " + receiver.getName() + ".");
		ItemStack hand = getMainHand(target);
		nullateMainHand(target);
		give(receiver, hand, 1);
	}
	public void dropHand(Player player) {
		UnderscoreEnchants.getStaticLogger().log("dropHand was called on player " + player.getName() + ".");
		ItemStack hand = getMainHand(player);
		nullateMainHand(player);
		dropItem(player, hand);
	}

	@SuppressWarnings("deprecation") // read comment inside
	public void giveHeadOf(Player player, String name) {
		UnderscoreEnchants.getStaticLogger().log("giveHeadOf was called on player " + player.getName() + " for head of" + name + ".");
		ItemStack head = SkullUtils.getSkull(Bukkit.getOfflinePlayer(name).getUniqueId()); // it works, and the deprecation here does not matter, no alternatives
		if (player.getInventory().firstEmpty() != 1) player.getInventory().addItem(head);
		else dropItem(player, head);
	}

	public void shuffleHotbar(Player player) {
		UnderscoreEnchants.getStaticLogger().log("shuffleHotbar was called on player " + player.getName() + ".");
		List<ItemStack> hotbar = new ArrayList<>();
		for (int i = 0; i < 9; i++) hotbar.add(player.getInventory().getItem(i));
		Collections.shuffle(hotbar);

		for (int i = 0; i < 9; i++) player.getInventory().setItem(i, hotbar.get(i));
		player.updateInventory();
	}
	public void shuffleInventory(Player player) {
		UnderscoreEnchants.getStaticLogger().log("shuffleInventory was called on player " + player.getName() + ".");
		List<ItemStack> inventory = Arrays.asList(player.getInventory().getContents());
		Collections.shuffle(inventory);

		player.getInventory().setContents(inventory.toArray(new ItemStack[0]));
		player.updateInventory();
	}

	public void setHelmet(Player player, ItemStack armor) {
		UnderscoreEnchants.getStaticLogger().log("setHelmet was called for player " + player.getName() + " and an armor piece of " + armor + ".");
		player.getInventory().setHelmet(armor);
	}
	public void setChestplate(Player player, ItemStack armor) {
		UnderscoreEnchants.getStaticLogger().log("setChestplate was called for player " + player.getName() + " and an armor piece of " + armor + ".");
		player.getInventory().setChestplate(armor);
	}
	public void setLeggings(Player player, ItemStack armor) {
		UnderscoreEnchants.getStaticLogger().log("setLeggings was called for player " + player.getName() + " and an armor piece of " + armor + ".");
		player.getInventory().setLeggings(armor);
	}
	public void setBoots(Player player, ItemStack armor) {
		UnderscoreEnchants.getStaticLogger().log("setBoots was called for player " + player.getName() + " and an armor piece of " + armor + ".");
		player.getInventory().setBoots(armor);
	}

	/**
	 * Utility method. Not advised to use outside of class.
	 */
	public ItemStack damageItem(ItemStack item, int damage) {
		// Can't work with null items or AIR items
		if (item == null || item.getType() == Material.AIR) return null;

		// Retrieve the meta
		ItemMeta meta0 = item.getItemMeta();

		// Check if it actually IS damageable; make that damageable a thing
		if (!(meta0 instanceof Damageable meta)) return null;

		// P.S. This line of code is redundant due to me checking for air earlier. Will leave it commented in case I will need it later.
		// Do not call ItemStack#hasItemMeta, as it returns false if an item doesn't have customizations, not if the meta is actually null
		// Instead, null-check the meta. Only returns false for AIR, which is what I want
		// if (meta0 == null) return null

		// Damage the meta by the given value
		meta.setDamage(meta.getDamage() + damage);

		// In case it exceeds the maximum durability (means the item is broken), rip it
		if (meta.getDamage() >= item.getType().getMaxDurability()) item.setType(Material.AIR);

		// Assign the meta back to the item
		item.setItemMeta(meta0);

		return item;
	}

	public void damageArmorPiece(Player player, int damage, ArmorPiece piece) {
		// Can't work with null armor pieces or null entries
		if (piece == null || piece.getItem() == null) return;

		// Retrieve the values
		ItemStack armor = piece.getItem();

		// Damage the item with
		armor = damageItem(armor, damage);
		if (armor == null) return;

		// Finally, give it back to the player
		switch (piece.getSlot()) {
			case HEAD -> setHelmet(player, armor);
			case CHEST -> setChestplate(player, armor);
			case LEGS -> setLeggings(player, armor);
			case FEET -> setBoots(player, armor);
			default -> {}
		}

		UnderscoreEnchants.getStaticLogger().log("damageArmorPiece was called for player " + player.getName() + ", damage of " + damage + " and a piece entry of " + piece + ". There might be a set(armor) log after, belonging here.");
	}
	public void damageArmorPiece(Player player, String damage, String piece0) {
		ArmorPiece piece;
		piece = switch (piece0.toLowerCase(Locale.ROOT)) {
			case "helmet" -> ArmorPiece.of(EquipmentSlot.HEAD, getHelmet(player));
			case "chestplate" -> ArmorPiece.of(EquipmentSlot.CHEST, getChestplate(player));
			case "leggings" -> ArmorPiece.of(EquipmentSlot.LEGS, getLeggings(player));
			case "boots" -> ArmorPiece.of(EquipmentSlot.FEET, getBoots(player));
			default -> null;
		};
		damageArmorPiece(player, Utils.parseI(damage), piece);
	}

	public void damageHand(Player player, int damage) {
		UnderscoreEnchants.getStaticLogger().log("damageHand was called for player " + player.getName() + "and damage of " + damage + ". There might be a setMainHand log after, belonging here.");
		setMainHand(player, damageItem(getMainHand(player), damage));
	}
	public void damageHand(Player player, String damage) {
		damageHand(player, Utils.parseI(damage));
	}

	/**
	 * Utility method. Not advised to use outside of class.
	 */
	public ItemStack fixItem(ItemStack item) {
		// Can't work with null items or AIR items
		if (item == null || item.getType() == Material.AIR) return null;

		// Retrieve the meta
		ItemMeta meta0 = item.getItemMeta();

		// Check if it actually IS damageable; make that damageable a thing
		if (!(meta0 instanceof Damageable meta)) return null;

		// Repair the meta
		meta.setDamage(0);

		// Assign the meta back to the item
		item.setItemMeta(meta0);

		return item;
	}

	public void fixArmorPiece(Player player, ArmorPiece piece) {
		// Can't work with null armor pieces or null entries
		if (piece == null || piece.getItem() == null) return;

		// Retrieve the values
		ItemStack armor = piece.getItem();

		// Damage the item with
		armor = fixItem(armor);
		if (armor == null) return;

		// Finally, give it back to the player
		switch (piece.getSlot()) {
			case HEAD -> setHelmet(player, armor);
			case CHEST -> setChestplate(player, armor);
			case LEGS -> setLeggings(player, armor);
			case FEET -> setBoots(player, armor);
			default -> {}
		}

		UnderscoreEnchants.getStaticLogger().log("fixArmorPiece was called for player " + player.getName() + "and an armorpiece entry of " + piece + ". There might be a set(armor) log after, belonging here.");
	}
	public void fixArmorPiece(Player player, String piece0) {
		ArmorPiece piece;
		piece = switch (piece0.toLowerCase(Locale.ROOT)) {
			case "helmet" -> ArmorPiece.of(EquipmentSlot.HEAD, getHelmet(player));
			case "chestplate" -> ArmorPiece.of(EquipmentSlot.CHEST, getChestplate(player));
			case "leggings" -> ArmorPiece.of(EquipmentSlot.LEGS, getLeggings(player));
			case "boots" -> ArmorPiece.of(EquipmentSlot.FEET, getBoots(player));
			default -> null;
		};
		fixArmorPiece(player, piece);
	}

	public void fixHand(Player player) {
		UnderscoreEnchants.getStaticLogger().log("damageHand was called for player " + player.getName() + ". There might be a setMainHand log after, belonging here.");
		setMainHand(player, fixItem(getMainHand(player)));
	}
	//</editor-fold>

	//<editor-fold desc="Player state">
	public void setFire(Player player) {
		setFire(player, 200);
	}
	public void setFire(Player player, int ticks) {
		UnderscoreEnchants.getStaticLogger().log("setFire was called for player " + player.getName() + "for " + ticks + "ticks.");
		player.setFireTicks(ticks);
	}
	public void setFire(Player player, String ticks) {
		setFire(player, Utils.parseI(ticks));
	}

	public void setPlayerTime(Player player, long ticks) {
		UnderscoreEnchants.getStaticLogger().log("setPlayerTime was called for player " + player.getName() + "for " + ticks + "ticks.");
		player.setPlayerTime(ticks, true);
	}
	public void setPlayerTime(Player player, String ticks) {
		setPlayerTime(player, Utils.parseL(ticks));
	}
	public void resetPlayerTime(Player player) {
		UnderscoreEnchants.getStaticLogger().log("resetPlayerTime was called for player " + player.getName() + ".");
		player.setPlayerTime(player.getWorld().getTime(), true);
		player.resetPlayerTime();
	}

	public void setPlayerWeather(Player player, String weather) {
		UnderscoreEnchants.getStaticLogger().log("setPlayerWeather was called for player " + player.getName() + "with the weather of " + weather + ".");
		switch (weather.toLowerCase(Locale.ROOT)) {
			case "sun" -> player.setPlayerWeather(WeatherType.CLEAR);
			case "rain", "thunder" -> player.setPlayerWeather(WeatherType.DOWNFALL);
		}
	}
	public void resetPlayerWeather(Player player) {
		UnderscoreEnchants.getStaticLogger().log("resetPlayerWeather was called for player " + player.getName() + ".");
		switch (player.getWorld().getClearWeatherDuration()) {
			case 0 -> player.setPlayerWeather(WeatherType.DOWNFALL);
			default -> player.setPlayerWeather(WeatherType.CLEAR);
		}
		player.resetPlayerWeather();
	}

	public void makeInvisibleFor(Player player, int ticks, UnderscoreEnchants plugin) {
		UnderscoreEnchants.getStaticLogger().log("makeInvisibleFor was called for player " + player.getName() + " with the duration of " + ticks + " ticks.");
		UnderscoreEnchants.gods.put(player.getUniqueId(), Math.max(0, ticks));
		player.setInvulnerable(true);
	}
	//</editor-fold>

	//<editor-fold desc="Projectiles on player's behalf">
	public void sendProjectile(Player player, Class<? extends Projectile> clazz) {
		UnderscoreEnchants.getStaticLogger().log("sendProjectile was called for player " + player.getName() + "with the projectile type of " + clazz.getName() + ".");
		player.launchProjectile(clazz).setShooter(player);
	}
	public void sendArrow(Player player) {
		sendProjectile(player, Arrow.class);
	}
	public void sendFireball(Player player) {
		sendProjectile(player, Fireball.class);
	}
	//</editor-fold>

	//<editor-fold desc="Text">
	public void say(Player player, String message) {
		UnderscoreEnchants.getStaticLogger().log("say was called for player " + player.getName() + "with a message of " + message + ".");
		player.chat(Utils.format(message));
	}
	public void tellTo(Player player, String message) {
		UnderscoreEnchants.getStaticLogger().log("tellTo was called for player " + player.getName() + "with a message of " + message + ".");
		player.sendMessage(Utils.format(message));
	}

	public void sendTitle(Player player, String title) {
		sendTitle(player, title, "");
	}
	public void sendTitle(Player player, String title, String subtitle) {
		sendTitle(player, title, subtitle, 10, 70, 20);
	}
	public void sendSubtitle(Player player, String subtitle) {
		sendTitle(player, "", subtitle);
	}
	public void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
		UnderscoreEnchants.getStaticLogger().log("sendTitle was called for player " + player.getName() + "with fadeIn, stay and fadeOut of " + fadeIn + ", " + stay + " and " + fadeOut + ", " +
			"respectively, and title and subtitle of \"" + title + "\" and \"" + subtitle + "\" respectively.");
		player.sendTitle(Utils.format(title), Utils.format(subtitle), fadeIn, stay, fadeOut);
	}

	public void sendActionbar(Player player, String message) {
		player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Utils.format(message)));
	}
	//</editor-fold>
	//</editor-fold>
	//<editor-fold desc="Miscellaneous (not necessarily players)">
	//<editor-fold desc="Entity">
	public Entity spawnEntity(Player worldHolder, EntityType type, double x, double y, double z) {
		UnderscoreEnchants.getStaticLogger().log(String.format("spawnEntity was called at X:%f, Y:%f, Z:%f in world %s with entity type of %s.", x, y, z, worldHolder.getWorld().getName(), type));
		return worldHolder.getWorld().spawnEntity(new Location(worldHolder.getWorld(), x, y, z), type, true);
	}
	@SuppressWarnings("all") // was angry that I didn't use the Entity return anywhere and advised to change it to void; doesn't have other warnings
	public Entity spawnEntity(Player worldHolder, String type, String x, String y, String z) {
		return spawnEntity(worldHolder, EntityType.valueOf(type), Utils.parseD(x), Utils.parseD(y), Utils.parseD(z));
	}

	public LivingEntity spawnEntity(Player pl, EntityType type, double x, double y, double z, String name, double hp, PotionEffectType pot, int ticks, int amplifier) {
		LivingEntity entity = (LivingEntity) spawnEntity(pl, type, x, y, z);

		entity.setCustomName(name);
		entity.setCustomNameVisible(true);
		entity.setRemoveWhenFarAway(false);

		Objects.requireNonNull(Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH))).setBaseValue(hp);
		entity.setHealth(hp);

		entity.addPotionEffect(new PotionEffect(pot, ticks, amplifier));

		UnderscoreEnchants.getStaticLogger().log(String.format("spawnEntity was called at X:%f, Y:%f, Z:%f in world %s with entity type of %s, name of %s, health of %f, with an effect of %s (lasts %d ticks and is of %d level).",
			x, y, z, pl.getWorld().getName(), type, name, hp, pot, ticks, amplifier));
		return entity;
	}
	@SuppressWarnings("all") // ref. above
	public LivingEntity spawnEntity(Player pl, String type, String x, String y, String z, String name, String hp, String pot, String ticks, String amplifier) {
		return spawnEntity(pl, EntityType.valueOf(type), Utils.parseD(x), Utils.parseD(y), Utils.parseD(z), name, Utils.parseD(hp),
					XPotion.valueOf(pot).getPotionEffectType(), Utils.parseI(ticks), Utils.parseI(amplifier));
	}
	//</editor-fold>

	//<editor-fold desc="World">
	public void createWorld(String name, WorldType type, String generator) {
		WorldCreator creator = new WorldCreator(name);
		creator.type(type);
		if (!generator.equals("-1")) creator.generatorSettings(generator);
		creator.createWorld();
		UnderscoreEnchants.getStaticLogger().log("createWorld was called for a world " + name + " of type " + type + " with the following generator settings: " + generator + ".");
	}
	public void createWorld(String name, String type, String generator) {
		createWorld(name, WorldType.valueOf(type), generator);
	}

	public void setBlock(World world, int x, int y, int z, Material material) {
		world.getBlockAt(x, y, z).setType(material);
		UnderscoreEnchants.getStaticLogger().log("setBlock was called for a block " + material.name() + " at X:" + x + ", Y:" + y + ", Z:" + z + " in " + world.getName() + ".");
	}
	public void setBlock(World world, int x, int y, int z, XMaterial material) {
		setBlock(world, x, y, z, material.parseMaterial());
	}
	public void setBlock(World world, int x, int y, int z, String material) {
		setBlock(world, x, y, z, XMaterial.valueOf(material).parseMaterial());
	}

	public void setBlock(World world, String x, String y, String z, Material material) {
		setBlock(world, Utils.parseI(x), Utils.parseI(y), Utils.parseI(z), material);
	}
	public void setBlock(World world, String x, String y, String z, XMaterial material) {
		setBlock(world, x, y, z, material.parseMaterial());
	}
	public void setBlock(World world, String x, String y, String z, String material) {
		setBlock(world, x, y, z, XMaterial.valueOf(material).parseMaterial());
	}

	public void setTime(World world, long ticks) {
		UnderscoreEnchants.getStaticLogger().log("setTime was called in a world " + world.getName() + " and time " + ticks + ".");
		world.setTime(ticks);
	}
	public void setTime(World world, String ticks) {
		setTime(world, Utils.parseL(ticks));
	}

	public void setWeather(World world, String weather) {
		UnderscoreEnchants.getStaticLogger().log("setWeather was called in a world " + world.getName() + " and a weather setting of " + weather + ".");
		if (weather.equalsIgnoreCase("sun")) {
			world.setStorm(false);
			world.setThundering(false);
		} else if (weather.equalsIgnoreCase("rain")) {
			world.setStorm(true);
			world.setThundering(false);
		} else if (weather.equalsIgnoreCase("thunder")) {
			world.setStorm(true);
			world.setThundering(true);
		}
	}
	//</editor-fold>

	//<editor-fold desc="Location">
	public void dropItem(Player player, ItemStack item) {
		dropItem(getLocation(player), item);
	}
	public void dropItem(Location location, ItemStack item) {
		UnderscoreEnchants.getStaticLogger().log("dropItem was called in a world " + location.getWorld() + " for an item of " + item + ".");
		if (location.getWorld() != null && item != null)
			location.getWorld().dropItemNaturally(location, item);
	}
	//</editor-fold>

	//<editor-fold desc="Server">
	public void announce(String message) {
		UnderscoreEnchants.getStaticLogger().log("announce was called to broadcast the following message: " + message + ".");
		Bukkit.broadcastMessage(Utils.format(message));
	}
	//</editor-fold>
	//</editor-fold>
}

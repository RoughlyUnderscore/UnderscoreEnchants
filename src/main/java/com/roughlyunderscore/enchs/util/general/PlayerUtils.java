package com.roughlyunderscore.enchs.util.general;

import com.cryptomorin.xseries.SkullUtils;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XPotion;
import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.particles.XParticle;
import com.roughlyunderscore.enchs.UnderscoreEnchants;
import com.roughlyunderscore.enchs.util.ArmorPiece;
import com.roughlyunderscore.enchs.util.cooldownutils.ActionbarCooldown;
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

@UtilityClass
@SuppressWarnings("unused")
/*
Just a lot of utils that I don't want to explain
 */
public class PlayerUtils<T, Z> {
  //<editor-fold desc="Getters">
  //<editor-fold desc="PDC">
  public PersistentDataContainer getPDC(Player player) {
    return player.getPersistentDataContainer();
  }

  public Object getPDCValue(Player player, NamespacedKey address, UnderscoreEnchants plugin) {
    plugin.getDebugger().log(String.format("getPDCValue was called on player %s with address %s and yielded %s.",
      player.getName(),
      address.toString(),
      getPDC(player).get(address, PersistentDataType.STRING)));
    return getPDC(player).get(address, PersistentDataType.STRING);
  }

  //</editor-fold>
  //<editor-fold desc="Location">
  public Location getLocation(Player player, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("getLocation was called on player " + player.getName() + " and resulted in the following location: " + player.getLocation());
    return player.getLocation();
  }

  public double getX(Player player, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("getX was called on player " + player.getName() + " and resulted in the following coordinate: " + getLocation(player, plugin).getX());
    return getLocation(player, plugin).getX();
  }

  public double getY(Player player, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("getY was called on player " + player.getName() + " and resulted in the following coordinate: " + getLocation(player, plugin).getY());
    return getLocation(player, plugin).getY();
  }

  public double getZ(Player player, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("getZ was called on player " + player.getName() + " and resulted in the following coordinate: " + getLocation(player, plugin).getZ());
    return getLocation(player, plugin).getZ();
  }

  public float getYaw(Player player, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("getYaw was called on player " + player.getName() + " and resulted in the following angle: " + getLocation(player, plugin).getYaw());
    return getLocation(player, plugin).getYaw();
  }

  public float getPitch(Player player, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("getPitch was called on player " + player.getName() + " and resulted in the following angle: " + getLocation(player, plugin).getPitch());
    return getLocation(player, plugin).getPitch();
  }

  public String getXString(Player player, UnderscoreEnchants plugin) {
    return String.valueOf(getX(player, plugin));
  }

  public String getYString(Player player, UnderscoreEnchants plugin) {
    return String.valueOf(getY(player, plugin));
  }

  public String getZString(Player player, UnderscoreEnchants plugin) {
    return String.valueOf(getZ(player, plugin));
  }

  public String getYawString(Player player, UnderscoreEnchants plugin) {
    return String.valueOf(getYaw(player, plugin));
  }

  public String getPitchString(Player player, UnderscoreEnchants plugin) {
    return String.valueOf(getPitch(player, plugin));
  }

  public World getWorld(Player player, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("getWorld was called on player " + player.getName() + " and resulted in the following world: " + getLocation(player, plugin).getWorld());
    return getLocation(player, plugin).getWorld();
  }

  public String getWorldName(Player player, UnderscoreEnchants plugin) {
    return getWorld(player, plugin).getName();
  }
  //</editor-fold>

  //<editor-fold desc="Money">
  public double getMoney(Player player, UnderscoreEnchants plugin) {
    if (UnderscoreEnchants.econ == null) {
      plugin.getDebugger().log("getMoney was called with economy features disabled");
      return 0;
    }
    plugin.getDebugger().log("getMoney was called on player " + player.getName() + " and resulted in the following balance: " + UnderscoreEnchants.econ.getBalance(player));
    return UnderscoreEnchants.econ.getBalance(player);
  }

  public String getMoneyString(Player player, UnderscoreEnchants plugin) {
    return String.valueOf(getMoney(player, plugin));
  }
  //</editor-fold>

  //<editor-fold desc="Experience">
  public int getXp(Player player, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("getXp was called on player " + player.getName() + " and resulted in the following value: " + player.getTotalExperience());
    return player.getTotalExperience();
  }

  public String getXpString(Player player, UnderscoreEnchants plugin) {
    return String.valueOf(getXp(player, plugin));
  }

  public int getLevel(Player player, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("getLevel was called on player " + player.getName() + " and resulted in the following value: " + player.getLevel());
    return player.getLevel();
  }

  public String getLevelString(Player player, UnderscoreEnchants plugin) {
    return String.valueOf(getLevel(player, plugin));
  }
  //</editor-fold>

  //<editor-fold desc="Health">
  public double getMaximumHealth(Player player, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("getMaximumHealth was called on player " + player.getName() + " and resulted in the following value: " + Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getBaseValue());
    return Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getBaseValue();
  }

  public String getMaximumHealthString(Player player, UnderscoreEnchants plugin) {
    return String.valueOf(getMaximumHealth(player, plugin));
  }

  public double getHealth(Player player, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("getHealth was called on player " + player.getName() + " and resulted in the following value: " + player.getHealth());
    return player.getHealth();
  }

  public String getHealthString(Player player, UnderscoreEnchants plugin) {
    return String.valueOf(getHealth(player, plugin));
  }
  //</editor-fold>

  //<editor-fold desc="Food">
  public int getFood(Player player, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("getFood was called on player " + player.getName() + " and resulted in the following value: " + player.getFoodLevel());
    return player.getFoodLevel();
  }

  public String getFoodString(Player player, UnderscoreEnchants plugin) {
    return String.valueOf(getFood(player, plugin));
  }
  //</editor-fold>

  //<editor-fold desc="Air">
  public int getAir(Player player, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("getAir was called on player " + player.getName() + " and resulted in the following value: " + player.getRemainingAir());
    return player.getRemainingAir();
  }

  public String getAirString(Player player, UnderscoreEnchants plugin) {
    return String.valueOf(getAir(player, plugin));
  }

  public int getMaximumAir(Player player, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("getMaximumAir was called on player " + player.getName() + " and resulted in the following value: " + player.getMaximumAir());
    return player.getMaximumAir();
  }

  public String getMaximumAirString(Player player, UnderscoreEnchants plugin) {
    return String.valueOf(getMaximumAir(player, plugin));
  }
  //</editor-fold>

  //<editor-fold desc="Effects & potions">
  public boolean hasPotion(Player player, PotionEffectType effect, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("hasPotion was called on player " + player.getName() + " for " + effect.getName() + " and resulted in: " + player.hasPotionEffect(effect));
    return player.hasPotionEffect(effect);
  }

  public boolean hasPotion(Player player, XPotion effect, UnderscoreEnchants plugin) {
    return hasPotion(player, Objects.requireNonNull(effect.getPotionEffectType()), plugin);
  }

  public boolean hasPotion(Player player, String effect, UnderscoreEnchants plugin) {
    return hasPotion(player, Objects.requireNonNull(XPotion.valueOf(effect)), plugin);
  }
  //</editor-fold>

  //<editor-fold desc="Inventory">
  public ItemStack getMainHand(Player player, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("getMainHand was called on player " + player.getName() + " and resulted in: " + player.getInventory().getItemInMainHand().getType() + " item");
    return player.getInventory().getItemInMainHand();
  }

  public ItemStack getHelmet(Player player, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("getHelmet was called on player " + player.getName() + " and resulted in: " + player.getInventory().getHelmet());
    return player.getInventory().getHelmet();
  }

  public ItemStack getChestplate(Player player, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("getChestplate was called on player " + player.getName() + " and resulted in: " + player.getInventory().getChestplate());
    return player.getInventory().getChestplate();
  }

  public ItemStack getLeggings(Player player, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("getLeggings was called on player " + player.getName() + " and resulted in: " + player.getInventory().getLeggings());
    return player.getInventory().getLeggings();
  }

  public ItemStack getBoots(Player player, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("getBoots was called on player " + player.getName() + " and resulted in: " + player.getInventory().getBoots());
    return player.getInventory().getBoots();
  }
  //</editor-fold>

  //<editor-fold desc="Player state">
  public boolean hasPDC(Player player, NamespacedKey address, UnderscoreEnchants plugin) {
    plugin.getDebugger().log(String.format("boolean$hasPDC was called on player %s with address %s and resulted in %b.",
      player.getName(),
      address.toString(),
      getPDC(player).has(address, PersistentDataType.STRING)));
    return getPDC(player).has(address, PersistentDataType.STRING);
  }

  public boolean sprinting(Player player, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("boolean$sprinting was called on player " + player.getName() + " and resulted in: " + player.isSprinting());
    return player.isSprinting();
  }

  public boolean sneaking(Player player, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("boolean$sneaking was called on player " + player.getName() + " and resulted in: " + player.isSneaking());
    return player.isSneaking();
  }

  public boolean swimming(Player player, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("boolean$swimming was called on player " + player.getName() + " and resulted in: " + player.isSwimming());
    return player.isSwimming();
  }

  public boolean blocking(Player player, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("boolean$blocking was called on player " + player.getName() + " and resulted in: " + player.isBlocking());
    return player.isBlocking();
  }

  public boolean flying(Player player, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("boolean$flying was called on player " + player.getName() + " and resulted in: " + player.isFlying());
    return player.isFlying();
  }

  public boolean onFire(Player player, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("onFire was called on player " + player.getName() + " and resulted in: " + (player.getFireTicks() > 0));
    return player.getFireTicks() > 0;
  }

  public boolean onTop(Player player, UnderscoreEnchants plugin) {
    // The Y of the player (floored, e.g. 56.14 -> 56) must be equal or above the top block in the same location.
    boolean onTop = (int) Math.floor(getY(player, plugin)) >= (int) Math.floor(getWorld(player, plugin).getHighestBlockYAt(getLocation(player, plugin)));

    plugin.getDebugger().log("onTop was called on player " + player.getName() + " and resulted in: " + onTop);
    return onTop;
  }

  public boolean op(Player player, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("boolean$op was called on player " + player.getName() + " and resulted in: " + player.isOp());
    return player.isOp();
  }
  //</editor-fold>

  //<editor-fold desc="World">
  public boolean rains(World world, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("rains was called on world " + world.getName() + " and resulted in: " + (world.getClearWeatherDuration() == 0 || world.hasStorm()));
    return world.hasStorm();
  }

  public boolean sunshines(World world, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("sunshines was called on world " + world.getName() + " and resulted in: " + (world.getClearWeatherDuration() > 0 && !world.hasStorm()));
    return !world.hasStorm();
  }

  public boolean thunders(World world, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("thunders was called on world " + world.getName() + " and resulted in: " + world.isThundering());
    return world.isThundering();
  }

  public boolean rains(Player player, UnderscoreEnchants plugin) {
    return rains(player.getWorld(), plugin);
  }

  public boolean sunshines(Player player, UnderscoreEnchants plugin) {
    return sunshines(player.getWorld(), plugin);
  }

  public boolean thunders(Player player, UnderscoreEnchants plugin) {
    return thunders(player.getWorld(), plugin);
  }

  public boolean day(World world, boolean external, UnderscoreEnchants plugin) {
    if (!external)
      plugin.getDebugger().log("boolean$day was called on world " + world.getName() + " and resulted in: " + (world.getTime() < 12300 || world.getTime() > 23850));
    return world.getTime() < 12300 || world.getTime() > 23850;
  }

  public boolean night(World world, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("boolean$night was called on world " + world.getName() + " and resulted in: " + !day(world, true, plugin));
    return !day(world, true, plugin);
  }

  public boolean day(Player player, UnderscoreEnchants plugin) {
    return day(player.getWorld(), false, plugin);
  }

  public boolean night(Player player, UnderscoreEnchants plugin) {
    return night(player.getWorld(), plugin);
  }

  public boolean overworld(World world, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("boolean$overworld was called on world " + world.getName() + " and resulted in: " + (world.getEnvironment() == World.Environment.NORMAL));
    return world.getEnvironment() == World.Environment.NORMAL;
  }

  public boolean nether(World world, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("boolean$nether was called on world " + world.getName() + " and resulted in: " + (world.getEnvironment() == World.Environment.NETHER));
    return world.getEnvironment() == World.Environment.NETHER;
  }

  public boolean end(World world, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("boolean$end was called on world " + world.getName() + " and resulted in: " + (world.getEnvironment() == World.Environment.THE_END));
    return world.getEnvironment() == World.Environment.THE_END;
  }

  public boolean overworld(Player player, UnderscoreEnchants plugin) {
    return overworld(player.getWorld(), plugin);
  }

  public boolean nether(Player player, UnderscoreEnchants plugin) {
    return nether(player.getWorld(), plugin);
  }

  public boolean end(Player player, UnderscoreEnchants plugin) {
    return end(player.getWorld(), plugin);
  }

  //</editor-fold>
  //</editor-fold>
  //<editor-fold desc="Setters & actions">
  public void setPDC(Player player, NamespacedKey address, String value, UnderscoreEnchants plugin) {
    getPDC(player).set(address, PersistentDataType.STRING, value);
    plugin.getDebugger().log(String.format("setPDC was called on player %s with address %s and value %s.",
      player.getName(),
      address,
      value));
  }

  public void deletePDC(Player player, NamespacedKey address, UnderscoreEnchants plugin) {
    getPDC(player).remove(address);
  }

  //<editor-fold desc="Location">
  public void setLocation(Player player, Location location, UnderscoreEnchants plugin) {
    player.teleport(location);
    plugin.getDebugger().log("setLocation was called on player " + player.getName() + " with location of: " + location);
  }

  public void setLocation(Player player, double x, double y, double z, UnderscoreEnchants plugin) {
    setLocation(player, x, y, z, getYaw(player, plugin), getPitch(player, plugin), plugin);
  }

  public void setLocation(Player player, double x, double y, double z, float yaw, float pitch, UnderscoreEnchants plugin) {
    setLocation(player, new Location(getWorld(player, plugin), x, y, z, yaw, pitch), plugin);
  }

  public void teleportToBed(Player player, UnderscoreEnchants plugin) {
    player.teleport(player.getBedSpawnLocation() == null ? player.getWorld().getSpawnLocation() : player.getBedSpawnLocation());
    plugin.getDebugger().log("teleportToBed was called on player " + player.getName() + ".");
  }

  public void setX(Player player, double x, UnderscoreEnchants plugin) {
    setLocation(player, x, getY(player, plugin), getZ(player, plugin), plugin);
  }

  public void increaseX(Player player, double adjustment, UnderscoreEnchants plugin) {
    setX(player, getX(player, plugin) + adjustment, plugin);
  }

  public void setY(Player player, double y, UnderscoreEnchants plugin) {
    setLocation(player, getX(player, plugin), y, getZ(player, plugin), plugin);
  }

  public void increaseY(Player player, double adjustment, UnderscoreEnchants plugin) {
    setY(player, getY(player, plugin) + adjustment, plugin);
  }

  public void setZ(Player player, double z, UnderscoreEnchants plugin) {
    setLocation(player, getX(player, plugin), getY(player, plugin), z, plugin);
  }

  public void increaseZ(Player player, double adjustment, UnderscoreEnchants plugin) {
    setZ(player, getZ(player, plugin) + adjustment, plugin);
  }

  public void sendForward(Player player, double adjustment, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("sendForward was called on player " + player.getName() + " with a value of " + adjustment + ".");
    Location loc = player.getLocation().clone();
    Vector dir = loc.getDirection();
    dir.normalize();
    dir.multiply(adjustment);
    loc.add(dir);
    player.teleport(loc);
  }

  public void sendForward(Player player, String adjustment, UnderscoreEnchants plugin) {
    sendForward(player, Utils.parseD(adjustment), plugin);
  }

  public void setYaw(Player player, float yaw, UnderscoreEnchants plugin) {
    setLocation(player, getX(player, plugin), getY(player, plugin), getZ(player, plugin), yaw, getPitch(player, plugin), plugin);
  }

  public void increaseYaw(Player player, float adjustment, UnderscoreEnchants plugin) {
    setYaw(player, getYaw(player, plugin) + adjustment, plugin);
  }

  public void setPitch(Player player, float pitch, UnderscoreEnchants plugin) {
    setLocation(player, getX(player, plugin), getY(player, plugin), getZ(player, plugin), getYaw(player, plugin), pitch, plugin);
  }

  public void increasePitch(Player player, float adjustment, UnderscoreEnchants plugin) {
    setPitch(player, getPitch(player, plugin) + adjustment, plugin);
  }

  public void setDirection(Player player, float yaw, float pitch, UnderscoreEnchants plugin) {
    setYaw(player, yaw, plugin);
    setPitch(player, pitch, plugin);
  }

  public void setDirection(Player player, String yaw, String pitch, UnderscoreEnchants plugin) {
    setYaw(player, Utils.parseF(yaw), plugin);
    setPitch(player, Utils.parseF(pitch), plugin);
  }

  public void setWorld(Player player, World world, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("sendForward was called on player " + player.getName() + " with a world of " + world + ".");
    player.teleport(world.getSpawnLocation());
  }

  public void setWorld(Player player, String name, UnderscoreEnchants plugin) {
    World world = (Bukkit.getWorld(name) == null ? Bukkit.createWorld(new WorldCreator(name)) : Bukkit.getWorld(name));
    setWorld(player, Objects.requireNonNull(world), plugin);
  }

  @SuppressWarnings("all") // with my usage it can't be null
  public void strikeLightning(Location location, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("strikeLightning was called on location " + location + ".");
    location.getWorld().strikeLightning(location);
  }

  @SuppressWarnings("all") // with my usage it can't be null
  public void strikeFakeLightning(Location location, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("strikeFakeLightning was called on location " + location + ".");
    location.getWorld().strikeLightningEffect(location);
  }

  public void spawnParticle(Player player, Location location, Particle particle, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("spawnParticle was called on location " + location + ", player " + player.getName() + " and particle " + particle.name());
    Location locatio = location.clone();
    locatio.setY(locatio.getY() + 1);
    player.spawnParticle(particle, locatio, 1);
  }

  public void spawnParticle(Player player, Location location, String particle0, UnderscoreEnchants plugin) {
    spawnParticle(player, location, XParticle.getParticle(particle0), plugin);
  }

  public void spawnParticleBoots(Player player, Location location, Particle particle, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("spawnParticleBoots was called on location " + location + ", player " + player.getName() + " and particle " + particle.name());
    player.spawnParticle(particle, location, 1);
  }

  public void spawnParticleBoots(Player player, Location location, String particle0, UnderscoreEnchants plugin) {
    spawnParticleBoots(player, location, XParticle.getParticle(particle0), plugin);
  }
  //</editor-fold>

  //<editor-fold desc="Money">
  public EconomyResponse setMoney(Player player, double money, UnderscoreEnchants plugin) {
    if (UnderscoreEnchants.econ == null) {
      plugin.getDebugger().log("setMoney was called with economy features disabled");
      return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "");
    }

    plugin.getDebugger().log("setMoney was called on player " + player.getName() + " with a value of " + money + ".");
    UnderscoreEnchants.econ.withdrawPlayer(player, UnderscoreEnchants.econ.getBalance(player));
    return UnderscoreEnchants.econ.depositPlayer(player, money);
  }

  public EconomyResponse setMoney(Player player, String money, UnderscoreEnchants plugin) {
    return setMoney(player, Utils.parseD(money), plugin);
  }

  public EconomyResponse increaseMoney(Player player, double adjustment, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("increaseMoney was called on player " + player.getName() + " with a value of " + adjustment + ".");
    return UnderscoreEnchants.econ.depositPlayer(player, adjustment);
  }

  public EconomyResponse increaseMoney(Player player, String adjustment, UnderscoreEnchants plugin) {
    return increaseMoney(player, Utils.parseD(adjustment), plugin);
  }
  //</editor-fold>

  //<editor-fold desc="Experience">
  public void setXp(Player player, int xp, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("setXp was called on player " + player.getName() + " with a value of " + xp + ".");
    player.setTotalExperience(0);
    player.setExp(0);
    player.setLevel(0);
    player.giveExp(xp);
  }

  public void setXp(Player player, String xp, UnderscoreEnchants plugin) {
    setXp(player, Utils.parseI(xp), plugin);
  }

  public void increaseXp(Player player, int adjustment, UnderscoreEnchants plugin) {
    setXp(player, getXp(player, plugin) + adjustment, plugin);
  }

  public void increaseXp(Player player, String adjustment, UnderscoreEnchants plugin) {
    increaseXp(player, Utils.parseI(adjustment), plugin);
  }

  public void setLevel(Player player, int level, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("setLevel was called on player " + player.getName() + " with a value of " + level + ".");
    player.setLevel(level);
  }

  public void setLevel(Player player, String level, UnderscoreEnchants plugin) {
    setLevel(player, Utils.parseI(level), plugin);
  }

  public void increaseLevel(Player player, int adjustment, UnderscoreEnchants plugin) {
    setLevel(player, getLevel(player, plugin) + adjustment, plugin);
  }

  public void increaseLevel(Player player, String adjustment, UnderscoreEnchants plugin) {
    increaseLevel(player, Utils.parseI(adjustment), plugin);
  }
  //</editor-fold>

  //<editor-fold desc="Health">
  public void setMaximumHealth(Player player, double health, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("setMaximumHealth was called on player " + player.getName() + " with a value of " + health + ".");
    Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(health);
  }

  public void setMaximumHealth(Player player, String health, UnderscoreEnchants plugin) {
    setMaximumHealth(player, Utils.parseD(health), plugin);
  }

  public void increaseMaximumHealth(Player player, double adjustment, UnderscoreEnchants plugin) {
    setMaximumHealth(player, getMaximumHealth(player, plugin) + adjustment, plugin);
  }

  public void increaseMaximumHealth(Player player, String adjustment, UnderscoreEnchants plugin) {
    increaseMaximumHealth(player, Utils.parseD(adjustment), plugin);
  }

  public void setHealth(Player player, double health, UnderscoreEnchants plugin) {
    if (player.isDead()) return;
    plugin.getDebugger().log("setHealth was called on player " + player.getName() + " with a value of " + health + ".");
    player.setHealth(Math.max(0, Math.min(getMaximumHealth(player, plugin), health))); // needs to be under the maximum value but above 0
  }

  public void setHealth(Player player, String health, UnderscoreEnchants plugin) {
    setHealth(player, Utils.parseD(health), plugin);
  }

  public void increaseHealth(Player player, double adjustment, UnderscoreEnchants plugin) {
    setHealth(player, getHealth(player, plugin) + adjustment, plugin);
  }

  public void increaseHealth(Player player, String adjustment, UnderscoreEnchants plugin) {
    increaseHealth(player, Utils.parseD(adjustment), plugin);
  }
  //</editor-fold>

  //<editor-fold desc="Food">
  public void setFood(Player player, int food, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("setFood was called on player " + player.getName() + " with a value of " + food + ".");
    player.setFoodLevel(Math.max(0, Math.min(food, 20))); // needs to be 20 or less
  }

  public void setFood(Player player, String food, UnderscoreEnchants plugin) {
    setFood(player, Utils.parseI(food), plugin);
  }

  public void increaseFood(Player player, int adjustment, UnderscoreEnchants plugin) {
    setFood(player, getFood(player, plugin) + adjustment, plugin);
  }

  public void increaseFood(Player player, String adjustment, UnderscoreEnchants plugin) {
    increaseFood(player, Utils.parseI(adjustment), plugin);
  }
  //</editor-fold>

  //<editor-fold desc="Air">
  public void setAir(Player player, int air, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("setAir was called on player " + player.getName() + " with a value of " + air + ".");
    player.setRemainingAir(Math.max(0, Math.min(getMaximumAir(player, plugin), air))); // needs to be under the maximum value but above 0
  }

  public void setAir(Player player, String air, UnderscoreEnchants plugin) {
    setAir(player, Utils.parseI(air), plugin);
  }

  public void increaseAir(Player player, int adjustment, UnderscoreEnchants plugin) {
    setAir(player, getAir(player, plugin) + adjustment, plugin);
  }

  public void increaseAir(Player player, String adjustment, UnderscoreEnchants plugin) {
    increaseAir(player, Utils.parseI(adjustment), plugin);
  }

  public void setMaximumAir(Player player, int air, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("setMaximumAir was called on player " + player.getName() + " with a value of " + air + ".");
    player.setMaximumAir(air);
  }

  public void setMaximumAir(Player player, String air, UnderscoreEnchants plugin) {
    setMaximumAir(player, Utils.parseI(air), plugin);
  }

  public void increaseMaximumAir(Player player, int adjustment, UnderscoreEnchants plugin) {
    setMaximumAir(player, getMaximumAir(player, plugin) + adjustment, plugin);
  }

  public void increaseMaximumAir(Player player, String adjustment, UnderscoreEnchants plugin) {
    increaseMaximumAir(player, Utils.parseI(adjustment), plugin);
  }
  //</editor-fold>

  //<editor-fold desc="Velocity">
  public void produceVelocity(Player player, double x, double y, double z, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("produceVelocity was called on player " + player.getName() + " with coordinates of " + x + ", " + y + " and " + z + ".");
    player.setVelocity(new Vector(x, y, z));
  }

  public void produceVelocity(Player player, String x, String y, String z, UnderscoreEnchants plugin) {
    player.setVelocity(new Vector(Utils.parseD(x), Utils.parseD(y), Utils.parseD(z)));
  }

  public void produceVelocity(Player player, String[] parameters, UnderscoreEnchants plugin) {
    produceVelocity(player, parameters[1], parameters[2], parameters[3], plugin);
  }
  //</editor-fold>

  //<editor-fold desc="Sound">
  public void playSound(Player player, Sound sound, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("playSound was called on player " + player.getName() + " with the following sound: " + sound + ".");
    player.playSound(getLocation(player, plugin), sound, SoundCategory.MASTER, 1f, 1f);
  }

  public void playSound(Player player, XSound sound, UnderscoreEnchants plugin) {
    playSound(player, sound.parseSound(), plugin);
  }

  public void playSound(Player player, String sound, UnderscoreEnchants plugin) {
    playSound(player, Objects.requireNonNull(XSound.parse(sound)).sound.parseSound(), plugin);
  }

  public void playWorldSound(Player player, String x, String y, String z, Sound sound, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("playWorldSound was called on world " + getWorld(player, plugin).getName() + " with the following sound: " + sound + ". There might be a getWorld log after this, belonging here.");
    player.getWorld().playSound(
      new Location(player.getWorld(), Utils.parseD(x), Utils.parseD(y), Utils.parseD(z)),
      sound,
      SoundCategory.MASTER,
      1f,
      1f
    );
  }

  public void playWorldSound(Player player, String x, String y, String z, XSound sound, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("playWorldSound was called on world " + getWorld(player, plugin).getName() + " with the following sound: " + sound + ". There might be a getWorld log after this, belonging here.");
    player.getWorld().playSound(
      new Location(player.getWorld(), Utils.parseD(x), Utils.parseD(y), Utils.parseD(z)),
      Objects.requireNonNull(sound.parseSound()),
      SoundCategory.MASTER,
      1f,
      1f
    );
  }

  public void playWorldSound(Player player, String x, String y, String z, String sound, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("playWorldSound was called on world " + getWorld(player, plugin).getName() + " with the following sound: " + sound + ". There might be a getWorld log after this, belonging here.");
    player.getWorld().playSound(
      new Location(player.getWorld(), Utils.parseD(x), Utils.parseD(y), Utils.parseD(z)),
      Objects.requireNonNull(Objects.requireNonNull(XSound.parse(sound)).sound.parseSound()),
      SoundCategory.MASTER,
      1f,
      1f
    );
  }

  public void playWorldSound(Player player, double x, double y, double z, Sound sound, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("playWorldSound was called on world " + getWorld(player, plugin).getName() + " with the following sound: " + sound + ". There might be a getWorld log after this, belonging here.");
    player.getWorld().playSound(
      new Location(player.getWorld(), x, y, z),
      sound,
      SoundCategory.MASTER,
      1f,
      1f
    );
  }

  public void playWorldSound(Player player, double x, double y, double z, XSound sound, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("playWorldSound was called on world " + getWorld(player, plugin).getName() + " with the following sound: " + sound + ". There might be a getWorld log after this, belonging here.");
    player.getWorld().playSound(
      new Location(player.getWorld(), x, y, z),
      Objects.requireNonNull(sound.parseSound()),
      SoundCategory.MASTER,
      1f,
      1f
    );
  }

  public void playWorldSound(Player player, double x, double y, double z, String sound, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("playWorldSound was called on world " + getWorld(player, plugin).getName() + " with the following sound: " + sound + ". There might be a getWorld log after this, belonging here.");
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
  public void addPotion(Player player, PotionEffect potion, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("addPotion was called on player " + player.getName() + " with the following potion: " + potion + ".");
    player.addPotionEffect(potion);
  }

  public void addPermanentPotion(Player player, PotionEffectType effect, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("addPermanentPotion was called on player " + player.getName() + " with the following potion effect: " + effect + ".");
    player.addPotionEffect(new PotionEffect(effect, 999999, 0));
  }

  public void addPermanentPotion(Player player, XPotion effect, UnderscoreEnchants plugin) {
    addPermanentPotion(player, effect.getPotionEffectType(), plugin);
  }

  public void addPermanentPotion(Player player, String effect, UnderscoreEnchants plugin) {
    addPermanentPotion(player, XPotion.valueOf(effect).getPotionEffectType(), plugin);
  }

  public void addPermanentPotion(Player player, PotionEffectType effect, int amplifier, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("addPermanentPotion was called on player " + player.getName() + " with effect " + effect + "and amplifier " + amplifier + ".");
    player.addPotionEffect(new PotionEffect(effect, 999999, amplifier));
  }

  public void addPermanentPotion(Player player, XPotion effect, int amplifier, UnderscoreEnchants plugin) {
    addPermanentPotion(player, effect.getPotionEffectType(), amplifier, plugin);
  }

  public void addPermanentPotion(Player player, String effect, int amplifier, UnderscoreEnchants plugin) {
    addPermanentPotion(player, XPotion.valueOf(effect).getPotionEffectType(), amplifier, plugin);
  }

  public void addPotion(Player player, PotionEffectType effect, int ticks, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("addPermanentPotion was called on player " + player.getName() + " with effect " + effect + "and duration " + ticks + ".");
    player.addPotionEffect(new PotionEffect(effect, ticks, 0));
  }

  public void addPotion(Player player, XPotion effect, int ticks, UnderscoreEnchants plugin) {
    addPotion(player, effect.getPotionEffectType(), ticks, 0, plugin);
  }

  public void addPotion(Player player, String effect, int ticks, UnderscoreEnchants plugin) {
    addPotion(player, XPotion.valueOf(effect).getPotionEffectType(), ticks, 0, plugin);
  }

  public void addPotion(Player player, PotionEffectType effect, int ticks, int amplifier, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("addPermanentPotion was called on player " + player.getName() + " with effect " + effect + ", duration " + ticks + "and amplifier " + amplifier + ".");
    player.addPotionEffect(new PotionEffect(effect, ticks, amplifier));
  }

  public void addPotion(Player player, XPotion effect, int ticks, int amplifier, UnderscoreEnchants plugin) {
    addPotion(player, effect.getPotionEffectType(), ticks, amplifier, plugin);
  }

  public void addPotion(Player player, String effect, int ticks, int amplifier, UnderscoreEnchants plugin) {
    addPotion(player, XPotion.valueOf(effect).getPotionEffectType(), ticks, amplifier, plugin);
  }

  public void removePotion(Player player, PotionEffectType effect, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("removePotion was called on player " + player.getName() + " with the following potion effect: " + effect + ".");
    player.removePotionEffect(effect);
  }

  public void removePotion(Player player, XPotion effect, UnderscoreEnchants plugin) {
    removePotion(player, effect.getPotionEffectType(), plugin);
  }

  public void removePotion(Player player, String effect, UnderscoreEnchants plugin) {
    removePotion(player, XPotion.valueOf(effect).getPotionEffectType(), plugin);
  }

  public void removeBuffs(Player player, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("removeBuffs was called on player " + player.getName() + ".");
    plugin.getPositiveEffects().forEach(player::removePotionEffect);
  }

  public void removeDebuffs(Player player, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("removeDebuffs was called on player " + player.getName() + ".");
    plugin.getNegativeEffects().forEach(player::removePotionEffect);
  }
  //</editor-fold>

  //<editor-fold desc="Bossbars">
  public void sendBar(Player player, String text, int ticks, BarStyle style, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("sendBar was called on player " + player.getName() + "with text " + text + ", duration " + ticks + " and style " + style + ".");
    BossBar bar = Bukkit.createBossBar(Utils.format(text), BarColor.RED, style);
    bar.addPlayer(player);
    Bukkit.getScheduler().runTaskLater(plugin, bar::removeAll, ticks);
  }

  public void sendBar(Player player, String style, String ticks, String text, UnderscoreEnchants plugin) {
    sendBar(player, text, Utils.parseI(ticks), BarStyle.valueOf(style), plugin);
  }
  //</editor-fold>

  //<editor-fold desc="Inventory">
  public void give(Player player, Material material, int amount, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("give was called on player " + player.getName() + " with the following material: " + material + ".");
    player.getInventory().addItem(new ItemStack(material, amount)).forEach((num, it) -> dropItem(player, it, plugin));
  }

  public void give(Player player, XMaterial material, int amount, UnderscoreEnchants plugin) {
    give(player, material.parseMaterial(), amount, plugin);
  }

  public void give(Player player, String material, int amount, UnderscoreEnchants plugin) {
    give(player, XMaterial.valueOf(material), amount, plugin);
  }

  public void give(Player player, ItemStack item, int amount, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("give was called on player " + player.getName() + " with the following item: " + item + ".");
    player.getInventory().addItem(new ItemStack(item.getType(), amount)).forEach((num, it) -> dropItem(player, it, plugin));
  }

  public void give(Player player, ItemStack item, UnderscoreEnchants plugin) {
    final int amount = item.getAmount();
    item.setAmount(1);
    give(player, item, amount, plugin);
  }

  public void setMainHand(Player player, Material material, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("setMainHand was called on player " + player.getName() + " with the following material: " + material + ".");
    player.getInventory().setItemInMainHand(new ItemStack(material));
  }

  public void setMainHand(Player player, XMaterial material, UnderscoreEnchants plugin) {
    setMainHand(player, material.parseMaterial(), plugin);
  }

  public void setMainHand(Player player, String material, UnderscoreEnchants plugin) {
    setMainHand(player, XMaterial.valueOf(material), plugin);
  }

  public void setMainHand(Player player, ItemStack item, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("setMainHand was called on player " + player.getName() + " with the following item: " + item + ".");
    player.getInventory().setItemInMainHand(item);
  }

  public void nullateMainHand(Player player, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("nullateMainHand was called on player " + player.getName() + ".");
    player.getInventory().setItemInMainHand(null);
  }

  public void takeHandFrom(Player target, Player receiver, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("takeHandFrom was called on target " + target.getName() + " and for receiver " + receiver.getName() + ".");
    ItemStack hand = getMainHand(target, plugin);
    nullateMainHand(target, plugin);
    give(receiver, hand, 1, plugin);
  }

  public void dropHand(Player player, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("dropHand was called on player " + player.getName() + ".");
    ItemStack hand = getMainHand(player, plugin);
    nullateMainHand(player, plugin);
    dropItem(player, hand, plugin);
  }

  @SuppressWarnings("deprecation") // read comment inside
  public void giveHeadOf(Player player, String name, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("giveHeadOf was called on player " + player.getName() + " for head of" + name + ".");
    ItemStack head = SkullUtils.getSkull(Bukkit.getOfflinePlayer(name).getUniqueId()); // it works, and the deprecation here does not matter, no alternatives
    if (player.getInventory().firstEmpty() != 1) player.getInventory().addItem(head);
    else dropItem(player, head, plugin);
  }

  public void shuffleHotbar(Player player, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("shuffleHotbar was called on player " + player.getName() + ".");
    List<ItemStack> hotbar = new ArrayList<>();
    for (int i = 0; i < 9; i++) hotbar.add(player.getInventory().getItem(i));
    Collections.shuffle(hotbar);

    for (int i = 0; i < 9; i++) player.getInventory().setItem(i, hotbar.get(i));
    player.updateInventory();
  }

  public void shuffleInventory(Player player, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("shuffleInventory was called on player " + player.getName() + ".");
    List<ItemStack> inventory = Arrays.asList(player.getInventory().getContents());
    Collections.shuffle(inventory);

    player.getInventory().setContents(inventory.toArray(new ItemStack[0]));
    player.updateInventory();
  }

  public void setHelmet(Player player, ItemStack armor, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("setHelmet was called for player " + player.getName() + " and an armor piece of " + armor + ".");
    player.getInventory().setHelmet(armor);
  }

  public void setChestplate(Player player, ItemStack armor, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("setChestplate was called for player " + player.getName() + " and an armor piece of " + armor + ".");
    player.getInventory().setChestplate(armor);
  }

  public void setLeggings(Player player, ItemStack armor, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("setLeggings was called for player " + player.getName() + " and an armor piece of " + armor + ".");
    player.getInventory().setLeggings(armor);
  }

  public void setBoots(Player player, ItemStack armor, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("setBoots was called for player " + player.getName() + " and an armor piece of " + armor + ".");
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

  public void damageArmorPiece(Player player, int damage, ArmorPiece piece, UnderscoreEnchants plugin) {
    // Can't work with null armor pieces or null entries
    if (piece == null || piece.getItem() == null) return;

    // Retrieve the values
    ItemStack armor = piece.getItem();

    // Damage the item with
    armor = damageItem(armor, damage);
    if (armor == null) return;

    // Finally, give it back to the player
    switch (piece.getSlot()) {
      case HEAD -> setHelmet(player, armor, plugin);
      case CHEST -> setChestplate(player, armor, plugin);
      case LEGS -> setLeggings(player, armor, plugin);
      case FEET -> setBoots(player, armor, plugin);
      default -> {
      }
    }

    plugin.getDebugger().log("damageArmorPiece was called for player " + player.getName() + ", damage of " + damage + " and a piece entry of " + piece + ". There might be a set(armor) log after, belonging here.");
  }

  public void damageArmorPiece(Player player, String damage, String piece0, UnderscoreEnchants plugin) {
    ArmorPiece piece;
    piece = switch (piece0.toLowerCase(Locale.ROOT)) {
      case "helmet" -> ArmorPiece.of(EquipmentSlot.HEAD, getHelmet(player, plugin));
      case "chestplate" -> ArmorPiece.of(EquipmentSlot.CHEST, getChestplate(player, plugin));
      case "leggings" -> ArmorPiece.of(EquipmentSlot.LEGS, getLeggings(player, plugin));
      case "boots" -> ArmorPiece.of(EquipmentSlot.FEET, getBoots(player, plugin));
      default -> null;
    };
    damageArmorPiece(player, Utils.parseI(damage), piece, plugin);
  }

  public void damageHand(Player player, int damage, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("damageHand was called for player " + player.getName() + "and damage of " + damage + ". There might be a setMainHand log after, belonging here.");
    setMainHand(player, damageItem(getMainHand(player, plugin), damage), plugin);
  }

  public void damageHand(Player player, String damage, UnderscoreEnchants plugin) {
    damageHand(player, Utils.parseI(damage), plugin);
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

  public void fixArmorPiece(Player player, ArmorPiece piece, UnderscoreEnchants plugin) {
    // Can't work with null armor pieces or null entries
    if (piece == null || piece.getItem() == null) return;

    // Retrieve the values
    ItemStack armor = piece.getItem();

    // Damage the item with
    armor = fixItem(armor);
    if (armor == null) return;

    // Finally, give it back to the player
    switch (piece.getSlot()) {
      case HEAD -> setHelmet(player, armor, plugin);
      case CHEST -> setChestplate(player, armor, plugin);
      case LEGS -> setLeggings(player, armor, plugin);
      case FEET -> setBoots(player, armor, plugin);
      default -> {
      }
    }

    plugin.getDebugger().log("fixArmorPiece was called for player " + player.getName() + "and an armorpiece entry of " + piece + ". There might be a set(armor) log after, belonging here.");
  }

  public void fixArmorPiece(Player player, String piece0, UnderscoreEnchants plugin) {
    ArmorPiece piece;
    piece = switch (piece0.toLowerCase(Locale.ROOT)) {
      case "helmet" -> ArmorPiece.of(EquipmentSlot.HEAD, getHelmet(player, plugin));
      case "chestplate" -> ArmorPiece.of(EquipmentSlot.CHEST, getChestplate(player, plugin));
      case "leggings" -> ArmorPiece.of(EquipmentSlot.LEGS, getLeggings(player, plugin));
      case "boots" -> ArmorPiece.of(EquipmentSlot.FEET, getBoots(player, plugin));
      default -> null;
    };
    fixArmorPiece(player, piece, plugin);
  }

  public void fixHand(Player player, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("damageHand was called for player " + player.getName() + ". There might be a setMainHand log after, belonging here.");
    setMainHand(player, fixItem(getMainHand(player, plugin)), plugin);
  }
  //</editor-fold>

  //<editor-fold desc="Player state">
  public void setFire(Player player, UnderscoreEnchants plugin) {
    setFire(player, 200, plugin);
  }

  public void setFire(Player player, int ticks, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("setFire was called for player " + player.getName() + "for " + ticks + "ticks.");
    player.setFireTicks(ticks);
  }

  public void setFire(Player player, String ticks, UnderscoreEnchants plugin) {
    setFire(player, Utils.parseI(ticks), plugin);
  }

  public void setPlayerTime(Player player, long ticks, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("setPlayerTime was called for player " + player.getName() + "for " + ticks + "ticks.");
    player.setPlayerTime(ticks, true);
  }

  public void setPlayerTime(Player player, String ticks, UnderscoreEnchants plugin) {
    setPlayerTime(player, Utils.parseL(ticks), plugin);
  }

  public void resetPlayerTime(Player player, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("resetPlayerTime was called for player " + player.getName() + ".");
    player.setPlayerTime(player.getWorld().getTime(), true);
    player.resetPlayerTime();
  }

  public void setPlayerWeather(Player player, String weather, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("setPlayerWeather was called for player " + player.getName() + "with the weather of " + weather + ".");
    switch (weather.toLowerCase(Locale.ROOT)) {
      case "sun" -> player.setPlayerWeather(WeatherType.CLEAR);
      case "rain", "thunder" -> player.setPlayerWeather(WeatherType.DOWNFALL);
    }
  }

  public void resetPlayerWeather(Player player, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("resetPlayerWeather was called for player " + player.getName() + ".");
    switch (player.getWorld().getClearWeatherDuration()) {
      case 0 -> player.setPlayerWeather(WeatherType.DOWNFALL);
      default -> player.setPlayerWeather(WeatherType.CLEAR);
    }
    player.resetPlayerWeather();
  }

  public void makeInvisibleFor(Player player, int ticks, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("makeInvisibleFor was called for player " + player.getName() + " with the duration of " + ticks + " ticks.");
    plugin.getGods().put(player.getUniqueId(), Math.max(0, ticks));
    player.setInvulnerable(true);
  }
  //</editor-fold>

  //<editor-fold desc="Projectiles on player's behalf">
  public void sendProjectile(Player player, Class<? extends Projectile> clazz, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("sendProjectile was called for player " + player.getName() + "with the projectile type of " + clazz.getName() + ".");
    player.launchProjectile(clazz).setShooter(player);
  }

  public void sendArrow(Player player, UnderscoreEnchants plugin) {
    sendProjectile(player, Arrow.class, plugin);
  }

  public void sendFireball(Player player, UnderscoreEnchants plugin) {
    sendProjectile(player, Fireball.class, plugin);
  }
  //</editor-fold>

  //<editor-fold desc="Gamemode">
  public void setGamemode(Player player, String gamemode, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("setGamemode was called for player " + player.getName() + "with the gamemode of " + gamemode + ".");
    switch (gamemode.toLowerCase(Locale.ROOT)) {
      case "survival", "0", "s", "surv", "sruvival", "suvriral", "survaval", "survivla", "выживание", "в" ->
        player.setGameMode(GameMode.SURVIVAL);
      case "creative", "1", "c", "crea", "create", "createve", "crteiati", "креатив", "творческий", "т" ->
        player.setGameMode(GameMode.CREATIVE);
      case "adventure", "2", "a", "adv", "adventrue", "adveruntre", "advuentru", "приключение", "приключенческий", "п" ->
        player.setGameMode(GameMode.ADVENTURE);
      case "spectator", "3", "sp", "spec", "spect", "spectatotr", "spectatort", "spectatrot", "specaotor", "spectoaror", "наблюдатель", "наблюдение", "н" ->
        player.setGameMode(GameMode.SPECTATOR);
    }
  }

  public void setGamemode(Player player, int gamemode, UnderscoreEnchants plugin) {
    setGamemode(player, String.valueOf(gamemode), plugin);
  }

  public void setGamemode(Player player, GameMode gamemode, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("setGamemode was called for player " + player.getName() + "with the gamemode of " + gamemode.name() + ".");
    player.setGameMode(gamemode);
  }
  //</editor-fold>

  //<editor-fold desc="Text">
  public void say(Player player, String message, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("say was called for player " + player.getName() + "with a message of " + message + ".");
    player.chat(Utils.format(message));
  }

  public void tellTo(Player player, String message, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("tellTo was called for player " + player.getName() + "with a message of " + message + ".");
    player.sendMessage(Utils.format(message));
  }

  public void sendTitle(Player player, String title, UnderscoreEnchants plugin) {
    sendTitle(player, title, "", plugin);
  }

  public void sendTitle(Player player, String title, String subtitle, UnderscoreEnchants plugin) {
    sendTitle(player, title, subtitle, 10, 70, 20, plugin);
  }

  public void sendSubtitle(Player player, String subtitle, UnderscoreEnchants plugin) {
    sendTitle(player, "", subtitle, plugin);
  }

  public void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("sendTitle was called for player " + player.getName() + "with fadeIn, stay and fadeOut of " + fadeIn + ", " + stay + " and " + fadeOut + ", " +
      "respectively, and title and subtitle of \"" + title + "\" and \"" + subtitle + "\" respectively.");
    player.sendTitle(Utils.format(title), Utils.format(subtitle), fadeIn, stay, fadeOut);
  }

  public void sendActionbar(Player player, String message, UnderscoreEnchants plugin) {
    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Utils.format(message)));
  }

  public void sendActionbar(Player player, String message, int duration, UnderscoreEnchants plugin) {
    plugin.getActionbars().add(ActionbarCooldown.builder().message(Utils.format(message)).seconds(duration).plugin(plugin).uuid(player.getUniqueId()).create());
  }

  //</editor-fold>
  //</editor-fold>
  //<editor-fold desc="Miscellaneous (not necessarily players)">
  //<editor-fold desc="Entity">
  public Entity spawnEntity(Player worldHolder, EntityType type, double x, double y, double z, UnderscoreEnchants plugin) {
    plugin.getDebugger().log(String.format("spawnEntity was called at X:%f, Y:%f, Z:%f in world %s with entity type of %s.", x, y, z, worldHolder.getWorld().getName(), type));
    return worldHolder.getWorld().spawnEntity(new Location(worldHolder.getWorld(), x, y, z), type, true);
  }

  public Entity spawnEntity(Player worldHolder, String type, String x, String y, String z, UnderscoreEnchants plugin) {
    return spawnEntity(worldHolder, EntityType.valueOf(type), Utils.parseD(x), Utils.parseD(y), Utils.parseD(z), plugin);
  }

  public LivingEntity spawnEntity(Player pl, EntityType type, double x, double y, double z, String name, double hp, PotionEffectType pot, int ticks, int amplifier, UnderscoreEnchants plugin) {
    LivingEntity entity = (LivingEntity) spawnEntity(pl, type, x, y, z, plugin);

    entity.setCustomName(name);
    entity.setCustomNameVisible(true);
    entity.setRemoveWhenFarAway(false);

    Objects.requireNonNull(Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH))).setBaseValue(hp);
    entity.setHealth(hp);

    entity.addPotionEffect(new PotionEffect(pot, ticks, amplifier));

    plugin.getDebugger().log(String.format("spawnEntity was called at X:%f, Y:%f, Z:%f in world %s with entity type of %s, name of %s, health of %f, with an effect of %s (lasts %d ticks and is of %d level).",
      x, y, z, pl.getWorld().getName(), type, name, hp, pot, ticks, amplifier));
    return entity;
  }

  @SuppressWarnings("all") // ref. above
  public LivingEntity spawnEntity(Player pl, String type, String x, String y, String z, String name, String hp, String pot, String ticks, String amplifier, UnderscoreEnchants plugin) {
    return spawnEntity(pl, EntityType.valueOf(type), Utils.parseD(x), Utils.parseD(y), Utils.parseD(z), name, Utils.parseD(hp),
      XPotion.valueOf(pot).getPotionEffectType(), Utils.parseI(ticks), Utils.parseI(amplifier), plugin);
  }
  //</editor-fold>

  //<editor-fold desc="World">


  public void createWorld(String name, WorldType type, String generator, UnderscoreEnchants plugin) {
    WorldCreator creator = new WorldCreator(name);
    creator.type(type);
    if (!generator.equals("-1")) creator.generatorSettings(generator);
    creator.createWorld();
    plugin.getDebugger().log("createWorld was called for a world " + name + " of type " + type + " with the following generator settings: " + generator + ".");
  }

  public void createWorld(String name, String type, String generator, UnderscoreEnchants plugin) {
    createWorld(name, WorldType.valueOf(type), generator, plugin);
  }

  public void setBlock(World world, int x, int y, int z, Material material, UnderscoreEnchants plugin) {
    world.getBlockAt(x, y, z).setType(material);
    plugin.getDebugger().log("setBlock was called for a block " + material.name() + " at X:" + x + ", Y:" + y + ", Z:" + z + " in " + world.getName() + ".");
  }

  public void setBlock(World world, int x, int y, int z, XMaterial material, UnderscoreEnchants plugin) {
    setBlock(world, x, y, z, material.parseMaterial(), plugin);
  }

  public void setBlock(World world, int x, int y, int z, String material, UnderscoreEnchants plugin) {
    setBlock(world, x, y, z, XMaterial.valueOf(material).parseMaterial(), plugin);
  }

  public void setBlock(World world, String x, String y, String z, Material material, UnderscoreEnchants plugin) {
    setBlock(world, Utils.parseI(x), Utils.parseI(y), Utils.parseI(z), material, plugin);
  }

  public void setBlock(World world, String x, String y, String z, XMaterial material, UnderscoreEnchants plugin) {
    setBlock(world, x, y, z, material.parseMaterial(), plugin);
  }

  public void setBlock(World world, String x, String y, String z, String material, UnderscoreEnchants plugin) {
    setBlock(world, x, y, z, XMaterial.valueOf(material).parseMaterial(), plugin);
  }

  public void setBlock(World world, int x, int y, int z, ItemStack item, UnderscoreEnchants plugin) {
    setBlock(world, x, y, z, item.getType(), plugin);
  }

  public void setTime(World world, long ticks, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("setTime was called in a world " + world.getName() + " and time " + ticks + ".");
    world.setTime(ticks);
  }

  public void setTime(World world, String ticks, UnderscoreEnchants plugin) {
    setTime(world, Utils.parseL(ticks), plugin);
  }

  public void setWeather(World world, String weather, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("setWeather was called in a world " + world.getName() + " and a weather setting of " + weather + ".");
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
  public void dropItem(Player player, ItemStack item, UnderscoreEnchants plugin) {
    dropItem(getLocation(player, plugin), item, plugin);
  }

  public void dropItem(Location location, ItemStack item, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("dropItem was called in a world " + location.getWorld() + " for an item of " + item + ".");
    if (location.getWorld() != null && item != null)
      location.getWorld().dropItemNaturally(location, item);
  }
  //</editor-fold>

  //<editor-fold desc="Server">
  public void announce(String message, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("announce was called to broadcast the following message: " + message + ".");
    Bukkit.broadcastMessage(Utils.format(message));
  }
  //</editor-fold>

  //<editor-fold desc="Other">
  public int getPing(Player player, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("getPing was called for a player " + player.getName() + ".");
    return player.getPing();
  }
  public String getPingString(Player player, UnderscoreEnchants plugin) {
    return String.valueOf(getPing(player, plugin));
  }

  public String getIP(Player player, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("getIP was called for a player " + player.getName() + ".");
    return player.getAddress().getAddress().getHostAddress();
  }

  public GameMode getGameMode(Player player, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("getGameMode was called for a player " + player.getName() + ".");
    return player.getGameMode();
  }
  //</editor-fold>
  //</editor-fold>
}

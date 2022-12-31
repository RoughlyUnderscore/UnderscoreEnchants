package com.roughlyunderscore.enchs.util.general;

import com.cryptomorin.xseries.XPotion;
import com.cryptomorin.xseries.particles.XParticle;
import com.roughlyunderscore.enchs.UnderscoreEnchants;
import com.roughlyunderscore.enchs.util.Constants;
import lombok.experimental.UtilityClass;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import java.util.Objects;

@UtilityClass
@SuppressWarnings("unused")
/*
Just a lot of utils that I don't want to explain
 */
public class EntityUtils {
  
  public Location getLocation(Entity entity, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("getLocation was called on entity " + entity.getName() + " and resulted in the following location: " + entity.getLocation());
    return entity.getLocation();
  }

  public double getX(Entity entity, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("getX was called on entity " + entity.getName() + " and resulted in the following coordinate: " + getLocation(entity, plugin).getX());
    return getLocation(entity, plugin).getX();
  }

  public double getY(Entity entity, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("getY was called on entity " + entity.getName() + " and resulted in the following coordinate: " + getLocation(entity, plugin).getY());
    return getLocation(entity, plugin).getY();
  }

  public double getZ(Entity entity, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("getZ was called on entity " + entity.getName() + " and resulted in the following coordinate: " + getLocation(entity, plugin).getZ());
    return getLocation(entity, plugin).getZ();
  }

  public float getYaw(Entity entity, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("getYaw was called on entity " + entity.getName() + " and resulted in the following angle: " + getLocation(entity, plugin).getYaw());
    return getLocation(entity, plugin).getYaw();
  }

  public float getPitch(Entity entity, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("getPitch was called on entity " + entity.getName() + " and resulted in the following angle: " + getLocation(entity, plugin).getPitch());
    return getLocation(entity, plugin).getPitch();
  }

  public String getXString(Entity entity, UnderscoreEnchants plugin) {
    return String.valueOf(getX(entity, plugin));
  }

  public String getYString(Entity entity, UnderscoreEnchants plugin) {
    return String.valueOf(getY(entity, plugin));
  }

  public String getZString(Entity entity, UnderscoreEnchants plugin) {
    return String.valueOf(getZ(entity, plugin));
  }

  public String getYawString(Entity entity, UnderscoreEnchants plugin) {
    return String.valueOf(getYaw(entity, plugin));
  }

  public String getPitchString(Entity entity, UnderscoreEnchants plugin) {
    return String.valueOf(getPitch(entity, plugin));
  }

  public World getWorld(Entity entity, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("getWorld was called on entity " + entity.getName() + " and resulted in the following world: " + getLocation(entity, plugin).getWorld());
    return getLocation(entity, plugin).getWorld();
  }

  public String getWorldName(Entity entity, UnderscoreEnchants plugin) {
    return getWorld(entity, plugin).getName();
  }
  

  
  public double getMaximumHealth(Entity entityy, UnderscoreEnchants plugin) {
    if (entityy instanceof LivingEntity entity) {
      plugin.getDebugger().log("getMaximumHealth was called on entity " + entity.getName() + " and resulted in the following value: " + Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getBaseValue());
      return Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getBaseValue();
    }
    return -1;
  }

  public String getMaximumHealthString(Entity entity, UnderscoreEnchants plugin) {
    return String.valueOf(getMaximumHealth(entity, plugin));
  }

  public double getHealth(Entity entityy, UnderscoreEnchants plugin) {
    if (entityy instanceof LivingEntity entity) {
      plugin.getDebugger().log("getHealth was called on entity " + entity.getName() + " and resulted in the following value: " + entity.getHealth());
      return entity.getHealth();
    }
    return 0;
  }

  public String getHealthString(Entity entity, UnderscoreEnchants plugin) {
    return String.valueOf(getHealth(entity, plugin));
  }
  

  
  public void produceVelocity(Entity entity, double x, double y, double z, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("produceVelocity was called on entity " + entity.getName() + " with coordinates of " + x + ", " + y + " and " + z + ".");
    produceVelocity(entity, String.valueOf(x), String.valueOf(y), String.valueOf(z));
  }

  public void produceVelocity(Entity entity, String x, String y, String z) {
    entity.setVelocity(new Vector(Utils.parseD(x), Utils.parseD(y), Utils.parseD(z)));
  }

  public void produceVelocity(Entity entity, String[] parameters) {
    produceVelocity(entity, parameters[1], parameters[2], parameters[3]);
  }
  

  
  public void addPotion(Entity entityy, PotionEffect potion, UnderscoreEnchants plugin) {
    if (entityy instanceof LivingEntity entity) {
      plugin.getDebugger().log("addPotion was called on entity " + entity.getName() + " with the following potion: " + potion + ".");
      entity.addPotionEffect(potion);
    }
  }

  public void addPermanentPotion(Entity entityy, PotionEffectType effect, UnderscoreEnchants plugin) {
    if (entityy instanceof LivingEntity entity) {
      plugin.getDebugger().log("addPermanentPotion was called on entity " + entity.getName() + " with the following potion effect: " + effect + ".");
      entity.addPotionEffect(new PotionEffect(effect, 999999, 0));
    }
  }

  public void addPermanentPotion(Entity entity, XPotion effect, UnderscoreEnchants plugin) {
    addPermanentPotion(entity, effect.getPotionEffectType(), plugin);
  }

  public void addPermanentPotion(Entity entity, String effect, UnderscoreEnchants plugin) {
    addPermanentPotion(entity, XPotion.valueOf(effect).getPotionEffectType(), plugin);
  }

  public void addPermanentPotion(Entity entityy, PotionEffectType effect, int amplifier, UnderscoreEnchants plugin) {
    if (entityy instanceof LivingEntity entity) {
      plugin.getDebugger().log("addPermanentPotion was called on entity " + entity.getName() + " with effect " + effect + "and amplifier " + amplifier + ".");
      entity.addPotionEffect(new PotionEffect(effect, 999999, amplifier));
    }
  }

  public void addPermanentPotion(Entity entity, XPotion effect, int amplifier, UnderscoreEnchants plugin) {
    addPermanentPotion(entity, effect.getPotionEffectType(), amplifier, plugin);
  }

  public void addPermanentPotion(Entity entity, String effect, int amplifier, UnderscoreEnchants plugin) {
    addPermanentPotion(entity, XPotion.valueOf(effect).getPotionEffectType(), amplifier, plugin);
  }

  public void addPotion(Entity entityy, PotionEffectType effect, int ticks, UnderscoreEnchants plugin) {
    if (entityy instanceof LivingEntity entity) {
      plugin.getDebugger().log("addPermanentPotion was called on entity " + entity.getName() + " with effect " + effect + "and duration " + ticks + ".");
      entity.addPotionEffect(new PotionEffect(effect, ticks, 0));
    }
  }

  public void addPotion(Entity entity, XPotion effect, int ticks, UnderscoreEnchants plugin) {
    addPotion(entity, effect.getPotionEffectType(), ticks, 0, plugin);
  }

  public void addPotion(Entity entity, String effect, int ticks, UnderscoreEnchants plugin) {
    addPotion(entity, XPotion.valueOf(effect).getPotionEffectType(), ticks, 0, plugin);
  }

  public void addPotion(Entity entityy, PotionEffectType effect, int ticks, int amplifier, UnderscoreEnchants plugin) {
    if (entityy instanceof LivingEntity entity) {
      plugin.getDebugger().log("addPermanentPotion was called on entity " + entity.getName() + " with effect " + effect + ", duration " + ticks + "and amplifier " + amplifier + ".");
      entity.addPotionEffect(new PotionEffect(effect, ticks, amplifier));
    }
  }

  public void addPotion(Entity entity, XPotion effect, int ticks, int amplifier, UnderscoreEnchants plugin) {
    addPotion(entity, effect.getPotionEffectType(), ticks, amplifier, plugin);
  }

  public void addPotion(Entity entity, String effect, int ticks, int amplifier, UnderscoreEnchants plugin) {
    addPotion(entity, XPotion.valueOf(effect).getPotionEffectType(), ticks, amplifier, plugin);
  }

  public void removePotion(Entity entityy, PotionEffectType effect, UnderscoreEnchants plugin) {
    if (entityy instanceof LivingEntity entity) {
      plugin.getDebugger().log("removePotion was called on entity " + entity.getName() + " with the following potion effect: " + effect + ".");
      entity.removePotionEffect(effect);
    }
  }

  public void removePotion(Entity entity, XPotion effect, UnderscoreEnchants plugin) {
    removePotion(entity, effect.getPotionEffectType(), plugin);
  }

  public void removePotion(Entity entity, String effect, UnderscoreEnchants plugin) {
    removePotion(entity, XPotion.valueOf(effect).getPotionEffectType(), plugin);
  }

  public void removeBuffs(Entity entityy, UnderscoreEnchants plugin) {
    if (entityy instanceof LivingEntity entity) {
      plugin.getDebugger().log("removeBuffs was called on entity " + entity.getName() + ".");
      Constants.BUFFS.forEach(entity::removePotionEffect);
    }
  }

  public void removeDebuffs(Entity entityy, UnderscoreEnchants plugin) {
    if (entityy instanceof LivingEntity entity) {
      plugin.getDebugger().log("removeDebuffs was called on entity " + entity.getName() + ".");
      Constants.DEBUFFS.forEach(entity::removePotionEffect);
    }
  }
  

  
  public void setLocation(Entity entity, Location location, UnderscoreEnchants plugin) {
    entity.teleport(location);
    plugin.getDebugger().log("setLocation was called on entity " + entity.getName() + " with location of: " + location);
  }

  public void setLocation(Entity entity, double x, double y, double z, UnderscoreEnchants plugin) {
    setLocation(entity, x, y, z, getYaw(entity, plugin), getPitch(entity, plugin), plugin);
  }

  public void setLocation(Entity entity, double x, double y, double z, float yaw, float pitch, UnderscoreEnchants plugin) {
    setLocation(entity, new Location(getWorld(entity, plugin), x, y, z, yaw, pitch), plugin);
  }

  public void setX(Entity entity, double x, UnderscoreEnchants plugin) {
    setLocation(entity, x, getY(entity, plugin), getZ(entity, plugin), plugin);
  }

  public void increaseX(Entity entity, double adjustment, UnderscoreEnchants plugin) {
    setX(entity, getX(entity, plugin) + adjustment, plugin);
  }

  public void setY(Entity entity, double y, UnderscoreEnchants plugin) {
    setLocation(entity, getX(entity, plugin), y, getZ(entity, plugin), plugin);
  }

  public void increaseY(Entity entity, double adjustment, UnderscoreEnchants plugin) {
    setY(entity, getY(entity, plugin) + adjustment, plugin);
  }

  public void setZ(Entity entity, double z, UnderscoreEnchants plugin) {
    setLocation(entity, getX(entity, plugin), getY(entity, plugin), z, plugin);
  }

  public void increaseZ(Entity entity, double adjustment, UnderscoreEnchants plugin) {
    setZ(entity, getZ(entity, plugin) + adjustment, plugin);
  }

  public void sendForward(Entity entity, double adjustment, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("sendForward was called on entity " + entity.getName() + " with a value of " + adjustment + ".");
    Location loc = entity.getLocation().clone();
    Vector dir = loc.getDirection();
    dir.normalize();
    dir.multiply(adjustment);
    loc.add(dir);
    entity.teleport(loc);
  }

  public void sendForward(Entity entity, String adjustment, UnderscoreEnchants plugin) {
    sendForward(entity, Utils.parseD(adjustment), plugin);
  }

  public void setYaw(Entity entity, float yaw, UnderscoreEnchants plugin) {
    setLocation(entity, getX(entity, plugin), getY(entity, plugin), getX(entity, plugin), yaw, getPitch(entity, plugin), plugin);
  }

  public void increaseYaw(Entity entity, float adjustment, UnderscoreEnchants plugin) {
    setYaw(entity, getYaw(entity, plugin) + adjustment, plugin);
  }

  public void setPitch(Entity entity, float pitch, UnderscoreEnchants plugin) {
    setLocation(entity, getX(entity, plugin), getY(entity, plugin), getX(entity, plugin), getYaw(entity, plugin), pitch, plugin);
  }

  public void increasePitch(Entity entity, float adjustment, UnderscoreEnchants plugin) {
    setPitch(entity, getPitch(entity, plugin) + adjustment, plugin);
  }

  public void setDirection(Entity entity, float yaw, float pitch, UnderscoreEnchants plugin) {
    setYaw(entity, yaw, plugin);
    setPitch(entity, pitch, plugin);
  }

  public void setDirection(Entity entity, String yaw, String pitch, UnderscoreEnchants plugin) {
    setYaw(entity, Utils.parseF(yaw), plugin);
    setPitch(entity, Utils.parseF(pitch), plugin);
  }

  public void setWorld(Entity entity, World world, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("sendForward was called on entity " + entity.getName() + " with a world of " + world + ".");
    entity.teleport(world.getSpawnLocation());
  }

  public void setWorld(Entity entity, String name, UnderscoreEnchants plugin) {
    World world = (Bukkit.getWorld(name) == null ? Bukkit.createWorld(new WorldCreator(name)) : Bukkit.getWorld(name));
    setWorld(entity, Objects.requireNonNull(world), plugin);
  }

  public void spawnParticle(Entity entity, Location location, Particle particle, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("spawnParticle was called on location " + location + ", entity " + entity.getName() + " and particle " + particle.name());
    Location locatio = location.clone();
    locatio.setY(locatio.getY() + 1);
    Objects.requireNonNull(location.getWorld()).spawnParticle(particle, locatio, 1);
  }

  public void spawnParticle(Entity entity, Location location, String particle0, UnderscoreEnchants plugin) {
    spawnParticle(entity, location, XParticle.getParticle(particle0), plugin);
  }

  public void spawnParticleBoots(Entity entity, Location location, Particle particle, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("spawnParticleBoots was called on location " + location + ", entity " + entity.getName() + " and particle " + particle.name());
    Objects.requireNonNull(location.getWorld()).spawnParticle(particle, location, 1);
  }

  public void spawnParticleBoots(Entity entity, Location location, String particle0, UnderscoreEnchants plugin) {
    spawnParticleBoots(entity, location, XParticle.getParticle(particle0), plugin);
  }
  

  
  public void setMaximumHealth(Entity entityy, double health, UnderscoreEnchants plugin) {
    if (entityy instanceof LivingEntity entity) {
      plugin.getDebugger().log("setMaximumHealth was called on entity " + entity.getName() + " with a value of " + health + ".");
      Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(health);
    }
  }

  public void setMaximumHealth(Entity entity, String health, UnderscoreEnchants plugin) {
    setMaximumHealth(entity, Utils.parseD(health), plugin);
  }

  public void increaseMaximumHealth(Entity entity, double adjustment, UnderscoreEnchants plugin) {
    setMaximumHealth(entity, getMaximumHealth(entity, plugin) + adjustment, plugin);
  }

  public void increaseMaximumHealth(Entity entity, String adjustment, UnderscoreEnchants plugin) {
    increaseMaximumHealth(entity, Utils.parseD(adjustment), plugin);
  }

  public void setHealth(Entity entityy, double health, UnderscoreEnchants plugin) {
    if (entityy instanceof LivingEntity entity) {
      plugin.getDebugger().log("setHealth was called on entity " + entity.getName() + " with a value of " + health + ".");
      entity.setHealth(Math.max(0, Math.min(getMaximumHealth(entity, plugin), health))); // needs to be under the maximum value but above 0
    }
  }

  public void setHealth(Entity entity, String health, UnderscoreEnchants plugin) {
    setHealth(entity, Utils.parseD(health), plugin);
  }

  public void increaseHealth(Entity entity, double adjustment, UnderscoreEnchants plugin) {
    setHealth(entity, getHealth(entity, plugin) + adjustment, plugin);
  }

  public void increaseHealth(Entity entity, String adjustment, UnderscoreEnchants plugin) {
    increaseHealth(entity, Utils.parseD(adjustment), plugin);
  }
  

  
  public void sendProjectile(Entity entityy, Class<? extends Projectile> clazz, UnderscoreEnchants plugin) {
    if (entityy instanceof ProjectileSource entity) {
      plugin.getDebugger().log("sendProjectile was called for entity " + entityy.getName() + "with the projectile type of " + clazz.getName() + ".");
      entity.launchProjectile(clazz).setShooter(entity);
    }
  }

  public void sendArrow(Entity entity, UnderscoreEnchants plugin) {
    sendProjectile(entity, Arrow.class, plugin);
  }

  public void sendFireball(Entity entity, UnderscoreEnchants plugin) {
    sendProjectile(entity, Fireball.class, plugin);
  }
  

  
  public void setFire(Entity entity, UnderscoreEnchants plugin) {
    setFire(entity, 200, plugin);
  }

  public void setFire(Entity entity, int ticks, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("setFire was called for entity " + entity.getName() + "for " + ticks + "ticks.");
    entity.setFireTicks(ticks);
  }

  public void setFire(Entity entity, String ticks, UnderscoreEnchants plugin) {
    setFire(entity, Utils.parseI(ticks), plugin);
  }
  

  
  public boolean swimming(Entity entityy, UnderscoreEnchants plugin) {
    if (entityy instanceof LivingEntity entity) {
      plugin.getDebugger().log("boolean$swimming was called on entity " + entity.getName() + " and resulted in: " + entity.isSwimming());
      return entity.isSwimming();
    }
    return false;
  }

  public boolean onFire(Entity entity, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("onFire was called on entity " + entity.getName() + " and resulted in: " + (entity.getFireTicks() > 0));
    return entity.getFireTicks() > 0;
  }

  public boolean onTop(Entity entity, UnderscoreEnchants plugin) {
    // The Y of the entity (floored, e.g. 56.14 -> 56) must be equal or above the top block in the same location.
    boolean onTop = (int) Math.floor(getY(entity, plugin)) >= (int) Math.floor(getWorld(entity, plugin).getHighestBlockYAt(getLocation(entity, plugin)));

    plugin.getDebugger().log("onTop was called on entity " + entity.getName() + " and resulted in: " + onTop);
    return onTop;
  }

  public boolean op(Entity entity, UnderscoreEnchants plugin) {
    plugin.getDebugger().log("boolean$op was called on entity " + entity.getName() + " and resulted in: " + entity.isOp());
    return entity.isOp();
  }

  public int invisibleFor(Player player, UnderscoreEnchants plugin) {
    int time = plugin.getGods().get(player.getUniqueId());
    plugin.getDebugger().log(String.format("invisibleFor was called for player %s and returned %d.", player.getName(), time));
    if (plugin.getGods().get(player.getUniqueId()) != null) return time;
    else return 0;

  }

  public String invisibleForString(Player player, UnderscoreEnchants plugin) {
    if (plugin.getGods().get(player.getUniqueId()) != null)
      return String.valueOf(plugin.getGods().get(player.getUniqueId()));
    else return "0";
  }
  
}
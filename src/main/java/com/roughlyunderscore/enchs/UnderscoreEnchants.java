package com.roughlyunderscore.enchs;

import com.codingforcookies.armorequip.*;
import com.cryptomorin.xseries.*;
import com.roughlyunderscore.enchantsapi.UEnchantsAPI;
import com.roughlyunderscore.enchs.commands.TabComplete;
import com.roughlyunderscore.enchs.commands.UnderscoreEnchantsCommand;
import com.roughlyunderscore.enchs.enchants.EnchantmentLevel;
import com.roughlyunderscore.enchs.enchants.abstracts.*;
import com.roughlyunderscore.enchs.events.*;
import com.roughlyunderscore.enchs.enchants.Cooldown;
import com.roughlyunderscore.enchs.listeners.*;
import com.roughlyunderscore.enchs.util.Debug;
import com.roughlyunderscore.enchs.util.Pair;
import com.roughlyunderscore.enchs.util.data.DetailedEnchantment;
import com.roughlyunderscore.enchs.util.data.Messages;
import com.roughlyunderscore.enchs.util.Metrics;
import static com.roughlyunderscore.enchs.registration.Register.*;

import com.roughlyunderscore.enchs.gui.AnvilGUI;
import com.roughlyunderscore.enchs.gui.EnchantGUI;
import com.roughlyunderscore.enchs.util.general.Utils;
import de.jeff_media.updatechecker.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;
import net.milkbowl.vault.economy.*;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.configuration.file.*;
import org.bukkit.enchantments.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.*;
import org.bukkit.potion.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import static com.roughlyunderscore.enchs.parsers.PreparatoryParsers.*;
import static com.roughlyunderscore.enchs.util.general.Utils.*;

public final class UnderscoreEnchants extends JavaPlugin implements UEnchantsAPI {


	// The main class.
	// A lot of stuff in a confined place, don't trip over something!

	//<editor-fold desc="Earliest variables.">
	private UnderscoreEnchants instance;
	@Getter public Messages messages;
	//</editor-fold>

	@Getter public DetailedEnchantment EMPTY;
	public static DetailedEnchantment STATIC_EMPTY;

	//<editor-fold desc="Boring initialization of maps, lists, and generally used variables.">

	@Getter public List<PotionEffectType> positiveEffects = new ArrayList<>();
	@Getter public List<PotionEffectType> negativeEffects = new ArrayList<>();

	public static Economy econ = null;

	@Getter public Debug debugger = null;
	private static Debug staticLogger = null;

	private static BufferedWriter writer = null;

	// this is not top tier init code for sure
	public static List<Material> weaponsList = new ArrayList<>();
	public static List<Material> toolsList = new ArrayList<>();
	public static List<Material> armorList = new ArrayList<>();
	@Getter public List<Enchantment> allEnchs = new ArrayList<>();
	@Getter	public List<DetailedEnchantment> enchantmentData = new ArrayList<>();
	public static List<DetailedEnchantment> staticEnchantmentData = new ArrayList<>();

	public static Map<UUID, Integer> gods = new HashMap<>();

	public static Debug getStaticLogger() {
		return staticLogger;
	}

	public final List<Cooldown> cooldowns = new ArrayList<>();

	// too late to refactor the arrays
	// upd: Never too late!
	public static ArrayList<Enchantment> weaponEnchantments = new ArrayList<>();
	public static ArrayList<Enchantment> bowEnchantments = new ArrayList<>();
	public static ArrayList<Enchantment> toolEnchantments = new ArrayList<>();
	public static ArrayList<Enchantment> helmetEnchantments = new ArrayList<>();
	public static ArrayList<Enchantment> chestplateEnchantments = new ArrayList<>();
	public static ArrayList<Enchantment> leggingsEnchantments = new ArrayList<>();
	public static ArrayList<Enchantment> bootsEnchantments = new ArrayList<>();
	public static ArrayList<Enchantment> tridentEnchantments = new ArrayList<>();

	public static String serverVersion = Bukkit.getBukkitVersion();

	public static FileConfiguration staticConfig;
	//</editor-fold>
	private void regTest() {
		if (!serverVersion.contains("1.13") &&
			!serverVersion.contains("1.14") &&
			!serverVersion.contains("1.15") &&
			!serverVersion.contains("1.16") &&
			!serverVersion.contains("1.17") &&
			!serverVersion.contains("1.18") &&
			!serverVersion.contains("1.19")){
			throw new IllegalStateException("Couldn't start the plugin. Is your server running on <1.13? The plugin works on 1.13+ and functions properly on 1.17+.");
		}
	}


	//<editor-fold desc="Some more variable initialization (resource ID and bStats ID + the Metrics and UpdateChecker).">
	@Getter final int id = 97002;
	@Getter final int metricsId = 12413;
	@Getter @Setter	UpdateChecker checker;
	@Getter @Setter Metrics metrics;
	//</editor-fold>

	//<editor-fold desc="The enchantment parser">
	public Pair<DetailedEnchantment, AbstractEnchantment> parseEnchantment(YamlConfiguration file) {

		//<editor-fold desc="Preparatory">
		@NonNull String enchantmentName = format(file.getString("name"));
		@NonNull EnchantmentTarget target = parseTarget(file.getString("applicable"));
		@NonNull Class<? extends Event> eventString = parseEvent(file.getString("trigger"));
		List<String> conditions = file.getStringList("conditions");
		List<String> forbidOn = file.getStringList("forbid-on");
		@NonNull List<EnchantmentLevel> levelsList = getLevelsOf(file.getConfigurationSection("levels"));
		String damagerOrVictim = file.getString("player");
		boolean forDamager = false, forVictim = false, valueEmpty = true;
		if (damagerOrVictim != null) {
			valueEmpty = false;
			if (damagerOrVictim.equalsIgnoreCase("damager")) forDamager = true;
			else if (damagerOrVictim.equalsIgnoreCase("victim")) forVictim = true;
		}

		int maximumLevel = getMaxLevelOf(file.getConfigurationSection("levels"));
		int cooldownApplied = file.getInt("cooldown");

		NamespacedKey key = new NamespacedKey(instance, "underscore_enchants_" + enchantmentName.replace(" ", "__"));
		DetailedEnchantment entry = new DetailedEnchantment(key.getKey(), instance);
		AbstractEnchantment ench;
		//</editor-fold>

		//<editor-fold desc="PlayerPVPEvent">
		if (eventString.getName().equals(PlayerPVPEvent.class.getName())) {
			boolean finalForDamager = forDamager, finalForVictim = forVictim, finalValueEmpty = valueEmpty;

			ench = new PVPEnchantment(key, enchantmentName, maximumLevel, target) {
				@Override
				public void onPVP(PlayerPVPEvent event) {
					twoPlayerDamageAction(  event,
											event.getDamager(),
											event.getVictim(),
											entry,
											target,
											forbidOn,
											levelsList,
											key,
											conditions,
											finalValueEmpty,
											finalForDamager,
											finalForVictim,
											enchantmentName,
											cooldownApplied,
											instance
					);
				}
			};

		}
		//</editor-fold>
		//<editor-fold desc="ArmorEquipEvent">
		else if (eventString.getName().equals(ArmorEquipEvent.class.getName())) {
			ench = new ArmorEquipEnchantment(key, enchantmentName, maximumLevel, target) {
				@Override
				public void onEquip(ArmorEquipEvent event) {
					extraItemAction(event,
									event.getPlayer(),
									entry,
									event.getNewArmorPiece(),
									target,
									forbidOn,
									levelsList,
									key,
									conditions,
									enchantmentName,
									cooldownApplied,
									instance);
				}
			};

		}
		//</editor-fold>
		//<editor-fold desc="BlockBreakEvent">
		else if (eventString.getName().equals(BlockBreakEvent.class.getName())) {
			ench = new BlockBreakEnchantment(key, enchantmentName, maximumLevel, target) {
				@Override
				public void onBreak(BlockBreakEvent event) {
					commonAction(event,
								event.getPlayer(),
								entry,
								target,
								forbidOn,
								levelsList,
								key,
								conditions,
								enchantmentName,
								cooldownApplied,
								instance);
				}
			};

		}
		//</editor-fold>
		//<editor-fold desc="PlayerItemBreakEvent">
		else if (eventString.getName().equals(PlayerItemBreakEvent.class.getName())) {
			ench = new ItemBreakEnchantment(key, enchantmentName, maximumLevel, target) {
				@Override
				public void onBreak(PlayerItemBreakEvent event) {
					// Create the EnchantmentLevel
					commonAction(event,
						event.getPlayer(),
						entry,
						target,
						forbidOn,
						levelsList,
						key,
						conditions,
						enchantmentName,
						cooldownApplied,
						instance);
				}
			};

		}
		//</editor-fold>
		//<editor-fold desc="PlayerItemConsumeEvent">
		else if (eventString.getName().equals(PlayerItemConsumeEvent.class.getName())) {
			ench = new ItemEatEnchantment(key, enchantmentName, maximumLevel, target) {
				@Override
				public void onConsume(PlayerItemConsumeEvent event) {
					commonAction(event,
						event.getPlayer(),
						entry,
						target,
						forbidOn,
						levelsList,
						key,
						conditions,
						enchantmentName,
						cooldownApplied,
						instance);
				}
			};

		}
		//</editor-fold>
		//<editor-fold desc="PlayerInteractAtEntityEvent">
		else if (eventString.getName().equals(PlayerInteractAtEntityEvent.class.getName())) {
			ench = new RMBEntityEnchantment(key, enchantmentName, maximumLevel, target) {
				@Override
				public void onRMB(PlayerInteractAtEntityEvent event) {
					commonAction(event,
						event.getPlayer(),
						entry,
						target,
						forbidOn,
						levelsList,
						key,
						conditions,
						enchantmentName,
						cooldownApplied,
						instance);
				}
			};

		}
		//</editor-fold>
		//<editor-fold desc="PlayerInteractEvent">
		else if (eventString.getName().equals(PlayerInteractEvent.class.getName())) {
			ench = new RMBEnchantment(key, enchantmentName, maximumLevel, target) {
				@Override
				public void onRMB(PlayerInteractEvent event) {
					commonAction(event,
						event.getPlayer(),
						entry,
						target,
						forbidOn,
						levelsList,
						key,
						conditions,
						enchantmentName,
						cooldownApplied,
						instance);
				}
			};

		}
		//</editor-fold>
		//<editor-fold desc="PlayerMoveEvent">
		else if (eventString.getName().equals(PlayerMoveEvent.class.getName())) {
			ench = new MoveEnchantment(key, enchantmentName, maximumLevel, target) {
				@Override
				public void onMove(PlayerMoveEvent event) {
					commonAction(event,
						event.getPlayer(),
						entry,
						target,
						forbidOn,
						levelsList,
						key,
						conditions,
						enchantmentName,
						cooldownApplied,
						instance);
				}
			};

		}
		//</editor-fold>
		//<editor-fold desc="PlayerGotHurtEvent">
		else if (eventString.getName().equals(PlayerGotHurtEvent.class.getName())) {
			ench = new GotHurtEnchantment(key, enchantmentName, maximumLevel, target) {
				@Override
				public void onHurt(PlayerGotHurtEvent event) {
					commonAction(event,
						event.getVictim(),
						entry,
						target,
						forbidOn,
						levelsList,
						key,
						conditions,
						enchantmentName,
						cooldownApplied,
						instance);
				}
			};

		}
		//</editor-fold>
		//<editor-fold desc="PlayerHurtsEntityEvent">
		else if (eventString.getName().equals(PlayerHurtsEntityEvent.class.getName())) {
			ench = new HurtsEntityEnchantment(key, enchantmentName, maximumLevel, target) {
				@Override
				public void onAttack(PlayerHurtsEntityEvent event) {
					commonAction(event,
						event.getDamager(),
						entry,
						target,
						forbidOn,
						levelsList,
						key,
						conditions,
						enchantmentName,
						cooldownApplied,
						instance);
				}
			};

		}
		//</editor-fold>
		//<editor-fold desc="PlayerShootBowEvent">
		else if (eventString.getName().equals(PlayerShootBowEvent.class.getName())) {
			ench = new ShootBowEnchantment(key, enchantmentName, maximumLevel, target) {
				@Override
				public void onShoot(PlayerShootBowEvent event) {
					commonAction(event,
						event.getShooter(),
						entry,
						target,
						forbidOn,
						levelsList,
						key,
						conditions,
						enchantmentName,
						cooldownApplied,
						instance);
				}
			};

		}
		//</editor-fold>
		//<editor-fold desc="PlayerToggleSneakEvent">
		else if (eventString.getName().equals(PlayerToggleSneakEvent.class.getName())) {
			ench = new ToggleSneakEnchantment(key, enchantmentName, maximumLevel, target) {
				@Override
				public void onToggle(PlayerToggleSneakEvent event) {
					commonAction(event,
						event.getPlayer(),
						entry,
						target,
						forbidOn,
						levelsList,
						key,
						conditions,
						enchantmentName,
						cooldownApplied,
						instance);
				}
			};

		}
		//</editor-fold>
		//<editor-fold desc="PlayerBowHitEvent">
		else if (eventString.getName().equals(PlayerBowHitEvent.class.getName())) {
			boolean finalForDamager = forDamager, finalForVictim = forVictim, finalValueEmpty = valueEmpty;
			ench = new BowHitEnchantment(key, enchantmentName, maximumLevel, target) {
				@Override
				public void onHit(PlayerBowHitEvent event) {
					twoPlayerDamageAction(  event,
						event.getDamager(),
						event.getVictim(),
						entry,
						target,
						forbidOn,
						levelsList,
						key,
						conditions,
						finalValueEmpty,
						finalForDamager,
						finalForVictim,
						enchantmentName,
						cooldownApplied,
						instance
					);
				}
			};

		}
		//</editor-fold>
		//<editor-fold desc="Exception case">
		else { // Invalid trigger parsing
			Bukkit.getLogger().severe("Enchantment " + enchantmentName + " did not get registered - invalid trigger!");
			debugger.log("Enchantment " + enchantmentName + " did not get registered - invalid trigger!");
			return null;
		}
		//</editor-fold>

		return new Pair<>(entry, ench);
	}
	//</editor-fold>

	@SneakyThrows
	@Override
	public void onEnable() {
		//<editor-fold desc="Last initializations.">
		EMPTY = new DetailedEnchantment(this);
		STATIC_EMPTY = new DetailedEnchantment(this);
		staticEnchantmentData.addAll(enchantmentData);
		instance = this;
		messages = new Messages(this);
		staticConfig = getConfig();

		saveDefaultConfig();
		FileConfiguration config = this.getConfig();
		config.options().copyDefaults(true);
		saveDefaultConfig();
		reloadConfig();

		//</editor-fold>

		try {
			regTest();
		} catch (IllegalStateException why_the_hell_do_you_run_a_plugin_that_explicitly_says_1_17_on_its_page_on_a_server_that_is_under_1_13_question_mark) {
			onDisable();
			return;
			// this is not a malicious easter egg, I'm just straight up saving my sanity because people actually tried running it on 1.8.
		}

		//<editor-fold desc="Debug mode initialization"
		String directPath = this.getDataFolder().getPath() + File.separator + "debug";
		String debugFile = System.currentTimeMillis() + ".debug";

		File directPathFile = new File(directPath);
		if (!directPathFile.exists()) {
			directPathFile.mkdirs();
		}
		File filee = new File(directPath + File.separator + debugFile);
		if (!filee.exists()) {
			filee.createNewFile();
		}

		FileWriter fileW = new FileWriter(filee.getAbsoluteFile());
		writer = new BufferedWriter(fileW);
		debugger = new Debug(config.getBoolean("debugMode"), writer);
		staticLogger = new Debug(config.getBoolean("debugMode"), writer);
		//</editor-fold>
		//<editor-fold desc="UpdateChecker initialization.">
		checker = UpdateChecker.init(this, id)
			.setDownloadLink(id)
			.setDonationLink("https://donationalerts.com/r/zbll")
			.onFail((senders, ex) -> {
				Bukkit.getLogger().severe("Could not check for updates, make sure your connection is stable!");
				Bukkit.getLogger().severe(ex.getMessage());
			})
			.onSuccess((senders, ex) -> {
				Bukkit.getLogger().finest("Thanks for using UnderscoreEnchants!");
				Bukkit.getLogger().finest("Successfully checked for updates.");
			})
			.checkEveryXHours(getConfig().getInt("updater"))
			.setNotifyOpsOnJoin(true)
			.checkNow();
		//</editor-fold>
		//<editor-fold desc="bStats initialization if is enabled.">
		if (getConfig().getBoolean("bStats")) {
			metrics = new Metrics(this, metricsId);

			metrics.addCustomChart(new Metrics.SimplePie(
				"replaced_table_gui",
				() -> String.valueOf(getConfig().getBoolean("replace-table-gui"))
			));

			metrics.addCustomChart(new Metrics.SimplePie(
				"replaced_anvil_gui",
				() -> String.valueOf(getConfig().getBoolean("replace-anvil-gui"))
			));

			metrics.addCustomChart(new Metrics.SimplePie(
				"fireworks_launched_after_enchanting",
				() -> String.valueOf(getConfig().getBoolean("fireworks-on-enchants"))
			));

			metrics.addCustomChart(new Metrics.SimplePie(
				"language",
				() -> getConfig().getString("lang")
			));

			metrics.addCustomChart(new Metrics.SimplePie(
				"enchantments_count",
				() -> String.valueOf(allEnchs.size())
			));

		}
		//</editor-fold>

		this.init();

		//<editor-fold desc="Enchantments">
		File file0 = new File(this.getDataFolder().getPath() + File.separator + "enchantments");
		if (!file0.exists()) file0.mkdirs();

		ArrayList<File> files = new ArrayList<>(FileUtils.listFiles(file0, new String[]{"yml","yaml"}, true));
		for (File file : files)
			loadEnchantment(file, this);

		//</editor-fold>

		//<editor-fold desc="Commands initialization.">
		getCommand("underscoreenchants").setExecutor(new UnderscoreEnchantsCommand(this));
		getCommand("underscoreenchants").setTabCompleter(new TabComplete(this));
		//</editor-fold>
		//<editor-fold desc="Listeners initialization.">
		PluginManager manager = getServer().getPluginManager();
		manager.registerEvents(new InteractListener(this), this);

		manager.registerEvents(new AnvilGUI(this), this);
		manager.registerEvents(new EnchantGUI(this), this);

		manager.registerEvents(new LootPopulateListener(this), this);

		manager.registerEvents(new ArmorListener(), this);
		manager.registerEvents(new DispenserArmorListener(), this);

		manager.registerEvents(new GeneralListener(), this);

		manager.registerEvents(new JoinListener(), this);
		manager.registerEvents(new LeaveListener(), this);

		//</editor-fold>

		//<editor-fold desc="Economy setup.">
		if (!setupEconomy()) {
			Bukkit.getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		//</editor-fold>

		new BukkitRunnable() {
			public void run() {
				cooldowns.removeIf(Cooldown::decrease);
			}
		}.runTaskTimer(this, 0, 20);

		new BukkitRunnable() {
			@Override
			public void run() {
				for (Map.Entry<UUID, Integer> entry : gods.entrySet()) {
					if (entry.getValue() == 1) {
						gods.remove(entry.getKey());
						Bukkit.getPlayer(entry.getKey()).setInvulnerable(false);
					}
					else {
						gods.replace(entry.getKey(), entry.getValue() - 1);
					}
				}
			}
		}.runTaskTimer(this, 0, 1);

	}

	//<editor-fold desc="Initialization and setup methods.">
	private boolean setupEconomy() {
		if (!getConfig().getBoolean("require-vault")) return true;
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return true;
	}

	private void init() {
		weaponEnchantments.addAll(Arrays.asList(
			XEnchantment.DAMAGE_ALL.getEnchant(),
			XEnchantment.DAMAGE_ARTHROPODS.getEnchant(),
			XEnchantment.FIRE_ASPECT.getEnchant(),
			XEnchantment.KNOCKBACK.getEnchant(),
			XEnchantment.LOOT_BONUS_MOBS.getEnchant(),
			XEnchantment.DAMAGE_UNDEAD.getEnchant(),
			XEnchantment.DURABILITY.getEnchant(),
			XEnchantment.MENDING.getEnchant(),
			XEnchantment.SWEEPING_EDGE.getEnchant(),
			XEnchantment.VANISHING_CURSE.getEnchant()
		));
		bowEnchantments.addAll(Arrays.asList(
			XEnchantment.ARROW_FIRE.getEnchant(),
			XEnchantment.ARROW_DAMAGE.getEnchant(),
			XEnchantment.ARROW_KNOCKBACK.getEnchant(),
			XEnchantment.ARROW_INFINITE.getEnchant(),
			XEnchantment.DURABILITY.getEnchant(),
			XEnchantment.MENDING.getEnchant(),
			XEnchantment.VANISHING_CURSE.getEnchant()
		));
		toolEnchantments.addAll(Arrays.asList(
			XEnchantment.DURABILITY.getEnchant(),
			XEnchantment.DIG_SPEED.getEnchant(),
			XEnchantment.LOOT_BONUS_BLOCKS.getEnchant(),
			XEnchantment.SILK_TOUCH.getEnchant(),
			XEnchantment.LURE.getEnchant(),
			XEnchantment.LUCK.getEnchant(),
			XEnchantment.MENDING.getEnchant(),
			XEnchantment.VANISHING_CURSE.getEnchant()
		));
		helmetEnchantments.addAll(Arrays.asList(
			XEnchantment.WATER_WORKER.getEnchant(),
			XEnchantment.PROTECTION_EXPLOSIONS.getEnchant(),
			XEnchantment.PROTECTION_FIRE.getEnchant(),
			XEnchantment.PROTECTION_PROJECTILE.getEnchant(),
			XEnchantment.PROTECTION_ENVIRONMENTAL.getEnchant(),
			XEnchantment.OXYGEN.getEnchant(),
			XEnchantment.THORNS.getEnchant(),
			XEnchantment.DURABILITY.getEnchant(),
			XEnchantment.VANISHING_CURSE.getEnchant(),
			XEnchantment.BINDING_CURSE.getEnchant(),
			XEnchantment.MENDING.getEnchant()
		));
		chestplateEnchantments.addAll(Arrays.asList(
			XEnchantment.PROTECTION_EXPLOSIONS.getEnchant(),
			XEnchantment.PROTECTION_FIRE.getEnchant(),
			XEnchantment.PROTECTION_PROJECTILE.getEnchant(),
			XEnchantment.PROTECTION_ENVIRONMENTAL.getEnchant(),
			XEnchantment.THORNS.getEnchant(),
			XEnchantment.DURABILITY.getEnchant(),
			XEnchantment.VANISHING_CURSE.getEnchant(),
			XEnchantment.BINDING_CURSE.getEnchant(),
			XEnchantment.MENDING.getEnchant()
		));
		leggingsEnchantments.addAll(Arrays.asList(
			XEnchantment.PROTECTION_EXPLOSIONS.getEnchant(),
			XEnchantment.PROTECTION_FIRE.getEnchant(),
			XEnchantment.PROTECTION_PROJECTILE.getEnchant(),
			XEnchantment.PROTECTION_ENVIRONMENTAL.getEnchant(),
			XEnchantment.THORNS.getEnchant(),
			XEnchantment.DURABILITY.getEnchant(),
			XEnchantment.VANISHING_CURSE.getEnchant(),
			XEnchantment.BINDING_CURSE.getEnchant(),
			XEnchantment.MENDING.getEnchant()
		));
		bootsEnchantments.addAll(Arrays.asList(
			XEnchantment.DEPTH_STRIDER.getEnchant(),
			XEnchantment.PROTECTION_FALL.getEnchant(),
			XEnchantment.PROTECTION_FIRE.getEnchant(),
			XEnchantment.PROTECTION_PROJECTILE.getEnchant(),
			XEnchantment.PROTECTION_ENVIRONMENTAL.getEnchant(),
			XEnchantment.THORNS.getEnchant(),
			XEnchantment.DURABILITY.getEnchant(),
			XEnchantment.PROTECTION_EXPLOSIONS.getEnchant(),
			XEnchantment.DEPTH_STRIDER.getEnchant(),
			XEnchantment.VANISHING_CURSE.getEnchant(),
			XEnchantment.BINDING_CURSE.getEnchant(),
			XEnchantment.FROST_WALKER.getEnchant(),
			XEnchantment.MENDING.getEnchant(),
			XEnchantment.SOUL_SPEED.getEnchant()
		));

		positiveEffects.add(XPotion.DAMAGE_RESISTANCE.getPotionEffectType());
		positiveEffects.add(XPotion.FIRE_RESISTANCE.getPotionEffectType());
		positiveEffects.add(XPotion.INCREASE_DAMAGE.getPotionEffectType());
		positiveEffects.add(XPotion.ABSORPTION.getPotionEffectType());
		positiveEffects.add(XPotion.FAST_DIGGING.getPotionEffectType());
		positiveEffects.add(XPotion.HEAL.getPotionEffectType());
		positiveEffects.add(XPotion.HEALTH_BOOST.getPotionEffectType());
		positiveEffects.add(XPotion.INVISIBILITY.getPotionEffectType());
		positiveEffects.add(XPotion.CONDUIT_POWER.getPotionEffectType());
		positiveEffects.add(XPotion.HERO_OF_THE_VILLAGE.getPotionEffectType());
		positiveEffects.add(XPotion.DOLPHINS_GRACE.getPotionEffectType());
		positiveEffects.add(XPotion.GLOWING.getPotionEffectType());
		positiveEffects.add(XPotion.LUCK.getPotionEffectType());
		positiveEffects.add(XPotion.WATER_BREATHING.getPotionEffectType());
		positiveEffects.add(XPotion.SATURATION.getPotionEffectType());
		positiveEffects.add(XPotion.SPEED.getPotionEffectType());
		positiveEffects.add(XPotion.JUMP.getPotionEffectType());
		positiveEffects.add(XPotion.NIGHT_VISION.getPotionEffectType());
		positiveEffects.add(XPotion.REGENERATION.getPotionEffectType());

		XPotion.DEBUFFS.forEach(pet -> negativeEffects.add(pet.getPotionEffectType()));

		weaponsList = Arrays.asList(
			XMaterial.WOODEN_SWORD.parseMaterial(),
			XMaterial.STONE_SWORD.parseMaterial(),
			XMaterial.GOLDEN_SWORD.parseMaterial(),
			XMaterial.IRON_SWORD.parseMaterial(),
			XMaterial.DIAMOND_SWORD.parseMaterial(),
			XMaterial.NETHERITE_SWORD.parseMaterial()
		);
		toolsList = Arrays.asList(
			XMaterial.IRON_SHOVEL.parseMaterial(),
			XMaterial.IRON_PICKAXE.parseMaterial(),
			XMaterial.IRON_AXE.parseMaterial(),
			XMaterial.WOODEN_SHOVEL.parseMaterial(),
			XMaterial.WOODEN_PICKAXE.parseMaterial(),
			XMaterial.WOODEN_AXE.parseMaterial(),
			XMaterial.STONE_SHOVEL.parseMaterial(),
			XMaterial.STONE_PICKAXE.parseMaterial(),
			XMaterial.STONE_AXE.parseMaterial(),
			XMaterial.DIAMOND_SHOVEL.parseMaterial(),
			XMaterial.DIAMOND_PICKAXE.parseMaterial(),
			XMaterial.DIAMOND_AXE.parseMaterial(),
			XMaterial.GOLDEN_SHOVEL.parseMaterial(),
			XMaterial.GOLDEN_PICKAXE.parseMaterial(),
			XMaterial.GOLDEN_AXE.parseMaterial(),
			XMaterial.WOODEN_HOE.parseMaterial(),
			XMaterial.STONE_HOE.parseMaterial(),
			XMaterial.IRON_HOE.parseMaterial(),
			XMaterial.DIAMOND_HOE.parseMaterial(),
			XMaterial.GOLDEN_HOE.parseMaterial(),
			XMaterial.NETHERITE_HOE.parseMaterial(),
			XMaterial.NETHERITE_SHOVEL.parseMaterial(),
			XMaterial.NETHERITE_AXE.parseMaterial(),
			XMaterial.NETHERITE_PICKAXE.parseMaterial(),
			XMaterial.FISHING_ROD.parseMaterial(),
			XMaterial.FLINT_AND_STEEL.parseMaterial()
		);
		armorList = Arrays.asList(
			XMaterial.DIAMOND_HELMET.parseMaterial(),
			XMaterial.DIAMOND_CHESTPLATE.parseMaterial(),
			XMaterial.DIAMOND_LEGGINGS.parseMaterial(),
			XMaterial.DIAMOND_BOOTS.parseMaterial(),
			XMaterial.GOLDEN_HELMET.parseMaterial(),
			XMaterial.GOLDEN_CHESTPLATE.parseMaterial(),
			XMaterial.GOLDEN_LEGGINGS.parseMaterial(),
			XMaterial.GOLDEN_BOOTS.parseMaterial(),
			XMaterial.IRON_HELMET.parseMaterial(),
			XMaterial.IRON_CHESTPLATE.parseMaterial(),
			XMaterial.IRON_LEGGINGS.parseMaterial(),
			XMaterial.IRON_BOOTS.parseMaterial(),
			XMaterial.CHAINMAIL_HELMET.parseMaterial(),
			XMaterial.CHAINMAIL_CHESTPLATE.parseMaterial(),
			XMaterial.CHAINMAIL_LEGGINGS.parseMaterial(),
			XMaterial.CHAINMAIL_BOOTS.parseMaterial(),
			XMaterial.LEATHER_BOOTS.parseMaterial(),
			XMaterial.LEATHER_CHESTPLATE.parseMaterial(),
			XMaterial.LEATHER_LEGGINGS.parseMaterial(),
			XMaterial.LEATHER_BOOTS.parseMaterial(),
			XMaterial.NETHERITE_HELMET.parseMaterial(),
			XMaterial.NETHERITE_CHESTPLATE.parseMaterial(),
			XMaterial.NETHERITE_LEGGINGS.parseMaterial(),
			XMaterial.NETHERITE_BOOTS.parseMaterial()
		);


	}
	//</editor-fold>

	@SneakyThrows
	@Override
	@SuppressWarnings({"unchecked", "unused"})
	public void onDisable() {

		// The following block of code, starting after this string and ending on the second enhanced-for loop, is not made by me.
		// I took it from a custom enchantment tutorial, because I couldn't figure out how to unregister enchantments upon disabling the plugin.
		// However, I made sure that I now understand what this code does and can rewrite it by myself if it ever will be necessary.

		Field keyField = Enchantment.class.getDeclaredField("byKey");
		keyField.setAccessible(true);

		HashMap<NamespacedKey, Enchantment> byKey = (HashMap<NamespacedKey, Enchantment>) keyField.get(null);
		for (Enchantment enchantment : allEnchs) byKey.remove(enchantment.getKey());

		Field nameField = Enchantment.class.getDeclaredField("byName");
		nameField.setAccessible(true);

		HashMap<String, Enchantment> byName = (HashMap<String, Enchantment>) nameField.get(null);
		for (Enchantment enchantment : allEnchs) byName.remove(enchantment.getName());



		writer.close();
		if (isEnabled()) Bukkit.getPluginManager().disablePlugin(this);
	}















	// IMPLEMENTATION START

	private ItemStack enchant0(ItemStack it, Enchantment en, int lvl) {
		return Utils.enchant(it, en, lvl).getKey();
	}

	/**
	 * Disenchants an ItemStack - removes an enchantment of this name
	 *
	 * @param item the item to disenchant
	 * @param name the name of the enchantment
	 * @return the disenchanted item
	 * @throws IllegalArgumentException if the enchantment wasn't found or if the item doesn't have the given enchantment
	 */
	@Override
	public ItemStack disenchant(ItemStack item, String name) throws IllegalArgumentException {
		DetailedEnchantment enchantment = Utils.parseEnchantment(name, -1, true);
		if (enchantment == null || enchantment.equals(UnderscoreEnchants.STATIC_EMPTY)) {
			throw new IllegalArgumentException(String.format("The enchantment wasn't found! Name: %s", name));
		}

		item = disenchant(item, enchantment.getEnchantment());
		return item;
	}

	/**
	 * Disenchants an ItemStack - removes an enchantment, found by this namespaced key
	 *
	 * @param item the item to disenchant
	 * @param key  the namespaced key of the enchantment
	 * @return the disenchanted item
	 * @throws IllegalArgumentException if the enchantment wasn't found or if the item doesn't have the given enchantment
	 */
	@Override
	public ItemStack disenchant(ItemStack item, NamespacedKey key) throws IllegalArgumentException {
		DetailedEnchantment ench = new DetailedEnchantment(key);
		if (ench.getEnchantment() == null) {
			throw new IllegalArgumentException(String.format("The enchantment wasn't found! Key: %s", key.getKey()));
		}

		item = disenchant(item, ench.getEnchantment());
		return item;
	}

	/**
	 * Disenchants an ItemStack - removes a given enchantment
	 *
	 * @param item the item to disenchant
	 * @param ench the enchantment
	 * @return the disenchanted item
	 * @throws IllegalArgumentException if the item doesn't have the given enchantment
	 */
	@Override
	public ItemStack disenchant(ItemStack item, Enchantment ench) throws IllegalArgumentException {
		if (item == null || item.getType() == Material.AIR) return item;

		ItemMeta meta = item.getItemMeta();

		List<String> lore = new ArrayList<>();
		if (meta.getLore() != null) lore = meta.getLore();

		Set<Enchantment> enchantments = meta.getEnchants().keySet();
		if (enchantments.isEmpty()) return item;
		if (!enchantments.contains(ench)) throw new IllegalArgumentException("The item doesn't have the marked-for-removal enchantment!");

		meta.removeEnchant(ench);
		lore.removeIf(s -> s.toLowerCase().contains(Utils.getName(ench).toLowerCase()));

		meta.setLore(lore);
		item.setItemMeta(meta);

		return item;
	}

	/**
	 * Fully disenchants an ItemStack
	 *
	 * @param item the item to disenchant
	 * @return the disenchanted item
	 */
	@Override
	public ItemStack fullyDisenchant(ItemStack item) {
		for (Enchantment ench : item.getEnchantments().keySet()) {
			item = disenchant(item, ench);
		}

		return item;
	}

	@Override
	public ItemStack enchant(ItemStack itemStack, String name, int level) throws IllegalArgumentException {
		DetailedEnchantment enchantment = Utils.parseEnchantment(name, level, false);
		if (enchantment == null || enchantment.equals(UnderscoreEnchants.STATIC_EMPTY)) {
			throw new IllegalArgumentException(String.format("The enchantment wasn't found or the level is invalid! Name: %s, level: %d", name, level));
		}

		return enchant0(itemStack, enchantment.getEnchantment(), level);
	}

	@Override
	public ItemStack enchant(ItemStack itemStack, NamespacedKey namespacedKey, int level) throws IllegalArgumentException {
		DetailedEnchantment ench = new DetailedEnchantment(namespacedKey);
		if (ench.getEnchantment() == null) {
			throw new IllegalArgumentException(String.format("The enchantment wasn't found! Key: %s", namespacedKey.getKey()));
		}

		if (level < ench.getEnchantment().getStartLevel() || level > ench.getEnchantment().getMaxLevel()) {
			throw new IllegalArgumentException(String.format("The enchantment's level is invalid! Key: %s", namespacedKey.getKey()));
		}

		return enchant0(itemStack, ench.getEnchantment(), level);
	}

	@Override
	public ItemStack enchant(ItemStack itemStack, Enchantment enchantment, int level) throws IllegalArgumentException {
		if (level < enchantment.getStartLevel() || level > enchantment.getMaxLevel()) {
			throw new IllegalArgumentException(String.format("The enchantment's level is invalid! Name: %s", enchantment.getName()));
		}

		return enchant0(itemStack, enchantment, level);
	}

	@Override
	public void enchant(Player player, EquipmentSlot equipmentSlot, String name, int level) throws IllegalArgumentException {
		ItemStack itemStack = player.getInventory().getItem(equipmentSlot);
		itemStack = enchant(itemStack, name, level);
		if (itemStack != null) {
			player.getInventory().setItem(equipmentSlot, itemStack);
		}
	}

	@Override
	public void enchant(Player player, EquipmentSlot equipmentSlot, NamespacedKey namespacedKey, int level) throws IllegalArgumentException {
		ItemStack itemStack = player.getInventory().getItem(equipmentSlot);
		itemStack = enchant(itemStack, namespacedKey, level);
		if (itemStack != null) {
			player.getInventory().setItem(equipmentSlot, itemStack);
		}
	}

	@Override
	public void enchant(Player player, EquipmentSlot equipmentSlot, Enchantment enchantment, int level) throws IllegalArgumentException {
		ItemStack itemStack = player.getInventory().getItem(equipmentSlot);
		itemStack = enchant(itemStack, enchantment, level);
		if (itemStack != null) {
			player.getInventory().setItem(equipmentSlot, itemStack);
		}
	}




	@Override
	public ItemStack enchantUnrestricted(ItemStack itemStack, String name, int level) throws IllegalArgumentException {
		DetailedEnchantment enchantment = Utils.parseEnchantment(name, level, true);
		if (enchantment.equals(UnderscoreEnchants.STATIC_EMPTY)) {
			throw new IllegalArgumentException(String.format("The enchantment wasn't found or the level is invalid! Name: %s, level: %d", name, level));
		}

		return enchant0(itemStack, enchantment.getEnchantment(), level);
	}

	@Override
	public ItemStack enchantUnrestricted(ItemStack itemStack, NamespacedKey namespacedKey, int level) throws IllegalArgumentException {
		DetailedEnchantment ench = new DetailedEnchantment(namespacedKey);
		if (ench.getEnchantment() == null) {
			throw new IllegalArgumentException(String.format("The enchantment wasn't found! Key: %s", namespacedKey.getKey()));
		}

		return enchant0(itemStack, ench.getEnchantment(), level);
	}

	@Override
	public ItemStack enchantUnrestricted(ItemStack itemStack, Enchantment enchantment, int level) {
		return enchant0(itemStack, enchantment, level);
	}

	@Override
	public void enchantUnrestricted(Player player, EquipmentSlot equipmentSlot, String name, int level) throws IllegalArgumentException {
		ItemStack itemStack = player.getInventory().getItem(equipmentSlot);
		itemStack = enchantUnrestricted(itemStack, name, level);
		if (itemStack != null) {
			player.getInventory().setItem(equipmentSlot, itemStack);
		}
	}

	@Override
	public void enchantUnrestricted(Player player, EquipmentSlot equipmentSlot, NamespacedKey namespacedKey, int level) throws IllegalArgumentException {
		ItemStack itemStack = player.getInventory().getItem(equipmentSlot);
		itemStack = enchantUnrestricted(itemStack, namespacedKey, level);
		if (itemStack != null) {
			player.getInventory().setItem(equipmentSlot, itemStack);
		}
	}

	@Override
	public void enchantUnrestricted(Player player, EquipmentSlot equipmentSlot, Enchantment enchantment, int level) {
		ItemStack itemStack = player.getInventory().getItem(equipmentSlot);
		itemStack = enchantUnrestricted(itemStack, enchantment, level);
		if (itemStack != null) {
			player.getInventory().setItem(equipmentSlot, itemStack);
		}
	}



	// IMPLEMENTATION END

}
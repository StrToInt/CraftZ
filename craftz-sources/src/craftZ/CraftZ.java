/*
 * CraftZ
 * Copyright (C) JangoBrick <http://jangobrick.de/>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package craftZ;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import craftZ.cmd.CMD_Help;
import craftZ.cmd.CMD_Kit;
import craftZ.cmd.CMD_Kitsadmin;
import craftZ.cmd.CMD_MakeBackpack;
import craftZ.cmd.CMD_Purge;
import craftZ.cmd.CMD_Reload;
import craftZ.cmd.CMD_RemoveItems;
import craftZ.cmd.CMD_SetBorder;
import craftZ.cmd.CMD_SetLobby;
import craftZ.cmd.CMD_Sign;
import craftZ.cmd.CMD_Smasher;
import craftZ.cmd.CMD_Spawn;
import craftZ.cmd.CMD_Top;
import craftZ.cmd.CraftZCommandManager;
import craftZ.modules.*;
import craftZ.util.EntityChecker;
import craftZ.util.ItemRenamer;
import craftZ.util.Rewarder;
import craftZ.worldData.Backpack;
import craftZ.worldData.WorldData;


public class CraftZ extends JavaPlugin {
	
	public static final Random RANDOM = new Random();
	private static CraftZ instance;
	private boolean firstRun, failedWorldLoad;
	private CraftZCommandManager cmd;
	
	private List<Module> modules = new ArrayList<Module>();
	private Dynmap dynmap;
	private ChestRefiller chestRefiller;
	private ZombieSpawner zombieSpawner;
	private PlayerManager playerManager;
	private ScoreboardHelper scoreboardHelper;
	private Kits kits;
	private DeadPlayers deadPlayers;
	private VisibilityBar visibilityBar;
	private WorldBorderModule worldBorder;
	
	private long tick = 0;
	
	
	
	@Override
	public void onEnable() {
		
		instance = this;
		
		loadConfigs();
		ItemRenamer.reloadDefaultNameMap();
		
		firstRun = ConfigManager.getConfig("config").getBoolean("Config.never-ever-modify.first-run");
		if (firstRun) {
			ConfigManager.getConfig("config").set("Config.never-ever-modify.first-run", false);
			ConfigManager.saveConfig("config");
		}
		
		
		
		addModule(cmd = new CraftZCommandManager(this));
		getCommand("craftz").setExecutor(cmd);
		
		cmd.setDefault(new CMD_Help(this));
		cmd.registerCommand(new CMD_Spawn(this), "spawn");
		cmd.registerCommand(new CMD_Top(this), "top");
		cmd.registerCommand(new CMD_Kit(this), "kit");
		cmd.registerCommand(new CMD_Kitsadmin(this), "kitsadmin");
		cmd.registerCommand(new CMD_Reload(this), "reload");
		cmd.registerCommand(new CMD_SetLobby(this), "setlobby");
		cmd.registerCommand(new CMD_SetBorder(this), "setborder");
		cmd.registerCommand(new CMD_Sign(this), "sign");
		cmd.registerCommand(new CMD_RemoveItems(this), "remitems", "removeitems");
		cmd.registerCommand(new CMD_Purge(this), "purge");
		cmd.registerCommand(new CMD_Smasher(this), "smasher");
		cmd.registerCommand(new CMD_MakeBackpack(this), "makebackpack", "makebp");
		
		
		
		addModule(dynmap = new Dynmap(this));
		dynmap.unpackIcons("loot_military", "loot_military-epic", "loot_civilian", "loot_farms", "loot_industrial", "loot_barracks", "loot_medical");
		
		addModule(scoreboardHelper = new ScoreboardHelper(this));
		addModule(chestRefiller = new ChestRefiller(this));
		addModule(zombieSpawner = new ZombieSpawner(this));
		addModule(playerManager = new PlayerManager(this));
		addModule(kits = new Kits(this));
		addModule(deadPlayers = new DeadPlayers(this));
		
		
		
		addModule(new RealTimeModule(this));
		
		addModule(new WeatherModule(this));
		addModule(new ChunkModule(this));
		
		addModule(new HealthModule(this));
		addModule(new BloodbagModule(this));
		addModule(new ThirstModule(this));
		addModule(new BleedingModule(this));
		addModule(new PoisoningModule(this));
		addModule(new BoneBreakingModule(this));
		addModule(new SugarRushModule(this));
		
		addModule(new ChatModule(this));
		
		addModule(new WoodHarvestingModule(this));
		addModule(new FireplaceModule(this));
		addModule(new InventoryModule(this));
		
		addModule(new SpawnControlModule(this));
		addModule(new ZombieBehaviorModule(this));
		addModule(visibilityBar = new VisibilityBar(this));
		
		addModule(new GrenadeModule(this));
		addModule(new ZombieSmasherModule(this));
		
		addModule(new SignModule(this));
		
		addModule(new PlayerWorldProtectionModule(this));
		addModule(new NaturalWorldProtectionModule(this));
		addModule(worldBorder = new WorldBorderModule(this));
		
		addModule(new BloodParticlesModule(this));
		
		addModule(new BackpackModule(this));
		
		
		
		Bukkit.getScheduler().runTask(this, new Runnable() {
			
			@Override
			public void run() {
				
				if (world() == null) {
					severe("World '" + worldName() + "' not found! Please check config.yml. CraftZ will not work.");
					failedWorldLoad = true;
					HandlerList.unregisterAll(CraftZ.this);
				}
				
				
				
				if (firstRun) {
					addModule(new FirstTimeUseModule(CraftZ.this));
				}
				
				
				
				if (!failedWorldLoad) {
					
					WorldData.setup();
					
					for (Module m : modules)
						m.onLoad(false);
					
					int lc = chestRefiller.getChestCount();
					int ps = playerManager.getSpawnCount();
					int zs = zombieSpawner.getSpawnCount();
					
					info("Loaded " + lc + " chests, " + ps + " player spawns, " + zs + " zombie spawns");
					
					for (Entity ent : world().getEntities()) {
						MetadataValue value;
						if (ent.getType() == EntityType.DROPPED_ITEM && (value = EntityChecker.getMeta(ent, "isBlood")) != null && value.asBoolean()) {
							ent.remove();
						}
					}
					
				} else {
					FirstTimeUseModule m = getModule(FirstTimeUseModule.class);
					if (m != null)
						m.onLoad(false);
				}
				
			}
			
		});
		
		
		
		Bukkit.getScheduler().runTaskTimer(this, new Runnable() {

			@Override
			public void run() {
				
				tick++;
				
				if (!failedWorldLoad) {
					for (Module m : modules)
						m.onServerTick(tick);
				}
				
			}
			
		}, 1L, 1L);
		
		
		
		if (Rewarder.setup()) {
			info("Successfully hooked into Vault. Players can receive rewards.");
		}
		
	
		
		info("Plugin successfully enabled.");
		
	}
	
	
	
	
	@Override
	public void onDisable() {
		
		for (Module m : modules)
			m.onDisable();
		
		info("Plugin successfully disabled.");
		
	}
	
	
	
	
	
	public static CraftZ getInstance() {
		return instance;
	}
	
	
	
	
	
	public void addModule(Module module) {
		if (modules.contains(module))
			return;
		modules.add(module);
		Bukkit.getPluginManager().registerEvents(module, instance);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Module> T getModule(Class<T> clazz) {
		for (Module m : modules) {
			if (m.getClass() == clazz)
				return (T) m;
		}
		return null;
	}
	
	public List<Module> getModules() {
		return Collections.unmodifiableList(modules);
	}
	
	
	
	
	
	public ChestRefiller getChestRefiller() {
		return chestRefiller;
	}
	
	public PlayerManager getPlayerManager() {
		return playerManager;
	}
	
	public ZombieSpawner getZombieSpawner() {
		return zombieSpawner;
	}
	
	public ScoreboardHelper getScoreboardHelper() {
		return scoreboardHelper;
	}
	
	public Kits getKits() {
		return kits;
	}
	
	public DeadPlayers getDeadPlayers() {
		return deadPlayers;
	}
	
	public Dynmap getDynmap() {
		return dynmap;
	}
	
	public VisibilityBar getVisibilityBar() {
		return visibilityBar;
	}
	
	public WorldBorderModule getWorldBorder() {
		return worldBorder;
	}
	
	
	
	
	
	public CraftZCommandManager getCommandManager() {
		return cmd;
	}
	
	
	
	
	
	private void loadConfigs() {
		
		// CONFIG
		Map<String, Object> def_config = new LinkedHashMap<String, Object>();
		
		def_config.put("Config.never-ever-modify.first-run", true);
			
			// WORLD
			def_config.put("Config.world.name", "world");
			def_config.put("Config.world.real-time", true);
			
				// LOBBY
				def_config.put("Config.world.lobby.world", "world");
				def_config.put("Config.world.lobby.x", 0);
				def_config.put("Config.world.lobby.y", 64);
				def_config.put("Config.world.lobby.z", 0);
				def_config.put("Config.world.lobby.yaw", 0);
				def_config.put("Config.world.lobby.pitch", 0);
				def_config.put("Config.world.lobby.radius", 20);
				
				// BORDER
				def_config.put("Config.world.world-border.enable", true);
				def_config.put("Config.world.world-border.shape", "round");
				def_config.put("Config.world.world-border.x", 0);
				def_config.put("Config.world.world-border.z", 0);
				def_config.put("Config.world.world-border.radius", 400);
				def_config.put("Config.world.world-border.rate", 0.018);
				
				// WORLDCHANGE
				def_config.put("Config.world.world-changing.allow-burning", false);
				def_config.put("Config.world.world-changing.allow-block-grow", false);
				def_config.put("Config.world.world-changing.allow-tree-grow", false);
				def_config.put("Config.world.world-changing.allow-grass-grow", false);
				def_config.put("Config.world.world-changing.allow-new-chunks", true);
				
				// WEATHER
				def_config.put("Config.world.weather.allow-weather-changing", true);
				
			// PLAYERS
			def_config.put("Config.players.use-scoreboard-for-stats", true);
			def_config.put("Config.players.kick-on-death", false);
			def_config.put("Config.players.spawn-death-zombie", true);
			def_config.put("Config.players.send-kill-stat-messages", true);
			def_config.put("Config.players.reset-in-lobby", true);
			def_config.put("Config.players.clear-inventory-on-spawn", true);
			def_config.put("Config.players.respawn-countdown", 0);
			def_config.put("Config.players.enable-visibility-bar", true);
			def_config.put("Config.players.drop-lootchests-on-punch", true);
			
				// INVULNERABILITY
				def_config.put("Config.players.invulnerability.on-spawn", 30);
				def_config.put("Config.players.invulnerability.on-return", 3);
			
				// INTERACT
				def_config.put("Config.players.interact.shearing", false);
				def_config.put("Config.players.interact.sleeping", false);
				def_config.put("Config.players.interact.block-breaking", false);
				def_config.put("Config.players.interact.block-placing", false);
				def_config.put("Config.players.interact.placeable-blocks", new String[] { "web" });
				def_config.put("Config.players.interact.breakable-blocks", makeConfigMap(new String[] {
						"web", "brown_mushroom", "red_mushroom"
				}, new String[] {
						"shears", "all", "all"
				}));
				
				// WOOD HARVESTING
				def_config.put("Config.players.wood-harvesting.enable", true);
				def_config.put("Config.players.wood-harvesting.log-limit", 1);
				
				// CAMPFIRES
				def_config.put("Config.players.campfires.enable", true);
				def_config.put("Config.players.campfires.tick-duration", 600);
				
				// WEAPONS
				def_config.put("Config.players.weapons.grenade-enable", true);
				def_config.put("Config.players.weapons.grenade-range", 8.0);
				def_config.put("Config.players.weapons.grenade-power", 6.0);
				def_config.put("Config.players.weapons.grenade-damage-players", true);
				def_config.put("Config.players.weapons.grenade-damage-mobs", true);
				
				// MEDICAL
				def_config.put("Config.players.medical.enable-sugar-speed-effect", true);
					// BLEEDING
					def_config.put("Config.players.medical.bleeding.enable", true);
					def_config.put("Config.players.medical.bleeding.chance", 0.04);
					def_config.put("Config.players.medical.bleeding.heal-with-paper", true);
					def_config.put("Config.players.medical.bleeding.damage-interval", 200);
					// HEALING
					def_config.put("Config.players.medical.healing.heal-with-rosered", true);
					def_config.put("Config.players.medical.healing.only-healing-others", true);
					// POISONING
					def_config.put("Config.players.medical.poisoning.enable", true);
					def_config.put("Config.players.medical.poisoning.chance", 0.04);
					def_config.put("Config.players.medical.poisoning.cure-with-limegreen", true);
					def_config.put("Config.players.medical.poisoning.damage-interval", 200);
					// BONEBREAK
					def_config.put("Config.players.medical.bonebreak.enable", true);
					def_config.put("Config.players.medical.bonebreak.height", 6);
					def_config.put("Config.players.medical.bonebreak.heal-with-blazerod", true);
					// THIRST
					def_config.put("Config.players.medical.thirst.enable", true);
					def_config.put("Config.players.medical.thirst.ticks-normal", 1200);
					def_config.put("Config.players.medical.thirst.ticks-desert", 800);
					def_config.put("Config.players.medical.thirst.show-messages", true);
				
				// REWARDS
				def_config.put("Config.players.rewards.enable", false);
				def_config.put("Config.players.rewards.enable-notifications", true);
				def_config.put("Config.players.rewards.amount-kill-zombie", 10.0);
				def_config.put("Config.players.rewards.amount-kill-player", 50.0);
				def_config.put("Config.players.rewards.amount-heal-player", 30.0);
			
			// MOBS
			def_config.put("Config.mobs.blood-particles-when-damaged", true);
			def_config.put("Config.mobs.no-exp-drops", true);
			def_config.put("Config.mobs.allow-all-plugin-spawning", true);
			def_config.put("Config.mobs.completely-disable-spawn-control", false);
			
				// ZOMBIES
				def_config.put("Config.mobs.zombies.pull-players-down", true);
				def_config.put("Config.mobs.zombies.burn-in-sunlight", false);
					// SPAWNING
					def_config.put("Config.mobs.zombies.spawning.interval", 40);
					def_config.put("Config.mobs.zombies.spawning.maxzombies", 200);
					def_config.put("Config.mobs.zombies.spawning.enable-auto-spawn", false);
					def_config.put("Config.mobs.zombies.spawning.auto-spawning-interval", 40);
					
//						// ANIMALS
//						
//							// SPAWNS
//							getConfig().addDefault("Config.mobs.animals.spawning.enable", true);
//							
//								// CHANCE
//								getConfig().addDefault("Config.mobs.animals.spawning.chance.cow", 0.1);
//								getConfig().addDefault("Config.mobs.animals.spawning.chance.chicken", 0.1);
//								getConfig().addDefault("Config.mobs.animals.spawning.chance.pig", 0.1);
//								getConfig().addDefault("Config.mobs.animals.spawning.chance.sheep", 0.1);
					
			// CHAT
			def_config.put("Config.chat.completely-disable-modifications", false);
			def_config.put("Config.chat.modify-join-and-quit-messages", true);
			def_config.put("Config.chat.modify-player-messages", false);
			def_config.put("Config.chat.modify-death-messages", true);
			def_config.put("Config.chat.separate-craftz-chat", true);
			def_config.put("Config.chat.extended-error-messages", true);
			def_config.put("Config.chat.prefix", "[CraftZ]");
			
				// RANGED
				def_config.put("Config.chat.ranged.enable", false);
				def_config.put("Config.chat.ranged.range", 80);
				def_config.put("Config.chat.ranged.enable-radio", true);
				def_config.put("Config.chat.ranged.radio-channels", 10);
			
			// VEHICLES
//			def_config.put("Config.vehicles.enable", false);
//			def_config.put("Config.vehicles.speed", 5.0);
//			def_config.put("Config.vehicles.speed-street-multiplier", 1.6);
//			def_config.put("Config.vehicles.speed-street-blocks", new String[] { "wool:7", "wool", "wool:15" });
//			def_config.put("Config.vehicles.street-border-blocks", new String[] { "double_step", "step" });
			
			// ITEMNAMES
			def_config.put("Config.change-item-names.enable", true);
			def_config.put("Config.change-item-names.names.paper", "Bandage");
			def_config.put("Config.change-item-names.names.ink_sack:1", "Blood Bag");
			def_config.put("Config.change-item-names.names.ink_sack:10", "Antibiotics");
			def_config.put("Config.change-item-names.names.shears", "Toolbox");
			def_config.put("Config.change-item-names.names.ender_pearl", "Grenade");
			def_config.put("Config.change-item-names.names.blaze_rod", "Morphine Auto Injector");
			def_config.put("Config.change-item-names.names.watch", "Radio");
			
			// DYNMAP
			def_config.put("Config.dynmap.enable", true);
			def_config.put("Config.dynmap.show-lootchests", true);
			def_config.put("Config.dynmap.show-playerspawns", true);
			def_config.put("Config.dynmap.show-worldborder", true);
			
		ConfigManager.newConfig("config", instance, def_config);
		ConfigManager.getConfig("config").options().header(
				  "CraftZ: Main Configuration (config.yml)" + "\n\n"
		 		+ "If you need help, feel free to ask at the plugin's page:" + "\n"
		 		+ "http://dev.bukkit.org/bukkit-plugins/craftz/" + "\n\n"
		 		+ "Defaults + explanation: http://pastebin.com/m1E7bKHG"
		);
		
		FileConfiguration config = ConfigManager.getConfig("config");
		
		if (config.contains("Config.world.weather.allowWeatherChanging")) { // rename old value
			config.set("Config.world.weather.allow-weather-changing", config.getBoolean("Config.world.weather.allowWeatherChanging"));
			config.set("Config.world.weather.allowWeatherChanging", null);
		}
		
		Object nameso = config.get("Config.change-item-names.names");
		if (nameso != null && nameso instanceof List<?>) {
			List<?> names = (List<?>) nameso;
			for (Object o : names) {
				String s = "" + o;
				if (!s.contains("="))
					continue;
				String[] spl = s.split("=", 2);
				config.set("Config.change-item-names.names." + spl[0], spl[1]);
			}
		}
		
		config.set("Config.players.interact.allow-spiderweb-placing", null);
		
		config.set("Config.mobs.zombies.spawning.enable-mini-zombies", null);
		config.set("Config.mobs.zombies.properties", null);
		config.set("Config.mobs.zombies.baby-properties", null);
		config.set("Config.mobs.zombies.enable-drops", null);
		config.set("Config.mobs.zombies.drops", null);
		
		ConfigManager.saveConfig("config");
		
		
		
		
		
		// Messages
		Map<String, Object> def_messages = new LinkedHashMap<String, Object>();
		
		def_messages.put("Messages.harvested-tree", "A pile of wood has been successfully added to your inventory.");
		def_messages.put("Messages.already-have-wood", "You already have wood.");
		def_messages.put("Messages.isnt-a-tree", "You must be in a forest and close to a tree to harvest wood.");
		def_messages.put("Messages.destroyed-sign", "You just destroyed a CraftZ sign.");
		def_messages.put("Messages.successfully-created", "Successfully created!");
		def_messages.put("Messages.spawned", "You're at spawnpoint %s");
		def_messages.put("Messages.died", "You died! Zombies killed: %z, players killed: %p, minutes survived: %m");
		def_messages.put("Messages.bleeding", "You are bleeding! You need a bandage to mend the wounds!");
		def_messages.put("Messages.bandaged", "Your wounds are now bandaged.");
		def_messages.put("Messages.bandaged-unnecessary", "You wasted a bandage!");
		def_messages.put("Messages.bloodbag", "Your health is restored.");
		def_messages.put("Messages.poisoned", "You are poisoned! You should use antibiotics soon.");
		def_messages.put("Messages.unpoisoned", "Your poisoning is healed!");
		def_messages.put("Messages.bones-broken", "You broke your bones! You need a Morphine Auto Injector!");
		def_messages.put("Messages.bones-healed", "You used the Morphine Auto Injector successfully.");
		def_messages.put("Messages.out-of-world", "You're in a very infected area! Go back, or you will die soon!");
		def_messages.put("Messages.thirsty", "You're becoming very thirsty. Think about drinking something!");
		def_messages.put("Messages.thirsty-dehydrating", "Get something to drink, you are dehydrating!");
        def_messages.put("Messages.placed-fireplace", "You built a bright warm fireplace.");
        def_messages.put("Messages.cannot-place-fireplace", "You cannot place a fireplace here.");
        def_messages.put("Messages.radio-channel", "Selected channel: %c");
			
			// KILLED
			def_messages.put("Messages.killed.zombie", "Killed the zombie! Total zombie kills: %k");
			def_messages.put("Messages.killed.player", "Killed %p! Total player kills: %k");
			
			// REWARDS
			def_messages.put("Messages.rewards.message", "You earned %m!");
			
			// HELP
			def_messages.put("Messages.help.title", "=== CraftZ Help ===");
			def_messages.put("Messages.help.commands.help", "Display this help menu");
			def_messages.put("Messages.help.commands.spawn", "Spawn at a random point inside of the world");
			def_messages.put("Messages.help.commands.top", "Take a look at the highscores");
			def_messages.put("Messages.help.commands.kit", "Select the kit you want to spawn with");
			def_messages.put("Messages.help.commands.kitsadmin", "Create, edit, or delete kits");
			def_messages.put("Messages.help.commands.reload", "Reload the configuration files");
			def_messages.put("Messages.help.commands.setlobby", "Configure the lobby");
			def_messages.put("Messages.help.commands.setborder", "Configure the world border.");
			def_messages.put("Messages.help.commands.sign", "Get a pre-written sign");
			def_messages.put("Messages.help.commands.remitems", "Remove all items in the world");
			def_messages.put("Messages.help.commands.purge", "Purge all zombies from the world");
			def_messages.put("Messages.help.commands.smasher", "Get the zombie smasher (admin tool)");
			def_messages.put("Messages.help.commands.makebackpack", "Create a backpack for use in kits etc");
			
			// COMMAND
			def_messages.put("Messages.cmd.removed-items", "Removed %i items.");
			def_messages.put("Messages.cmd.reloaded", "Reloaded the config files.");
			def_messages.put("Messages.cmd.setlobby", "The lobby was set at your location.");
			def_messages.put("Messages.cmd.setborder", "The world border is now configured and enabled. To disable, enter '/craftz setborder disable'.");
			def_messages.put("Messages.cmd.setborder-disable", "The world border is now disabled.");
			def_messages.put("Messages.cmd.purged", "All %z loaded zombies were purged from the world.");
			def_messages.put("Messages.cmd.sign", "A pre-written sign was given to you.");
				// TOP
				def_messages.put("Messages.cmd.top.minutes-survived", "LONGEST TIME SURVIVED");
				def_messages.put("Messages.cmd.top.zombies-killed", "MOST ZOMBIE KILLS IN 1 LIFE");
				def_messages.put("Messages.cmd.top.players-killed", "MOST PLAYER KILLS IN 1 LIFE");
				// KITSADMIN
				def_messages.put("Messages.cmd.kitsadmin.kit-not-found", "The kit %k could not be found.");
				def_messages.put("Messages.cmd.kitsadmin.kit-already-exists", "The kit %k already exists.");
				def_messages.put("Messages.cmd.kitsadmin.kit-created", "The kit %k was successfully created. You can configure it with '/craftz kitsadmin %k edit'.");
				def_messages.put("Messages.cmd.kitsadmin.kit-editing", "You are now editing the kit %k. When done, enter 'done' in the chat, or 'cancel' to abort.");
				def_messages.put("Messages.cmd.kitsadmin.kit-already-editing", "You are already editing the kit %k. When done, enter 'done' in the chat, or 'cancel' to abort.");
				def_messages.put("Messages.cmd.kitsadmin.kit-edited", "Your changes to the kit %k were saved.");
				def_messages.put("Messages.cmd.kitsadmin.kit-editing-cancelled", "No changes to the kit %k were made.");
				def_messages.put("Messages.cmd.kitsadmin.kit-deleted", "The kit %k was deleted.");
			
			// ERRORS
			def_messages.put("Messages.errors.must-be-player", "You must be a player to use this command.");
			def_messages.put("Messages.errors.wrong-usage", "Wrong command usage.");
			def_messages.put("Messages.errors.sign-not-complete", "The sign is not complete.");
			def_messages.put("Messages.errors.sign-facing-wrong", "The facing direction you defined is wrong. It may be n, s, e or w.");
			def_messages.put("Messages.errors.not-enough-permissions", "You don't have the required permission to do this.");
			def_messages.put("Messages.errors.not-in-lobby", "You are too far away from the lobby.");
			def_messages.put("Messages.errors.respawn-countdown", "Please wait another %t seconds until respawning.");
			def_messages.put("Messages.errors.cmd-not-existing", "This command does not exist. Use '/craftz' to display the help.");
			def_messages.put("Messages.errors.no-player-spawns", "No player spawnpoints are defined!");
			def_messages.put("Messages.errors.player-spawn-not-found", "The spawnpoint you tried spawning at does not exist.");
			def_messages.put("Messages.errors.backpack-size-incorrect", "Backpack sizes must be multiples of 9.");
			
		ConfigManager.newConfig("messages", instance, def_messages);
		ConfigManager.getConfig("messages").options().header(
				"CraftZ: Message Localization (messages.yml)" + "\n\n"
		 	  + "If you need help, feel free to ask at the plugin's page:" + "\n"
		 	  + "http://dev.bukkit.org/bukkit-plugins/craftz/" + "\n\n"
		 	  + "Defaults + explanation: http://pastebin.com/i5nYYdT5"
		);
		
		FileConfiguration messages = ConfigManager.getConfig("messages");
		ConfigurationSection msgHelpSec = messages.getConfigurationSection("Messages.help");
		boolean changed = false;
		for (String key : msgHelpSec.getKeys(false)) {
			if (key.endsWith("-command")) {
				msgHelpSec.set(key, null);
				changed = true;
			}
		}
		if (changed) {
			ConfigManager.saveConfig("messages");
		}
		
		
		
		
		
		// LOOT
		Map<String, Object> def_loot = new LinkedHashMap<String, Object>();
			
			// SETTINGS
			def_loot.put("Loot.settings.time-before-refill", 120);
			def_loot.put("Loot.settings.min-stacks-filled", 1);
			def_loot.put("Loot.settings.max-stacks-filled", 3);
			def_loot.put("Loot.settings.despawn", true);
			def_loot.put("Loot.settings.time-before-despawn", 300);
			def_loot.put("Loot.settings.drop-on-despawn", false);
			def_loot.put("Loot.settings.max-player-vicinity", -1);
			def_loot.put("Loot.settings.despawn-on-startup", true);
			
		ConfigManager.newConfig("loot", instance, def_loot);
		ConfigManager.getConfig("loot").options().header(
				"CraftZ: Loot Setup (loot.yml)" + "\n\n"
			  + "If you need help, feel free to ask at the plugin's page:" + "\n"
			  + "http://dev.bukkit.org/bukkit-plugins/craftz/" + "\n\n"
			  + "Defaults + explanation: http://pastebin.com/K5YHS4MQ"
		);
		
		FileConfiguration loot = ConfigManager.getConfig("loot");
		if (!loot.contains("Loot.lists") || loot.getConfigurationSection("Loot.lists").getKeys(false).isEmpty()) {
			// if there are no lists at all, create defaults
			
			String[] all = {
				"web", "tnt", "2x'brown_mushroom'", "2x'red_mushroom'", "iron_axe", "flint_and_steel", "2x'apple'", "bow",
				"4x'arrow'", "iron_sword", "2x'wood_sword'", "stone_sword", "3x'bowl'", "2x'mushroom_soup'",
				"2x'wheat'", "bread",	"leather_helmet", "leather_chestplate", "leather_leggings", "leather_boots", "chainmail_helmet",
				"chainmail_chestplate", "chainmail_leggings", "chainmail_boots", "iron_helmet", "iron_chestplate", "iron_leggings", "iron_boots",
				"paper", "sugar", "cookie", "melon", "ender_pearl", "blaze_rod", "glass_bottle", "carrot_item", "baked_potato",
				"pumpkin_pie", "potion:5", "potion:16389"
			};
			loot.addDefault("Loot.lists.all", all);
			
			loot.addDefault("Loot.lists-settings.all.max-stacks-filled", 2);
			
			
			
			String[] military = {
				"web", "iron_axe", "bow", "4x'arrow'", "2x'wood_sword'", "2x'bowl'", "2x'mushroom_soup'", "3x'leather_helmet'",
				"3x'leather_chestplate'", "3x'leather_leggings'", "3x'leather_boots'"
			};
			loot.addDefault("Loot.lists.military", military);
			
			
			
			String[] militaryEpic = {
				"web", "tnt", "iron_axe", "bow", "4x'arrow'", "iron_sword", "2x'wood_sword'", "stone_sword", "2x'bowl'", "mushroom_soup",
				"2x'leather_helmet'", "2x'leather_chestplate'", "2x'leather_leggings'", "2x'leather_boots'",
				"chainmail_helmet", "chainmail_chestplate", "chainmail_leggings", "chainmail_boots",
				"iron_helmet", "iron_chestplate", "iron_leggings", "iron_boots", "ender_pearl", "<backpack:27:Czech Backpack>"
			};
			loot.addDefault("Loot.lists.military-epic", militaryEpic);
			
			loot.addDefault("Loot.lists-settings.military-epic.time-before-refill", 240);
			
			
			
			String[] civilian = {
				"2x'brown_mushroom'", "2x'red_mushroom'", "iron_axe", "flint_and_steel", "2x'apple'", "2x'arrow'", "wood_sword", "2x'bowl'",
				"mushroom_soup", "wheat", "bread", "leather_helmet", "leather_chestplate",
				"leather_leggings", "leather_boots", "cookie", "melon", "blaze_rod", "carrot_item", "baked_potato", "pumpkin_pie",
				"<backpack:9:Czech Vest Pouch>"
			};
			loot.addDefault("Loot.lists.civilian", civilian);
			
			
			
			String[] farms = {
				"3x'brown_mushroom'", "3x'red_mushroom'", "iron_axe", "flint_and_steel", "4x'apple'", "bow", "4x'arrow'", "2x'wood_sword'",
				"4x'bowl'", "2x'mushroom_soup'", "2x'wheat'", "2x'bread'", "leather_helmet", "leather_chestplate", "leather_leggings", "leather_boots",
				"sugar", "cookie", "melon", "glass_bottle", "carrot_item", "baked_potato", "pumpkin_pie"
			};
			loot.addDefault("Loot.lists.farms", farms);
			
			
			
			String[] industrial = {
				"web", "4x'arrow'", "2x'wood_sword'", "wheat"
			};
			loot.addDefault("Loot.lists.industrial", industrial);
			
			
			
			String[] barracks = {
				"2x'brown_mushroom'", "2x'red_mushroom'", "apple", "arrow", "wood_sword", "bowl", "<backpack:18:British Assault Backpack>"
			};
			loot.addDefault("Loot.lists.barracks", barracks);
			
			loot.addDefault("Loot.lists-settings.barracks.time-before-refill", 180);
			loot.addDefault("Loot.lists-settings.barracks.min-stacks-filled", 2);
			loot.addDefault("Loot.lists-settings.barracks.max-stacks-filled", 4);
			
			
			
			String[] medical = {
				"2x'apple'", "2x'bowl'", "2x'mushroom_soup'", "paper", "2x'ink_sack:1'", "ink_sack:10", "2x'sugar'", "cookie", "2x'blaze_rod'",
				"melon", "glass_bottle", "carrot_item", "2x'potion:5'", "potion:16389"
			};
			loot.addDefault("Loot.lists.medical", medical);
			
		}
		
		ConfigManager.saveConfig("loot");
		
		
		
		
		
		// HIGHSCORES
		Map<String, Object> def_highscores = new LinkedHashMap<String, Object>();
		
		ConfigManager.newConfig("highscores", instance, def_highscores);
		ConfigManager.getConfig("highscores").options().header(
				"CraftZ: Highscore Database (highscores.yml)" + "\n\n"
			  + "This file is used for internal storage and should not be edited." + "\n\n"
		 	  + "If you need help, feel free to ask at the plugin's page:" + "\n"
		 	  + "http://dev.bukkit.org/bukkit-plugins/craftz/"
		);
		
		
		
		
		
		// KITS
		Map<String, Object> def_kits = new LinkedHashMap<String, Object>();
		
		def_kits.put("Kits.settings.soulbound-label", "Soulbound");
		
		ConfigManager.newConfig("kits", instance, def_kits);
		ConfigManager.getConfig("kits").options().header(
				"CraftZ: Kits Setup (kits.yml)" + "\n\n"
			  + "The kits should be edited through the ingame commands." + "\n"
			  + "Try '/craftz kitsadmin' in the chat." + "\n\n"
			  + "If you need help, feel free to ask at the plugin's page:" + "\n"
			  + "http://dev.bukkit.org/bukkit-plugins/craftz/"
		);
		
		FileConfiguration kits = ConfigManager.getConfig("kits");
		if (!kits.contains("Kits.kits")) {
			
			kits.addDefault("Kits.kits.nothing.default", true);
			kits.addDefault("Kits.kits.nothing.items", new LinkedHashMap<Integer, ItemStack>());
			
			kits.addDefault("Kits.kits.basic.permission", "craftz.kit.basic");
			Map<String, ItemStack> kits_basic_items = new LinkedHashMap<String, ItemStack>();
			{
				kits_basic_items.put("0", new ItemStack(Material.WOOD_SWORD, 1, (short) 40));
				kits_basic_items.put("1", new ItemStack(Material.GLASS_BOTTLE, 1));
				kits_basic_items.put("2", new ItemStack(Material.COOKIE, 3));
				kits_basic_items.put("8", Backpack.createItem(9, "Coyote Patrol Pack", false));
				kits_basic_items.put("chestplate", new ItemStack(Material.LEATHER_CHESTPLATE, 1, (short) 60));
				kits_basic_items.put("leggings", new ItemStack(Material.LEATHER_LEGGINGS, 1, (short) 60));
				kits_basic_items.put("boots", new ItemStack(Material.LEATHER_BOOTS, 1, (short) 50));
			}
			kits.addDefault("Kits.kits.basic.items", kits_basic_items);
			
			ConfigManager.saveConfig("kits");
			
		}
		
		
		
		
		
		// ENEMIES
		Map<String, Object> def_enemies = new LinkedHashMap<String, Object>();
		
		ConfigManager.newConfig("enemies", this, def_enemies);
		ConfigManager.getConfig("kits").options().header(
				"CraftZ: Enemy Setup (enemies.yml)" + "\n\n"
			  + "If you need help, feel free to ask at the plugin's page:" + "\n"
			  + "http://dev.bukkit.org/bukkit-plugins/craftz/" + "\n\n"
			  + "Defaults + explanation: http://pastebin.com/Y24Ept1q"
		);
		
		FileConfiguration enemies = ConfigManager.getConfig("enemies");
		ConfigurationSection sec = enemies.getConfigurationSection("Enemies");
		if (sec == null || sec.getKeys(false).isEmpty()) {
			
			// ZOMBIE
			{
				enemies.addDefault("Enemies.zombie.default", true);
				enemies.addDefault("Enemies.zombie.type", "zombie");
					// properties
					enemies.addDefault("Enemies.zombie.properties.health", 16);
					enemies.addDefault("Enemies.zombie.properties.speed-boost", 1);
					enemies.addDefault("Enemies.zombie.properties.damage-boost", 0);
				enemies.addDefault("Enemies.zombie.use-for-auto-spawning", true);
				enemies.addDefault("Enemies.zombie.baby-chance", 0.15);
					// baby properties
					enemies.addDefault("Enemies.zombie.baby-properties.speed-boost", -1);
					enemies.addDefault("Enemies.zombie.baby-properties.damage-boost", "same");
					enemies.addDefault("Enemies.zombie.baby-properties.health", "same");
				enemies.addDefault("Enemies.zombie.drops", makeConfigMap(new String[] {
						"arrow", "2x'rotten_flesh'"
				}, new Double[] {
						0.1, 0.25
				}));
			}
			
			// PIG ZOMBIE
			{
				enemies.addDefault("Enemies.pigzombie.type", "pig_zombie");
					// properties
					enemies.addDefault("Enemies.pigzombie.properties.health", 30);
					enemies.addDefault("Enemies.pigzombie.properties.speed-boost", 0);
					enemies.addDefault("Enemies.pigzombie.properties.damage-boost", 2);
				enemies.addDefault("Enemies.pigzombie.use-for-auto-spawning", false);
				enemies.addDefault("Enemies.pigzombie.drops", makeConfigMap(new String[] {
						"arrow", "pork", "iron_helmet", "gold_sword"
				}, new Double[] {
						0.25,    0.25,   0.05,          0.1
				}));
			}
			
			ConfigManager.saveConfig("enemies");
			
		}
		
	}
	
	
	
	
	
	public void reloadConfigs() {
		
		ConfigManager.reloadConfigs();
		WorldData.reload();
		
		for (Module m : modules) {
			m.onLoad(true);
			if (dynmap.hasAccess())
				m.onDynmapEnabled(dynmap);
		}
		
		ItemRenamer.reloadDefaultNameMap();
		
		if (world() == null) {
			severe("World '" + worldName() + "' not found! Please check config.yml. CraftZ will stop.");
			failedWorldLoad = true;
			Bukkit.getPluginManager().disablePlugin(instance);
		}
		
	}
	
	
	
	
	
	public String getMsg(String path) {
		return ConfigManager.getConfig("messages").getString(path);
	}
	
	public String getPrefix() {
		String pre = ConfigManager.getConfig("config").getString("Config.chat.prefix");
		return pre.length() > 0 ? pre : "[CraftZ]";
	}
	
	
	
	
	
	public String worldName() {
		return ConfigManager.getConfig("config").getString("Config.world.name");
	}
	
	public World world() {
		return Bukkit.getWorld(worldName());
	}
	
	public boolean isWorld(String worldName) {
		return worldName.equals(worldName());
	}
	
	public boolean isWorld(World world) {
		return world.getName().equals(worldName());
	}
	
	
	
	
	
	public Set<String> getEnemyDefinitions() {
		
		ConfigurationSection all = ConfigManager.getConfig("enemies").getConfigurationSection("Enemies");
		if (all == null)
			return new HashSet<String>();
		
		return all.getKeys(false);
		
	}
	
	public ConfigurationSection getEnemyDefinition(String type) {
		
		ConfigurationSection all = ConfigManager.getConfig("enemies").getConfigurationSection("Enemies");
		if (all == null)
			return null;
		
		ConfigurationSection result = type != null && !(type = type.trim()).equals("")
				? all.getConfigurationSection(type)
				: null;
		if (result != null)
			return result;
		
		for (String key : all.getKeys(false)) {
			ConfigurationSection sec = all.getConfigurationSection(key);
			if (sec != null && (result == null || sec.getBoolean("default")))
				result = sec;
		}
		
		return result;
		
	}
	
	public List<ConfigurationSection> getAutoSpawnEnemyDefinitions() {
		
		List<ConfigurationSection> list = new ArrayList<ConfigurationSection>();
		
		ConfigurationSection all = ConfigManager.getConfig("enemies").getConfigurationSection("Enemies");
		if (all == null)
			return list;
		
		for (String key : all.getKeys(false)) {
			ConfigurationSection sec = all.getConfigurationSection(key);
			if (sec != null && sec.getBoolean("use-for-auto-spawning"))
				list.add(sec);
		}
		
		return list;
		
	}
	
	
	
	
	
	public LivingEntity spawnEnemy(ConfigurationSection sec, Location loc) {
		
		if (sec == null || loc == null)
			return null;
		
		EntityType type = EntityType.valueOf(sec.getString("type").toUpperCase());
		if (!type.isAlive())
			return null;
		
		LivingEntity ent = (LivingEntity) loc.getWorld().spawnEntity(loc, type);
		ent.setMetadata("enemyType", new FixedMetadataValue(this, sec.getName()));
		
		ent.setCanPickupItems(false);
		
		String propPre = "";
		
		if (ent instanceof Zombie) {
			
			Zombie z = (Zombie) ent;
			
			z.setVillager(false);
			
			if (CraftZ.RANDOM.nextDouble() < sec.getDouble("baby-chance", 0.15)) {
				propPre = "baby-";
				z.setBaby(true);
			} else {
				z.setBaby(false);
			}
			
		}
		
		int health = Math.max(getEnemyProperty(sec, "health", propPre), 1);
		ent.setMaxHealth(health);
		ent.setHealth(health);
		
		int speedBoost = getEnemyProperty(sec, "speed-boost", propPre);
		if (speedBoost > 0) {
			ent.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, speedBoost - 1));
		} else if (speedBoost < 0) {
			ent.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, -speedBoost - 1));
		}
		
		int damageBoost = getEnemyProperty(sec, "damage-boost", propPre);
		if (damageBoost > 0) {
			ent.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, damageBoost - 1));
		} else if (damageBoost < 0) {
			ent.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, Integer.MAX_VALUE, -damageBoost - 1));
		}
		
		return ent;
		
	}
	
	private static int getEnemyProperty(ConfigurationSection sec, String property, String prefix) {
		
		if (prefix == null) {
			return sec.getInt("properties." + property);
		}
		
		if (sec.getString(prefix + "properties." + property, "same").equalsIgnoreCase("same"))
			return sec.getInt("properties." + property);
		
		return sec.getInt(prefix + "properties." + property);
		
	}
	
	
	
	
	
	public boolean isEnemy(Entity ent) {//TODO implement checking for manually spawned entities of other types
		return ent != null && (ent.getType() == EntityType.ZOMBIE || EntityChecker.getMeta(ent, "enemyType") != null);
	}
	
	
	
	
	
	public static void info(Object msg) {
		instance.getLogger().log(Level.INFO, "" + msg);
	}
	
	public static void warn(Object msg) {
		instance.getLogger().log(Level.WARNING, "" + msg);
	}
	
	public static void severe(Object msg) {
		instance.getLogger().log(Level.SEVERE, "" + msg);
	}
	
	public static void br() {
		info("");
	}
	
	
	
	
	
	public static void broadcastToWorld(String msg, World world) {
		
		List<Player> players = world.getPlayers();
		for (int i=0; i<players.size(); i++) {
			players.get(i).sendMessage(msg);
		}
		
	}
	
	
	
	
	
	public static Location centerOfBlock(Location loc) {
		return new Location(loc.getWorld(), centerOf(loc.getBlockX()), centerOf(loc.getBlockY()), centerOf(loc.getBlockZ()));
	}
	
	public static Location centerOfBlock(World world, double x, double y, double z) {
		return centerOfBlock(new Location(world, x, y, z));
	}
	
	private static double centerOf(int coord) {
		return coord < 0 ? coord - 0.5 : coord + 0.5;
	}
	
	
	
	
	
	private static <K, V> LinkedHashMap<K, V> makeConfigMap(K[] keys, V[] values) {
		
		LinkedHashMap<K, V> map = new LinkedHashMap<K, V>();
		
		for (int i=0; i<keys.length; i++)
			map.put(keys[i], values[i]);
		
		return map;
		
	}
	
}
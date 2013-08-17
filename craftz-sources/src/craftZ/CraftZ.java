package craftZ;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;


import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import craftZ.listeners.*;
import craftZ.util.Messager;
import craftZ.util.Time;

public class CraftZ extends JavaPlugin {
	
	Time time = new Time(this);
	Messager messager = new Messager(this);
	public static int tickID = 0;
	public Map<Player, Integer> movingPlayers = new HashMap<Player, Integer>();
	
	public static CraftZ instance;
	
	
	@Override
	public void onEnable() {
		
		instance = this;
		
		//Load config part
		loadConfig();
		
		//Load event part
		registerEvents();
		
		ZombieSpawner.setup(this);
		AnimalSpawner.setup(this);
		ChestRefiller.setup(this);
		PlayerManager.setup(this);
		WorldData.setup(this);
		
		
		
		//Server tick event
		
		this.getServer().getScheduler().runTaskTimer(this, new Runnable() {

			@Override
			public void run() {
				
				tickID++;
				
				ZombieSpawner.onServerTick(tickID);
				AnimalSpawner.onServerTick(tickID);
				ChestRefiller.onServerTick(tickID);
				PlayerManager.onServerTick(tickID);
				
			}
			
		}, 1L, 1L);
		
		
		
		this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			@Override
			public void run() {
				
				if (getWorld() == null) {
					getLogger().log(Level.SEVERE, "World not found! Please check config.yml. CraftZ will shutdown now.");
					getPluginLoader().disablePlugin(instance);
				}
				
				ScoreboardHelper.setup();
				ChestRefiller.resetAllChestsAndStartRefill();
				ZombieSpawner.addSpawns();
				
			}
		});
		
	
		
		System.out.println("++===================================================++");
		System.out.println("||  [CraftZ] Visit dev.bukkit.org/server-mods/craftz ||");
		System.out.println("||  [CraftZ] Plugin successfully enabled.            ||");
		System.out.println("++===================================================++");
		
	}
	
	
	
	
	@Override
	public void onDisable() {
		
		PlayerManager.saveAllPlayersToConfig();
		
		System.out.println("++==========================================++");
		System.out.println("||  [CraftZ] Plugin successfully disabled.  ||");
		System.out.println("++==========================================++");
		
	}
	
	
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdlabel, String[] args) {
		
		if (cmd.getName().equalsIgnoreCase("craftz")) {
			
			if (args.length == 0) {
				
				if (sender.hasPermission("craftz.help") || sender.hasPermission("craftz.help")) {
					
					String msg_craftz_helptitle = ChatColor.GOLD + this.getLangConfig().getString("Messages.help.title");
					sender.sendMessage(msg_craftz_helptitle);
					sender.sendMessage("");
					
					String msg_craftz_help_main = ChatColor.YELLOW + this.getLangConfig().getString("Messages.help.help-command");
					sender.sendMessage(msg_craftz_help_main);
					
					if (sender.hasPermission("craftz.removeitems")) {
						String msg_craftz_help_rmi = ChatColor.YELLOW + this.getLangConfig().getString("Messages.help.removeitems-command");
						sender.sendMessage(msg_craftz_help_rmi);
					}
					
					if (sender.hasPermission("craftz.reload")) {
						String msg_craftz_help_rld = ChatColor.YELLOW + this.getLangConfig().getString("Messages.help.reload-command");
						sender.sendMessage(msg_craftz_help_rld);
					}
					
					if (sender.hasPermission("craftz.spawn")) {
						String msg_craftz_help_spawn = ChatColor.YELLOW + this.getLangConfig().getString("Messages.help.spawn-command");
						sender.sendMessage(msg_craftz_help_spawn);
					}
					
					if (sender.hasPermission("craftz.setlobby")) {
						String msg_craftz_help_setlobby = ChatColor.YELLOW + this.getLangConfig().getString("Messages.help.setlobby-command");
						sender.sendMessage(msg_craftz_help_setlobby);
					}
					
				} else {
					String value_notEnoughPerms = ChatColor.DARK_RED + this.getLangConfig()
							.getString("Messages.errors.not-enough-permissions");
					sender.sendMessage(value_notEnoughPerms);
				}
				
				return true;
				
			}
			
			
			
			
			if (args.length > 0) {
								
				if (args[0].equalsIgnoreCase("reload")) {
					
					if (sender.hasPermission("craftz.reload")) {
						
						this.reloadConfigs();
						String value_cmd_reloaded = ChatColor.GREEN + this.getLangConfig().getString("Messages.cmd.reloaded");
						sender.sendMessage(value_cmd_reloaded);
						
					} else {
						String value_notEnoughPerms = ChatColor.DARK_RED + this.getLangConfig()
								.getString("Messages.errors.not-enough-permissions");
						sender.sendMessage(value_notEnoughPerms);
					}
					
				}
				
				
				
				if (args[0].equalsIgnoreCase("removeitems") || args[0].equalsIgnoreCase("remitems")) {
					
					if (sender.hasPermission("craftz.removeitems")) {
						
						int craftz_removed_items = 0;
						String value_world_name = this.getConfig().getString("Config.world.name");
						List<Entity> craftz_entities = this.getServer().getWorld(value_world_name).getEntities();
						for (int i=0; i<craftz_entities.toArray().length; i++) {
							Entity craftz_entity_ = craftz_entities.get(i);
							if (craftz_entity_.getType() == EntityType.DROPPED_ITEM) {
								craftz_entity_.remove();
								craftz_removed_items++;
							}
						}
						
						String msg_cmd_removedItems = ChatColor.GREEN + this.getLangConfig().getString("Messages.cmd.removed-items");
						msg_cmd_removedItems = msg_cmd_removedItems.replaceAll("%i", "" + ChatColor.AQUA + craftz_removed_items + ChatColor.GREEN);
						sender.sendMessage("[CraftZ] " + msg_cmd_removedItems);
						
					} else {
						String value_notEnoughPerms = ChatColor.DARK_RED + this.getLangConfig()
								.getString("Messages.errors.not-enough-permissions");
						sender.sendMessage(value_notEnoughPerms);
					}
					
				}
				
				
				
				if (args[0].equalsIgnoreCase("spawn")) {
					
					if (!(sender instanceof Player)) {
						return true;
					}
					
					Player p = (Player) sender;
					
					if (p.hasPermission("craftz.spawn")) {
						
						if (PlayerManager.isInsideOfLobby(p)) {
							PlayerManager.loadPlayer(p);
						}
						
					} else {
						String value_notEnoughPerms = ChatColor.DARK_RED + this.getLangConfig()
								.getString("Messages.errors.not-enough-permissions");
						p.sendMessage(value_notEnoughPerms);
					}
					
				}
				
				
				
				if (args[0].equalsIgnoreCase("setlobby")) {
					
					if (!(sender instanceof Player)) {
						return true;
					}
					
					Player p = (Player) sender;
					
					if (p.hasPermission("craftz.setlobby")) {
						
						int x = p.getLocation().getBlockX();
						int y = p.getLocation().getBlockY();
						int z = p.getLocation().getBlockZ();
						
						getConfig().set("Config.world.lobby.x", x);
						getConfig().set("Config.world.lobby.y", y);
						getConfig().set("Config.world.lobby.z", z);
						
						saveConfig();
						
					} else {
						String value_notEnoughPerms = ChatColor.DARK_RED + this.getLangConfig()
								.getString("Messages.errors.not-enough-permissions");
						p.sendMessage(value_notEnoughPerms);
					}
					
				}
				
				return true;
					
			}
			
		}
			
		
		
		return true;
		
	}
	
	
	
	
	
	private void registerEvents() {
		
		// PLAYER
		new PlayerInteractListener(this);
		new PlayerItemConsumeListener(this);
		new PlayerJoinListener(this);
		new PlayerQuitListener(this);
		new ShearEntityListener(this);
		new PlayerMoveListener(this);
		new PlayerBedEnterListener(this);
		new AsyncPlayerChatListener(this);
		new PlayerDeathListener(this);
		new EntityShootBowListener(this);
		new EntityCreatePortalListener(this);
		new PlayerCommandPreprocessListener(this);
		new FoodLevelChangeListener(this);
		new PlayerChangedWorldListener(this);
		new PlayerTeleportListener(this);
		
		// INVENTORY
		new PlayerDropItemListener(this);
		new PlayerPickupItemListener(this);
		new InventoryCloseListener(this);
		new InventoryClickListener(this);
		
		// CREATURE
		new CreatureSpawnListener(this);
		new EntityDamageByEntityListener(this);
		new EntityDamageListener(this);
		new EntityDeathListener(this);
		new SheepDyeWoolListener(this);
		new EntityRegainHealthListener(this);
		
		// ITEM
		new ItemDespawnListener(this);
		
		// ENTITY
		new ProjectileHitListener(this);
		new EntityExplodeListener(this);
		new VehicleUpdateListener(this);
		new VehicleBlockCollisionListener(this);
		new VehicleMoveListener(this);
		
		// BLOCK
		new BlockBreakListener(this);
		new BlockPlaceListener(this);
		new HangingBreakListener(this);
		new HangingBreakByEntityListener(this);
		new HangingPlaceListener(this);
		new BlockIgniteListener(this);
		new BlockBurnListener(this);
		new BlockGrowListener(this);
		new StructureGrowListener(this);
		new BlockSpreadListener(this);
		new SignChangeListener(this);
		
		// WORLD
		new WeatherChangeListener(this);
		new ChunkLoadListener(this);
		
	}
	
	
	
	
	
	private void loadConfig() {
		
		reloadLangConfig();
		reloadLootConfig();
		
		// SET HEADER
		
		this.getConfig().options().header(  "++===================================================++"
								 		+ "\n|| Configuration for the CraftZ plugin by JangoBrick ||"
								 		+ "\n++===================================================++"
		);
		
		// CONFIG
		
			// WORLD
			
			String path_world_name = "Config.world.name";
			this.getConfig().addDefault(path_world_name, "world");
			
			String path_world_lobby_radius = "Config.world.lobby.radius";
			this.getConfig().addDefault(path_world_lobby_radius, 20);
			
			String path_world_lobby_x = "Config.world.lobby.x";
			this.getConfig().addDefault(path_world_lobby_x, 0);
			
			String path_world_lobby_y = "Config.world.lobby.y";
			this.getConfig().addDefault(path_world_lobby_y, 64);
			
			String path_world_lobby_z = "Config.world.lobby.z";
			this.getConfig().addDefault(path_world_lobby_z, 0);
			
			String path_world_realtime = "Config.world.real-time";
			this.getConfig().addDefault(path_world_realtime, true);
			
			String path_worldborder_enable = "Config.world.world-border.enable";
			this.getConfig().addDefault(path_worldborder_enable, true);
			
			String path_worldborder_radius = "Config.world.world-border.radius";
			this.getConfig().addDefault(path_worldborder_radius, 400);
				
				// WORLDCHANGE
				
				String path_worldchange_allowBlockBurning = "Config.world.world-changing.allow-burning";
				this.getConfig().addDefault(path_worldchange_allowBlockBurning, false);
				
				String path_worldchange_allowBlockGrow = "Config.world.world-changing.allow-block-grow";
				this.getConfig().addDefault(path_worldchange_allowBlockGrow, false);
				
				String path_worldchange_allowTreeGrow = "Config.world.world-changing.allow-tree-grow";
				this.getConfig().addDefault(path_worldchange_allowTreeGrow, false);
				
				String path_worldchange_allowGrassGrow = "Config.world.world-changing.allow-grass-grow";
				this.getConfig().addDefault(path_worldchange_allowGrassGrow, false);
				
				String path_worldchange_allowNewChunks = "Config.world.world-changing.allow-new-chunks";
				this.getConfig().addDefault(path_worldchange_allowNewChunks, true);
				
				// WEATHER
				
				String path_weatherChanging_allow = "Config.world.weather.allowWeatherChanging";
				this.getConfig().addDefault(path_weatherChanging_allow, true);
				
			// PLAYERS
				
				String path_use_scoreboard = "Config.players.use-scoreboard-for-stats";
				this.getConfig().addDefault(path_use_scoreboard, false);
			
				// INTERACT
				
				String path_shearing_allow = "Config.players.interact.shearing";
				this.getConfig().addDefault(path_shearing_allow, false);
				
				String path_sleeping_allow = "Config.players.interact.sleeping";
				this.getConfig().addDefault(path_sleeping_allow, false);
				
					// BLOCKS
					
					String path_blockBreaking_allow = "Config.players.interact.block-breaking";
					this.getConfig().addDefault(path_blockBreaking_allow, false);
					
					String path_blockPlacing_allow = "Config.players.interact.block-placing";
					this.getConfig().addDefault(path_blockPlacing_allow, false);
					
					String path_blockPlacing_spiderweb_allow = "Config.players.interact.allow-spiderweb-placing";
					this.getConfig().addDefault(path_blockPlacing_spiderweb_allow, true);
				
				// MEDICAL
				
				String path_enableSugarEffect = "Config.players.medical.enable-sugar-speed-effect";
				this.getConfig().addDefault(path_enableSugarEffect, true);
				
				String path_bleeding_enable = "Config.players.medical.bleeding.enable";
				this.getConfig().addDefault(path_bleeding_enable, true);
				
				String path_bleeding_chance = "Config.players.medical.bleeding.chance";
				this.getConfig().addDefault(path_bleeding_chance, 0.04);
				
				String path_bleeding_healWithPaper = "Config.players.medical.bleeding.heal-with-paper";
				this.getConfig().addDefault(path_bleeding_healWithPaper, true);
				
				String path_healing_healWithRoseRed = "Config.players.medical.healing.heal-with-rosered";
				this.getConfig().addDefault(path_healing_healWithRoseRed, true);
				
				String path_healing_onlyHealingOthers = "Config.players.medical.healing.only-healing-others";
				this.getConfig().addDefault(path_healing_onlyHealingOthers, true);
				
				String path_poisoning_enable = "Config.players.medical.poisoning.enable";
				this.getConfig().addDefault(path_poisoning_enable, true);
				
				String path_poisoning_chance = "Config.players.medical.poisoning.chance";
				this.getConfig().addDefault(path_poisoning_chance, 0.04);
				
				String path_poisoning_healWithLimeGreen = "Config.players.medical.poisoning.cure-with-limegreen";
				this.getConfig().addDefault(path_poisoning_healWithLimeGreen, true);
			
			// MOBS
				
				String path_mobs_blood = "Config.mobs.blood-particles-when-damaged";
				this.getConfig().addDefault(path_mobs_blood, true);
			
				// ZOMBIES
				
					// DROPS
					
					String path_zombies_drops_enable = "Config.mobs.zombies.enable-drops";
					this.getConfig().addDefault(path_zombies_drops_enable, true);
					
					String path_zombies_drops_chance = "Config.mobs.zombies.drops.chance";
					this.getConfig().addDefault(path_zombies_drops_chance, 0.3);
					
//					String path_zombies_drops_rf = "Config.mobs.zombies.drops.rottenflesh";
//					this.getConfig().addDefault(path_zombies_drops_rf, true);
//					
//					String path_zombies_drops_arrows = "Config.mobs.zombies.drops.arrows";
//					this.getConfig().addDefault(path_zombies_drops_arrows, true);
					
					String path_zombies_drops_items = "Config.mobs.zombies.drops.items";
					String[] value_zombies_drops_items = { "262", "2x367" };
					this.getConfig().addDefault(path_zombies_drops_items, value_zombies_drops_items);
					
					// SPAWNING
					
					String path_zombies_spawninterval = "Config.mobs.zombies.spawning.interval";
					this.getConfig().addDefault(path_zombies_spawninterval, 40);
					
					String path_zombies_maxzombies = "Config.mobs.zombies.spawning.maxzombies";
					this.getConfig().addDefault(path_zombies_maxzombies, 200);
					
					String path_zombies_autospawn = "Config.mobs.zombies.spawning.enable-auto-spawn";
					this.getConfig().addDefault(path_zombies_autospawn, false);
					
					String path_zombies_autointerval = "Config.mobs.zombies.spawning.auto-spawning-interval";
					this.getConfig().addDefault(path_zombies_autointerval, 40);
				
				// ANIMALS
				
					// SPAWNS
					
					String path_animalspawns_enable = "Config.mobs.animals.spawning.enable";
					this.getConfig().addDefault(path_animalspawns_enable, true);
					
						// CHANCE
						
						String path_animalspawns_chance_cow = "Config.mobs.animals.spawning.chance.cow";
						this.getConfig().addDefault(path_animalspawns_chance_cow, 0.1);
						
						String path_animalspawns_chance_chicken = "Config.mobs.animals.spawning.chance.chicken";
						this.getConfig().addDefault(path_animalspawns_chance_chicken, 0.1);
						
						String path_animalspawns_chance_pig = "Config.mobs.animals.spawning.chance.pig";
						this.getConfig().addDefault(path_animalspawns_chance_pig, 0.1);
						
						String path_animalspawns_chance_sheep = "Config.mobs.animals.spawning.chance.sheep";
						this.getConfig().addDefault(path_animalspawns_chance_sheep, 0.1);
					
			// CHAT
		
			String path_modifyJoinQuitMessages = "Config.chat.modify-join-and-quit-messages";
			this.getConfig().addDefault(path_modifyJoinQuitMessages, true);
			
			String path_modifyPlayerMessages = "Config.chat.modify-player-messages";
			this.getConfig().addDefault(path_modifyPlayerMessages, false);
			
			String path_modifyDeathMessages = "Config.chat.modify-death-messages";
			this.getConfig().addDefault(path_modifyDeathMessages, true);
			
			// VEHICLES
			
			String path_vehicles_enable = "Config.vehicles.enable";
			this.getConfig().addDefault(path_vehicles_enable, false);
			
			String path_vehicles_speed = "Config.vehicles.speed";
			this.getConfig().addDefault(path_vehicles_speed, 5.0);
			
			String path_vehicles_speed_streetMulti = "Config.vehicles.speed-street-multiplier";
			this.getConfig().addDefault(path_vehicles_speed_streetMulti, 1.6);
			
			String path_vehicles_speed_streetBlocks = "Config.vehicles.speed-street-blocks";
			String[] value_vehicles_speed_streetBlocks = { "35:7", "35", "35:15" };
			this.getConfig().addDefault(path_vehicles_speed_streetBlocks, value_vehicles_speed_streetBlocks);
			
			String path_vehicles_streetBorderBlocks = "Config.vehicles.street-border-blocks";
			String[] value_vehicles_streetBorderBlocks = { "43", "44" };
			this.getConfig().addDefault(path_vehicles_streetBorderBlocks, value_vehicles_streetBorderBlocks);
			
			// ITEMNAMES
			
			String path_changeItemnames_enable = "Config.change-item-names.enable";
			this.getConfig().addDefault(path_changeItemnames_enable, true);
			
			String path_changeItemnames_names = "Config.change-item-names.names";
			String[] value_changeItemnames_names = { "339=Bandage", "351:1=Blood Bag", "351:10=Antibiotics",
					"368=Grenade" };
			this.getConfig().addDefault(path_changeItemnames_names, value_changeItemnames_names);
			
			
			
		
		
		
		
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();
		
	}
	
	
	
	
	private void loadLangConfig() {
		
		// SET HEADER
		
		this.getLangConfig().options().header("++==============================================++"
								 		  + "\n|| Messages for the CraftZ plugin by JangoBrick ||"
								 		  + "\n++==============================================++"
		);
		
		// MESSAGES
		
		String path_harvestedTree = "Messages.harvested-tree";
		this.getLangConfig().addDefault(path_harvestedTree, "A pile of wood has been successfully added to your inventory.");
		
		String path_alreadyHaveWood = "Messages.already-have-wood";
		this.getLangConfig().addDefault(path_alreadyHaveWood, "You already have wood.");
		
		String path_isntTree = "Messages.isnt-a-tree";
		this.getLangConfig().addDefault(path_isntTree, "You must be in a forest and close to a tree to harvest wood.");
		
		String path_destroyedSign = "Messages.destroyed-sign";
		this.getLangConfig().addDefault(path_destroyedSign, "You just destroyed a CraftZ sign.");
		
		String path_successfullyCreated = "Messages.successfully-created";
		this.getLangConfig().addDefault(path_successfullyCreated, "Successfully created!");
		
		String path_spawned = "Messages.spawned";
		this.getLangConfig().addDefault(path_spawned, "You're at spawnpoint %s");
		
		String path_died = "Messages.died";
		this.getLangConfig().addDefault(path_died, "You died! Zombies killed: %z, players killed: %p, minutes survived: %m");
		
		String path_bleeding = "Messages.bleeding";
		this.getLangConfig().addDefault(path_bleeding, "You are bleeding! You need a bandage to mend the wounds!");
		
		String path_bandaged = "Messages.bandaged";
		this.getLangConfig().addDefault(path_bandaged, "Your wounds are now bandaged.");
		
		String path_bloodbag = "Messages.bloodbag";
		this.getLangConfig().addDefault(path_bloodbag, "Your health is restored.");
		
		String path_poisoned = "Messages.poisoned";
		this.getLangConfig().addDefault(path_poisoned, "You are poisoned! You should use antibiotics soon.");
		
		String path_unpoisoned = "Messages.unpoisoned";
		this.getLangConfig().addDefault(path_unpoisoned, "Your poisoning is healed!");
		
		String path_outOfWorld = "Messages.out-of-world";
		this.getLangConfig().addDefault(path_outOfWorld, "You're in a very infected area! Go back, or you will die soon!");
		
		String path_killed_zombie = "Messages.killed.zombie";
		this.getLangConfig().addDefault(path_killed_zombie, "Killed the zombie! Total zombie kills: %k");
		
		String path_killed_player = "Messages.killed.player";
		this.getLangConfig().addDefault(path_killed_player, "Killed %p! Total player kills: %k");
		
			// HELP
			
			String path_craftz_helptitle = "Messages.help.title";
			this.getLangConfig().addDefault(path_craftz_helptitle, "=== CraftZ Help ===");
			
			String path_craftz_help_main = "Messages.help.help-command";
			this.getLangConfig().addDefault(path_craftz_help_main, "/craftz: Displays this help menu.");
			
			String path_craftz_help_rmi = "Messages.help.removeitems-command";
			this.getLangConfig().addDefault(path_craftz_help_rmi, "/craftz removeitems: Removes all items in the world. (Alias: /craftz remitems)");
			
			String path_craftz_help_rld = "Messages.help.reload-command";
			this.getLangConfig().addDefault(path_craftz_help_rld, "/craftz reload: Reload the configuration files.");
			
			String path_craftz_help_spawn = "Messages.help.spawn-command";
			this.getLangConfig().addDefault(path_craftz_help_spawn, "/craftz spawn: Spawn at a random point inside of the world.");
			
			String path_craftz_help_setlobby = "Messages.help.setlobby-command";
			this.getLangConfig().addDefault(path_craftz_help_setlobby, "/craftz setlobby: Set the lobby location where you're standing.");
			
			// COMMAND
			
			String path_cmd_removedItems = "Messages.cmd.removed-items";
			this.getLangConfig().addDefault(path_cmd_removedItems, "Removed %i items.");
			
			String path_cmd_reloaded = "Messages.cmd.reloaded";
			this.getLangConfig().addDefault(path_cmd_reloaded, "Reloaded the config files.");
			
			// ERRORS
		
			String path_error_mustBePlayer = "Messages.errors.mustBePlayer";
			this.getLangConfig().addDefault(path_error_mustBePlayer, "You must be a player to use this command.");
			
			String path_error_tooFewArgs = "Messages.errors.tooFewArguments";
			this.getLangConfig().addDefault(path_error_tooFewArgs, "Too few arguments given.");
			
			String path_error_signNotComplete = "Messages.errors.sign-not-complete";
			this.getLangConfig().addDefault(path_error_signNotComplete, "The sign is not complete.");
			
			String path_error_notEnoughPerms = "Messages.errors.not-enough-permissions";
			this.getLangConfig().addDefault(path_error_notEnoughPerms, "You don't have the required permission to do this.");
		
		
		
		
		this.getLangConfig().options().copyDefaults(true);
		this.saveLangConfig();
		
	}
	
	
	
	private FileConfiguration langConfig = null;
	private File langConfigFile = null;
	
	public void reloadLangConfig() {
		
		if (langConfigFile == null) {
			langConfigFile = new File(this.getDataFolder(), "messages.yml");
		}
		
		langConfig = YamlConfiguration.loadConfiguration(langConfigFile);
		
		loadLangConfig();
		
	}
	
	public FileConfiguration getLangConfig() {
		if (langConfig == null) {
			this.reloadLangConfig();
		}
		
		return langConfig;
	}
	
	public void saveLangConfig() {
		if (langConfig == null || langConfigFile == null) {
			return;
		}
		
		try {
			getLangConfig().save(langConfigFile);
		} catch (IOException ex) {
			this.getLogger().log(Level.SEVERE, "Could not save config to " + langConfigFile, ex);
		}
	}
	
	
	
	
	
	private void loadLootConfig() {
		
		// SET HEADER
		
		this.getLootConfig().options().header("++==========================================++"
								 		  + "\n|| Loot for the CraftZ plugin by JangoBrick ||"
								 		  + "\n++==========================================++"
		);
		
		
		
		// LOOT
			
			// SETTINGS
			
			String path_settings_refillTime = "Loot.settings.time-before-refill";
			this.getLootConfig().addDefault(path_settings_refillTime, 120);
			
			String path_settings_minStacksFilled = "Loot.settings.min-stacks-filled";
			this.getLootConfig().addDefault(path_settings_minStacksFilled, 1);
			
			String path_settings_maxStacksFilled = "Loot.settings.max-stacks-filled";
			this.getLootConfig().addDefault(path_settings_maxStacksFilled, 3);
			
			// LISTS
			
			String path_lists_all = "Loot.lists.all";
			String[] value_lists_all = {
				"30", "46", "2x39", "2x40", "258", "259", "2x260", "261", "4x262", "267", "2x268", "272", "3x281", "2x282",
				"2x296", "297",	"298", "299", "300", "301", "302", "303", "304", "305", "306", "307", "308", "309", "339",
				"346", "353", "357", "360", "368", "374", "391", "393", "400", "373:5", "373:16389"
			};
			this.getLootConfig().addDefault(path_lists_all, value_lists_all);
			
			
			
			String path_lists_military = "Loot.lists.military";
			String[] value_lists_military = {
				"30", "258", "261", "4x262", "2x268", "2x281", "2x282", "3x298", "3x299", "3x300", "3x301"
			};
			this.getLootConfig().addDefault(path_lists_military, value_lists_military);
			
			
			
			String path_lists_militaryEpic = "Loot.lists.military-epic";
			String[] value_lists_militaryEpic = {
				"30", "46", "258", "261", "4x262", "267", "2x268", "272", "2x281", "282", "2x298", "2x299", "2x300",
				"2x301", "302", "303", "304", "305", "306", "307", "308", "309", "368"
			};
			this.getLootConfig().addDefault(path_lists_militaryEpic, value_lists_militaryEpic);
			
			
			
			String path_lists_civilian = "Loot.lists.civilian";
			String[] value_lists_civilian = {
				"2x39", "2x40", "258", "259", "2x260", "2x262", "268", "2x281", "282", "296", "297", "298", "299",
				"300", "301", "346", "357", "360", "391", "393", "400"
			};
			this.getLootConfig().addDefault(path_lists_civilian, value_lists_civilian);
			
			
			
			String path_lists_farms = "Loot.lists.farms";
			String[] value_lists_farms = {
				"3x39", "3x40", "258", "259", "4x260", "261", "4x262", "2x268", "4x281", "2x282", "2x296", "2x297",
				"298", "299", "300", "301", "346", "353", "357", "360", "374", "391", "393", "400"
			};
			this.getLootConfig().addDefault(path_lists_farms, value_lists_farms);
			
			
			
			String path_lists_industrial = "Loot.lists.industrial";
			String[] value_lists_industrial = {
				"30", "4x262", "2x268", "296"
			};
			this.getLootConfig().addDefault(path_lists_industrial, value_lists_industrial);
			
			
			
			String path_lists_barracks = "Loot.lists.barracks";
			String[] value_lists_barracks = {
				"2x39", "2x40", "260", "262", "268", "281"
			};
			this.getLootConfig().addDefault(path_lists_barracks, value_lists_barracks);
			
			
			
			String path_lists_medical = "Loot.lists.medical";
			String[] value_lists_medical = {
				"2x260", "2x281", "2x282", "339", "2x351:1", "351:10", "2x353", "357", "360", "374", "391",
				"2x373:5", "373:16389"
			};
			this.getLootConfig().addDefault(path_lists_medical, value_lists_medical);
		
		
		
		this.getLootConfig().options().copyDefaults(true);
		this.saveLootConfig();
		
	}
	
	
	
	private FileConfiguration lootConfig = null;
	private File lootConfigFile = null;
	
	public void reloadLootConfig() {
		
		if (lootConfigFile == null) {
			lootConfigFile = new File(this.getDataFolder(), "loot.yml");
		}
		
		lootConfig = YamlConfiguration.loadConfiguration(lootConfigFile);
		
		loadLootConfig();
		
	}
	
	public FileConfiguration getLootConfig() {
		if (lootConfig == null) {
			this.reloadLootConfig();
		}
		
		return lootConfig;
	}
	
	public void saveLootConfig() {
		if (lootConfig == null || lootConfigFile == null) {
			return;
		}
		
		try {
			getLootConfig().save(lootConfigFile);
		} catch (IOException ex) {
			this.getLogger().log(Level.SEVERE, "Could not save config to " + lootConfigFile, ex);
		}
	}
	
	
	
	
	public void reloadConfigs() {
		
		reloadConfig();
		loadConfig();
		
	}
	
	
	
	
	
	public World getWorld() {
		return getServer().getWorld(getConfig().getString("Config.world.name"));
	}
	
}
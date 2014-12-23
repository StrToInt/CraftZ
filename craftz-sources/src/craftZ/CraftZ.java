package craftZ;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import craftZ.cmd.*;
import craftZ.listeners.*;
import craftZ.util.Dynmap;
import craftZ.util.Rewarder;
import craftZ.util.ScoreboardHelper;


public class CraftZ extends JavaPlugin {
	
	public static String[] firstRunMessages = {
		"CraftZ -- It seems that this is the first time you run CraftZ. There are a few important things on first-time use:",
		"* It is likely that CraftZ will not be able to load up because the default world might not exist.",
		"* The whole world which is used by CraftZ (defaults to 'world') will be changed. This includes:",
		"   - Ingame daytime will be the same as reallife daytime.",
		"   - Only zombies will spawn, even during the day.",
		"   - The world is protected from most changes (by players AND other things). " +
				"In addition, players without special permissions are restricted in their actions.",
		"* Please modify the configuration file located at '/plugins/CraftZ/config.yml' to suit your needs. " +
				"Help can be found at http://bit.ly/1baXddU (Bukkit).",
		"* You should setup your world for CraftZ: place chests and spawns, make a lobby and so on. " +
				"Help can be found at http://bit.ly/1ejXhsU (Bukkit).",
		"Have fun!"
	};
	public static String[] firstRunPlayerMessages = {
		"CraftZ -- It seems that this is the first time you run CraftZ. There are a few important things on first-time use:",
		"* It is likely that CraftZ will not be able to load up because the default world might not exist.",
		"* The whole world which is used by CraftZ (defaults to 'world') will be changed.",
		"* Please modify the configuration file to suit your needs. See http://bit.ly/1baXddU",
		"* You should setup your world for CraftZ. See http://bit.ly/1ejXhsU",
		"More info can be found in the console or at '/plugins/CraftZ/README.txt'."
	};
	
	public static final Random RANDOM = new Random();
	public static CraftZ i;
	public static boolean firstRun, failedWorldLoad;
	private static CraftZCommandManager cmd;
	public static long tick = 0;
	
	
	
	@Override
	public void onEnable() {
		
		i = this;
		
		loadConfigs();
		registerEvents();
		
		firstRun = ConfigManager.getConfig("config").getBoolean("Config.never-ever-modify.first-run");
		if (firstRun) {
			ConfigManager.getConfig("config").set("Config.never-ever-modify.first-run", false);
			ConfigManager.saveConfig("config");
		}
		
		
		
		cmd = new CraftZCommandManager();
		getCommand("craftz").setExecutor(cmd);
		
		cmd.setDefault(new CMD_Help());
		cmd.registerCommand(new CMD_Reload(), "reload");
		cmd.registerCommand(new CMD_RemoveItems(), "removeitems", "remitems");
		cmd.registerCommand(new CMD_Spawn(), "spawn");
		cmd.registerCommand(new CMD_SetLobby(), "setlobby");
		cmd.registerCommand(new CMD_Smasher(), "smasher");
		cmd.registerCommand(new CMD_Purge(), "purge");
		cmd.registerCommand(new CMD_Sign(), "sign");
		cmd.registerCommand(new CMD_Top(), "top");
		
		
		
		File readme = new File(getDataFolder(), "README.txt");
		readme.getParentFile().mkdirs();
		
		if (!readme.exists()) {
			
			try {
				
				BufferedWriter wr = new BufferedWriter(new FileWriter(readme));
				for (String s : firstRunMessages) {
					wr.write(s);
					wr.newLine();
				}
				wr.flush();
				wr.close();
				
			} catch (Exception ex) {
				br();
				severe("Could not write the README.txt file to disk!");
			}
			
		}
		
		
		
		Bukkit.getScheduler().runTask(this, new Runnable() {
			
			@Override
			public void run() {
				
				if (world() == null) {
					severe("World '" + worldName() + "' not found! Please check config.yml. CraftZ will not work.");
					failedWorldLoad = true;
					HandlerList.unregisterAll(CraftZ.this);
				}
				
				
				
				if (firstRun) {
					
					br();
					for (String s : firstRunMessages)
						info(s);
					br();
					info("You can also find this message at any time in '/plugins/CraftZ/README.txt'.");
					br();
					
					rl(new PlayerJoinListener.FirstTimeUse());
					
				}
				
				
				
				if (!failedWorldLoad) {
					
					WorldData.setup();
					
					ScoreboardHelper.setup();
					
					int lc = ChestRefiller.loadChests();
					int ps = PlayerManager.loadSpawns();
					int zs = ZombieSpawner.loadSpawns();
					int dp = DeadPlayer.loadDeadPlayers();
					
					info("Loaded " + lc + " chests, " + ps + " player spawns, " + zs + " zombie spawns, " + dp + " dead players");
					
					for (Player p : world().getPlayers()) {
						PlayerJoinListener.joinPlayer(p);
					}
					
				}
				
			}
			
		});
		
		
		
		Bukkit.getScheduler().runTaskTimer(this, new Runnable() {

			@Override
			public void run() {
				
				tick++;
				
				if (!failedWorldLoad) {
					
					ZombieSpawner.onServerTick();
					AnimalSpawner.onServerTick(tick);
					ChestRefiller.onServerTick();
					PlayerManager.onServerTick(tick);
					
					
					
					if (ConfigManager.getConfig("config").getBoolean("Config.world.real-time")) {
						
						int h = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) - 6,
							m = Calendar.getInstance().get(Calendar.MINUTE);
						
						int t = (int) (h * 1000    +    m * (1000.0 / 60));
						
					    CraftZ.world().setFullTime(t);
					    
					}
					
				}
				
			}
			
		}, 1L, 1L);
		
		
		
		if (Rewarder.setup()) {
			info("Successfully hooked into Vault. Players can receive rewards.");
		}
		
		Dynmap.setup();
		
		Dynmap.unpackIcons("loot_military", "loot_military-epic", "loot_civilian", "loot_farms", "loot_industrial", "loot_barracks", "loot_medical");
		
	
		
		info("++=============================================++");
		info("||  Visit dev.bukkit.org/bukkit-plugins/craftz ||");
		info("||  Plugin successfully enabled.               ||");
		info("++=============================================++");
		
	}
	
	
	
	
	@Override
	public void onDisable() {
		
		PlayerManager.saveAllPlayers();
		
		info("++=================================++");
		info("||  Plugin successfully disabled.  ||");
		info("++=================================++");
		
	}
	
	
	
	
	
	private static void registerEvents() {
		
		// PLAYER
		rl(new PlayerInteractListener());
		rl(new PlayerItemConsumeListener());
		rl(new PlayerJoinListener());
		rl(new PlayerQuitListener());
		rl(new ShearEntityListener());
		rl(new PlayerMoveListener());
		rl(new PlayerBedEnterListener());
		rl(new AsyncPlayerChatListener());
		rl(new PlayerDeathListener());
		rl(new EntityShootBowListener());
		rl(new EntityCreatePortalListener());
		rl(new FoodLevelChangeListener());
		rl(new PlayerChangedWorldListener());
		rl(new PlayerTeleportListener());
		rl(new PlayerToggleSprintListener());
		
		// INVENTORY
		rl(new PlayerDropItemListener());
		rl(new PlayerPickupItemListener());
		rl(new InventoryCloseListener());
		rl(new InventoryClickListener());
		
		// CREATURE
		rl(new CreatureSpawnListener());
		rl(new EntityDamageByEntityListener());
		rl(new EntityDamageListener());
		rl(new EntityDeathListener());
		rl(new SheepDyeWoolListener());
		rl(new EntityRegainHealthListener());
		
		// ENTITY
		rl(new ProjectileHitListener());
		rl(new EntityExplodeListener());
		rl(new EntityTargetLivingEntityListener());
		rl(new VehicleUpdateListener());
		rl(new VehicleBlockCollisionListener());
		rl(new VehicleMoveListener());
		
		// BLOCK
		rl(new BlockBreakListener());
		rl(new BlockPlaceListener());
		rl(new HangingBreakListener());
		rl(new HangingBreakByEntityListener());
		rl(new HangingPlaceListener());
		rl(new BlockIgniteListener());
		rl(new BlockBurnListener());
		rl(new BlockGrowListener());
		rl(new StructureGrowListener());
		rl(new BlockSpreadListener());
		rl(new SignChangeListener());
		
		// WORLD
		rl(new WeatherChangeListener());
		rl(new ChunkLoadListener());
		
	}
	
	
	
	
	
	private static void rl(Listener l) {
		Bukkit.getPluginManager().registerEvents(l, i);
	}
	
	
	
	
	
	private static void loadConfigs() {
		
		// CONFIG
		Map<String, Object> def_config = new LinkedHashMap<String, Object>();
		
		def_config.put("Config.never-ever-modify.first-run", true);
			
			// WORLD
			def_config.put("Config.world.name", "world");
			def_config.put("Config.world.lobby.radius", 20);
			def_config.put("Config.world.lobby.world", "world");
			def_config.put("Config.world.lobby.x", 0);
			def_config.put("Config.world.lobby.y", 64);
			def_config.put("Config.world.lobby.z", 0);
			def_config.put("Config.world.real-time", true);
			def_config.put("Config.world.world-border.enable", true);
			def_config.put("Config.world.world-border.radius", 400);
				
				// WORLDCHANGE
				def_config.put("Config.world.world-changing.allow-burning", false);
				def_config.put("Config.world.world-changing.allow-block-grow", false);
				def_config.put("Config.world.world-changing.allow-tree-grow", false);
				def_config.put("Config.world.world-changing.allow-grass-grow", false);
				def_config.put("Config.world.world-changing.allow-new-chunks", true);
				
				// WEATHER
				def_config.put("Config.world.weather.allow-weather-changing", true);
				
			// PLAYERS
			def_config.put("Config.players.use-scoreboard-for-stats", false);
			def_config.put("Config.players.kick-on-death", false);
			def_config.put("Config.players.spawn-death-zombie", true);
			def_config.put("Config.players.send-kill-stat-messages", true);
			def_config.put("Config.players.clear-inventory-on-spawn", true);
			
				// INTERACT
				def_config.put("Config.players.interact.shearing", false);
				def_config.put("Config.players.interact.sleeping", false);
				def_config.put("Config.players.interact.block-breaking", false);
				def_config.put("Config.players.interact.block-placing", false);
				def_config.put("Config.players.interact.allow-spiderweb-placing", true);
				
				// WOOD HARVESTING
				def_config.put("Config.players.wood-harvesting.enable", true);

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
				def_config.put("Config.players.medical.bleeding.enable", true);
				def_config.put("Config.players.medical.bleeding.chance", 0.04);
				def_config.put("Config.players.medical.bleeding.heal-with-paper", true);
				def_config.put("Config.players.medical.healing.heal-with-rosered", true);
				def_config.put("Config.players.medical.healing.only-healing-others", true);
				def_config.put("Config.players.medical.poisoning.enable", true);
				def_config.put("Config.players.medical.poisoning.chance", 0.04);
				def_config.put("Config.players.medical.poisoning.cure-with-limegreen", true);
				def_config.put("Config.players.medical.bonebreak.enable", true);
				def_config.put("Config.players.medical.bonebreak.height", 6);
				def_config.put("Config.players.medical.bonebreak.heal-with-blazerod", true);
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
			def_config.put("Config.mobs.allow-all-plugin-spawning", true);
			def_config.put("Config.mobs.completely-disable-spawn-control", false);
			
				// ZOMBIES
					
					// DROPS
					def_config.put("Config.mobs.zombies.enable-drops", true);
					def_config.put("Config.mobs.zombies.drops.chance", 0.3);
					def_config.put("Config.mobs.zombies.drops.items", new String[] { "arrow", "2x'rotten_flesh'" }); // new String[] { "262", "2x367" }
					
					// SPAWNING
					def_config.put("Config.mobs.zombies.spawning.interval", 40);
					def_config.put("Config.mobs.zombies.spawning.maxzombies", 200);
					def_config.put("Config.mobs.zombies.spawning.enable-auto-spawn", false);
					def_config.put("Config.mobs.zombies.spawning.enable-mini-zombies", true);
					def_config.put("Config.mobs.zombies.spawning.auto-spawning-interval", 40);
					
					// EFFECTS
					def_config.put("Config.mobs.zombies.properties.speed-boost", 1);
					def_config.put("Config.mobs.zombies.properties.damage-boost", 0);
					def_config.put("Config.mobs.zombies.properties.health", 16);
					
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
			def_config.put("Config.vehicles.enable", false);
			def_config.put("Config.vehicles.speed", 5.0);
			def_config.put("Config.vehicles.speed-street-multiplier", 1.6);
			def_config.put("Config.vehicles.speed-street-blocks", new String[] { "wool:7", "wool", "wool:15" });
			def_config.put("Config.vehicles.street-border-blocks", new String[] { "double_step", "step" });
			
			// ITEMNAMES
			def_config.put("Config.change-item-names.enable", true);
			def_config.put("Config.change-item-names.names", new String[] {
					"paper=Bandage", "ink_sack:1=Blood Bag", "ink_sack:10=Antibiotics", "shears=Toolbox", "ender_pearl=Grenade",
					"blaze_rod=Morphine Auto Injector", "watch=Radio"
			});
			
			// DYNMAP
			def_config.put("Config.dynmap.enable", true);
			def_config.put("Config.dynmap.show-lootchests", true);
			def_config.put("Config.dynmap.show-playerspawns", true);
			def_config.put("Config.dynmap.show-worldborder", true);
			
		ConfigManager.newConfig("config", i, def_config);
		ConfigManager.getConfig("config").options().header(
				  "++===================================================++\n"
		 		+ "|| Configuration for the CraftZ plugin by JangoBrick ||\n"
		 		+ "++===================================================++"
		);
		
		FileConfiguration config = ConfigManager.getConfig("config");
		if (config.contains("Config.world.weather.allowWeatherChanging")) { // rename old value
			config.set("Config.world.weather.allow-weather-changing", config.getBoolean("Config.world.weather.allowWeatherChanging"));
			config.set("Config.world.weather.allowWeatherChanging", null);
		}
		
		
		
		
		
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
			def_messages.put("Messages.help.help-command", "/craftz: Displays this help menu.");
			def_messages.put("Messages.help.removeitems-command", "/craftz removeitems: Removes all items in the world. (Alias: /craftz remitems)");
			def_messages.put("Messages.help.reload-command", "/craftz reload: Reload the configuration files.");
			def_messages.put("Messages.help.spawn-command", "/craftz spawn: Spawn at a random point inside of the world.");
			def_messages.put("Messages.help.setlobby-command", "/craftz setlobby: Set the lobby location where you're standing.");
			def_messages.put("Messages.help.smasher-command", "/craftz smasher: Get the ultimate zombie smasher!");
			def_messages.put("Messages.help.purge-command", "/craftz purge: Purge all zombies from the world.");
			def_messages.put("Messages.help.sign-command", "/craftz sign <line2> <line3> <line4>: Get a pre-written sign.");
			def_messages.put("Messages.help.top-command", "/craftz top: Take a look at the highscores.");
			
			// COMMAND
			def_messages.put("Messages.cmd.removed-items", "Removed %i items.");
			def_messages.put("Messages.cmd.reloaded", "Reloaded the config files.");
			def_messages.put("Messages.cmd.setlobby", "The lobby center is set at your location. For lobby radius, see configuration file.");
			def_messages.put("Messages.cmd.purged", "All %z loaded zombies were purged from the world.");
			def_messages.put("Messages.cmd.sign", "A pre-written sign was given to you.");
			def_messages.put("Messages.cmd.top.minutes-survived", "LONGEST TIME SURVIVED");
			def_messages.put("Messages.cmd.top.zombies-killed", "MOST ZOMBIE KILLS IN 1 LIFE");
			def_messages.put("Messages.cmd.top.players-killed", "MOST PLAYER KILLS IN 1 LIFE");
			
			// ERRORS
			def_messages.put("Messages.errors.must-be-player", "You must be a player to use this command.");
			def_messages.put("Messages.errors.too-few-arguments", "Too few arguments given.");
			def_messages.put("Messages.errors.sign-not-complete", "The sign is not complete.");
			def_messages.put("Messages.errors.sign-facing-wrong", "The facing direction you defined is wrong. It may be n, s, e or w.");
			def_messages.put("Messages.errors.not-enough-permissions", "You don't have the required permission to do this.");
			def_messages.put("Messages.errors.not-in-lobby", "You are too far away from the lobby.");
			def_messages.put("Messages.errors.cmd-not-existing", "This command does not exist. Use '/craftz' to display the help.");
			
		ConfigManager.newConfig("messages", i, def_messages);
		ConfigManager.getConfig("messages").options().header(
				  "++==============================================++\n"
		 		+ "|| Messages for the CraftZ plugin by JangoBrick ||\n"
		 		+ "++==============================================++"
		);
		
		
		
		
		
		// LOOT
		Map<String, Object> def_loot = new LinkedHashMap<String, Object>();
			
			// SETTINGS
			def_loot.put("Loot.settings.time-before-refill", 120);
			def_loot.put("Loot.settings.min-stacks-filled", 1);
			def_loot.put("Loot.settings.max-stacks-filled", 3);
			
			
		
		ConfigManager.newConfig("loot", i, def_loot);
		ConfigManager.getConfig("loot").options().header(
				  "++================================================++\n"
		 		+ "|| Loot setup for the CraftZ plugin by JangoBrick ||\n"
		 		+ "++================================================++"
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
				"iron_helmet", "iron_chestplate", "iron_leggings", "iron_boots", "ender_pearl"
			};
			loot.addDefault("Loot.lists.military-epic", militaryEpic);
			
			loot.addDefault("Loot.lists-settings.military-epic.time-before-refill", 240);
			
			
			
			String[] civilian = {
				"2x'brown_mushroom'", "2x'red_mushroom'", "iron_axe", "flint_and_steel", "2x'apple'", "2x'arrow'", "wood_sword", "2x'bowl'",
				"mushroom_soup", "wheat", "bread", "leather_helmet", "leather_chestplate",
				"leather_leggings", "leather_boots", "cookie", "melon", "blaze_rod", "carrot_item", "baked_potato", "pumpkin_pie"
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
				"2x'brown_mushroom'", "2x'red_mushroom'", "apple", "arrow", "wood_sword", "bowl"
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
		
		ConfigManager.newConfig("highscores", i, def_highscores);
		ConfigManager.getConfig("highscores").options().header(
				  "++========================================================++\n"
		 		+ "|| Highscore database for the CraftZ plugin by JangoBrick ||\n"
		 		+ "++========================================================++"
		);
		
	}
	
	
	
	
	
	public static void reloadConfigs() {
		
		ConfigManager.reloadConfigs();
		WorldData.reload();
		
		ChestRefiller.loadChests();
		ZombieSpawner.loadSpawns();
		
		DeadPlayer.loadDeadPlayers();
		
		onDynmapEnabled();
		
		if (world() == null) {
			severe("World '" + worldName() + "' not found! Please check config.yml. CraftZ will stop.");
			failedWorldLoad = true;
			Bukkit.getPluginManager().disablePlugin(i);
		}
		
	}
	
	
	
	
	
	public static String getMsg(String path) {
		return ConfigManager.getConfig("messages").getString(path);
	}
	
	public static String getPrefix() {
		String pre = ConfigManager.getConfig("config").getString("Config.chat.prefix");
		return pre.length() > 0 ? pre : "[CraftZ]";
	}
	
	
	
	
	
	public static String worldName() {
		return ConfigManager.getConfig("config").getString("Config.world.name");
	}
	
	public static World world() {
		return Bukkit.getWorld(worldName());
	}
	
	public static boolean isWorld(String worldName) {
		return worldName.equals(worldName());
	}
	
	public static boolean isWorld(World world) {
		return world.getName().equals(worldName());
	}
	
	
	
	
	
	public static void info(Object msg) {
		i.getLogger().log(Level.INFO, "" + msg);
	}
	
	public static void warn(Object msg) {
		i.getLogger().log(Level.WARNING, "" + msg);
	}
	
	public static void severe(Object msg) {
		i.getLogger().log(Level.SEVERE, "" + msg);
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
	
	
	
	
	
	public static void onDynmapEnabled() {
		
		ChestRefiller.onDynmapEnabled();
		PlayerManager.onDynmapEnabled();
		
	}
	
}
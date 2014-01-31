package craftZ;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import craftZ.listeners.*;
import craftZ.util.AnimalSpawner;
import craftZ.util.ChestRefiller;
import craftZ.util.ConfigManager;
import craftZ.util.DeadPlayer;
import craftZ.util.PlayerManager;
import craftZ.util.ScoreboardHelper;
import craftZ.util.Time;
import craftZ.util.ZombieSpawner;


public class CraftZ extends JavaPlugin {
	
	public static long tickID = 0;
	public static HashMap<Player, Integer> movingPlayers = new HashMap<Player, Integer>();
	public static ArrayList<DeadPlayer> deadPlayers = new ArrayList<DeadPlayer>();
	public static boolean firstRun, failedWorldLoad = false;
	
	public static String[] firstRunMessages = {
		"CraftZ -- It seems that this is the first time you run CraftZ. There are a few important things on first-time use:",
		"* It is likely that CraftZ will not be able to load up because the default world might not exist.",
		"* The whole world which is used by CraftZ (defaults to 'world') will be changed. This includes:",
		"* - Ingame daytime will be the same as reallife daytime.",
		"* - Only zombies will spawn, even during the day.",
		"* - The world is protected from most changes (by players AND other things). " +
				"In addition, players without special permissions are restricted in their actions.",
		"* Please modify the configuration file located at '/plugins/CraftZ/config.yml' to suit your needs. " +
				"Help can be found at http://bit.ly/1baXddU (Bukkit).",
		"* You should setup your world for CraftZ: place chests and spawns, make a lobby and so on. " +
				"Help can be found at http://bit.ly/1ejXhsU (Bukkit).",
		"Have fun!"
	};
	public static String[] firstRunPlayerMessages = {
		"CraftZ -- It seems that this is the first time you run CraftZ. There are a few important things on first-time use:",
		"* It is likely that CraftZ will not be able to load up because the default world does not exist.",
		"* The whole world which is used by CraftZ (defaults to 'world') will be changed.",
		"* Please modify the configuration file to suit your needs. See http://bit.ly/1baXddU",
		"* You should setup your world for CraftZ. See http://bit.ly/1ejXhsU",
		"More info can be found in the console or at '/plugins/CraftZ/README.txt'."
	};
	
	public static CraftZ i;
	
	
	
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
				System.out.println();
				System.err.println("[CraftZ] Could not write the README.txt file to disk!");
			}
			
		}
		
		
		
		this.getServer().getScheduler().runTask(this, new Runnable() {
			
			@Override
			public void run() {
				
				if (world() == null) {
					
					getLogger().log(Level.SEVERE, "World '" + worldName() + "' not found! Please check config.yml. CraftZ will not work.");
					failedWorldLoad = true;
					
					HandlerList.unregisterAll(CraftZ.this);
					
				}
				
				
				
				if (firstRun) {
					
					System.out.println("");
					
					for (String s : firstRunMessages)
						System.out.println(s);
					
					System.out.println("");
					System.out.println("You can also find this message at any time in '/plugins/CraftZ/README.txt'.");
					System.out.println("");
					
					rl(new PlayerJoinListener.FirstTimeUse());
					
				}
				
				
				
				if (!failedWorldLoad) {
					
					ScoreboardHelper.setup();
					ChestRefiller.resetAllChestsAndStartRefill();
					ZombieSpawner.addSpawns();
					DeadPlayer.loadDeadPlayers();
					
				}
				
			}
			
		});
		
		
		
		this.getServer().getScheduler().runTaskTimer(this, new Runnable() {

			@Override
			public void run() {
				
				tickID++;
				
				if (!failedWorldLoad) {
					
					ZombieSpawner.onServerTick();
					AnimalSpawner.onServerTick(tickID);
					ChestRefiller.onServerTick();
					PlayerManager.onServerTick(tickID);
					
					if (ConfigManager.getConfig("config").getBoolean("Config.world.real-time"))
						Time.setToServerTime();
					
				}
				
			}
			
		}, 1L, 1L);
		
	
		
		System.out.println("++======================================================++");
		System.out.println("||  [CraftZ] Visit dev.bukkit.org/bukkit-plugins/craftz ||");
		System.out.println("||  [CraftZ] Plugin successfully enabled.               ||");
		System.out.println("++======================================================++");
		
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
		
		String noPerms = ChatColor.DARK_RED + getMsg("Messages.errors.not-enough-permissions");
		
		
		
		if (cmd.getName().equalsIgnoreCase("craftz")) {
			
			if (args.length == 0) {
				
				if (sender.hasPermission("craftz.help") || sender.hasPermission("craftz.help")) {
					
					sender.sendMessage(ChatColor.GOLD + getMsg("Messages.help.title"));
					sender.sendMessage("");
					
					sender.sendMessage(ChatColor.YELLOW + getMsg("Messages.help.help-command"));
					
					if (sender.hasPermission("craftz.removeitems"))
						sender.sendMessage(ChatColor.YELLOW + getMsg("Messages.help.removeitems-command"));
					
					if (sender.hasPermission("craftz.reload"))
						sender.sendMessage(ChatColor.YELLOW + getMsg("Messages.help.reload-command"));
					
					if (sender.hasPermission("craftz.spawn"))
						sender.sendMessage(ChatColor.YELLOW + getMsg("Messages.help.spawn-command"));
					
					if (sender.hasPermission("craftz.setlobby"))
						sender.sendMessage(ChatColor.YELLOW + getMsg("Messages.help.setlobby-command"));
					
					if (sender.hasPermission("craftz.smasher"))
						sender.sendMessage(ChatColor.YELLOW + getMsg("Messages.help.smasher-command"));
					
				} else {
					sender.sendMessage(noPerms);
				}
				
				return true;
				
			}
			
			
			
			
			if (args.length > 0) {
								
				if (args[0].equalsIgnoreCase("reload")) {
					
					if (sender.hasPermission("craftz.reload")) {
						reloadConfigs();
						sender.sendMessage(ChatColor.GREEN + getMsg("Messages.cmd.reloaded"));
					} else {
						sender.sendMessage(noPerms);
					}
					
					return true;
					
				}
				
				
				
				if (args[0].equalsIgnoreCase("removeitems") || args[0].equalsIgnoreCase("remitems")) {
					
					if (sender.hasPermission("craftz.removeitems")) {
						
						int craftz_removed_items = 0;
						String value_world_name = ConfigManager.getConfig("config").getString("Config.world.name");
						List<Entity> craftz_entities = this.getServer().getWorld(value_world_name).getEntities();
						for (int i=0; i<craftz_entities.toArray().length; i++) {
							
							Entity craftz_entity_ = craftz_entities.get(i);
							if (craftz_entity_.getType() == EntityType.DROPPED_ITEM) {
								craftz_entity_.remove();
								craftz_removed_items++;
							}
							
						}
						
						sender.sendMessage("[CraftZ] " + ChatColor.GREEN + getMsg("Messages.cmd.removed-items")
								.replace("%i", "" + ChatColor.AQUA + craftz_removed_items + ChatColor.GREEN));
						
					} else {
						sender.sendMessage(noPerms);
					}
					
					return true;
					
				}
				
				
				
				if (args[0].equalsIgnoreCase("spawn")) {
					
					if (!(sender instanceof Player)) return true;
					
					Player p = (Player) sender;
					
					if (p.hasPermission("craftz.spawn"))
						if (PlayerManager.isInsideOfLobby(p)) PlayerManager.loadPlayer(p);
					else
						p.sendMessage(noPerms);
					
					return true;
					
				}
				
				
				
				if (args[0].equalsIgnoreCase("setlobby")) {
					
					if (!(sender instanceof Player)) return true;
					
					Player p = (Player) sender;
					
					if (p.hasPermission("craftz.setlobby")) {
						
						String world = p.getWorld().getName();
						int x = p.getLocation().getBlockX();
						int y = p.getLocation().getBlockY();
						int z = p.getLocation().getBlockZ();
						
						ConfigManager.getConfig("config").set("Config.world.lobby.world", world);
						ConfigManager.getConfig("config").set("Config.world.lobby.x", x);
						ConfigManager.getConfig("config").set("Config.world.lobby.y", y);
						ConfigManager.getConfig("config").set("Config.world.lobby.z", z);
						saveConfig();
						
						p.sendMessage(ChatColor.AQUA + getMsg("Messages.cmd.setlobby"));
						
					} else {
						p.sendMessage(noPerms);
					}
					
					return true;
					
				}
				
				
				
				if (args[0].equalsIgnoreCase("smasher")) {
					
					if (!(sender instanceof Player)) return true;
					
					Player p = (Player) sender;
					
					if (p.hasPermission("craftz.smasher")) {
						
						ItemStack stack = new ItemStack(Material.STICK);
						ItemMeta m = stack.getItemMeta();
						m.setDisplayName(ChatColor.GOLD + "Zombie Smasher");
						stack.setItemMeta(m);
						
						p.getInventory().addItem(stack);
//						Item i = p.getWorld().dropItem(p.getLocation(), stack);
//						i.setPickupDelay(0);
						
					} else {
						p.sendMessage(noPerms);
					}
					
					return true;
					
				}
				
				sender.sendMessage(ChatColor.RED + "This command does not exist. Use '/craftz' to display the help.");
				return true;
					
			}
			
		}
			
		
		
		return true;
		
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
		Map<String, Object> def_config = new HashMap<>();
		
		def_config.put("Config.never-ever-modify.first-run", true);
			
			// WORLD
			def_config.put("Config.world.name", "world");
			def_config.put("Config.world.lobby.radius", 20);
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
				def_config.put("Config.world.weather.allowWeatherChanging", true);
				
			// PLAYERS
			def_config.put("Config.players.use-scoreboard-for-stats", false);
			def_config.put("Config.players.kick-on-death", true);
			
				// INTERACT
				def_config.put("Config.players.interact.shearing", false);
				def_config.put("Config.players.interact.sleeping", false);
				
					// BLOCKS
					def_config.put("Config.players.interact.block-breaking", false);
					def_config.put("Config.players.interact.block-placing", false);
					def_config.put("Config.players.interact.allow-spiderweb-placing", true);
				
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
			
			// MOBS
			def_config.put("Config.mobs.blood-particles-when-damaged", true);
			
				// ZOMBIES
					
					// DROPS
					def_config.put("Config.mobs.zombies.enable-drops", true);
					def_config.put("Config.mobs.zombies.drops.chance", 0.3);
					def_config.put("Config.mobs.zombies.drops.items", new String[] { "262", "2x367" });
					
					// SPAWNING
					def_config.put("Config.mobs.zombies.spawning.interval", 40);
					def_config.put("Config.mobs.zombies.spawning.maxzombies", 200);
					def_config.put("Config.mobs.zombies.spawning.enable-auto-spawn", false);
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
			def_config.put("Config.chat.modify-join-and-quit-messages", true);
			def_config.put("Config.chat.modify-player-messages", false);
			def_config.put("Config.chat.modify-death-messages", true);
			
			// VEHICLES
			def_config.put("Config.vehicles.enable", false);
			def_config.put("Config.vehicles.speed", 5.0);
			def_config.put("Config.vehicles.speed-street-multiplier", 1.6);
			def_config.put("Config.vehicles.speed-street-blocks", new String[] { "35:7", "35", "35:15" });
			def_config.put("Config.vehicles.street-border-blocks", new String[] { "43", "44" });
			
			// ITEMNAMES
			def_config.put("Config.change-item-names.enable", true);
			def_config.put("Config.change-item-names.names",
					new String[] { "339=Bandage", "351:1=Blood Bag", "351:10=Antibiotics", "368=Grenade", "369=Morphine Auto Injector" });
			
		ConfigManager.newConfig("config", i, def_config);
		ConfigManager.getConfig("config").options().header(
				  "++===================================================++\n"
		 		+ "|| Configuration for the CraftZ plugin by JangoBrick ||\n"
		 		+ "++===================================================++"
		);
		
		
		
		
		
		// Messages
		Map<String, Object> def_messages = new HashMap<>();
		
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
		def_messages.put("Messages.killed.zombie", "Killed the zombie! Total zombie kills: %k");
		def_messages.put("Messages.killed.player", "Killed %p! Total player kills: %k");
		
			// HELP
			def_messages.put("Messages.help.title", "=== CraftZ Help ===");
			def_messages.put("Messages.help.help-command", "/craftz: Displays this help menu.");
			def_messages.put("Messages.help.removeitems-command", "/craftz removeitems: Removes all items in the world. (Alias: /craftz remitems)");
			def_messages.put("Messages.help.reload-command", "/craftz reload: Reload the configuration files.");
			def_messages.put("Messages.help.spawn-command", "/craftz spawn: Spawn at a random point inside of the world.");
			def_messages.put("Messages.help.setlobby-command", "/craftz setlobby: Set the lobby location where you're standing.");
			def_messages.put("Messages.help.smasher-command", "/craftz smasher: Get the ultimate zombie smasher!");
			
			// COMMAND
			def_messages.put("Messages.cmd.removed-items", "Removed %i items.");
			def_messages.put("Messages.cmd.reloaded", "Reloaded the config files.");
			def_messages.put("Messages.cmd.setlobby", "The lobby center is set at your location. For lobby radius, see configuration file.");
			
			// ERRORS
			def_messages.put("Messages.errors.mustBePlayer", "You must be a player to use this command.");
			def_messages.put("Messages.errors.tooFewArguments", "Too few arguments given.");
			def_messages.put("Messages.errors.sign-not-complete", "The sign is not complete.");
			def_messages.put("Messages.errors.not-enough-permissions", "You don't have the required permission to do this.");
			
		ConfigManager.newConfig("messages", i, def_messages);
		ConfigManager.getConfig("messages").options().header(
				  "++==============================================++\n"
		 		+ "|| Messages for the CraftZ plugin by JangoBrick ||\n"
		 		+ "++==============================================++"
		);
		
		
		
		
		
		// LOOT
		Map<String, Object> def_loot = new HashMap<>();
			
			// SETTINGS
			def_loot.put("Loot.settings.time-before-refill", 120);
			def_loot.put("Loot.settings.min-stacks-filled", 1);
			def_loot.put("Loot.settings.max-stacks-filled", 3);
			
			// LISTS
			
			String[] value_lists_all = {
				"30", "46", "2x39", "2x40", "258", "259", "2x260", "261", "4x262", "267", "2x268", "272", "3x281", "2x282",
				"2x296", "297",	"298", "299", "300", "301", "302", "303", "304", "305", "306", "307", "308", "309", "339",
				"346", "353", "357", "360", "368", "369", "374", "391", "393", "400", "373:5", "373:16389"
			};
			def_loot.put("Loot.lists.all", value_lists_all);
			
			
			
			String[] value_lists_military = {
				"30", "258", "261", "4x262", "2x268", "2x281", "2x282", "3x298", "3x299", "3x300", "3x301"
			};
			def_loot.put("Loot.lists.military", value_lists_military);
			
			
			
			String[] value_lists_militaryEpic = {
				"30", "46", "258", "261", "4x262", "267", "2x268", "272", "2x281", "282", "2x298", "2x299", "2x300",
				"2x301", "302", "303", "304", "305", "306", "307", "308", "309", "368"
			};
			def_loot.put("Loot.lists.military-epic", value_lists_militaryEpic);
			
			
			
			String[] value_lists_civilian = {
				"2x39", "2x40", "258", "259", "2x260", "2x262", "268", "2x281", "282", "296", "297", "298", "299",
				"300", "301", "346", "357", "360", "369", "391", "393", "400"
			};
			def_loot.put("Loot.lists.civilian", value_lists_civilian);
			
			
			
			String[] value_lists_farms = {
				"3x39", "3x40", "258", "259", "4x260", "261", "4x262", "2x268", "4x281", "2x282", "2x296", "2x297",
				"298", "299", "300", "301", "346", "353", "357", "360", "374", "391", "393", "400"
			};
			def_loot.put("Loot.lists.farms", value_lists_farms);
			
			
			
			String[] value_lists_industrial = {
				"30", "4x262", "2x268", "296"
			};
			def_loot.put("Loot.lists.industrial", value_lists_industrial);
			
			
			
			String[] value_lists_barracks = {
				"2x39", "2x40", "260", "262", "268", "281"
			};
			def_loot.put("Loot.lists.barracks", value_lists_barracks);
			
			
			
			String[] value_lists_medical = {
				"2x260", "2x281", "2x282", "339", "2x351:1", "351:10", "2x353", "357", "2x369", "360", "374", "391",
				"2x373:5", "373:16389"
			};
			def_loot.put("Loot.lists.medical", value_lists_medical);
			
		ConfigManager.newConfig("loot", i, def_messages);
		ConfigManager.getConfig("loot").options().header(
				  "++================================================++\n"
		 		+ "|| Loot setup for the CraftZ plugin by JangoBrick ||\n"
		 		+ "++================================================++"
		);
		
	}
	
	
	
	
	
	public static void reloadConfigs() {
		ConfigManager.reloadConfigs();
		DeadPlayer.loadDeadPlayers();
	}
	
	
	
	
	
	public static String getMsg(String path) {
		return ConfigManager.getConfig("messages").getString(path);
	}
	
	
	
	
	
	public static String worldName() {
		return ConfigManager.getConfig("config").getString("Config.world.name");
	}
	
	public static World world() {
		return Bukkit.getWorld(worldName());
	}
	
}
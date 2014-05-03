package craftZ;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
	public static Map<UUID, Integer> movingPlayers = new HashMap<UUID, Integer>();
	public static ArrayList<DeadPlayer> deadPlayers = new ArrayList<DeadPlayer>();
	public static boolean firstRun, failedWorldLoad = false;
	
	public static String[] firstRunMessages = {
		"CraftZ -- It seems that this is the first time you run CraftZ. There are a few important things on first-time use:",
		"* It is likely that CraftZ will not be able to load up because the default world might not exist.",
		"* The whole world which is used by CraftZ (defaults to 'world') will be changed. This includes:",
		"   - Ingame daytime will be the same as reallife daytime.",
		"   - Only zombies will spawn, even during the day. http://youtube.com/ ",
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
					
					for (Player p : world().getPlayers()) {
						PlayerJoinListener.joinPlayer(p);
					}
					
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
					
					
					
					List<UUID> toRemove = new ArrayList<UUID>();
					
					for (UUID id : movingPlayers.keySet()) {
						int mt = movingPlayers.get(id);
						movingPlayers.put(id, ++mt);
						if (mt > 8) toRemove.add(id);
					}
					
					for (int i=0; i<toRemove.size(); i++) {
						movingPlayers.remove(toRemove.get(i));
					}
					
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
		
		
		
		if (cmd.getName().equals("craftz")) {
			
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
					
					if (sender.hasPermission("craftz.sign"))
						sender.sendMessage(ChatColor.YELLOW + getMsg("Messages.help.sign-command"));
					
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
						
						sender.sendMessage(CraftZ.getPrefix() + " " + ChatColor.GREEN + getMsg("Messages.cmd.removed-items")
								.replace("%i", "" + ChatColor.AQUA + craftz_removed_items + ChatColor.GREEN));
						
					} else {
						sender.sendMessage(noPerms);
					}
					
					return true;
					
				}
				
				
				
				if (args[0].equalsIgnoreCase("spawn")) {
					
					if (!(sender instanceof Player)) {
						return true;
					}
					
					Player p = (Player) sender;
					
					if (p.hasPermission("craftz.spawn")) {
						
						if (PlayerManager.isInsideOfLobby(p)) {
							PlayerManager.loadPlayer(p, true);
						} else {
							p.sendMessage(ChatColor.DARK_RED + getMsg("Messages.errors.not-in-lobby"));
						}
						
					} else {
						p.sendMessage(noPerms);
					}
					
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
						ConfigManager.saveConfig("config");
						
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
				
				
				
				if (args[0].equalsIgnoreCase("sign")) {
					
					if (!(sender instanceof Player)) return true;
					
					Player p = (Player) sender;
					
					if (p.hasPermission("craftz.sign")) {
						
						if (args.length > 1) {
							
							String line2 = args[1];
							String line3 = args.length > 2 ? args[2] : "";
							String line4 = args.length > 3 ? args[3] : "";
							
							String desc = "Unknown";
							if (line2.equalsIgnoreCase("lootchest")) {
								desc = "Loot '" + line4 + "'";
							} else if (line2.equalsIgnoreCase("playerspawn")) {
								desc = "Player Spawn '" + line3 + "'";
							} else if (line2.equalsIgnoreCase("zombiespawn")) {
								desc = "Zombie Spawn " + line3;
							}
							
							ItemStack sign = new ItemStack(Material.SIGN);
							ItemMeta meta = sign.getItemMeta();
							meta.setDisplayName(ChatColor.DARK_PURPLE + "Pre-written Sign / " + desc);
							meta.setLore(Arrays.asList("[CraftZ]", line2, line3, line4));
							sign.setItemMeta(meta);
							
							p.getInventory().addItem(sign);
							
						} else {
							sender.sendMessage(ChatColor.RED + getMsg("Messages.errors.tooFewArguments"));
						}
						
					} else {
						p.sendMessage(noPerms);
					}
					
					return true;
					
				}
				
				
				
				sender.sendMessage(ChatColor.RED + getMsg("Messages.errors.cmd-not-existing"));
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
		Map<String, Object> def_config = new HashMap<String, Object>();
		
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
				def_config.put("Config.world.weather.allowWeatherChanging", true);
				
			// PLAYERS
			def_config.put("Config.players.use-scoreboard-for-stats", false);
			def_config.put("Config.players.kick-on-death", false);
			def_config.put("Config.players.spawn-death-zombie", true);
			
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
				def_config.put("Config.players.medical.thirst.enable", true);
				def_config.put("Config.players.medical.thirst.ticks-normal", 1200);
				def_config.put("Config.players.medical.thirst.ticks-desert", 800);
			
			// MOBS
			def_config.put("Config.mobs.blood-particles-when-damaged", true);
			def_config.put("Config.mobs.allow-all-plugin-spawning", true);
			
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
			def_config.put("Config.chat.modify-join-and-quit-messages", true);
			def_config.put("Config.chat.modify-player-messages", false);
			def_config.put("Config.chat.modify-death-messages", true);
			def_config.put("Config.chat.separate-craftz-chat", true);
			def_config.put("Config.chat.extended-error-messages", true);
			def_config.put("Config.chat.prefix", "[CraftZ]");
			
				// RANGE
				def_config.put("Config.chat.ranged.enable", false);
				def_config.put("Config.chat.ranged.range", 80);
			
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
					"blaze_rod=Morphine Auto Injector"
			});
			
		ConfigManager.newConfig("config", i, def_config);
		ConfigManager.getConfig("config").options().header(
				  "++===================================================++\n"
		 		+ "|| Configuration for the CraftZ plugin by JangoBrick ||\n"
		 		+ "++===================================================++"
		);
		
		
		
		
		
		// Messages
		Map<String, Object> def_messages = new HashMap<String, Object>();
		
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
			def_messages.put("Messages.help.sign-command", "/craftz sign <line2> <line3> <line4>: Get a pre-written sign.");
			
			// COMMAND
			def_messages.put("Messages.cmd.removed-items", "Removed %i items.");
			def_messages.put("Messages.cmd.reloaded", "Reloaded the config files.");
			def_messages.put("Messages.cmd.setlobby", "The lobby center is set at your location. For lobby radius, see configuration file.");
			def_messages.put("Messages.cmd.sign", "A pre-written sign was given to you.");
			
			// ERRORS
			def_messages.put("Messages.errors.mustBePlayer", "You must be a player to use this command.");
			def_messages.put("Messages.errors.tooFewArguments", "Too few arguments given.");
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
		Map<String, Object> def_loot = new HashMap<String, Object>();
			
			// SETTINGS
			def_loot.put("Loot.settings.time-before-refill", 120);
			def_loot.put("Loot.settings.min-stacks-filled", 1);
			def_loot.put("Loot.settings.max-stacks-filled", 3);
			
			// LISTS
			
			String[] value_lists_all = {
//				"30", "46", "2x39", "2x40", "258", "259", "2x260", "261", "4x262", "267", "2x268", "272", "3x281", "2x282",
//				"2x296", "297",	"298", "299", "300", "301", "302", "303", "304", "305", "306", "307", "308", "309", "339",
//				"346", "353", "357", "360", "368", "369", "374", "391", "393", "400", "373:5", "373:16389"
				"web", "tnt", "2x'brown_mushroom'", "2x'red_mushroom'", "iron_axe", "flint_and_steel", "2x'apple'", "bow",
				"4x'arrow'", "iron_sword", "2x'wood_sword'", "stone_sword", "3x'bowl'", "2x'mushroom_soup'",
				"2x'wheat'", "bread",	"leather_helmet", "leather_chestplate", "leather_leggings", "leather_boots", "chainmail_helmet",
				"chainmail_chestplate", "chainmail_leggings", "chainmail_boots", "iron_helmet", "iron_chestplate", "iron_leggings", "iron_boots",
				"paper", "sugar", "cookie", "melon", "ender_pearl", "blaze_rod", "glass_bottle", "carrot_item", "baked_potato",
				"pumpkin_pie", "potion:5", "potion:16389"
			};
			def_loot.put("Loot.lists.all", value_lists_all);
			
			
			
			String[] value_lists_military = {
//				"30", "258", "261", "4x262", "2x268", "2x281", "2x282", "3x298", "3x299", "3x300", "3x301"
				"web", "iron_axe", "bow", "4x'arrow'", "2x'wood_sword'", "2x'bowl'", "2x'mushroom_soup'", "3x'leather_helmet'",
				"3x'leather_chestplate'", "3x'leather_leggings'", "3x'leather_boots'"
			};
			def_loot.put("Loot.lists.military", value_lists_military);
			
			
			
			String[] value_lists_militaryEpic = {
//				"30", "46", "258", "261", "4x262", "267", "2x268", "272", "2x281", "282", "2x298", "2x299", "2x300",
//				"2x301", "302", "303", "304", "305", "306", "307", "308", "309", "368"
				"web", "tnt", "iron_axe", "bow", "4x'arrow'", "iron_sword", "2x'wood_sword'", "stone_sword", "2x'bowl'", "mushroom_soup",
				"2x'leather_helmet'", "2x'leather_chestplate'", "2x'leather_leggings'", "2x'leather_boots'",
				"chainmail_helmet", "chainmail_chestplate", "chainmail_leggings", "chainmail_boots",
				"iron_helmet", "iron_chestplate", "iron_leggings", "iron_boots", "ender_pearl"
			};
			def_loot.put("Loot.lists.military-epic", value_lists_militaryEpic);
			
			
			
			String[] value_lists_civilian = {
//				"2x39", "2x40", "258", "259", "2x260", "2x262", "268", "2x281", "282", "296", "297", "298", "299",
//				"300", "301", "346", "357", "360", "369", "391", "393", "400"
				"2x'brown_mushroom'", "2x'red_mushroom'", "iron_axe", "flint_and_steel", "2x'apple'", "2x'arrow'", "wood_sword", "2x'bowl'",
				"mushroom_soup", "wheat", "bread", "leather_helmet", "leather_chestplate",
				"leather_leggings", "leather_boots", "cookie", "melon", "blaze_rod", "carrot_item", "baked_potato", "pumpkin_pie"
			};
			def_loot.put("Loot.lists.civilian", value_lists_civilian);
			
			
			
			String[] value_lists_farms = {
//				"3x39", "3x40", "258", "259", "4x260", "261", "4x262", "2x268", "4x281", "2x282", "2x296", "2x297",
//				"298", "299", "300", "301", "346", "353", "357", "360", "374", "391", "393", "400"
				"3x'brown_mushroom'", "3x'red_mushroom'", "iron_axe", "flint_and_steel", "4x'apple'", "bow", "4x'arrow'", "2x'wood_sword'",
				"4x'bowl'", "2x'mushroom_soup'", "2x'wheat'", "2x'bread'", "leather_helmet", "leather_chestplate", "leather_leggings", "leather_boots",
				"sugar", "cookie", "melon", "glass_bottle", "carrot_item", "baked_potato", "pumpkin_pie"
			};
			def_loot.put("Loot.lists.farms", value_lists_farms);
			
			
			
			String[] value_lists_industrial = {
//				"30", "4x262", "2x268", "296"
				"web", "4x'arrow'", "2x'wood_sword'", "wheat"
			};
			def_loot.put("Loot.lists.industrial", value_lists_industrial);
			
			
			
			String[] value_lists_barracks = {
//				"2x39", "2x40", "260", "262", "268", "281"
				"2x'brown_mushroom'", "2x'red_mushroom'", "apple", "arrow", "wood_sword", "bowl"
			};
			def_loot.put("Loot.lists.barracks", value_lists_barracks);
			
			
			
			String[] value_lists_medical = {
//				"2x260", "2x281", "2x282", "339", "2x351:1", "351:10", "2x353", "357", "2x369", "360", "374", "391",
//				"2x373:5", "373:16389"
				"2x'apple'", "2x'bowl'", "2x'mushroom_soup'", "paper", "2x'ink_sack:1'", "ink_sack:10", "2x'sugar'", "cookie", "2x'blaze_rod'",
				"melon", "glass_bottle", "carrot_item", "2x'potion:5'", "potion:16389"
			};
			def_loot.put("Loot.lists.medical", value_lists_medical);
		
		
		
		ConfigManager.newConfig("loot", i, def_loot);
		ConfigManager.getConfig("loot").options().header(
				  "++================================================++\n"
		 		+ "|| Loot setup for the CraftZ plugin by JangoBrick ||\n"
		 		+ "++================================================++"
		);
		
		if (ConfigManager.getConfig("loot").contains("Messages")) {
			ConfigManager.getConfig("loot").set("Messages", null);
			ConfigManager.saveConfig("loot");
		}
		
	}
	
	
	
	
	
	public static void reloadConfigs() {
		ConfigManager.reloadConfigs();
		DeadPlayer.loadDeadPlayers();
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
	
}
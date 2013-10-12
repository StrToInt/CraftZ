package craftZ;

import java.io.File;
import java.io.IOException;
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
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import craftZ.listeners.*;
import craftZ.util.Time;


public class CraftZ extends JavaPlugin {
	
	public static long tickID = 0;
	public Map<Player, Integer> movingPlayers = new HashMap<Player, Integer>();
	
	public static CraftZ i;
	
	
	
	@Override
	public void onEnable() {
		
		i = this;
		
		loadConfig();
		registerEvents();
		
		
		
		this.getServer().getScheduler().runTaskTimer(this, new Runnable() {

			@Override
			public void run() {
				
				tickID++;
				
				ZombieSpawner.onServerTick();
				AnimalSpawner.onServerTick(tickID);
				ChestRefiller.onServerTick();
				PlayerManager.onServerTick(tickID);
				
				if (getConfig().getBoolean("Config.world.real-time"))
					Time.setToServerTime();
				
			}
			
		}, 1L, 1L);
		
		
		
		this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			
			@Override
			public void run() {
				
				if (world() == null) {
					getLogger().log(Level.SEVERE, "World not found! Please check config.yml. CraftZ will shutdown now.");
					getPluginLoader().disablePlugin(i);
					return;
				}
				
				ScoreboardHelper.setup();
				ChestRefiller.resetAllChestsAndStartRefill();
				ZombieSpawner.addSpawns();
				
			}
			
		});
		
	
		
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
		
		String noPerms = ChatColor.DARK_RED + getLangConfig().getString("Messages.errors.not-enough-permissions");
		
		
		
		if (cmd.getName().equalsIgnoreCase("craftz")) {
			
			if (args.length == 0) {
				
				if (sender.hasPermission("craftz.help") || sender.hasPermission("craftz.help")) {
					
					sender.sendMessage(ChatColor.GOLD + getLangConfig().getString("Messages.help.title"));
					sender.sendMessage("");
					
					sender.sendMessage(ChatColor.YELLOW + getLangConfig().getString("Messages.help.help-command"));
					
					if (sender.hasPermission("craftz.removeitems"))
						sender.sendMessage(ChatColor.YELLOW + getLangConfig().getString("Messages.help.removeitems-command"));
					
					if (sender.hasPermission("craftz.reload"))
						sender.sendMessage(ChatColor.YELLOW + getLangConfig().getString("Messages.help.reload-command"));
					
					if (sender.hasPermission("craftz.spawn"))
						sender.sendMessage(ChatColor.YELLOW + getLangConfig().getString("Messages.help.spawn-command"));
					
					if (sender.hasPermission("craftz.setlobby"))
						sender.sendMessage(ChatColor.YELLOW + getLangConfig().getString("Messages.help.setlobby-command"));
					
					if (sender.hasPermission("craftz.smasher"))
						sender.sendMessage(ChatColor.YELLOW + getLangConfig().getString("Messages.help.smasher-command"));
					
				} else {
					sender.sendMessage(noPerms);
				}
				
				return true;
				
			}
			
			
			
			
			if (args.length > 0) {
								
				if (args[0].equalsIgnoreCase("reload")) {
					
					if (sender.hasPermission("craftz.reload")) {
						reloadConfigs();
						sender.sendMessage(ChatColor.GREEN + getLangConfig().getString("Messages.cmd.reloaded"));
					} else {
						sender.sendMessage(noPerms);
					}
					
				}
				
				
				
				if (args[0].equalsIgnoreCase("removeitems") || args[0].equalsIgnoreCase("remitems")) {
					
					if (sender.hasPermission("craftz.removeitems")) {
						
						int craftz_removed_items = 0;
						String value_world_name = getConfig().getString("Config.world.name");
						List<Entity> craftz_entities = this.getServer().getWorld(value_world_name).getEntities();
						for (int i=0; i<craftz_entities.toArray().length; i++) {
							
							Entity craftz_entity_ = craftz_entities.get(i);
							if (craftz_entity_.getType() == EntityType.DROPPED_ITEM) {
								craftz_entity_.remove();
								craftz_removed_items++;
							}
							
						}
						
						sender.sendMessage("[CraftZ] " + ChatColor.GREEN + getLangConfig().getString("Messages.cmd.removed-items")
								.replace("%i", "" + ChatColor.AQUA + craftz_removed_items + ChatColor.GREEN));
						
					} else {
						sender.sendMessage(noPerms);
					}
					
				}
				
				
				
				if (args[0].equalsIgnoreCase("spawn")) {
					
					if (!(sender instanceof Player)) return true;
					
					Player p = (Player) sender;
					
					if (p.hasPermission("craftz.spawn"))
						if (PlayerManager.isInsideOfLobby(p)) PlayerManager.loadPlayer(p);
					else
						p.sendMessage(noPerms);
					
				}
				
				
				
				if (args[0].equalsIgnoreCase("setlobby")) {
					
					if (!(sender instanceof Player)) return true;
					
					Player p = (Player) sender;
					
					if (p.hasPermission("craftz.setlobby")) {
						
						int x = p.getLocation().getBlockX();
						int y = p.getLocation().getBlockY();
						int z = p.getLocation().getBlockZ();
						
						getConfig().set("Config.world.lobby.x", x);
						getConfig().set("Config.world.lobby.y", y);
						getConfig().set("Config.world.lobby.z", z);
						
						saveConfig();
						
						String value_lobbySet = ChatColor.AQUA + getLangConfig().getString("Messages.cmd.setlobby");
						p.sendMessage(value_lobbySet);
						
					} else {
						p.sendMessage(noPerms);
					}
					
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
					
				}
				
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
		rl(new PlayerCommandPreprocessListener());
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
		
		// ITEM
		rl(new ItemDespawnListener());
		
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
	
	
	
	
	
	private static void loadConfig() {
		
		reloadLangConfig();
		reloadLootConfig();
		
		// SET HEADER
		
		i.getConfig().options().header(  "++===================================================++"
								 		+ "\n|| Configuration for the CraftZ plugin by JangoBrick ||"
								 		+ "\n++===================================================++"
		);
		
		// CONFIG
		
			// WORLD
			i.getConfig().addDefault("Config.world.name", "world");
			i.getConfig().addDefault("Config.world.lobby.radius", 20);
			i.getConfig().addDefault("Config.world.lobby.x", 0);
			i.getConfig().addDefault("Config.world.lobby.y", 64);
			i.getConfig().addDefault("Config.world.lobby.z", 0);
			i.getConfig().addDefault("Config.world.real-time", true);
			i.getConfig().addDefault("Config.world.world-border.enable", true);
			i.getConfig().addDefault("Config.world.world-border.radius", 400);
				
				// WORLDCHANGE
				i.getConfig().addDefault("Config.world.world-changing.allow-burning", false);
				i.getConfig().addDefault("Config.world.world-changing.allow-block-grow", false);
				i.getConfig().addDefault("Config.world.world-changing.allow-tree-grow", false);
				i.getConfig().addDefault("Config.world.world-changing.allow-grass-grow", false);
				i.getConfig().addDefault("Config.world.world-changing.allow-new-chunks", true);
				
				// WEATHER
				i.getConfig().addDefault("Config.world.weather.allowWeatherChanging", true);
				
			// PLAYERS
			i.getConfig().addDefault("Config.players.use-scoreboard-for-stats", false);
			i.getConfig().addDefault("Config.players.kick-on-death", true);
			
				// INTERACT
				i.getConfig().addDefault("Config.players.interact.shearing", false);
				i.getConfig().addDefault("Config.players.interact.sleeping", false);
				
					// BLOCKS
					i.getConfig().addDefault("Config.players.interact.block-breaking", false);
					i.getConfig().addDefault("Config.players.interact.block-placing", false);
					i.getConfig().addDefault("Config.players.interact.allow-spiderweb-placing", true);
				
				// MEDICAL
				i.getConfig().addDefault("Config.players.medical.enable-sugar-speed-effect", true);
				i.getConfig().addDefault("Config.players.medical.bleeding.enable", true);
				i.getConfig().addDefault("Config.players.medical.bleeding.chance", 0.04);
				i.getConfig().addDefault("Config.players.medical.bleeding.heal-with-paper", true);
				i.getConfig().addDefault("Config.players.medical.healing.heal-with-rosered", true);
				i.getConfig().addDefault("Config.players.medical.healing.only-healing-others", true);
				i.getConfig().addDefault("Config.players.medical.poisoning.enable", true);
				i.getConfig().addDefault("Config.players.medical.poisoning.chance", 0.04);
				i.getConfig().addDefault("Config.players.medical.poisoning.cure-with-limegreen", true);
				i.getConfig().addDefault("Config.players.medical.bonebreak.enable", true);
				i.getConfig().addDefault("Config.players.medical.bonebreak.height", 6);
				i.getConfig().addDefault("Config.players.medical.bonebreak.heal-with-blazerod", true);
			
			// MOBS
			i.getConfig().addDefault("Config.mobs.blood-particles-when-damaged", true);
			
				// ZOMBIES
				
					// DROPS
					i.getConfig().addDefault("Config.mobs.zombies.enable-drops", true);
					i.getConfig().addDefault("Config.mobs.zombies.drops.chance", 0.3);
					i.getConfig().addDefault("Config.mobs.zombies.drops.items", new String[] { "262", "2x367" });
					
					// SPAWNING
					i.getConfig().addDefault("Config.mobs.zombies.spawning.interval", 40);
					i.getConfig().addDefault("Config.mobs.zombies.spawning.maxzombies", 200);
					i.getConfig().addDefault("Config.mobs.zombies.spawning.enable-auto-spawn", false);
					i.getConfig().addDefault("Config.mobs.zombies.spawning.auto-spawning-interval", 40);
				
//				// ANIMALS
//				
//					// SPAWNS
//					getConfig().addDefault("Config.mobs.animals.spawning.enable", true);
//					
//						// CHANCE
//						getConfig().addDefault("Config.mobs.animals.spawning.chance.cow", 0.1);
//						getConfig().addDefault("Config.mobs.animals.spawning.chance.chicken", 0.1);
//						getConfig().addDefault("Config.mobs.animals.spawning.chance.pig", 0.1);
//						getConfig().addDefault("Config.mobs.animals.spawning.chance.sheep", 0.1);
					
			// CHAT
			i.getConfig().addDefault("Config.chat.modify-join-and-quit-messages", true);
			i.getConfig().addDefault("Config.chat.modify-player-messages", false);
			i.getConfig().addDefault("Config.chat.modify-death-messages", true);
			
			// VEHICLES
			i.getConfig().addDefault("Config.vehicles.enable", false);
			i.getConfig().addDefault("Config.vehicles.speed", 5.0);
			i.getConfig().addDefault("Config.vehicles.speed-street-multiplier", 1.6);
			i.getConfig().addDefault("Config.vehicles.speed-street-blocks", new String[] { "35:7", "35", "35:15" });
			i.getConfig().addDefault("Config.vehicles.street-border-blocks", new String[] { "43", "44" });
			
			// ITEMNAMES
			i.getConfig().addDefault("Config.change-item-names.enable", true);
			i.getConfig().addDefault("Config.change-item-names.names",
					new String[] { "339=Bandage", "351:1=Blood Bag", "351:10=Antibiotics", "368=Grenade", "369=Morphine Auto Injector" });
		
		
		
		i.getConfig().options().copyDefaults(true);
		i.saveConfig();
		
	}
	
	
	
	
	
	private static void loadLangConfig() {
		
		// SET HEADER
		
		getLangConfig().options().header("++==============================================++"
								 		  + "\n|| Messages for the CraftZ plugin by JangoBrick ||"
								 		  + "\n++==============================================++"
		);
		
		// MESSAGES
		getLangConfig().addDefault("Messages.harvested-tree", "A pile of wood has been successfully added to your inventory.");
		getLangConfig().addDefault("Messages.already-have-wood", "You already have wood.");
		getLangConfig().addDefault("Messages.isnt-a-tree", "You must be in a forest and close to a tree to harvest wood.");
		getLangConfig().addDefault("Messages.destroyed-sign", "You just destroyed a CraftZ sign.");
		getLangConfig().addDefault("Messages.successfully-created", "Successfully created!");
		getLangConfig().addDefault("Messages.spawned", "You're at spawnpoint %s");
		getLangConfig().addDefault("Messages.died", "You died! Zombies killed: %z, players killed: %p, minutes survived: %m");
		getLangConfig().addDefault("Messages.bleeding", "You are bleeding! You need a bandage to mend the wounds!");
		getLangConfig().addDefault("Messages.bandaged", "Your wounds are now bandaged.");
		getLangConfig().addDefault("Messages.bloodbag", "Your health is restored.");
		getLangConfig().addDefault("Messages.poisoned", "You are poisoned! You should use antibiotics soon.");
		getLangConfig().addDefault("Messages.unpoisoned", "Your poisoning is healed!");
		getLangConfig().addDefault("Messages.bones-broken", "You broke your bones! You need a Morphine Auto Injector!");
		getLangConfig().addDefault("Messages.bones-healed", "You used the Morphine Auto Injector successfully.");
		getLangConfig().addDefault("Messages.out-of-world", "You're in a very infected area! Go back, or you will die soon!");
		getLangConfig().addDefault("Messages.killed.zombie", "Killed the zombie! Total zombie kills: %k");
		getLangConfig().addDefault("Messages.killed.player", "Killed %p! Total player kills: %k");
		
			// HELP
			getLangConfig().addDefault("Messages.help.title", "=== CraftZ Help ===");
			getLangConfig().addDefault("Messages.help.help-command", "/craftz: Displays this help menu.");
			getLangConfig().addDefault("Messages.help.removeitems-command", "/craftz removeitems: Removes all items in the world. (Alias: /craftz remitems)");
			getLangConfig().addDefault("Messages.help.reload-command", "/craftz reload: Reload the configuration files.");
			getLangConfig().addDefault("Messages.help.spawn-command", "/craftz spawn: Spawn at a random point inside of the world.");
			getLangConfig().addDefault("Messages.help.setlobby-command", "/craftz setlobby: Set the lobby location where you're standing.");
			getLangConfig().addDefault("Messages.help.smasher-command", "/craftz smasher: Get the ultimate zombie smasher!");
			
			// COMMAND
			getLangConfig().addDefault("Messages.cmd.removed-items", "Removed %i items.");
			getLangConfig().addDefault("Messages.cmd.reloaded", "Reloaded the config files.");
			getLangConfig().addDefault("Messages.cmd.setlobby", "The lobby center is set at your location. For lobby radius, see configuration file.");
			
			// ERRORS
			getLangConfig().addDefault("Messages.errors.mustBePlayer", "You must be a player to use this command.");
			getLangConfig().addDefault("Messages.errors.tooFewArguments", "Too few arguments given.");
			getLangConfig().addDefault("Messages.errors.sign-not-complete", "The sign is not complete.");
			getLangConfig().addDefault("Messages.errors.not-enough-permissions", "You don't have the required permission to do this.");
		
		
		
		
		getLangConfig().options().copyDefaults(true);
		saveLangConfig();
		
	}
	
	
	
	
	
	private static FileConfiguration langConfig = null;
	private static File langConfigFile = null;
	
	public static void reloadLangConfig() {
		if (langConfigFile == null) langConfigFile = new File(i.getDataFolder(), "messages.yml");
		langConfig = YamlConfiguration.loadConfiguration(langConfigFile);
		loadLangConfig();
	}
	
	public static FileConfiguration getLangConfig() {
		if (langConfig == null) reloadLangConfig();
		return langConfig;
	}
	
	public static void saveLangConfig() {
		
		if (langConfig == null || langConfigFile == null) return;
		
		try {
			getLangConfig().save(langConfigFile);
		} catch (IOException ex) {
			i.getLogger().log(Level.SEVERE, "Could not save config to " + langConfigFile, ex);
		}
		
	}
	
	
	
	
	
	private static void loadLootConfig() {
		
		// SET HEADER
		
		getLootConfig().options().header("++==========================================++"
								 		  + "\n|| Loot for the CraftZ plugin by JangoBrick ||"
								 		  + "\n++==========================================++"
		);
		
		
		
		// LOOT
			
			// SETTINGS
			getLootConfig().addDefault("Loot.settings.time-before-refill", 120);
			getLootConfig().addDefault("Loot.settings.min-stacks-filled", 1);
			getLootConfig().addDefault("Loot.settings.max-stacks-filled", 3);
			
			// LISTS
			
			String[] value_lists_all = {
				"30", "46", "2x39", "2x40", "258", "259", "2x260", "261", "4x262", "267", "2x268", "272", "3x281", "2x282",
				"2x296", "297",	"298", "299", "300", "301", "302", "303", "304", "305", "306", "307", "308", "309", "339",
				"346", "353", "357", "360", "368", "369", "374", "391", "393", "400", "373:5", "373:16389"
			};
			getLootConfig().addDefault("Loot.lists.all", value_lists_all);
			
			
			
			String[] value_lists_military = {
				"30", "258", "261", "4x262", "2x268", "2x281", "2x282", "3x298", "3x299", "3x300", "3x301"
			};
			getLootConfig().addDefault("Loot.lists.military", value_lists_military);
			
			
			
			String[] value_lists_militaryEpic = {
				"30", "46", "258", "261", "4x262", "267", "2x268", "272", "2x281", "282", "2x298", "2x299", "2x300",
				"2x301", "302", "303", "304", "305", "306", "307", "308", "309", "368"
			};
			getLootConfig().addDefault("Loot.lists.military-epic", value_lists_militaryEpic);
			
			
			
			String[] value_lists_civilian = {
				"2x39", "2x40", "258", "259", "2x260", "2x262", "268", "2x281", "282", "296", "297", "298", "299",
				"300", "301", "346", "357", "360", "369", "391", "393", "400"
			};
			getLootConfig().addDefault("Loot.lists.civilian", value_lists_civilian);
			
			
			
			String[] value_lists_farms = {
				"3x39", "3x40", "258", "259", "4x260", "261", "4x262", "2x268", "4x281", "2x282", "2x296", "2x297",
				"298", "299", "300", "301", "346", "353", "357", "360", "374", "391", "393", "400"
			};
			getLootConfig().addDefault("Loot.lists.farms", value_lists_farms);
			
			
			
			String[] value_lists_industrial = {
				"30", "4x262", "2x268", "296"
			};
			getLootConfig().addDefault("Loot.lists.industrial", value_lists_industrial);
			
			
			
			String[] value_lists_barracks = {
				"2x39", "2x40", "260", "262", "268", "281"
			};
			getLootConfig().addDefault("Loot.lists.barracks", value_lists_barracks);
			
			
			
			String[] value_lists_medical = {
				"2x260", "2x281", "2x282", "339", "2x351:1", "351:10", "2x353", "357", "2x369", "360", "374", "391",
				"2x373:5", "373:16389"
			};
			getLootConfig().addDefault("Loot.lists.medical", value_lists_medical);
		
		
		
		getLootConfig().options().copyDefaults(true);
		saveLootConfig();
		
	}
	
	
	
	
	
	private static FileConfiguration lootConfig = null;
	private static File lootConfigFile = null;
	
	public static void reloadLootConfig() {
		if (lootConfigFile == null) lootConfigFile = new File(i.getDataFolder(), "loot.yml");
		lootConfig = YamlConfiguration.loadConfiguration(lootConfigFile);
		loadLootConfig();
		
	}
	
	public static FileConfiguration getLootConfig() {
		if (lootConfig == null) reloadLootConfig();
		return lootConfig;
	}
	
	public static void saveLootConfig() {
		
		if (lootConfig == null || lootConfigFile == null) return;
		
		try {
			getLootConfig().save(lootConfigFile);
		} catch (IOException ex) {
			i.getLogger().log(Level.SEVERE, "Could not save config to " + lootConfigFile, ex);
		}
		
	}
	
	
	
	
	
	public static void reloadConfigs() {
		i.reloadConfig();
		loadConfig();
	}
	
	
	
	
	
	public static String worldName() {
		return i.getConfig().getString("Config.world.name");
	}
	
	public static World world() {
		return Bukkit.getWorld(worldName());
	}
	
}
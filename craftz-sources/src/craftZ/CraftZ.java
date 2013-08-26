package craftZ;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import craftZ.listeners.AsyncPlayerChatListener;
import craftZ.listeners.BlockBreakListener;
import craftZ.listeners.BlockBurnListener;
import craftZ.listeners.BlockGrowListener;
import craftZ.listeners.BlockIgniteListener;
import craftZ.listeners.BlockPlaceListener;
import craftZ.listeners.BlockSpreadListener;
import craftZ.listeners.ChunkLoadListener;
import craftZ.listeners.CreatureSpawnListener;
import craftZ.listeners.EntityCreatePortalListener;
import craftZ.listeners.EntityDamageByEntityListener;
import craftZ.listeners.EntityDamageListener;
import craftZ.listeners.EntityDeathListener;
import craftZ.listeners.EntityExplodeListener;
import craftZ.listeners.EntityRegainHealthListener;
import craftZ.listeners.EntityShootBowListener;
import craftZ.listeners.FoodLevelChangeListener;
import craftZ.listeners.HangingBreakByEntityListener;
import craftZ.listeners.HangingBreakListener;
import craftZ.listeners.HangingPlaceListener;
import craftZ.listeners.InventoryClickListener;
import craftZ.listeners.InventoryCloseListener;
import craftZ.listeners.ItemDespawnListener;
import craftZ.listeners.PlayerBedEnterListener;
import craftZ.listeners.PlayerChangedWorldListener;
import craftZ.listeners.PlayerCommandPreprocessListener;
import craftZ.listeners.PlayerDeathListener;
import craftZ.listeners.PlayerDropItemListener;
import craftZ.listeners.PlayerInteractListener;
import craftZ.listeners.PlayerItemConsumeListener;
import craftZ.listeners.PlayerJoinListener;
import craftZ.listeners.PlayerMoveListener;
import craftZ.listeners.PlayerPickupItemListener;
import craftZ.listeners.PlayerQuitListener;
import craftZ.listeners.PlayerTeleportListener;
import craftZ.listeners.ProjectileHitListener;
import craftZ.listeners.ShearEntityListener;
import craftZ.listeners.SheepDyeWoolListener;
import craftZ.listeners.SignChangeListener;
import craftZ.listeners.StructureGrowListener;
import craftZ.listeners.VehicleBlockCollisionListener;
import craftZ.listeners.VehicleMoveListener;
import craftZ.listeners.VehicleUpdateListener;
import craftZ.listeners.WeatherChangeListener;
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
				
				ZombieSpawner.onServerTick();
				AnimalSpawner.onServerTick(tickID);
				ChestRefiller.onServerTick();
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
					
					if (sender.hasPermission("craftz.smasher")) {
						String msg_craftz_help_smasher = ChatColor.YELLOW + this.getLangConfig().getString("Messages.help.smasher-command");
						sender.sendMessage(msg_craftz_help_smasher);
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
						
						String value_lobbySet = ChatColor.AQUA + this.getLangConfig().getString("Messages.cmd.setlobby");
						p.sendMessage(value_lobbySet);
						
					} else {
						String value_notEnoughPerms = ChatColor.DARK_RED + this.getLangConfig()
								.getString("Messages.errors.not-enough-permissions");
						p.sendMessage(value_notEnoughPerms);
					}
					
				}
				
				
				
				if (args[0].equalsIgnoreCase("smasher")) {
					
					if (!(sender instanceof Player)) {
						return true;
					}
					
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
			this.getConfig().addDefault("Config.world.name", "world");
			this.getConfig().addDefault("Config.world.lobby.radius", 20);
			this.getConfig().addDefault("Config.world.lobby.x", 0);
			this.getConfig().addDefault("Config.world.lobby.y", 64);
			this.getConfig().addDefault("Config.world.lobby.z", 0);
			this.getConfig().addDefault("Config.world.real-time", true);
			this.getConfig().addDefault("Config.world.world-border.enable", true);
			this.getConfig().addDefault("Config.world.world-border.radius", 400);
				
				// WORLDCHANGE
				this.getConfig().addDefault("Config.world.world-changing.allow-burning", false);
				this.getConfig().addDefault("Config.world.world-changing.allow-block-grow", false);
				this.getConfig().addDefault("Config.world.world-changing.allow-tree-grow", false);
				this.getConfig().addDefault("Config.world.world-changing.allow-grass-grow", false);
				this.getConfig().addDefault("Config.world.world-changing.allow-new-chunks", true);
				
				// WEATHER
				this.getConfig().addDefault("Config.world.weather.allowWeatherChanging", true);
				
			// PLAYERS
				
				this.getConfig().addDefault("Config.players.use-scoreboard-for-stats", false);
				this.getConfig().addDefault("Config.players.kick-on-death", false);
			
				// INTERACT
				this.getConfig().addDefault("Config.players.interact.shearing", false);
				this.getConfig().addDefault("Config.players.interact.sleeping", false);
				
					// BLOCKS
					this.getConfig().addDefault("Config.players.interact.block-breaking", false);
					this.getConfig().addDefault("Config.players.interact.block-placing", false);
					this.getConfig().addDefault("Config.players.interact.allow-spiderweb-placing", true);
				
				// MEDICAL
				this.getConfig().addDefault("Config.players.medical.enable-sugar-speed-effect", true);
				this.getConfig().addDefault("Config.players.medical.bleeding.enable", true);
				this.getConfig().addDefault("Config.players.medical.bleeding.chance", 0.04);
				this.getConfig().addDefault("Config.players.medical.bleeding.heal-with-paper", true);
				this.getConfig().addDefault("Config.players.medical.healing.heal-with-rosered", true);
				this.getConfig().addDefault("Config.players.medical.healing.only-healing-others", true);
				this.getConfig().addDefault("Config.players.medical.poisoning.enable", true);
				this.getConfig().addDefault("Config.players.medical.poisoning.chance", 0.04);
				this.getConfig().addDefault("Config.players.medical.poisoning.cure-with-limegreen", true);
			
			// MOBS
				this.getConfig().addDefault("Config.mobs.blood-particles-when-damaged", true);
			
				// ZOMBIES
				
					// DROPS
					this.getConfig().addDefault("Config.mobs.zombies.enable-drops", true);
					this.getConfig().addDefault("Config.mobs.zombies.drops.chance", 0.3);
					this.getConfig().addDefault("Config.mobs.zombies.drops.items", new String[] { "262", "2x367" });
					
					// SPAWNING
					this.getConfig().addDefault("Config.mobs.zombies.spawning.interval", 40);
					this.getConfig().addDefault("Config.mobs.zombies.spawning.maxzombies", 200);
					this.getConfig().addDefault("Config.mobs.zombies.spawning.enable-auto-spawn", false);
					this.getConfig().addDefault("Config.mobs.zombies.spawning.auto-spawning-interval", 40);
				
//				// ANIMALS
//				
//					// SPAWNS
//					this.getConfig().addDefault("Config.mobs.animals.spawning.enable", true);
//					
//						// CHANCE
//						this.getConfig().addDefault("Config.mobs.animals.spawning.chance.cow", 0.1);
//						this.getConfig().addDefault("Config.mobs.animals.spawning.chance.chicken", 0.1);
//						this.getConfig().addDefault("Config.mobs.animals.spawning.chance.pig", 0.1);
//						this.getConfig().addDefault("Config.mobs.animals.spawning.chance.sheep", 0.1);
					
			// CHAT
			this.getConfig().addDefault("Config.chat.modify-join-and-quit-messages", true);
			this.getConfig().addDefault("Config.chat.modify-player-messages", false);
			this.getConfig().addDefault("Config.chat.modify-death-messages", true);
			
			// VEHICLES
			this.getConfig().addDefault("Config.vehicles.enable", false);
			this.getConfig().addDefault("Config.vehicles.speed", 5.0);
			this.getConfig().addDefault("Config.vehicles.speed-street-multiplier", 1.6);
			this.getConfig().addDefault("Config.vehicles.speed-street-blocks", new String[] { "35:7", "35", "35:15" });
			this.getConfig().addDefault("Config.vehicles.street-border-blocks", new String[] { "43", "44" });
			
			// ITEMNAMES
			this.getConfig().addDefault("Config.change-item-names.enable", true);
			this.getConfig().addDefault("Config.change-item-names.names", new String[] { "339=Bandage", "351:1=Blood Bag", "351:10=Antibiotics", "368=Grenade" });
			
			
			
		
		
		
		
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
		this.getLangConfig().addDefault("Messages.harvested-tree", "A pile of wood has been successfully added to your inventory.");
		this.getLangConfig().addDefault("Messages.already-have-wood", "You already have wood.");
		this.getLangConfig().addDefault("Messages.isnt-a-tree", "You must be in a forest and close to a tree to harvest wood.");
		this.getLangConfig().addDefault("Messages.destroyed-sign", "You just destroyed a CraftZ sign.");
		this.getLangConfig().addDefault("Messages.successfully-created", "Successfully created!");
		this.getLangConfig().addDefault("Messages.spawned", "You're at spawnpoint %s");
		this.getLangConfig().addDefault("Messages.died", "You died! Zombies killed: %z, players killed: %p, minutes survived: %m");
		this.getLangConfig().addDefault("Messages.bleeding", "You are bleeding! You need a bandage to mend the wounds!");
		this.getLangConfig().addDefault("Messages.bandaged", "Your wounds are now bandaged.");
		this.getLangConfig().addDefault("Messages.bloodbag", "Your health is restored.");
		this.getLangConfig().addDefault("Messages.poisoned", "You are poisoned! You should use antibiotics soon.");
		this.getLangConfig().addDefault("Messages.unpoisoned", "Your poisoning is healed!");
		this.getLangConfig().addDefault("Messages.out-of-world", "You're in a very infected area! Go back, or you will die soon!");
		this.getLangConfig().addDefault("Messages.killed.zombie", "Killed the zombie! Total zombie kills: %k");
		this.getLangConfig().addDefault("Messages.killed.player", "Killed %p! Total player kills: %k");
		
			// HELP
			this.getLangConfig().addDefault("Messages.help.title", "=== CraftZ Help ===");
			this.getLangConfig().addDefault("Messages.help.help-command", "/craftz: Displays this help menu.");
			this.getLangConfig().addDefault("Messages.help.removeitems-command", "/craftz removeitems: Removes all items in the world. (Alias: /craftz remitems)");
			this.getLangConfig().addDefault("Messages.help.reload-command", "/craftz reload: Reload the configuration files.");
			this.getLangConfig().addDefault("Messages.help.spawn-command", "/craftz spawn: Spawn at a random point inside of the world.");
			this.getLangConfig().addDefault("Messages.help.setlobby-command", "/craftz setlobby: Set the lobby location where you're standing.");
			this.getLangConfig().addDefault("Messages.help.smasher-command", "/craftz smasher: Get the ultimate zombie smasher!");
			
			// COMMAND
			this.getLangConfig().addDefault("Messages.cmd.removed-items", "Removed %i items.");
			this.getLangConfig().addDefault("Messages.cmd.reloaded", "Reloaded the config files.");
			this.getLangConfig().addDefault("Messages.cmd.setlobby", "The lobby center is set at your location. For lobby radius, see configuration file.");
			
			// ERRORS
			this.getLangConfig().addDefault("Messages.errors.mustBePlayer", "You must be a player to use this command.");
			this.getLangConfig().addDefault("Messages.errors.tooFewArguments", "Too few arguments given.");
			this.getLangConfig().addDefault("Messages.errors.sign-not-complete", "The sign is not complete.");
			this.getLangConfig().addDefault("Messages.errors.not-enough-permissions", "You don't have the required permission to do this.");
		
		
		
		
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
			this.getLootConfig().addDefault("Loot.settings.time-before-refill", 120);
			this.getLootConfig().addDefault("Loot.settings.min-stacks-filled", 1);
			this.getLootConfig().addDefault("Loot.settings.max-stacks-filled", 3);
			
			// LISTS
			
			String[] value_lists_all = {
				"30", "46", "2x39", "2x40", "258", "259", "2x260", "261", "4x262", "267", "2x268", "272", "3x281", "2x282",
				"2x296", "297",	"298", "299", "300", "301", "302", "303", "304", "305", "306", "307", "308", "309", "339",
				"346", "353", "357", "360", "368", "374", "391", "393", "400", "373:5", "373:16389"
			};
			this.getLootConfig().addDefault("Loot.lists.all", value_lists_all);
			
			
			
			String[] value_lists_military = {
				"30", "258", "261", "4x262", "2x268", "2x281", "2x282", "3x298", "3x299", "3x300", "3x301"
			};
			this.getLootConfig().addDefault("Loot.lists.military", value_lists_military);
			
			
			
			String[] value_lists_militaryEpic = {
				"30", "46", "258", "261", "4x262", "267", "2x268", "272", "2x281", "282", "2x298", "2x299", "2x300",
				"2x301", "302", "303", "304", "305", "306", "307", "308", "309", "368"
			};
			this.getLootConfig().addDefault("Loot.lists.military-epic", value_lists_militaryEpic);
			
			
			
			String[] value_lists_civilian = {
				"2x39", "2x40", "258", "259", "2x260", "2x262", "268", "2x281", "282", "296", "297", "298", "299",
				"300", "301", "346", "357", "360", "391", "393", "400"
			};
			this.getLootConfig().addDefault("Loot.lists.civilian", value_lists_civilian);
			
			
			
			String[] value_lists_farms = {
				"3x39", "3x40", "258", "259", "4x260", "261", "4x262", "2x268", "4x281", "2x282", "2x296", "2x297",
				"298", "299", "300", "301", "346", "353", "357", "360", "374", "391", "393", "400"
			};
			this.getLootConfig().addDefault("Loot.lists.farms", value_lists_farms);
			
			
			
			String[] value_lists_industrial = {
				"30", "4x262", "2x268", "296"
			};
			this.getLootConfig().addDefault("Loot.lists.industrial", value_lists_industrial);
			
			
			
			String[] value_lists_barracks = {
				"2x39", "2x40", "260", "262", "268", "281"
			};
			this.getLootConfig().addDefault("Loot.lists.barracks", value_lists_barracks);
			
			
			
			String[] value_lists_medical = {
				"2x260", "2x281", "2x282", "339", "2x351:1", "351:10", "2x353", "357", "360", "374", "391",
				"2x373:5", "373:16389"
			};
			this.getLootConfig().addDefault("Loot.lists.medical", value_lists_medical);
		
		
		
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
package craftZ;

import java.util.*;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import craftZ.util.Dynmap;
import craftZ.util.EntityChecker;
import craftZ.util.ItemRenamer;
import craftZ.util.PlayerData;
import craftZ.util.ScoreboardHelper;
import craftZ.worldData.PlayerSpawnpoint;
import craftZ.worldData.WorldData;


public class PlayerManager {
	
	private static List<PlayerSpawnpoint> spawns = new ArrayList<PlayerSpawnpoint>();
	private static Map<UUID, PlayerData> players = new HashMap<UUID, PlayerData>();
	private static Map<UUID, Integer> movingPlayers = new HashMap<UUID, Integer>();
	private static Map<UUID, Long> lastDeaths = new HashMap<UUID, Long>();
	
	
	
	public static Player p(UUID uuid) {
		return Bukkit.getPlayer(uuid);
	}
	
	
	
	
	
	public static ConfigurationSection getConfig() {
		return WorldData.get();
	}
	
	public static void saveConfig() {
		WorldData.save();
	}
	
	
	
	
	
	public static boolean hasPlayer(Player p) {
		return hasPlayer(p.getUniqueId());
	}
	
	public static boolean hasPlayer(UUID uuid) {
		return players.containsKey(uuid);
	}
	
	
	
	
	
	public static void savePlayer(Player p) {
		
		if (hasPlayer(p)) {
			getConfig().set("Data.players." + p.getUniqueId(), getData(p).toString());
			saveConfig();
		}
		
	}
	
	public static void saveAllPlayers() {
		
		for (Player p : CraftZ.world().getPlayers()) {
			if (hasPlayer(p)) {
				getConfig().set("Data.players." + p.getUniqueId(), getData(p).toString());
			}
		}
		
		saveConfig();
		
	}
	
	
	
	
	
	public static void loadPlayer(Player p, boolean forceRespawn) {
		
		if (hasPlayer(p) && !forceRespawn) {
			return;
		}
		
		FileConfiguration config = ConfigManager.getConfig("config");
		
		
		
		int invulnTime = 0;
		
		if (existsInConfig(p) && !forceRespawn) {
			
			putPlayer(p, false);
			p.setLevel(players.get(p.getUniqueId()).thirst);
			
			invulnTime = (int) (config.getDouble("Config.players.invulnerability.on-return") * 20);
			
		} else {
			
			putPlayer(p, true);
			savePlayer(p);
			
			if (config.getBoolean("Config.players.clear-inventory-on-spawn")) {
				
				PlayerInventory inv = p.getInventory();
				
				for (int i=0; i<inv.getSize(); i++) {
					ItemStack stack = inv.getItem(i);
					if (!Kits.isSoulbound(stack))
						inv.setItem(i, null);
				}
				
				if (!Kits.isSoulbound(inv.getHelmet()))
					inv.setHelmet(null);
				if (!Kits.isSoulbound(inv.getChestplate()))
					inv.setChestplate(null);
				if (!Kits.isSoulbound(inv.getLeggings()))
					inv.setLeggings(null);
				if (!Kits.isSoulbound(inv.getBoots()))
					inv.setBoots(null);
				
			}
			
			p.setHealth(p.getMaxHealth());
			p.setFoodLevel(20);
			
			spawnPlayerAtRandomSpawn(p);
			
			p.setLevel(players.get(p.getUniqueId()).thirst);
			
			invulnTime = (int) (config.getDouble("Config.players.invulnerability.on-spawn") * 20);
			
		}
		
		p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, invulnTime, 1000));
		
		ScoreboardHelper.addPlayer(p);
		
	}
	
	private static void putPlayer(Player p, boolean defaults) {
		
		if (defaults) {
			players.put(p.getUniqueId(), new PlayerData(20, 0, 0, 0, false, false, false));
		} else {
			String s = getConfig().getString("Data.players." + p.getUniqueId());
			players.put(p.getUniqueId(), PlayerData.fromString(s));
		}
		
	}
	
	
	
	
	
	public static void resetPlayer(Player p) {
		
		if (hasPlayer(p)) {
			PlayerData data = getData(p);
			addToHighscores(p, data);
		}
		
		getConfig().set("Data.players." + p.getUniqueId(), null);
		saveConfig();
		
		ScoreboardHelper.removePlayer(p.getUniqueId());
		players.remove(p.getUniqueId());
		
	}
	
	
	
	
	
	public static int loadSpawns() {
		
		spawns.clear();
		
		ConfigurationSection sec = WorldData.get().getConfigurationSection("Data.playerspawns");
		if (sec != null) {
			
			for (String entry : sec.getKeys(false)) {
				
				ConfigurationSection data = sec.getConfigurationSection(entry);
				
				PlayerSpawnpoint spawn = new PlayerSpawnpoint(data);
				spawns.add(spawn);
				
			}
			
		}
		
		return spawns.size();
		
	}
	
	
	
	
	
	public static String makeSpawnID(Location signLoc) {
		return "x" + signLoc.getBlockX() + "y" + signLoc.getBlockY() + "z" + signLoc.getBlockZ();
	}
	
	public static PlayerSpawnpoint getSpawnpoint(String signID) {
		
		for (PlayerSpawnpoint spawn : spawns) {
			if (spawn.getID().equals(signID))
				return spawn;
		}
		
		return null;
	}
	
	public static PlayerSpawnpoint getSpawnpoint(Location signLoc) {
		return getSpawnpoint(makeSpawnID(signLoc));
	}
	
	
	
	
	
	public static void addSpawn(Location signLoc, String name) {
		
		String id = makeSpawnID(signLoc);
		
		PlayerSpawnpoint spawn = new PlayerSpawnpoint(id, signLoc, name);
		spawns.add(spawn);
		
		spawn.save();
		
		Dynmap.createMarker(Dynmap.SET_PLAYERSPAWNS, "playerspawn_" + id, "Spawn: " + name, signLoc, Dynmap.ICON_PLAYERSPAWN);
		
	}
	
	public static void removeSpawn(String signID) {
		
		WorldData.get().set("Data.playerspawns." + signID, null);
		WorldData.save();
		
		PlayerSpawnpoint spawn = getSpawnpoint(signID);
		if (spawn != null)
			spawns.remove(spawn);
		
		Dynmap.removeMarker(Dynmap.getMarker(Dynmap.SET_PLAYERSPAWNS, "playerspawn_" + signID));
		
	}
	
	
	
	
	
	public static void spawnPlayerAtRandomSpawn(Player p) {
		
		if (!getConfig().contains("Data.playerspawns"))
			return;
		
		if (spawns.isEmpty())
			return;
		
		PlayerSpawnpoint spawn = spawns.get(CraftZ.RANDOM.nextInt(spawns.size()));
		
		p.teleport(spawn.getSafeLocation());
		p.sendMessage(ChatColor.YELLOW + CraftZ.getMsg("Messages.spawned").replaceAll("%s", spawn.getName()));
		
	}
	
	
	
	
	
	public static int getRespawnCountdown(Player player) {
		if (!lastDeaths.containsKey(player.getUniqueId()) || player.hasPermission("craftz.instantRespawn"))
			return 0;
		int countdown = ConfigManager.getConfig("config").getInt("Config.players.respawn-countdown");
		return (int) (countdown*1000 - (System.currentTimeMillis() - lastDeaths.get(player.getUniqueId())));
	}
	
	public static void setLastDeath(Player p, long timestamp) {
		lastDeaths.put(p.getUniqueId(), timestamp);
	}
	
	
	
	
	
	public static PlayerData getData(UUID p) {
		if (!players.containsKey(p))
			loadPlayer(p(p), false);
		return players.get(p);
	}
	
	public static PlayerData getData(Player p) {
		if (!players.containsKey(p.getUniqueId()))
			loadPlayer(p, false);
		return players.get(p.getUniqueId());
	}
	
	
	
	
	
	public static void onServerTick(long tickID) {
		
		for (Iterator<Entry<UUID, PlayerData>> it=players.entrySet().iterator(); it.hasNext(); ) {
			
			Entry<UUID, PlayerData> entry = it.next();
			UUID id = entry.getKey();
			PlayerData data = entry.getValue();
			
			Player p = p(id);
			
			if (!isPlaying(id)) {
				
				if (p != null)
					savePlayer(p);
				
				ScoreboardHelper.removePlayer(id);
				
				it.remove();
				continue;
				
			}
			
			boolean survival = p.getGameMode() != GameMode.CREATIVE && p.getGameMode() != GameMode.SPECTATOR;
			Location ploc = p.getLocation();
			
			
			
			updateVisibility(p);
			
			
			
			if (survival && ConfigManager.getConfig("config").getBoolean("Config.players.medical.thirst.enable")) {
				
				Biome biome = ploc.getBlock().getBiome();
				boolean desert = biome == Biome.DESERT || biome == Biome.DESERT_HILLS || biome == Biome.DESERT_MOUNTAINS;
				int ticksNeeded = desert ? ConfigManager.getConfig("config").getInt("Config.players.medical.thirst.ticks-desert")
						: ConfigManager.getConfig("config").getInt("Config.players.medical.thirst.ticks-normal");
				
				if (tickID % ticksNeeded == 0) {
					
					if (data.thirst > 0) {
						data.thirst--;
						p.setLevel(data.thirst);
					} else {
						p.damage(2);
					}
					
					if (ConfigManager.getConfig("config").getBoolean("Config.players.medical.thirst.show-messages")) {
						
						if (data.thirst <= 8 && data.thirst > 1 && data.thirst % 2 == 0) {
							p.sendMessage(ChatColor.RED + CraftZ.getMsg("Messages.thirsty"));
						} else if (data.thirst <= 1) {
							p.sendMessage(ChatColor.DARK_RED + CraftZ.getMsg("Messages.thirsty-dehydrating"));
						}
						
					}
					
				}
				
			}
			
			
			
			if (survival && tickID % 1200 == 0) {
				data.minutesSurvived++;
			}
			
			
			
			if (tickID % 10 == 0) {
				ItemRenamer.convertInventory(p, ItemRenamer.DEFAULT_MAP);
				ScoreboardHelper.update();
			}
			
			
			
			if (tickID % 30 == 0) {
				
				if (survival && ConfigManager.getConfig("config").getBoolean("Config.world.world-border.enable")) {
					
					double dmg = getWorldBorderDamage(ploc);
					
					if (dmg > 0) {
						if (tickID % 200 == 0)
							p.sendMessage(CraftZ.getPrefix() + " " + CraftZ.getMsg("Messages.out-of-world"));
						p.damage(dmg);
						p.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 20, 1));
						p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 1));
						p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 80, 1));
					}
					
				}
				
			}
			
			
			
			if (survival && tickID % 200 == 0) {
				
				if (data.bleeding) {
					p.damage(1);
				}
				
				if (data.poisoned) {
					p.damage(1);
					p.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 20, 1));
					p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10, 1));
					p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 30, 1));
				}
				
			}
			
			
			
			if (survival && data.bonesBroken) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 2), true);
			}
			
			
			
			if (survival && ConfigManager.getConfig("config").getBoolean("Config.mobs.zombies.pull-players-down")
					&& tickID % 20 == 0 && Math.random() < 0.15) {
				
				List<Entity> entities = EntityChecker.getNearbyEntities(p, 2.5);
				for (Entity ent : entities) {
					
					if (ent.getType() == EntityType.ZOMBIE) {
						Location zloc = ent.getLocation();
						if (zloc.getY() + 1 < ploc.getY()) {
							p.setVelocity(zloc.toVector().subtract(ploc.toVector()).normalize().multiply(0.5 + Math.random()*0.4));
						}
					}
					
				}
				
			}
			
		}
		
		
		
		
		
		for (Iterator<Entry<UUID, Integer>> it=movingPlayers.entrySet().iterator(); it.hasNext(); ) {
			
			Entry<UUID, Integer> entry = it.next();
			int v = entry.getValue() + 1;
			entry.setValue(v);
			
			if (v > 8)
				it.remove();
			
		}
		
	}
	
	
	
	
	
	public static void onPlayerMove(Player p, double distance) {
		
		if (distance > 0) {
			movingPlayers.put(p.getUniqueId(), 0);
		}
		
	}
	
	public static boolean isMoving(Player p) {
		return movingPlayers.containsKey(p.getUniqueId());
	}
	
	
	
	
	
	public static double getWorldBorderDistance(Location ploc) {
		
		ConfigurationSection sec = ConfigManager.getConfig("config").getConfigurationSection("Config.world.world-border");
		int r = sec.getInt("radius");
		String shape = sec.getString("shape");
		
		Location loc = new Location(CraftZ.world(), sec.getDouble("x"), ploc.getY(), sec.getDouble("z"));
		if (!ploc.getWorld().getName().equals(loc.getWorld().getName()))
			return 0;
		
		double dist;
		
		if (shape.equalsIgnoreCase("square") || shape.equalsIgnoreCase("rect")) {
			
			int x = loc.getBlockX(), z = loc.getBlockZ();
			int px = ploc.getBlockX(), pz = ploc.getBlockZ();
			
			int dx = Math.max(Math.max((x-r) - px, 0), px - (x+r));
			int dy = Math.max(Math.max((z-r) - pz, 0), pz - (z+r));
			
			dist = Math.sqrt(dx*dx + dy*dy);
			
		} else {
			dist = ploc.distance(loc) - r;
		}
		
		return dist < 0 ? 0 : dist;
		
	}
	
	public static double getWorldBorderDamage(Location ploc) {
		return getWorldBorderDistance(ploc) * ConfigManager.getConfig("config").getDouble("Config.world.world-border.rate");
	}
	
	
	
	
	
	public static boolean isLobby(Location loc) {
		
		Location lobby = getLobby();
		int radius = ConfigManager.getConfig("config").getInt("Config.world.lobby.radius");
		
		return loc.getWorld().getName().equals(lobby.getWorld().getName()) && lobby.distance(loc) <= radius;
		
	}
	
	public static boolean isInsideOfLobby(Player p) {
		return isLobby(p.getLocation());
	}
	
	
	
	
	
	public static Location getLobby() {
		
		World cw = CraftZ.world();
		if (cw == null)
			return null;
		Location lobby = cw.getSpawnLocation();
		ConfigurationSection sec = ConfigManager.getConfig("config").getConfigurationSection("Config.world.lobby");
		
		String ws = sec.getString("world");
		World w = ws == null ? null : Bukkit.getWorld(ws);
		if (w != null)
			lobby.setWorld(w);
		lobby.setX(sec.getDouble("x"));
		lobby.setY(sec.getDouble("y"));
		lobby.setZ(sec.getDouble("z"));
		lobby.setYaw((float) sec.getDouble("yaw"));
		lobby.setPitch((float) sec.getDouble("pitch"));
		
		return lobby;
		
	}
	
	
	
	
	
	public static void updateVisibility(Player p) {
		
		float visibility = 0.32F;
		
		boolean mov = isMoving(p);
		
		if (!mov)
			visibility -= 0.25f;
		
		if (p.isSneaking())
			visibility -= mov ? 0.15f : 0.3f;
		if (p.isSprinting())
			visibility = 0.6f;
		if (p.isInsideVehicle())
			visibility = mov ? 1.0f : visibility*4;
		
		if (p.getLocation().getBlock().getType() != Material.AIR)
			visibility -= 0.15f;
		
		if (p.isSleeping())
			visibility /= 4;
		
		p.setExp(visibility > 0f ? visibility : 0f);
		
	}
	
	public static float getVisibility(Player p) {
		return p.getExp();
	}
	
	
	
	
	
	public static boolean existsInConfig(Player p) {
		return getConfig().contains("Data.players." + p.getUniqueId());
	}
	
	public static boolean existsInWorld(Player p) {
		return players.containsKey(p.getUniqueId());
	}
	
	
	
	
	
	public static int getPlayerCount() {
		return players.size();
	}
	
	
	
	
	
	public static Player randomPlayer() {
		
		List<Player> players = CraftZ.world().getPlayers();
		if (players.isEmpty())
			return null;
		Collections.shuffle(players);
		
		for (int i=0; i<players.size(); i++) {
			Player chosen = players.get(i);
			if (!isInsideOfLobby(chosen))
				return chosen;
		}
		
		return null;
		
	}
	
	
	
	
	
	public static boolean isPlaying(Player p) {
		return players.containsKey(p.getUniqueId()) && CraftZ.isWorld(p.getWorld()) && !isInsideOfLobby(p);
	}
	
	public static boolean isPlaying(UUID id) {
		Player p = p(id);
		return p != null && players.containsKey(id) && CraftZ.isWorld(p.getWorld()) && !isInsideOfLobby(p);
	}
	
	
	
	
	
	public static Map<String, Integer> getHighscores(String category) {
		
		LinkedHashMap<String, Integer> scores = new LinkedHashMap<String, Integer>();
		
		ConfigurationSection sec = ConfigManager.getConfig("highscores").getConfigurationSection("Highscores." + category);
		if (sec != null) {
			for (String player : sec.getKeys(false)) {
				scores.put(player, sec.getInt(player));
			}
		}
		
		return scores;
		
	}
	
	
	
	
	
	public static SortedSet<Map.Entry<String, Integer>> sortHighscores(Map<String, Integer> scoresMap) {
		
		SortedSet<Map.Entry<String, Integer>> scores = new TreeSet<Map.Entry<String, Integer>>(new Comparator<Map.Entry<String, Integer>>() {
			@Override
			public int compare(Map.Entry<String, Integer> e1, Map.Entry<String, Integer> e2) {
				int res = e2.getValue().compareTo(e1.getValue());
				return res != 0 ? res : 1;
			}
		});
		
		scores.addAll(scoresMap.entrySet());
		
		return scores;
		
	}
	
	
	
	
	
	public static void addToHighscores(Player p, PlayerData data) {
		
		addToHighscores(p, data.minutesSurvived, "minutes-survived");
		addToHighscores(p, data.zombiesKilled, "zombies-killed");
		addToHighscores(p, data.playersKilled, "players-killed");
		
	}
	
	public static void addToHighscores(Player p, int v, String category) {
		
		Map<String, Integer> scores = getHighscores(category);
		SortedSet<Map.Entry<String, Integer>> scoresSorted = sortHighscores(scores);
		
		if (scores.containsKey(p.getName())) {
			int score = scores.get(p.getName());
			if (v < score) {
				return;
			}
		}
		
		Map.Entry<String, Integer> scoresLast = scores.isEmpty() ? null : scoresSorted.last();
		if (scores.size() < 10 || scoresLast.getValue() < v) {
			scores.put(p.getName(), v);
			scores.remove(scoresLast);
		}
		
		if (scores.size() > 10) {
			scores.remove(scoresLast);
		}
		
		ConfigManager.getConfig("highscores").createSection("Highscores." + category, scores);
		ConfigManager.saveConfig("highscores");
		
	}
	
	
	
	
	
	public static void onDynmapEnabled() {
		
		FileConfiguration config = ConfigManager.getConfig("config");
		
		
		
		Dynmap.clearSet(Dynmap.SET_WORLDBORDER);
		
		if (config.getBoolean("Config.dynmap.show-worldborder") && config.getBoolean("Config.world.world-border.enable")) {
			
			double r = config.getDouble("Config.world.world-border.radius");
			Dynmap.createCircleMarker(Dynmap.SET_WORLDBORDER, "worldborder", "World Border", 6, 0.4, 0xEE2222, getLobby(), r, r);
			
		}
		
		
		
		Dynmap.clearSet(Dynmap.SET_PLAYERSPAWNS);
		
		if (config.getBoolean("Config.dynmap.show-playerspawns")) {
			
			for (PlayerSpawnpoint spawn : spawns) {
				String id = "playerspawn_" + spawn.getID();
				String label = "Spawn: " + spawn.getName();
				Dynmap.createMarker(Dynmap.SET_PLAYERSPAWNS, id, label, spawn.getLocation(), Dynmap.ICON_PLAYERSPAWN);
			}
			
		}
		
	}
	
}
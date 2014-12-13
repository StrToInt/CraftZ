package craftZ.util;

import java.util.*;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import craftZ.CraftZ;


public class PlayerManager {
	
	private static Map<UUID, PlayerData> players = new HashMap<UUID, PlayerData>();
	private static Map<UUID, Integer> movingPlayers = new HashMap<UUID, Integer>();
	
	
	
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
		
		
		
		p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 30, 1000));
		
		if (existsInConfig(p) && !forceRespawn) {
			
			putPlayer(p, false);
			p.setLevel(players.get(p.getUniqueId()).thirst);
			
		} else {
			
			if (ConfigManager.getConfig("config").getBoolean("Config.players.clear-inventory-on-spawn")) {
				p.getInventory().clear();
				p.getInventory().setArmorContents(new ItemStack[4]);
			}
			
			p.setHealth(p.getMaxHealth());
			p.setFoodLevel(20);
			spawnPlayerAtRandomSpawn(p);
			
			putPlayer(p, true);
			
			savePlayer(p);
			
			p.setLevel(players.get(p.getUniqueId()).thirst);
			
		}
		
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
		
		getConfig().set("Data.players." + p.getUniqueId(), null);
		saveConfig();
		
		ScoreboardHelper.removePlayer(p.getUniqueId());
		players.remove(p.getUniqueId());
		
	}
	
	
	
	
	
	public static void spawnPlayerAtRandomSpawn(Player p) {
		
		if (!getConfig().contains("Data.playerspawns"))
			return;
		
		ConfigurationSection sec = getConfig().getConfigurationSection("Data.playerspawns");
		if (sec == null)
			return;
		Set<String> spawnsset = sec.getKeys(false);
		String[] spawns = spawnsset.toArray(new String[spawnsset.size()]);
		
		if (spawns.length < 1)
			return;
		
		int spawn = CraftZ.RANDOM.nextInt(spawns.length);
		
		ConfigurationSection ssec = getConfig().getConfigurationSection("Data.playerspawns." + spawns[spawn]);
		if (ssec == null)
			return;
		
		Location loc = new Location(CraftZ.world(), ssec.getInt("coords.x"), ssec.getInt("coords.y"), ssec.getInt("coords.z"));
		p.teleport(BlockChecker.getSafeSpawnLocationOver(loc));
		p.sendMessage(ChatColor.YELLOW + CraftZ.getMsg("Messages.spawned").replaceAll("%s", ssec.getString("name")));
		
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
			
			if (!isPlaying(id)) {
				
				Player p = p(id);
				if (p != null) savePlayer(p);
				
				ScoreboardHelper.removePlayer(id);
				
				it.remove();
				continue;
				
			}
			
			
			
			Player p = p(id);
			PlayerData data = players.get(id);
			
			boolean survival = p.getGameMode() != GameMode.CREATIVE && p.getGameMode() != GameMode.SPECTATOR;
			
			
			
			updateVisibility(p);
			
			
			
			if (survival && ConfigManager.getConfig("config").getBoolean("Config.players.medical.thirst.enable")) {
				
				Biome biome = p.getLocation().getBlock().getBiome();
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
			
			
			
			if (tickID % 1200 == 0) {
				data.minutesSurvived++;
			}
			
			
			
			if (tickID % 10 == 0) {
				ItemRenamer.convertPlayerInventory(p, ConfigManager.getConfig("config").getStringList("Config.change-item-names.names"));
				ScoreboardHelper.update();
			}
			
			
			
			if (tickID % 30 == 0) {
				
				if (survival && ConfigManager.getConfig("config").getBoolean("Config.world.world-border.enable")) {
					
					double dmg = getWorldBorderDamage(p, ConfigManager.getConfig("config").getDouble("Config.world.world-border.radius"), getLobby());
					
					if (dmg > 0) {
						if (tickID % 200 == 0)
							p.sendMessage(CraftZ.getPrefix() + " " + CraftZ.getMsg("Messages.out-of-world"));
						p.damage(dmg);
						p.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 20, 1));
						p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 1));
						p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 50, 1));
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
	
	
	
	
	
	public static double getWorldBorderDamage(Player p, double radius, Location spawn) {
		
		//TODO: make compatible with lobby in different world.
		//Perhaps add configurable world center?
		
		Location ploc = p.getLocation();
		Location loc = new Location(ploc.getWorld(), spawn.getX(), ploc.getY(), spawn.getZ());
		
		double dist = ploc.distance(loc) - radius;
		if (dist <= 0) {
			return 0;
		} else {
			return dist / 60D;
		}
		
	}
	
	
	
	
	
	public static boolean isInsideOfLobby(Player p) {
		
		Location lobby = getLobby();
		int radius = ConfigManager.getConfig("config").getInt("Config.world.lobby.radius");
		
		return p.getWorld().getName().equals(lobby.getWorld().getName()) && lobby.distance(p.getLocation()) <= radius;
		
	}
	
	
	
	
	
	public static Location getLobby() {
		
		Location lobby = CraftZ.world().getSpawnLocation();
		ConfigurationSection sec = ConfigManager.getConfig("config").getConfigurationSection("Config.world.lobby");
		
		String w = sec.getString("world");
		if (w != null)
			lobby.setWorld(Bukkit.getWorld(w));
		lobby.setX(sec.getDouble("x"));
		lobby.setY(sec.getDouble("y"));
		lobby.setZ(sec.getDouble("z"));
		
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
	
}
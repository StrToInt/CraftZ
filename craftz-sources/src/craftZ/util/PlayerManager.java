package craftZ.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import craftZ.CraftZ;


public class PlayerManager {
	
	private static HashMap<String, AdditionalCraftZData> players = new HashMap<String, AdditionalCraftZData>();
	
	
	
	public static ConfigurationSection getConfig() {
		return WorldData.get();
	}
	
	
	
	
	
	public static void savePlayerToConfig(Player p) {
		
		if (players.containsKey(p.getName())) {
			
			getConfig().set("Data.players." + p.getName() + ".thirst", getData(p.getName()).thirst);
			getConfig().set("Data.players." + p.getName() + ".zombiesKilled", getData(p.getName()).zombiesKilled);
			getConfig().set("Data.players." + p.getName() + ".playersKilled", getData(p.getName()).playersKilled);
			getConfig().set("Data.players." + p.getName() + ".minsSurvived", getData(p.getName()).minutesSurvived);
			getConfig().set("Data.players." + p.getName() + ".bleeding", getData(p.getName()).bleeding);
			getConfig().set("Data.players." + p.getName() + ".poisoned", getData(p.getName()).poisoned);
			getConfig().set("Data.players." + p.getName() + ".bonesBroken", getData(p.getName()).bonesBroken);
			
			WorldData.save();
			
		}
		
	}
	
	
	
	
	
	public static void loadPlayer(Player p, boolean forceRespawn) {
		
		if (players.containsKey(p.getName()) && !forceRespawn) {
			return;
		}
		
		
		
		p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 30, 1000));
		
		if (wasAlreadyInWorld(p) && !forceRespawn) {
			
			putPlayer(p, false);
			p.setLevel(players.get(p.getName()).thirst);
			
		} else {
			
			p.getInventory().clear();
			p.getInventory().setArmorContents(new ItemStack[4]);
			
			p.setHealth(20);
			p.setFoodLevel(20);
			spawnPlayerAtRandomSpawn(p);
			
			putPlayer(p, true);
			
			savePlayerToConfig(p);
			
			p.setLevel(players.get(p.getName()).thirst);
			
		}
		
		ScoreboardHelper.createPlayer(p);
		
	}
	
	
	
	
	
	private static void putPlayer(Player p, boolean defaults) {
		
		if (defaults)
			players.put(p.getName(), new AdditionalCraftZData(20, 0, 0, 0, false, false, false));
		else
			players.put(p.getName(), new AdditionalCraftZData(getConfig().getInt("Data.players." + p.getName() + ".thirst"),
					getConfig().getInt("Data.players." + p.getName() + ".zombiesKilled"),
					getConfig().getInt("Data.players." + p.getName() + ".playersKilled"),
					getConfig().getInt("Data.players." + p.getName() + ".minsSurvived"),
					getConfig().getBoolean("Data.players." + p.getName() + ".bleeding"),
					getConfig().getBoolean("Data.players." + p.getName() + ".bonesBroken"),
					getConfig().getBoolean("Data.players." + p.getName() + ".poisoned")));
		
		
	}
	
	
	
	
	
	public static void spawnPlayerAtRandomSpawn(Player p) {
		
		if (!getConfig().contains("Data.playerspawns")) return;
		
		Set<String> spts_players_set = getConfig().getConfigurationSection("Data.playerspawns").getKeys(false);
		
		if (spts_players_set != null && !spts_players_set.isEmpty()) {
			
			Object[] spts_players = spts_players_set.toArray();
			int taken = new Random().nextInt(spts_players.length);
			
			ConfigurationSection configSec = getConfig().getConfigurationSection("Data.playerspawns." + spts_players[taken]);
			if (configSec == null) return;
			
			int spnLocX = configSec.getInt("coords.x");
			int spnLocY = configSec.getInt("coords.y");
			int spnLocZ = configSec.getInt("coords.z");
			World spnWorld = CraftZ.world();
			Location spnLoc = new Location(spnWorld, spnLocX, spnLocY, spnLocZ);
			
			p.teleport(BlockChecker.getSafeSpawnLocationOver(spnLoc, true));
			
			p.sendMessage(ChatColor.YELLOW + CraftZ.getMsg("Messages.spawned")
					.replaceAll("%s", configSec.getString("name")));
			
		}
		
	}
	
	
	
	
	
	public static void saveAllPlayersToConfig() {
		
		for (Player p : CraftZ.world().getPlayers())
			savePlayerToConfig(p);
		
	}
	
	
	
	
	
	public static void resetPlayer(Player p) {
		
		getConfig().set("Data.players." + p.getName(), null);
		WorldData.save();
		WorldData.reload();
		
		ScoreboardHelper.removePlayer(p.getName());
		players.remove(p.getName());
		
	}
	
	
	
	
	
	public static AdditionalCraftZData getData(String p) {
		if (!players.containsKey(p)) loadPlayer(Bukkit.getPlayer(p), false);
		return players.get(p);
	}
	
	
	
	
	
	public static void onServerTick(long tickID) {
		
		ArrayList<String> toRemove = new ArrayList<String>();
		
		for (String pn : players.keySet()) {
			
			if (isNotPlaying(pn)) {
				toRemove.add(pn);
				continue;
			}
			
			
			
			Player p = Bukkit.getPlayer(pn);
			AdditionalCraftZData data = players.get(pn);
			
			
			
			if (ConfigManager.getConfig("config").getBoolean("Config.players.medical.thirst.enable")) {
				
				Biome biome = p.getLocation().getBlock().getBiome();
				boolean desert = biome == Biome.DESERT || biome == Biome.DESERT_HILLS || biome == Biome.DESERT_MOUNTAINS;
				
				if (tickID % (desert ? ConfigManager.getConfig("config").getInt("Config.players.medical.thirst.ticks-desert")
						: ConfigManager.getConfig("config").getInt("Config.players.medical.thirst.ticks-normal")) == 0) {
					
					if (data.thirst > 0) {
						data.thirst--;
						p.setLevel(data.thirst);
					} else {
						p.damage(2);
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
				
				if (ConfigManager.getConfig("config").getBoolean("Config.world.world-border.enable")) {
					
					double dmg = getWorldBorderDamage(p, ConfigManager.getConfig("config").getDouble("Config.world.world-border.radius"), getLobby());
					
					if (dmg > 0) {
						if (tickID % 200 == 0)
							p.sendMessage("[CraftZ] " + CraftZ.getMsg("Messages.out-of-world"));
						p.damage(dmg);
						p.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 20, 1));
						p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 1));
						p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 50, 1));
					}
					
				}
				
			}
			
			
			
			if (tickID % 200 == 0) {
				
				if (data.bleeding) p.damage(1);
				
				if (data.poisoned) {
					p.damage(1);
					p.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 20, 1));
					p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10, 1));
					p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 30, 1));
				}
				
			}
			
			
			
			if (data.bonesBroken)
				p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 2), true);
			
		}
		
		
		
		for (String pn : toRemove) {
			
			Player p = Bukkit.getPlayer(pn);
			if (p != null) savePlayerToConfig(p);
			
			ScoreboardHelper.removePlayer(pn);
			
			players.remove(pn);
			
		}
		
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
		String w = ConfigManager.getConfig("config").getString("Config.world.lobby.world");
		if (w != null) lobby.setWorld(Bukkit.getWorld(w));
		lobby.setX(ConfigManager.getConfig("config").getDouble("Config.world.lobby.x"));
		lobby.setY(ConfigManager.getConfig("config").getDouble("Config.world.lobby.y"));
		lobby.setZ(ConfigManager.getConfig("config").getDouble("Config.world.lobby.z"));
		
		return lobby;
		
	}
	
	
	
	
	
	public static boolean wasAlreadyInWorld(Player p) {
		return getConfig().contains("Data.players." + p.getName());
	}
	
	
	
	
	
	public static int getPlayCount() {
		return players.size();
	}
	
	
	
	
	
	public static Player randomPlayer() {
		
		List<Player> players = CraftZ.world().getPlayers();
		if (players.isEmpty()) return null;
		Collections.shuffle(players);
		
		Player chosen = players.get(0);
		
		if (!isInsideOfLobby(chosen)) return chosen;
		
		boolean otherExists = false;
		for (Player p : players) {
			
			if (!isInsideOfLobby(p)) {
				otherExists = true;
				break;
			}
			
		}
		
		if (otherExists) return randomPlayer();
		
		return null;
		
	}
	
	
	
	
	
	public static boolean isNotPlaying(String p) {
		return Bukkit.getPlayer(p) == null || !CraftZ.isWorld(Bukkit.getPlayer(p).getWorld())
				|| isInsideOfLobby(Bukkit.getPlayer(p)) || !players.containsKey(p);
	}
	
}
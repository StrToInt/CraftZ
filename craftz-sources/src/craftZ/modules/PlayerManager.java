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
package craftZ.modules;

import java.util.*;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import craftZ.CraftZ;
import craftZ.Module;
import craftZ.util.ItemRenamer;
import craftZ.util.Rewarder.RewardType;
import craftZ.worldData.PlayerData;
import craftZ.worldData.PlayerSpawnpoint;
import craftZ.worldData.WorldData;


public class PlayerManager extends Module {
	
	private List<PlayerSpawnpoint> spawns = new ArrayList<PlayerSpawnpoint>();
	private Map<UUID, PlayerData> players = new HashMap<UUID, PlayerData>();
	private Map<UUID, Integer> movingPlayers = new HashMap<UUID, Integer>();
	private Map<UUID, Long> lastDeaths = new HashMap<UUID, Long>();
	
	
	
	public PlayerManager(CraftZ craftZ) {
		super(craftZ);
	}
	
	
	
	
	
	public static Player p(UUID uuid) {
		return Bukkit.getPlayer(uuid);
	}
	
	
	
	
	
	@Override
	public void onLoad(boolean configReload) {
		
		spawns.clear();
		
		ConfigurationSection sec = WorldData.get().getConfigurationSection("Data.playerspawns");
		if (sec != null) {
			
			for (String entry : sec.getKeys(false)) {
				
				ConfigurationSection data = sec.getConfigurationSection(entry);
				
				PlayerSpawnpoint spawn = new PlayerSpawnpoint(this, data);
				spawns.add(spawn);
				
			}
			
		}
		
		
		
		for (Player p : world().getPlayers()) {
			joinPlayer(p);
		}
		
	}
	
	@Override
	public void onDisable() {
		saveAllPlayers();
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		
		Player p = event.getPlayer();
		
		if (isWorld(p.getWorld())) {
			joinPlayer(p);
		}
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onWorldChanged(PlayerChangedWorldEvent event) {
		
		Player p = event.getPlayer();
		World w = p.getWorld();
		World f = event.getFrom();
		
		if (isWorld(f)) {
			savePlayer(p);
		} else if (isWorld(w)) {
			joinPlayer(p);
		}
		
	}
	
	public void joinPlayer(Player p) {
		
		if (existsInConfig(p)) {
			loadPlayer(p, false, null);
		} else {
			
			boolean reset = getConfig("config").getBoolean("Config.players.reset-in-lobby");
			
			if (reset || p.getHealth() == 0)
				p.setHealth(p.getMaxHealth());
			
			if (reset) {
				p.setFoodLevel(20);
				p.getInventory().clear();
				p.getInventory().setArmorContents(new ItemStack[4]);
			}
			
			p.teleport(getLobby());
			
			Kit kit = getCraftZ().getKits().getDefaultKit();
			if (kit != null) {
				kit.select(p);
			}
			
		}
		
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		
		Player p = event.getPlayer();
		
		if (isWorld(p.getWorld())) {
			savePlayer(p);
		}
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerKick(PlayerKickEvent event) {
		
		Player p = event.getPlayer();
		
		if (isWorld(p.getWorld())) {
			if (!event.getReason().startsWith(getCraftZ().getPrefix())) {
				savePlayer(p);
			}
		}
		
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDeath(PlayerDeathEvent event) {
		
		final Player p = event.getEntity();
		
		if (isWorld(p.getWorld())) {
			
			FileConfiguration config = getConfig("config");
			
			
			
			Player killer = p.getKiller();
			
			if (killer != null) {
				
				getData(killer).playersKilled++;
				
				if (config.getBoolean("Config.players.send-kill-stat-messages")) {
					killer.sendMessage(ChatColor.GOLD + getMsg("Messages.killed.player").replaceAll("%p", p.getDisplayName())
							.replaceAll("%k", "" + getData(killer).playersKilled));
				}
				
				RewardType.KILL_PLAYER.reward(killer);
				
			}
			
			
			
			final String kickMsg = (getCraftZ().getPrefix() + " " + getMsg("Messages.died"))
					.replaceAll("%z", "" + getData(p).zombiesKilled)
					.replaceAll("%p", "" + getData(p).playersKilled)
					.replaceAll("%m", "" + getData(p).minutesSurvived);
			
			resetPlayer(p);
			
			setLastDeath(p, System.currentTimeMillis());
			
			
			
			if (config.getBoolean("Config.players.kick-on-death") && !p.hasPermission("craftz.bypassKick")) {
				p.kickPlayer(kickMsg);
			} else {
				
				p.sendMessage(ChatColor.GREEN + kickMsg);
				
				p.setHealth(p.getMaxHealth());
				p.setFoodLevel(20);
				
				Bukkit.getScheduler().runTask(getCraftZ(), new Runnable() {
					@Override
					public void run() {
						
						p.getInventory().clear();
						p.getInventory().setArmorContents(new ItemStack[4]);
						
						p.setVelocity(new Vector());
						p.teleport(getLobby());
						
						Kit kit = getCraftZ().getKits().getDefaultKit();
						if (kit != null) {
							kit.select(p);
						}
						
					}
				});
				
			}
			
		}
		
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamage(EntityDamageEvent event) {
		
		EntityType type = event.getEntityType();
		
		if (type == EntityType.PLAYER) {
			
			Player p = (Player) event.getEntity();
			
			if (isInsideOfLobby(p) || (isWorld(p.getWorld()) && !isPlaying(p))) {
				event.setCancelled(true);
			}
			
		}
		
	}
	
	
	
	
	
	public boolean hasPlayer(Player p) {
		return hasPlayer(p.getUniqueId());
	}
	
	public boolean hasPlayer(UUID uuid) {
		return players.containsKey(uuid);
	}
	
	
	
	
	
	public void savePlayer(Player p) {
		
		if (hasPlayer(p)) {
			WorldData.get().set("Data.players." + p.getUniqueId(), getData(p).toString());
			WorldData.save();
		}
		
	}
	
	public void saveAllPlayers() {
		
		for (Player p : world().getPlayers()) {
			if (hasPlayer(p)) {
				WorldData.get().set("Data.players." + p.getUniqueId(), getData(p).toString());
			}
		}
		
		WorldData.save();
		
	}
	
	
	
	
	
	public void loadPlayer(Player p, boolean forceRespawn, PlayerSpawnpoint spawnpoint) {
		
		if (hasPlayer(p) && !forceRespawn) {
			return;
		}
		
		FileConfiguration config = getConfig("config");
		
		
		
		int invulnTime = 0;
		
		if (existsInConfig(p) && !forceRespawn) {
			
			putPlayer(p, false);
			invulnTime = (int) (config.getDouble("Config.players.invulnerability.on-return") * 20);
			
		} else {
			
			if (spawnpoint == null && (spawnpoint = randomSpawn()) == null) {
				p.sendMessage(ChatColor.RED + getMsg("Messages.errors.no-player-spawns"));
				return;
			}
			
			putPlayer(p, true);
			savePlayer(p);
			
			if (config.getBoolean("Config.players.clear-inventory-on-spawn")) {
				
				PlayerInventory inv = p.getInventory();
				Kits kits = getCraftZ().getKits();
				
				for (int i=0; i<inv.getSize(); i++) {
					ItemStack stack = inv.getItem(i);
					if (!kits.isSoulbound(stack))
						inv.setItem(i, null);
				}
				
				if (!kits.isSoulbound(inv.getHelmet()))
					inv.setHelmet(null);
				if (!kits.isSoulbound(inv.getChestplate()))
					inv.setChestplate(null);
				if (!kits.isSoulbound(inv.getLeggings()))
					inv.setLeggings(null);
				if (!kits.isSoulbound(inv.getBoots()))
					inv.setBoots(null);
				
			}
			
			p.setHealth(p.getMaxHealth());
			p.setFoodLevel(20);
			
			spawnpoint.spawn(p);
			
			invulnTime = (int) (config.getDouble("Config.players.invulnerability.on-spawn") * 20);
			
		}
		
		if (config.getBoolean("Config.players.medical.thirst.enable")) {
			p.setLevel(players.get(p.getUniqueId()).thirst);
		}
		
		p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, invulnTime, 1000));
		
		getCraftZ().getScoreboardHelper().addPlayer(p);
		
	}
	
	private void putPlayer(Player p, boolean defaults) {
		
		if (defaults) {
			players.put(p.getUniqueId(), new PlayerData(20, 0, 0, 0, false, false, false));
		} else {
			String s = WorldData.get().getString("Data.players." + p.getUniqueId());
			players.put(p.getUniqueId(), PlayerData.fromString(s));
		}
		
	}
	
	
	
	
	
	public void resetPlayer(Player p) {
		
		if (hasPlayer(p)) {
			PlayerData data = getData(p);
			addToHighscores(p, data);
		}
		
		WorldData.get().set("Data.players." + p.getUniqueId(), null);
		WorldData.save();
		
		getCraftZ().getScoreboardHelper().removePlayer(p.getUniqueId());
		players.remove(p.getUniqueId());
		
	}
	
	
	
	
	
	public static String makeSpawnID(Location signLoc) {
		return "x" + signLoc.getBlockX() + "y" + signLoc.getBlockY() + "z" + signLoc.getBlockZ();
	}
	
	public PlayerSpawnpoint getSpawnpoint(String signID) {
		
		for (PlayerSpawnpoint spawn : spawns) {
			if (spawn.getID().equals(signID))
				return spawn;
		}
		
		return null;
	}
	
	public PlayerSpawnpoint getSpawnpoint(Location signLoc) {
		return getSpawnpoint(makeSpawnID(signLoc));
	}
	
	
	
	
	
	public void addSpawn(Location signLoc, String name) {
		
		String id = makeSpawnID(signLoc);
		
		PlayerSpawnpoint spawn = new PlayerSpawnpoint(this, id, signLoc, name);
		spawns.add(spawn);
		
		spawn.save();
		
		Dynmap dynmap = getCraftZ().getDynmap();
		dynmap.createMarker(dynmap.SET_PLAYERSPAWNS, "playerspawn_" + id, "Spawn: " + name, signLoc, dynmap.ICON_PLAYERSPAWN);
		
	}
	
	public void removeSpawn(String signID) {
		
		WorldData.get().set("Data.playerspawns." + signID, null);
		WorldData.save();
		
		PlayerSpawnpoint spawn = getSpawnpoint(signID);
		if (spawn != null)
			spawns.remove(spawn);
		
		Dynmap dynmap = getCraftZ().getDynmap();
		dynmap.removeMarker(dynmap.getMarker(dynmap.SET_PLAYERSPAWNS, "playerspawn_" + signID));
		
	}
	
	
	
	
	
	public List<PlayerSpawnpoint> getSpawns() {
		return Collections.unmodifiableList(spawns);
	}
	
	public PlayerSpawnpoint matchSpawn(String name) {
		
		for (PlayerSpawnpoint spawn : spawns) {
			if (spawn.getName().equalsIgnoreCase(name.trim()))
				return spawn;
		}
		
		return null;
		
	}
	
	public PlayerSpawnpoint randomSpawn() {
		
		if (spawns.isEmpty())
			return null;
		
		return spawns.get(CraftZ.RANDOM.nextInt(spawns.size()));
		
	}
	
	public int getSpawnCount() {
		return spawns.size();
	}
	
	
	
	
	
	public int getRespawnCountdown(Player player) {
		if (!lastDeaths.containsKey(player.getUniqueId()) || player.hasPermission("craftz.instantRespawn"))
			return 0;
		int countdown = getConfig("config").getInt("Config.players.respawn-countdown");
		return (int) (countdown*1000 - (System.currentTimeMillis() - lastDeaths.get(player.getUniqueId())));
	}
	
	public void setLastDeath(Player p, long timestamp) {
		lastDeaths.put(p.getUniqueId(), timestamp);
	}
	
	
	
	
	
	@Override
	public PlayerData getData(UUID p) {
		if (!players.containsKey(p))
			loadPlayer(p(p), false, null);
		return players.get(p);
	}
	
	@Override
	public PlayerData getData(Player p) {
		if (!players.containsKey(p.getUniqueId()))
			loadPlayer(p, false, null);
		return players.get(p.getUniqueId());
	}
	
	
	
	
	
	@Override
	public void onServerTick(long tick) {
		
		for (Iterator<Entry<UUID, PlayerData>> it=players.entrySet().iterator(); it.hasNext(); ) {
			
			Entry<UUID, PlayerData> entry = it.next();
			UUID id = entry.getKey();
			PlayerData data = entry.getValue();
			
			Player p = p(id);
			
			if (!isPlaying(id)) {
				
				if (p != null)
					savePlayer(p);
				
				getCraftZ().getScoreboardHelper().removePlayer(id);
				
				it.remove();
				continue;
				
			}
			
			for (Module m : getCraftZ().getModules()) {
				if (m != this)
					m.onPlayerTick(p, tick);
			}
			
			
			
			if (isSurvival(p) && tick % 1200 == 0) {
				data.minutesSurvived++;
			}
			
			
			
			if (tick % 10 == 0) {
				ItemRenamer.on(p).setSpecificNames(ItemRenamer.DEFAULT_MAP);
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
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerMove(PlayerMoveEvent event) {
		
		Player p = event.getPlayer();
		
		if (isWorld(p.getWorld())) {
			double distance = event.getFrom().distance(event.getTo());
			if (distance > 0) {
				movingPlayers.put(p.getUniqueId(), 0);
			}
		}
		
	}
	
	public boolean isMoving(Player p) {
		return movingPlayers.containsKey(p.getUniqueId());
	}
	
	
	
	
	
	public boolean isLobby(Location loc) {
		
		Location lobby = getLobby();
		int radius = getConfig("config").getInt("Config.world.lobby.radius");
		
		return loc.getWorld().getName().equals(lobby.getWorld().getName()) && lobby.distance(loc) <= radius;
		
	}
	
	public boolean isInsideOfLobby(Player p) {
		return isLobby(p.getLocation());
	}
	
	
	
	
	
	public Location getLobby() {
		
		World cw = world();
		if (cw == null)
			return null;
		Location lobby = cw.getSpawnLocation();
		ConfigurationSection sec = getConfig("config").getConfigurationSection("Config.world.lobby");
		
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
	
	public void setLobby(Location loc, double radius) {
		
		ConfigurationSection sec = getConfig("config").getConfigurationSection("Config.world.lobby");
		
		sec.set("world", loc.getWorld().getName());
		sec.set("x", Math.round(loc.getX() * 100) / 100.0);
		sec.set("y", Math.round(loc.getY() * 100) / 100.0);
		sec.set("z", Math.round(loc.getZ() * 100) / 100.0);
		sec.set("yaw", Math.round(loc.getYaw() * 100) / 100f);
		sec.set("pitch", Math.round(loc.getPitch() * 100) / 100f);
		
		sec.set("radius", radius);
		
		saveConfig("config");
		
	}
	
	
	
	
	
	public boolean existsInConfig(Player p) {
		return WorldData.get().contains("Data.players." + p.getUniqueId());
	}
	
	public boolean existsInWorld(Player p) {
		return players.containsKey(p.getUniqueId());
	}
	
	
	
	
	
	public int getPlayerCount() {
		return players.size();
	}
	
	
	
	
	
	public Player randomPlayer() {
		
		List<Player> players = world().getPlayers();
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
	
	
	
	
	
	public boolean isPlaying(Player p) {
		return players.containsKey(p.getUniqueId()) && isWorld(p.getWorld()) && !isInsideOfLobby(p);
	}
	
	public boolean isPlaying(UUID id) {
		Player p = p(id);
		return p != null && players.containsKey(id) && isWorld(p.getWorld()) && !isInsideOfLobby(p);
	}
	
	
	
	
	
	public Map<String, Integer> getHighscores(String category) {
		
		LinkedHashMap<String, Integer> scores = new LinkedHashMap<String, Integer>();
		
		ConfigurationSection sec = getConfig("highscores").getConfigurationSection("Highscores." + category);
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
	
	
	
	
	
	public void addToHighscores(Player p, PlayerData data) {
		
		addToHighscores(p, data.minutesSurvived, "minutes-survived");
		addToHighscores(p, data.zombiesKilled, "zombies-killed");
		addToHighscores(p, data.playersKilled, "players-killed");
		
	}
	
	public void addToHighscores(Player p, int v, String category) {
		
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
		
		getConfig("highscores").createSection("Highscores." + category, scores);
		saveConfig("highscores");
		
	}
	
	
	
	
	
	@Override
	public void onDynmapEnabled(Dynmap dynmap) {
		
		FileConfiguration config = getConfig("config");
		
		
		
		dynmap.clearSet(dynmap.SET_WORLDBORDER);
		
		if (config.getBoolean("Config.dynmap.show-worldborder") && config.getBoolean("Config.world.world-border.enable")) {
			
			double r = config.getDouble("Config.world.world-border.radius");
			dynmap.createCircleMarker(dynmap.SET_WORLDBORDER, "worldborder", "World Border", 6, 0.4, 0xEE2222, getLobby(), r, r);
			
		}
		
		
		
		dynmap.clearSet(dynmap.SET_PLAYERSPAWNS);
		
		if (config.getBoolean("Config.dynmap.show-playerspawns")) {
			
			for (PlayerSpawnpoint spawn : spawns) {
				String id = "playerspawn_" + spawn.getID();
				String label = "Spawn: " + spawn.getName();
				dynmap.createMarker(dynmap.SET_PLAYERSPAWNS, id, label, spawn.getLocation(), dynmap.ICON_PLAYERSPAWN);
			}
			
		}
		
	}
	
}
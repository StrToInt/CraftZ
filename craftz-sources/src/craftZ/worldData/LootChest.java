package craftZ.worldData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import craftZ.CraftZ;
import craftZ.modules.ChestRefiller;
import craftZ.util.EntityChecker;
import craftZ.util.ItemRenamer;
import craftZ.util.StackParser;


public class LootChest extends WorldDataObject {
	
	private final ChestRefiller refiller;
	private final String list;
	private final Location loc;
	private final String face;
	private int refillCountdown, despawnCountdown;
	
	
	
	public LootChest(ChestRefiller refiller, ConfigurationSection data) {
		this(refiller, data.getName(), data.getString("list"),
				new Location(refiller.world(), data.getInt("coords.x"), data.getInt("coords.y"), data.getInt("coords.z")),
				data.getString("face"));
	}
	
	public LootChest(ChestRefiller refiller, String id, String list, Location loc, String face) {
		
		super(id);
		
		this.refiller = refiller;
		
		this.list = list;
		this.loc = loc;
		this.face = face == null ? "n" : face.toLowerCase();
		
	}
	
	
	
	
	
	public String getList() {
		return list;
	}
	
	
	
	
	
	public Location getLocation() {
		return loc.clone();
	}
	
	
	
	
	
	public String getFace() {
		return face;
	}
	
	public BlockFace getBlockFace() {
		return face.equals("s") ? BlockFace.SOUTH
				: (face.equals("e") ? BlockFace.EAST
						: (face.equals("w") ? BlockFace.WEST
								: BlockFace.NORTH));
	}
	
	
	
	
	
	public void save() {
		save("Data.lootchests");
	}
	
	@Override
	public void store(ConfigurationSection section) {
		
		section.set("coords.x", loc.getBlockX());
		section.set("coords.y", loc.getBlockY());
		section.set("coords.z", loc.getBlockZ());
		section.set("face", face);
		section.set("list", list);
		
	}
	
	
	
	
	
	public List<String> getLootDefinitions(boolean multiplied) {
		
		List<String> defs = refiller.getConfig("loot").getStringList("Loot.lists." + list);
		List<String> ndefs = new ArrayList<String>();
		if (!multiplied || defs == null)
			return ndefs;
		
		for (int i=0; i<defs.size(); i++) {
			
			String str = defs.get(i);
			int count = 1;
			String itm = str;
			
			Pattern pattern = Pattern.compile("^([0-9])x");
			Matcher matcher = pattern.matcher(str);
			if (matcher.find()) {
				count = Integer.parseInt(matcher.group(1));
				itm = str.substring(matcher.end());
			}
			
			for (int a=0; a<count; a++) {
				ndefs.add(itm);
			}
			
		}
		
		return ndefs;
		
	}
	
	
	
	
	
	public void refill(boolean placeChest) {
		
		Block block = loc.getBlock();
		
		double mpv = refiller.getPropertyInt("max-player-vicinity", list);
		if ((mpv > 0 && EntityChecker.areEntitiesNearby(loc, mpv, EntityType.PLAYER, 1))
				|| (!placeChest && block.getType() != Material.CHEST)) {
			startRefill(false);
			return;
		}
		
		block.setType(Material.CHEST);
		Chest chest = (Chest) block.getState();
		Inventory inv = chest.getInventory();
		
		BlockFace face = getBlockFace();
		((org.bukkit.material.Chest) chest.getData()).setFacingDirection(face);
		
		for (Iterator<ItemStack> it=inv.iterator(); it.hasNext(); ) { // do not refill if already full
			ItemStack stack = it.next();
			if (stack != null && stack.getType() != Material.AIR) {
				if (refiller.getPropertyBoolean("despawn", list)) {
					despawnCountdown = refiller.getPropertyInt("time-before-despawn", list) * 20;
				}
				return;
			}
		}
		
		List<String> defs = getLootDefinitions(true);
		
		if (!defs.isEmpty()) {
			
			int min = refiller.getPropertyInt("min-stacks-filled", list);
			int max = refiller.getPropertyInt("max-stacks-filled", list);
			
			for (int i = 0, n = (1 + min + (max > min ? CraftZ.RANDOM.nextInt(max - min) : 0)); i < n; i++) {
				ItemStack stack = StackParser.fromString(defs.get(CraftZ.RANDOM.nextInt(defs.size())), false);
				if (stack != null) {
					chest.getInventory().addItem(stack);
				}
			}
			
			ItemRenamer.convertInventory(chest, ItemRenamer.DEFAULT_MAP);
			
		}
		
		if (refiller.getPropertyBoolean("despawn", list)) {
			despawnCountdown = refiller.getPropertyInt("time-before-despawn", list) * 20;
		}
		
	}
	
	
	
	
	
	public void startRefill(boolean drop) {
		
		refillCountdown = refiller.getPropertyInt("time-before-refill", list) * 20;
		
		Block block = loc.getBlock();
		
		try { // try-catch-clause is workaround for NPE when Bukkit calls CraftInventory.getSize() [got an idea why this happens, anybody?]
			BlockState bs = block.getState();
			if (bs instanceof Chest && !drop)
				((Chest) bs).getInventory().clear();
		} catch (NullPointerException ex) { }
		
		block.setType(Material.AIR);
		
	}
	
	
	
	
	
	public void onServerTick() {
		
		if (despawnCountdown > 0) {
			
			if (--despawnCountdown <= 0)
				startRefill(refiller.getPropertyBoolean("drop-on-despawn", list));
			
		} else if (refillCountdown > 0) {
			
			if (--refillCountdown <= 0)
				refill(true);
			
		}
		
	}
	
}
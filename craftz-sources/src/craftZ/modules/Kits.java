package craftZ.modules;

import java.util.*;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import craftZ.CraftZ;
import craftZ.Module;
import craftZ.util.ItemRenamer;
import craftZ.util.KitEditingSession;


public class Kits extends Module {
	
	private Map<String, Kit> kits = new HashMap<String, Kit>();
	private Map<UUID, KitEditingSession> editingSessions = new HashMap<UUID, KitEditingSession>();
	
	
	
	public Kits(CraftZ craftZ) {
		super(craftZ);
	}
	
	
	
	
	
	@Override
	public void onLoad(boolean configReload) {
		
		kits.clear();
		
		ConfigurationSection kits = getConfig("kits").getConfigurationSection("Kits.kits");
		if (kits != null) {
			
			for (String name : kits.getKeys(false)) {
				
				ConfigurationSection sec = kits.getConfigurationSection(name);
				
				ConfigurationSection itemsSec = sec.getConfigurationSection("items");
				LinkedHashMap<String, ItemStack> items = new LinkedHashMap<String, ItemStack>();
				
				if (itemsSec != null) {
					for (String slot : itemsSec.getKeys(false)) {
						items.put(slot, itemsSec.getItemStack(slot));
					}
				}
				
				this.kits.put(name, new Kit(this, name, sec.getBoolean("default"), sec.getString("permission"), items));
				
			}
			
		}
		
	}
	
	@Override
	public void onDisable() {
		for (Player p : getCraftZ().getServer().getOnlinePlayers()) {
			if (isEditing(p))
				stopEditing(p, false, false);
		}
	}
	
	
	
	
	
	public void addKit(Kit kit) {
		kits.put(kit.getName(), kit);
		kit.save();
	}
	
	public void removeKit(Kit kit) {
		
		for (Iterator<KitEditingSession> it=editingSessions.values().iterator(); it.hasNext(); ) {
			KitEditingSession session = it.next();
			if (session.kit == kit) {
				session.stop(false);
				it.remove();
			}
		}
		
		kits.remove(kit.getName());
		kit.delete();
		
	}
	
	
	
	
	
	public void setDefault(Kit defaultKit) {
		
		for (Kit kit : kits.values()) {
			kit.setDefault(false);
			kit.save();
		}
		
		if (defaultKit != null) {
			defaultKit.setDefault(true);
			defaultKit.save();
		}
		
	}
	
	
	
	
	
	public Kit getDefaultKit() {
		
		Kit kit = null;
		
		for (Entry<String, Kit> entry : kits.entrySet()) {
			if (kit == null)
				kit = entry.getValue();
			else if (entry.getValue().isDefault())
				return entry.getValue();
		}
		
		return kit;
		
	}
	
	
	
	
	
	public Collection<Kit> getKits() {
		return kits.values();
	}
	
	public List<Kit> getAvailableKits(Player p) {
		
		List<Kit> available = new ArrayList<Kit>();
		
		for (Entry<String, Kit> entry : kits.entrySet()) {
			Kit kit = entry.getValue();
			if (kit.canUse(p))
				available.add(kit);
		}
		
		return available;
		
	}
	
	
	
	
	
	public Kit get(String name) {
		return kits.get(name);
	}
	
	public Kit match(String name) {
		return kits.get(name.toLowerCase());
	}
	
	
	
	
	
	public String getSoulboundLabel() {
		return ChatColor.DARK_PURPLE + getCraftZ().getPrefix() + " " + ChatColor.LIGHT_PURPLE
				+ getConfig("kits").getString("Kits.settings.soulbound-label");
	}
	
	public boolean isSoulbound(ItemStack stack) {
		
		if (stack == null || !stack.hasItemMeta())
			return false;
		
		ItemMeta meta = stack.getItemMeta();
		if (!meta.hasLore())
			return false;
		
		List<String> lore = meta.getLore();
		
		return !lore.isEmpty() && lore.get(0).equals(getSoulboundLabel());
		
	}
	
	public ItemStack setSoulbound(ItemStack stack) {
		return stack.hasItemMeta() && stack.getItemMeta().hasLore()
				? stack
				: ItemRenamer.on(stack).setLore(getSoulboundLabel()).get();
	}
	
	
	
	
	
	public KitEditingSession startEditing(Player p, Kit kit) {
		
		if (isEditing(p))
			return null;
		
		KitEditingSession session = KitEditingSession.start(p, kit);
		editingSessions.put(p.getUniqueId(), session);
		
		return session;
		
	}
	
	public void stopEditing(Player p, boolean message, boolean save) {
		
		if (!isEditing(p))
			return;
		
		KitEditingSession session = getEditingSession(p);
		session.stop(save);
		
		editingSessions.remove(p.getUniqueId());
		
		if (message) {
			p.sendMessage(ChatColor.AQUA + getMsg("Messages.cmd.kitsadmin." + (save ? "kit-edited" : "kit-editing-cancelled"))
					.replace("%k", session.kit.getName()));
		}
		
	}
	
	public boolean isEditing(Player p) {
		return editingSessions.containsKey(p.getUniqueId());
	}
	
	public KitEditingSession getEditingSession(Player p) {
		return editingSessions.get(p.getUniqueId());
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		
		Player p = event.getPlayer();
		
		if (isEditing(p)) {
			
			String msg = event.getMessage();
			
			if (msg.equalsIgnoreCase("done")) {
				stopEditing(p, true, true);
				event.setCancelled(true);
			} else if (msg.equalsIgnoreCase("cancel")) {
				stopEditing(p, true, false);
				event.setCancelled(true);
			}
			
		}
		
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		
		final Item item = event.getItemDrop();
		
		if (isSoulbound(item.getItemStack())) {
			item.remove();
		}
		
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDeath(PlayerDeathEvent event) {
		
		final Player p = event.getEntity();
		
		if (isWorld(p.getWorld())) {
			
			for (Iterator<ItemStack> it=event.getDrops().iterator(); it.hasNext(); ) {
				ItemStack stack = it.next();
				if (stack != null && isSoulbound(stack))
					it.remove();
			}
			
		}
		
	}
	
}
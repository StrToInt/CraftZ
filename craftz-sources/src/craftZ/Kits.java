package craftZ;

import java.util.*;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import craftZ.util.ItemRenamer;
import craftZ.util.KitEditingSession;

public class Kits {
	
	private static Map<String, Kit> kits = new HashMap<String, Kit>();
	private static Map<UUID, KitEditingSession> editingSessions = new HashMap<UUID, KitEditingSession>();
	
	
	
	public static int loadKits() {
		
		Kits.kits.clear();
		
		ConfigurationSection kits = ConfigManager.getConfig("kits").getConfigurationSection("Kits.kits");
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
				
				Kits.kits.put(name, new Kit(name, sec.getBoolean("default"), sec.getString("permission"), items));
				
			}
			
		}
		
		return Kits.kits.size();
		
	}
	
	
	
	
	
	public static void addKit(Kit kit) {
		kits.put(kit.getName(), kit);
		kit.save();
	}
	
	public static void removeKit(Kit kit) {
		
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
	
	
	
	
	
	public static void setDefault(Kit defaultKit) {
		
		for (Kit kit : kits.values()) {
			kit.setDefault(false);
			kit.save();
		}
		
		if (defaultKit != null) {
			defaultKit.setDefault(true);
			defaultKit.save();
		}
		
	}
	
	
	
	
	
	public static Kit getDefaultKit() {
		
		Kit kit = null;
		
		for (Entry<String, Kit> entry : kits.entrySet()) {
			if (kit == null)
				kit = entry.getValue();
			else if (entry.getValue().isDefault())
				return entry.getValue();
		}
		
		return kit;
		
	}
	
	
	
	
	
	public static Collection<Kit> getKits() {
		return kits.values();
	}
	
	public static List<Kit> getAvailableKits(Player p) {
		
		List<Kit> available = new ArrayList<Kit>();
		
		for (Entry<String, Kit> entry : kits.entrySet()) {
			Kit kit = entry.getValue();
			if (kit.canUse(p))
				available.add(kit);
		}
		
		return available;
		
	}
	
	
	
	
	
	public static Kit get(String name) {
		return kits.get(name);
	}
	
	public static Kit match(String name) {
		return kits.get(name.toLowerCase());
	}
	
	
	
	
	
	public static String getSoulboundLabel() {
		return ChatColor.DARK_PURPLE + CraftZ.getPrefix() + " " + ChatColor.LIGHT_PURPLE
				+ ConfigManager.getConfig("kits").getString("Kits.settings.soulbound-label");
	}
	
	public static boolean isSoulbound(ItemStack stack) {
		
		if (stack == null || !stack.hasItemMeta())
			return false;
		
		ItemMeta meta = stack.getItemMeta();
		if (!meta.hasLore())
			return false;
		
		List<String> lore = meta.getLore();
		
		return !lore.isEmpty() && lore.get(0).equals(getSoulboundLabel());
		
	}
	
	public static ItemStack setSoulbound(ItemStack stack) {
		return ItemRenamer.setLore(stack, Arrays.asList(getSoulboundLabel()));
	}
	
	
	
	
	
	public static KitEditingSession startEditing(Player p, Kit kit) {
		
		if (isEditing(p))
			return null;
		
		KitEditingSession session = KitEditingSession.start(p, kit);
		editingSessions.put(p.getUniqueId(), session);
		
		return session;
		
	}
	
	public static void stopEditing(Player p, boolean message, boolean save) {
		
		if (!isEditing(p))
			return;
		
		KitEditingSession session = getEditingSession(p);
		session.stop(save);
		
		editingSessions.remove(p.getUniqueId());
		
		if (message) {
			p.sendMessage(ChatColor.AQUA + CraftZ.getMsg("Messages.cmd.kitsadmin." + (save ? "kit-edited" : "kit-editing-cancelled"))
					.replace("%k", session.kit.getName()));
		}
		
	}
	
	public static boolean isEditing(Player p) {
		return editingSessions.containsKey(p.getUniqueId());
	}
	
	public static KitEditingSession getEditingSession(Player p) {
		return editingSessions.get(p.getUniqueId());
	}
	
}
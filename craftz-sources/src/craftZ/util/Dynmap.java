package craftZ.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.dynmap.DynmapCommonAPI;
import org.dynmap.markers.CircleMarker;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerIcon;
import org.dynmap.markers.MarkerSet;

import craftZ.CraftZ;


public class Dynmap {
	
	private static DynmapCommonAPI api;
	
	public static Object ICON_LOOT, ICON_PLAYERSPAWN;
	public static Object SET_LOOT, SET_PLAYERSPAWNS, SET_WORLDBORDER;
	
	private static Map<Material, String> itemImageMap = new HashMap<Material, String>();
	static {
		itemImageMap.put(Material.LEATHER_HELMET, "leather-cap");
		itemImageMap.put(Material.LEATHER_CHESTPLATE, "leather-tunic");
		itemImageMap.put(Material.LEATHER_LEGGINGS, "leather-pants");
		itemImageMap.put(Material.WEB, "web-block");
		itemImageMap.put(Material.WOOD_SWORD, "wooden-sword");
		itemImageMap.put(Material.WOOD_PICKAXE, "wooden-pickaxe");
		itemImageMap.put(Material.WOOD_AXE, "wooden-axe");
		itemImageMap.put(Material.WOOD_HOE, "wooden-hoe");
		itemImageMap.put(Material.WOOD_SPADE, "wooden-shovel");
		itemImageMap.put(Material.MUSHROOM_SOUP, "mushroom-stew");
		itemImageMap.put(Material.CARROT_ITEM, "carrot");
		itemImageMap.put(Material.WORKBENCH, "crafting-table");
	}
	
	
	
	public static void setup() {
		
		Bukkit.getPluginManager().registerEvents(new Listener() {
			
			@EventHandler
			public void onPluginEnable(PluginEnableEvent event) {
				if (event.getPlugin().getName().equalsIgnoreCase("dynmap"))
					apiReady((DynmapCommonAPI) event.getPlugin());
			}
			
		}, CraftZ.i);
		
	}
	
	private static void apiReady(DynmapCommonAPI api) {
		
		Dynmap.api = api;
		CraftZ.info("Successfully hooked into Dynmap.");
		
		ICON_LOOT = createIcon("loot", "Loot", CraftZ.i.getResource("icon_loot.png"));
		ICON_PLAYERSPAWN = createIcon("playerspawn", "Spawn", CraftZ.i.getResource("icon_playerspawn.png"));
		
		SET_LOOT = createSet("loot", "Loot");
		SET_PLAYERSPAWNS = createSet("playerspawns", "Spawns");
		SET_WORLDBORDER = createSet("worldborder", "World Border");
		
		CraftZ.onDynmapEnabled();
		
	}
	
	
	
	
	
	public static boolean hasAccess() {
		return api != null && api.markerAPIInitialized();
	}
	
	
	
	
	
	public static Object createSet(String id, String label) {
		
		if (!hasAccess())
			return null;
		
		id = "craftz_" + id;
		label = CraftZ.getPrefix() + " " + label;
		
		MarkerAPI mapi = api.getMarkerAPI();
		MarkerSet set = mapi.getMarkerSet(id);
		
		return set != null ? set : mapi.createMarkerSet(id, label, null, false);
		
	}
	
	
	
	
	
	public static Object createIcon(String id, String label, InputStream pngStream) {
		return createIcon(id, label, pngStream, false);
	}
	
	public static Object createIcon(String id, String label, InputStream pngStream, boolean deleteExisting) {
		
		if (!hasAccess())
			return null;
		
		MarkerAPI mapi = api.getMarkerAPI();
		
		id = "craftz_" + id;
		label = CraftZ.getPrefix() + " " + label;
		
		MarkerIcon icon = mapi.getMarkerIcon(id);
		if (icon != null && deleteExisting)
			icon.deleteIcon();
		
		return !deleteExisting && icon != null ? icon : mapi.createMarkerIcon(id, label, pngStream);
		
	}
	
	public static Object createUserIcon(String id, String label, String name, Object defaultIconHandle) {
		
		File f = new File(CraftZ.i.getDataFolder(), "mapicons/" + name + ".png");
		
		try {
			return createIcon(id, label, new FileInputStream(f), true);
		} catch (FileNotFoundException ex) {
			InputStream cstream = CraftZ.i.getResource("icon_" + name + ".png");
			if (cstream != null)
				return createIcon(id, label, cstream);
		}
		
		return defaultIconHandle;
		
	}
	
	
	
	
	
	public static void unpackIcons(String... icons) {
		
		File dir = new File(CraftZ.i.getDataFolder(), "mapicons");
		dir.mkdirs();
		
		for (String icon : icons) {
			
			try {
				
				InputStream in = CraftZ.i.getResource("icon_" + icon + ".png");
				if (in == null) {
					CraftZ.severe("Default icon '" + icon + "' ('icon_" + icon + ".png') not found!");
					continue;
				}
				File f = new File(dir, icon + ".png");
				if (f.exists())
					continue;
				OutputStream out = new FileOutputStream(f);
				
				int readBytes;
				byte[] buffer = new byte[4096];
				while ((readBytes = in.read(buffer)) > 0) {
					out.write(buffer, 0, readBytes);
				}
				
				in.close();
				out.flush();
				out.close();
				
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			
		}
		
	}
	
	
	
	
	
	public static Object createMarker(Object setHandle, String id, String label, Location loc, Object iconHandle) {
		
		if (!hasAccess())
			return null;
		
		MarkerSet set = (MarkerSet) setHandle;
		MarkerIcon icon = (MarkerIcon) iconHandle;
		
		id = "craftz_" + id;
		label = CraftZ.getPrefix() + " " + label;
		
		Marker marker = set.findMarker(id);
		
		return marker != null ? marker : set.createMarker(id, label, loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ(), icon, false);
		
	}
	
	public static void removeMarker(Object markerHandle) {
		
		if (!hasAccess())
			return;
		
		Marker m = (Marker) markerHandle;
		if (m != null) {
			m.deleteMarker();
		}
		
	}
	
	public static Object getMarker(Object setHandle, String id) {
		
		if (!hasAccess())
			return null;
		
		MarkerSet set = (MarkerSet) setHandle;
		
		id = "craftz_" + id;
		
		return set.findMarker(id);
		
	}
	
	
	
	
	
	public static void setMarkerDescription(Object markerHandle, String s) {
		
		if (!hasAccess())
			return;
		
		Marker m = (Marker) markerHandle;
		if (m != null) {
			m.setDescription(s);
		}
		
	}
	
	
	
	
	
	public static String getItemImage(Material material) {
		
		String n = itemImageMap.containsKey(material) ? itemImageMap.get(material) : material.name().toLowerCase().replace('_', '-');
		String url = "http://www.minecraftinformation.com/images/" + n + ".png";
		String rn = material.name().toLowerCase().replace('_', ' ');
		String onerror = "this.parentNode.replaceChild(document.createTextNode(\"[" + rn + "]\"), this)";
		
		return "<img src='" + url + "' onerror='" + onerror + "' style='width: 32px;' />";
		
	}
	
	
	
	
	
	public static Object createCircleMarker(Object setHandle, String id, String label, int weight, double opacity, int color,
			Location loc, double xr, double zr) {
		
		if (!hasAccess())
			return null;
		
		MarkerSet set = (MarkerSet) setHandle;
		
		id = "craftz_" + id;
		label = CraftZ.getPrefix() + " " + label;
		
		CircleMarker marker = set.findCircleMarker(id);
		if (marker == null) {
			marker = set.createCircleMarker(id, label, false, loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ(), xr, zr, false);
		}
		
		marker.setLineStyle(weight, opacity, color);
		marker.setFillStyle(0, 0x000000);
		
		return marker;
		
	}
	
	public static void removeCircleMarker(Object setHandle, String id) {
		
		if (!hasAccess())
			return;
		
		MarkerSet set = (MarkerSet) setHandle;
		
		id = "craftz_" + id;
		
		CircleMarker m = set.findCircleMarker(id);
		if (m != null) {
			m.deleteMarker();
		}
		
	}
	
	
	
	
	
	public static void clearSet(Object setHandle) {
		
		if (!hasAccess())
			return;
		
		MarkerSet set = (MarkerSet) setHandle;
		
		for (Marker m : set.getMarkers()) {
			m.deleteMarker();
		}
		
	}
	
}
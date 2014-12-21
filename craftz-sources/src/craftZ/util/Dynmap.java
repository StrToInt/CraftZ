package craftZ.util;

import java.io.InputStream;

import org.bukkit.Bukkit;
import org.bukkit.Location;
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
		
		ICON_LOOT = createIcon("loot", "Loot", CraftZ.class.getResourceAsStream("/icon_loot.png"));
		ICON_PLAYERSPAWN = createIcon("playerspawn", "Spawn", CraftZ.class.getResourceAsStream("/icon_playerspawn.png"));
		
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
		
		if (!hasAccess())
			return null;
		
		MarkerAPI mapi = api.getMarkerAPI();
		
		id = "craftz_" + id;
		label = CraftZ.getPrefix() + " " + label;
		
		MarkerIcon icon = mapi.getMarkerIcon(id);
		
		return icon != null ? icon : mapi.createMarkerIcon(id, label, pngStream);
		
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
	
	public static void removeMarker(Object setHandle, String id) {
		
		if (!hasAccess())
			return;
		
		MarkerSet set = (MarkerSet) setHandle;
		
		id = "craftz_" + id;
		
		Marker m = set.findMarker(id);
		if (m != null) {
			m.deleteMarker();
		}
		
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
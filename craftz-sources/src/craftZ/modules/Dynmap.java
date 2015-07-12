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
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.dynmap.DynmapCommonAPI;
import org.dynmap.markers.CircleMarker;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerIcon;
import org.dynmap.markers.MarkerSet;

import craftZ.CraftZ;
import craftZ.Module;


public class Dynmap extends Module {
	
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
	
	private DynmapCommonAPI api;
	
	public Object ICON_LOOT, ICON_PLAYERSPAWN;
	public Object SET_LOOT, SET_PLAYERSPAWNS, SET_WORLDBORDER;
	
	
	
	public Dynmap(CraftZ craftZ) {
		super(craftZ);
	}
	
	
	
	
	
	@Override
	public void onLoad(boolean configReload) {
		
		if (configReload) {
			for (Module m : getCraftZ().getModules())
				m.onDynmapEnabled(this);
		} else if (Bukkit.getPluginManager().isPluginEnabled("dynmap")) {
			apiReady(Bukkit.getPluginManager().getPlugin("dynmap"));
		}
		
	}
	
	
	
	
	
	@EventHandler
	public void onPluginEnable(PluginEnableEvent event) {
		if (event.getPlugin().getName().equalsIgnoreCase("dynmap")) {
			apiReady(event.getPlugin());
		}
	}
	
	private void apiReady(Plugin plugin) {
		
		this.api = (DynmapCommonAPI) plugin;
		CraftZ.info("Successfully hooked into Dynmap.");
		
		ICON_LOOT = createIcon("loot", "Loot", getCraftZ().getResource("icon_loot.png"));
		ICON_PLAYERSPAWN = createIcon("playerspawn", "Spawn", getCraftZ().getResource("icon_playerspawn.png"));
		
		SET_LOOT = createSet("loot", "Loot");
		SET_PLAYERSPAWNS = createSet("playerspawns", "Spawns");
		SET_WORLDBORDER = createSet("worldborder", "World Border");
		
		for (Module m : getCraftZ().getModules())
			m.onDynmapEnabled(this);
		
	}
	
	
	
	
	
	public boolean hasAccess() {
		return api != null && api.markerAPIInitialized();
	}
	
	
	
	
	
	public Object createSet(String id, String label) {
		
		if (!hasAccess())
			return null;
		
		id = "craftz_" + id;
		label = getCraftZ().getPrefix() + " " + label;
		
		MarkerAPI mapi = api.getMarkerAPI();
		MarkerSet set = mapi.getMarkerSet(id);
		
		return set != null ? set : mapi.createMarkerSet(id, label, null, false);
		
	}
	
	
	
	
	
	public Object createIcon(String id, String label, InputStream pngStream) {
		return createIcon(id, label, pngStream, false);
	}
	
	public Object createIcon(String id, String label, InputStream pngStream, boolean deleteExisting) {
		
		if (!hasAccess())
			return null;
		
		MarkerAPI mapi = api.getMarkerAPI();
		
		id = "craftz_" + id;
		label = getCraftZ().getPrefix() + " " + label;
		
		MarkerIcon icon = mapi.getMarkerIcon(id);
		if (icon != null && deleteExisting)
			icon.deleteIcon();
		
		return !deleteExisting && icon != null ? icon : mapi.createMarkerIcon(id, label, pngStream);
		
	}
	
	public Object createUserIcon(String id, String label, String name, Object defaultIconHandle) {
		
		File f = new File(getCraftZ().getDataFolder(), "mapicons/" + name + ".png");
		
		try {
			return createIcon(id, label, new FileInputStream(f), true);
		} catch (FileNotFoundException ex) {
			InputStream cstream = getCraftZ().getResource("icon_" + name + ".png");
			if (cstream != null)
				return createIcon(id, label, cstream);
		}
		
		return defaultIconHandle;
		
	}
	
	
	
	
	
	public void unpackIcons(String... icons) {
		
		File dir = new File(getCraftZ().getDataFolder(), "mapicons");
		dir.mkdirs();
		
		for (String icon : icons) {
			
			try {
				
				InputStream in = getCraftZ().getResource("icon_" + icon + ".png");
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
	
	
	
	
	
	public Object createMarker(Object setHandle, String id, String label, Location loc, Object iconHandle) {
		
		if (!hasAccess())
			return null;
		
		MarkerSet set = (MarkerSet) setHandle;
		MarkerIcon icon = (MarkerIcon) iconHandle;
		
		id = "craftz_" + id;
		label = getCraftZ().getPrefix() + " " + label;
		
		Marker marker = set.findMarker(id);
		
		return marker != null ? marker : set.createMarker(id, label, loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ(), icon, false);
		
	}
	
	public void removeMarker(Object markerHandle) {
		
		if (!hasAccess())
			return;
		
		Marker m = (Marker) markerHandle;
		if (m != null) {
			m.deleteMarker();
		}
		
	}
	
	public Object getMarker(Object setHandle, String id) {
		
		if (!hasAccess())
			return null;
		
		MarkerSet set = (MarkerSet) setHandle;
		
		id = "craftz_" + id;
		
		return set.findMarker(id);
		
	}
	
	
	
	
	
	public void setMarkerDescription(Object markerHandle, String s) {
		
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
	
	
	
	
	
	public Object createCircleMarker(Object setHandle, String id, String label, int weight, double opacity, int color,
			Location loc, double xr, double zr) {
		
		if (!hasAccess() || loc == null)
			return null;
		
		MarkerSet set = (MarkerSet) setHandle;
		
		id = "craftz_" + id;
		label = getCraftZ().getPrefix() + " " + label;
		
		CircleMarker marker = set.findCircleMarker(id);
		if (marker == null) {
			marker = set.createCircleMarker(id, label, false, loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ(), xr, zr, false);
		}
		
		marker.setLineStyle(weight, opacity, color);
		marker.setFillStyle(0, 0x000000);
		
		return marker;
		
	}
	
	public void removeCircleMarker(Object setHandle, String id) {
		
		if (!hasAccess())
			return;
		
		MarkerSet set = (MarkerSet) setHandle;
		
		id = "craftz_" + id;
		
		CircleMarker m = set.findCircleMarker(id);
		if (m != null) {
			m.deleteMarker();
		}
		
	}
	
	
	
	
	
	public void clearSet(Object setHandle) {
		
		if (!hasAccess())
			return;
		
		MarkerSet set = (MarkerSet) setHandle;
		
		for (Marker m : set.getMarkers()) {
			m.deleteMarker();
		}
		
	}
	
}
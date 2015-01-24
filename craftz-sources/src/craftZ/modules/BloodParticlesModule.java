package craftZ.modules;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import craftZ.CraftZ;
import craftZ.Module;
import craftZ.util.EntityChecker;
import craftZ.util.ItemRenamer;


public class BloodParticlesModule extends Module {
	
	public BloodParticlesModule(CraftZ craftZ) {
		super(craftZ);
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamage(EntityDamageEvent event) {
		
		Entity entity = event.getEntity();
		EntityType type = event.getEntityType();
		
		if (isWorld(entity.getWorld())) {
			
			if (!event.isCancelled() && getConfig("config").getBoolean("Config.mobs.blood-particles-when-damaged")) {
				
				if (!type.isAlive() || (type == EntityType.PLAYER && ((Player) entity).getGameMode() == GameMode.CREATIVE)) {
					return;
				}
				
				Location loc = entity.getLocation();
				World w = entity.getWorld();
				
				int bloodCount = (int) (Math.min(event.getDamage() * (type == EntityType.ZOMBIE ? 1 : 2), 100));
				for (int i=0; i<bloodCount; i++) {
					
					ItemStack stack = ItemRenamer.setName(new ItemStack(Material.REDSTONE), "blood" + CraftZ.RANDOM.nextInt());
					final Item blood = w.dropItemNaturally(loc, stack);
					
					blood.setPickupDelay(Integer.MAX_VALUE);
					blood.setMetadata("isBlood", new FixedMetadataValue(getCraftZ(), true));
					
					Bukkit.getScheduler().scheduleSyncDelayedTask(getCraftZ(), new Runnable() {
						@Override
						public void run() {
							blood.remove();
						}
					}, 4 + CraftZ.RANDOM.nextInt(6));
					
				}
				
			}
		
		}
		
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChunkLoad(ChunkLoadEvent event) {
		
		if (isWorld(event.getWorld())) {
			
			if (!getConfig("config").getBoolean("Config.world.world-changing.allow-new-chunks") && event.isNewChunk()) {
				event.getChunk().unload(false, false);
				return;
			}
			
			for (Entity ent : event.getChunk().getEntities()) {
				MetadataValue value;
				if (ent.getType() == EntityType.DROPPED_ITEM && (value = EntityChecker.getMeta(ent, "isBlood")) != null && value.asBoolean()) {
					ent.remove();
				}
			}
			
		}
		
	}
	
}
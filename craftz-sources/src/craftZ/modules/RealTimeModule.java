package craftZ.modules;

import java.util.Calendar;

import craftZ.CraftZ;
import craftZ.Module;


public class RealTimeModule extends Module {
	
	public RealTimeModule(CraftZ craftZ) {
		super(craftZ);
	}
	
	
	
	
	
	@Override
	public void onServerTick(long tick) {
		
		if (getConfig("config").getBoolean("Config.world.real-time")) {
			
			int h = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) - 6,
				m = Calendar.getInstance().get(Calendar.MINUTE);
			
			int t = (int) (h * 1000    +    m * (1000.0 / 60));
			
		    world().setFullTime(t);
		    
		}
		
	}
	
}
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
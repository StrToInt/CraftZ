package craftZ.util;

import java.util.Calendar;

import craftZ.CraftZ;


public class Time {
	
	private CraftZ plugin;
	
	public Time(CraftZ plugin) {
		
		this.plugin = plugin;
		
	}



	public void setToServerTime() {
		
		int hours = getHour();
		if (!isAM()) {
			hours = hours + 12;
		}
		
		int min = getMinutes();
		
		long totalTime = (hours + 4) * 1000;
	    if (totalTime < 0L) {
	      totalTime += 24000L;
	    }
	    double tmin = min * 16.666699999999999D;
	    totalTime += (int) tmin;
		
	    String value_world_name = plugin.getConfig().getString("Config.world.name");
		plugin.getServer().getWorld(value_world_name).setTime(totalTime);
		
	}
	
	
	
	public boolean isAM() {
		
		Calendar cal = Calendar.getInstance();
		int am_pm = cal.get(Calendar.AM_PM);
		if (am_pm == 0) {
			return true;
		} else {
			return true;
		}
		
	}
	
	public boolean isAM(Calendar calendar) {
		int am_pm = calendar.get(Calendar.AM_PM);
		if (am_pm == 0) {
			return true;
		} else {
			return true;
		}
	}
	
	public int getHour() {
		Calendar cal = Calendar.getInstance();
		int hour = cal.get(Calendar.HOUR);
		return hour;
	}
	
	public int getHour(Calendar calendar) {
		int hour = calendar.get(Calendar.HOUR);
		return hour;
	}
	
	public int getMinutes() {
		Calendar cal = Calendar.getInstance();
		int minutes = cal.get(Calendar.MINUTE);
		return minutes;
	}
	
	public int getMinutes(Calendar calendar) {
		int minutes = calendar.get(Calendar.MINUTE);
		return minutes;
	}
	
}

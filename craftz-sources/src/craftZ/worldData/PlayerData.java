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
package craftZ.worldData;


public class PlayerData {
	
	public int thirst = 20, zombiesKilled, playersKilled, minutesSurvived;
	public boolean bleeding, poisoned, bonesBroken;
	
	
	
	public PlayerData(int thirst, int zombiesKilled, int playersKilled, int minutesSurvived,
			boolean bleeding, boolean bonesBroken, boolean poisoned) {
		
		this.thirst = thirst;
		this.zombiesKilled = zombiesKilled;
		this.playersKilled = playersKilled;
		this.minutesSurvived = minutesSurvived;
		this.bleeding = bleeding;
		this.bonesBroken = bonesBroken;
		this.poisoned = poisoned;
		
	}
	
	
	
	
	
	@Override
	public String toString() {
		return thirst + "|" + zombiesKilled + "|" + playersKilled + "|" + minutesSurvived
				+ "|" + (bleeding ? "1" : "0") + "|" + (poisoned ? "1" : "0") + "|" + (bonesBroken ? "1" : "0");
	}
	
	
	
	
	
	public static PlayerData fromString(String s) {
		
		String[] spl = s.split("\\|");
		
		int thirst = spl.length > 0 ? Integer.valueOf(spl[0]) : 20;
		int zombiesKilled = spl.length > 1 ? Integer.valueOf(spl[1]) : 0;
		int playersKilled = spl.length > 2 ? Integer.valueOf(spl[2]) : 0;
		int minutesSurvived = spl.length > 3 ? Integer.valueOf(spl[3]) : 0;
		boolean bleeding = spl.length > 4 ? spl[4].equals("1") : false;
		boolean bonesBroken = spl.length > 5 ? spl[5].equals("1") : false;
		boolean poisoned = spl.length > 6 ? spl[6].equals("1") : false;
		
		return new PlayerData(thirst, zombiesKilled, playersKilled, minutesSurvived, bleeding, bonesBroken, poisoned);
		
	}
	
}
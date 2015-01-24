package craftZ.modules;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

import craftZ.CraftZ;
import craftZ.Module;


public class FirstTimeUseModule extends Module {
	
	public static String[] firstRunMessages = {
		"CraftZ -- It seems that this is the first time you run CraftZ. There are a few important things on first-time use:",
		"* It is likely that CraftZ will not be able to load up because the default world might not exist.",
		"* The whole world which is used by CraftZ (defaults to 'world') will be changed. This includes:",
		"   - Ingame daytime will be the same as reallife daytime.",
		"   - Only zombies will spawn, even during the day.",
		"   - The world is protected from most changes (by players AND other things). " +
				"In addition, players without special permissions are restricted in their actions.",
		"* Please modify the configuration file located at '/plugins/CraftZ/config.yml' to suit your needs. " +
				"Help can be found at http://bit.ly/1baXddU (Bukkit).",
		"* You should setup your world for CraftZ: place chests and spawns, make a lobby and so on. " +
				"Help can be found at http://bit.ly/1ejXhsU (Bukkit).",
		"Have fun!"
	};
	public static String[] firstRunPlayerMessages = {
		"CraftZ -- It seems that this is the first time you run CraftZ. There are a few important things on first-time use:",
		"* It is likely that CraftZ will not be able to load up because the default world might not exist.",
		"* The whole world which is used by CraftZ (defaults to 'world') will be changed.",
		"* Please modify the configuration file to suit your needs. See http://bit.ly/1baXddU",
		"* You should setup your world for CraftZ. See http://bit.ly/1ejXhsU",
		"More info can be found in the console or at '/plugins/CraftZ/README.txt'."
	};
	
	
	
	public FirstTimeUseModule(CraftZ craftZ) {
		super(craftZ);
	}
	
	
	
	
	
	@Override
	public void onLoad(boolean configReload) {
		
		if (configReload)
			return;
		
		File readme = new File(getCraftZ().getDataFolder(), "README.txt");
		readme.getParentFile().mkdirs();
		
		if (!readme.exists()) {
			
			try {
				
				BufferedWriter wr = new BufferedWriter(new FileWriter(readme));
				for (String s : firstRunMessages) {
					wr.write(s);
					wr.newLine();
				}
				wr.flush();
				wr.close();
				
			} catch (Exception ex) {
				CraftZ.br();
				CraftZ.severe("Could not write the README.txt file to disk!");
			}
			
		}
		
		
		
		CraftZ.br();
		for (String s : firstRunMessages)
			CraftZ.info(s);
		CraftZ.br();
		CraftZ.info("You can also find this message at any time in '/plugins/CraftZ/README.txt'.");
		CraftZ.br();
		
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		
		Player p = event.getPlayer();
		
		if (p.isOp()) {
			
			p.sendMessage("");
			for (String s : firstRunPlayerMessages)
				p.sendMessage(s);
			p.sendMessage("");
			
		} else {
			p.sendMessage("Thanks for installing CraftZ! Please take a look at the console for some important information on how to get started.");
		}
		
	}
	
}
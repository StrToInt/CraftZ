package craftZ;

import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigData {
	
	public FileConfiguration config;
	public File configFile;
	
	
	
	@Override
	public boolean equals(Object anObject) {
		
		if (!(anObject instanceof ConfigData)) {
			return false;
		}
		
		return configFile.equals(((ConfigData) anObject).configFile);
		
	}
	
}
package craftZ.util;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;


public class ConfigData {
	
	public FileConfiguration config;
	public File configFile;
	
	
	
	public ConfigData(File configFile) {
		this.configFile = configFile;
	}
	
	
	
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((configFile == null) ? 0 : configFile.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConfigData other = (ConfigData) obj;
		if (configFile == null) {
			if (other.configFile != null)
				return false;
		} else if (!configFile.equals(other.configFile))
			return false;
		return true;
	}
	
}
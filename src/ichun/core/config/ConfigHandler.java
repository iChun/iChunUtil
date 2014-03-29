package ichun.core.config;

import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class ConfigHandler
{
	public static ArrayList<Config> configs = new ArrayList<Config>();
	
	public static Config createConfig(File file, String modIdName, String modName, Logger logger, IConfigUser parent)
	{
		Configuration cfg = new Configuration(file);
		cfg.load();
		Config config = new Config(cfg, modIdName, modName, logger, parent);
		configs.add(config);
		Collections.sort(configs);
		return config;
	}
}

package ichun.core.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import net.minecraftforge.common.Configuration;

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

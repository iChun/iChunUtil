package ichun.common.core.config;

import net.minecraftforge.common.config.Property;

public interface IConfigUser 
{
	boolean onConfigChange(Config cfg, Property prop);
}

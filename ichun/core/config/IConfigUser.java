package ichun.core.config;

import net.minecraftforge.common.Property;

public interface IConfigUser 
{
	boolean onConfigChange(Config cfg, Property prop);
}

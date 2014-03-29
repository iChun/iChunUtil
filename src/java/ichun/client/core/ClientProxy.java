package ichun.client.core;

import ichun.core.CommonProxy;
import ichun.core.util.ResourceHelper;

public class ClientProxy extends CommonProxy 
{

	@Override
	public void init()
	{
		ResourceHelper.init();
	}
}

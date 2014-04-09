package ichun.client.core;

import ichun.common.core.CommonProxy;
import ichun.common.core.util.ResourceHelper;

public class ClientProxy extends CommonProxy 
{

	@Override
	public void init()
	{
        super.init();
		ResourceHelper.init();
	}
}

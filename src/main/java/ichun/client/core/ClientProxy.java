package ichun.client.core;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import ichun.client.keybind.KeyBind;
import ichun.common.core.CommonProxy;
import ichun.common.core.util.ResourceHelper;

public class ClientProxy extends CommonProxy 
{

	@Override
	public void init()
	{
        super.init();
		ResourceHelper.init();

        if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
        {
            tickHandlerClient = new TickHandlerClient();
            FMLCommonHandler.instance().bus().register(tickHandlerClient);
        }
    }

    @Override
    public KeyBind registerKeyBind(KeyBind bind, KeyBind replacing)
    {
        if(replacing != null)
        {
            if(bind.equals(replacing))
            {
                return replacing;
            }
            for(int i = tickHandlerClient.keyBindList.size() - 1; i >= 0; i--)
            {
                KeyBind keybind = tickHandlerClient.keyBindList.get(i);
                if(keybind.equals(replacing))
                {
                    keybind.usages--;
                    if(keybind.usages <= 0)
                    {
                        tickHandlerClient.keyBindList.remove(i);
                    }
                }
            }
        }

        for(KeyBind keybind : tickHandlerClient.keyBindList)//Check to see if the keybind is already registered. If it is, increase usages count. If not, add it.
        {
            if(keybind.equals(bind))
            {
                keybind.usages++;
                return keybind;
            }
        }
        bind.usages++;
        tickHandlerClient.keyBindList.add(bind);
        return bind;
    }
}

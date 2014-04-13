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
    public void registerKeyBind(KeyBind bind)
    {
        boolean used = false;
        for(KeyBind keybind : tickHandlerClient.keyBindList)//Check to see if the keybind is already registered. If it is, increase usages count. If not, add it.
        {
            if(keybind.equals(bind))
            {
                keybind.usages++;
                used = true;
                break;
            }
        }
        if(!used)
        {
            bind.usages++;
            tickHandlerClient.keyBindList.add(bind);
        }
    }
}

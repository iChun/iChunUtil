package ichun.client.core;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ichun.client.keybind.KeyBind;
import ichun.common.core.CommonProxy;
import ichun.common.core.config.Config;
import ichun.common.core.util.ResourceHelper;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class ClientProxy extends CommonProxy 
{

	@Override
	public void init()
	{
        super.init();
		ResourceHelper.init();

        File file = new File(ResourceHelper.getConfigFolder(), "iChunUtil_KeyBinds.cfg");
        Config.configKeybind = new Configuration(file);
        Config.configKeybind.load();

        tickHandlerClient = new TickHandlerClient();
        FMLCommonHandler.instance().bus().register(tickHandlerClient);
    }

    @Override
    @SideOnly(Side.CLIENT)
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
                    bind.setPulse(keybind.canPulse, keybind.pulseTime);
                    bind.ignoreHold = keybind.ignoreHold;
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

    @Override
    @SideOnly(Side.CLIENT)
    public void registerMinecraftKeyBind(KeyBinding bind)
    {
        tickHandlerClient.mcKeyBindList.put(bind, new KeyBind(bind.getKeyCode(), false, false, false, true));
    }

}

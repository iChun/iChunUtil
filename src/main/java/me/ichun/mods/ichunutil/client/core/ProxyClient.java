package me.ichun.mods.ichunutil.client.core;

import com.google.common.base.Splitter;
import me.ichun.mods.ichunutil.client.core.event.EventHandlerClient;
import me.ichun.mods.ichunutil.client.keybind.KeyBind;
import me.ichun.mods.ichunutil.client.render.RendererHelper;
import me.ichun.mods.ichunutil.common.core.ProxyCommon;
import me.ichun.mods.ichunutil.common.core.config.ConfigHandler;
import me.ichun.mods.ichunutil.common.core.util.EntityHelper;
import me.ichun.mods.ichunutil.common.core.util.ResourceHelper;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.File;
import java.util.List;

public class ProxyClient extends ProxyCommon
{
    @Override
    public void preInit()
    {
        super.preInit();
        ResourceHelper.init();
        RendererHelper.init();

        File file = new File(ResourceHelper.getConfigFolder(), "iChunUtil_KeyBinds.cfg");
        ConfigHandler.configKeybind = new Configuration(file);
        ConfigHandler.configKeybind.load();

        List cms = Splitter.on("\\n").splitToList(StatCollector.translateToLocal("ichunutil.config.cat.keybind.comment"));
        String cm = "";
        for(int ll = 0; ll < cms.size(); ll++)
        {
            cm = cm + cms.get(ll);
            if(ll != cms.size() - 1)
            {
                cm = cm + "\n";
            }
        }
        ConfigHandler.configKeybind.addCustomCategoryComment("keybinds", cm);

        EntityHelper.injectMinecraftPlayerGameProfile();

        iChunUtil.eventHandlerClient = new EventHandlerClient();
        MinecraftForge.EVENT_BUS.register(iChunUtil.eventHandlerClient);
    }

    @Override
    public void init()
    {
        super.init();

        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(iChunUtil.blockCompactPorkchop), 0, new ModelResourceLocation("ichunutil:compactPorkchop", "inventory"));
    }

    @Override
    public void postInit()
    {
        super.postInit();
        ConfigHandler.configKeybind.save();
    }

    @Override
    public void nudgeHand(float mag)
    {
        Minecraft.getMinecraft().thePlayer.renderArmPitch += mag;
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
            for(int i = iChunUtil.eventHandlerClient.keyBindList.size() - 1; i >= 0; i--)
            {
                KeyBind keybind = iChunUtil.eventHandlerClient.keyBindList.get(i);
                if(keybind.equals(replacing))
                {
                    keybind.usages--;
                    if(keybind.usages <= 0)
                    {
                        iChunUtil.eventHandlerClient.keyBindList.remove(i);
                    }
                    bind.ignoreHold = keybind.ignoreHold;
                }
            }
        }

        for(KeyBind keybind : iChunUtil.eventHandlerClient.keyBindList)//Check to see if the keybind is already registered. If it is, increase usages count. If not, add it.
        {
            if(keybind.equals(bind))
            {
                keybind.usages++;
                return keybind;
            }
        }
        bind.usages++;
        iChunUtil.eventHandlerClient.keyBindList.add(bind);
        return bind;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerMinecraftKeyBind(KeyBinding bind)
    {
        iChunUtil.eventHandlerClient.mcKeyBindList.put(bind, (new KeyBind(bind.getKeyCode(), false, false, false, true)).setIsMinecraftBind());
    }
}

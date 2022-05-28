package me.ichun.mods.ichunutil.loader.fabric.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.ichun.mods.ichunutil.client.core.EventHandlerClient;
import net.minecraft.client.Minecraft;

public class ModMenuIntegration implements ModMenuApi
{
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory()
    {
        return parent -> EventHandlerClient.getConfigGui(Minecraft.getInstance(), parent);
    }
}

package us.ichun.mods.ichunutil.common.core.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;
import us.ichun.mods.ichunutil.client.gui.config.GuiConfigs;

import java.util.Set;

public class GenericModGuiFactory implements IModGuiFactory
{
    @Override
    public void initialize(Minecraft mc)
    {
    }

    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass()
    {
        return GuiConfigs.class;
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories()
    {
        return null;
    }

    @Override
    public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element)
    {
        return null;
    }
}

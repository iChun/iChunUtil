package us.ichun.mods.ichunutil.client.gui;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

//If your GUI has sliders you need to implement this interface.
@SideOnly(Side.CLIENT)
public interface ISlider
{
    void onChangeSliderValue(GuiSlider slider);
}

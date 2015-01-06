package us.ichun.mods.ichunutil.client.gui;

import net.minecraftforge.fml.relauncher.*;

//If your GUI has sliders you need to implement this interface.
@SideOnly(Side.CLIENT)
public interface ISlider
{
    void onChangeSliderValue(GuiSlider slider);
}

package us.ichun.mods.ichunutil.common.core.config.types;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Colour
{
    public int r = 0;
    public int g = 0;
    public int b = 0;

    public Colour(int clr)
    {
        r = (clr >> 16 & 0xff);
        g = (clr >> 8 & 0xff);
        b = (clr & 0xff);
    }

    public Colour(int r, int g, int b)
    {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public int getColour()
    {
        return (r << 16) + (g << 8) + (b);
    }

    public String serialize()
    {
        String rS = Integer.toHexString(r);
        String gS = Integer.toHexString(g);
        String bS = Integer.toHexString(b);
        while(rS.length() < 2)
        {
            rS = "0" + rS;
        }
        while(gS.length() < 2)
        {
            gS = "0" + gS;
        }
        while(bS.length() < 2)
        {
            bS = "0" + bS;
        }
        return "#" + rS + gS + bS;
    }

    public void deserialize(String s)
    {
        int clr = Integer.decode(s);
        r = (clr >> 16 & 0xff);
        g = (clr >> 8 & 0xff);
        b = (clr & 0xff);
    }

    @SideOnly(Side.CLIENT)
    public void setColourToCurrent()
    {
        setColourToCurrent(255);
    }

    @SideOnly(Side.CLIENT)
    public void setColourToCurrent(int alpha)
    {
        GlStateManager.color((float)r / 255F, (float)g / 255F, (float)b / 255F, (float)alpha / 255F);
    }
}

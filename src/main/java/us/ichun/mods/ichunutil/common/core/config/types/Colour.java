package us.ichun.mods.ichunutil.common.core.config.types;

import scala.Int;

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
}

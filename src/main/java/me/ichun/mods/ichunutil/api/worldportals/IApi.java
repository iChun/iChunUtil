package me.ichun.mods.ichunutil.api.worldportals;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IApi
{
    /**
     * Returns the render level of the World Portal. 0 means currently rendering the world. Anything higher means it's rendering a world portal.
     * If you disable stencils or clear the stencil buffer please only do so when getRenderLevel() is 0.
     *
     * @return render level.
     */
    @SideOnly(Side.CLIENT)
    public int getRenderLevel();
}

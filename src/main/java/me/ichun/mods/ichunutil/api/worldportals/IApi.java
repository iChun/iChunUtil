package me.ichun.mods.ichunutil.api.worldportals;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IApi
{
    /**
     * Returns the render level of the World Portal. 0 means not currently rendering anything/rendering the world. Anything higher means it's rendering a world portal.
     * If you disable stencils or clear the stencil buffer please only do so when getRenderLevel() is 0.
     *
     * @return render level.
     */
    @SideOnly(Side.CLIENT)
    public int getRenderLevel();

    /**
     * Returns the camera roll (per render level).
     * Yes, camera roll exists in Minecraft.
     *
     * @param renderLevel the current render level of the World Portals
     * @param partialTick partial render tick.
     * @return the camera roll for the provided renderLevel. Returns 0 if we don't have a roll for said renderLevel.
     */
    @SideOnly(Side.CLIENT)
    public float getCameraRoll(int renderLevel, float partialTick);
}

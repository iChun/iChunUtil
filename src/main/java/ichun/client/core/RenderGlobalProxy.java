package ichun.client.core;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.player.EntityPlayer;

@SideOnly(Side.CLIENT)
public class RenderGlobalProxy extends RenderGlobal
{

	private boolean renderSky;
	
    public RenderGlobalProxy(Minecraft par1Minecraft)
    {
        super(par1Minecraft);
        renderSky = true;
    }
    
    public RenderGlobalProxy setRenderSky(boolean flag)
    {
    	renderSky = flag;
    	return this;
    }

    @Override
    public void updateClouds()
    {
    	if(renderSky)
    	{
    		super.updateClouds();
    	}
    }

    @Override
    public void renderSky(float par1)
    {
    	if(renderSky)
    	{
    		super.renderSky(par1);
    	}
    }

    @Override
    public void renderClouds(float par1)
    {
    	if(renderSky)
    	{
    		super.renderClouds(par1);
    	}
    }

    @Override
    public boolean hasCloudFog(double par1, double par3, double par5, float par7)
    {
    	if(renderSky)
    	{
    		return super.hasCloudFog(par1, par3, par5, par7);
    	}
        return false;
    }

    @Override
    public void renderCloudsFancy(float par1)
    {
    	if(renderSky)
    	{
    		super.renderCloudsFancy(par1);
    	}
    }

    /**
     * Plays the specified record. Arg: recordName, x, y, z
     */
    @Override
    public void playRecord(String par1Str, int par2, int par3, int par4)
    {
    }

    /**
     * Plays the specified sound. Arg: soundName, x, y, z, volume, pitch
     */
    @Override
    public void playSound(String par1Str, double par2, double par4, double par6, float par8, float par9) {}

    /**
     * Plays sound to all near players except the player reference given
     */
    @Override
    public void playSoundToNearExcept(EntityPlayer par1EntityPlayer, String par2Str, double par3, double par5, double par7, float par9, float par10) {}

    @Override
    public void broadcastSound(int par1, int par2, int par3, int par4, int par5)
    {
    }

    /**
     * Plays a pre-canned sound effect along with potentially auxiliary data-driven one-shot behaviour (particles, etc).
     */
    @Override
    public void playAuxSFX(EntityPlayer par1EntityPlayer, int par2, int par3, int par4, int par5, int par6)
    {
    }
}

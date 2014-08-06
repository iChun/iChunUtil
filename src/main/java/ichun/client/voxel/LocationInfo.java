package ichun.client.voxel;

import ichun.common.iChunUtil;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class LocationInfo 
{
	public double posX;
	public double posY;
	public double posZ;
	
	public float renderYawOffset;
	public float rotationYawHead;
	public float rotationPitch;
	
	public float limbSwing;
	public float limbSwingAmount;

    public boolean sneaking;
    public boolean canRender;
	
	public long lastTick;

    public float pitchChange;
    public float yawChange;

    public ResourceLocation txLocation;

	public LocationInfo(EntityPlayer player)
	{
		update(player);
	}
	
	public void update(EntityPlayer player)
	{
		posX = player.posX;
		posY = player.boundingBox.minY;
		posZ = player.posZ;
		
		renderYawOffset = player.renderYawOffset;
		rotationYawHead = player.rotationYawHead;
		rotationPitch = player.rotationPitch;
		
		limbSwing = player.limbSwing;
		limbSwingAmount = player.limbSwingAmount;

        sneaking = player.isSneaking();
		
		lastTick = player.worldObj.getWorldTime();
        float speed = 7.5F;
        pitchChange = player.worldObj.rand.nextFloat() * (speed * 2F) - speed;
        yawChange = player.worldObj.rand.nextFloat() * (speed * 2F) - speed;

        canRender = false;
        for(String s : iChunUtil.proxy.trailTicker.patronList)
        {
            if(s.equals(player.getGameProfile().getId().toString()))
            {
                canRender = true;
                break;
            }
        }
        txLocation = ((AbstractClientPlayer)player).getLocationSkin();
        if(canRender && player.isInvisible())
        {
            canRender = false;
        }
        if(canRender && iChunUtil.hasMorphMod)
        {
            if(morph.api.Api.hasMorph(player.getCommandSenderName(), true))
            {
                if(morph.api.Api.morphProgress(player.getCommandSenderName(), true) < 1.0F || !(morph.api.Api.getMorphEntity(player.getCommandSenderName(), true) instanceof AbstractClientPlayer))
                {
                    canRender = false;
                }
                if(morph.api.Api.getMorphEntity(player.getCommandSenderName(), true) instanceof AbstractClientPlayer)
                {
                    txLocation = ((AbstractClientPlayer)morph.api.Api.getMorphEntity(player.getCommandSenderName(), true)).getLocationSkin();
                }
            }
        }
    }
}

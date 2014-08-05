package ichun.client.voxel;

import net.minecraft.entity.player.EntityPlayer;

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
	
	public long lastTick;

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
		
		lastTick = player.worldObj.getWorldTime();
	}
}

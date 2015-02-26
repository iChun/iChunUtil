package us.ichun.mods.ichunutil.client.voxel;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import us.ichun.mods.ichunutil.common.iChunUtil;
import us.ichun.mods.ichunutil.common.tracker.EntityInfo;
import us.ichun.mods.ichunutil.common.tracker.IAdditionalTrackerInfo;

public class TrailTracker
    implements IAdditionalTrackerInfo
{
    public boolean canRender;

    public float pitchChange;
    public float yawChange;

    public ResourceLocation txLocation;

    @Override
    public void track(EntityInfo info)
    {
        if(info.tracked instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer)info.tracked;
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
            if(canRender && (player.isInvisible() || player.isPlayerSleeping()))
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
}

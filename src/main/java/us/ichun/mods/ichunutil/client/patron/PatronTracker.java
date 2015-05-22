package us.ichun.mods.ichunutil.client.patron;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import us.ichun.mods.ichunutil.common.core.patron.PatronInfo;
import us.ichun.mods.ichunutil.common.iChunUtil;
import us.ichun.mods.ichunutil.common.tracker.EntityInfo;
import us.ichun.mods.ichunutil.common.tracker.IAdditionalTrackerInfo;

public class PatronTracker
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
            for(PatronInfo info1 : iChunUtil.proxy.effectTicker.patronList)
            {
                if(info1.id.equals(player.getGameProfile().getId().toString()) && info1.type == 1)
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

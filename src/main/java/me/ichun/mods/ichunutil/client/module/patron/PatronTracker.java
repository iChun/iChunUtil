package me.ichun.mods.ichunutil.client.module.patron;

import me.ichun.mods.ichunutil.common.core.tracker.EntityTrackerRegistry;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.module.patron.PatronInfo;
import me.ichun.mods.morph.api.MorphApi;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;

public class PatronTracker
    implements EntityTrackerRegistry.IAdditionalTrackerInfo
{
    public boolean canRender;

    public float pitchChange;
    public float yawChange;

    public ResourceLocation txLocation;

    @Override
    public void track(EntityTrackerRegistry.EntityInfo info)
    {
        if(info.tracked instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer)info.tracked;
            float speed = 7.5F;
            pitchChange = player.worldObj.rand.nextFloat() * (speed * 2F) - speed;
            yawChange = player.worldObj.rand.nextFloat() * (speed * 2F) - speed;

            canRender = false;
            for(PatronInfo info1 : iChunUtil.eventHandlerClient.patrons)
            {
                if(info1.id.equals(player.getGameProfile().getId().toString().replaceAll("-", "")) && info1.effectType == 1)
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
            if(canRender && iChunUtil.hasMorphMod())
            {
                if(MorphApi.getApiImpl().hasMorph(player.getName(), Side.CLIENT))
                {
                    if(MorphApi.getApiImpl().morphProgress(player.getName(), Side.CLIENT) < 1.0F || !(MorphApi.getApiImpl().getMorphEntity(player.worldObj, player.getName(), Side.CLIENT) instanceof AbstractClientPlayer))
                    {
                        canRender = false;
                    }
                    if(MorphApi.getApiImpl().getMorphEntity(player.worldObj, player.getName(), Side.CLIENT) instanceof AbstractClientPlayer)
                    {
                        txLocation = ((AbstractClientPlayer)MorphApi.getApiImpl().getMorphEntity(player.worldObj, player.getName(), Side.CLIENT)).getLocationSkin();
                    }
                }
            }
        }
    }
}

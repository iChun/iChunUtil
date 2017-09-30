package me.ichun.mods.ichunutil.client.module.patron;

import me.ichun.mods.ichunutil.common.core.tracker.EntityTrackerRegistry;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.module.patron.PatronInfo;
import me.ichun.mods.morph.api.MorphApi;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;

public class PatronTracker implements EntityTrackerRegistry.IAdditionalTrackerInfo
{
    public boolean canRender;

    public float pitchChange;
    public float yawChange;

    public float elytraX;
    public float elytraZ;

    public ResourceLocation txLocation;

    @Override
    public void track(EntityTrackerRegistry.EntityInfo info)
    {
        if(info.tracked instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer)info.tracked;
            float speed = 7.5F;
            pitchChange = player.getEntityWorld().rand.nextFloat() * (speed * 2F) - speed;
            yawChange = player.getEntityWorld().rand.nextFloat() * (speed * 2F) - speed;

            canRender = false;
            for(PatronInfo info1 : PatronEffectRenderer.patrons)
            {
                if(info1.id.equals(player.getGameProfile().getId().toString().replaceAll("-", "")) && info1.effectType == PatronEffectRenderer.EnumEffect.VOXEL.getId())
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
                    if(MorphApi.getApiImpl().morphProgress(player.getName(), Side.CLIENT) < 1.0F || !(MorphApi.getApiImpl().getMorphEntity(player.getEntityWorld(), player.getName(), Side.CLIENT) instanceof AbstractClientPlayer))
                    {
                        canRender = false;
                    }
                    if(MorphApi.getApiImpl().getMorphEntity(player.getEntityWorld(), player.getName(), Side.CLIENT) instanceof AbstractClientPlayer)
                    {
                        txLocation = ((AbstractClientPlayer)MorphApi.getApiImpl().getMorphEntity(player.getEntityWorld(), player.getName(), Side.CLIENT)).getLocationSkin();
                    }
                }
            }

            float f = 0.2617994F;
            float f1 = -0.2617994F;

            if(player.isElytraFlying())
            {
                float f4 = 1.0F;

                double motionX = player.posX - player.prevPosX;
                double motionY = player.posY - player.prevPosY;
                double motionZ = player.posZ - player.prevPosZ;
                if(motionY < 0.0D)
                {
                    Vec3d vec3d = (new Vec3d(motionX, motionY, motionZ)).normalize();
                    f4 = 1.0F - (float)Math.pow(-vec3d.y, 1.5D);
                }

                f = f4 * 0.34906584F + (1.0F - f4) * f;
                f1 = f4 * -((float)Math.PI / 2F) + (1.0F - f4) * f1;
            }
            else if(player.isSneaking())
            {
                f = ((float)Math.PI * 2F / 9F);
                f1 = -((float)Math.PI / 4F);
            }

            elytraX = f;
            elytraZ = f1;
        }
    }
}

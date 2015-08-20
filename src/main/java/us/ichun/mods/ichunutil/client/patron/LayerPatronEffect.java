package us.ichun.mods.ichunutil.client.patron;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import us.ichun.mods.ichunutil.client.model.ModelSnout;
import us.ichun.mods.ichunutil.common.core.EntityHelperBase;
import us.ichun.mods.ichunutil.common.core.patron.PatronInfo;
import us.ichun.mods.ichunutil.common.core.util.EventCalendar;
import us.ichun.mods.ichunutil.common.core.util.ResourceHelper;
import us.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.morph.api.MorphApi;

@SideOnly(Side.CLIENT)
public class LayerPatronEffect implements LayerRenderer
{
    public ModelSnout modelSnout = new ModelSnout();
    public RenderPlayer parentRenderer;

    public LayerPatronEffect(RenderPlayer render)
    {
        parentRenderer = render;
    }

    //func_177093_a(entity, limb stuff, limb stuff, partialTicks, f5, yaw stuff, pitch stuff, 0.0625F);
    public void doRenderLayer(EntityPlayer player, float f, float f1, float renderTick, float f2, float f3, float f4, float f5)
    {
        EntityPlayer oriPlayer = player;
        if(iChunUtil.hasMorphMod)
        {
            EntityLivingBase ent = MorphApi.getApiImpl().getMorphEntity(player.worldObj, player.getCommandSenderName(), Side.CLIENT);
            if(ent != null) //is morphed
            {
                if(!(ent instanceof EntityPlayer) || MorphApi.getApiImpl().morphProgress(player.getCommandSenderName(), Side.CLIENT) < 1.0F)
                {
                    return;
                }
                player = (EntityPlayer)ent;
            }
        }
        PatronInfo info = null;

        for(PatronInfo info1 : iChunUtil.proxy.effectTicker.patronList)
        {
            if(info1.id.equals(oriPlayer.getGameProfile().getId().toString()))
            {
                info = info1;
                break;
            }
        }

        if(!player.isInvisible() && (EventCalendar.isAFDay() || info != null && info.type == 2)) //special casing for pig snout effect
        {
            parentRenderer.bindTexture(ResourceHelper.texPig);

            GlStateManager.pushMatrix();

            if(player.isSneaking())
            {
                GlStateManager.translate(0F, 0.265F, 0.0F);
            }

            GlStateManager.rotate(EntityHelperBase.interpolateRotation(player.prevRotationYawHead, player.rotationYawHead, renderTick) - EntityHelperBase.interpolateRotation(player.prevRenderYawOffset, player.renderYawOffset, renderTick), 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(EntityHelperBase.interpolateRotation(player.prevRotationPitch, player.rotationPitch, renderTick), 1.0F, 0.0F, 0.0F);

            modelSnout.render(0.0625F);

            GlStateManager.popMatrix();
        }
        //render other on-player patron rewards below here
//        if(info != null)
//        {
//            switch(info.type)
//            {
//
//            }
//        }
    }

    public boolean shouldCombineTextures()
    {
        return false;
    }

    public void doRenderLayer(EntityLivingBase p_177141_1_, float p_177141_2_, float p_177141_3_, float p_177141_4_, float p_177141_5_, float p_177141_6_, float p_177141_7_, float p_177141_8_)
    {
        this.doRenderLayer((EntityPlayer)p_177141_1_, p_177141_2_, p_177141_3_, p_177141_4_, p_177141_5_, p_177141_6_, p_177141_7_, p_177141_8_);
    }
}
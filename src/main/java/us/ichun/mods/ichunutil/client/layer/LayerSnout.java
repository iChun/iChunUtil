package us.ichun.mods.ichunutil.client.layer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import us.ichun.mods.ichunutil.client.model.ModelSnout;
import us.ichun.mods.ichunutil.client.render.RendererHelper;
import us.ichun.mods.ichunutil.common.core.EntityHelperBase;
import us.ichun.mods.ichunutil.common.core.patron.PatronInfo;
import us.ichun.mods.ichunutil.common.core.util.EventCalendar;
import us.ichun.mods.ichunutil.common.core.util.ResourceHelper;
import us.ichun.mods.ichunutil.common.iChunUtil;

@SideOnly(Side.CLIENT)
public class LayerSnout implements LayerRenderer
{
    public ModelSnout modelSnout = new ModelSnout();
    public RenderPlayer parentRenderer;

    public LayerSnout(RenderPlayer render)
    {
        parentRenderer = render;
    }

    //func_177093_a(entity, limb stuff, limb stuff, partialTicks, f5, yaw stuff, pitch stuff, 0.0625F);
    public void doRenderLayer(EntityPlayer player, float f, float f1, float renderTick, float f2, float f3, float f4, float f5)
    {
        if(iChunUtil.hasMorphMod)
        {
            EntityLivingBase ent = morph.api.Api.getMorphEntity(player.getCommandSenderName(), true);
            if(ent != null)
            {
                if(!(ent instanceof EntityPlayer))
                {
                    return;
                }
                player = (EntityPlayer)ent;
            }
        }
        boolean render = false;

        for(PatronInfo info1 : iChunUtil.proxy.trailTicker.patronList)
        {
            if(info1.id.equals(player.getGameProfile().getId().toString()) && info1.type == 2)
            {
                render = true;
                break;
            }
        }
        if(!player.isInvisible() && (EventCalendar.isAFDay() || render))
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
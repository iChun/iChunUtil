package us.ichun.mods.ichunutil.client.patron;

import me.ichun.mods.morph.api.MorphApi;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import us.ichun.mods.ichunutil.client.model.ModelBee;
import us.ichun.mods.ichunutil.client.model.ModelSnout;
import us.ichun.mods.ichunutil.common.core.EntityHelperBase;
import us.ichun.mods.ichunutil.common.core.patron.PatronInfo;
import us.ichun.mods.ichunutil.common.core.util.EventCalendar;
import us.ichun.mods.ichunutil.common.core.util.ResourceHelper;
import us.ichun.mods.ichunutil.common.iChunUtil;

@SideOnly(Side.CLIENT)
public class LayerPatronEffect implements LayerRenderer
{
    public ModelSnout modelSnout = new ModelSnout();
    public ModelBee modelBee = new ModelBee();
    public RenderPlayer parentRenderer;

    public static final ResourceLocation texBee = new ResourceLocation("ichunutil","textures/model/bee.png");

    public LayerPatronEffect(RenderPlayer render)
    {
        parentRenderer = render;
    }

    //func_177093_a(entity, limb stuff, limb stuff, partialTicks, f5, yaw stuff, pitch stuff, 0.0625F);
    public void doRenderLayer(EntityPlayer player, float f, float f1, float renderTick, float f2, float f3, float f4, float f5)
    {
        PatronInfo info = EntityPatronEffect.getPatronInfo(player);

        if(!player.isInvisible()) //special casing for pig snout effect
        {
            if(EventCalendar.isAFDay() || info != null && info.type == 2) //render Pig Snout
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
            if(info != null)
            {
                switch(info.type)
                {
                    case 4: //render BEE
                    {
                        parentRenderer.bindTexture(texBee);

                        GlStateManager.pushMatrix();

                        if(player.isSneaking())
                        {
                            GlStateManager.translate(0F, 0.265F, 0.0F);
                        }

                        float sinProg1 = (float)Math.sin(Math.toRadians((player.ticksExisted / 30F) * 360F));
                        float sinProg2 = (float)Math.sin(Math.toRadians((player.ticksExisted / 20F) * 360F));
                        float pitchOffset = Math.abs(EntityHelperBase.interpolateRotation(player.prevRotationPitch, player.rotationPitch, renderTick)) / 90F;

                        GlStateManager.rotate(((player.ticksExisted + renderTick) / 40F) * 360F, 0F, 1F, 0F);

                        GlStateManager.translate(0.4F + (pitchOffset * 0.25F) + (sinProg1 * 0.05F), -0.25F + (pitchOffset * 0.15F) + (sinProg2 * 0.05F), 0F);

                        modelBee.render(null, 0F, 0F, 0F, 0F, 0F, 0.0625F);

                        GlStateManager.popMatrix();

                        break;
                    }
                }
            }
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
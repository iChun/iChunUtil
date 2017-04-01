package me.ichun.mods.ichunutil.client.module.patron;

import me.ichun.mods.ichunutil.client.model.ModelBee;
import me.ichun.mods.ichunutil.client.model.ModelSnout;
import me.ichun.mods.ichunutil.common.core.util.EntityHelper;
import me.ichun.mods.ichunutil.common.core.util.EventCalendar;
import me.ichun.mods.ichunutil.common.core.util.ResourceHelper;
import me.ichun.mods.ichunutil.common.module.patron.PatronInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class LayerPatronEffect implements LayerRenderer<EntityPlayer>
{
    public ModelSnout modelSnout = new ModelSnout();
    public ModelBee modelBee = new ModelBee();
    public RenderPlayer parentRenderer;

    public static final ResourceLocation texBee = new ResourceLocation("ichunutil", "textures/model/bee.png");

    public LayerPatronEffect(RenderPlayer render)
    {
        parentRenderer = render;
    }

    @Override
    //func_177093_a(entity, limb stuff, limb stuff, partialTicks, f5, yaw stuff, pitch stuff, 0.0625F);
    public void doRenderLayer(EntityPlayer player, float f, float f1, float renderTick, float f2, float f3, float f4, float f5)
    {
        PatronInfo info = PatronEffectRenderer.getPatronInfo(player);

        if(!player.isInvisible()) //special casing for pig snout effect
        {
            if(EventCalendar.isAFDay() || info != null && info.showEffect && info.effectType == PatronEffectRenderer.EnumEffect.PIG_SNOUT.getId())
            {
                parentRenderer.bindTexture(ResourceHelper.texPig);

                GlStateManager.pushMatrix();

                if(player.isSneaking())
                {
                    GlStateManager.translate(0F, 0.265F, 0.0F);
                }

                GlStateManager.rotate(EntityHelper.interpolateValues(player.prevRotationYawHead, player.rotationYawHead, renderTick) - EntityHelper.interpolateValues(player.prevRenderYawOffset, player.renderYawOffset, renderTick), 0.0F, 1.0F, 0.0F);
                if(player.getTicksElytraFlying() > 4)
                {
                    GlStateManager.rotate((float)Math.toDegrees(-((float)Math.PI / 4F)), 1.0F, 0.0F, 0.0F);
                }
                else
                {
                    GlStateManager.rotate(EntityHelper.interpolateValues(player.prevRotationPitch, player.rotationPitch, renderTick), 1.0F, 0.0F, 0.0F);
                }

                modelSnout.render(0.0625F);

                GlStateManager.popMatrix();
            }
            if(info != null && info.showEffect)
            {
                PatronEffectRenderer.EnumEffect effect = PatronEffectRenderer.EnumEffect.getById(info.effectType);
                switch(effect)
                {
                    case BEE:
                    {
                        parentRenderer.bindTexture(texBee);

                        GlStateManager.pushMatrix();

                        if(player.isSneaking())
                        {
                            GlStateManager.translate(0F, 0.265F, 0.0F);
                        }

                        float sinProg1 = (float)Math.sin(Math.toRadians((player.ticksExisted / 30F) * 360F));
                        float sinProg2 = (float)Math.sin(Math.toRadians((player.ticksExisted / 20F) * 360F));
                        float pitchOffset = (player.getTicksElytraFlying() > 4 ? 60F : Math.abs(EntityHelper.interpolateValues(player.prevRotationPitch, player.rotationPitch, renderTick))) / 90F;

                        GlStateManager.rotate(((player.ticksExisted + renderTick) / 40F) * 360F, 0F, 1F, 0F);

                        GlStateManager.translate(0.4F + (pitchOffset * 0.25F) + (sinProg1 * 0.05F), -0.25F + (pitchOffset * 0.15F) + (sinProg2 * 0.05F), 0F);

                        modelBee.render(0.0625F);

                        GlStateManager.popMatrix();

                        break;
                    }
                }
            }
        }
    }

    @Override
    public boolean shouldCombineTextures()
    {
        return false;
    }
}

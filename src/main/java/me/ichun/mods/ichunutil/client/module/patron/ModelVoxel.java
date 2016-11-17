package me.ichun.mods.ichunutil.client.module.patron;

import me.ichun.mods.ichunutil.client.entity.EntityLatchedRenderer;
import me.ichun.mods.ichunutil.common.core.tracker.EntityTrackerRegistry;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Random;

public class ModelVoxel extends ModelBase
{
    public Random rand = new Random();

    public ModelRenderer[] modelHead;
    public ModelRenderer[] modelBody;
    public ModelRenderer[] modelLimb;
    public ModelRenderer[] modelLimbMirrored;

    public ModelVoxel()
    {
        modelHead = new ModelRenderer[8 * 8 * 8];
        modelBody = new ModelRenderer[12 * 4 * 8];
        modelLimb = new ModelRenderer[12 * 4 * 4];
        modelLimbMirrored = new ModelRenderer[12 * 4 * 4];


        int sizeX = 8;
        int sizeY = 8;
        int sizeZ = 8;

        textureWidth = 48;
        textureHeight = 24;

        for(int i = 0; i < sizeX; i++)
        {
            for(int j = 0; j < sizeY; j++)
            {
                for(int k = 0; k < sizeZ; k++)
                {
                    if(i == 0 || i == sizeX - 1 || j == 0 || j == sizeY - 1 || k == 0 || k == sizeZ - 1)
                    {
                        int x = j == sizeY - 1 && !(i == 0 || i == sizeX - 1 || k == 0 || k == sizeZ - 1) ? sizeX + sizeZ + i : k == 0 ? sizeX + sizeZ + sizeX + sizeZ + i - 3 : k == sizeZ - 1 ? sizeX + sizeZ + i - 1 : k > 0 && i < sizeX / 2 ? sizeX + sizeZ + sizeX + sizeZ - 3 - k : sizeX - 1 + k;
                        int y = j == sizeY - 1 && !(i == 0 || i == sizeX - 1 || k == 0 || k == sizeZ - 1) ? sizeZ - k : sizeZ + sizeY - j - 2;

                        ModelRenderer pixel = new ModelRenderer(this, x, sizeZ + sizeY - j - 2);
                        modelHead[i + j * sizeX + k * sizeY * sizeX] = pixel;
                        pixel.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1);
                    }
                }
            }
        }

        sizeX = 8;
        sizeY = 12;
        sizeZ = 4;

        for(int i = 0; i < sizeX; i++)
        {
            for(int j = 0; j < sizeY; j++)
            {
                for(int k = 0; k < sizeZ; k++)
                {
                    if(i == 0 || i == sizeX - 1 || j == 0 || j == sizeY - 1 || k == 0 || k == sizeZ - 1)
                    {
                        int x = j == sizeY - 1 && !(i == 0 || i == sizeX - 1 || k == 0 || k == sizeZ - 1) ? sizeX + sizeZ + i : k == 0 ? sizeX + sizeZ + sizeX + sizeZ + i - 3 : k == sizeZ - 1 ? sizeX + sizeZ + i - 1 : k > 0 && i < sizeX / 2 ? sizeX + sizeZ + sizeX + sizeZ - 3 - k : sizeX - 1 + k;
                        int y = j == sizeY - 1 && !(i == 0 || i == sizeX - 1 || k == 0 || k == sizeZ - 1) ? sizeZ - k : sizeZ + sizeY - j - 2;

                        ModelRenderer pixel = new ModelRenderer(this, x, sizeZ + sizeY - j - 2);
                        modelBody[i + j * sizeX + k * sizeY * sizeX] = pixel;
                        pixel.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1);
                    }
                }
            }
        }

        sizeX = 4;
        sizeY = 12;
        sizeZ = 4;

        for(int i = 0; i < sizeX; i++)
        {
            for(int j = 0; j < sizeY; j++)
            {
                for(int k = 0; k < sizeZ; k++)
                {
                    if(i == 0 || i == sizeX - 1 || j == 0 || j == sizeY - 1 || k == 0 || k == sizeZ - 1)
                    {
                        int x = j == sizeY - 1 && !(i == 0 || i == sizeX - 1 || k == 0 || k == sizeZ - 1) ? sizeX + sizeZ + i : k == 0 ? sizeX + sizeZ + sizeX + sizeZ + i - 3 : k == sizeZ - 1 ? sizeX + sizeZ + i - 1 : k > 0 && i < sizeX / 2 ? sizeX + sizeZ + sizeX + sizeZ - 3 - k : sizeX - 1 + k;
                        int y = j == sizeY - 1 && !(i == 0 || i == sizeX - 1 || k == 0 || k == sizeZ - 1) ? sizeZ - k : sizeZ + sizeY - j - 2;

                        ModelRenderer pixel = new ModelRenderer(this, x, sizeZ + sizeY - j - 2);
                        modelLimb[i + j * sizeX + k * sizeY * sizeX] = pixel;
                        pixel.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1);
                    }
                }
            }
        }

        for(int i = 0; i < sizeX; i++)
        {
            for(int j = 0; j < sizeY; j++)
            {
                for(int k = 0; k < sizeZ; k++)
                {
                    if(i == 0 || i == sizeX - 1 || j == 0 || j == sizeY - 1 || k == 0 || k == sizeZ - 1)
                    {
                        int x = j == sizeY - 1 && !(i == 0 || i == sizeX - 1 || k == 0 || k == sizeZ - 1) ? sizeX + sizeZ + i : k == 0 ? sizeX + sizeZ + sizeX + sizeZ + i - 3 : k == sizeZ - 1 ? sizeX + sizeZ + i - 1 : k > 0 && i < sizeX / 2 ? sizeX + sizeZ + sizeX + sizeZ - 3 - k : sizeX - 1 + k;
                        int y = j == sizeY - 1 && !(i == 0 || i == sizeX - 1 || k == 0 || k == sizeZ - 1) ? sizeZ - k : sizeZ + sizeY - j - 2;

                        ModelRenderer pixel = new ModelRenderer(this, x, sizeZ + sizeY - j - 2);
                        pixel.mirror = true;
                        modelLimbMirrored[i + j * sizeX + k * sizeY * sizeX] = pixel;
                        pixel.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1);
                    }
                }
            }
        }
    }

    public void renderPlayer(EntityLatchedRenderer trail, long time, int seedBase, ArrayList<EntityTrackerRegistry.EntityInfo> loc, double pX, double pY, double pZ, float f5, float renderTick, int[] skins)
    {
        GlStateManager.pushMatrix();

        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.003921569F);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GlStateManager.disableCull();
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);

        //last one = newest. first = oldest
        for(int j = 0; j < Math.min(loc.size(), 100); j++)
        {
            EntityTrackerRegistry.EntityInfo info = loc.get(j);

            if(info.lastTick % 2 == 0)
            {
                continue;
            }

            boolean canRender = false;

            PatronTracker info1 = info.getTracker(PatronTracker.class);
            if(info1 != null)
            {
                canRender = info1.canRender;
            }

            if(!canRender)
            {
                continue;
            }

            int prog = j;
            float prog2 = MathHelper.clamp_float((prog + renderTick) / 100F, 0.0F, 1.0F);

            GlStateManager.pushMatrix();
            double tX = trail.prevPosX + (trail.posX - trail.prevPosX) * renderTick;
            double tY = trail.prevPosY + (trail.posY - trail.prevPosY) * renderTick;
            double tZ = trail.prevPosZ + (trail.posZ - trail.prevPosZ) * renderTick;
            GlStateManager.translate(info.posX - tX + pX, info.posY - tY + pY, info.posZ - tZ + pZ);

            GlStateManager.scale(-1.0F, -1.0F, 1.0F);

            float scale = 0.9375F;
            GlStateManager.scale(scale, scale, scale);
            GlStateManager.translate(0.0F, -0.6825F, 0.0F);

            GlStateManager.rotate(180F, 0.0F, 1.0F, 0.0F);

            rand.setSeed(info.lastTick + seedBase);

            int i = rand.nextInt(6);

            if(skins == null)
            {
                GlStateManager.disableTexture2D();
                GlStateManager.color(0.7F, 0.7F, 0.7F, 0.8F);
            }
            else
            {
                GlStateManager.color(1.0F, 1.0F, 1.0F, 0.9F - 0.9F * (MathHelper.clamp_float((prog + renderTick) / 100F, 0.0F, 1.0F)));
            }

            if(skins != null)
            {
                if(i < 2)
                {
                    GlStateManager.bindTexture(skins[0]);
                }
                else if(i < 4)
                {
                    GlStateManager.bindTexture(skins[1]);
                }
                else
                {
                    GlStateManager.bindTexture(skins[i - 2]);
                }
            }
            renderLimb(prog, i, info.sneaking, info.renderYawOffset, info.rotationYawHead, info.rotationPitch, info.limbSwing, info.limbSwingAmount, info1.yawChange * prog * (1.0F - 0.4F * prog2), info1.pitchChange * prog * (1.0F - 0.4F * prog2), f5, renderTick);
            GlStateManager.popMatrix();
        }

        GlStateManager.disableBlend();

        if(skins == null)
        {
            GlStateManager.enableTexture2D();
        }

        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);

        GlStateManager.popMatrix();
    }

    public void renderLimb(int progInt, int limb, boolean sneaking, float renderYaw, float rotationYaw, float rotationPitch, float limbSwing, float limbSwingAmount, float yaw, float pitch, float f5, float renderTick)
    {
        GlStateManager.pushMatrix();
        //0 = left leg
        //1 = right leg
        //2 = left arm
        //3 = right arm
        //4 = body
        //5 = head

        float prog = MathHelper.clamp_float((progInt + renderTick) / 100F, 0.0F, 1.0F);

        float shatterProg = 1.0F + 0.7F * prog;
        float properShatterProg = 1.0F - MathHelper.clamp_float((float)(Math.pow(1.0F - MathHelper.clamp_float(((prog - 0.025F) / 0.2F), 0.0F, 1.0F), 2D)), 0.0F, 1.0F);

        rotationPitch *= shatterProg;

        if(limb != 5)
        {
            GlStateManager.rotate(renderYaw, 0.0F, 1.0F, 0.0F);
        }
        else
        {
            GlStateManager.rotate(rotationYaw, 0.0F, 1.0F, 0.0F);
        }


        float f8 = limbSwing - limbSwingAmount;

        int sizeX = 4;
        int sizeY = 12;
        int sizeZ = 4;

        float offsetX = 0.0F;
        float offsetY = 0.0F;
        float offsetZ = 0.0F;

        float spread = 0.65F;

        if(limb == 4)
        {
            sizeX = 8;
        }
        else if(limb == 5)
        {
            sizeX = 8;
            sizeY = 8;
            sizeZ = 8;
        }

        int i = 0, j = 0, k = 0;
        while(!(i == -(sizeX / 2) || i == (sizeX / 2) - 1 || j == -sizeY || j == -1 || k == -(sizeZ / 2) || k == (sizeZ / 2) - 1))
        {
            i = rand.nextInt(sizeX) - (sizeX / 2);
            j = rand.nextInt(sizeY) - sizeY;
            k = rand.nextInt(sizeZ) - (sizeZ / 2);
        }
        GlStateManager.pushMatrix();

        ModelRenderer[] list;
        if(limb < 4)
        {
            list = (limb % 2 == 0) ? modelLimbMirrored : modelLimb;
        }
        else if(limb == 4)
        {
            list = modelBody;
        }
        else
        {
            list = modelHead;
        }

        ModelRenderer pixel = list[((sizeX / 2) + i) + ((sizeY + j) * sizeX) + ((((sizeZ / 2) + k) * (sizeX * sizeY)))];
        //i + j * 8 + k * 64

        pixel.rotationPointX = 0;
        pixel.rotationPointY = 0;
        pixel.rotationPointZ = 0;

        if(limb == 0)
        {
            GlStateManager.translate(2F / 16F + ((rand.nextFloat() - 0.5F) * spread * properShatterProg), 0.0F, 0.0F);
        }
        else if(limb == 1)
        {
            GlStateManager.translate(-2F / 16F + ((rand.nextFloat() - 0.5F) * spread * properShatterProg), 0.0F, 0.0F);
        }
        else if(limb == 2)
        {
            GlStateManager.translate(4F / 16F + ((rand.nextFloat() - 0.5F) * spread * properShatterProg), -10F / 16F * shatterProg, 0.0F);
            pixel.rotationPointX = 2F;
            pixel.rotationPointY = -2F * shatterProg;
        }
        else if(limb == 3)
        {
            GlStateManager.translate(-4F / 16F + ((rand.nextFloat() - 0.5F) * spread * properShatterProg), -10F / 16F * shatterProg, 0.0F);
            pixel.rotationPointX = -2F;
            pixel.rotationPointY = -2F * shatterProg;
        }
        else if(limb == 4)
        {
            GlStateManager.translate(0.0F, -12F / 16F * shatterProg, 0.0F);
        }
        else if(limb == 5)
        {
            GlStateManager.translate(0.0F, sneaking ? -9F / 16F : -12F / 16F * shatterProg, 0.0F);

            GlStateManager.rotate(rotationPitch, 1.0F, 0.0F, 0.0F);
            pixel.rotationPointY = -8F * shatterProg;
        }

        if(limb < 4)
        {
            if(limb == 0 || limb == 3)
            {
                GlStateManager.rotate((float)(Math.toDegrees(Math.cos(f8 * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount)) * shatterProg, 1.0F, 0.0F, 0.0F);
            }
            if(limb == 1 || limb == 2)
            {
                GlStateManager.rotate((float)(Math.toDegrees(Math.cos(f8 * 0.6662F) * 1.4F * limbSwingAmount)) * shatterProg, 1.0F, 0.0F, 0.0F);
            }
        }

        GlStateManager.translate(0.0F, 11F / 16F * (1.0F - shatterProg) + rand.nextFloat() * 0.01F, 0.0F);

        GlStateManager.translate(-(offsetX + (i + 0.5F)) / 16F + ((rand.nextFloat() - 0.5F) * spread * properShatterProg), (-(offsetY + (j + 0.5F)) / 16F) * shatterProg, -(offsetZ + (k + 0.5F)) / 16F + ((rand.nextFloat() - 0.5F) * spread * properShatterProg));
        pixel.rotateAngleX = pitch / (180F / (float)Math.PI);
        pixel.rotateAngleY = yaw / (180F / (float)Math.PI);

        double vScale = Math.pow((1.0D - prog), 0.75D);
        GlStateManager.scale(vScale, vScale, vScale);
        pixel.render(f5);

        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
    }
}

package me.ichun.mods.ichunutil.client.render.world;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;
import java.util.Random;

public class RenderGlobalProxy extends RenderGlobal
{
    public boolean renderSky;
    public boolean released;

    public RenderGlobalProxy(Minecraft mcIn)
    {
        super(mcIn);
        renderSky = true;
        released = false;
    }

    public RenderGlobalProxy setRenderSky(boolean flag)
    {
        renderSky = flag;
        return this;
    }

    @Override
    public void updateClouds()
    {
        if(renderSky)
        {
            super.updateClouds();
        }
    }

    @Override
    public void renderSky(float partialTicks, int pass)
    {
        if(renderSky)
        {
            super.renderSky(partialTicks, pass);
        }
    }

    @Override
    public void renderClouds(float partialTicks, int pass, double p_180447_3_, double p_180447_5_, double p_180447_7_)
    {
        if(renderSky)
        {
            super.renderClouds(partialTicks, pass, p_180447_3_, p_180447_5_, p_180447_7_);
        }
    }

    @Override
    public boolean hasCloudFog(double x, double y, double z, float partialTicks)
    {
        return renderSky && super.hasCloudFog(x, y, z, partialTicks);
    }

    @Override
    public void playRecord(@Nullable SoundEvent soundIn, BlockPos pos) {}

    @Override
    public void playSoundToAllNearExcept(@Nullable EntityPlayer player, SoundEvent soundIn, SoundCategory category, double x, double y, double z, float volume, float pitch) {}

    @Override
    public void broadcastSound(int soundID, BlockPos pos, int data) {}

    @Override
    public void playEvent(EntityPlayer player, int type, BlockPos blockPosIn, int data) //Remove every case that plays sound instead
    {
        Random random = this.world.rand;

        switch(type)
        {
            case 2000:
                int i1 = data % 3 - 1;
                int i = data / 3 % 3 - 1;
                double d8 = (double)blockPosIn.getX() + (double)i1 * 0.6D + 0.5D;
                double d10 = (double)blockPosIn.getY() + 0.5D;
                double d12 = (double)blockPosIn.getZ() + (double)i * 0.6D + 0.5D;

                for(int k1 = 0; k1 < 10; ++k1)
                {
                    double d13 = random.nextDouble() * 0.2D + 0.01D;
                    double d14 = d8 + (double)i1 * 0.01D + (random.nextDouble() - 0.5D) * (double)i * 0.5D;
                    double d17 = d10 + (random.nextDouble() - 0.5D) * 0.5D;
                    double d20 = d12 + (double)i * 0.01D + (random.nextDouble() - 0.5D) * (double)i1 * 0.5D;
                    double d23 = (double)i1 * d13 + random.nextGaussian() * 0.01D;
                    double d25 = -0.03D + random.nextGaussian() * 0.01D;
                    double d27 = (double)i * d13 + random.nextGaussian() * 0.01D;
                    this.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d14, d17, d20, d23, d25, d27, new int[0]);
                }

                return;
            case 2001:
                Block block = Block.getBlockById(data & 4095);
                this.mc.effectRenderer.addBlockDestroyEffects(blockPosIn, block.getStateFromMeta(data >> 12 & 255));
                break;
            case 2002:
            case 2007:
                double d6 = (double)blockPosIn.getX();
                double d7 = (double)blockPosIn.getY();
                double d9 = (double)blockPosIn.getZ();

                for (int iterations = 0; iterations < 8; ++iterations)
                {
                    this.spawnParticle(EnumParticleTypes.ITEM_CRACK, d6, d7, d9, random.nextGaussian() * 0.15D, random.nextDouble() * 0.2D, random.nextGaussian() * 0.15D, new int[] {Item.getIdFromItem(Items.SPLASH_POTION)});
                }

                float f5 = (float)(data >> 16 & 255) / 255.0F;
                float f = (float)(data >> 8 & 255) / 255.0F;
                float f1 = (float)(data >> 0 & 255) / 255.0F;
                EnumParticleTypes enumparticletypes = type == 2007 ? EnumParticleTypes.SPELL_INSTANT : EnumParticleTypes.SPELL;

                for (int l1 = 0; l1 < 100; ++l1)
                {
                    double d15 = random.nextDouble() * 4.0D;
                    double d18 = random.nextDouble() * Math.PI * 2.0D;
                    double d21 = Math.cos(d18) * d15;
                    double d23 = 0.01D + random.nextDouble() * 0.5D;
                    double d25 = Math.sin(d18) * d15;
                    Particle particle1 = this.spawnEntityFX(enumparticletypes.getParticleID(), enumparticletypes.getShouldIgnoreRange(), d6 + d21 * 0.1D, d7 + 0.3D, d9 + d25 * 0.1D, d21, d23, d25, new int[0]);

                    if (particle1 != null)
                    {
                        float f4 = 0.75F + random.nextFloat() * 0.25F;
                        particle1.setRBGColorF(f5 * f4, f * f4, f1 * f4);
                        particle1.multiplyVelocity((float)d15);
                    }
                }

                break;
            case 2003:
                double d0 = (double)blockPosIn.getX() + 0.5D;
                double d1 = (double)blockPosIn.getY();
                double d2 = (double)blockPosIn.getZ() + 0.5D;

                for(int j = 0; j < 8; ++j)
                {
                    this.spawnParticle(EnumParticleTypes.ITEM_CRACK, d0, d1, d2, random.nextGaussian() * 0.15D, random.nextDouble() * 0.2D, random.nextGaussian() * 0.15D, new int[] { Item.getIdFromItem(Items.ENDER_EYE) });
                }

                for(double d11 = 0.0D; d11 < (Math.PI * 2D); d11 += 0.15707963267948966D)
                {
                    this.spawnParticle(EnumParticleTypes.PORTAL, d0 + Math.cos(d11) * 5.0D, d1 - 0.4D, d2 + Math.sin(d11) * 5.0D, Math.cos(d11) * -5.0D, 0.0D, Math.sin(d11) * -5.0D, new int[0]);
                    this.spawnParticle(EnumParticleTypes.PORTAL, d0 + Math.cos(d11) * 5.0D, d1 - 0.4D, d2 + Math.sin(d11) * 5.0D, Math.cos(d11) * -7.0D, 0.0D, Math.sin(d11) * -7.0D, new int[0]);
                }

                return;
            case 2004:

                for(int l1 = 0; l1 < 20; ++l1)
                {
                    double d15 = (double)blockPosIn.getX() + 0.5D + ((double)this.world.rand.nextFloat() - 0.5D) * 2.0D;
                    double d18 = (double)blockPosIn.getY() + 0.5D + ((double)this.world.rand.nextFloat() - 0.5D) * 2.0D;
                    double d21 = (double)blockPosIn.getZ() + 0.5D + ((double)this.world.rand.nextFloat() - 0.5D) * 2.0D;
                    this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d15, d18, d21, 0.0D, 0.0D, 0.0D, new int[0]);
                    this.world.spawnParticle(EnumParticleTypes.FLAME, d15, d18, d21, 0.0D, 0.0D, 0.0D, new int[0]);
                }

                return;
            case 2005:
                ItemDye.spawnBonemealParticles(this.world, blockPosIn, data);
                break;
            case 2006:

                for(int l = 0; l < 200; ++l)
                {
                    float f3 = random.nextFloat() * 4.0F;
                    float f4 = random.nextFloat() * ((float)Math.PI * 2F);
                    double d3 = (double)(MathHelper.cos(f4) * f3);
                    double d4 = 0.01D + random.nextDouble() * 0.5D;
                    double d5 = (double)(MathHelper.sin(f4) * f3);
                    Particle particle = this.spawnEntityFX(EnumParticleTypes.DRAGON_BREATH.getParticleID(), false, (double)blockPosIn.getX() + d3 * 0.1D, (double)blockPosIn.getY() + 0.3D, (double)blockPosIn.getZ() + d5 * 0.1D, d3, d4, d5, new int[0]);

                    if(particle != null)
                    {
                        particle.multiplyVelocity(f3);
                    }
                }
                break;
            case 3000:
                this.world.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, true, (double)blockPosIn.getX() + 0.5D, (double)blockPosIn.getY() + 0.5D, (double)blockPosIn.getZ() + 0.5D, 0.0D, 0.0D, 0.0D, new int[0]);
                break;
            case 3001:
        }
    }
}
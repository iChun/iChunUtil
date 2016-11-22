package me.ichun.mods.ichunutil.client.render.world;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.chunk.*;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderLinkHelper;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemRecord;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.*;

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
    public void renderClouds(float partialTicks, int pass)
    {
        if(renderSky)
        {
            super.renderClouds(partialTicks, pass);
        }
    }

    @Override
    public boolean hasCloudFog(double x, double y, double z, float partialTicks)
    {
        return renderSky && super.hasCloudFog(x, y, z, partialTicks);
    }

    @Override
    public void playRecord(@Nullable SoundEvent soundIn, BlockPos pos){}

    @Override
    public void playSoundToAllNearExcept(@Nullable EntityPlayer player, SoundEvent soundIn, SoundCategory category, double x, double y, double z, float volume, float pitch){}

    @Override
    public void broadcastSound(int soundID, BlockPos pos, int data){}

    @Override
    public void playEvent(EntityPlayer player, int type, BlockPos blockPosIn, int data) //Remove every case that plays sound instead
    {
        Random random = this.theWorld.rand;

        switch (type)
        {
            case 2000:
                int i1 = data % 3 - 1;
                int i = data / 3 % 3 - 1;
                double d8 = (double)blockPosIn.getX() + (double)i1 * 0.6D + 0.5D;
                double d10 = (double)blockPosIn.getY() + 0.5D;
                double d12 = (double)blockPosIn.getZ() + (double)i * 0.6D + 0.5D;

                for (int k1 = 0; k1 < 10; ++k1)
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
                double d6 = (double)blockPosIn.getX();
                double d7 = (double)blockPosIn.getY();
                double d9 = (double)blockPosIn.getZ();

                for (int j1 = 0; j1 < 8; ++j1)
                {
                    this.spawnParticle(EnumParticleTypes.ITEM_CRACK, d6, d7, d9, random.nextGaussian() * 0.15D, random.nextDouble() * 0.2D, random.nextGaussian() * 0.15D, new int[] {Item.getIdFromItem(Items.SPLASH_POTION)});
                }

                PotionType potiontype = PotionType.getPotionTypeForID(data);
                int k = PotionUtils.getPotionColor(potiontype);
                float f = (float)(k >> 16 & 255) / 255.0F;
                float f1 = (float)(k >> 8 & 255) / 255.0F;
                float f2 = (float)(k >> 0 & 255) / 255.0F;
                EnumParticleTypes enumparticletypes = potiontype.hasInstantEffect() ? EnumParticleTypes.SPELL_INSTANT : EnumParticleTypes.SPELL;

                for (int i2 = 0; i2 < 100; ++i2)
                {
                    double d16 = random.nextDouble() * 4.0D;
                    double d19 = random.nextDouble() * Math.PI * 2.0D;
                    double d22 = Math.cos(d19) * d16;
                    double d24 = 0.01D + random.nextDouble() * 0.5D;
                    double d26 = Math.sin(d19) * d16;
                    Particle particle1 = this.spawnEntityFX(enumparticletypes.getParticleID(), enumparticletypes.getShouldIgnoreRange(), d6 + d22 * 0.1D, d7 + 0.3D, d9 + d26 * 0.1D, d22, d24, d26, new int[0]);

                    if (particle1 != null)
                    {
                        float f5 = 0.75F + random.nextFloat() * 0.25F;
                        particle1.setRBGColorF(f * f5, f1 * f5, f2 * f5);
                        particle1.multiplyVelocity((float)d16);
                    }
                }
                break;
            case 2003:
                double d0 = (double)blockPosIn.getX() + 0.5D;
                double d1 = (double)blockPosIn.getY();
                double d2 = (double)blockPosIn.getZ() + 0.5D;

                for (int j = 0; j < 8; ++j)
                {
                    this.spawnParticle(EnumParticleTypes.ITEM_CRACK, d0, d1, d2, random.nextGaussian() * 0.15D, random.nextDouble() * 0.2D, random.nextGaussian() * 0.15D, new int[] {Item.getIdFromItem(Items.ENDER_EYE)});
                }

                for (double d11 = 0.0D; d11 < (Math.PI * 2D); d11 += 0.15707963267948966D)
                {
                    this.spawnParticle(EnumParticleTypes.PORTAL, d0 + Math.cos(d11) * 5.0D, d1 - 0.4D, d2 + Math.sin(d11) * 5.0D, Math.cos(d11) * -5.0D, 0.0D, Math.sin(d11) * -5.0D, new int[0]);
                    this.spawnParticle(EnumParticleTypes.PORTAL, d0 + Math.cos(d11) * 5.0D, d1 - 0.4D, d2 + Math.sin(d11) * 5.0D, Math.cos(d11) * -7.0D, 0.0D, Math.sin(d11) * -7.0D, new int[0]);
                }

                return;
            case 2004:

                for (int l1 = 0; l1 < 20; ++l1)
                {
                    double d15 = (double)blockPosIn.getX() + 0.5D + ((double)this.theWorld.rand.nextFloat() - 0.5D) * 2.0D;
                    double d18 = (double)blockPosIn.getY() + 0.5D + ((double)this.theWorld.rand.nextFloat() - 0.5D) * 2.0D;
                    double d21 = (double)blockPosIn.getZ() + 0.5D + ((double)this.theWorld.rand.nextFloat() - 0.5D) * 2.0D;
                    this.theWorld.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d15, d18, d21, 0.0D, 0.0D, 0.0D, new int[0]);
                    this.theWorld.spawnParticle(EnumParticleTypes.FLAME, d15, d18, d21, 0.0D, 0.0D, 0.0D, new int[0]);
                }

                return;
            case 2005:
                ItemDye.spawnBonemealParticles(this.theWorld, blockPosIn, data);
                break;
            case 2006:

                for (int l = 0; l < 200; ++l)
                {
                    float f3 = random.nextFloat() * 4.0F;
                    float f4 = random.nextFloat() * ((float)Math.PI * 2F);
                    double d3 = (double)(MathHelper.cos(f4) * f3);
                    double d4 = 0.01D + random.nextDouble() * 0.5D;
                    double d5 = (double)(MathHelper.sin(f4) * f3);
                    Particle particle = this.spawnEntityFX(EnumParticleTypes.DRAGON_BREATH.getParticleID(), false, (double)blockPosIn.getX() + d3 * 0.1D, (double)blockPosIn.getY() + 0.3D, (double)blockPosIn.getZ() + d5 * 0.1D, d3, d4, d5, new int[0]);

                    if (particle != null)
                    {
                        particle.multiplyVelocity(f3);
                    }
                }
                break;
            case 3000:
                this.theWorld.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, true, (double)blockPosIn.getX() + 0.5D, (double)blockPosIn.getY() + 0.5D, (double)blockPosIn.getZ() + 0.5D, 0.0D, 0.0D, 0.0D, new int[0]);
                break;
            case 3001:
        }
    }
}
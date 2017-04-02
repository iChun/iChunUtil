package me.ichun.mods.ichunutil.client.module.patron;

import me.ichun.mods.ichunutil.client.core.EntityTrackerHandler;
import me.ichun.mods.ichunutil.client.entity.EntityLatchedRenderer;
import me.ichun.mods.ichunutil.client.render.entity.RenderLatchedRenderer;
import me.ichun.mods.ichunutil.common.core.tracker.EntityTrackerRegistry;
import me.ichun.mods.ichunutil.common.core.util.EntityHelper;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.module.patron.PatronInfo;
import me.ichun.mods.ichunutil.common.module.worldportals.client.render.WorldPortalRenderer;
import me.ichun.mods.morph.api.MorphApi;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerElytra;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

//This class has it's methods passed from EventHandlerClient or EntityTrackerHandler. It is NOT registered with the EVENT_BUS and shouldn't be.
public class PatronEffectRenderer
{
    public enum EnumEffect
    {
        NONE(0), VOXEL(1), PIG_SNOUT(2), GHOST_TRAILS(3), BEE(4), FIRE_TRAILS(5), ANGELIC(6);

        private final int id;

        EnumEffect(int id)
        {
            this.id = id;
        }

        public int getId()
        {
            return id;
        }

        public static EnumEffect getById(int i)
        {
            for(EnumEffect effect : EnumEffect.values())
            {
                if(effect.getId() == i)
                {
                    return effect;
                }
            }
            return NONE;
        }
    }

    public static HashSet<PatronInfo> patrons = new HashSet<>();

    public static HashMap<ResourceLocation, BufferedImage[]> patronRestitchedSkins = new HashMap<>();
    public static HashMap<ResourceLocation, int[]> patronRestitchedSkinsId = new HashMap<>();
    public static ModelVoxel patronModelVoxel = new ModelVoxel();

    public static LayerElytra layerElytra;

    public static void onPlayerRenderPre(RenderPlayerEvent.Pre event)
    {
        if(layerElytra != null) //uh oh, something went wrong.
        {
            boolean found = false;
            for(LayerRenderer renderer : event.getRenderer().layerRenderers)
            {
                if(renderer instanceof LayerElytra)
                {
                    found = true;
                    break;
                }
            }
            if(!found)
            {
                event.getRenderer().addLayer(layerElytra); //PUT IT BACK WHERE IT CAME FROM OR SO HELP ME
                layerElytra = null;
            }
        }
        PatronInfo info = getPatronInfo(event.getEntityPlayer());
        if(info != null && info.showEffect)
        {
            EnumEffect effect = EnumEffect.getById(info.effectType);
            if(effect == EnumEffect.ANGELIC) //steal the elytra renderer
            {
                for(int i = event.getRenderer().layerRenderers.size() - 1; i >= 0; i--) //Be CME safe.
                {
                    LayerRenderer renderer = event.getRenderer().layerRenderers.get(i);
                    if(renderer instanceof LayerElytra)
                    {
                        layerElytra = (LayerElytra)renderer;
                        event.getRenderer().layerRenderers.remove(i);
                        break;
                    }
                }
            }
        }
    }

    public static void onPlayerRenderPost(RenderPlayerEvent.Post event)
    {
        if(layerElytra != null) //we stole it, so now lets put it back.
        {
            event.getRenderer().addLayer(layerElytra); //PUT IT BACK WHERE IT CAME FROM OR SO HELP ME
            layerElytra = null;
        }
    }

    public static void onLatchedRendererRender(RenderLatchedRenderer.RenderLatchedRendererEvent event)
    {
        if(event.ent.latchedEnt instanceof AbstractClientPlayer)
        {
            AbstractClientPlayer parent = (AbstractClientPlayer)event.ent.latchedEnt;

            if(!(parent.getName().equals(Minecraft.getMinecraft().getRenderViewEntity().getName()) && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0 && WorldPortalRenderer.renderLevel <= 0))
            {
                GlStateManager.pushMatrix();

                EntityTrackerRegistry.Entry entry = EntityTrackerHandler.getEntityTrackerRegistry().getOrCreateEntry(parent, 100).addAdditionalTrackerInfo(PatronTracker.class);

                ArrayList<EntityTrackerRegistry.EntityInfo> loc = entry.trackedInfo;

                ResourceLocation rl = parent.getLocationSkin();

                if(loc.size() > 21)
                {
                    for(EntityTrackerRegistry.IAdditionalTrackerInfo tracker : loc.get(20).additionalInfo)
                    {
                        if(tracker instanceof PatronTracker && ((PatronTracker)tracker).txLocation != null)
                        {
                            rl = ((PatronTracker)tracker).txLocation;
                        }
                    }
                }

                PatronInfo info = getPatronInfo(parent);
                if(info != null && info.showEffect)
                {
                    event.ent.setIgnoreFrustumCheck();
                    EnumEffect effect = EnumEffect.getById(info.effectType);
                    switch(effect)
                    {
                        case VOXEL:
                        {
                            BufferedImage[] skins = patronRestitchedSkins.get(rl);

                            if(skins == null)
                            {
                                ITextureObject obj = Minecraft.getMinecraft().getTextureManager().getTexture(rl);
                                if(obj instanceof ThreadDownloadImageData)
                                {
                                    try
                                    {
                                        BufferedImage img = ((ThreadDownloadImageData)obj).bufferedImage;
                                        if(img != null)
                                        {
                                            int[] imgId = new int[4];
                                            skins = new BufferedImage[4];

                                            int[] dimsX = new int[] { 4, 4, 8, 8 };
                                            int[] dimsZ = new int[] { 4, 4, 4, 8 };
                                            int[] dimsY = new int[] { 12, 12, 12, 8 };

                                            int[] startX = new int[] { 0, 40, 16, 0 };
                                            int[] startY = new int[] { 16, 16, 16, 0 };

                                            for(int j = 0; j < dimsX.length; j++)
                                            {
                                                int[] dim = new int[] { dimsX[j], dimsY[j], dimsZ[j] };
                                                int[] rots = new int[] { -90, 180, 0, 0, 90, 0, -90, 180, 90 };
                                                BufferedImage tmp = new BufferedImage(48, 24, 1);

                                                Graphics2D gfx = tmp.createGraphics();

                                                int[] xSource = new int[] { dim[2], dim[2], dim[2] + dim[0] + dim[2], 0, dim[2] + dim[0], dim[2] + dim[0], dim[2] + dim[0], dim[2] + dim[0], dim[2] };
                                                int[] ySource = new int[] { 0, 0, dim[2], dim[2], 0, 0, 0, 0, 0 };

                                                int[] xCoord = new int[] { dim[0], dim[0] + dim[2] + dim[0] + dim[2], 0, dim[0] + dim[2] + dim[0] + dim[2] + dim[0], dim[0], dim[0] + dim[2], dim[0] + dim[2] + dim[0], dim[0] + dim[2] + dim[0] + dim[2], dim[2] + dim[0] + dim[2] };
                                                int[] yCoord = new int[] { 0, 0, dim[2], dim[2], dim[2] + dim[1], dim[2] + dim[1], dim[2] + dim[1], dim[2] + dim[1], 0 };

                                                int[] dimX = new int[] { dim[0], dim[0], dim[0], dim[2], dim[0], dim[0], dim[0], dim[0], dim[0] };
                                                int[] dimY = new int[] { dim[2], dim[2], dim[1], dim[1], dim[2], dim[2], dim[2], dim[2], dim[2] };

                                                for(int i = 0; i < rots.length; i++)
                                                {
                                                    if(i == rots.length - 1)
                                                    {
                                                        gfx.drawImage(img, dim[0], 0, dim[0] + dim[2] + dim[0] + dim[2] + dim[0], dim[2] + dim[1], startX[j], startY[j], startX[j] + (2 * dim[0] + 2 * dim[2]), startY[j] + dim[1] + dim[2], null);
                                                    }

                                                    BufferedImage temp = img.getSubimage(startX[j] + xSource[i], startY[j] + ySource[i], dimX[i], dimY[i]); //new BufferedImage(img.getWidth(), img.getHeight(), 1);

                                                    BufferedImage temp1 = new BufferedImage(dimX[i], dimY[i], 1);

                                                    Graphics2D gfx1 = temp1.createGraphics();
                                                    gfx1.rotate(Math.toRadians(rots[i]), dimX[i] / 2, dimY[i] / 2);
                                                    gfx1.drawImage(temp, 0, 0, dimX[i], dimY[i], 0, 0, dimX[i], dimY[i], null);
                                                    gfx1.dispose();

                                                    gfx.drawImage(temp1, xCoord[i], yCoord[i], xCoord[i] + dimX[i], yCoord[i] + dimY[i], 0, 0, dimX[i], dimY[i], null);
                                                }

                                                imgId[j] = TextureUtil.uploadTextureImage(TextureUtil.glGenTextures(), tmp);
                                                skins[j] = tmp;
                                            }

                                            patronRestitchedSkinsId.put(rl, imgId);
                                            patronRestitchedSkins.put(rl, skins);
                                        }
                                    }
                                    catch(Exception e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            //hacky fix for elytra flying.
                            if(parent.getTicksElytraFlying() > 4)
                            {
                                GlStateManager.translate(0F, -0.8F, 0F);
                            }

                            patronModelVoxel.renderPlayer(event.ent, 0, parent.hashCode(), loc, event.x, event.y, event.z, 0.0625F, event.partialTick, patronRestitchedSkinsId.get(rl));

                            break;
                        }
                        case GHOST_TRAILS:
                        {
                            if(loc.size() > 6)
                            {
                                Render render = Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(parent);
                                RenderPlayer renderPlayer = (RenderPlayer)render;
                                ModelBase biped = renderPlayer.getMainModel();

                                int ii = parent.getBrightnessForRender(event.partialTick);
                                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)(ii % 65536) / 1.0F, (float)(ii / 65536) / 1.0F);

                                GlStateManager.enableBlend();
                                GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

                                for(int i = 1; i < 6; i++)
                                {
                                    EntityTrackerRegistry.EntityInfo entInfo = loc.get(i);

                                    for(EntityTrackerRegistry.IAdditionalTrackerInfo tracker : entInfo.additionalInfo)
                                    {
                                        if(tracker instanceof PatronTracker && ((PatronTracker)tracker).txLocation != null)
                                        {
                                            rl = ((PatronTracker)tracker).txLocation;
                                        }
                                    }

                                    GlStateManager.pushMatrix();

                                    double tX = event.ent.prevPosX + (event.ent.posX - event.ent.prevPosX) * event.partialTick;
                                    double tY = event.ent.prevPosY + (event.ent.posY - event.ent.prevPosY) * event.partialTick;
                                    double tZ = event.ent.prevPosZ + (event.ent.posZ - event.ent.prevPosZ) * event.partialTick;
                                    GlStateManager.translate(entInfo.posX - tX + event.x, entInfo.posY - tY + event.y, entInfo.posZ - tZ + event.z);

                                    GlStateManager.rotate(entInfo.renderYawOffset, 0.0F, -1.0F, 0.0F);

                                    //elytra rotation
                                    if(parent.isElytraFlying())
                                    {
                                        float f = (float)parent.getTicksElytraFlying() + event.partialTick;
                                        float f1 = MathHelper.clamp_float(f * f / 100.0F, 0.0F, 1.0F);
                                        GlStateManager.rotate(f1 * (-90.0F - parent.rotationPitch), -1.0F, 0.0F, 0.0F);
                                        Vec3d vec3d = parent.getLook(event.partialTick);
                                        double d0 = parent.motionX * parent.motionX + parent.motionZ * parent.motionZ;
                                        double d1 = vec3d.xCoord * vec3d.xCoord + vec3d.zCoord * vec3d.zCoord;

                                        if(d0 > 0.0D && d1 > 0.0D)
                                        {
                                            double d2 = (parent.motionX * vec3d.xCoord + parent.motionZ * vec3d.zCoord) / (Math.sqrt(d0) * Math.sqrt(d1));
                                            double d3 = parent.motionX * vec3d.zCoord - parent.motionZ * vec3d.xCoord;
                                            GlStateManager.rotate((float)(Math.signum(d3) * Math.acos(d2)) * 180.0F / (float)Math.PI, 0.0F, 1.0F, 0.0F);
                                        }
                                    }
                                    //end elytra rotation

                                    float scalee = 0.9375F;
                                    GlStateManager.scale(scalee, -scalee, -scalee);

                                    GlStateManager.translate(0.0F, -1.5F, 0.0F);

                                    float alpha = 1.0F - MathHelper.clamp_float(((i - 1) + event.partialTick) / 5F, 0.0F, 1.0F);//1.0F - MathHelper.clamp_float(((float)(loc.size() - 2 - i) + partialTick) / (float)((loc.size() - 2) > 5 ? 5 : loc.size() - 2), 0.0F, 1.0F);

                                    GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);

                                    Minecraft.getMinecraft().getTextureManager().bindTexture(rl);
                                    float f2 = entInfo.renderYawOffset;
                                    float f3 = entInfo.rotationYawHead;

                                    float f7 = entInfo.limbSwingAmount;

                                    float f8 = entInfo.limbSwing - entInfo.limbSwingAmount;

                                    if(f7 > 1.0F)
                                    {
                                        f7 = 1.0F;
                                    }

                                    float f4 = (float)parent.ticksExisted - i + event.partialTick;

                                    float f5 = entInfo.rotationPitch;

                                    biped.render(parent, f8, f7, f4, f3 - f2, f5, 0.0625F);

                                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

                                    GlStateManager.popMatrix();
                                }

                                GlStateManager.disableBlend();
                            }
                            break;
                        }
                        case ANGELIC:
                        {
                            event.ent.setDeathPersistTime(110);

                            Render render = Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(parent);
                            RenderPlayer renderPlayer = (RenderPlayer)render;
                            ModelPlayer biped = renderPlayer.getMainModel();

                            GlStateManager.pushMatrix();

                            GlStateManager.translate(event.x, event.y, event.z);

                            float scale = 0.9375F;
                            GlStateManager.scale(scale, scale, scale);

                            float elyX = 0.2617994F;
                            float elyZ = -0.2617994F;

                            if(loc.size() > 1)
                            {
                                float newElyX = loc.get(0).getTracker(PatronTracker.class).elytraX;
                                float newElyZ = loc.get(0).getTracker(PatronTracker.class).elytraZ;
                                float oldElyX = loc.get(1).getTracker(PatronTracker.class).elytraX;
                                float oldElyZ = loc.get(1).getTracker(PatronTracker.class).elytraZ;

                                elyX = EntityHelper.interpolateValues(oldElyX, newElyX, event.partialTick) - 0.2617994F;
                                elyZ = EntityHelper.interpolateValues(oldElyZ, newElyZ, event.partialTick) - (-0.2617994F);
                            }
                            else if(!loc.isEmpty())
                            {
                                elyX = loc.get(0).getTracker(PatronTracker.class).elytraX - 0.2617994F;
                                elyZ = loc.get(0).getTracker(PatronTracker.class).elytraZ - (-0.2617994F);
                            }

                            float prog = MathHelper.clamp_float((elyX / (0.34776512F - 0.2617994F)) * 0.5F + (elyZ / -(1.5512857F - 0.2617994F)) * 0.5F, 0F, 1F);

                            EntityTrackerHandler.renderAngelicModel(parent, event.ent, biped, rl, prog, event.partialTick);

                            GlStateManager.popMatrix();
                            break;
                        }
                    }
                }
                GlStateManager.popMatrix();
            }
        }
    }

    public static void onLatchedRendererUpdate(EntityLatchedRenderer.EntityLatchedRendererUpdateEvent event)
    {
        if(event.ent.latchedEnt instanceof EntityPlayer)
        {
            EntityPlayer parent = (EntityPlayer)event.ent.latchedEnt;
            PatronInfo info = getPatronInfo(parent);
            if(info != null && info.showEffect && info.effectType == EnumEffect.FIRE_TRAILS.getId())
            {
                double moX = parent.posX - parent.prevPosX;
                double moZ = parent.posZ - parent.prevPosZ;
                if(Math.sqrt(moX * moX + moZ * moZ) > 0.11D)
                {
                    int i = MathHelper.floor_double(parent.posX);
                    int j = MathHelper.floor_double(parent.posY - 0.20000000298023224D);
                    int k = MathHelper.floor_double(parent.posZ);
                    BlockPos blockpos = new BlockPos(i, j, k);
                    IBlockState iblockstate = event.ent.worldObj.getBlockState(blockpos);
                    if(iblockstate.getRenderType() != EnumBlockRenderType.INVISIBLE)
                    {
                        if(parent.isSprinting())
                        {
                            for(int kk = 0; kk < 2; kk++)
                            {
                                event.ent.worldObj.spawnParticle(EnumParticleTypes.BLOCK_DUST, parent.posX + ((double)parent.getRNG().nextFloat() - 0.5D) * (double)parent.width, parent.getEntityBoundingBox().minY + 0.1D, parent.posZ + ((double)parent.getRNG().nextFloat() - 0.5D) * (double)parent.width, -moX * 0.8D, 0.2D + 0.3D * parent.getRNG().nextDouble(), -moZ * 0.8D, Block.getStateId(iblockstate));
                            }
                        }
                        for(int kk = 0; kk < 3; kk++)
                        {
                            double d0 = event.ent.worldObj.rand.nextGaussian() * 0.1D;
                            double d2 = event.ent.worldObj.rand.nextGaussian() * 0.1D;
                            event.ent.worldObj.spawnParticle(EnumParticleTypes.FLAME, parent.posX + d0, parent.posY + 0.1F, parent.posZ + d2, 0D, parent.isSprinting() ? parent.getRNG().nextFloat() * 0.05D : 0.0125D, 0D);
                        }
                    }
                    if(parent.isElytraFlying())
                    {
                        for(int kk = 0; kk < 5; kk++)
                        {
                            double d0 = event.ent.worldObj.rand.nextGaussian() * 0.1D;
                            double d2 = event.ent.worldObj.rand.nextGaussian() * 0.1D;
                            event.ent.worldObj.spawnParticle(EnumParticleTypes.FLAME, parent.posX + d0 - parent.motionX * 1.5D, parent.posY + 0.1F - parent.motionY * 1.5D, parent.posZ + d2 - parent.motionZ * 1.5D, 0D, parent.isSprinting() ? parent.getRNG().nextFloat() * 0.05D : 0.0125D, 0D);
                        }
                    }
                }
            }
        }
    }

    public static PatronInfo getPatronInfo(EntityPlayer player)
    {
        EntityPlayer oriPlayer = player;
        if(iChunUtil.hasMorphMod())
        {
            EntityLivingBase ent = MorphApi.getApiImpl().getMorphEntity(player.worldObj, player.getName(), Side.CLIENT);
            if(ent != null) //is morphed
            {
                if(!(ent instanceof EntityPlayer) || MorphApi.getApiImpl().morphProgress(player.getName(), Side.CLIENT) < 1.0F)
                {
                    return null;
                }
                player = (EntityPlayer)ent;
            }
        }
        PatronInfo info = null;

        for(PatronInfo info1 : patrons)
        {
            if(info1.id.equals(player.getGameProfile().getId().toString().replaceAll("-", "")))
            {
                info = info1;
                break;
            }
        }
        return info;
    }

    public static void onClientDisconnect()
    {
        patrons.clear();
    }
}

package us.ichun.mods.ichunutil.client.patron;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import us.ichun.mods.ichunutil.common.core.patron.PatronInfo;
import us.ichun.mods.ichunutil.common.iChunUtil;
import us.ichun.mods.ichunutil.common.tracker.EntityInfo;
import us.ichun.mods.ichunutil.common.tracker.IAdditionalTrackerInfo;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

public class RenderPatronEffect extends Render
{
    public HashMap<ResourceLocation, BufferedImage[]> restitchedSkins = new HashMap<ResourceLocation, BufferedImage[]>();
    public HashMap<ResourceLocation, int[]> restitchedSkinsId = new HashMap<ResourceLocation, int[]>();

    public RenderPatronEffect()
    {
        super(Minecraft.getMinecraft().getRenderManager());
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return DefaultPlayerSkin.getDefaultSkinLegacy();
    }

    @Override
    public void doRender(Entity entity, double d, double d1, double d2, float f, float f1)
    {
        EntityPatronEffect sd = (EntityPatronEffect)entity;

        if(sd.parent.getCommandSenderName().equals(Minecraft.getMinecraft().getRenderViewEntity().getCommandSenderName()) && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0)
        {
            return;
        }

        GlStateManager.pushMatrix();
        //        GlStateManager.translate((float)d, (float)d1, (float)d2);

        ArrayList<EntityInfo> loc = iChunUtil.proxy.tickHandlerClient.getOrRegisterEntityTracker(sd.parent, 100, PatronTracker.class, true);

        ResourceLocation rl = sd.parent.getLocationSkin();

        if(loc.size() > 21)
        {
            for(IAdditionalTrackerInfo tracker : loc.get(20).additionalInfo)
            {
                if(tracker instanceof PatronTracker && ((PatronTracker)tracker).txLocation != null)
                {
                    rl = ((PatronTracker)tracker).txLocation;
                }
            }
        }

        PatronInfo info = null;
        for(PatronInfo info1 : iChunUtil.proxy.effectTicker.patronList)
        {
            if(info1.id.equals(sd.parent.getGameProfile().getId().toString()))
            {
                info = info1;
                break;
            }
        }

        if(info != null)
        {
            switch(info.type)
            {
                case 1:
                {
                    BufferedImage[] skins = restitchedSkins.get(rl);

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
                                        int[] rots = new int[] { -90, 180, 0, 0, 90, 0, -90, 180, 				90 };
                                        BufferedImage tmp = new BufferedImage(48, 24, 1);

                                        Graphics2D gfx = tmp.createGraphics();

                                        int[] xSource = new int[] { dim[2], dim[2], dim[2] + dim[0] + dim[2], 0, dim[2] + dim[0], dim[2] + dim[0], dim[2] + dim[0], dim[2] + dim[0], 				dim[2]};
                                        int[] ySource = new int[] { 0, 0, dim[2], dim[2], 0, 0, 0, 0,  					0 };

                                        int[] xCoord = new int[] { dim[0], dim[0] + dim[2] + dim[0] + dim[2], 0, dim[0] + dim[2] + dim[0] + dim[2] + dim[0], dim[0], dim[0] + dim[2], dim[0] + dim[2] + dim[0], dim[0] + dim[2] + dim[0] + dim[2], 				dim[2] + dim[0] + dim[2] };
                                        int[] yCoord = new int[] { 0, 0, dim[2], dim[2], dim[2] + dim[1], dim[2] + dim[1], dim[2] + dim[1], dim[2] + dim[1], 				0 };

                                        int[] dimX = new int[] { dim[0], dim[0], dim[0], dim[2], dim[0], dim[0], dim[0], dim[0], 					dim[0] };
                                        int[] dimY = new int[] { dim[2], dim[2], dim[1], dim[1], dim[2], dim[2], dim[2], dim[2], 					dim[2] };

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
                                            //    			                gfx1.drawImage(temp, 0, 0, (Math.abs(rots[i]) == 90) ? dimY[i] : dimX[i], (Math.abs(rots[i]) == 90) ? dimX[i] : dimY[i], 0, 0, (Math.abs(rots[i]) == 90) ? dimY[i] : dimX[i], (Math.abs(rots[i]) == 90) ? dimX[i] : dimY[i], (ImageObserver)null);
                                            gfx1.drawImage(temp, 0, 0, dimX[i], dimY[i], 0, 0, dimX[i], dimY[i], null);
                                            gfx1.dispose();

                                            gfx.drawImage(temp1, xCoord[i], yCoord[i], xCoord[i] + dimX[i], yCoord[i] + dimY[i], 0, 0, dimX[i], dimY[i], null);
                                        }

                                        imgId[j] = TextureUtil.uploadTextureImage(TextureUtil.glGenTextures(), tmp);
                                        skins[j] = tmp;
                                        //			                try
                                        //			                {
                                        //			                    ImageIO.write(tmp, "png", new File(Minecraft.getMinecraft().mcDataDir, "test"+j+".png"));
                                        //			                }
                                        //			                catch (IOException ioexception)
                                        //			                {
                                        //			                    ioexception.printStackTrace();
                                        //			                }
                                    }

                                    restitchedSkinsId.put(rl, imgId);
                                    restitchedSkins.put(rl, skins);
                                }
                            }
                            catch(Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }

                    sd.model.renderPlayer(sd, 0, entity.hashCode(), loc, d, d1, d2, 0.0625F, f1, restitchedSkinsId.get(rl));

                    break;
                }
                case 3:
                {
                    if(loc.size() > 6)
                    {
                        ModelBase biped = ((RenderPlayer)Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(sd.parent)).mainModel;

                        int ii = sd.parent.getBrightnessForRender(f1);
                        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)(ii % 65536) / 1.0F, (float)(ii / 65536) / 1.0F);

                        GlStateManager.enableBlend();
                        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

                        for(int i = 1; i < 6; i++)
                        {
                            EntityInfo entInfo = loc.get(i);

                            for(IAdditionalTrackerInfo tracker : entInfo.additionalInfo)
                            {
                                if(tracker instanceof PatronTracker && ((PatronTracker)tracker).txLocation != null)
                                {
                                    rl = ((PatronTracker)tracker).txLocation;
                                }
                            }

                            GlStateManager.pushMatrix();

                            double tX = sd.prevPosX + (sd.posX - sd.prevPosX) * f1;
                            double tY = sd.prevPosY + (sd.posY - sd.prevPosY) * f1;
                            double tZ = sd.prevPosZ + (sd.posZ - sd.prevPosZ) * f1;
                            GlStateManager.translate(entInfo.posX - tX + d, entInfo.posY - tY + d1, entInfo.posZ - tZ + d2);

                            GlStateManager.rotate(entInfo.renderYawOffset, 0.0F, -1.0F, 0.0F);

                            float scalee = 0.9375F;
                            GlStateManager.scale(scalee, -scalee, -scalee);

                            GlStateManager.translate(0.0F, -1.5F, 0.0F);

                            float alpha = 1.0F - MathHelper.clamp_float(((i - 1) + f1) / 5F, 0.0F, 1.0F);//1.0F - MathHelper.clamp_float(((float)(loc.size() - 2 - i) + f1) / (float)((loc.size() - 2) > 5 ? 5 : loc.size() - 2), 0.0F, 1.0F);

                            GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);

                            Minecraft.getMinecraft().getTextureManager().bindTexture(rl);
                            float f2 = entInfo.renderYawOffset;
                            float f3 = entInfo.rotationYawHead;

                            float f7 = entInfo.limbSwingAmount;

                            float f8 = entInfo.limbSwing - entInfo.limbSwingAmount;

                            if (f7 > 1.0F)
                            {
                                f7 = 1.0F;
                            }

                            float f4 = (float)sd.parent.ticksExisted - i + f1;

                            float f5 = entInfo.rotationPitch;

                            biped.render(sd.parent, f8, f7, f4, f3 - f2, f5, 0.0625F);

                            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

                            GlStateManager.popMatrix();
                        }

                        GlStateManager.disableBlend();
                    }
                    break;
                }
            }
        }
        GlStateManager.popMatrix();
    }
}

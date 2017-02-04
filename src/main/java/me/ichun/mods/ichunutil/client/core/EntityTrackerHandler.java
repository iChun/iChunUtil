package me.ichun.mods.ichunutil.client.core;

import me.ichun.mods.ichunutil.client.entity.EntityLatchedRenderer;
import me.ichun.mods.ichunutil.client.model.ModelAngelPeriphs;
import me.ichun.mods.ichunutil.client.module.patron.PatronEffectRenderer;
import me.ichun.mods.ichunutil.client.render.entity.RenderLatchedRenderer;
import me.ichun.mods.ichunutil.common.core.tracker.EntityTrackerRegistry;
import me.ichun.mods.ichunutil.common.core.util.EntityHelper;
import me.ichun.mods.ichunutil.common.core.util.EventCalendar;
import me.ichun.mods.ichunutil.common.core.util.ResourceHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

//This class has it's methods passed from EventHandlerClient. It is NOT registered with the EVENT_BUS and shouldn't be.
public class EntityTrackerHandler
{
    public static boolean angelZombies;
    public static ModelZombie modelZombie;
    public static ModelAngelPeriphs modelAngelPeriphs;
    public static ResourceLocation texModelAngel = new ResourceLocation("ichunutil", "textures/model/modelangel.png");

    protected static EntityTrackerRegistry entityTrackerRegistry = new EntityTrackerRegistry();

    protected static ArrayList<EntityLatchedRenderer> latchedRendererEntities = new ArrayList<>();

    public static void init()
    {
        angelZombies = Minecraft.getMinecraft().getSession().getUsername().equalsIgnoreCase("Kleetho") || EventCalendar.isHalloween() || EventCalendar.isChristmas() || EventCalendar.isValentinesDay();
    }

    public static void onRenderTickStart(TickEvent.RenderTickEvent event)
    {
        for(int i = latchedRendererEntities.size() - 1; i >= 0; i--) //Check the latched renderers
        {
            EntityLatchedRenderer latchedRenderer = latchedRendererEntities.get(i);
            if(latchedRenderer.latchedEnt != null && (!latchedRenderer.latchedEnt.isDead || !latchedRenderer.latchedEnt.isEntityAlive() && latchedRenderer.maxDeathPersistTime > 0 && latchedRenderer.currentDeathPersistTime < latchedRenderer.maxDeathPersistTime)) //latched ent exists and is alive and well, or is persisting post-death
            {
                if(latchedRenderer.isDead || (latchedRenderer.world.getWorldTime() - latchedRenderer.lastUpdate) > 10L)//latcher died/stopped updating, kill it replace with new one.
                {
                    latchedRenderer.setDead();
                    latchedRendererEntities.remove(i);

                    EntityLatchedRenderer newLatchedRenderer = new EntityLatchedRenderer(latchedRenderer.latchedEnt.world, latchedRenderer.latchedEnt);
                    latchedRenderer.latchedEnt.world.spawnEntity(newLatchedRenderer);
                    latchedRendererEntities.add(newLatchedRenderer);
                    newLatchedRenderer.maxDeathPersistTime = latchedRenderer.maxDeathPersistTime;
                    newLatchedRenderer.currentDeathPersistTime = latchedRenderer.currentDeathPersistTime;
                }
                else
                {
                    latchedRenderer.updatePos();
                }
            }
            else
            {
                latchedRenderer.setDead();
                latchedRendererEntities.remove(i);
            }
        }
    }

    public static void tick()
    {
        entityTrackerRegistry.tick();
    }

    public static void onClientDisconnect()
    {
        entityTrackerRegistry.trackerEntries.clear();
        latchedRendererEntities.clear();
    }

    public static void onEntitySpawn(EntityJoinWorldEvent event)
    {
        if(event.getEntity().world.isRemote && (event.getEntity() instanceof EntityPlayer || event.getEntity() instanceof EntityZombie && angelZombies))
        {
            EntityLatchedRenderer latchedRenderer = new EntityLatchedRenderer(event.getEntity().world, event.getEntity());
            event.getEntity().world.spawnEntity(latchedRenderer);
            latchedRendererEntities.add(latchedRenderer);
            if(event.getEntity() instanceof EntityZombie)
            {
                latchedRenderer.setDeathPersistTime(110);
                latchedRenderer.setRenderSize(4F, 10F);
            }
        }
    }

    public static void onLatchedRendererUpdate(EntityLatchedRenderer.EntityLatchedRendererUpdateEvent event)
    {
        PatronEffectRenderer.onLatchedRendererUpdate(event);
    }

    public static void onLatchedRendererRender(RenderLatchedRenderer.RenderLatchedRendererEvent event)
    {
        PatronEffectRenderer.onLatchedRendererRender(event);
        if(event.ent.latchedEnt instanceof EntityZombie)
        {
            if(modelZombie == null)
            {
                modelZombie = new ModelZombie();
                modelZombie.isChild = false;
            }
            EntityZombie zombie = (EntityZombie)event.ent.latchedEnt;
            GlStateManager.pushMatrix();

            GlStateManager.translate(event.x, event.y, event.z);

            renderAngelicModel(zombie, event.ent, modelZombie, ResourceHelper.texZombie, -1F, event.partialTick);

            GlStateManager.popMatrix();
        }
    }

    public static EntityTrackerRegistry getEntityTrackerRegistry()
    {
        return entityTrackerRegistry;
    }

    public static void renderAngelicModel(EntityLivingBase living, EntityLatchedRenderer latched, ModelBiped modelBiped, ResourceLocation modelTex, float progressOverride, float partialTick)
    {
        if(living.isChild())
        {
            return;
        }
        if(modelAngelPeriphs == null)
        {
            modelAngelPeriphs = new ModelAngelPeriphs();
        }

        //TODO set if sneaking.
        Minecraft mc = Minecraft.getMinecraft();

        boolean alive = living.isEntityAlive();

        float renderYaw = alive ? EntityHelper.interpolateRotation(living.prevRenderYawOffset, living.renderYawOffset, partialTick) : living.renderYawOffset;

        GlStateManager.rotate(renderYaw, 0.0F, -1.0F, 0.0F);

        //elytra rotation
        boolean elytraFlying = living.isElytraFlying();
        if(elytraFlying)
        {
            float f = (float)living.getTicksElytraFlying() + partialTick;
            float f1 = MathHelper.clamp(f * f / 100.0F, 0.0F, 1.0F);
            GlStateManager.rotate(f1 * (-90.0F - living.rotationPitch), -1.0F, 0.0F, 0.0F);
            Vec3d vec3d = living.getLook(partialTick);
            double d0 = living.motionX * living.motionX + living.motionZ * living.motionZ;
            double d1 = vec3d.xCoord * vec3d.xCoord + vec3d.zCoord * vec3d.zCoord;

            if(d0 > 0.0D && d1 > 0.0D)
            {
                double d2 = (living.motionX * vec3d.xCoord + living.motionZ * vec3d.zCoord) / (Math.sqrt(d0) * Math.sqrt(d1));
                double d3 = living.motionX * vec3d.zCoord - living.motionZ * vec3d.xCoord;
                GlStateManager.rotate((float)(Math.signum(d3) * Math.acos(d2)) * 180.0F / (float)Math.PI, 0.0F, 1.0F, 0.0F);
            }
        }
        //end elytra rotation

        GlStateManager.scale(1F, -1F, -1F);

        GlStateManager.translate(0.0F, -1.5F, 0.0F);

        if(living.isSneaking())
        {
            GlStateManager.translate(0.0F, 0.2F, 0.0F);
        }

        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.003921569F);

        GlStateManager.depthMask(true);
        GlStateManager.disableCull();

        float progress = MathHelper.clamp((latched.currentDeathPersistTime - 40 + partialTick) / 60F, 0F, 1F);
        float progressSin = alive ? 0F : (float)Math.sin(Math.toRadians(90F * progress)) ;

        float pitch = (float)Math.toRadians(alive ? EntityHelper.interpolateRotation(living.prevRotationPitch, living.rotationPitch, partialTick) : -45F * progress);
        float yaw = alive ? (float)Math.toRadians(EntityHelper.interpolateRotation(living.prevRotationYawHead, living.rotationYawHead, partialTick) - renderYaw) : 0F;

        GlStateManager.enableBlend();

        if(!alive)
        {
            GlStateManager.translate(0F, -0.5F - 1.5F * progress, 0F);

            GlStateManager.color(1F, 1F, 1F, 0.5F * (float)Math.sin(Math.toRadians(MathHelper.clamp(progress * 1.2F, 0F, 1F) * 180F)));

            mc.getTextureManager().bindTexture(modelTex);
            modelBiped.isChild = false;
            modelBiped.bipedHead.rotateAngleX = pitch;
            modelBiped.bipedHead.rotateAngleY = yaw;
            modelBiped.bipedRightArm.rotateAngleX = 0F;
            modelBiped.bipedLeftArm.rotateAngleX = 0F;
            modelBiped.bipedHead.render(0.0625F);
            modelBiped.bipedBody.render(0.0625F);
            modelBiped.bipedRightArm.render(0.0625F);
            modelBiped.bipedLeftArm.render(0.0625F);
            modelBiped.bipedRightLeg.render(0.0625F);
            modelBiped.bipedLeftLeg.render(0.0625F);
        }
        else
        {
            progressSin += (living.prevLimbSwingAmount + (living.limbSwingAmount - living.prevLimbSwingAmount) * partialTick) * 0.5F;
            if(living.isSneaking())
            {
                progressSin += 0.3F;
            }
            if(living instanceof EntityPlayer && ((EntityPlayer)living).capabilities.isFlying)
            {
                progressSin *= 2F;
            }
            if(elytraFlying)
            {
                pitch = -45F;
                progressSin = progressOverride;
            }
        }

        mc.getTextureManager().bindTexture(texModelAngel);
        modelAngelPeriphs.haloInner.rotateAngleX = modelAngelPeriphs.haloOuter.rotateAngleX = pitch;
        modelAngelPeriphs.haloInner.rotateAngleY = modelAngelPeriphs.haloOuter.rotateAngleY = yaw;
        modelAngelPeriphs.setRotations(progressSin);

        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        modelAngelPeriphs.renderHalo(0.0625F);

        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        modelAngelPeriphs.renderWing(0.0625F);
        GlStateManager.scale(-1F, 1F, 1F);
        modelAngelPeriphs.renderWing(0.0625F);

        GlStateManager.enableCull();
        GlStateManager.depthMask(false);

        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);

        GlStateManager.color(1F, 1F, 1F, 1F);
    }
}

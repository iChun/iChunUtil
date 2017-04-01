package me.ichun.mods.ichunutil.common.module.worldportals.common.portal;

import me.ichun.mods.ichunutil.common.module.worldportals.client.render.WorldPortalRenderer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayDeque;
import java.util.Deque;

public class EntityTransformationStack
{

    private static final Deque<EntityTransformation> stack = new ArrayDeque<>();
    private static Entity entity;

    public static void setEntity(Entity entity)
    {

        if(EntityTransformationStack.entity == entity)
        {
            return;
        }
        EntityTransformationStack.entity = entity;
        stack.clear();
    }

    public static void push()
    {

        stack.push(new EntityTransformationSeparator());
    }

    public static void pop()
    {

        EntityTransformation last;
        while((last = stack.poll()) != null && !(last instanceof EntityTransformationSeparator))
        {
            last.revert();
        }
    }

    public static void translate(double x, double y, double z)
    {

        EntityTransformation transformation = new EntityTransformation(x, y, z, 0, 0, 0);
        stack.push(transformation);
        transformation.apply();
    }

    public static void rotate(float yaw, float pitch, float roll)
    {

        EntityTransformation transformation = new EntityTransformation(0, 0, 0, yaw, pitch, roll);
        stack.push(transformation);
        transformation.apply();
    }

    private static class EntityTransformation
    {

        private double x, y, z;
        private float yaw, pitch, roll;

        public EntityTransformation(double x, double y, double z, float yaw, float pitch, float roll)
        {

            this.x = x;
            this.y = y;
            this.z = z;
            this.yaw = yaw;
            this.pitch = pitch;
            this.roll = roll;
        }

        public EntityTransformation()
        {

        }

        public void apply()
        {
            if(entity.worldObj.isRemote)
            {
                applyClient();
            }
            else
            {
                applyMain();
            }
        }
        //TODO try and figure out how to handle roll.

        //This fixes the hand bouncing around when you look around
        @SideOnly(Side.CLIENT)
        private void applyClient()
        {
            float prevYaw = 0.0F;
            float yaw = 0.0F;
            float prevPitch = 0.0F;
            float pitch = 0.0F;

            if(entity instanceof EntityPlayerSP)
            {
                EntityPlayerSP player = (EntityPlayerSP)entity;
                prevYaw = player.prevRotationYaw - player.prevRenderArmYaw;
                yaw = player.rotationYaw - player.renderArmYaw;
                prevPitch = player.prevRotationPitch - player.prevRenderArmPitch;
                pitch = player.rotationPitch - player.renderArmPitch;
            }

            WorldPortalRenderer.renderRoll = roll;

            applyMain();

            if(entity instanceof EntityPlayerSP)
            {
                EntityPlayerSP player = (EntityPlayerSP)entity;
                player.prevRenderArmYaw = player.prevRotationYaw;
                player.renderArmYaw = player.rotationYaw;
                player.prevRenderArmPitch = player.prevRotationPitch;
                player.renderArmPitch = player.rotationPitch;
                player.prevRenderArmYaw -= prevYaw;
                player.renderArmYaw -= yaw;
                player.prevRenderArmPitch -= prevPitch;
                player.renderArmPitch -= pitch;
            }
        }

        private void applyMain()
        {
            entity.posX += x;
            entity.posY += y;
            entity.posZ += z;
            entity.prevPosX += x;
            entity.prevPosY += y;
            entity.prevPosZ += z;
            entity.lastTickPosX += x;
            entity.lastTickPosY += y;
            entity.lastTickPosZ += z;

            entity.rotationPitch = (entity.rotationPitch + pitch);
            entity.rotationYaw = (entity.rotationYaw + yaw);
            entity.prevRotationPitch = (entity.prevRotationPitch + pitch);
            entity.prevRotationYaw = (entity.prevRotationYaw + yaw);
            if(entity instanceof EntityLivingBase)
            {
                EntityLivingBase living = (EntityLivingBase)entity;
                living.rotationYawHead = (living.rotationYawHead + yaw);
                living.prevRotationYawHead = (living.prevRotationYawHead + yaw);
            }
        }

        public void revert()
        {
            if(entity.worldObj.isRemote)
            {
                revertClient();
            }
            else
            {
                revertMain();
            }
        }

        //This fixes the hand bouncing around when you look around
        @SideOnly(Side.CLIENT)
        private void revertClient()
        {
            float prevYaw = 0.0F;
            float yaw = 0.0F;
            float prevPitch = 0.0F;
            float pitch = 0.0F;

            if(entity instanceof EntityPlayerSP)
            {
                EntityPlayerSP player = (EntityPlayerSP)entity;
                prevYaw = player.prevRotationYaw - player.prevRenderArmYaw;
                yaw = player.rotationYaw - player.renderArmYaw;
                prevPitch = player.prevRotationPitch - player.prevRenderArmPitch;
                pitch = player.rotationPitch - player.renderArmPitch;
            }

            revertMain();

            WorldPortalRenderer.renderRoll = 0F;

            if(entity instanceof EntityPlayerSP)
            {
                EntityPlayerSP player = (EntityPlayerSP)entity;
                player.prevRenderArmYaw = player.prevRotationYaw;
                player.renderArmYaw = player.rotationYaw;
                player.prevRenderArmPitch = player.prevRotationPitch;
                player.renderArmPitch = player.rotationPitch;
                player.prevRenderArmYaw -= prevYaw;
                player.renderArmYaw -= yaw;
                player.prevRenderArmPitch -= prevPitch;
                player.renderArmPitch -= pitch;
            }
        }

        private void revertMain()
        {
            entity.posX -= x;
            entity.posY -= y;
            entity.posZ -= z;
            entity.prevPosX -= x;
            entity.prevPosY -= y;
            entity.prevPosZ -= z;
            entity.lastTickPosX -= x;
            entity.lastTickPosY -= y;
            entity.lastTickPosZ -= z;

            entity.rotationPitch = (entity.rotationPitch - pitch);
            entity.rotationYaw = (entity.rotationYaw - yaw);
            entity.prevRotationPitch = (entity.prevRotationPitch - pitch);
            entity.prevRotationYaw = (entity.prevRotationYaw - yaw);
            if(entity instanceof EntityLivingBase)
            {
                EntityLivingBase living = (EntityLivingBase)entity;
                living.rotationYawHead = (living.rotationYawHead - yaw);
                living.prevRotationYawHead = (living.prevRotationYawHead - yaw);
            }
        }

    }

    private static class EntityTransformationSeparator extends EntityTransformation
    {

    }

    public static void moveEntity(Entity ent, double destX, double destY, double destZ, float[] pos, float[] rot, float partialTicks)
    {
        EntityTransformationStack.setEntity(ent);
        EntityTransformationStack.push();

        double ePosX = ent.lastTickPosX + (ent.posX - ent.lastTickPosX) * (double)partialTicks;
        double ePosY = ent.lastTickPosY + (ent.posY - ent.lastTickPosY) * (double)partialTicks + ent.getEyeHeight();
        double ePosZ = ent.lastTickPosZ + (ent.posZ - ent.lastTickPosZ) * (double)partialTicks;

        EntityTransformationStack.translate(destX - ePosX + pos[0], destY - ePosY + pos[1], destZ - ePosZ + pos[2]); //go to the centre of the dest portal and offset with the fields
        EntityTransformationStack.rotate(rot[0], rot[1], rot[2]);
    }

    public static void resetEntity(Entity ent)
    {
        if(ent == entity)
        {
            EntityTransformationStack.pop();
        }
    }
}

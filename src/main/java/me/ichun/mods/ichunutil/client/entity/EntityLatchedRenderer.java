package me.ichun.mods.ichunutil.client.entity;

import me.ichun.mods.ichunutil.client.render.entity.RenderLatchedRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityLatchedRenderer extends Entity
    implements IRenderFactory<EntityLatchedRenderer>
{
    public Entity latchedEnt;

    public long lastUpdate;

    public int relocationTries;

    public EntityLatchedRenderer(World par1World)
    {
        super(par1World);
        if(par1World != null)
        {
            setSize(0.1F, 0.1F);
            lastUpdate = par1World.getWorldTime();
        }
    }

    public EntityLatchedRenderer(World par1World, Entity ent)
    {
        super(par1World);
        setSize(ent.width * 0.75F, ent.height * 0.75F);
        latchedEnt = ent;
        setLocationAndAngles(latchedEnt.posX, latchedEnt.posY, latchedEnt.posZ, latchedEnt.rotationYaw, latchedEnt.rotationPitch);
        lastUpdate = par1World.getWorldTime();
    }

    public EntityLatchedRenderer setIgnoreFrustumCheck()
    {
        ignoreFrustumCheck = true;
        return this;
    }

    public EntityLatchedRenderer setRenderSize(float width, float height) //use this for hats eg
    {
        if(!ignoreFrustumCheck)
        {
            if(width > this.width && height > this.height)
            {
                setSize(width, height);
            }
            else if(width > this.width)
            {
                setSize(width, this.height);
            }
            else if(height > this.height)
            {
                setSize(this.width, height);
            }
        }
        return this;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean isInRangeToRenderDist(double distance)
    {
        return latchedEnt.isInRangeToRenderDist(distance);
    }

    public void updatePos()
    {
        if(latchedEnt != null)
        {
            this.lastTickPosX = this.latchedEnt.lastTickPosX;
            this.lastTickPosY = this.latchedEnt.lastTickPosY;
            this.lastTickPosZ = this.latchedEnt.lastTickPosZ;

            this.prevPosX = this.latchedEnt.prevPosX;
            this.prevPosY = this.latchedEnt.prevPosY;
            this.prevPosZ = this.latchedEnt.prevPosZ;

            this.posX = this.latchedEnt.posX;
            this.posY = this.latchedEnt.posY;
            this.posZ = this.latchedEnt.posZ;
        }
    }

    @Override
    public void onUpdate()
    {
        ticksExisted++;
        if(latchedEnt == null || !latchedEnt.isEntityAlive())
        {
            setDead();
            return;
        }

        relocationTries = 0;

        MinecraftForge.EVENT_BUS.post(new EntityLatchedRendererUpdateEvent(this));

        lastUpdate = worldObj.getWorldTime();
    }

    @Override
    public boolean shouldRenderInPass(int pass)
    {
        return pass == 1;
    }

    @Override
    public int getBrightnessForRender(float par1)
    {
        return latchedEnt.getBrightnessForRender(par1);
    }

    @Override
    public void entityInit()
    {
    }

    @Override
    public boolean writeToNBTOptional(NBTTagCompound par1NBTTagCompound)//disable saving of the entity
    {
        return false;
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbttagcompound)
    {
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbttagcompound)
    {
    }

    @Override
    public Render<EntityLatchedRenderer> createRenderFor(RenderManager manager)
    {
        return new RenderLatchedRenderer(manager);
    }

    public class EntityLatchedRendererUpdateEvent extends EntityEvent
    {
        public final EntityLatchedRenderer ent;
        public EntityLatchedRendererUpdateEvent(EntityLatchedRenderer ent)
        {
            super(ent);
            this.ent = ent;
        }
    }
}

package me.ichun.mods.ichunutil.client.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityLatchedRenderer extends Entity
{
    public Entity latchedEnt;

    public long lastUpdate;

    public int maxDeathPersistTime;
    public int currentDeathPersistTime;

    public EntityLatchedRenderer(World par1World)
    {
        super(par1World);
        setSize(0.1F, 0.1F);
        lastUpdate = par1World.getWorldTime();
        maxDeathPersistTime = currentDeathPersistTime = 0;
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

    public EntityLatchedRenderer setDeathPersistTime(int ticks)
    {
        if(ticks > maxDeathPersistTime)
        {
            maxDeathPersistTime = ticks;
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
            if(latchedEnt.isEntityAlive())
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
            else
            {
                this.lastTickPosX = this.prevPosX = this.posX = this.latchedEnt.posX;
                this.lastTickPosY = this.prevPosY = this.posY = this.latchedEnt.posY;
                this.lastTickPosZ = this.prevPosZ = this.posZ = this.latchedEnt.posZ;
            }
            this.setPosition(posX, posY, posZ);
        }
    }

    @Override
    public void onUpdate()
    {
        if(latchedEnt == null)
        {
            return;
        }

        ticksExisted++;

        if(!latchedEnt.isEntityAlive())
        {
            currentDeathPersistTime++;
            if(currentDeathPersistTime > maxDeathPersistTime)
            {
                setDead();
                return;
            }
        }

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

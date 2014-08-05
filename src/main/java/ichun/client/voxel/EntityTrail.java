package ichun.client.voxel;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ichun.common.iChunUtil;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class EntityTrail extends Entity
{
    public AbstractClientPlayer parent;

    public long lastUpdate;

    public ModelVoxel model;

    public EntityTrail(World par1World)
    {
        super(par1World);
        setSize(0.1F, 0.1F);

        lastUpdate = par1World.getWorldTime();
        ignoreFrustumCheck = true;
        renderDistanceWeight = 10D;

        model = new ModelVoxel();
    }

    public EntityTrail(World par1World, AbstractClientPlayer ent)
    {
        super(par1World);
        setSize(0.1F, 0.1F);
        parent = ent;
        setLocationAndAngles(parent.posX, parent.boundingBox.minY, parent.posZ, parent.rotationYaw, parent.rotationPitch);
        lastUpdate = par1World.getWorldTime();
        ignoreFrustumCheck = true;
        renderDistanceWeight = 10D;

        model = new ModelVoxel();
    }

    @Override
    public void onUpdate()
    {
        ticksExisted++;

        if(parent == null || !parent.isEntityAlive() || parent.isChild())
        {
            setDead();
            return;
        }

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
        return super.getBrightnessForRender(par1);
    }

    @Override
    public void setDead()
    {
        super.setDead();
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

}

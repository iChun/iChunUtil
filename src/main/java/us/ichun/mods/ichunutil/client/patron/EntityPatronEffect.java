package us.ichun.mods.ichunutil.client.patron;

import me.ichun.mods.morph.api.MorphApi;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import us.ichun.mods.ichunutil.common.core.patron.PatronInfo;
import us.ichun.mods.ichunutil.common.iChunUtil;

@SideOnly(Side.CLIENT)
public class EntityPatronEffect extends Entity
{
    public AbstractClientPlayer parent;

    public long lastUpdate;

    public ModelVoxel model;

    public EntityPatronEffect(World par1World)
    {
        super(par1World);
        setSize(0.1F, 0.1F);

        lastUpdate = par1World.getWorldTime();
        ignoreFrustumCheck = true;
        renderDistanceWeight = 10D;

        model = new ModelVoxel();
    }

    public EntityPatronEffect(World par1World, AbstractClientPlayer ent)
    {
        super(par1World);
        setSize(0.1F, 0.1F);
        parent = ent;
        setLocationAndAngles(parent.posX, parent.getEntityBoundingBox().minY, parent.posZ, parent.rotationYaw, parent.rotationPitch);
        lastUpdate = par1World.getWorldTime();
        ignoreFrustumCheck = true;
        renderDistanceWeight = 10D;

        model = new ModelVoxel();
    }

    @Override
    public void onUpdate()
    {
        ticksExisted++;
        if(parent == null || !parent.isEntityAlive() || parent.isChild() || iChunUtil.proxy.effectTicker.streaks.get(parent.getCommandSenderName()) != this)
        {
            setDead();
            return;
        }

        PatronInfo info = getPatronInfo(parent);
        if(info != null && info.type == 5)
        {
            double moX = parent.posX - parent.prevPosX;
            double moZ = parent.posZ - parent.prevPosZ;
            if(Math.sqrt(moX * moX + moZ * moZ) > 0.11D)
            {
                int i = MathHelper.floor_double(parent.posX);
                int j = MathHelper.floor_double(parent.posY - 0.20000000298023224D);
                int k = MathHelper.floor_double(parent.posZ);
                BlockPos blockpos = new BlockPos(i, j, k);
                IBlockState iblockstate = this.worldObj.getBlockState(blockpos);
                Block block = iblockstate.getBlock();
                if(block.getRenderType() != -1)
                {
                    if(parent.isSprinting())
                    {
                        for(int kk = 0; kk < 2; kk++)
                        {
                            worldObj.spawnParticle(EnumParticleTypes.BLOCK_DUST, parent.posX + ((double)parent.getRNG().nextFloat() - 0.5D) * (double)parent.width, parent.getEntityBoundingBox().minY + 0.1D, parent.posZ + ((double)parent.getRNG().nextFloat() - 0.5D) * (double)parent.width, -moX * 0.8D, 0.2D + 0.3D * parent.getRNG().nextDouble(), -moZ * 0.8D, Block.getStateId(iblockstate));
                        }
                    }
                    for(int kk = 0; kk < 3; kk++)
                    {
                        double d0 = worldObj.rand.nextGaussian() * 0.1D;
                        double d2 = worldObj.rand.nextGaussian() * 0.1D;
                        worldObj.spawnParticle(EnumParticleTypes.FLAME, parent.posX + d0, parent.posY + 0.1F, parent.posZ + d2, 0D, parent.isSprinting() ? parent.getRNG().nextFloat() * 0.05D : 0.0125D, 0D);
                    }
                }
            }
        }

        lastUpdate = worldObj.getWorldTime();
    }

    public static PatronInfo getPatronInfo(EntityPlayer player)
    {
        EntityPlayer oriPlayer = player;
        if(iChunUtil.hasMorphMod)
        {
            EntityLivingBase ent = MorphApi.getApiImpl().getMorphEntity(player.worldObj, player.getCommandSenderName(), Side.CLIENT);
            if(ent != null) //is morphed
            {
                if(!(ent instanceof EntityPlayer) || MorphApi.getApiImpl().morphProgress(player.getCommandSenderName(), Side.CLIENT) < 1.0F)
                {
                    return null;
                }
                player = (EntityPlayer)ent;
            }
        }
        PatronInfo info = null;

        for(PatronInfo info1 : iChunUtil.proxy.effectTicker.patronList)
        {
            if(info1.id.equals(player.getGameProfile().getId().toString()))
            {
                info = info1;
                break;
            }
        }
        return info;
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

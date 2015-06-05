package us.ichun.mods.ichunutil.common.entity;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import us.ichun.mods.ichunutil.common.core.packet.mod.PacketRequestBlockEntityData;
import us.ichun.mods.ichunutil.common.grab.GrabHandler;
import us.ichun.mods.ichunutil.common.iChunUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EntityBlock extends Entity
{
    public float rotYaw;
    public float rotPitch;
    public float prevRotYaw;
    public float prevRotPitch;

    public double prevMotionX;
    public double prevMotionY;
    public double prevMotionZ;

    public int timeOnGround;
    public int timeExisting;
    public int timeout;

    public boolean canDropItems;

    public boolean requestedSetupPacket;
    public boolean setup;

    public IBlockState[][][] blocks;
    public NBTTagCompound[][][] tileEntityNBTs;

    @SideOnly(Side.CLIENT)
    public TileEntity[][][] renderingTileEntities;

    public static float maxRotFac = 25F;

    public EntityBlock(World world)
    {
        super(world);
        setSize(0.95F, 0.95F);

        preventEntitySpawning = true;
        renderDistanceWeight = 20D;
        isImmuneToFire = true;
        canDropItems = true;
    }

    public EntityBlock(World world, ArrayList<BlockPos> poses)
    {
        this(world);
        int lowX = Integer.MAX_VALUE;
        int lowY = Integer.MAX_VALUE;
        int lowZ = Integer.MAX_VALUE;

        int highX = Integer.MIN_VALUE;
        int highY = Integer.MIN_VALUE;
        int highZ = Integer.MIN_VALUE;

        for(BlockPos pos : poses)
        {
            if(pos.getX() < lowX)
            {
                lowX = pos.getX();
            }
            if(pos.getY() < lowY)
            {
                lowY = pos.getY();
            }
            if(pos.getZ() < lowZ)
            {
                lowZ = pos.getZ();
            }
            if(pos.getX() > highX)
            {
                highX = pos.getX();
            }
            if(pos.getY() > highY)
            {
                highY = pos.getY();
            }
            if(pos.getZ() > highZ)
            {
                highZ = pos.getZ();
            }
        }

        int countX = highX - lowX + 1;
        int countY = highY - lowY + 1;
        int countZ = highZ - lowZ + 1;

        blocks = new IBlockState[countX][countY][countZ];
        tileEntityNBTs = new NBTTagCompound[countX][countY][countZ];
        for(BlockPos pos : poses)
        {
            IBlockState state = world.getBlockState(pos);

            if(state.getBlock() != Blocks.air)
            {
                blocks[highX - pos.getX()][highY - pos.getY()][highZ - pos.getZ()] = state;

                TileEntity te = world.getTileEntity(pos);
                if(te != null && state.getBlock().hasTileEntity(state))
                {
                    world.setTileEntity(pos, state.getBlock().createTileEntity(world, state));

                    tileEntityNBTs[highX - pos.getX()][highY - pos.getY()][highZ - pos.getZ()] = new NBTTagCompound();
                    te.writeToNBT(tileEntityNBTs[highX - pos.getX()][highY - pos.getY()][highZ - pos.getZ()]);

                    te.invalidate();
                }
            }

            world.setBlockState(pos, Blocks.dirt.getDefaultState(), 2);
        }

        for(BlockPos pos : poses)
        {
            world.setBlockToAir(pos);
        }

        setSize();
        setLocationAndAngles(lowX + (countX / 2F), lowY + 0.025D, lowZ + (countZ / 2F), 0F, 0F);
    }

    public void setSize()
    {
        setSize(Math.max(blocks.length, blocks[0][0].length) - 0.05F, blocks[0].length - 0.05F);
    }

    @Override
    public void setSize(float width, float height)
    {
        float f2 = this.width;
        this.width = width;
        this.height = height;
        if(blocks == null)
        {
            this.setEntityBoundingBox(new AxisAlignedBB(this.getEntityBoundingBox().minX, this.getEntityBoundingBox().minY, this.getEntityBoundingBox().minZ, this.getEntityBoundingBox().minX + (double)this.width, this.getEntityBoundingBox().minY + (double)this.height, this.getEntityBoundingBox().minZ + (double)this.width));
        }
        else
        {
            this.setEntityBoundingBox(new AxisAlignedBB(this.getEntityBoundingBox().minX, this.getEntityBoundingBox().minY, this.getEntityBoundingBox().minZ, this.getEntityBoundingBox().minX + (double)(blocks.length - 0.05F), this.getEntityBoundingBox().minY + (double)(blocks[0].length - 0.05F), this.getEntityBoundingBox().minZ + (double)(blocks[0][0].length - 0.05F)));
        }

        if (this.width > f2 && !this.firstUpdate && !this.worldObj.isRemote)
        {
            this.moveEntity((double)(f2 - this.width), 0.0D, (double)(f2 - this.width));
        }
    }

    @Override
    public void setPosition(double x, double y, double z)
    {
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        if(blocks == null)
        {
            float f = this.width / 2.0F;
            float f1 = this.height;
            this.setEntityBoundingBox(new AxisAlignedBB(x - (double)f, y, z - (double)f, x + (double)f, y + (double)f1, z + (double)f));
        }
        else
        {
            float fx = (blocks.length - 0.05F) / 2.0F;
            float f1 = (blocks[0].length - 0.05F);
            float fz = (blocks[0][0].length - 0.05F) / 2.0F;
            this.setEntityBoundingBox(new AxisAlignedBB(x - (double)fx, y, z - (double)fz, x + (double)fx, y + (double)f1, z + (double)fz));
        }
    }

    @Override
    protected void entityInit()
    {
        dataWatcher.addObject(18, rand.nextFloat() * (2F * maxRotFac) - maxRotFac); //rotFactor Yaw
        dataWatcher.addObject(19, rand.nextFloat() * (2F * maxRotFac) - maxRotFac); //rotFactor Pitch
        dataWatcher.addObject(20, (byte)1); //canRotate
        dataWatcher.addObject(21, 0); //behaviour
    }

    public void setRotFacYaw(float f)
    {
        dataWatcher.updateObject(18, f);
    }

    public float getRotFacYaw()
    {
        return dataWatcher.getWatchableObjectFloat(18);
    }

    public void setRotFacPitch(float f)
    {
        dataWatcher.updateObject(19, f);
    }

    public float getRotFacPitch()
    {
        return dataWatcher.getWatchableObjectFloat(19);
    }

    public void setCanRotate(boolean flag)
    {
        dataWatcher.updateObject(20, (byte)(flag ? 1 : 0));
    }

    public boolean getCanRotate()
    {
        return dataWatcher.getWatchableObjectByte(20) == 1;
    }

    public void setBehaviour(int i)
    {
        dataWatcher.updateObject(21, i);
    }

    public int getBehaviour()
    {
        return dataWatcher.getWatchableObjectInt(21);
    }

    @Override
    public void onUpdate()
    {
        if(worldObj.isRemote && !setup)
        {
            if(!requestedSetupPacket)
            {
                requestedSetupPacket = true;
                iChunUtil.channel.sendToServer(new PacketRequestBlockEntityData(this));
            }
            return;
        }

        timeExisting++;

        if(!worldObj.isRemote && posY < -500D)
        {
            setDead();
            return;
        }

        if(timeExisting < timeout)
        {
            noClip = true;
        }
        else
        {
            noClip = false;
        }

        prevRotYaw = rotYaw;
        prevRotPitch = rotPitch;
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;

        if(!GrabHandler.isGrabbed(this, FMLCommonHandler.instance().getEffectiveSide()))
        {
            rotYaw += getRotFacYaw();
            rotPitch += getRotFacPitch();
        }

        prevMotionX = motionX;
        prevMotionY = motionY;
        prevMotionZ = motionZ;

        motionY -= 0.06D;

        moveEntity(motionX, motionY, motionZ);

        boolean setBlock = false;

        if(!GrabHandler.isGrabbed(this, FMLCommonHandler.instance().getEffectiveSide()))
        {
            if(onGround && ticksExisted > 2)
            {
                timeOnGround++;
                if(motionY == 0.0D)
                {
                    if(prevMotionY < -0.1D)
                    {
                        double minBounceFactor = Math.sqrt(100D / 75D);
                        float blockHardness = getAverageBlockHardness();
                        double bounceFactor = (blockHardness < minBounceFactor ? minBounceFactor : blockHardness);
                        motionY = prevMotionY * -(1D / (2 * bounceFactor));

                        setRotFacYaw(rand.nextFloat() * (2F * maxRotFac) - maxRotFac);
                        setRotFacPitch(rand.nextFloat() * (2F * maxRotFac) - maxRotFac);
                    }
                    if(timeOnGround > 3)
                    {
                        setRotFacYaw(getRotFacYaw() * 0.6F);
                        setRotFacPitch(getRotFacPitch() * 0.6F);
                    }
                    motionX *= 0.7D;
                    motionZ *= 0.7D;

                    if(Math.abs(prevMotionX) < 0.05D && Math.abs(prevMotionZ) < 0.05D && Math.abs(prevMotionY) < 0.05D && Math.abs(getRotFacYaw()) < 0.05D && Math.abs(getRotFacPitch()) < 0.05D)
                    {
                        setBlock = true;
                    }
                }
            }
            else
            {
                timeOnGround = 0;
            }

            if(motionX == 0D && prevMotionX != motionX)
            {
                double minBounceFactor = Math.sqrt(100D / 75D);
                float blockHardness = getAverageBlockHardness();
                double bounceFactor = 2D * (blockHardness < minBounceFactor ? minBounceFactor : blockHardness);
                motionX = prevMotionX * -(1D / (bounceFactor * bounceFactor));

                setRotFacYaw(rand.nextFloat() * (2F * maxRotFac) - maxRotFac);
                setRotFacPitch(rand.nextFloat() * (2F * maxRotFac) - maxRotFac);
            }
            if(motionZ == 0D && prevMotionZ != motionZ)
            {
                double minBounceFactor = Math.sqrt(100D / 75D);
                float blockHardness = getAverageBlockHardness();
                double bounceFactor = 2D * (blockHardness < minBounceFactor ? minBounceFactor : blockHardness);
                motionZ = prevMotionZ * -(1D / (bounceFactor * bounceFactor));

                setRotFacYaw(rand.nextFloat() * (2F * maxRotFac) - maxRotFac);
                setRotFacPitch(rand.nextFloat() * (2F * maxRotFac) - maxRotFac);
            }
        }

        if(!worldObj.isRemote && (setBlock || timeExisting > (20 * 60 * 5)))
        {
            setDead();

            for(int i = 0; i < blocks.length; i++)
            {
                for(int j = blocks[i].length - 1; j >= 0; j--)
                {
                    for(int k = 0; k < blocks[i][j].length; k++)
                    {
                        if(blocks[i][j][k] != null)
                        {
                            BlockPos pos = new BlockPos(posX - (((getEntityBoundingBox().maxX - getEntityBoundingBox().minX) + 0.05D) / 2F) + blocks.length - i - 0.5D, posY + blocks[i].length - j - 0.5D, posZ - (((getEntityBoundingBox().maxZ - getEntityBoundingBox().minZ) + 0.05D) / 2F) + blocks[i][j].length - k - 0.5D);
                            if(!worldObj.setBlockState(pos, blocks[i][j][k], 2) && canDropItems)
                            {
                                blocks[i][j][k].getBlock().dropBlockAsItem(worldObj, pos, blocks[i][j][k], 0);

                                if(tileEntityNBTs[i][j][k] != null && blocks[i][j][k].getBlock().hasTileEntity(blocks[i][j][k]))
                                {
                                    TileEntity te = blocks[i][j][k].getBlock().createTileEntity(worldObj, blocks[i][j][k]);
                                    if(te instanceof IInventory)
                                    {
                                        te.readFromNBT(tileEntityNBTs[i][j][k]);
                                        handleIInventoryBreak((IInventory)te);
                                    }
                                }
                            }
                            else if(tileEntityNBTs[i][j][k] != null && blocks[i][j][k].getBlock().hasTileEntity(blocks[i][j][k]))
                            {
                                TileEntity te = worldObj.getTileEntity(pos);

                                if(te != null)
                                {
                                    NBTTagCompound nbttagcompound = new NBTTagCompound();
                                    te.writeToNBT(nbttagcompound);
                                    Iterator iterator = tileEntityNBTs[i][j][k].getKeySet().iterator();

                                    while(iterator.hasNext())
                                    {
                                        String s = (String)iterator.next();
                                        NBTBase nbtbase = tileEntityNBTs[i][j][k].getTag(s);

                                        if(!s.equals("x") && !s.equals("y") && !s.equals("z"))
                                        {
                                            nbttagcompound.setTag(s, nbtbase.copy());
                                        }
                                    }

                                    te.readFromNBT(nbttagcompound);
                                    te.markDirty();
                                }
                            }
                        }
                    }
                }
            }
            for(int i = 0; i < blocks.length; i++)
            {
                for(int j = blocks[i].length - 1; j >= 0; j--)
                {
                    for(int k = 0; k < blocks[i][j].length; k++)
                    {
                        if(blocks[i][j][k] != null)
                        {
                            BlockPos pos = new BlockPos(posX - (((getEntityBoundingBox().maxX - getEntityBoundingBox().minX) + 0.05D) / 2F) + blocks.length - i - 0.5D, posY + blocks[i].length - j - 0.5D, posZ - (((getEntityBoundingBox().maxZ - getEntityBoundingBox().minZ) + 0.05D) / 2F) + blocks[i][j].length - k - 0.5D);
                            worldObj.notifyNeighborsRespectDebug(pos, Blocks.air);
                        }
                    }
                }
            }
        }

        motionX *= 0.95D;
        motionY *= 0.95D;
        motionZ *= 0.95D;
    }

    public float getAverageBlockHardness()
    {
        float hardness = 0.0F;
        int count = 0;
        for(int i = 0; i < blocks.length; i++)
        {
            for(int j = 0; j < blocks[i].length; j++)
            {
                for(int k = 0; k < blocks[i][j].length; k++)
                {
                    if(blocks[i][j][k] != null)
                    {
                        hardness += blocks[i][j][k].getBlock().blockHardness;
                        count++;
                    }
                }
            }
        }
        return hardness / (float)count;
    }

    public void handleIInventoryBreak(IInventory var7)
    {
        if (var7 != null)
        {
            for (int var8 = 0; var8 < var7.getSizeInventory(); ++var8)
            {
                ItemStack var9 = var7.getStackInSlot(var8);

                if (var9 != null)
                {
                    float var10 = this.rand.nextFloat() * 0.8F + 0.1F;
                    float var11 = this.rand.nextFloat() * 0.8F + 0.1F;
                    EntityItem var14;

                    for (float var12 = this.rand.nextFloat() * 0.8F + 0.1F; var9.stackSize > 0; worldObj.spawnEntityInWorld(var14))
                    {
                        int var13 = this.rand.nextInt(21) + 10;

                        if (var13 > var9.stackSize)
                        {
                            var13 = var9.stackSize;
                        }

                        var9.stackSize -= var13;
                        var14 = new EntityItem(worldObj, (double)((float)posX + var10), (double)((float)posY + var11), (double)((float)posZ + var12), new ItemStack(var9.getItem(), var13, var9.getItemDamage()));
                        float var15 = 0.05F;
                        var14.motionX = (double)((float)this.rand.nextGaussian() * var15);
                        var14.motionY = (double)((float)this.rand.nextGaussian() * var15 + 0.2F);
                        var14.motionZ = (double)((float)this.rand.nextGaussian() * var15);

                        if (var9.hasTagCompound())
                        {
                            var14.getEntityItem().setTagCompound((NBTTagCompound)var9.getTagCompound().copy());
                        }
                    }
                }
            }
        }
    }

    public void shatter()
    {
        if(!(blocks.length == 1 && blocks[0].length == 1 && blocks[0][0].length == 1) && !isDead)
        {
            setDead();

            for(int i = 0; i < blocks.length; i++)
            {
                for(int j = 0; j < blocks[i].length; j++)
                {
                    for(int k = 0; k < blocks[i][j].length; k++)
                    {
                        if(blocks[i][j][k] != null)
                        {
                            EntityBlock block = new EntityBlock(worldObj);
                            block.blocks = new IBlockState[1][1][1];
                            block.tileEntityNBTs = new NBTTagCompound[1][1][1];
                            block.blocks[0][0][0] = blocks[i][j][k];
                            block.tileEntityNBTs[0][0][0] = tileEntityNBTs[i][j][k];
                            block.setSize();
                            block.setLocationAndAngles(posX - (width / 2F) + i, posY + j, posZ - (width / 2F) + k, 0F, 0F);
                            block.setCanRotate(block.getCanRotate());
                            block.setBehaviour(block.getBehaviour());
                            float randMag = 0.1F;
                            block.motionX = motionX + (rand.nextFloat() * 2F - 1F) * randMag;
                            block.motionY = motionY + (rand.nextFloat() * 2F - 1F) * randMag;
                            block.motionZ = motionZ + (rand.nextFloat() * 2F - 1F) * randMag;
                            worldObj.spawnEntityInWorld(block);
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean canBeCollidedWith()
    {
        return timeExisting > timeout && !isDead;
    }

    @Override
    public boolean canBePushed()
    {
        return timeExisting > timeout && !isDead;
    }

    @Override
    protected void dealFireDamage(int par1)
    {
    }

    @Override
    public AxisAlignedBB getCollisionBox(Entity entity)
    {
        return entity.getEntityBoundingBox();
    }

    @Override
    public AxisAlignedBB getBoundingBox()
    {
        return getEntityBoundingBox();
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag)
    {
        timeExisting = tag.getInteger("timeExisting");

        setCanRotate(tag.getBoolean("canRotate"));
        setBehaviour(tag.getInteger("behaviour"));

        blocks = new IBlockState[tag.getInteger("lengthX")][tag.getInteger("lengthY")][tag.getInteger("lengthZ")];
        tileEntityNBTs = new NBTTagCompound[tag.getInteger("lengthX")][tag.getInteger("lengthY")][tag.getInteger("lengthZ")];

        for(int i = 0; i < blocks.length; i++)
        {
            for(int j = 0; j < blocks[i].length; j++)
            {
                for(int k = 0; k < blocks[i][j].length; k++)
                {
                    if(tag.hasKey("state_" + Integer.toString(i) + "_" + Integer.toString(j) + "_" + Integer.toString(k)))
                    {
                        blocks[i][j][k] = Block.getStateById(tag.getInteger("state_" + Integer.toString(i) + "_" + Integer.toString(j) + "_" + Integer.toString(k)));

                        if(tag.hasKey("tileEnt_" + Integer.toString(i) + "_" + Integer.toString(j) + "_" + Integer.toString(k)))
                        {
                            tileEntityNBTs[i][j][k] = tag.getCompoundTag("tileEnt_" + Integer.toString(i) + "_" + Integer.toString(j) + "_" + Integer.toString(k));
                        }
                    }
                }
            }
        }

        setSize();
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag)
    {
        tag.setInteger("timeExisting", timeExisting);

        tag.setBoolean("canRotate", getCanRotate());
        tag.setInteger("behaviour", getBehaviour());

        tag.setInteger("lengthX", blocks.length);
        tag.setInteger("lengthY", blocks[0].length);
        tag.setInteger("lengthZ", blocks[0][0].length);

        for(int i = 0; i < blocks.length; i++)
        {
            for(int j = 0; j < blocks[i].length; j++)
            {
                for(int k = 0; k < blocks[i][j].length; k++)
                {
                    if(blocks[i][j][k] != null)
                    {
                        tag.setInteger("state_" + Integer.toString(i) + "_" + Integer.toString(j) + "_" + Integer.toString(k), Block.getStateId(blocks[i][j][k]));

                        if(tileEntityNBTs[i][j][k] != null)
                        {
                            tag.setTag("tileEnt_" + Integer.toString(i) + "_" + Integer.toString(j) + "_" + Integer.toString(k), tileEntityNBTs[i][j][k]);
                        }
                    }
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean canRenderOnFire()
    {
        return false;
    }
}

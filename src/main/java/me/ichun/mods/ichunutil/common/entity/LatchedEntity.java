package me.ichun.mods.ichunutil.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import java.util.UUID;

public abstract class LatchedEntity<M extends Entity> extends Entity
{
    private static final DataParameter<Integer> PARENT_ID = EntityDataManager.createKey(LatchedEntity.class, DataSerializers.VARINT);

    public M parent;

    public UUID parentUUID;

    public LatchedEntity(EntityType<?> type, World world)
    {
        super(type, world);

        this.setInvisible(true); //so we don't have a bounding box on debug
    }

    public <T extends LatchedEntity> T setParent(@Nonnull M parent)
    {
        this.parent = parent;
        setParentId(parent.getEntityId());
        this.setPositionAndRotation(parent.posX, parent.posY, parent.posZ, parent.rotationYaw, parent.rotationPitch);
        return (T)this;
    }

    @Override
    public void registerData()
    {
        getDataManager().register(PARENT_ID, -1);
    }

    public void setParentId(int i)
    {
        getDataManager().set(PARENT_ID, i);
    }

    public int getParentId()
    {
        return getDataManager().get(PARENT_ID);
    }

    @Override
    public void tick()
    {
        super.tick();

        if(parent == null)
        {
            if(parentUUID != null || getParentId() != -1)
            {
                if(ticksExisted % 5 == 1)
                {
                    Entity ent;
                    if(world.isRemote)
                    {
                        if(getParentId() != -1)
                        {
                            ent = world.getEntityByID(getParentId());
                        }
                        else
                        {
                            ent = null;
                        }
                    }
                    else
                    {
                        ent = ((ServerWorld)world).getEntityByUuid(parentUUID);
                    }

                    if(ent != null)
                    {
                        parent = (M)ent;
                        setParentId(parent.getEntityId());
                        setPositionAndRotation(parent.posX, parent.posY, parent.posZ, parent.rotationYaw, parent.rotationPitch);
                        size = parent.size;
                        setBoundingBox(parent.getBoundingBox()); // match the parent's size for rendering
                    }
                    else if(ticksExisted > 201) //10 seconds
                    {
                        unableToFindParent(true);
                        return;
                    }
                }
            }
            else
            {
                unableToFindParent(false);
                return;
            }
        }
        else if(parent.removed) //parent has been removed from the world
        {
            unableToFindParent(true);
            return;
        }
        else if(!parent.dimension.equals(dimension))
        {
            parentDifferentDimension();
            return;
        }
        else //parent is "alive" and safe
        {
            this.setPosition(parent.posX, parent.posY, parent.posZ);
            this.setRotation(parent.rotationYaw, parent.rotationPitch);
        }
    }

    public void unableToFindParent(boolean hasId)
    {
        remove();
    }

    public void parentDifferentDimension()
    {
        unableToFindParent(true);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean isInRangeToRenderDist(double distance)
    {
        return parent != null ? parent.isInRangeToRenderDist(distance) : super.isInRangeToRenderDist(distance);
    }

    @Override
    public float getBrightness()
    {
        return parent != null ? parent.getBrightness() : super.getBrightness();
    }

    @Override
    protected void readAdditional(CompoundNBT tag)
    {
        parentUUID = tag.getUniqueId("parentUUID");
        setParentId(tag.getInt("parentID"));
    }

    @Override
    protected void writeAdditional(CompoundNBT tag)
    {
        tag.putUniqueId("parentUUID", parent.getUniqueID());
        tag.putInt("parentID", getParentId());
    }

    @Nonnull
    @Override
    public IPacket<?> createSpawnPacket()
    {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}

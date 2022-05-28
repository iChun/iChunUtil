package me.ichun.mods.ichunutil.common.entity;

import me.ichun.mods.ichunutil.loader.LoaderHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.UUID;

public abstract class LatchedEntity<M extends Entity> extends Entity
{
    private static final EntityDataAccessor<Integer> PARENT_ID = SynchedEntityData.defineId(LatchedEntity.class, EntityDataSerializers.INT);

    public M parent;

    public UUID parentUUID;

    public int maxPersistAfterDeath = 0; //parent death, not this entity
    public int timeAfterDeath = 0; //parent death, not this entity

    //If you don't want to render anything, register a NoopRenderer for your entity.
    public LatchedEntity(EntityType<?> type, Level world)
    {
        super(type, world);

        this.setInvisible(true); //so we don't have a bounding box on debug
    }

    public <T extends LatchedEntity> T setParent(@Nonnull M parent)
    {
        this.parent = parent;
        setParentId(parent.getId());
        absMoveTo(parent.getX(), parent.getY(), parent.getZ(), parent.getYRot(), parent.getXRot());
        dimensions = parent.dimensions;
        setBoundingBox(parent.getBoundingBox()); // match the parent's size for rendering
        return (T)this;
    }

    public <T extends LatchedEntity> T setPersistAfterDeath(int i)
    {
        maxPersistAfterDeath = i;
        return (T)this;
    }

    @Override
    public void defineSynchedData()
    {
        getEntityData().define(PARENT_ID, -1);
    }

    public void setParentId(int i)
    {
        getEntityData().set(PARENT_ID, i);
    }

    public int getParentId()
    {
        return getEntityData().get(PARENT_ID);
    }

    @Override
    public void tick()
    {
        super.tick();

        if(parent == null)
        {
            if(parentUUID != null || getParentId() != -1)
            {
                if(tickCount % 5 == 1)
                {
                    Entity ent;
                    if(level.isClientSide)
                    {
                        if(getParentId() != -1)
                        {
                            ent = level.getEntity(getParentId());
                        }
                        else
                        {
                            ent = null;
                        }
                    }
                    else
                    {
                        ent = ((ServerLevel)level).getEntity(parentUUID);
                    }

                    if(ent != null)
                    {
                        setParent((M)ent);
                    }
                    else if(tickCount > 201) //10 seconds
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
        else if(!parent.isAlive())
        {
            if(maxPersistAfterDeath > 0)
            {
                if(timeAfterDeath >= maxPersistAfterDeath)
                {
                    unableToFindParent(true);
                    return;
                }
                timeAfterDeath++;
            }
            else if(parent.isRemoved())
            {
                unableToFindParent(true);
                return;
            }
        }
        else if(!parent.level.dimension().equals(level.dimension()))
        {
            parentDifferentDimension();
            return;
        }
        else //parent is "alive" and safe
        {
            this.setPos(parent.getX(), parent.getY(), parent.getZ());
            this.setRot(parent.getYRot(), parent.getXRot());
        }
    }

    public void unableToFindParent(boolean hasId)
    {
        discard();
    }

    public void parentDifferentDimension()
    {
        unableToFindParent(true);
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    @Override
    public boolean shouldRenderAtSqrDistance(double distance)
    {
        return parent != null ? parent.shouldRenderAtSqrDistance(distance) : super.shouldRenderAtSqrDistance(distance);
    }

    @Override
    public float getBrightness()
    {
        return parent != null ? parent.getBrightness() : super.getBrightness();
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag)
    {
        parentUUID = tag.getUUID("parentUUID");
        setParentId(tag.getInt("parentID"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag)
    {
        tag.putUUID("parentUUID", parent.getUUID());
        tag.putInt("parentID", getParentId());
    }

    @Nonnull
    @Override
    public Packet<?> getAddEntityPacket()
    {
        return LoaderHandler.d().getEntitySpawnPacket(this);
    }
}

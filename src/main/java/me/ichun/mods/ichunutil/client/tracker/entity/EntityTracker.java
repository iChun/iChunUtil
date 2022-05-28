package me.ichun.mods.ichunutil.client.tracker.entity;

import me.ichun.mods.ichunutil.client.tracker.tag.Tag;
import me.ichun.mods.ichunutil.loader.LoaderHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class EntityTracker extends Entity
{
    public HashSet<Tag> tags = new HashSet<>();

    @Nonnull
    public Entity parent;

    public int maxPersistAfterDeath = 0;
    public int timeAfterDeath = 0;

    public int maxTrack = 0;
    public ArrayList<EntityInfo> trackedInfo = new ArrayList<>();

    public int lastUpdate = -1;

    public EntityTracker(EntityType<?> entityTypeIn, Level worldIn)
    {
        super(entityTypeIn, worldIn);
        setInvisible(true);
        setInvulnerable(true);
    }

    public EntityTracker setParent(@Nonnull Entity tracked)
    {
        this.parent = tracked;
        return this;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <T extends Tag> T getTag(Class<T> clz)
    {
        for(Tag tag : tags)
        {
            if(clz.isInstance(tag))
            {
                return (T)tag;
            }
        }
        return null;
    }

    public void addTag(Tag tag)
    {
        tags.add(tag);
        tag.init(this);

        updateBounds();
    }

    public void removeTag(Class<? extends Tag> clz)
    {
        tags.removeIf(clz::isInstance);

        updateBounds();
    }

    public void updateBounds()
    {
        int tracks = 0;
        int deathTicks = 0;
        float w = 0.1F;
        float h = 0.1F;
        boolean ignoreFrustum = false;
        for(Tag tag : tags)
        {
            if(tag.maxTracks() > tracks)
            {
                tracks = tag.maxTracks();
            }
            if(tag.maxDeathPersist() > deathTicks)
            {
                deathTicks = tag.maxDeathPersist();
            }
            float tagW = tag.width(this);
            if(w > tagW)
            {
                w = tagW;
            }
            float tagH = tag.height(this);
            if(h > tagH)
            {
                h = tagH;
            }
            ignoreFrustum = tag.ignoreFrustumCheck() | ignoreFrustum;
        }

        maxTrack = tracks;
        maxPersistAfterDeath = deathTicks;
        dimensions = EntityDimensions.scalable(w, h);
        noCulling = ignoreFrustum;

        setPos(getX(), getY(), getZ());
    }

    @Override
    public void tick()
    {
        super.tick();

        if(Minecraft.getInstance().player != null)
        {
            lastUpdate = Minecraft.getInstance().player.tickCount;
        }
        else
        {
            discard(); // player's null. IMPOSSIBRU. Kill.
            return;
        }

        if(!parent.isAlive() || !parent.level.dimension().equals(level.dimension())) //parent is "dead"
        {
            if(maxPersistAfterDeath > 0)
            {
                if(timeAfterDeath >= maxPersistAfterDeath)
                {
                    discard();
                }
                timeAfterDeath++;
            }
            else if(parent.isRemoved())
            {
                discard();
            }
        }
        else //parent is "alive" and safe
        {
            this.setPos(parent.getX(), parent.getY(), parent.getZ());
            this.setRot(parent.getYRot(), parent.getXRot());

            if(maxTrack > 0)
            {
                EntityInfo info = new EntityInfo(parent.getX(), parent.getY(), parent.getZ(), parent.dimensions.width, parent.dimensions.height, parent.getYRot(), parent.getXRot(), parent.isInvisible());
                trackedInfo.add(0, info);
                tags.forEach(tag -> tag.addInfo(this, info));

                while(trackedInfo.size() > maxTrack)
                {
                    EntityInfo removed = trackedInfo.get(trackedInfo.size() - 1);
                    tags.forEach(tag -> tag.removeInfo(this, removed));
                    trackedInfo.remove(trackedInfo.size() - 1);
                }
            }
        }

        tags.forEach(tag -> tag.tick(this));
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return parent.shouldRenderAtSqrDistance(distance);
    }

    @Override
    public float getBrightness()
    {
        return parent.getBrightness();
    }

    @Override
    protected void defineSynchedData(){}

    @Override
    public boolean saveAsPassenger(CompoundTag compound) { return false; } //disable saving of entity

    @Override
    protected void readAdditionalSaveData(CompoundTag compound){}

    @Override
    protected void addAdditionalSaveData(CompoundTag compound){}

    @Override
    public Packet<?> getAddEntityPacket()
    {
        return LoaderHandler.d().getEntitySpawnPacket(this);
    }

    public static class EntityInfo
    {
        public final double posX;
        public final double posY;
        public final double posZ;
        public final float width;
        public final float height;
        public final float yaw;
        public final float pitch;
        public final boolean invisible;

        public EntityInfo(double posX, double posY, double posZ, float width, float height, float yaw, float pitch, boolean invisible) {
            this.posX = posX;
            this.posY = posY;
            this.posZ = posZ;
            this.width = width;
            this.height = height;
            this.yaw = yaw;
            this.pitch = pitch;
            this.invisible = invisible;
        }
    }
}

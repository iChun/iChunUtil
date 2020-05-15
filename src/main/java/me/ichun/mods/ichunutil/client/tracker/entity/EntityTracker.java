package me.ichun.mods.ichunutil.client.tracker.entity;

import me.ichun.mods.ichunutil.client.tracker.tag.Tag;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;

@OnlyIn(Dist.CLIENT)
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

    public EntityTracker(EntityType<?> entityTypeIn, World worldIn)
    {
        super(entityTypeIn, worldIn);
        setInvisible(true);
        setInvulnerable(true);
    }

    public EntityTracker setParent(Entity tracked)
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
        size = EntitySize.flexible(w, h);
        ignoreFrustumCheck = ignoreFrustum;

        setPosition(getPosX(), getPosY(), getPosZ());
    }

    @Override
    public void tick()
    {
        super.tick();

        if(Minecraft.getInstance().player != null)
        {
            lastUpdate = Minecraft.getInstance().player.ticksExisted;
        }
        else
        {
            remove(); // player's null. IMPOSSIBRU. Kill.
            return;
        }

        if(!parent.isAlive() || !parent.dimension.equals(dimension)) //parent is "dead"
        {
            if(maxPersistAfterDeath > 0)
            {
                if(timeAfterDeath >= maxPersistAfterDeath)
                {
                    remove();
                }
                timeAfterDeath++;
            }
            else if(parent.removed)
            {
                remove();
            }
        }
        else //parent is "alive" and safe
        {
            this.setPosition(parent.getPosX(), parent.getPosY(), parent.getPosZ());
            this.setRotation(parent.rotationYaw, parent.rotationPitch);

            if(maxTrack > 0)
            {
                EntityInfo info = new EntityInfo(parent.getPosX(), parent.getPosY(), parent.getPosZ(), parent.size.width, parent.size.height, parent.rotationYaw, parent.rotationPitch, parent.isInvisible());
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
    public boolean isInRangeToRenderDist(double distance) {
        return parent.isInRangeToRenderDist(distance);
    }

    @Override
    public float getBrightness()
    {
        return parent.getBrightness();
    }

    @Override
    protected void registerData(){}

    @Override
    public boolean writeUnlessRemoved(CompoundNBT compound) { return false; } //disable saving of entity

    @Override
    protected void readAdditional(CompoundNBT compound){}

    @Override
    protected void writeAdditional(CompoundNBT compound){}

    @Override
    public IPacket<?> createSpawnPacket()
    {
        return NetworkHooks.getEntitySpawningPacket(this);
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

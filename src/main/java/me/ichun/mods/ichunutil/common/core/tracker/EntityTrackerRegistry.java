package me.ichun.mods.ichunutil.common.core.tracker;

import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.entity.EntityLivingBase;

import java.util.ArrayList;

public class EntityTrackerRegistry
{
    public ArrayList<Entry> trackerEntries = new ArrayList<Entry>();

    public void tick()
    {
        for(int i = trackerEntries.size() - 1; i >= 0; i--)
        {
            Entry entry = trackerEntries.get(i);
            if(!entry.update())
            {
                trackerEntries.remove(i);
            }
        }
    }

    public Entry getOrCreateEntry(EntityLivingBase living, int maxTrack)
    {
        Entry entry = null;
        for(Entry ent1 : trackerEntries)
        {
            if(ent1.entityToTrack == living)
            {
                entry = ent1;
                break;
            }
        }
        if(entry == null)
        {
            entry = new Entry(living);
            trackerEntries.add(entry);
        }
        entry.setMaxTrack(maxTrack);

        return entry;
    }

    public class Entry
    {
        public final EntityLivingBase entityToTrack;
        public int age;
        public int maxTrack;
        public ArrayList<Class<? extends IAdditionalTrackerInfo>> additionalInfo;
        public ArrayList<EntityInfo> trackedInfo; //0 = newest;

        public Entry(EntityLivingBase entityToTrack)
        {
            this.entityToTrack = entityToTrack;
            this.maxTrack = 20;
            this.additionalInfo = new ArrayList<Class<? extends IAdditionalTrackerInfo>>();
            this.trackedInfo = new ArrayList<EntityInfo>();
        }

        public Entry setMaxTrack(int max)
        {
            if(max > maxTrack)
            {
                maxTrack = max;
            }
            return this;
        }

        public Entry addAdditionalTrackerInfo(Class<? extends IAdditionalTrackerInfo> tracker)
        {
            for(Class<? extends IAdditionalTrackerInfo> trackers : additionalInfo)
            {
                if(tracker.equals(trackers))
                {
                    return this;
                }
            }
            additionalInfo.add(tracker);
            for(EntityInfo info : trackedInfo)
            {
                try
                {
                    info.addTracker(tracker.getConstructor().newInstance());
                }
                catch(Exception e)
                {
                    iChunUtil.LOGGER.warn("Error creating tracker: " + tracker.getName());
                    e.printStackTrace();
                }
            }
            return this;
        }

        public boolean update()
        {
            age++;
            if(entityToTrack.isDead)
            {
                return false;
            }

            EntityInfo info = new EntityInfo(entityToTrack);
            for(Class<? extends IAdditionalTrackerInfo> tracker : additionalInfo)
            {
                try
                {
                    info.addTracker(tracker.getConstructor().newInstance());
                }
                catch(Exception e)
                {
                    iChunUtil.LOGGER.warn("Error creating trackers: " + tracker.getName());
                    e.printStackTrace();
                }
            }

            info.update();

            trackedInfo.add(0, info);

            while(trackedInfo.size() > maxTrack)
            {
                trackedInfo.remove(trackedInfo.size() - 1);
            }

            return true;
        }
    }

    public class EntityInfo
    {
        public EntityLivingBase tracked;

        public double posX;
        public double posY;
        public double posZ;

        public float renderYawOffset;
        public float rotationYawHead;
        public float rotationPitch;

        public float limbSwing;
        public float limbSwingAmount;

        public boolean sneaking;
        public boolean sleeping;
        public boolean sprinting;
        public boolean invisible;

        public float height;

        public long lastTick;

        public ArrayList<IAdditionalTrackerInfo> additionalInfo = new ArrayList<IAdditionalTrackerInfo>();

        public EntityInfo(EntityLivingBase ent)
        {
            tracked = ent;
        }

        public EntityInfo addTracker(IAdditionalTrackerInfo trackerInfo)
        {
            additionalInfo.add(trackerInfo);
            return this;
        }

        public IAdditionalTrackerInfo getTracker(Class<? extends IAdditionalTrackerInfo> clz)
        {
            for(IAdditionalTrackerInfo tracker : additionalInfo)
            {
                if(tracker.getClass().equals(clz))
                {
                    return tracker;
                }
            }
            return null;
        }

        public void update()
        {
            posX = tracked.posX;
            posY = tracked.posY;
            posZ = tracked.posZ;

            renderYawOffset = tracked.renderYawOffset;
            rotationYawHead = tracked.rotationYawHead;
            rotationPitch = tracked.getTicksElytraFlying() > 4 ? (float)Math.toDegrees(-((float)Math.PI / 4F)) : tracked.rotationPitch;

            limbSwing = tracked.limbSwing;
            limbSwingAmount = tracked.limbSwingAmount;

            sneaking = tracked.isSneaking();
            sleeping = tracked.isPlayerSleeping();
            sprinting = tracked.isSprinting();
            invisible = tracked.isInvisible();

            height = tracked.height;

            lastTick = tracked.ticksExisted;

            for(IAdditionalTrackerInfo trackers : additionalInfo)
            {
                trackers.track(this);
            }
        }

        public boolean hasSameCoords(EntityInfo info)
        {
            return info.posX == posX && info.posY == posY && info.posZ == posZ;
        }
    }

    public interface IAdditionalTrackerInfo
    {
        void track(EntityInfo info);
    }
}

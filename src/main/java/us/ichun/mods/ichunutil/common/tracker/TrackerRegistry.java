package us.ichun.mods.ichunutil.common.tracker;

import net.minecraft.entity.EntityLivingBase;
import us.ichun.mods.ichunutil.common.iChunUtil;

import java.util.ArrayList;

public class TrackerRegistry
{
    public final EnumTrackerType type;
    public EntityLivingBase entityToTrack;
    public int length;
    public ArrayList<Class<? extends IAdditionalTrackerInfo>> additionalInfo;
    public ArrayList<EntityInfo> trackedInfo; //0 = newest

    public TrackerRegistry(EnumTrackerType theType, EntityLivingBase tracker, int totalLength)
    {
        type = theType;
        entityToTrack = tracker;
        length = totalLength;
        additionalInfo = new ArrayList<Class<? extends IAdditionalTrackerInfo>>();
        trackedInfo = new ArrayList<EntityInfo>();
    }

    public TrackerRegistry addTracker(Class<? extends IAdditionalTrackerInfo> tracker)
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
                iChunUtil.logger.warn("Error creating tracker: " + tracker.getName());
                e.printStackTrace();
            }
        }
        return this;
    }

    public boolean update()
    {
        if(type.equals(EnumTrackerType.SPECIFIC) && entityToTrack.isDead)
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
                iChunUtil.logger.warn("Error creating trackers: " + tracker.getName());
                e.printStackTrace();
            }
        }

        info.update();

        trackedInfo.add(0, info);

        while(trackedInfo.size() > length)
        {
            trackedInfo.remove(trackedInfo.size() - 1);
        }

        return true;
    }

    public static enum EnumTrackerType
    {
        SPECIFIC,
        PERSISTENT_PLAYER
    }
}

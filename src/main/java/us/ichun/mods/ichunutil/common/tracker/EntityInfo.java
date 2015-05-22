package us.ichun.mods.ichunutil.common.tracker;

import net.minecraft.entity.EntityLivingBase;

import java.util.ArrayList;

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
        rotationPitch = tracked.rotationPitch;

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

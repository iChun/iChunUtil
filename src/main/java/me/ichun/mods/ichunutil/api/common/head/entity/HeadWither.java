package me.ichun.mods.ichunutil.api.common.head.entity;

import me.ichun.mods.ichunutil.api.common.head.HeadInfo;
import net.minecraft.world.entity.boss.wither.WitherBoss;

public class HeadWither extends HeadInfo<WitherBoss>
{
    //These are only here cause of Googly Eyes

    @Override
    public float getHeadYaw(WitherBoss living, float partialTick, int head, int eye)
    {
        if(head >= 0)
        {
            if(head == 0)
            {
                return super.getHeadYaw(living, partialTick, head, eye);
            }
            else
            {
                return living.getHeadYRot(head % 2); //Bear in mind the function is only in the client.
            }
        }
        else
        {
            if(eye <= 1)
            {
                return super.getHeadYaw(living, partialTick, head, eye);
            }
            else
            {
                return living.getHeadYRot(eye <= 3 ? 1 : 0); //Bear in mind the function is only in the client.
            }
        }
    }

    @Override
    public float getHeadPitch(WitherBoss living, float partialTick, int head, int eye)
    {
        if(head >= 0)
        {
            if(head == 0)
            {
                return super.getHeadPitch(living, partialTick, head, eye);
            }
            else
            {
                return living.getHeadXRot(head % 2); //Bear in mind the function is only in the client.
            }
        }
        else
        {
            if(eye <= 1)
            {
                return super.getHeadPitch(living, partialTick, head, eye);
            }
            else
            {
                return living.getHeadXRot(eye <= 3 ? 1 : 0); //Bear in mind the function is only in the client.
            }
        }
    }
}

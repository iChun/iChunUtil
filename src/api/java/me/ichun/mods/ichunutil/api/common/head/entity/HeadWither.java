package me.ichun.mods.ichunutil.api.common.head.entity;

import me.ichun.mods.ichunutil.api.common.head.HeadInfo;
import net.minecraft.entity.boss.WitherEntity;

public class HeadWither extends HeadInfo<WitherEntity>
{
    //These are only here cause of Googly Eyes

    @Override
    public float getHeadYaw(WitherEntity living, float partialTick, int eye, int head)
    {
        if(head >= 0)
        {
            if(head == 0)
            {
                return super.getHeadYaw(living, partialTick, eye, head);
            }
            else
            {
                return living.getHeadYRotation(head % 2); //Bear in mind the function is only in the client.
            }
        }
        else
        {
            if(eye <= 1)
            {
                return super.getHeadYaw(living, partialTick, eye, head);
            }
            else
            {
                return living.getHeadYRotation(eye <= 3 ? 1 : 0); //Bear in mind the function is only in the client.
            }
        }
    }

    @Override
    public float getHeadPitch(WitherEntity living, float partialTick, int eye, int head)
    {
        if(head >= 0)
        {
            if(head == 0)
            {
                return super.getHeadPitch(living, partialTick, eye, head);
            }
            else
            {
                return living.getHeadXRotation(head % 2); //Bear in mind the function is only in the client.
            }
        }
        else
        {
            if(eye <= 1)
            {
                return super.getHeadPitch(living, partialTick, eye, head);
            }
            else
            {
                return living.getHeadXRotation(eye <= 3 ? 1 : 0); //Bear in mind the function is only in the client.
            }
        }
    }
}

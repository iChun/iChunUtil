package me.ichun.mods.ichunutil.common.grab.handlers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import me.ichun.mods.ichunutil.common.grab.GrabHandler;

public class GrabbedFallingBlockHandler implements GrabHandler.GrabbedEntityHandler
{
    @Override
    public boolean eligible(Entity grabbed)
    {
        return grabbed instanceof EntityFallingBlock;
    }

    @Override
    public void handle(GrabHandler grabHandler)
    {
        ((EntityFallingBlock)grabHandler.grabbed).fallTime = 2;
    }
}

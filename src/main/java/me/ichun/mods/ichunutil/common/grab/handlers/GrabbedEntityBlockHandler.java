package me.ichun.mods.ichunutil.common.grab.handlers;

import me.ichun.mods.ichunutil.common.entity.EntityBlock;
import me.ichun.mods.ichunutil.common.grab.GrabHandler;
import net.minecraft.entity.Entity;

public class GrabbedEntityBlockHandler implements GrabHandler.GrabbedEntityHandler
{
    @Override
    public boolean eligible(Entity grabbed)
    {
        return grabbed instanceof EntityBlock;
    }

    @Override
    public void handle(GrabHandler grabHandler)
    {
        ((EntityBlock)grabHandler.grabbed).timeExisting = 2;
    }
}

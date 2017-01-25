package me.ichun.mods.ichunutil.common.grab.handlers;

import me.ichun.mods.ichunutil.common.grab.GrabHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFireball;

public class GrabbedFireballHandler implements GrabHandler.GrabbedEntityHandler
{
    @Override
    public boolean eligible(Entity grabbed)
    {
        return grabbed instanceof EntityFireball;
    }

    @Override
    public void handle(GrabHandler grabHandler)
    {
        EntityFireball fireball = (EntityFireball)grabHandler.grabbed;
        fireball.accelerationX = fireball.accelerationY = fireball.accelerationZ = 0.0D;
    }
}

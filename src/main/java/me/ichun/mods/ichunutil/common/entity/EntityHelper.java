package me.ichun.mods.ichunutil.common.entity;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.common.util.FakePlayer;

import javax.annotation.Nonnull;

public class EntityHelper
{
    public static boolean isFakePlayer(@Nonnull ServerPlayerEntity player) //Fake Players extend ServerPlayerEntity. Please check or cast first
    {
        return player instanceof FakePlayer || player.connection == null; // || player.getName().getUnformattedComponentText().toLowerCase().startsWith("fakeplayer") || player.getName().getUnformattedComponentText().toLowerCase().startsWith("[minecraft]");
    }
}

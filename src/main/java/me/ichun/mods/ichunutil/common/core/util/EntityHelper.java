package me.ichun.mods.ichunutil.common.core.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.UUID;

public class EntityHelper
{
    public static final UUID uuidExample = UUID.fromString("DEADBEEF-DEAD-BEEF-DEAD-DEADBEEFD00D");

    private static HashMap<String, GameProfile> nameToFullProfileMap = new HashMap<String, GameProfile>();


    @SideOnly(Side.CLIENT)
    public static void injectMinecraftPlayerGameProfile()
    {
        nameToFullProfileMap.put(Minecraft.getMinecraft().getSession().getUsername(), Minecraft.getMinecraft().getSession().getProfile());
    }
}

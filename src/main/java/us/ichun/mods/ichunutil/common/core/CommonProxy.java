package us.ichun.mods.ichunutil.common.core;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import us.ichun.mods.ichunutil.client.core.TickHandlerClient;
import us.ichun.mods.ichunutil.client.keybind.KeyBind;
import us.ichun.mods.ichunutil.client.voxel.TrailTicker;
import us.ichun.mods.ichunutil.common.block.BlockCompactPorkchop;
import us.ichun.mods.ichunutil.common.core.network.ChannelHandler;
import us.ichun.mods.ichunutil.common.core.packet.mod.PacketPatientData;
import us.ichun.mods.ichunutil.common.core.packet.mod.PacketPatrons;
import us.ichun.mods.ichunutil.common.core.packet.mod.PacketSession;
import us.ichun.mods.ichunutil.common.core.packet.mod.PacketShowPatronReward;
import us.ichun.mods.ichunutil.common.core.updateChecker.PacketModsList;
import us.ichun.mods.ichunutil.common.core.util.EventCalendar;
import us.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.server.MinecraftServer;

import java.util.HashMap;

public class CommonProxy
{
    public TickHandlerClient tickHandlerClient;
    public TickHandlerServer tickHandlerServer;
    public TrailTicker trailTicker;
    public HashMap<String, String> versionChecker = new HashMap<String, String>();
    public HashMap<String, String> prevVerChecker = new HashMap<String, String>();

    public void preInit()
    {
        EventCalendar.checkDate();

        iChunUtil.blockCompactPorkchop = (new BlockCompactPorkchop()).setCreativeTab(CreativeTabs.tabBlock).setHardness(0.8F).setUnlocalizedName("ichunutil.block.compactporkchop");

        GameRegistry.registerBlock(iChunUtil.blockCompactPorkchop, "compactPorkchop");

        tickHandlerServer = new TickHandlerServer();
        FMLCommonHandler.instance().bus().register(tickHandlerServer);

        iChunUtil.channel = ChannelHandler.getChannelHandlers("iChunUtil", PacketModsList.class, PacketPatrons.class, PacketShowPatronReward.class, PacketSession.class, PacketPatientData.class);
    }

    public void init()
    {
        OreDictionary.registerOre("blockCompactRawPorkchop", iChunUtil.blockCompactPorkchop);
    }

    public void postInit()
    {
        iChunUtil.oreDictBlockCompactRawPorkchop = OreDictionary.getOres("blockCompactRawPorkchop");
    }

    public GameProfileRepository createProfileRepo()
    {
        return FMLCommonHandler.instance().getMinecraftServerInstance().getGameProfileRepository();
    }

    public MinecraftSessionService getSessionService()
    {
        return MinecraftServer.getServer().getMinecraftSessionService();
    }

    /**
     * This is just a proxy method to add a new update notification.
     * Actual update checking should be done by the mod itself.
     * The one supplied with iChunUtil is made to check for updates for my (iChun) mods.
     * Feel free to reference that and write your own.
     * @param modName
     * @param version
     */
    public void notifyNewUpdate(String modName, String version)
    {
        versionChecker.put(modName, version);
        iChunUtil.logger.info("[NEW UPDATE AVAILABLE] " + modName + " - " + version);
    }

    @SideOnly(Side.CLIENT)
    public KeyBind registerKeyBind(KeyBind bind, KeyBind replacing) { return bind; }

    /**
     * Please note that this keybind will trigger without checking for SHIFT/CTRL/ALT being held down. That checking has to be done on your end.
     * @param bind Minecraft Keybind
     */
    @SideOnly(Side.CLIENT)
    public void registerMinecraftKeyBind(KeyBinding bind) {}
}

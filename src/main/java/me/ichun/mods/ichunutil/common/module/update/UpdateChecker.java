package me.ichun.mods.ichunutil.common.module.update;

import com.google.common.collect.ImmutableList;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class UpdateChecker
{
    private static ArrayList<ModVersionInfo> registeredMods = new ArrayList<ModVersionInfo>();
    private static ArrayList<ModVersionInfo> modsWithUpdates = new ArrayList<ModVersionInfo>();

    private static boolean updatesChecked;
    private static boolean requireLogging;

    public static ArrayList<ModVersionInfo> getModsWithUpdates()
    {
        return modsWithUpdates;
    }

    public static void registerMod(ModVersionInfo info)
    {
        if(!registeredMods.contains(info))
        {
            registeredMods.add(info);
            Collections.sort(registeredMods);
        }
    }

    public static void processModsList(Map<String, Object> json)
    {
        for(ModVersionInfo info : registeredMods)
        {
            Map<String, String> versionInfo = (Map<String, String>)json.get(info.modName);
            if(versionInfo != null && versionInfo.containsKey(info.mcVersion))
            {
                ArtifactVersion current = new DefaultArtifactVersion(info.modVersion);

                String lat = versionInfo.get(info.mcVersion);
                ArtifactVersion latest = new DefaultArtifactVersion(lat);

                int diff = latest.compareTo(current);

                if(diff > 0)
                {
                    info.modVersionNew = lat.trim();
                    modsWithUpdates.add(info);
                    if(requireLogging && !info.isModClientOnly)
                    {
                        iChunUtil.LOGGER.info("[NEW UPDATE AVAILABLE] " + info.modName + " - " + info.modVersionNew);
                    }
                }
            }
        }
        Collections.sort(modsWithUpdates);

        requireLogging = false;
        updatesChecked = true;
    }

    public static boolean hasCheckedForUpdates()
    {
        return updatesChecked;
    }

    public static void serverStarted()
    {
        if(UpdateChecker.hasCheckedForUpdates())
        {
            for(UpdateChecker.ModVersionInfo info : UpdateChecker.getModsWithUpdates())
            {
                if(!info.isModClientOnly)
                {
                    iChunUtil.LOGGER.info("[NEW UPDATE AVAILABLE] " + info.modName + " - " + info.modVersionNew);
                }
            }
        }
        else
        {
            requireLogging = true;
        }
    }

    public static class ModVersionInfo
            implements Comparable<ModVersionInfo>
    {
        public final String modName;
        public final String mcVersion;
        public final String modVersion;
        public final boolean isModClientOnly;

        public String modVersionNew;

        public ModVersionInfo(String modName, String mcVersion, String modVersion, boolean isModClientOnly)
        {
            this.modName = modName;
            this.mcVersion = mcVersion;
            this.modVersion = modVersion;
            this.isModClientOnly = isModClientOnly;
        }

        @Override
        public boolean equals(Object o)
        {
            return o instanceof ModVersionInfo && ((ModVersionInfo)o).modName.equals(modName);
        }

        @Override
        public int compareTo(ModVersionInfo o)
        {
            return modName.compareTo(o.modName);
        }
    }
}

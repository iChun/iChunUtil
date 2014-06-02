package ichun.common.core.updateChecker;

import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.versioning.DefaultArtifactVersion;

import java.util.Map;

public class ModVersionInfo
{
    public final String modName;
    public final String mcVersion;
    public final String modVersion;
    public final boolean sided; //sided = true means the mod can be installed on only ONE side. Normally only the client.

    public String newModVersion;

    public ModVersionInfo(String modName, String mcVersion, String modVersion, boolean sided)
    {
        this.modName = modName;
        this.mcVersion = mcVersion;
        this.modVersion = modVersion;
        this.sided = sided;
        newModVersion = modVersion;
    }

    /**
     * You want to override this function unless you're using the exact same JSON format as me.
     * @param map JSON file object
     */
    public boolean processAndReturnHasUpdate(Map<String, String> map)
    {
        ArtifactVersion current = new DefaultArtifactVersion(modVersion);

        String lat = map.get(mcVersion);
        ArtifactVersion latest = new DefaultArtifactVersion(lat);

        int diff = latest.compareTo(current);

        if(diff > 0)
        {
            newModVersion = lat.trim();
            return true;
        }
        return false;
    }

    public boolean isVersionOutdated(String version)
    {
        ArtifactVersion test = new DefaultArtifactVersion(version);

        ArtifactVersion latest = new DefaultArtifactVersion(newModVersion);

        int diff = latest.compareTo(test);

        return diff > 0 || !modVersion.equals(newModVersion);
    }
}

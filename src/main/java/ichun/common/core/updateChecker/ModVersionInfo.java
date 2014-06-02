package ichun.common.core.updateChecker;

import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.versioning.DefaultArtifactVersion;

import java.util.Map;

import static net.minecraftforge.common.ForgeVersion.Status.AHEAD;
import static net.minecraftforge.common.ForgeVersion.Status.OUTDATED;
import static net.minecraftforge.common.ForgeVersion.Status.UP_TO_DATE;

public class ModVersionInfo
{
    public final String modName;
    public final String mcVersion;
    public final String modVersion;
    public final boolean sided; //sided = true means the mod can be installed on only ONE side.

    public String newModVersion;

    public ModVersionInfo(String modName, String mcVersion, String modVersion, boolean sided)
    {
        this.modName = modName;
        this.mcVersion = mcVersion;
        this.modVersion = modVersion;
        this.sided = sided;
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
}

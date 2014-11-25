package us.ichun.module.tabula.client.formats;

import us.ichun.module.tabula.client.formats.types.ImportTabula;
import us.ichun.module.tabula.client.formats.types.Importer;
import us.ichun.module.tabula.common.project.ProjectInfo;

import java.io.File;
import java.util.HashMap;

public class ImportList
{
    public static final ImportTabula tabulaImporterInstance = new ImportTabula();
    public static final HashMap<String, Importer> compatibleFormats = new HashMap<String, Importer>() {{
        put("tcn", tabulaImporterInstance);
        put("tc2", tabulaImporterInstance);
        put("tbl", tabulaImporterInstance);
    }};

    public static boolean isFileSupported(File file)
    {
        return compatibleFormats.containsKey(file.getName().substring(file.getName().length() - 3, file.getName().length()));
    }

    public static ProjectInfo createProjectFromFile(File file)
    {
        if(compatibleFormats.containsKey(file.getName().substring(file.getName().length() - 3, file.getName().length())))
        {
            return compatibleFormats.get(file.getName().substring(file.getName().length() - 3, file.getName().length())).createProjectInfo(file);
        }
        return null;
    }
}

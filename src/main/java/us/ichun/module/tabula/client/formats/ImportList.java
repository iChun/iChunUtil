package us.ichun.module.tabula.client.formats;

import org.apache.commons.io.FilenameUtils;
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
        return compatibleFormats.containsKey(FilenameUtils.getExtension(file.getName()));
    }

    public static ProjectInfo createProjectFromFile(File file)
    {
        if(compatibleFormats.containsKey(FilenameUtils.getExtension(file.getName())))
        {
            return compatibleFormats.get(FilenameUtils.getExtension(file.getName())).createProjectInfo(file);
        }
        return null;
    }
}

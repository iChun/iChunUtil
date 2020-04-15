package me.ichun.mods.ichunutil.common.module.tabula.formats;

import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.module.tabula.formats.types.Importer;
import me.ichun.mods.ichunutil.common.module.tabula.formats.types.ImporterTabula;
import me.ichun.mods.ichunutil.common.module.tabula.project.Project;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.HashMap;

public class ImportList
{
    public static final ImporterTabula IMPORTER_TABULA = new ImporterTabula();
    public static final HashMap<String, Importer> COMPATIBLE_FORMATS = new HashMap<String, Importer>() {{
        put("tcn", IMPORTER_TABULA);
        put("tc2", IMPORTER_TABULA);
        put("tbl", IMPORTER_TABULA);
    }};

    public static boolean isFileSupported(File file)
    {
        return !file.isDirectory() && COMPATIBLE_FORMATS.containsKey(FilenameUtils.getExtension(file.getName()));
    }

    public static Project createProjectFromFile(File file)
    {
        if(COMPATIBLE_FORMATS.containsKey(FilenameUtils.getExtension(file.getName())))
        {
            Importer importer = COMPATIBLE_FORMATS.get(FilenameUtils.getExtension(file.getName()));
            try
            {
                Project project = importer.createProject(file);
                if(project != null)
                {
                    project.projVersion = importer.getProjectVersion();
                    project.repair();
                    return project;
                }
            }
            catch(Exception e)
            {
                iChunUtil.LOGGER.warn("Error creating Project for format {} for file {} by importer {}", FilenameUtils.getExtension(file.getName()), file.getAbsolutePath(), importer);
            }
        }
        else
        {
            iChunUtil.LOGGER.warn("Error creating Project for file {}. No importer assigned.", file.getAbsolutePath());
        }
        return null;
    }

    /**
     * Use this method if you want to register your importer for Tabula reading/use.
     * @param format File extension format you want to parse.
     * @param importer Importer object to manage the file.
     * @return false if importer format has been registered
     */
    public static boolean registerImporter(String format, Importer importer)
    {
        if(COMPATIBLE_FORMATS.containsKey(format))
        {
            iChunUtil.LOGGER.warn("Format {} has already been registered with {}. Unable to register with importer {}", format, COMPATIBLE_FORMATS.get(format), importer);
            return false;
        }
        iChunUtil.LOGGER.info("Registered importer {} with format {}", importer, format);
        COMPATIBLE_FORMATS.put(format, importer);
        return true;
    }
}

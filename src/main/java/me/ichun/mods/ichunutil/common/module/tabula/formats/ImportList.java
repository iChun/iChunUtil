package me.ichun.mods.ichunutil.common.module.tabula.formats;

import org.apache.commons.io.FilenameUtils;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.module.tabula.formats.types.Importer;
import me.ichun.mods.ichunutil.common.module.tabula.project.ProjectInfo;
import me.ichun.mods.ichunutil.common.module.tabula.formats.types.ImportTabula;

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
        return !file.isDirectory() && compatibleFormats.containsKey(FilenameUtils.getExtension(file.getName()));
    }

    public static ProjectInfo createProjectFromFile(File file)
    {
        if(compatibleFormats.containsKey(FilenameUtils.getExtension(file.getName())))
        {
            Importer importer = compatibleFormats.get(FilenameUtils.getExtension(file.getName()));
            try
            {
                ProjectInfo projectInfo = importer.createProjectInfo(file);
                projectInfo.projVersion = importer.getProjectVersion();
                projectInfo.repair();
                return projectInfo;
            }
            catch(Exception e)
            {
                StringBuilder sb = new StringBuilder();
                sb.append("Error creating Project for format ").append(FilenameUtils.getExtension(file.getName())).append(" for file ").append(file.getAbsolutePath()).append(" by importer ").append(importer);
                iChunUtil.LOGGER.warn(sb.toString());
                return null;
            }
        }
        return null;
    }

    /**
     * Use this method if you want to register your importer for Tabula reading/use.
     * @param format
     * @param importer
     * @return false if importer format has been registered
     */
    public static boolean registerImporter(String format, Importer importer)
    {
        if(compatibleFormats.containsKey(format))
        {
            return false;
        }
        compatibleFormats.put(format, importer);
        return true;
    }
}

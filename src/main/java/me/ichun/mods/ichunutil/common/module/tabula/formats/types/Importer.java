package me.ichun.mods.ichunutil.common.module.tabula.formats.types;

import me.ichun.mods.ichunutil.common.module.tabula.project.Project;

import javax.annotation.Nullable;
import java.io.File;

public interface Importer
{
    /**
     * Parse a file into a Project
     * @param file File to read
     * @return Project object created from file
     */
    @Nullable
    Project createProject(File file);

    /**
     * The Project version that this importer creates. It is used for Tabula to attempt to "repair" old project files.
     * @return Project Version
     */
    int getProjectVersion();
}

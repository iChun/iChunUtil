package us.ichun.module.tabula.client.formats.types;

import us.ichun.module.tabula.common.project.ProjectInfo;

import java.io.File;

public abstract class Importer
{
    public abstract ProjectInfo createProjectInfo(File file);
}

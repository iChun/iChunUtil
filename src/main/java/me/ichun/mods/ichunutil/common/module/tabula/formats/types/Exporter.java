package me.ichun.mods.ichunutil.common.module.tabula.formats.types;

import me.ichun.mods.ichunutil.client.gui.bns.Workspace;
import me.ichun.mods.ichunutil.common.module.tabula.project.Project;


public abstract class Exporter
{
    public final String name;

    public Exporter(String name)
    {
        this.name = name;
    }

    public abstract String getId();

    public abstract boolean export(Project project, Object...params);

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    public boolean override(Workspace workspace, Project project)
    {
        return false;
    }
}

package me.ichun.mods.ichunutil.common.module.tabula.formats.types;

import me.ichun.mods.ichunutil.client.gui.bns.Workspace;
import me.ichun.mods.ichunutil.common.module.tabula.project.Project;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class Exporter
{
    public final String name;

    public Exporter(String name)
    {
        this.name = name;
    }

    public abstract String getId();

    public abstract boolean export(Project project, Object...params);

    @OnlyIn(Dist.CLIENT)
    public boolean override(Workspace workspace, Project project)
    {
        return false;
    }
}

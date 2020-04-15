package me.ichun.mods.ichunutil.common.module.tabula.project;

import org.apache.commons.lang3.RandomStringUtils;

import javax.annotation.Nonnull;

public abstract class Identifiable<T>
{
    @Nonnull
    public String identifier = RandomStringUtils.randomAscii(Project.IDENTIFIER_LENGTH);
    public transient Identifiable<?> parent = null; //TODO should this be transient?

    public String name = "";

    public abstract Identifiable getById(String id);
    public abstract String getJsonWithoutChildren();
    public abstract void transferChildren(T clone);
    public abstract void adoptChildren();

    public void markDirty()
    {
        if(parent != null)
        {
            parent.markDirty();
        }
    }
}

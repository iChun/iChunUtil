package me.ichun.mods.ichunutil.common.module.tabula.project;

import org.apache.commons.lang3.RandomStringUtils;

import javax.annotation.Nonnull;

public abstract class Identifiable<T>
{
    @Nonnull
    public String identifier = RandomStringUtils.randomAscii(Project.IDENTIFIER_LENGTH);
    public transient String parentIdent = null; //TODO should this be transient?

    public String name = "";

    public abstract Identifiable getById(String id);
    public abstract String getJsonWithoutChildren();
    public abstract void transferChildren(T clone);

    public void markDirty()
    {
        Identifiable parent = getById(parentIdent);
        if(parent != null)
        {
            parent.markDirty();
        }
    }
}

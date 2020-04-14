package me.ichun.mods.ichunutil.common.module.tabula.project;

import org.apache.commons.lang3.RandomStringUtils;

public abstract class Identifiable<T>
{
    public String identifier = RandomStringUtils.randomAscii(Project.IDENTIFIER_LENGTH);
    public transient String parentIdent = null; //TODO should this be transient?


    public abstract Identifiable getById(String id);
    public abstract String getJsonWithoutChildren();
    public abstract void transferChildren(T clone);
}

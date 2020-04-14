package me.ichun.mods.ichunutil.common.module.tabula.project;

import org.apache.commons.lang3.RandomStringUtils;

public abstract class Identifiable
{
    public String identifier = RandomStringUtils.randomAscii(Project.IDENTIFIER_LENGTH);
    public String parentIdent = null;

    public abstract Identifiable getById(String id);
}

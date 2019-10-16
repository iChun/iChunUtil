package me.ichun.mods.ichunutil.client.gui.bns.impl;

import me.ichun.mods.ichunutil.client.gui.bns.window.Window;
import me.ichun.mods.ichunutil.client.gui.bns.window.constraint.Constraint;

public class WindowTest extends Window<WorkspaceTest>
{
    public WindowTest(WorkspaceTest parent)
    {
        super(parent, "ALSKDJ yo title");
        setView(new ViewTest(this));
        setPos(25, 25);
        setSize(400, 100);
        setConstraint(new Constraint(this).left(parent, Constraint.Property.Type.LEFT, 25).top(parent, Constraint.Property.Type.TOP, 25));
    }
}

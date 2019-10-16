package me.ichun.mods.ichunutil.client.gui.bns.impl;

import me.ichun.mods.ichunutil.client.gui.bns.window.constraint.Constraint;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.View;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ButtonElement;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.Element;

import javax.annotation.Nonnull;

public class ViewTest extends View<WindowTest>
{
    public ViewTest(@Nonnull WindowTest parent)
    {
        super(parent);
        Element e = new ButtonElement(this, "test string");
        e.setConstraint(new Constraint(e).left(this, Constraint.Property.Type.LEFT, 20)
                .top(this, Constraint.Property.Type.TOP, 20)
                .min(() -> 40, () -> renderMinecraftStyle() ? 20 : 16)
                .max(() -> 10000, () -> renderMinecraftStyle() ? 20 : 10000)
        );
        elements.add(e);

        e = new ButtonElement(this, "test string 2");
        e.setConstraint(new Constraint(e).left(this, Constraint.Property.Type.LEFT, 20).bottom(this, Constraint.Property.Type.BOTTOM, 20).min(() -> 40, () -> renderMinecraftStyle() ? 20 : 16).max(() -> 10000, () -> renderMinecraftStyle() ? 20 : 10000));
        elements.add(e);

        e = new ButtonElement(this, "test string 3");
        e.setConstraint(new Constraint(e).right(this, Constraint.Property.Type.RIGHT, 20).top(this, Constraint.Property.Type.TOP, 20).min(() -> 40, () -> renderMinecraftStyle() ? 20 : 16).max(() -> 10000, () -> renderMinecraftStyle() ? 20 : 10000));
        elements.add(e);

        e = new ButtonElement(this, "test string 4");
        e.setConstraint(new Constraint(e).right(this, Constraint.Property.Type.RIGHT, 20).bottom(this, Constraint.Property.Type.BOTTOM, 20).min(() -> 40, () -> renderMinecraftStyle() ? 20 : 16).max(() -> 10000, () -> renderMinecraftStyle() ? 20 : 10000));
        elements.add(e);

        e = new ButtonElement(this, "test string 5");
        e.setWidth(100);
        e.setConstraint(new Constraint(e));
        elements.add(e);

        //TODO test both constraints on one axis
    }
}

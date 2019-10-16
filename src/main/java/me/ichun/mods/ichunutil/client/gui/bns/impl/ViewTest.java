package me.ichun.mods.ichunutil.client.gui.bns.impl;

import me.ichun.mods.ichunutil.client.gui.bns.window.constraint.Constraint;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.View;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.Element;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementButton;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementButtonTextured;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementToggle;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class ViewTest extends View<WindowTest>
{
    public ViewTest(@Nonnull WindowTest parent, @Nonnull String title)
    {
        super(parent, title);
        Element e = new ElementButton(this, "test string");
        e.setWidth(40);
        e.setConstraint(new Constraint(e).left(this, Constraint.Property.Type.LEFT, 20)
                .top(this, Constraint.Property.Type.TOP, 20)
        );
        elements.add(e);

        e = new ElementButton(this, "test string 2");
        e.setWidth(40);
        e.setConstraint(new Constraint(e).left(this, Constraint.Property.Type.LEFT, 20).bottom(this, Constraint.Property.Type.BOTTOM, 20));
        elements.add(e);

        e = new ElementButton(this, "test string 3");
        e.setWidth(40);
        e.setConstraint(new Constraint(e).right(this, Constraint.Property.Type.RIGHT, 20).top(this, Constraint.Property.Type.TOP, 20));
        elements.add(e);

        e = new ElementToggle(this, "test string 4");
        e.setWidth(60);
        e.setConstraint(new Constraint(e).right(this, Constraint.Property.Type.RIGHT, 20).bottom(this, Constraint.Property.Type.BOTTOM, 20));
        elements.add(e);

        e = new ElementButtonTextured(this, "test string 5", new ResourceLocation("textures/item/gold_ingot.png"));
        e.setWidth(100);
        e.setConstraint(new Constraint(e).top(this, Constraint.Property.Type.TOP, 40).bottom(this, Constraint.Property.Type.BOTTOM, 40));
        elements.add(e);

        //TODO test both constraints on one axis
    }
}

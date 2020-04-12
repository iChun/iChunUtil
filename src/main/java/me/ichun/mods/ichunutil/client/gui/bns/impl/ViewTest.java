package me.ichun.mods.ichunutil.client.gui.bns.impl;

import me.ichun.mods.ichunutil.client.gui.bns.window.constraint.Constraint;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.View;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.*;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class ViewTest extends View<WindowTest>
{
    public ViewTest(@Nonnull WindowTest parent, @Nonnull String title)
    {
        super(parent, title);
        Element<?> e;

        e = new ElementButton(this, "test string");
        e.setWidth(40);
        e.setHeight(20);
        e.setConstraint(new Constraint(e).left(this, Constraint.Property.Type.LEFT, 20)
                .top(this, Constraint.Property.Type.TOP, 20)
        );
        elements.add(e);

        e = new ElementCheckbox(this, "test string 2", (button) -> {});
        e.setConstraint(new Constraint(e).left(this, Constraint.Property.Type.LEFT, 20).bottom(this, Constraint.Property.Type.BOTTOM, 20));
        elements.add(e);

        e = new ElementButton(this, "test string 3");
        e.setWidth(40);
        e.setConstraint(new Constraint(e).right(this, Constraint.Property.Type.RIGHT, 20).top(this, Constraint.Property.Type.TOP, 20));
        elements.add(e);

        e = new ElementToggle(this, "test string 4");
        e.setWidth(60);
        e.setConstraint(new Constraint(e).right(this, Constraint.Property.Type.RIGHT, 20).bottom(this, Constraint.Property.Type.BOTTOM, 40));
        elements.add(e);

        e = new ElementButtonTextured(this, "test string 5", new ResourceLocation("textures/item/gold_ingot.png"));
        e.setWidth(100);
        e.setConstraint(new Constraint(e).top(this, Constraint.Property.Type.TOP, 40).bottom(this, Constraint.Property.Type.BOTTOM, 40)
                .left(this, Constraint.Property.Type.LEFT, 20).right(this, Constraint.Property.Type.RIGHT, 20)
        );
        elements.add(e);

        e = new ElementNumberInput(this, true).setMax(60000).setMin(-3723998).setMaxDec(4).setDefaultText("123");
        e.setWidth(100);
        e.setConstraint(new Constraint(e).left(this, Constraint.Property.Type.LEFT, 30)
                .right(this, Constraint.Property.Type.RIGHT, 30).bottom(this, Constraint.Property.Type.BOTTOM, 20)
//                .top(this, Constraint.Property.Type.TOP, 40)
        );
        elements.add(e);

        e = new ElementScrollBar(this, ElementScrollBar.Orientation.VERTICAL, 0.6F);
        e.setConstraint(new Constraint(e).top(this, Constraint.Property.Type.TOP, 0)
                .bottom(this, Constraint.Property.Type.BOTTOM, 0)
                .right(this, Constraint.Property.Type.RIGHT, 0)
        );
        elements.add(e);

//        ElementScrollBar sv = new ElementScrollBar(this, ElementScrollBar.Orientation.VERTICAL, 0.6F);
//        sv.setConstraint(new Constraint(sv).top(this, Constraint.Property.Type.TOP, 0)
//                .bottom(this, Constraint.Property.Type.BOTTOM, 0)
//                .right(this, Constraint.Property.Type.RIGHT, 0)
//        );
//        elements.add(sv);
//
//        ElementScrollBar sh = new ElementScrollBar(this, ElementScrollBar.Orientation.HORIZONTAL, 0.6F);
//        sh.setConstraint(new Constraint(sh).left(this, Constraint.Property.Type.LEFT, 0)
//                .bottom(this, Constraint.Property.Type.BOTTOM, 0)
//                .right(sv, Constraint.Property.Type.LEFT, 0)
//        );
//        elements.add(sh);
//
//
//        ElementList list = new ElementList(this).setScrollVertical(sv).setScrollHorizontal(sh).setDragHandler((i, j) -> {})
//                .setRearrangeHandler((i, j) -> {})
//                ;
//        list.setConstraint(new Constraint(list).left(this, Constraint.Property.Type.LEFT, 0)
//                .bottom(sh, Constraint.Property.Type.TOP, 0)
//                .top(this, Constraint.Property.Type.TOP, 0)
//                .right(sv, Constraint.Property.Type.LEFT, 0)
//        );
//        ElementList.Item item = list.addItem("Test");
//        ElementTextWrapper wrapper = new ElementTextWrapper(item).setText(Arrays.asList(("<lululandd> farLurk farHype farLork\n" + "<lululandd> jasjawSmile\n" + "<lululandd> jasjawSmile jasjawSmile\n" + "<lululandd> jasjawSmile jasjawSmile jasjawSmile\n" + "<lululandd> jasjawSmile jasjawSmile jasjawSmile jasjawSmile\n" + "<lululandd> jasjawSmile jasjawSmile jasjawSmile jasjawSmile jasjawSmile\n" + "<lululandd> jasjawSmile jasjawSmile jasjawSmile jasjawSmile\n" + "<lululandd> jasjawSmile jasjawSmile jasjawSmile\n" + "<lululandd> jasjawSmile jasjawSmile\n" + "<lululandd> jasjawSmile\n" + "<lululandd> damn son\n" + "<lululandd> ok bye").split("\n")));
//        wrapper.setConstraint(Constraint.matchParent(wrapper, item, item.getBorderSize()).top(item, Constraint.Property.Type.TOP, item.getBorderSize()).bottom(null, Constraint.Property.Type.BOTTOM, 0));
//        item.addElement(wrapper);
//        for(int i = 1; i <= 12; i++)
//        {
//            list.addItem("Item numbah " + i);
//        }
//        elements.add(list);

//        e = new ElementScrollBar(this, ElementScrollBar.Orientation.HORIZONTAL, 0.6F, (e1) -> {});
//        e.setConstraint(new Constraint(e).left(this, Constraint.Property.Type.LEFT, 0)
//                .bottom(this, Constraint.Property.Type.BOTTOM, 0)
//                .right(this, Constraint.Property.Type.RIGHT, 0)
//        );
//        elements.add(e);
    }
}

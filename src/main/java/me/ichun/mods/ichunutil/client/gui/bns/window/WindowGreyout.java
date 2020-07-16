package me.ichun.mods.ichunutil.client.gui.bns.window;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import me.ichun.mods.ichunutil.client.gui.bns.Workspace;
import me.ichun.mods.ichunutil.client.gui.bns.window.constraint.Constraint;
import me.ichun.mods.ichunutil.client.render.RenderHelper;
import net.minecraft.client.Minecraft;

import java.util.function.Consumer;

public class WindowGreyout<M extends Workspace> extends Window<M>
{
    public Window<?> attachedWindow;
    public Consumer<WindowGreyout<M>> closeConsumer;

    public WindowGreyout(M parent, Window<?> attached)
    {
        super(parent);
        this.attachedWindow = attached;
        size(parent.getWidth(), parent.getHeight());
        setConstraint(Constraint.matchParent(this, parent, 0));
        borderSize = () -> 0;
        titleSize = () -> 0;

        disableBringToFront();
        disableDocking();
        disableDockStacking();
        disableUndocking();
        disableDrag();
        disableDragResize();
        disableTitle();
        isNotUnique();
    }

    public WindowGreyout<M> setCloseConsumer(Consumer<WindowGreyout<M>> closeConsumer)
    {
        this.closeConsumer = closeConsumer;
        return this;
    }

    @Override
    public void init()
    {
        constraint.apply();
    }

    @Override
    public void resize(Minecraft mc, int width, int height)
    {
        constraint.apply();
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTick)
    {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderHelper.drawColour(0, 0, 0, 150, getLeft(), getTop(), width, height, 0);
        RenderSystem.disableBlend();
        if(!parent.children().contains(attachedWindow))
        {
            parent.removeWindow(this);
            if(closeConsumer != null)
            {
                closeConsumer.accept(this);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if(isMouseOver(mouseX, mouseY))
        {
            parent.removeWindow(attachedWindow);
            parent.removeWindow(this);
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_)
    {
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers)
    {
        return false;
    }
}

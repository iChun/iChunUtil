package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import me.ichun.mods.ichunutil.client.gui.bns.window.Fragment;
import me.ichun.mods.ichunutil.client.gui.bns.window.WindowContextMenu;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class ElementContextMenu extends Element
        implements WindowContextMenu.IContextMenu
{
    public final @Nonnull List<Object> contextMenuObjects;
    public final @Nonnull BiConsumer<WindowContextMenu.IContextMenu, ElementList.Item<?>> contextMenuReceiver;
    public @Nonnull Function<Object, String> nameProvider = Object::toString;
    public boolean lmbTriggers = false;

    public ElementContextMenu(@Nonnull Fragment parent, @Nonnull List<Object> contextMenuObjects, @Nonnull BiConsumer<WindowContextMenu.IContextMenu, ElementList.Item<?>> contextMenuReceiver)
    {
        super(parent);
        this.contextMenuObjects = contextMenuObjects;
        this.contextMenuReceiver = contextMenuReceiver;
    }

    public ElementContextMenu setNameProvider(Function<Object, String> nameProvider)
    {
        this.nameProvider = nameProvider;
        return this;
    }

    public ElementContextMenu lmbTriggers()
    {
        lmbTriggers = true;
        return this;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY)
    {
        return super.isMouseOver(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if(isMouseOver(mouseX, mouseY) && (button == GLFW.GLFW_MOUSE_BUTTON_LEFT && lmbTriggers || button == 1))
        {
            return super.mouseClicked(mouseX, mouseY, button);
        }
        return false; //don't capture the click, let it pass on
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        if(isMouseOver(mouseX, mouseY))
        {
            WindowContextMenu.create(getWorkspace(), this, mouseX + 10, mouseY + 10, (int)(parentFragment.width * 0.8F), -20);
        }
        return false; // don't capture the click, let it pass
    }

    @Override
    public boolean changeFocus(boolean direction)
    {
        return false;
    }

    @Nonnull
    @Override
    public List<Object> getObjects()
    {
        return contextMenuObjects;
    }

    @Nonnull
    @Override
    public BiConsumer<WindowContextMenu.IContextMenu, ElementList.Item<?>> getReceiver()
    {
        return contextMenuReceiver;
    }

    @Nonnull
    @Override
    public Function<Object, String> getNameProvider()
    {
        return nameProvider;
    }

}

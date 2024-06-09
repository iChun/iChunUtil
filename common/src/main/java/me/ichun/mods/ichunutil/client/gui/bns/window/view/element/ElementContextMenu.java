package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import me.ichun.mods.ichunutil.client.gui.bns.Fragment;
import me.ichun.mods.ichunutil.client.gui.bns.window.WindowContextMenu;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class ElementContextMenu extends Element<Fragment<?>>
        implements WindowContextMenu.IContextMenu
{
    public final @NotNull List<?> contextMenuObjects;
    public final @NotNull BiConsumer<WindowContextMenu.IContextMenu, ElementList.Item<?>> contextMenuReceiver;
    public @NotNull Function<Object, String> nameProvider = Object::toString;
    public boolean lmbTriggers = false;

    public ElementContextMenu(@NotNull Fragment<?> parent, @NotNull List<?> contextMenuObjects, @NotNull BiConsumer<WindowContextMenu.IContextMenu, ElementList.Item<?>> contextMenuReceiver)
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
            WindowContextMenu.create(getWorkspace(), this, mouseX + 10, mouseY + 10, (int)(parent.width * 0.8F), -20);
        }
        return false; // don't capture the click, let it pass
    }

    @Override
    public ComponentPath nextFocusPath(FocusNavigationEvent event)
    {
        return null;
    }

    @NotNull
    @Override
    public List<?> getObjects()
    {
        return contextMenuObjects;
    }

    @NotNull
    @Override
    public BiConsumer<WindowContextMenu.IContextMenu, ElementList.Item<?>> getReceiver()
    {
        return contextMenuReceiver;
    }

    @NotNull
    @Override
    public Function<Object, String> getNameProvider()
    {
        return nameProvider;
    }

}

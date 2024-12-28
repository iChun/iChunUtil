package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import me.ichun.mods.ichunutil.client.gui.bns.Fragment;
import me.ichun.mods.ichunutil.client.gui.bns.window.WindowContextMenu;
import me.ichun.mods.ichunutil.common.util.StringUtil;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * This is an invisible element that is meant to overlay another element and opens a new WindowContextMenu when clicked
 * @param <I> Class type of items in list.
 */
public class ElementContextMenu<I> extends Element<Fragment<?>>
        implements WindowContextMenu.IContextMenu<ElementContextMenu<I>, I>
{
    public final @NotNull List<I> contextMenuObjects;
    public final @NotNull BiConsumer<ElementContextMenu<I>, ElementList.Item<I>> contextMenuReceiver;
    public @NotNull Function<I, List<String>> nameProvider = StringUtil::getInterpretedInfo;
    public boolean lmbTriggers = false;

    public ElementContextMenu(@NotNull Fragment<?> parent, @NotNull List<I> contextMenuObjects, @NotNull BiConsumer<ElementContextMenu<I>, ElementList.Item<I>> contextMenuReceiver)
    {
        super(parent);
        this.contextMenuObjects = contextMenuObjects;
        this.contextMenuReceiver = contextMenuReceiver;
    }

    public ElementContextMenu<I> setNameProvider(Function<I, List<String>> nameProvider)
    {
        this.nameProvider = nameProvider;
        return this;
    }

    public ElementContextMenu<I> lmbTriggers()
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
    public List<I> getObjects()
    {
        return contextMenuObjects;
    }

    @NotNull
    @Override
    public BiConsumer<ElementContextMenu<I>, ElementList.Item<I>> getReceiver()
    {
        return contextMenuReceiver;
    }

    @NotNull
    @Override
    public Function<I, List<String>> getNameProvider()
    {
        return nameProvider;
    }

}

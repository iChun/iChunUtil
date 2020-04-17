package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import me.ichun.mods.ichunutil.client.gui.bns.window.Fragment;
import me.ichun.mods.ichunutil.client.gui.bns.window.WindowContextMenu;

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

    @Override
    public boolean isMouseOver(double mouseX, double mouseY)
    {
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        return false; //don't capture the click, let it pass on
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        WindowContextMenu.create(getWorkspace(), this, mouseX + 10, mouseY + 10, (int)(parentFragment.width * 0.8F), -20);
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

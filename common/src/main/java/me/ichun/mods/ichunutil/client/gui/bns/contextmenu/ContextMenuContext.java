package me.ichun.mods.ichunutil.client.gui.bns.contextmenu;

import me.ichun.mods.ichunutil.client.gui.bns.Fragment;
import me.ichun.mods.ichunutil.client.gui.bns.window.WindowContextMenu;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class ContextMenuContext<I> implements IContextMenu<ContextMenuContext<I>, I>
{
    public @NotNull Fragment<?> parent;
    public final @NotNull List<I> contextMenuObjects;
    public final @NotNull BiConsumer<ContextMenuContext<I>, ElementList.Item<I>> contextMenuReceiver;
    public @Nullable Function<I, List<String>> nameProvider = null;

    public ContextMenuContext(Fragment<?> parent, @NotNull List<I> contextMenuObjects, @NotNull BiConsumer<ContextMenuContext<I>, ElementList.Item<I>> contextMenuReceiver)
    {
        this.parent = parent;
        this.contextMenuObjects = contextMenuObjects;
        this.contextMenuReceiver = contextMenuReceiver;
    }

    public ContextMenuContext<I> setParent(Fragment<?> fragment)
    {
        this.parent = fragment;
        return this;
    }

    public ContextMenuContext<I> setNameProvider(Function<I, List<String>> nameProvider)
    {
        this.nameProvider = nameProvider;
        return this;
    }

    @Override
    public @NotNull List<I> getObjects()
    {
        return contextMenuObjects;
    }

    @Override
    public @NotNull BiConsumer<ContextMenuContext<I>, ElementList.Item<I>> getReceiver()
    {
        return contextMenuReceiver;
    }

    @Override
    public @NotNull Function<I, List<String>> getNameProvider()
    {
        return nameProvider != null ? nameProvider : IContextMenu.super.getNameProvider();
    }

    public void create(double mouseX, double mouseY)
    {
        WindowContextMenu.create(parent.getWorkspace(), this, mouseX + 10, mouseY + 10, (int)(parent.width * 0.8F), -20);
    }
}

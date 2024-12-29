package me.ichun.mods.ichunutil.client.gui.bns.window;

import me.ichun.mods.ichunutil.client.gui.bns.Workspace;
import me.ichun.mods.ichunutil.client.gui.bns.constraint.Constraint;
import me.ichun.mods.ichunutil.client.gui.bns.contextmenu.IContextMenu;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.View;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementList;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementScrollBar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

@SuppressWarnings("unchecked")
public class WindowContextMenu<M extends Workspace, I> extends Window<M, View<?>>
{
    private final ElementList<ViewContextMenu, I> list;
    private int minWidth = 1;
    private boolean killed;

    private WindowContextMenu(M parent)
    {
        super(parent);

        setBorderSize(() -> 1);
        setView(new ViewContextMenu(this, ""));

        list = (ElementList<ViewContextMenu, I>)getCurrentView().elements.get(1);

        disableTitle();
        disableDocking();
        disableDockStacking();
        disableDrag();
        disableDragResize();
        disableUndocking();
    }

    private ElementList<ViewContextMenu, I> getList()
    {
        return list;
    }

    private void setupAround(double posX, double posY, int minWidth, int yFlipHeight) //this sets the placement of the window.
    {
        this.width = this.minWidth = minWidth;
        resize(Minecraft.getInstance(), parent.getWidth(), parent.getHeight());
        this.height = list.getTotalItemHeight() + (borderSize.get() * 2) + (list.getBorderSize() * 2);

        int pX = (int)posX;
        int pY = (int)posY;
        int screenWidth = parent.getWidth();
        int screenHeight = parent.getHeight();

        int tooltipX = pX;
        if (tooltipX + minWidth > screenWidth) // if it can't go to the right
        {
            tooltipX = pX - minWidth;
        }

        int tooltipY = pY;
        int tooltipHeight = height;

        if (tooltipY < 0)
        {
            tooltipY = 0;
        }
        else if (tooltipY + tooltipHeight > screenHeight) //too big to go down
        {
            if(tooltipHeight > pY - yFlipHeight) //we can't flip either
            {
                this.height = tooltipHeight = Math.min(Math.max(pY - yFlipHeight, screenHeight - tooltipY) - 2, list.getTotalItemHeight() + (borderSize.get() * 2) + (list.getBorderSize() * 2));
            }
            if (tooltipY + tooltipHeight > screenHeight) //still too big to go down
            {
                tooltipY = pY - yFlipHeight - tooltipHeight;
                if(tooltipY < 0)
                {
                    tooltipY = screenHeight - tooltipHeight; // just dump it anywhere?
                }
            }
        }
        this.constraint.apply();

        pos(tooltipX, tooltipY);

        resize(Minecraft.getInstance(), parent.getWidth(), parent.getHeight());
    }

    @Override
    public int getMinWidth()
    {
        return minWidth;
    }

    @Override
    public void unfocus(@Nullable GuiEventListener guiReplacing)
    {
        super.unfocus(guiReplacing);
        if(!killed)
        {
            killed = true;
            parent.removeWindow(this);
        }
    }

    private class ViewContextMenu extends View<WindowContextMenu<M, I>>
    {
        public ViewContextMenu(@NotNull WindowContextMenu<M, I> parent, @NotNull String s)
        {
            super(parent, s);

            ElementScrollBar sv = new ElementScrollBar(this, ElementScrollBar.Orientation.VERTICAL, 0.6F);
            sv.setConstraint(new Constraint(sv).top(this, Constraint.Property.Type.TOP, 0)
                .bottom(this, Constraint.Property.Type.BOTTOM, 0)
                .right(this, Constraint.Property.Type.RIGHT, 0)
            );
            elements.add(sv);

            ElementList<ViewContextMenu, I> list = new ElementList<>(this);
            list.setScrollVertical(sv)
                .setConstraint(new Constraint(list).left(this, Constraint.Property.Type.LEFT, 0)
                .bottom(this, Constraint.Property.Type.BOTTOM, 0)
                .top(this, Constraint.Property.Type.TOP, 0)
                .right(sv, Constraint.Property.Type.LEFT, 0)
            );
            elements.add(list);
        }
    }

    public static <W extends Workspace, I, M extends IContextMenu<M, I>> WindowContextMenu<W, I> create(W parent, M context, double posX, double posY, int minWidth, int yFlipHeight)
    {
        WindowContextMenu<W, I> windowContextMenu = new WindowContextMenu<>(parent);
        ElementList<?, I> list = windowContextMenu.getList();
        List<I> contextMenuObjects = context.getObjects();
        Function<I, List<String>> nameProvider = context.getNameProvider();
        BiConsumer<M, ElementList.Item<I>> contextMenuContext = context.getReceiver();

        contextMenuObjects.forEach(o -> {
            list.addItem(o).addTextWrapper(nameProvider.apply(o).getFirst()).setSelectionHandler(item -> { //TODO see how show file list handles this
                item.getWorkspace().setFocused(null);
                contextMenuContext.accept(context, item);
            });
        });
        if(windowContextMenu.getWorkspace().hasInit())
        {
            windowContextMenu.init();
        }
        windowContextMenu.setupAround(posX, posY, minWidth, yFlipHeight);
        windowContextMenu.getWorkspace().addWindow(windowContextMenu);
        windowContextMenu.getWorkspace().setFocused(windowContextMenu);

        return windowContextMenu;
    }
}

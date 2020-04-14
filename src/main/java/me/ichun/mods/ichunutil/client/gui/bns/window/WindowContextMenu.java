package me.ichun.mods.ichunutil.client.gui.bns.window;

import me.ichun.mods.ichunutil.client.gui.bns.window.constraint.Constraint;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.View;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementList;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementScrollBar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

@SuppressWarnings("unchecked")
public class WindowContextMenu<M extends IWindows> extends Window<M>
{
    private final ElementList<?> list;
    private int minWidth = 1;

    private WindowContextMenu(M parent)
    {
        super(parent);

        setBorderSize(() -> 1);
        setView(new ViewContextMenu(this, ""));

        list = (ElementList<?>)((ViewContextMenu)currentView).elements.get(1);

        disableTitle();
        disableDocking();
        disableDockStacking();
        disableDrag();
        disableDragResize();
        disableUndocking();
    }

    public ElementList<?> getList()
    {
        return list;
    }

    public void setupAround(double posX, double posY, int minWidth, int yFlipHeight) //this sets the placement of the window.
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
    public void unfocus(@Nullable IGuiEventListener guiReplacing)
    {
        super.unfocus(guiReplacing);
        parent.removeWindow(this);
    }

    private class ViewContextMenu extends View<WindowContextMenu<M>>
    {
        public ViewContextMenu(@Nonnull WindowContextMenu<M> parent, @Nonnull String s)
        {
            super(parent, s);

            ElementScrollBar<?, ?> sv = new ElementScrollBar<>(this, ElementScrollBar.Orientation.VERTICAL, 0.6F);
            sv.setConstraint(new Constraint(sv).top(this, Constraint.Property.Type.TOP, 0)
                    .bottom(this, Constraint.Property.Type.BOTTOM, 0)
                    .right(this, Constraint.Property.Type.RIGHT, 0)
            );
            elements.add(sv);

            ElementList<?> list = new ElementList<>(this).setScrollVertical(sv);
            list.setConstraint(new Constraint(list).left(this, Constraint.Property.Type.LEFT, 0)
                    .bottom(this, Constraint.Property.Type.BOTTOM, 0)
                    .top(this, Constraint.Property.Type.TOP, 0)
                    .right(sv, Constraint.Property.Type.LEFT, 0)
            );
            elements.add(list);
        }
    }

    public interface IContextMenu
    {
        @Nonnull List<Object> getObjects();
        @Nonnull BiConsumer<IContextMenu, ElementList.Item<?>> getReceiver();
        default @Nonnull Function<Object, String> getNameProvider() { return Object::toString; }
    }

    public static <M extends IWindows> WindowContextMenu<M> create(M parent, IContextMenu iContextMenu, double posX, double posY, int minWidth, int yFlipHeight)
    {
        WindowContextMenu<M> windowContextMenu = new WindowContextMenu<>(parent);
        ElementList<?> list = windowContextMenu.getList();
        List<Object> contextMenuObjects = iContextMenu.getObjects();
        Function<Object, String> nameProvider = iContextMenu.getNameProvider();
        BiConsumer<IContextMenu, ElementList.Item<?>> contextMenuReceiver = iContextMenu.getReceiver();

        contextMenuObjects.forEach(o -> {
            list.addItem(o).addTextWrapper(nameProvider.apply(o)).setSelectionHandler(item -> {
                item.getWorkspace().setFocused(null);
                contextMenuReceiver.accept(iContextMenu, item);
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

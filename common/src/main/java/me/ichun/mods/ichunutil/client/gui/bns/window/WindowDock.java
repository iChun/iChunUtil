package me.ichun.mods.ichunutil.client.gui.bns.window;

import me.ichun.mods.ichunutil.client.gui.bns.Fragment;
import me.ichun.mods.ichunutil.client.gui.bns.Rectangle;
import me.ichun.mods.ichunutil.client.gui.bns.Workspace;
import me.ichun.mods.ichunutil.client.gui.bns.constraint.Constraint;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.View;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class WindowDock<W extends Workspace<W>> extends Window<W, View<?>>
{
    //TODO change dock behaviour - window reference is kept, we take the views and render that instead

    public LinkedHashMap<ArrayListHolder<W>, Constraint.Property.Type> docked = new LinkedHashMap<>();
    public HashMap<Window<W,?>, WindowSize> dockedOriSize = new HashMap<>();
    public HashSet<Constraint.Property.Type> disabledDocks = new HashSet<>();

    public WindowDock(W parent)
    {
        super(parent);
        size(parent.getWidth(), parent.getHeight());
        setConstraint(Constraint.matchParent(this, parent, 0));
        borderSize = () -> iChunUtil.configClient.bnsDockPadding;
        titleSize = () -> 0;
    }

    @Override
    public boolean canShowTitle()
    {
        return false;
    }

    @Override
    public boolean hasTitle()
    {
        return false;
    }

    @Override
    public boolean canDrag()
    {
        return false;
    }

    @Override
    public boolean canDragResize()
    {
        return false;
    }

    @Override
    public boolean canBringToFront()
    {
        return false;
    }

    @Override
    public boolean canBeDocked() { return false; }

    @Override
    public boolean canBeUndocked() { return false; }

    @Override
    public void init()
    {
        constraint.apply();
        docked.keySet().forEach(h -> h.windows.forEach(window -> {
            window.constraint.apply();
            window.resize(Minecraft.getInstance(), this.width, this.height);
            window.init();
        }));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        List<ArrayListHolder<W>> keys = new ArrayList<>(docked.keySet());
        for(int i = keys.size() - 1; i >= 0; i--)
        {
            ArrayList<Window<W,?>> windows = keys.get(i).windows;
            windows.forEach(window -> window.render(graphics, mouseX, mouseY, partialTick));
        }
    }

    @Override
    public void resize(Minecraft mc, int width, int height)
    {
        constraint.apply();
        docked.keySet().forEach(h -> h.windows.forEach(window -> {
            window.constraint.apply();
            window.resize(Minecraft.getInstance(), width, height);
        }));
    }

    @Override
    public void tick()
    {
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double distX, double distY)
    {
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY)
    {
        Window<W,?> windowOver = getWindowOver(mouseX, mouseY);
        if(windowOver != null)
        {
            return windowOver.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        }
        return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY)
    {
        return !parent.isObstructed(this, mouseX, mouseY) && isMouseBetween(mouseX, getLeft(), getLeft() + width) && isMouseBetween(mouseY, getTop(), getTop() + height);
    }

    //TODO test changeFocus!

    @Override
    public @Nullable Fragment<?> getTopMostFragment(double mouseX, double mouseY)
    {
        if(isMouseOver(mouseX, mouseY))
        {
            Fragment<?> fragment = this;
            for(ArrayListHolder<W> h : this.docked.keySet())
            {
                for(Window<W,?> window : h.windows)
                {
                    Fragment<?> fragment1 = window.getTopMostFragment(mouseX, mouseY);
                    if(fragment1 != null)
                    {
                        fragment = fragment1;
                    }
                }
            }
            return fragment;
        }
        return null;
    }

    public boolean isDocked(Window<W,?> window)
    {
        for(ArrayListHolder<W> h : docked.keySet())
        {
            if(h.windows.contains(window))
            {
                return true;
            }
        }
        return false;
    }

    public boolean sameDockStack(Rectangle window, Rectangle window1)
    {
        for(ArrayListHolder<W> h : docked.keySet())
        {
            if(h.windows.contains(window))
            {
                return h.windows.contains(window1);
            }
        }
        return false;
    }

    public void disableDock(Constraint.Property.Type type)
    {
        disabledDocks.add(type);
    }

    @Nullable
    public DockInfo<W> getDockInfo(double mouseX, double mouseY, boolean dockStack)
    {
        if(dockStack)
        {
            Window<W,?> window = getWindowOver(mouseX, mouseY);
            if(window != null && window.canDockStack())
            {
                return new DockInfo<>(window, getAnchorType(window));
            }
        }

        double left = 0;
        double top = 0;
        double right = width;
        double bottom = height;
        for(Map.Entry<ArrayListHolder<W>, Constraint.Property.Type> e : docked.entrySet())
        {
            for(Window<W,?> key : e.getKey().windows)
            {
                Constraint.Property.Type value = e.getValue();
                switch(value)
                {
                    case LEFT:
                    {
                        if(key.getRight() > left)
                        {
                            left = key.getRight();
                        }
                        break;
                    }
                    case TOP:
                    {
                        if(key.getBottom() > top)
                        {
                            top = key.getBottom();
                        }
                        break;
                    }
                    case RIGHT:
                    {
                        if(key.getLeft() < right)
                        {
                            right = key.getLeft();
                        }
                        break;
                    }
                    case BOTTOM:
                    {
                        if(key.getTop() < bottom)
                        {
                            bottom = key.getTop();
                        }
                        break;
                    }
                }
            }
        }

        int dockSnap = iChunUtil.configClient.bnsDockBorder;
        if(mouseY >= top && mouseY < bottom)
        {
            if(mouseX >= left && mouseX < left + dockSnap && !disabledDocks.contains(Constraint.Property.Type.LEFT))
            {
                return new DockInfo<>(null, Constraint.Property.Type.LEFT);
            }
            else if(mouseX >= right - dockSnap && mouseX < right && !disabledDocks.contains(Constraint.Property.Type.RIGHT))
            {
                return new DockInfo<>(null, Constraint.Property.Type.RIGHT);
            }
        }
        if(mouseX >= left && mouseX < right)
        {
            if(mouseY >= top && mouseY < top + dockSnap && !disabledDocks.contains(Constraint.Property.Type.TOP))
            {
                return new DockInfo<>(null, Constraint.Property.Type.TOP);
            }
            else if(mouseY >= bottom - dockSnap && bottom < right && !disabledDocks.contains(Constraint.Property.Type.BOTTOM))
            {
                return new DockInfo<>(null, Constraint.Property.Type.BOTTOM);
            }
        }

        return null;
    }

    public boolean addToDocked(Window<W,?> dockedWin, Window<W,?> window)
    {
        for(Map.Entry<ArrayListHolder<W>, Constraint.Property.Type> e : docked.entrySet())
        {
            if(e.getKey().windows.contains(dockedWin))
            {
                dockedOriSize.put(window, new WindowSize(window.constraint, window.getLeft(), window.getTop(), window.getWidth(), window.getHeight()));

                Constraint.Property.Type dockType = e.getValue();
                ArrayList<Window<W,?>> dockStack = e.getKey().windows;
                Window<W,?> lastInStack = dockStack.getLast(); // we stack downwards and to the right.

                int maxWidth = -1;
                int maxHeight = -1;
                if(dockType.getAxis().isHorizontal())
                {
                    maxWidth = window.width;
                    for(Window<W,?> window1 : dockStack)
                    {
                        if(window1.width > maxWidth)
                        {
                            maxWidth = window1.width;
                        }
                    }
                }
                else if(dockType.getAxis().isVertical())
                {
                    maxHeight = window.height;
                    for(Window<W,?> window1 : dockStack)
                    {
                        if(window1.height > maxHeight)
                        {
                            maxHeight = window1.height;
                        }
                    }
                }


                Constraint constraint = new Constraint(window);
                Constraint.Property.Type[] values = Constraint.Property.Type.values();
                for(int i = values.length - 1; i >= 0; i--)
                {
                    Constraint.Property.Type type1 = values[i];
                    if(type1.equals(Constraint.Property.Type.WIDTH) || type1.equals(Constraint.Property.Type.HEIGHT))
                    {
                        continue;
                    }

                    Rectangle constrainable = getWindowAnchor(lastInStack, type1);
                    if(dockType.getAxis().isHorizontal() && type1 == Constraint.Property.Type.TOP || dockType.getAxis().isVertical() && type1 == Constraint.Property.Type.LEFT) //X. if type1 == top, anchor is lastInStack, same for Y.
                    {
                        constrainable = lastInStack;

                        //we drop the constraint of lastInStack. let it free float.
                        lastInStack.constraint.type(type1.getOpposite(), null, null, 0);

                        //set the size
                        if(type1 == Constraint.Property.Type.TOP) //if we're docked left or right, reset height
                        {
                            lastInStack.setHeight(dockedOriSize.get(lastInStack).height);
                        }
                        else
                        {
                            lastInStack.setWidth(dockedOriSize.get(lastInStack).width);
                        }
                    }
                    if(type1 != dockType.getOpposite())
                    {
                        if(constrainable != null && !(constrainable instanceof WindowDock))
                        {
                            constraint = constraint.type(type1, constrainable, type1.getOpposite(), -(Integer)window.borderSize.get() + borderSize.get());
                        }
                        else
                        {
                            constraint = constraint.type(type1, this, type1, -(Integer)window.borderSize.get() + borderSize.get());
                        }
                    }
                }

                e.getKey().windows.add(window);
                window.setConstraint(constraint);

                for(Window<W,?> window1 : dockStack)
                {
                    if(maxWidth >= 0)
                    {
                        window1.setWidth(maxWidth);
                    }
                    else if(maxHeight >= 0)
                    {
                        window1.setHeight(maxHeight);
                    }
                    window1.constraint.apply();
                    if(getWorkspace().hasInit())
                    {
                        window1.resize(Minecraft.getInstance(), this.width, this.height);
                    }
                }
                return true;
            }
        }
        return false;
    }

    public void addToDock(Window<W,?> window, Constraint.Property.Type type)
    {
        dockedOriSize.put(window, new WindowSize(window.constraint, window.getLeft(), window.getTop(), window.getWidth(), window.getHeight()));

        Constraint constraint = new Constraint(window);
        for(Constraint.Property.Type type1 : Constraint.Property.Type.values())
        {
            if(type1.equals(Constraint.Property.Type.WIDTH) || type1.equals(Constraint.Property.Type.HEIGHT))
            {
                continue;
            }

            Rectangle constrainable = getAnchor(type1);
            if(type1 != type.getOpposite())
            {
                if(constrainable != null)
                {
                    constraint = constraint.type(type1, constrainable, type1.getOpposite(), -(Integer)window.borderSize.get() + borderSize.get());
                }
                else
                {
                    constraint = constraint.type(type1, this, type1, -(Integer)window.borderSize.get() + borderSize.get());
                }
            }
        }

        ArrayList<Window<W,?>> windows = new ArrayList<>();
        windows.add(window);
        docked.put(new ArrayListHolder<>(windows), type);
        window.setConstraint(constraint);
        window.constraint.apply();
        if(getWorkspace().hasInit())
        {
            window.resize(Minecraft.getInstance(), this.width, this.height);
        }
    }

    public void removeFromDock(Window<W,?> window)
    {
        boolean redoConstraints = false;

        Iterator<Map.Entry<ArrayListHolder<W>, Constraint.Property.Type>> iterator = docked.entrySet().iterator();
        while(iterator.hasNext())
        {
            Map.Entry<ArrayListHolder<W>, Constraint.Property.Type> e = iterator.next();
            ArrayList<Window<W,?>> windows = e.getKey().windows;

            //redo the constraints
            EnumMap<Constraint.Property.Type, Constraint.Property> anchors = new EnumMap<>(Constraint.Property.Type.class);
            if(redoConstraints || windows.contains(window))
            {
                for(Constraint.Property.Type type : Constraint.Property.Type.values())
                {
                    if(type.equals(Constraint.Property.Type.WIDTH) || type.equals(Constraint.Property.Type.HEIGHT))
                    {
                        continue;
                    }

                    Constraint.Property stackAnchor = getStackAnchor(windows, type);
                    if(stackAnchor != null)
                    {
                        anchors.put(type, stackAnchor);
                    }
                }
            }

            if(windows.contains(window)) // window is in this arraylist. sort it out
            {
                redoConstraints = true;

                if(windows.size() == 1)
                {
                    iterator.remove(); //we're using a linkedHASHmap. Empty ArrayLists aren't very friendly.
                    continue;
                }
                else //we're in a dock stack.
                {
                    //we have the anchors of each type. remove the nonbeliever!
                    windows.remove(window);
                }
            }

            if(redoConstraints)
            {
                Constraint.Property.Type dockType = e.getValue();
                //Update the constraints
                for(int i = 0; i < windows.size(); i++)
                {
                    Window<W,?> dockWindow = windows.get(i);
                    if(i == 0)
                    {
                        Constraint constraint = new Constraint(dockWindow);
                        anchors.forEach((type, property) -> {
                            if(property.getReference() == window)
                            {
                                Rectangle constrainable = getAnchor(type, dockWindow);
                                if(constrainable != null && constrainable != dockWindow)
                                {
                                    constraint.type(type, constrainable, type.getOpposite(), -(Integer)dockWindow.borderSize.get() + borderSize.get());
                                }
                                else
                                {
                                    constraint.type(type, this, type, -(Integer)dockWindow.borderSize.get() + borderSize.get());
                                }
                            }
                            else
                            {
                                constraint.type(type, property.getReference(), property.getType(), property.getDist());
                            }
                        });
                        dockWindow.setConstraint(constraint);
                    }
                    else
                    {
                        Window<W,?> lastInStack = windows.get(i - 1); // we stack downwards and to the right.

                        Constraint constraint = new Constraint(dockWindow);
                        Constraint.Property.Type[] values = Constraint.Property.Type.values();
                        for(int ii = values.length - 1; ii >= 0; ii--)
                        {
                            Constraint.Property.Type type1 = values[ii];
                            if(type1.equals(Constraint.Property.Type.WIDTH) || type1.equals(Constraint.Property.Type.HEIGHT))
                            {
                                continue;
                            }

                            Rectangle constrainable = getWindowAnchor(lastInStack, type1);
                            if(dockType.getAxis().isHorizontal() && type1 == Constraint.Property.Type.TOP || dockType.getAxis().isVertical() && type1 == Constraint.Property.Type.LEFT) //X. if type1 == top, anchor is lastInStack, same for Y.
                            {
                                constrainable = lastInStack;

                                //we drop the constraint of lastInStack. let it free float.
                                lastInStack.constraint.type(type1.getOpposite(), null, null, 0);

                                //set the size
                                if(type1 == Constraint.Property.Type.TOP) //if we're docked left or right, reset height
                                {
                                    lastInStack.setHeight(dockedOriSize.get(lastInStack).height);
                                }
                                else
                                {
                                    lastInStack.setWidth(dockedOriSize.get(lastInStack).width);
                                }
                            }
                            if(type1 != dockType.getOpposite())
                            {
                                if(constrainable != null && !(constrainable instanceof WindowDock))
                                {
                                    constraint = constraint.type(type1, constrainable, type1.getOpposite(), -(Integer)dockWindow.borderSize.get() + borderSize.get());
                                }
                                else
                                {
                                    constraint = constraint.type(type1, this, type1, -(Integer)dockWindow.borderSize.get() + borderSize.get());
                                }
                            }
                        }
                        dockWindow.setConstraint(constraint);
                    }
                }

                for(Window<W,?> window1 : windows)
                {
                    window1.constraint.apply();
                    if(getWorkspace().hasInit())
                    {
                        window1.resize(Minecraft.getInstance(), this.width, this.height);
                    }
                }
            }
        }

        WindowSize size = dockedOriSize.get(window);
        window.setConstraint(size.constraint);
        if(!(size.x == 0 && size.y == 0))
        {
            window.setLeft(size.x);
            window.setTop(size.y);
        }
        window.setWidth(size.width);
        window.setHeight(size.height);
        window.resize(Minecraft.getInstance(), window.parent.getWidth(), window.parent.getHeight());
        dockedOriSize.remove(window);
    }

    @Nullable
    public Rectangle getAnchor(Constraint.Property.Type type) //gets the element to anchor on based on type
    {
        return getAnchor(type, null);
    }

    @Nullable
    public Rectangle getAnchor(Constraint.Property.Type type, Rectangle ignored) //gets the element to anchor on based on type
    {
        Rectangle typeMost = null;
        for(Map.Entry<ArrayListHolder<W>, Constraint.Property.Type> e : docked.entrySet())
        {
            if(e.getValue() == type && (ignored == null || !e.getKey().windows.contains(ignored)))
            {
                typeMost = e.getKey().windows.get(0);
            }
        }
        return typeMost;
    }

    @Nullable
    public Rectangle getWindowAnchor(Window<W,?> window, Constraint.Property.Type type) //gets the element to anchor on based on type
    {
        return window.constraint.get(type).getReference();
    }

    @Nullable
    public Constraint.Property getStackAnchor(ArrayList<Window<W,?>> stack, Constraint.Property.Type type) //gets the element to anchor on based on type
    {
        for(Window<W,?> window : stack)
        {
            Constraint.Property anchor = window.constraint.get(type);
            if(anchor != Constraint.Property.NONE && !stack.contains(anchor.getReference())) //Window extends Rectangle
            {
                return anchor;
            }
        }
        return null;
    }

    public Constraint.Property.Type getAnchorType(Window<W,?> window)
    {
        for(Map.Entry<ArrayListHolder<W>, Constraint.Property.Type> e : docked.entrySet())
        {
            if(e.getKey().windows.contains(window))
            {
                return e.getValue();
            }
        }
        return null;
    }

    public Window<W,?> getWindowOver(double mouseX, double mouseY)
    {
        for(ArrayListHolder<W> h : docked.keySet())
        {
            for(Window<W,?> window : h.windows)
            {
                if(window.isMouseOver(mouseX, mouseY))
                {
                    return window;
                }
            }
        }
        return null;
    }

    @NotNull
    public ArrayList<Window<W,?>> getDockStack(Window<W,?> window)
    {
        for(ArrayListHolder<W> h : docked.keySet())
        {
            if(h.windows.contains(window))
            {
                return h.windows;
            }
        }
        return new ArrayList<>();
    }

    public void edgeGrab(Window<W,?> draggedWindow, double mouseX, double mouseY, EdgeGrab edgeGrab)
    {
        Constraint.Property.Type anchorType = getAnchorType(draggedWindow);
        if(anchorType != null && (anchorType.getAxis().isHorizontal() && edgeGrab.left && draggedWindow.constraint.get(Constraint.Property.Type.LEFT) == Constraint.Property.NONE ||
            anchorType.getAxis().isHorizontal() && edgeGrab.right && draggedWindow.constraint.get(Constraint.Property.Type.RIGHT) == Constraint.Property.NONE ||
            anchorType.getAxis().isVertical() && edgeGrab.top && draggedWindow.constraint.get(Constraint.Property.Type.TOP) == Constraint.Property.NONE ||
            anchorType.getAxis().isVertical() && edgeGrab.bottom && draggedWindow.constraint.get(Constraint.Property.Type.BOTTOM) == Constraint.Property.NONE
        ))
        {
            ArrayList<Window<W,?>> dockStack = getDockStack(draggedWindow);

            for(int i = 0; i < dockStack.size(); i++)
            {
                Window<W,?> window = dockStack.get(i);
                if(window != draggedWindow)
                {
                    window.dragResize(mouseX, mouseY, edgeGrab);
                }
            }
        }

        getWorkspace().getDock().init();
    }

    public record WindowSize(Constraint constraint, int x, int y, int width, int height){}

    public record ArrayListHolder<W extends Workspace<W>>(ArrayList<Window<W,?>> windows){} //this is to have a consistent hashcode for hashmaps since there is no IdentityLinkedHashMap

    public record DockInfo<W extends Workspace<W>>(@Nullable Window<W,?> window, @Nullable Constraint.Property.Type type){}
}

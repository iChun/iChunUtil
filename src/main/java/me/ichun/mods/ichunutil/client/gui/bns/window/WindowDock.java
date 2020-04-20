package me.ichun.mods.ichunutil.client.gui.bns.window;

import me.ichun.mods.ichunutil.client.gui.bns.window.constraint.Constraint;
import me.ichun.mods.ichunutil.client.gui.bns.window.constraint.IConstrainable;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.client.Minecraft;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

import static me.ichun.mods.ichunutil.client.gui.bns.window.constraint.Constraint.Property.Type.*;

public class WindowDock<M extends IWindows> extends Window<M>
{
    public LinkedHashMap<ArrayList<Window<?>>, Constraint.Property.Type> docked = new LinkedHashMap<>();
    public HashMap<Window<?>, WindowSize> dockedOriSize = new HashMap<>();
    public HashSet<Constraint.Property.Type> disabledDocks = new HashSet<>();

    public WindowDock(M parent)
    {
        super(parent);
        size(parent.getWidth(), parent.getHeight());
        if(parent instanceof IConstrainable)
        {
            setConstraint(Constraint.matchParent(this, (IConstrainable)parent, 0));
        }
        borderSize = () -> iChunUtil.configClient.guiDockPadding;
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
        docked.keySet().forEach(windows -> windows.forEach(window -> {
            window.constraint.apply();
            window.resize(Minecraft.getInstance(), this.width, this.height);
            window.init();
        }));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick)
    {
        List<ArrayList<Window<?>>> keys = new ArrayList<>(docked.keySet());
        for(int i = keys.size() - 1; i >= 0; i--)
        {
            ArrayList<Window<?>> windows = keys.get(i);
            windows.forEach(window -> window.render(mouseX, mouseY, partialTick));
        }
    }

    @Override
    public void resize(Minecraft mc, int width, int height)
    {
        constraint.apply();
        docked.keySet().forEach(windows -> windows.forEach(window -> {
            window.constraint.apply();
            window.resize(Minecraft.getInstance(), this.width, this.height);
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
    public boolean mouseScrolled(double mouseX, double mouseY, double amount)
    {
        Window<?> windowOver = getWindowOver(mouseX, mouseY);
        if(windowOver != null)
        {
            return windowOver.mouseScrolled(mouseX, mouseY, amount);
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
            for(ArrayList<Window<?>> windows : this.docked.keySet())
            {
                for(Window<?> window : windows)
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

    public boolean isDocked(Window<?> window)
    {
        for(ArrayList<Window<?>> windows : docked.keySet())
        {
            if(windows.contains(window))
            {
                return true;
            }
        }
        return false;
    }

    public boolean sameDockStack(IConstrainable window, IConstrainable window1)
    {
        for(ArrayList<Window<?>> windows : docked.keySet())
        {
            if(windows.contains(window))
            {
                return windows.contains(window1);
            }
        }
        return false;
    }

    public void disableDock(Constraint.Property.Type type)
    {
        disabledDocks.add(type);
    }

    public @Nullable IWindows.DockInfo getDockInfo(double mouseX, double mouseY, boolean dockStack)
    {
        if(dockStack)
        {
            Window<?> window = getWindowOver(mouseX, mouseY);
            if(window != null && window.canDockStack())
            {
                return new IWindows.DockInfo(window, getAnchorType(window));
            }
        }

        double left = 0;
        double top = 0;
        double right = width;
        double bottom = height;
        for(Map.Entry<ArrayList<Window<?>>, Constraint.Property.Type> e : docked.entrySet())
        {
            for(Window<?> key : e.getKey())
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

        int dockSnap = iChunUtil.configClient.guiDockBorder;
        if(mouseY >= top && mouseY < bottom)
        {
            if(mouseX >= left && mouseX < left + dockSnap && !disabledDocks.contains(LEFT))
            {
                return new IWindows.DockInfo(null, LEFT);
            }
            else if(mouseX >= right - dockSnap && mouseX < right && !disabledDocks.contains(RIGHT))
            {
                return new IWindows.DockInfo(null, Constraint.Property.Type.RIGHT);
            }
        }
        if(mouseX >= left && mouseX < right)
        {
            if(mouseY >= top && mouseY < top + dockSnap && !disabledDocks.contains(TOP))
            {
                return new IWindows.DockInfo(null, Constraint.Property.Type.TOP);
            }
            else if(mouseY >= bottom - dockSnap && bottom < right && !disabledDocks.contains(BOTTOM))
            {
                return new IWindows.DockInfo(null, Constraint.Property.Type.BOTTOM);
            }
        }

        return null;
    }

    public boolean addToDocked(Window<?> dockedWin, Window<?> window)
    {
        for(Map.Entry<ArrayList<Window<?>>, Constraint.Property.Type> e : docked.entrySet())
        {
            if(e.getKey().contains(dockedWin))
            {
                dockedOriSize.put(window, new WindowSize(window.constraint, window.getLeft(), window.getTop(), window.getWidth(), window.getHeight()));

                Constraint.Property.Type dockType = e.getValue();
                ArrayList<Window<?>> dockStack = e.getKey();
                Window<?> lastInStack = dockStack.get(dockStack.size() - 1); // we stack downwards and to the right.

                int maxWidth = -1;
                int maxHeight = -1;
                if(dockType.getAxis().isHorizontal())
                {
                    maxWidth = window.width;
                    for(Window<?> window1 : dockStack)
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
                    for(Window<?> window1 : dockStack)
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
                    IConstrainable constrainable = getWindowAnchor(lastInStack, type1);
                    if(dockType.getAxis().isHorizontal() && type1 == TOP || dockType.getAxis().isVertical() && type1 == LEFT) //X. if type1 == top, anchor is lastInStack, same for Y.
                    {
                        constrainable = lastInStack;

                        //we drop the constraint of lastInStack. let it free float.
                        lastInStack.constraint.type(type1.getOpposite(), null, null, 0);

                        //set the size
                        if(type1 == TOP) //if we're docked left or right, reset height
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

                e.getKey().add(window);
                window.setConstraint(constraint);

                for(Window<?> window1 : dockStack)
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

    public void addToDock(Window<?> window, Constraint.Property.Type type)
    {
        dockedOriSize.put(window, new WindowSize(window.constraint, window.getLeft(), window.getTop(), window.getWidth(), window.getHeight()));

        Constraint constraint = new Constraint(window);
        for(Constraint.Property.Type type1 : Constraint.Property.Type.values())
        {
            IConstrainable constrainable = getAnchor(type1);
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

        ArrayList<Window<?>> windows = new ArrayList<>();
        windows.add(window);
        docked.put(windows, type);
        window.setConstraint(constraint);
        window.constraint.apply();
        if(getWorkspace().hasInit())
        {
            window.resize(Minecraft.getInstance(), this.width, this.height);
        }
    }

    public void removeFromDock(Window<?> window)
    {
        boolean redoConstraints = false;

        Iterator<Map.Entry<ArrayList<Window<?>>, Constraint.Property.Type>> iterator = docked.entrySet().iterator();
        while(iterator.hasNext())
        {
            Map.Entry<ArrayList<Window<?>>, Constraint.Property.Type> e = iterator.next();
            ArrayList<Window<?>> windows = e.getKey();

            //redo the constraints
            EnumMap<Constraint.Property.Type, Constraint.Property> anchors = new EnumMap<>(Constraint.Property.Type.class);
            if(redoConstraints || windows.contains(window))
            {
                for(Constraint.Property.Type type : Constraint.Property.Type.values())
                {
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
                    Window<?> dockWindow = windows.get(i);
                    if(i == 0)
                    {
                        Constraint constraint = new Constraint(dockWindow);
                        anchors.forEach((type, property) -> {
                            if(property.getReference() == window)
                            {
                                IConstrainable constrainable = getAnchor(type, dockWindow);
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
                        Window<?> lastInStack = windows.get(i - 1); // we stack downwards and to the right.

                        Constraint constraint = new Constraint(dockWindow);
                        Constraint.Property.Type[] values = Constraint.Property.Type.values();
                        for(int ii = values.length - 1; ii >= 0; ii--)
                        {
                            Constraint.Property.Type type1 = values[ii];
                            IConstrainable constrainable = getWindowAnchor(lastInStack, type1);
                            if(dockType.getAxis().isHorizontal() && type1 == TOP || dockType.getAxis().isVertical() && type1 == LEFT) //X. if type1 == top, anchor is lastInStack, same for Y.
                            {
                                constrainable = lastInStack;

                                //we drop the constraint of lastInStack. let it free float.
                                lastInStack.constraint.type(type1.getOpposite(), null, null, 0);

                                //set the size
                                if(type1 == TOP) //if we're docked left or right, reset height
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

                for(Window<?> window1 : windows)
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

    public @Nullable IConstrainable getAnchor(Constraint.Property.Type type) //gets the element to anchor on based on type
    {
        return getAnchor(type, null);
    }


    public @Nullable IConstrainable getAnchor(Constraint.Property.Type type, IConstrainable ignored) //gets the element to anchor on based on type
    {
        IConstrainable typeMost = null;
        for(Map.Entry<ArrayList<Window<?>>, Constraint.Property.Type> e : docked.entrySet())
        {
            if(e.getValue() == type && (ignored == null || !e.getKey().contains(ignored)))
            {
                typeMost = e.getKey().get(0);
            }
        }
        return typeMost;
    }

    public @Nullable IConstrainable getWindowAnchor(Window<?> window, Constraint.Property.Type type) //gets the element to anchor on based on type
    {
        return window.constraint.get(type).getReference();
    }

    public @Nullable Constraint.Property getStackAnchor(ArrayList<Window<?>> stack, Constraint.Property.Type type) //gets the element to anchor on based on type
    {
        for(Window<?> window : stack)
        {
            Constraint.Property anchor = window.constraint.get(type);
            if(anchor != Constraint.Property.NONE && !stack.contains(anchor.getReference())) //Window extends IConstrainable
            {
                return anchor;
            }
        }
        return null;
    }

    public Constraint.Property.Type getAnchorType(Window<?> window)
    {
        for(Map.Entry<ArrayList<Window<?>>, Constraint.Property.Type> e : docked.entrySet())
        {
            if(e.getKey().contains(window))
            {
                return e.getValue();
            }
        }
        return null;
    }

    public Window<?> getWindowOver(double mouseX, double mouseY)
    {
        for(ArrayList<Window<?>> windows : docked.keySet())
        {
            for(Window<?> window : windows)
            {
                if(window.isMouseOver(mouseX, mouseY))
                {
                    return window;
                }
            }
        }
        return null;
    }

    public @Nonnull ArrayList<Window<?>> getDockStack(Window<?> window)
    {
        for(ArrayList<Window<?>> windows : docked.keySet())
        {
            if(windows.contains(window))
            {
                return windows;
            }
        }
        return new ArrayList<>();
    }

    public <M extends IWindows> void edgeGrab(Window<M> draggedWindow, double mouseX, double mouseY, EdgeGrab edgeGrab)
    {
        Constraint.Property.Type anchorType = getAnchorType(draggedWindow);
        if(anchorType != null && (anchorType.getAxis().isHorizontal() && edgeGrab.left && draggedWindow.constraint.get(LEFT) == Constraint.Property.NONE ||
                anchorType.getAxis().isHorizontal() && edgeGrab.right && draggedWindow.constraint.get(RIGHT) == Constraint.Property.NONE ||
                anchorType.getAxis().isVertical() && edgeGrab.top && draggedWindow.constraint.get(TOP) == Constraint.Property.NONE ||
                anchorType.getAxis().isVertical() && edgeGrab.bottom && draggedWindow.constraint.get(BOTTOM) == Constraint.Property.NONE
        ))
        {
            ArrayList<Window<?>> dockStack = getDockStack(draggedWindow);

            for(int i = 0; i < dockStack.size(); i++)
            {
                Window<?> window = dockStack.get(i);
                if(window != draggedWindow)
                {
                    window.dragResize(mouseX, mouseY, edgeGrab);
                }
            }
        }

        getWorkspace().getDock().init();
    }

    public static class WindowSize
    {
        public final Constraint constraint;
        public final int x;
        public final int y;
        public final int width;
        public final int height;

        public WindowSize(Constraint constraint, int x, int y, int width, int height) {
            this.constraint = constraint;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }
}

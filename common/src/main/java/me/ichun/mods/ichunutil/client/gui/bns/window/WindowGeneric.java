package me.ichun.mods.ichunutil.client.gui.bns.window;

import me.ichun.mods.ichunutil.client.gui.bns.Workspace;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.View;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class WindowGeneric<W extends Workspace, V extends View<?>> extends Window<W, V>
{
    //Variable generic typing reference
    //WindowGeneric<WorkspaceConfigs, ViewEditList<WindowGeneric<?,?>>> window = WindowGeneric.create(parent.parent, windowGeneric -> new ViewEditList<>(windowGeneric, entryName, list, finalValidator, list1 -> {}));
    public WindowGeneric(@NotNull W parent)
    {
        super(parent);

        isNotUnique();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <T extends Window<W, V>> T setCurrentView(V v)
    {
        super.setCurrentView(v);
        ((View)getCurrentView()).setWindowGenericProperties(this);
        return (T)this;
    }

    @SuppressWarnings("unchecked")
    public static <T extends WindowGeneric<W, V>, W extends Workspace, V extends View<?>> T create(W w, Function<WindowGeneric<W, V>, V> viewCreator)
    {
        WindowGeneric<W, V> window = new WindowGeneric<>(w);
        window.setView(viewCreator.apply(window));
        return (T)window;
    }
}

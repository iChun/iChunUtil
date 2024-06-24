package me.ichun.mods.ichunutil.client.gui.bns.window;

import me.ichun.mods.ichunutil.client.gui.bns.Workspace;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.View;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class WindowGeneric<W extends Workspace, V extends View<WindowGeneric<W, V>>> extends Window<W, V>
{
    public WindowGeneric(@NotNull W parent)
    {
        super(parent);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <T extends Window<W, V>> T setCurrentView(V v)
    {
        super.setCurrentView(v);
        ((View)getCurrentView()).setWindowGenericProperties(this);
        return (T)this;
    }

    public static <W extends Workspace, V extends View<WindowGeneric<W, V>>> WindowGeneric<W, V> create(W w, Class<V> clz)
    {
        return new WindowGeneric<>(w);
    }

    public static <T extends WindowGeneric<W, V>, W extends Workspace, V extends View<WindowGeneric<W, V>>> T create(W w, Class<V> clz, Function<WindowGeneric<W, V>, V> viewCreator)
    {
        WindowGeneric<W, V> window = new WindowGeneric<>(w);
        window.setView(viewCreator.apply(window));
        return (T)window;
    }
}

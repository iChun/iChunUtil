package me.ichun.mods.ichunutil.client.gui.bns.window;

import me.ichun.mods.ichunutil.client.gui.bns.Workspace;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.ViewEditList;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementList;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class WindowEditList<M extends Workspace> extends Window<M, ViewEditList>
{
    public WindowEditList(@NotNull M parent, @NotNull String s, @NotNull List<?> objectList, @NotNull Predicate<String> validator, @NotNull Consumer<ElementList<?>> responder)
    {
        this(parent, s, objectList, validator, responder, null);
    }

    public WindowEditList(@NotNull M parent, @NotNull String s, @NotNull List<?> objectList, @NotNull Predicate<String> validator, @NotNull Consumer<ElementList<?>> responder, @Nullable BiFunction<String, Integer, FormattedCharSequence> textFormatter)
    {
        super(parent);

        setView(new ViewEditList(this, s, objectList, validator, responder, textFormatter));
        disableDockingEntirely();
    }
}

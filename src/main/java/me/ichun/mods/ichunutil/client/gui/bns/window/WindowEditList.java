package me.ichun.mods.ichunutil.client.gui.bns.window;

import me.ichun.mods.ichunutil.client.gui.bns.window.view.ViewEditList;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementList;
import net.minecraft.util.FormattedCharSequence;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class WindowEditList<M extends IWindows> extends Window<M>
{
    public WindowEditList(@Nonnull M parent, @Nonnull String s, @Nonnull List<?> objectList, @Nonnull Predicate<String> validator, @Nonnull Consumer<ElementList<?>> responder)
    {
        this(parent, s, objectList, validator, responder, null);
    }

    public WindowEditList(@Nonnull M parent, @Nonnull String s, @Nonnull List<?> objectList, @Nonnull Predicate<String> validator, @Nonnull Consumer<ElementList<?>> responder, @Nullable BiFunction<String, Integer, FormattedCharSequence> textFormatter)
    {
        super(parent);

        setView(new ViewEditList(this, s, objectList, validator, responder, textFormatter));
        disableDockingEntirely();
    }
}

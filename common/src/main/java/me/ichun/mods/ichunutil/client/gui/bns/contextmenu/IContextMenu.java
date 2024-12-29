package me.ichun.mods.ichunutil.client.gui.bns.contextmenu;

import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementList;
import me.ichun.mods.ichunutil.common.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public interface IContextMenu<M, I>
{
    @NotNull List<I> getObjects();
    @NotNull BiConsumer<M, ElementList.Item<I>> getReceiver();
    default @NotNull Function<I, List<String>> getNameProvider() {return StringUtil::getInterpretedInfo;}
}

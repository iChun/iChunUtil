package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import me.ichun.mods.ichunutil.client.gui.bns.Fragment;
import net.minecraft.client.resources.language.I18n;
import org.apache.commons.lang3.function.TriConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class ElementButton extends ElementButtonAbstract<ElementButton>
{
    @NotNull
    private String defaultText;
    private String altText;
    private Supplier<Boolean> altTextCondition;

    public ElementButton(@NotNull Fragment<?> parent, @NotNull String s, TriConsumer<ElementButton, Double, Double> callback, @Nullable TriConsumer<ElementButton, Double, Double> rightMouseCallback)
    {
        super(parent, s, callback, rightMouseCallback);

        this.defaultText = text;
    }

    public ElementButton(@NotNull Fragment<?> parent, String s, TriConsumer<ElementButton, Double, Double> callback)
    {
        this(parent, s, callback, null);
    }

    public ElementButton setAltText(@NotNull String altText, @NotNull Supplier<Boolean> altTextCondition)
    {
        this.altText = I18n.get(altText);
        this.altTextCondition = altTextCondition;
        return this;
    }

    @Override
    public void tick()
    {
        super.tick();

        if(this.altTextCondition != null)
        {
            text = this.altTextCondition.get() ? this.altText : this.defaultText;
        }
    }
}

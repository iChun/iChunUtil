package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import com.google.common.collect.Lists;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.View;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.TextFieldWidget;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ElementTextField extends Element<View>
{
    private List<IGuiEventListener> children = Lists.newArrayList();
    private TextFieldWidget widget;
    private String defaultText = "";
    private int maxStringLength = 256;
    private Predicate<String> validator = s -> true;
    private @Nullable Consumer<String> responder;

    private int lastLeft;
    private int lastTop;

    public ElementTextField(@Nonnull View parent)
    {
        super(parent);
    }

    public ElementTextField setDefaultText(String s)
    {
        defaultText = s;
        return this;
    }

    public ElementTextField setValidator(Predicate<String> validator)
    {
        this.validator = validator;
        return this;
    }

    public ElementTextField setResponder(Consumer<String> responder)
    {
        this.responder = responder;
        return this;
    }

    public ElementTextField setMaxStringLength(int i)
    {
        this.maxStringLength = i;
        return this;
    }

    @Override
    public void init()
    {
        super.init();
        widget = new TextFieldWidget(getFontRenderer(), getLeft(), getTop(), width, height, "Text Field"); //TODO update this narration message?
        widget.setText(defaultText);
        widget.setMaxStringLength(maxStringLength);
        widget.setValidator(validator);
        widget.func_212954_a(responder);
        children.add(widget);
        adjustWidget();

        lastLeft = getLeft();
        lastTop = getTop();
    }

    @Override
    public void tick()
    {
        super.tick();
        if(widget != null)
        {
            widget.tick();
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick)
    {
        if(lastLeft != getLeft() || lastTop != getTop())
        {
            adjustWidget();
            lastLeft = getLeft();
            lastTop = getTop();
        }

        if(renderMinecraftStyle())
        {
            widget.setEnableBackgroundDrawing(true);
            widget.render(mouseX, mouseY, partialTick);
        }
        else
        {
            int[] colour;
            if(isMouseOver(mouseX, mouseY))
            {
                colour = getTheme().elementInputBackgroundHover;
            }
            else
            {
                colour = getTheme().elementInputBackgroundInactive;
            }
            fill(getTheme().elementInputBorder, 0);
            fill(colour, 1);
            widget.setEnableBackgroundDrawing(false);
            widget.render(mouseX, mouseY, partialTick);
        }
    }

    @Override
    public void resize(Minecraft mc, int width, int height)
    {
        super.resize(mc, width, height);
        adjustWidget();
    }

    public void adjustWidget()
    {
        if(widget != null)
        {
            if(renderMinecraftStyle())
            {
                widget.x = getLeft() + 1;
                widget.y = getTop() + 1;
                widget.setWidth(this.width - 2);
                widget.setHeight(this.height - 2);
            }
            else
            {
                widget.x = getLeft() + 5;
                widget.y = getTop() + 1 + ((this.height - getFontRenderer().FONT_HEIGHT) / 2);
                widget.setWidth(this.width - 6);
                widget.setHeight(this.height - 2);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if(isMouseOver(mouseX, mouseY))
        {
            setFocused(widget);
            widget.setFocused2(true);
            return true;
        }
        return false;
    }

    @Override
    public void unfocus(@Nullable IGuiEventListener guiReplacing)
    {
        super.unfocus(guiReplacing);
        widget.setFocused2(false);
        setFocused(null);
    }

    @Override
    public boolean changeFocus(boolean direction)
    {
        if(parentFragment.getFocused() != this)
        {
            setFocused(widget);
            widget.setFocused2(true);
            return true;
        }
        return false;
    }

    public void setText(@Nonnull String s) //ONLY do AFTER init
    {
        if(widget == null)
        {
            iChunUtil.LOGGER.error("You're trying to set a text field widget whilst it is still null. Use setDefaultText instead");
            return;
        }
        widget.setText(s);
    }

    public String getText()
    {
        return widget.getText();
    }

    public TextFieldWidget getTextField()
    {
        return widget;
    }

    @Override
    public Supplier<Integer> getMinHeight()
    {
        return () -> 12;
    }
}

package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.ichun.mods.ichunutil.client.gui.bns.Fragment;
import me.ichun.mods.ichunutil.client.gui.bns.Workspace;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

@SuppressWarnings("unchecked")
public class ElementTextField extends Element<Fragment<?>>
{
    public static final Predicate<String> INTEGERS = (s) ->
    {
        if(s.isEmpty() || s.equals("-"))
        {
            return true;
        }
        try
        {
            if(s.contains("."))
            {
                return false; //integers only
            }
            Integer.parseInt(s);
            return true;
        }
        catch(NumberFormatException e)
        {
            return false;
        }
    };
    public static final Predicate<String> NUMBERS = (s) ->
    {
        if(s.isEmpty() || s.equals("-"))
        {
            return true;
        }
        try
        {
            if(s.contains("f") || s.contains("d") || s.contains("F") || s.contains("D"))
            {
                return false;
            }
            Double.parseDouble(s);
            return true;
        }
        catch(NumberFormatException e)
        {
            return false;
        }
    };
    public static final Predicate<String> FILE_SAFE = (s) ->
    {
        if(s.isEmpty())
        {
            return true;
        }
        String[] invalidChars = new String[] { "\\", "/", ":", "*", "?", "\"", "<", ">", "|" };
        for(String c : invalidChars)
        {
            if(s.contains(c))
            {
                return false;
            }
        }
        return !s.startsWith(".");
    };

    //    private List<IGuiEventListener> children = Lists.newArrayList();
    protected EditBox widget;
    private String defaultText = "";
    private int maxStringLength = 32767;
    private Predicate<String> validator = s -> true;
    private BiFunction<String, Integer, FormattedCharSequence> textFormatter = (s, cursorPos) -> FormattedCharSequence.forward(s, Style.EMPTY);
    private @Nullable Consumer<String> responder;
    private @Nullable Consumer<String> enterResponder;

    private int lastLeft;
    private int lastTop;

    public ElementTextField(@NotNull Fragment parent)
    {
        super(parent);
    }

    public <T extends ElementTextField> T setDefaultText(String s)
    {
        defaultText = s;
        return (T)this;
    }

    public <T extends ElementTextField> T setValidator(Predicate<String> validator)
    {
        this.validator = validator;
        return (T)this;
    }

    public Predicate<String> getValidator()
    {
        return this.validator;
    }

    public <T extends ElementTextField> T setResponder(Consumer<String> responder)
    {
        this.responder = responder;
        return (T)this;
    }

    public Consumer<String> getResponder()
    {
        return this.responder;
    }

    public <T extends ElementTextField> T setEnterResponder(Consumer<String> responder)
    {
        this.enterResponder = responder;
        return (T)this;
    }

    public <T extends ElementTextField> T setMaxStringLength(int i)
    {
        this.maxStringLength = i;
        return (T)this;
    }

    public <T extends ElementTextField> T setTextFormatter(BiFunction<String, Integer, FormattedCharSequence> textFormatter)
    {
        this.textFormatter = textFormatter;
        return (T)this;
    }

    @Override
    public void init()
    {
        super.init();
        widget = new EditBox(getFontRenderer(), getLeft(), getTop(), width, height, Component.literal("Text Field"));
        widget.setMaxLength(maxStringLength);
        widget.setValue(defaultText);
        widget.setFilter(validator);
        widget.setResponder(responder);
        widget.setFormatter(textFormatter);
        //        children.add(widget);
        adjustWidget();

        lastLeft = getLeft();
        lastTop = getTop();
    }

    @Override
    public void tick()
    {
        super.tick();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        if(isMouseOver(mouseX, mouseY))
        {
            getWorkspace().cursorState = Workspace.CURSOR_IBEAM;
        }

        if(lastLeft != getLeft() || lastTop != getTop())
        {
            adjustWidget();
            lastLeft = getLeft();
            lastTop = getTop();
        }

        drawTextBox(graphics, mouseX, mouseY, partialTick);
    }

    public void drawTextBox(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        if(renderMinecraftStyle() > 0)
        {
            widget.setBordered(true);
            widget.render(graphics, mouseX, mouseY, partialTick);
        }
        else
        {
            PoseStack stack = graphics.pose();
            int[] colour;
            if(isMouseOver(mouseX, mouseY))
            {
                colour = getTheme().elementInputBackgroundHover;
            }
            else
            {
                colour = getTheme().elementInputBackgroundInactive;
            }
            fill(graphics, getTheme().elementInputBorder, 0);
            fill(graphics, colour, 1);
            widget.setBordered(false);
            widget.render(graphics, mouseX, mouseY, partialTick);
        }
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
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
            if(renderMinecraftStyle() > 0)
            {
                widget.setX(getLeft() + 1);
                widget.setY(getTop() + 1);
                widget.setWidth(this.width - 2);
                widget.height = (this.height - 2);
            }
            else
            {
                widget.setX(getLeft() + 5);
                widget.setY(getTop() + 1 + ((this.height - getFontRenderer().lineHeight) / 2));
                widget.setWidth(this.width - 6);
                widget.height = (this.height - 2);
            }
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int p_keyPressed_2_, int p_keyPressed_3_)
    {
        boolean flag = super.keyPressed(keyCode, p_keyPressed_2_, p_keyPressed_3_);
        if((keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) && enterResponder != null)
        {
            enterResponder.accept(getText());
        }
        return flag;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if(isMouseOver(mouseX, mouseY))
        {
            focus();
            if(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT)
            {
                widget.setValue("");
            }
            else if(button == GLFW.GLFW_MOUSE_BUTTON_MIDDLE)
            {
                widget.insertText(Minecraft.getInstance().keyboardHandler.getClipboard());
            }
            widget.mouseClicked(mouseX, mouseY, button);
            return true;
        }
        return false;
    }

    public void focus()
    {
        setFocused(widget);
        widget.setFocused(true);
    }

    @Override
    public void unfocus(@Nullable GuiEventListener guiReplacing)
    {
        super.unfocus(guiReplacing);
        widget.setFocused(false);
        setFocused(null);
    }

    @Override
    public ComponentPath nextFocusPath(FocusNavigationEvent event)
    {
        if(parent.getFocused() != this)
        {
            setFocused(widget);
            widget.setFocused(true);
            return super.nextFocusPath(event);
        }
        return null;
    }

    public void setText(@NotNull String s) //ONLY do AFTER init
    {
        if(widget == null)
        {
            iChunUtil.LOGGER.error("You're trying to set a text field widget whilst it is still null. Use setDefaultText instead");
            return;
        }
        widget.setValue(s);
    }

    public String getText()
    {
        return widget.getValue();
    }

    public EditBox getTextField()
    {
        return widget;
    }

    @Override
    public int getMinHeight()
    {
        return 12;
    }
}

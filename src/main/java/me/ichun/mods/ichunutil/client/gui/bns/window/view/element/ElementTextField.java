package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import me.ichun.mods.ichunutil.client.gui.bns.Workspace;
import me.ichun.mods.ichunutil.client.gui.bns.window.Fragment;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

@SuppressWarnings("unchecked")
public class ElementTextField extends Element
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

    public ElementTextField(@Nonnull Fragment parent)
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
        widget = new EditBox(getFontRenderer(), getLeft(), getTop(), width, height, new TranslatableComponent("gui.ichunutil.element.textField")); //TODO update this narration message?
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
        if(widget != null)
        {
            widget.tick();
        }
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTick)
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

        drawTextBox(stack, mouseX, mouseY, partialTick);
    }

    public void drawTextBox(PoseStack stack, int mouseX, int mouseY, float partialTick)
    {
        if(renderMinecraftStyle() > 0)
        {
            widget.setBordered(true);
            widget.render(stack, mouseX, mouseY, partialTick);
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
            fill(stack, getTheme().elementInputBorder, 0);
            fill(stack, colour, 1);
            widget.setBordered(false);
            widget.render(stack, mouseX, mouseY, partialTick);
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
                widget.x = getLeft() + 1;
                widget.y = getTop() + 1;
                widget.setWidth(this.width - 2);
                widget.height = (this.height - 2); //no setter in fabric
            }
            else
            {
                widget.x = getLeft() + 5;
                widget.y = getTop() + 1 + ((this.height - getFontRenderer().lineHeight) / 2);
                widget.setWidth(this.width - 6);
                widget.height = (this.height - 2); //no setter in fabric
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
            setFocused(widget);
            widget.setFocus(true);
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

    @Override
    public void unfocus(@Nullable GuiEventListener guiReplacing)
    {
        super.unfocus(guiReplacing);
        widget.setFocus(false);
        setFocused(null);
    }

    @Override
    public boolean changeFocus(boolean direction)
    {
        if(parentFragment.getFocused() != this)
        {
            setFocused(widget);
            widget.setFocus(true);
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

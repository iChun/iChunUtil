package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import com.mojang.blaze3d.vertex.PoseStack;
import me.ichun.mods.ichunutil.client.gui.bns.Fragment;
import me.ichun.mods.ichunutil.client.gui.bns.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ElementTextWrapper extends Element<Fragment<?>>
{
    private List<String> text = new ArrayList<>();
    private List<String> textWrapped = new ArrayList<>();
    private boolean doNotWrap;
    public int longestLine;
    public @Nullable Integer color;

    public ElementTextWrapper(@NotNull Fragment<?> parent)
    {
        super(parent);
    }

    public <T extends ElementTextWrapper> T setText(List<String> text)
    {
        this.text.clear();
        this.text.addAll(text);
        return (T)this;
    }

    public <T extends ElementTextWrapper> T setText(String text)
    {
        this.text.clear();
        this.text.add(text);
        return (T)this;
    }

    public <T extends ElementTextWrapper> T setNoWrap()
    {
        this.doNotWrap = true;
        return (T)this;
    }

    public <T extends ElementTextWrapper> T setColor(Integer clr)
    {
        this.color = clr;
        return (T)this;
    }


    public List<String> getText()
    {
        return text;
    }

    @Override
    public void init()
    {
        super.init();
        if(!text.isEmpty() && setupText())
        {
            constraint.apply();

            parent.resize(getWorkspace().getMinecraft(), parent.getParentWidth(), parent.getParentHeight());
        }
    }

    @Override
    public void resize(Minecraft mc, int width, int height)
    {
        super.resize(mc, width, height);
        if(!text.isEmpty() && setupText())
        {
            constraint.apply();

            parent.resize(getWorkspace().getMinecraft(), parent.getParentWidth(), parent.getParentHeight());
        }
    }

    public boolean setupText()
    {
        int lines = textWrapped.size();
        int tooltipTextWidth = 0;
        for (String textLine : text)
        {
            int textLineWidth = getFontRenderer().width(textLine);

            if (textLineWidth > tooltipTextWidth)
            {
                tooltipTextWidth = textLineWidth;
            }
        }

        longestLine = tooltipTextWidth;
        if(doNotWrap)
        {
            textWrapped = text;
            return textWrapped.size() != lines;
        }

        boolean needsWrap = false;

        if (tooltipTextWidth > width - 4)
        {
            tooltipTextWidth = width - 4;
            needsWrap = true;
        }

        List<String> wrappedTextLines = new ArrayList<>();
        for(String textLine : text)
        {
            if(textLine.isEmpty())
            {
                wrappedTextLines.add(textLine);
                continue;
            }
            List<FormattedText> texts = getFontRenderer().getSplitter().splitLines(Component.literal(textLine), needsWrap ? tooltipTextWidth : longestLine, Style.EMPTY);
            for(FormattedText text : texts)
            {
                wrappedTextLines.add(text.getString());
            }
        }
        textWrapped = wrappedTextLines;
        return lines != textWrapped.size();
    }

    @Override
    public void render(PoseStack graphics, int mouseX, int mouseY, float partialTick)
    {
        int textX = getLeft() + 2;
        int textY = getTop() + 4;
        for (int lineNumber = 0; lineNumber < textWrapped.size(); ++lineNumber)
        {
            String line = textWrapped.get(lineNumber);
            drawString(graphics, line, (float)textX, (float)textY);
            textY += 12;
        }
    }

    @Override
    public void drawString(PoseStack graphics, String s, float posX, float posY)
    {
        if(renderMinecraftStyle() > 0)
        {
            getFontRenderer().drawShadow(graphics, s, posX, posY, color != null ? color : getMinecraftFontColour());
        }
        else
        {
            getFontRenderer().draw(graphics, s, posX, posY, color != null ? color : Theme.getAsHex(getTheme().font));
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) //we can't click on this
    {
        return false;
    }

    @Override
    public boolean changeFocus(boolean direction) //we can't change focus on this
    {
        return false;
    }

    @Override
    public int getMinWidth()
    {
        return doNotWrap ? longestLine : 4;
    }

    @Override
    public int getMinHeight()
    {
        return textWrapped.isEmpty() ? 12 : textWrapped.size() * 12 + 4;
    }

    @Override
    public int getMaxWidth()
    {
        return getParentWidth() - 4;
    }

    @Override
    public int getMaxHeight()
    {
        return getMinHeight();
    }
}

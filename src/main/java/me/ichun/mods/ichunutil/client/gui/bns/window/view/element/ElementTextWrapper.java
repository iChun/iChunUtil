package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import me.ichun.mods.ichunutil.client.gui.bns.Theme;
import me.ichun.mods.ichunutil.client.gui.bns.window.Fragment;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ElementTextWrapper extends Element //TODO image element
{
    private List<String> text = new ArrayList<>();
    private List<String> textWrapped = new ArrayList<>();
    private boolean doNotWrap;
    public int longestLine;
    public @Nullable Integer color;

    //TODO text formatter?

    public ElementTextWrapper(@Nonnull Fragment parent)
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

            parentFragment.resize(getWorkspace().getMinecraft(), parentFragment.getParentWidth(), parentFragment.getParentHeight());
        }
    }

    @Override
    public void resize(Minecraft mc, int width, int height)
    {
        super.resize(mc, width, height);
        if(!text.isEmpty() && setupText())
        {
            constraint.apply();

            parentFragment.resize(getWorkspace().getMinecraft(), parentFragment.getParentWidth(), parentFragment.getParentHeight());
        }
    }

    public boolean setupText()
    {
        int lines = textWrapped.size();
        int tooltipTextWidth = 0;
        for (String textLine : text)
        {
            int textLineWidth = getFontRenderer().getStringWidth(textLine);

            if (textLineWidth > tooltipTextWidth)
            {
                tooltipTextWidth = textLineWidth;
            }
        }

        textWrapped = text;
        if(doNotWrap)
        {
            longestLine = tooltipTextWidth;
            return textWrapped.size() != lines;
        }

        boolean needsWrap = false;

        if (tooltipTextWidth > width - 4)
        {
            tooltipTextWidth = width - 4;
            needsWrap = true;
        }

        if (needsWrap)
        {
            List<String> wrappedTextLines = new ArrayList<>();
            for(String textLine : text)
            {
                wrappedTextLines.addAll(getFontRenderer().listFormattedStringToWidth(textLine, tooltipTextWidth));
            }
            textWrapped = wrappedTextLines;
        }
        else
        {
            textWrapped = new ArrayList<>(text);
        }
        return lines != textWrapped.size();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick)
    {
        int textX = getLeft() + 2;
        int textY = getTop() + 4;
        for (int lineNumber = 0; lineNumber < textWrapped.size(); ++lineNumber)
        {
            String line = textWrapped.get(lineNumber);
            drawString(line, (float)textX, (float)textY);
            textY += 12;
        }
    }

    @Override
    public void drawString(String s, float posX, float posY)
    {
        if(renderMinecraftStyle())
        {
            getFontRenderer().drawStringWithShadow(s, posX, posY, color != null ? color : getMinecraftFontColour());
        }
        else
        {
            getFontRenderer().drawString(s, posX, posY, color != null ? color : Theme.getAsHex(getTheme().font));
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) //we can't click on this
    {
        return false;
    }

    @Override
    public boolean changeFocus(boolean direction)
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


    public static int getRandomColourForName(String s)
    {
        if(s.equalsIgnoreCase("System"))
        {
            return 0xffcc00;
        }
        else
        {
            return Math.abs(s.hashCode()) & 0xffffff;
        }
    }

    public static final Random RANDOM = new Random();
    public static TextFormatting getRandomTextFormattingColorForName(String s) //I know this can be cached but meh
    {
        if(s.equalsIgnoreCase("System"))
        {
            return TextFormatting.RED;
        }

        ArrayList<TextFormatting> formats = new ArrayList<>();
        for(int i = 1; i < 15; i++) // no black no red no white
        {
            if(i != 12) //no red
            {
                formats.add(TextFormatting.values()[i]);
            }
        }
        RANDOM.setSeed(Math.abs(s.hashCode()));
        return formats.get(RANDOM.nextInt(formats.size()));
    }

}

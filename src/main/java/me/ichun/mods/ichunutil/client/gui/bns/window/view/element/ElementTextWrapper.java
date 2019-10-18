package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import me.ichun.mods.ichunutil.client.gui.bns.window.Fragment;
import net.minecraft.client.Minecraft;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ElementTextWrapper extends Element<Fragment>
{
    private List<String> text = new ArrayList<>();
    private List<String> textWrapped = new ArrayList<>();


    public ElementTextWrapper(@Nonnull Fragment parent)
    {
        super(parent);
    }

    public ElementTextWrapper setText(List<String> text)
    {
        this.text = text;
        return this;
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
            textY += 10;
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
        return 4;
    }

    @Override
    public int getMinHeight()
    {
        return textWrapped.isEmpty() ? 12 : textWrapped.size() * 10 + 4;
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

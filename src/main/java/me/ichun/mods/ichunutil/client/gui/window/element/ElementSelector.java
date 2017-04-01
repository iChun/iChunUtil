package me.ichun.mods.ichunutil.client.gui.window.element;

import com.google.common.collect.Ordering;
import me.ichun.mods.ichunutil.client.gui.Theme;
import me.ichun.mods.ichunutil.client.gui.window.Window;
import me.ichun.mods.ichunutil.client.render.RendererHelper;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;

public class ElementSelector extends Element
{
    public String tooltip;

    public TreeMap<String, Object> choices = new TreeMap<>(Ordering.natural());

    public String selected;

    public ElementSelector(Window window, int x, int y, int w, int h, int ID, String tip, String selected)
    {
        super(window, x, y, w, h, ID, false);
        this.tooltip = tip;
        this.selected = selected;
    }

    @Override
    public void draw(int mouseX, int mouseY, boolean hover)
    {
        RendererHelper.drawColourOnScreen(parent.workspace.currentTheme.elementInputBorder[0], parent.workspace.currentTheme.elementInputBorder[1], parent.workspace.currentTheme.elementInputBorder[2], 255, getPosX(), getPosY(), width, height, 0);
        RendererHelper.drawColourOnScreen(parent.workspace.currentTheme.elementInputBackgroundInactive[0], parent.workspace.currentTheme.elementInputBackgroundInactive[1], parent.workspace.currentTheme.elementInputBackgroundInactive[2], 255, getPosX() + 1, getPosY() + 1, width - 2, height - 2, 0);
        boolean hoverLeft = mouseX >= posX && mouseX <= posX + height && mouseY >= posY && mouseY <= posY + height;
        if(hoverLeft)
        {
            if(Mouse.isButtonDown(0))
            {
                RendererHelper.drawColourOnScreen(parent.workspace.currentTheme.elementInputUpDownClick[0], parent.workspace.currentTheme.elementInputUpDownClick[1], parent.workspace.currentTheme.elementInputUpDownClick[2], 255, getPosX(), getPosY(), height, height, 0);
            }
            else
            {
                RendererHelper.drawColourOnScreen(parent.workspace.currentTheme.elementInputUpDownHover[0], parent.workspace.currentTheme.elementInputUpDownHover[1], parent.workspace.currentTheme.elementInputUpDownHover[2], 255, getPosX(), getPosY(), height, height, 0);
            }
        }
        else
        {
            RendererHelper.drawColourOnScreen(parent.workspace.currentTheme.elementInputBorder[0], parent.workspace.currentTheme.elementInputBorder[1], parent.workspace.currentTheme.elementInputBorder[2], 255, getPosX(), getPosY(), height, height, 0);
        }
        //◀
        parent.workspace.getFontRenderer().drawString("\u25C0", getPosX() + height - ((height + parent.workspace.getFontRenderer().getStringWidth("\u25C0")) / 2), getPosY() + height - ((height + parent.workspace.getFontRenderer().FONT_HEIGHT) / 2), Theme.getAsHex(parent.workspace.currentTheme.font), false);

        boolean hoverRight = mouseX >= posX + width - height && mouseX <= posX + width && mouseY >= posY && mouseY <= posY + height;
        if(hoverRight)
        {
            if(Mouse.isButtonDown(0))
            {
                RendererHelper.drawColourOnScreen(parent.workspace.currentTheme.elementInputUpDownClick[0], parent.workspace.currentTheme.elementInputUpDownClick[1], parent.workspace.currentTheme.elementInputUpDownClick[2], 255, getPosX() + width - height, getPosY(), height, height, 0);
            }
            else
            {
                RendererHelper.drawColourOnScreen(parent.workspace.currentTheme.elementInputUpDownHover[0], parent.workspace.currentTheme.elementInputUpDownHover[1], parent.workspace.currentTheme.elementInputUpDownHover[2], 255, getPosX() + width - height, getPosY(), height, height, 0);
            }
        }
        else
        {
            RendererHelper.drawColourOnScreen(parent.workspace.currentTheme.elementInputBorder[0], parent.workspace.currentTheme.elementInputBorder[1], parent.workspace.currentTheme.elementInputBorder[2], 255, getPosX() + width - height, getPosY(), height, height, 0);
        }
        //▶"
        parent.workspace.getFontRenderer().drawString("\u25B6", getPosX() + width - ((height + parent.workspace.getFontRenderer().getStringWidth("\u25B6")) / 2), getPosY() + height - ((height + parent.workspace.getFontRenderer().FONT_HEIGHT) / 2), Theme.getAsHex(parent.workspace.currentTheme.font), false);

        if(!choices.containsKey(selected))
        {
            if(choices.isEmpty())
            {
                selected = "";
            }
            else
            {
                selected = choices.entrySet().iterator().next().getKey();
            }
        }

        parent.workspace.getFontRenderer().drawString(parent.workspace.reString(selected, width - height - height), getPosX() + height + 2, getPosY() + 2, Theme.getAsHex(parent.workspace.currentTheme.font), false);
    }

    @Override
    public boolean onClick(int mouseX, int mouseY, int id)
    {
        if(id == 0)
        {
            String before = "";
            String after = "";
            ArrayList<String> names = new ArrayList<String>(choices.keySet());
            Collections.sort(names);
            for(int i = 0; i < names.size(); i++)
            {
                if(names.get(i).equals(selected))
                {
                    int beforeI = i - 1;
                    int afterI = i + 1;
                    if(i == 0)
                    {
                        beforeI = names.size() - 1;
                    }
                    if(i == names.size() - 1)
                    {
                        afterI = 0;
                    }
                    before = names.get(beforeI);
                    after = names.get(afterI);
                }
            }
            if(mouseX >= posX && mouseX <= posX + height && mouseY >= posY && mouseY <= posY + height)
            {
                selected = before;
            }
            else if(mouseX >= posX + width - height && mouseX <= posX + width && mouseY >= posY && mouseY <= posY + height)
            {
                selected = after;
            }
        }
        return false;
    }

    @Override
    public boolean mouseScroll(int mouseX, int mouseY, int k)
    {
        String before = "";
        String after = "";
        ArrayList<String> names = new ArrayList<String>(choices.keySet());
        Collections.sort(names);
        for(int i = 0; i < names.size(); i++)
        {
            if(names.get(i).equals(selected))
            {
                int beforeI = i - 1;
                int afterI = i + 1;
                if(i == 0)
                {
                    beforeI = names.size() - 1;
                }
                if(i == names.size() - 1)
                {
                    afterI = 0;
                }
                before = names.get(beforeI);
                after = names.get(afterI);
            }
        }
        if(k > 0)
        {
            selected = before;
        }
        else
        {
            selected = after;
        }
        return false;
    }

    @Override
    public String tooltip()
    {
        return tooltip;
    }
}

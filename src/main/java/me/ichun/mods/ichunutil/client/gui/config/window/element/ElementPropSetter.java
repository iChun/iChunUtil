package me.ichun.mods.ichunutil.client.gui.config.window.element;

import me.ichun.mods.ichunutil.client.gui.Theme;
import me.ichun.mods.ichunutil.client.gui.config.GuiConfigs;
import me.ichun.mods.ichunutil.client.gui.config.window.WindowSetIntArray;
import me.ichun.mods.ichunutil.client.gui.config.window.WindowSetKeyBind;
import me.ichun.mods.ichunutil.client.gui.config.window.WindowSetNestedIntArray;
import me.ichun.mods.ichunutil.client.gui.config.window.WindowSetStringArray;
import me.ichun.mods.ichunutil.client.gui.window.Window;
import me.ichun.mods.ichunutil.client.gui.window.element.*;
import me.ichun.mods.ichunutil.client.keybind.KeyBind;
import me.ichun.mods.ichunutil.client.render.RendererHelper;
import me.ichun.mods.ichunutil.common.core.config.ConfigBase;
import me.ichun.mods.ichunutil.common.core.config.ConfigHandler;
import me.ichun.mods.ichunutil.common.core.config.annotations.ConfigProp;
import me.ichun.mods.ichunutil.common.core.config.annotations.IntBool;
import me.ichun.mods.ichunutil.common.core.config.annotations.IntMinMax;
import me.ichun.mods.ichunutil.common.core.config.types.Colour;
import me.ichun.mods.ichunutil.common.core.config.types.NestedIntArray;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Mouse;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class ElementPropSetter extends Element
{
    public int spacerL;
    public int spacerR;
    public int spacerU;
    public int spacerD;

    public double sliderProg = 0.0D;

    public ArrayList<Tree> trees = new ArrayList<>();

    public Tree treeClicked;
    public int clickTimeout;
    public int saveTimeout;

    public boolean lmbDown;
    public boolean rmbDown;

    public int space;

    public int mX;
    public int mY;

    public String selectedIdentifier;

    public ElementPropSetter(Window window, int x, int y, int w, int h, int ID)
    {
        super(window, x, y, w, h, ID, false);
        spacerL = x;
        spacerR = parent.width - x - width;
        spacerU = y;
        spacerD = parent.height - y - height;
        selectedIdentifier = "";
    }

    @Override
    public void update()
    {
        if(clickTimeout > 0)
        {
            clickTimeout--;
        }
        if(saveTimeout > 0)
        {
            saveTimeout--;
            if(saveTimeout == 0)
            {
                save();
            }
        }
        for(Tree tree : trees)
        {
            if(tree.element != null)
            {
                tree.element.update();
            }
        }
    }

    @Override
    public void draw(int mouseX, int mouseY, boolean hover)
    {
        mX = mouseX;
        mY = mouseY;
        int x1 = getPosX();
        int x2 = getPosX() + width;
        int y1 = getPosY();
        int y2 = getPosY() + height;

        int longestName = 0;
        int treeHeight1 = 0;
        for(int i = 0; i < trees.size(); i++)
        {
            Tree tree = trees.get(i);
            treeHeight1 += tree.getHeight();
            if(parent.workspace.getFontRenderer().getStringWidth(tree.propInfo.name) > longestName)
            {
                longestName = parent.workspace.getFontRenderer().getStringWidth(tree.propInfo.name);
            }
        }
        space = Math.min(200, Math.max(width - 40 - longestName, 50));

        RendererHelper.endGlScissor();

        RendererHelper.startGlScissor(getPosX(), getPosY() - 1, width + 2, height + 3);

        if(treeHeight1 > height)
        {
            x2 -= 10;

            RendererHelper.drawColourOnScreen(parent.workspace.currentTheme.elementTreeScrollBarBorder[0], parent.workspace.currentTheme.elementTreeScrollBarBorder[1], parent.workspace.currentTheme.elementTreeScrollBarBorder[2], 255, x2 + 5, getPosY() + (height / 40), 2, height - ((height / 40) * 2), 0);

            RendererHelper.drawColourOnScreen(parent.workspace.currentTheme.elementTreeScrollBarBorder[0], parent.workspace.currentTheme.elementTreeScrollBarBorder[1], parent.workspace.currentTheme.elementTreeScrollBarBorder[2], 255, x2 + 1, getPosY() - 1 + ((height - (height / 11)) * sliderProg), 10, height / 10, 0);
            RendererHelper.drawColourOnScreen(parent.workspace.currentTheme.elementTreeScrollBar[0], parent.workspace.currentTheme.elementTreeScrollBar[1], parent.workspace.currentTheme.elementTreeScrollBar[2], 255, x2 + 2, getPosY() + ((height - (height / 11)) * sliderProg), 8, (height / 10) - 2, 0);

            int sbx1 = x2 + 1 - parent.posX;
            int sbx2 = sbx1 + 10;
            int sby1 = getPosY() - 1 - parent.posY;
            int sby2 = getPosY() + height - parent.posY;

            if(Mouse.isButtonDown(0) && mouseX >= sbx1 && mouseX <= sbx2 && mouseY >= sby1 && mouseY <= sby2)
            {
                sby1 += 10;
                sby2 -= 10;
                sliderProg = 1.0F - MathHelper.clamp((double)(sby2 - mouseY) / (double)(sby2 - sby1), 0.0D, 1.0D);
            }
        }

        RendererHelper.startGlScissor(getPosX(), getPosY(), width + 2, height + 2);

        GlStateManager.pushMatrix();
        GlStateManager.translate(0D, (double)-((treeHeight1 - height) * sliderProg), 0D);
        int treeHeight = 0;
        for(int i = 0; i < trees.size(); i++)
        {
            Tree tree = trees.get(i);

            tree.draw(mouseX, mouseY, hover, (x2 - x1), treeHeight, treeHeight1 > height, treeHeight1, Mouse.isButtonDown(0) && !lmbDown, Mouse.isButtonDown(1) && !rmbDown);

            treeHeight += tree.getHeight();
        }
        GlStateManager.popMatrix();

        RendererHelper.endGlScissor();

        if(parent.isTab)
        {
            RendererHelper.startGlScissor(parent.posX + 1, parent.posY + 1 + 12, parent.getWidth() - 2, parent.getHeight() - 2 - 12);
        }
        else
        {
            RendererHelper.startGlScissor(parent.posX + 1, parent.posY + 1, parent.getWidth() - 2, parent.getHeight() - 2);
        }

        if(parent.docked < 0)
        {
            RendererHelper.drawColourOnScreen(parent.workspace.currentTheme.elementTreeBorder[0], parent.workspace.currentTheme.elementTreeBorder[1], parent.workspace.currentTheme.elementTreeBorder[2], 255, x1 - 1, y1 - 1, (x2 - x1) + 1, 1, 0);
            RendererHelper.drawColourOnScreen(parent.workspace.currentTheme.elementTreeBorder[0], parent.workspace.currentTheme.elementTreeBorder[1], parent.workspace.currentTheme.elementTreeBorder[2], 255, x1 - 1, y1 - 1, 1, height + 2, 0);
            RendererHelper.drawColourOnScreen(parent.workspace.currentTheme.elementTreeBorder[0], parent.workspace.currentTheme.elementTreeBorder[1], parent.workspace.currentTheme.elementTreeBorder[2], 255, x1 - 1, y2 + 1, (x2 - x1) + 2, 1, 0);
            RendererHelper.drawColourOnScreen(parent.workspace.currentTheme.elementTreeBorder[0], parent.workspace.currentTheme.elementTreeBorder[1], parent.workspace.currentTheme.elementTreeBorder[2], 255, x2, y1 - 1, 1, height + 2, 0);
        }

        lmbDown = Mouse.isButtonDown(0);
        rmbDown = Mouse.isButtonDown(1);
    }

    @Override
    public void resized()
    {
        posX = spacerL;
        width = parent.width - posX - spacerR;
        posY = spacerU;
        height = parent.height - posY - spacerD;
        sliderProg = 0.0D;
        for(Tree tree : trees)
        {
            tree.resized();
        }
    }

    @Override
    public boolean mouseScroll(int mouseX, int mouseY, int k)
    {
        int treeHeight = 0;
        int treeHeight1 = 0;
        for(int i = 0; i < trees.size(); i++)
        {
            Tree tree = trees.get(i);
            treeHeight1 += tree.getHeight();
        }
        int scrollHeight = 0;
        if(treeHeight1 > height)
        {
            scrollHeight = (int)((height - treeHeight1) * sliderProg);
        }

        boolean reacted = false;
        for(int i = 0; i < trees.size(); i++)
        {
            Tree tree = trees.get(i);

            if(mX >= posX && mX < posX + width + (treeHeight1 > height ? 10 : 0) && mY >= posY + treeHeight + scrollHeight && mY < posY + treeHeight + scrollHeight + tree.getHeight() && tree.selected)
            {
                if(tree.element != null)
                {
                    tree.element.mouseScroll(mouseX, mouseY - scrollHeight, k);
                    saveTimeout = 10;
                }
                reacted = true;
                break;
            }

            treeHeight += tree.getHeight();
        }

        if(!reacted && treeHeight1 > height)
        {
            sliderProg += 0.05D * -k;
            sliderProg = MathHelper.clamp(sliderProg, 0.0D, 1.0D);
        }
        return false;//return true to say you're interacted with
    }

    @Override
    public String tooltip()
    {
        int treeHeight = 0;
        int treeHeight1 = 0;
        for(int i = 0; i < trees.size(); i++)
        {
            Tree tree = trees.get(i);
            treeHeight1 += tree.getHeight();
        }

        int scrollHeight = 0;
        if(treeHeight1 > height)
        {
            scrollHeight = (int)((height - treeHeight1) * sliderProg);
        }

        for(int i = 0; i < trees.size(); i++)
        {
            Tree tree = trees.get(i);

            if(mX >= posX && mX < posX + width + (treeHeight1 > height ? 10 : 0) && mY >= posY + treeHeight + scrollHeight && mY < posY + treeHeight + scrollHeight + tree.getHeight())
            {
                return tree.propInfo.comment;
            }

            treeHeight += tree.getHeight();
        }
        return null; //return null for no tooltip. This is localized.
    }

    public void createTree(ConfigBase conf, ConfigBase.PropInfo obj, int h)
    {
        trees.add(new Tree(conf, obj, h));
    }

    public void clickElement(Object obj)
    {
    }

    public void triggerParent()
    {
        parent.elementTriggered(this);
    }

    public void save()
    {
        ConfigBase config = null;
        for(Tree tree : trees)
        {
            if(tree.element != null)
            {
                tree.config.save();
                break;
            }
        }
        if(ConfigHandler.configKeybind.hasChanged())
        {
            ConfigHandler.configKeybind.save();
        }
    }

    public class Tree
    {
        public ConfigBase config;
        public ConfigBase.PropInfo propInfo;

        public Element element;

        private int theHeight;

        public boolean selected;

        public Tree(ConfigBase conf, ConfigBase.PropInfo obj, int h)
        {
            config = conf;
            propInfo = obj;
            theHeight = h;
            try
            {
                obj.field.setAccessible(true);
                Class clz = obj.field.getType();
                if(clz.equals(int.class) || clz.equals(Integer.class))
                {
                    int[] minmax = new int[] { Integer.MIN_VALUE, Integer.MAX_VALUE };
                    if(obj.field.isAnnotationPresent(IntMinMax.class))
                    {
                        IntMinMax minMax = obj.field.getAnnotation(IntMinMax.class);
                        minmax[0] = minMax.min();
                        minmax[1] = minMax.max();

                        element = new ElementNumberInput(parent, 0, 0, 200, 12, 0, "", 1, false, minmax[0], minmax[1], obj.field.getInt(conf));
                    }
                    else if(obj.field.isAnnotationPresent(IntBool.class))
                    {
                        element = new ElementToggle(parent, 0, 0, 0, 12, 0, false, 0, 0, "gui.yes", null, obj.field.getInt(conf) == 1);
                    }
                    else
                    {
                        element = new ElementNumberInput(parent, 0, 0, 200, 12, 0, "", 1, false, minmax[0], minmax[1], obj.field.getInt(conf));
                    }
                }
                else if(clz.equals(Colour.class))
                {
                    element = new ElementTextInput(parent, 0, 0, 40, 12, 0, "", Integer.toHexString(((Colour)obj.field.get(conf)).getColour())); //last arg is current text
                    ((ElementTextInput)element).textField.setMaxStringLength(6);
                }
                else if(clz.equals(String.class))
                {
                    element = new ElementTextInput(parent, 0, 0, 40, 12, 0, "", (String)obj.field.get(conf)); //last arg is current text
                }
                else if(clz.equals(KeyBind.class))
                {
                    element = new ElementButton(parent, 0, 0, 0, 12, 0, false, 0, 0, "");
                }
                else if(clz.equals(String[].class) || clz.equals(int[].class) || clz.equals(NestedIntArray.class))
                {
                    element = new ElementButton(parent, 0, 0, 0, 12, 0, false, 0, 0, "Set");
                }
            }
            catch(Exception ignored){}
        }

        public void resized()
        {

        }

        public int getHeight()
        {
            return theHeight;
        }

        public Tree draw(int mouseX, int mouseY, boolean hover, int width, int treeHeight, boolean hasScroll, int totalHeight, boolean clicking, boolean rClicking)
        {
            double scrollHeight = 0.0D;
            if(hasScroll)
            {
                scrollHeight = (height - totalHeight) * sliderProg;
            }
            boolean realBorder = mouseX >= posX && mouseX < posX + width && mouseY >= posY + treeHeight + scrollHeight && mouseY < posY + treeHeight + scrollHeight + theHeight;
            int offX = 0;
            int offY = 0;
            if(selected)
            {
                RendererHelper.drawColourOnScreen(parent.workspace.currentTheme.elementTreeItemBgSelect[0], parent.workspace.currentTheme.elementTreeItemBgSelect[1], parent.workspace.currentTheme.elementTreeItemBgSelect[2], 255, getPosX() + offX, getPosY() + offY + treeHeight, width, theHeight, 0);
            }
            else if(realBorder)
            {
                RendererHelper.drawColourOnScreen(parent.workspace.currentTheme.elementTreeItemBgHover[0], parent.workspace.currentTheme.elementTreeItemBgHover[1], parent.workspace.currentTheme.elementTreeItemBgHover[2], 255, getPosX() + offX, getPosY() + offY + treeHeight, width, theHeight, 0);
            }
            else
            {
                RendererHelper.drawColourOnScreen(parent.workspace.currentTheme.elementTreeItemBorder[0], parent.workspace.currentTheme.elementTreeItemBorder[1], parent.workspace.currentTheme.elementTreeItemBorder[2], 255, getPosX() + offX, getPosY() + offY + treeHeight, width, theHeight, 0);
            }

            if(realBorder && hasScroll)
            {
                if(mouseY > height + posY || mouseY <= posY)
                {
                    clicking = false;
                }
            }

            parent.workspace.getFontRenderer().drawString(parent.workspace.reString(propInfo.name, width - 60), getPosX() + offX + 4, getPosY() + offY + ((theHeight - parent.workspace.getFontRenderer().FONT_HEIGHT) / 2) + treeHeight, Theme.getAsHex(parent.workspace.currentTheme.font), false);

            propInfo.field.setAccessible(true);
            Class clz = propInfo.field.getType();

            boolean found = false;
            boolean obstructed = false;

            for(int i = parent.workspace.levels.size() - 1; i >= 0 ; i--)
            {
                if(found)
                {
                    break;
                }
                for(int j = 0; j < parent.workspace.levels.get(i).size(); j++)
                {
                    Window window = parent.workspace.levels.get(i).get(j);
                    if(window == parent)
                    {
                        found = true;
                        break;
                    }
                    if(mouseX >= window.posX && mouseX <= window.posX + window.getWidth() && mouseY >= window.posY && mouseY <= window.posY + window.getHeight())
                    {
                        obstructed = true;
                        break;
                    }
                }
            }

            if(realBorder && clicking && !obstructed)
            {
                selected = true;
                deselectOthers(trees);
                clickElement(propInfo);

                parent.workspace.elementSelected = element;
                parent.workspace.elementSelected.selected();
                parent.workspace.elementSelected.onClick(mouseX, mouseY - (int)scrollHeight, 0);

                if(clz.equals(KeyBind.class))
                {
                    if(((GuiConfigs)parent.workspace).keyBindTimeout == 0)
                    {
                        parent.workspace.addWindowOnTop(new WindowSetKeyBind((GuiConfigs)parent.workspace, 300, 200, 180, 80, "ichunutil.config.gui.hitKeys", config, propInfo).putInMiddleOfScreen());
                    }
                }
                else if(clz.equals(String[].class))
                {
                    parent.workspace.addWindowOnTop(new WindowSetStringArray((GuiConfigs)parent.workspace, 300, 200, 180, 80, config, propInfo).putInMiddleOfScreen());
                }
                else if(clz.equals(int[].class))
                {
                    parent.workspace.addWindowOnTop(new WindowSetIntArray((GuiConfigs)parent.workspace, 300, 200, 180, 80, config, propInfo).putInMiddleOfScreen());
                }
                else if(clz.equals(NestedIntArray.class))
                {
                    parent.workspace.addWindowOnTop(new WindowSetNestedIntArray((GuiConfigs)parent.workspace, 300, 200, 180, 80, config, propInfo).putInMiddleOfScreen());
                }

                if(clickTimeout > 0 && treeClicked == this)
                {
                    triggerParent();
                }

                treeClicked = this;
                clickTimeout = 10;
            }

            if(element != null)
            {
                int x = posX + width - space - 3 - 0;
                int y = posY + offY + ((theHeight - parent.workspace.getFontRenderer().FONT_HEIGHT) / 2) + treeHeight - 2;
                int w = space;

                if(element instanceof ElementToggle)
                {
                    ElementToggle toggle = (ElementToggle)element;
                    toggle.text = toggle.toggledState ? "gui.yes" : "gui.no";
                }
                else if(element instanceof ElementButton)
                {
                    ElementButton button = (ElementButton)element;
                    if(propInfo.field.getType().equals(KeyBind.class))
                    {
                        w -= 10;
                        x += 10;

                        String keyString = "";
                        try
                        {
                            keyString = ((KeyBind)propInfo.field.get(config)).serialize();
                            String[] strings = keyString.split(":");

                            int key = 0;
                            if(strings.length > 0)
                            {
                                key = Integer.parseInt(strings[0].trim());
                            }

                            button.text = GameSettings.getKeyDisplayString(key);
                        }
                        catch(Exception ignored)
                        {
                        }

                        GlStateManager.pushMatrix();

                        float scale = 0.5F;
                        GlStateManager.scale(scale, scale, scale);
                        GlStateManager.translate(0.0F, -1F, 0.0F);

                        if(keyString.contains("SHIFT"))
                        {
                            parent.workspace.drawString(parent.workspace.getFontRenderer(), "Shift", (int)((button.getPosX() - 1) / scale) - parent.workspace.getFontRenderer().getStringWidth("Shift"), (int)(button.getPosY() / scale), parent.workspace.currentTheme.getAsHex(parent.workspace.currentTheme.font));
                            GlStateManager.translate(0.0F, 10F, 0.0F);
                        }

                        if(keyString.contains("CTRL"))
                        {
                            parent.workspace.drawString(parent.workspace.getFontRenderer(), "Ctrl", (int)((button.getPosX() - 1) / scale) - parent.workspace.getFontRenderer().getStringWidth("Ctrl"), (int)(button.getPosY() / scale), parent.workspace.currentTheme.getAsHex(parent.workspace.currentTheme.font));
                            GlStateManager.translate(0.0F, 10F, 0.0F);
                        }

                        if(keyString.contains("ALT"))
                        {
                            parent.workspace.drawString(parent.workspace.getFontRenderer(), "Alt", (int)((button.getPosX() - 1) / scale) - parent.workspace.getFontRenderer().getStringWidth("Alt"), (int)(button.getPosY() / scale), parent.workspace.currentTheme.getAsHex(parent.workspace.currentTheme.font));
                        }

                        GlStateManager.popMatrix();
                    }
                }
                else if(element instanceof ElementTextInput && propInfo.field.getType().equals(Colour.class))
                {
                    int clr = 0xff0000;
                    try
                    {
                        clr = Integer.decode("#" + ((ElementTextInput)element).textField.getText());
                    }
                    catch(NumberFormatException e)
                    {
                    }

                    parent.workspace.drawString(parent.workspace.getFontRenderer(), "#", x - posX + getPosX(), y - posY + getPosY() + 2, clr);

                    w -= 6;
                    x += 6;
                }

                try
                {
                    //Keybind save is handled elsewhere
                    if(clz.equals(int.class))
                    {
                        int min = Integer.MIN_VALUE;
                        int max = Integer.MAX_VALUE;
                        if(propInfo.field.isAnnotationPresent(IntMinMax.class))
                        {
                            IntMinMax minMax = propInfo.field.getAnnotation(IntMinMax.class);
                            min = minMax.min();
                            max = minMax.max();
                        }
                        else if(propInfo.field.isAnnotationPresent(IntBool.class))
                        {
                            min = 0;
                            max = 1;
                        }

                        int val = propInfo.field.getInt(config);
                        if(element instanceof ElementNumberInput)
                        {
                            val = Integer.parseInt(((ElementNumberInput)element).textFields.get(0).getText());
                        }
                        else if(element instanceof ElementToggle)
                        {
                            val = ((ElementToggle)element).toggledState ? 1 : 0;
                        }

                        if(val > max)
                        {
                            val = max;
                        }
                        else if(val < min)
                        {
                            val = min;
                        }

                        int oldVal = propInfo.field.getInt(config);
                        if(oldVal != val)
                        {
                            ConfigProp propInfo1 = propInfo.field.getAnnotation(ConfigProp.class);
                            if(!propInfo1.changeable() || propInfo1.useSession())
                            {
                                ((GuiConfigs)parent.workspace).needsRestart();
                            }
                            propInfo.field.set(config, val);
                            updateField(propInfo.field, oldVal);
                        }
                    }
                    else if(clz.equals(String.class))
                    {
                        String newText = ((ElementTextInput)element).textField.getText();
                        String oldText = (String)propInfo.field.get(config);
                        if(!newText.equals(oldText))
                        {
                            ConfigProp propInfo1 = propInfo.field.getAnnotation(ConfigProp.class);
                            if(!propInfo1.changeable() || propInfo1.useSession())
                            {
                                ((GuiConfigs)parent.workspace).needsRestart();
                            }
                            propInfo.field.set(config, newText);
                            updateField(propInfo.field, oldText);
                        }
                    }
                    else if(clz.equals(Colour.class))
                    {
                        try
                        {
                            int val = Integer.decode("#" + ((ElementTextInput)element).textField.getText());

                            Colour clr = (Colour)propInfo.field.get(config);
                            if(val != clr.getColour())
                            {
                                ConfigProp propInfo1 = propInfo.field.getAnnotation(ConfigProp.class);
                                if(!propInfo1.changeable() || propInfo1.useSession())
                                {
                                    ((GuiConfigs)parent.workspace).needsRestart();
                                }
                                int oriClr = clr.getColour();
                                clr.deserialize("#" + ((ElementTextInput)element).textField.getText());
                                updateField(propInfo.field, new Colour(oriClr));
                            }
                        }
                        catch(NumberFormatException e)
                        {
                        }
                    }
                }
                catch(Exception ignored){}

                boolean flag = x != element.posX || y != element.posY || w != element.width;

                element.posX = x;
                element.posY = y;
                element.width = w;

                if(flag)
                {
                    element.resized();
                }

                element.posX = x;
                element.posY = y;
                element.width = w;
                element.draw(mouseX, mouseY - (int)scrollHeight, realBorder || selected);
            }
            return null;
        }

        public void deselectOthers(ArrayList<Tree> trees)
        {
            for(Tree tree : trees)
            {
                if(tree != this && tree.selected)
                {
                    tree.selected = false;
                }
            }
        }

        public void updateField(Field field, Object ori)
        {
            config.onConfigChange(field, ori);
            saveTimeout = 10;
        }
    }
}

package us.ichun.mods.ichunutil.client.gui.config.window.element;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.config.Property;
import org.lwjgl.input.Mouse;
import us.ichun.mods.ichunutil.client.gui.Theme;
import us.ichun.mods.ichunutil.client.gui.config.window.WindowSetter;
import us.ichun.mods.ichunutil.client.gui.window.Window;
import us.ichun.mods.ichunutil.client.gui.window.element.*;
import us.ichun.mods.ichunutil.client.keybind.KeyBind;
import us.ichun.mods.ichunutil.client.render.RendererHelper;
import us.ichun.mods.ichunutil.common.core.config.ConfigBase;
import us.ichun.mods.ichunutil.common.core.config.annotations.IntBool;
import us.ichun.mods.ichunutil.common.core.config.annotations.IntMinMax;
import us.ichun.mods.ichunutil.common.core.config.types.Colour;

import java.util.ArrayList;

public class ElementPropSetter extends Element
{
    public int spacerL;
    public int spacerR;
    public int spacerU;
    public int spacerD;

    public double sliderProg = 0.0D;

    public ArrayList<Tree> trees = new ArrayList<Tree>();

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
                sliderProg = 1.0F - MathHelper.clamp_double((double)(sby2 - mouseY) / (double)(sby2 - sby1), 0.0D, 1.0D);
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
                    tree.element.mouseScroll(mouseX, mouseY, k);
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
            sliderProg = MathHelper.clamp_double(sliderProg, 0.0D, 1.0D);
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

    //    public boolean isValidValue(ConfigOld config, Property prop, String s)
    //    {
    //        switch(config.getPropType(prop))
    //        {
    //            case COLOUR:
    //            {
    //                try
    //                {
    //                    Integer.decode(s);
    //                    return s.length() < 8;
    //                }
    //                catch(NumberFormatException e)
    //                {
    //                    return false;
    //                }
    //            }
    //            case INT_BOOL:
    //            case INT:
    //            {
    //                try
    //                {
    //                    int i = Integer.parseInt(s);
    //                    int[] minmax = config.minmax.get(prop);
    //                    if(!(i >= minmax[0] && i <= minmax[1]))
    //                    {
    //                        return false;
    //                    }
    //                    return true;
    //                }
    //                catch(NumberFormatException e)
    //                {
    //                    return false;
    //                }
    //            }
    //            case KEYBIND:
    //            case STRING:
    //            {
    //                return true;
    //            }
    //TODO update these
    //            case INT_ARRAY:
    //            {
    //                try
    //                {
    //                    int i = Integer.parseInt(s);
    //                    int[] minmax = config.minmax.get(prop);
    //                    if(!(i >= minmax[0] && i <= minmax[1]))
    //                    {
    //                        return false;
    //                    }
    //                    if(intArrayList.contains(Integer.parseInt(s)))
    //                    {
    //                        return false;
    //                    }
    //                    return true;
    //                }
    //                catch(NumberFormatException e)
    //                {
    //                    return false;
    //                }
    //            }
    //            case NESTED_INT_ARRAY:
    //            {
    //                if(selected == selectedIntArrayProp + intArraySlots)
    //                {
    //                    try
    //                    {
    //                        int i = Integer.parseInt(s);
    //                        int[] minmax = config.minmax.get(prop);
    //                        if(!(i >= minmax[0] && i <= minmax[1]))
    //                        {
    //                            return false;
    //                        }
    //                        if(nestedIntArrayList.containsKey(Integer.parseInt(s)))
    //                        {
    //                            return false;
    //                        }
    //                        return true;
    //                    }
    //                    catch(NumberFormatException e)
    //                    {
    //                        return false;
    //                    }
    //                }
    //                else
    //                {
    //                    try
    //                    {
    //                        int i = Integer.parseInt(s);
    //                        int[] minmax = config.nestedMinmax.get(prop);
    //                        if(!(i >= minmax[0] && i <= minmax[1]))
    //                        {
    //                            return false;
    //                        }
    //
    //                        int m = 0;
    //                        Iterator<Map.Entry<Integer, ArrayList<Integer>>> ite = nestedIntArrayList.entrySet().iterator();
    //                        while(ite.hasNext())
    //                        {
    //                            Map.Entry<Integer, ArrayList<Integer>> e = ite.next();
    //                            m++;
    //                            if(selected - selectedIntArrayProp == m)
    //                            {
    //                                if(e.getValue().contains(Integer.parseInt(s)))
    //                                {
    //                                    return false;
    //                                }
    //                                break;
    //                            }
    //                        }
    //
    //                        return true;
    //                    }
    //                    catch(NumberFormatException e)
    //                    {
    //                        return false;
    //                    }
    //                }
    //            }
    //            default:
    //            {
    //                return false;
    //            }
    //        }
    //    }

        public boolean updateProperty(ConfigBase config, Property prop, String s){return true;}
    //    {
    //        String _default = prop.getString();
    //
    //        switch(config.getPropType(prop))
    //        {
    //            case INT_BOOL:
    //            case INT:
    //            {
    //                try
    //                {
    //                    int i = Integer.parseInt(s);
    //
    //                    int[] minmax = config.minmax.get(prop);
    //                    if(!(i >= minmax[0] && i <= minmax[1]))
    //                    {
    //                        return false;
    //                    }
    //
    //                    prop.set(i);
    //
    //                    Object def = null;
    //
    //                    if(config.sessionProps.contains(prop))
    //                    {
    //                        def = config.sessionState.get(Side.SERVER).get(prop.getName());
    //                        config.sessionState.get(Side.SERVER).put(prop.getName(), i);
    //                    }
    //
    //                    if(config.parent.onConfigChange(config, prop))
    //                    {
    //                        config.save();
    //
    //                        if(config.propNeedsRestart.contains(prop))
    //                        {
    //                            ((GuiConfigs)parent.workspace).needsRestart();
    //                        }
    //                    }
    //                    else
    //                    {
    //                        prop.set(_default);
    //
    //                        if(def != null)
    //                        {
    //                            config.sessionState.get(Side.SERVER).put(prop.getName(), def);
    //                        }
    //                        return false;
    //                    }
    //                    return true;
    //                }
    //                catch(NumberFormatException e)
    //                {
    //                    return false;
    //                }
    //            }
    //            //TODO fix these
    //            //            case INT_ARRAY:
    //            //            {
    //            //                StringBuilder sb = new StringBuilder();
    //            //
    //            //                for(int i = 0; i < intArrayList.size(); i++)
    //            //                {
    //            //                    sb.append(intArrayList.get(i));
    //            //                    if(i < intArrayList.size() - 1)
    //            //                    {
    //            //                        sb.append(", ");
    //            //                    }
    //            //                }
    //            //                prop.set(sb.toString());
    //            //
    //            //                break;
    //            //            }
    //            //            case NESTED_INT_ARRAY:
    //            //            {
    //            //                StringBuilder sb = new StringBuilder();
    //            //                int i = 0;
    //            //                for(Map.Entry<Integer, ArrayList<Integer>> e : nestedIntArrayList.entrySet())
    //            //                {
    //            //                    sb.append(e.getKey());
    //            //                    for(int m = 0; m < e.getValue().size(); m++)
    //            //                    {
    //            //                        if(m == 0)
    //            //                        {
    //            //                            sb.append(": ");
    //            //                        }
    //            //                        sb.append(e.getValue().get(m));
    //            //                        if(m < e.getValue().size() - 1)
    //            //                        {
    //            //                            sb.append(": ");
    //            //                        }
    //            //                    }
    //            //                    if(i < nestedIntArrayList.size() - 1 || e.getValue().isEmpty() && i < nestedIntArrayList.size() - 1)
    //            //                    {
    //            //                        sb.append(", ");
    //            //                    }
    //            //                    i++;
    //            //                }
    //            //                prop.set(sb.toString());
    //            //
    //            //                break;
    //            //            }
    //            case KEYBIND:
    //            {
    //                KeyBind oriKeyBind = config.keyBindMap.get(prop);
    //
    //                String keyString = s;
    //                String[] strings = keyString.split(":");
    //                KeyBind bind;
    //                try
    //                {
    //                    bind = new KeyBind(Integer.parseInt(strings[0].trim()), keyString.contains("SHIFT"), keyString.contains("CTRL"), keyString.contains("ALT"), false);
    //                }
    //                catch(Exception e)
    //                {
    //                    iChunUtil.console("Error parsing key, this shouldn't happen: " + keyString, true);
    //                    e.printStackTrace();
    //                    bind = oriKeyBind;
    //                }
    //
    //                config.keyBindMap.put(prop.getName(), iChunUtil.proxy.registerKeyBind(bind, oriKeyBind));
    //
    //                prop.set(s);
    //
    //                break;
    //            }
    //            case COLOUR:
    //            case STRING:
    //            {
    //                prop.set(s);
    //
    //                break;
    //            }
    //            default:
    //            {
    //                return false;
    //            }
    //        }
    //
    //        Object def = null;
    //
    //        if(config.sessionProps.contains(prop))
    //        {
    //            def = config.sessionState.get(Side.SERVER).get(prop.getName());
    //            config.sessionState.get(Side.SERVER).put(prop.getName(), prop.getString());
    //        }
    //
    //        if(config.parent.onConfigChange(config, prop))
    //        {
    //            if(config.getPropType(prop).equals(ConfigOld.EnumPropType.KEYBIND))
    //            {
    //                ConfigOld.configKeybind.save();
    //            }
    //            else
    //            {
    //                config.save();
    //            }
    //
    //            if(config.propNeedsRestart.contains(prop))
    //            {
    //                ((GuiConfigs)parent.workspace).needsRestart();
    //            }
    //        }
    //        else
    //        {
    //            prop.set(_default);
    //
    //            if(def != null)
    //            {
    //                config.sessionState.get(Side.SERVER).put(prop.getName(), def);
    //            }
    //            return false;
    //        }
    //        return true;
    //    }

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
                String val = "";
                if(tree.element instanceof ElementTextInput)
                {
                    val = ((ElementTextInput)tree.element).textField.getText();
                }
                else if(tree.element instanceof ElementToggle)
                {
                    val = Integer.toString(((ElementToggle)tree.element).toggledState ? 1 : 0);
                }
                else if(tree.element instanceof ElementNumberInput)
                {
                    val = ((ElementNumberInput)tree.element).textFields.get(0).getText();
                }
                else
                {
//                    val = tree.propInfo.prop.getString();
                }
                //                if(isValidValue(tree.config, tree.propInfo.prop, val))
                //                {
                //                    config = tree.config;
                //                    updateProperty(tree.config, tree.propInfo.prop, val);
                //                }
            }
        }
        if(config != null)
        {
            config.save();
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
                if(clz.equals(int.class))
                {
                    int[] minmax = new int[] { Integer.MIN_VALUE, Integer.MAX_VALUE };
                    if(obj.field.isAnnotationPresent(IntMinMax.class))
                    {
                        IntMinMax minMax = obj.field.getAnnotation(IntMinMax.class);
                        minmax[0] = minMax.min();
                        minmax[1] = minMax.max();

                        element = new ElementNumberInput(parent, 0, 0, 40, 12, 0, "", 1, false, minmax[0], minmax[1], obj.field.getInt(conf));
                    }
                    else if(obj.field.isAnnotationPresent(IntBool.class))
                    {
                        element = new ElementToggle(parent, 0, 0, 0, 12, 0, false, 0, 0, "gui.yes", null, obj.field.getInt(conf) == 1);
                    }
                }
                else if(clz.equals(Colour.class))
                {
                    element = new ElementTextInput(parent, 0, 0, 40, 12, 0, "", ((Colour)obj.field.get(conf)).getColour()); //last arg is current text
                }
                else if(clz.equals(String.class))
                {
                    element = new ElementTextInput(parent, 0, 0, 40, 12, 0, "", (String)obj.field.get(conf)); //last arg is current text
                }
                else if(clz.equals(KeyBind.class))
                {
                    element = new ElementButton(parent, 0, 0, 0, 12, 0, false, 0, 0, "");
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

            if(realBorder && clicking)
            {
                selected = true;
                deselectOthers(trees);
                clickElement(propInfo);

                parent.workspace.elementSelected = element;
                parent.workspace.elementSelected.selected();
                parent.workspace.elementSelected.onClick(mouseX, mouseY, 0);

                //                if(propInfo.type.equals(ConfigOld.EnumPropType.KEYBIND) && ((GuiConfigs)parent.workspace).keyBindTimeout == 0)
                //                {
                //                    parent.workspace.addWindowOnTop(new WindowSetKeyBind((GuiConfigs)parent.workspace, 300, 200, 180, 80, "ichun.config.gui.hitHeys", config, propInfo.prop).putInMiddleOfScreen());
                //                }

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
                else if(element instanceof ElementButton && propInfo.field.getType().equals(KeyBind.class))
                {
                    ElementButton button = (ElementButton)element;

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
                    catch(Exception ignored){}

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
                element.draw(mouseX, mouseY, realBorder || selected);
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
    }
}

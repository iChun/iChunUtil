package me.ichun.mods.ichunutil.client.gui.window.element;

import me.ichun.mods.ichunutil.client.gui.Theme;
import me.ichun.mods.ichunutil.client.gui.window.Window;
import me.ichun.mods.ichunutil.client.gui.window.WindowTopDockBase;
import me.ichun.mods.ichunutil.client.render.RendererHelper;
import me.ichun.mods.ichunutil.common.core.util.IOUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.translation.I18n;
import org.lwjgl.input.Mouse;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ElementListTree extends Element
{
    public int spacerL;
    public int spacerR;
    public int spacerU;
    public int spacerD;

    public double sliderProg = 0.0D;

    public ArrayList<Tree> trees = new ArrayList<>();

    public boolean canDrag;

    public Tree treeDragged;
    public int dragX;
    public int dragY;
    public double dragScroll;

    public Tree treeClicked;
    public int clickTimeout;

    public boolean lmbDown;
    public boolean rmbDown;

    public String selectedIdentifier;

    public static final ResourceLocation txModel = new ResourceLocation("tabula", "textures/icon/model.png");
    public static final ResourceLocation txGroup = new ResourceLocation("tabula", "textures/icon/group.png");

    public ElementListTree(Window window, int x, int y, int w, int h, int ID, boolean igMin, boolean drag)
    {
        super(window, x, y, w, h, ID, igMin);
        spacerL = x;
        spacerR = parent.width - x - width;
        spacerU = y;
        spacerD = parent.height - y - height;
        selectedIdentifier = "";
        canDrag = drag;
    }

    @Override
    public void update()
    {
        if(clickTimeout > 0)
        {
            clickTimeout--;
        }
    }

    @Override
    public void draw(int mouseX, int mouseY, boolean hover)
    {
        int x1 = getPosX();
        int x2 = getPosX() + width;
        int y1 = getPosY();
        int y2 = getPosY() + height;

        int treeHeight1 = 0;
        for(int i = 0; i < trees.size(); i++)
        {
            Tree tree = trees.get(i);
            treeHeight1 += tree.getHeight();
        }

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

        if(treeDragged != null && !(dragX == mouseX && dragY == mouseY))
        {
            treeHeight = 0;
            for(int i = 0; i < trees.size(); i++)
            {
                Tree tree = trees.get(i);

                if(tree == treeDragged)
                {
                    break;
                }

                treeHeight += tree.getHeight();
            }
            treeDragged.dragDraw = true;
            treeDragged.draw(mouseX, mouseY, hover, (x2 - x1), treeHeight, treeHeight1 > height, treeHeight1, Mouse.isButtonDown(0) && !lmbDown, Mouse.isButtonDown(1) && !rmbDown);
            treeDragged.dragDraw = false;
        }

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

        if(!Mouse.isButtonDown(0) && lmbDown)
        {
            if(treeDragged != null && !(dragX == mouseX && dragY == mouseY))
            {
                boolean unHook = true;
                treeHeight = 0;
                for(int i = 0; i < trees.size(); i++)
                {
                    Tree tree = trees.get(i);

                    double scrollHeight = 0.0D;
                    if(treeHeight1 > height)
                    {
                        scrollHeight = (height - treeHeight1) * sliderProg;
                    }
                    boolean realBorder = mouseX >= posX && mouseX < posX + width && mouseY >= posY + treeHeight + scrollHeight && mouseY < posY + treeHeight + scrollHeight + tree.theHeight;

                    if(realBorder)
                    {
                        if(treeHeight1 > height)
                        {
                            if(mouseY > height + posY || mouseY <= posY)
                            {
                                break;
                            }
                        }

                        unHook = false;
                        if(tree == treeDragged)
                        {
                            treeHeight += tree.getHeight();
                            continue;
                        }

                        dragOnto(tree.attachedObject, treeDragged.attachedObject);
                        break;
                    }

                    treeHeight += tree.getHeight();
                }
                if(unHook)
                {
                    dragOnto(null, treeDragged.attachedObject);
                }
            }

            treeDragged = null;
        }

        lmbDown = Mouse.isButtonDown(0);
        rmbDown = Mouse.isButtonDown(1);
    }

    public void dragOnto(Object draggedOn, Object dragged){}

    @Override
    public void resized()
    {
        posX = spacerL;
        width = parent.width - posX - spacerR;
        posY = spacerU;
        height = parent.height - posY - spacerD;
        sliderProg = 0.0D;
    }

    @Override
    public boolean mouseScroll(int mouseX, int mouseY, int k)
    {
        int treeHeight1 = 0;
        for(int i = 0; i < trees.size(); i++)
        {
            Tree tree = trees.get(i);
            treeHeight1 += tree.getHeight();
        }

        if(treeHeight1 > height)
        {
            sliderProg += 0.05D * -k;
            sliderProg = MathHelper.clamp(sliderProg, 0.0D, 1.0D);
        }
        return false;//return true to say you're interacted with
    }

    @Override
    public String tooltip()
    {
        return null; //return null for no tooltip. This is localized.
    }

    public void createTree(ResourceLocation loc, Object obj, int h, int attach, boolean expandable, boolean collapse)
    {
        trees.add(new Tree(loc, obj, h, attach, expandable, collapse));
    }

    public void clickElement(Object obj)
    {
        if(obj instanceof String)
        {
            selectedIdentifier = ((String)obj);
        }
        else if(obj instanceof IIdentifiable)
        {
            selectedIdentifier = ((IIdentifiable)obj).getIdentifier();
        }
    }

    public void rightClickElement(Object obj)
    {

    }

    public void triggerParent()
    {
        parent.elementTriggered(this);
    }

    public class Tree
    {
        public ResourceLocation txLoc;

        public Object attachedObject;

        private int theHeight;

        public boolean canExpand;
        public boolean collapsed;

        public int attached;// attachment level

        public boolean selected;

        public boolean dragDraw;

        public Tree(ResourceLocation loc, Object obj, int h, int attach, boolean expandable, boolean collapse)
        {
            txLoc = loc;
            attachedObject = obj;
            theHeight = h;
            attached = attach;
            canExpand = expandable;
            collapsed = collapse;
        }

        public int getHeight()
        {
            return theHeight;
        }

        public Tree draw(int mouseX, int mouseY, boolean hover, int width, int treeHeight, boolean hasScroll, int totalHeight, boolean clicking, boolean rClicking)
        {
            if(!(treeDragged == this && !(dragX == mouseX && dragY == mouseY)) || dragDraw)
            {
                double scrollHeight = 0.0D;
                if(hasScroll)
                {
                    scrollHeight = (height - totalHeight) * sliderProg;
                }
                boolean realBorder = mouseX >= posX && mouseX < posX + width && mouseY >= posY + treeHeight + scrollHeight && mouseY < posY + treeHeight + scrollHeight + theHeight;
                int offX = 0;
                int offY = 0;
                if(dragDraw)
                {
                    scrollHeight -= (height - totalHeight) * (sliderProg - dragScroll);
                    offX = mouseX - dragX;
                    offY = mouseY - dragY + (int)scrollHeight;
                }
                RendererHelper.drawColourOnScreen(parent.workspace.currentTheme.elementTreeItemBorder[0], parent.workspace.currentTheme.elementTreeItemBorder[1], parent.workspace.currentTheme.elementTreeItemBorder[2], 255, getPosX() + offX, getPosY() + offY + treeHeight, width, theHeight, 0);
                if(selected)
                {
                    RendererHelper.drawColourOnScreen(parent.workspace.currentTheme.elementTreeItemBgSelect[0], parent.workspace.currentTheme.elementTreeItemBgSelect[1], parent.workspace.currentTheme.elementTreeItemBgSelect[2], 255, getPosX() + offX + 1, getPosY() + offY + treeHeight + 1, width - 2, theHeight - 2, 0);
                }
                else if(realBorder)
                {
                    RendererHelper.drawColourOnScreen(parent.workspace.currentTheme.elementTreeItemBgHover[0], parent.workspace.currentTheme.elementTreeItemBgHover[1], parent.workspace.currentTheme.elementTreeItemBgHover[2], 255, getPosX() + offX + 1, getPosY() + offY + treeHeight + 1, width - 2, theHeight - 2, 0);
                }
                else
                {
                    RendererHelper.drawColourOnScreen(parent.workspace.currentTheme.elementTreeItemBg[0], parent.workspace.currentTheme.elementTreeItemBg[1], parent.workspace.currentTheme.elementTreeItemBg[2], 255, getPosX() + offX + 1, getPosY() + offY + treeHeight + 1, width - 2, theHeight - 2, 0);
                }

                if(realBorder && hasScroll)
                {
                    if(mouseY > height + posY || mouseY <= posY)
                    {
                        clicking = false;
                        rClicking = false;
                    }
                }

                if(attachedObject instanceof File)
                {
                    File info = (File)attachedObject;
                    parent.workspace.getFontRenderer().drawString(info.getName(), getPosX() + offX + 4, getPosY() + offY + 3 + treeHeight, Theme.getAsHex(parent.workspace.currentTheme.font), false);
                    parent.workspace.getFontRenderer().drawString((new SimpleDateFormat()).format(new Date(info.lastModified())), getPosX() + offX + 4, getPosY() + offY + 14 + treeHeight, Theme.getAsHex(parent.workspace.currentTheme.font), false);
                    parent.workspace.getFontRenderer().drawString(IOUtil.readableFileSize(info.length()), getPosX() + offX + width - 4 - parent.workspace.getFontRenderer().getStringWidth(IOUtil.readableFileSize(info.length())), getPosY() + offY + 3 + treeHeight, Theme.getAsHex(parent.workspace.currentTheme.font), false);
                }
                else if(attachedObject instanceof String)
                {
                    parent.workspace.getFontRenderer().drawString(parent.workspace.reString((String)attachedObject, width), getPosX() + offX + 4, getPosY() + offY + ((theHeight - parent.workspace.getFontRenderer().FONT_HEIGHT) / 2) + treeHeight, Theme.getAsHex(parent.workspace.currentTheme.font), false);
                }
                else if(attachedObject instanceof IListable)
                {
                    IListable info = (IListable)attachedObject;
                    parent.workspace.getFontRenderer().drawString(parent.workspace.reString(info.localizable() ? I18n.translateToLocal(info.getName()) : info.getName(), width), getPosX() + offX + 4, getPosY() + offY + ((theHeight - parent.workspace.getFontRenderer().FONT_HEIGHT) / 2) + treeHeight, Theme.getAsHex(parent.workspace.currentTheme.font), false);
                }

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
                        if(mouseX >= window.posX && mouseX <= window.posX + window.getWidth() && mouseY >= window.posY && mouseY <= window.posY + window.getHeight() && !(window instanceof WindowTopDockBase))
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
                    clickElement(attachedObject);

                    if(clickTimeout > 0 && treeClicked == this)
                    {
                        triggerParent();
                    }

                    treeClicked = this;
                    clickTimeout = 10;

                    if(canDrag)
                    {
                        treeDragged = this;
                        dragX = mouseX;
                        dragY = mouseY;
                        dragScroll = sliderProg;
                    }
                }
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

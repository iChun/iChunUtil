package us.ichun.mods.ichunutil.client.gui.window.element;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.input.Mouse;
import us.ichun.mods.ichunutil.client.gui.Theme;
import us.ichun.mods.ichunutil.client.gui.window.Window;
import us.ichun.mods.ichunutil.client.render.RendererHelper;
import us.ichun.mods.ichunutil.common.core.util.IOUtil;
import us.ichun.mods.ichunutil.common.module.tabula.client.model.ModelInfo;
import us.ichun.mods.ichunutil.common.module.tabula.common.project.components.Animation;
import us.ichun.mods.ichunutil.common.module.tabula.common.project.components.CubeGroup;
import us.ichun.mods.ichunutil.common.module.tabula.common.project.components.CubeInfo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public abstract class ElementListTree extends Element
{
    public int spacerL;
    public int spacerR;
    public int spacerU;
    public int spacerD;

    public double sliderProg = 0.0D;

    public ArrayList<Tree> trees = new ArrayList<Tree>();

    public boolean canDrag;

    public Tree treeDragged;
    public int dragX;
    public int dragY;

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

    public abstract void dragOnto(Object draggedOn, Object dragged);

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
            sliderProg = MathHelper.clamp_double(sliderProg, 0.0D, 1.0D);
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
        if(obj instanceof Animation)
        {
            selectedIdentifier = ((Animation)obj).identifier;
        }
    }

    public void rightClickElement(Object obj)
    {

    }

    public Object getObjectByIdentifier(String s)
    {
        for(Tree tree : trees)
        {
            if(tree.attachedObject instanceof CubeInfo && ((CubeInfo)tree.attachedObject).identifier.equals(s) || tree.attachedObject instanceof CubeGroup && ((CubeGroup)tree.attachedObject).identifier.equals(s) || tree.attachedObject instanceof Animation && ((Animation)tree.attachedObject).identifier.equals(s))
            {
                return tree.attachedObject;
            }
        }
        return null;
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

                if(attachedObject instanceof CubeInfo)
                {
                    CubeInfo info = (CubeInfo)attachedObject;
                    boolean hide = info.hidden;
                    int level = attached;
                    int index = -1;
                    for(int i = 0; i < trees.size(); i++)
                    {
                        Tree tree = trees.get(i);

                        if(tree == this)
                        {
                            index = i;
                            break;
                        }
                    }
                    while(level > 0 && !hide && index != -1)
                    {
                        Tree tree = trees.get(--index);
                        if(tree.attached == level - 1)//that means it's the parent
                        {
                            level = tree.attached;
                            if(tree.attachedObject instanceof CubeInfo)
                            {
                                hide = ((CubeInfo)tree.attachedObject).hidden;
                            }
                            else if(tree.attachedObject instanceof CubeGroup)
                            {
                                hide = ((CubeGroup)tree.attachedObject).hidden;
                            }
                        }
                    }
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    RendererHelper.drawTextureOnScreen(txModel, getPosX() + offX + 1.5D + (attached * 5), getPosY() + offY + ((theHeight - parent.workspace.getFontRenderer().FONT_HEIGHT) / 2) + treeHeight, theHeight - 4, theHeight - 4, 0);
                    parent.workspace.getFontRenderer().drawString(reString(info.name, width - 8), getPosX() + offX + 4 + 8 + (attached * 5), getPosY() + offY + ((theHeight - parent.workspace.getFontRenderer().FONT_HEIGHT) / 2) + treeHeight, hide ? Theme.getAsHex(parent.workspace.currentTheme.fontDim) : Theme.getAsHex(parent.workspace.currentTheme.font), false);
                    if(info.parentIdentifier == null && realBorder && rClicking)
                    {
                        rightClickElement(attachedObject);
                    }
                }
                else if(attachedObject instanceof CubeGroup)
                {
                    CubeGroup info = (CubeGroup)attachedObject;
                    boolean hide = info.hidden;
                    int level = attached;
                    int index = -1;
                    for(int i = 0; i < trees.size(); i++)
                    {
                        Tree tree = trees.get(i);

                        if(tree == this)
                        {
                            index = i;
                            break;
                        }
                    }
                    while(level > 0 && !hide && index != -1)
                    {
                        Tree tree = trees.get(--index);
                        if(tree.attached == level - 1)//that means it's the parent
                        {
                            level = tree.attached;
                            if(tree.attachedObject instanceof CubeInfo)
                            {
                                hide = ((CubeInfo)tree.attachedObject).hidden;
                            }
                            else if(tree.attachedObject instanceof CubeGroup)
                            {
                                hide = ((CubeGroup)tree.attachedObject).hidden;
                            }
                        }
                    }
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    RendererHelper.drawTextureOnScreen(txGroup, getPosX() + offX + 1.5D + (attached * 5), getPosY() + offY + ((theHeight - parent.workspace.getFontRenderer().FONT_HEIGHT) / 2) + treeHeight, theHeight - 4, theHeight - 4, 0);
                    parent.workspace.getFontRenderer().drawString(reString(info.name, width - 8), getPosX() + offX + 4 + 8 + (attached * 5), getPosY() + offY + ((theHeight - parent.workspace.getFontRenderer().FONT_HEIGHT) / 2) + treeHeight, hide ? Theme.getAsHex(parent.workspace.currentTheme.fontDim) : Theme.getAsHex(parent.workspace.currentTheme.font), false);
                    if(realBorder && rClicking)
                    {
                        rightClickElement(attachedObject);
                    }
                }
                else if(attachedObject instanceof ModelInfo)
                {
                    ModelInfo info = (ModelInfo)attachedObject;
                    parent.workspace.getFontRenderer().drawString(reString(info.modelParent.getClass().getSimpleName() + " - " + info.clz.getSimpleName(), width), getPosX() + offX + 4, getPosY() + offY + ((theHeight - parent.workspace.getFontRenderer().FONT_HEIGHT) / 2) + treeHeight, Theme.getAsHex(parent.workspace.currentTheme.font), false);
                }
                else if(attachedObject instanceof File)
                {
                    File info = (File)attachedObject;
                    parent.workspace.getFontRenderer().drawString(info.getName(), getPosX() + offX + 4, getPosY() + offY + 3 + treeHeight, Theme.getAsHex(parent.workspace.currentTheme.font), false);
                    parent.workspace.getFontRenderer().drawString((new SimpleDateFormat()).format(new Date(info.lastModified())), getPosX() + offX + 4, getPosY() + offY + 14 + treeHeight, Theme.getAsHex(parent.workspace.currentTheme.font), false);
                    parent.workspace.getFontRenderer().drawString(IOUtil.readableFileSize(info.length()), getPosX() + offX + width - 4 - parent.workspace.getFontRenderer().getStringWidth(IOUtil.readableFileSize(info.length())), getPosY() + offY + 3 + treeHeight, Theme.getAsHex(parent.workspace.currentTheme.font), false);
                }
                else if(attachedObject instanceof Theme)
                {
                    Theme theme = (Theme)attachedObject;
                    parent.workspace.getFontRenderer().drawString(reString(theme.name + " - " + theme.author, width), getPosX() + offX + 4, getPosY() + offY + ((theHeight - parent.workspace.getFontRenderer().FONT_HEIGHT) / 2) + treeHeight, Theme.getAsHex(parent.workspace.currentTheme.font), false);
                }
                else if(attachedObject instanceof Animation)
                {
                    Animation anim = (Animation)attachedObject;
                    parent.workspace.getFontRenderer().drawString(reString(anim.name, width), getPosX() + offX + 4, getPosY() + offY + ((theHeight - parent.workspace.getFontRenderer().FONT_HEIGHT) / 2) + treeHeight, Theme.getAsHex(parent.workspace.currentTheme.font), false);
                }
                else if(attachedObject instanceof String)
                {
                    parent.workspace.getFontRenderer().drawString(reString((String)attachedObject, width), getPosX() + offX + 4, getPosY() + offY + ((theHeight - parent.workspace.getFontRenderer().FONT_HEIGHT) / 2) + treeHeight, Theme.getAsHex(parent.workspace.currentTheme.font), false);
                }
                else if(attachedObject instanceof IListable)
                {
                    IListable info = (IListable)attachedObject;
                    parent.workspace.getFontRenderer().drawString(reString(StatCollector.translateToLocal(info.getName()), width), getPosX() + offX + 4, getPosY() + offY + ((theHeight - parent.workspace.getFontRenderer().FONT_HEIGHT) / 2) + treeHeight, Theme.getAsHex(parent.workspace.currentTheme.font), false);
                }

                if(realBorder && clicking)
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

        public String reString(String s, int width)
        {
            while(s.length() > 1 && parent.workspace.getFontRenderer().getStringWidth(s) > width - 3 - (attached * 5))
            {
                if(s.startsWith("..."))
                {
                    break;
                }
                if(s.endsWith("..."))
                {
                    s = s.substring(0, s.length() - 5) + "...";
                }
                else
                {
                    s = s.substring(0, s.length() - 1) + "...";
                }
            }
            return s;
        }
    }
}

package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import me.ichun.mods.ichunutil.client.gui.bns.window.Fragment;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.View;
import me.ichun.mods.ichunutil.client.render.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unchecked")
public abstract class Element<M extends View> extends Fragment //TODO handle narration?
{
    public final static List<Element> INFERTILE = Collections.emptyList();

    public String tooltip;

    public Element(@Nonnull M parent) //TODO Make aligned element, which counts the gaps between elements and equally distributes widht/height to each
    {
        super(parent);
    }

    public <T extends Element> T setPos(int x, int y)
    {
        posX = x;
        posY = y;
        return (T)this;
    }

    public <T extends Element> T setSize(int width, int height)
    {
        this.width = width;
        this.height = height;
        return (T)this;
    }

    @Override
    public void init()
    {
        constraint.apply();
    }

    @Override
    public List<? extends IGuiEventListener> children()
    {
        return INFERTILE;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick){}

    @Override
    public void resize(Minecraft mc, int width, int height)
    {
        constraint.apply();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if(isMouseOver(mouseX, mouseY))
        {
            return true;
        }
        return false;
    }

    @Override
    public boolean changeFocus(boolean direction)
    {
        return parentFragment.getFocused() != this; //focus on us if we're not focused
    }

    @Override
    public @Nullable
    String tooltip(double mouseX, double mouseY)
    {
        return tooltip;
    }

    public void setTooltip(String s)
    {
        tooltip = s;
    }

    public enum ButtonState
    {
        IDLE,
        HOVER,
        CLICK
    }

    public static void renderMinecraftStyleButton(int posX, int posY, int width, int height, ButtonState state) // BUTTONS NEED TO BE LARGER THAN 3x3
    {
        Fragment.bindTexture(Fragment.VANILLA_WIDGETS);

        int yOffset = state == ButtonState.CLICK ? 0 : state == ButtonState.HOVER ? 2 : 1;
        if(height == 20 && width > 15)
        {
            //gg easy life
            int i = width - 28;
            int x = posX + 14;
            while(i > 0)
            {
                int dist = Math.min(i, 172);
                RenderHelper.draw(x, posY, dist, 20, 0, 14D / 256D, (14 + dist) / 256D, (46 + yOffset * 20) / 256D, (66 + yOffset * 20) / 256D); //draw body
                i -= dist;
                x += dist;
            }


            RenderHelper.draw(posX, posY, 14, 20, 0, 0D/256D, 14D/256D, (46 + yOffset * 20)/256D, (66 + yOffset * 20)/256D); //draw leftblock
            RenderHelper.draw(posX + width - 14, posY, 14, 20, 0, 186D/256D, 200D/256D, (46 + yOffset * 20)/256D, (66 + yOffset * 20)/256D); //draw leftblock
        }
        else if(height < 20 && width < 200) //default button length
        {
            RenderHelper.draw(posX, posY + height - (height - 3), (width - 3), (height - 3), 0, 0D/256D, (width - 3)/256D, ((66 - (height - 3)) + yOffset * 20)/256D, (66 + yOffset * 20)/256D); //draw bottomLeft
            RenderHelper.draw(posX, posY, (width - 3), (height - 3), 0, 0D/256D, (width - 3)/256D, (46 + yOffset * 20)/256D, (46 + (height - 3) + yOffset * 20)/256D); //draw topLeft
            RenderHelper.draw(posX + width - (width - 3), posY, (width - 3), (height - 3), 0, (200D - (width - 3))/256D, 200D/256D, (46 + yOffset * 20)/256D, (46 + (height - 3) + yOffset * 20)/256D); //draw topRight
            RenderHelper.draw(posX + width - (width - 3), posY + height - (height - 3), (width - 3), (height - 3), 0, (200 - (width - 3))/256D, 200D/256D, (66 - (height - 3) + yOffset * 20)/256D, (66 + yOffset * 20)/256D); //draw topRight
        }
        else //big bois
        {
            int ii = width - 10;
            int xx = posX + 5;
            while(ii > 0)
            {
                int jj = height - 10;
                int yy = posY + 5;
                int distx = Math.min(ii, 190);
                while(jj > 0)
                {
                    int disty = Math.min(jj, 14);
                    RenderHelper.draw(xx, yy, distx, disty, 0, 5D / 256D, (5 + distx) / 256D, (49 + yOffset * 20) / 256D, (49 + disty + yOffset * 20) / 256D); //draw body
                    jj -= disty;
                    yy += disty;
                }
                ii -= distx;
                xx += distx;
            }

            int i = width - 10;
            int x = posX + 5;
            while(i > 0)
            {
                int dist = Math.min(i, 190);
                RenderHelper.draw(x, posY, dist, 5, 0, 5D / 256D, (5 + dist) / 256D, (46 + yOffset * 20) / 256D, (46 + 5 + yOffset * 20) / 256D); //draw body
                RenderHelper.draw(x, posY + height - 5, dist, 5, 0, 5D / 256D, (5 + dist) / 256D, (61 + yOffset * 20) / 256D, (61 + 5 + yOffset * 20) / 256D); //draw body
                i -= dist;
                x += dist;
            }

            i = height - 6;
            x = posY + 3;
            while(i > 0)
            {
                int dist = Math.min(i, 14);
                RenderHelper.draw(posX, x, 5, dist, 0, 0D / 256D, 5D / 256D, (46 + 3 + yOffset * 20) / 256D, (46 + 3 + dist + yOffset * 20) / 256D); //draw body
                RenderHelper.draw(posX + width - 5, x, 5, dist, 0, 195D / 256D, 200D / 256D, (46 + 3 + yOffset * 20) / 256D, (46 + 3 + dist + yOffset * 20) / 256D); //draw body
                i -= dist;
                x += dist;
            }

            RenderHelper.draw(posX, posY + height - 5, 5, 5, 0, 0D/256D, 5D/256D, (61 + yOffset * 20)/256D, (66 + yOffset * 20)/256D); //draw bottomLeft
            RenderHelper.draw(posX, posY, 5, 5, 0, 0D/256D, 5D/256D, (46 + yOffset * 20)/256D, (51 + yOffset * 20)/256D); //draw topLeft
            RenderHelper.draw(posX + width - 5, posY, 5, 5, 0, 195D/256D, 200D/256D, (46 + yOffset * 20)/256D, (51 + yOffset * 20)/256D); //draw topRight
            RenderHelper.draw(posX + width - 5, posY + height - 5, 5, 5, 0, 195D/256D, 200D/256D, (61 + yOffset * 20)/256D, (66 + yOffset * 20)/256D); //draw topRight
        }
    }
}

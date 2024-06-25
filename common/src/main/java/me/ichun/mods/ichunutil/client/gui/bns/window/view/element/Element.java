package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import me.ichun.mods.ichunutil.client.gui.bns.Fragment;
import me.ichun.mods.ichunutil.client.gui.bns.TextureDefinition;
import me.ichun.mods.ichunutil.client.render.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("unchecked")
public abstract class Element<P extends Fragment<?>> extends Fragment<P>
{
    public final static List<Element<?>> INFERTILE = Collections.emptyList();

    public String tooltip;

    public Element(@NotNull P parent)
    {
        super(parent);
    }

    public <T extends Element<?>> T setTooltip(String s)
    {
        tooltip = s;
        return (T)this;
    }

    @Override
    @Nullable
    public String tooltip(double mouseX, double mouseY)
    {
        return tooltip;
    }

    @Override
    public void init()
    {
        constraint.apply();
    }

    @Override
    public List<? extends Fragment<?>> children()
    {
        return INFERTILE;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        return isMouseOver(mouseX, mouseY);
    }

    /**
     * This is here to bypass our mouseClicked and use Fragment's
     */
    public boolean defaultMouseClicked(double mouseX, double mouseY, int button)
    {
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick){}

    @Override
    public void resize(Minecraft mc, int width, int height)
    {
        constraint.apply();
    }

    @Override
    public ComponentPath nextFocusPath(FocusNavigationEvent event)
    {
        return parent.nextFocusPath(event);
    }

    public void renderMinecraftStyleButton(PoseStack stack, int posX, int posY, int width, int height, ButtonState state) // BUTTONS NEED TO BE LARGER THAN 3x3
    {
        ResourceLocation rl = resourceButton(state);
        Fragment.bindTexture(rl);

        TextureDefinition texButton = TEXDEF_BUTTON;
        if(height == 20 && width > 15)
        {
            BufferBuilder bufferbuilder = RenderHelper.startDrawBatch();

            //gg easy life
            int i = width - 28;
            int x = posX + 14;
            while(i > 0)
            {
                int dist = Math.min(i, 172);
                RenderHelper.drawBatch(stack, bufferbuilder, x, posY, dist, 20, 0, 14D / texButton.width(), (14 + dist) / texButton.width(), 0 / texButton.height(), 20 / texButton.height()); //draw body
                i -= dist;
                x += dist;
            }


            RenderHelper.drawBatch(stack, bufferbuilder, posX, posY, 14, 20, 0, 0D / texButton.width(), 14D / texButton.width(), 0 / texButton.height(), 20 / texButton.height()); //draw leftblock
            RenderHelper.drawBatch(stack, bufferbuilder, posX + width - 14, posY, 14, 20, 0, 186D / texButton.width(), 200D / texButton.width(), 0 / texButton.height(), 20 / texButton.height()); //draw leftblock

            RenderHelper.endDrawBatch(bufferbuilder);
        }
        else if(height < 20 && width < 200) //default button length
        {
            BufferBuilder bufferbuilder = RenderHelper.startDrawBatch();

            RenderHelper.drawBatch(stack, bufferbuilder, posX, posY + height - (height - 3), (width - 3), (height - 3), 0, 0D/texButton.width(), (width - 3)/texButton.width(), (20 - (height - 3))/texButton.height(), 20/texButton.height()); //draw bottomLeft
            RenderHelper.drawBatch(stack, bufferbuilder, posX, posY, (width - 3), (height - 3), 0, 0D/texButton.width(), (width - 3)/texButton.width(), 0/texButton.height(), (height - 3)/texButton.height()); //draw topLeft
            RenderHelper.drawBatch(stack, bufferbuilder, posX + width - (width - 3), posY, (width - 3), (height - 3), 0, (200D - (width - 3))/texButton.width(), 200D/texButton.width(), 0/texButton.height(), (height - 3)/texButton.height()); //draw topRight
            RenderHelper.drawBatch(stack, bufferbuilder, posX + width - (width - 3), posY + height - (height - 3), (width - 3), (height - 3), 0, (200 - (width - 3))/texButton.width(), 200D/texButton.width(), (20 - (height - 3))/texButton.height(), 20/texButton.height()); //draw bottomRight

            RenderHelper.endDrawBatch(bufferbuilder);
        }
        else //big bois
        {
            cropAndStitch(stack, posX, posY, width, height, texButton);
        }
    }

    public static void cropAndStitch(PoseStack stack, int posX, int posY, int width, int height, TextureDefinition def)
    {
        cropAndStitch(stack, posX, posY, width, height, def.cornerSize(), def.x1(), def.y1(), def.x2() - def.x1(), def.y2() - def.y1(), def.width(), def.height());
    }

    public static void cropAndStitch(PoseStack stack, int posX, int posY, int width, int height, int borderSize, double u, double v, int uLength, int vLength, double texWidth, double texHeight)
    {
        BufferBuilder bufferbuilder = RenderHelper.startDrawBatch();

        int ii = width - (borderSize * 2);
        int xx = posX + borderSize;
        while(ii > 0)
        {
            int jj = height - (borderSize * 2);
            int yy = posY + borderSize;
            int distx = Math.min(ii, uLength - (borderSize * 2));
            while(jj > 0)
            {
                int disty = Math.min(jj, vLength - (borderSize * 2));
                RenderHelper.drawBatch(stack, bufferbuilder, xx, yy, distx, disty, 0, (u + borderSize)/texWidth, ((u + borderSize) + distx)/texWidth, (v + borderSize)/texWidth, ((v + borderSize) + disty)/texWidth); //draw body
                jj -= disty;
                yy += disty;
            }
            ii -= distx;
            xx += distx;
        }


        int i = width - (borderSize * 2);
        int x = posX + borderSize;
        while(i > 0)
        {
            int dist = Math.min(i, uLength - (borderSize * 2));
            RenderHelper.drawBatch(stack, bufferbuilder, x, posY, dist, borderSize, 0, (u + borderSize)/texWidth, ((u + borderSize) + dist)/texWidth, v/texHeight, (v + borderSize)/texHeight); //draw top bar
            RenderHelper.drawBatch(stack, bufferbuilder, x, posY + height - borderSize, dist, borderSize, 0, (u + borderSize)/texWidth, ((u + borderSize) + dist)/texWidth, (v + vLength - borderSize)/texHeight, (v + vLength)/texHeight); //draw bottom bar
            i -= dist;
            x += dist;
        }

        i = height - (borderSize * 2);
        x = posY + borderSize;
        while(i > 0)
        {
            int dist = Math.min(i, vLength - (borderSize * 2));
            RenderHelper.drawBatch(stack, bufferbuilder, posX, x, borderSize, dist, 0, u/texWidth, (u + borderSize)/texWidth, (v + borderSize)/texWidth, ((v + borderSize) + dist)/texWidth); //draw left bar
            RenderHelper.drawBatch(stack, bufferbuilder, posX + width - borderSize, x, borderSize, dist, 0, (u + uLength - borderSize)/texWidth, (u + uLength)/texWidth, (v + borderSize)/texWidth, ((v + borderSize) + dist)/texWidth); //draw left bar
            i -= dist;
            x += dist;
        }

        RenderHelper.drawBatch(stack, bufferbuilder, posX, posY + height - borderSize, borderSize, borderSize, 0, u/texWidth, (u + borderSize)/texWidth, (v + vLength - borderSize)/texHeight, (v + vLength)/texHeight); //draw bottomLeft
        RenderHelper.drawBatch(stack, bufferbuilder, posX, posY, borderSize, borderSize, 0, u/texWidth, (u + borderSize)/texWidth, v/texHeight, (v + borderSize)/texHeight); //draw topLeft
        RenderHelper.drawBatch(stack, bufferbuilder, posX + width - borderSize, posY, borderSize, borderSize, 0, (u + uLength - borderSize)/texWidth, (u + uLength)/texWidth, v/texHeight, (v + borderSize)/texHeight); //draw topRight
        RenderHelper.drawBatch(stack, bufferbuilder, posX + width - borderSize, posY + height - borderSize, borderSize, borderSize, 0, (u + uLength - borderSize)/texWidth, (u + uLength)/texWidth, (v + vLength - borderSize)/texHeight, (v + vLength)/texHeight); //draw bottomRight

        RenderHelper.endDrawBatch(bufferbuilder);
    }

    public enum ButtonState
    {
        IDLE,
        HOVER,
        CLICK
    }

    public static class MousePos
    {
        int x;
        int y;

        public MousePos(int x, int y)
        {
            this.x = x;
            this.y = y;
        }
    }
}

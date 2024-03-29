package me.ichun.mods.ichunutil.api.common;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

public class PlacementCorrector
{
    @Nonnull
    public String type = "scale/translate/rotate(in degrees)";
    @Nonnull
    public String axis = "x/y/z";
    @Nonnull
    public Double amount = 0D;

    @OnlyIn(Dist.CLIENT)
    public void apply(MatrixStack stack)
    {
        switch(type)
        {
            case "scale":
            {
                switch(axis)
                {
                    case "all":
                    {
                        float scale = amount.floatValue();
                        stack.scale(scale, scale, scale);
                        break;
                    }
                    case "x":
                    {
                        stack.scale(amount.floatValue(), 1F, 1F);
                        break;
                    }
                    case "y":
                    {
                        stack.scale(1F, amount.floatValue(), 1F);
                        break;
                    }
                    case "z":
                    {
                        stack.scale(1F, 1F, amount.floatValue());
                        break;
                    }
                }
                break;
            }
            case "translate":
            {
                switch(axis)
                {
                    case "x":
                    {
                        stack.translate(amount, 0D, 0D);
                        break;
                    }
                    case "y":
                    {
                        stack.translate(0D, amount, 0D);
                        break;
                    }
                    case "z":
                    {
                        stack.translate(0D, 0D, amount);
                        break;
                    }
                }
                break;
            }
            case "rotate":
            {
                switch(axis)
                {
                    case "x":
                    {
                        stack.rotate(Vector3f.XP.rotationDegrees(amount.floatValue()));
                        break;
                    }
                    case "y":
                    {
                        stack.rotate(Vector3f.YP.rotationDegrees(amount.floatValue()));
                        break;
                    }
                    case "z":
                    {
                        stack.rotate(Vector3f.ZP.rotationDegrees(amount.floatValue()));
                        break;
                    }
                }
                break;
            }
        }
    }

    public static void multiplyStackWithStack(@Nonnull MatrixStack stack, @Nonnull MatrixStack otherStack)
    {
        MatrixStack.Entry entLast = stack.getLast();
        MatrixStack.Entry correctorLast = otherStack.getLast();

        entLast.getMatrix().mul(correctorLast.getMatrix());
        entLast.getNormal().mul(correctorLast.getNormal());
    }
}

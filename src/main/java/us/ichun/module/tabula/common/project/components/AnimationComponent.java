package us.ichun.module.tabula.common.project.components;

import net.minecraft.client.model.ModelRenderer;

public class AnimationComponent
{
    public double[] posChange = new double[3];
    public double[] rotChange = new double[3];
    public double[] scaleChange = new double[3];

    public int startKey;
    public int length;

    public void animate(ModelRenderer rend, int time)
    {

    }

    public void reset(ModelRenderer rend, int time)
    {

    }
}

package us.ichun.module.tabula.common.project.components;

import net.minecraft.client.model.ModelRenderer;
import org.apache.commons.lang3.RandomStringUtils;
import us.ichun.module.tabula.common.project.ProjectInfo;

public class AnimationComponent
{
    public double[] posChange = new double[3];
    public double[] rotChange = new double[3];
    public double[] scaleChange = new double[3];
    public double opacityChange = 0.0D;

    public String name;

    public int length;
    public int startKey;

    public boolean hidden;

    public String identifier;

    public AnimationComponent(String name, int length, int startKey)
    {
        this.name = name;
        this.length = length;
        this.startKey = startKey;
        identifier = RandomStringUtils.randomAscii(ProjectInfo.IDENTIFIER_LENGTH);
    }

    public void animate(ModelRenderer rend, int time)
    {

    }

    public void reset(ModelRenderer rend, int time)
    {

    }
}

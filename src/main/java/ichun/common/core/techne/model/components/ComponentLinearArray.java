package ichun.common.core.techne.model.components;

import org.lwjgl.opengl.GL11;

public class ComponentLinearArray extends ComponentGroup
{
    public int countX;
    public int countY;
    public int countZ;

    public float spaceX;
    public float spaceY;
    public float spaceZ;

    //TODO this
    @Override
    protected void renderGroup(float f5)
    {
        if(countX == 0 && countY == 0 && countZ == 0)
        {
            return;
        }
        for(int i = 0; i < countX; i++)
        {
            for(int j = 0; j < countY; j++)
            {
                for(int k = 0; k < countZ; k++)
                {
                    GL11.glPushMatrix();

                    GL11.glTranslatef((spaceX * i) / 16F, (spaceY * j) / 16F, (spaceZ * k) / 16F);

                    groupModels.render(f5);

                    GL11.glPopMatrix();
                }
            }
        }
    }
}

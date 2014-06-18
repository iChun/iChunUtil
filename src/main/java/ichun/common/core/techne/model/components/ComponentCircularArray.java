package ichun.common.core.techne.model.components;

import org.lwjgl.opengl.GL11;

public class ComponentCircularArray extends ComponentGroup
{
    public float radius;
    public int count;

    //TODO this
    @Override
    protected void renderGroup(float f5)
    {
        for(int i = 0; i < count; i++)
        {
            GL11.glPushMatrix();

            float angle = 360F / (float)count * i;

            if(angle != 0.0F)
            {
                GL11.glRotatef(angle, 0.0F, 1.0F, 0.0F);
            }
            groupModels.render(f5);

            GL11.glPopMatrix();
        }
    }
}

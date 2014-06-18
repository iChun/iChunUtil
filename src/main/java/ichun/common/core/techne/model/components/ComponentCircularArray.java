package ichun.common.core.techne.model.components;

import org.lwjgl.opengl.GL11;

public class ComponentCircularArray extends ComponentGroup
{
    public float radius;
    public int count;

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

            GL11.glTranslatef(radius / 16F, 0.0F, 0.0F);

            //ModelRenderers with rotations in them are intentionally set to 0 when importing because I could not get support properly working with Techne 2 in such a broken state at the time of writing this code.
            groupModels.render(f5);

            GL11.glPopMatrix();
        }
    }
}

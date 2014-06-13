package ichun.common.core.techne.model.components;

import org.lwjgl.opengl.GL11;

/**
 * This is equivalent to the null element in Techne2. Circular and Linear Arrays extends this.
 */
public class ComponentGroup
{
    public float posX;
    public float posY;
    public float posZ;

    //Rotations are in radians.
    public float rotationX;
    public float rotationY;
    public float rotationZ;

    public GroupModels groupModels = new GroupModels();

    public void render(float f5)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef(posX / 16F, posY / 16F, posZ / 16F);

        if(rotationY != 0.0F)
        {
            GL11.glRotatef((float)Math.toDegrees(rotationY), 0.0F, 1.0F, 0.0F);
        }
        if(rotationX != 0.0F)
        {
            GL11.glRotatef((float)Math.toDegrees(rotationX), 1.0F, 0.0F, 0.0F);
        }
        if(rotationZ != 0.0F)
        {
            GL11.glRotatef((float)Math.toDegrees(rotationZ), 0.0F, 0.0F, 1.0F);
        }

        renderGroup(f5);

        GL11.glPopMatrix();
    }

    /**
     * Overriden in other classes to do other things before rendering.
     * @param f5
     */
    protected void renderGroup(float f5)
    {
        groupModels.render(f5);
    }
}

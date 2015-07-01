package us.ichun.mods.ichunutil.common.module.tabula.client.model;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;
import net.minecraft.client.model.ModelBase;

public class ModelRotationPoint extends ModelBase
{
	private Sphere sphere = new Sphere();
	private int sphereID;
	
	public ModelRotationPoint()
	{
		this(0.05F, 32, 32);
	}

	public ModelRotationPoint(float radius, int slices, int stacks)
	{
		this.sphere.setDrawStyle(GLU.GLU_FILL);
		this.sphere.setNormals(GLU.GLU_SMOOTH);
		this.sphere.setOrientation(GLU.GLU_OUTSIDE);
		this.sphereID = GL11.glGenLists(1);
		GL11.glNewList(this.sphereID, GL11.GL_COMPILE);
		GL11.glTranslatef( -radius, -radius, -radius);
		// GlStateManager.bindTexture(Color.CYAN.getRGB());
		this.sphere.draw(radius, 32, 32);
		GL11.glEndList();
	}

	public void destroy()
	{
		GL11.glDeleteLists(this.sphereID, 1);
	}

	public void render(float f5)
	{
		GL11.glPushMatrix();
		GL11.glTranslatef(f5, f5, f5);
		GL11.glCallList(this.sphereID);
		GL11.glPopMatrix();
	}
}

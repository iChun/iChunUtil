package us.ichun.mods.ichunutil.common.module.tabula.client.model;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;

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
		this.sphereID = GLAllocation.generateDisplayLists(1);
		GL11.glNewList(this.sphereID, GL11.GL_COMPILE);
		GlStateManager.translate( -radius, -radius, -radius);
		// GlStateManager.bindTexture(Color.CYAN.getRGB());
		this.sphere.draw(radius, 32, 32);
		GL11.glEndList();
	}

	public void destroy()
	{
		GLAllocation.deleteDisplayLists(this.sphereID);
	}

	public void render(float f5)
	{
		GlStateManager.pushMatrix();
		GlStateManager.translate(f5, f5, f5);
		GlStateManager.callList(this.sphereID);
		GlStateManager.popMatrix();
	}
}

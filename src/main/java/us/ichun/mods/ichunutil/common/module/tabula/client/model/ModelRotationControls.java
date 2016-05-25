package us.ichun.mods.ichunutil.common.module.tabula.client.model;

import org.lwjgl.opengl.GL11;
import net.minecraft.client.model.ModelBase;

/**
 * ModelRotationControls - Distjubo (re-)Created using his brain 4.1.0 (You may
 * change this comment if you want :-D)
 */
public class ModelRotationControls extends ModelBase
{
	private int torusID;

	public ModelRotationControls()
	{
		// TODO are those really the numbers we want to have?
		// maybe make this a config option, because performance drops with
		// more rings
		this(0.5F, 0.05F, 32, 4);
	}
	
	/**
	 * Generates a new model for a torus (donut-shape).
	 *
	 * @param R
	 *            radius of the big circle
	 * @param r
	 *            radius of the small rings
	 * @param N
	 *            number of rings
	 * @param n
	 *            number of points on the small rings
	 */
	private ModelRotationControls(float R, float r, int N, int n)
	{
		this.torusID = GL11.glGenLists(1);
		// Create that donut!
		GL11.glNewList(this.torusID, GL11.GL_COMPILE);
		GL11.glTranslatef( -r, -r, -r);
		float rr = 1.5f*r;
		double dv = 2*Math.PI/n;
		double dw = 2*Math.PI/N;
		double w = 0.0f;
		while(w<2*Math.PI+dw)
		{
			double v = 0.0f;
			GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
			while(v<2*Math.PI+dv)
			{
				GL11.glNormal3d((R+rr*Math.cos(v))*Math.cos(w)-(R+r*Math.cos(v))*Math.cos(w), (R+rr*Math.cos(v))*Math.sin(w)-(R+r*Math.cos(v))*Math.sin(w), (rr*Math.sin(v)-r*Math.sin(v)));
				GL11.glVertex3d((R+r*Math.cos(v))*Math.cos(w), (R+r*Math.cos(v))*Math.sin(w), r*Math.sin(v));
				GL11.glNormal3d((R+rr*Math.cos(v+dv))*Math.cos(w+dw)-(R+r*Math.cos(v+dv))*Math.cos(w+dw), (R+rr*Math.cos(v+dv))*Math.sin(w+dw)-(R+r*Math.cos(v+dv))*Math.sin(w+dw), rr*Math.sin(v+dv)-r*Math.sin(v+dv));
				GL11.glVertex3d((R+r*Math.cos(v+dv))*Math.cos(w+dw), (R+r*Math.cos(v+dv))*Math.sin(w+dw), r*Math.sin(v+dv));
				v += dv;
			}
			GL11.glEnd();
			w += dw;
		}
		GL11.glEndList();
	}
	
	public void destroy()
	{
		GL11.glDeleteLists(this.torusID, 1);
	}
	
	public void render(float f5)
	{
		GL11.glPushMatrix();
		GL11.glTranslatef(f5, f5, f5);
		GL11.glCallList(this.torusID);
		GL11.glPopMatrix();
	}
}

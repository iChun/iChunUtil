package us.ichun.module.tabula.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;
import us.ichun.module.tabula.common.project.ProjectInfo;
import us.ichun.module.tabula.common.project.components.CubeInfo;

import java.util.ArrayList;

public class ModelBaseDummy extends ModelBase
{
    public ProjectInfo parent;
    public ArrayList<CubeInfo> cubes = new ArrayList<CubeInfo>();

    public ModelRotationPoint rotationPoint;

    public ModelBaseDummy(ProjectInfo par)
    {
        parent = par;
        textureHeight = parent.textureHeight;
        textureWidth = parent.textureWidth;

        rotationPoint = new ModelRotationPoint();
    }

    @Override
    public void render(Entity ent, float f, float f1, float f2, float f3, float f4, float f5)
    {
    }

    public void render(float f5, ArrayList<CubeInfo> cubesToSelect, float zoomLevel, boolean hasTexture, int pass)
    {
        for(int i = cubes.size() - 1; i >= 0 ; i--)
        {
            CubeInfo info = cubes.get(i);
            if(info.modelCube != null)
            {
                if(cubesToSelect.isEmpty() && pass == 1)
                {
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    info.modelCube.render(f5);
                }
                else if(cubesToSelect.contains(info))
                {
                    if(pass == 0)
                    {
                        GL11.glColor4f(0.0F, 0.0F, 0.7F, 0.8F);

                        if(hasTexture)
                        {
                            GL11.glDisable(GL11.GL_TEXTURE_2D);
                        }
                        GL11.glDisable(GL11.GL_LIGHTING);
                        GL11.glEnable(GL11.GL_CULL_FACE);
                        GL11.glPushMatrix();
                        GL11.glTranslatef(info.modelCube.offsetX, info.modelCube.offsetY, info.modelCube.offsetZ);
                        GL11.glTranslatef(info.modelCube.rotationPointX * f5, info.modelCube.rotationPointY * f5, info.modelCube.rotationPointZ * f5);

                        rotationPoint.render(f5);

                        //TODO check for child models?
                        GL11.glPopMatrix();
                        GL11.glDisable(GL11.GL_CULL_FACE);
                        GL11.glEnable(GL11.GL_LIGHTING);
                        if(hasTexture)
                        {
                            GL11.glEnable(GL11.GL_TEXTURE_2D);
                        }

                        //Render cube
                        GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.90F);//to allow rendering of the rotation point internally
                        info.modelCube.render(f5);

                        if(hasTexture)
                        {
                            GL11.glDisable(GL11.GL_TEXTURE_2D);
                        }
                        GL11.glDisable(GL11.GL_LIGHTING);
                        GL11.glPushMatrix();
                        GL11.glTranslatef(info.modelCube.offsetX, info.modelCube.offsetY, info.modelCube.offsetZ);
                        GL11.glTranslatef(info.modelCube.rotationPointX * f5, info.modelCube.rotationPointY * f5, info.modelCube.rotationPointZ * f5);

                        if(info.modelCube.rotateAngleZ != 0.0F)
                        {
                            GL11.glRotatef(info.modelCube.rotateAngleZ * (180F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
                        }

                        if(info.modelCube.rotateAngleY != 0.0F)
                        {
                            GL11.glRotatef(info.modelCube.rotateAngleY * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
                        }

                        if(info.modelCube.rotateAngleX != 0.0F)
                        {
                            GL11.glRotatef(info.modelCube.rotateAngleX * (180F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
                        }
                        GL11.glTranslated(info.offset[0] * f5, info.offset[1] * f5, info.offset[2] * f5);

                        float width = 4F * zoomLevel;
                        float border = width * f5 * 0.000625F;
                        GL11.glLineWidth(width);
                        GL11.glColor4f(0.9F, 0.9F, 0.0F, 0.6F);
                        GL11.glBegin(GL11.GL_LINES);

                        GL11.glVertex3f(-border, 0.0F, 0.0F);
                        GL11.glVertex3f(info.dimensions[0] * f5 + border, 0F, 0F);

                        GL11.glVertex3f(-border, info.dimensions[1] * f5, 0.0F);
                        GL11.glVertex3f(info.dimensions[0] * f5 + border, info.dimensions[1] * f5, 0F);

                        GL11.glVertex3f(-border, 0.0F, info.dimensions[2] * f5);
                        GL11.glVertex3f(info.dimensions[0] * f5 + border, 0F, info.dimensions[2] * f5);

                        GL11.glVertex3f(-border, info.dimensions[1] * f5, info.dimensions[2] * f5);
                        GL11.glVertex3f(info.dimensions[0] * f5 + border, info.dimensions[1] * f5, info.dimensions[2] * f5);

                        GL11.glVertex3f(0.0F, 0.0F, -border);
                        GL11.glVertex3f(0.0F, 0.0F, info.dimensions[2] * f5 + border);

                        GL11.glVertex3f(0.0F, info.dimensions[1] * f5, -border);
                        GL11.glVertex3f(0.0F, info.dimensions[1] * f5, info.dimensions[2] * f5 + border);

                        GL11.glVertex3f(info.dimensions[0] * f5, 0.0F, -border);
                        GL11.glVertex3f(info.dimensions[0] * f5, 0.0F, info.dimensions[2] * f5 + border);

                        GL11.glVertex3f(info.dimensions[0] * f5, info.dimensions[1] * f5, -border);
                        GL11.glVertex3f(info.dimensions[0] * f5, info.dimensions[1] * f5, info.dimensions[2] * f5 + border);

                        GL11.glVertex3f(0.0F, -border, 0.0F);
                        GL11.glVertex3f(0.0F, info.dimensions[1] * f5 + border, 0.0F);

                        GL11.glVertex3f(info.dimensions[0] * f5, -border, 0.0F);
                        GL11.glVertex3f(info.dimensions[0] * f5, info.dimensions[1] * f5 + border, 0.0F);

                        GL11.glVertex3f(0.0F, -border, info.dimensions[2] * f5);
                        GL11.glVertex3f(0.0F, info.dimensions[1] * f5 + border, info.dimensions[2] * f5);

                        GL11.glVertex3f(info.dimensions[0] * f5, -border, info.dimensions[2] * f5);
                        GL11.glVertex3f(info.dimensions[0] * f5, info.dimensions[1] * f5 + border, info.dimensions[2] * f5);

                        //                    GL11.glEnd();
                        //                    GL11.glBegin(GL11.GL_LINES);
                        //                    GL11.glVertex3f(0.0F, 2.0F, 0.0F);
                        //                    GL11.glVertex3f(15F, 2F, 0F);
                        GL11.glEnd();
                        //TODO check for child models?
                        GL11.glPopMatrix();
                        GL11.glEnable(GL11.GL_LIGHTING);
                        if(hasTexture)
                        {
                            GL11.glEnable(GL11.GL_TEXTURE_2D);
                        }
                    }
                }
                else if(pass == 1)
                {
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.3F);

                    info.modelCube.render(f5);
                }
            }
        }
    }

    public void createModelFromCubeInfo(CubeInfo info)
    {
        cubes.add(info);

        info.modelCube = new ModelRenderer(this, info.txOffset[0], info.txOffset[1]);
        info.modelCube.addBox((float)info.offset[0], (float)info.offset[1], (float)info.offset[2], info.dimensions[0], info.dimensions[1], info.dimensions[2]);
        info.modelCube.setRotationPoint((float)info.position[0], (float)info.position[1], (float)info.position[2]);
        info.modelCube.rotateAngleX = (float)Math.toRadians(info.rotation[0]);
        info.modelCube.rotateAngleY = (float)Math.toRadians(info.rotation[1]);
        info.modelCube.rotateAngleZ = (float)Math.toRadians(info.rotation[2]);
    }

    public void removeCubeInfo(CubeInfo info)
    {
        deleteModelDisplayList(info);
        cubes.remove(info);
    }

    private void deleteModelDisplayList(CubeInfo info)//Done to free up Graphics memory
    {
        for(CubeInfo info1 : info.children)
        {
            deleteModelDisplayList(info1);
        }
        if(info.modelCube != null && info.modelCube.compiled)
        {
            GLAllocation.deleteDisplayLists(info.modelCube.displayList);
            info.modelCube = null;
        }
    }
}

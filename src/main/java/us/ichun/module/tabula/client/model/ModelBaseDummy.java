package us.ichun.module.tabula.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;
import us.ichun.module.tabula.common.project.ProjectInfo;
import us.ichun.module.tabula.common.project.components.CubeInfo;

import java.lang.reflect.Array;
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

    public boolean isChild(ArrayList<CubeInfo> childList, CubeInfo cube)
    {
        boolean flag = false;
        for(CubeInfo cube1 : childList)
        {
            if(cube1.equals(cube))
            {
                return true;
            }
            if(!flag)
            {
                flag = isChild(cube1.getChildren(), cube);
            }
        }
        return flag;
    }

    public ArrayList<CubeInfo> getParents(CubeInfo info) // in reverse order.
    {
        ArrayList<CubeInfo> parents = new ArrayList<CubeInfo>();

        for(CubeInfo cube : cubes)
        {
            addIfParent(parents, cube, info);
        }

        return parents;
    }

    public void addIfParent(ArrayList<CubeInfo> parents, CubeInfo parent, CubeInfo cube)
    {
        for(CubeInfo children : parent.getChildren())
        {
            addIfParent(parents, children, cube);
        }
        if(parent.getChildren().contains(cube) || !parents.isEmpty() && parent.getChildren().contains(parents.get(parents.size() - 1)))
        {
            parents.add(parent);
        }
    }

    public void render(float f5, ArrayList<CubeInfo> cubesToSelect, ArrayList<CubeInfo> cubesToHide, float zoomLevel, boolean hasTexture, int pass)
    {
        ArrayList<CubeInfo> cubesToRender = new ArrayList<CubeInfo>(cubesToSelect);
        ArrayList<CubeInfo> unrendered = new ArrayList<CubeInfo>(cubesToSelect);
        unrendered.removeAll(cubes);
        if(!unrendered.isEmpty())
        {
            for(CubeInfo cube : unrendered)
            {
                for(int i = cubes.size() - 1; i >= 0; i--)
                {
                    if(isChild(cubes.get(i).getChildren(), cube))
                    {
                        cubesToRender.add(cubes.get(i));
                    }
                }
            }
        }
        for(int i = cubes.size() - 1; i >= 0 ; i--)
        {
            CubeInfo info = cubes.get(i);
            if(info.modelCube != null)
            {
                if(cubesToRender.isEmpty() && pass == 1 && (!info.hidden && !cubesToHide.contains(info)))
                {
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

                    GL11.glPushMatrix();
                    GL11.glTranslatef(info.modelCube.offsetX, info.modelCube.offsetY, info.modelCube.offsetZ);
                    GL11.glTranslatef(info.modelCube.rotationPointX * f5, info.modelCube.rotationPointY * f5, info.modelCube.rotationPointZ * f5);
                    GL11.glScaled(info.scale[0], info.scale[1], info.scale[2]);
                    GL11.glTranslatef(-info.modelCube.offsetX, -info.modelCube.offsetY, -info.modelCube.offsetZ);
                    GL11.glTranslatef(-info.modelCube.rotationPointX * f5, -info.modelCube.rotationPointY * f5, -info.modelCube.rotationPointZ * f5);

                    info.modelCube.render(f5);

                    GL11.glPopMatrix();
                }
                else if(cubesToRender.contains(info))
                {
                    if(pass == 0)
                    {
                        if(cubesToRender.size() == 1)
                        {
                            GL11.glColor4f(0.0F, 0.0F, 0.7F, 0.8F);
                        }
                        else
                        {
                            int clr = Math.abs(info.identifier.hashCode()) & 0xffffff;
                            float r = (clr >> 16 & 0xff) / 255.0F;
                            float g = (clr >> 8 & 0xff) / 255.0F;
                            float b = (clr & 0xff) / 255.0F;
                            GL11.glColor4f(r, g, b, 0.8F);
                        }

                        renderSelectedCube(info, cubesToHide, f5, zoomLevel, hasTexture, unrendered.contains(info) || cubesToSelect.contains(info));
                    }
                }
                else if(pass == 1 && (!info.hidden && !cubesToHide.contains(info)))
                {
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.3F);

                    GL11.glPushMatrix();
                    GL11.glTranslatef(info.modelCube.offsetX, info.modelCube.offsetY, info.modelCube.offsetZ);
                    GL11.glTranslatef(info.modelCube.rotationPointX * f5, info.modelCube.rotationPointY * f5, info.modelCube.rotationPointZ * f5);
                    GL11.glScaled(info.scale[0], info.scale[1], info.scale[2]);
                    GL11.glTranslatef(-info.modelCube.offsetX, -info.modelCube.offsetY, -info.modelCube.offsetZ);
                    GL11.glTranslatef(-info.modelCube.rotationPointX * f5, -info.modelCube.rotationPointY * f5, -info.modelCube.rotationPointZ * f5);

                    info.modelCube.render(f5);

                    GL11.glPopMatrix();
                }
            }
        }
        for(CubeInfo info : unrendered)
        {
            if(pass == 0)
            {
                if(cubesToRender.size() == 1)
                {
                    GL11.glColor4f(0.0F, 0.0F, 0.7F, 0.8F);
                }
                else
                {
                    int clr = Math.abs(info.identifier.hashCode()) & 0xffffff;
                    float r = (clr >> 16 & 0xff) / 255.0F;
                    float g = (clr >> 8 & 0xff) / 255.0F;
                    float b = (clr & 0xff) / 255.0F;
                    GL11.glColor4f(r, g, b, 0.8F);
                }

                renderSelectedCube(info, cubesToHide, f5, zoomLevel, hasTexture, unrendered.contains(info) || cubesToSelect.contains(info));
            }
        }
    }

    public void renderSelectedCube(CubeInfo info, ArrayList<CubeInfo> hidden, float f5, float zoomLevel, boolean hasTexture, boolean focus)
    {
        if(hasTexture)
        {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
        }
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_CULL_FACE);

        ArrayList<CubeInfo> parents = getParents(info);

        GL11.glPushMatrix();

        if(parents.isEmpty())
        {
            GL11.glTranslatef(info.modelCube.offsetX, info.modelCube.offsetY, info.modelCube.offsetZ);
            GL11.glTranslatef(info.modelCube.rotationPointX * f5, info.modelCube.rotationPointY * f5, info.modelCube.rotationPointZ * f5);
            GL11.glScaled(info.scale[0], info.scale[1], info.scale[2]);
            GL11.glTranslatef(-info.modelCube.offsetX, -info.modelCube.offsetY, -info.modelCube.offsetZ);
            GL11.glTranslatef(-info.modelCube.rotationPointX * f5, -info.modelCube.rotationPointY * f5, -info.modelCube.rotationPointZ * f5);
        }

        for(int i = parents.size() - 1; i >= 0; i--)
        {
            CubeInfo parent = parents.get(i);

            if(i == parents.size() - 1)
            {
                GL11.glTranslatef(parent.modelCube.offsetX, parent.modelCube.offsetY, parent.modelCube.offsetZ);
                GL11.glTranslatef(parent.modelCube.rotationPointX * f5, parent.modelCube.rotationPointY * f5, parent.modelCube.rotationPointZ * f5);
                GL11.glScaled(parent.scale[0], parent.scale[1], parent.scale[2]);
                GL11.glTranslatef(-parent.modelCube.offsetX, -parent.modelCube.offsetY, -parent.modelCube.offsetZ);
                GL11.glTranslatef(-parent.modelCube.rotationPointX * f5, -parent.modelCube.rotationPointY * f5, -parent.modelCube.rotationPointZ * f5);
            }

            GL11.glTranslatef(parent.modelCube.offsetX, parent.modelCube.offsetY, parent.modelCube.offsetZ);
            GL11.glTranslatef(parent.modelCube.rotationPointX * f5, parent.modelCube.rotationPointY * f5, parent.modelCube.rotationPointZ * f5);

            if(parent.modelCube.rotateAngleZ != 0.0F)
            {
                GL11.glRotatef(parent.modelCube.rotateAngleZ * (180F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
            }

            if(parent.modelCube.rotateAngleY != 0.0F)
            {
                GL11.glRotatef(parent.modelCube.rotateAngleY * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
            }

            if(parent.modelCube.rotateAngleX != 0.0F)
            {
                GL11.glRotatef(parent.modelCube.rotateAngleX * (180F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
            }

        }

        GL11.glTranslatef(info.modelCube.offsetX, info.modelCube.offsetY, info.modelCube.offsetZ);
        GL11.glTranslatef(info.modelCube.rotationPointX * f5, info.modelCube.rotationPointY * f5, info.modelCube.rotationPointZ * f5);

        if(focus)
        {
            rotationPoint.render(f5);
        }

        GL11.glPopMatrix();

        GL11.glPushMatrix();

        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_LIGHTING);
        if(hasTexture)
        {
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }

        //Render cube
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.90F);//to allow rendering of the rotation point internally
        if(info.parentIdentifier == null && (!info.hidden && !hidden.contains(info)))//only render if it's not a child
        {
            GL11.glTranslatef(info.modelCube.offsetX, info.modelCube.offsetY, info.modelCube.offsetZ);
            GL11.glTranslatef(info.modelCube.rotationPointX * f5, info.modelCube.rotationPointY * f5, info.modelCube.rotationPointZ * f5);
            GL11.glScaled(info.scale[0], info.scale[1], info.scale[2]);
            GL11.glTranslatef(-info.modelCube.offsetX, -info.modelCube.offsetY, -info.modelCube.offsetZ);
            GL11.glTranslatef(-info.modelCube.rotationPointX * f5, -info.modelCube.rotationPointY * f5, -info.modelCube.rotationPointZ * f5);
            info.modelCube.render(f5);
        }

        if(hasTexture)
        {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
        }
        GL11.glDisable(GL11.GL_LIGHTING);

        if(focus)
        {
            GL11.glPushMatrix();

            for(int i = parents.size() - 1; i >= 0; i--)
            {
                CubeInfo parent = parents.get(i);

                if(i == parents.size() - 1)
                {
                    GL11.glTranslatef(parent.modelCube.offsetX, parent.modelCube.offsetY, parent.modelCube.offsetZ);
                    GL11.glTranslatef(parent.modelCube.rotationPointX * f5, parent.modelCube.rotationPointY * f5, parent.modelCube.rotationPointZ * f5);
                    GL11.glScaled(parent.scale[0], parent.scale[1], parent.scale[2]);
                    GL11.glTranslatef(-parent.modelCube.offsetX, -parent.modelCube.offsetY, -parent.modelCube.offsetZ);
                    GL11.glTranslatef(-parent.modelCube.rotationPointX * f5, -parent.modelCube.rotationPointY * f5, -parent.modelCube.rotationPointZ * f5);
                }

                GL11.glTranslatef(parent.modelCube.offsetX, parent.modelCube.offsetY, parent.modelCube.offsetZ);
                GL11.glTranslatef(parent.modelCube.rotationPointX * f5, parent.modelCube.rotationPointY * f5, parent.modelCube.rotationPointZ * f5);

                if(parent.modelCube.rotateAngleZ != 0.0F)
                {
                    GL11.glRotatef(parent.modelCube.rotateAngleZ * (180F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
                }

                if(parent.modelCube.rotateAngleY != 0.0F)
                {
                    GL11.glRotatef(parent.modelCube.rotateAngleY * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
                }

                if(parent.modelCube.rotateAngleX != 0.0F)
                {
                    GL11.glRotatef(parent.modelCube.rotateAngleX * (180F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
                }

            }

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

            GL11.glVertex3f(-border - (float)info.mcScale * f5, - (float)info.mcScale * f5, - (float)info.mcScale * f5);
            GL11.glVertex3f((float)(info.dimensions[0] + info.mcScale) * f5 + border, - (float)info.mcScale * f5, - (float)info.mcScale * f5);

            GL11.glVertex3f(-border - (float)info.mcScale * f5, (float)(info.dimensions[1] + info.mcScale) * f5, - (float)info.mcScale * f5);
            GL11.glVertex3f((float)(info.dimensions[0] + info.mcScale) * f5 + border, (float)(info.dimensions[1] + info.mcScale) * f5, - (float)info.mcScale * f5);

            GL11.glVertex3f(-border - (float)info.mcScale * f5, - (float)info.mcScale * f5, (float)(info.dimensions[2] + info.mcScale) * f5);
            GL11.glVertex3f((float)(info.dimensions[0] + info.mcScale) * f5 + border, - (float)info.mcScale * f5, (float)(info.dimensions[2] + info.mcScale) * f5);

            GL11.glVertex3f(-border - (float)info.mcScale * f5, (float)(info.dimensions[1] + info.mcScale) * f5, (float)(info.dimensions[2] + info.mcScale) * f5);
            GL11.glVertex3f((float)(info.dimensions[0] + info.mcScale) * f5 + border, (float)(info.dimensions[1] + info.mcScale) * f5, (float)(info.dimensions[2] + info.mcScale) * f5);

            GL11.glVertex3f(- (float)info.mcScale * f5, - (float)info.mcScale * f5, -border - (float)info.mcScale * f5);
            GL11.glVertex3f(- (float)info.mcScale * f5, - (float)info.mcScale * f5, (float)(info.dimensions[2] + info.mcScale) * f5 + border);

            GL11.glVertex3f(- (float)info.mcScale * f5, (float)(info.dimensions[1] + info.mcScale) * f5, -border - (float)info.mcScale * f5);
            GL11.glVertex3f(- (float)info.mcScale * f5, (float)(info.dimensions[1] + info.mcScale) * f5, (float)(info.dimensions[2] + info.mcScale) * f5 + border);

            GL11.glVertex3f((float)(info.dimensions[0] + info.mcScale) * f5, - (float)info.mcScale * f5, -border - (float)info.mcScale * f5);
            GL11.glVertex3f((float)(info.dimensions[0] + info.mcScale) * f5, - (float)info.mcScale * f5, (float)(info.dimensions[2] + info.mcScale) * f5 + border);

            GL11.glVertex3f((float)(info.dimensions[0] + info.mcScale) * f5, (float)(info.dimensions[1] + info.mcScale) * f5, -border - (float)info.mcScale * f5);
            GL11.glVertex3f((float)(info.dimensions[0] + info.mcScale) * f5, (float)(info.dimensions[1] + info.mcScale) * f5, (float)(info.dimensions[2] + info.mcScale) * f5 + border);

            GL11.glVertex3f(- (float)info.mcScale * f5, -border - (float)info.mcScale * f5, - (float)info.mcScale * f5);
            GL11.glVertex3f(- (float)info.mcScale * f5, (float)(info.dimensions[1] + info.mcScale) * f5 + border, - (float)info.mcScale * f5);

            GL11.glVertex3f((float)(info.dimensions[0] + info.mcScale) * f5, -border - (float)info.mcScale * f5, - (float)info.mcScale * f5);
            GL11.glVertex3f((float)(info.dimensions[0] + info.mcScale) * f5, (float)(info.dimensions[1] + info.mcScale) * f5 + border, - (float)info.mcScale * f5);

            GL11.glVertex3f(- (float)info.mcScale * f5, -border - (float)info.mcScale * f5, (float)(info.dimensions[2] + info.mcScale) * f5 );
            GL11.glVertex3f(- (float)info.mcScale * f5, (float)(info.dimensions[1] + info.mcScale) * f5 + border, (float)(info.dimensions[2] + info.mcScale) * f5 );

            GL11.glVertex3f((float)(info.dimensions[0] + info.mcScale) * f5, -border - (float)info.mcScale * f5, (float)(info.dimensions[2] + info.mcScale) * f5 );
            GL11.glVertex3f((float)(info.dimensions[0] + info.mcScale) * f5, (float)(info.dimensions[1] + info.mcScale) * f5 + border, (float)(info.dimensions[2] + info.mcScale) * f5 );

            GL11.glEnd();

            GL11.glPopMatrix();
        }
        GL11.glEnable(GL11.GL_LIGHTING);
        if(hasTexture)
        {
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }

        GL11.glPopMatrix();
    }

    public void createModelFromCubeInfo(CubeInfo info)
    {
        cubes.add(info);

        info.modelCube = new ModelRenderer(this, info.txOffset[0], info.txOffset[1]);
        info.modelCube.mirror = info.txMirror;
        info.modelCube.setRotationPoint((float)info.position[0], (float)info.position[1], (float)info.position[2]);
        info.modelCube.addBox((float)info.offset[0], (float)info.offset[1], (float)info.offset[2], info.dimensions[0], info.dimensions[1], info.dimensions[2], (float)info.mcScale);
        info.modelCube.rotateAngleX = (float)Math.toRadians(info.rotation[0]);
        info.modelCube.rotateAngleY = (float)Math.toRadians(info.rotation[1]);
        info.modelCube.rotateAngleZ = (float)Math.toRadians(info.rotation[2]);

        createChildren(info);
    }

    public void createChildren(CubeInfo cube)
    {
        for(CubeInfo child : cube.getChildren())
        {
            child.modelCube = new ModelRenderer(this, child.txOffset[0], child.txOffset[1]);
            child.modelCube.mirror = child.txMirror;
            child.modelCube.addBox((float)child.offset[0], (float)child.offset[1], (float)child.offset[2], child.dimensions[0], child.dimensions[1], child.dimensions[2]);
            child.modelCube.setRotationPoint((float)child.position[0], (float)child.position[1], (float)child.position[2]);
            child.modelCube.rotateAngleX = (float)Math.toRadians(child.rotation[0]);
            child.modelCube.rotateAngleY = (float)Math.toRadians(child.rotation[1]);
            child.modelCube.rotateAngleZ = (float)Math.toRadians(child.rotation[2]);

            cube.modelCube.addChild(child.modelCube);

            createChildren(child);
        }
    }

    public void removeCubeInfo(CubeInfo info)
    {
        deleteModelDisplayList(info);
        cubes.remove(info);
    }

    private void deleteModelDisplayList(CubeInfo info)//Done to free up Graphics memory
    {
        for(CubeInfo info1 : info.getChildren())
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

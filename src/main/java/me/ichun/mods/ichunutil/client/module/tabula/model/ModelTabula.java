package me.ichun.mods.ichunutil.client.module.tabula.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.Entity;
import me.ichun.mods.ichunutil.common.module.tabula.project.ProjectInfo;
import me.ichun.mods.ichunutil.common.module.tabula.project.components.CubeGroup;
import me.ichun.mods.ichunutil.common.module.tabula.project.components.CubeInfo;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Model class used to render a ProjectInfo (Tabula format) in game. Not meant to be discarded by GC, no memory freeing is done in this class.
 * Meant to used on Projects which have a texture as well.
 * If you want to use a Model you can discard and recreate without leaking memory, use ModelBaseDummy in the same package.
 */
public class ModelTabula extends ModelBase
{
    public final ProjectInfo projectInfo;
    public ArrayList<CubeInfo> cubes;

    public ModelTabula(ProjectInfo info)
    {
        this.projectInfo = info;
        this.textureHeight = projectInfo.textureHeight;
        this.textureWidth = projectInfo.textureWidth;

        this.cubes = new ArrayList<>();

        for(int i = 0; i < projectInfo.cubeGroups.size(); i++)
        {
            createGroupCubes(projectInfo.cubeGroups.get(i));
        }
        for(int i = 0 ; i < projectInfo.cubes.size(); i++)
        {
            projectInfo.cubes.get(i).createModel(this);
            cubes.add(projectInfo.cubes.get(i));
        }
    }

    @Override
    public void render(Entity ent, float f, float f1, float f2, float f3, float f4, float f5)
    {
        render(f5, false, false, 1F, 1F, 1F, 1F);
    }

    public void render(float f5, boolean useTexture, boolean useOpacity)
    {
        render(f5, useTexture, useOpacity, 1F, 1F, 1F, 1F);
    }

    public void render(float f5, boolean useTexture, boolean useOpacity, float r, float g, float b, float alpha)
    {
        if(useTexture && projectInfo.bufferedTexture != null)
        {
            if(projectInfo.bufferedTextureId == -1)
            {
                projectInfo.bufferedTextureId = TextureUtil.uploadTextureImage(TextureUtil.glGenTextures(), projectInfo.bufferedTexture);
            }
            GlStateManager.bindTexture(projectInfo.bufferedTextureId);
        }

        GlStateManager.pushMatrix();

        GlStateManager.scale(1D / projectInfo.scale[0], 1D / projectInfo.scale[1], 1D / projectInfo.scale[2]);

        cubes.stream().filter(info -> info.modelCube != null && !info.hidden).forEach(info ->
        {
            GlStateManager.pushMatrix();
            if(useOpacity)
            {
                GlStateManager.color(r, g, b, alpha * (float)(info.opacity / 100D));
            }

            if(!(info.scale[0] == 1D && info.scale[1] == 1D && info.scale[2] == 1D))
            {
                GlStateManager.translate(info.modelCube.offsetX, info.modelCube.offsetY, info.modelCube.offsetZ);
                GlStateManager.translate(info.modelCube.rotationPointX * f5, info.modelCube.rotationPointY * f5, info.modelCube.rotationPointZ * f5);
                GlStateManager.scale(info.scale[0], info.scale[1], info.scale[2]);
                GlStateManager.translate(-info.modelCube.offsetX, -info.modelCube.offsetY, -info.modelCube.offsetZ);
                GlStateManager.translate(-info.modelCube.rotationPointX * f5, -info.modelCube.rotationPointY * f5, -info.modelCube.rotationPointZ * f5);
            }

            info.modelCube.render(f5);

            GlStateManager.popMatrix();
        });

        GlStateManager.popMatrix();
    }

    public void bindTexture(BufferedImage image)
    {
        projectInfo.bufferedTexture = image;
        projectInfo.bufferedTextureId = TextureUtil.uploadTextureImage(TextureUtil.glGenTextures(), projectInfo.bufferedTexture);
    }

    private void createGroupCubes(CubeGroup group)
    {
        for(int i = 0; i < group.cubeGroups.size(); i++)
        {
            createGroupCubes(group.cubeGroups.get(i));
        }
        for(int i = 0; i < group.cubes.size(); i++)
        {
            group.cubes.get(i).createModel(this);
            cubes.add(group.cubes.get(i));
        }
    }
}

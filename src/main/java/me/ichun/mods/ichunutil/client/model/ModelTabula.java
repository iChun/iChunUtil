package me.ichun.mods.ichunutil.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import me.ichun.mods.ichunutil.common.module.tabula.project.Project;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;

public class ModelTabula extends Model
{
    public static final RenderType RENDER_MODEL_NO_TEXTURE = RenderType.makeType("tabula_model_no_texture", DefaultVertexFormats.ENTITY, 7, 256, true, false, RenderType.State.getBuilder()
            .texture(RenderState.NO_TEXTURE)
            .transparency(RenderState.TRANSLUCENT_TRANSPARENCY)
            .diffuseLighting(RenderState.DIFFUSE_LIGHTING_ENABLED)
            .alpha(RenderState.DEFAULT_ALPHA)
            .fog(RenderState.NO_FOG)
            .lightmap(RenderState.LIGHTMAP_ENABLED)
            .build(false));
    public static final RenderType RENDER_MODEL_FLAT = RenderType.makeType("tabula_model_flat", DefaultVertexFormats.ENTITY, 7, 256, true, false, RenderType.State.getBuilder()
            .texture(RenderState.NO_TEXTURE)
            .alpha(RenderState.DEFAULT_ALPHA)
            .fog(RenderState.NO_FOG)
            .build(false));

    @Nonnull
    public final Project project;
    public ArrayList<ModelRenderer> models = new ArrayList<>();
    public boolean isDirty = true;

    public ModelTabula(Project project)
    {
        super(rl -> RENDER_MODEL_NO_TEXTURE);
        this.project = project;
    }

    public void createParts()
    {
        models.clear();

        textureWidth = project.texWidth;
        textureHeight = project.texHeight;

        project.parts.forEach(part -> populateModel(models, part));
    }

    public void populateModel(Collection<ModelRenderer> parts, Project.Part part)
    {
        int[] dims = part.getProjectTextureDims();
        if(!part.matchProject)
        {
            dims[0] = part.texWidth;
            dims[1] = part.texHeight;
        }
        ModelRenderer modelPart = new ModelRenderer(dims[0], dims[1], part.texOffX, part.texOffY);
        modelPart.rotationPointX = part.rotPX;
        modelPart.rotationPointY = part.rotPY;
        modelPart.rotationPointZ = part.rotPZ;

        modelPart.rotateAngleX = (float)Math.toRadians(part.rotAX);
        modelPart.rotateAngleY = (float)Math.toRadians(part.rotAY);
        modelPart.rotateAngleZ = (float)Math.toRadians(part.rotAZ);

        modelPart.mirror = part.mirror;
        modelPart.showModel = part.showModel;

        part.boxes.forEach(box -> modelPart.addBox(box.posX, box.posY, box.posZ, box.dimX, box.dimY, box.dimZ, box.expandX, box.expandY, box.expandZ));
        part.children.forEach(part1 -> populateModel(modelPart.childModels, part1));

        parts.add(modelPart);
    }

    @Override
    public void render(MatrixStack matrixStack, IVertexBuilder iVertexBuilder, int i, int i1, float v, float v1, float v2, float v3)
    {
        if(isDirty)
        {
            isDirty = false;
            createParts();
        }
        models.forEach(modelRenderer -> modelRenderer.render(matrixStack, iVertexBuilder, i, i1, v, v1, v2, v3));
    }
}

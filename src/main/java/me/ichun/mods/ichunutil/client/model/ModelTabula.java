package me.ichun.mods.ichunutil.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import me.ichun.mods.ichunutil.common.module.tabula.project.Project;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
    public ArrayList<ModelRendererTabula> models = new ArrayList<>();
    public HashMap<Project.Part, ModelRendererTabula> partMap = new HashMap<>();
    public boolean isDirty = true;

    private float selectionR = 0F;
    private float selectionG = 0F;
    private float selectionB = 0F;
    protected Project.Part selectedPart;
    protected Project.Part.Box selectedBox;

    public ModelTabula(Project project)
    {
        super(rl -> RENDER_MODEL_NO_TEXTURE);
        this.project = project;
    }

    public void createParts()
    {
        models.clear();
        partMap.clear();

        textureWidth = project.texWidth;
        textureHeight = project.texHeight;

        project.parts.forEach(part -> populateModel(models, part));
    }

    public void populateModel(Collection<? super ModelRendererTabula> parts, Project.Part part)
    {
        int[] dims = part.getProjectTextureDims();
        if(!part.matchProject)
        {
            dims[0] = part.texWidth;
            dims[1] = part.texHeight;
        }
        ModelRendererTabula modelPart = new ModelRendererTabula(this, dims[0], dims[1], part.texOffX, part.texOffY);
        modelPart.rotationPointX = part.rotPX;
        modelPart.rotationPointY = part.rotPY;
        modelPart.rotationPointZ = part.rotPZ;

        modelPart.rotateAngleX = (float)Math.toRadians(part.rotAX);
        modelPart.rotateAngleY = (float)Math.toRadians(part.rotAY);
        modelPart.rotateAngleZ = (float)Math.toRadians(part.rotAZ);

        modelPart.mirror = part.mirror;
        modelPart.showModel = part.showModel;

        part.boxes.forEach(modelPart::addBox);
        part.children.forEach(part1 -> populateModel(modelPart.childModels, part1));

        parts.add(modelPart);
        partMap.put(part, modelPart);
    }

    @Override
    public void render(MatrixStack matrixStack, IVertexBuilder iVertexBuilder, int light, int overlay, float r, float g, float b, float alpha)
    {
        preRender();
        models.forEach(modelRenderer -> modelRenderer.render(matrixStack, iVertexBuilder, light, overlay, r, g, b, alpha));
    }

    public void render(MatrixStack matrixStack, IVertexBuilder iVertexBuilder, Project.Part selectedPart, Project.Part.Box selectedBox)
    {
        this.selectedPart = selectedPart;
        this.selectedBox = selectedBox;
        if(selectedPart != null || selectedBox != null)
        {
            render(matrixStack, iVertexBuilder, 15728880, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1F);

            this.selectedPart = null;
            this.selectedBox = null;
            render(matrixStack, iVertexBuilder, 15728880, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 0.25F);
        }
        else
        {
            render(matrixStack, iVertexBuilder, 15728880, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1F);
        }
    }

    public void resetForSelection()
    {
        selectionR = selectionG = selectionB = 0F;
    }

    public float getSelectionR()
    {
        if(selectionR < 1F)
        {
            selectionR += 1/255F;
        }
        return selectionR;
    }

    public float getSelectionG()
    {
        if(selectionR >= 1F && selectionG < 1F)
        {
            selectionG += 1/255F;
        }
        return selectionG;
    }

    public float getSelectionB()
    {
        if(selectionG >= 1F && selectionB < 1F)
        {
            selectionB += 1/255F;
        }
        return selectionB;
    }

    public void renderForSelection(MatrixStack matrixStack, IVertexBuilder iVertexBuilder)
    {
        preRender();
        resetForSelection();
        models.forEach(modelRenderer -> modelRenderer.renderForSelection(matrixStack, iVertexBuilder, 1F));
    }

    public Project.Part.Box getSelectedBox(int r, int g, int b)
    {
        for(ModelRendererTabula model : models)
        {
            Project.Part.Box box = model.getSelectedBox(r, g, b);
            if(box != null)
            {
                return box;
            }
        }
        return null;
    }

    public void preRender()
    {
        if(isDirty)
        {
            isDirty = false;
            createParts();
        }
    }

    public static class ModelRendererTabula extends ModelRenderer
    {
        @Nonnull
        public final ModelTabula parentModel;
        public HashMap<BoxToBox, int[]> boxes = new HashMap<>();
        public boolean selecting;

        public ModelRendererTabula(ModelTabula parent, int textureWidthIn, int textureHeightIn, int textureOffsetXIn, int textureOffsetYIn)
        {
            super(textureWidthIn, textureHeightIn, textureOffsetXIn, textureOffsetYIn);
            parentModel = parent;
        }

        public void addBox(Project.Part.Box box)
        {
            addBox(box.posX, box.posY, box.posZ, box.dimX, box.dimY, box.dimZ, box.expandX, box.expandY, box.expandZ);
            boxes.put(new BoxToBox(box, cubeList.get(cubeList.size() - 1)), null);
        }

        public void renderForSelection(MatrixStack matrixStackIn, IVertexBuilder bufferIn, float alpha)
        {
            selecting = true;
            super.render(matrixStackIn, bufferIn, 15728880, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, alpha);
            selecting = false;
        }

        public Project.Part.Box getSelectedBox(int r, int g, int b)
        {
            for(Map.Entry<BoxToBox, int[]> boxToBoxEntry : boxes.entrySet())
            {
                int[] value = boxToBoxEntry.getValue();
                if(value != null && value[0] == r && value[1] == g && value[2] == b)
                {
                    return boxToBoxEntry.getKey().box;
                }
            }
            return null;
        }

        public BoxToBox getBoxToBox(ModelBox box)
        {
            for(BoxToBox boxToBox : boxes.keySet())
            {
                if(boxToBox.modelBox == box)
                {
                    return boxToBox;
                }
            }
            return null;
        }

        @Override
        public void doRender(MatrixStack.Entry matrixEntryIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
            if(parentModel.selectedPart != null && parentModel.partMap.get(parentModel.selectedPart) != this)
            {
                return;
            }

            Matrix4f matrix4f = matrixEntryIn.getMatrix();
            Matrix3f matrix3f = matrixEntryIn.getNormal();

            for(ModelRenderer.ModelBox modelBox : this.cubeList) {
                float r, g, b, a;
                r = red;
                g = green;
                b = blue;
                a = alpha;
                if(selecting)
                {
                    BoxToBox box = getBoxToBox(modelBox);
                    if(box != null)
                    {
                        r = MathHelper.clamp(parentModel.getSelectionR(), 0F, 1F);
                        g = MathHelper.clamp(parentModel.getSelectionG(), 0F, 1F);
                        b = MathHelper.clamp(parentModel.getSelectionB(), 0F, 1F);
                        boxes.put(box, new int[] { (int)(r * 255F), (int)(g * 255F), (int)(b * 255F) });
                    }
                }
                if(parentModel.selectedPart != null || parentModel.selectedBox != null)
                {

                }
                for(ModelRenderer.TexturedQuad modelrenderer$texturedquad : modelBox.quads) {
                    Vector3f vector3f = modelrenderer$texturedquad.normal.copy();
                    vector3f.transform(matrix3f);
                    float f = vector3f.getX();
                    float f1 = vector3f.getY();
                    float f2 = vector3f.getZ();

                    for(int i = 0; i < 4; ++i) {
                        ModelRenderer.PositionTextureVertex modelrenderer$positiontexturevertex = modelrenderer$texturedquad.vertexPositions[i];
                        float f3 = modelrenderer$positiontexturevertex.position.getX() / 16.0F;
                        float f4 = modelrenderer$positiontexturevertex.position.getY() / 16.0F;
                        float f5 = modelrenderer$positiontexturevertex.position.getZ() / 16.0F;
                        Vector4f vector4f = new Vector4f(f3, f4, f5, 1.0F);
                        vector4f.transform(matrix4f);
                        bufferIn.addVertex(vector4f.getX(), vector4f.getY(), vector4f.getZ(), r, g, b, alpha, modelrenderer$positiontexturevertex.textureU, modelrenderer$positiontexturevertex.textureV, packedOverlayIn, packedLightIn, f, f1, f2);
                    }
                }
            }
        }

        public static class BoxToBox
        {
            public final Project.Part.Box box;
            public final ModelBox modelBox;

            public BoxToBox(Project.Part.Box box, ModelBox modelBox) {
                this.box = box;
                this.modelBox = modelBox;
            }
        }
    }
}

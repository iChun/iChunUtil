package me.ichun.mods.ichunutil.client.model.tabula;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.ichun.mods.ichunutil.common.module.tabula.project.Project;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.texture.OverlayTexture;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.model.geom.ModelPart.Cube;

public class ModelTabula extends Model
{
    //TODO fix ModelRotationPoint's render type
//    public static final RenderType RENDER_MODEL_NO_TEXTURE = RenderType.create("tabula_model_no_texture", DefaultVertexFormat.NEW_ENTITY, 7, 256, true, false, RenderType.CompositeState.builder()
//            .setTextureState(RenderStateShard.NO_TEXTURE)
//            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
//            .setDiffuseLightingState(RenderStateShard.DIFFUSE_LIGHTING)
//            .setAlphaState(RenderStateShard.DEFAULT_ALPHA)
//            .setFogState(RenderStateShard.NO_FOG)
//            .setLightmapState(RenderStateShard.LIGHTMAP)
//            .createCompositeState(false));
//    public static final RenderType RENDER_MODEL_FLAT = RenderType.create("tabula_model_flat", DefaultVertexFormat.NEW_ENTITY, 7, 256, true, false, RenderType.CompositeState.builder()
//            .setTextureState(RenderStateShard.NO_TEXTURE)
//            .setAlphaState(RenderStateShard.DEFAULT_ALPHA)
//            .setFogState(RenderStateShard.NO_FOG)
//            .createCompositeState(false));
//    public static final RenderType RENDER_MODEL_COMPASS_FLAT = RenderType.create("tabula_model_compass_flat", DefaultVertexFormat.NEW_ENTITY, 7, 256, true, false, RenderType.CompositeState.builder()
//            .setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation("tabula", "textures/model/cube.png"), false, false))
//            .setAlphaState(RenderStateShard.DEFAULT_ALPHA)
//            .setFogState(RenderStateShard.NO_FOG)
//            .createCompositeState(false));

    @Nonnull
    public final Project project;
    //TODO
//    public ArrayList<ModelRendererTabula> models = new ArrayList<>();
//    public HashMap<Project.Part, ModelRendererTabula> partMap = new HashMap<>();
    public boolean isDirty = true;

    protected boolean selecting;
    private float selectionR = 0F;
    private float selectionG = 0F;
    private float selectionB = 0F;
    protected Project.Part selectedPart;
    protected Project.Part.Box selectedBox;

    protected ModelRotationPoint rotationPoint;

    public ModelTabula(Project project)
    {
//        super(rl -> RENDER_MODEL_NO_TEXTURE);
        super(RenderType::entityTranslucentCull);
        this.project = project;
        this.rotationPoint = new ModelRotationPoint();
    }

    public void createParts()
    {
        //TODO
//        models.clear();
//        partMap.clear();
//
//        texWidth = project.texWidth;
//        texHeight = project.texHeight;
//
//        project.parts.forEach(part -> populateModel(models, part));
    }

    //TODO
//    public void populateModel(Collection<? super ModelRendererTabula> parts, Project.Part part)
//    {
//        int[] dims = part.getProjectTextureDims();
//        if(!part.matchProject)
//        {
//            dims[0] = part.texWidth;
//            dims[1] = part.texHeight;
//        }
//
//        ModelRendererTabula modelPart = new ModelRendererTabula(this, dims[0], dims[1], part.texOffX, part.texOffY);
//        modelPart.x = part.rotPX;
//        modelPart.y = part.rotPY;
//        modelPart.z = part.rotPZ;
//
//        modelPart.xRot = (float)Math.toRadians(part.rotAX);
//        modelPart.yRot = (float)Math.toRadians(part.rotAY);
//        modelPart.zRot = (float)Math.toRadians(part.rotAZ);
//
//        modelPart.mirror = part.mirror;
//        modelPart.visible = part.showModel;
//
//        part.boxes.forEach(modelPart::addBox);
//        part.children.forEach(part1 -> populateModel(modelPart.children, part1));
//
//        parts.add(modelPart);
//        partMap.put(part, modelPart);
//    }

    @Override
    public void renderToBuffer(PoseStack matrixStack, VertexConsumer iVertexBuilder, int light, int overlay, float r, float g, float b, float alpha)
    {
        preRender();
        //TODO
//        models.forEach(modelRenderer -> modelRenderer.render(matrixStack, iVertexBuilder, light, overlay, r, g, b, alpha));
    }

    public void render(PoseStack matrixStack, Project.Part selectedPart, Project.Part.Box selectedBox, boolean hideTexture, float alpha)
    {
        this.selectedPart = selectedPart;
        this.selectedBox = selectedBox;
        if(selectedPart != null || selectedBox != null)
        {
            render(matrixStack, 0.85F, hideTexture);

            this.selectedPart = null;
            this.selectedBox = null;
            render(matrixStack, 0.25F, hideTexture);
        }
        else
        {
            render(matrixStack, alpha, hideTexture);
        }
    }

    private void render(PoseStack matrixStack, float alpha, boolean hideTexture)
    {
        preRender();

        //TODO
//        models.forEach(modelRenderer -> {
//            modelRenderer.render(matrixStack, null, hideTexture ? 0 : 15728880, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, alpha);
//        });
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

    public void renderForSelection(PoseStack matrixStack)
    {
        //TODO
//        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
//        VertexConsumer ivertexbuilder = bufferSource.getBuffer(ModelTabula.RENDER_MODEL_FLAT);
//
//        preRender();
//        resetForSelection();
//        selecting = true;
//        models.forEach(modelRenderer -> modelRenderer.renderForSelection(matrixStack, ivertexbuilder, 1F));
//        selecting = false;
//
//        bufferSource.endBatch();
//    }
//
//    public Project.Part.Box getSelectedBox(int r, int g, int b)
//    {
//        for(ModelRendererTabula model : models)
//        {
//            Project.Part.Box box = model.getSelectedBox(r, g, b);
//            if(box != null)
//            {
//                return box;
//            }
//        }
//        return null;
    }

    public void preRender()
    {
        if(isDirty)
        {
            isDirty = false;
            createParts();
        }
    }


    //TODO
//    public static class ModelRendererTabula extends ModelPart
//    {
//        @Nonnull
//        public final ModelTabula parentModel;
//        public HashMap<BoxToBox, int[]> boxes = new HashMap<>();
//
//        public ModelRendererTabula(ModelTabula parent, int textureWidthIn, int textureHeightIn, int textureOffsetXIn, int textureOffsetYIn)
//        {
//            super(textureWidthIn, textureHeightIn, textureOffsetXIn, textureOffsetYIn);
//            parentModel = parent;
//        }
//
//        public void addBox(Project.Part.Box box)
//        {
//            int texOffX = this.xTexOffs;
//            int texOffY = this.yTexOffs;
//            this.texOffs(this.xTexOffs + box.texOffX, this.yTexOffs + box.texOffY);
//            this.addBox(box.posX, box.posY, box.posZ, box.dimX, box.dimY, box.dimZ, box.expandX, box.expandY, box.expandZ);
//            this.texOffs(texOffX, texOffY);
//            boxes.put(new BoxToBox(box, cubes.get(cubes.size() - 1)), null);
//        }
//
//        public void renderForSelection(PoseStack matrixStackIn, VertexConsumer bufferIn, float alpha)
//        {
//            super.render(matrixStackIn, bufferIn, 15728880, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, alpha);
//        }
//
//        public Project.Part.Box getSelectedBox(int r, int g, int b)
//        {
//            for(Map.Entry<BoxToBox, int[]> boxToBoxEntry : boxes.entrySet())
//            {
//                int[] value = boxToBoxEntry.getValue();
//                if(value != null && value[0] == r && value[1] == g && value[2] == b)
//                {
//                    return boxToBoxEntry.getKey().box;
//                }
//            }
//            for(ModelPart childModel : children)
//            {
//                if(childModel instanceof ModelRendererTabula)
//                {
//                    Project.Part.Box box = ((ModelRendererTabula)childModel).getSelectedBox(r, g, b);
//                    if(box != null)
//                    {
//                        return box;
//                    }
//                }
//            }
//            return null;
//        }
//
//        public BoxToBox getBoxToBox(Cube box)
//        {
//            for(BoxToBox boxToBox : boxes.keySet())
//            {
//                if(boxToBox.modelBox == box)
//                {
//                    return boxToBox;
//                }
//            }
//            return null;
//        }
//
//        @Override
//        public void render(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
//            if (this.visible) {
//                if (!this.cubes.isEmpty() || !this.children.isEmpty()) {
//                    matrixStackIn.pushPose();
//                    this.translateAndRotate(matrixStackIn);
//
//                    if(bufferIn == null)
//                    {
//                        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
//                        RenderType type = ModelTabula.RENDER_MODEL_NO_TEXTURE;
//
//                        VertexConsumer ivertexbuilder = bufferSource.getBuffer(type);
//
//                        if(parentModel.selectedPart != null && parentModel.partMap.get(parentModel.selectedPart) == this)
//                        {
//                            parentModel.rotationPoint.renderToBuffer(matrixStackIn, ivertexbuilder, 0xF000F0, OverlayTexture.NO_OVERLAY, 0F, 0F, 1F, 1F);
//                        }
//
//                        if(parentModel.project.getTextureBytes() != null && packedLightIn > 0)
//                        {
//                            type = RenderType.entityTranslucent(parentModel.project.getNativeImageResourceLocation());
//                        }
//
//                        ivertexbuilder = bufferSource.getBuffer(type);
//
//                        this.compile(matrixStackIn.last(), ivertexbuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
//
//                        bufferSource.endBatch();
//
//                        for(ModelPart modelrenderer : this.children)
//                        {
//                            modelrenderer.render(matrixStackIn, null, packedLightIn, packedOverlayIn, red, green, blue, alpha);
//                        }
//                    }
//                    else
//                    {
//                        this.compile(matrixStackIn.last(), bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
//
//                        for(ModelPart modelrenderer : this.children)
//                        {
//                            modelrenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
//                        }
//                    }
//
//                    matrixStackIn.popPose();
//                }
//            }
//        }
//
//        @Override
//        public void compile(PoseStack.Pose matrixEntryIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
//            if(parentModel.selectedPart != null && parentModel.partMap.get(parentModel.selectedPart) != this)
//            {
//                return;
//            }
//
//            Matrix4f matrix4f = matrixEntryIn.pose();
//            Matrix3f matrix3f = matrixEntryIn.normal();
//
//            for(ModelPart.Cube modelBox : this.cubes) {
//                if(parentModel.selectedBox != null)
//                {
//                    BoxToBox box = getBoxToBox(modelBox);
//                    if(parentModel.selectedBox != box.box)
//                    {
//                        continue;
//                    }
//                }
//
//                float r, g, b, a;
//                r = red;
//                g = green;
//                b = blue;
//                a = alpha;
//                if(parentModel.selecting)
//                {
//                    BoxToBox box = getBoxToBox(modelBox);
//                    if(box != null)
//                    {
//                        r = Mth.clamp(parentModel.getSelectionR(), 0F, 1F);
//                        g = Mth.clamp(parentModel.getSelectionG(), 0F, 1F);
//                        b = Mth.clamp(parentModel.getSelectionB(), 0F, 1F);
//                        boxes.put(box, new int[] { (int)(r * 255F), (int)(g * 255F), (int)(b * 255F) });
//                    }
//                }
//
//                for(ModelPart.Polygon modelrenderer$texturedquad : modelBox.polygons) {
//                    Vector3f vector3f = modelrenderer$texturedquad.normal.copy();
//                    vector3f.transform(matrix3f);
//                    float f = vector3f.x();
//                    float f1 = vector3f.y();
//                    float f2 = vector3f.z();
//
//                    for(int i = 0; i < 4; ++i) {
//                        ModelPart.Vertex modelrenderer$positiontexturevertex = modelrenderer$texturedquad.vertices[i];
//                        float f3 = modelrenderer$positiontexturevertex.pos.x() / 16.0F;
//                        float f4 = modelrenderer$positiontexturevertex.pos.y() / 16.0F;
//                        float f5 = modelrenderer$positiontexturevertex.pos.z() / 16.0F;
//                        Vector4f vector4f = new Vector4f(f3, f4, f5, 1.0F);
//                        vector4f.transform(matrix4f);
//                        bufferIn.vertex(vector4f.x(), vector4f.y(), vector4f.z(), r, g, b, a, modelrenderer$positiontexturevertex.u, modelrenderer$positiontexturevertex.v, packedOverlayIn, packedLightIn, f, f1, f2);
//                    }
//                }
//            }
//        }
//
//        public static class BoxToBox
//        {
//            public final Project.Part.Box box;
//            public final Cube modelBox;
//
//            public BoxToBox(Project.Part.Box box, Cube modelBox) {
//                this.box = box;
//                this.modelBox = modelBox;
//            }
//        }
//    }
}

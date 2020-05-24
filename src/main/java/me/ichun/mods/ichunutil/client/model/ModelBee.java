package me.ichun.mods.ichunutil.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;

public class ModelBee extends Model
{
    public static final ResourceLocation TEX_BEE = new ResourceLocation("ichunutil", "textures/model/bee.png");

    public ModelRenderer bee;

    public ModelBee()
    {
        super(RenderType::getEntityTranslucentCull);
        this.textureWidth = 4;
        this.textureHeight = 2;
        this.bee = new ModelRenderer(this, 0, 0);
        this.bee.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.bee.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha)
    {
        this.bee.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }
}

package us.ichun.mods.ichunutil.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelBee extends ModelBase
{
    public ModelRenderer shape1;

    public ModelBee()
    {
        this.textureWidth = 4;
        this.textureHeight = 2;
        this.shape1 = new ModelRenderer(this, 0, 0);
        this.shape1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.shape1.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
    {
        this.shape1.render(f5);
    }
}

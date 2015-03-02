package us.ichun.mods.ichunutil.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * Created using Tabula 5.0.0
 */
public class ModelSnout extends ModelBase {
    public ModelRenderer snout;

    public ModelSnout() {
        this.snout = new ModelRenderer(this, 16, 16);
        this.snout.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.snout.addBox(-2.0F, -3.5F, -5.0F, 4, 3, 1, 0.0F);
    }

    public void render(float f5) {
        this.snout.render(f5);
    }
}

package me.ichun.mods.ichunutil.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityZombie;

public class ModelAngelPeriphs extends ModelBase {
    public ModelRenderer haloOuter;
    public ModelRenderer haloInner;
    public ModelRenderer wingSpineLeft1;
    public ModelRenderer wingSpineLeft2;
    public ModelRenderer wingFeathersLeftFront1;
    public ModelRenderer wingFeathersLeftBack1;
    public ModelRenderer wingSpineLeft3;
    public ModelRenderer wingFeathersLeftFront2;
    public ModelRenderer wingFeathersLeftBack2;
    public ModelRenderer wingSpineLeft4;
    public ModelRenderer wingFeathersLeftFront3;
    public ModelRenderer wingFeathersLeftBack3;
    public ModelRenderer wingSpineLeft5;
    public ModelRenderer wingFeathersLeftFront4;
    public ModelRenderer wingFeathersLeftBack4;
    public ModelRenderer wingFeathersLeftFront5;
    public ModelRenderer wingFeathersLeftBack5;

    public ModelAngelPeriphs() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.wingSpineLeft2 = new ModelRenderer(this, 25, 6);
        this.wingSpineLeft2.setRotationPoint(0.0F, 0.0F, 4.5F);
        this.wingSpineLeft2.addBox(-0.3F, -0.4F, 0.0F, 1, 1, 8, 0.0F);
        this.setRotateAngle(wingSpineLeft2, 0.6829473363053812F, 0.8196066167365371F, 0.0F);
        this.wingFeathersLeftBack5 = new ModelRenderer(this, 0, 1);
        this.wingFeathersLeftBack5.setRotationPoint(0.5F, -0.3F, 0.0F);
        this.wingFeathersLeftBack5.addBox(-1.0F, 0.4F, 0.0F, 0, 9, 17, 0.0F);
        this.setRotateAngle(wingFeathersLeftBack5, 0.0F, 0.0F, -0.05235987755982988F);
        this.wingFeathersLeftBack1 = new ModelRenderer(this, 26, 30);
        this.wingFeathersLeftBack1.setRotationPoint(0.5F, 0.0F, 0.0F);
        this.wingFeathersLeftBack1.addBox(-0.6F, 0.4F, 0.0F, 0, 4, 6, 0.0F);
        this.setRotateAngle(wingFeathersLeftBack1, 0.0F, 0.0F, -0.11344640137963141F);
        this.wingFeathersLeftBack2 = new ModelRenderer(this, 8, 27);
        this.wingFeathersLeftBack2.setRotationPoint(-0.5F, 0.0F, -1.0F);
        this.wingFeathersLeftBack2.addBox(0.1F, 0.4F, 0.0F, 0, 5, 9, 0.0F);
        this.setRotateAngle(wingFeathersLeftBack2, 0.0F, 0.0F, -0.09599310885968812F);
        this.wingFeathersLeftFront4 = new ModelRenderer(this, 1, 12);
        this.wingFeathersLeftFront4.setRotationPoint(0.5F, -0.3F, 0.0F);
        this.wingFeathersLeftFront4.addBox(0.0F, 0.4F, 0.0F, 0, 9, 15, 0.0F);
        this.setRotateAngle(wingFeathersLeftFront4, 0.0F, 0.0F, 0.05235987755982988F);
        this.wingFeathersLeftFront2 = new ModelRenderer(this, 8, 27);
        this.wingFeathersLeftFront2.setRotationPoint(0.5F, 0.0F, -1.0F);
        this.wingFeathersLeftFront2.addBox(0.2F, 0.4F, 0.0F, 0, 5, 9, 0.0F);
        this.setRotateAngle(wingFeathersLeftFront2, 0.0F, 0.0F, 0.09599310885968812F);
        this.wingSpineLeft4 = new ModelRenderer(this, 32, 18);
        this.wingSpineLeft4.setRotationPoint(0.4F, 0.0F, 3.7F);
        this.wingSpineLeft4.addBox(-0.5F, -0.5F, 0.0F, 1, 1, 15, 0.0F);
        this.setRotateAngle(wingSpineLeft4, -1.1838568316277536F, 0.8196066167365371F, -0.22759093446006054F);
        this.wingFeathersLeftBack3 = new ModelRenderer(this, 0, 32);
        this.wingFeathersLeftBack3.setRotationPoint(0.5F, 0.0F, 0.0F);
        this.wingFeathersLeftBack3.addBox(-0.5F, 0.4F, 0.0F, 0, 6, 4, 0.0F);
        this.setRotateAngle(wingFeathersLeftBack3, 0.0F, 0.0F, -0.0645771823237902F);
        this.haloInner = new ModelRenderer(this, 14, 2);
        this.haloInner.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.haloInner.addBox(-2.0F, -10.5F, -2.0F, 4, 1, 4, 0.0F);
        this.wingFeathersLeftFront3 = new ModelRenderer(this, 0, 32);
        this.wingFeathersLeftFront3.setRotationPoint(0.5F, 0.0F, 0.0F);
        this.wingFeathersLeftFront3.addBox(0.5F, 0.4F, 0.0F, 0, 6, 4, 0.0F);
        this.setRotateAngle(wingFeathersLeftFront3, 0.0F, 0.0F, 0.0645771823237902F);
        this.wingFeathersLeftFront5 = new ModelRenderer(this, 0, 1);
        this.wingFeathersLeftFront5.setRotationPoint(0.5F, -0.3F, 0.0F);
        this.wingFeathersLeftFront5.addBox(0.0F, 0.4F, 0.0F, 0, 9, 17, 0.0F);
        this.setRotateAngle(wingFeathersLeftFront5, 0.0F, 0.0F, 0.05235987755982988F);
        this.haloOuter = new ModelRenderer(this, 0, 0);
        this.haloOuter.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.haloOuter.addBox(-3.0F, -10.5F, -3.0F, 6, 1, 6, 0.0F);
        this.wingSpineLeft3 = new ModelRenderer(this, 33, 0);
        this.wingSpineLeft3.setRotationPoint(0.0F, 0.0F, 7.9F);
        this.wingSpineLeft3.addBox(0.0F, -0.5F, 0.0F, 1, 1, 4, 0.0F);
        this.setRotateAngle(wingSpineLeft3, -1.0471975511965976F, 0.136659280431156F, -0.40980330836826856F);
        this.wingFeathersLeftBack4 = new ModelRenderer(this, 1, 12);
        this.wingFeathersLeftBack4.setRotationPoint(0.5F, -0.3F, 0.0F);
        this.wingFeathersLeftBack4.addBox(-1.0F, 0.4F, 0.0F, 0, 9, 15, 0.0F);
        this.setRotateAngle(wingFeathersLeftBack4, 0.0F, 0.0F, -0.05235987755982988F);
        this.wingSpineLeft5 = new ModelRenderer(this, 28, 0);
        this.wingSpineLeft5.setRotationPoint(0.0F, 0.0F, 14.7F);
        this.wingSpineLeft5.addBox(-0.5F, -0.5F, 0.0F, 1, 1, 17, 0.0F);
        this.setRotateAngle(wingSpineLeft5, -0.5462880558742251F, 0.31869712141416456F, 0.0F);
        this.wingSpineLeft1 = new ModelRenderer(this, 48, 0);
        this.wingSpineLeft1.setRotationPoint(1.5F, 2.0F, 1.9F);
        this.wingSpineLeft1.addBox(0.0F, -0.5F, 0.0F, 1, 1, 5, 0.0F);
        this.setRotateAngle(wingSpineLeft1, 0.5918411493512771F, 0.27314402793711257F, 0.0F);
        this.wingFeathersLeftFront1 = new ModelRenderer(this, 26, 30);
        this.wingFeathersLeftFront1.setRotationPoint(0.5F, 0.0F, 0.0F);
        this.wingFeathersLeftFront1.addBox(0.6F, 0.4F, 0.0F, 0, 4, 6, 0.0F);
        this.setRotateAngle(wingFeathersLeftFront1, 0.0F, 0.0F, 0.11344640137963141F);
        this.wingSpineLeft1.addChild(this.wingSpineLeft2);
        this.wingSpineLeft5.addChild(this.wingFeathersLeftBack5);
        this.wingSpineLeft1.addChild(this.wingFeathersLeftBack1);
        this.wingSpineLeft2.addChild(this.wingFeathersLeftBack2);
        this.wingSpineLeft4.addChild(this.wingFeathersLeftFront4);
        this.wingSpineLeft2.addChild(this.wingFeathersLeftFront2);
        this.wingSpineLeft3.addChild(this.wingSpineLeft4);
        this.wingSpineLeft3.addChild(this.wingFeathersLeftBack3);
        this.wingSpineLeft3.addChild(this.wingFeathersLeftFront3);
        this.wingSpineLeft5.addChild(this.wingFeathersLeftFront5);
        this.wingSpineLeft2.addChild(this.wingSpineLeft3);
        this.wingSpineLeft4.addChild(this.wingFeathersLeftBack4);
        this.wingSpineLeft4.addChild(this.wingSpineLeft5);
        this.wingSpineLeft1.addChild(this.wingFeathersLeftFront1);
    }

    public void renderHalo(float f5) {
        this.haloInner.render(f5);
        this.haloOuter.render(f5);
    }

    public void renderWing(float f5)
    {
        this.wingSpineLeft1.render(f5);
    }

    public void setRotations(float progress)
    {
        this.setRotateAngle(wingSpineLeft1, 0.5918411493512771F, 0.27314402793711257F, 0.0F);
        this.setRotateAngle(wingSpineLeft2, 0.6829473363053812F - (0.22759093446F * progress), 0.8196066167365371F, 0.0F);
        this.setRotateAngle(wingSpineLeft3, -1.0471975511965976F - (-0.81960661673F * progress), 0.136659280431156F - (-0.09093165402F * progress), -0.40980330836826856F - (-0.36425021489F * progress));
        this.setRotateAngle(wingSpineLeft4, -1.1838568316277536F - (-1.1838568316277536F * progress), 0.8196066167365371F - (0.1822123739F * progress), -0.22759093446006054F);
        this.setRotateAngle(wingSpineLeft5, -0.5462880558742251F - (-0.5462880558742251F * progress), 0.31869712141416456F, 0.0F);
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}

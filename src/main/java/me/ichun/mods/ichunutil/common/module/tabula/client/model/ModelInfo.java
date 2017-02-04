package me.ichun.mods.ichunutil.common.module.tabula.client.model;

import me.ichun.mods.ichunutil.client.model.ModelHelper;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;

public class ModelInfo
        implements Comparable
{
    public final ResourceLocation texture;
    public final ModelBase modelParent;
    public final HashMap<String, ModelRenderer> modelList;
    public final Class clz;

    public ModelInfo(ResourceLocation texture, ModelBase modelParent, Class clz)
    {
        this.texture = texture;
        this.modelParent = modelParent;
        this.modelList = ModelHelper.getModelCubesWithNames(modelParent);
        this.clz = clz;
    }

    @Override
    public int compareTo(Object arg0)
    {
        if(arg0 instanceof ModelInfo)
        {
            ModelInfo info = (ModelInfo)arg0;
            return modelParent.getClass().getSimpleName().compareTo(info.modelParent.getClass().getSimpleName());
        }
        return 0;
    }
}

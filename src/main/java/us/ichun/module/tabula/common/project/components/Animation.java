package us.ichun.module.tabula.common.project.components;

import net.minecraft.client.model.ModelRenderer;

import java.util.ArrayList;
import java.util.HashMap;

public class Animation
{
    public String name;

    public boolean loops;

    public HashMap<ModelRenderer, ArrayList<AnimationSet>> sets = new HashMap<ModelRenderer, ArrayList<AnimationSet>>();
}

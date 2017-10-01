package me.ichun.mods.ichunutil.common.recipe.internal;

import com.google.gson.JsonObject;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.recipe.ConditionalRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;

public class RecipeCompactPorkchop implements IRecipeFactory
{
    @Override
    public IRecipe parse(JsonContext context, JsonObject json)
    {
        return ConditionalRecipe.defaultParserWithCondition(() -> iChunUtil.config.enableCompactPorkchop == 1, context, json);
    }
}

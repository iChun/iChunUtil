package me.ichun.mods.ichunutil.common.recipe;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;
import net.minecraft.client.util.RecipeItemHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.BooleanSupplier;

public class ConditionalIngredient extends Ingredient
{
    private final BooleanSupplier isConditionMet;
    private final ItemStack stackConditionMet;
    private final ItemStack stackConditionNotMet;
    private IntList packedConditionMet;
    private IntList packedConditionNotMet;

    public ConditionalIngredient(BooleanSupplier isConditionMet, ItemStack stackConditionMet, ItemStack stackConditionNotMet)
    {
        this.isConditionMet = isConditionMet;
        this.stackConditionMet = stackConditionMet;
        this.stackConditionNotMet = stackConditionNotMet;
    }

    @Override
    public boolean apply(@Nullable ItemStack input) {
        if (input == null)
            return false;
        ItemStack itemStack = isConditionMet.getAsBoolean() ? stackConditionMet : stackConditionNotMet;
        if (itemStack.getItem() == input.getItem()) //copied from normal ingredient
        {
            int i = itemStack.getMetadata();

            if (i == OreDictionary.WILDCARD_VALUE || i == input.getMetadata())
            {
                return true;
            }
        }
        return false;
    }

    @Override
    @Nonnull
    public ItemStack[] getMatchingStacks() {
        return new ItemStack[]{ isConditionMet.getAsBoolean() ? stackConditionMet : stackConditionNotMet };
    }

    @Override
    @Nonnull
    public IntList getValidItemStacksPacked() {
        boolean normal = isConditionMet.getAsBoolean();
        IntList validStacks = normal ? packedConditionMet : packedConditionNotMet;
        if (validStacks == null)
        {
            validStacks = IntLists.singleton(RecipeItemHelper.pack(normal ? stackConditionMet : stackConditionNotMet));
            if (normal)
                packedConditionMet = validStacks;
            else
                packedConditionNotMet = validStacks;
        }
        return validStacks;
    }

    @Override
    protected void invalidate() {
        super.invalidate();
        this.packedConditionMet = null;
        this.packedConditionNotMet = null;
    }

    public static ItemStack parseItemStack(JsonObject json, String identifier) {
        ResourceLocation normal = new ResourceLocation(JsonUtils.getString(json, identifier));
        return new ItemStack(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(normal)), JsonUtils.getInt(json, identifier + "Count", 1), JsonUtils.getInt(json, identifier + "Data", 0));
    }

    public static ConditionalIngredient parseWithCondition(BooleanSupplier condition, JsonContext context, JsonObject json)
    {
        ItemStack stackMet = ConditionalIngredient.parseItemStack(json, "met");
        ItemStack stackNotMet = ConditionalIngredient.parseItemStack(json, "notmet");
        return new ConditionalIngredient(condition, stackMet, stackNotMet);
    }
}

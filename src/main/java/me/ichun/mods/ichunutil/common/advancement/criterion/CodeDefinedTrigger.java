package me.ichun.mods.ichunutil.common.advancement.criterion;

import com.google.gson.JsonObject;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.util.ResourceLocation;

public class CodeDefinedTrigger extends AbstractCriterionTrigger<CodeDefinedTrigger.Instance>
{
    private final ResourceLocation id;

    public CodeDefinedTrigger(ResourceLocation id)
    {
        this.id = id;
    }

    @Override
    public ResourceLocation getId()
    {
        return id;
    }

    @Override
    protected Instance deserializeTrigger(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser)
    {
        return new Instance(this.id, entityPredicate);
    }

    public void trigger(ServerPlayerEntity player)
    {
        this.triggerListeners(player, (instance -> true));
    }

    public static class Instance extends CriterionInstance
    {
        public Instance(ResourceLocation id, EntityPredicate.AndPredicate playerCondition)
        {
            super(id, playerCondition);
        }

        public static Instance create(ResourceLocation id)
        {
            return new Instance(id, EntityPredicate.AndPredicate.ANY_AND);
        }
    }
}

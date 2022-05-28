package me.ichun.mods.ichunutil.common.advancement.criterion;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.resources.ResourceLocation;

public class CodeDefinedTrigger extends SimpleCriterionTrigger<CodeDefinedTrigger.Instance>
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
    protected Instance createInstance(JsonObject json, EntityPredicate.Composite entityPredicate, DeserializationContext conditionsParser)
    {
        return new Instance(this.id, entityPredicate);
    }

    public void trigger(ServerPlayer player)
    {
        this.trigger(player, (instance -> true));
    }

    public static class Instance extends AbstractCriterionTriggerInstance
    {
        public Instance(ResourceLocation id, EntityPredicate.Composite playerCondition)
        {
            super(id, playerCondition);
        }

        public static Instance create(ResourceLocation id)
        {
            return new Instance(id, EntityPredicate.Composite.ANY);
        }
    }
}

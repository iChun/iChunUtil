package me.ichun.mods.ichunutil.common.advancement.criterion;

import com.google.gson.JsonObject;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class ValueAtOrAboveTrigger extends AbstractCriterionTrigger<ValueAtOrAboveTrigger.Instance>
{
    private final ResourceLocation id;

    public ValueAtOrAboveTrigger(ResourceLocation id)
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
        int count = JSONUtils.getInt(json, "count");
        return new Instance(this.id, entityPredicate, count);
    }

    public void test(ServerPlayerEntity player, int count)
    {
        this.triggerListeners(player, (instance -> instance.test(count)));
    }

    public static class Instance extends CriterionInstance
    {
        private final int count;

        public Instance(ResourceLocation id, EntityPredicate.AndPredicate playerCondition, int count)
        {
            super(id, playerCondition);
            this.count = count;
        }

        public static Instance count(ResourceLocation id, int i)
        {
            return new Instance(id, EntityPredicate.AndPredicate.ANY_AND, i);
        }

        public boolean test(int count)
        {
            return count >= this.count;
        }

        public JsonObject serialize(ConditionArraySerializer conditions) {
            JsonObject jsonobject = super.serialize(conditions);
            jsonobject.addProperty("count", count);
            return jsonobject;
        }
    }
}

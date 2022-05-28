package me.ichun.mods.ichunutil.common.advancement.criterion;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;

public class ValueAtOrAboveTrigger extends SimpleCriterionTrigger<ValueAtOrAboveTrigger.Instance>
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
    protected Instance createInstance(JsonObject json, EntityPredicate.Composite entityPredicate, DeserializationContext conditionsParser)
    {
        int count = GsonHelper.getAsInt(json, "count");
        return new Instance(this.id, entityPredicate, count);
    }

    public void test(ServerPlayer player, int count)
    {
        this.trigger(player, (instance -> instance.test(count)));
    }

    public static class Instance extends AbstractCriterionTriggerInstance
    {
        private final int count;

        public Instance(ResourceLocation id, EntityPredicate.Composite playerCondition, int count)
        {
            super(id, playerCondition);
            this.count = count;
        }

        public static Instance count(ResourceLocation id, int i)
        {
            return new Instance(id, EntityPredicate.Composite.ANY, i);
        }

        public boolean test(int count)
        {
            return count >= this.count;
        }

        public JsonObject serializeToJson(SerializationContext conditions) {
            JsonObject jsonobject = super.serializeToJson(conditions);
            jsonobject.addProperty("count", count);
            return jsonobject;
        }
    }
}

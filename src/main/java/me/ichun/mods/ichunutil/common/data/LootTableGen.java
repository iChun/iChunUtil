package me.ichun.mods.ichunutil.common.data;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.loader.LoaderDelegate;
import me.ichun.mods.ichunutil.loader.LoaderHandler;
import net.minecraft.world.level.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.HashCache;
import net.minecraft.data.DataProvider;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.EntityLoot;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.util.TriConsumer;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

public class LootTableGen implements DataProvider
{
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    private final DataGenerator gen;
    private final HashSet<Data> sets = new HashSet<>();

    public LootTableGen(DataGenerator gen, Data...set)
    {
        this.gen = gen;
        this.sets.addAll(Arrays.asList(set));

        if(sets.isEmpty())
        {
            throw new RuntimeException("Trying to generate loot tables with no data");
        }
    }

    @Override
    public String getName() {
        return "LootTables";
    }

    @Override
    public void run(HashCache cache) {
        Map<ResourceLocation, LootTable> map = Maps.newHashMap();
        TriConsumer<LootContextParamSet, ResourceLocation, LootTable.Builder> consumer = (set, key, builder) -> {
            if (map.put(key, builder.setParamSet(set).build()) != null)
                throw new IllegalStateException("Duplicate loot table " + key);
        };

        for(Data data : sets)
        {
            data.accept((key, builder) -> consumer.accept(data.lootSet(), key, builder));
        }

        map.forEach((key, table) -> {
            Path target = this.gen.getOutputFolder().resolve("data/" + key.getNamespace() + "/loot_tables/" + key.getPath() + ".json");

            try {
                DataProvider.save(GSON, cache, LootTables.serialize(table), target);
                iChunUtil.LOGGER.info("Saved loot table {}", target);
            } catch (IOException ioexception) {
                iChunUtil.LOGGER.error("Couldn't save loot table {}", target, ioexception);
            }
        });
    }

    public static class Blocks extends BlockLoot
            implements Data
    {
        private HashMap<Block, BiConsumer<BlockLoot, Block>> blocksToGen = new HashMap<>();

        public Blocks(){}

        public Blocks add(Block block, BiConsumer<BlockLoot, Block> consumer)
        {
            blocksToGen.put(block, consumer);
            return this;
        }

        @Override
        public LootContextParamSet lootSet()
        {
            return LootContextParamSets.BLOCK;
        }

        @Override
        public void accept(@Nonnull BiConsumer<ResourceLocation, LootTable.Builder> consumer)
        {
            blocksToGen.forEach((block, blockConsumer) -> blockConsumer.accept(this, block));

            Set<ResourceLocation> set = Sets.newHashSet(); //added but never used?
            blocksToGen.keySet().forEach((block) -> {
                ResourceLocation table = block.getLootTable();
                if(!BuiltInLootTables.EMPTY.equals(table) && set.add(table))
                {
                    LootTable.Builder builder = this.map.remove(table);
                    if (builder == null)
                        throw new IllegalStateException(String.format("Missing loot table '%s' for '%s'", table, LoaderHandler.d().getRegistryName(block)));

                    consumer.accept(table, builder);
                }
            });

            if (!this.map.isEmpty())
                throw new IllegalStateException("Created block loot tables for non-blocks: " + this.map.keySet());
        }
    }

    public static class Entities extends EntityLoot
            implements Data
    {
        private HashMap<EntityType<?>, BiConsumer<EntityLoot, EntityType<?>>> entitiesToGen = new HashMap<>();

        public Entities(){}

        public Entities add(EntityType<?> type, BiConsumer<EntityLoot, EntityType<?>> consumer)
        {
            entitiesToGen.put(type, consumer);
            return this;
        }

        @Override
        public LootContextParamSet lootSet()
        {
            return LootContextParamSets.ENTITY;
        }

        @Override
        public void accept(@Nonnull BiConsumer<ResourceLocation, LootTable.Builder> consumer)
        {
            entitiesToGen.forEach((block, blockConsumer) -> blockConsumer.accept(this, block));

            Set<ResourceLocation> set = Sets.newHashSet();
            entitiesToGen.keySet().forEach((type) -> {
                if(type.getCategory() != MobCategory.MISC)
                {
                    ResourceLocation table = type.getDefaultLootTable();
                    if(table != BuiltInLootTables.EMPTY && set.add(table))
                    {
                        LootTable.Builder builder = this.map.remove(table);
                        if (builder == null)
                            throw new IllegalStateException(String.format("Missing loot table '%s' for '%s'", table, LoaderHandler.d().getRegistryName(type)));

                        consumer.accept(table, builder);
                    }
                }
            });

            if (!this.map.isEmpty())
                throw new IllegalStateException("Created entity loot tables for non-living entities: " + this.map.keySet());
        }
    }

    public interface Data extends Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>
    {
        LootContextParamSet lootSet();
    }
}

package me.ichun.mods.ichunutil.common.data;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.data.loot.EntityLootTables;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.*;
import org.apache.logging.log4j.util.TriConsumer;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class LootTableGen implements IDataProvider
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
    public void act(DirectoryCache cache) {
        Map<ResourceLocation, LootTable> map = Maps.newHashMap();
        TriConsumer<LootParameterSet, ResourceLocation, LootTable.Builder> consumer = (set, key, builder) -> {
            if (map.put(key, builder.setParameterSet(set).build()) != null)
                throw new IllegalStateException("Duplicate loot table " + key);
        };

        for(Data data : sets)
        {
            data.accept((key, builder) -> consumer.accept(data.lootSet(), key, builder));
        }

        map.forEach((key, table) -> {
            Path target = this.gen.getOutputFolder().resolve("data/" + key.getNamespace() + "/loot_tables/" + key.getPath() + ".json");

            try {
                IDataProvider.save(GSON, cache, LootTableManager.toJson(table), target);
                iChunUtil.LOGGER.info("Saved loot table {}", target);
            } catch (IOException ioexception) {
                iChunUtil.LOGGER.error("Couldn't save loot table {}", target, ioexception);
            }
        });
    }

    public static class Blocks extends BlockLootTables
            implements Data
    {
        private HashMap<Block, BiConsumer<BlockLootTables, Block>> blocksToGen = new HashMap<>();

        public Blocks(){}

        public Blocks add(Block block, BiConsumer<BlockLootTables, Block> consumer)
        {
            blocksToGen.put(block, consumer);
            return this;
        }

        @Override
        public LootParameterSet lootSet()
        {
            return LootParameterSets.BLOCK;
        }

        @Override
        public void accept(@Nonnull BiConsumer<ResourceLocation, LootTable.Builder> consumer)
        {
            blocksToGen.forEach((block, blockConsumer) -> blockConsumer.accept(this, block));

            Set<ResourceLocation> set = Sets.newHashSet(); //added but never used?
            blocksToGen.keySet().forEach((block) -> {
                ResourceLocation table = block.getLootTable();
                if(!LootTables.EMPTY.equals(table) && set.add(table))
                {
                    LootTable.Builder builder = this.field_218581_i.remove(table);
                    if (builder == null)
                        throw new IllegalStateException(String.format("Missing loot table '%s' for '%s'", table, block.getRegistryName()));

                    consumer.accept(table, builder);
                }
            });

            if (!this.field_218581_i.isEmpty())
                throw new IllegalStateException("Created block loot tables for non-blocks: " + this.field_218581_i.keySet());
        }
    }

    public static class Entities extends EntityLootTables
            implements Data
    {
        private HashMap<EntityType<?>, BiConsumer<EntityLootTables, EntityType<?>>> entitiesToGen = new HashMap<>();

        public Entities(){}

        public Entities add(EntityType<?> type, BiConsumer<EntityLootTables, EntityType<?>> consumer)
        {
            entitiesToGen.put(type, consumer);
            return this;
        }

        @Override
        public LootParameterSet lootSet()
        {
            return LootParameterSets.ENTITY;
        }

        @Override
        public void accept(@Nonnull BiConsumer<ResourceLocation, LootTable.Builder> consumer)
        {
            entitiesToGen.forEach((block, blockConsumer) -> blockConsumer.accept(this, block));

            Set<ResourceLocation> set = Sets.newHashSet();
            entitiesToGen.keySet().forEach((type) -> {
                if(type.getClassification() != EntityClassification.MISC)
                {
                    ResourceLocation table = type.getLootTable();
                    if(table != LootTables.EMPTY && set.add(table))
                    {
                        LootTable.Builder builder = this.field_218587_b.remove(table);
                        if (builder == null)
                            throw new IllegalStateException(String.format("Missing loot table '%s' for '%s'", table, type.getRegistryName()));

                        consumer.accept(table, builder);
                    }
                }
            });

            if (!this.field_218587_b.isEmpty())
                throw new IllegalStateException("Created entity loot tables for non-living entities: " + this.field_218587_b.keySet());
        }
    }

    public interface Data extends Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>
    {
        LootParameterSet lootSet();
    }
}

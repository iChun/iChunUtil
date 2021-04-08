package me.ichun.mods.ichunutil.common.data;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.function.Consumer;

//Large parts from AdvancementProvider
public class AdvancementGen implements IDataProvider
{
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    private final DataGenerator gen;
    private final Consumer<Consumer<Advancement>> consumer;

    public AdvancementGen(@Nonnull DataGenerator gen, @Nonnull Consumer<Consumer<Advancement>> consumer)
    {
        this.gen = gen;
        this.consumer = consumer;
    }

    @Override
    public String getName() {
        return "Advancements";
    }

    @Override
    public void act(DirectoryCache cache) {
        Path path = this.gen.getOutputFolder();
        Set<ResourceLocation> set = Sets.newHashSet();

        this.consumer.accept((advancement) -> {
            if (!set.add(advancement.getId())) {
                throw new IllegalStateException("Duplicate advancement " + advancement.getId());
            } else {
                Path path1 = getPath(path, advancement);

                try {
                    IDataProvider.save(GSON, cache, advancement.copy().serialize(), path1);
                    iChunUtil.LOGGER.info("Saved advancement: {}", path1);
                } catch (IOException ioexception) {
                    iChunUtil.LOGGER.error("Couldn't save advancement {}", path1, ioexception);
                }

            }
        });
    }

    private static Path getPath(Path pathIn, Advancement advancementIn) {
        return pathIn.resolve("data/" + advancementIn.getId().getNamespace() + "/advancements/" + advancementIn.getId().getPath() + ".json");
    }
}

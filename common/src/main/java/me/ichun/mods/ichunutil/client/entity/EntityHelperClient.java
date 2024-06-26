package me.ichun.mods.ichunutil.client.entity;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class EntityHelperClient
{
    public static List<? extends Entity> clientGetTarget(@NotNull String input)
    {
        if(Minecraft.getInstance().player != null)
        {
            Player player = Minecraft.getInstance().player;
            if(input.startsWith("@")) //entity selector
            {
                EntitySelectorParser parser = new EntitySelectorParser(new StringReader(input), true);
                EntitySelector selector;
                try
                {
                    selector = parser.parse();
                }
                catch(CommandSyntaxException e)
                {
                    return Collections.emptyList();
                }

                //taken from EntitySelector.select
                Vec3 vec3d = selector.position.apply(new Vec3(player.getX(), player.getY(), player.getZ()));
                Predicate<Entity> predicate = selector.getPredicate(vec3d);
                if (selector.currentEntity) {
                    return (List<? extends Entity>)(predicate.test(player) ? Lists.newArrayList(player) : Collections.emptyList());
                } else {
                    List<Entity> list = Lists.newArrayList();
                    list.addAll(player.level.getEntities(selector.type, selector.aabb != null ? selector.aabb.move(vec3d) : player.getBoundingBox().inflate(256, 256, 256), predicate));

                    return selector.sortAndLimit(vec3d, list);
                }
            }
            else //getting by username or uuid
            {
                try {
                    UUID uuid = UUID.fromString(input);
                    for(Entity entity : Minecraft.getInstance().level.entitiesForRendering())
                    {
                        if(uuid.equals(entity.getUUID()))
                        {
                            return Lists.newArrayList(entity);
                        }
                    }
                    for(Player worldPlayer : player.level.players())
                    {
                        if(worldPlayer.getUUID().equals(uuid))
                        {
                            return Lists.newArrayList(worldPlayer);
                        }
                    }
                } catch (IllegalArgumentException var4) {
                    for(Player worldPlayer : player.level.players())
                    {
                        if(worldPlayer.getName().getString().equals(input))
                        {
                            return Lists.newArrayList(worldPlayer);
                        }
                    }
                }
            }
        }
        return Collections.emptyList();
    }
}

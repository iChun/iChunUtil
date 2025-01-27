package me.ichun.mods.ichunutil.client.entity;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.ichun.mods.ichunutil.mixin.EntitySelectorAccessorMixin;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

@Environment(EnvType.CLIENT)
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

                //taken from EntitySelector.findEntities
                Vec3 vec3d = ((EntitySelectorAccessorMixin)selector).getPosition().apply(new Vec3(player.getX(), player.getY(), player.getZ()));
                AABB aABB = ((EntitySelectorAccessorMixin)selector).getAabb() != null ? ((EntitySelectorAccessorMixin)selector).getAabb().move(vec3d) : player.getBoundingBox().inflate(256, 256, 256);
                Predicate<Entity> predicate;
                if (((EntitySelectorAccessorMixin)selector).getCurrentEntity()) {
                    predicate = ((EntitySelectorAccessorMixin)selector).invokeGetPredicate(vec3d, aABB, (FeatureFlagSet)null);
                    return predicate.test(player) ? List.of(player) : List.of();
                } else {
                    predicate = ((EntitySelectorAccessorMixin)selector).invokeGetPredicate(vec3d, aABB, (FeatureFlagSet)player.level().enabledFeatures());

                    List<Entity> list = new ObjectArrayList();
                    list.addAll(player.level().getEntities(((EntitySelectorAccessorMixin)selector).getType(), ((EntitySelectorAccessorMixin)selector).getAabb() != null ? ((EntitySelectorAccessorMixin)selector).getAabb().move(vec3d) : player.getBoundingBox().inflate(256, 256, 256), predicate));

                    return ((EntitySelectorAccessorMixin)selector).invokeSortAndLimit(vec3d, list);
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
                    for(Player worldPlayer : player.level().players())
                    {
                        if(worldPlayer.getUUID().equals(uuid))
                        {
                            return Lists.newArrayList(worldPlayer);
                        }
                    }
                } catch (IllegalArgumentException var4) {
                    //is when the string passed isn't a UUID
                    for(Player worldPlayer : player.level().players())
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

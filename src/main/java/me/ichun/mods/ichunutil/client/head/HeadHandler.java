package me.ichun.mods.ichunutil.client.head;

import me.ichun.mods.ichunutil.client.head.entity.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.passive.fish.CodEntity;
import net.minecraft.entity.passive.fish.PufferfishEntity;
import net.minecraft.entity.passive.fish.SalmonEntity;
import net.minecraft.entity.passive.fish.TropicalFishEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.passive.horse.LlamaEntity;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nullable;
import java.util.HashMap;

public class HeadHandler
{
    public static HashMap<Class<? extends LivingEntity>, HeadBase<?>> MODEL_OFFSET_HELPERS = new HashMap<Class<? extends LivingEntity>, HeadBase<?>>() {{
        put(AbstractHorseEntity.class, new HeadHorse());
        put(AbstractIllagerEntity.class, new HeadBase<>().setEyeOffset(0F, 3.2F/16F, 4F/16F).setHalfInterpupillaryDistance(1.9F / 16F).setEyeScale(0.7F));
        put(AbstractSkeletonEntity.class, new HeadBase<>());
        put(AbstractVillagerEntity.class, new HeadBase<>().setEyeOffset(0F, 3.2F/16F, 4F/16F).setHalfInterpupillaryDistance(1.9F / 16F).setEyeScale(0.7F).setPupilColour(0F, 150F/255F, 17F/255F));
        put(PlayerEntity.class, new HeadBase<>());
        put(BatEntity.class, new HeadBat());
        put(BeeEntity.class, new HeadBee());
        put(BlazeEntity.class, new HeadBase<>().setEyeOffset(0F, 0F, 4F/16F));
        put(CatEntity.class, new HeadCat());
        put(ChickenEntity.class, new HeadBase<>().setEyeOffset(0F, 4.5F/16F, 2F/16F).setHalfInterpupillaryDistance(1.5F / 16F).setEyeScale(0.375F));
        put(CodEntity.class, new HeadBase<>().setEyeOffset(0F, 0.5F/16F, 1.5F/16F).setHalfInterpupillaryDistance(1F/16F).setEyeScale(0.4F).setSideEyed());
        put(CowEntity.class, new HeadBase<>().setEyeOffset(0F, 1F/16F, 6F/16F).setHalfInterpupillaryDistance(3F / 16F));
        put(CreeperEntity.class, new HeadBase<>().setEyeOffset(0F, 5F/16F, 4F/16F)); //make creeper maaaaaaad with narrowing pupils?
        put(DolphinEntity.class, new HeadBase<>().setEyeOffset(0F, 2.5F/16F, 3F/16F).setHalfInterpupillaryDistance(4F/16F).setSideEyed().setPupilColour(8F / 255F, 9F / 255F, 31F / 255F));
        put(EnderDragonEntity.class, new HeadDragon());
        put(ElderGuardianEntity.class, new HeadGuardian().setIrisColour(203F / 255F, 177F / 255F, 165F / 255F).setPupilColour(237F / 255F, 228F / 255F, 224F / 255F));
        put(EndermanEntity.class, new HeadEnderman());
        put(EndermiteEntity.class, new HeadEndermite());
        put(FoxEntity.class, new HeadBase<>().setEyeOffset(-1F/16F, -1.5F/16F, 5F/16F).setHalfInterpupillaryDistance(3F/16F).setEyeScale(0.65F));
        put(GhastEntity.class, new HeadGhast());
        put(GuardianEntity.class, new HeadGuardian());
        put(IronGolemEntity.class, new HeadBase<>().setEyeOffset(0F, 6F/16F, 5.5F/16F));
        put(LlamaEntity.class, new HeadLlama());
        put(MagmaCubeEntity.class, new HeadMagmaCube());
        put(MooshroomEntity.class, new HeadBase<>().setEyeOffset(0F, 1F/16F, 6F/16F).setHalfInterpupillaryDistance(3F / 16F));
        put(OcelotEntity.class, new HeadBase<>().setEyeOffset(0F, 1F/16F, 3F/16F).setHalfInterpupillaryDistance(1.5F / 16F).setEyeScale(0.7F).setPupilColour(31F / 255F , 111F / 255F, 50F / 255F));
        put(PandaEntity.class, new HeadBase<>().setEyeOffset(0F, 0.5F/16F, 4F/16F).setHalfInterpupillaryDistance(3.5F/16F).setEyeScale(0.65F));
        put(ParrotEntity.class, new HeadParrot());
        put(PhantomEntity.class, new HeadPhantom());
        put(PigEntity.class, new HeadBase<>().setEyeOffset(0F, 0.5F/16F, 8F/16F).setHalfInterpupillaryDistance(3F/16F));
        put(ZombiePigmanEntity.class, new HeadPigZombie());
        put(PolarBearEntity.class, new HeadBase<>().setEyeOffset(0F, -0.5F/16F, 3F/16F).setEyeScale(0.4F));
        put(PufferfishEntity.class, new HeadPufferfish());
        put(RabbitEntity.class, new HeadRabbit());
        put(RavagerEntity.class, new HeadRavager());
        put(SalmonEntity.class, new HeadBase<>().setEyeOffset(0F, 0.5F/16F, 1.5F/16F).setHalfInterpupillaryDistance(1F/16F).setEyeScale(0.4F).setSideEyed());
        put(SheepEntity.class, new HeadSheep());
        put(ShulkerEntity.class, new HeadShulker());
        put(SilverfishEntity.class, new HeadBase<>().setEyeOffset(0F, 0F, 1F/16F).setHalfInterpupillaryDistance(1F/16F).setEyeScale(0.5F));
        put(SlimeEntity.class, new HeadBase<>().setEyeOffset(0F, -19F/16F, 4F/16F));
        put(SnowGolemEntity.class, new HeadSnowman());
        put(SpiderEntity.class, new HeadSpider());
        put(SquidEntity.class, new HeadBase<>().setEyeOffset(0F, 1F/16F, 6F/16F).setHalfInterpupillaryDistance(3F/16F));
        put(TropicalFishEntity.class, new HeadTropicalFish());
        put(TurtleEntity.class, new HeadBase<>().setEyeOffset(0F, -1.5F/16F, 1F/16F).setHalfInterpupillaryDistance(3F/16F).setEyeScale(0.65F).setSideEyed());
        put(VexEntity.class, new HeadBase<>());
        put(WitchEntity.class, new HeadBase<>().setEyeOffset(0F, 3.2F/16F, 4F/16F).setHalfInterpupillaryDistance(1.9F / 16F).setEyeScale(0.7F));
        put(WitherEntity.class, new HeadWither());
        put(WolfEntity.class, new HeadWolf());
        put(ZombieEntity.class, new HeadBase<>());
        put(ZombieVillagerEntity.class, new HeadBase<>().setEyeOffset(0F, 3.2F/16F, 4F/16F).setHalfInterpupillaryDistance(1.9F / 16F).setEyeScale(0.7F));
    }};

    @Nullable
    public static HeadBase<?> getHelperBase(Class<? extends LivingEntity> clz)
    {
        if(MODEL_OFFSET_HELPERS.containsKey(clz))
        {
            return MODEL_OFFSET_HELPERS.get(clz);
        }
        HeadBase<?> helper = null;
        Class clzz = clz.getSuperclass();
        if(clzz != LivingEntity.class)
        {
            helper = getHelperBase(clzz);
            if(helper != null)
            {
                helper = helper.clone();
            }
        }
        MODEL_OFFSET_HELPERS.put(clz, helper);
        return helper;
    }
}

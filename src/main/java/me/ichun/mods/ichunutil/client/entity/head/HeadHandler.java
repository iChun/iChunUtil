package me.ichun.mods.ichunutil.client.entity.head;

import me.ichun.mods.ichunutil.api.client.head.HeadBase;
import me.ichun.mods.ichunutil.client.model.util.ModelHelper;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.module.tabula.project.components.CubeInfo;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;

import javax.annotation.Nullable;
import java.util.HashMap;

public class HeadHandler
{
    public static HashMap<Class<? extends EntityLivingBase>, HeadBase> modelOffsetHelpers = new HashMap<Class<? extends EntityLivingBase>, HeadBase>() {{
        put(AbstractHorse.class, new HeadHorse());
        put(AbstractIllager.class, new HeadBase().setEyeOffset(0F, 3.2F/16F, 4F/16F).setHalfInterpupillaryDistance(1.9F / 16F).setEyeScale(0.7F));
        put(AbstractSkeleton.class, new HeadBiped());
        put(EntityPlayer.class, new HeadPlayer());
        put(EntityBat.class, new HeadBat());
        put(EntityBlaze.class, new HeadBase().setEyeOffset(0F, 0F, 4F/16F));
        put(EntityChicken.class, new HeadBase().setEyeOffset(0F, 4.5F/16F, 2F/16F).setHalfInterpupillaryDistance(1.5F / 16F).setEyeScale(0.375F));
        put(EntityCow.class, new HeadBase().setEyeOffset(0F, 1F/16F, 6F/16F).setHalfInterpupillaryDistance(3F / 16F));
        put(EntityCreeper.class, new HeadBase().setEyeOffset(0F, 5F/16F, 4F/16F)); //make creeper maaaaaaad with narrowing pupils
        put(EntityDragon.class, new HeadDragon());
        put(EntityElderGuardian.class, new HeadElderGuardian());
        put(EntityEnderman.class, new HeadEnderman());
        put(EntityEndermite.class, new HeadEndermite());
        put(EntityGhast.class, new HeadGhast());
        put(EntityGuardian.class, new HeadGuardian());
        put(EntityIronGolem.class, new HeadBase().setEyeOffset(0F, 6F/16F, 5.5F/16F));
        put(EntityLlama.class, new HeadLlama());
        put(EntityMagmaCube.class, new HeadMagmaCube());
        put(EntityMooshroom.class, new HeadBase().setEyeOffset(0F, 1F/16F, 6F/16F).setHalfInterpupillaryDistance(3F / 16F));
        put(EntityOcelot.class, new HeadOcelot());
        put(EntityParrot.class, new HeadParrot());
        put(EntityPig.class, new HeadBase().setEyeOffset(0F, 0.5F/16F, 8F/16F).setHalfInterpupillaryDistance(3F/16F));
        put(EntityPigZombie.class, new HeadPigZombie());
        put(EntityPolarBear.class, new HeadBase().setEyeOffset(0F, -0.5F/16F, 3F/16F).setEyeScale(0.4F));
        put(EntityRabbit.class, new HeadRabbit());
        put(EntitySheep.class, new HeadSheep());
        put(EntityShulker.class, new HeadShulker());
        put(EntitySilverfish.class, new HeadBase().setEyeOffset(0F, 0F, 1F/16F).setHalfInterpupillaryDistance(1F/16F).setEyeScale(0.5F));
        put(EntitySlime.class, new HeadBase().setEyeOffset(0F, -19F/16F, 4F/16F));
        put(EntitySnowman.class, new HeadSnowman());
        put(EntitySpider.class, new HeadSpider());
        put(EntitySquid.class, new HeadBase().setEyeOffset(0F, 1F/16F, 6F/16F).setHalfInterpupillaryDistance(3F/16F));
        put(EntityVex.class, new HeadBiped());
        put(EntityVillager.class, new HeadBase().setEyeOffset(0F, 3.2F/16F, 4F/16F).setHalfInterpupillaryDistance(1.9F / 16F).setEyeScale(0.7F));
        put(EntityWitch.class, new HeadBase().setEyeOffset(0F, 3.2F/16F, 4F/16F).setHalfInterpupillaryDistance(1.9F / 16F).setEyeScale(0.7F));
        put(EntityWither.class, new HeadWither());
        put(EntityWolf.class, new HeadWolf());
        put(EntityZombie.class, new HeadBase());
        put(EntityZombieVillager.class, new HeadBase().setEyeOffset(0F, 3.2F/16F, 4F/16F).setHalfInterpupillaryDistance(1.9F / 16F).setEyeScale(0.7F));
    }};

    @Nullable
    public static HeadBase getHelperBase(Class<? extends EntityLivingBase> clz)
    {
        HeadBase helper = modelOffsetHelpers.get(clz);
        Class clzz = clz.getSuperclass();
        while(helper == null && clzz != EntityLivingBase.class)
        {
            helper = getHelperBase(clzz);
            if(helper != null)
            {
                helper = helper.clone();
                modelOffsetHelpers.put(clz, helper);
                break;
            }
            clzz = clzz.getSuperclass();
        }
        return helper;
    }

    public static void setHeadModel(HeadBase helper, RenderLivingBase renderer)
    {
        if(helper.headModel == null || iChunUtil.config.aggressiveHeadTracking == 1 || iChunUtil.config.aggressiveHeadTracking == 2 && renderer instanceof RenderPlayer)
        {
            ModelBase model = renderer.getMainModel();
            helper.headModel = new ModelRenderer[1];
            if(model instanceof ModelBiped)
            {
                helper.headModel[0] = ((ModelBiped)model).bipedHead;
            }
            else if(model instanceof ModelHorse)
            {
                helper.headModel[0] = iChunUtil.config.horseEasterEgg == 1 ? ((ModelHorse)model).body : ((ModelHorse)model).head;
            }
            else if(model instanceof ModelLlama)
            {
                helper.headModel[0] = iChunUtil.config.horseEasterEgg == 1 ? ((ModelLlama)model).body : ((ModelLlama)model).head;
            }
            else if(model instanceof ModelIllager)
            {
                helper.headModel[0] = ((ModelIllager)model).head;
            }
            else if(model instanceof ModelBat)
            {
                helper.headModel[0] = ((ModelBat)model).batHead;
            }
            else if(model instanceof ModelBlaze)
            {
                helper.headModel[0] = ((ModelBlaze)model).blazeHead;
            }
            else if(model instanceof ModelChicken)
            {
                helper.headModel[0] = ((ModelChicken)model).head;
            }
            else if(model instanceof ModelQuadruped)
            {
                helper.headModel[0] = ((ModelQuadruped)model).head;
            }
            else if(model instanceof ModelCreeper)
            {
                helper.headModel[0] = ((ModelCreeper)model).head;
            }
            else if(model instanceof ModelDragon)
            {
                helper.headModel[0] = ((ModelDragon)model).head;
            }
            else if(model instanceof ModelEnderMite)
            {
                helper.headModel[0] = ((ModelEnderMite)model).bodyParts[0];
            }
            else if(model instanceof ModelGhast)
            {
                helper.headModel[0] = ((ModelGhast)model).body;
            }
            else if(model instanceof ModelGuardian)
            {
                helper.headModel[0] = ((ModelGuardian)model).guardianBody;
            }
            else if(model instanceof ModelIronGolem)
            {
                helper.headModel[0] = ((ModelIronGolem)model).ironGolemHead;
            }
            else if(model instanceof ModelMagmaCube)
            {
                helper.headModel[0] = ((ModelMagmaCube)model).core;
            }
            else if(model instanceof ModelOcelot)
            {
                helper.headModel[0] = ((ModelOcelot)model).ocelotHead;
            }
            else if(model instanceof ModelParrot)
            {
                helper.headModel[0] = ((ModelParrot)model).head;
            }
            else if(model instanceof ModelRabbit)
            {
                helper.headModel[0] = ((ModelRabbit)model).rabbitHead;
            }
            else if(model instanceof ModelShulker)
            {
                helper.headModel[0] = ((ModelShulker)model).head;
            }
            else if(model instanceof ModelSilverfish)
            {
                helper.headModel[0] = ((ModelSilverfish)model).silverfishBodyParts[0];
            }
            else if(model instanceof ModelSlime)
            {
                helper.headModel[0] = ((ModelSlime)model).slimeBodies;
            }
            else if(model instanceof ModelSnowMan)
            {
                helper.headModel[0] = ((ModelSnowMan)model).head;
            }
            else if(model instanceof ModelSpider)
            {
                helper.headModel[0] = ((ModelSpider)model).spiderHead;
            }
            else if(model instanceof ModelSquid)
            {
                helper.headModel[0] = ((ModelSquid)model).squidBody;
            }
            else if(model instanceof ModelVillager)
            {
                helper.headModel[0] = ((ModelVillager)model).villagerHead;
            }
            else if(model instanceof ModelWither)
            {
                helper.headModel = ((ModelWither)model).heads;
            }
            else if(model instanceof ModelWolf)
            {
                helper.headModel[0] = ((ModelWolf)model).wolfHeadMain;
            }
            for(ModelRenderer head : helper.headModel)
            {
                if(head == null)
                {
                    helper.headModel = null;
                    break;
                }
            }
        }
    }

    @Nullable
    public static CubeInfo getHeadInfo(HeadBase helper)
    {
        if(helper.headInfo == null && helper.headModel != null && !helper.headModel[0].cubeList.isEmpty())
        {
            ModelBox box = helper.headModel[0].cubeList.get(0);
            helper.headInfo = ModelHelper.createCubeInfoFromModelBox(helper.headModel[0], box, box.boxName != null ? (box.boxName.substring(box.boxName.lastIndexOf(".") + 1)) : "");
        }
        return (CubeInfo)helper.headInfo;
    }
}

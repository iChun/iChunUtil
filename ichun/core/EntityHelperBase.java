package ichun.core;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHalfSlab;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityHelperBase
{
	public static MovingObjectPosition getEntityLook(EntityLivingBase ent, double d)
	{
		return getEntityLook(ent, d, false);
	}

	public static MovingObjectPosition getEntityLook(EntityLivingBase ent, double d, boolean ignoreEntities)
	{
		return getEntityLook(ent, d, ignoreEntities, 1.0F);
	}

	public static MovingObjectPosition getEntityLook(EntityLivingBase ent, double d, boolean ignoreEntities, float renderTick)
	{
		if (ent == null)
		{
			return null;
		}

		double d1 = d;
		MovingObjectPosition mop = rayTrace(ent, d, renderTick);
		Vec3 vec3d = getPosition(ent, renderTick);

		if (mop != null)
		{
			d1 = mop.hitVec.distanceTo(vec3d);
		}

		double dd2 = d;

		if (d1 > dd2)
		{
			d1 = dd2;
		}

		d = d1;
		Vec3 vec3d1 = ent.getLook(renderTick);
		Vec3 vec3d2 = vec3d.addVector(vec3d1.xCoord * d, vec3d1.yCoord * d, vec3d1.zCoord * d);

		if (!ignoreEntities)
		{
			Entity entity1 = null;
			float f1 = 1.0F;
			List list = ent.worldObj.getEntitiesWithinAABBExcludingEntity(ent, ent.boundingBox.addCoord(vec3d1.xCoord * d, vec3d1.yCoord * d, vec3d1.zCoord * d).expand(f1, f1, f1));
			double d2 = 0.0D;

			for (int i = 0; i < list.size(); i++)
			{
				Entity entity = (Entity)list.get(i);

				if (!entity.canBeCollidedWith())
				{
					continue;
				}

				float f2 = entity.getCollisionBorderSize();
				AxisAlignedBB axisalignedbb = entity.boundingBox.expand(f2, f2, f2);
				MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3d, vec3d2);

				if (axisalignedbb.isVecInside(vec3d))
				{
					if (0.0D < d2 || d2 == 0.0D)
					{
						entity1 = entity;
						d2 = 0.0D;
					}

					continue;
				}

				if (movingobjectposition == null)
				{
					continue;
				}

				double d3 = vec3d.distanceTo(movingobjectposition.hitVec);

				if (d3 < d2 || d2 == 0.0D)
				{
					entity1 = entity;
					d2 = d3;
				}
			}

			if (entity1 != null)
			{
				mop = new MovingObjectPosition(entity1);
			}
		}

		return mop;
	}

	public static Vec3 getPosition(Entity ent, float par1)
	{
		return getPosition(ent, par1, false);
	}

	public static Vec3 getPosition(Entity ent, float par1, boolean midPoint)
	{
		if (par1 == 1.0F)
		{
			return ent.worldObj.getWorldVec3Pool().getVecFromPool(ent.posX, midPoint ? ((ent.boundingBox.minY + ent.boundingBox.maxY) / 2D) : (ent.posY + (ent.worldObj.isRemote ? 0.0D : (ent.getEyeHeight() - 0.09D))), ent.posZ);
		}
		else
		{
			double var2 = ent.prevPosX + (ent.posX - ent.prevPosX) * (double)par1;
			double var4 = midPoint ? ((ent.boundingBox.minY + ent.boundingBox.maxY) / 2D) : (ent.prevPosY + (ent.worldObj.isRemote ? 0.0D : (ent.getEyeHeight() - 0.09D)) + (ent.posY - ent.prevPosY) * (double)par1);
			double var6 = ent.prevPosZ + (ent.posZ - ent.prevPosZ) * (double)par1;
			return ent.worldObj.getWorldVec3Pool().getVecFromPool(var2, var4, var6);
		}
	}

	public static MovingObjectPosition rayTrace(EntityLivingBase ent, double distance, float par3)
	{
		return rayTrace(ent, distance, par3, false);
	}

	public static MovingObjectPosition rayTrace(EntityLivingBase ent, double distance, float par3, boolean midPoint)
	{
		Vec3 var4 = getPosition(ent, par3, midPoint);
		Vec3 var5 = ent.getLook(par3);
		Vec3 var6 = var4.addVector(var5.xCoord * distance, var5.yCoord * distance, var5.zCoord * distance);
		return ent.worldObj.clip(var4, var6);
	}

	public static MovingObjectPosition rayTrace(World world, Vec3 vec3d, Vec3 vec3d1, boolean flag, boolean flag1, boolean goThroughTransparentBlocks)
	{
		return rayTrace(world, vec3d, vec3d1, flag, flag1, goThroughTransparentBlocks, 200);
	}

	public static MovingObjectPosition rayTrace(World world, Vec3 vec3d, Vec3 vec3d1, boolean flag, boolean flag1, boolean goThroughTransparentBlocks, int distance)
	{
		if (Double.isNaN(vec3d.xCoord) || Double.isNaN(vec3d.yCoord) || Double.isNaN(vec3d.zCoord))
		{
			return null;
		}

		if (Double.isNaN(vec3d1.xCoord) || Double.isNaN(vec3d1.yCoord) || Double.isNaN(vec3d1.zCoord))
		{
			return null;
		}

		int i = MathHelper.floor_double(vec3d1.xCoord);
		int j = MathHelper.floor_double(vec3d1.yCoord);
		int k = MathHelper.floor_double(vec3d1.zCoord);
		int l = MathHelper.floor_double(vec3d.xCoord);
		int i1 = MathHelper.floor_double(vec3d.yCoord);
		int j1 = MathHelper.floor_double(vec3d.zCoord);
		int k1 = world.getBlockId(l, i1, j1);
		int i2 = world.getBlockMetadata(l, i1, j1);
		Block block = Block.blocksList[k1];

		if ((!flag1 || block == null || block.getCollisionBoundingBoxFromPool(world, l, i1, j1) != null) && k1 > 0 && block.canCollideCheck(i2, flag))
		{
			MovingObjectPosition movingobjectposition = block.collisionRayTrace(world, l, i1, j1, vec3d, vec3d1);

			if (movingobjectposition != null)
			{
				return movingobjectposition;
			}
		}

		for (int l1 = distance; l1-- >= 0;)
		{
			if (Double.isNaN(vec3d.xCoord) || Double.isNaN(vec3d.yCoord) || Double.isNaN(vec3d.zCoord))
			{
				return null;
			}

			if (l == i && i1 == j && j1 == k)
			{
				return null;
			}

			boolean flag2 = true;
			boolean flag3 = true;
			boolean flag4 = true;
			double d = 999D;
			double d1 = 999D;
			double d2 = 999D;

			if (i > l)
			{
				d = (double)l + 1.0D;
			}
			else if (i < l)
			{
				d = (double)l + 0.0D;
			}
			else
			{
				flag2 = false;
			}

			if (j > i1)
			{
				d1 = (double)i1 + 1.0D;
			}
			else if (j < i1)
			{
				d1 = (double)i1 + 0.0D;
			}
			else
			{
				flag3 = false;
			}

			if (k > j1)
			{
				d2 = (double)j1 + 1.0D;
			}
			else if (k < j1)
			{
				d2 = (double)j1 + 0.0D;
			}
			else
			{
				flag4 = false;
			}

			double d3 = 999D;
			double d4 = 999D;
			double d5 = 999D;
			double d6 = vec3d1.xCoord - vec3d.xCoord;
			double d7 = vec3d1.yCoord - vec3d.yCoord;
			double d8 = vec3d1.zCoord - vec3d.zCoord;

			if (flag2)
			{
				d3 = (d - vec3d.xCoord) / d6;
			}

			if (flag3)
			{
				d4 = (d1 - vec3d.yCoord) / d7;
			}

			if (flag4)
			{
				d5 = (d2 - vec3d.zCoord) / d8;
			}

			byte byte0 = 0;

			if (d3 < d4 && d3 < d5)
			{
				if (i > l)
				{
					byte0 = 4;
				}
				else
				{
					byte0 = 5;
				}

				vec3d.xCoord = d;
				vec3d.yCoord += d7 * d3;
				vec3d.zCoord += d8 * d3;
			}
			else if (d4 < d5)
			{
				if (j > i1)
				{
					byte0 = 0;
				}
				else
				{
					byte0 = 1;
				}

				vec3d.xCoord += d6 * d4;
				vec3d.yCoord = d1;
				vec3d.zCoord += d8 * d4;
			}
			else
			{
				if (k > j1)
				{
					byte0 = 2;
				}
				else
				{
					byte0 = 3;
				}

				vec3d.xCoord += d6 * d5;
				vec3d.yCoord += d7 * d5;
				vec3d.zCoord = d2;
			}

			Vec3 vec3d2 = world.getWorldVec3Pool().getVecFromPool(vec3d.xCoord, vec3d.yCoord, vec3d.zCoord);
			l = (int)(vec3d2.xCoord = MathHelper.floor_double(vec3d.xCoord));

			if (byte0 == 5)
			{
				l--;
				vec3d2.xCoord++;
			}

			i1 = (int)(vec3d2.yCoord = MathHelper.floor_double(vec3d.yCoord));

			if (byte0 == 1)
			{
				i1--;
				vec3d2.yCoord++;
			}

			j1 = (int)(vec3d2.zCoord = MathHelper.floor_double(vec3d.zCoord));

			if (byte0 == 3)
			{
				j1--;
				vec3d2.zCoord++;
			}

			int j2 = world.getBlockId(l, i1, j1);

			if (goThroughTransparentBlocks && isTransparent(j2))
			{
				continue;
			}

			int k2 = world.getBlockMetadata(l, i1, j1);
			Block block1 = Block.blocksList[j2];

			if ((!flag1 || block1 == null || block1.getCollisionBoundingBoxFromPool(world, l, i1, j1) != null) && j2 > 0 && block1.canCollideCheck(k2, flag))
			{
				MovingObjectPosition movingobjectposition1 = block1.collisionRayTrace(world, l, i1, j1, vec3d, vec3d1);

				if (movingobjectposition1 != null)
				{
					return movingobjectposition1;
				}
			}
		}

		return null;
	}

	public static boolean hasFuel(InventoryPlayer inventory, int itemID, int damage, int amount)
	{
		if (amount <= 0)
		{
			return true;
		}

		int amountFound = 0;

		for (int var3 = 0; var3 < inventory.mainInventory.length; ++var3)
		{
			if (inventory.mainInventory[var3] != null && inventory.mainInventory[var3].itemID == itemID && inventory.mainInventory[var3].getItemDamage() == damage)
			{
				amountFound += inventory.mainInventory[var3].stackSize;

				if (amountFound >= amount)
				{
					return true;
				}
			}
		}

		return false;
	}

	public static boolean isTransparent(int i)
	{
		return Block.lightOpacity[i] != 0xff;
	}

	public static boolean isLookingAtMoon(World world, EntityLivingBase ent, float renderTick, boolean goThroughTransparentBlocks)
	{
		if (ent.dimension == -1 || ent.dimension == 1)
		{
			return false;
		}

		//13000 - 18000 - 23000
		//0.26 - 0.50 - 0.74
		//rotYaw = -88 to -92, 268 to 272
		//opposite for the other end, -268 to -272, 88 to 92
		//at 0.26 = -88 to -92
		//at 0.4 = -86 to -94
		//at 0.425 = -85 to -95
		//at 0.45 = -83 to -97
		//at 0.475 = -78 to -102
		//at 0.4875 = -64 to -116 //can 360 from here on i guess?
				//at 0.5 = -0 to -180
				// y = range, x = 0.45 etc, e = standard constant
				// y=e^(8.92574x) - 90 and y=-e^(8.92574x) - 90
				// 1.423 = 0.26 to 0.4
				// 1.52 = 0.4 to
				double de = 2.71828183D;
				float f = world.getCelestialAngle(1.0F);

				if (!(f >= 0.26D && f <= 0.74D))
				{
					return false;
				}

				float f2 = f > 0.5F ? f - 0.5F : 0.5F - f;
				float f3 = ent.rotationYaw > 0F ? 270 : -90;
				f3 = f > 0.5F ? ent.rotationYaw > 0F ? 90 : -270 : f3;
				f = f > 0.5F ? 1.0F - f : f;

				if (f <= 0.475)
				{
					de = 2.71828183D;
				}
				else if (f <= 0.4875)
				{
					de = 3.88377D;
				}
				else if (f <= 0.4935)
				{
					de = 4.91616;
				}
				else if (f <= 0.4965)
				{
					de = 5.40624;
				}
				else if (f <= 0.5000)
				{
					de = 9.8;
				}

				//yaw check = player.rotationYaw % 360 <= Math.pow(de, (4.92574 * mc.theWorld.getCelestialAngle(1.0F))) + f3 && mc.thePlayer.rotationYaw % 360 >= -Math.pow(de, (4.92574 * mc.theWorld.getCelestialAngle(1.0F))) + f3
						boolean yawCheck = ent.rotationYaw % 360 <= Math.pow(de, (4.92574 * world.getCelestialAngle(1.0F))) + f3 && ent.rotationYaw % 360 >= -Math.pow(de, (4.92574 * world.getCelestialAngle(1.0F))) + f3;
						float ff = world.getCelestialAngle(1.0F);
						ff = ff > 0.5F ? 1.0F - ff : ff;
						ff -= 0.26F;
						ff = (ff / 0.26F) * -94F - 4F;
						//pitch check = mc.thePlayer.rotationPitch <= ff + 2.5F && mc.thePlayer.rotationPitch >= ff - 2.5F
								boolean pitchCheck = ent.rotationPitch <= ff + 2.5F && ent.rotationPitch >= ff - 2.5F;
								Vec3 vec3d = getPosition(ent, renderTick);
								Vec3 vec3d1 = ent.getLook(renderTick);
								Vec3 vec3d2 = vec3d.addVector(vec3d1.xCoord * 500D, vec3d1.yCoord * 500D, vec3d1.zCoord * 500D);
								boolean mopCheck = rayTrace(ent.worldObj, vec3d, vec3d2, true, false, goThroughTransparentBlocks, 500) == null;
								return (yawCheck && pitchCheck && mopCheck);
	}

	public static boolean consumeInventoryItem(InventoryPlayer inventory, int itemID, int damage, int amount)
	{
		if (amount <= 0)
		{
			return true;
		}

		int amountFound = 0;

		for (int var3 = 0; var3 < inventory.mainInventory.length; ++var3)
		{
			if (inventory.mainInventory[var3] != null && inventory.mainInventory[var3].itemID == itemID && inventory.mainInventory[var3].getItemDamage() == damage)
			{
				amountFound += inventory.mainInventory[var3].stackSize;

				if (amountFound >= amount)
				{
					break;
				}
			}
		}

		if (amountFound >= amount)
		{
			for (int var3 = 0; var3 < inventory.mainInventory.length; ++var3)
			{
				if (inventory.mainInventory[var3] != null && inventory.mainInventory[var3].itemID == itemID && inventory.mainInventory[var3].getItemDamage() == damage)
				{
					while (amount > 0 && inventory.mainInventory[var3] != null && inventory.mainInventory[var3].stackSize > 0)
					{
						amount--;
						inventory.mainInventory[var3].stackSize--;

						if (inventory.mainInventory[var3].stackSize <= 0)
						{
							inventory.mainInventory[var3] = null;
						}

						if (amount <= 0)
						{
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	public static void forceJump(EntityLivingBase ent)
	{
		try
		{
			Method m = EntityLivingBase.class.getDeclaredMethod(ObfHelper.obfuscation ? ObfHelper.jumpObf : ObfHelper.jumpDeobf);
			m.setAccessible(true);
			m.invoke(ent);
		}
		catch (NoSuchMethodException e)
		{
			ent.motionY = 0.41999998688697815D;

			if (ent.isPotionActive(Potion.jump))
			{
				ent.motionY += (double)((float)(ent.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F);
			}

			if (ent.isSprinting())
			{
				float var1 = ent.rotationYaw * 0.017453292F;
				ent.motionX -= (double)(MathHelper.sin(var1) * 0.2F);
				ent.motionZ += (double)(MathHelper.cos(var1) * 0.2F);
			}

			ent.isAirBorne = true;
			ForgeHooks.onLivingJump(ent);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static String getDeathSound(EntityLivingBase ent)
	{
		String s = "";

		try
		{
			Class entClass = ent.getClass();
			Method m = entClass.getDeclaredMethod(ObfHelper.obfuscation ? ObfHelper.getDeathSoundObf : ObfHelper.getDeathSoundDeobf, new Class[] {});
			m.setAccessible(true);
			s = (String)m.invoke(ent, (Object[])null);
		}
		catch (Exception e)
		{}

		return s;
	}

	@SideOnly(Side.CLIENT)
	public static void renderHand(float f)
	{
		boolean hideGUI = Minecraft.getMinecraft().gameSettings.hideGUI;
		Minecraft.getMinecraft().gameSettings.hideGUI = false;
		try
		{
			Method m = EntityRenderer.class.getDeclaredMethod(ObfHelper.renderHandObf, float.class, int.class);
			m.setAccessible(true);
			m.invoke(Minecraft.getMinecraft().entityRenderer, f, 0);
			m.invoke(Minecraft.getMinecraft().entityRenderer, f, 1);
		}
		catch (NoSuchMethodException e)
		{
			try
			{
				Method m = EntityRenderer.class.getDeclaredMethod(ObfHelper.renderHandDeobf, float.class, int.class);
				m.setAccessible(true);
				m.invoke(Minecraft.getMinecraft().entityRenderer, f, 0);
				m.invoke(Minecraft.getMinecraft().entityRenderer, f, 1);
			}
			catch (Exception e1)
			{
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		Minecraft.getMinecraft().gameSettings.hideGUI = hideGUI;
	}

	//    public static void setPlayerLocation(EntityPlayerMP player, double d, double d1, double d2, float f, float f1)
	//    {
	//    	try
	//    	{
	////    		ObfuscationReflectionHelper.setPrivateValue(NetServerHandler.class, player.playerNetServerHandler, false, "r", "hasMoved");
	////    		ObfuscationReflectionHelper.setPrivateValue(NetServerHandler.class, player.playerNetServerHandler, d, "o", "lastPosX");
	////    		ObfuscationReflectionHelper.setPrivateValue(NetServerHandler.class, player.playerNetServerHandler, d1, "p", "lastPosY");
	////    		ObfuscationReflectionHelper.setPrivateValue(NetServerHandler.class, player.playerNetServerHandler, d2, "q", "lastPosZ");
	////    		player.setPositionAndRotation(d, d1, d2, f, f1);
	//    	}
	//    	catch(Exception e)
	//    	{
	//    		e.printStackTrace();
	//    		PortalGun.console("Forgot to update obfuscation!");
	//    	}
	//    }

	public static float updateRotation(float oriRot, float intendedRot, float maxChange)
	{
		float var4 = MathHelper.wrapAngleTo180_float(intendedRot - oriRot);

		if (var4 > maxChange)
		{
			var4 = maxChange;
		}

		if (var4 < -maxChange)
		{
			var4 = -maxChange;
		}

		return oriRot + var4;
	}

	public static float interpolateRotation(float prevRotation, float nextRotation, float partialTick)
	{
		float f3;

		for (f3 = nextRotation - prevRotation; f3 < -180.0F; f3 += 360.0F)
		{
			;
		}

		while (f3 >= 180.0F)
		{
			f3 -= 360.0F;
		}

		return prevRotation + partialTick * f3;
	}

	public static float interpolateValues(float prevVal, float nextVal, float partialTick)
	{
		return prevVal + partialTick * (nextVal - prevVal);
	}

	public static void faceEntity(Entity facer, Entity faced, float maxYaw, float maxPitch)
	{
		double d0 = faced.posX - facer.posX;
		double d1 = faced.posZ - facer.posZ;
		double d2;

		if (faced instanceof EntityLivingBase)
		{
			EntityLivingBase entitylivingbase = (EntityLivingBase)faced;
			d2 = entitylivingbase.posY + (double)entitylivingbase.getEyeHeight() - (facer.posY + (double)facer.getEyeHeight());
		}
		else
		{
			d2 = (faced.boundingBox.minY + faced.boundingBox.maxY) / 2.0D - (facer.posY + (double)facer.getEyeHeight());
		}

		double d3 = (double)MathHelper.sqrt_double(d0 * d0 + d1 * d1);
		float f2 = (float)(Math.atan2(d1, d0) * 180.0D / Math.PI) - 90.0F;
		float f3 = (float)(-(Math.atan2(d2, d3) * 180.0D / Math.PI));
		facer.rotationPitch = updateRotation(facer.rotationPitch, f3, maxPitch);
		facer.rotationYaw = updateRotation(facer.rotationYaw, f2, maxYaw);
	}

	public static void setVelocity(Entity entity, double d, double d1, double d2)
	{
		if (entity == null)
		{
			return;
		}

		entity.motionX = d;
		entity.motionY = d1;
		entity.motionZ = d2;
	}

	public static boolean destroyBlocksInAABB(Entity ent, AxisAlignedBB aabb)
	{
		int i = MathHelper.floor_double(aabb.minX);
		int j = MathHelper.floor_double(aabb.minY);
		int k = MathHelper.floor_double(aabb.minZ);
		int l = MathHelper.floor_double(aabb.maxX);
		int i1 = MathHelper.floor_double(aabb.maxY);
		int j1 = MathHelper.floor_double(aabb.maxZ);
		boolean flag = false;
		boolean flag1 = false;

		for (int k1 = i; k1 <= l; ++k1)
		{
			for (int l1 = j; l1 <= i1; ++l1)
			{
				for (int i2 = k; i2 <= j1; ++i2)
				{
					int j2 = ent.worldObj.getBlockId(k1, l1, i2);
					Block block = Block.blocksList[j2];

					if (block != null)
					{
						if (block.getBlockHardness(ent.worldObj, k1, l1, i2) >= 0F && block.canEntityDestroy(ent.worldObj, k1, l1, i2, ent) && ent.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing"))
						{
							flag1 = (ent.worldObj.isRemote ? true : (ent.worldObj.setBlockToAir(k1, l1, i2) || flag1));
						}
						else
						{
							flag = true;
						}
					}
				}
			}
		}

		if (flag1)
		{
			double d0 = aabb.minX + (aabb.maxX - aabb.minX) * (double)ent.worldObj.rand.nextFloat();
			double d1 = aabb.minY + (aabb.maxY - aabb.minY) * (double)ent.worldObj.rand.nextFloat();
			double d2 = aabb.minZ + (aabb.maxZ - aabb.minZ) * (double)ent.worldObj.rand.nextFloat();
			ent.worldObj.spawnParticle("largeexplode", d0, d1, d2, 0.0D, 0.0D, 0.0D);
		}

		return flag;
	}

	/**
     * Reads a compressed NBTTagCompound from the InputStream
     */
    public static NBTTagCompound readNBTTagCompound(DataInput par0DataInput) throws IOException
    {
        short short1 = par0DataInput.readShort();

        if (short1 < 0)
        {
            return null;
        }
        else
        {
            byte[] abyte = new byte[short1];
            par0DataInput.readFully(abyte);
            return CompressedStreamTools.decompress(abyte);
        }
    }

    /**
     * Writes a compressed NBTTagCompound to the OutputStream
     */
    public static void writeNBTTagCompound(NBTTagCompound par0NBTTagCompound, DataOutput par1DataOutput) throws IOException
    {
        if (par0NBTTagCompound == null)
        {
            par1DataOutput.writeShort(-1);
        }
        else
        {
            byte[] abyte = CompressedStreamTools.compress(par0NBTTagCompound);
            par1DataOutput.writeShort((short)abyte.length);
            par1DataOutput.write(abyte);
        }
    }

	public static void addPosition(Entity living, double offset, boolean subtract, int axis)
	{
	    if (axis == 0) //X axis
	    {
	        if (subtract)
	        {
	            living.lastTickPosX -= offset;
	            living.prevPosX -= offset;
	            living.posX -= offset;
	        }
	        else
	        {
	            living.lastTickPosX += offset;
	            living.prevPosX += offset;
	            living.posX += offset;
	        }
	    }
	    else if (axis == 1) //Y axis
	    {
	        if (subtract)
	        {
	            living.lastTickPosY -= offset;
	            living.prevPosY -= offset;
	            living.posY -= offset;
	        }
	        else
	        {
	            living.lastTickPosY += offset;
	            living.prevPosY += offset;
	            living.posY += offset;
	        }
	    }
	    else if (axis == 2) //Z axis
	    {
	        if (subtract)
	        {
	            living.lastTickPosZ -= offset;
	            living.prevPosZ -= offset;
	            living.posZ -= offset;
	        }
	        else
	        {
	            living.lastTickPosZ += offset;
	            living.prevPosZ += offset;
	            living.posZ += offset;
	        }
	    }
	}

}

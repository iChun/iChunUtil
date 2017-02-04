package me.ichun.mods.ichunutil.common.block;

import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockCompactPorkchop extends Block
{
    public BlockCompactPorkchop()
    {
        super(Material.CAKE);
        this.blockSoundType = new SoundType(0.8F, 1.0F, SoundEvents.ENTITY_PIG_AMBIENT, SoundEvents.ENTITY_PIG_AMBIENT, SoundEvents.ENTITY_PIG_AMBIENT, SoundEvents.ENTITY_PIG_AMBIENT, SoundEvents.ENTITY_PIG_AMBIENT)
        {
            public Random rand = new Random();

            @Override
            public float getPitch()
            {
                return (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F;
            }
        };
        setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
        setHardness(0.8F);

        setRegistryName(new ResourceLocation("ichunutil", "compact_porkchop"));
        setUnlocalizedName("ichunutil.block.compactporkchop");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> list)
    {
        if(iChunUtil.config.enableCompactPorkchop == 1)
        {
            list.add(new ItemStack(itemIn, 1, 0));
        }
    }

    //For ObfHelper use to check
    @Override
    public Block setBlockUnbreakable()
    {
        return super.setBlockUnbreakable();
    }
}

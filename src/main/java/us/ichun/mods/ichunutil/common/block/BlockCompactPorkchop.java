package us.ichun.mods.ichunutil.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import us.ichun.mods.ichunutil.common.iChunUtil;

import java.util.List;
import java.util.Random;

public class BlockCompactPorkchop extends Block
{
    public BlockCompactPorkchop()
    {
        super(Material.cake);
        this.stepSound = new SoundType("cloth", 1.0F, 1.0F)
        {
            public Random rand = new Random();

            @Override
            public float getFrequency()
            {
                return (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F;
            }

            @Override
            public String getBreakSound()
            {
                return "mob.pig.say";
            }

            @Override
            public String getStepSound()
            {
                return "mob.pig.say";
            }

            @Override
            public String getPlaceSound()
            {
                return "mob.pig.say";
            }
        };
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List list)
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

package com.builtbroken.threadedgrass;

import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Created by Cow Pi on 8/4/2015.
 */
public class BlockGrass2 extends BlockGrass
{
    public BlockGrass2()
    {
        setTickRandomly(false);
        setHardness(0.6F);
        setStepSound(soundTypeGrass);
        setBlockName("grass");
        setBlockTextureName("grass");
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random rand)
    {
        if (ThreadedGrass.blockUpdateThread == null)
        {
            if (!world.isRemote)
            {
                if (world.getBlockLightValue(x, y + 1, z) < 4 && world.getBlockLightOpacity(x, y + 1, z) > 2)
                {
                    world.setBlock(x, y, z, Blocks.dirt);
                }
                else if (world.getBlockLightValue(x, y + 1, z) >= 9)
                {
                    for (int l = 0; l < 4; ++l)
                    {
                        int i1 = x + rand.nextInt(3) - 1;
                        int j1 = y + rand.nextInt(5) - 3;
                        int k1 = z + rand.nextInt(3) - 1;
                        Block block = world.getBlock(i1, j1 + 1, k1);

                        if (world.getBlock(i1, j1, k1) == Blocks.dirt && world.getBlockMetadata(i1, j1, k1) == 0 && world.getBlockLightValue(i1, j1 + 1, k1) >= 4 && world.getBlockLightOpacity(i1, j1 + 1, k1) <= 2)
                        {
                            world.setBlock(i1, j1, k1, Blocks.grass);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z)
    {
        if (ThreadedGrass.blockUpdateThread != null)
        {
            ThreadedGrass.blockUpdateThread.que(world, x, y, z);
        }
    }
}

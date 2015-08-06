package com.builtbroken.threadedgrass.server;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Cow Pi on 8/4/2015.
 */
public class ThreadBlockUpdates extends Thread
{
    public final List<Block> blockTypesToUpdate = new ArrayList();
    public boolean killThread = false;
    public ConcurrentLinkedQueue<BlockTick> additionQue = new ConcurrentLinkedQueue();

    @Override
    public void run()
    {
        super.run();
        System.out.println("Run block update thread");
        //TODO make sure thread dies if Minecraft closes
        while (!killThread)
        {
            Iterator<BlockTick> it = additionQue.iterator();
            while(it.hasNext())
            {

            }
        }
    }

    public void que(World world, int x, int y, int z)
    {
        Block block = world.getBlock(x, y, z);
        if (block != null && world.blockExists(x, y, z) && blockTypesToUpdate.contains(block))
        {
            additionQue.add(new BlockTick(block, world, x, y, z));
        }
    }

    public class BlockTick
    {
        Block block;
        World world;
        int x, y, z;

        public BlockTick(Block block, World world, int x, int y, int z)
        {
            this.block = block;
            this.world = world;
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}

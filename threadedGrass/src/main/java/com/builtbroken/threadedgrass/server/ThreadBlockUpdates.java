package com.builtbroken.threadedgrass.server;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cow Pi on 8/4/2015.
 */
public class ThreadBlockUpdates extends Thread
{
    public final List<Block> blockTypesToUpdate = new ArrayList();
    public boolean killThread = false;

    @Override
    public void run()
    {
        super.run();
        //TODO make sure thread dies if Minecraft closes
        while(!killThread)
        {

        }
    }

    public void que(World world, int x, int y, int z)
    {
        Block block = world.getBlock(x, y, z);
        if(block != null && world.blockExists(x, y, z) && blockTypesToUpdate.contains(block))
        {

        }
    }
}

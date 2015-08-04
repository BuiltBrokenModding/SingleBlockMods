package com.builtbroken.threadedgrass;

import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.RegistryDelegate;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.world.World;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by Dark on 7/25/2015.
 */
public class CommonProxy implements IGuiHandler
{

    public void preInit()
    {
        ThreadedGrass.LOGGER.info("Replacing Minecraft's grass block to allow Multi-threading to function easier");
        Block grass = new BlockGrass2();
        try
        {
            //Set delegate name
            Method method = RegistryDelegate.Delegate.class.getMethod("setName", String.class);
            method.setAccessible(true);
            method.invoke(grass.delegate, "minecraft:grass");

            //Block.blockRegistry.addObject(2, "grass", new BlockGrass2());
            Field field = Block.blockRegistry.getClass().getField("underlyingIntegerMap");
            field.setAccessible(true);
            ((ObjectIntIdentityMap) field.get(Block.blockRegistry)).func_148746_a(grass, 2);
            Block.blockRegistry.putObject("minecraft:grass", grass);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Crashing game to prevent world corruption. Error: Failed to replace grass block", e);
        }
        ThreadedGrass.LOGGER.info("...Done");
    }

    public void init()
    {

    }

    public void postInit()
    {

    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        return null;
    }
}

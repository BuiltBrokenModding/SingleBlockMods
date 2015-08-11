package com.builtbroken.woodenrails;

import com.builtbroken.woodenrails.cart.EntityWoodenCart;
import com.builtbroken.woodenrails.cart.EnumCartTypes;
import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerHopper;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.IInventory;
import net.minecraft.world.World;

/**
 * Created by Dark on 7/25/2015.
 */
public class CommonProxy implements IGuiHandler
{
    public void preInit()
    {

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
        if (ID == 0)
        {
            Entity entity = world.getEntityByID(x);
            if (entity instanceof EntityWoodenCart && ((EntityWoodenCart) entity).getCartType() == EnumCartTypes.HOPPER)
                return new ContainerHopper(player.inventory, (IInventory) entity);
            else
                WoodenRails.LOGGER.error("Unknown entity[" + x + "," + entity + "] attempted to open a Hopper Gui ");
        }
        else if (ID == 1)
        {
            return new ContainerWorkbench(player.inventory, world, (int) player.posX, (int) player.posY, (int) player.posZ);
        }
        else
        {
            WoodenRails.LOGGER.error("Unknown Gui ID " + ID + " was opened at Dim@" + world.provider.dimensionId + " " + x + "x " + y + "y " + z + "z ");
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        return null;
    }
}

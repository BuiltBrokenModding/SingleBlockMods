package com.builtbroken.woodenrails;

import com.builtbroken.woodenrails.cart.EntityWoodenCart;
import com.builtbroken.woodenrails.cart.EnumCartTypes;
import com.builtbroken.woodenrails.cart.RenderWoodenCart;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.client.gui.GuiHopper;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.world.World;

/**
 * Created by Dark on 7/25/2015.
 */
public class ClientProxy extends CommonProxy
{
    @Override
    public void init()
    {
        super.init();
        if (WoodenRails.itemWoodCart != null)
        {
            RenderingRegistry.registerEntityRenderingHandler(EntityWoodenCart.class, new RenderWoodenCart());
        }
    }


    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if (ID == 0)
        {
            Entity entity = world.getEntityByID(x);
            if (entity instanceof EntityWoodenCart && ((EntityWoodenCart) entity).getCartType() == EnumCartTypes.HOPPER)
                return new GuiHopper(player.inventory, (IInventory) entity);
            else
                WoodenRails.LOGGER.error("Unknown entity[" + x + "," + entity + "] attempted to open a Hopper Gui ");
        }
        else if (ID == 1)
        {
            return new GuiCrafting(player.inventory, world, (int) player.posX, (int) player.posY, (int) player.posZ);
        }
        else
        {
            WoodenRails.LOGGER.error("Unknown Gui ID " + ID + " was opened at Dim@" + world.provider.dimensionId + " " + x + "x " + y + "y " + z + "z ");
        }
        return null;
    }
}

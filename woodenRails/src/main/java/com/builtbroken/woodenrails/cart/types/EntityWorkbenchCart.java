package com.builtbroken.woodenrails.cart.types;

import com.builtbroken.woodenrails.WoodenRails;
import com.builtbroken.woodenrails.cart.EntityWoodenCart;
import com.builtbroken.woodenrails.cart.EnumCartTypes;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

/**
 * Created by Dark on 8/11/2015.
 */
public class EntityWorkbenchCart extends EntityWoodenCart
{
    public EntityWorkbenchCart(World world)
    {
        super(world);
    }

    public EntityWorkbenchCart(World world, double xx, double yy, double zz)
    {
        super(world, xx, yy, zz);
    }

    @Override
    public int getMinecartType()
    {
        return 0;
    }

    @Override
    public boolean interactFirst(EntityPlayer player)
    {
        if (worldObj.isRemote)
        {
            player.openGui(WoodenRails.INSTANCE, 1, worldObj, getEntityId(), 0, 0);
        }
        return true;
    }

    @Override
    public EnumCartTypes getCartType()
    {
        return EnumCartTypes.WORKTABLE;
    }

    @Override
    public Block func_145820_n()
    {
        return Blocks.crafting_table;
    }
}

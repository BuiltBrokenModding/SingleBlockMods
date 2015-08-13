package com.builtbroken.woodenrails.cart.types;

import com.builtbroken.woodenrails.cart.EnumCartTypes;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

/**
 * Created by Dark on 8/11/2015.
 */
public class EntityChestCart extends EntityContainerCart
{
    public EntityChestCart(World world)
    {
        super(world);
    }

    public EntityChestCart(World world, double xx, double yy, double zz)
    {
        super(world, xx, yy, zz);
    }

    @Override
    public EnumCartTypes getCartType()
    {
        return EnumCartTypes.CHEST;
    }

    @Override
    public int getSizeInventory()
    {
        return 27;
    }

    @Override
    public int getMinecartType()
    {
        return 1;
    }

    @Override
    public Block func_145817_o()
    {
        return Blocks.chest;
    }

    @Override
    public int getDefaultDisplayTileOffset()
    {
        return 8;
    }
}

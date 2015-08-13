package com.builtbroken.woodenrails.cart.types;

import com.builtbroken.woodenrails.cart.EntityWoodenCart;
import com.builtbroken.woodenrails.cart.EnumCartTypes;
import net.minecraft.world.World;

/**
 * Created by Dark on 8/12/2015.
 */
public class EntityBCTank extends EntityWoodenCart
{
    public EntityBCTank(World world)
    {
        super(world);
    }

    public EntityBCTank(World world, double xx, double yy, double zz)
    {
        super(world, xx, yy, zz);
    }

    @Override
    public EnumCartTypes getCartType()
    {
        return EnumCartTypes.BC_TANK;
    }

    @Override
    public int getMinecartType()
    {
        return 0;
    }
}

package com.builtbroken.woodenrails.cart.types;

import com.builtbroken.woodenrails.cart.EntityWoodenCart;
import com.builtbroken.woodenrails.cart.EnumCartTypes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;

/**
 * Created by Dark on 8/11/2015.
 */
public class EntityEmptyCart extends EntityWoodenCart
{
    public EntityEmptyCart(World world)
    {
        super(world);
    }

    public EntityEmptyCart(World world, double xx, double yy, double zz)
    {
        super(world, xx, yy, zz);
    }

    @Override
    public EnumCartTypes getCartType()
    {
        return EnumCartTypes.EMPTY;
    }

    @Override
    public boolean interactFirst(EntityPlayer p_130002_1_)
    {
        if (MinecraftForge.EVENT_BUS.post(new MinecartInteractEvent(this, p_130002_1_)))
            return true;
        if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer && this.riddenByEntity != p_130002_1_)
        {
            return true;
        }
        else if (this.riddenByEntity != null && this.riddenByEntity != p_130002_1_)
        {
            return false;
        }
        else
        {
            if (!this.worldObj.isRemote)
            {
                p_130002_1_.mountEntity(this);
            }

            return true;
        }
    }

    @Override
    public int getMinecartType()
    {
        return 0;
    }
}

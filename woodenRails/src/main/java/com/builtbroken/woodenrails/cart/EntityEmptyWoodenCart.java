package com.builtbroken.woodenrails.cart;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * Created by Dark on 7/25/2015.
 */
public class EntityEmptyWoodenCart extends EntityWoodenCart
{
    public EntityEmptyWoodenCart(World world)
    {
        super(world);
    }

    public EntityEmptyWoodenCart(World world, double xx, double yy, double zz)
    {
        super(world, xx, yy, zz);
    }

    @Override
    public boolean interactFirst(EntityPlayer player)
    {
        if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.minecart.MinecartInteractEvent(this, player)))
            return true;
        if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer && this.riddenByEntity != player)
        {
            return true;
        }
        else if (this.riddenByEntity != null && this.riddenByEntity != player)
        {
            return false;
        }
        else
        {
            if (!this.worldObj.isRemote)
            {
                player.mountEntity(this);
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

package com.builtbroken.woodenrails.cart;

import com.builtbroken.woodenrails.WoodenRails;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

/**
 * Created by Dark on 7/25/2015.
 */
public class EntityWoodenCart extends EntityMinecart
{
    //TODO add fire damage to cart
    //TODO reduce max speed
    //TODO allow breaking on impact, add config for this option as well
    //TODO add minecart types
    //TODO have coal powered cart catch fire randomly, have tool tip "This doesn't look very safe"
    public EntityWoodenCart(World world)
    {
        super(world);
    }

    public EntityWoodenCart(World world, double xx, double yy, double zz)
    {
        super(world, xx, yy, zz);
    }

    @Override
    public ItemStack getCartItem()
    {
        return new ItemStack(WoodenRails.itemWoodCart);
    }

    @Override
    public boolean canBeRidden()
    {
        return false;
    }

    @Override
    public void killMinecart(DamageSource p_94095_1_)
    {
        this.setDead();
        ItemStack itemstack = new ItemStack(WoodenRails.itemWoodCart);
        /** TODO re-add after creating an access transformer
        if (this.entityName != null)
        {
            itemstack.setStackDisplayName(this.entityName);
        }
         */
        this.entityDropItem(itemstack, 0.0F);
    }

    protected void applyDrag()
    {
        if (this.riddenByEntity != null)
        {
            this.motionX *= 0.7;
            this.motionY *= 0.0D;
            this.motionZ *= 0.7;
        }
        else
        {
            this.motionX *= 0.8;
            this.motionY *= 0.0D;
            this.motionZ *= 0.8;
        }
    }

    @Override
    public int getMinecartType()
    {
        return 0;
    }
}

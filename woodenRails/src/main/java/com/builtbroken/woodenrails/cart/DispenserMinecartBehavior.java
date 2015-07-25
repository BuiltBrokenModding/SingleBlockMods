package com.builtbroken.woodenrails.cart;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.material.Material;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

/**
 * Created by Dark on 7/25/2015.
 */
public class DispenserMinecartBehavior extends BehaviorDefaultDispenseItem
{
    @Override
    public ItemStack dispenseStack(IBlockSource blockSource, ItemStack itemStack)
    {
        if (itemStack != null && itemStack.getItem() instanceof ItemWoodenCart)
        {
            EnumFacing enumfacing = BlockDispenser.func_149937_b(blockSource.getBlockMetadata());
            World world = blockSource.getWorld();

            double xx = blockSource.getX() + (double) ((float) enumfacing.getFrontOffsetX() * 1.125F);
            double yy = blockSource.getY() + (double) ((float) enumfacing.getFrontOffsetY() * 1.125F);
            double zz = blockSource.getZ() + (double) ((float) enumfacing.getFrontOffsetZ() * 1.125F);

            int i = blockSource.getXInt() + enumfacing.getFrontOffsetX();
            int j = blockSource.getYInt() + enumfacing.getFrontOffsetY();
            int k = blockSource.getZInt() + enumfacing.getFrontOffsetZ();

            Block block = world.getBlock(i, j, k);
            double deltaY;

            if (BlockRailBase.func_150051_a(block))
            {
                deltaY = 0.0D;
            }
            else
            {
                if (block.getMaterial() != Material.air || !BlockRailBase.func_150051_a(world.getBlock(i, j - 1, k)))
                {
                    return super.dispenseStack(blockSource, itemStack);
                }

                deltaY = -1.0D;
            }

            EntityMinecart cart = ItemWoodenCart.createNewCart(world, itemStack);

            cart.setPosition(xx, yy + deltaY, zz);
            if(cart != null)
            {
                if (itemStack.hasDisplayName())
                {
                    cart.setMinecartName(itemStack.getDisplayName());
                }

                world.spawnEntityInWorld(cart);
                itemStack.splitStack(1);
            }
            else
            {
                super.dispenseStack(blockSource, itemStack);
            }
        }
        return itemStack;
    }

    @Override
    protected void playDispenseSound(IBlockSource blockSource)
    {
        blockSource.getWorld().playAuxSFX(1000, blockSource.getXInt(), blockSource.getYInt(), blockSource.getZInt(), 0);
    }
}

package com.builtbroken.woodenrails.cart;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockRailBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by Dark on 7/25/2015.
 */
public class ItemWoodenCart extends Item
{
    public ItemWoodenCart()
    {
        this.maxStackSize = 3;
        this.setCreativeTab(CreativeTabs.tabTransport);
        BlockDispenser.dispenseBehaviorRegistry.putObject(this, new DispenserMinecartBehavior());
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float xHit, float yHit, float zHit)
    {
        if (BlockRailBase.func_150051_a(world.getBlock(x, y, z)))
        {
            EntityWoodenCart cart = createNewCart(world, itemStack);
            if (cart != null)
            {
                if (!world.isRemote)
                {
                    cart.setPosition(x + 0.5F, y + 0.5F, z + 0.5F);

                    if (itemStack.hasDisplayName())
                        cart.setMinecartName(itemStack.getDisplayName());

                    world.spawnEntityInWorld(cart);
                }
                if (!player.capabilities.isCreativeMode)
                    --itemStack.stackSize;
                return true;
            }
        }
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(Item item, CreativeTabs tab, List items)
    {
        items.add(new ItemStack(item, 1, 0));
    }

    public static EntityWoodenCart createNewCart(World world, ItemStack itemStack)
    {
        switch (itemStack.getItemDamage())
        {

        }
        return new EntityEmptyWoodenCart(world);
    }
}

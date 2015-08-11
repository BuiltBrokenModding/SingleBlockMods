package com.builtbroken.woodenrails.cart;

import com.builtbroken.woodenrails.WoodenRails;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockRailBase;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by Dark on 7/25/2015.
 */
public class ItemWoodenCart extends Item
{
    @SideOnly(Side.CLIENT)
    public static IIcon furnaceMinecraft;

    public ItemWoodenCart()
    {
        this.maxStackSize = 3;
        this.setUnlocalizedName(WoodenRails.PREFIX + "WoodenCart");
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
    public IIcon getIconFromDamage(int meta)
    {
        return this.itemIcon;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister reg)
    {
        this.itemIcon = reg.registerIcon(WoodenRails.PREFIX + "minecart_normal");
        this.furnaceMinecraft = reg.registerIcon(WoodenRails.PREFIX + "minecart_furnace");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(Item item, CreativeTabs tab, List items)
    {
        items.add(new ItemStack(item, 1, 0));
    }

    public static EntityWoodenCart createNewCart(World world, ItemStack itemStack)
    {
        EntityWoodenCart cart = new EntityWoodenCart(world);
        if (itemStack.getItemDamage() >= 0 && itemStack.getItemDamage() < EnumCartTypes.values().length)
        {
            cart.setCartType(EnumCartTypes.values()[itemStack.getItemDamage()]);
        }
        return cart;
    }
}

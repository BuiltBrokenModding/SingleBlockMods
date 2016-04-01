package com.builtbroken.redcow.item;

import com.builtbroken.redcow.RedCow;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by Cow Pi on 7/31/2015.
 */
public class ItemRedbull extends Item
{
    @SideOnly(Side.CLIENT)
    IIcon empty_icon;

    public ItemRedbull()
    {
        this.setMaxStackSize(16);
        this.setHasSubtypes(true);
        this.setCreativeTab(CreativeTabs.tabFood);
        this.setUnlocalizedName(RedCow.PREFIX + "can");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b)
    {
        if (stack.getItemDamage() != 0)
        {
            list.add(StatCollector.translateToLocal(getUnlocalizedName(stack) + ".desc.name"));
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        if (stack.getItemDamage() == 0)
            return super.getUnlocalizedName(stack) + ".empty";
        return super.getUnlocalizedName(stack);
    }

    @Override
    public ItemStack onEaten(ItemStack item, World world, EntityPlayer player)
    {
        if (item.getItemDamage() != 0 && !player.capabilities.isCreativeMode)
        {
            if (!world.isRemote)
            {
                player.addPotionEffect(new PotionEffect(RedCow.potionRedBull.getId(), 20 * 120));
            }

            if (!player.capabilities.isCreativeMode)
            {
                --item.stackSize;
                if (item.stackSize <= 0)
                {
                    return new ItemStack(this);
                }

                player.inventory.addItemStackToInventory(new ItemStack(this));
            }
        }
        return item;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack)
    {
        if (stack.getItemDamage() == 0)
            return super.getMaxItemUseDuration(stack);

        return 32;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack)
    {
        if (stack.getItemDamage() == 0)
            return EnumAction.none;
        return EnumAction.drink;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        if (stack.getItemDamage() != 0)
            player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
        return stack;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list)
    {
        list.add(new ItemStack(item, 1, 0));
        list.add(new ItemStack(item, 1, 1));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        this.itemIcon = reg.registerIcon(RedCow.PREFIX + "can");
        this.empty_icon = reg.registerIcon(RedCow.PREFIX + "empty");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta)
    {
        if (meta == 0)
            return empty_icon;
        return itemIcon;
    }
}

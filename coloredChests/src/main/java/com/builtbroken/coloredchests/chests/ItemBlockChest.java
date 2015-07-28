package com.builtbroken.coloredchests.chests;

import com.builtbroken.coloredchests.ColoredChests;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.awt.*;
import java.util.List;

/**
 * Created by Dark on 7/27/2015.
 */
public class ItemBlockChest extends ItemBlock
{
    public ItemBlockChest(Block p_i45326_1_)
    {
        super(p_i45326_1_);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b)
    {
        if (stack != null && stack.getTagCompound() != null)
        {
            if (stack.getTagCompound().hasKey("rgb"))
            {
                Color color = ColoredChests.getColor(stack.getTagCompound().getInteger("rgb"));
                list.add("R: " + color.getRed() + " G: " + color.getGreen() + " B: " + color.getBlue());
            }

            if (stack.getTagCompound().hasKey("colorName"))
            {
                list.add("N: " + stack.getTagCompound().getString("colorName"));
            }
        }
    }


    @SideOnly(Side.CLIENT)
    @Override
    public int getColorFromItemStack(ItemStack stack, int pass)
    {
        if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("rgb"))
        {
            return stack.getTagCompound().getInteger("rgb");
        }
        return 16777215;
    }
}

package com.builtbroken.woodenbucket.bucket;

import com.builtbroken.woodenbucket.WoodenBucket;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

/**
 * Created by Dark on 8/24/2015.
 */
public class PamBucketRecipe extends ShapedRecipes
{
    public PamBucketRecipe(ItemStack bucket, ItemStack pamBucket)
    {
        super(3, 3, new ItemStack[]{null, null, null, null, bucket, null, null, null, null}, pamBucket);
    }

    @Override
    public boolean matches(InventoryCrafting grid, World world)
    {
        if (grid.getSizeInventory() == 9)
        {
            ItemStack stack = grid.getStackInSlot(4);
            if (stack != null && stack.getItem() instanceof ItemWoodenBucket)
            {
                ItemWoodenBucket item = (ItemWoodenBucket) grid.getStackInSlot(4).getItem();
                FluidStack fluidStack = item.getFluid(stack);
                return fluidStack != null && fluidStack.getFluid() == WoodenBucket.fluid_milk;
            }
        }
        return false;
    }
}

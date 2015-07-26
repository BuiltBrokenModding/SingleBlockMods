package com.builtbroken.woodenbucket;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.fluids.*;

/**
 * Created by Dark on 7/25/2015.
 */
public class ItemWoodenBucket extends Item implements IFluidContainerItem
{
    public ItemWoodenBucket()
    {
        this.maxStackSize = 1;
        this.setCreativeTab(CreativeTabs.tabMisc);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player)
    {
        boolean isBucketEmpty = this.isEmpty(itemstack);
        MovingObjectPosition movingobjectposition = this.getMovingObjectPositionFromPlayer(world, player, isBucketEmpty);

        if (movingobjectposition != null)
        {
            //Forge event code, most likely this will do nothing but oh well... TODO check if mods assume bucket passed in is always a vanilla bucket
            FillBucketEvent event = new FillBucketEvent(player, itemstack, world, movingobjectposition);
            if (MinecraftForge.EVENT_BUS.post(event))
            {
                //Even was cancel for what ever reason
                return itemstack;
            }
            else if (event.getResult() == Event.Result.ALLOW)
            {
                return consumeBucket(itemstack, player, event.result);
            }


            if (movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
            {
                int i = movingobjectposition.blockX;
                int j = movingobjectposition.blockY;
                int k = movingobjectposition.blockZ;

                if (!world.canMineBlock(player, i, j, k))
                {
                    return itemstack;
                }

                //Fill bucket code
                if (isBucketEmpty)
                {
                    if (player.canPlayerEdit(i, j, k, movingobjectposition.sideHit, itemstack))
                    {
                        pickupFluid(player, itemstack, world, i, j, k);
                    }
                }
                else //Empty bucket code
                {

                    //Offset position based on side hit
                    if (movingobjectposition.sideHit == 0)
                    {
                        --j;
                    }

                    if (movingobjectposition.sideHit == 1)
                    {
                        ++j;
                    }

                    if (movingobjectposition.sideHit == 2)
                    {
                        --k;
                    }

                    if (movingobjectposition.sideHit == 3)
                    {
                        ++k;
                    }

                    if (movingobjectposition.sideHit == 4)
                    {
                        --i;
                    }

                    if (movingobjectposition.sideHit == 5)
                    {
                        ++i;
                    }

                    if (player.canPlayerEdit(i, j, k, movingobjectposition.sideHit, itemstack))
                    {
                        placeFluid(player, itemstack, world, i, j, k);
                    }
                }
            }
        }
        return itemstack;

    }

    protected ItemStack consumeBucket(ItemStack itemstack, EntityPlayer player, ItemStack item)
    {
        //Creative mode we don't care about items
        if (player.capabilities.isCreativeMode)
        {
            return itemstack;
        }
        //If we only have one bucket consume and replace slot with new bucket
        else if (--itemstack.stackSize <= 0)
        {
            return item;
        }
        //If we have more than one bucket try to add the new one to the player's inventory
        else
        {
            if (!player.inventory.addItemStackToInventory(item))
            {
                player.dropPlayerItemWithRandomChoice(item, false);
            }

            return itemstack;
        }
    }

    public void pickupFluid(EntityPlayer player, ItemStack itemstack, World world, int i, int j, int k)
    {
        Block block = world.getBlock(i, j, k);
        Material material = block.getMaterial();
        int l = world.getBlockMetadata(i, j, k);

        if (material == Material.water && l == 0)
        {
            if (world.setBlockToAir(i, j, k))
            {
                ItemStack bucket = new ItemStack(this);
                fill(bucket, new FluidStack(FluidRegistry.WATER, FluidContainerRegistry.BUCKET_VOLUME), true);
                this.consumeBucket(itemstack, player, bucket);
            }
        }
        else if (material == Material.lava && l == 0)
        {
            if (world.setBlockToAir(i, j, k))
            {
                ItemStack bucket = new ItemStack(this);
                fill(bucket, new FluidStack(FluidRegistry.LAVA, FluidContainerRegistry.BUCKET_VOLUME), true);
                this.consumeBucket(itemstack, player, bucket);
            }
        }
        else if (block instanceof IFluidBlock)
        {
            FluidStack stack = ((IFluidBlock) block).drain(world, i, j, k, false);
            //TODO allow partial fills
            if (stack != null && stack.getFluid() != null && stack.amount == FluidContainerRegistry.BUCKET_VOLUME)
            {
                ItemStack bucket = new ItemStack(this);
                ((IFluidBlock) block).drain(world, i, j, k, true);
                fill(bucket, stack, true);
                this.consumeBucket(itemstack, player, bucket);
            }
        }
    }

    /**
     * Attempts to place the liquid contained inside the bucket.
     */
    public void placeFluid(EntityPlayer player, ItemStack itemstack, World world, int x, int y, int z)
    {
        Material material = world.getBlock(x, y, z).getMaterial();
        if (!isEmpty(itemstack) && world.isAirBlock(x, y, z) && material.isSolid())
        {
            FluidStack stack = getFluid(itemstack);
            if (stack != null && stack.getFluid() != null && stack.getFluid().canBePlacedInWorld() && stack.getFluid().getBlock() != null)
            {
                if (stack.amount == FluidContainerRegistry.BUCKET_VOLUME)
                {
                    if (world.provider.isHellWorld && stack.getFluid().getBlock() == Blocks.flowing_water)
                    {
                        //TODO unit test to ensure functionality
                        world.playSoundEffect((double) ((float) x + 0.5F), (double) ((float) y + 0.5F), (double) ((float) z + 0.5F), "random.fizz", 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);

                        for (int l = 0; l < 8; ++l)
                        {
                            world.spawnParticle("largesmoke", (double) x + Math.random(), (double) y + Math.random(), (double) z + Math.random(), 0.0D, 0.0D, 0.0D);
                        }
                        //TODO unit test to ensure functionality
                        consumeBucket(itemstack, player, new ItemStack(this));
                    }
                    else
                    {
                        //TODO unit test to ensure functionality
                        if (!world.isRemote && !material.isLiquid())
                        {
                            world.func_147480_a(x, y, z, true);
                        }

                        if (world.setBlock(x, y, z, stack.getFluid().getBlock(), 0, 3))
                            consumeBucket(itemstack, player, new ItemStack(this));
                    }
                }
                //TODO add support for blocks that can be filled without the bucket being full, IFluidBlock
            }
        }
    }

    /**
     * Helper method to check if the bucket is empty
     *
     * @param container - bucket
     * @return true if it is empty
     */
    public boolean isEmpty(ItemStack container)
    {
        return getFluid(container) == null;
    }

    /**
     * Helper method to check if the bucket is full
     *
     * @param container - bucket
     * @return true if it is full
     */
    public boolean isFull(ItemStack container)
    {
        FluidStack stack = getFluid(container);
        if (stack != null)
        {
            return stack.amount == getCapacity(container);
        }
        return false;
    }

    /* IFluidContainerItem */
    @Override
    public FluidStack getFluid(ItemStack container)
    {
        if (container.stackTagCompound == null || !container.stackTagCompound.hasKey("Fluid"))
        {
            return null;
        }
        return FluidStack.loadFluidStackFromNBT(container.stackTagCompound.getCompoundTag("Fluid"));
    }

    @Override
    public int getCapacity(ItemStack container)
    {
        return FluidContainerRegistry.BUCKET_VOLUME;
    }

    @Override
    public int fill(ItemStack container, FluidStack resource, boolean doFill)
    {
        if (resource != null)
        {
            if (!doFill)
            {
                if (container.stackTagCompound == null || !container.stackTagCompound.hasKey("Fluid"))
                {
                    return Math.min(getCapacity(container), resource.amount);
                }

                FluidStack stack = FluidStack.loadFluidStackFromNBT(container.stackTagCompound.getCompoundTag("Fluid"));

                if (stack == null)
                {
                    return Math.min(getCapacity(container), resource.amount);
                }

                if (!stack.isFluidEqual(resource))
                {
                    return 0;
                }

                return Math.min(getCapacity(container) - stack.amount, resource.amount);
            }

            if (container.stackTagCompound == null)
            {
                container.stackTagCompound = new NBTTagCompound();
            }

            if (!container.stackTagCompound.hasKey("Fluid"))
            {
                NBTTagCompound fluidTag = resource.writeToNBT(new NBTTagCompound());

                if (getCapacity(container) < resource.amount)
                {
                    fluidTag.setInteger("Amount", getCapacity(container));
                    container.stackTagCompound.setTag("Fluid", fluidTag);
                    return getCapacity(container);
                }

                container.stackTagCompound.setTag("Fluid", fluidTag);
                return resource.amount;
            }
            else
            {

                NBTTagCompound fluidTag = container.stackTagCompound.getCompoundTag("Fluid");
                FluidStack stack = FluidStack.loadFluidStackFromNBT(fluidTag);

                if (!stack.isFluidEqual(resource))
                {
                    return 0;
                }

                int filled = getCapacity(container) - stack.amount;
                if (resource.amount < filled)
                {
                    stack.amount += resource.amount;
                    filled = resource.amount;
                }
                else
                {
                    stack.amount = getCapacity(container);
                }

                container.stackTagCompound.setTag("Fluid", stack.writeToNBT(fluidTag));
                return filled;
            }
        }
        return 0;
    }

    @Override
    public FluidStack drain(ItemStack container, int maxDrain, boolean doDrain)
    {
        if (container.stackTagCompound == null || !container.stackTagCompound.hasKey("Fluid"))
        {
            return null;
        }

        FluidStack stack = FluidStack.loadFluidStackFromNBT(container.stackTagCompound.getCompoundTag("Fluid"));
        if (stack == null)
        {
            return null;
        }

        int currentAmount = stack.amount;
        stack.amount = Math.min(stack.amount, maxDrain);
        if (doDrain)
        {
            if (currentAmount == stack.amount)
            {
                container.stackTagCompound.removeTag("Fluid");

                if (container.stackTagCompound.hasNoTags())
                {
                    container.stackTagCompound = null;
                }
                return stack;
            }

            NBTTagCompound fluidTag = container.stackTagCompound.getCompoundTag("Fluid");
            fluidTag.setInteger("Amount", currentAmount - stack.amount);
            container.stackTagCompound.setTag("Fluid", fluidTag);
        }
        return stack;
    }

    @Override
    public int getItemStackLimit(ItemStack stack)
    {
        return isEmpty(stack) ? Items.bucket.getItemStackLimit() : 1;
    }
}

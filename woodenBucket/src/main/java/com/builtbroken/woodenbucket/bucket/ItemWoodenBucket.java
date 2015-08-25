package com.builtbroken.woodenbucket.bucket;

import com.builtbroken.woodenbucket.WoodenBucket;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fluids.*;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Dark on 7/25/2015.
 */
public class ItemWoodenBucket extends Item implements IFluidContainerItem
{
    @SideOnly(Side.CLIENT)
    public static IIcon fluidTexture;

    @SideOnly(Side.CLIENT)
    public static IIcon lavaTexture;

    @SideOnly(Side.CLIENT)
    public static IIcon blankTexture;

    @SideOnly(Side.CLIENT)
    public static IIcon acaciaTexture;

    @SideOnly(Side.CLIENT)
    public static IIcon birchTexture;

    @SideOnly(Side.CLIENT)
    public static IIcon jungleTexture;

    @SideOnly(Side.CLIENT)
    public static IIcon spruceTexture;

    @SideOnly(Side.CLIENT)
    public static IIcon charredTexture;

    @SideOnly(Side.CLIENT)
    public static IIcon bigOakTexture;

    public static HashMap<String, IIcon> fluidToIconMap = new HashMap();

    //TODO rename to fluid.molten
    public static String[] supportedFluidTextures = new String[]{"milk", "blood", "slime.blue", "fuel", "aluminum.molten", "glue", "alubrass.molten", "alumite.molten", "angmallen.molten", "ardite.molten", "bronze.molten", "cobalt.molten", "copper.molten", "electrum.molten", "emerald.molten", "ender.molten", "enderium.molten", "glass.molten", "gold.molten", "invar.molten", "iron.molten", "lead.molten", "lumium.molten", "manyullyn.molten", "mithril.molten", "nickel.molten", "obsidian.molten", "pigiron.molten", "shiny.molten", "signalum.molten", "silver.molten", "steel.molten", "tin.molten", "oil", "redplasma"};

    public ItemWoodenBucket()
    {
        this.maxStackSize = 1;
        this.setUnlocalizedName(WoodenBucket.PREFIX + "WoodenBucket");
        this.setCreativeTab(CreativeTabs.tabMisc);
        this.setHasSubtypes(true);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b)
    {
        if (!isEmpty(stack))
        {
            list.add(StatCollector.translateToLocal(getUnlocalizedName() + ".fluid.name") + ": " + getFluid(stack).getLocalizedName());
            list.add(StatCollector.translateToLocal(getUnlocalizedName() + ".fluid.amount.name") + ": " + getFluid(stack).amount + "mb");
        }
    }

    @SubscribeEvent
    public void onRightClickEvent(PlayerInteractEvent event)
    {
        if (!event.world.isRemote && event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK && event.entityPlayer.getCurrentEquippedItem() != null && event.entityPlayer.getCurrentEquippedItem().getItem() == this)
        {
            TileEntity tile = event.world.getTileEntity(event.x, event.y, event.z);

            if (tile instanceof IFluidHandler)
            {
                boolean isBucketEmpty = this.isEmpty(event.entityPlayer.getCurrentEquippedItem());
                ForgeDirection side = ForgeDirection.getOrientation(event.face);
                if (isBucketEmpty)
                {
                    FluidStack drainedFromTank = ((IFluidHandler) tile).drain(side, getCapacity(event.entityPlayer.getCurrentEquippedItem()), false);
                    if (drainedFromTank != null && drainedFromTank.getFluid() != null && ((IFluidHandler) tile).canDrain(side, drainedFromTank.getFluid()))
                    {
                        if (event.entityPlayer.capabilities.isCreativeMode)
                        {
                            ((IFluidHandler) tile).drain(side, FluidContainerRegistry.BUCKET_VOLUME, true);
                        }
                        else
                        {
                            ItemStack bucket = new ItemStack(this, 1, event.entityPlayer.getCurrentEquippedItem().getItemDamage());
                            int filledIntoBucket = fill(bucket, drainedFromTank, true);
                            if (filledIntoBucket > 0)
                            {
                                ((IFluidHandler) tile).drain(side, filledIntoBucket, true);
                                event.entityPlayer.inventory.setInventorySlotContents(event.entityPlayer.inventory.currentItem, consumeBucket(event.entityPlayer.getCurrentEquippedItem(), event.entityPlayer, bucket));
                                event.entityPlayer.inventoryContainer.detectAndSendChanges();
                            }
                        }
                    }
                }
                else
                {
                    FluidStack containedFluid = getFluid(event.entityPlayer.getCurrentEquippedItem());
                    if (((IFluidHandler) tile).canFill(side, containedFluid.getFluid()))
                    {
                        int filled = ((IFluidHandler) tile).fill(side, containedFluid, true);
                        if (!event.entityPlayer.capabilities.isCreativeMode)
                        {
                            drain(event.entityPlayer.getCurrentEquippedItem(), filled, true);
                            containedFluid = getFluid(event.entityPlayer.getCurrentEquippedItem());
                            if (containedFluid == null || containedFluid.amount == 0)
                            {
                                event.entityPlayer.inventory.setInventorySlotContents(event.entityPlayer.inventory.currentItem, consumeBucket(event.entityPlayer.getCurrentEquippedItem(), event.entityPlayer, new ItemStack(this, 1, event.entityPlayer.getCurrentEquippedItem().getItemDamage())));
                                event.entityPlayer.inventoryContainer.detectAndSendChanges();
                            }
                        }
                    }
                }
                if (event.isCancelable())
                {
                    event.setCanceled(true);
                }
            }
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player)
    {
        boolean isBucketEmpty = this.isEmpty(itemstack);
        MovingObjectPosition movingobjectposition = this.getMovingObjectPositionFromPlayer(world, player, isBucketEmpty);

        if (movingobjectposition != null)
        {
            if (movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
            {
                int i = movingobjectposition.blockX;
                int j = movingobjectposition.blockY;
                int k = movingobjectposition.blockZ;

                TileEntity tile = world.getTileEntity(i, j, k);

                if (tile instanceof IFluidHandler)
                {
                    return itemstack;
                }


                if (!world.canMineBlock(player, i, j, k))
                {
                    return itemstack;
                }

                //Fill bucket code
                if (isBucketEmpty)
                {
                    if (player.canPlayerEdit(i, j, k, movingobjectposition.sideHit, itemstack))
                    {
                        return pickupFluid(player, itemstack, world, i, j, k);
                    }
                }
                else //Empty bucket code
                {
                    Block block = world.getBlock(i, j, k);
                    Material material = block.getMaterial();

                    if (!material.isSolid() && block.isReplaceable(world, i, j, k) && player.canPlayerEdit(i, j, k, movingobjectposition.sideHit, itemstack))
                    {
                        return placeFluid(player, itemstack, world, i, j, k);
                    }

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
                        return placeFluid(player, itemstack, world, i, j, k);
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

    public ItemStack pickupFluid(EntityPlayer player, ItemStack itemstack, World world, int i, int j, int k)
    {
        Block block = world.getBlock(i, j, k);
        int l = world.getBlockMetadata(i, j, k);

        if (block == Blocks.water && l == 0)
        {
            if (world.setBlockToAir(i, j, k))
            {
                ItemStack bucket = new ItemStack(this, 1, itemstack.getItemDamage());
                fill(bucket, new FluidStack(FluidRegistry.WATER, FluidContainerRegistry.BUCKET_VOLUME), true);
                return this.consumeBucket(itemstack, player, bucket);
            }
        }
        else if (block == Blocks.lava && l == 0)
        {
            if (world.setBlockToAir(i, j, k))
            {
                ItemStack bucket = new ItemStack(this, 1, itemstack.getItemDamage());
                fill(bucket, new FluidStack(FluidRegistry.LAVA, FluidContainerRegistry.BUCKET_VOLUME), true);
                return this.consumeBucket(itemstack, player, bucket);
            }
        }
        else if (block instanceof IFluidBlock && ((IFluidBlock) block).canDrain(world, i, j, k))
        {
            int meta = world.getBlockMetadata(i, j, k);
            FluidStack drainedFluid = ((IFluidBlock) block).drain(world, i, j, k, false);

            //TODO allow partial fills
            if (isValidFluidStack(drainedFluid))
            {
                ItemStack bucket = new ItemStack(this, 1, itemstack.getItemDamage());
                drainedFluid = ((IFluidBlock) block).drain(world, i, j, k, true);

                if (isValidFluidStack(drainedFluid))
                {
                    fill(bucket, drainedFluid, true);
                    return this.consumeBucket(itemstack, player, bucket);
                }
                else if (world.getBlock(i, j, k) != block)
                {
                    world.setBlock(i, j, k, block, meta, 3);
                }
            }
        }
        return itemstack;
    }

    private boolean isValidFluidStack(FluidStack drainedFluid)
    {
        return drainedFluid != null && drainedFluid.getFluid() != null && drainedFluid.amount == FluidContainerRegistry.BUCKET_VOLUME;
    }

    /**
     * Attempts to place the liquid contained inside the bucket.
     */
    public ItemStack placeFluid(EntityPlayer player, ItemStack itemstack, World world, int x, int y, int z)
    {
        Block block = world.getBlock(x, y, z);
        //Material material = block.getMaterial();
        if (isFull(itemstack))
        {
            if (world.isAirBlock(x, y, z) || block.isReplaceable(world, x, y, z))
            {
                FluidStack stack = getFluid(itemstack);
                if (stack != null && stack.getFluid() != null && stack.getFluid().canBePlacedInWorld() && stack.getFluid().getBlock() != null)
                {
                    //TODO add support for oil and other fuel types to explode in the nether
                    if (world.provider.isHellWorld && stack.getFluid().getUnlocalizedName().contains("water"))
                    {
                        world.playSoundEffect((double) ((float) x + 0.5F), (double) ((float) y + 0.5F), (double) ((float) z + 0.5F), "random.fizz", 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);

                        for (int l = 0; l < 8; ++l)
                        {
                            world.spawnParticle("largesmoke", (double) x + Math.random(), (double) y + Math.random(), (double) z + Math.random(), 0.0D, 0.0D, 0.0D);
                        }
                        return consumeBucket(itemstack, player, new ItemStack(this, 1, itemstack.getItemDamage()));
                    }
                    else
                    {
                        if (!world.isRemote)
                        {
                            world.func_147480_a(x, y, z, true);
                        }
                        if (stack.getFluid() == FluidRegistry.WATER)
                        {
                            world.setBlock(x, y, z, Blocks.flowing_water);
                        }
                        else if (stack.getFluid() == FluidRegistry.LAVA)
                        {
                            world.setBlock(x, y, z, Blocks.flowing_lava);
                        }
                        else
                        {
                            world.setBlock(x, y, z, stack.getFluid().getBlock());
                        }
                        return consumeBucket(itemstack, player, new ItemStack(this, 1, itemstack.getItemDamage()));
                    }
                }
            }
        }
        else if (!world.isRemote)
        {
            player.addChatComponentMessage(new ChatComponentText(getUnlocalizedName() + ".volume.notEnoughForFullBlock"));
        }
        return itemstack;
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

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister reg)
    {
        itemIcon = reg.registerIcon(WoodenBucket.PREFIX + "bucket.oak");
        acaciaTexture = reg.registerIcon(WoodenBucket.PREFIX + "bucket.acacia");
        birchTexture = reg.registerIcon(WoodenBucket.PREFIX + "bucket.birch");
        jungleTexture = reg.registerIcon(WoodenBucket.PREFIX + "bucket.jungle");
        spruceTexture = reg.registerIcon(WoodenBucket.PREFIX + "bucket.spruce");
        charredTexture = reg.registerIcon(WoodenBucket.PREFIX + "bucket.charred");
        bigOakTexture = reg.registerIcon(WoodenBucket.PREFIX + "bucket.big_oak");
        fluidTexture = reg.registerIcon(WoodenBucket.PREFIX + "bucket.fluid");
        lavaTexture = reg.registerIcon(WoodenBucket.PREFIX + "bucket.lava");
        blankTexture = reg.registerIcon(WoodenBucket.PREFIX + "blank");

        for (String string : supportedFluidTextures)
        {
            fluidToIconMap.put(string, reg.registerIcon(WoodenBucket.PREFIX + "bucket." + string));
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(ItemStack stack, int pass)
    {
        if (pass == 1)
        {
            if (isEmpty(stack))
                return blankTexture;
            else if (fluidToIconMap.containsKey(getFluid(stack).getFluid().getName()))
                return fluidToIconMap.get(getFluid(stack).getFluid().getName());
            else if (getFluid(stack).getFluid() == FluidRegistry.LAVA || getFluid(stack).getFluid().getTemperature() > 600)
                return lavaTexture;
            else
                return fluidTexture;
        }
        return super.getIcon(stack, pass);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIconFromDamage(int meta)
    {
        if (meta > 0 && meta < BucketTypes.values().length)
        {
            switch (BucketTypes.values()[meta])
            {
                case ACACIA:
                    return acaciaTexture;
                case BIRCH:
                    return birchTexture;
                case JUNGLE:
                    return jungleTexture;
                case SPRUCE:
                    return spruceTexture;
                case BIG_OAK:
                    return bigOakTexture;
                case CHARRED:
                    return charredTexture;
            }
        }
        return this.itemIcon;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        int meta = stack.getItemDamage();
        if (meta > 0 && meta < BucketTypes.values().length)
        {
            switch (BucketTypes.values()[meta])
            {
                case OAK:
                    return super.getUnlocalizedName() + ".oak";
                case ACACIA:
                    return super.getUnlocalizedName() + ".acacia";
                case BIRCH:
                    return super.getUnlocalizedName() + ".birch";
                case JUNGLE:
                    return super.getUnlocalizedName() + ".jungle";
                case SPRUCE:
                    return super.getUnlocalizedName() + ".spruce";
                case BIG_OAK:
                    return super.getUnlocalizedName() + ".big_oak";
                case CHARRED:
                    return super.getUnlocalizedName() + ".charred";
            }
        }
        return super.getUnlocalizedName();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean requiresMultipleRenderPasses()
    {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getColorFromItemStack(ItemStack stack, int pass)
    {
        if (!isEmpty(stack) && pass == 1)
            return getFluid(stack).getFluid().getColor();
        return 16777215;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean held)
    {
        FluidStack fluid = getFluid(stack);
        if (world.getWorldTime() % 5 == 0)
        {
            if (WoodenBucket.PREVENT_HOT_FLUID_USAGE && fluid != null && fluid.getFluid() != null && fluid.getFluid().getTemperature(fluid) > 400)
            {
                //Default 26% chance to be caught on fire
                if (WoodenBucket.BURN_ENTITY_WITH_HOT_FLUID && entity instanceof EntityLivingBase && world.rand.nextFloat() < ((float) fluid.getFluid().getTemperature(fluid) / 1500f))
                {
                    EntityLivingBase living = (EntityLivingBase) entity;
                    if (!living.isImmuneToFire())
                    {
                        living.setFire(1 + world.rand.nextInt(15));
                    }
                    //TODO implement direct damage based on armor, or leave that to ItHurtsToDie?
                }
                if (WoodenBucket.DAMAGE_BUCKET_WITH_HOT_FLUID && world.rand.nextFloat() < ((float) fluid.getFluid().getTemperature(fluid) / 1500f))
                {
                    //TODO play sound effect of items burning
                    stack.setItemDamage(BucketTypes.CHARRED.ordinal());
                }
            }
        }
    }

    @Override
    public boolean onEntityItemUpdate(EntityItem entityItem)
    {
        FluidStack fluid = getFluid(entityItem.getEntityItem());
        if (entityItem.worldObj.getWorldTime() % 5 == 0)
        {
            if (WoodenBucket.PREVENT_HOT_FLUID_USAGE && fluid != null && fluid.getFluid() != null && fluid.getFluid().getTemperature(fluid) > 400)
            {
                if (WoodenBucket.DAMAGE_BUCKET_WITH_HOT_FLUID && entityItem.worldObj.rand.nextFloat() < ((float) fluid.getFluid().getTemperature(fluid) / 1500f))
                {
                    //TODO play sound effect of items burning
                    //TODO add slightly burnt, crisp, and ash version of the bucket to simulate each time it gets damaged
                    entityItem.getEntityItem().setItemDamage(BucketTypes.CHARRED.ordinal());
                }
                //TODO chance to catch area on fire around it
            }
        }
        return false;
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase entity)
    {
        if (entity instanceof EntityCow && isEmpty(stack))
        {
            if (!player.worldObj.isRemote)
                return true;
            Fluid fluid = FluidRegistry.getFluid("milk");
            if (fluid != null)
            {
                ItemStack newBucket = new ItemStack(this, 1, stack.getItemDamage());
                fill(newBucket, new FluidStack(fluid, FluidContainerRegistry.BUCKET_VOLUME), true);
                player.inventory.setInventorySlotContents(player.inventory.currentItem, consumeBucket(stack, player, newBucket));
                player.inventoryContainer.detectAndSendChanges();
            }
            else
            {
                ((EntityCow) entity).playLivingSound();
                player.addChatComponentMessage(new ChatComponentText(StatCollector.translateToLocal(getUnlocalizedName() + ".error.fluid.milk.notRegistered")));
            }
            return true;
        }
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list)
    {
        for (BucketTypes type : BucketTypes.values())
        {
            list.add(new ItemStack(item, 1, type.ordinal()));
        }

        ItemStack waterBucket = new ItemStack(item);
        fill(waterBucket, new FluidStack(FluidRegistry.WATER, FluidContainerRegistry.BUCKET_VOLUME), true);
        list.add(waterBucket);

        for (String string : supportedFluidTextures)
        {
            if (FluidRegistry.getFluid(string) != null)
            {
                ItemStack milkBucket = new ItemStack(item);
                fill(milkBucket, new FluidStack(FluidRegistry.getFluid(string), FluidContainerRegistry.BUCKET_VOLUME), true);
                list.add(milkBucket);
            }
        }
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemstack)
    {
        if (isEmpty(itemstack))
            return null;
        return new ItemStack(this, itemstack.getItemDamage());
    }

    @Override
    public boolean doesContainerItemLeaveCraftingGrid(ItemStack stack)
    {
        return isEmpty(stack);
    }

    @Override
    public boolean hasContainerItem(ItemStack stack)
    {
        return getFluid(stack) != null;
    }

    public enum BucketTypes
    {
        OAK,
        ACACIA,
        BIRCH,
        JUNGLE,
        SPRUCE,
        BIG_OAK,
        CHARRED
    }
}

package com.builtbroken.woodenrails.cart;

import com.builtbroken.woodenrails.WoodenRails;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;

/**
 * Created by Dark on 7/25/2015.
 */
public class EntityWoodenCart extends EntityMinecart implements IInventory
{
    //Inventory cart data
    private boolean dropContentsWhenDead = true;
    private ItemStack[] inventoryArray;

    //Furnace cart data
    private int fuel;
    public double pushX;
    public double pushZ;

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
    public void onUpdate()
    {
        super.onUpdate();

        if (getCartType() == EnumCartTypes.FURNACE)
        {
            if (this.fuel > 0)
            {
                --this.fuel;
            }

            if (this.fuel <= 0)
            {
                this.pushX = this.pushZ = 0.0D;
            }

            this.setMinecartPowered(this.fuel > 0);

            if (this.isMinecartPowered() && this.rand.nextInt(4) == 0)
            {
                this.worldObj.spawnParticle("largesmoke", this.posX, this.posY + 0.8D, this.posZ, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    public boolean interactFirst(EntityPlayer player)
    {
        if (MinecraftForge.EVENT_BUS.post(new MinecartInteractEvent(this, player)))
            return true;
        if (getCartType() == EnumCartTypes.EMPTY)
        {
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
        else if (getCartType() == EnumCartTypes.CHEST || getCartType() == EnumCartTypes.COLORED_CHEST)
        {
            if (!this.worldObj.isRemote)
            {
                player.displayGUIChest(this);
            }
            return true;
        }
        else if (getCartType() == EnumCartTypes.FURNACE)
        {
            ItemStack itemstack = player.inventory.getCurrentItem();

            if (itemstack != null && itemstack.getItem() == Items.coal)
            {
                if (!player.capabilities.isCreativeMode && --itemstack.stackSize == 0)
                {
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                }

                this.fuel += 3600;
            }

            this.pushX = this.posX - player.posX;
            this.pushZ = this.posZ - player.posZ;
            return true;
        }
        return false;
    }

    @Override
    protected void func_145821_a(int p_145821_1_, int p_145821_2_, int p_145821_3_, double p_145821_4_, double p_145821_6_, Block p_145821_8_, int p_145821_9_)
    {
        super.func_145821_a(p_145821_1_, p_145821_2_, p_145821_3_, p_145821_4_, p_145821_6_, p_145821_8_, p_145821_9_);
        double d2 = this.pushX * this.pushX + this.pushZ * this.pushZ;

        if (d2 > 1.0E-4D && this.motionX * this.motionX + this.motionZ * this.motionZ > 0.001D)
        {
            d2 = (double) MathHelper.sqrt_double(d2);
            this.pushX /= d2;
            this.pushZ /= d2;

            if (this.pushX * this.motionX + this.pushZ * this.motionZ < 0.0D)
            {
                this.pushX = 0.0D;
                this.pushZ = 0.0D;
            }
            else
            {
                this.pushX = this.motionX;
                this.pushZ = this.motionZ;
            }
        }
    }

    @Override
    public ItemStack getCartItem()
    {
        ItemStack stack = new ItemStack(WoodenRails.itemWoodCart, 1, getCartType().ordinal());
        if (getBlockRenderColor() != -1)
        {
            stack.setTagCompound(new NBTTagCompound());
            stack.getTagCompound().setInteger("rgb", getBlockRenderColor());
        }
        return stack;
    }

    @Override
    public boolean canBeRidden()
    {
        return getCartType() == EnumCartTypes.EMPTY;
    }

    @Override
    public void killMinecart(DamageSource p_94095_1_)
    {
        this.setDead();
        ItemStack cartStack = new ItemStack(WoodenRails.itemWoodCart);
        /** TODO re-add after creating an access transformer
         if (this.entityName != null)
         {
         itemstack.setStackDisplayName(this.entityName);
         }
         */
        this.entityDropItem(cartStack, 0.0F);
    }


    protected void applyDrag()
    {
        if (getCartType() == EnumCartTypes.EMPTY)
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
        else if (getCartType() == EnumCartTypes.CHEST || getCartType() == EnumCartTypes.COLORED_CHEST)
        {
            int i = 15 - Container.calcRedstoneFromInventory(this);
            float f = 0.98F + (float) i * 0.001F;
            this.motionX *= (double) f;
            this.motionY *= 0.0D;
            this.motionZ *= (double) f;
        }
        else if (getCartType() == EnumCartTypes.FURNACE)
        {
            double d0 = this.pushX * this.pushX + this.pushZ * this.pushZ;

            if (d0 > 1.0E-4D)
            {
                d0 = (double) MathHelper.sqrt_double(d0);
                this.pushX /= d0;
                this.pushZ /= d0;
                double d1 = 0.05D;
                this.motionX *= 0.800000011920929D;
                this.motionY *= 0.0D;
                this.motionZ *= 0.800000011920929D;
                this.motionX += this.pushX * d1;
                this.motionZ += this.pushZ * d1;
            }
            else
            {
                this.motionX *= 0.9800000190734863D;
                this.motionY *= 0.0D;
                this.motionZ *= 0.9800000190734863D;
            }
        }
    }

    @Override
    public int getMinecartType()
    {
        switch (getCartType())
        {
            case CHEST:
            case COLORED_CHEST:
                return 1;
        }
        return 0;
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        this.dataWatcher.addObject(16, new Byte((byte) 0));
        this.dataWatcher.addObject(23, Byte.valueOf((byte) 0));
        this.dataWatcher.addObject(24, Integer.valueOf(-1));
    }

    public void setBlockRenderColor(int color)
    {
        this.dataWatcher.updateObject(24, Integer.valueOf(color));
    }

    public int getBlockRenderColor()
    {
        return this.dataWatcher.getWatchableObjectInt(24);
    }

    public EnumCartTypes getCartType()
    {
        byte type = this.dataWatcher.getWatchableObjectByte(23);
        if (type >= 0 && type < EnumCartTypes.values().length)
            return EnumCartTypes.values()[type];
        return EnumCartTypes.EMPTY;
    }

    public void setCartType(EnumCartTypes type)
    {
        this.dataWatcher.updateObject(23, Byte.valueOf((byte) type.ordinal()));
    }

    protected boolean isMinecartPowered()
    {
        return (this.dataWatcher.getWatchableObjectByte(16) & 1) != 0;
    }

    protected void setMinecartPowered(boolean p_94107_1_)
    {
        if (p_94107_1_)
        {
            this.dataWatcher.updateObject(16, Byte.valueOf((byte) (this.dataWatcher.getWatchableObjectByte(16) | 1)));
        }
        else
        {
            this.dataWatcher.updateObject(16, Byte.valueOf((byte) (this.dataWatcher.getWatchableObjectByte(16) & -2)));
        }
    }

    private ItemStack getContentsOfSlot(int slot)
    {
        if (getCartType() == EnumCartTypes.CHEST || getCartType() == EnumCartTypes.COLORED_CHEST)
        {
            if (inventoryArray == null)
                inventoryArray = new ItemStack[36];
            return inventoryArray[slot];
        }
        return null;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt)
    {
        super.readEntityFromNBT(nbt);

        //Read block render color
        if (nbt.hasKey("blockRenderColor"))
            setBlockRenderColor(nbt.getInteger("blockRenderColor"));

        //Read cart type
        byte b = nbt.getByte("cartType");
        if (b >= 0 && b < EnumCartTypes.values().length)
            setCartType(EnumCartTypes.values()[b]);

        //Load data for each cart type
        if (getCartType() == EnumCartTypes.CHEST || getCartType() == EnumCartTypes.COLORED_CHEST)
        {
            NBTTagList nbttaglist = nbt.getTagList("Items", 10);
            this.inventoryArray = new ItemStack[this.getSizeInventory()];

            for (int i = 0; i < nbttaglist.tagCount(); ++i)
            {
                NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
                int j = nbttagcompound1.getByte("Slot") & 255;

                if (j >= 0 && j < this.getSizeInventory())
                {
                    this.setInventorySlotContents(j, ItemStack.loadItemStackFromNBT(nbttagcompound1));
                }
            }
        }
        else if (getCartType() == EnumCartTypes.FURNACE)
        {
            this.pushX = nbt.getDouble("PushX");
            this.pushZ = nbt.getDouble("PushZ");
            this.fuel = nbt.getShort("Fuel");
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt)
    {
        super.writeEntityToNBT(nbt);
        nbt.setByte("cartType", (byte) getCartType().ordinal());

        if (getBlockRenderColor() != -1)
        {
            nbt.setInteger("blockRenderColor", getBlockRenderColor());
        }
        if (getCartType() == EnumCartTypes.CHEST || getCartType() == EnumCartTypes.COLORED_CHEST)
        {
            NBTTagList nbttaglist = new NBTTagList();

            for (int i = 0; i < this.getSizeInventory(); ++i)
            {
                if (this.getStackInSlot(i) != null)
                {
                    NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                    nbttagcompound1.setByte("Slot", (byte) i);
                    this.getStackInSlot(i).writeToNBT(nbttagcompound1);
                    nbttaglist.appendTag(nbttagcompound1);
                }
            }

            nbt.setTag("Items", nbttaglist);
        }
        else if (getCartType() == EnumCartTypes.FURNACE)
        {
            nbt.setDouble("PushX", this.pushX);
            nbt.setDouble("PushZ", this.pushZ);
            nbt.setShort("Fuel", (short) this.fuel);
        }
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        return this.getContentsOfSlot(slot);
    }

    @Override
    public ItemStack decrStackSize(int slot, int p_70298_2_)
    {
        if (this.getStackInSlot(slot) != null)
        {
            ItemStack itemstack;

            if (this.getStackInSlot(slot).stackSize <= p_70298_2_)
            {
                itemstack = this.getStackInSlot(slot);
                this.setInventorySlotContents(slot, null);
                return itemstack;
            }
            else
            {
                itemstack = this.getStackInSlot(slot).splitStack(p_70298_2_);

                if (this.getStackInSlot(slot).stackSize == 0)
                {
                    this.setInventorySlotContents(slot, null);
                }

                return itemstack;
            }
        }
        else
        {
            return null;
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot)
    {
        if (this.getStackInSlot(slot) != null)
        {
            ItemStack itemstack = this.getStackInSlot(slot);
            this.setInventorySlotContents(slot, null);
            return itemstack;
        }
        else
        {
            return null;
        }
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack)
    {
        this.inventoryArray[slot] = stack;

        if (stack != null && stack.stackSize > this.getInventoryStackLimit())
        {
            stack.stackSize = this.getInventoryStackLimit();
        }
    }

    @Override
    public void markDirty() {}

    @Override
    public boolean isUseableByPlayer(EntityPlayer p_70300_1_)
    {
        return this.isDead ? false : p_70300_1_.getDistanceSqToEntity(this) <= 64.0D;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_)
    {
        return true;
    }

    @Override
    public String getInventoryName()
    {
        return this.hasCustomInventoryName() ? this.func_95999_t() : "container.minecart";
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    @Override
    public void travelToDimension(int p_71027_1_)
    {
        this.dropContentsWhenDead = false;
        super.travelToDimension(p_71027_1_);
    }

    @Override
    public void setDead()
    {
        if (this.dropContentsWhenDead)
        {
            for (int i = 0; i < this.getSizeInventory(); ++i)
            {
                ItemStack itemstack = this.getStackInSlot(i);

                if (itemstack != null)
                {
                    float f = this.rand.nextFloat() * 0.8F + 0.1F;
                    float f1 = this.rand.nextFloat() * 0.8F + 0.1F;
                    float f2 = this.rand.nextFloat() * 0.8F + 0.1F;

                    while (itemstack.stackSize > 0)
                    {
                        int j = this.rand.nextInt(21) + 10;

                        if (j > itemstack.stackSize)
                        {
                            j = itemstack.stackSize;
                        }

                        itemstack.stackSize -= j;
                        EntityItem entityitem = new EntityItem(this.worldObj, this.posX + (double) f, this.posY + (double) f1, this.posZ + (double) f2, new ItemStack(itemstack.getItem(), j, itemstack.getItemDamage()));

                        if (itemstack.hasTagCompound())
                        {
                            entityitem.getEntityItem().setTagCompound((NBTTagCompound) itemstack.getTagCompound().copy());
                        }

                        float f3 = 0.05F;
                        entityitem.motionX = (double) ((float) this.rand.nextGaussian() * f3);
                        entityitem.motionY = (double) ((float) this.rand.nextGaussian() * f3 + 0.2F);
                        entityitem.motionZ = (double) ((float) this.rand.nextGaussian() * f3);
                        this.worldObj.spawnEntityInWorld(entityitem);
                    }
                }
            }
        }

        super.setDead();
    }

    @Override
    public int getSizeInventory()
    {
        if (getCartType() == EnumCartTypes.CHEST || getCartType() == EnumCartTypes.COLORED_CHEST)
            return 27;
        return 0;
    }

    @Override
    public Block func_145817_o()
    {
        if (getCartType() == EnumCartTypes.CHEST || getCartType() == EnumCartTypes.COLORED_CHEST)
            return Blocks.chest;
        else if (getCartType() == EnumCartTypes.FURNACE)
            return Blocks.lit_furnace;
        return Blocks.air;
    }

    @Override
    public int getDefaultDisplayTileOffset()
    {
        if (getCartType() == EnumCartTypes.CHEST || getCartType() == EnumCartTypes.COLORED_CHEST)
            return 8;
        return 6;
    }

    @Override
    public int getDefaultDisplayTileData()
    {
        if (getCartType() == EnumCartTypes.FURNACE)
            return 2;
        return 0;
    }
}

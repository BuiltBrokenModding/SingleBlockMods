package com.builtbroken.woodenrails.cart;

import com.builtbroken.woodenrails.WoodenRails;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

/**
 * Created by Dark on 7/25/2015.
 */
public class EntityWoodenCart extends EntityMinecart implements IInventory
{
    private boolean dropContentsWhenDead = true;
    private ItemStack[] inventoryArray;

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
    public boolean interactFirst(EntityPlayer player)
    {
        if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.minecart.MinecartInteractEvent(this, player)))
            return true;
        if (getCartType() == EnumCartTypes.EMPTY)
        {
            if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer && this.riddenByEntity != player)
            {
                return true;
            } else if (this.riddenByEntity != null && this.riddenByEntity != player)
            {
                return false;
            } else
            {
                if (!this.worldObj.isRemote)
                {
                    player.mountEntity(this);
                }

                return true;
            }
        } else if (getCartType() == EnumCartTypes.CHEST || getCartType() == EnumCartTypes.COLORED_CHEST)
        {
            if (!this.worldObj.isRemote)
            {
                player.displayGUIChest(this);
            }
        }
        return false;
    }

    @Override
    public ItemStack getCartItem()
    {
        return new ItemStack(WoodenRails.itemWoodCart, 1, getCartType().ordinal());
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
            } else
            {
                this.motionX *= 0.8;
                this.motionY *= 0.0D;
                this.motionZ *= 0.8;
            }
        } else if (getCartType() == EnumCartTypes.CHEST || getCartType() == EnumCartTypes.COLORED_CHEST)
        {
            int i = 15 - Container.calcRedstoneFromInventory(this);
            float f = 0.98F + (float) i * 0.001F;
            this.motionX *= (double) f;
            this.motionY *= 0.0D;
            this.motionZ *= (double) f;
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
        this.dataWatcher.addObject(22, Byte.valueOf((byte) 0));
    }

    public EnumCartTypes getCartType()
    {
        byte type = this.dataWatcher.getWatchableObjectByte(22);
        if (type >= 0 && type < EnumCartTypes.values().length)
            return EnumCartTypes.values()[type];
        return EnumCartTypes.EMPTY;
    }

    public void setCartType(EnumCartTypes type)
    {
        this.dataWatcher.updateObject(22, (byte) type.ordinal());
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
        byte b = nbt.getByte("cartType");
        if (b >= 0 && b < EnumCartTypes.values().length)
            setCartType(EnumCartTypes.values()[b]);
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
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt)
    {
        super.writeEntityToNBT(nbt);
        nbt.setByte("cartType", (byte) getCartType().ordinal());

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
            } else
            {
                itemstack = this.getStackInSlot(slot).splitStack(p_70298_2_);

                if (this.getStackInSlot(slot).stackSize == 0)
                {
                    this.setInventorySlotContents(slot, null);
                }

                return itemstack;
            }
        } else
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
        } else
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
        return null;
    }

    @Override
    public int getDefaultDisplayTileOffset()
    {
        return 8;
    }
}

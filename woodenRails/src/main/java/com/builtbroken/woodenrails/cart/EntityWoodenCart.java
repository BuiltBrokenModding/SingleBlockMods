package com.builtbroken.woodenrails.cart;

import com.builtbroken.woodenrails.WoodenRails;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.command.IEntitySelector;
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
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;

import java.util.List;

/**
 * Created by Dark on 7/25/2015.
 */
public class EntityWoodenCart extends EntityMinecart implements IInventory, IHopper
{
    //Inventory cart data
    private boolean dropContentsWhenDead = true;
    private ItemStack[] inventoryArray;

    //Furnace cart data
    private int fuel;
    public double pushX;
    public double pushZ;

    //TNT data
    private int minecartTNTFuse = -1;

    //Hopper cart data
    private boolean isBlocked = true;
    private int transferTicker = -1;

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
        else if (getCartType() == EnumCartTypes.BC_TANK)
        {
            //TODO if fluid is hot burn up cart slowly
        }
        else if (getCartType() == EnumCartTypes.TNT)
        {
            if (this.minecartTNTFuse > 0)
            {
                --this.minecartTNTFuse;
                this.worldObj.spawnParticle("smoke", this.posX, this.posY + 0.5D, this.posZ, 0.0D, 0.0D, 0.0D);
            }
            else if (this.minecartTNTFuse == 0)
            {
                this.explodeCart(this.motionX * this.motionX + this.motionZ * this.motionZ);
            }

            if (this.isCollidedHorizontally)
            {
                double d0 = this.motionX * this.motionX + this.motionZ * this.motionZ;

                if (d0 >= 0.009999999776482582D)
                {
                    this.explodeCart(d0);
                }
            }
        }
        else if (getCartType() == EnumCartTypes.HOPPER)
        {
            if (!this.worldObj.isRemote && this.isEntityAlive() && this.getBlocked())
            {
                --this.transferTicker;

                if (!this.canTransfer())
                {
                    this.setTransferTicker(0);

                    if (this.transferItems())
                    {
                        this.setTransferTicker(4);
                        this.markDirty();
                    }
                }
            }
        }
    }

    public boolean transferItems()
    {
        if (TileEntityHopper.func_145891_a(this))
        {
            return true;
        }
        else
        {
            List list = this.worldObj.selectEntitiesWithinAABB(EntityItem.class, this.boundingBox.expand(0.25D, 0.0D, 0.25D), IEntitySelector.selectAnything);

            if (list.size() > 0)
            {
                TileEntityHopper.func_145898_a(this, (EntityItem) list.get(0));
            }

            return false;
        }
    }

    /**
     * Sets the transfer ticker, used to determine the delay between transfers.
     */
    public void setTransferTicker(int p_98042_1_)
    {
        this.transferTicker = p_98042_1_;
    }

    /**
     * Returns whether the hopper cart can currently transfer an item.
     */
    public boolean canTransfer()
    {
        return this.transferTicker > 0;
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
        else if (getCartType() == EnumCartTypes.HOPPER)
        {
            if (!this.worldObj.isRemote)
            {
                player.openGui(WoodenRails.INSTANCE, 0, worldObj, getEntityId(), 0, 0);
            }
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

        double d0 = this.motionX * this.motionX + this.motionZ * this.motionZ;

        if (!p_94095_1_.isExplosion())
        {
            this.entityDropItem(getCartItem(), 0.0F);
        }

        if (p_94095_1_.isFireDamage() || p_94095_1_.isExplosion() || d0 >= 0.009999999776482582D)
        {
            this.explodeCart(d0);
        }
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
            case HOPPER:
                return 5;
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
        if (getCartType() == EnumCartTypes.CHEST || getCartType() == EnumCartTypes.COLORED_CHEST || getCartType() == EnumCartTypes.HOPPER)
        {
            if (slot >= 0 && slot < getSizeInventory())
            {
                if (inventoryArray == null)
                    inventoryArray = new ItemStack[getSizeInventory()];
                return inventoryArray[slot];
            }
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
        if (this.getSizeInventory() > 0)
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

        if (getCartType() == EnumCartTypes.FURNACE)
        {
            this.pushX = nbt.getDouble("PushX");
            this.pushZ = nbt.getDouble("PushZ");
            this.fuel = nbt.getShort("Fuel");
        }
        else if (getCartType() == EnumCartTypes.TNT)
        {
            if (nbt.hasKey("TNTFuse", 99))
            {
                this.minecartTNTFuse = nbt.getInteger("TNTFuse");
            }
        }
        else if (getCartType() == EnumCartTypes.HOPPER)
        {
            this.transferTicker = nbt.getInteger("TransferCooldown");
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
        if (this.getSizeInventory() > 0)
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

        if (getCartType() == EnumCartTypes.FURNACE)
        {
            nbt.setDouble("PushX", this.pushX);
            nbt.setDouble("PushZ", this.pushZ);
            nbt.setShort("Fuel", (short) this.fuel);
        }
        else if (getCartType() == EnumCartTypes.TNT)
        {
            nbt.setInteger("TNTFuse", this.minecartTNTFuse);
        }
        else if (getCartType() == EnumCartTypes.HOPPER)
        {
            nbt.setInteger("TransferCooldown", this.transferTicker);
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
        if (this.inventoryArray == null)
            this.inventoryArray = new ItemStack[getSizeInventory()];
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
        else if (getCartType() == EnumCartTypes.HOPPER)
            return 5;
        return 0;
    }

    @Override
    public Block func_145817_o()
    {
        if (getCartType() == EnumCartTypes.CHEST || getCartType() == EnumCartTypes.COLORED_CHEST)
            return Blocks.chest;
        else if (getCartType() == EnumCartTypes.FURNACE)
            return Blocks.lit_furnace;
        else if (getCartType() == EnumCartTypes.TNT)
            return Blocks.tnt;
        else if (getCartType() == EnumCartTypes.HOPPER)
            return Blocks.hopper;
        return Blocks.air;
    }

    @Override
    public int getDefaultDisplayTileOffset()
    {
        if (getCartType() == EnumCartTypes.CHEST || getCartType() == EnumCartTypes.COLORED_CHEST)
            return 8;
        else if (getCartType() == EnumCartTypes.HOPPER)
            return 1;
        return 6;
    }

    @Override
    public int getDefaultDisplayTileData()
    {
        if (getCartType() == EnumCartTypes.FURNACE)
            return 2;
        return 0;
    }

    /**
     * Makes the minecart explode.
     */
    protected void explodeCart(double p_94103_1_)
    {
        if (!this.worldObj.isRemote && getCartType() == EnumCartTypes.TNT)
        {
            double d1 = Math.sqrt(p_94103_1_);

            if (d1 > 5.0D)
            {
                d1 = 5.0D;
            }

            this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, (float) (4.0D + this.rand.nextDouble() * 1.5D * d1), true);
            this.setDead();
        }
    }

    @Override
    protected void fall(float p_70069_1_)
    {
        if (getCartType() == EnumCartTypes.TNT && p_70069_1_ >= 3.0F)
        {
            float f1 = p_70069_1_ / 10.0F;
            this.explodeCart((double) (f1 * f1));
        }

        super.fall(p_70069_1_);
    }

    @Override
    public void onActivatorRailPass(int p_96095_1_, int p_96095_2_, int p_96095_3_, boolean p_96095_4_)
    {
        if (getCartType() == EnumCartTypes.TNT && p_96095_4_ && this.minecartTNTFuse < 0)
        {
            this.ignite();
        }
        else if (getCartType() == EnumCartTypes.HOPPER)
        {
            boolean flag1 = !p_96095_4_;

            if (flag1 != this.getBlocked())
            {
                this.setBlocked(flag1);
            }
        }
    }

    /**
     * Get whether this hopper minecart is being blocked by an activator rail.
     */
    public boolean getBlocked()
    {
        return this.isBlocked;
    }

    /**
     * Set whether this hopper minecart is being blocked by an activator rail.
     */
    public void setBlocked(boolean p_96110_1_)
    {
        this.isBlocked = p_96110_1_;
    }

    @SideOnly(Side.CLIENT)
    public void handleHealthUpdate(byte p_70103_1_)
    {
        if (getCartType() == EnumCartTypes.TNT && p_70103_1_ == 10)
        {
            this.ignite();
        }
        else
        {
            super.handleHealthUpdate(p_70103_1_);
        }
    }

    /**
     * Ignites this TNT cart.
     */
    public void ignite()
    {
        if (getCartType() == EnumCartTypes.TNT)
        {
            this.minecartTNTFuse = 80;

            if (!this.worldObj.isRemote)
            {
                this.worldObj.setEntityState(this, (byte) 10);
                this.worldObj.playSoundAtEntity(this, "game.tnt.primed", 1.0F, 1.0F);
            }
        }
    }

    /**
     * Returns true if the TNT minecart is ignited.
     */
    public boolean isIgnited()
    {
        return getCartType() == EnumCartTypes.TNT && this.minecartTNTFuse > -1;
    }

    @Override
    public float func_145772_a(Explosion p_145772_1_, World p_145772_2_, int p_145772_3_, int p_145772_4_, int p_145772_5_, Block p_145772_6_)
    {
        return this.isIgnited() && (BlockRailBase.func_150051_a(p_145772_6_) || BlockRailBase.func_150049_b_(p_145772_2_, p_145772_3_, p_145772_4_ + 1, p_145772_5_)) ? 0.0F : super.func_145772_a(p_145772_1_, p_145772_2_, p_145772_3_, p_145772_4_, p_145772_5_, p_145772_6_);
    }

    @Override
    public boolean func_145774_a(Explosion p_145774_1_, World p_145774_2_, int p_145774_3_, int p_145774_4_, int p_145774_5_, Block p_145774_6_, float p_145774_7_)
    {
        return this.isIgnited() && (BlockRailBase.func_150051_a(p_145774_6_) || BlockRailBase.func_150049_b_(p_145774_2_, p_145774_3_, p_145774_4_ + 1, p_145774_5_)) ? false : super.func_145774_a(p_145774_1_, p_145774_2_, p_145774_3_, p_145774_4_, p_145774_5_, p_145774_6_, p_145774_7_);
    }

    @Override
    public World getWorldObj()
    {
        return this.worldObj;
    }

    @Override
    public double getXPos()
    {
        return this.posX;
    }

    @Override
    public double getYPos()
    {
        return this.posY;
    }

    @Override
    public double getZPos()
    {
        return this.posZ;
    }
}

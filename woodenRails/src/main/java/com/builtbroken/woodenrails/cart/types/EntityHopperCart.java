package com.builtbroken.woodenrails.cart.types;

import com.builtbroken.woodenrails.WoodenRails;
import com.builtbroken.woodenrails.cart.EnumCartTypes;
import net.minecraft.block.Block;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;

import java.util.List;

/**
 * Created by Dark on 8/11/2015.
 */
public class EntityHopperCart extends EntityContainerCart implements IHopper
{
    private boolean isBlocked = true;
    private int transferTicker = -1;

    public EntityHopperCart(World world)
    {
        super(world);
    }

    public EntityHopperCart(World world, double xx, double yy, double zz)
    {
        super(world, xx, yy, zz);
    }

    @Override
    public EnumCartTypes getCartType()
    {
        return EnumCartTypes.HOPPER;
    }

    @Override
    public int getMinecartType()
    {
        return 5;
    }

    @Override
    public Block func_145817_o()
    {
        return Blocks.hopper;
    }

    @Override
    public int getDefaultDisplayTileOffset()
    {
        return 1;
    }

    @Override
    public int getSizeInventory()
    {
        return 5;
    }

    @Override
    public boolean interactFirst(EntityPlayer player)
    {
        if (MinecraftForge.EVENT_BUS.post(new MinecartInteractEvent(this, player)))
            return true;
        if (!this.worldObj.isRemote)
        {
            player.openGui(WoodenRails.INSTANCE, 0, worldObj, getEntityId(), 0, 0);
        }

        return true;
    }

    @Override
    public void onActivatorRailPass(int p_96095_1_, int p_96095_2_, int p_96095_3_, boolean p_96095_4_)
    {
        boolean flag1 = !p_96095_4_;

        if (flag1 != this.getBlocked())
        {
            this.setBlocked(flag1);
        }
    }

    public boolean getBlocked()
    {
        return this.isBlocked;
    }

    public void setBlocked(boolean p_96110_1_)
    {
        this.isBlocked = p_96110_1_;
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

    @Override
    public void onUpdate()
    {
        super.onUpdate();

        if (!this.worldObj.isRemote && this.isEntityAlive() && this.getBlocked())
        {
            --this.transferTicker;

            if (!this.canTransfer())
            {
                this.setTransferTicker(0);

                if (this.func_96112_aD())
                {
                    this.setTransferTicker(4);
                    this.markDirty();
                }
            }
        }
    }

    public boolean func_96112_aD()
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

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt)
    {
        super.writeEntityToNBT(nbt);
        nbt.setInteger("TransferCooldown", this.transferTicker);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt)
    {
        super.readEntityFromNBT(nbt);
        this.transferTicker = nbt.getInteger("TransferCooldown");
    }

    public void setTransferTicker(int p_98042_1_)
    {
        this.transferTicker = p_98042_1_;
    }

    public boolean canTransfer()
    {
        return this.transferTicker > 0;
    }
}

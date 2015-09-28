package com.builtbroken.woodenrails.cart.types;

import com.builtbroken.woodenrails.cart.EntityWoodenCart;
import com.builtbroken.woodenrails.cart.EnumCartTypes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;

/**
 * Cart that contains  a fluid tank
 * Created by Dark on 8/12/2015.
 */
public class EntityTankCart extends EntityWoodenCart implements IFluidHandler
{
    protected FluidTank internal_tank;

    public EntityTankCart(World world)
    {
        super(world);
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        //Fluid id
        dataWatcher.addObject(30, new Integer(-1));
        //Fluid amount
        dataWatcher.addObject(31, new Integer(0));
        //Tank type
        dataWatcher.addObject(32, Byte.valueOf((byte) 0));
    }

    protected void setTankType(TankCartType type)
    {
        if (!worldObj.isRemote)
            dataWatcher.updateObject(32, Byte.valueOf((byte) type.ordinal()));
    }

    protected TankCartType getTankType()
    {
        byte b = dataWatcher.getWatchableObjectByte(32);
        if (b >= 0 && b < TankCartType.values().length)
        {
            return TankCartType.values()[b];
        }
        return TankCartType.BUILDCRAFT;
    }

    protected void setFluidID(int id)
    {
        if (!worldObj.isRemote)
            dataWatcher.updateObject(30, Integer.valueOf(id));
    }

    protected int getFluidID()
    {
        return dataWatcher.getWatchableObjectInt(30);
    }

    protected void setFluidAmount(int a)
    {
        if (!worldObj.isRemote)
            dataWatcher.updateObject(31, Integer.valueOf(a));
    }

    protected int getFluidAmount()
    {
        return dataWatcher.getWatchableObjectInt(31);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt)
    {
        super.readEntityFromNBT(nbt);
        if (nbt.hasKey("tank"))
        {
            getTank().readFromNBT(nbt.getCompoundTag("tank"));
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt)
    {
        super.writeEntityToNBT(nbt);
        if (getTank() != null && getTank().getFluid() != null)
        {
            nbt.setTag("tank", getTank().writeToNBT(new NBTTagCompound()));
        }
    }

    @Override
    public EnumCartTypes getCartType()
    {
        return EnumCartTypes.TANK;
    }

    @Override
    public int getMinecartType()
    {
        return 1;
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
    {
        return getTank().fill(resource, doFill);
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
    {
        if (resource != null && (getTank().getFluid() == null || resource.getFluid() == getTank().getFluid().getFluid()))
        {
            return getTank().drain(resource.amount, doDrain);
        }
        return null;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
    {
        return getTank().drain(maxDrain, doDrain);
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid)
    {
        return getTank().getFluid() == null || getTank().getFluid().getFluid() == fluid;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid)
    {
        return getTank().getFluid() != null && getTank().getFluid().getFluid() == fluid;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from)
    {
        if (getTank() == null)
        {
            return new FluidTankInfo[0];
        }
        return new FluidTankInfo[]{getTank().getInfo()};
    }

    public FluidTank getTank()
    {
        if (internal_tank == null)
        {
            //Custom internal class to auto update the datawatchers used by the renderers
            internal_tank = new FluidTank(getTankType().buckets * FluidContainerRegistry.BUCKET_VOLUME)
            {
                @Override
                public int fill(FluidStack resource, boolean doFill)
                {
                    Fluid fluid = getFluid() != null ? getFluid().getFluid() : null;
                    if (resource != null && (fluid == null || resource.getFluid() == fluid))
                    {
                        int fill = super.fill(resource, doFill);
                        if (fluid != getFluid().getFluid())
                        {
                            EntityTankCart.this.setFluidID(this.getFluid().getFluidID());
                        }
                        if (doFill && fill > 0)
                        {
                            EntityTankCart.this.setFluidAmount(getFluidAmount());
                        }
                        return fill;
                    }
                    return 0;
                }

                @Override
                public FluidStack drain(int maxDrain, boolean doDrain)
                {
                    if (maxDrain > 0 && doDrain)
                    {
                        Fluid fluid = getFluid() != null ? getFluid().getFluid() : null;
                        FluidStack drain = super.drain(maxDrain, doDrain);
                        if (drain != null)
                        {
                            //Should never happen but just in case a freak ASM :P
                            if (fluid != getFluid().getFluid())
                            {
                                EntityTankCart.this.setFluidID(this.getFluid().getFluidID());
                            }
                            if (drain.amount > 0)
                            {
                                EntityTankCart.this.setFluidAmount(getFluidAmount());
                            }
                        }
                        return drain;
                    }
                    return super.drain(maxDrain, doDrain);
                }
            };
        }
        return internal_tank;
    }

    public enum TankCartType
    {
        BUILDCRAFT(16),
        OPENBLOCKS(16);
        //TODO add more tanks types for the lolz

        public final int buckets;

        TankCartType(int buckets)
        {
            this.buckets = buckets;
        }
    }
}

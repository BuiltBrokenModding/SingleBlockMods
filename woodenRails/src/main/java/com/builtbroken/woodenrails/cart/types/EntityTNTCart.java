package com.builtbroken.woodenrails.cart.types;

import com.builtbroken.woodenrails.cart.EntityWoodenCart;
import com.builtbroken.woodenrails.cart.EnumCartTypes;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

/**
 * Created by Dark on 8/11/2015.
 */
public class EntityTNTCart extends EntityWoodenCart
{
    private int minecartTNTFuse = -1;

    public EntityTNTCart(World world)
    {
        super(world);
    }

    public EntityTNTCart(World world, double xx, double yy, double zz)
    {
        super(world, xx, yy, zz);
    }

    @Override
    public EnumCartTypes getCartType()
    {
        return EnumCartTypes.TNT;
    }

    @Override
    public int getMinecartType()
    {
        return 3;
    }

    @Override
    public Block func_145817_o()
    {
        return Blocks.tnt;
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();

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

    @Override
    public void killMinecart(DamageSource p_94095_1_)
    {
        super.killMinecart(p_94095_1_);
        double d0 = this.motionX * this.motionX + this.motionZ * this.motionZ;

        if (!p_94095_1_.isExplosion())
        {
            this.entityDropItem(new ItemStack(Blocks.tnt, 1), 0.0F);
        }

        if (p_94095_1_.isFireDamage() || p_94095_1_.isExplosion() || d0 >= 0.009999999776482582D)
        {
            this.explodeCart(d0);
        }
    }

    protected void explodeCart(double p_94103_1_)
    {
        if (!this.worldObj.isRemote)
        {
            double d1 = Math.sqrt(p_94103_1_);

            if (d1 > 5.0D)
            {
                d1 = 5.0D;
            }

            this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, (float)(4.0D + this.rand.nextDouble() * 1.5D * d1), true);
            this.setDead();
        }
    }

    @Override
    protected void fall(float p_70069_1_)
    {
        if (p_70069_1_ >= 3.0F)
        {
            float f1 = p_70069_1_ / 10.0F;
            this.explodeCart((double)(f1 * f1));
        }

        super.fall(p_70069_1_);
    }

    @Override
    public void onActivatorRailPass(int p_96095_1_, int p_96095_2_, int p_96095_3_, boolean p_96095_4_)
    {
        if (p_96095_4_ && this.minecartTNTFuse < 0)
        {
            this.ignite();
        }
    }

    @Override @SideOnly(Side.CLIENT)
    public void handleHealthUpdate(byte p_70103_1_)
    {
        if (p_70103_1_ == 10)
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
        this.minecartTNTFuse = 80;

        if (!this.worldObj.isRemote)
        {
            this.worldObj.setEntityState(this, (byte)10);
            this.worldObj.playSoundAtEntity(this, "game.tnt.primed", 1.0F, 1.0F);
        }
    }

    @SideOnly(Side.CLIENT)
    public int func_94104_d()
    {
        return this.minecartTNTFuse;
    }

    /**
     * Returns true if the TNT minecart is ignited.
     */
    public boolean isIgnited()
    {
        return this.minecartTNTFuse > -1;
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
    protected void readEntityFromNBT(NBTTagCompound p_70037_1_)
    {
        super.readEntityFromNBT(p_70037_1_);

        if (p_70037_1_.hasKey("TNTFuse", 99))
        {
            this.minecartTNTFuse = p_70037_1_.getInteger("TNTFuse");
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound p_70014_1_)
    {
        super.writeEntityToNBT(p_70014_1_);
        p_70014_1_.setInteger("TNTFuse", this.minecartTNTFuse);
    }
}

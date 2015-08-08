package com.builtbroken.woodenbucket.fluid;

import com.builtbroken.woodenbucket.WoodenBucket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

/**
 * Created by Dark on 8/8/2015.
 */
public class BlockMilk extends BlockFluidClassic
{
    IIcon blockFlowing;

    public BlockMilk(Fluid fluid)
    {
        super(fluid, Material.water);
        setBlockName(WoodenBucket.PREFIX + "milk");
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg)
    {
        this.blockIcon = reg.registerIcon(WoodenBucket.PREFIX + "milk_still");
        this.blockFlowing = reg.registerIcon(WoodenBucket.PREFIX + "milk_flow");
        getFluid().setFlowingIcon(blockFlowing);
        getFluid().setStillIcon(blockIcon);
    }
}

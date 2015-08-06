package com.builtbroken.tele.door.door;

import com.builtbroken.tele.door.TeleDoor;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S19PacketEntityHeadLook;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

/**
 * Created by Dark on 8/4/2015.
 */
public class BlockDoor extends BlockContainer
{
    public BlockDoor()
    {
        super(Material.rock);
        this.setBlockName(TeleDoor.PREFIX + "teleDoor");
        this.setCreativeTab(CreativeTabs.tabTransport);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        if (side == meta)
        {
            return Blocks.clay.getIcon(side, meta);
        }
        return Blocks.planks.getIcon(side, meta);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister p_149651_1_)
    {
        //this.blockIcon = p_149651_1_.registerIcon(this.getTextureName());
    }

    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item p_149666_1_, CreativeTabs p_149666_2_, List p_149666_3_)
    {
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
        {
            p_149666_3_.add(new ItemStack(p_149666_1_, 1, dir.ordinal()));
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World p_149668_1_, int p_149668_2_, int p_149668_3_, int p_149668_4_)
    {
        return null;
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity e)
    {
        if (!world.isRemote)
        {
            if (e instanceof EntityPlayer)
            {
                int meta = Math.max(Math.min(world.getBlockMetadata(x, y, z), 5), 0);
                ForgeDirection facing = ForgeDirection.getOrientation(meta);
                ForgeDirection toTheRight = getDirectionToRight(facing);
                int destX = -(4 * facing.offsetX) - (toTheRight.offsetX * 3);
                int destY = -(4 * facing.offsetY) - (toTheRight.offsetY * 3);
                int destZ = -(4 * facing.offsetZ) - (toTheRight.offsetZ * 3);

                //double dx = e.posX - x - 0.5;
                double dy = e.posY - y - 0.5;
                //double dz = e.posZ - z - 0.5;

                float yaw = this.getYaw(facing, toTheRight);

                double nx = x + 0.5 + destX; // + vec.xCoord;
                double ny = y + 0.5 + destY + dy;
                double nz = z + 0.5 + destZ; // + + vec.zCoord;
                //e.setVelocity(0, 0, 0);

                e.rotationYaw += yaw;
                ((EntityPlayer) e).rotationYawHead += yaw;
                ((EntityPlayer) e).prevRotationYawHead += yaw;
                e.prevRotationYaw += yaw;
                ((EntityPlayer) e).setPositionAndUpdate(nx, ny, nz);
                byte a = (byte) (MathHelper.floor_float(((EntityPlayer) e).rotationYawHead * 256.0F / 360.0F));
                ((EntityPlayerMP) e).playerNetServerHandler.sendPacket(new S19PacketEntityHeadLook(e, a));
                //onTeleport((EntityPlayer) e, this);

            }
        }
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
    {
        return new TileDoor();
    }

    private float getYaw(ForgeDirection facing, ForgeDirection newFacing)
    {
        int rel = getRelativeAngle(facing, newFacing);
        if (rel > 180)
            rel = rel - 360;
        return rel;
    }

    public static int getRelativeAngle(ForgeDirection from, ForgeDirection to)
    {
        int rel = getAngleFromDirection(to) - getAngleFromDirection(from);
        return (360 + rel % 360) % 360;
    }

    private static int getAngleFromDirection(ForgeDirection dir)
    {
        switch (dir)
        {
            case NORTH:
                return 0;
            case EAST:
                return 90;
            case SOUTH:
                return 180;
            case WEST:
                return 270;
            default:
                return 0;
        }
    }

    public static ForgeDirection getDirectionToRight(ForgeDirection dir)
    {
        switch (dir)
        {
            case EAST:
                return ForgeDirection.SOUTH;
            case NORTH:
                return ForgeDirection.EAST;
            case SOUTH:
                return ForgeDirection.WEST;
            case WEST:
                return ForgeDirection.NORTH;
            default:
                return dir;
        }
    }
}

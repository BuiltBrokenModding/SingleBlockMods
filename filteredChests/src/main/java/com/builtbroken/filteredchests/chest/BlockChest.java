package com.builtbroken.filteredchests.chest;

import com.builtbroken.filteredchests.FilteredChests;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import static net.minecraftforge.common.util.ForgeDirection.DOWN;

public class BlockChest extends BlockContainer
{
    private final Random random = new Random();

    //public static Color[] defaultColors = new Color[]{Color.BLACK, Color.RED, Color.GREEN, new Color(102, 51, 0), Color.BLUE, new Color(170, 0, 170), Color.CYAN, new Color(224, 224, 224), Color.GRAY, Color.PINK, new Color(128, 255, 0), Color.YELLOW, new Color(51, 255, 255), Color.MAGENTA, Color.ORANGE, Color.WHITE};
    //public static final String[] defaultColorNames = new String[]{"black", "red", "green", "brown", "blue", "purple", "cyan", "silver", "gray", "pink", "lime", "yellow", "lightBlue", "magenta", "orange", "white"};

    public BlockChest()
    {
        super(Material.wood);
        this.setBlockName(FilteredChests.PREFIX + "filteredChest");
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    public int getRenderType()
    {
        return -1;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
    {
        boolean block = isMatchingChest(world, x, y, z - 1);
        boolean block1 = isMatchingChest(world, x, y, z + 1);
        boolean block2 = isMatchingChest(world, x - 1, y, z);
        boolean block3 = isMatchingChest(world, x + 1, y, z);

        if (block)
        {
            this.setBlockBounds(0.0625F, 0.0F, 0.0F, 0.9375F, 0.875F, 0.9375F);
        }
        else if (block1)
        {
            this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 1.0F);
        }
        else if (block2)
        {
            this.setBlockBounds(0.0F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
        }
        else if (block3)
        {
            this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 1.0F, 0.875F, 0.9375F);
        }
        else
        {
            this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
        }
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z)
    {
        super.onBlockAdded(world, x, y, z);
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileChest)
        {
            this.attemptToConnectToChest(world, x, y, z);

            for (int i = 2; i < ForgeDirection.VALID_DIRECTIONS.length; i++)
            {
                ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
                if (isMatchingChest(world, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ))
                {
                    this.attemptToConnectToChest(world, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
                }
            }
        }
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack)
    {
        boolean block = isMatchingChest(world, x, y, z - 1);
        boolean block1 = isMatchingChest(world, x, y, z + 1);
        boolean block2 = isMatchingChest(world, x - 1, y, z);
        boolean block3 = isMatchingChest(world, x + 1, y, z);

        byte b0 = 0;
        int facingDirection = MathHelper.floor_double((double) (entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;


        if (facingDirection == 0)
        {
            b0 = 2;
        }

        if (facingDirection == 1)
        {
            b0 = 5;
        }

        if (facingDirection == 2)
        {
            b0 = 3;
        }

        if (facingDirection == 3)
        {
            b0 = 4;
        }

        if (!block && !block1 && !block2 && !block3)
        {
            world.setBlockMetadataWithNotify(x, y, z, b0, 3);
        }
        else
        {
            //Force the chest next to us to face the same direction
            if ((block || block1) && (b0 == 4 || b0 == 5))
            {
                if (block)
                {
                    world.setBlockMetadataWithNotify(x, y, z - 1, b0, 3);
                }
                else
                {
                    world.setBlockMetadataWithNotify(x, y, z + 1, b0, 3);
                }

                world.setBlockMetadataWithNotify(x, y, z, b0, 3);
            }

            if ((block2 || block3) && (b0 == 2 || b0 == 3))
            {
                if (block2)
                {
                    world.setBlockMetadataWithNotify(x - 1, y, z, b0, 3);
                }
                else
                {
                    world.setBlockMetadataWithNotify(x + 1, y, z, b0, 3);
                }

                world.setBlockMetadataWithNotify(x, y, z, b0, 3);
            }
        }

        if (stack.hasDisplayName())
        {
            ((TileChest) world.getTileEntity(x, y, z)).setCustomName(stack.getDisplayName());
        }
    }

    public boolean isMatchingChest(IBlockAccess world, int x, int y, int z)
    {
        Block block = world.getBlock(x, y, z);
        TileEntity tile = world.getTileEntity(x, y, z);
        return block == this && tile instanceof TileChest;
    }

    public void attemptToConnectToChest(World world, int x, int y, int z)
    {
        if (!world.isRemote)
        {
            Block block = world.getBlock(x, y, z - 1);
            Block block1 = world.getBlock(x, y, z + 1);
            Block block2 = world.getBlock(x - 1, y, z);
            Block block3 = world.getBlock(x + 1, y, z);
            boolean flag = true;
            int l;
            Block block4;
            int i1;
            Block block5;
            boolean flag1;
            byte b0;
            int j1;

            if (block != this && block1 != this)
            {
                if (block2 != this && block3 != this)
                {
                    b0 = 3;

                    if (block.func_149730_j() && !block1.func_149730_j())
                    {
                        b0 = 3;
                    }

                    if (block1.func_149730_j() && !block.func_149730_j())
                    {
                        b0 = 2;
                    }

                    if (block2.func_149730_j() && !block3.func_149730_j())
                    {
                        b0 = 5;
                    }

                    if (block3.func_149730_j() && !block2.func_149730_j())
                    {
                        b0 = 4;
                    }
                }
                else
                {
                    l = block2 == this ? x - 1 : x + 1;
                    block4 = world.getBlock(l, y, z - 1);
                    i1 = block2 == this ? x - 1 : x + 1;
                    block5 = world.getBlock(i1, y, z + 1);
                    b0 = 3;
                    flag1 = true;

                    if (block2 == this)
                    {
                        j1 = world.getBlockMetadata(x - 1, y, z);
                    }
                    else
                    {
                        j1 = world.getBlockMetadata(x + 1, y, z);
                    }

                    if (j1 == 2)
                    {
                        b0 = 2;
                    }

                    if ((block.func_149730_j() || block4.func_149730_j()) && !block1.func_149730_j() && !block5.func_149730_j())
                    {
                        b0 = 3;
                    }

                    if ((block1.func_149730_j() || block5.func_149730_j()) && !block.func_149730_j() && !block4.func_149730_j())
                    {
                        b0 = 2;
                    }
                }
            }
            else
            {
                l = block == this ? z - 1 : z + 1;
                block4 = world.getBlock(x - 1, y, l);
                i1 = block == this ? z - 1 : z + 1;
                block5 = world.getBlock(x + 1, y, i1);
                b0 = 5;
                flag1 = true;

                if (block == this)
                {
                    j1 = world.getBlockMetadata(x, y, z - 1);
                }
                else
                {
                    j1 = world.getBlockMetadata(x, y, z + 1);
                }

                if (j1 == 4)
                {
                    b0 = 4;
                }

                if ((block2.func_149730_j() || block4.func_149730_j()) && !block3.func_149730_j() && !block5.func_149730_j())
                {
                    b0 = 5;
                }

                if ((block3.func_149730_j() || block5.func_149730_j()) && !block2.func_149730_j() && !block4.func_149730_j())
                {
                    b0 = 4;
                }
            }

            world.setBlockMetadataWithNotify(x, y, z, b0, 3);
        }
    }

    @Override
    public boolean canPlaceBlockAt(World world, int i, int j, int k)
    {
        for (int b = 2; b < ForgeDirection.VALID_DIRECTIONS.length; b++)
        {
            ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[b];
            int x = dir.offsetX + i;
            int y = dir.offsetY + j;
            int z = dir.offsetZ + k;
            TileEntity tile = world.getTileEntity(x, y, z);
            if (world.getBlock(dir.offsetX + x, dir.offsetY + y, dir.offsetZ + z) == this && tile instanceof TileChest)
            {
                if (isMatchingChest(world, x, y, z - 1) || isMatchingChest(world, x, y, z + 1) || isMatchingChest(world, x - 1, y, z) || isMatchingChest(world, x + 1, y, z))
                {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean canReplace(World world, int i, int j, int k, int side, ItemStack stack)
    {
        for (int b = 2; b < ForgeDirection.VALID_DIRECTIONS.length; b++)
        {
            ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[b];
            int x = dir.offsetX + i;
            int y = dir.offsetY + j;
            int z = dir.offsetZ + k;
            TileEntity tile = world.getTileEntity(x, y, z);
            if (world.getBlock(x, y, z) == this && tile instanceof TileChest)
            {
                if (isMatchingChest(world, x, y, z - 1) || isMatchingChest(world, x, y, z + 1) || isMatchingChest(world, x - 1, y, z) || isMatchingChest(world, x + 1, y, z))
                {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
    {
        super.onNeighborBlockChange(world, x, y, z, block);
        TileChest chest = (TileChest) world.getTileEntity(x, y, z);

        if (chest != null)
        {
            chest.updateContainingBlockInfo();
        }
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta)
    {
        TileChest tile = (TileChest) world.getTileEntity(x, y, z);

        if (tile != null)
        {
            for (int i1 = 0; i1 < tile.getSizeInventory(); ++i1)
            {
                ItemStack itemstack = tile.getStackInSlot(i1);

                if (itemstack != null)
                {
                    float f = this.random.nextFloat() * 0.8F + 0.1F;
                    float f1 = this.random.nextFloat() * 0.8F + 0.1F;
                    EntityItem entityitem;

                    for (float f2 = this.random.nextFloat() * 0.8F + 0.1F; itemstack.stackSize > 0; world.spawnEntityInWorld(entityitem))
                    {
                        int j1 = this.random.nextInt(21) + 10;

                        if (j1 > itemstack.stackSize)
                        {
                            j1 = itemstack.stackSize;
                        }

                        itemstack.stackSize -= j1;
                        entityitem = new EntityItem(world, (double) ((float) x + f), (double) ((float) y + f1), (double) ((float) z + f2), new ItemStack(itemstack.getItem(), j1, itemstack.getItemDamage()));
                        float f3 = 0.05F;
                        entityitem.motionX = (double) ((float) this.random.nextGaussian() * f3);
                        entityitem.motionY = (double) ((float) this.random.nextGaussian() * f3 + 0.2F);
                        entityitem.motionZ = (double) ((float) this.random.nextGaussian() * f3);

                        if (itemstack.hasTagCompound())
                        {
                            entityitem.getEntityItem().setTagCompound((NBTTagCompound) itemstack.getTagCompound().copy());
                        }
                    }
                }
            }

            world.func_147453_f(x, y, z, block);
        }

        super.breakBlock(world, x, y, z, block, meta);
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune)
    {
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        ret.add(new ItemStack(this, 1, damageDropped(metadata)));
        //TODO encode filter
        return ret;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xHit, float yHit, float zHit)
    {
        if (world.isRemote)
        {
            return true;
        }
        else
        {
            IInventory iinventory = this.getCombinedInventory(world, x, y, z);

            if (iinventory != null)
            {
                player.displayGUIChest(iinventory);
            }

            return true;
        }
    }

    public IInventory getCombinedInventory(World world, int x, int y, int z)
    {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileChest)
        {
            boolean negZChest = isMatchingChest(world, x, y, z - 1);
            boolean posZChest = isMatchingChest(world, x, y, z + 1);
            boolean negXChest = isMatchingChest(world, x - 1, y, z);
            boolean posXChest = isMatchingChest(world, x + 1, y, z);


            if (world.isSideSolid(x, y + 1, z, DOWN))
            {
                return null;
            }
            else if (isOcelotSittingOnBlock(world, x, y, z))
            {
                return null;
            }
            else if (negXChest && (world.isSideSolid(x - 1, y + 1, z, DOWN) || isOcelotSittingOnBlock(world, x - 1, y, z)))
            {
                return null;
            }
            else if (posXChest && (world.isSideSolid(x + 1, y + 1, z, DOWN) || isOcelotSittingOnBlock(world, x + 1, y, z)))
            {
                return null;
            }
            else if (negZChest && (world.isSideSolid(x, y + 1, z - 1, DOWN) || isOcelotSittingOnBlock(world, x, y, z - 1)))
            {
                return null;
            }
            else if (posZChest && (world.isSideSolid(x, y + 1, z + 1, DOWN) || isOcelotSittingOnBlock(world, x, y, z + 1)))
            {
                return null;
            }
            else
            {
                if (negXChest)
                {
                    return new InventoryLargeChest("container.chestDouble", (TileChest) world.getTileEntity(x - 1, y, z), (IInventory) tile);
                }

                if (posXChest)
                {
                    return new InventoryLargeChest("container.chestDouble", (IInventory) tile, (TileChest) world.getTileEntity(x + 1, y, z));
                }

                if (negZChest)
                {
                    return new InventoryLargeChest("container.chestDouble", (TileChest) world.getTileEntity(x, y, z - 1), (IInventory) tile);
                }

                if (posZChest)
                {
                    return new InventoryLargeChest("container.chestDouble", (IInventory) tile, (TileChest) world.getTileEntity(x, y, z + 1));
                }
                return (IInventory) tile;
            }
        }
        return null;
    }

    @Override
    public TileEntity createNewTileEntity(World w, int m)
    {
        return new TileChest();
    }

    private static boolean isOcelotSittingOnBlock(World world, int x, int y, int z)
    {
        Iterator iterator = world.getEntitiesWithinAABB(EntityOcelot.class, AxisAlignedBB.getBoundingBox((double) x, (double) (y + 1), (double) z, (double) (x + 1), (double) (y + 2), (double) (z + 1))).iterator();
        EntityOcelot entityocelot;

        do
        {
            if (!iterator.hasNext())
            {
                return false;
            }

            Entity entity = (Entity) iterator.next();
            entityocelot = (EntityOcelot) entity;
        }
        while (!entityocelot.isSitting());

        return true;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
    {
        ItemStack stack = new ItemStack(this, 1, this.getDamageValue(world, x, y, z));
        //TODO encode filter if player is sneak clicking
        return stack;
    }

    @Override
    public boolean hasComparatorInputOverride()
    {
        return true;
    }

    @Override
    public int getComparatorInputOverride(World world, int x, int y, int z, int side)
    {
        return Container.calcRedstoneFromInventory(this.getCombinedInventory(world, x, y, z));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg)
    {
    }

    @Override
    public IIcon getIcon(int side, int meta)
    {
        return Blocks.planks.getIcon(side, meta);
    }
}

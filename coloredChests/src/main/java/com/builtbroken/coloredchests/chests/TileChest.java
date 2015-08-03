package com.builtbroken.coloredchests.chests;

import com.builtbroken.coloredchests.ColoredChests;
import com.builtbroken.coloredchests.network.PacketChest;
import com.builtbroken.coloredchests.network.PacketManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

import java.awt.*;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Dark on 7/26/2015.
 */
public class TileChest extends TileInv
{
    //TODO have custom name render on outside of chest
    /** Determines if the check for adjacent chests has taken place. */
    public boolean adjacentChestChecked;
    /** Contains the chest tile located adjacent to this one (if any) */
    public TileChest adjacentChestZNeg;
    /** Contains the chest tile located adjacent to this one (if any) */
    public TileChest adjacentChestXPos;
    /** Contains the chest tile located adjacent to this one (if any) */
    public TileChest adjacentChestXNeg;
    /** Contains the chest tile located adjacent to this one (if any) */
    public TileChest adjacentChestZPos;
    /** The current angle of the lid (between 0 and 1) */
    public float lidAngle;
    /** The angle of the lid last tick */
    public float prevLidAngle;
    /** The number of players currently using this chest */
    public int numPlayersUsing;
    public Color color = Color.WHITE;

    /** Server sync counter (once per 20 ticks) */
    private int ticksSinceSync;
    public String customName = "";


    @Override
    public String getInventoryName()
    {
        return this.hasCustomInventoryName() ? this.customName : "container.chest";
    }

    @Override
    public boolean hasCustomInventoryName()
    {
        return this.customName != null && this.customName.length() > 0;
    }

    public void setCustomName(String name)
    {
        this.customName = name;
        if (!getWorldObj().isRemote)
            PacketManager.sendToAllAround(new PacketChest(this, PacketChest.ChestPacketType.NAME), this);
    }

    public void setColor(Color color)
    {
        this.color = color;
        if (!getWorldObj().isRemote)
            PacketManager.sendToAllAround(new PacketChest(this, PacketChest.ChestPacketType.COLOR), this);
    }

    @Override
    public Packet getDescriptionPacket()
    {
        return PacketManager.toMcPacket(new PacketChest(this, PacketChest.ChestPacketType.DESC));
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        if (nbt.hasKey("CustomName", 8))
        {
            this.customName = nbt.getString("CustomName");
        }
        if (nbt.hasKey("rgb"))
        {
            this.color = new Color(nbt.getInteger("rgb"));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        if (this.hasCustomInventoryName())
        {
            nbt.setString("CustomName", this.customName);
        }
        if (color != null)
        {
            nbt.setInteger("rgb", color.getRGB());
        }
    }

    @Override
    public void updateContainingBlockInfo()
    {
        super.updateContainingBlockInfo();
        this.adjacentChestChecked = false;
    }

    private void checkAdjacentChest(TileChest tile, int side)
    {
        if (tile.isInvalid())
        {
            this.adjacentChestChecked = false;
        }
        else if (this.adjacentChestChecked)
        {
            switch (side)
            {
                case 0:
                    if (this.adjacentChestZPos != tile)
                    {
                        this.adjacentChestChecked = false;
                    }

                    break;
                case 1:
                    if (this.adjacentChestXNeg != tile)
                    {
                        this.adjacentChestChecked = false;
                    }

                    break;
                case 2:
                    if (this.adjacentChestZNeg != tile)
                    {
                        this.adjacentChestChecked = false;
                    }

                    break;
                case 3:
                    if (this.adjacentChestXPos != tile)
                    {
                        this.adjacentChestChecked = false;
                    }
            }
        }
    }

    /**
     * Performs the check for adjacent chests to determine if this chest is double or not.
     */
    public void checkForAdjacentChests()
    {
        if (!this.adjacentChestChecked)
        {
            this.adjacentChestChecked = true;
            this.adjacentChestZNeg = null;
            this.adjacentChestXPos = null;
            this.adjacentChestXNeg = null;
            this.adjacentChestZPos = null;

            if (this.canConnectToBlock(this.xCoord - 1, this.yCoord, this.zCoord))
            {
                this.adjacentChestXNeg = (TileChest) this.getWorldObj().getTileEntity(this.xCoord - 1, this.yCoord, this.zCoord);
            }

            if (this.canConnectToBlock(this.xCoord + 1, this.yCoord, this.zCoord))
            {
                this.adjacentChestXPos = (TileChest) this.getWorldObj().getTileEntity(this.xCoord + 1, this.yCoord, this.zCoord);
            }

            if (this.canConnectToBlock(this.xCoord, this.yCoord, this.zCoord - 1))
            {
                this.adjacentChestZNeg = (TileChest) this.getWorldObj().getTileEntity(this.xCoord, this.yCoord, this.zCoord - 1);
            }

            if (this.canConnectToBlock(this.xCoord, this.yCoord, this.zCoord + 1))
            {
                this.adjacentChestZPos = (TileChest) this.getWorldObj().getTileEntity(this.xCoord, this.yCoord, this.zCoord + 1);
            }

            if (this.adjacentChestZNeg != null)
            {
                this.adjacentChestZNeg.checkAdjacentChest(this, 0);
            }

            if (this.adjacentChestZPos != null)
            {
                this.adjacentChestZPos.checkAdjacentChest(this, 2);
            }

            if (this.adjacentChestXPos != null)
            {
                this.adjacentChestXPos.checkAdjacentChest(this, 1);
            }

            if (this.adjacentChestXNeg != null)
            {
                this.adjacentChestXNeg.checkAdjacentChest(this, 3);
            }
        }
    }

    private boolean canConnectToBlock(int x, int y, int z)
    {
        if (this.getWorldObj() != null)
        {
            TileEntity tile = this.getWorldObj().getTileEntity(x, y, z);
            return tile instanceof TileChest && ColoredChests.doColorsMatch(((TileChest) tile).color,  color);
        }
        return false;
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();
        this.checkForAdjacentChests();
        ++this.ticksSinceSync;
        float f;

        if (!this.getWorldObj().isRemote && this.numPlayersUsing != 0 && (this.ticksSinceSync + this.xCoord + this.yCoord + this.zCoord) % 200 == 0)
        {
            this.numPlayersUsing = 0;
            f = 5.0F;
            List list = this.getWorldObj().getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox((double) ((float) this.xCoord - f), (double) ((float) this.yCoord - f), (double) ((float) this.zCoord - f), (double) ((float) (this.xCoord + 1) + f), (double) ((float) (this.yCoord + 1) + f), (double) ((float) (this.zCoord + 1) + f)));
            Iterator iterator = list.iterator();

            while (iterator.hasNext())
            {
                EntityPlayer entityplayer = (EntityPlayer) iterator.next();

                if (entityplayer.openContainer instanceof ContainerChest)
                {
                    IInventory iinventory = ((ContainerChest) entityplayer.openContainer).getLowerChestInventory();

                    if (iinventory == this || iinventory instanceof InventoryLargeChest && ((InventoryLargeChest) iinventory).isPartOfLargeChest(this))
                    {
                        ++this.numPlayersUsing;
                    }
                }
            }
        }

        this.prevLidAngle = this.lidAngle;
        f = 0.1F;
        double d2;

        if (this.numPlayersUsing > 0 && this.lidAngle == 0.0F && this.adjacentChestZNeg == null && this.adjacentChestXNeg == null)
        {
            double d1 = (double) this.xCoord + 0.5D;
            d2 = (double) this.zCoord + 0.5D;

            if (this.adjacentChestZPos != null)
            {
                d2 += 0.5D;
            }

            if (this.adjacentChestXPos != null)
            {
                d1 += 0.5D;
            }

            this.getWorldObj().playSoundEffect(d1, (double) this.yCoord + 0.5D, d2, "random.chestopen", 0.5F, this.getWorldObj().rand.nextFloat() * 0.1F + 0.9F);
        }

        if (this.numPlayersUsing == 0 && this.lidAngle > 0.0F || this.numPlayersUsing > 0 && this.lidAngle < 1.0F)
        {
            float f1 = this.lidAngle;

            if (this.numPlayersUsing > 0)
            {
                this.lidAngle += f;
            }
            else
            {
                this.lidAngle -= f;
            }

            if (this.lidAngle > 1.0F)
            {
                this.lidAngle = 1.0F;
            }

            float f2 = 0.5F;

            if (this.lidAngle < f2 && f1 >= f2 && this.adjacentChestZNeg == null && this.adjacentChestXNeg == null)
            {
                d2 = (double) this.xCoord + 0.5D;
                double d0 = (double) this.zCoord + 0.5D;

                if (this.adjacentChestZPos != null)
                {
                    d0 += 0.5D;
                }

                if (this.adjacentChestXPos != null)
                {
                    d2 += 0.5D;
                }

                this.getWorldObj().playSoundEffect(d2, (double) this.yCoord + 0.5D, d0, "random.chestclosed", 0.5F, this.getWorldObj().rand.nextFloat() * 0.1F + 0.9F);
            }

            if (this.lidAngle < 0.0F)
            {
                this.lidAngle = 0.0F;
            }
        }
    }

    @Override
    public boolean receiveClientEvent(int packet_id, int value)
    {
        if (packet_id == 1)
        {
            this.numPlayersUsing = value;
            return true;
        }
        else
        {
            return super.receiveClientEvent(packet_id, value);
        }
    }

    @Override
    public void openInventory()
    {
        if (this.numPlayersUsing < 0)
        {
            this.numPlayersUsing = 0;
        }

        ++this.numPlayersUsing;
        this.getWorldObj().addBlockEvent(this.xCoord, this.yCoord, this.zCoord, this.getBlockType(), 1, this.numPlayersUsing);
        this.getWorldObj().notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType());
        this.getWorldObj().notifyBlocksOfNeighborChange(this.xCoord, this.yCoord - 1, this.zCoord, this.getBlockType());
    }

    @Override
    public void closeInventory()
    {
        if (this.getBlockType() instanceof BlockChest)
        {
            --this.numPlayersUsing;
            this.getWorldObj().addBlockEvent(this.xCoord, this.yCoord, this.zCoord, this.getBlockType(), 1, this.numPlayersUsing);
            this.getWorldObj().notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType());
            this.getWorldObj().notifyBlocksOfNeighborChange(this.xCoord, this.yCoord - 1, this.zCoord, this.getBlockType());
        }
    }

    @Override
    public void invalidate()
    {
        super.invalidate();
        this.updateContainingBlockInfo();
        this.checkForAdjacentChests();
    }
}

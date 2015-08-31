package com.builtbroken.filteredchests.chest;

import com.builtbroken.filteredchests.network.PacketTile;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.awt.*;


public class PacketChest extends PacketTile
{
    ChestPacketType type;
    Color color;
    String name;

    public PacketChest()
    {

    }

    public PacketChest(TileChest tile, ChestPacketType type)
    {
        super(tile);
        this.type = type;
        switch (type)
        {
            case DESC:
                name = tile.customName;
                break;
            case NAME:
                name = tile.customName;
                break;
        }
    }

    @Override
    public void encode(ByteArrayDataOutput output)
    {
        super.encode(output);
        output.writeInt(type.ordinal());
        switch (type)
        {
            case DESC:
                output.writeInt(color.getRGB());
                output.writeUTF(name);
                break;
            case NAME:
                output.writeUTF(name);
                break;
        }
    }

    @Override
    public void decode(ByteArrayDataInput input)
    {
        super.decode(input);
        int t = input.readInt();
        if (t >= 0 && t < ChestPacketType.values().length)
        {
            type = ChestPacketType.values()[t];
            switch (type)
            {
                case DESC:
                    color = new Color(input.readInt());
                    name = input.readUTF();
                    break;
                case NAME:
                    name = input.readUTF();
                    break;
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void onClientPacket(World world, EntityPlayer player)
    {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileChest && type != null)
        {
            switch (type)
            {
                case DESC:
                    ((TileChest) tile).customName = name;
                    break;
                case NAME:
                    ((TileChest) tile).customName = name;
                    break;
            }
        }
    }

    public void onServerPacket(World world, EntityPlayerMP player)
    {
        //Nothing happens server side :p
    }

    public enum ChestPacketType
    {
        DESC,
        NAME
    }
}

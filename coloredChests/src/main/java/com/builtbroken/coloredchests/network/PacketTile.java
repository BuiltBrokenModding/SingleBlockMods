package com.builtbroken.coloredchests.network;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by Dark on 7/27/2015.
 */
public class PacketTile extends PacketBase
{
    public int x;
    public int y;
    public int z;

    public PacketTile()
    {

    }

    public PacketTile(TileEntity tile)
    {
        x = tile.xCoord;
        y = tile.yCoord;
        z = tile.zCoord;
    }

    @Override
    public void encode(ByteArrayDataOutput output)
    {
        output.writeInt(x);
        output.writeInt(y);
        output.writeInt(z);
    }

    @Override
    public void decode(ByteArrayDataInput input)
    {
        x = input.readInt();
        y = input.readInt();
        z = input.readInt();
    }

    @SideOnly(Side.CLIENT)
    public void onClientPacket(World world, EntityPlayer player)
    {

    }

    public void onServerPacket(World world, EntityPlayerMP player)
    {

    }
}

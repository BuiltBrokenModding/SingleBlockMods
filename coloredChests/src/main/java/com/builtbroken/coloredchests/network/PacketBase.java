package com.builtbroken.coloredchests.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PacketBase
{
    public void encode(ByteArrayDataOutput output)
    {

    }

    public void decode(ByteArrayDataInput input)
    {

    }

    @SideOnly(Side.CLIENT)
    public void onClientPacket(World world, EntityPlayer player)
    {

    }

    public void onServerPacket(World world, EntityPlayerMP player)
    {

    }
}
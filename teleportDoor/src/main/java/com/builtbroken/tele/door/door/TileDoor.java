package com.builtbroken.tele.door.door;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by Dark on 8/4/2015.
 */
public class TileDoor extends TileEntity
{
    public ForgeDirection destFacing;
    public boolean relativeDest = true;
    public int destX;
    public int destY;
    public int destZ;

}

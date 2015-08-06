package com.builtbroken.tele.door;

import com.builtbroken.tele.door.door.BlockDoor;
import com.builtbroken.tele.door.door.ItemBlockTeleDoor;
import com.builtbroken.tele.door.door.TileDoor;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Dark on 7/25/2015.
 */
@Mod(modid = TeleDoor.DOMAIN, name = "Teleporting Door", version = "@MAJOR@.@MINOR@.@REVIS@.@BUILD@")
public class TeleDoor
{
    public static final String DOMAIN = "teledoor";
    public static final String PREFIX = DOMAIN + ":";

    @SidedProxy(clientSide = "com.builtbroken.tele.door.ClientProxy", serverSide = "com.builtbroken.tele.door.CommonProxy")
    public static CommonProxy proxy;

    public static Block blockDoor;

    public static Logger LOGGER;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        LOGGER = LogManager.getLogger("TeleDoor");

        blockDoor = new BlockDoor();
        GameRegistry.registerBlock(blockDoor, ItemBlockTeleDoor.class, "tdBlockDoor");
        GameRegistry.registerTileEntity(TileDoor.class, "tdTileDoor");

        proxy.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        proxy.postInit();
    }
}

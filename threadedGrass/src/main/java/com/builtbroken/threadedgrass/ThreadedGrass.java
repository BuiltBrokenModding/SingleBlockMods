package com.builtbroken.threadedgrass;

import com.builtbroken.threadedgrass.server.ThreadBlockUpdates;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Dark on 7/25/2015.
 */
@Mod(modid = ThreadedGrass.DOMAIN, name = "Threaded Grass", version = "@MAJOR@.@MINOR@.@REVIS@.@BUILD@", acceptableRemoteVersions = "*")
public class ThreadedGrass
{
    public static final String DOMAIN = "threadedgrass";
    public static final String PREFIX = DOMAIN + ":";

    @SidedProxy(clientSide = "com.builtbroken.threadedgrass.client.ClientProxy", serverSide = "com.builtbroken.threadedgrass.server.ServerProxy")
    public static CommonProxy proxy;

    public static Logger LOGGER;

    public static ThreadBlockUpdates blockUpdateThread;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        LOGGER = LogManager.getLogger("ThreadedGrass");
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

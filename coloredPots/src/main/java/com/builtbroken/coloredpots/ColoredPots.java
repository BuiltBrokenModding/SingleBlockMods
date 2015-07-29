package com.builtbroken.coloredpots;

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
@Mod(modid = ColoredPots.DOMAIN, name = "Colored Pots", version = "@MAJOR@.@MINOR@.@REVIS@.@BUILD@")
public class ColoredPots
{
    public static final String DOMAIN = "coloredpots";
    public static final String PREFIX = DOMAIN + ":";

    @SidedProxy(clientSide = "com.builtbroken.coloredpots.ClientProxy", serverSide = "com.builtbroken.coloredpots.CommonProxy")
    public static CommonProxy proxy;

    public static Logger LOGGER;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        LOGGER = LogManager.getLogger("ColoredPots");
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

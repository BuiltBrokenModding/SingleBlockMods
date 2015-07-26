package com.builtbroken.cardboardboxes;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * Created by Dark on 7/25/2015.
 */
@Mod(modid = Cardboardboxes.DOMAIN, name = "Cardboard Boxes", version = "@MAJOR@.@MINOR@.@REVIS@.@BUILD@")
public class Cardboardboxes
{
    public static final String DOMAIN = "cardboardboxes";
    public static final String PREFIX = DOMAIN + ":";

    @SidedProxy(clientSide = "com.builtbroken.cardboardboxes.ClientProxy", serverSide = "com.builtbroken.cardboardboxes.CommonProxy")
    public static CommonProxy proxy;

    public static Logger LOGGER;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        LOGGER = LogManager.getLogger("CardboardBoxes");
        Configuration config = new Configuration(new File(event.getModConfigurationDirectory(), "bbm/Cardboard_Boxes.cfg"));
        config.load();


        config.save();
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

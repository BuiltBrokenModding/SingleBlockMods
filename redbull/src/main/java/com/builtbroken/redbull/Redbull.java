package com.builtbroken.redbull;

import com.builtbroken.redbull.item.ItemRedbull;
import com.builtbroken.redbull.potion.PotionWings;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * Created by Dark on 7/25/2015.
 */
@Mod(modid = Redbull.DOMAIN, name = "Redbull", version = "@MAJOR@.@MINOR@.@REVIS@.@BUILD@")
public class Redbull
{
    public static final String DOMAIN = "redbull";
    public static final String PREFIX = DOMAIN + ":";

    @SidedProxy(clientSide = "com.builtbroken.redbull.ClientProxy", serverSide = "com.builtbroken.redbull.CommonProxy")
    public static CommonProxy proxy;

    public static Configuration config;
    public static Logger LOGGER;

    public static Potion potionRedBull;
    public static Item itemRedbullCan;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        LOGGER = LogManager.getLogger("Redbull");
        config = new Configuration(new File(event.getModConfigurationDirectory(), "bbm/Cardboard_Boxes.cfg"));
        config.load();

        //Create items
        itemRedbullCan = new ItemRedbull();
        GameRegistry.registerItem(itemRedbullCan, "rbRedBullcan");

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
        //Create potions
        int freeID = 23;
        while (freeID < Potion.potionTypes.length)
        {
            if (Potion.potionTypes[freeID] == null)
                break;
            freeID++;
        }
        potionRedBull = new PotionWings(config.getInt("PotionID", Configuration.CATEGORY_GENERAL, freeID, 23, Potion.potionTypes.length, "Potion ID used by the potion effect"));

        config.save();
        proxy.postInit();
    }
}

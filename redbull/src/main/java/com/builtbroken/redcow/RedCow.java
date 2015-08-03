package com.builtbroken.redcow;

import com.builtbroken.redcow.item.ItemRedbull;
import com.builtbroken.redcow.potion.PotionWings;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * Created by Dark on 7/25/2015.
 */
@Mod(modid = RedCow.DOMAIN, name = "Red Cow", version = "@MAJOR@.@MINOR@.@REVIS@.@BUILD@")
public class RedCow
{
    public static final String DOMAIN = "redcow";
    public static final String PREFIX = DOMAIN + ":";

    @SidedProxy(clientSide = "com.builtbroken.redcow.ClientProxy", serverSide = "com.builtbroken.redcow.CommonProxy")
    public static CommonProxy proxy;

    public static Configuration config;
    public static Logger LOGGER;

    public static Potion potionRedBull;
    public static Item itemRedbullCan;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        LOGGER = LogManager.getLogger("RedCow");

        config = new Configuration(new File(event.getModConfigurationDirectory(), "bbm/RedCow.cfg"));
        config.load();
        //Create items
        itemRedbullCan = new ItemRedbull();
        GameRegistry.registerItem(itemRedbullCan, "rbRedCowCan");

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

        GameRegistry.addShapelessRecipe(new ItemStack(itemRedbullCan, 1, 1), new ItemStack(itemRedbullCan, 1, 0), Items.slime_ball, Items.apple, Items.blaze_powder, Items.carrot);
        GameRegistry.addShapedRecipe(new ItemStack(itemRedbullCan, 16, 0), "g", "i", "g", 'i', Items.iron_ingot, 'g', new ItemStack(Items.dye, 1, 8));

        proxy.postInit();
        config.save();
    }
}

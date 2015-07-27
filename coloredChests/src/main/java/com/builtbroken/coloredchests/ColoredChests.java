package com.builtbroken.coloredchests;

import com.builtbroken.coloredchests.chests.BlockChest;
import com.builtbroken.coloredchests.chests.ItemBlockChest;
import com.builtbroken.coloredchests.chests.TileChest;
import com.builtbroken.coloredchests.network.PacketManager;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.File;

/**
 * Created by Dark on 7/25/2015.
 */
@Mod(modid = ColoredChests.DOMAIN, name = "Colored Chests", version = "@MAJOR@.@MINOR@.@REVIS@.@BUILD@")
public class ColoredChests
{
    public static final String DOMAIN = "coloredchests";
    public static final String PREFIX = DOMAIN + ":";

    @SidedProxy(clientSide = "com.builtbroken.coloredchests.ClientProxy", serverSide = "com.builtbroken.coloredchests.CommonProxy")
    public static CommonProxy proxy;

    public static Logger LOGGER;

    public static Block blockChest;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        LOGGER = LogManager.getLogger("ColoredChests");
        Configuration config = new Configuration(new File(event.getModConfigurationDirectory(), "bbm/Colored_Chests.cfg"));
        config.load();


        config.save();
        proxy.preInit();

        blockChest = new BlockChest();
        GameRegistry.registerBlock(blockChest, ItemBlockChest.class, "coloredChest");
        GameRegistry.registerTileEntity(TileChest.class, "coloredChest");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        PacketManager.init();
        proxy.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        proxy.postInit();
    }

    public static int getIntFromColor(Color color)
    {
        if(color == null)
            return getIntFromColor(Color.WHITE);
        return getIntFromColor(color.getRed(), color.getGreen(), color.getRed());
    }

    public static int getIntFromColor(int Red, int Green, int Blue)
    {
        Red = (Red << 16) & 0x00FF0000; //Shift red 16-bits and mask out other stuff
        Green = (Green << 8) & 0x0000FF00; //Shift Green 8-bits and mask out other stuff
        Blue = Blue & 0x000000FF; //Mask out anything not blue.

        return 0xFF000000 | Red | Green | Blue; //0xFF000000 for 100% Alpha. Bitwise OR everything together.
    }
}

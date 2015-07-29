package com.builtbroken.cardboardboxes;

import com.builtbroken.cardboardboxes.box.BlockBox;
import com.builtbroken.cardboardboxes.box.ItemBlockBox;
import com.builtbroken.cardboardboxes.box.TileBox;
import com.builtbroken.cardboardboxes.handler.HandlerManager;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.*;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;

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

    public static Configuration config;
    public static Logger LOGGER;

    public static Block blockBox;

    public static HandlerManager boxHandler;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        LOGGER = LogManager.getLogger("CardboardBoxes");
        boxHandler = new HandlerManager();
        config = new Configuration(new File(event.getModConfigurationDirectory(), "bbm/Cardboard_Boxes.cfg"));
        config.load();

        //Create block
        blockBox = new BlockBox();
        GameRegistry.registerBlock(blockBox, ItemBlockBox.class, "cbCardboardBox");
        GameRegistry.registerTileEntity(TileBox.class, "cbCardboardBox");
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
        if (config.getBoolean("BlackListMobSpawners", "BlackListSettings", true, "Prevents mobs spawners from being placed into cardboard boxes"))
        {
            boxHandler.banBlock(Blocks.mob_spawner);
            boxHandler.banTile(TileEntityMobSpawner.class);
        }

        //These tiles are banned as there is no point in using a box on them
        boxHandler.banBlock(Blocks.beacon);
        boxHandler.banTile(TileEntityBeacon.class);
        boxHandler.banBlock(Blocks.piston);
        boxHandler.banBlock(Blocks.piston_extension);
        boxHandler.banBlock(Blocks.piston_head);
        boxHandler.banBlock(Blocks.sticky_piston);
        boxHandler.banTile(TileEntityPiston.class);
        boxHandler.banBlock(Blocks.daylight_detector);
        boxHandler.banTile(TileEntityDaylightDetector.class);
        boxHandler.banBlock(Blocks.ender_chest);
        boxHandler.banTile(TileEntityEnderChest.class);
        boxHandler.banBlock(Blocks.powered_comparator);
        boxHandler.banBlock(Blocks.unpowered_comparator);
        boxHandler.banTile(TileEntityComparator.class);
        boxHandler.banBlock(Blocks.command_block);
        boxHandler.banTile(TileEntityCommandBlock.class);
        boxHandler.banBlock(Blocks.end_portal);
        boxHandler.banBlock(Blocks.end_portal_frame);
        boxHandler.banTile(TileEntityEndPortal.class);
        boxHandler.banBlock(Blocks.noteblock);
        boxHandler.banTile(TileEntityNote.class);
        boxHandler.banBlock(Blocks.enchanting_table);
        boxHandler.banTile(TileEntityEnchantmentTable.class);
        boxHandler.banBlock(Blocks.standing_sign);
        boxHandler.banBlock(Blocks.wall_sign);
        boxHandler.banTile(TileEntitySign.class);
        boxHandler.banBlock(Blocks.skull);
        boxHandler.banTile(TileEntitySkull.class);
        boxHandler.banBlock(Blocks.cauldron);
        boxHandler.banBlock(Blocks.flower_pot);
        boxHandler.banTile(TileEntityFlowerPot.class);

        try
        {
            Field field = null;
            try
            {
                field = TileEntity.class.getDeclaredField("field_145855_i");
            } catch (NoSuchFieldException e)
            {
                field = TileEntity.class.getDeclaredField("nameToClassMap");
            }
            field.setAccessible(true);
            Map map = (Map) field.get(null);
            //TODO see if we can sort the files by mod to help users find what they are looking for
            for (Object entry : map.entrySet())
            {
                try
                {
                    if (entry instanceof Map.Entry)
                    {
                        Class<? extends TileEntity> clazz = (Class) ((Map.Entry) entry).getValue();
                        String name = (String) ((Map.Entry) entry).getKey();
                        if (name != null && !name.isEmpty())
                        {
                            if (clazz != null && !boxHandler.blackListedTiles.contains(clazz))
                            {
                                if (config.getBoolean("" + name, "BlackListTilesByName", false, "Prevents the cardboard box from picking up this tile"))
                                {
                                    boxHandler.banTile(clazz);
                                }
                            }
                        }
                    }
                } catch (Exception e)
                {
                    LOGGER.error("Failed to add entry to config " + entry);
                    e.printStackTrace();
                }

            }
        } catch (NoSuchFieldException e)
        {
            LOGGER.error("Failed to find the tile map field");
            e.printStackTrace();
        } catch (IllegalAccessException e)
        {
            LOGGER.error("Failed to access tile map");
            e.printStackTrace();
        } catch (Exception e)
        {
            LOGGER.error("Failed to add tile map to config");
            e.printStackTrace();
        }


        config.save();

        proxy.postInit();
    }
}

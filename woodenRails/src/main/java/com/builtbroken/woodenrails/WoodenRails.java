package com.builtbroken.woodenrails;

import com.builtbroken.woodenrails.cart.ColoredChestCartRecipe;
import com.builtbroken.woodenrails.cart.EntityWoodenCart;
import com.builtbroken.woodenrails.cart.EnumCartTypes;
import com.builtbroken.woodenrails.cart.ItemWoodenCart;
import com.builtbroken.woodenrails.rail.BlockWoodrails;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.File;

/**
 * Created by Dark on 7/25/2015.
 */
@Mod(modid = WoodenRails.DOMAIN, name = "Wooden Rails", version = "@MAJOR@.@MINOR@.@REVIS@.@BUILD@")
public class WoodenRails
{
    public int ENTITY_ID_PREFIX = 56;
    public static final String DOMAIN = "woodenrails";
    public static final String PREFIX = DOMAIN + ":";

    @SidedProxy(clientSide = "com.builtbroken.woodenrails.ClientProxy", serverSide = "com.builtbroken.woodenrails.CommonProxy")
    public static CommonProxy proxy;

    public static Logger LOGGER;

    public static Item itemWoodCart;
    public static Block blockRail;

    @Mod.Instance(DOMAIN)
    public static WoodenRails INSTANCE;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        LOGGER = LogManager.getLogger("WoodenRails");
        NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);
        Configuration config = new Configuration(new File(event.getModConfigurationDirectory(), "bbm/Wooden_Rails.cfg"));
        config.load();
        if (config.getBoolean("EnableCart", Configuration.CATEGORY_GENERAL, true, "Allows disabling the wooden cart item and entity"))
        {
            itemWoodCart = new ItemWoodenCart();
            GameRegistry.registerItem(itemWoodCart, "wrWoodenCart", DOMAIN);

            EntityRegistry.registerGlobalEntityID(EntityWoodenCart.class, "wrEmptyCart", EntityRegistry.findGlobalUniqueEntityId());
            EntityRegistry.registerModEntity(EntityWoodenCart.class, "wrEmptyCart", config.getInt("EmptyCart", "EntityIDs", ENTITY_ID_PREFIX, 0, 10000, "Entity ID used for the empty wooden cart, max ID is unknown so keep it low"), this, 64, 1, true);
        }
        if (config.getBoolean("EnableRail", Configuration.CATEGORY_GENERAL, true, "Allows disabling the wooden rail item and block"))
        {
            blockRail = new BlockWoodrails();
            GameRegistry.registerBlock(blockRail, "wrWoodenRail");
        }
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
        if (itemWoodCart != null)
        {
            //TODO ensure/add ore dictionary support
            GameRegistry.addShapedRecipe(new ItemStack(itemWoodCart), "psp", " b ", "psp", 'b', Items.boat, 's', Items.stick, 'p', Blocks.planks);
            GameRegistry.addShapedRecipe(new ItemStack(itemWoodCart, 1, EnumCartTypes.FURNACE.ordinal()), "f", "c", 'f', Blocks.furnace, 'c', new ItemStack(itemWoodCart));
            GameRegistry.addShapedRecipe(new ItemStack(itemWoodCart, 1, EnumCartTypes.CHEST.ordinal()), "f", "c", 'f', Blocks.chest, 'c', new ItemStack(itemWoodCart));
            GameRegistry.addShapedRecipe(new ItemStack(itemWoodCart, 1, EnumCartTypes.HOPPER.ordinal()), "f", "c", 'f', Blocks.hopper, 'c', new ItemStack(itemWoodCart));
            GameRegistry.addShapedRecipe(new ItemStack(itemWoodCart, 1, EnumCartTypes.TNT.ordinal()), "f", "c", 'f', Blocks.tnt, 'c', new ItemStack(itemWoodCart));
            GameRegistry.addShapedRecipe(new ItemStack(itemWoodCart, 1, EnumCartTypes.WORKTABLE.ordinal()), "f", "c", 'f', Blocks.crafting_table, 'c', new ItemStack(itemWoodCart));
            if (Loader.isModLoaded("coloredchests"))
            {
                try
                {
                    Block blockChest = (Block) Block.blockRegistry.getObject("coloredchests:coloredChest");
                    if (blockChest != null)
                        GameRegistry.addRecipe(new ColoredChestCartRecipe(blockChest));
                } catch (Exception e)
                {
                    LOGGER.error("Failed to load Colored Chest support");
                    ((ItemWoodenCart) itemWoodCart).enableColoredChestSupport = false;
                    e.printStackTrace();
                }
            }
        }
        if (blockRail != null)
        {
            GameRegistry.addShapedRecipe(new ItemStack(blockRail, 16, 0), "ptp", "psp", "ptp", 's', Items.stick, 'p', Blocks.planks, 't', Blocks.sapling);
        }
        proxy.postInit();
    }

    public static Color getColor(int rgb)
    {
        return new Color((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF);
    }

    public static int getRGB(Color color)
    {
        int rgb = color.getRed();
        rgb = (rgb << 8) + color.getGreen();
        rgb = (rgb << 8) + color.getBlue();
        return rgb;
    }

    public static boolean doColorsMatch(Color a, Color b)
    {
        return a == b || a != null && b != null && a.getRGB() == b.getRGB();
    }
}

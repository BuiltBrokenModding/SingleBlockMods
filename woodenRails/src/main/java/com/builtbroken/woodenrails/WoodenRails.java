package com.builtbroken.woodenrails;

import com.builtbroken.woodenrails.rail.BlockWoodrails;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import com.builtbroken.woodenrails.cart.EntityEmptyWoodenCart;
import com.builtbroken.woodenrails.cart.ItemWoodenCart;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        LOGGER = LogManager.getLogger("WoodenRails");
        Configuration config = new Configuration(new File(event.getModConfigurationDirectory(), "bbm/Wooden_Rails.cfg"));
        config.load();
        if (config.getBoolean("EnableCart", Configuration.CATEGORY_GENERAL, true, "Allows disabling the wooden cart item and entity"))
        {
            itemWoodCart = new ItemWoodenCart();
            GameRegistry.registerItem(itemWoodCart, "wrWoodenCart", DOMAIN);

            EntityRegistry.registerGlobalEntityID(EntityEmptyWoodenCart.class, "wrEmptyCart", EntityRegistry.findGlobalUniqueEntityId());
            EntityRegistry.registerModEntity(EntityEmptyWoodenCart.class, "wrEmptyCart", config.getInt("EmptyCart", "EntityIDs", ENTITY_ID_PREFIX, 0, 10000, "Entity ID used for the empty wooden cart, max ID is unknown so keep it low"), this, 64, 1, true);
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
            GameRegistry.addShapedRecipe(new ItemStack(itemWoodCart), "psp", "pbp", "psp", 'b', Items.boat, 's', Items.stick, 'w', Blocks.planks);
        }
        proxy.postInit();
    }
}

package com.builtbroken.woodenbucket;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * Created by Dark on 7/25/2015.
 */
@Mod(modid = WoodenBucket.DOMAIN, name = "Wooden Bucket", version = "@MAJOR@.@MINOR@.@REVIS@.@BUILD@")
public class WoodenBucket
{
    public int ENTITY_ID_PREFIX = 56;
    public static final String DOMAIN = "woodenbucket";
    public static final String PREFIX = DOMAIN + ":";

    public static Logger LOGGER;

    public static Item itemBucket;

    public static Fluid fluid_milk;

    public static boolean PREVENT_HOT_FLUID_USAGE = true;
    public static boolean DAMAGE_BUCKET_WITH_HOT_FLUID = true;
    public static boolean BURN_ENTITY_WITH_HOT_FLUID = true;
    public static boolean GENERATE_MILK_FLUID = true;


    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        LOGGER = LogManager.getLogger("WoodenBucket");
        Configuration config = new Configuration(new File(event.getModConfigurationDirectory(), "bbm/Wooden_Bucket.cfg"));
        config.load();
        PREVENT_HOT_FLUID_USAGE = config.getBoolean("PreventHotFluidUsage", "WoodenBucketUsage", PREVENT_HOT_FLUID_USAGE, "Enables settings that attempt to prevent players from wanting to use the bucket for moving hot fluids");
        DAMAGE_BUCKET_WITH_HOT_FLUID = config.getBoolean("DamageBucketWithHotFluid", "WoodenBucketUsage", DAMAGE_BUCKET_WITH_HOT_FLUID, "Will randomly destroy the bucket if it contains hot fluid, lava in other words");
        BURN_ENTITY_WITH_HOT_FLUID = config.getBoolean("BurnPlayerWithHotFluid", "WoodenBucketUsage", BURN_ENTITY_WITH_HOT_FLUID, "Will light the player on fire if the bucket contains a hot fluid, lava in other words");
        GENERATE_MILK_FLUID = config.getBoolean("EnableMilkFluidGeneration", Configuration.CATEGORY_GENERAL, GENERATE_MILK_FLUID, "Will generate a fluid for milk allowing for the bucket to be used for gathering milk from cows");

        config.save();

        itemBucket = new ItemWoodenBucket();
        GameRegistry.registerItem(itemBucket, "wbBucket", DOMAIN);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {

    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        if (GENERATE_MILK_FLUID && FluidRegistry.getFluid("milk") == null)
        {
            fluid_milk = new Fluid("milk")
            {
                @Override
                public int getColor()
                {
                    return 0xFFFFFF;
                }
            };
            FluidRegistry.registerFluid(fluid_milk);
            MinecraftForge.EVENT_BUS.register(this);
        }

        //TODO add crafting recipes for milk bucket
        GameRegistry.addShapedRecipe(new ItemStack(itemBucket), " s ", "wcw", " w ", 'w', Blocks.planks, 's', Items.stick, 'c', new ItemStack(Items.dye, 1, 2));
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void textureStichEvent(TextureStitchEvent.Pre event)
    {
        IIcon flowing = event.map.registerIcon(PREFIX + "milk_flow");
        IIcon still = event.map.registerIcon(PREFIX + "milk_still");

        fluid_milk.setFlowingIcon(flowing);
        fluid_milk.setStillIcon(still);
    }
}

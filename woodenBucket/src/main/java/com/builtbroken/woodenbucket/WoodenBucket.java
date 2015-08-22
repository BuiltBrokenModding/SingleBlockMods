package com.builtbroken.woodenbucket;

import com.builtbroken.woodenbucket.bucket.ItemWoodenBucket;
import com.builtbroken.woodenbucket.fluid.BlockMilk;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
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

    public static Configuration config;

    public static boolean PREVENT_HOT_FLUID_USAGE = true;
    public static boolean DAMAGE_BUCKET_WITH_HOT_FLUID = true;
    public static boolean BURN_ENTITY_WITH_HOT_FLUID = true;
    public static boolean GENERATE_MILK_FLUID = true;


    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        LOGGER = LogManager.getLogger("WoodenBucket");
        config = new Configuration(new File(event.getModConfigurationDirectory(), "bbm/Wooden_Bucket.cfg"));
        config.load();
        PREVENT_HOT_FLUID_USAGE = config.getBoolean("PreventHotFluidUsage", "WoodenBucketUsage", PREVENT_HOT_FLUID_USAGE, "Enables settings that attempt to prevent players from wanting to use the bucket for moving hot fluids");
        DAMAGE_BUCKET_WITH_HOT_FLUID = config.getBoolean("DamageBucketWithHotFluid", "WoodenBucketUsage", DAMAGE_BUCKET_WITH_HOT_FLUID, "Will randomly destroy the bucket if it contains hot fluid, lava in other words");
        BURN_ENTITY_WITH_HOT_FLUID = config.getBoolean("BurnPlayerWithHotFluid", "WoodenBucketUsage", BURN_ENTITY_WITH_HOT_FLUID, "Will light the player on fire if the bucket contains a hot fluid, lava in other words");
        GENERATE_MILK_FLUID = config.getBoolean("EnableMilkFluidGeneration", Configuration.CATEGORY_GENERAL, GENERATE_MILK_FLUID, "Will generate a fluid for milk allowing for the bucket to be used for gathering milk from cows");


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
            fluid_milk = new Fluid("milk");
            FluidRegistry.registerFluid(fluid_milk);
            Block blockMilk = new BlockMilk(fluid_milk);
            GameRegistry.registerBlock(blockMilk, "wbBlockMilk");
        }


        //TODO add crafting recipes for milk bucket
        // TODO add proper ore shaped recipes so modded sticks and other items can be used in the recipes
        GameRegistry.addShapedRecipe(new ItemStack(itemBucket, 1, ItemWoodenBucket.BucketTypes.OAK.ordinal()), " s ", "wcw", " w ", 'w', new ItemStack(Blocks.planks, 1, 0), 's', Items.stick, 'c', new ItemStack(Items.dye, 1, 2));
        GameRegistry.addShapedRecipe(new ItemStack(itemBucket, 1, ItemWoodenBucket.BucketTypes.SPRUCE.ordinal()), " s ", "wcw", " w ", 'w', new ItemStack(Blocks.planks, 1, 1), 's', Items.stick, 'c', new ItemStack(Items.dye, 1, 2));
        GameRegistry.addShapedRecipe(new ItemStack(itemBucket, 1, ItemWoodenBucket.BucketTypes.BIRCH.ordinal()), " s ", "wcw", " w ", 'w', new ItemStack(Blocks.planks, 1, 2), 's', Items.stick, 'c', new ItemStack(Items.dye, 1, 2));
        GameRegistry.addShapedRecipe(new ItemStack(itemBucket, 1, ItemWoodenBucket.BucketTypes.JUNGLE.ordinal()), " s ", "wcw", " w ", 'w', new ItemStack(Blocks.planks, 1, 3), 's', Items.stick, 'c', new ItemStack(Items.dye, 1, 2));
        GameRegistry.addShapedRecipe(new ItemStack(itemBucket, 1, ItemWoodenBucket.BucketTypes.ACACIA.ordinal()), " s ", "wcw", " w ", 'w', new ItemStack(Blocks.planks, 1, 4), 's', Items.stick, 'c', new ItemStack(Items.dye, 1, 2));
        GameRegistry.addShapedRecipe(new ItemStack(itemBucket, 1, ItemWoodenBucket.BucketTypes.BIG_OAK.ordinal()), " s ", "wcw", " w ", 'w', new ItemStack(Blocks.planks, 1, 5), 's', Items.stick, 'c', new ItemStack(Items.dye, 1, 2));
        for (ItemStack itemstack : OreDictionary.getOres("planks"))
        {
            if (itemstack != null && itemstack.getItem() != Item.getItemFromBlock(Blocks.planks))
            {
                GameRegistry.addShapedRecipe(new ItemStack(itemBucket, 1, ItemWoodenBucket.BucketTypes.OAK.ordinal()), " s ", "wcw", " w ", 'w', itemstack, 's', Items.stick, 'c', new ItemStack(Items.dye, 1, 2));
            }
        }

        //TODO add pam's harvest craft support
        if (Loader.isModLoaded("harvestcraft"))
        {
            //
            if (config.getBoolean("EnableRegisteringMilkBucket", "PamHarvestCraftSupport", true, "Registers the milk bucket to the ore dictionary to be used in Pam's Harvest Craft recipes"))

                if (FluidRegistry.getFluid("milk") != null)
                {
                    Item itemFreshMilk = (Item) Item.itemRegistry.getObject("harvestcraft:freshmilkItem");
                    if (itemFreshMilk == null)
                        LOGGER.error("Failed to find item harvestcraft:freshmilkItem");

                    FluidStack milkFluidStack = new FluidStack(FluidRegistry.getFluid("milk"), FluidContainerRegistry.BUCKET_VOLUME);
                    for (ItemWoodenBucket.BucketTypes type : ItemWoodenBucket.BucketTypes.values())
                    {
                        ItemStack milkBucket = new ItemStack(itemBucket, 1, type.ordinal());
                        ((ItemWoodenBucket) itemBucket).fill(milkBucket, milkFluidStack, true);
                        OreDictionary.registerOre("listAllmilk", milkBucket);

                        try
                        {
                            GameRegistry.addShapedRecipe(new ItemStack(itemFreshMilk, 4, 0), "   ", " b ", "   ", 'b', milkBucket);
                        } catch (Exception e)
                        {
                            LOGGER.error("Failed to generate recipe for pam's fresh milk item for " + milkBucket);
                            e.printStackTrace();
                        }
                    }
                }
        }

        if(config.getBoolean("listAllWater", "OreDictionary", true, "Lists all water buckets under the ore dictionary name listAllWater"))
        {
            FluidStack waterFluidStack = new FluidStack(FluidRegistry.WATER, FluidContainerRegistry.BUCKET_VOLUME);
            for (ItemWoodenBucket.BucketTypes type : ItemWoodenBucket.BucketTypes.values())
            {
                ItemStack waterBucket = new ItemStack(itemBucket, 1, type.ordinal());
                ((ItemWoodenBucket) itemBucket).fill(waterBucket, waterFluidStack, true);
                OreDictionary.registerOre("listAllWater", waterBucket);
            }
        }
        config.save();
    }
}

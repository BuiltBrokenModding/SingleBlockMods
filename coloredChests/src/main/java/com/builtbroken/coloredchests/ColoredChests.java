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
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;

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
        blockChest = new BlockChest();
        GameRegistry.registerBlock(blockChest, ItemBlockChest.class, "coloredChest");
        GameRegistry.registerTileEntity(TileChest.class, "coloredChest");
        proxy.preInit();
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
        //TODO add ore dictionary support
        for (int i = 0; i < ItemDye.field_150922_c.length; i++)
        {
            ItemStack stack = new ItemStack(blockChest);
            stack.setTagCompound(new NBTTagCompound());
            stack.getTagCompound().setInteger("rgb", ItemDye.field_150922_c[i]);
            stack.getTagCompound().setString("colorName", ItemDye.field_150921_b[i]);

            GameRegistry.addShapedRecipe(stack, "d", "c", 'd', new ItemStack(Items.dye, 1, i), 'c', Blocks.chest);
            for (int b = 0; b < ItemDye.field_150922_c.length; b++)
            {
                if (b != i)
                {
                    ItemStack stack2 = new ItemStack(blockChest);
                    stack2.setTagCompound(new NBTTagCompound());
                    stack2.getTagCompound().setInteger("rgb", ItemDye.field_150922_c[b]);
                    stack2.getTagCompound().setString("colorName", ItemDye.field_150921_b[b]);
                    GameRegistry.addShapedRecipe(stack, "d", "c", 'd', new ItemStack(Items.dye, 1, i), 'c', stack2);
                }
            }
        }
        GameRegistry.addShapelessRecipe(new ItemStack(Blocks.chest), blockChest);
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

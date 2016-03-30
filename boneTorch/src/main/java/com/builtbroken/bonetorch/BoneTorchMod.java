package com.builtbroken.bonetorch;

import com.builtbroken.bonetorch.torch.BlockBoneTorch;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

/**
 * Simple mod to add a bone torch to MC. Idea was spawned from running out of
 * torches in the nether. Where the play may still be able to get coal and bones
 * from wither skeletons.
 * Created by Dark on 7/25/2015.
 */
@Mod(modid = BoneTorchMod.DOMAIN, name = "Bone Torch Mod", version = "@MAJOR@.@MINOR@.@REVIS@.@BUILD@")
public class BoneTorchMod
{
    public static final String DOMAIN = "bonetorch";
    public static final String PREFIX = DOMAIN + ":";

    Block blockTorch;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        blockTorch = new BlockBoneTorch();
        //TODO add bone sound type
        //GameRegistry.registerBlock(blockTorch, BlockBoneTorch.class, "BTBoneTorch");
        GameRegistry.registerBlock(blockTorch, String.valueOf(BlockBoneTorch.class));

    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockTorch, 4, 0), "c", "s", 'c', Items.coal, 's', Items.bone));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockTorch, 4, 0), "c", "s", 'c', new ItemStack(Items.coal, 1, 1), 's', Items.bone));
    }
}

package com.builtbroken.threadedgrass;

import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.RegistryDelegate;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.RegistryNamespaced;
import net.minecraft.world.World;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

/**
 * Created by Dark on 7/25/2015.
 */
public class CommonProxy implements IGuiHandler
{

    public void preInit()
    {
        ThreadedGrass.LOGGER.info("Replacing Minecraft's grass block to allow Multi-threading to function easier");
        Block grass = new BlockGrass2();
        try
        {
            //Set delegate name
            Item item = Item.getItemFromBlock(Blocks.grass);
            if (item instanceof ItemBlock)
            {
                int m = -1;
                Field field = getField(ItemBlock.class, "field_150939_a", "field_150939_a");
                //Set the field to public
                field.setAccessible(true);

                //Removed final modifier
                if (Modifier.isFinal(field.getModifiers()))
                    m = removeFinalFromField(field);

                //Sets field value
                field.set(item, grass);

                //Restores final modifier
                if (m != -1)
                    setModifiers(field, m);
            }

            Method method = RegistryDelegate.Delegate.class.getDeclaredMethod("setName", String.class);
            method.setAccessible(true);
            method.invoke(grass.delegate, "minecraft:grass");

            //Block.blockRegistry.addObject(2, "grass", new BlockGrass2());
            Field field = getDeclaredField(RegistryNamespaced.class, "field_148759_a", "underlyingIntegerMap");
            field.setAccessible(true);
            ((ObjectIntIdentityMap) field.get(Block.blockRegistry)).func_148746_a(grass, 2);

            //Add block to block map
            field = getDeclaredField(RegistryNamespaced.class, "field_148758_b", "field_148758_b");
            field.setAccessible(true);
            ((Map) field.get(Block.blockRegistry)).remove(Blocks.grass);
            ((Map) field.get(Block.blockRegistry)).put(grass, "minecraft:grass");
            //Block.blockRegistry.putObject("minecraft:grass", grass);

            //Set grass block in Blocks singleton
            int m = -1;
            field = getField(Blocks.class, "field_150349_c", "grass");
            //Set the field to public
            field.setAccessible(true);

            //Removed final modifier
            if (Modifier.isFinal(field.getModifiers()))
                m = removeFinalFromField(field);

            //Sets field value
            field.set(null, (BlockGrass2) grass);

            //Restores final modifier
            if (m != -1)
                setModifiers(field, m);

        } catch (Exception e)
        {
            throw new RuntimeException("Crashing game to prevent world corruption. Error: Failed to replace grass block", e);
        }
        ThreadedGrass.LOGGER.info("...Done");
    }

    public static Field getField(Class clazz, String fieldName, String fieldName2)
    {
        Field field = null;
        try
        {
            field = clazz.getField(fieldName);
        } catch (NoSuchFieldException e)
        {
            System.out.println("Could not find field by name " + fieldName);
            try
            {
                field = clazz.getField(fieldName2);
            } catch (NoSuchFieldException e2)
            {
                System.out.println("Could not find field by name " + fieldName2);
                for (Field f : clazz.getFields())
                {
                    System.out.println("  Field > " + f.getName());
                }
                for (Field f : clazz.getDeclaredFields())
                {
                    System.out.println("  FieldD > " + f.getName());
                }
            }
        }
        return field;
    }

    public static Field getDeclaredField(Class clazz, String fieldName, String fieldName2)
    {
        Field field = null;
        try
        {
            field = clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e)
        {
            System.out.println("Could not find declared field by name " + fieldName);
            try
            {
                field = clazz.getDeclaredField(fieldName2);
            } catch (NoSuchFieldException e2)
            {
                System.out.println("Could not find declared field by name " + fieldName2);
                for (Field f : clazz.getFields())
                {
                    System.out.println("  Field > " + f.getName());
                }
                for (Field f : clazz.getDeclaredFields())
                {
                    System.out.println("  FieldD > " + f.getName());
                }
            }
        }
        return field;
    }

    public static int removeFinalFromField(Field field) throws NoSuchFieldException, IllegalAccessException
    {
        return setModifiers(field, field.getModifiers() & ~Modifier.FINAL);
    }

    /**
     * Sets a fields modifiers, mainly used for removing final modifiers and then restoring it after settings the field
     *
     * @param field    - field to mess with
     * @param modifier - modifers to set
     * @return modifiers before they were changed
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public static int setModifiers(Field field, int modifier) throws NoSuchFieldException, IllegalAccessException
    {
        int m = field.getModifiers();
        //Gets the modifier field from the field
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        //set the modifier field to public so it can be accessed
        modifiersField.setAccessible(true);
        //Sets the new modifier
        modifiersField.setInt(field, modifier);
        return m;
    }

    public void init()
    {

    }

    public void postInit()
    {

    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        return null;
    }
}

package com.builtbroken.woodenrails.cart;

import com.builtbroken.woodenrails.WoodenRails;
import com.builtbroken.woodenrails.cart.types.*;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockRailBase;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.awt.*;
import java.util.List;

/**
 * Created by Dark on 7/25/2015.
 */
public class ItemWoodenCart extends Item
{
    @SideOnly(Side.CLIENT)
    public IIcon furnaceMinecraft;

    @SideOnly(Side.CLIENT)
    public IIcon hopperMinecraft;

    @SideOnly(Side.CLIENT)
    public IIcon chestMinecraft;

    @SideOnly(Side.CLIENT)
    public IIcon tntMinecraft;

    @SideOnly(Side.CLIENT)
    public IIcon workbenchMinecraft;

    @SideOnly(Side.CLIENT)
    public IIcon bcTankMinecraft;

    public boolean enableColoredChestSupport;

    public ItemWoodenCart()
    {
        this.setMaxStackSize(3);
        this.setHasSubtypes(true);
        this.setUnlocalizedName(WoodenRails.PREFIX + "WoodenCart");
        this.setCreativeTab(CreativeTabs.tabTransport);
        BlockDispenser.dispenseBehaviorRegistry.putObject(this, new DispenserMinecartBehavior());
        enableColoredChestSupport = Loader.isModLoaded("coloredchests");
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float xHit, float yHit, float zHit)
    {
        if (BlockRailBase.func_150051_a(world.getBlock(x, y, z)))
        {
            EntityWoodenCart cart = createNewCart(world, itemStack);
            if (cart != null)
            {
                if (!world.isRemote)
                {
                    cart.setPosition(x + 0.5F, y + 0.5F, z + 0.5F);

                    if (itemStack.hasDisplayName())
                        cart.setMinecartName(itemStack.getDisplayName());

                    world.spawnEntityInWorld(cart);

                    if (itemStack.getTagCompound() != null && itemStack.getTagCompound().hasKey("rgb"))
                    {
                        cart.setBlockRenderColor(itemStack.getTagCompound().getInteger("rgb"));
                    }
                }
                if (!player.capabilities.isCreativeMode)
                    --itemStack.stackSize;
                return true;
            }
        }
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b)
    {
        if (stack.getItemDamage() >= 0 && stack.getItemDamage() < EnumCartTypes.values().length)
        {
            EnumCartTypes type = EnumCartTypes.values()[stack.getItemDamage()];
            if (type == EnumCartTypes.HOPPER || type == EnumCartTypes.HOPPER || type == EnumCartTypes.WORKTABLE)
                list.add("Not implemented");
        }
        if (stack != null && stack.getTagCompound() != null)
        {
            if (stack.getTagCompound().hasKey("rgb"))
            {
                Color color = WoodenRails.getColor(stack.getTagCompound().getInteger("rgb"));
                list.add("R: " + color.getRed() + " G: " + color.getGreen() + " B: " + color.getBlue());
            }

            if (stack.getTagCompound().hasKey("colorName"))
            {
                list.add("N: " + stack.getTagCompound().getString("colorName"));
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIconFromDamageForRenderPass(int meta, int pass)
    {
        if (pass == 1 && meta >= 0 && meta < EnumCartTypes.values().length)
        {
            switch (EnumCartTypes.values()[meta])
            {
                case FURNACE:
                    return this.furnaceMinecraft;
                case CHEST:
                    return this.chestMinecraft;
                case HOPPER:
                    return this.hopperMinecraft;
                case WORKTABLE:
                    return this.workbenchMinecraft;
                case TNT:
                    return this.tntMinecraft;
                case TANK:
                    return this.bcTankMinecraft;
            }
        }
        return this.itemIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int pass)
    {
        if (pass == 1 && stack.getTagCompound() != null && stack.getTagCompound().hasKey("rgb"))
        {
            return stack.getTagCompound().getInteger("rgb");
        }
        return 16777215;
    }

    @Override
    public int getRenderPasses(int metadata)
    {
        return metadata != 0 ? 2 : 1;
    }


    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses()
    {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister reg)
    {
        this.itemIcon = reg.registerIcon(WoodenRails.PREFIX + "minecart_normal");
        this.furnaceMinecraft = reg.registerIcon(WoodenRails.PREFIX + "minecart_furnace");
        this.chestMinecraft = reg.registerIcon(WoodenRails.PREFIX + "minecart_chest");
        this.hopperMinecraft = reg.registerIcon(WoodenRails.PREFIX + "minecart_hopper");
        this.tntMinecraft = reg.registerIcon(WoodenRails.PREFIX + "minecart_tnt");
        this.bcTankMinecraft = reg.registerIcon(WoodenRails.PREFIX + "minecraft_bc_tank");
        this.workbenchMinecraft = reg.registerIcon(WoodenRails.PREFIX + "minecraft_workbench");
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack)
    {
        EnumCartTypes type = EnumCartTypes.EMPTY;
        if (itemStack.getItemDamage() >= 0 && itemStack.getItemDamage() < EnumCartTypes.values().length)
        {
            type = EnumCartTypes.values()[itemStack.getItemDamage()];
        }
        return getUnlocalizedName() + "." + type.name().toLowerCase();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(Item item, CreativeTabs tab, List items)
    {
        for (EnumCartTypes type : EnumCartTypes.values())
        {
            items.add(new ItemStack(item, 1, type.ordinal()));
            if (type == EnumCartTypes.CHEST && enableColoredChestSupport)
            {
                for (int i = 0; i < ItemDye.field_150922_c.length; i++)
                {
                    ItemStack stack = new ItemStack(item, 1, type.ordinal());
                    stack.setTagCompound(new NBTTagCompound());
                    stack.getTagCompound().setInteger("rgb", ItemDye.field_150922_c[i]);
                    stack.getTagCompound().setString("colorName", ItemDye.field_150921_b[i]);
                    items.add(stack);
                }
            }
        }
    }

    public static EntityWoodenCart createNewCart(World world, ItemStack itemStack)
    {
        if (itemStack.getItemDamage() >= 0 && itemStack.getItemDamage() < EnumCartTypes.values().length)
        {
            switch (EnumCartTypes.values()[itemStack.getItemDamage()])
            {
                case CHEST:
                    return new EntityChestCart(world);
                case TNT:
                    return new EntityTNTCart(world);
                case FURNACE:
                    return new EntityPoweredCart(world);
                case HOPPER:
                    return new EntityHopperCart(world);
                case WORKTABLE:
                    return new EntityWorkbenchCart(world);
                case TANK:
                    return new EntityTankCart(world);
            }
        }
        return new EntityEmptyCart(world);
    }
}

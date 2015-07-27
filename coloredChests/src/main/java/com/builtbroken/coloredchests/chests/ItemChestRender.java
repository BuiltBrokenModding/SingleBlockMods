package com.builtbroken.coloredchests.chests;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.awt.*;

/**
 * Created by Dark on 7/27/2015.
 */
public class ItemChestRender implements IItemRenderer
{
    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type)
    {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
    {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data)
    {
        //TODO fix entity render rotation point
        //line 8005 in RenderBlocks
        GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        //GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        GL11.glScalef(1.0F, -1.0F, -1.0F);
        RenderChest.modelChest.chestLid.rotateAngleX = 0;
        if (item.getTagCompound() != null && item.getTagCompound().hasKey("rgb"))
        {
            Color color = new Color(item.getTagCompound().getInteger("rgb"));
            GL11.glColor4f(color.getRed(), color.getGreen(), color.getBlue(), 1.0f);
        }
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(RenderChest.textureChest);
        RenderChest.modelChest.renderAll();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
    }
}

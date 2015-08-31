package com.builtbroken.filteredchests.chest;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

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

        FMLClientHandler.instance().getClient().renderEngine.bindTexture(RenderChest.textureChest);
        RenderChest.modelChest.renderAll();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
    }
}

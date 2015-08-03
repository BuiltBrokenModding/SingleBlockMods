package com.builtbroken.coloredchests.chests;

import com.builtbroken.coloredchests.ColoredChests;
import net.minecraft.block.Block;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.model.ModelLargeChest;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

/**
 * Created by Dark on 7/26/2015.
 */
public class RenderChest extends TileEntitySpecialRenderer
{
    public static final ResourceLocation textureDoubleChest = new ResourceLocation(ColoredChests.DOMAIN, "textures/entity/chest/grey_scale_double.png");
    public static final ResourceLocation textureChest = new ResourceLocation(ColoredChests.DOMAIN, "textures/entity/chest/grey_scale.png");

    public static ModelChest modelChest = new ModelChest();
    public static ModelChest modelLargeChest = new ModelLargeChest();

    public void renderTileEntityAt(TileChest tile, double xx, double yy, double zz, float deltaTime)
    {
        int i;

        if (!tile.hasWorldObj())
        {
            i = 0;
        }
        else
        {
            Block block = tile.getBlockType();
            i = tile.getWorldObj().getBlockMetadata(tile.xCoord, tile.yCoord, tile.zCoord);

            if (block instanceof BlockChest && i == 0)
            {
                ((BlockChest) block).attemptToConnectToChest(tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord);
                i = tile.getWorldObj().getBlockMetadata(tile.xCoord, tile.yCoord, tile.zCoord);
            }

            tile.checkForAdjacentChests();
        }

        if (tile.adjacentChestZNeg == null && tile.adjacentChestXNeg == null)
        {
            ModelChest modelchest;

            if (tile.adjacentChestXPos == null && tile.adjacentChestZPos == null)
            {
                modelchest = this.modelChest;
                this.bindTexture(textureChest);
            }
            else
            {
                modelchest = this.modelLargeChest;
                this.bindTexture(textureDoubleChest);
            }

            GL11.glPushMatrix();
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            if (tile.color != null)
            {
                float r = (float)tile.color.getRed() / 255f;
                float g = (float)tile.color.getGreen() / 255f;
                float b = (float)tile.color.getBlue() / 255f;
                GL11.glColor3f(r, g, b);
            }
            GL11.glTranslatef((float) xx, (float) yy + 1.0F, (float) zz + 1.0F);
            GL11.glScalef(1.0F, -1.0F, -1.0F);
            GL11.glTranslatef(0.5F, 0.5F, 0.5F);
            short short1 = 0;

            if (i == 2)
            {
                short1 = 180;
            }

            if (i == 3)
            {
                short1 = 0;
            }

            if (i == 4)
            {
                short1 = 90;
            }

            if (i == 5)
            {
                short1 = -90;
            }

            if (i == 2 && tile.adjacentChestXPos != null)
            {
                GL11.glTranslatef(1.0F, 0.0F, 0.0F);
            }

            if (i == 5 && tile.adjacentChestZPos != null)
            {
                GL11.glTranslatef(0.0F, 0.0F, -1.0F);
            }

            GL11.glRotatef((float) short1, 0.0F, 1.0F, 0.0F);
            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
            float f1 = tile.prevLidAngle + (tile.lidAngle - tile.prevLidAngle) * deltaTime;
            float f2;

            if (tile.adjacentChestZNeg != null)
            {
                f2 = tile.adjacentChestZNeg.prevLidAngle + (tile.adjacentChestZNeg.lidAngle - tile.adjacentChestZNeg.prevLidAngle) * deltaTime;

                if (f2 > f1)
                {
                    f1 = f2;
                }
            }

            if (tile.adjacentChestXNeg != null)
            {
                f2 = tile.adjacentChestXNeg.prevLidAngle + (tile.adjacentChestXNeg.lidAngle - tile.adjacentChestXNeg.prevLidAngle) * deltaTime;

                if (f2 > f1)
                {
                    f1 = f2;
                }
            }

            f1 = 1.0F - f1;
            f1 = 1.0F - f1 * f1 * f1;
            modelchest.chestLid.rotateAngleX = -(f1 * (float) Math.PI / 2.0F);
            modelchest.renderAll();
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            GL11.glPopMatrix();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @Override
    public void renderTileEntityAt(TileEntity tile, double xx, double yy, double zz, float deltaTime)
    {
        this.renderTileEntityAt((TileChest) tile, xx, yy, zz, deltaTime);
    }
}

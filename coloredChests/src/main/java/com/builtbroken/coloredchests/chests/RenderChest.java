package com.builtbroken.coloredchests.chests;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.block.Block;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.model.ModelLargeChest;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.Calendar;

/**
 * Created by Dark on 7/26/2015.
 */
public class RenderChest extends TileEntitySpecialRenderer
{
    private static final ResourceLocation field_147507_b = new ResourceLocation("textures/entity/chest/trapped_double.png");
    private static final ResourceLocation field_147508_c = new ResourceLocation("textures/entity/chest/christmas_double.png");
    private static final ResourceLocation field_147505_d = new ResourceLocation("textures/entity/chest/normal_double.png");
    private static final ResourceLocation field_147506_e = new ResourceLocation("textures/entity/chest/trapped.png");
    private static final ResourceLocation field_147503_f = new ResourceLocation("textures/entity/chest/christmas.png");
    private static final ResourceLocation field_147504_g = new ResourceLocation("textures/entity/chest/normal.png");
    private ModelChest field_147510_h = new ModelChest();
    private ModelChest field_147511_i = new ModelLargeChest();
    private boolean field_147509_j;

    public RenderChest()
    {
        Calendar calendar = Calendar.getInstance();

        if (calendar.get(2) + 1 == 12 && calendar.get(5) >= 24 && calendar.get(5) <= 26)
        {
            this.field_147509_j = true;
        }
    }

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
            i = tile.getBlockMetadata();

            if (block instanceof BlockChest && i == 0)
            {
                try
                {
                    ((BlockChest)block).func_149954_e(tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord);
                }
                catch (ClassCastException e)
                {
                    FMLLog.severe("Attempted to render a chest at %d,  %d, %d that was not a chest", tile.xCoord, tile.yCoord, tile.zCoord);
                }
                i = tile.getBlockMetadata();
            }

            tile.checkForAdjacentChests();
        }

        if (tile.adjacentChestZNeg == null && tile.adjacentChestXNeg == null)
        {
            ModelChest modelchest;

            if (tile.adjacentChestXPos == null && tile.adjacentChestZPos == null)
            {
                modelchest = this.field_147510_h;

                if (tile.func_145980_j() == 1)
                {
                    this.bindTexture(field_147506_e);
                }
                else if (this.field_147509_j)
                {
                    this.bindTexture(field_147503_f);
                }
                else
                {
                    this.bindTexture(field_147504_g);
                }
            }
            else
            {
                modelchest = this.field_147511_i;

                if (tile.func_145980_j() == 1)
                {
                    this.bindTexture(field_147507_b);
                }
                else if (this.field_147509_j)
                {
                    this.bindTexture(field_147508_c);
                }
                else
                {
                    this.bindTexture(field_147505_d);
                }
            }

            GL11.glPushMatrix();
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glTranslatef((float)xx, (float)yy + 1.0F, (float)zz + 1.0F);
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

            GL11.glRotatef((float)short1, 0.0F, 1.0F, 0.0F);
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
            modelchest.chestLid.rotateAngleX = -(f1 * (float)Math.PI / 2.0F);
            modelchest.renderAll();
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            GL11.glPopMatrix();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @Override
    public void renderTileEntityAt(TileEntity tile, double xx, double yy, double zz, float deltaTime)
    {
        this.renderTileEntityAt((TileChest)tile, xx, yy, zz, deltaTime);
    }
}

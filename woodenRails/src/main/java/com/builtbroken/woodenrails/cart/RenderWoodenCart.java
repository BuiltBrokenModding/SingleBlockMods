package com.builtbroken.woodenrails.cart;

import com.builtbroken.woodenrails.WoodenRails;
import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.block.Block;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.model.ModelMinecart;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.awt.*;

/**
 * Created by Dark on 7/25/2015.
 */
public class RenderWoodenCart extends Render
{
    private static final ResourceLocation minecartTextures = new ResourceLocation(WoodenRails.DOMAIN, "textures/entity/minecart.png");
    public static final ResourceLocation textureChest = new ResourceLocation("coloredchests", "textures/entity/chest/grey_scale.png");

    public static ModelChest modelChest = new ModelChest();

    /** instance of ModelMinecart for rendering */
    protected ModelBase modelMinecart = new ModelMinecart();
    protected final RenderBlocks renderBlocks;

    public RenderWoodenCart()
    {
        this.shadowSize = 0.5F;
        this.renderBlocks = new RenderBlocks();
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity p_110775_1_)
    {
        return minecartTextures;
    }

    @Override
    public void doRender(Entity p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_)
    {
        this.doRender((EntityWoodenCart) p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
    }

    public void doRender(EntityWoodenCart entityMinecart, double xx, double yy, double zz, float p_76986_8_, float p_76986_9_)
    {
        GL11.glPushMatrix();
        //this.bindEntityTexture(entityMinecart);
        long i = (long) entityMinecart.getEntityId() * 493286711L;
        i = i * i * 4392167121L + i * 98761L;
        float f2 = (((float) (i >> 16 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        float f3 = (((float) (i >> 20 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        float f4 = (((float) (i >> 24 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        GL11.glTranslatef(f2, f3, f4);

        double d3 = entityMinecart.lastTickPosX + (entityMinecart.posX - entityMinecart.lastTickPosX) * (double) p_76986_9_;
        double d4 = entityMinecart.lastTickPosY + (entityMinecart.posY - entityMinecart.lastTickPosY) * (double) p_76986_9_;
        double d5 = entityMinecart.lastTickPosZ + (entityMinecart.posZ - entityMinecart.lastTickPosZ) * (double) p_76986_9_;
        double d6 = 0.30000001192092896D;

        Vec3 vec3 = entityMinecart.func_70489_a(d3, d4, d5);
        float f5 = entityMinecart.prevRotationPitch + (entityMinecart.rotationPitch - entityMinecart.prevRotationPitch) * p_76986_9_;

        if (vec3 != null)
        {
            Vec3 vec31 = entityMinecart.func_70495_a(d3, d4, d5, d6);
            Vec3 vec32 = entityMinecart.func_70495_a(d3, d4, d5, -d6);

            if (vec31 == null)
            {
                vec31 = vec3;
            }

            if (vec32 == null)
            {
                vec32 = vec3;
            }

            xx += vec3.xCoord - d3;
            yy += (vec31.yCoord + vec32.yCoord) / 2.0D - d4;
            zz += vec3.zCoord - d5;
            Vec3 vec33 = vec32.addVector(-vec31.xCoord, -vec31.yCoord, -vec31.zCoord);

            if (vec33.lengthVector() != 0.0D)
            {
                vec33 = vec33.normalize();
                p_76986_8_ = (float) (Math.atan2(vec33.zCoord, vec33.xCoord) * 180.0D / Math.PI);
                f5 = (float) (Math.atan(vec33.yCoord) * 73.0D);
            }
        }

        GL11.glTranslatef((float) xx, (float) yy, (float) zz);
        GL11.glRotatef(180.0F - p_76986_8_, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-f5, 0.0F, 0.0F, 1.0F);
        float f7 = (float) entityMinecart.getRollingAmplitude() - p_76986_9_;
        float f8 = entityMinecart.getDamage() - p_76986_9_;

        if (f8 < 0.0F)
        {
            f8 = 0.0F;
        }

        if (f7 > 0.0F)
        {
            GL11.glRotatef(MathHelper.sin(f7) * f7 * f8 / 10.0F * (float) entityMinecart.getRollingDirection(), 1.0F, 0.0F, 0.0F);
        }

        //Render block that is inside the minecart
        Block block = entityMinecart.func_145820_n();
        if (block != null && block != Blocks.air)
        {
            this.renderContainedBlock(entityMinecart, p_76986_9_, block);
        }

        //Render Minecart
        this.bindEntityTexture(entityMinecart);
        GL11.glColor4f(1f, 1f, 1f, 1f);
        GL11.glScalef(-1.0F, -1.0F, 1.0F);
        this.modelMinecart.render(entityMinecart, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        GL11.glPopMatrix();
    }

    protected void renderContainedBlock(EntityWoodenCart entityMinecart, float par, Block block)
    {
        GL11.glPushMatrix();

        float f1 = entityMinecart.getBrightness(par);
        this.bindTexture(TextureMap.locationBlocksTexture);

        float f6 = 0.75F;
        GL11.glScalef(f6, f6, f6);

        GL11.glTranslatef(0.0F, (float) entityMinecart.getDisplayTileOffset() / 16.0F, 0.0F);
        if (entityMinecart.getCartType() == EnumCartTypes.CHEST && entityMinecart.getBlockRenderColor() != -1)
            renderColoredChest(entityMinecart.getBlockRenderColor());
        else
            this.renderBlocks.renderBlockAsItem(block, entityMinecart.getDisplayTileData(), f1);

        GL11.glPopMatrix();
    }

    protected void renderColoredChest(int c)
    {
        GL11.glTranslatef(0.5f, 0.5f, 0.5f);
        GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glScalef(1.0F, -1.0F, -1.0F);
        modelChest.chestLid.rotateAngleX = 0;

        //set color
        if (c != -1)
        {
            Color color = WoodenRails.getColor(c);
            float r = (float) color.getRed() / 255f;
            float g = (float) color.getGreen() / 255f;
            float b = (float) color.getBlue() / 255f;
            GL11.glColor3f(r, g, b);
        }

        FMLClientHandler.instance().getClient().renderEngine.bindTexture(textureChest);
        modelChest.renderAll();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
    }
}
